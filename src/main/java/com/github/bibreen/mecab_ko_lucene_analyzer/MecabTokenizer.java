package com.github.bibreen.mecab_ko_lucene_analyzer;

import java.io.IOException;
import java.io.Reader;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import com.github.bibreen.mecab_ko_lucene_analyzer.LuceneTokenExtractor.Option;

public class MecabTokenizer extends Tokenizer {

  private CharTermAttribute charTermAtt;
  private PositionIncrementAttribute posIncrAtt;
  private OffsetAttribute offsetAtt;
  
  private String document;
  private LuceneTokenExtractor tokenExtractor;
  private List<TokenInfo> tokens;
  private Iterator<TokenInfo> tokenIterator;
  
  public MecabTokenizer(Reader input) {
    this(
        input,
        EnumSet.of(
            Option.EXTRACT_STEMMING_ENGLISH,
            Option.EXTRACT_DECOMPOSED_NOUN,
            Option.EXTRACT_EOJEOL));
  }

  public MecabTokenizer(Reader input, EnumSet<Option> options) {
    super(input);
    
    tokenExtractor = new LuceneTokenExtractor(options);
    
    charTermAtt = addAttribute(CharTermAttribute.class);
    posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    offsetAtt = addAttribute(OffsetAttribute.class);
  }

  @Override
  public boolean incrementToken() throws IOException {
    if (isBegin()) {
      tokenIterator = extractTokens();
    }
    
    if (tokenIterator.hasNext()) {
      setAttributes(tokenIterator.next());
      return true;
    } else {
      return false;
    }
  }

  private Iterator<TokenInfo> extractTokens() {
    Iterator<TokenInfo> iterator = null;
    try {
      document = getDocument();
      tokens = tokenExtractor.extract(document);
      iterator = tokens.iterator();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return iterator;
  }
  
  private boolean isBegin() {
    return (tokens == null);
  }

  private void setAttributes(TokenInfo token) {
    posIncrAtt.setPositionIncrement(token.getPosIncr());
    offsetAtt.setOffset(
        correctOffset(token.getOffsets().start),
        correctOffset(token.getOffsets().end));
    charTermAtt.copyBuffer(
        token.getTerm().toCharArray(), 0, token.getTerm().length());
  }
  
  @Override
  public final void end() {
    // set final offset
    offsetAtt.setOffset(
        correctOffset(document.length()), correctOffset(document.length()));
    tokens = null;
    document = null;
  }
  
  private String getDocument() throws IOException {
    StringBuffer document = new StringBuffer();
    char[] tmp = new char[1024];
    int len;
    while ((len = input.read(tmp)) != -1) {
      document.append(new String(tmp, 0, len));
    }
    return document.toString();
  }
}
