package com.github.bibreen.mecab_ko_lucene_analyzer.tokenattributes;

import org.apache.lucene.util.Attribute;

public interface PartOfSpeechAttribute extends Attribute {
  public String partOfSpeech();
  public void setPartOfSpeech(String partOfSpeech);
}
