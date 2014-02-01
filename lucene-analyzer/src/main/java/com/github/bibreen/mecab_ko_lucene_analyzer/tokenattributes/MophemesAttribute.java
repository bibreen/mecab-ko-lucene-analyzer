package com.github.bibreen.mecab_ko_lucene_analyzer.tokenattributes;

import org.apache.lucene.util.Attribute;

public interface MophemesAttribute extends Attribute {
  public String mophemes();
  public void setMophemes(String mophemes);
}
