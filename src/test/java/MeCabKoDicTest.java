import static org.junit.Assert.*;

import org.chasen.mecab.Node;
import org.chasen.mecab.Tagger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


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
    Tagger tagger = new Tagger("-d /home/deicide/mecab-ko-dic");
    String x = tagger.parse("아버지가방에들어가신다.");
    System.out.println(x);
    System.out.println("#######");
    Node node = tagger.parseToNode("아버지가방에들어가신다.");
    for (;node != null; node = node.getNext()) {
      System.out.println(node.getSurface() + "\t" + node.getFeature());
   }
  }
}