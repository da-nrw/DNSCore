#!/bin/bash

# author: Daniel M. de Oliveira

mvn clean && mvn pre-integration-test -Pdev -DappHome=$1

mvn failsafe:integration-test -Dit.test=ATUseCaseIngestDelta
mvn failsafe:verify 
if [ "$?" = "1" ]
then
	there were test errors. will not push upstream
	exit 1
fi
git push origin master