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
	imkdir /da-nrw/fork/TEST              2>/dev/null
	imkdir /da-nrw/aip/TEST               2>/dev/null
	imkdir /da-nrw/pip/institution/TEST   2>/dev/null
	imkdir /da-nrw/pip/public/TEST        2>/dev/null
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

	rm $INSTALL_PATH/conf/config.properties
	rm $INSTALL_PATH/conf/hibernateCentralDB.cfg.xml
	rm $INSTALL_PATH/actionCommunicatorService.recovery
	install $INSTALL_PATH
	
	src/main/bash/populatetestdb.sh create
	src/main/bash/populatetestdb.sh populate
	
	cp src/main/xml/beans.xml.dev conf/beans.xml
	prepareTestEnvironment $INSTALL_PATH
	restartContentBroker $INSTALL_PATH
;;
vm3)
	export PGPASSWORD="kulle_oezil06"
	psql contentbroker -c "delete from queue;"
	psql contentbroker -c "delete from objects;"
	psql contentbroker -c "delete from packages;"
	psql contentbroker -c "delete from objects_packages;"

	INSTALL_PATH=/data/danrw/ContentBroker

	rm $INSTALL_PATH/conf/config.properties
	rm $INSTALL_PATH/conf/hibernateCentralDB.cfg.xml
	install $INSTALL_PATH

	createIrodsDirs
	cp src/main/xml/beans.xml.ci conf/beans.xml
	prepareTestEnvironment $INSTALL_PATH
	restartContentBroker $INSTALL_PATH
;;
esac


