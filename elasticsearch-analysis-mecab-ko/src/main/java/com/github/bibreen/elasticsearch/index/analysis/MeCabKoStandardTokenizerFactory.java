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
package com.github.bibreen.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;
import com.github.bibreen.mecab_ko_lucene_analyzer.*;

import java.io.IOException;
import java.io.Reader;

/**
 * 표준 index용 tokenizer 팩토리 생성자. 다음과 같은 파라미터를 받는다.
 *   - mecab_dic_dir: mecab-ko-dic 사전 경로. 디폴트 경로는 /usr/local/lib/mecab/dic/mecab-ko-dic 이다.
 *   - compound_noun_min_length: 분해를 해야하는 복합명사의 최소 길이. 디폴트 값은 3이다.
 * 
 * @author bibreen <bibreen@gmail.com>
 */
public class MeCabKoStandardTokenizerFactory extends AbstractTokenizerFactory {
  private static final String DEFAULT_MECAB_DIC_DIR =
      "/usr/local/lib/mecab/dic/mecab-ko-dic";
  private String mecabDicDir;
  private int compoundNounMinLength;

  @Inject
  public MeCabKoStandardTokenizerFactory(
      Index index,
      @IndexSettings Settings indexSettings,
      Environment env,
      @Assisted String name,
      @Assisted Settings settings) {
    super(index, indexSettings, name, settings);
    setMeCabDicDir(env, settings);
    setCompoundNounMinLength(settings);
  }
  
  private void setMeCabDicDir(Environment env, Settings settings) {
    String path = settings.get(
        "mecab_dic_dir",
        MeCabKoStandardTokenizerFactory.DEFAULT_MECAB_DIC_DIR);
    if (path.startsWith("/")) {
      mecabDicDir = path;
    } else {
      try {
        mecabDicDir = env.homeFile().getCanonicalPath() + "/" + path;
      } catch (IOException e) {
        mecabDicDir = path;
      }
    }
  }
  
  private void setCompoundNounMinLength(Settings settings) {
    compoundNounMinLength = settings.getAsInt(
        "compound_noun_min_length",
        TokenGenerator.DEFAULT_COMPOUND_NOUN_MIN_LENGTH);
  }

  @Override
  public Tokenizer create(Reader reader) {
    return new MeCabKoTokenizer(
        reader,
        mecabDicDir,
        new StandardPosAppender(),
        compoundNounMinLength);
  }
}
