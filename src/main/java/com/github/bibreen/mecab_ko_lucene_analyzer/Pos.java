package com.github.bibreen.mecab_ko_lucene_analyzer;

import org.chasen.mecab.Node;
import com.github.bibreen.mecab_ko_lucene_analyzer.PosIdManager.PosId;

public class Pos {
  private String surface;
  private int startOffset;
  private PosId posId;
  private PosId startPosId;
  private PosId endPosId;
  private String expression = null;
  /// 복합명사 분해과정에서 복합명사와 같은 위치에 인덱싱 되어야할 품사를 저장하기 위한 변수
  private Pos samePositionPos = null;
  private Node node;
  
  public Pos(String surface, PosId posId, int startOffset) {
    this.surface = surface;
    this.posId = posId;
    startPosId = posId;
    endPosId = posId;
    this.startOffset = startOffset;
  }
  
  public Pos(Node node, int prevEndOffset) {
    this(
        node.getSurface(),
        PosId.convertFrom(node.getPosid()),
        prevEndOffset + node.getRlength() - node.getLength());
    this.node = node;
    if (posId == PosId.COMPOUND || posId == PosId.INFLECT) {
      parseFeatureString();
    }
  }
  
  private void parseFeatureString() {
    final int startPosPosition = 4;
    final int endPosPosition = 5;
    final int expressionPosition = 6;
    
    String feature = node.getFeature();
    String items[] = feature.split(",");
    if (posId == PosId.INFLECT) {
      startPosId = PosId.convertFrom(items[startPosPosition].toUpperCase());
      endPosId = PosId.convertFrom(items[endPosPosition].toUpperCase());
    } else {
      this.startPosId = posId;
      this.endPosId = posId;
    }
    expression = items[expressionPosition];
  }
  
  public Node getNode()  {
    return node;
  }
  
  public PosId getPosId() {
    return posId;
  }
  
  public PosId getStartPosId() {
    return startPosId;
  }
  
  public PosId getEndPosId() {
    return endPosId;
  }
  
  public String getSurface() {
    return surface;
  }
  
  public int getSurfaceLength() {
    return surface.length();
  }
  
  public String getExpression() {
    return expression;
  }
  
  public int getStartOffset() {
    return startOffset;
  }
  
  public int getEndOffset() {
    return startOffset + surface.length();
  }
  
  public int getSpaceLength() {
    if (node == null) return 0;
    return node.getRlength() - node.getLength(); 
  }
  
  public int getLength() {
    return getSpaceLength() + getSurfaceLength();
  }
  
  public boolean isPosIdOf(PosId posId) {
    return (this.posId == posId);
  }
 
  public Pos getSamePositionPos() {
    return samePositionPos;
  }

  public void setSamePositionPos(Pos pos) {
    samePositionPos = pos;
  }

  @Override
  public String toString() {
    return surface + "/" + posId +
        "/" + startPosId + "," + endPosId + "(" + expression + ")";
  }
}