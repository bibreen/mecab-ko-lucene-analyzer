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
      extractNouns(lattice.bos_node());
//      extractComposedNouns(morphemes);
//      extractDecomposedNouns();
      extractEojeols(lattice.bos_node());
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
    }
    
    return tokens;
  }
  
  public void extractNouns(Node beginNode) {
    int offset = 0;
    for (Node node = beginNode; node != null; node = node.getNext()) {
      if (isNoun(node)) {
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
    // 명사인지 확인하는 방법은 3가지가 있음.
    // 첫째, 왼쪽ID가 168 또는 169인지 확인.
    // 둘째, 오른쪽ID가 19 또는 20인지 확인.
    // 셋째, feature 스트링에서 NN을 확인하는 방법.
    // 그 중 두번째 방법을 사용함.
    if (node.getRcAttr() == 19 || node.getRcAttr() == 20) {
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
  
  public class TokenInfo implements Comparable<TokenInfo> {
    public static final long NO_INCREMENT_POSITION = -1L;
    private String term;
    private int posIncr;
    private Offsets offsets;
    private boolean composed;
   
    public TokenInfo(
        String term, int posIncr, Offsets offsets, boolean composed) {
      this.term = term;
      this.posIncr = posIncr;
      this.offsets = offsets;
      this.composed = composed;
    }
    
    public String getTerm() {
      return term;
    }
    
    public int getPosIncr() {
      return posIncr;
    }
    
    public Offsets getOffsets() {
      return offsets;
    }
    
    public boolean isComposed() {
      return composed;
    }

    @Override
    public int compareTo(TokenInfo t) {
      if (offsets.end == t.getOffsets().end) {
        // end가 같은 경우에는 보다 긴 term(start가 작은 term)이 상위이다.
        // start와 end가 동일한 term은 SortedSet에 들어갈 필요가 없다.
        return t.getOffsets().start - offsets.start;
      }
      return offsets.end - t.getOffsets().end;
    }
    
    @Override
    public String toString() {
      return new String(
          term + ":" + posIncr + ":" +
          offsets.start + ":" + offsets.end);
    }
  }
  
  class Offsets {
    public int start;
    public int end;
    
    public Offsets(int s, int e) {
      start = s;
      end = e;
    }
    
    @Override
    public String toString() {
      return new String("Offsets(" + start + ":" + end + ")");
    }
  }
}
