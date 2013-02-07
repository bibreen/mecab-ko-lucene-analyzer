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

public class MeCabKoStandardTokenizerTest {
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
  
  private Tokenizer createTokenizer(
      StringReader reader, boolean needNounDecompound) {
    return new MeCabKoTokenizer(
        reader,
        "/usr/local/lib/mecab/dic/mecab-ko-dic",
        new StandardPosAppender(),
        needNounDecompound);
  }

  @Test
  public void test() throws Exception {
    Tokenizer tokenizer = createTokenizer(
        new StringReader("꽃배달 꽃망울 오토바이"), true);
    assertEquals(
        "꽃:1:0:1,배달:1:1:3,꽃:1:4:5,꽃망울:1:4:7,망울:0:5:7,오토바이:1:8:12,",
        tokenizerToString(tokenizer));
   
    tokenizer.reset();
    tokenizer.setReader(new StringReader("소설 무궁화꽃이 피었습니다."));
    assertEquals(
        "소설:1:0:2,무궁:1:3:5,무궁화:1:3:6,화:0:5:6,꽃이:1:6:8,꽃:0:6:7," +
        "피었습니다:1:9:14,",
        tokenizerToString(tokenizer));
    tokenizer.close();
  }

  @Test
  public void testEmptyQuery() throws Exception {
    Tokenizer tokenizer = createTokenizer(new StringReader(""), true);
    assertEquals(false, tokenizer.incrementToken());
    tokenizer.close();
  }

  @Test
  public void testEmptyMorphemes() throws Exception {
    Tokenizer tokenizer = createTokenizer(new StringReader("!@#$%^&*"), true);
    assertEquals(false, tokenizer.incrementToken());
    tokenizer.close();
  }

  @Test
  public void testHanEnglish() throws Exception {
    Tokenizer tokenizer = createTokenizer(new StringReader("한win"), true);
    assertEquals("한:1:0:1,win:1:1:4,", tokenizerToString(tokenizer));
    tokenizer.close();
  }
}
