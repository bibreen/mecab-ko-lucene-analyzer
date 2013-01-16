package com.github.bibreen.mecab_ko_lucene_analyzer;

import com.github.bibreen.mecab_ko_lucene_analyzer.Pos.Tag;

public class Appendable {
  private Tag left;
  private Tag right;
  
  public Appendable(Tag left, Tag right) {
    this.left = left;
    this.right = right;
  }
  
  public Tag getLeft() {
    return left;
  }
  
  public Tag getRight() {
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


