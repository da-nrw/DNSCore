#!/bin/bash

# author: Jens Peters
# author: Daniel M. de Oliveira

TAR=tar

OS=`uname -s`
case "$OS" in
SunOS)
	TAR=gtar
	;;
esac


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


echo Installing to $INSTALLATION_TARGET
$TAR xf ContentBroker.tar -C $INSTALLATION_TARGET
if [ -e config.properties ]
then
	if [ -e $INSTALLATION_TARGET/conf/config.properties ]
	then
		echo Installer found an existing config.properties at the target.
		echo Please either delete the config.properties from the installation dir or from the target, depending on which one you want to keep.
		exit
	fi
	cp config.properties $INSTALLATION_TARGET/conf/
fi
if  [ -e hibernateCentralDB.cfg.xml ]
then
	if [ -e $INSTALLATION_TARGET/conf/hibernateCentralDB.cfg.xml ]
	then
		echo Installer found an existing hibernateCentralDB.cfg.xml at the target.
		echo Please either delete the hibernateCentralDB.cfg.xml from the installation dir or from the target, depending on which one you want to keep.
		exit
	fi
	cp hibernateCentralDB.cfg.xml $INSTALLATION_TARGET/conf/
fi
cp beans.xml $INSTALLATION_TARGET/conf
cp logback.xml $INSTALLATION_TARGET/conf
cp configure.sh $INSTALLATION_TARGET/
if [ -e ffmpeg.sh ] 
then
	cp -f ffmpeg.sh $INSTALLATION_TARGET/
fi
cd $INSTALLATION_TARGET
./configure.sh
