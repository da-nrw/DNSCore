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
	imkdir /da-nrw/dip/institution/TEST   2>/dev/null
	imkdir /da-nrw/dip/public/TEST        2>/dev/null
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
    tail -f log/contentbroker.log | grep "ContentBroker is up and running"
	cd $SOURCE_PATH
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

	cd ../installation; ./install.sh $INSTALL_PATH; cd ../ContentBroker;
	
	./populatetestdb.sh create
	./populatetestdb.sh populate
	
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
	cd ../installation; ./install.sh $INSTALL_PATH; cd ../ContentBroker;

	createIrodsDirs
	prepareTestEnvironment $INSTALL_PATH
	restartContentBroker $INSTALL_PATH
;;
integration)
	cd ../installation; ./install.sh $INSTALL_PATH; cd ../ContentBroker;
;;
esac


