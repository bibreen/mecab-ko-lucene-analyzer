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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.chasen.mecab.Node;
import org.junit.*;

public class TokenGeneratorMockTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }
  
  public static Node mockNodeFactory(
      String surface, 
      int posId, 
      int Rlength, 
      int length, 
      String feature, 
      Node next) {
    Node node = mock(Node.class);
    when(node.getSurface()).thenReturn(surface);
    when(node.getPosid()).thenReturn(posId); // PosId.(X)
    when(node.getRlength()).thenReturn(Rlength);
    when(node.getLength()).thenReturn(length);
    when(node.getFeature()).thenReturn(feature);
    when(node.getNext()).thenReturn(next);
    
    return node;
  }

  @Test
  public void testBasicString() {
    // "무궁화 꽃이 피었습니다."
    Node pointNode = mockNodeFactory(
       ".", 160/*PosId.SF*/, 1, 1,"SF,*,*,*,*,*,*", null);
    Node supnidaNode = mockNodeFactory(
        "습니다", 100/*PosId.E*/, 3, 3, "EF,F,습니다,*,*,*,ㅂ니다/EF", pointNode);
    Node utNode = mockNodeFactory(
        "었", 100/*PosId.E*/, 1, 1, "EP,T,었,*,*,*,*", supnidaNode);
    Node peeNode = mockNodeFactory(
        "피", 173/*PosId.VV*/, 2, 1, "VV,F,피,*,*,*,*", utNode);
    Node ieeNode = mockNodeFactory(
        "이", 120/*PosId.J*/, 1, 1, "JKC,F,이,*,*,*,*", peeNode);
    Node kkotNode = mockNodeFactory(
        "꽃", 150/*PosId.N*/, 2, 1, "NN,T,꽃,*,*,*,*", ieeNode);
    Node mugungwhaNode = mockNodeFactory(
        "무궁화", 150/*PosId.N*/, 3, 3, "NN,F,무궁화,Compound,*,*,무궁+화", kkotNode);
    Node firstNode = mockNodeFactory("", 0, 0, 0, "", mugungwhaNode);
    
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(), TokenGenerator.NO_DECOMPOUND, firstNode);
    
    List<TokenInfo> tokens;
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
    // "모른다. 몰라."
    Node pointNode2 = mockNodeFactory(
       ".", 160/*PosId.SF*/, 1, 1,"SF,*,*,*,*,*,*", null);
    Node molaNode = mockNodeFactory(
       "몰라", 173/*PosId.VV*/, 3, 2, 
       "VV+EF,F,몰라,Inflect,VV,EF,모르/VV+ㅏ/EF", pointNode2);
    Node pointNode1 = mockNodeFactory(
       ".", 160/*PosId.SF*/, 1, 1, "SF,*,*,*,*,*,*", molaNode);
    Node molundaNode = mockNodeFactory(
       "모른다", 173/*PosId.SF*/, 3, 3, 
       "VV+EF,F,모른다,Inflect,VV,EF,모르/VV+ㄴ다/EF", pointNode1);
    Node firstNode = mockNodeFactory("", 0, 0, 0, "", molundaNode);
    
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(), TokenGenerator.NO_DECOMPOUND, firstNode);
    
    List<TokenInfo> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[모른다:1:0:3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[몰라:1:5:7]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testDecompoundNoun() {
    // "삼성전자"
    Node emptyNode = mockNodeFactory(
       "", 0/*PosId.UNKNOWN*/, 0, 0, "", null);
    Node samsungJunjaNode = mockNodeFactory(
       "삼성전자", 1/*PosId.COMPOUND*/, 4, 4, 
       "NN,F,삼성전자,Compound,*,*,삼성+전자", emptyNode);
    Node firstNode = mockNodeFactory("", 0, 0, 0, "", samsungJunjaNode);
    
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(),
        TokenGenerator.DEFAULT_DECOMPOUND,
        firstNode);
    
    List<TokenInfo> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[삼성:1:0:2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[삼성전자:1:0:4, 전자:0:2:4]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testDecompounMinLengthWith1() {
    // "무전기"
    Node emptyNode = mockNodeFactory(
       "", 0/*PosId.UNKNOWN*/, 3, 3, "", null);
    Node mujungiNode = mockNodeFactory(
       "무전기", 1/*PosId.COMPOUND*/, 3, 3, 
       "NN,F,무전기,Compound,*,*,무전+기", emptyNode);
    Node firstNode = mockNodeFactory("", 0, 0, 0, "", mujungiNode);
    
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(), 1, firstNode);
    List<TokenInfo> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[무전:1:0:2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[무전기:1:0:3, 기:0:2:3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testDecompounMinLengthWith4() {
    // "무전기"
    Node emptyNode = mockNodeFactory(
       "", 0/*PosId.UNKNOWN*/, 3, 3, "", null);
    Node mujungiNode = mockNodeFactory(
       "무전기", 1/*PosId.COMPOUND*/, 3, 3, 
       "NN,F,무전기,Compound,*,*,무전+기", emptyNode);
    Node firstNode = mockNodeFactory("", 0, 0, 0, "", mujungiNode);
    
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(), 4, firstNode);
    List<TokenInfo> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[무전기:1:0:3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testEmptySentence() {
    // ""
    Node emptyNode = mockNodeFactory(
       "", 0/*PosId.UNKNOWN*/, 0, 0, "", null);
    Node firstNode = mockNodeFactory("", 0, 0, 0, "", emptyNode);
    
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(),
        TokenGenerator.DEFAULT_DECOMPOUND,
        firstNode);
    List<TokenInfo> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testSymbolOnlySentence() {
    // ".,;:~"
    Node symbolsNode = mockNodeFactory(
       ".,;:~", 168/*PosId.SY*/, 5, 5, "", null);
    Node firstNode = mockNodeFactory("", 0, 0, 0, "", symbolsNode);
    
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(),
        TokenGenerator.DEFAULT_DECOMPOUND,
        firstNode);
    List<TokenInfo> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
}
