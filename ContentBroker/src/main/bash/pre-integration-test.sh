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
		exit 1
	fi
	INSTALL_PATH=$2
	if [[ "${INSTALL_PATH:${#INSTALL_PATH}-1}" == "/" ]]; then
		INSTALL_PATH="${INSTALL_PATH%?}"
	fi	
	if [ ! -d "$INSTALL_PATH" ]; then
		echo Error: $INSTALL_PATH is not a directory.
	  	exit 1
	fi
	HOME=`pwd`
	if [ $INSTALL_PATH = $HOME ]; then 
		echo Error target environment $INSTALL_PATH and current src tree are identical!
		exit 1
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
	echo Recreating da-nrw schema in hsql database.
	
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


## remove some stuff so that the installer can make the real files out of the template files again
rm $INSTALL_PATH/conf/beans.xml > /dev/null
rm $INSTALL_PATH/conf/config.properties > /dev/null
rm $INSTALL_PATH/conf/hibernateCentralDB.cfg.xml > /dev/null
rm $INSTALL_PATH/conf/logback.xml > /dev/null
rm $INSTALL_PATH/ContentBroker_start.sh > /dev/null
rm $INSTALL_PATH/ContentBroker_stop.sh > /dev/null
rm $INSTALL_PATH/log/contentbroker.log
> $INSTALL_PATH/log/contentbroker.log

install $INSTALL_PATH $BEANS
sed -i "s/INFO/DEBUG/g" $INSTALL_PATH/conf/logback.xml
cp $INSTALL_PATH/conf/config.properties conf/

startContentBroker $INSTALL_PATH $1


## in case fake jhove will get used
echo -e "<fakejhove>\n</fakejhove>" > /tmp/abc.txt




