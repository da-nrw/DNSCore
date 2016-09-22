#!/bin/bash

echo "Prepare Index"
conf/rebuildTestIndex.sh

java -jar ./RegressionTestCBFull.jar $@ |  tee -a regressExec`date "+%Y%m%d-%H%M%S"`.txt

#echo "Cleanup Index"
conf/rebuildTestIndex.sh

