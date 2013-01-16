package com.github.bibreen.mecab_ko_lucene_analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.chasen.mecab.Node;

import com.github.bibreen.mecab_ko_lucene_analyzer.Pos.Tag;

public class EojeolGenerator {
  
  static public Set<Appendable> appendableSet;
  
  static {
    // 접두사(XP) + 체언(N*)
    // 어근(XR) + E [+ E]*
    // 용언(V*) + E [+ E]*
    // 체언(N*) + 명사 파생 접미사(XSN)
    // 체언(N*) + 조사 [+ 조사]*
     appendableSet = new HashSet<Appendable>();
     appendableSet.add(new Appendable(Tag.XP, Tag.N));
     appendableSet.add(new Appendable(Tag.XR, Tag.E));
     appendableSet.add(new Appendable(Tag.E, Tag.E));
     appendableSet.add(new Appendable(Tag.V, Tag.E));
     appendableSet.add(new Appendable(Tag.N, Tag.XSN));
     appendableSet.add(new Appendable(Tag.N, Tag.JO));
     appendableSet.add(new Appendable(Tag.JO, Tag.JO));
  }
  
  private Pos prev = null;
  private boolean isStarted = false;
  private List<Pos> poses = new ArrayList<Pos>();
  private int prevEojeolEndOffset = 0;
 
  private Node curNode;
  
  public EojeolGenerator(Node beginNode) {
    if (beginNode != null)
      this.curNode = beginNode.getNext();
  }
  
  /**
   * 다음 Token들을 반환한다.
   * @return 반환 값이 null이면 generator 종료이다.
   */
  public LinkedList<TokenInfo> getNextTokens() {
    while (curNode != null) {
      Pos pos = new Pos(curNode);
      if (pos.getTag() != Tag.OTHER) {
        insert(new Pos(curNode));
        if (isStarted()) {
          return getTokenInfo();
        }
      }
      curNode = curNode.getNext();
    }
    // return last tokens
    return getTokenInfo();
  }
  
  public void insert(Pos pos) {
    if (prev == null || isAppendable(pos)) {
      isStarted = false;
      prev = pos;
      poses.add(pos);
    } else {
      isStarted = true;
      prev = null;
    }
  }
  
  private boolean isAppendable(Pos cur) {
    return appendableSet.contains(new Appendable(prev.getTag(), cur.getTag()));
  }
  
  public boolean isStarted() {
    return isStarted;
  }
  
  public LinkedList<TokenInfo> getTokenInfo() {
    // TODO: 리팩토링 필요
    if (poses.isEmpty()) {
      return null;
    }
    LinkedList<TokenInfo> result = new LinkedList<TokenInfo>();
    
    Node firstNode = poses.get(0).getNode();
    int startOffset = prevEojeolEndOffset + spaceLen(firstNode);
    
    String str = "";
    int length = 0;
    for (Pos pos: poses) {
      Node node = pos.getNode();
      System.out.println(node.getSurface());
      String surface = node.getSurface();
      
      if (poses.size() > 1 &&
          (pos.getTag() == Tag.N || pos.getTag() == Tag.XR) /*메서드로 분리*/) {
        // 명사와 어근은 유의어로 미리 넣어둔다.
        int start = prevEojeolEndOffset + length + spaceLen(pos.getNode()) ;
        int end = start + surface.length();
        result.add(
            new TokenInfo( surface, 0, new Offsets(start, end), false));
      }
      
      str += surface;
      length += spaceLen(node) + surface.length();
    }
    int endOffset = prevEojeolEndOffset + length;
    
    result.addFirst(
        new TokenInfo(str, 1, new Offsets(startOffset, endOffset), false));
    
    prevEojeolEndOffset = endOffset;
    poses.clear();
    return result;
  }
  
  static private int spaceLen(Node node) {
    return node.getRlength() - node.getLength();
  }
 
  /**
   * 해당 node 표층형 문자열의 실제 길이를 반환한다.
   * node.length()는 byte 길이이므로 사용할 수 없다.
   */
  static private int surfaceLen(Node node) {
    return node.getSurface().length();
  }
}
