#!/bin/bash
SRCDIR=/ci/DNSCore/ContentBroker/src/
INSTALLER=target/installation

rm $INSTALLER
mkdir -p $INSTALLER

mkdir $INSTALLER/conf

#cp src/main/bash/install.sh $INSTALLER/conf
cp $SRCDIR/main/xml/beans.xml.acceptance-test.ci $INSTALLER/conf/beans.xml
cp $SRCDIR/main/xml/hibernateCentralDB.cfg.xml.ci $INSTALLER/conf/hibernateCentralDB.cfg.xml
cp $SRCDIR/main/conf/config.properties.ci $INSTALLER/conf/config.properties
cp $SRCDIR/main/xml/logback.xml $INSTALLER/conf/logback.xml
cp target/RegressionTestCBFull.jar $INSTALLER/

echo "java -jar ./RegressionTestCBFull.jar \$@ |  tee -a regressExec\`date "+%Y%m%d%H%M%S"\`.txt" >> ./$INSTALLER/StartRegressTester.sh
chmod +x  ./$INSTALLER/StartRegressTester.sh


