#!/bin/bash

# author: Daniel M. de Oliveira
# author: Jens Peters

#################
#### PARAMS #####
################# 

REPO=../installation/
VERSION=`cat ../VERSION.txt`

LANG="de_DE.UTF-8"
export LANG

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
	imkdir /c-i/TEST                    2>/dev/null
	imkdir /c-i/aip/TEST                2>/dev/null
	imkdir /c-i/pips/institution/TEST   2>/dev/null
	imkdir /c-i/pips/public/TEST        2>/dev/null
}


# $1 = INSTALL_PATH
function restartContentBroker(){
	SOURCE_PATH=`pwd`
	cd $1
	echo -e "\nTrying to start ContentBroker "
	kill -9 `ps -aef | grep ContentBroker.jar | grep -v grep | awk '{print $2}'` 2>/dev/null
	rm -f /tmp/cb.running
	./ContentBroker_start.sh
	sleep 15
   cd $SOURCE_PATH
}

# $1 = INSTALL_PATH
function install(){
	cd ../installation 
	echo call ./install.sh $1 full
	./install.sh $1 full
	if [ $? = 1 ]
	then
		echo Error in install script
		exit
	fi
	cd ../ContentBroker;
}


case "$1" in
dev)
	if [ $# -ne 2 ] 
	then
		echo you chose a development environment as target environment. call
		echo "./install.sh dev <contentBrokerInstallationRootPath>"
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
	
	src/main/bash/populatetestdb.sh create $1
	src/main/bash/populatetestdb.sh populate $1
	
	cp src/main/xml/beans.xml.$1 conf/beans.xml
;;
ci)
	INSTALL_PATH=/ci/ContentBroker

	# TODO remove
	export PGPASSWORD="kulle_oezil06"
	src/main/bash/populatetestdb.sh clean $1
	src/main/bash/populatetestdb.sh populate $1

	createIrodsDirs
	cp src/main/xml/beans.xml.$1 conf/beans.xml
	
	# TODO really needed on a ci machine?
	cp src/main/bash/ffmpeg.sh.fake $INSTALL_PATH/ffmpeg.sh
	
;;
esac

install $INSTALL_PATH
rm $INSTALL_PATH/conf/config.properties
rm $INSTALL_PATH/conf/hibernateCentralDB.cfg.xml
rm $INSTALL_PATH/actionCommunicatorService.recovery
prepareTestEnvironment $INSTALL_PATH
restartContentBroker $INSTALL_PATH

