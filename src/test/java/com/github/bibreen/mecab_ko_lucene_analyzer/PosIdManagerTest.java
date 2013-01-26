package com.github.bibreen.mecab_ko_lucene_analyzer;

import static org.junit.Assert.*;

import org.junit.Test;
import com.github.bibreen.mecab_ko_lucene_analyzer.PosIdManager.PosId;

public class PosIdManagerTest {
  @Test
  public void testConvertFromTagString() {
    PosId posId = PosId.convertFrom("NN");
    assertEquals(PosId.NN, posId);
    
    posId = PosId.convertFrom("InvalidTagString");
    assertEquals(PosId.UNKNOWN, posId);
  }
}
