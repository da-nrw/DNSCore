#!/bin/bash

# Author: Daniel M. de Oliveira

VERSION=`cat ../VERSION.txt`

mvn clean
mvn package

TARGET=target/deliverable
rm -rf $TARGET
mkdir $TARGET
cp target/ContentBroker-$VERSION.jar $TARGET/ContentBroker.jar
if [ $? -ne 0 ]
then
	echo target has to be build first by mvn package
	exit
fi

mkdir $TARGET/conf
cp -r src/main/fido $TARGET
cp -r src/main/jhove $TARGET
cp -r src/main/xslt $TARGET/conf
cp src/main/resources/premis.xsd $TARGET/conf
cp src/main/resources/xlink.xsd $TARGET/conf
cp src/main/resources/frame.jsonld $TARGET/conf
cp src/main/scripts/ffmpeg.sh $TARGET
cp src/main/scripts/ContentBroker_stop.sh $TARGET
cp src/main/scripts/ContentBroker_start.sh $TARGET
cp src/main/scripts/cbTalk.sh $TARGET
cp src/main/scripts/fido.sh $TARGET
cp src/main/scripts/PDFA_def.ps $TARGET/conf
cp src/main/resources/healthCheck.avi $TARGET/conf
cp src/main/resources/healthCheck.tif $TARGET/conf
cp src/main/resources/frame.jsonld $TARGET/conf
mkdir $TARGET/log
touch $TARGET/log/contentbroker.log
echo -e "ContentBroker Version $VERSION\nWritten by\n Daniel M. de Oliveira\n Jens Peters\n Sebastian Cuy\n Thomas Kleinke" > $TARGET/README.txt

cd $TARGET
tar cf ../../../installation/binary-repository/ContentBroker-binaries-$VERSION.tar *
cd ../../../installation
find . ! -name ".gitignore" -type f -maxdepth 1 -exec rm {} \;
cd ../ContentBroker

#for last; do true; done
#if [[ $last == --version=* ]]
#then
#  VERSION=`echo $last | sed 's/--version=//'`
#fi
