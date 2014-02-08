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


public class SemanticClassAttributeImpl extends AttributeImpl implements
    SemanticClassAttribute, Cloneable {

  private String semanticClass;

  @Override
  public String semanticClass() {
    return semanticClass;
  }

  @Override
  public void setSemanticClass(String semanticClass) {
    this.semanticClass = semanticClass;
  }

  @Override
  public void clear() {
    this.semanticClass = null;

  }

  @Override
  public void copyTo(AttributeImpl target) {
    SemanticClassAttribute targetAttribute = (SemanticClassAttribute) target;
    targetAttribute.setSemanticClass(semanticClass);
  }
  
  @Override
  public void reflectWith(AttributeReflector reflector) {                        
    reflector.reflect(SemanticClassAttribute.class, "semanticClass", semanticClass());
  }
}
