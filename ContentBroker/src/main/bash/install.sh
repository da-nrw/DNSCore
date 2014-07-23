#!/bin/bash

# author: Daniel M. de Oliveira
# author: Jens Peters

if [ $# -lt 1 ] 
then
	echo script needs at least one param for the installation target dir
	exit 1
fi

INSTALLATION_TARGET=$1
if [[ "${INSTALLATION_TARGET:${#INSTALLATION_TARGET}-1}" == "/" ]]; then
	INSTALLATION_TARGET="${INSTALLATION_TARGET%?}"
fi	
if [ ! -d "$INSTALLATION_TARGET" ]; then
	echo Error: $INSTALLATION_TARGET is not a directory.
		exit
fi
HOME=`pwd`
if [ $HOME = $INSTALLATION_TARGET ]; then 
	echo Error target environment $INSTALLATION_TARGET and current src tree are identical!
	exit
fi

echo INSTALLING TO $INSTALLATION_TARGET



if [ $# -eq 2 ]
then
	BEANS_TYPE=$2
else
	echo "Select your desired feature set from the available options: (n)ode. (p)res. (f)ull. preserve (e)xisting beans."
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
	e)
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

echo Installing to $INSTALLATION_TARGET
$TAR xf ContentBroker.tar -C $INSTALLATION_TARGET
if [ -e config.properties ]
then
	if [ -e $INSTALLATION_TARGET/conf/config.properties ]
	then
		echo Installer found an existing config.properties at the target.
		echo Please either delete the config.properties from the installation dir or from the target, depending on which one you want to keep.
		exit 1
	fi
	cp config.properties $INSTALLATION_TARGET/conf/
fi
if  [ -e hibernateCentralDB.cfg.xml ]
then
	if [ -e $INSTALLATION_TARGET/conf/hibernateCentralDB.cfg.xml ]
	then
		echo Installer found an existing hibernateCentralDB.cfg.xml at the target.
		echo Please either delete the hibernateCentralDB.cfg.xml from the installation dir or from the target, depending on which one you want to keep.
		exit 1
	fi
	cp hibernateCentralDB.cfg.xml $INSTALLATION_TARGET/conf/
fi
if [ ! -z $BEANS_TYPE ]
then 
	echo Copying beans.xml.$BEANS_TYPE to $INSTALLATION_TARGET/conf/beans.xml
	cp -f beans.xml.$BEANS_TYPE $INSTALLATION_TARGET/conf/beans.xml
fi
cp -f logback.xml $INSTALLATION_TARGET/conf
if [ -e ffmpeg.sh ] 
then
	echo copy new ffmpeg
	cp -f ffmpeg.sh $INSTALLATION_TARGET/
fi
if [ -e handBrake.sh ]
then
	echo copy new handBrake
	cp -f handBrake.sh $INSTALLATION_TARGET/
fi
cd $INSTALLATION_TARGET
./configure.sh
exit 0
