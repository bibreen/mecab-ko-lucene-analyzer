package com.github.bibreen.mecab_ko_lucene_analyzer;

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