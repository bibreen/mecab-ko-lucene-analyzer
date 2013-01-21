package com.github.bibreen.mecab_ko_lucene_analyzer;

import java.io.Reader;
import java.util.EnumSet;
import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;

import com.github.bibreen.mecab_ko_lucene_analyzer.LuceneTokenExtractor.Option;

public class MecabKoTokenizerFactory extends TokenizerFactory {
  @Override
  public void init(Map<String,String> args) {
  }

  @Override
  public Tokenizer create(Reader input) {
    EnumSet<Option> options = EnumSet.allOf(Option.class);
    return new MecabTokenizer(input, options);
  }
  

}
