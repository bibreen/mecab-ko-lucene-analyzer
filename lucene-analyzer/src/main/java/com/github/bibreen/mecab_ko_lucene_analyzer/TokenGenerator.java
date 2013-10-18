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
import org.chasen.mecab.MeCab;

import com.github.bibreen.mecab_ko_lucene_analyzer.PosIdManager.PosId;

/**
 * MeCab의 node를 받아서 Lucene tokenizer에 사용될 Pos 리스트를 생성하는 클래스.
 * 
 * @author bibreen <bibreen@gmail.com>
 * @author amitabul <mousegood@gmail.com>
 */
public class TokenGenerator {
  public static final int NO_DECOMPOUND = 9999;
  public static final int DEFAULT_COMPOUND_NOUN_MIN_LENGTH = 3;
  
  PosAppender appender;
  private LinkedList<Pos> posList = new LinkedList<Pos>();
  private ListIterator<Pos> posIter;
  private int compoundNounMinLength;
  
  /**
   * TokenGenerator 생성자
   * 
   * @param appender PosAppender
   * @param compoundNounMinLength 복합명사에서 분해할 명사의 최소길이.
   * 복합명사 분해가 필요없는 경우, TokenGenerator.NO_DECOMPOUND를 입력한다.
   * @param beginNode 시작 노드
   */
  public TokenGenerator(
      PosAppender appender, int compoundNounMinLength, Node beginNode) {
    this.appender = appender;
    this.compoundNounMinLength = compoundNounMinLength;
    convertNodeListToPosList(beginNode);
    posIter = posList.listIterator();
  }
  
  private void convertNodeListToPosList(Node beginNode) {
    Node node = beginNode.getNext();
    Pos prevPos = new Pos("", PosId.UNKNOWN, 0, 0, 0);
    while (!isEosNode(node)) {
      Pos curPos = new Pos(node, prevPos.getEndOffset());
      if (curPos.getPosId() == PosId.PREANALYSIS) {
        posList.addAll(getAnalyzedPoses(curPos));
      } else {
        posList.add(curPos);
      }
      prevPos = curPos;
      node = node.getNext();
    }
  }
  
  static private boolean isEosNode(Node node) {
    return node == null || node.getStat() == MeCab.MECAB_EOS_NODE;
  }
 
  /**
   * mecab-ko-dic의 인덱스 표현 문자열을 해석하여 품사(Pos) 리스트를 반환한다.
   */
  static public LinkedList<Pos> getAnalyzedPoses(Pos pos) {
    LinkedList<Pos> output = new LinkedList<Pos>();
    String indexExp = pos.getIndexExpression();
    if (indexExp == null) {
      output.add(pos);
      return output;
    }
    String[] posExps = indexExp.split("\\+");
    if (posExps.length == 1) {
      output.add(pos);
      return output;
    }
    
    for (String posExp: posExps) {
      output.add(new Pos(posExp, 0));
    }
    // 분해된 POS의 offset 재계산
    Pos prevPos = null;
    for (Pos curPos: output) {
      if (prevPos == null) {
        curPos.setStartOffset(pos.getStartOffset());
        prevPos = curPos;
      } else {
        if (curPos.getPositionIncr() == 0) {
          curPos.setStartOffset(prevPos.getStartOffset());
        } else {
          curPos.setStartOffset(prevPos.getEndOffset());
          prevPos = curPos;
        }
      }
    }
    return output;
  }
  
  /**
   * 다음 어절의 Pos들을 반환한다.
   * @return 반환 값이 null이면 generator 종료이다.
   */
  public LinkedList<Pos> getNextEojeolTokens() {
    Eojeol eojeol = new Eojeol(appender, compoundNounMinLength);
    while (posIter.hasNext()) {
      Pos curPos = posIter.next();
      if (!eojeol.append(curPos)) {
        posIter.previous();
        LinkedList<Pos> poses = eojeol.generateTokens();
        if (poses != null) {
          return poses;
        } else {
          eojeol.clear();
        }
      }
    }
    // return last eojeol tokens
    return eojeol.generateTokens();
  }

  /**
   * 품사 객체(Pos)를 받아서 어절을 구성하는 클래스.
   * 
   * @author bibreen <bibreen@gmail.com>
   */
  static private class Eojeol {
    private PosAppender appender;
    private int compoundNounMinLength;
    
    private LinkedList<Pos> posList = new LinkedList<Pos>();
    private String term = "";

    Eojeol(PosAppender appender, int compoundNounMinLength) {
      this.appender = appender;
      this.compoundNounMinLength = compoundNounMinLength;
    }
    
    public boolean append(Pos pos) {
      if (isAppendable(pos)) {
        posList.add(pos);
        term += pos.getSurface();
        return true;
      } else {
        return false;
      }
    }
    
    private boolean isAppendable(Pos pos) {
      return posList.isEmpty() || appender.isAppendable(posList.getLast(), pos);
    }
    
    /**
     * Eojeol에 있는 Pos를 조합하여, Token이 되어야 하는 Pos를 생성한다.
     * @return token이 있을 경우 Pos의 리스트를 반환하고, 뽑아낼 token이 없을 경우
     * null을 반환한다.
     */
    private LinkedList<Pos> generateTokens() {
      if (isSkippable()) {
        return null;
      }
      LinkedList<Pos> output = appender.getAdditionalPoses(posList);
      Pos eojeolPos = addEojeolPos(output);
      addDecompoundedNoun(output);
      if (output.size() == 1) {
        return output;
      }
      eojeolPos.setPositionLength(recalcEojeolPositionLength(output));
      return output;
    }

    public boolean isSkippable() {
      if (posList.isEmpty()) {
        return true;
      }
      if (posList.size() == 1) {
        return appender.isSkippablePos(posList.get(0));
      } else {
        return false;
      }
    }

    private Pos addEojeolPos(LinkedList<Pos> eojeolTokens) {
      Pos eojeolPos;
      if (posList.size() == 1) {
        if (eojeolTokens.isEmpty()) {
          eojeolTokens.add(posList.getFirst());
        }
        eojeolPos = eojeolTokens.getFirst();
        eojeolPos.setPositionIncr(1);
      } else {
        eojeolPos = new Pos(getTerm(), PosId.EOJEOL, getStartOffset(), 1, 1);
        removeDuplicatedPos(eojeolTokens, eojeolPos);
        eojeolTokens.addFirst(eojeolPos);
      }
      return eojeolPos;
    }

    private int recalcEojeolPositionLength(LinkedList<Pos> eojeolTokens) {
      int positionLength = 0;
      for (Pos pos: eojeolTokens) {
        positionLength += pos.getPositionIncr();
      }
      return positionLength;
    }

    private void removeDuplicatedPos(
        LinkedList<Pos> eojeolTokens, Pos eojeolPos) {
      // 한 어절 안에서 길이가 같은 pos는 중복된 pos로 간주한다.
      int eojeolLength = eojeolPos.getSurfaceLength();
      ListIterator<Pos> iter = eojeolTokens.listIterator();
      while (iter.hasNext()) {
        Pos pos = iter.next();
        if (eojeolLength == pos.getSurfaceLength()) {
          iter.remove();
        }
      }
    }

    /**
     * output 리스트에 분해된 복합명사 토큰을 넣는다.
     * 복합명사 분해와 token의 위치에 대해서는 다음의 문서를 참조하였다.
     * http://www.slideshare.net/lucenerevolution/japanese-linguistics-in-lucene-and-solr
     */
    private void addDecompoundedNoun(LinkedList<Pos> output) {
      for (Pos pos: getPosList()) {
        if (pos.isPosIdOf(PosId.COMPOUND) &&
            pos.getSurfaceLength() >= compoundNounMinLength) {
          LinkedList<Pos> decompoundedTokens = decompound(pos);
          mergeDecompoundedTokensIntoEojeolTokens(output, decompoundedTokens);
        }
      }
    }
    
    static private LinkedList<Pos> decompound(Pos pos) {
      return TokenGenerator.getAnalyzedPoses(pos);
    }
    
    private void mergeDecompoundedTokensIntoEojeolTokens(
        LinkedList<Pos> eojeolTokens, LinkedList<Pos> decompoundedTokens) {
      LinkedList<Pos> decompoundedTokensCopy = new LinkedList<Pos>();
      decompoundedTokensCopy.addAll(decompoundedTokens);

      ListIterator<Pos> iter = eojeolTokens.listIterator();
      while (iter.hasNext()) {
        Pos pos = iter.next();
        if (pos.getPosId() == PosId.COMPOUND) {
          iter.remove();
        }
        pos.setPositionIncr(0);
      }
      
      int compoundNounPosition =
          findPositionByPosId(decompoundedTokens, PosId.COMPOUND);
      if (compoundNounPosition != -1) {
        decompoundedTokensCopy.addAll(compoundNounPosition, eojeolTokens);
      }
      eojeolTokens.clear();
      eojeolTokens.addAll(decompoundedTokensCopy);
    }
    
    private int findPositionByPosId(LinkedList<Pos> poses, PosId id) {
      int index = 0;
      for (Pos token: poses) {
        if (token.getPosId() == id) {
          return index;
        }
        ++index;
      }
      return -1;
    }
    
    public LinkedList<Pos> getPosList() {
      return posList;
    }
    
    public String getTerm() {
      return term;
    }
    
    public int getStartOffset() {
      return posList.getFirst().getStartOffset();
    }
    
    public void clear() {
      posList.clear();
      term = "";
    }
   
    @Override
    public String toString() {
      return posList.toString();
    }
  }
}