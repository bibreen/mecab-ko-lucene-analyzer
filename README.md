# mecab-ko-lucene-analyzer

## 소개

[mecab-ko-lucene-analyzer](https://github.com/bibreen/mecab-ko-lucene-analyzer)는 [mecab-ko-dic](https://bitbucket.org/bibreen/mecab-ko-dic/src)을 사용한 lucene/solr용 한국어 형태소분석기입니다.

다음과 같은 기능들을 제공합니다.
  - 명사추출
  - 합성명사 분해
  - 원어절 추출

## 특징
  - '무궁화꽃이피었습니다.'와 같이 띄어 쓰기가 잘못된 오류를 교정하여 형태소 분석이 가능합니다.
  - Standard[Index|Query]Tokenizer의 경우, 명사뿐 아니라 품사가 결합된 어절도 Token으로 뽑아냅니다.
    철수가 학교에 간다. -> 철수가, 철수, 학교에, 학교, 간다
  - 문장의 끝에 문장의 끝을 알리는 기호 "`.!?`"가 있으면 더 자연스럽게 형태소 분석이 됩니다.
  - Apache Lucene/Solr 4.0 버전 기준으로 작성되었습니다.

## 설치

### Mecab 설치

[여기 (MeCab-0.994)](http://code.google.com/p/mecab/downloads/detail?name=mecab-0.994.tar.gz&can=1&q=) 에서 MeCab의 소스를 다운 받고 설치합니다.

    $ tar zxfv mecab-XX.tar.gz
    $ cd mecab-XX
    $ ./configure 
    $ make
    $ make check
    $ su
    # make install

MeCab 설치의 자세한 내용은 [MeCab 홈페이지](http://mecab.googlecode.com/svn/trunk/mecab/doc/index.html)를 참조하시기 바랍니다.

### MeCab.jar와 libMeCab.so 설치

[mecab-java-XX.tar.gz](http://code.google.com/p/mecab/downloads/list) 를 다운받아 설치합니다. \(주의: Makefile에서 INCLUDE 값을 자신의 환경에 맞게 변경해야 합니다.\)

    $ tar zxvf mecab-java-XX.tar.gz
    $ mv mecab-java-XX.tar.gz mecab-XX/java
    $ cd mecab-XX/java
    $ make
    $ cp MeCab.jar [solr 라이브러리 디렉터리]
    $ su
    # cp libMeCab.so /usr/local/lib

### mecab-ko-dic 설치

[mecab-ko-dic 다운로드 페이지](https://bitbucket.org/bibreen/mecab-ko-dic/downloads) 에서 `mecab-ko-dic`의 최신 버전을 다운 받습니다. *반드시  mecab-ko-dic-1.1.0-XXXX 이상의 버전을 사용하여야 합니다.*

tar.gz를 압축 해제하시고 일반적인 자유 소프트웨어와 같은 순서로 설치할 수 있습니다.
기본으로 `/usr/local/lib/mecab/dic/mecab-ko-dic`에 설치됩니다.

    $ tar zxfv mecab-ko-dic-XX.tar.gz
    $ cd mecab-ko-dic-XX
    $ ./configure 
    $ make
    $ su
    # make install

### mecab-ko-lucene-analyzer 다운로드
[mecab-ko-lucene-analyzer 다운로드 페이지](https://bitbucket.org/bibreen/mecab-ko-dic/downloads)에서 `mecab-ko-lucene-analyze`의 최신 버전을 받아 solr library 디렉터리로 복사합니다.

### mecab-ko-lucene-analyzer 소스 다운로드 및 컴파일
[프로젝트 소스](https://github.com/bibreen/mecab-ko-lucene-analyzer/archive/master.zip)를 다운로드 받아 이클립스에 import하여 `make-jar.jardesc` 를 실행하여 `mecab-ko-lucene-analyzer.jar` 파일을 생성합니다.

## 사용법

### solr 설정
`solrconfig.xml` 에 `mecab-ko-lucene-analyzer-XX.jar` 와 `Mecab.jar` 가 있는 경로를 설정합니다.

    <lib dir="../lib" regex=".*\.jar" />

`schema.xml` 에 `fieldType` 을 설정합니다.

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
`libMeCab.so` 파일이 있는 라이브러리 경로를 지정해 주면서 solr를 실행합니다.

    $ java -Djava.library.path="/usr/local/lib" -jar start.jar

### 분석 결과

    input:
    mecab-ko-lucene-analyzer를 사용하여 한글 검색서버를 개발하세요.

    output:
    mecab | ko | lucene | analyzer를 | analyzer | 사용하여 | 사용 | 한글 | 검색 | 서버를 | 서버 | 개발하세요 | 개발

## 라이센스
Copyright 2013 Yongwoon Lee, Yungho Yu.
`mecab-ko-lucene-analyzer`는 아파치 라이센스 2.0에 따라 소프트웨어를 사용, 재배포 할 수 있습니다. 더 자세한 사항은 [Apache License Version 2.0](https://github.com/bibreen/mecab-ko-lucene-analyzer/blob/master/LICENSE)을 참조하시기 바랍니다.
