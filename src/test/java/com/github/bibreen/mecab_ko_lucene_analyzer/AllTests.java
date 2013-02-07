package com.github.bibreen.mecab_ko_lucene_analyzer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
  MeCabKoDicTest.class,
  MeCabKoStandardTokenizerTest.class,
  PosIdManagerTest.class,
  TokenGeneratorTest.class})
public class AllTests {
}
