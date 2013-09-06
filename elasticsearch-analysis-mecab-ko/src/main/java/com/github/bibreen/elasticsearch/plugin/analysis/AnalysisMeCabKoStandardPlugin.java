/*******************************************************************************
 * Copyright 2013 Yongwoon Lee, Yungho Yu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.github.bibreen.elasticsearch.plugin.analysis;

import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;

import com.github.bibreen.elasticsearch.index.analysis.MeCabKoStandardTokenizerFactory;

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
