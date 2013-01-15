package com.github.bibreen.mecab_ko_lucene_analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.SortedSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    LuceneTokenExtractor extractor = 
        new LuceneTokenExtractor("/home/amitabul/mecab-ko-dic");
    
    SortedSet<TokenInfo> tokens = extractor.extract("무궁화 꽃이 피었습니다.");
    assertEquals(
        "[무궁화:1:0:9, 꽃:1:10:13, 꽃이:0:10:16, 피었습니다:1:17:32]", 
        tokens.toString());
  }

}
