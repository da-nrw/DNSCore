#!/bin/bash

# author: Daniel M. de Oliveira
# author: Jens Peters
VERSION=0.6.3-rc2
LANG="de_DE.UTF-8"
export LANG

if [ $# -eq 0 ] 
then 
	echo you have to specify your target environment
	exit
fi
if [ "$1" = "dev" ] 
then
	if [ $# -ne 2 ] 
	then
		echo you chose a development environment as target environment. call
		echo "./deliver.sh dev <machineName> <contentBrokerInstallationRootPath>"
		exit
	fi
	INSTALL_PATH=$2
	if [[ "${INSTALL_PATH:${#INSTALL_PATH}-1}" == "/" ]]; then
                INSTALL_PATH="${INSTALL_PATH%?}"
        fi	
	if [ ! -d "$INSTALL_PATH" ]; then
  		echo Error: $INSTALL_PATH is not a directory.
  		exit
	fi
	HOME=`pwd`
	if [ $INSTALL_PATH = $HOME ]; then 
		echo Error target environment $INSTALL_PATH and current src tree are identical!
		exit
	fi
fi

#################
#### PART I #####
################# 

###################
echo collecting

TARGET=target/deliverable
rm -rf $TARGET
mkdir $TARGET
echo -e "ContentBroker Version $VERSION\nWritten by\n Daniel M. de Oliveira\n Jens Peters\n Sebastian Cuy\n Thomas Kleinke" > $TARGET/README.txt
mkdir $TARGET/conf
cp -r src/main/fido $TARGET
cp -r src/main/jhove $TARGET
cp -r src/main/xslt $TARGET/conf
cp target/ContentBroker-$VERSION.jar $TARGET/ContentBroker.jar
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


################## 
echo Setting up and collecting environment specific configuration.

function createStorageFolder(){
	mkdir $TARGET/storage/
	mkdir $TARGET/storage/grid
	mkdir -p $TARGET/storage/dip/institution/TEST
	mkdir -p $TARGET/storage/dip/public/TEST
	mkdir -p $TARGET/storage/user/TEST/outgoing
	mkdir -p $TARGET/storage/fork/TEST
	mkdir -p $TARGET/storage/ingest/TEST
}

#
# $1 beansType
# $2 type of db
# $3 debug level for logback.xml
#
function prepareCustomInstallation(){
	cp configure.sh $TARGET/
	cp src/main/conf/jhove.conf $TARGET/jhove/conf/jhove.conf
	cp src/main/scripts/jhove $TARGET/jhove/jhove
	cp src/main/conf/beans.xml.$1 $TARGET/conf/beans.xml
	if [ $2 != "none" ]
	then
		cp src/main/conf/hibernateCentralDB.cfg.xml.$2 $TARGET/conf/hibernateCentralDB.cfg.xml
	fi
	cp src/main/conf/logback.xml.$3 $TARGET/conf/logback.xml
}



case "$1" in
dev)
	sed "s@CONTENTBROKER_ROOT@$INSTALL_PATH@" src/main/conf/config.properties.dev  > $TARGET/conf/config.properties
	createStorageFolder	
	cp -f src/main/scripts/ffmpeg.sh.fake $TARGET/ffmpeg.sh
	cp src/main/conf/sqltool.rc ~/
	prepareCustomInstallation node hsql debug
;;
integration)
	cp src/main/conf/config.properties.vm3 $TARGET/conf/config.properties
	prepareCustomInstallation integration postgres debug
;;
vm2)
	cp src/main/conf/config.properties.vm2 $TARGET/conf/config.properties
	prepareCustomInstallation pres postgres debug
;;
vm3)
	cp src/main/conf/config.properties.vm3 $TARGET/conf/config.properties
	prepareCustomInstallation node postgres debug
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


# package
rm deliverable*tar  2> /dev/null
cd target
cd deliverable
rm deliverable*tar  2> /dev/null
tar cf deliverable.tar *
cd ..
cd ..



#################
#### PART II ####
################# 
echo Delivering and starting machines.

mv target/deliverable/deliverable.tar ./deliverable.$1.$VERSION.tar

# $1 = INSTALL_PATH
function restartContentBroker(){
	SOURCE_PATH=`pwd`
	cd $1
	echo -e "\nWait for message \"INFO  de.uzk.hki.da.core.ContentBroker - ContentBroker is up and running\". Then hit ctrl-c."
	echo -e "If you don't see this message after a couple of seconds, try debugging ContentBroker by starting it manually via java -jar ContentBroker.jar\n"
	kill -9 `ps -aef | grep ContentBroker.jar | grep -v grep | awk '{print $2}'` 2>/dev/null
	rm -f /tmp/cb.running
	./ContentBroker_start.sh
	cd $SOURCE_PATH
}

# $1 = TYPE
# $2 = VERSION
# $3 = INSTALL_PATH
function deliver(){
	TYPE=$1
	VERSION=$2
	INSTALL_PATH=$3
	SOURCE_PATH=`pwd`
	echo -e "\nUnpacking and starting ContentBroker Version $VERSION at \"$INSTALL_PATH\"."
	cp deliverable.$TYPE.$VERSION.tar $INSTALL_PATH
	cd $INSTALL_PATH
	tar xf deliverable.$TYPE.$VERSION.tar
	./configure.sh
	rm deliverable.$TYPE.$VERSION.tar
	cd $SOURCE_PATH
}

# $1 = INSTALL_PATH
function prepareTestEnvironment(){
	echo Prepare test environment for acceptance testing.
	cp $1/conf/config.properties conf/
	cp $1/conf/hibernateCentralDB.cfg.xml conf/
}
function createIrodsDirs(){
	imkdir /da-nrw/fork/TEST               > /dev/null
	imkdir /da-nrw/aip/TEST                > /dev/null
	imkdir /da-nrw/dip/institution/TEST    > /dev/null
	imkdir /da-nrw/dip/public/TEST         > /dev/null
}


case "$1" in 
dev)
	./populatetestdb.sh create
	./populatetestdb.sh populate
	
	deliver dev $VERSION $INSTALL_PATH
	prepareTestEnvironment $INSTALL_PATH
	restartContentBroker $INSTALL_PATH
;;
integration)
	createIrodsDirs
	tar xf deliverable.$1.$VERSION.tar
	./configure.sh
;;
vm2)
	INSTALL_PATH=/data/danrw/ContentBroker
	scp deliverable.$1.$VERSION.tar vm2:$INSTALL_PATH 
	ssh vm2 "cd $INSTALL_PATH; tar xf deliverable.$1.$VERSION.tar; ./configure.sh; rm deliverable.$1.$VERSION.tar"
;;
vm3)
	createIrodsDirs
    INSTALL_PATH=/data/danrw/ContentBroker

	deliver vm3 $VERSION $INSTALL_PATH
	prepareTestEnvironment $INSTALL_PATH
	restartContentBroker $INSTALL_PATH
;;
esac





