package com.github.bibreen.mecab_ko_lucene_analyzer.tokenattributes;

import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

public class MophemesAttributeImpl extends AttributeImpl implements
    MophemesAttribute, Cloneable {

  private String mophemes;

  @Override
  public String mophemes() {
    return mophemes;
  }

  @Override
  public void setMophemes(String mophemes) {
    this.mophemes = mophemes;
  }

  @Override
  public void clear() {
    this.mophemes = null;
  }

  @Override
  public void copyTo(AttributeImpl target) {
    MophemesAttribute targetAttribute = (MophemesAttribute) target;
    targetAttribute.setMophemes(mophemes);
  }
  
  @Override
  public void reflectWith(AttributeReflector reflector) {                        
    reflector.reflect(MophemesAttribute.class, "mophemes", mophemes());
  }

}