package com.github.bibreen.mecab_ko_lucene_analyzer;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.chasen.mecab.Lattice;
import org.chasen.mecab.Model;
import org.chasen.mecab.Tagger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EojeolGeneratorTest {
  private Tagger tagger;
  private Lattice lattice;
  
  static {
    // TODO: 이 코드가 계속 중복인데 어떻게 해야하지?
    try {
      System.loadLibrary("MeCab");
    } catch (UnsatisfiedLinkError e) {
      System.err.println(
          "Cannot load the example native code.\n"
          + "Make sure your LD_LIBRARY_PATH contains \'.\'\n" + e);
      System.exit(1);
    }
  }

  @Before
  public void setUp() throws Exception {
    Model model = new Model("-d /usr/local/lib/mecab/dic/mecab-ko-dic"); 
    tagger = model.createTagger();
    lattice = model.createLattice();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testAppendable() {
    EojeolGenerator generator = new EojeolGenerator(null);
    
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
  @Test
  public void testHangul() {
    List<TokenInfo> tokens;
    lattice.set_sentence("무궁화 꽃이 피었습니다.");
    
    if (tagger.parse(lattice)) {
      EojeolGenerator generator = new EojeolGenerator(lattice.bos_node());
      tokens = generator.getNextTokens();
      System.out.println(tokens);
      tokens = generator.getNextTokens();
      System.out.println(tokens);
      tokens = generator.getNextTokens();
      System.out.println(tokens);
      tokens = generator.getNextTokens();
      System.out.println(tokens);
    }
  }

}
