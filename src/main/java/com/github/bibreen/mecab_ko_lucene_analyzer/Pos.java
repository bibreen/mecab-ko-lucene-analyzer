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
    this.tag = convertToTag(node.getPosid());
  }
  
  public Node getNode()  {
    return node;
  }
  
  public Tag getTag() {
    return tag;
  }
  
  private static Tag convertToTag(int posId) {
    // TODO: pos-id가 숫자로 적혀있음 나중에 빼야함
    // 외국어처리 빠져있음.
    if (109 <= posId && posId <= 113) { 
      return Tag.N;
    } else if (posId == 101) {
      return Tag.E;
    } else if (posId == 103) {
      return Tag.JO;
    } else if (125 <= posId && posId <= 132) {
      return Tag.V;
    } else if (133 <= posId && posId <= 134) {
      return Tag.XP;
    } else if (posId == 135) {
      return Tag.XR;
    } else if (138 <= posId && posId <= 139) {
      return Tag.XSN;
    } else {
      return Tag.OTHER;
    }
  }
}