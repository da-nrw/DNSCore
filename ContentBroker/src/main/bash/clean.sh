#!/bin/bash

# author: Daniel M. de Oliveira
# author: Jens Peters
echo clean iRODS Stuff

irm -r /c-i/aip/TEST                2>/dev/null
irm -r /c-i/pips/institution/TEST   2>/dev/null
irm -r /c-i/pips/public/TEST        2>/dev/null
imkdir /c-i/aip/TEST                2>/dev/null
imkdir /c-i/pips/institution/TEST   2>/dev/null
imkdir /c-i/pips/public/TEST        2>/dev/null

echo clean up installer
cd target/installation
rm ContentBroker.tar 2>/dev/null
rm beans.xml* 2>/dev/null
rm daweb3.war 2>/dev/null
rm config.properties 2>/dev/null
rm hibernateCentralDB.cfg.xml 2>/dev/null
rm logback.xml 2>/dev/null
rm ffmpeg.sh 2>/dev/null
rm VERSION.txt 2>/dev/null
cd ../..
