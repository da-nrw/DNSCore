#!/bin/bash
SRCDIR_CB=/ci/DNSCore/ContentBroker/src/
SRCDIR_RT=/ci/DNSCore/RegressionTestCB/src/
INSTALLER=target/installation

rm $INSTALLER -rf
mkdir -p $INSTALLER

mkdir $INSTALLER/conf

#cp src/main/bash/install.sh $INSTALLER/conf
cp $SRCDIR_CB/main/xml/beans.xml.acceptance-test.ci $INSTALLER/conf/beans.xml
cp $SRCDIR_CB/main/xml/hibernateCentralDB.cfg.xml.ci $INSTALLER/conf/hibernateCentralDB.cfg.xml
#cp $SRCDIR_CB/main/conf/config.properties.ci $INSTALLER/conf/config.properties
cp $SRCDIR_CB/main/xml/logback.xml $INSTALLER/conf/logback.xml
cp $SRCDIR_CB/main/conf/es_mapping.json $INSTALLER/conf/
cp $SRCDIR_CB/main/conf/es_settings.json $INSTALLER/conf/

cp target/RegressionTestCBFull.jar $INSTALLER/


cp $SRCDIR_RT/main/bash/StartRegressTester.sh $INSTALLER/StartRegressTester.sh
cp $SRCDIR_RT/main/bash/rebuildTestIndex.sh $INSTALLER/conf/rebuildTestIndex.sh
cp $SRCDIR_RT/main/conf/config.properties.ci $INSTALLER/conf/config.properties	
cp $SRCDIR_RT/main/conf/ExpectedFailureList.txt $INSTALLER/ExpectedFailureList.txt

#echo "java -jar ./RegressionTestCBFull.jar \$@ |  tee -a regressExec\`date "+%Y%m%d%H%M%S"\`.txt" >> ./$INSTALLER/StartRegressTester.sh
#chmod +x  ./$INSTALLER/StartRegressTester.sh


