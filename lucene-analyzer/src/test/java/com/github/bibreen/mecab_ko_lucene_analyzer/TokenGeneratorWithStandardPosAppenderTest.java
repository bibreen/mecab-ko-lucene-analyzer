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
import org.junit.Ignore;
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
        "진달래\tNNG,*,F,진달래,*,*,*,*,*",
        " 꽃\tNNG,*,T,꽃,*,*,*,*,*",
        "이\tJKS,*,F,이,*,*,*,*,*",
        " 피\tVV,*,F,피,*,*,*,*,*",
        "었\tEP,*,T,었,*,*,*,*,*",
        "습니다\tEF,F,습니다,*,*,*,*,*",
        ".\t SF,*,*,*,*,*,*,*,*"
    });
  
    TokenGenerator generator = new TokenGenerator(
        new StandardPosAppender(), TokenGenerator.NO_DECOMPOUND, node);
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[진달래/N/null/1/1/0/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[꽃이/EOJEOL/null/1/1/4/6, 꽃/N/null/0/1/4/5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[피었습니다/EOJEOL/null/1/1/7/12]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testDecompound() {
    Node node = mockNodeListFactory(new String[] {
        "삼성전자\tNNP,*,F,삼성전자,Compound,*,*,삼성+전자,삼성/NNG/*/1/1+삼성전자/Compound/*/0/2+전자/NNG/*/1/1",
    });
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(), 1, node);
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[삼성/N/null/1/1/0/2, 삼성전자/COMPOUND/null/0/2/0/4, 전자/N/null/1/1/2/4]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testComplexDecompound() {
    Node node = mockNodeListFactory(new String[] {
        "아질산나트륨\tNNG,*,T,아질산나트륨,Compound,*,*,아질산+나트륨," +
        "아/NNG/*/1/1+아질산나트륨/Compound/*/0/3+아질산/NNG/*/0/2+질산/NNG/*/1/1+나트륨/NNG/*/1/1"
    });
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(), 1, node);
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[아/N/null/1/1/0/1, 아질산나트륨/COMPOUND/null/0/3/0/6, 아질산/N/null/0/2/0/3, 질산/N/null/1/1/1/3, 나트륨/N/null/1/1/3/6]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testNoDecompound() {
    Node node = mockNodeListFactory(new String[] {
        "삼성전자\tNNP,*,F,삼성전자,Compound,*,*,삼성+전자,삼성/NNG/*/1/1+삼성전자/Compound/*/0/2+전자/NNG/*/1/1",
    });
    TokenGenerator generator =
        new TokenGenerator(
            new StandardPosAppender(), TokenGenerator.NO_DECOMPOUND, node);
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[삼성전자/COMPOUND/null/1/2/0/4]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testCompoundNounMinLength4() {
    Node node = mockNodeListFactory(new String[] {
        "무궁화\tNNG,*,F,무궁화,Compound,*,*,무궁+화,무궁/NNG/*/1/1+무궁화/Compound/*/0/2+화/NNG/*/1/1"
    });
    TokenGenerator generator =
        new TokenGenerator(
            new StandardPosAppender(), 4, node);
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[무궁화/COMPOUND/null/1/2/0/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
    
    node = mockNodeListFactory(new String[] {
        "삼성전자\tNNP,*,F,삼성전자,Compound,*,*,삼성+전자,삼성/NNG/*/1/1+삼성전자/Compound/*/0/2+전자/NNG/*/1/1"
    });
    generator = new TokenGenerator(new StandardPosAppender(), 4, node);
    
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[삼성/N/null/1/1/0/2, 삼성전자/COMPOUND/null/0/2/0/4, 전자/N/null/1/1/2/4]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testSentenceWithDecompoundAll() {
    Node node = mockNodeListFactory(new String[] {
        "삼성전자\tNNP,*,F,삼성전자,Compound,*,*,삼성+전자,삼성/NNG/*/1/1+삼성전자/Compound/*/0/2+전자/NNG/*/1/1",
        "는\tJX,*,T,는,*,*,*,*,*",
        " 대표\tNNG,*,F,대표,*,*,*,*,*",
        "적\tXSN,*,T,적,*,*,*,*,*",
        "인\tVCP+ETM,*,T,인,Inflect,VCP,ETM,이/VCP+ㄴ/ETM,*",
        " 복합\tNNG,*,T,복합,*,*,*,*,*",
        "명사\tNNG,*,F,명사,*,*,*,*,*",
        "이\tVCP,*,F,이,*,*,*,*,*",
        "다\tEF,*,F,다,*,*,*,*,*",
        ".\tSF,*,*,*,*,*,*,*,*",
    });
  	
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(), 1, node);
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[삼성/N/null/1/1/0/2, 삼성전자는/EOJEOL/null/0/2/0/5, 삼성전자/COMPOUND/null/0/2/0/4, 전자/N/null/1/1/2/4]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[대표/N/null/1/1/6/8]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[적인/EOJEOL/null/1/1/8/10, 적/XSN/null/0/1/8/9]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[복합/N/null/1/1/11/13]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[명사이다/EOJEOL/null/1/1/13/17, 명사/N/null/0/1/13/15]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testSentenceWithDecompoundComplexCompoundNoun() {
    Node node = mockNodeListFactory(new String[] {
        "아질산나트륨\tNNG,*,T,아질산나트륨,Compound,*,*,아질산+나트륨,아/NNG/*/1/1+아질산나트륨/Compound/*/0/3+아질산/NNG/*/0/2+질산/NNG/*/1/1+나트륨/NNG/*/1/1",
        "이란\tJX,*,T,이란,*,*,*,*,*",
        "무엇\tNP,*,T,무엇,*,*,*,*,*",
        "인가요\tVCP+EF,*,F,인가요,Inflect,VCP,EF,이/VCP+ㄴ가요/EF,*",
        "?\tSF,*,*,*,*,*,*,*,*",
    });
    
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(), 1, node);
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[아/N/null/1/1/0/1, 아질산나트륨이란/EOJEOL/null/0/3/0/8, 아질산나트륨/COMPOUND/null/0/3/0/6, 아질산/N/null/0/2/0/3, 질산/N/null/1/1/1/3, 나트륨/N/null/1/1/3/6]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[무엇인가요/EOJEOL/null/1/1/8/13, 무엇/N/null/0/1/8/10]",
        tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testSentenceWithCompoundNounMinLength4() {
    Node node = mockNodeListFactory(new String[] {
        "나\tNP,*,F,나,*,*,*,*,*",
        "의\tJKG,*,F,의,*,*,*,*,*",
        "무궁화\tNNG,*,F,무궁화,Compound,*,*,무궁+화,무궁/NNG/*/1/1+무궁화/Compound/*/0/2+화/NNG/*/1/1",
        "꽃\tNNG,*,T,꽃,*,*,*,*,*",
        "을\tJKO,*,T,을,*,*,*,*,*",
        "보\tVV,*,F,보,*,*,*,*,*",
        "아라\tEF,*,F,아라,*,*,*,*,*",
        ".\tSF,*,*,*,*,*,*,*,*",

    });
  	
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(), 4, node);
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[나의/EOJEOL/null/1/1/0/2, 나/N/null/0/1/0/1]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(
        "[무궁화/COMPOUND/null/1/2/2/5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[꽃을/EOJEOL/null/1/1/5/7, 꽃/N/null/0/1/5/6]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[보아라/EOJEOL/null/1/1/7/10]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testSentenceWithEnglishAndSymbols() {
    Node node = mockNodeListFactory(new String[] {
        "영어\tNNG,*,F,영어,*,*,*,*,*",
        "(\tSSO,*,*,*,*,*,*,*,*",
        "english\tSL,*,*,*,*,*,*,*,*",
        ")\tSSC,*,*,*,*,*,*,*,*",
        "를\tJKO,*,T,를,*,*,*,*,*",
        "study\tSL,*,*,*,*,*,*,*,*",
        "하\tXSV,*,F,하,*,*,*,*,*",
        "는\tETM,*,T,는,*,*,*,*,*",
        "것\tNNB,*,T,것,*,*,*,*,*",
        "은\tJX,*,T,은,*,*,*,*,*",
        "어렵\tVA,*,T,어렵,*,*,*,*,*",
        "다\tEF,*,F,다,*,*,*,*,*",
        ".\tSF,*,*,*,*,*,*,*,*",
    });
  	
    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(), 2, node);
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[영어/N/null/1/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[english/SL/null/1/1/3/10]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[를/J/null/1/1/11/12]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[study/SL/null/1/1/12/17]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[하는/EOJEOL/null/1/1/17/19]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[것은/EOJEOL/null/1/1/19/21, 것/N/null/0/1/19/20]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[어렵다/EOJEOL/null/1/1/21/24]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testPreanaysis() {
    Node node = mockNodeListFactory(new String[] {
        "은전한닢\tNNG+NR+NNG,*,T,은전한닢,Preanalysis,NNG,NR,은전+한+닢,은전/NNG/*/1/1+한/NR/*/1/1+닢/NNG/*/1/1",
    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[은전/N/null/1/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[한/N/null/1/1/2/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[닢/N/null/1/1/3/4]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testPreanaysisSentence() {
    Node node = mockNodeListFactory(new String[] {
        "은전한닢\tNNG+NR+NNG,*,T,은전한닢,Preanalysis,NNG,NR,은전+한+닢,은전/NNG/*/1/1+한/NR/*/1/1+닢/NNG/*/1/1",
        "은\tJX,*,T,은,*,*,*,*,*",
        "오픈\tNNG,*,T,오픈,*,*,*,*,*",
        "소스\tNNG,*,F,소스,*,*,*,*,*",
        "이\tVCP,*,F,이,*,*,*,*,*",
        "다\tEF,*,F,다,*,*,*,*,*",
    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[은전/N/null/1/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[한/N/null/1/1/2/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[닢은/EOJEOL/null/1/1/3/5, 닢/N/null/0/1/3/4]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[오픈/N/null/1/1/5/7]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[소스이다/EOJEOL/null/1/1/7/11, 소스/N/null/0/1/7/9]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testUnknownSurface() {
    Node node = mockNodeListFactory(new String[] {
        "걀꿀\tUNKNOWN,*,*,*,*,*,*,*,*",
        " 없\tVA,*,T,없,*,*,*,*,*",
        "는\tETM,*,T,는,*,*,*,*,*",
        " 단어\tNNG,*,F,단어,*,*,*,*,*",
    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[걀꿀/UNKNOWN/null/1/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[없는/EOJEOL/null/1/1/3/5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[단어/N/null/1/1/6/8]", tokens.toString());
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
    
    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testXsn() {
    Node node = mockNodeListFactory(new String[] {
        "의대\tNNG,*,F,의대,*,*,*,*,*",
        "생\tXSN,*,T,생,*,*,*,*,*",

    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[의대/N/null/1/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[생/XSN/null/1/1/2/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testSentenceWithXsn() {
    Node node = mockNodeListFactory(new String[] {
        "공대\tNNG,*,F,공대,*,*,*,*,*",
        "생\tXSN,*,T,생,*,*,*,*,*",
        "은\tJX,*,T,은,*,*,*,*,*",
        " 바쁘\tVA,*,F,바쁘,*,*,*,*,*",
        "다\tEF,*,F,다,*,*,*,*,*",
    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[공대/N/null/1/1/0/2]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[생은/EOJEOL/null/1/1/2/4, 생/XSN/null/0/1/2/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[바쁘다/EOJEOL/null/1/1/5/8]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }

  @Test
  public void testXpn() {
    Node node = mockNodeListFactory(new String[] {
        "왕\tXPN,*,T,왕,*,*,*,*,*",
        "게임\tNNG,*,T,게임,*,*,*,*,*",
    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[왕/XPN/null/1/1/0/1]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[게임/N/null/1/1/1/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
  }
  
  @Test
  public void testSemanticClass() {
    Node node = mockNodeListFactory(new String[] {
        "남희석\tNNP,인명,T,남희석,*,*,*,*,*",
        "은\tJX,*,T,은,*,*,*,*,*",
        " 충남\tNNP,지명,T,충남,*,*,*,*,*",
        "사람\tNNG,*,T,사람,*,*,*,*,*",
        "이\tVCP,*,F,이,*,*,*,*,*",
        "다\tEF,*,F,다,*,*,*,*,*",
        ".\tSF,*,*,*,*,*,*,*,*",
    });

    TokenGenerator generator =
        new TokenGenerator(new StandardPosAppender(), 4, node);

    List<Pos> tokens;
    tokens = generator.getNextEojeolTokens();
    assertEquals("[남희석은/EOJEOL/null/1/1/0/4, 남희석/N/인명/0/1/0/3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[충남/N/지명/1/1/5/7]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[사람이다/EOJEOL/null/1/1/7/11, 사람/N/null/0/1/7/9]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
    
  }
}