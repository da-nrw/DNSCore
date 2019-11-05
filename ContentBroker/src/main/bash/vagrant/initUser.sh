#!/bin/bash
## LVR-InfoKom 2019

# LVR-Infokom 2019

if [ "$#" -ne "1" ] ; then
  echo "Require <USER> as argument"
  exit 0;
fi

DEV_NAME="$1"
DEV_GROUP="developer"
DEV_GROUPS="$DEV_GROUP,$DEV_NAME,tomcat,irods"

OUT_MSG="";

if [ $(cat /etc/group | grep "$DEV_GROUP" | wc -l ) == "0" ] ; then
	#groupadd -g 500 $DEV_GROUP
	groupadd -g 12348 developer
	cat /etc/group | grep "$DEV_GROUP"
fi

if [ $(cat /etc/passwd | grep "$DEV_NAME" | wc -l ) == "0" ] ; then
	groupadd $DEV_NAME
	useradd -c "$DEV_NAME" -g  $DEV_GROUP -G $DEV_GROUPS  $DEV_NAME

	cat /etc/passwd | grep "$DEV_NAME"
	usermod -aG wheel $DEV_NAME
	printf "toor\ntoor\n" | passwd $DEV_NAME
	OUT_MSG=$OUT_MSG:"\nUser: $DEV_NAME\nPW: toor"
	echo "passwort is toor"
	
	cp -R ~irods/.m2 /home/$DEV_NAME/
	chown  $DEV_NAME:$DEV_GROUP -R /home/$DEV_NAME/.m2
	
	chown irods:developer -R /ci
	chown  $DEV_NAME:$DEV_GROUP -R /ci/DNSCore
	chmod g+rw -R /ci/
	#chown  $DEV_NAME:$DEV_GROUP -R /ci/
	#chown  $DEV_NAME:tomcat  -R /ci/fedora
	
	rm -rf /tmp/forkDir
	rm -rf /tmp/aip/
	rm -rf /tmp/folder1
	rm -rf /tmp/abc*
	
	su - $DEV_NAME -c "printf 'cihost\n1247\nrods\nci\nsdor78-bvc\n' | iinit"
fi


if [ "installXFCE" == "installXFCEno" ] ; then
	#https://www.rootusers.com/how-to-install-xfce-gui-in-centos-7-linux/
	#https://www.hiroom2.com/2017/10/01/centos-7-xrdp-xfce-en/
	yum install epel-release -y
	yum groupinstall "Server with GUI" -y
	yum groupinstall "Xfce" -y
	echo "xfce4-session" > /home/$DEV_NAME/.Xclients
	chown $DEV_NAME:$DEV_GROUP /home/$DEV_NAME/.Xclients
	chmod a+x /home/$DEV_NAME/.Xclients
fi

echo $OUT_MSG



