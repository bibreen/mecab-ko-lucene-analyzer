package com.github.bibreen.mecab_ko_lucene_analyzer;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EojeolGeneratorTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test() {
    EojeolGenerator generator = new EojeolGenerator();
    
    generator.insert(new Pos(Pos.Tag.N));
    assertEquals(false, generator.isStarted());
    generator.insert(new Pos(Pos.Tag.JO));
    assertEquals(false, generator.isStarted());
    
    generator.insert(new Pos(Pos.Tag.N));
    assertEquals(true, generator.isStarted());
    generator.insert(new Pos(Pos.Tag.JO));
    assertEquals(false, generator.isStarted());
    generator.insert(new Pos(Pos.Tag.JO));
    assertEquals(false, generator.isStarted());
    
    generator.insert(new Pos(Pos.Tag.V));
    assertEquals(true, generator.isStarted());
    generator.insert(new Pos(Pos.Tag.E));
    assertEquals(false, generator.isStarted());
    generator.insert(new Pos(Pos.Tag.E));
    assertEquals(false, generator.isStarted());
    generator.insert(new Pos(Pos.Tag.OTHER));
    assertEquals(true, generator.isStarted());
  }

}
