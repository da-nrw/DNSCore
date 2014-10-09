#!/bin/bash

# author: Daniel M. de Oliveira

INSTALLATION=target/installation

VERSION="v`cat ../VERSION.txt` (build: `echo $BUILD_NUMBER` rev: `git rev-parse HEAD` created on `date`)"

mkdir $INSTALLATION
mkdir $INSTALLATION/conf
mkdir $INSTALLATION/data
cp target/SipBuilder-1.0-SNAPSHOT-jar-with-dependencies.jar $INSTALLATION/SipBuilder.jar
cp src/main/bash/SipBuilder-Unix.sh $INSTALLATION/
cp src/main/binary/SipBuilder-Windows.exe $INSTALLATION/
cp "src/main/binary/SIP-Builder Anleitung.pdf" $INSTALLATION
cp src/main/xml/standardRights.xml $INSTALLATION/conf

echo -e "SipBuilder Version $VERSION\nWritten by\n Thomas Kleinke\n Martin Fischer" > $INSTALLATION/README.txt


