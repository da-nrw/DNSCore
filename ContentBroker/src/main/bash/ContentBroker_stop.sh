#!/bin/bash

MAX_TIME=30 
STEP=5

PID=`ps -aef | grep ContentBroker.jar | grep -v grep | awk '{print $2}'`

if [ "$PID" == "" ]; then
	echo "Application is not running"
else
	kill -15 $PID  # for graceful shutdown
	sleep 1
	CUR_TIME=0
	while [[ $CUR_TIME -lt $MAX_TIME ]]
	do
		CUR_TIME=$((CUR_TIME +STEP))
		if [  `ps --pid $PID | wc -l` -eq 1 ]; then
			break;
		fi
		echo "Wait for gracefully exiting of ContentBroker $CUR_TIME/$MAX_TIME s"
		sleep $STEP
	done
	
	if [  `ps --pid $PID | wc -l` -eq 2 ]; then
		kill -9 $PID
	fi
fi
rm -f /tmp/cb.running
