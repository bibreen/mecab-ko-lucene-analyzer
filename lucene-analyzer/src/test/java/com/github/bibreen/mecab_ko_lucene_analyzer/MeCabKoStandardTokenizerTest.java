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

import static org.junit.Assert.*;

import java.io.StringReader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.*;
import org.junit.Ignore;
import org.junit.Test;

import com.github.bibreen.mecab_ko_lucene_analyzer.tokenattributes.PartOfSpeechAttribute;
import com.github.bibreen.mecab_ko_lucene_analyzer.tokenattributes.SemanticClassAttribute;

public class MeCabKoStandardTokenizerTest {
  private String tokenizerToString(Tokenizer tokenizer) throws Exception {
    OffsetAttribute extOffset = tokenizer.addAttribute(OffsetAttribute.class);
    PositionIncrementAttribute posIncrAtt = 
        tokenizer.addAttribute(PositionIncrementAttribute.class);
    PositionLengthAttribute posLengthAtt = 
        tokenizer.addAttribute(PositionLengthAttribute.class);
    CharTermAttribute term =
        (CharTermAttribute)tokenizer.addAttribute(CharTermAttribute.class);
    TypeAttribute type =
        (TypeAttribute)tokenizer.addAttribute(TypeAttribute.class);
    SemanticClassAttribute semanticClass = 
        (SemanticClassAttribute)tokenizer.addAttribute(SemanticClassAttribute.class);
    PartOfSpeechAttribute pos = 
        (PartOfSpeechAttribute)tokenizer.addAttribute(PartOfSpeechAttribute.class);
        

    StringBuilder result = new StringBuilder();
    while (tokenizer.incrementToken() == true) {
      result.append(new String(term.buffer(), 0, term.length())).append(":");
      result.append(type.type()).append(":");
      result.append(pos.partOfSpeech()).append(":");
      result.append(semanticClass.semanticClass()).append(":");
      result.append(String.valueOf(posIncrAtt.getPositionIncrement())).append(":");
      result.append(String.valueOf(posLengthAtt.getPositionLength())).append(":");
      result.append(String.valueOf(extOffset.startOffset())).append(":");
      result.append(String.valueOf(extOffset.endOffset()));
      result.append(",");
    }
    tokenizer.end();
    return result.toString();
  }
  
  private Tokenizer createTokenizer(
      StringReader reader, int decompoundMinLength) {
    return new MeCabKoTokenizer(
        reader,
        "/usr/local/lib/mecab/dic/mecab-ko-dic",
        new StandardPosAppender(),
        decompoundMinLength);
  }
  
  @Test
  public void testEmptyQuery() throws Exception {
    Tokenizer tokenizer = createTokenizer(
        new StringReader(""), TokenGenerator.DEFAULT_COMPOUND_NOUN_MIN_LENGTH);
    assertEquals(false, tokenizer.incrementToken());
    tokenizer.close();
  }
  
  @Test
  public void testEmptyMorphemes() throws Exception {
    Tokenizer tokenizer = createTokenizer(
        new StringReader("!@#$%^&*"),
        TokenGenerator.DEFAULT_COMPOUND_NOUN_MIN_LENGTH);
    assertEquals(false, tokenizer.incrementToken());
    tokenizer.close();
  }
  
  @Test
  public void testSemanticClassSentence() throws Exception {
    Tokenizer tokenizer = createTokenizer(
        new StringReader("이승기 미근동"), 2);
    assertEquals(
        "이승기:N:NNP:인명:1:1:0:3,미근:N:NNP:지명:1:1:4:6,"
        + "미근동:COMPOUND:Compound:지명:0:2:4:7,동:N:NNG:null:1:1:6:7,",
        tokenizerToString(tokenizer));
  }
  

  @Test
  public void testShortSentence() throws Exception {
    Tokenizer tokenizer = createTokenizer(
        new StringReader("꽃배달 꽃망울 오토바이"), 2);
    assertEquals(
        "꽃:N:NNG:null:1:1:0:1,배달:N:NNG:null:1:1:1:3,"
        + "꽃:N:NNG:null:1:1:4:5,꽃망울:COMPOUND:Compound:null:0:2:4:7,"
        + "망울:N:NNG:null:1:1:5:7,오토바이:N:NNG:null:1:1:8:12,",
        tokenizerToString(tokenizer));
   
    tokenizer.reset();
    tokenizer.setReader(new StringReader("소설 무궁화꽃이 피었습니다."));
    assertEquals(
        "소설:N:NNG:null:1:1:0:2,무궁:N:NNG:null:1:1:3:5,"
        + "무궁화:COMPOUND:Compound:null:0:2:3:6,화:N:NNG:null:1:1:5:6,"
        + "꽃이:EOJEOL:NNG+JKS:null:1:1:6:8,꽃:N:NNG:null:0:1:6:7,"
        + "피었습니다:EOJEOL:VV+EP+EF:null:1:1:9:14,",
        tokenizerToString(tokenizer));
    tokenizer.close();
  }
  
  @Ignore
  public void testComplexSentence() throws Exception {
    Tokenizer tokenizer = createTokenizer(
        new StringReader(
            "지금보다 어리고 민감하던 시절 아버지가 충고를 한마디 했는데 " +
            "아직도 그 말이 기억난다."),
        TokenGenerator.DEFAULT_COMPOUND_NOUN_MIN_LENGTH);
    assertEquals(
        "지금보다:EOJEOL:1:1:0:4,지금:N:0:1:0:2,어리고:EOJEOL:1:1:5:8,"
        + "민감하던:EOJEOL:1:1:9:13,민감:XR:0:1:9:11,시절:N:1:1:14:16,"
        + "아버지가:EOJEOL:1:1:17:21,아버지:N:0:1:17:20,충고를:EOJEOL:1:1:22:25,"
        + "충고:N:0:1:22:24,한:N:1:1:26:27,한마디:COMPOUND:0:2:26:29,"
        + "마디:N:1:1:27:29,했는데:EOJEOL:1:1:30:33,아직도:EOJEOL:1:1:34:37,"
        + "아직:MAG:0:1:34:36,그:MM:1:1:38:39,말이:EOJEOL:1:1:40:42,"
        + "말:N:0:1:40:41,기억난다:INFLECT:1:1:43:47,",
        tokenizerToString(tokenizer));
    tokenizer.close();
  }
  
  @Test
  public void testHanEnglish() throws Exception {
    Tokenizer tokenizer = createTokenizer(
        new StringReader("한글win"),
        TokenGenerator.DEFAULT_COMPOUND_NOUN_MIN_LENGTH);
    assertEquals("한글:N:NNG:null:1:1:0:2,win:SL:SL:null:1:1:2:5,", 
        tokenizerToString(tokenizer));
    tokenizer.close();
  }
  
  @Test
  public void testDecompound() throws Exception {
    Tokenizer tokenizer = createTokenizer(
        new StringReader("형태소"),
        TokenGenerator.DEFAULT_COMPOUND_NOUN_MIN_LENGTH);
    assertEquals(
        "형태:N:NNG:null:1:1:0:2,형태소:COMPOUND:Compound:null:0:2:0:3,소:N:NNG:null:1:1:2:3,",
        tokenizerToString(tokenizer));
    tokenizer.close();
    
    tokenizer = createTokenizer(
        new StringReader("가고문헌"),
        TokenGenerator.DEFAULT_COMPOUND_NOUN_MIN_LENGTH);
    assertEquals(
        "가고:N:NNG:null:1:1:0:2,가고문헌:COMPOUND:Compound:null:0:2:0:4,"
        + "문헌:N:NNG:null:1:1:2:4,",
        tokenizerToString(tokenizer));
    tokenizer.close();
  }
  
  @Test
  public void testNoDecompound() throws Exception {
    Tokenizer tokenizer = createTokenizer(
        new StringReader("형태소"),
        TokenGenerator.NO_DECOMPOUND);
    assertEquals("형태소:COMPOUND:NNG:null:1:2:0:3,", tokenizerToString(tokenizer));
    tokenizer.close();
    
    tokenizer = createTokenizer(
        new StringReader("가고문헌"),
        TokenGenerator.NO_DECOMPOUND);
    assertEquals(
        "가고문헌:COMPOUND:NNG:null:1:2:0:4,", tokenizerToString(tokenizer));
    tokenizer.close();
  }
  
  @Test
  public void testPreanalysisSentence() throws Exception {
    Tokenizer tokenizer = createTokenizer(
        new StringReader("은전한닢 프로젝트는 오픈소스이다."),
        TokenGenerator.DEFAULT_COMPOUND_NOUN_MIN_LENGTH);
    assertEquals(
        "은전:N:NNG:null:1:1:0:2,한:N:NR:null:1:1:2:3,닢:N:NNG:null:1:1:3:4,"
        + "프로젝트는:EOJEOL:NNG+JX:null:1:1:5:10,프로젝트:N:NNG:null:0:1:5:9,"
        + "오픈:N:NNG:null:1:1:11:13,소스이다:EOJEOL:NNG+VCP+EF:null:1:1:13:17,"
        + "소스:N:NNG:null:0:1:13:15,",
        tokenizerToString(tokenizer));
    tokenizer.close();
  }
  
  @Test
  public void testUnknownSurface() throws Exception {
    Tokenizer tokenizer = createTokenizer(
        new StringReader("걀꿀 없는 단어"),
        TokenGenerator.DEFAULT_COMPOUND_NOUN_MIN_LENGTH);
    assertEquals(
        "걀꿀:UNKNOWN:UNKNOWN:null:1:1:0:2,없는:EOJEOL:VA+ETM:null:1:1:3:5,"
        + "단어:N:NNG:null:1:1:6:8,",
        tokenizerToString(tokenizer));
    tokenizer.close();
  }
}