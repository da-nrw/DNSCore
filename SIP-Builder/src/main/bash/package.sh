#!/bin/bash

# author: Daniel M. de Oliveira
# 	Jens Peters

INSTALLATION=target/installation
URL="http://www.danrw.de/schemas/contract/v1/"
CONTRACT="danrw-contract-1.xsd"
VERSION="v`cat ../VERSION.txt` (build: `echo $BUILD_NUMBER` rev: `git rev-parse HEAD` created on `date`)"

mkdir $INSTALLATION
mkdir $INSTALLATION/conf
mkdir $INSTALLATION/data
mkdir $INSTALLATION/documentation
mkdir -p $INSTALLATION/conf/schemas/contract/v1
cp -pr src/manual/* $INSTALLATION/documentation
cp target/SipBuilder-1.0-SNAPSHOT-jar-with-dependencies.jar $INSTALLATION/SipBuilder.jar
cp src/main/bash/SipBuilder-Unix.sh $INSTALLATION/
cp src/main/binary/SipBuilder-Windows.exe $INSTALLATION/
cp src/main/xml/standardRights.xml $INSTALLATION/conf
wget $URL$CONTRACT -O $INSTALLATION/conf/schemas/contract/v1/$CONTRACT
rc=$?; if [[ $rc != 0 ]]; then 
	echo "CONTRACT at   NOT AVAILABLE!"	
	exit $rc; 
fi
cp LICENSE $INSTALLATION

echo -e "SipBuilder Version $VERSION\ Geschrieben von \n Thomas Kleinke\n Martin Fischer\n Polina Gubaidullina. Hinweise zur Bedienung finden Sie unter documentation/manual_SIPBuilder.md . Hinweise zur technischen Funktionsweise der Gesamtsoftware DNS können Sie unter https://github.com/da-nrw bekommen. " > $INSTALLATION/README.txt


