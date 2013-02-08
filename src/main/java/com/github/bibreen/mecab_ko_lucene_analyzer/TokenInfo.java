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

/**
 * Tokenizer에서 사용될 토큰 정보를 갖는 클래스.
 * @author bibreen <bibreen@gmail.com>
 */
public class TokenInfo {
  private String term;
  private int posIncr;
  private Offsets offsets;

  public TokenInfo(String term, int posIncr, Offsets offsets) {
    this.term = term;
    this.posIncr = posIncr;
    this.offsets = offsets;
  }
  
  public TokenInfo(Pos pos, int posIncr) {
    this(
        pos.getSurface(),
        posIncr,
        new Offsets(pos.getStartOffset(), pos.getEndOffset()));
  }

  public String getTerm() {
    return term;
  }

  public int getPosIncr() {
    return posIncr;
  }

  public Offsets getOffsets() {
    return offsets;
  }

  @Override
  public String toString() {
    return new String(
        term + ":" + posIncr + ":" + offsets.start + ":" + offsets.end);
  }
}
