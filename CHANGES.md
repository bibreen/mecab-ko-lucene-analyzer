# mecab-ko-lucene-analyzer ChangeLog

## 0.13.3
  - StandardPosAppender에서 체언 접두사(XPN) 인덱싱 방식 변경. (독립적인 토큰이 되도록 변경)

## 0.13.2
  - StandardPosAppender에서 명사 파생 접미사(XSN) 인덱싱 방식 변경. (독립적인 토큰이 되도록 변경)

## 0.13.1
  - UNKNOWN 형태소(사전에 없는 형태소)가 인덱스에서 제외되는 오류 수정

## 0.13.0
  - ElasticSearch plugin 추가
  - 어미와 보조 용언을 어절로 결합하는 규칙 제거

## 0.12.0
  - mecab-ko-dic-1.4.0을 위한 코드 추가

## 0.11.0
  - Apache Lucene/Solr 4.3.1 버전에 맞춰 코드 수정.

## 0.10.0
  - 복합명사 처리 로직 재작성
  - 기분석 사전 처리 로직 추가
  - StandardIndexTokenizerFactory에서 decompoundMinLength 속성 제거
  - StandardIndexTokenizerFactory에서 compoundNounMinLength 속성 추가. 해당 길이 보다 짧은 복합명사는 분해하지 않음. 기본값은 3.

## 0.9.5
  - jar 패키지를 mecab-ko-mecab-loader.jar와 mecab-ko-lucene-analyzer.jar로 분리. (mecab-ko-mecab-loader.jar는 JNI 클래스를 포함하므로 System classpath에 위치해야 함)
  - 위의 사항과 관련하여 README.md를 변경

## 0.9.4

  - TokenGenerator.decompoundNoun() 로직 변경 및 관련 유닛 테스트 수정.
  - Token TypeAttribute에 품사 태그 넣도록 코드 수정.
  - '떨어진 명사 파생 접미사(XSN)'에도 떨어진 조사와 같은 처리를 하도록 수정.

## 0.9.3

  - StandardIndexTokenizerFactory에서 decompoundMinLength 의 역할 변경함. 복합명사 분해시 분해되는 토큰의 최소 길이 설정. 기본값은 2.

    decompoundMinLength = 1 : "자동차" -> "자동차", "자동", "차"
    decompoundMinLength = 2 : "자동차" -> "자동차", "자동"

## 0.9.2

  - StandardIndexTokenizerFactory에서 decompoundMinLength(복합명사 분해 최소 길이)를 세팅할 수 있도록 함. 복합명사의 길이가 decompoundMinLength 보다 작으면 분해하지 않음.

## 0.9.1

  - 오분석 된 조사 처리 방식 적용. [참고](https://bitbucket.org/bibreen/mecab-ko-dic/issue/1/--------------------)

## 0.9.0

  - 최초 배포
