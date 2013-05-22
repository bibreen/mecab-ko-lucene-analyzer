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

import java.util.List;

import org.chasen.mecab.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TokenGeneratorWithStandardPosAppenderTest
    extends TokenGeneratorTestCase {
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBasicHangulSentence() {
		Node node = mockNodeListFactory(new String[] {
		    "진달래\tNN,F,진달래,*,*,*,*,*",
		    " 꽃\tNN,T,꽃,*,*,*,*,*",
		    "이\tJKS,F,이,*,*,*,*,*",
		    " 피\tVV,F,피,*,*,*,*,*",
		    "었\tEP,T,었,*,*,*,*,*",
		    "습니다\tEF,F,습니다,*,*,*,*,*",
		    ".\tSF,*,*,*,*,*,*,*"
		});
	
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(), TokenGenerator.NO_DECOMPOUND, node);
    List<TokenInfo> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[진달래/N/1/1/0/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[꽃이/EOJEOL/1/1/4/6, 꽃/N/0/1/4/5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[피었습니다/EOJEOL/1/1/7/12]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
	}
	
	@Test
	public void testDecompound() {
		Node node = mockNodeListFactory(new String[] {
		    "삼성전자\tNN,F,삼성전자,Compound,*,*,삼성+전자,삼성/NN/1/1+삼성전자/COMPOUND/0/2+전자/NN/1/1"
		});
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(), 1, node);
    
    List<TokenInfo> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[삼성/N/1/1/0/2, 삼성전자/COMPOUND/0/2/0/4, 전자/N/1/1/2/4]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
	}
	
	@Test
	public void testNoDecompound() {
		Node node = mockNodeListFactory(new String[] {
		    "삼성전자\tNN,F,삼성전자,Compound,*,*,삼성+전자,삼성/NN/1/1+삼성전자/NN/0/2+전자/NN/1/1"
		});
    TokenGenerator generator =
        new TokenGenerator(
            new StandardPosAppender(), TokenGenerator.NO_DECOMPOUND, node);
    
    List<TokenInfo> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[삼성전자/COMPOUND/1/2/0/4]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
	}
	
	@Test
	public void testCompoundNounMinLength4() {
		Node node = mockNodeListFactory(new String[] {
		    "무궁화\tNN,F,무궁화,Compound,*,*,무궁+화,무궁/NN/1/1+무궁화/NN/0/2+화/NN/1/1"
		});
    TokenGenerator generator =
        new TokenGenerator(
            new StandardPosAppender(), 4, node);
    
    List<TokenInfo> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[무궁화/COMPOUND/1/2/0/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
    
		node = mockNodeListFactory(new String[] {
		    "삼성전자\tNN,F,삼성전자,Compound,*,*,삼성+전자,삼성/NN/1/1+삼성전자/COMPOUND/0/2+전자/NN/1/1"
		});
    generator = new TokenGenerator(new StandardPosAppender(), 4, node);
    
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[삼성/N/1/1/0/2, 삼성전자/COMPOUND/0/2/0/4, 전자/N/1/1/2/4]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
	}
	
	@Test
	public void testSentenceWithDecompoundAll() {
		Node node = mockNodeListFactory(new String[] {
		    "삼성전자\tNN,F,삼성전자,Compound,*,*,삼성+전자,삼성/NN/1/1+삼성전자/COMPOUND/0/2+전자/NN/1/1",
		    "는\tJX,T,는,*,*,*,*,*",
		    " 대표\tNN,F,대표,*,*,*,*,*",
		    "적\tXSN,T,적,*,*,*,*,*",
		    "인\tVCP+ETM,T,인,Inflect,VCP,ETM,이/VCP+ㄴ/ETM,*",
		    " 복합\tNN,T,복합,*,*,*,*,*",
		    "명사\tNN,F,명사,*,*,*,*,*",
		    "이\tVCP,F,이,*,*,*,*,*",
		    "다\tEF,F,다,*,*,*,*,*",
		    ".\tSF,*,*,*,*,*,*,*"
		});
		
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(), 1, node);
    
    List<TokenInfo> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[삼성/N/1/1/0/2, 삼성전자는/EOJEOL/0/2/0/5, 삼성전자/COMPOUND/0/2/0/4, 전자/N/1/1/2/4]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[대표적인/EOJEOL/1/1/6/10, 대표/N/0/1/6/8]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[복합/N/1/1/11/13]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[명사이다/EOJEOL/1/1/13/17, 명사/N/0/1/13/15]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
	}

	@Test
	public void testSentenceWithDecompoundMinLength2() {
		Node node = mockNodeListFactory(new String[] {
		    "나\tNP,F,나,*,*,*,*,*",
		    "의\tJKG,F,의,*,*,*,*,*",
		    "무궁화\tNN,F,무궁화,Compound,*,*,무궁+화,무궁/NN/1/1+무궁화/NN/0/2+화/NN/1/1",
		    "꽃\tNN,T,꽃,*,*,*,*,*",
		    "을\tJKO,T,을,*,*,*,*,*",
		    "보\tVV,F,보,*,*,*,*,*",
		    "아라\tEF,F,아라,*,*,*,*,*",
		    ".\tSF,*,*,*,*,*,*,*"
		});
		
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(), 2, node);
    
    List<TokenInfo> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[나의/EOJEOL/1/1/0/2, 나/N/0/1/0/1]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[무궁/N/1/1/2/4, 무궁화/N/0/2/2/5, 화/N/1/1/4/5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[꽃을/EOJEOL/1/1/5/7, 꽃/N/0/1/5/6]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[보아라/EOJEOL/1/1/7/10]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
	}
	
	@Test
	public void testSentenceWithEnglishAndSymbols() {
		Node node = mockNodeListFactory(new String[] {
		    "영어\tNN,F,영어,*,*,*,*,*",
		    "(\tSSO,*,*,*,*,*,*,*",
		    "english\tSL,*,*,*,*,*,*,*",
		    ")\tSSC,*,*,*,*,*,*,*",
		    "를\tJKO,T,를,*,*,*,*,*",
		    "study\tSL,*,*,*,*,*,*,*",
		    "하\tXSV,F,하,*,*,*,*,*",
		    "는\tETM,T,는,*,*,*,*,*",
		    "것\tNNB,T,것,*,*,*,*,*",
		    "은\tJX,T,은,*,*,*,*,*",
		    "어렵\tVA,T,어렵,*,*,*,*,*",
		    "다\tEF,F,다,*,*,*,*,*",
		    ".\tSF,*,*,*,*,*,*,*"
		});
		
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(), 2, node);
    
    List<TokenInfo> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[영어/N/1/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[english/SL/1/1/3/10]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[를/J/1/1/11/12]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[study/SL/1/1/12/17]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[하는/EOJEOL/1/1/17/19]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[것은/EOJEOL/1/1/19/21, 것/N/0/1/19/20]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[어렵다/EOJEOL/1/1/21/24]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
	}
	
  @Test
  public void testSymbolOnlySentence() {
		Node node = mockNodeListFactory(new String[] {
		    "!@#$%^&*()\tSY,*,*,*,*,*,*,*"
		});
		
    TokenGenerator generator =
        new TokenGenerator(
            new StandardPosAppender(),
            TokenGenerator.DEFAULT_COMPOUND_NOUN_MIN_LENGTH,
            node);
    
    List<TokenInfo> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
}
