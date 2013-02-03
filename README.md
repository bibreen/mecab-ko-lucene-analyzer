# mecab-ko-lucene-analyzer

## 소개

[mecab-ko-lucene-analyzer](https://github.com/bibreen/mecab-ko-lucene-analyzer)는 [mecab-ko-dic](https://bitbucket.org/bibreen/mecab-ko-dic/src)을 사용한 lucene/solr용 색인어 추출기 입니다.

다음과 같은 기능들을 제공합니다.
- 명사추출
- 합성명사 분해
- 원어절 추출

## 설치

### Mecab 설치

[여기 (MeCab-0.994)](http://code.google.com/p/mecab/downloads/detail?name=mecab-0.994.tar.gz&can=1&q=) 에서 MeCab의 소스를 다운 받고 설치합니다.

    :::text
    $ tar zxfv mecab-XX.tar.gz
    $ cd mecab-XX
    $ ./configure 
    $ make
    $ make check
    $ su
    # make install

MeCab 설치의 자세한 내용은 [MeCab 홈페이지](http://mecab.googlecode.com/svn/trunk/mecab/doc/index.html)를 참조하시기 바랍니다.

### libMeCab.so 설치

[mecab-java-XX.tar.gz](http://code.google.com/p/mecab/downloads/list) 를 다운받아 설치합니다.

    $ tar zxvf mecab-java-XX.tar.gz
    $ mv mecab-java-XX.tar.gz mecab-XX/java
    $ cd mecab-XX/java
    $ make
    $ su
    # cp libMeCab.so /usr/local/lib

### mecab-ko-dic 설치

[mecab-ko-dic 다운로드 페이지](https://bitbucket.org/bibreen/mecab-ko-dic/downloads) 에서 mecab-ko-dic의 최신 버전을 다운 받습니다.

tar.gz를 압축 해제하시고 일반적인 자유 소프트웨어와 같은 순서로 설치할 수 있습니다.
기본으로 /usr/local/lib/mecab/dic/mecab-ko-dic에 설치됩니다.

    $ tar zxfv mecab-ko-dic-XX.tar.gz
    $ cd mecab-ko-dic-XX
    $ ./configure 
    $ make
    $ su
    # make install

### mecab-ko-lucene-analyzer 다운로드

[mecab-ko-lucene-analyzer.jar](https://github.com/bibreen/mecab-ko-lucene-analyzer/downloads)와 [MeCab.jar](https://github.com/bibreen/mecab-ko-lucene-analyzer/blob/master/lib/MeCab.jar) 파일을 다운로드 받아 solr library 디렉토리로 복사합니다.

## 사용법

### solr 설정
solrconfig.xml 에 mecab-ko-lucene-analyzer.jar 와 Mecab.jar 가 있는 path를 설정합니다.

    <lib dir="../lib" regex=".*\.jar" />

schema.xml 에 fieldType 을 설정합니다.

    <!-- Korean -->
    <fieldType name="text_ko" class="solr.TextField" positionIncrementGap="100">
      <analyzer type="index">
        <tokenizer class="com.github.bibreen.mecab_ko_lucene_analyzer.StandardQueryTokenizerFactory"/>
      </analyzer>
      <analyzer type="query">
        <tokenizer class="com.github.bibreen.mecab_ko_lucene_analyzer.StandardIndexTokenizerFactory"/>
      </analyzer>
    </fieldType>

### solr 실행
libMeCab.so 파일이 있는 라이브러리 경로를 지정해 주면서 solr를 실행합니다.

    $ java -Djava.library.path="/usr/local/lib" -jar start.jar

### 실행 결과

    input:
    mecab-ko-lucene-analyzer를 사용하여 한글 검색서버를 개발하세요.

    output
    mecab | ko | lucene | analyzer | 를 | 사용하여 | 사용 | 한글 | 검색 | 서버를 | 서버 | 개발하세요 | 개발

## 라이센스
Copyright 2013 Yongwoon Lee, Yungho Yu. 
See [LICENSE-2.0.html](https://github.com/bibreen/mecab-ko-lucene-analyzer/blob/master/LICENSE-2.0.html) for further details.
