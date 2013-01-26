package com.github.bibreen.mecab_ko_lucene_analyzer;

/**
 * TokenGenerator에서 token으로 뽑는 품사와 품사의 연결과 품사의 선택의 알고리즘을
 * 담당하는 추상 클래스.
 * 
 * @author bibreen <bibreen@gmail.com>
 */
public abstract class PosAppender {
  /**
   * left PosId와 right PosId가 붙을 수 있는 품사인지 여부를 반환한다.
   */
  public abstract boolean isAppendable(Pos left, Pos right);
  /**
   * 해당 Pos가 단독으로 Token으로 뽑힐 수 있는 품사일 경우 true, 아니면 false를
   * 반환한다.
   */
  public abstract boolean isAbsolutePos(Pos pos);
  /**
   * 해당 Pos가 단독 Token으로 생성될 수 없는 품사인 경우 true, 아니면 false를 반환한다.
   */
  public abstract boolean isSkippablePos(Pos pos);
}
