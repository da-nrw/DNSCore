#!/bin/bash

# author: Daniel M. de Oliveira
# author: Jens Peters

#################
#### PARAMS #####
################# 

REPO=target/installation/
CI_INSTALL_PATH=/ci/ContentBroker
DEV_INSTALL_PATH=target/ContentBroker
VERSION=`cat ../VERSION.txt`
SOURCE_PATH=`pwd`
LANG="de_DE.UTF-8"
export LANG

if [ "$1" = "dev" ]
then
	HOME=`pwd`
	INSTALL_PATH=${HOME}/${DEV_INSTALL_PATH}
	mkdir $INSTALL_PATH

else 
	INSTALL_PATH=${CI_INSTALL_PATH}
fi

#############################
######## FUNCTIONS ##########
############################# 

function createIrodsDirs(){
	imkdir /c-i/aip/TEST                2>/dev/null
	imkdir /c-i/pips/institution/TEST   2>/dev/null
	imkdir /c-i/pips/public/TEST        2>/dev/null
}

function startContentBroker(){
	cd $1
	
	if [ "$2" = "dev" ]
	then
		./ContentBroker_start.sh.template suppress_diagnostics
	else
		./ContentBroker_start.sh.template 	
	fi

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


## remove some stuff so that the installer can make the real files out of the template files again
function cleanUpExistingInstallation(){

	rm $1/conf/beans.xml > /dev/null
	rm $1/conf/config.properties > /dev/null
	rm $1/conf/hibernateCentralDB.cfg.xml > /dev/null
	rm $1/conf/logback.xml > /dev/null
	rm $1/ContentBroker_start.sh > /dev/null
	rm $1/ContentBroker_stop.sh > /dev/null
	rm $1/log/contentbroker.log
	# > $INSTALL_PATH/log/contentbroker.log
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

cleanUpExistingInstallation $INSTALL_PATH

install $INSTALL_PATH $BEANS
sed -i "s/INFO/DEBUG/g" $INSTALL_PATH/conf/logback.xml
cp $INSTALL_PATH/conf/config.properties conf/

startContentBroker $INSTALL_PATH $1


## in case fake jhove will get used
echo -e "<fakejhove>\n</fakejhove>" > /tmp/abc.txt




