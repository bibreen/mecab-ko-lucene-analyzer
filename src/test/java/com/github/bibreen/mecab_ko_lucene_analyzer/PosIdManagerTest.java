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
