#!/bin/bash
# author: Jens Peters 

VERSION=`cat ../VERSION.txt`
ENVIRONMENT=$1
echo "Cleaning"
grails clean
echo "Building WAR"
grails $ENVIRONMENT war
if [ $? != 0 ]; then
	echo "ERROR in building DAWEB"
	exit 1;
fi
cp target/daweb3-$VERSION.war ../installation/daweb3.war

