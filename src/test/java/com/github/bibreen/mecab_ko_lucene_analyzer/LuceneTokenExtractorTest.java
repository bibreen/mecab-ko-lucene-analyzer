package com.github.bibreen.mecab_ko_lucene_analyzer;

import static org.junit.Assert.assertEquals;

import java.util.EnumSet;
import java.util.SortedSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.bibreen.mecab_ko_lucene_analyzer.LuceneTokenExtractor.Option;
import com.github.bibreen.mecab_ko_lucene_analyzer.LuceneTokenExtractor.TokenInfo;

public class LuceneTokenExtractorTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testOne() throws Exception {
    LuceneTokenExtractor extractor = new LuceneTokenExtractor(
        EnumSet.of(
            Option.EXTRACT_STEMMING_ENGLISH,
            Option.EXTRACT_DECOMPOSED_NOUN,
            Option.EXTRACT_EOJEOL));
    
    SortedSet<TokenInfo> tokens = extractor.extract("아! 그러나 불완전 아주 황당한 무궁화 꽃이 피었습니다.");
    System.out.println(tokens);
//    assertEquals(
//        "[아:1:0:3, 그러나:1:5:14, 아주:1:15:21, 무궁화:1:22:31, 꽃:1:32:35, 꽃이:0:32:38, 피었습니다:1:39:54]", 
//        tokens.toString());
    tokens = extractor.extract("꽃이 황당하다. 학동에서도 있다..");
    System.out.println(tokens);
  }
  
  @Test
  public void testNoEojeol() throws Exception {
    LuceneTokenExtractor extractor = new LuceneTokenExtractor(
        EnumSet.of(
            Option.EXTRACT_STEMMING_ENGLISH,
            Option.EXTRACT_DECOMPOSED_NOUN));
    
    SortedSet<TokenInfo> tokens = extractor.extract("무궁화 꽃이 피었습니다.");
    assertEquals(
        "[무궁화:1:0:9, 꽃:1:10:13]", 
        tokens.toString());
  }
  

}
