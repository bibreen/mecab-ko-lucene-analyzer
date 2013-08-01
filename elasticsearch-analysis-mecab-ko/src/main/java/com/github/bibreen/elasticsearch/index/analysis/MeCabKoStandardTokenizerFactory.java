package com.github.bibreen.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;
import com.github.bibreen.mecab_ko_lucene_analyzer.*;

import java.io.Reader;

/**
 *
 */
public class MeCabKoStandardTokenizerFactory extends AbstractTokenizerFactory {

  @Inject
  public MeCabKoStandardTokenizerFactory(Index index, @IndexSettings Settings indexSettings, @Assisted String name, @Assisted Settings settings) {
    super(index, indexSettings, name, settings);
  }

  @Override
  public Tokenizer create(Reader reader) {
    return new MeCabKoTokenizer(
        reader,
        "/usr/local/lib/mecab/dic/mecab-ko-dic/",
        new StandardPosAppender(),
        3);
  }
}
