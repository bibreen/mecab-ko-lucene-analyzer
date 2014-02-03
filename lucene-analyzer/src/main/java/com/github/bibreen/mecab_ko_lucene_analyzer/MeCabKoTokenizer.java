/*******************************************************************************
 * Copyright 2013 Yongwoon Lee, Yungho Yu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.github.bibreen.mecab_ko_lucene_analyzer;

import java.io.IOException;
import java.io.Reader;
import java.util.Queue;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.chasen.mecab.Lattice;
import org.chasen.mecab.Tagger;

import com.github.bibreen.mecab_ko_lucene_analyzer.tokenattributes.PartOfSpeechAttribute;
import com.github.bibreen.mecab_ko_lucene_analyzer.tokenattributes.SemanticClassAttribute;
import com.github.bibreen.mecab_ko_mecab_loader.MeCabLoader;

/**
 * Lucene/Solr용 Tokenizer.
 * 
 * @author bibreen <bibreen@gmail.com>
 * @author amitabul <mousegood@gmail.com>
 */
public final class MeCabKoTokenizer extends Tokenizer {
  private CharTermAttribute charTermAtt;
  private PositionIncrementAttribute posIncrAtt;
  private PositionLengthAttribute posLenAtt;
  private OffsetAttribute offsetAtt;
  private TypeAttribute typeAtt;
  private PartOfSpeechAttribute posAtt;
  private SemanticClassAttribute semanticClassAtt;
 
  private String document;
  private String mecabDicDir;
  private MeCabLoader mecabLoader;
  private Lattice lattice;
  private Tagger tagger;
  private PosAppender posAppender;
  private int compoundNounMinLength;
  private TokenGenerator generator;
  private Queue<Pos> tokensQueue;
  
  /**
   * MeCabKoTokenizer 생성자.
   * Default AttributeFactory 사용.
   * 
   * @param input
   * @param dicDir mecab 사전 디렉터리 경로
   * @param appender PosAppender
   * @param compoundNounMinLength 분해를 해야하는 복합명사의 최소 길이.
   * 복합명사 분해가 필요없는 경우, TokenGenerator.NO_DECOMPOUND를 입력한다.
   */
  public MeCabKoTokenizer(
      Reader input,
      String dicDir,
      PosAppender appender,
      int compoundNounMinLength) {
    this(
        AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY,
        input,
        dicDir,
        appender,
        compoundNounMinLength);
  }

  /**
   * MeCabKoTokenizer 생성자.
   * 
   * @param factory the AttributeFactory to use
   * @param input
   * @param dicDir mecab 사전 디렉터리 경로
   * @param appender PosAppender
   * @param compoundNounMinLength 분해를 해야하는 복합명사의 최소 길이.
   * 복합명사 분해가 필요없는 경우, TokenGenerator.NO_DECOMPOUND를 입력한다.
   */
  public MeCabKoTokenizer(
      AttributeFactory factory,
      Reader input,
      String dicDir,
      PosAppender appender,
      int compoundNounMinLength) {
    super(factory, input);
    posAppender = appender;
    mecabDicDir = dicDir;
    this.compoundNounMinLength = compoundNounMinLength;
    setMeCab();
    setAttributes();
  }

  private void setMeCab() {
    mecabLoader = MeCabLoader.getInstance(mecabDicDir);
    lattice = mecabLoader.createLattice();
    tagger = mecabLoader.createTagger();
  }
  
  private void setAttributes() {
    charTermAtt = addAttribute(CharTermAttribute.class);
    posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    posLenAtt = addAttribute(PositionLengthAttribute.class);
    offsetAtt = addAttribute(OffsetAttribute.class);
    typeAtt = addAttribute(TypeAttribute.class);
    posAtt = addAttribute(PartOfSpeechAttribute.class);
    semanticClassAtt = addAttribute(SemanticClassAttribute.class);
  }

  @Override
  public boolean incrementToken() throws IOException {
    if (isBegin()) {
      document = getDocument();
      createTokenGenerator();
    }
    
    if (tokensQueue == null || tokensQueue.isEmpty()) {
      tokensQueue = generator.getNextEojeolTokens();
      if (tokensQueue == null) {
        return false;
      }
    }
    Pos token = tokensQueue.poll();
    setAttributes(token);
    return true;
  }

  private boolean isBegin() {
    return generator == null;
  }

  private void createTokenGenerator() {
    lattice.set_sentence(document);
    tagger.parse(lattice);
    this.generator = new TokenGenerator(
        posAppender, compoundNounMinLength, lattice.bos_node());
  }
  
  private void setAttributes(Pos token) {
    posIncrAtt.setPositionIncrement(token.getPositionIncr());
    posLenAtt.setPositionLength(token.getPositionLength());
    offsetAtt.setOffset(
        correctOffset(token.getStartOffset()),
        correctOffset(token.getEndOffset()));
    charTermAtt.copyBuffer(
        token.getSurface().toCharArray(), 0, token.getSurfaceLength());
    typeAtt.setType(token.getPosId().toString());
    posAtt.setMophemes(token.getMophemes());
    semanticClassAtt.setSemanteme(token.getSemanteme());
  }
  
  @Override
  public final void end() {
    // set final offset
    offsetAtt.setOffset(
        correctOffset(document.length()), correctOffset(document.length()));
    document = null;
  }
  
  @Override
  public final void reset() throws IOException {
    super.reset();
    generator = null;
    tokensQueue = null;
  }
  
  private String getDocument() throws IOException {
    StringBuffer document = new StringBuffer();
    char[] tmp = new char[1024];
    int len;
    while ((len = input.read(tmp)) != -1) {
      document.append(new String(tmp, 0, len));
    }
    return document.toString().toLowerCase();
  }
}
