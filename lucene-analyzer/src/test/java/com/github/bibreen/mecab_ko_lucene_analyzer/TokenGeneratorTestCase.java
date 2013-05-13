package com.github.bibreen.mecab_ko_lucene_analyzer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.chasen.mecab.Node;

import com.github.bibreen.mecab_ko_lucene_analyzer.PosIdManager.PosId;

public class TokenGeneratorTestCase {
  public static Node mockNodeListFactory(String[] posStrings) {
    Node nextNode = null;
    for (int i = posStrings.length - 1; i >= 0; --i) {
      System.out.println(posStrings[i]);
      Node node = mockNodeFactory(posStrings[i], nextNode);
      nextNode = node;
    }
    return mockBeginNode(nextNode);
  }
  
  public static Node mockNodeFactory(String posString, Node next) {
    String[] surfaceAndFeature = posString.split("\t");
    String surface = surfaceAndFeature[0].trim();
    String feature = surfaceAndFeature[1].trim();
    
    Node node = mock(Node.class);
    when(node.getSurface()).thenReturn(surface);
    when(node.getPosid()).thenReturn(getPosId(feature));
    when(node.getRlength()).thenReturn(surfaceAndFeature[0].length());
    when(node.getLength()).thenReturn(surface.length());
    when(node.getFeature()).thenReturn(feature);
    when(node.getNext()).thenReturn(next);
    return node;
  }
  
  private static Node mockBeginNode(Node next) {
    Node node = mock(Node.class);
    when(node.getSurface()).thenReturn("BOS");
    when(node.getPosid()).thenReturn(0);
    when(node.getRlength()).thenReturn(0);
    when(node.getLength()).thenReturn(0);
    when(node.getFeature()).thenReturn("");
    when(node.getNext()).thenReturn(next);
    return node;
  }
  
  private static int getPosId(String feature) {
    final int TAG_POSITION = 0;
    final int TYPE_POSITION = 3;
    
    String[] features = feature.split(",");
    String tag = features[TAG_POSITION];
    String type = features[TYPE_POSITION];
    if (type == "Compound") {
      return PosId.COMPOUND.getNum();
    } else if (type == "Inflect") {
      return PosId.INFLECT.getNum();
    } else {
      return PosId.convertFrom(tag).getNum();
    }
  }
}
