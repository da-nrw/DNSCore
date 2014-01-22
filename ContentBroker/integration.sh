#!/bin/bash

# TEST 

VERSION="0.6.3"
echo component and integration testing for $VERSION 

# trap ctrl-c
trap ctrl_c INT

function ctrl_c(){
	echo "** TRAPPED CTRL-C"
	exit 1
}

./deliver.sh integration

kill -9 `ps -aef | grep ContentBroker.jar | grep -v grep | awk '{print $2}'`
rm -f /tmp/cb.running

export PGPASSWORD="kulle_oezil06"
psql contentbroker -U irods -c "delete from queue;"
psql contentbroker -U irods -c "delete from objects;"

mvn -Dtest=CTFidoTest test
if [ "$?" != "0" ] 
then
	echo CTFidoTest. There were errors in component tests.
fi
mvn -Dtest=CTIrodsGridFacadeTest test
if [ "$?" != "0" ] 
then
	echo CTIrodsGridFacadeTest. There were errors in component tests.
	exit 0
fi
mvn -Dtest=CTIrodsFederatedGridFacadeTest test
if [ "$?" != "0" ] 
then
	echo CTIrodsFederatedGridFacadeTest. There were errors in component tests.
	exit 0
fi	

mvn -Dtest=ITUseCaseAudit test
if [ "$?" != "0" ] 
then
	echo there were errors in integration tests for use case audit
	exit 0
fi	
mvn -Dtest=ITUseCaseIngest test
if [ "$?" != "0" ] 
then
	echo there were errors in integration tests for use case ingest
	exit 0
fi

echo "Generating Javadoc"
///data/danrw/tools/groovy-1.8.7/bin/groovydoc -classpath /data/danrw/tools/groovy-1.8.7/lib/ -sourcepath /data/danrw/development/ContentBrokerTrunk/src/main/java:/data/danrw/development/ContentBrokerTrunk/src/main/resources:/data/danrw/development/ContentBrokerTrunk/src/test/java:/data/danrw/development/ContentBrokerTrunk/src/test/resources -d /data/danrw/www/javadoc -doctitle DA-NRW\ ContentBroker -windowtitle DA-NRW\ ContentBroker de.uzk.hki.da.archivers de.uzk.hki.da.cb de.uzk.hki.da.convert de.uzk.hki.da.core de.uzk.hki.da.db de.uzk.hki.da.grid de.uzk.hki.da.integrity de.uzk.hki.da.it de.uzk.hki.da.metadata de.uzk.hki.da.model de.uzk.hki.da.model.contract de.uzk.hki.da.service de.uzk.hki.da.utils  *.java
mvn javadoc:javadoc
echo "finished generating javadoc"


