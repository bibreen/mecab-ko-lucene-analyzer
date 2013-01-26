package com.github.bibreen.mecab_ko_lucene_analyzer;

/**
 * mecab-ko-dic의 PosId를 관리하는 클래스
 * 
 * @author bibreen <bibreen@gmail.com>
 */
public final class PosIdManager {
  /**
   * PosId와 일대일 매핑되는 enum
   */
  public enum PosId {
    UNKNOWN(0),
    COMPOUND(1), INFLECT(2),
    E(100), IC(101), J(102), MAG(103), MAJ(104), MM(105), NN(106), NNB(107),
    NP(108), NR(109), SF(110), SH(111), SL(112), SN(113), SP(114), SSC(115),
    SSO(116), SU(117), SY(118), VA(119), VCN(120), VCP(121), VV(122), VX(123),
    XPN(124), XR(125), XSA(126), XSN(127), XSV(128); 

    private int num;
    
    PosId(int num) {
      this.num = num;
    }
    
    public int getNum() {
      return num;
    }
    
    public static PosId convertFrom(int posIdNum) {
      switch(posIdNum) {
      case 0: return PosId.UNKNOWN;
      case 1: return PosId.COMPOUND;
      case 2: return PosId.INFLECT;
      case 100: return PosId.E;
      case 101: return PosId.IC;
      case 102: return PosId.J;
      case 103: return PosId.MAG;
      case 104: return PosId.MAJ;
      case 105: return PosId.MM;
      case 106: return PosId.NN;
      case 107: return PosId.NNB;
      case 108: return PosId.NP;
      case 109: return PosId.NR;
      case 110: return PosId.SF;
      case 111: return PosId.SH;
      case 112: return PosId.SL;
      case 113: return PosId.SN;
      case 114: return PosId.SP;
      case 115: return PosId.SSC;
      case 116: return PosId.SSO;
      case 117: return PosId.SU;
      case 118: return PosId.SY;
      case 119: return PosId.VA;
      case 120: return PosId.VCN;
      case 121: return PosId.VCP;
      case 122: return PosId.VV;
      case 123: return PosId.VX;
      case 124: return PosId.XPN;
      case 125: return PosId.XR;
      case 126: return PosId.XSA;
      case 127: return PosId.XSN;
      case 128: return PosId.XSV;
      default:
        return PosId.UNKNOWN;
      }
    }
    
    public static PosId convertFrom(String tagString) {
      try {
        return PosId.valueOf(tagString);
      } catch(Exception e) {
        return PosId.UNKNOWN;
      }
    }
    
    public boolean in(PosId start, PosId end) {
      return start.getNum() <= this.getNum()&& this.getNum() <= end.getNum();
    }
  }
}