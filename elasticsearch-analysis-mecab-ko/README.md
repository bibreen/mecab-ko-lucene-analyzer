# mecab-ko analysis for ElasticSearch
mecab-ko Analysis Plugin은 [mecab-ko-lucene-analyzer](https://github.com/bibreen/mecab-ko-lucene-analyzer)를 elasticsearch에서 사용하는 플러그인 입니다.

  - 이 플러그인은 `mecab_ko_standard_tokenizer`를 포함하고 있습니다.
  - elasticsearch 0.90.3 버전 기준으로 작성되었습니다.

## 설명

### mecab_ko_standard_tokenizer
mecab-ko Analysis Plugin의 기본 tokenizer.

`mecab_ko_standard_tokenizer`에 세팅할 수 있는 것들은 다음과 같다.

| 세팅                         |  설명                                                                       |
| ---------------------------- | --------------------------------------------------------------------------- |
| **mecab_dic_dir**            | mecab-ko-dic 사전 경로. 기본 경로는 '/usr/local/lib/mecab/dic/mecab-ko-dic' |
| **compound_noun_min_length** | 분해를 해야하는 복합명사의 최소 길이. 기본 값은 3                           |

## 설치

### mecab-ko(형태소 분석기 엔진)과 mecab-ko-dic(사전 파일) 설치

mecab-ko와 mecab-ko-dic의 설치는 [mecab-ko-dic 설명](https://bitbucket.org/bibreen/mecab-ko-dic)을 참조하시기 바랍니다.

### libMeCab.so 설치
[mecab-java-XX.tar.gz](http://code.google.com/p/mecab/downloads/list) 를 다운받아 설치합니다.

    $ tar zxvf mecab-java-XX.tar.gz
    $ mv mecab-java-XX mecab-XX/java
    $ cd mecab-XX/java
    $ make # Makefile 에서 INCLUDE 변수에 java include directory를 설정해준다.
    $ sudo cp libMeCab.so /usr/local/lib

### ElasticSearch Plugin 설치
    bin/plugin --install analysis-mecab-ko-0.13.2 --url https://bitbucket.org/bibreen/mecab-ko-dic/downloads/elasticsearch-analysis-mecab-ko-0.13.2.zip

### ElasticSearch 실행
    $ ./elasticsearch -f -Djava.library.path=/usr/local/lib

## 테스트 스크립트
### index, query 모두 복합명사 분해를 하는 경우
    #!/bin/bash
    
    ES='http://localhost:9200'
    ESIDX='eunjeon'

    curl -XDELETE $ES/$ESIDX

    curl -XPUT $ES/$ESIDX/ -d '{
      "settings" : {
        "index":{
          "analysis":{
            "analyzer":{
              "korean":{
                "type":"custom",
                "tokenizer":"mecab_ko_standard_tokenizer"
              }
            }
          }
        }
      }
    }'

    curl -XGET $ES/$ESIDX/_analyze?analyzer=korean\&pretty=true -d '은전한닢 프로젝트'

### query에서는 복합명사 분해를 하지 않는 경우
    #!/bin/bash
  
    ES='http://localhost:9200'
    ESIDX='eunjeon'
  
    curl -XDELETE $ES/$ESIDX
  
    curl -XPUT $ES/$ESIDX/ -d '{
      "settings": {
        "index": {
          "analysis": {
            "analyzer": {
              "korean_index": {
                "type": "custom",
                "tokenizer": "mecab_ko_standard_tokenizer"
              },
              "korean_query": {
                "type": "custom",
                "tokenizer": "korean_query_tokenizer"
              }
            },
            "tokenizer": {
              "korean_query_tokenizer": {
                "type": "mecab_ko_standard_tokenizer",
                "compound_noun_min_length": 100
              }
            }
          }
        }
      }
    }'

    curl -XGET $ES/$ESIDX/_analyze?analyzer=korean_index\&pretty=true -d '무궁화 꽃'
    curl -XGET $ES/$ESIDX/_analyze?analyzer=korean_query\&pretty=true -d '무궁화 꽃'

## 라이센스
Copyright 2013 Yongwoon Lee, Yungho Yu.
`elasticsearch-analysis-mecab-ko`는 아파치 라이센스 2.0에 따라 소프트웨어를 사용, 재배포 할 수 있습니다. 더 자세한 사항은 [Apache License Version 2.0](https://github.com/bibreen/mecab-ko-lucene-analyzer/blob/master/LICENSE)을 참조하시기 바랍니다.
