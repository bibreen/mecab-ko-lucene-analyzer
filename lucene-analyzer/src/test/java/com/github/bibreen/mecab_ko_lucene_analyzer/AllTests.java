package com.github.bibreen.mecab_ko_lucene_analyzer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
  MeCabKoStandardTokenizerTest.class,
  PosIdManagerTest.class,
  TokenGeneratorTest.class,
  TokenGeneratorWithStandardPosAppenderTest.class})
public class AllTests {
}
