package com.github.bibreen.mecab_ko_lucene_analyzer;

import org.junit.Test;

import org.chasen.mecab.Lattice;
import org.chasen.mecab.MeCab;
import org.chasen.mecab.Model;
import org.chasen.mecab.Node;
import org.chasen.mecab.Tagger;

public class MeCabKoDicTest {
  static {
    try {
      System.loadLibrary("MeCab");
    } catch (UnsatisfiedLinkError e) {
      System.err.println(
          "Cannot load the example native code.\n"
          + "Make sure your LD_LIBRARY_PATH contains \'.\'\n" + e);
      System.exit(1);
    }
  }

//  @Before
//  public void setUp() throws Exception {
//  }
//
//  @After
//  public void tearDown() throws Exception {
//  }

  @Test
  public void test() {
    System.out.println(MeCab.VERSION);
    Tagger tagger = new Tagger("-d /usr/local/lib/mecab/dic");
    String str = "아버지가방에들어가신다.";
    //System.out.println(tagger.parse(str));
    Node node = tagger.parseToNode(str);
//    for (;node != null; node = node.getNext()) {
//       System.out.println(node.getSurface() + "\t" + node.getFeature());
//    }
//    System.out.println ("EOS\n");

    System.out.println("###");
    Model model = new Model("-d /home/amitabul/mecab-ko-dic");
    Tagger tagger2 = model.createTagger();
    //System.out.println (tagger2.parse(str));

    Lattice lattice = model.createLattice();
    //System.out.println(str);
    lattice.set_sentence(str);
    if (tagger2.parse(lattice)) {
      //System.out.println(lattice.toString());
      for (node = lattice.bos_node(); node != null; node = node.getNext()) {
         System.out.println(node.getSurface() + "\t" + node.getFeature());
      }
      System.out.println("EOS\n");
    }

//    lattice.add_request_type(MeCab.MECAB_NBEST);
//    lattice.set_sentence(str);
//    tagger2.parse(lattice);
//    for (int i = 0; i < 10; ++i) {
//      if (lattice.next()) {
//        System.out.println("nbest:" + i + "\n" +
//                           lattice.toString());
//      }
//    }


    
  }
}