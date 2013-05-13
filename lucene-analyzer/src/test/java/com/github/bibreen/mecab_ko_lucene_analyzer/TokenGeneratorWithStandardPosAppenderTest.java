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
    assertEquals("[진달래:1:0:3]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[꽃이:1:4:6, 꽃:0:4:5]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals("[피었습니다:1:7:12]", tokens.toString());
    tokens = generator.getNextEojeolTokens();
    assertEquals(null, tokens);
	}
}
