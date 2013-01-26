package com.github.bibreen.mecab_ko_lucene_analyzer;

public class TokenInfo implements Comparable<TokenInfo> {
  public static final long NO_INCREMENT_POSITION = -1L;
  private String term;
  private int posIncr;
  private Offsets offsets;

  public TokenInfo(String term, int posIncr, Offsets offsets) {
    this.term = term;
    this.posIncr = posIncr;
    this.offsets = offsets;
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
  public int compareTo(TokenInfo t) {
    if (offsets.end == t.getOffsets().end) {
      // end가 같은 경우에는 보다 긴 term(start가 작은 term)이 상위이다.
      // start와 end가 동일한 term은 SortedSet에 들어갈 필요가 없다.
      return t.getOffsets().start - offsets.start;
    }
    return offsets.end - t.getOffsets().end;
  }

  @Override
  public String toString() {
    return new String(term + ":" + posIncr + ":" + offsets.start + ":" + offsets.end);
  }
}