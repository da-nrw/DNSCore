#!/bin/bash

# Author: Daniel M. de Oliveira

echo call src/main/bash/collect $1 $2


SED_BIN=sed
OS=`uname -s`
case "$OS" in
SunOS)
				SED_BIN=gsed
        ;;
esac

mkdir $1/conf
cp src/main/resources/healthCheck.avi $1/conf
cp src/main/resources/healthCheck.tif $1/conf
cp -r ../3rdParty/fido/fido $1/
cp -r ../3rdParty/jhove $1/
cp src/main/sh/jhove $1/jhove
cp src/main/conf/jhove.conf $1/jhove/conf
cp src/test/resources/at/ATUseCaseIngest1.tgz $1/conf/basic_test_package.tgz
cp src/main/bash/fido.sh $1/
cp src/main/bash/ffmpeg.sh $1/
cp src/main/bash/configure.sh $1/
cp src/main/bash/cbTalk.sh $1/
cp -r src/main/xslt $1/conf
cp src/main/xsd/premis.xsd $1/conf
cp src/main/xsd/xlink.xsd $1/conf
cp src/main/resources/frame.jsonld $1/conf
cp src/main/conf/PDFA_def.ps $1/conf
cp src/main/resources/frame.jsonld $1/conf
mkdir $1/activemq-data
mkdir $1/log
touch $1/log/contentbroker.log
echo -e "ContentBroker Version $2\nWritten by\n Daniel M. de Oliveira\n Jens Peters\n Sebastian Cuy\n Thomas Kleinke\n Polina Gubaidullina" > $1/README.txt

echo "copying Version and Build Info to DaWeb"
cp -f ../DAWeb/application.properties.template ../DAWeb/application.properties
mv ../DAWeb/application.properties ../DAWeb/application.properties.tmp
$SED_BIN "s@app\.version=.*@app\.version=$2@" ../DAWeb/application.properties.tmp >> ../DAWeb/application.properties
rm ../DAWeb/application.properties.tmp
