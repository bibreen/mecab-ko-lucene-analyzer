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
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.junit.Test;

public class MeCabKoTokenizerTest {
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
    Tokenizer tokenizer = new MeCabKoTokenizer(
        new StringReader("꽃배달 꽃망울 오토바이"),
        new StandardPosAppender(), true);
    assertEquals(
        "꽃:1:0:1,배달:1:1:3,꽃:1:4:5,꽃망울:1:4:7,망울:0:5:7,오토바이:1:8:12,",
        tokenizerToString(tokenizer));
   
    tokenizer.reset();
    tokenizer.setReader(new StringReader("김진명 소설 무궁화꽃이 피었습니다."));
    assertEquals(
        "김진명:1:0:3,소설:1:4:6,무궁화:1:7:10,꽃이:1:10:12,꽃:0:10:11," +
        "피었습니다:1:13:18,",
        tokenizerToString(tokenizer));
    tokenizer.close();
  }

  @Test
  public void testEmptyQuery() throws Exception {
    Tokenizer tokenizer = new MeCabKoTokenizer(
        new StringReader(""),
        new StandardPosAppender(), true);
    assertEquals(false, tokenizer.incrementToken());
    tokenizer.close();
  }

  @Test
  public void testEmptyMorphemes() throws Exception {
    Tokenizer tokenizer = new MeCabKoTokenizer(
        new StringReader("!@#$%^&*"),
        new StandardPosAppender(), true);
    assertEquals(false, tokenizer.incrementToken());
    tokenizer.close();
  }

  @Test
  public void testHanEnglish() throws Exception {
    Tokenizer tokenizer = new MeCabKoTokenizer(
        new StringReader("한win"),
        new StandardPosAppender(), true);
    assertEquals("한:1:0:1,win:1:1:4,", tokenizerToString(tokenizer));
    tokenizer.close();
  }
}
