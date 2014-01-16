#!/bin/bash

# Author: Daniel M. de Oliveira

REPO=../installation/
VERSION=`cat ../VERSION.txt`

#
# $1 beansType
# $2 type of db
# $3 debug level for logback.xml
#
function prepareCustomInstallation(){
	cp configure.sh $REPO/
	cp src/main/conf/jhove.conf $REPO/jhove.conf
	cp src/main/scripts/jhove $REPO/jhove
	cp src/main/conf/beans.xml.$1 $REPO/beans.xml
	if [ $2 != "none" ]
	then
		cp src/main/conf/hibernateCentralDB.cfg.xml.$2 $REPO/hibernateCentralDB.cfg.xml
	fi
	cp src/main/conf/logback.xml.$3 $REPO/logback.xml
}

function createStorageFolder(){
	mkdir $TARGET/storage/
	mkdir $TARGET/storage/grid
	mkdir -p $TARGET/storage/dip/institution/TEST
	mkdir -p $TARGET/storage/dip/public/TEST
	mkdir -p $TARGET/storage/user/TEST/outgoing
	mkdir -p $TARGET/storage/fork/TEST
	mkdir -p $TARGET/storage/ingest/TEST
}

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
rm ../../../installation/ContentBroker.tar
tar cf ../../../installation/ContentBroker.tar *
cd ../../../installation
find . -type f ! -name "ContentBroker.tar" ! -name "install.sh" ! -name ".gitignore" -exec rm {} \;
cd ../ContentBroker

case "$1" in
dev)
	sed "s@CONTENTBROKER_ROOT@$2@" src/main/conf/config.properties.dev  > $REPO/config.properties.cb
	createStorageFolder	
	cp -f src/main/scripts/ffmpeg.sh.fake $REPO/ffmpeg.sh
	cp src/main/conf/sqltool.rc ~/
	prepareCustomInstallation node hsql debug
;;
vm3)
	cp src/main/conf/config.properties.vm3 $REPO/config.properties.cb
	prepareCustomInstallation node postgres debug
    INSTALL_PATH=/data/danrw/ContentBroker
;;
integration)
	cp src/main/conf/config.properties.vm3 $REPO/config.properties.cb
	prepareCustomInstallation integration postgres debug
;;
full)
	prepareCustomInstallation full none debug
;;
node)
	prepareCustomInstallation node none debug
;;
pres)
	prepareCustomInstallation pres none debug
;;
esac








