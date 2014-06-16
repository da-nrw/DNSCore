#!/bin/bash
# author: Jens Peters 
FILENAME=daweb3.war

ENVIRONMENT=$1
echo "Cleaning"
grails clean
echo "Building WAR"
grails $ENVIRONMENT war target/$FILENAME
if [ $? != 0 ]; then
	echo "ERROR in building DAWEB"
	exit 1;
fi
cp target/$FILENAME ../ContentBroker/target/installation/$FILENAME
if [ -f ../ContentBroker/target/installation/$FILENAME ]; then
	echo "successfully built DAWEB"	
	exit 0;
	else exit 1;
fi

