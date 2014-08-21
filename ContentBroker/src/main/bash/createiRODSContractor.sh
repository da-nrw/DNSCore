#!/bin/bash

#Create folders need on the Storage level
#Contentbroker User must have read access to all directories created.

asksure() {
echo -n "Are you sure (Y/N)? "
	while read -r -n 1 -s answer; do
  	if [[ $answer = [YyNn] ]]; then
  	 [[ $answer = [Yy] ]] && retval=0
    	 [[ $answer = [Nn] ]] && retval=1
   	break
  	fi
done
return $retval
}

echo "Create iRODS Contractor Named $1"
EXPECTED_ARGS=1
E_BADARGS=65

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` {contractor_short_name}"
  exit $E_BADARGS
fi

if asksure; then
	echo "creating necessary folders"
fi
# We don't need autentication on the storage layer anymore
#echo "iRODS Password"
#read INPUT
#if [ $(echo -n "$INPUT" | wc -m) -ge 6 ]
#then 
#	if asksure; then
#
#		iadmin mkuser $1 rodsuser
#		iadmin moduser $1 password $INPUT
#		echo "user successfully created"
#		echo "configured users"
#		iadmin lu
#	else 
#		echo "something went wrong"
#		exit $E_BADARGS
#	fi
##lse 
#	echo "Password has to be longer then 6 characters"	
#	exit $E_BADARGS
#fi
imkdir /da-nrw/work/$1
imkdir /da-nrw/aip/$1
ichmod -M own rods /da-nrw/aip/$1
ichmod -M own contentbroker /da-nrw/aip/$1
ichmod -M inherit /da-nrw/aip/$1
echo "aip folders created, granted rights"
imkdir /da-nrw/pips/public/$1
imkdir /da-nrw/pips/institution/$1
ichmod -M own contentbroker /da-nrw/pips/public/$1
ichmod -M own contentbroker /da-nrw/pips/institution/$1
echo -e "\nUSER $1 created!"
echo "Don't forget to"
echo "To configure ingest and retrieval folders as needed by ContentBroker."
