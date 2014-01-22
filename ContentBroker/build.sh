#!/bin/bash

# Author: Daniel M. de Oliveira

INSTALLER=../installation/
VERSION=`cat ../VERSION.txt`

#
# $1 beansType
# $2 type of db
# $3 debug level for logback.xml
#
function prepareCustomInstallation(){
	cp src/main/conf/beans.xml.$1 $INSTALLER/beans.xml
	if [ $2 != "none" ]
	then
		cp src/main/conf/hibernateCentralDB.cfg.xml.$2 $INSTALLER/hibernateCentralDB.cfg.xml
	fi
	cp src/main/conf/logback.xml.$3 $INSTALLER/logback.xml
	echo DNSCore installer for the $1 version of the DNSCore-$VERSION > $INSTALLER/VERSION.txt
}

function createStorageFolder(){
	mkdir $CBTAR_SRC/storage/
	mkdir $CBTAR_SRC/storage/grid
	mkdir -p $CBTAR_SRC/storage/dip/institution/TEST
	mkdir -p $CBTAR_SRC/storage/dip/public/TEST
	mkdir -p $CBTAR_SRC/storage/user/TEST/outgoing
	mkdir -p $CBTAR_SRC/storage/fork/TEST
	mkdir -p $CBTAR_SRC/storage/ingest/TEST
}

# clean up installation dir

cd ../installation
rm beans.xml 2>/dev/null
rm config.properties 2>/dev/null
rm hibernateCentralDB.cfg.xml 2>/dev/null
rm logback.xml 2>/dev/null
rm ffmpeg.sh 2>/dev/null
rm VERSION.txt 2>/dev/null
cd ../ContentBroker 


if [ "$1" == "dev" ]
then 
	echo setting Maven profile to dev
	PROFILE="-Pdev"
fi
mvn clean 
mvn package $PROFILE



CBTAR_SRC=target/deliverable
mkdir $CBTAR_SRC
cp target/ContentBroker-$VERSION.jar $CBTAR_SRC/ContentBroker.jar
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
mkdir $CBTAR_SRC/log
touch $CBTAR_SRC/log/contentbroker.log
echo -e "ContentBroker Version $VERSION\nWritten by\n Daniel M. de Oliveira\n Jens Peters\n Sebastian Cuy\n Thomas Kleinke" > $CBTAR_SRC/README.txt

case "$1" in
dev)
	sed "s@CONTENTBROKER_ROOT@$2@" src/main/conf/config.properties.dev  > $INSTALLER/config.properties
	createStorageFolder	
	cp -f src/main/scripts/ffmpeg.sh.fake $INSTALLER/ffmpeg.sh
	cp src/main/conf/sqltool.rc ~/
	prepareCustomInstallation node hsql debug
;;
vm3)
	cp src/main/conf/config.properties.vm3 $INSTALLER/config.properties
	prepareCustomInstallation node postgres debug
	INSTALL_PATH=/data/danrw/ContentBroker
;;
integration)
	cp src/main/conf/config.properties.vm3 $INSTALLER/config.properties
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



cd $CBTAR_SRC
rm ../../../installation/ContentBroker.tar 2>/dev/null
tar cf ../../../installation/ContentBroker.tar *
cd ../../
