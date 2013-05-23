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
  public static final int DEFAULT_COMPOUND_NOUN_MIN_LENGTH = 3;
  
  PosAppender appender;
  private LinkedList<Pos> posList = new LinkedList<Pos>();
  private ListIterator<Pos> posIter;
  private int compoundNounMinLength;
  
  /**
   * TokenGenerator 생성자
   * 
   * @param appender PosAppender
   * @param decompoundMinLength 복합명사에서 분해할 명사의 최소길이.
   * 복합명사 분해가 필요없는 경우, TokenGenerator.NO_DECOMPOUND를 입력한다.
   * @param beginNode
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
    Pos prevPos = new Pos("", PosId.UNKNOWN, 0, 0);
    while (node != null) {
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
  
  private LinkedList<Pos> getAnalyzedPoses(Pos pos) {
    LinkedList<Pos> output = new LinkedList<Pos>();
    String indexExp = pos.getIndexExpression();
    String[] posExps = indexExp.split("\\+");
    if (posExps.length == 1) {
      output.add(pos);
    } else {
      int startOffset = pos.getStartOffset();
      for (String exp: posExps) {
        Pos newPos = new Pos(exp, startOffset);
        output.add(newPos);
        startOffset += newPos.getSurfaceLength();
      }
    }
    return output;
  }
  
  /**
   * 다음 어절의 Token들을 반환한다.
   * @return 반환 값이 null이면 generator 종료이다.
   */
  public LinkedList<TokenInfo> getNextEojeolTokens() {
    Eojeol eojeol = new Eojeol(appender);
    while (posIter.hasNext()) {
      Pos curPos = posIter.next();
      if (!eojeol.append(curPos)) {
        posIter.previous();
        LinkedList<TokenInfo> tokens = makeTokens(eojeol);
        if (tokens != null) {
          return tokens;
        } else {
          eojeol.clear();
          continue;
        }
      }
    }
    // return last tokens
    return makeTokens(eojeol);
  }

  /**
   * posList에 있는 Pos를 조합하여, Token을 뽑아낸다.
   * @return token이 있을 경우 TokenInfo의 리스트를 반환하고, 뽑아낼 token이 없을 경우
   * null을 반환한다.
   */
  private LinkedList<TokenInfo> makeTokens(Eojeol eojeol) {
    if (eojeol.isSkippable()) {
      return null;
    }
    
    LinkedList<TokenInfo> output = new LinkedList<TokenInfo>();
    addAdditionalToken(output, eojeol);
    output.addFirst(eojeol.createToken(1));
    addDecompoundedNoun(output, eojeol);
    return output;
  }

  /**
   * output 리스트에 분해된 복합명사 토큰을 넣는다.
   * 복합명사 분해와 token의 위치에 대해서는 다음의 문서를 참조하였다.
   * http://www.slideshare.net/lucenerevolution/japanese-linguistics-in-lucene-and-solr
   */
  private void addDecompoundedNoun(
      LinkedList<TokenInfo> output, Eojeol eojeol) {
    for (Pos pos: eojeol.getPosList()) {
      if (pos.isPosIdOf(PosId.COMPOUND) &&
          pos.getSurfaceLength() >= compoundNounMinLength) {
        LinkedList<TokenInfo> decompoundedTokens = decompoundToTokens(pos);
        mergeDecompoundedTokensIntoEojeolTokens(output, decompoundedTokens);
      }
    }
  }
  
  private LinkedList<TokenInfo> decompoundToTokens(Pos compoundNoun) {
    LinkedList<TokenInfo> result = new LinkedList<TokenInfo>();
    String exp = compoundNoun.getIndexExpression();
    String[] nounStrs = exp.split("\\+");
    if (nounStrs.length == 1) {
      return result;
    } else {
      for (String tokenExp: nounStrs) {
        TokenInfo token = new TokenInfo(tokenExp, 0);
        result.add(token);
      }
      // 분해된 token의 offset 재계산
      TokenInfo prevToken = null;
      for (TokenInfo token: result) {
        if (prevToken == null) {
          token.setStartOffset(compoundNoun.getStartOffset());
          prevToken = token;
        } else {
          if (token.getPositionIncr() == 0) {
            token.setStartOffset(prevToken.getOffsets().start);
          } else {
            token.setStartOffset(prevToken.getOffsets().end);
            prevToken = token;
          }
        }
      }
      return result;
    }
  }
  
  private void mergeDecompoundedTokensIntoEojeolTokens(
      LinkedList<TokenInfo> eojeolTokens,
      LinkedList<TokenInfo> decompoundedTokens) {
    LinkedList<TokenInfo> decompoundedTokensCopy = new LinkedList<TokenInfo>();
    decompoundedTokensCopy.addAll(decompoundedTokens);
    
    ListIterator<TokenInfo> iter = eojeolTokens.listIterator();
    while (iter.hasNext()) {
      TokenInfo token = iter.next();
      if (token.getPosTag().equalsIgnoreCase("COMPOUND")) {
        iter.remove();
      }
      token.setPositionIncr(0);
    }
    
    int compoundNounPosition = findTokenByTag(decompoundedTokens, "COMPOUND");
    if (compoundNounPosition != -1) {
      decompoundedTokensCopy.addAll(compoundNounPosition, eojeolTokens);
    }
    eojeolTokens.clear();
    eojeolTokens.addAll(decompoundedTokensCopy);
  }
  
  private int findTokenByTag(LinkedList<TokenInfo> tokens, String tag) {
    int index = 0;
    for (TokenInfo token: tokens) {
      if (token.getPosTag().equalsIgnoreCase(tag)) {
        return index;
      }
      ++index;
    }
    return -1;
  }
  
  private void addAdditionalToken(LinkedList<TokenInfo> output, Eojeol eojeol) {
    addAbsolutePosToken(output, eojeol);
  }

  /**
   * 독립적인 Token이 되어야 하는 품사는 incrPos=0 으로 미리 넣어둔다.
   */
  private void addAbsolutePosToken(
      LinkedList<TokenInfo> output, Eojeol eojeol) {
    LinkedList<Pos> eojeolPosList = eojeol.getPosList();
    if (eojeolPosList.size() <= 1) return;
    for (Pos pos: eojeolPosList) {
      if (isAbsolutePos(pos)) {
        output.add(new TokenInfo(pos, 0));
      }
    }
  }
  
  private boolean isAbsolutePos(Pos pos) {
    return appender.isAbsolutePos(pos);
  }

  /**
   * 품사 객체(Pos)를 받아서 어절을 구성하는 클래스.
   * 
   * @author bibreen <bibreen@gmail.com>
   */
  static private class Eojeol {
    PosAppender appender;
    private LinkedList<Pos> posList = new LinkedList<Pos>();
    String term = "";
    int positionLength = 0;
    
    Eojeol(PosAppender appender) {
      this.appender = appender;
    }
    
    public boolean append(Pos pos) {
      if (isAppendable(pos)) {
        posList.add(pos);
        term += pos.getSurface();
        if (isAbsolutePos(pos)) {
          positionLength += pos.getPositionLength();
        }
        return true;
      } else {
        return false;
      }
    }
    
    private boolean isAppendable(Pos pos) {
      if (posList.isEmpty()) {
        return true;
      } else {
        return appender.isAppendable(posList.getLast(), pos);
      }
    }
    
    private boolean isAbsolutePos(Pos pos) {
      return appender.isAbsolutePos(pos);
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
    
    public LinkedList<Pos> getPosList() {
      return posList;
    }
    
    public String getTerm() {
      return term;
    }
    
    public int getStartOffset() {
      return posList.getFirst().getStartOffset();
    }
    
    public int getPositionLength() {
      if (positionLength == 0) {
        return 1;
      } else {
        return positionLength;
      }
    }
    
    public TokenInfo createToken(int positionIncr) {
      if (posList.size() > 1) {
        return new TokenInfo(
            getTerm(),
            PosId.EOJEOL,
            positionIncr,
            getPositionLength(),
            getStartOffset());
      } else {
        return new TokenInfo(posList.get(0), 1);
      }
    }
    
    public void clear() {
      posList.clear();
      term = "";
      positionLength = 0;
    }
   
    @Override
    public String toString() {
      return posList.toString();
    }
  }
}