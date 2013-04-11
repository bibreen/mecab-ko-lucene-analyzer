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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.chasen.mecab.Lattice;
import org.chasen.mecab.Tagger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TokenGeneratorTest {
  private Tagger tagger;
  private Lattice lattice;
  
  @Before
  public void setUp() throws Exception {
    MeCabLoader loader =
        MeCabLoader.getInstance("/usr/local/lib/mecab/dic/mecab-ko-dic");
    tagger = loader.createTagger();
    lattice = loader.createLattice();
  }

  @After
  public void tearDown() throws Exception {
  }
  
  @Test
  public void testHangulSentence() {
    List<TokenInfo> tokens;
    lattice.set_sentence("무궁화 꽃이 피었습니다.");
    tagger.parse(lattice);
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(),
        TokenGenerator.NO_DECOMPOUND,
        lattice.bos_node());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[무궁화:1:0:3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[꽃이:1:4:6, 꽃:0:4:5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[피었습니다:1:7:12]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testHangulMultiSentence() {
    List<TokenInfo> tokens;
    lattice.set_sentence("무궁화 꽃이 피었습니다. 이 문장 지겹다.");
    tagger.parse(lattice);
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(),
        TokenGenerator.NO_DECOMPOUND,
        lattice.bos_node());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[무궁화:1:0:3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[꽃이:1:4:6, 꽃:0:4:5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[피었습니다:1:7:12]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[이:1:14:15]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[문장:1:16:18]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[지겹다:1:19:22]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testEnglishSentence() {
    List<TokenInfo> tokens;
    lattice.set_sentence("영어(english)를 study 하는 것은 어렵다.");
    tagger.parse(lattice);
    TokenGenerator generator = new TokenGenerator(
            new StandardPosAppender(),
            TokenGenerator.NO_DECOMPOUND,
            lattice.bos_node());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[영어:1:0:2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[english:1:3:10]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[를:1:11:12]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[study:1:13:18]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[하는:1:19:21]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[것은:1:22:24, 것:0:22:23]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[어렵다:1:25:28]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testInflectSentence() {
    List<TokenInfo> tokens;
    lattice.set_sentence("속이 쓰린 이유를 모릅니다.");
    tagger.parse(lattice);
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(),
        TokenGenerator.NO_DECOMPOUND,
        lattice.bos_node());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[속이:1:0:2, 속:0:0:1]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[쓰린:1:3:5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[이유를:1:6:9, 이유:0:6:8]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[모릅니다:1:10:14]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testDecompoundNoun() {
    List<TokenInfo> tokens;
    lattice.set_sentence("삼성전자");
    tagger.parse(lattice);
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(),
        TokenGenerator.DEFAULT_DECOMPOUND,
        lattice.bos_node());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[삼성:1:0:2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[삼성전자:1:0:4, 전자:0:2:4]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testDecompoundNounSentence() {
    List<TokenInfo> tokens;
    lattice.set_sentence("삼성전자는 대표적인 복합명사이다.");
    tagger.parse(lattice);
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(),
        TokenGenerator.DEFAULT_DECOMPOUND,
        lattice.bos_node());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[삼성:1:0:2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[삼성전자는:1:0:5, 삼성전자:0:0:4, 전자:0:2:4]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[대표적인:1:6:10, 대표:0:6:8]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[복합:1:11:13]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[명사이다:1:13:17, 명사:0:13:15]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testNoDecompoundNounSentence() {
    List<TokenInfo> tokens;
    lattice.set_sentence("삼성전자는 대표적인 복합명사이다.");
    tagger.parse(lattice);
    TokenGenerator generator = new TokenGenerator(
            new StandardPosAppender(),
            TokenGenerator.NO_DECOMPOUND,
            lattice.bos_node());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[삼성전자는:1:0:5, 삼성전자:0:0:4]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[대표적인:1:6:10, 대표:0:6:8]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[복합:1:11:13]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[명사이다:1:13:17, 명사:0:13:15]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testEnglishKorean() {
    List<TokenInfo> tokens;
    lattice.set_sentence("bag은 가방이다.");
    tagger.parse(lattice);
    TokenGenerator generator = new TokenGenerator(
            new StandardPosAppender(),
            TokenGenerator.NO_DECOMPOUND,
            lattice.bos_node());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[bag은:1:0:4, bag:0:0:3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[가방이다:1:5:9, 가방:0:5:7]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  /**
   * 떨어진 조사 문제(https://bitbucket.org/bibreen/mecab-ko-dic/issue/1/--------------------)
   * 해결을 위한 코드 테스트.
   */
  @Test
  public void testIsolatedJosaCase1() {
    List<TokenInfo> tokens;
    lattice.set_sentence("무궁화 꽃 이 그림같다.");
    tagger.parse(lattice);
    TokenGenerator generator = new TokenGenerator(
            new StandardPosAppender(),
            TokenGenerator.NO_DECOMPOUND,
            lattice.bos_node());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[무궁화:1:0:3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[꽃:1:4:5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[이:1:6:7]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[그림:1:8:10]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[같다:1:10:12]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  /**
   * 떨어진 조사 문제(https://bitbucket.org/bibreen/mecab-ko-dic/issue/1/--------------------)
   * 해결을 위한 코드 테스트.
   */
  @Test
  public void testIsolatedJosaCase2() {
    List<TokenInfo> tokens;
    lattice.set_sentence("무궁화 꽃 이그림같다.");
    tagger.parse(lattice);
    TokenGenerator generator = new TokenGenerator(
            new StandardPosAppender(),
            TokenGenerator.NO_DECOMPOUND,
            lattice.bos_node());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[무궁화:1:0:3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[꽃:1:4:5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[이:1:6:7]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[그림:1:7:9, 이:0:6:7, 이그림:0:6:9]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[같다:1:9:11]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  /**
   * 떨어진 명사 파생 접미사 해결을 위한 코드 테스트.
   */
  @Test
  public void testIsolatedJosaCase3() {
    List<TokenInfo> tokens;
    lattice.set_sentence("샤프 심이 뾰족하다.");
    tagger.parse(lattice);
    TokenGenerator generator = new TokenGenerator(
            new StandardPosAppender(),
            TokenGenerator.NO_DECOMPOUND,
            lattice.bos_node());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[샤프:1:0:2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[심이:1:3:5, 심:0:3:4, 심이:0:3:5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[뾰족하다:1:6:10, 뾰족:0:6:8]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testDecompounMinLengthWith1() {
    List<TokenInfo> tokens;
    lattice.set_sentence("그는 담배 생각이 날 때마다 한 개비씩 낱값을 주고 사서 핀다.");
    tagger.parse(lattice);
    TokenGenerator generator = new TokenGenerator(
            new StandardPosAppender(), 1, lattice.bos_node());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[그는:1:0:2, 그:0:0:1]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[담배:1:3:5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[생각이:1:6:9, 생각:0:6:8]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[날:1:10:11]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[때마다:1:12:15, 때:0:12:13]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[한:1:16:17]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[개비씩:1:18:21, 개비:0:18:20]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[낱:1:22:23]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[낱값을:1:22:25, 낱값:0:22:24, 값:0:23:24]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[주고:1:26:28]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[사서:1:29:31]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[핀다:1:32:34]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testDecompounMinLengthWith4() {
    List<TokenInfo> tokens;
    lattice.set_sentence("한국을 최대한 배려했다는 사실을 이해해주길 바란다.");
    tagger.parse(lattice);
    TokenGenerator generator = new TokenGenerator(
            new StandardPosAppender(), 4, lattice.bos_node());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[한국을:1:0:3, 한국:0:0:2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[최대한:1:4:7]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[배려했다는:1:8:13, 배려:0:8:10]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[사실을:1:14:17, 사실:0:14:16]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[이해해주길:1:18:23, 이해:0:18:20]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[바란다:1:24:27]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testEmptySentence() {
    List<TokenInfo> tokens;
    lattice.set_sentence("");
    tagger.parse(lattice);
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(),
        TokenGenerator.DEFAULT_DECOMPOUND,
        lattice.bos_node());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testSymbolOnlySentence() {
    List<TokenInfo> tokens;
    lattice.set_sentence(".,;:~");
    tagger.parse(lattice);
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(),
        TokenGenerator.DEFAULT_DECOMPOUND,
        lattice.bos_node());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
}
