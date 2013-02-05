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
package com.github.bibreen.mecab_ko_lucene_analyzer;

import java.io.Reader;
import java.util.Map;

import org.apache.solr.core.SolrResourceLoader;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;

/**
 * 
 * @author bibreen <bibreen@gmail.com>
 */
public class StandardIndexTokenizerFactory extends TokenizerFactory {
  private String mecabDicDir = "/usr/local/lib/mecab/dic/mecab-ko-dic";
  @Override
  public void init(Map<String, String> args) {
    super.init(args);
    setMeCabDicDir();
  }

  private void setMeCabDicDir() {
    String path = getArgs().get("mecabDicDir");
    if (path != null) {
      if (path.startsWith("/")) {
        mecabDicDir = path;
      } else {
        mecabDicDir = SolrResourceLoader.locateSolrHome() + path;
      }
    }
  }

  @Override
  public Tokenizer create(Reader input) {
    return new MeCabKoTokenizer(
        input, mecabDicDir, new StandardPosAppender(), true);
  }
}
