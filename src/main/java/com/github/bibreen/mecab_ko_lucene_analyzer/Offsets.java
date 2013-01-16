package com.github.bibreen.mecab_ko_lucene_analyzer;

public class Offsets {
  public int start;
  public int end;

  public Offsets(int s, int e) {
    start = s;
    end = e;
  }

  @Override
  public String toString() {
    return new String("Offsets(" + start + ":" + end + ")");
  }
}
