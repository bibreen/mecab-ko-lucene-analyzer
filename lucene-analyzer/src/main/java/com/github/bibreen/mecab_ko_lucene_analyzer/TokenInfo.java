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
 * Tokenizer에서 사용될 토큰 정보를 갖는 자료 전달 객체.
 * @author bibreen <bibreen@gmail.com>
 */
public class TokenInfo {
  private String term;
  private PosId posId;
  private int positionIncr;
  private int positionLength;
  private Offsets offsets;

  public TokenInfo(
      String term,
      PosId posId,
      int positionIncr,
      int positionLength,
      Offsets offsets) {
    this.term = term;
    this.posId = posId;
    this.positionIncr = positionIncr;
    this.positionLength = positionLength;
    this.offsets = offsets;
  }
  
  public TokenInfo(
      String term,
      PosId posId,
      int positionIncr,
      int positionLength,
      int startOffset) {
    this(
        term,
        posId,
        positionIncr,
        positionLength,
        new Offsets(startOffset, startOffset + term.length()));
  }
  
//  public TokenInfo(
//      String term, PosId posId, int positionIncr, Offsets offsets) {
//    this(term, posId, positionIncr, 1, offsets);
//  }

  public TokenInfo(Pos pos, int positionIncr) {
    this(
        pos.getSurface(),
        pos.getPosId(),
        positionIncr,
        pos.getPositionLength(),
        new Offsets(pos.getStartOffset(), pos.getEndOffset()));
  }

  public String getTerm() {
    return term;
  }

  public int getPositionIncr() {
    return positionIncr;
  }
  
  public int getPositionLength() {
    return positionLength;
  }

  public Offsets getOffsets() {
    return offsets;
  }
  
  public String getPosTag() {
    return posId.toString();
  }
  
  public void setPositionIncr(int positionIncr) {
    this.positionIncr = positionIncr;
  }
  
  public void setOffsets(Offsets offsets) {
    this.offsets = offsets;
  }
  
  public void setStartOffset(int startOffset) {
    this.offsets.start = startOffset;
    this.offsets.end = startOffset + term.length();
  }

  @Override
  public String toString() {
    return new String(
        term + "/" + getPosTag() + "/" +
        positionIncr + "/" + positionLength + "/" +
        offsets.start + "/" + offsets.end);
  }
}
