package com.github.bibreen.mecab_ko_lucene_analyzer;

import com.github.bibreen.mecab_ko_lucene_analyzer.PosIdManager.PosId;

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