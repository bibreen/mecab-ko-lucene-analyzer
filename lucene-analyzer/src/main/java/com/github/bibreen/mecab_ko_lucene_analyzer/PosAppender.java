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

import java.util.LinkedList;

/**
 * TokenGenerator에서 token으로 뽑는 품사와 품사의 연접과 token으로 뽑는 품사의 선택
 * 알고리즘을 담당하는 추상 클래스.
 * 
 * @author bibreen <bibreen@gmail.com>
 */
public abstract class PosAppender {
  /**
   * left PosId와 right PosId가 붙을 수 있는 품사인지 여부를 반환한다.
   */
  public abstract boolean isAppendable(Pos left, Pos right);
  /**
   * 해당 Pos가 단독 Token으로 생성될 수 없는 품사인 경우 true, 아니면 false를 반환한다.
   */
  public abstract boolean isSkippablePos(Pos pos);
  /**
   * 추가적인 Pos가 Token으로 뽑혀야 하는 경우 해당 Pos 리스트를 반환한다.
   *
   * @param eojeolTokens 어절을 구성하는 Pos 리스트
   */
  public abstract LinkedList<Pos> getAdditionalPoses(
      LinkedList<Pos> eojeolTokens);
}
