package com.github.bibreen.mecab_ko_lucene_analyzer;

import org.chasen.mecab.Node;

public class Pos {
  enum Tag {
    IC,
    XPN,
    N,
    XR,
    E,
    V,
    XSN,
    J,
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
  
  public boolean isTagOf(Tag tag) {
    return this.tag == tag;
  }
  
  public static Tag convertToTag(int posId) {
    // TODO: pos-id가 숫자로 적혀있음 나중에 빼야함
    // 외국어처리 빠져있음.
    if (107 <= posId && posId <= 110) { 
      return Tag.N;
    } else if (posId == 101) {
      return Tag.E;
    } else if (posId == 102) {
      return Tag.IC;
    } else if (posId == 103) {
      return Tag.J;
    } else if (122 <= posId && posId <= 127) {
      return Tag.V;
//    } else if (133 <= posId && posId <= 134) {
    } else if (posId == 128) {
      return Tag.XPN;
    } else if (posId == 129) {
      return Tag.XR;
    } else if (posId == 131) {
      return Tag.XSN;
    } else {
      return Tag.OTHER;
    }
  }
}