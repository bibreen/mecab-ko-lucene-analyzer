/*******************************************************************************
 * Copyright 2013 Yongwoon Lee, Yungho Yu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.github.bibreen.mecab_ko_lucene_analyzer.tokenattributes;

import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;


public class SemanticAttributeImpl extends AttributeImpl implements
    SemanticAttribute, Cloneable {

  private String semantic;

  @Override
  public String semantic() {
    return semantic;
  }

  @Override
  public void setSemantic(String semantic) {
    this.semantic = semantic;
  }

  @Override
  public void clear() {
    this.semantic = null;

  }

  @Override
  public void copyTo(AttributeImpl target) {
    SemanticAttribute targetAttribute = (SemanticAttribute) target;
    targetAttribute.setSemantic(semantic);                                                           
  }
  
  @Override
  public void reflectWith(AttributeReflector reflector) {                        
    reflector.reflect(SemanticAttribute.class, "semantic", semantic());
  }
}
