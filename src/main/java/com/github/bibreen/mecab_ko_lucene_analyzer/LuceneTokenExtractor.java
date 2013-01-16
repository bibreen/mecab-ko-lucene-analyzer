package com.github.bibreen.mecab_ko_lucene_analyzer;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.chasen.mecab.Lattice;
import org.chasen.mecab.Model;
import org.chasen.mecab.Node;
import org.chasen.mecab.Tagger;

import com.github.bibreen.mecab_ko_lucene_analyzer.*;

public class LuceneTokenExtractor {
  private SortedSet<TokenInfo> tokens;
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
    tokens = new TreeSet<TokenInfo>();
    //engStemmer = new EnglishStemmer();
    this.options = options;
    
    Model model = new Model("-d /usr/local/lib/mecab/dic"); 
    tagger = model.createTagger();
    lattice = model.createLattice();
  }
  
  public SortedSet<TokenInfo> extract(String string) throws Exception {
    lattice.set_sentence(string);
    tokens.clear();
    if (tagger.parse(lattice)) {
      extractKeywords(lattice.bos_node());
//      extractComposedNouns(morphemes);
//      extractDecomposedNouns();
//      extractEojeols(lattice.bos_node());
      for (Node node = lattice.bos_node(); node != null; node = node.getNext()) {
         System.out.println(
             node.getSurface() + "\t" + 
             node.getLength() + "\t" + 
             node.getRlength() + "\t" +
             node.getLcAttr() + "\t" +
             node.getRcAttr() + "\t" +
             node.getChar_type() + "\t" +
             node.getFeature());
      }
    }
    
    return tokens;
  }
  
  public void extractKeywords(Node beginNode) {
    // 접두사(XP) + 체언(N*)
    // 어근(XR) [+ ?]* + 어미
    // 용언(V*) [+ ?]* + 어말어미(EM)
    // 용언(V*) + 어미(EM)
    // 체언(N*) + 명사 파생 접미사(XSN)
    // 체언(N*) + 조사 [+ 조사]*
    
    int offset = 0;
    for (Node node = beginNode; node != null; node = node.getNext()) {
      if (isNoun(node) || isAdverb(node) || isInterjection(node) || 
          isRadix(node)) {
        String surface = node.getSurface();
        int whiteSpaceLength = node.getRlength() - node.getLength();
        Offsets offsets = 
            new Offsets(offset + whiteSpaceLength, offset + node.getRlength());
        
        tokens.add(new TokenInfo(surface, 1, offsets, false));
      }
      offset += node.getRlength();
    }
  }
  
  public boolean isNoun(Node node) {
    if (109 <= node.getPosid() && node.getPosid() <= 113) { 
      return true;
    }
    return false;
  }
  
  public boolean isAdverb(Node node) {
    if (104 <= node.getPosid() && node.getPosid() <= 106) { 
      return true;
    }
    return false;
  }
  
  public boolean isInterjection(Node node) {
    if (node.getPosid() == 102) { 
      return true;
    }
    return false;
  }
  
  public boolean isRadix(Node node) {
    if (node.getPosid() == 135) { 
      return true;
    }
    return false;
  }
  
  
  public void extractEojeols(Node beginNode) {
    if (!options.contains(Option.EXTRACT_EOJEOL)) {
      return;
    }

    int offset = 0;
    Eojeol eojeol = new Eojeol(offset);
    for (Node node = beginNode; node != null; node = node.getNext()) {
      int whiteSpaceLength = node.getRlength() - node.getLength();
      boolean isEndEojeol = (whiteSpaceLength != 0 || node.getRcAttr() == 0);
      if (isEndEojeol && offset != 0) {
        tokens.add(eojeol.getTokenInfo());
        eojeol = new Eojeol(offset + whiteSpaceLength);
      }
      eojeol.append(node);
      offset += node.getRlength();
    }
    
  }
  
  class Eojeol {
    List<Node> nodes;
    private int startOffset;
    
    public Eojeol(int startOffset) {
      nodes = new ArrayList<Node>();
      this.startOffset = startOffset;
    }
    
    public void append(Node node) {
      // nodegetRcAttr():30 은 SF
      if (node.getLength() != 0 && node.getRcAttr() != 30) {
        nodes.add(node);
      }
    }
    
    public TokenInfo getTokenInfo() {
      int endOffset = startOffset;
      StringBuilder surface = new StringBuilder();
      boolean foundNoun = false;
      for (Node node: nodes) {
        endOffset += node.getLength();
        surface.append(node.getSurface());
        if (isNoun(node)) {
          foundNoun = true;
        }
      }
      TokenInfo token = new TokenInfo(
              surface.toString(), 
              foundNoun ? 0:1, 
              new Offsets( startOffset, endOffset),
              false);
      return token;
    }
    
    @Override
    public String toString() {
      StringBuilder str = new StringBuilder();
      for (Node node: nodes) {
        str.append("+" + node.getSurface());
      }
      return str.toString();
    }
  }
  
  
}
