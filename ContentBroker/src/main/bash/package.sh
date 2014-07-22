#!/bin/bash

# Author: Daniel M. de Oliveira

INSTALLER=target/installation
CBTAR_SRC=target/installation_tar

VERSION="v`cat ../VERSION.txt` (build: `git rev-parse HEAD` created on `date`)"


if [ $# -lt 1 ]
then
	echo We need at least one argument for the env here
	exit
fi
echo "calling package.sh $1 $2"


function createStorageFolder(){
	mkdir $CBTAR_SRC/storage/
	mkdir $CBTAR_SRC/storage/grid
	mkdir -p $CBTAR_SRC/storage/pips/institution/TEST
	mkdir -p $CBTAR_SRC/storage/pips/public/TEST
	mkdir -p $CBTAR_SRC/storage/user/TEST/outgoing
	mkdir -p $CBTAR_SRC/storage/work/TEST
	mkdir -p $CBTAR_SRC/storage/ingest/TEST
}

mkdir $INSTALLER
mkdir $CBTAR_SRC

cp target/ContentBroker-SNAPSHOT.jar $CBTAR_SRC/ContentBroker.jar
if [ $? -ne 0 ]
then
	echo target has to be build first by mvn package. remember that you cannot build anything other than dev on development machines.
	echo any other target environment will need the CT tests to pass
	exit
fi

mkdir $CBTAR_SRC/conf
cp -r ../3rdParty/fido $CBTAR_SRC
cp -r ../3rdParty/jhove $CBTAR_SRC
cp src/main/sh/jhove $CBTAR_SRC/jhove
cp src/main/conf/jhove.conf $CBTAR_SRC/jhove/conf
cp -r src/main/xslt $CBTAR_SRC/conf
cp src/main/xsd/premis.xsd $CBTAR_SRC/conf
cp src/main/xsd/xlink.xsd $CBTAR_SRC/conf
cp src/main/resources/frame.jsonld $CBTAR_SRC/conf
cp src/main/bash/ffmpeg.sh $CBTAR_SRC
cp src/main/bash/ContentBroker_stop.sh $CBTAR_SRC
cp src/main/bash/ContentBroker_start.sh $CBTAR_SRC
cp src/main/bash/cbTalk.sh $CBTAR_SRC
cp src/main/bash/fido.sh $CBTAR_SRC
cp src/main/conf/PDFA_def.ps $CBTAR_SRC/conf
cp src/main/resources/healthCheck.avi $CBTAR_SRC/conf
cp src/main/resources/healthCheck.tif $CBTAR_SRC/conf
cp src/main/resources/frame.jsonld $CBTAR_SRC/conf
cp src/test/resources/at/ATUseCaseIngest1.tgz $CBTAR_SRC/conf/basic_test_package.tgz
mkdir $CBTAR_SRC/activemq-data
mkdir $CBTAR_SRC/log
touch $CBTAR_SRC/log/contentbroker.log
echo -e "ContentBroker Version $VERSION\nWritten by\n Daniel M. de Oliveira\n Jens Peters\n Sebastian Cuy\n Thomas Kleinke\n Polina Gubaidullina" > $CBTAR_SRC/README.txt

cp src/main/bash/install.sh $INSTALLER
cp src/main/bash/configure.sh $INSTALLER
cp src/main/xml/beans.xml.node $INSTALLER
cp src/main/xml/beans.xml.pres $INSTALLER
cp src/main/xml/beans.xml.full $INSTALLER
cp src/main/xml/beans.xml.full.dev $INSTALLER
cp src/main/xml/logback.xml.debug $INSTALLER/logback.xml


cp src/main/xml/hibernateCentralDB.cfg.xml.$1 $INSTALLER/hibernateCentralDB.cfg.xml
case "$1" in
dev)
	sed "s@CONTENTBROKER_ROOT@$2@" src/main/conf/config.properties.dev  > $INSTALLER/config.properties # TODO move to pre-integration-test.sh
	createStorageFolder	
	cp -f src/main/bash/ffmpeg.sh.fake $INSTALLER/ffmpeg.sh
	cp src/main/conf/sqltool.rc ~/
;;
ci)
	cp src/main/conf/config.properties.ci $INSTALLER/config.properties
;;
esac


cd ../DAWeb
./build.sh prod
if [ "$?" = "1" ]
then
	echo there was an error in ./build.sh prod
	exit 1
fi 
cd ../ContentBroker


cd $CBTAR_SRC
rm ../installation/ContentBroker.tar 2>/dev/null
tar cf ../installation/ContentBroker.tar *
cd ../..
