package com.github.bibreen.mecab_ko_lucene_analyzer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.chasen.mecab.Node;

import com.github.bibreen.mecab_ko_lucene_analyzer.PosIdManager.PosId;

/**
 * MeCab의 node를 받아서 Lucene tokenizer에 사용될 TokenInfo 리스트를 생성하는 클래스.
 * 
 * @author bibreen <bibreen@gmail.com>
 * @author amitabul <mousegood@gmail.com>
 */
public class TokenGenerator {
  PosAppender appender;
  
  private Node curNode;
  private Pos prev = null;
  private ArrayList<Pos> posList = new ArrayList<Pos>();
  
  private boolean needNounDecompound = true;
  private Queue<Pos> decompoundNounsQueue;
  
  public TokenGenerator(Node beginNode) {
    if (beginNode != null)
      this.curNode = beginNode.getNext();
    appender = new StandardPosAppender();
    decompoundNounsQueue = new LinkedList<Pos>();
  }
  
  /**
   * 다음 Token들을 반환한다.
   * @return 반환 값이 null이면 generator 종료이다.
   */
  public LinkedList<TokenInfo> getNextEojeolTokens() {
    while (curNode != null) {
      Pos curPos;
      if (decompoundNounsQueue.isEmpty()) {
        curPos = new Pos(curNode, getPrevEndOffset());
        curNode = curNode.getNext();
        if (needNounDecompound && curPos.isPosIdOf(PosId.COMPOUND)) {
          decompoundNoun(curPos);
          continue;
        }
      } else {
        curPos = decompoundNounsQueue.poll();
      }
      
      if (!append(curPos)) {
        LinkedList<TokenInfo> tokens = makeTokens();
        if (tokens != null) {
          return tokens;
        } else {
          continue;
        }
      }
    }
    // return last tokens
    return makeTokens();
  }

  private int getPrevEndOffset() {
    if (prev != null) {
      return prev.getEndOffset();
    } else {
      return 0;
    }
  }
  
  private void decompoundNoun(Pos pos) {
    String expression = pos.getExpression();
    if (expression == null) return;
   
    String nouns[] = expression.split("\\+");
    int startOffset = pos.getStartOffset();
    for (int i = 0; i < nouns.length; ++i) {
      Pos noun = new Pos(nouns[i], PosId.NN, startOffset);
      if (i < nouns.length - 1) {
        decompoundNounsQueue.add(noun);
      } else {
        pos.setSamePositionPos(noun);
        decompoundNounsQueue.add(pos);
      }
      startOffset = noun.getEndOffset();
    }
  }
  
  /**
   * POS를 현재 어절에 포함시킬 수 있다면 포함시키고, true를 반환하고,
   * 그렇지 않다면 false를 반환한다.
   * 유닛 테스트 때문에 public이다.
   */
  public boolean append(Pos pos) {
    if (posList.isEmpty() && prev != null) {
      posList.add(prev);
    }
    
    if (posList.isEmpty() || isAppendable(pos)) {
      prev = pos;
      posList.add(pos);
      return true;
    } else {
      prev = pos;
      return false;
    }
  }
  
  private boolean isAppendable(Pos curPos) {
    Pos prevPos = posList.get(posList.size() - 1);
    return appender.isAppendable(prevPos, curPos);
  }
 
  /**
   * poses에 있는 Pos를 조합하여, Token을 뽑아낸다.
   * @return token이 있을 경우 TokenInfo의 리스트를 반환하고, 뽑아낼 token이 없을 경우
   * null을 반환한다.
   */
  private LinkedList<TokenInfo> makeTokens() {
    if (posList.isEmpty()) {
      return null;
    }
    
    if (isSkippablePoses()) {
      clearPoses();
      return null;
    }
    
    LinkedList<TokenInfo> result = new LinkedList<TokenInfo>();
    int startOffset = posList.get(0).getStartOffset();
    String str = "";
    for (Pos pos: posList) {
      addAdditinalToken(result, pos);
      str += pos.getSurface();
    }
    int endOffset = posList.get(posList.size() - 1).getEndOffset();
    result.addFirst(new TokenInfo(str, 1, new Offsets(startOffset, endOffset)));
    
    clearPoses();
    return result;
  }

  private void addAdditinalToken(LinkedList<TokenInfo> result, Pos pos) {
    if (posList.size() > 1 && isAbsolutePos(pos)) {
      // 독립적인 Token이 되어야 하는 품사는 incrPos=0 으로 미리 넣어둔다.
      result.add(new TokenInfo(pos, 0));
    }
    Pos samePositionPos = pos.getSamePositionPos();
    if (samePositionPos != null) result.add(new TokenInfo(samePositionPos, 0));
  }

  private boolean isAbsolutePos(Pos pos) {
    return appender.isAbsolutePos(pos);
  }
  
  private void clearPoses() {
    posList.clear();
  }
  
  private boolean isSkippablePoses() {
    // 단독으로 쓰인 심볼은 Token 생성 제외한다.
    if (posList.size() == 1) {
      return appender.isSkippablePos(posList.get(0));
    } else {
      return false;
    }
  }
}