#!/bin/bash

mkdir conf
cp src/main/xml/hibernateCentralDB.cfg.xml.ci conf/hibernateCentralDB.cfg.xml
cp src/main/xml/beans.xml.full.dev conf/beans.xml
cp src/main/conf/config.properties.local conf/config.properties
cp src/main/xml/logback.xml.debug conf/logback.xml
cp src/main/xsd/xlink.xsd conf/xlink.xsd
cp src/main/xsd/premis.xsd conf/premis.xsd



