#!/bin/bash

# Author: Daniel M. de Oliveira

INSTALLER=../installation/
VERSION=`cat ../VERSION.txt`

if [ $# -lt 1 ]
then
	echo We need at least one argument for the env here
	exit
fi
echo params are $1 $2

function createStorageFolder(){
	mkdir $CBTAR_SRC/storage/
	mkdir $CBTAR_SRC/storage/grid
	mkdir -p $CBTAR_SRC/storage/dip/institution/TEST
	mkdir -p $CBTAR_SRC/storage/dip/public/TEST
	mkdir -p $CBTAR_SRC/storage/user/TEST/outgoing
	mkdir -p $CBTAR_SRC/storage/fork/TEST
	mkdir -p $CBTAR_SRC/storage/ingest/TEST
}


CBTAR_SRC=target/deliverable
mkdir $CBTAR_SRC
cp target/ContentBroker-SNAPSHOT.jar $CBTAR_SRC/ContentBroker.jar
if [ $? -ne 0 ]
then
	echo target has to be build first by mvn package. remember that you cannot build anything other than dev on development machines.
	echo any other target environment will need the CT tests to pass
	exit
fi


mkdir $CBTAR_SRC/conf
cp -r src/main/fido $CBTAR_SRC
cp -r src/main/jhove $CBTAR_SRC
cp src/main/scripts/jhove $CBTAR_SRC/jhove
cp src/main/conf/jhove.conf $CBTAR_SRC/jhove/conf
cp -r src/main/xslt $CBTAR_SRC/conf
cp src/main/resources/premis.xsd $CBTAR_SRC/conf
cp src/main/resources/xlink.xsd $CBTAR_SRC/conf
cp src/main/resources/frame.jsonld $CBTAR_SRC/conf
cp src/main/scripts/ffmpeg.sh $CBTAR_SRC
cp src/main/scripts/ContentBroker_stop.sh $CBTAR_SRC
cp src/main/scripts/ContentBroker_start.sh $CBTAR_SRC
cp src/main/scripts/cbTalk.sh $CBTAR_SRC
cp src/main/scripts/fido.sh $CBTAR_SRC
cp src/main/scripts/PDFA_def.ps $CBTAR_SRC/conf
cp src/main/resources/healthCheck.avi $CBTAR_SRC/conf
cp src/main/resources/healthCheck.tif $CBTAR_SRC/conf
cp src/main/resources/frame.jsonld $CBTAR_SRC/conf
mkdir $CBTAR_SRC/activemq-data
mkdir $CBTAR_SRC/log
touch $CBTAR_SRC/log/contentbroker.log
echo -e "ContentBroker Version $VERSION\nWritten by\n Daniel M. de Oliveira\n Jens Peters\n Sebastian Cuy\n Thomas Kleinke" > $CBTAR_SRC/README.txt

cp src/main/conf/beans.xml.node $INSTALLER/
cp src/main/conf/beans.xml.node.test $INSTALLER/
cp src/main/conf/beans.xml.pres $INSTALLER/
cp src/main/conf/beans.xml.full $INSTALLER/
cp src/main/conf/logback.xml.debug $INSTALLER/logback.xml

case "$1" in
dev)
	sed "s@CONTENTBROKER_ROOT@$2@" src/main/conf/config.properties.dev  > $INSTALLER/config.properties # TODO move to pre-integration-test.sh
	createStorageFolder	
	cp -f src/main/scripts/ffmpeg.sh.fake $INSTALLER/ffmpeg.sh
	cp src/main/conf/sqltool.rc ~/
	cp src/main/conf/hibernateCentralDB.cfg.xml.hsql $INSTALLER/hibernateCentralDB.cfg.xml
;;
vm3)
	cp src/main/conf/config.properties.vm3 $INSTALLER/config.properties
	cp src/main/conf/hibernateCentralDB.cfg.xml.postgres $INSTALLER/hibernateCentralDB.cfg.xml
	INSTALL_PATH=/data/danrw/ContentBroker
;;
esac

cd ../DAWeb
./build.sh prod
mv target/daweb3-$VERSION.war ../installation/daweb3.war
cd ../ContentBroker


cd $CBTAR_SRC
rm ../../../installation/ContentBroker.tar 2>/dev/null
tar cf ../../../installation/ContentBroker.tar *
cd ../../
