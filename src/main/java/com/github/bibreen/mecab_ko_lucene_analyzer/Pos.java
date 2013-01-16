package com.github.bibreen.mecab_ko_lucene_analyzer;

import org.chasen.mecab.Node;

public class Pos {
  enum Tag {
    XP,
    N,
    XR,
    E,
    V,
    XSN,
    JO,
    OTHER,
  }
  
  private Tag tag;
  private Node node;
  
  public Pos(Tag tag) {
    this.tag = tag;
  }
  
  public Pos(Node node) {
    this.node = node;
  }
  
  public Node getNode()  {
    return node;
  }
  
  public Tag getTag() {
    return tag;
  }
}
