package com.github.bibreen.mecab_ko_lucene_analyzer;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MecabTokenizerTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  private String tokenizerToString(Tokenizer tokenizer) throws Exception {
    String result = new String();
    OffsetAttribute extOffset = tokenizer.addAttribute(OffsetAttribute.class);
    PositionIncrementAttribute posIncrAtt = 
        tokenizer.addAttribute(PositionIncrementAttribute.class);
    CharTermAttribute term =
        (CharTermAttribute)tokenizer.addAttribute(CharTermAttribute.class);

    while (tokenizer.incrementToken() == true) {
      result += new String(term.buffer(), 0, term.length()) + ":";
      result += String.valueOf(posIncrAtt.getPositionIncrement()) + ":";
      result += String.valueOf(extOffset.startOffset()) + ":";
      result += String.valueOf(extOffset.endOffset());
      result += ",";
    }
    tokenizer.end();
    return result;
  }

  @Test
  public void test() throws Exception {
    Tokenizer tokenizer = new MecabTokenizer(
        new StringReader("꽃배달 꽃망울 오토바이"));
    assertEquals(
        "꽃:1:0:3,배달:1:3:9,꽃배달:0:0:9,꽃망울:1:10:19,오토바이:1:20:32,",
        tokenizerToString(tokenizer));
    tokenizer.close();

//    tokenizer.setReader(new StringReader("김진명 소설 무궁화꽃이 피었습니다."));
//    assertEquals(
//        "김:1:0:1,진명:1:1:3,김진명:0:0:3,소설:1:4:6," +
//        "무궁화꽃:1:7:11,무궁화꽃이:0:7:12,피었습니다:1:13:18,",
//        tokenizerToString(tokenizer));
//    tokenizer.close();
  }
  
  
  @Test
  public void testEmptyQuery() throws Exception {
    Tokenizer tokenizer = new MecabTokenizer(new StringReader(""));
    assertEquals(false, tokenizer.incrementToken());
    tokenizer.close();
  }

//  @Test
//  public void testEmptyMorphemes() throws Exception {
//    Tokenizer tokenizer = new MecabTokenizer(new StringReader("!@#$%^&*"));
//    System.out.println(tokenizerToString(tokenizer));
//    assertEquals(false, tokenizer.incrementToken());
//    tokenizer.close();
//  }

//  @Test
//  public void testHanEnglish() throws Exception {
//    Tokenizer tokenizer = new MecabTokenizer(
//        new StringReader("한win"));
//    assertEquals("한:1:0:1,win:1:1:4,", tokenizerToString(tokenizer));
//    tokenizer.close();
//  }
}
