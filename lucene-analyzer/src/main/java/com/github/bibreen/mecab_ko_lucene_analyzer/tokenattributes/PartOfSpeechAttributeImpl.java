package com.github.bibreen.mecab_ko_lucene_analyzer.tokenattributes;

import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

public class PartOfSpeechAttributeImpl extends AttributeImpl implements
    PartOfSpeechAttribute, Cloneable {

  private String partOfSpeech;

  @Override
  public String partOfSpeech() {
    return partOfSpeech;
  }

  @Override
  public void setMophemes(String partOfSpeech) {
    this.partOfSpeech = partOfSpeech;
  }

  @Override
  public void clear() {
    this.partOfSpeech = null;
  }

  @Override
  public void copyTo(AttributeImpl target) {
    PartOfSpeechAttribute targetAttribute = (PartOfSpeechAttribute) target;
    targetAttribute.setMophemes(partOfSpeech);
  }
  
  @Override
  public void reflectWith(AttributeReflector reflector) {                        
    reflector.reflect(PartOfSpeechAttribute.class, "partOfSpeech", partOfSpeech());
  }

}