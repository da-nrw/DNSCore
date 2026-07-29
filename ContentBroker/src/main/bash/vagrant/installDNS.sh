#!/bin/bash
## LVR-InfoKom 2019

export SCPATH=$(pwd)
echo "SCPATH $SCPATH"
if [ -f /etc/redhat-release ] ; then
	export HOSTOS=$(cat /etc/system-release-cpe | cut -f 3 -d : )
	export HOSTREL=$( rpm -qf /etc/redhat-release | cut -f 4,5 -d - | cut -f 1-2 -d .)
	export SYSDVER=$(cat /etc/system-release-cpe | cut -f 5 -d : | cut -c 1)
else 
	echo "Skript ist nur fuer RedHat-OS angepasst" 
	exit 1;
	#exit $?
fi

if [ $SYSDVER -ne "7" ] ; then
	echo "Skript ist nur fuer RedHat-OS 7 nicht fuer $SYSDVER" 
	exit 1;
fi

if [ ! -d /ci ]; then
    mkdir /ci
fi

if [ $( localectl | grep "System Locale" | cut -f 2 -d : | cut -f 2 -d = ) != "en_US.UTF-8" ] ; then
	localectl set-locale LANG=en_US.UTF-8
fi

export LANG=en_US.UTF-8

####
 
echo "Fuer die Installation soll die Firewall und IPv6 und SELINUX erst mal abgeschaltet werden."
if [ `getenforce`=='Enforcing' ] ; then
	setenforce 0
	sed -i 's/SELINUX=enforcing/SELINUX=permissive/g' /etc/selinux/config
fi


function iusrcmd {  
 su - $IUSER -c "$1" 
}

systemctl stop firewalld
systemctl disable firewalld
	
ANSWER=`getenforce`	
if [ $ANSWER != 'Permissive' ] ; then
	echo "Selinux abstellen mislungen: $ANSWER" 
	exit 1;
fi

###

ANSWER=`ls -l /etc/yum.repos.d/ | wc -l `
echo "YUM RepoANSWER: $ANSWER"

if [ $(grep -i "epel" /etc/yum.repos.d/*repo | wc -l ) == "0" ] ; then
	yum -y localinstall $SCPATH/data/epel-release-latest-7.noarch.rpm
	rpm --import $SCPATH/data/RPM-GPG-KEY-EPEL-7
fi

if [ "1" -eq "1" ]; then  # yum kennt sonst kein HandBrake und ffmpeg
	cp $SCPATH/data/dns-7-repo.tgz /etc/yum.repos.d
	cd /etc/yum.repos.d; 
	tar -xzvf dns-7-repo.tgz
	rpm --import $SCPATH/data/RPM-GPG-KEY-nux
	rm -f dns-7-repo.tgz
	yum update
fi 


#rm -f /etc/profile.d/dns.sh

if [ ! -f /etc/profile.d/dns.sh ] ; then 
	cp $SCPATH/data/profileDns.sh  /etc/profile.d/dns.sh
else
	echo  'export FEDORA_HOME=/ci/fedora' >> /etc/profile.d/dns.sh
	echo  'export CATALINA_HOME=/usr/share/tomcat' >> /etc/profile.d/dns.sh
	echo  'export BUILD_NUMBER=123' >> /etc/profile.d/dns.sh
	echo  'export MAVEN_OPTS="-Xmx512m -XX:MaxPermSize=256m"' >> /etc/profile.d/dns.sh
	echo  'umask 002 ' >> /etc/profile.d/dns.sh
	echo  ' ' >> /etc/profile.d/dns.sh
fi

### User anlegen


groupadd -g 401 irods
groupadd -g 402 postgres   
groupadd -f -g 403 tomcat
#groupadd -g 404 isupport 


useradd -c "irods user" -d /home/irods -s /bin/bash -g 401 -u 401 irods
useradd -c "postgres db user" -d /home/postgres -s /bin/bash -g 402 -u 402 postgres
useradd -c "tomcat user" -d /home/tomcat -s /bin/bash -g 403 -G 401 -u 403 tomcat
#useradd -c "irods support user" -d /home/isupport -s /bin/bash -g 404 -G 401 -u 404 isupport
cat /etc/group | grep ":40"
cat /etc/passwd | grep ":40"

groupadd -g 12348 developer
usermod -a -G developer irods

####script e


yum update

java -version
ls -l /usr/bin/java
### java pruefen
if [ ! -d /usr/java/jdk1.8.0_181-amd64 ] ; then
   #rm -f /usr/bin/java
   echo "JDK 1.8.0.181 wird installiert."
   mkdir /usr/java
   #cp $SCPATH/../ship/jdk-6u45-linux-x64.bin /usr/java
   #cd /usr/java; chmod a+x *bin; ./jdk-6u45-linux-x64.bin 
   #rm -f jdk-6u45-linux-x64.bin
   #ln -s /usr/java/jdk1.6.0_45 /usr/java/latest
   #ln -s /usr/java/latest /usr/java/default
   yum localinstall -y $SCPATH/data/jdk-8u181-linux-x64.rpm
fi

ln -s /usr/java/jdk1.8.0_181-amd64 /usr/java/latest 
ln -s /usr/java/latest /usr/java/default
if [ $( rpm -qa | grep -i chkconfig | wc -l ) == "0" ] ; then
	yum -y install chkconfig
fi
/usr/sbin/alternatives --install /usr/bin/java java /usr/java/jdk1.8.0_181-amd64/bin/java  3
/usr/sbin/alternatives --set  java /usr/java/jdk1.8.0_181-amd64/bin/java
echo "export JAVA_HOME=/usr/java/jdk1.8.0_181-amd64" >> ~irods/.bashrc
echo "export JAVA_HOME=/usr/java/jdk1.8.0_181-amd64" >> ~tomcat/.bashrc
echo "export JAVA_HOME=/usr/java/jdk1.8.0_181-amd64" >> /etc/profile.d/dns.sh

java -version
ANSWER=$(java -version 2>&1 | head -n 1 | cut -f 3 -d " " )
echo "java ANSWER: $ANSWER"




#yum install -y clamav
yum -y install clamav-server clamav-data clamav-update clamav-filesystem clamav clamav-scanner-systemd clamav-devel clamav-lib clamav-server-systemd
sed -i -e "s/^Example/#Example/" /etc/clamd.d/scan.conf
sed -i -e "s/^#LocalSocket/LocalSocket/" /etc/clamd.d/scan.conf
#yum install  -y ffmpeg ImageMagick-6.7.8.9 ghostscript sox HandBrake-cli python

yum install -y ffmpeg ImageMagick-6.7.8.9 ghostscript sox HandBrake-cli python git
#yum --enablerepo=linuxtech --enablerepo=linuxtech-testing -y install handbrake-cli --nogpgcheck

	
####script f
yum install -y tomcat tomcat-webapps
mkdir /usr/share/tomcat/.grails/
cp $SCPATH/data/daweb3_properties.groovy /usr/share/tomcat/.grails/daweb3_properties.groovy
chmod 644 /usr/share/tomcat/.grails/daweb3_properties.groovy
chown tomcat:tomcat -R /usr/share/tomcat/.grails


	
ANSWER=$((`rpm -qa | grep -i ImageMagick `))
echo "ImageMagick ANSWER: $ANSWER"
ANSWER=$((`rpm -qa | grep -i ffmpeg  `))
echo "ffmpeg ANSWER: $ANSWER"
ANSWER=$((`rpm -qa | grep -i ghostscript`))
echo "ghostscript ANSWER: $ANSWER"
ANSWER=$((`rpm -qa | grep -i sox `))
echo "sox ANSWER: $ANSWER"
ANSWER=$((`rpm -qa | grep -i handbrake `))
echo "handbrake ANSWER: $ANSWER"
ANSWER=$((`rpm -qa | grep -i tomcat | wc -l ` ))
echo "tomcat ANSWER: $ANSWER"




#### script h  postgress
yum remove postgresql\* -y
yum -y install postgresql93-server postgresql93-contrib postgresql93-devel postgresql93-libs postgresql93-odbc pgadmin3_93.x86_64 --nogpgcheck
/usr/pgsql-9.3/bin/postgresql93-setup initdb
systemctl stop postgresql-9.3
systemctl start postgresql-9.3
#rm -f /var/lib/pgsql/9.3/data/pg_hba.conf
mv /var/lib/pgsql/9.3/data/pg_hba.conf /var/lib/pgsql/9.3/data/pg_hba.confBU
cp -f $SCPATH/data/pg_hba.conf /var/lib/pgsql/9.3/data/pg_hba.conf
sed -i "s/#listen_addresses/listen_addresses = '*'\n#listen_addresses/g" /var/lib/pgsql/9.3/data/postgresql.conf 
sed -i 's/max_connections = 100/max_connections = 200/g' /var/lib/pgsql/9.3/data/postgresql.conf   # TODO: wird nichts ersetzt

systemctl enable postgresql-9.3
systemctl stop postgresql-9.3
systemctl start postgresql-9.3
mv /etc/odbcinst.ini /etc/odbcinst.orig
head -n 11 /etc/odbcinst.orig >> /etc/odbcinst.ini

ANSWER=`ps -ef | grep -i pgsql `
echo "postgres ANSWER: $ANSWER"

#### script i



IUSER=postgres
        
# DB ICAT and FED anlegen:
iusrcmd "/usr/pgsql-9.3/bin/createuser -s -d -r -l irods"
iusrcmd "/usr/pgsql-9.3/bin/createuser -s -d -r -l fed_usr"
iusrcmd "/usr/pgsql-9.3/bin/createuser -s -d -r -l cb_usr"
#iusrcmd "/usr/pgsql-9.3/bin/createuser -s -D -r -l backup"

iusrcmd "/usr/pgsql-9.3/bin/dropdb CB"
iusrcmd "/usr/pgsql-9.3/bin/dropdb ICAT"
iusrcmd "/usr/pgsql-9.3/bin/dropdb FED"
iusrcmd "/usr/pgsql-9.3/bin/createdb -E UTF-8 -O irods CB"
iusrcmd "/usr/pgsql-9.3/bin/createdb -E UTF-8 -O irods ICAT"
iusrcmd "/usr/pgsql-9.3/bin/createdb -E UTF-8 -O irods FED"
echo "alter role irods with password '"$FEDPASS"';" > ~postgres/alter-irods-user.sql
echo "alter role irods with password '"$ICATPASS"';" >> ~postgres/alter-irods-user.sql
echo "alter role irods with password '"$RODSPASS"';" >> ~postgres/alter-irods-user.sql
        
cp $SCPATH/data/client-encoding-utf8.sql ~postgres
cp $SCPATH/data/createDB.sql ~postgres
iusrcmd "/usr/bin/psql -f ~postgres/alter-irods-user.sql"
iusrcmd "/usr/bin/psql -f ~postgres/client-encoding-utf8.sql"
iusrcmd "/usr/bin/psql -f ~postgres/createDB.sql"

## fedora install 

if [ "1" == "1" ] ; then # dieser Bereich sollte spaeter durch das fedora installskript ersetzt werden
	echo "fedora install"
	cd $SCPATH/data/ 
	tar -xzf $SCPATH/data/FED-DB-20180517.dump.tgz
	psql -d FED -U fed_usr < ./FED-DB-20180517.dump
	rm -f $SCPATH/data/FED-DB-20180517.dump
	sleep 1
	echo "fedora unpack tar"
	tar -xzf $SCPATH/data/fedora-files.tgz
	if [ -d /ci/fedora ] ; then
		rm -rf /ci/fedora
	fi
	mv fedora /ci/
	chown -R irods:developer /ci/fedora
	rm -rf fedora
	
	systemctl stop tomcat
	sed -i "s/unpackWARs=\"true\" autoDeploy=\"true\"/unpackWARs=\"true\" autoDeploy=\"false\"/g" /usr/share/tomcat/conf/server.xml 
	cp $SCPATH/data/fedoraTomcatConf.xml /usr/share/tomcat/conf/Catalina/localhost/fedora.xml	
	cd /usr/share/tomcat/webapps/
	if [ -d /usr/share/tomcat/webapps/fedora ] ; then 
		rm -rf /usr/share/tomcat/webapps/fedora;
	fi
	tar -xzf $SCPATH/data/fedoraNoWar.tgz
	rm -f fedora.war
	systemctl enable tomcat
	systemctl start tomcat
fi
sleep 1

yum -y install perl-JSON python-jsonschema python-requests python-psutil authd


echo "Beginne iRODS Install"
## install irods
IUSER=irods
ZONENAME="ci"
HOSTNR=1
RODSPASS="sdor78-bvc"
ICATPASS="irods123"
FEDPASS="clBDmno7"
#CACHEDIR="/ci/storage/GridCacheArea"
CACHEDIR="/ci/archiveStorage"
#CACHEDIR="/ci/storage/WorkArea"
WORKDIR="/ci/storage/WorkArea"
CACHERESC="ciWorkingResource"
DBPASS="KKLmno13g"
mkdir -p $CACHEDIR/work
chown -R irods:irods $CACHEDIR
if [ -d ~irods/.irods ] ; then
	rm -rf  ~irods/.irods 
fi

mkdir ~irods/.irods;
chmod 770 ~irods/.irods


#



systemctl stop irods
yum remove irods\* -y

rm -rf  /var/lib/irods/
rm -rf ~irods/.irods
rm -rf /etc/irods




yum -y localinstall $SCPATH/data/irods-icat-4.1.11-centos7-x86_64.rpm
yum -y localinstall $SCPATH/data/irods-database-plugin-postgres-1.11-centos7-x86_64.rpm

if [ -f /usr/lib64/psqlodbc.so ] ; then
	rm -f /usr/lib64/psqlodbc.so  
fi
if [ -f /usr/pgsql-9.3/lib/psqlodbc.so ] ; then
	rm -f /usr/pgsql-9.3/lib/psqlodbc.so
fi

chown -R irods:irods /var/lib/irods


	
systemctl stop postgresql-9.3
systemctl start postgresql-9.3


#### irods

systemctl stop irods 


#irsed -i "s/testpassword/$RODSPASS/g"  /etc/irods/database_config.json
sed -i "s/SHA256/MD5/g" /etc/irods/server_config.json

if [ -f /etc/init.d/irods ]; then
    chkconfig irods off
    service irods stop
    rm -f /etc/init.d/irods
fi
cp $SCPATH/data/irodsC7 /etc/systemd/system/irods.service
systemctl enable irods
systemctl start irods
		
		
		
ZONES="12345"	
echo "Zone $ZONES"
ZONEKEY="dns"$ZONES"dns"$ZONES"dns"$ZONES"dns"$ZONES
echo "Zonenkey $ZONEKEY"
echo "Default-Dir $CACHEDIR"

printf "irods\nirods\n$ZONENAME\n1247\n\n\n$CACHEDIR\ndns$ZONES\n$ZONEKEY\n1248\ndnszone-dnszone-dnszone-dnszone-\noff\nrods\n$RODSPASS\nyes\nlocalhost\n\n\n\n$ICATPASS\nyes\n" | /var/lib/irods/packaging/setup_irods.sh
IUSER=irods
sleep 3

systemctl start irods 
sleep 1
#service irods stop
#systemctl stop irods
#sleep 1
#sed -i 's!"icat_host": null!"icat_host": "$(hostname)"!g' /etc/irods/server_config.json
#sed -i 's!"icat_host": null!"icat_host": "$(hostname)"!g' /etc/irods/server_config.json

#sed -i 's!\"icat_host\": \"localhost\"!\"icat_host\": \""$(hostname)"\"!g' /etc/irods/server_config.json
#service irods start
#systemctl start irods
sed -i 's!\"default_dir_mode\": \"0750\"!\"default_dir_mode\": \"0775\"!g' /etc/irods/server_config.json
sed -i 's!\"default_file_mode\": \"0600\"!\"default_file_mode\": \"0664\"!g' /etc/irods/server_config.json

iusrcmd "printf 'y\n' | /usr/bin/iadmin modresc demoResc name $CACHERESC"
#service irods stop
systemctl stop irods
sleep 1


sed -i 's/SHA256/MD5/g' ~irods/.irods/irods_environment.json
sed -i "s/demoResc/$CACHERESC/g" /etc/irods/server_config.json
sed -i "s/demoResc/$CACHERESC/g" ~irods/.irods/irods_environment.json
sed -i "s/demoResc/$CACHERESC/g" /etc/irods/core.re

#begin
export IUSER=irods
service irods start	


OWNHOST=$(hostname -s)
#OWNZONE="ci"
ARCHRESC="ciArchiveResource"
LZAPATH="/ci/archiveStorage"
LZARESCG="ciArchiveRescGroup"
#CBUSERPASS="KKLmno13g"

iusrcmd "printf 'y\n' | /usr/bin/iadmin mkresc $ARCHRESC unixfilesystem $OWNHOST:$LZAPATH"
iusrcmd "printf 'y\n' | /usr/bin/iadmin mkresc $LZARESCG passthru"
iusrcmd "printf 'y\n' | /usr/bin/iadmin addchildtoresc $LZARESCG $ARCHRESC"
  
iusrcmd "printf 'y\n' | /usr/bin/iadmin modresc ciWorkingResource path $WORKDIR"


### elastiksearch
groupadd -g 396 elasticsearch
useradd -c "elasticsearch" -d /usr/share/elasticsearch -s /sbin/nologin -g 396 -u 397 elasticsearch
yum -y localinstall $SCPATH/data/elasticsearch-0.90.3.noarch.rpm
sed -i "s/# cluster.name/cluster.name: cluster_ci\n# cluster.name/g" /etc/elasticsearch/elasticsearch.yml
systemctl restart elasticsearch


### maven grails gradle
mkdir -p /ci/projects; 
cd /ci/projects
wget http://www-us.apache.org/dist/maven/maven-3/3.6.0/binaries/apache-maven-3.6.0-bin.tar.gz
tar xvfz apache-maven-3.6.0-bin.tar.gz
rm -f apache-maven-3.6.0-bin.tar.gz
#ln -s apache-maven-3.6.0 apache-maven
mkdir -p ~irods/.m2
cp $SCPATH/data/MavenSettings.xml  ~irods/.m2/settings.xml
mkdir -p ~/.m2
cp $SCPATH/data/MavenSettings.xml  ~/.m2/settings.xml
chown -R irods:irods ~irods/.m2

ln -s /ci/projects/apache-maven-3.6.0/bin/mvn /usr/bin/mvn
echo  'export M2_HOME=/ci/projects/apache-maven-3.6.0' >> /etc/profile.d/dns.sh
echo  'export PATH=${M2_HOME}/bin:${PATH}' >> /etc/profile.d/dns.sh
	
cp $SCPATH/data/grails-3.2.11.tgz /ci/projects/grails-3.2.11.tgz
cd /ci/projects/; 
tar -xzf grails-3.2.11.tgz
rm -f grails-3.2.11.tgz
cp $SCPATH/data/gradle-3.4.1-bin.tgz /ci/projects/gradle-3.4.1-bin.tgz
cd /ci/projects/; 
tar -xzf gradle-3.4.1-bin.tgz
rm -f gradle-3.4.1-bin.tgz

chown -R irods:developer /ci/projects/

ln -s /ci/projects/grails-3.2.11/bin/grails /usr/bin/grails
ln -s /ci/projects/gradle-3.4.1/bin/gradle /usr/bin/gradle

	
##project clone


mkdir /ci/ContentBroker
chown irods:developer /ci/ContentBroker

cd /ci/
git clone https://github.com/da-nrw/DNSCore.git
mkdir -p /ci/storage/IngestArea
mkdir -p /ci/storage/IngestArea/noBagit
mkdir -p /ci/storage/IngestArea/noBagit/TEST
mkdir -p /ci/storage/IngestArea/TEST
mkdir -p /ci/storage/WorkArea
mkdir -p /ci/storage/UserArea
mkdir -p /ci/storage/GridCacheArea
mkdir -p /ci/storage/UserArea/TEST
mkdir -p /ci/storage/UserArea/rods
mkdir -p /ci/storage/UserArea/TEST/incoming
mkdir -p /ci/storage/UserArea/TEST/outgoing

chown -R irods:developer /ci/DNSCore
chown -R irods:developer /ci/storage

mkdir -p /ci/python/
ln -s /usr/bin/python2.7 /ci/python/python

$SCPATH/data/initES.sh "portal_ci_test" 
$SCPATH/data/initES.sh "portal_ci"
chmod -R g+w /ci

cd /ci/DNSCore
#iusrcmd "mvn install -Pci"

