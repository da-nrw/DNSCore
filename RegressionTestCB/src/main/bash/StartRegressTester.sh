#!/bin/bash


LASTLOG=regressLog`date "+%Y%m%d-%H%M%S"`.txt
java -jar ./RegressionTestCBFull.jar $@ |  tee -a $LASTLOG

more $LASTLOG | grep '>>>TestFailure' | cut -d ':' -f 2 | sort > LastFailureList.txt

echo "--------------------------------------------------"
echo " "

#grep -vf ExpectedFuilureList.txt LastFailureList.txt || echo 'Last Regression-Test OK'

newErrors=`grep -vf ExpectedFuilureList.txt LastFailureList.txt`

if [ "$newErrors" != "" ] ; then 
	echo "Unexpected Fails: "; 
	echo "$newErrors" ; 
	echo " "; 
	echo 'Last Regression-Test NOT OK (unexpected failures)'; 
	exit 1;
else 
	echo 'Last Regression-Test OK (only expected failures)'; 
	exit 0;
fi;