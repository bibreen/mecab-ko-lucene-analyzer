package com.github.bibreen.mecab_ko_lucene_analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.chasen.mecab.Node;

import com.github.bibreen.mecab_ko_lucene_analyzer.LuceneTokenExtractor;
import com.github.bibreen.mecab_ko_lucene_analyzer.LuceneTokenExtractor.Offsets;
import com.github.bibreen.mecab_ko_lucene_analyzer.LuceneTokenExtractor.TokenInfo;
import com.github.bibreen.mecab_ko_lucene_analyzer.Pos.Tag;

public class EojeolGenerator {
  
  static public Set<Appendable> appendableSet;
  
  static {
    // 접두사(XP) + 체언(N*)
    // 어근(XR) + E [+ E]*
    // 용언(V*) + E [+ E]*
    // 체언(N*) + 명사 파생 접미사(XSN)
    // 체언(N*) + 조사 [+ 조사]*
     appendableSet = new HashSet<Appendable>();
     appendableSet.add(new Appendable(Tag.XP, Tag.N));
     appendableSet.add(new Appendable(Tag.XR, Tag.E));
     appendableSet.add(new Appendable(Tag.E, Tag.E));
     appendableSet.add(new Appendable(Tag.V, Tag.E));
     appendableSet.add(new Appendable(Tag.N, Tag.XSN));
     appendableSet.add(new Appendable(Tag.N, Tag.JO));
     appendableSet.add(new Appendable(Tag.JO, Tag.JO));
  }
  
  private Pos prev = null;
  private boolean isStarted = false;
  private List<Pos> poses = new ArrayList<Pos>();
  private int prevEojeolEndOffset = 0;
  
  public void insert(Pos pos) {
    if (prev ==null || isAppendable(pos)) {
      isStarted = false;
      prev = pos;
      poses.add(pos);//    TokenInfo t = 
//    new LuceneTokenExtractor.TokenInfo(str, 1, new LuceneTokenExtractor.Offsets(startOffset, endOffset), false);
    } else {
      isStarted = true;
      prev = pos;
    }
  }
  
  private boolean isAppendable(Pos cur) {
    return appendableSet.contains(new Appendable(prev.getTag(), cur.getTag()));
  }
  
  public boolean isStarted() {
    return isStarted;
  }
  
  public ArrayList<TokenInfo> getTokenInfo() {
    if (poses.isEmpty()) {
      return null;
    }
    ArrayList<TokenInfo> result = new ArrayList<TokenInfo>();
    
    Node firstNode = poses.get(0).getNode();
    
    int startOffset = 
        prevEojeolEndOffset + 
        firstNode.getRlength() - firstNode.getLength();
 
    String str = "";
    int length = 0;
    for (Pos pos: poses) {
      Node node = pos.getNode();
      str += node.getSurface();
      length += node.getRlength();
    }
    int endOffset = prevEojeolEndOffset + length;
//    TokenInfo t = 
//        new LuceneTokenExtractor.TokenInfo(str, 1, new LuceneTokenExtractor.Offsets(startOffset, endOffset), false);
    
//    LuceneTokenExtractor.Offsets o =
//        new LuceneTokenExtractor.Offsets(startOffset, endOffset);
    poses.clear();
    return null;
  }
  
}
