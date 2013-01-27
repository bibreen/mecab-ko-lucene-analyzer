package com.github.bibreen.mecab_ko_lucene_analyzer;

import java.util.ArrayList;
import java.util.LinkedList;

import org.chasen.mecab.Node;

public class TokenGenerator {
  PosAppender appender;
  
  private Node curNode;
  private Pos prev = null;
  private ArrayList<Pos> poses = new ArrayList<Pos>();
  
  public TokenGenerator(Node beginNode) {
    if (beginNode != null)
      this.curNode = beginNode.getNext();
    appender = new StandardPosAppender();
  }
  
  /**
   * 다음 Token들을 반환한다.
   * @return 반환 값이 null이면 generator 종료이다.
   */
  public LinkedList<TokenInfo> getNextEojeolTokens() {
    while (curNode != null) {
      int prevEndOffset = 0;
      if (prev != null) prevEndOffset = prev.getEndOffset();
      
      if (!append(new Pos(curNode, prevEndOffset))) {
        curNode = curNode.getNext();
        LinkedList<TokenInfo> tokens = makeTokens();
        if (tokens != null) {
          return tokens;
        } else {
          continue;
        }
      } else {
        curNode = curNode.getNext();
      }
    }
    // return last tokens
    return makeTokens();
  }
  
  /**
   * POS를 현재 어절에 포함시킬 수 있다면 포함시키고, true를 반환하고,
   * 그렇지 않다면 false를 반환한다.
   * 유닛 테스트 때문에 public이다.
   */
  public boolean append(Pos pos) {
    if (poses.isEmpty() && prev != null) {
      poses.add(prev);
    }
    
    if (poses.isEmpty() || isAppendable(pos)) {
      prev = pos;
      poses.add(pos);
      return true;
    } else {
      prev = pos;
      return false;
    }
  }
  
  private boolean isAppendable(Pos curPos) {
    Pos prevPos = poses.get(poses.size() - 1);
    return appender.isAppendable(prevPos, curPos);
  }
 
  /**
   * poses에 있는 Pos를 조합하여, Token을 뽑아낸다.
   * @return token이 있을 경우 TokenInfo의 리스트를 반환하고, 뽑아낼 token이 없을 경우
   * null을 반환한다.
   */
  private LinkedList<TokenInfo> makeTokens() {
    if (poses.isEmpty()) {
      return null;
    }
    
    if (isSkippablePoses()) {
      clearPoses();
      return null;
    }
    
    LinkedList<TokenInfo> result = new LinkedList<TokenInfo>();
    int startOffset = poses.get(0).getStartOffset();
    String str = "";
    for (Pos pos: poses) {
      addAdditinalToken(result, pos);
      str += pos.getSurface();
    }
    int endOffset = poses.get(poses.size() - 1).getEndOffset();
    result.addFirst(new TokenInfo(str, 1, new Offsets(startOffset, endOffset)));
    
    clearPoses();
    return result;
  }

  private void addAdditinalToken(LinkedList<TokenInfo> result, Pos pos) {
    if (poses.size() > 1 && isAbsolutePos(pos)) {
      // 독립적인 Token이 되어야 하는 품사는 incrPos=0 으로 미리 넣어둔다.
      Offsets offsets = new Offsets(pos.getStartOffset(), pos.getEndOffset());
      result.add(new TokenInfo(pos.getSurface(), 0, offsets));
    }
  }

  private boolean isAbsolutePos(Pos pos) {
    return appender.isAbsolutePos(pos);
  }
  
  private void clearPoses() {
    poses.clear();
  }
  
//  private int getPosesLength() {
//    int length = 0;
//    for (Pos pos: poses) {
//      length += pos.getLength();
//    }
//    return length;
//  }
  
  private boolean isSkippablePoses() {
    // 단독으로 쓰인 심볼은 Token 생성 제외한다.
    if (poses.size() == 1) {
      return appender.isSkippablePos(poses.get(0));
    } else {
      return false;
    }
  }
}