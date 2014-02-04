#/bin/bash
# DA-NRW 2013 Jens Peters, CB TALK Client starter

if [ $# -eq 0 ] 
then 
	echo you have to specify at least a command
	exit
fi
SERVER=tcp://localhost:4455
COMMAND=""
if [ $# -eq 2 ] 
then 
	SERVER=$1
	COMMAND=$2
fi
if [ $# -eq 1 ]
then
        COMMAND=$1
fi

echo "launched CBTalk on $SERVER with command $COMMAND"
java -cp ContentBroker.jar de.uzk.hki.da.core.ControllerClient $SERVER $COMMAND
