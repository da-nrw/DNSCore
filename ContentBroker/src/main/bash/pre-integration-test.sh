#!/bin/bash

# author: Daniel M. de Oliveira
# author: Jens Peters

#################
#### PARAMS #####
################# 

REPO=target/installation/
VERSION=`cat ../VERSION.txt`
SOURCE_PATH=`pwd`
LANG="de_DE.UTF-8"
export LANG

if [ "$1" = "dev" ]
then
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
else 
	INSTALL_PATH=/ci/ContentBroker
fi

#############################
######## FUNCTIONS ##########
############################# 

function createIrodsDirs(){
	imkdir /c-i/aip/TEST                2>/dev/null
	imkdir /c-i/pips/institution/TEST   2>/dev/null
	imkdir /c-i/pips/public/TEST        2>/dev/null
}

# $1 = INSTALL_PATH
#function stopContentBroker(){
#	cd $1
	#echo -e "\nTrying to start ContentBroker "
	#kill -9 `ps -aef | grep ContentBroker.jar | grep -v grep | awk '{print $2}'` 2>/dev/null
	#rm -f /tmp/cb.running
#	./ContentBroker_stop.sh.template
#    cd $SOURCE_PATH
#}

# $1 = INSTALL_PATH
function startContentBroker(){
	cd $1
	
	./ContentBroker_start.sh.template
	sleep 15
	cd $SOURCE_PATH
}

# $1 = INSTALL_PATH
# $2 = mode
function install(){
	cd $REPO
	echo call ./install.sh $1 $2
	./install.sh $1 $2
	if [ $? = 1 ]
	then
		echo Error in install script
		exit
	fi
	cd $SOURCE_PATH
}

function launchXDB(){
	DATABASE_PROC_ID=`ps -aef | grep hsqldb.jar | grep -v grep | awk '{print $2}'`
	if [ "$DATABASE_PROC_ID" != "" ]
	then
	        echo Killing hsql database process $DATABASE_PROC_ID.
	        kill -9 $DATABASE_PROC_ID
	fi
	
	sleep 2
	
	echo Recreating da-nrw schema in hsql database.
	rm -r mydb.tmp 2> /dev/null
	rm mydb.*      2> /dev/null
	
	cd $HIER
	java -cp ../3rdParty/hsqldb/lib/hsqldb.jar org.hsqldb.server.Server --database.0 file:mydb --dbname.0 xdb &
	
	sleep 2
}




#############################
######## MAIN ###############
############################# 

src/main/bash/ContentBroker_stop.sh

case "$1" in
dev)
	launchXDB
	BEANS=full.dev
;;
ci)
	createIrodsDirs
	BEANS=full
;;
esac

mkdir conf
cp src/main/xml/hibernateCentralDB.cfg.xml.$1 conf/hibernateCentralDB.cfg.xml
cp src/main/xml/beans.xml.acceptance-test.$1 conf/beans.xml
cp src/main/conf/config.properties.$1 conf/config.properties

java -jar target/ContentBroker-SNAPSHOT.jar createSchema
src/main/bash/populatetestdb.sh populate $1

rm $INSTALL_PATH/conf/beans.xml
rm $INSTALL_PATH/conf/config.properties
rm $INSTALL_PATH/conf/hibernateCentralDB.cfg.xml
rm $INSTALL_PATH/actionCommunicatorService.recovery

install $INSTALL_PATH $BEANS
# TODO 1. really needed on a ci machine? 2. duplication with installer?
cp src/main/bash/ffmpeg.sh.fake $INSTALL_PATH/ffmpeg.sh

cp $INSTALL_PATH/conf/config.properties conf/

startContentBroker $INSTALL_PATH

