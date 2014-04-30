#!/bin/bash

#HOWTO Create Contractor
#Contentbroker User must have read access to all directoroes created.
#This is being done by this script.

#don't forget to: (examples!)
# insert into contractors (short_name,email_contact,id) values ('TEST1','da-nrw-notifier@uni-koeln.de',7);
# insert into users (id,login,password,name,contractorshortname,nodeindex) values  (158,'TEST1','TEST1','TEST1 LVR','TEST1',1);  


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
ichmod -M own rods /da-nrw/home/$1
ichmod -M own contentbroker /da-nrw/home/$1
ichmod -M inherit /da-nrw/home/$1
imkdir /da-nrw/home/$1/outgoing
imkdir /da-nrw/work/$1
echo "home dirs and for created"
imkdir /da-nrw/aip/$1
ichmod -M own rods /da-nrw/aip/$1
ichmod -M own contentbroker /da-nrw/aip/$1
ichmod -M inherit /da-nrw/aip/$1
echo "aip folders created, granted rights"
ichmod read $1  /da-nrw/home/$1/outgoing
ichmod inherit /da-nrw/home/$1/outgoing
iadmin atg danrwUser $1

imkdir /da-nrw/dip/public/$1
imkdir /da-nrw/dip/instution/$1
ichmod -M own contentbroker /da-nrw/dip/public/$1
ichmod -M own contentbroker /da-nrw/dip/instution/$1
echo -e "\nUSER $1 created!"
echo "Don't forget to"
echo "insert into contractors (short_name,email_contact,id) values ('$1','email@rechner.de',ID);"

