/*******************************************************************************
 * Copyright 2013 Yongwoon Lee, Yungho Yu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.github.bibreen.mecab_ko_lucene_analyzer;

import java.util.*;

import org.chasen.mecab.Node;

import com.github.bibreen.mecab_ko_lucene_analyzer.PosIdManager.PosId;

/**
 * MeCab의 node를 받아서 Lucene tokenizer에 사용될 TokenInfo 리스트를 생성하는 클래스.
 * 
 * @author bibreen <bibreen@gmail.com>
 * @author amitabul <mousegood@gmail.com>
 */
public class TokenGenerator {
  public static final int NO_DECOMPOUND = 9999;
  public static final int DEFAULT_DECOMPOUND = 2;
  
  PosAppender appender;
  
  private Node curNode;
  private Pos lastPos = null;
  private ArrayList<Pos> posList = new ArrayList<Pos>();
  
  private int decompoundMinLength;
  private Queue<Pos> decompoundedNounsQueue;
 
  /**
   * TokenGenerator 생성자
   * 
   * @param appender PosAppender
   * @param decompoundMinLength 복합명사에서 분해할 명사의 최소길이.
   * 복합명사 분해가 필요없는 경우, TokenGenerator.NO_DECOMPOUND를 입력한다.
   * @param beginNode
   */
  public TokenGenerator(
      PosAppender appender, int decompoundMinLength, Node beginNode) {
    if (beginNode != null)
      this.curNode = beginNode.getNext();
    this.appender = appender;
    this.decompoundMinLength = decompoundMinLength;
    decompoundedNounsQueue = new LinkedList<Pos>();
  }
  
  /**
   * 다음 Token들을 반환한다.
   * @return 반환 값이 null이면 generator 종료이다.
   */
  public LinkedList<TokenInfo> getNextEojeolTokens() {
    while (curNode != null) {
      Pos curPos;
      if (decompoundedNounsQueue.isEmpty()) {
        curPos = new Pos(curNode, getLastPosEndOffset());
        curNode = curNode.getNext();
        if (curPos.isPosIdOf(PosId.COMPOUND)) {
          decompoundNoun(curPos);
          continue;
        }
      } else {
        curPos = decompoundedNounsQueue.poll();
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

  private int getLastPosEndOffset() {
    if (lastPos != null) {
      return lastPos.getEndOffset();
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
      Pos noun = new Pos(nouns[i], PosId.N, startOffset);
      if (noun.getSurfaceLength() >= decompoundMinLength) {
        decompoundedNounsQueue.add(noun);
      }
      startOffset = noun.getEndOffset();
    }
    if (!decompoundedNounsQueue.isEmpty()) {
      LinkedList<Pos> nounList = (LinkedList<Pos>)decompoundedNounsQueue;
      pos.setSamePositionPos(nounList.getLast());
      nounList.set(nounList.size() - 1, pos);
    } else {
      decompoundedNounsQueue.add(pos);
    }
  }
  
  /**
   * POS를 현재 어절에 포함시킬 수 있다면 포함시키고, true를 반환하고,
   * 그렇지 않다면 false를 반환한다.
   * 유닛 테스트 때문에 public이다.
   */
  public boolean append(Pos pos) {
    if (posList.isEmpty() && lastPos != null) {
      posList.add(lastPos);
    }
    
    if (posList.isEmpty() || isAppendable(pos)) {
      lastPos = pos;
      posList.add(pos);
      return true;
    } else {
      lastPos = pos;
      return false;
    }
  }
  
  private boolean isAppendable(Pos curPos) {
    Pos prevPos = posList.get(posList.size() - 1);
    return appender.isAppendable(prevPos, curPos);
  }
 
  /**
   * posList에 있는 Pos를 조합하여, Token을 뽑아낸다.
   * @return token이 있을 경우 TokenInfo의 리스트를 반환하고, 뽑아낼 token이 없을 경우
   * null을 반환한다.
   */
  private LinkedList<TokenInfo> makeTokens() {
    if (posList.isEmpty()) {
      return null;
    }
    
    if (isSkippablePoses()) {
      clearPosList();
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
    boolean isEojeol = posList.size() > 1;
    if (isEojeol) {
      result.addFirst(new TokenInfo(
          str, PosId.EOJEOL, 1, new Offsets(startOffset, endOffset)));
    } else {
      result.addFirst(new TokenInfo(posList.get(0), 1));
    }
    
    clearPosList();
    return result;
  }

  private void addAdditinalToken(LinkedList<TokenInfo> result, Pos pos) {
    addAbsolutePosToken(result, pos);
    addSamePositionPosToken(result, pos);
    addIsolatedJosaToken(result, pos);
  }

  /**
   * 독립적인 Token이 되어야 하는 품사는 incrPos=0 으로 미리 넣어둔다.
   */
  private void addAbsolutePosToken(LinkedList<TokenInfo> result, Pos pos) {
    if (posList.size() > 1 && isAbsolutePos(pos)) {
      result.add(new TokenInfo(pos, 0));
    }
  }
  
  /**
   * 같은 위치에 넣어야되는 품사가 있을 경우(복합명사의 경우), 해당 품사를 icrsPos=0으로
   * 넣어둔다.
   */
  private void addSamePositionPosToken(LinkedList<TokenInfo> result, Pos pos) {
    Pos samePositionPos = pos.getSamePositionPos();
    if (samePositionPos != null) result.add(new TokenInfo(samePositionPos, 0));
  }

  /**
   * '떨어진 조사' 이슈에 대한 처리를 한다.
   * 참고: https://bitbucket.org/bibreen/mecab-ko-dic/issue/1/--------------------
   */
  private void addIsolatedJosaToken(LinkedList<TokenInfo> result, Pos pos) {
    if (pos.getNode() == null || pos.getNode().getPrev() == null) {
      return;
    }
    Node prevNode = pos.getNode().getPrev();
    if (isIsolatedJosa(prevNode) && !pos.hasSpace()) {
      String prevSurface = prevNode.getSurface();
      result.add(
          new TokenInfo(
              prevSurface + pos.getSurface(),
              PosId.N,
              0,
              new Offsets(
                  pos.getStartOffset() - prevSurface.length(),
                  pos.getEndOffset())));
    }
  }
  
  private boolean isAbsolutePos(Pos pos) {
    return appender.isAbsolutePos(pos);
  }
  
  private static boolean isIsolatedJosa(Node node) {
    return node.getPosid() == PosId.J.getNum() &&
        node.getRlength() - node.getLength() > 0;
  }

  private void clearPosList() {
    posList.clear();
  }
  
  private boolean isSkippablePoses() {
    if (posList.size() == 1) {
      return appender.isSkippablePos(posList.get(0));
    } else {
      return false;
    }
  }
}