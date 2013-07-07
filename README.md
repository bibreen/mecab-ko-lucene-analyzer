# mecab-ko-lucene-analyzer

## 소개

[mecab-ko-lucene-analyzer](https://github.com/bibreen/mecab-ko-lucene-analyzer)는 [mecab-ko](https://bitbucket.org/bibreen/mecab-ko), [mecab-ko-dic](https://bitbucket.org/bibreen/mecab-ko-dic)을 사용한 lucene/solr용 한국어 형태소 분석기입니다.

다음과 같은 기능들을 제공합니다.

  - 명사추출
  - 복합명사 분해
  - 원어절 추출

## 특징

  - '무궁화꽃이피었습니다.'와 같이 띄어 쓰기가 잘못된 오류를 교정하여 형태소 분석이 가능합니다.
  - Standard[Index|Query]Tokenizer의 경우, 명사뿐 아니라 품사가 결합된 어절도 Token으로 뽑아냅니다.

        철수가 학교에 간다. -> 철수가, 철수, 학교에, 학교, 간다
  - 문장의 끝에 문장의 끝을 알리는 기호 "`.!?`"가 있으면 더 자연스럽게 형태소 분석이 됩니다.
  - Apache Lucene/Solr 4.3.X 버전 기준으로 작성되었습니다.

## 설치

### mecab-ko(형태소 분석기 엔진)과 mecab-ko-dic(사전 파일) 설치

mecab-ko와 mecab-ko-dic의 설치는 [mecab-ko-dic 설명](https://bitbucket.org/bibreen/mecab-ko-dic)을 참조하시기 바랍니다.

### MeCab.jar와 libMeCab.so 설치
Solr example(Solr with Jetty)의 사용을 기준으로 설명합니다.

[mecab-java-XX.tar.gz](http://code.google.com/p/mecab/downloads/list) 를 다운받아 설치합니다.

    $ tar zxvf mecab-java-XX.tar.gz
    $ mv mecab-java-XX mecab-XX/java
    $ cd mecab-XX/java
    $ make # Makefile 에서 INCLUDE 변수에 java include directory를 설정해준다.
    $ cp MeCab.jar [solr 디렉터리]/example/lib/ext # JNI 클래스는 System classpath에 위치해야 합니다. Jetty는 기본값으로 $jetty.home/lib/ext에 추가적인 jar를 넣을 수 있습니다.
    $ sudo cp libMeCab.so /usr/local/lib

__주의 사항__

  - mecab-ko의 버전에 맞는 mecab-java-XX.tar.gz를 선택해야 합니다. mecab-0.996-ko.0.9.0 버전에서는 mecab-java-0.996을 사용해야 합니다.
  - Makefile에서 INCLUDE 값을 자신의 환경에 맞게 변경해야 합니다.
  - OpenJDK를 사용하시는 경우, 최적화 옵션을 -O나 -O1로 고쳐야 합니다. [mecab-ko-lucene-analyzer OpenJDK에서 사용하기](http://eunjeon.blogspot.kr/2013/04/mecab-ko-lucene-analyzer-openjdk.html) 참조

### mecab-ko-lucene-analyzer 다운로드 및 설치
[mecab-ko-lucene-analyzer 다운로드 페이지](https://bitbucket.org/bibreen/mecab-ko-dic/downloads)에서 `mecab-ko-lucene-analyzer-XX.tar.gz`의 최신 버전을 다운 받아 압축을 풀면 두개의 jar파일이 있습니다. 

  - mecab-ko-mecab-loader-XX.jar: System classpath에 복사합니다. (ex: `[solr 디렉터리]/example/lib/ext`)
  - mecab-ko-lucene-analyzer-XX.jar: Solr 라이브러리 디렉터리에 설치합니다. (ex: `[solr 디렉터리]/example/solr/lib`)

#### mecab-ko-lucene-analyzer 버전별 mecab-ko-dic, Lucene/Solr 지원 버전

<table border="0" style="border-width:1px;border-color:#999999;border-collapse:collapse;border-style:solid;">
<tr style="background:#DDDDDD;">
<td style="padding:0 1em;" align="center"><strong>mecab-ko-lucene-analyzer</strong></td>
<td style="padding:0 1em;" align="center"><strong>mecab-ko-dic</strong></td>
<td style="padding:0 1em;" align="center"><strong>Lucene/Solr</strong></td>
</tr>
<tr>
<td style="padding:0 1em;"><strong><font face="monospace" size="-1">0.11.x</font></strong></td>
<td style="padding:0 1em;"><font size="-1">mecab-ko-dc-1.3.0 or higher</font></td>
<td style="padding:0 1em;"><font size="-1">Lucene/Solr 4.3.x</font></td>
</tr>
<tr>
<td style="padding:0 1em;"><strong><font face="monospace" size="-1">0.10.x</font></strong></td>
<td style="padding:0 1em;"><font size="-1">mecab-ko-dc-1.3.0 or higher</font></td>
<td style="padding:0 1em;"><font size="-1">Lucene/Solr 4.1.x - 4.2.x</font></td>
</tr>
<td style="padding:0 1em;"><strong><font face="monospace" size="-1">0.9.x</font></strong></td>
<td style="padding:0 1em;"><font size="-1">mecab-ko-dc-1.1.0 or higher</font></td>
<td style="padding:0 1em;"><font size="-1">Lucene/Solr 4.1.x - 4.2.x</font></td>
</tr>
</table>

## 사용법

### solr 설정

#### solrconfig.xml 설정
`solrconfig.xml` 에 `mecab-ko-lucene-analyzer-XX.jar`가 있는 경로를 설정합니다.

    <lib dir="../lib" regex=".*\.jar" />

#### schema.xml 설정
`schema.xml` 에 `fieldType` 을 설정합니다.

##### query에서는 복합명사 분해를 하지 않는 경우

    <!-- Korean -->
    <fieldType name="text_ko" class="solr.TextField" positionIncrementGap="100">
      <analyzer type="index">
        <tokenizer class="com.github.bibreen.mecab_ko_lucene_analyzer.StandardIndexTokenizerFactory"/>
      </analyzer>
      <analyzer type="query">
        <tokenizer class="com.github.bibreen.mecab_ko_lucene_analyzer.StandardQueryTokenizerFactory"/>
      </analyzer>
    </fieldType>

##### index, query 모두 복합명사 분해를 하는 경우

    <!-- StandardIndexTokenizerFactory는 compoundNounMinLength를 속성으로 받을 수 있습니다.
         분해를 하는 복합명사의 최소 길이를 뜻하며 기본 값은 3입니다. 이 경우, 길이가 3미만인 복합명사는 분해하지 않습니다.
    -->
    <!-- Korean -->
    <fieldType name="text_ko" class="solr.TextField" positionIncrementGap="100">
      <analyzer>
        <tokenizer class="com.github.bibreen.mecab_ko_lucene_analyzer.StandardIndexTokenizerFactory" compoundNounMinLength="3"/>
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
