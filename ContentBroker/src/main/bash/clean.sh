#!/bin/bash

# author: Daniel M. de Oliveira

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
