#!/bin/bash

version=$(grep -m 1 "<version>.*</version>" pom.xml | sed -n 's/.*>\(.*\)-SNAPSHOT.*/\1/p')
lucene_analyzer=mecab-ko-lucene-analyzer
mecab_loader=mecab-ko-mecab-loader
elasticsearch_analysis=elasticsearch-analysis-mecab-ko

# make Lucene/Solr package
dir=mecab-ko-lucene-analyzer-$version
mkdir $dir
cp lucene-analyzer/target/$lucene_analyzer-$version-SNAPSHOT.jar $dir/$lucene_analyzer-$version.jar
cp mecab-loader/target/$mecab_loader-$version-SNAPSHOT.jar $dir/$mecab_loader-$version.jar
tar czf $dir.tar.gz $dir
rm -rf $dir

# make ElasticSearch plugin
dir=$elasticsearch_analysis-$version
mkdir $dir
cp lucene-analyzer/target/$lucene_analyzer-$version-SNAPSHOT.jar $dir/$lucene_analyzer-$version.jar
cp mecab-loader/target/$mecab_loader-$version-SNAPSHOT.jar $dir/$mecab_loader-$version.jar
cp elasticsearch-analysis-mecab-ko/target/$elasticsearch_analysis-$version-SNAPSHOT.jar $dir/$elasticsearch_analysis-$version.jar
cp ~/.m2/repository/org/chasen/mecab/mecab-java/0.996/mecab-java-0.996.jar $dir/.
pushd $dir
zip $elasticsearch_analysis-$version.zip *.jar
mv $elasticsearch_analysis-$version.zip ../.
popd
rm -rf $dir
