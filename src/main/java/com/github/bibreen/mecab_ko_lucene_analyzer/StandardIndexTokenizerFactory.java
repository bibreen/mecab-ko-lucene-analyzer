package com.github.bibreen.mecab_ko_lucene_analyzer;

import java.io.Reader;
import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;

public class StandardIndexTokenizerFactory extends TokenizerFactory {
  @Override
  public void init(Map<String,String> args) {
  }

  @Override
  public Tokenizer create(Reader input) {
    return new MeCabKoTokenizer(input, new StandardPosAppender(), true);
  }
}
