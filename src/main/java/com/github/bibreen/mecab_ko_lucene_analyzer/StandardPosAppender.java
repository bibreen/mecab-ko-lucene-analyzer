package com.github.bibreen.mecab_ko_lucene_analyzer;

import java.util.HashSet;
import java.util.Set;

import com.github.bibreen.mecab_ko_lucene_analyzer.PosIdManager.PosId;

public class StandardPosAppender extends PosAppender {
  static public Set<Appendable> appendableSet;
  
  static {
    appendableSet = new HashSet<Appendable>();
   
    // Appenable HashSet 구성
    // 체언접두사(XPN) + 체언(N*)
    appendableSet.add(new Appendable(PosId.XPN, PosId.NN));
    appendableSet.add(new Appendable(PosId.XPN, PosId.NNB));
    appendableSet.add(new Appendable(PosId.XPN, PosId.NR));
    appendableSet.add(new Appendable(PosId.XPN, PosId.NP));
    appendableSet.add(new Appendable(PosId.XPN, PosId.COMPOUND));
    // 어미(E) + 어미(E)
    appendableSet.add(new Appendable(PosId.E, PosId.E));
    // 어근(XR) + E [+ E]*
    appendableSet.add(new Appendable(PosId.XR, PosId.E));
    // 용언(V*) + E [+ E]*
    appendableSet.add(new Appendable(PosId.VV, PosId.E));
    appendableSet.add(new Appendable(PosId.VA, PosId.E));
    appendableSet.add(new Appendable(PosId.VX, PosId.E));
    appendableSet.add(new Appendable(PosId.VCP, PosId.E));
    appendableSet.add(new Appendable(PosId.VCN, PosId.E));
    // 체언(N*) + 명사 파생 접미사(XSN)
    appendableSet.add(new Appendable(PosId.NN, PosId.XSN));
    appendableSet.add(new Appendable(PosId.NNB, PosId.XSN));
    appendableSet.add(new Appendable(PosId.NR, PosId.XSN));
    appendableSet.add(new Appendable(PosId.NP, PosId.XSN));
    appendableSet.add(new Appendable(PosId.COMPOUND, PosId.XSN));
    // 체언(N*) + 조사 [+ 조사]*
    appendableSet.add(new Appendable(PosId.NN, PosId.J));
    appendableSet.add(new Appendable(PosId.NNB, PosId.J));
    appendableSet.add(new Appendable(PosId.NR, PosId.J));
    appendableSet.add(new Appendable(PosId.NP, PosId.J));
    appendableSet.add(new Appendable(PosId.COMPOUND, PosId.J));
    // 체언(N*) + 긍정/부정 지정사(VCP, VCN)
    appendableSet.add(new Appendable(PosId.NN, PosId.VCP));
    appendableSet.add(new Appendable(PosId.NNB, PosId.VCP));
    appendableSet.add(new Appendable(PosId.NR, PosId.VCP));
    appendableSet.add(new Appendable(PosId.NP, PosId.VCP));
    appendableSet.add(new Appendable(PosId.COMPOUND, PosId.VCP));
    appendableSet.add(new Appendable(PosId.NN, PosId.VCN));
    appendableSet.add(new Appendable(PosId.NNB, PosId.VCN));
    appendableSet.add(new Appendable(PosId.NR, PosId.VCN));
    appendableSet.add(new Appendable(PosId.NP, PosId.VCN));
    appendableSet.add(new Appendable(PosId.COMPOUND, PosId.VCN));
    // 명사 파생 접미사(XSN) + 긍정/부정 지정사(VCP, VCN)
    appendableSet.add(new Appendable(PosId.XSN, PosId.VCP));
    appendableSet.add(new Appendable(PosId.XSN, PosId.VCN));
    // 명사 파생 접미사(XSN) + 조사(J)
    appendableSet.add(new Appendable(PosId.XSN, PosId.J));
    // 조사(J) + 조사(J)
    appendableSet.add(new Appendable(PosId.J, PosId.J));
  }

  @Override
  public boolean isAppendable(Pos left, Pos right) {
    if (right.getNode() != null && right.getSpaceLength() > 0) {
      return false;
    }
    return appendableSet.contains(
        new Appendable(left.getEndPosId(), right.getStartPosId()));
  }

  @Override
  public boolean isAbsolutePos(Pos pos) {
    // 체언(명사류)와 XR(어근)은 단독으로도 token을 생성한다.
    return (pos.isPosIdOf(PosId.COMPOUND) ||
        pos.getPosId().in(PosId.NN, PosId.NR) ||
        pos.isPosIdOf(PosId.XR));
  }

  @Override
  public boolean isSkippablePos(Pos pos) {
    // 단독으로 쓰인 심볼과 UNKNOWN은 token 생성 제외한다.
    PosId posId = pos.getPosId();
    if (posId == PosId.UNKNOWN ||
        posId == PosId.SF ||
        posId.in(PosId.SP, PosId.SY)) {
      return true;
    } else {
      return false;
    }
  }
}