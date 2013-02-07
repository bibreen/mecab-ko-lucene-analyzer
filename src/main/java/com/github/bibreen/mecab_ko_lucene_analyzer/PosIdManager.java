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
    E(100), IC(110), J(120), MAG(130), MAJ(131), MM(140), N(150), SF(160),
    SH(161), SL(162), SN(163), SP(164), SSC(165), SSO(166), SU(167), SY(168),
    VA(170), VCN(171), VCP(172), VV(173), VX(174), XPN(181), XR(182), XSA(183),
    XSN(184), XSV(185);

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
      case 110: return PosId.IC;
      case 120: return PosId.J;
      case 130: return PosId.MAG;
      case 131: return PosId.MAJ;
      case 140: return PosId.MM;
      case 150: return PosId.N;
      case 160: return PosId.SF;
      case 161: return PosId.SH;
      case 162: return PosId.SL;
      case 163: return PosId.SN;
      case 164: return PosId.SP;
      case 165: return PosId.SSC;
      case 166: return PosId.SSO;
      case 167: return PosId.SU;
      case 168: return PosId.SY;
      case 170: return PosId.VA;
      case 171: return PosId.VCN;
      case 172: return PosId.VCP;
      case 173: return PosId.VV;
      case 174: return PosId.VX;
      case 181: return PosId.XPN;
      case 182: return PosId.XR;
      case 183: return PosId.XSA;
      case 184: return PosId.XSN;
      case 185: return PosId.XSV;
      default:
        return PosId.UNKNOWN;
      }
    }
    
    public static PosId convertFrom(String tagString) {
      try {
        if (tagString.charAt(0) == 'N') {
          return PosId.N;
        } else if (tagString.charAt(0) == 'J') {
          return PosId.J;
        } else if (tagString.charAt(0) == 'E') {
          return PosId.E;
        } else {
          return PosId.valueOf(tagString);
        }
      } catch(Exception e) {
        return PosId.UNKNOWN;
      }
    }
    
    public boolean in(PosId start, PosId end) {
      return start.getNum() <= this.getNum()&& this.getNum() <= end.getNum();
    }
  }
}