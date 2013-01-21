package com.github.bibreen.mecab_ko_lucene_analyzer;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import org.chasen.mecab.Lattice;
import org.chasen.mecab.Model;
import org.chasen.mecab.Node;
import org.chasen.mecab.Tagger;

public class LuceneTokenExtractor {
  private List<TokenInfo> tokens;
  private Tagger tagger;
  private Lattice lattice;
  private EnumSet<Option> options;
  
  enum Option {
    EXTRACT_STEMMING_ENGLISH,
    EXTRACT_DECOMPOSED_NOUN,
    EXTRACT_EOJEOL,
  };
  
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
  
  public LuceneTokenExtractor(EnumSet<Option> options) {
    tokens = new LinkedList<TokenInfo>();
    //engStemmer = new EnglishStemmer();
    this.options = options;
    
    Model model = new Model("-d /usr/local/lib/mecab/dic/mecab-ko-dic"); 
    tagger = model.createTagger();
    lattice = model.createLattice();
  }
  
  public List<TokenInfo> extract(String string) throws Exception {
    lattice.set_sentence(string);
    if (tagger.parse(lattice)) {
//      for (Node node = lattice.bos_node(); node != null; node = node.getNext()) {
//         System.out.println(
//             node.getSurface() + "\t" + 
//             node.getLength() + "\t" + 
//             node.getRlength() + "\t" +
//             node.getLcAttr() + "\t" +
//             node.getRcAttr() + "\t" +
//             node.getChar_type() + "\t" +
//             node.getFeature());
//      }
      TokenGenerator generator = new TokenGenerator(lattice.bos_node());
      List<TokenInfo> eojeolTokens;
      while (true) {
        eojeolTokens = generator.getNextEojeolTokens();
        if (eojeolTokens == null) {
          break;
        } else {
          tokens.addAll(eojeolTokens);
        }
      }
    }
    
    return tokens;
  }
}
