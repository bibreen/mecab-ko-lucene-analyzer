package com.github.bibreen.mecab_ko_lucene_analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.chasen.mecab.Node;

import com.github.bibreen.mecab_ko_lucene_analyzer.Pos.Tag;

public class TokenGenerator {
  
  static public Set<Appendable> appendableSet;
  
  static {
    // 체언접두사(XPN) + 체언(N*)
    // 어근(XR) + E [+ E]*
    // 용언(V*) + E [+ E]*
    // 체언(N*) + 명사 파생 접미사(XSN)
    // 체언(N*) + 조사 [+ 조사]*
     appendableSet = new HashSet<Appendable>();
     appendableSet.add(new Appendable(Tag.XPN, Tag.N));
     appendableSet.add(new Appendable(Tag.XR, Tag.E));
     appendableSet.add(new Appendable(Tag.E, Tag.E));
     appendableSet.add(new Appendable(Tag.V, Tag.E));
     appendableSet.add(new Appendable(Tag.N, Tag.XSN));
     appendableSet.add(new Appendable(Tag.N, Tag.J));
     appendableSet.add(new Appendable(Tag.J, Tag.J));
  }
  
  private Node curNode;
  private Pos prev = null;
  private int prevEojeolEndOffset = 0;
  private List<Pos> poses = new ArrayList<Pos>();
  
  public TokenGenerator(Node beginNode) {
    if (beginNode != null)
      this.curNode = beginNode.getNext();
  }
  
  /**
   * 다음 Token들을 반환한다.
   * @return 반환 값이 null이면 generator 종료이다.
   */
  public LinkedList<TokenInfo> getNextEojeolTokens() {
    while (curNode != null) {
      Pos pos = new Pos(curNode);
      if (willBeExtracted(pos)) {
        if (!append(new Pos(curNode))) {
          return makeTokens();
        }
      }
      curNode = curNode.getNext();
    }
    // return last tokens
    return makeTokens();
  }
  
  private boolean willBeExtracted(Pos pos) {
    return pos.getTag() != Tag.OTHER;
  }
 
  /**
   * POS를 현재 어절에 포함시킬 수 있다면 포함시키고, true를 반환하고,
   * 그렇지 않다면 false를 반환한다.
   */
  public boolean append(Pos pos) {
    // 유닛 테스트 때문에 public
    if (prev == null || isAppendable(pos)) {
      prev = pos;
      poses.add(pos);
      return true;
    } else {
      prev = null;
      return false;
    }
  }
  
  private boolean isAppendable(Pos cur) {
    if (cur.getNode() != null && spaceLen(cur.getNode()) > 0) return false;
    return appendableSet.contains(new Appendable(prev.getTag(), cur.getTag()));
  }
  
  private LinkedList<TokenInfo> makeTokens() {
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
      String surface = node.getSurface();
      
      if (poses.size() > 1 && (pos.isTagOf(Tag.N) || pos.isTagOf(Tag.XR))) {
        // 명사와 어근은 incrPos=0 으로 미리 넣어둔다.
        int start = prevEojeolEndOffset + length + spaceLen(pos.getNode()) ;
        int end = start + surface.length();
        result.add(
            new TokenInfo(surface, 0, new Offsets(start, end), false));
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
}
