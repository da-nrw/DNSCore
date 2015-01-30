#!/bin/bash

# author: Daniel M. de Oliveira
# author: Jens Peters

echo ###################### DNCore . ContentBroker Installer ########################## 

if [ $# -lt 1 ] 
then
	echo "INSTALL - script needs at least one param for the installation target dir"
	exit 1
fi

INSTALLATION_TARGET=$1
if [[ "${INSTALLATION_TARGET:${#INSTALLATION_TARGET}-1}" == "/" ]]; then
	INSTALLATION_TARGET="${INSTALLATION_TARGET%?}"
fi	
if [ ! -d "$INSTALLATION_TARGET" ]; then
	echo "INSTALL - ERROR: $INSTALLATION_TARGET is not a directory."
		exit
fi
HOME=`pwd`
if [ $HOME = $INSTALLATION_TARGET ]; then 
	echo "INSTALL - ERROR: target environment $INSTALLATION_TARGET and current src tree are identical!"
	exit
fi

echo "INSTALL - INSTALLING TO: $INSTALLATION_TARGET"


if [ $# -eq 2 ]
then
	BEANS_TYPE=$2
else
	echo "INSTALL: Select your desired feature set from the available options: (n)ode. (p)res. (f)ull."
	read ANSWER
	case "$ANSWER" in
	n)
		BEANS_TYPE="node"  
	;;
	f)
		BEANS_TYPE="full"
	;;
	p)
		BEANS_TYPE="pres"
	;;
	*)
		echo invalid option
		exit
	;;
	esac
fi


TAR=tar
OS=`uname -s`
case "$OS" in
SunOS)
	TAR=gtar
	;;
esac

##############################################################

echo "INSTALL - Installing to $INSTALLATION_TARGET"
$TAR xf ContentBroker.tar -C $INSTALLATION_TARGET
if [ -e config.properties ]
then
	if [ -e $INSTALLATION_TARGET/conf/config.properties ]
	then
		echo "INSTALL - Installer found an existing config.properties at the target."
		echo "INSTALL - please either delete the config.properties from the installation dir or from the target, depending on which one you want to keep."
		exit 1
	fi
	cp config.properties $INSTALLATION_TARGET/conf/
fi
if  [ -e hibernateCentralDB.cfg.xml ]
then
	if [ -e $INSTALLATION_TARGET/conf/hibernateCentralDB.cfg.xml ]
	then
		echo "INSTALL - Installer found an existing hibernateCentralDB.cfg.xml at the target."
		echo "INSTALL - Please either delete the hibernateCentralDB.cfg.xml from the installation dir or from the target, depending on which one you want to keep."
		exit 1
	fi
	cp hibernateCentralDB.cfg.xml $INSTALLATION_TARGET/conf/
fi

if  [ -e $INSTALLATION_TARGET/conf/beans.xml ]
then 
	cp -f $INSTALLATION_TARGET/conf/beans.xml $INSTALLATION_TARGET/conf/beans.xml.BAK	
	echo "INSTALL - Found existing beans.xml, saved to beans.xml.BAK. Please use always newly installed beans.xml file - but check for settings needed for your system (e.g. freeDiskSpacePercent)."
fi
if [ ! -z $BEANS_TYPE ]
then 
echo "INSTALL - Copying beans.xml.$BEANS_TYPE to $INSTALLATION_TARGET/conf/beans.xml"
cp -f beans.xml.$BEANS_TYPE $INSTALLATION_TARGET/conf/beans.xml
fi


##### deliver template files and copy them to the regular names if they do not exist #######
##### logback.xml #######
##### ContentBroker_stop.sh #######
##### ContentBroker_start.sh #######
echo "INSTALL -Copying template file logback.xml.template to $INSTALLATION_TARGET/conf/logback.xml.template"
cp -f logback.xml.template $INSTALLATION_TARGET/conf # overwrite existing template file.
if  [ ! -e $INSTALLATION_TARGET/conf/logback.xml ]
then 
	echo "INSTALL - No logback.xml present in $INSTALLATION_TARGET." 
	echo "INSTALL - Installer will use logback.xml.template to create a logback.xml file."
	cp -f $INSTALLATION_TARGET/conf/logback.xml.template $INSTALLATION_TARGET/conf/logback.xml
fi
echo "INSTALL - Copying template file ContentBroker_stop.sh.template to $INSTALLATION_TARGET/conf/ContentBroker_stop.sh.template"
cp -f ContentBroker_stop.sh.template $INSTALLATION_TARGET/ # overwrite existing template file.
if  [ ! -e $INSTALLATION_TARGET/ContentBroker_stop.sh ]
then 
	echo "INSTALL - No ContentBroker_stop.sh present in $INSTALLATION_TARGET."
	echo "INSTALL - Installer will use ContentBroker_stop.sh.template to create a ContentBroker_stop.sh file."
	cp -f $INSTALLATION_TARGET/ContentBroker_stop.sh.template $INSTALLATION_TARGET/ContentBroker_stop.sh
fi
echo "INSTALL - Copying template file ContentBroker_start.sh.template to $INSTALLATION_TARGET/conf/ContentBroker_start.sh.template"
cp -f ContentBroker_start.sh.template $INSTALLATION_TARGET/ # overwrite existing template file.
if  [ ! -e $INSTALLATION_TARGET/ContentBroker_start.sh ]
then
	echo "INSTALL - No ContentBroker_start.sh present in $INSTALLATION_TARGET."
	echo "INSTALL - Installer will use ContentBroker_stop.sh.template to create a ContentBroker_start.sh file." 
	cp -f $INSTALLATION_TARGET/ContentBroker_start.sh.template $INSTALLATION_TARGET/ContentBroker_start.sh
fi
#############################


cd $INSTALLATION_TARGET
./configure.sh
if [ $? -eq 0 ]
then
  rm configure.sh
fi
exit 0
