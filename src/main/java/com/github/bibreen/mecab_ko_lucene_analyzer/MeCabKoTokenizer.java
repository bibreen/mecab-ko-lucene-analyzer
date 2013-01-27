package com.github.bibreen.mecab_ko_lucene_analyzer;

import java.io.IOException;
import java.io.Reader;
import java.util.Queue;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.chasen.mecab.Lattice;
import org.chasen.mecab.Tagger;

public class MeCabKoTokenizer extends Tokenizer {
  private CharTermAttribute charTermAtt;
  private PositionIncrementAttribute posIncrAtt;
  private OffsetAttribute offsetAtt;
 
  private String document;
  private MeCabManager mecabManager;
  private Lattice lattice;
  private Tagger tagger;
  private PosAppender posAppender;
  private boolean needNounDecompound;
  private TokenGenerator generator;
  private Queue<TokenInfo> tokensQueue;

  protected MeCabKoTokenizer(
      Reader input, PosAppender appender, boolean needNounDecompound) {
    super(input);
    posAppender = appender;
    this.needNounDecompound = needNounDecompound;
    setMeCab();
    setAttributes();
  }

  private void setMeCab() {
    mecabManager = new MeCabManager();
    lattice = mecabManager.getLattice();
    tagger = mecabManager.getTagger();
  }
  
  private void setAttributes() {
    charTermAtt = addAttribute(CharTermAttribute.class);
    posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    offsetAtt = addAttribute(OffsetAttribute.class);
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
    TokenInfo token = tokensQueue.poll();
    System.out.println(token);
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
        posAppender, needNounDecompound, lattice.bos_node());
  }
  
  private void setAttributes(TokenInfo token) {
    posIncrAtt.setPositionIncrement(token.getPosIncr());
    offsetAtt.setOffset(
        correctOffset(token.getOffsets().start),
        correctOffset(token.getOffsets().end));
    charTermAtt.copyBuffer(
        token.getTerm().toCharArray(), 0, token.getTerm().length());
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
