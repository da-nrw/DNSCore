#!/bin/bash

# author: Daniel M. de Oliveira
# author: Jens Peters

######################
#### PACK BINARY #####
###################### 

REPO=../installation/
VERSION=`cat ../VERSION.txt`

#################
#### PARAMS #####
################# 

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


#############################
#### CREATE DELIVERABLE #####
############################# 

echo Setting up and collecting environment specific configuration.

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

function createStorageFolder(){
	mkdir $INSTALL_PATH/storage/
	mkdir $INSTALL_PATH/storage/grid
	mkdir -p $INSTALL_PATH/storage/dip/institution/TEST
	mkdir -p $INSTALL_PATH/storage/dip/public/TEST
	mkdir -p $INSTALL_PATH/storage/user/TEST/outgoing
	mkdir -p $INSTALL_PATH/storage/fork/TEST
	mkdir -p $INSTALL_PATH/storage/ingest/TEST
}

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



case "$1" in
dev)
	sed "s@CONTENTBROKER_ROOT@$INSTALL_PATH@" src/main/conf/config.properties.dev  > $REPO/config.properties.cb
	createStorageFolder	
	cp -f src/main/scripts/ffmpeg.sh.fake $REPO/ffmpeg.sh
	cp src/main/conf/sqltool.rc ~/
	prepareCustomInstallation node hsql debug
	cd ..; ./install.sh $VERSION $INSTALL_PATH; cd ContentBroker;
	
	./populatetestdb.sh create
	./populatetestdb.sh populate
	
	deliver dev $VERSION $INSTALL_PATH
	prepareTestEnvironment $INSTALL_PATH
	restartContentBroker $INSTALL_PATH
;;
vm3)
	cp src/main/conf/config.properties.vm3 $REPO/config.properties.cb
	prepareCustomInstallation node postgres debug
    INSTALL_PATH=/data/danrw/ContentBroker
	cd ..; ./install.sh $VERSION $INSTALL_PATH; cd ContentBroker;

	createIrodsDirs
	deliver vm3 $VERSION $INSTALL_PATH
	prepareTestEnvironment $INSTALL_PATH
	restartContentBroker $INSTALL_PATH
;;
integration)
	cp src/main/conf/config.properties.vm3 $REPO/config.properties.cb
	prepareCustomInstallation integration postgres debug
	cd..; ./install.sh $VERSION `pwd`; cd ContentBroker
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

exit




