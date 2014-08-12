#!/bin/bash

#HOWTO Create Contractor
#Contentbroker User must have read access to all directories created.
#This is being done by this script.

#don't forget to: (examples!)
# insert into contractors (short_name,email_contact,id,admin) values ('TEST1','da-nrw-notifier@uni-koeln.de',7,0);


echo "Create iRODS Contractor Named $1"
EXPECTED_ARGS=1
E_BADARGS=65

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` {contractor_short_name}"
  exit $E_BADARGS
fi
echo "iRODS Password"
read INPUT

read -p "Are you sure (y/Y)? Create User $1" -n 1
if [[ ! $REPLY =~ ^[Yy]$ ]]
then
    exit 1
fi
iadmin mkuser $1 rodsuser
if [ $(echo -n "$INPUT" | wc -m) -ge 6 ]
then iadmin moduser $1 password $INPUT
fi
echo "user created"
imkdir /da-nrw/work/$1
imkdir /da-nrw/aip/$1
ichmod -M own rods /da-nrw/aip/$1
ichmod -M own contentbroker /da-nrw/aip/$1
ichmod -M inherit /da-nrw/aip/$1
echo "aip folders created, granted rights"
imkdir /da-nrw/pips/public/$1
imkdir /da-nrw/pips/instution/$1
ichmod -M own contentbroker /da-nrw/pips/public/$1
ichmod -M own contentbroker /da-nrw/pips/instution/$1
echo -e "\nUSER $1 created!"
echo "Don't forget to"
echo "insert into contractors (short_name,email_contact,id,admin) values ('$1','email@rechner.de',ID,0);"

