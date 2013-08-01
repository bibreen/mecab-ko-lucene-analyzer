package com.github.bibreen.elasticsearch.plugin.analysis;

import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;

import java.util.Collection;

import com.github.bibreen.elasticsearch.index.analysis.MeCabKoStandardTokenizerFactory;

/**
 *
 */
public class AnalysisMeCabKoStandardPlugin extends AbstractPlugin {
  @Override
  public String name() {
    return "analysis-mecab-ko-standard";
  }

  @Override
  public String description() {
    return "mecab-ko-lucene-analyzer analysis support";
  }

  public void onModule(AnalysisModule module) {
    module.addTokenizer(
        "mecab_ko_standard_tokenizer", MeCabKoStandardTokenizerFactory.class);
  }
}
