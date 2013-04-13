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
package com.github.bibreen.mecab_ko_lucene_analyzer;

import com.github.bibreen.mecab_ko_lucene_analyzer.PosIdManager.PosId;

/**
 * 연접 가능한 PosId를 저장하는 클래스
 */
public class Appendable {
  private PosId left;
  private PosId right;
  
  public Appendable(PosId left, PosId right) {
    this.left = left;
    this.right = right;
  }
  
  public PosId getLeft() {
    return left;
  }
  
  public PosId getRight() {
    return right;
  }
  
  @Override
  public int hashCode() { return left.hashCode() ^ right.hashCode(); }

  @Override
  public boolean equals(Object o) {
    if (o == null) return false;
    if (!(o instanceof Appendable)) return false;
    Appendable appendableObject = (Appendable) o;
    return this.left.equals(appendableObject.getLeft()) &&
           this.right.equals(appendableObject.getRight());
  }
}
