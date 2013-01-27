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
    tokenizer.close();
  }
}
