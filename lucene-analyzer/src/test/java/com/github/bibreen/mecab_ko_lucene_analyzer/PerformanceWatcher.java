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

import org.chasen.mecab.Lattice;
import org.chasen.mecab.Model;
import org.chasen.mecab.Node;
import org.chasen.mecab.Tagger;

public class PerformanceWatcher {

  static {
    try {
      System.loadLibrary("MeCab");
    } catch (UnsatisfiedLinkError e) {
      System.err.println(
          "Cannot load the example native code.\n"
          + "Make sure your LD_LIBRARY_PATH contains \'.\'\n" + e);
      System.exit(1);
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    Model model = new Model("-d /usr/local/lib/mecab/dic/mecab-ko-dic");
    Tagger tagger = model.createTagger();
    Lattice lattice = model.createLattice();
    long start, end;
    
	  String text = "화학(, )은 물질의 성질, 조성, 구조, 변화 및 그에 수반하는 에너지의 변화를 연구하는 자연과학의 한 분야이다. 물리학 역시도 물질을 다루는 학문이지만, 물리학이 원소와 화합물을 모두 포함한 물체의 운동과 에너지, 열적·전기적·광학적·기계적 속성을 다루고 이러한 현상으로부터 통일된 이론을 구축하려는 것과는 달리 화학에서는 물질 자체를 연구 대상으로 한다.學園出版公社 事典編纂局 편, 〈화학〉, 《學園世界大百科事典》(Vol. 32), 서울:學園出版公社, 1993, 330~334쪽. 화학은 이미 존재하는 물질을 이용하여 특정한 목적에 맞는 새로운 물질을 합성하는 길을 제공하며, 이는 농작물의 증산, 질병의 치료 및 예방, 에너지 효율 증대, 환경오염 감소 등 여러 가지 이점을 제공한다.Oxtoby, D. W. et al., \"Principles of Modern Chemisty\", 6th edition, Belmont: Thomson Brooks/Cole, 2007, p. 2주요 개념.원자(atom)와 원소(element).화학에서의 기초적인 개념은 일반적인 방법으로는 더 이상 나누어지지 않는 기초적인 요소가 존재한다는 것인데, 이 기초적인 요소를 원자라 한다. 원자는 고대 그리스에서부터 그 존재가 주장되었는데, 1803년 존 돌턴에 의해서 원자론으로 정리되었다. 20세기 초, 화학자들은 원자를 구성하는 더 작은 입자들, 즉 전자, 양성자, 중성자가 존재한다는 사실을 발견하였다. 전자는 음전하를 띠고 있고, 양성자는 양전하를 띠고 있으며, 중성자는 전하를 띠지 않고 있다. 원자는 양성자와 중성자로 구성되어 있는 원자핵을 가지고 있으며 전자는 이 주변에 오비탈을 이루며 분포되어 있다.Parker, S. P. et al., \"Chemistry\", \"McGraw-Hill encyclopedia of chemistry\", New York: McGraw-Hill, 1993, pp. 202~204.원소는 일반적인 화학적, 물리학적 방법으로는 분해되지 않는 물질을 의미한다.Oxtoby, D. W. et al., op. cit., p. 7. 원소는 원자핵에 존재하는 양성자 수로 정의되는 원자 번호로 구별된다. 산소, 황, 주석, 철 등은 원소이다. 19세기 중엽까지 약 80가지의 원소가 발견되었는데, 이들은 주기율에 따라 배열될 수 있다.동위 원소(isotope).대부분의 원소는 동위 원소를 가진다. 동위 원소는 원자 번호는 같으나, 중성자수가 다른 원소를 뜻한다. 동위 원소는 화학적인 성질은 동일하나, 원자량의 차이를 이용하여 분리할 수 있다. 자연에서도 발견되는 92개의 원소 중 88개는 동위 원소가 지표면 상에 존재한다. 자연에서 발견되지 않더라도 동위 원소는 핵반응을 이용하여 만들어낼 수 있다. 어떤 동위 원소는 방사능을 가지기도 하는데, 이 경우 동위 원소의 원자핵은 불안정하고 방사선을 방출하며 자연적으로 붕괴된다.분자(molecule)와 화학 반응.분자란 두 개 이상의 원자가 결합하여 생성되는 입자를 말한다. 일정한 개수의 원자가 특정하게 정렬되어 서로 결합할 경우 분자가 형성된다. 원자가 원소의 최소단위이듯, 분자는 화합물의 최소단위가 된다. 원자가 결합될 때 전자의 재배치가 일어나는데, 이는 화학에서의 중요한 관심사중 하나이다.화학 반응은 원자 혹은 분자가 화학적인 변화를 겪는 일을 말한다. 화학 반응은 원자간의 결합이 끊어지는 일과 다시 이어지는 일을 포함한다. 결합이 끊어질 때는 에너지가 흡수되고, 결합이 이어질 때는 에너지가 방출된다. 화학 반응의 간단한 예로는 수소와 산소가 반응하여 물이 되는 것을 들 수 있다. 반응식은 다음과 같다.반응식에서 알 수 있듯이, 화학 반응에서는 원자가 새로 생성되거나 나타나는 일이 일어나지 않는다. ΔH는 에너지 또는 엔탈피 변화를 뜻한다. 반응은 발열반응일 수도 있고, 흡열반응일 수도 있다. 위 반응의 경우는 발열반응인데, 이는 계로부터 주위로 열이 이동하였다는 의미이다.화학 결합.화학 결합을 주된 세 가지 부류로 나누어보면 이온 결합, 공유 결합 그리고 금속결합으로 나눌 수 있다. 이온이란 전하를 띤 원자 또는 분자를 뜻한다. 이온 결합은 양전하와 음전하의 전기적인 인력에 의해서 생성되는 화학 결합이다. 예를 들면 염화 나트륨은 양전하를 띤 나트륨 이온(Na+)과 염소 이온(Cl- 사이의 전기적인 결합으로 이루어진 이온 화합물이다. 이러한 물질을 물에 녹이면 이온은 물 분자에 의해 수화되고 이렇게 해서 만들어진 수용액은 전기전도도를 가진다.공유 결합은 오비탈이 겹쳐진 결과 두 원자가 전자쌍을 공유하게 되어 생성되는 결합을 의미한다. 공유 결합이 형성되는 결합은 발열반응인데, 이때 방출되는 에너지의 양이 그 결합의 결합 에너지이다. 결합 에너지만큼의 에너지를 그 결합에 가해주면 결합은 끊어질 수 있다.Ibid., pp. 80~81.금속 결합은 금속 원자에서 전자들이 떨어져 나와 자유전자를 생성하게 되어 생성되는 결합을 의미한다. 금속의 특성인 연성과 전성이 생성되는 이유이기도 하다.화합물.화합물은 구성하고 있는 원자의 종류, 수, 배치에 의해서 그 특성이 결정된다. 자연에서 찾을 수 있거나 인공적으로 합성할 수 있는 화합물의 수는 엄청나고, 이들 중 대부분은 유기 화합물이다. 유기 화합물을 이루는 주된 화학 원소인 탄소는 다른 화학 원소와는 다르게 매우 긴 사슬 형태로 정렬될 수 있으며, 같은 수많은 이성질체를 형성할 수 있다. 예를 들어, 분자식 C8H16O는 약 천 개의 서로 다른 화합물을 뜻할 수 있다.분과.화학은 취급 대상 및 대상의 취급 방법에 따라서 몇 가지 분과로 구분될 수 있다. 일반화학은 화학 전체에 대한 입문과 통찰로 이루어진다. 물질을 분석하는 분석화학은 크게 물질의 존재를 취급하는 정성 분석과 물질의 양을 결정하는 정량 분석으로 나눌 수 있다. 탄소를 포함한 유기 화합물을 다루는 유기화학과 유기 화합물을 제외한 무기 화합물을 다루는 무기화학도 있다. 물리학과 화학의 경계에는 물리화학이 있고 생물학과의 경계에는 생화학이 있다. 물리화학에서 특히 분자의 구조와 성질과의 관계를 다루는 부분을 구조화학이라고 부르기도 한다. 제2차 세계 대전 이후에는 방사성 물질을 다루는 방사화학이 발전하였고 화학 공업을 다루는 공업화학도 있다.化學大辭典編集委員會 편, 성용길, 김창홍 역, 〈화학의 분류〉, 《화학대사전》(Vol. 10), 서울: 世和, 2001, 627쪽.이 외에도 화학의 분과는 매우 다양하다.화학의 분과는 전통적으로 다음과 같은 5가지로 나눌 수 있으며, 각각의 분과는 더욱 세분화될 수 있다.무기화학(inorganic chemistry).무기화학은 유기화학에서 다루지 않는 물질을 다루며 주로 금속이나 준금속이 포함된 물질에 대해서 연구한다. 따라서 무기화학에서는 매우 넓은 범위의 화합물을 다루게 된다. 초기에는 광물의 구성이나 새 원소의 발견이 주요 관심사였고 여기서부터 지구화학이 분기되었다. 주로 전이 금속 등을 이용한 촉매나 생물에서 산소 수송, 광합성, 질소 고정 등의 과정에서 중요한 역할을 하는 금속 원자들에 대해 연구하며 이 외에도 세라믹, 복합재료, 초전도체등에 대한 연구를 한다.물리화학(physical chemistry).물리화학은 화학적 현상에 대한 해석과 이를 설명하기 위한 물리적 원리들에 대해 다루는 분과이다. 화학반응에 관련된 열역학적 원리와 물질의 물리학적 성질에 대한 설명은 물리화학이 다루는 고전적인 주제이다. 물리화학은 양자화학의 발전에도 큰 기여를 하였다. 분광계나 자기 공명, 회절 기기 등 물리화학에서 사용하는 실험 장비나 실험 방법들은 다른 화학의 분과에서도 매우 많이 사용된다. 물리화학이 다루는 대상은 유기 화합물, 무기 화합물, 혼합물을 모두 포함한다.분석화학(analytical chemistry).분석화학은 물질의 조성이나 혼합물의 구성요소 등을 결정하는 방법에 대해서 연구하는 화학의 분과이다. 혼합물을 이루고 있는 성분의 탐색, 분리, 정량과 분자를 이루고 있는 원자의 비율을 측정하여 분자식을 결정하는 일 등이 분석화학에서 행해진다. 1950년대의 분석화학의 발전은 많은 질량 분석계를 포함한 분석 기구의 등장을 불러일으켰다. 이 외에도 고해상도 크로마토그래피, 전기화학에서의 많은 실험방법 등은 분석화학에 있어서 중요한 분석법이다. 분석화학에 있어서 최종 목표는 더 정확한 측정법이나 측정기기 등을 개발하는 것이다. 분석화학의 발전으로 인해 환경오염 물질 등을 피코그램의 수준에서도 감지하는 것이 가능해졌다.생화학(biochemistry).생화학은 생물을 화학의 관점에서 다루는 학문이다. 식물이나 동물의 세포에서 발견되는 물질이나 일어나는 화학 반응들이 주 관심사이다. 생명체에서 발견되는 탄수화물, 지방, 단백질, 핵산, 호르몬 등은 유기 화합물이라서 유기화학에서도 다루어지기도 하나, 이들 화합물에 관련된 물질대사 과정이나 조절 과정에 대한 연구는 생화학의 고유 분야이다. 효소와 조효소, 그리고 이들의 작용 과정에 대해서도 연구하며, 세포막을 통과하는 이온과 분자, 신경전달물질과 다른 조절 물질들의 작용에 대해서도 연구한다. 생화학은 내분비학, 유전학, 면역학, 바이러스학의 발전에 큰 영향을 끼쳤다.유기화학(organic chemistry).유기화학은 탄소로 이루어진 화합물을 연구하는 분과이다. 원래 유기 화합물은 식물이나 동물로부터 추출해낸 화합물을 뜻하였으나 지금은 유기 화합물의 범위가 크게 넓어져 탄소 사슬 또는 탄소 고리를 가진 모든 화합물을 뜻한다. 유기화학의 오랜 관심사는 유기 화합물의 합성 메커니즘이다. 현대에 들어서 핵자기 공명법과 엑스레이 회절법 등이 개발되어 유기 화합물 분석에 있어서 매우 중요한 방법으로 자리잡았다. 플라스틱, 합성섬유등의 고분자물질 등도 유기화학에서 다루어진다.";
    lattice.set_sentence(text);
//    if (tagger.parse(lattice)) {
//      for (Node node = lattice.bos_node(); node != null; node = node.getNext()) {
//         System.out.println(node.getSurface() + "\t" + node.getFeature());
//      }
//      System.out.println("EOS\n");
//    }
    
    start = System.currentTimeMillis();
    tagger.parse(lattice);
    for (Node node = lattice.bos_node(); node != null; node = node.getNext()) {
       System.out.println(node.getSurface() + "\t" + node.getFeature());
    }
    System.out.println("EOS\n");
    end = System.currentTimeMillis();
    System.out.println("analyze time: " + (end - start));

    start = System.currentTimeMillis();
    tagger.parse(lattice);
    end = System.currentTimeMillis();
    System.out.println("analyze time: " + (end - start));

    start = System.currentTimeMillis();
    tagger.parse(lattice);
    end = System.currentTimeMillis();
    System.out.println("analyze time: " + (end - start));

  }

}
