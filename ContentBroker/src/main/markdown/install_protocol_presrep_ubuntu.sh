# example installation of all presentation repository
# components on a server running on Ubuntu 12.04 LTS

# Download java 1.6
sh jdk-6u45-linux-x64.bin
mv jdk1.6.0_45 /opt
ln -s /opt/jdk1.6.0_45/ java
update-alternatives --config java
sudo update-alternatives --config java
sudo update-alternatives --install java
update-alternatives --install "/usr/bin/java" "java" "/opt/java/bin/java" 1
update-alternatives --install "/usr/bin/javac" "javac" "/opt/java/bin/javac" 1
update-alternatives --install "/usr/bin/javaws" "javaws" "/opt/java/bin/javaws" 1
update-alternatives --install "/usr/bin/jar" "jar" "/opt/java/bin/jar" 1
update-alternatives --set "java" "/opt/java/bin/java" 1
update-alternatives --set "java" "/opt/java/bin/java"
update-alternatives --set "javac" "/opt/java/bin/javac"
update-alternatives --set "jar" "/opt/java/bin/jar"
update-alternatives --set "javaws" "/opt/java/bin/javaws"
sudo update-alternatives --config java
java -version
apt-get install tomcat7 tomcat7-admin
java -version
apt-get install postgresql
useradd fedora
passwd fedora
su -l postgres
echo "CREATE USER fedora WITH PASSWORD 's3cr3t';
CREATE DATABASE fedora;
GRANT ALL PRIVILEGES ON DATABASE fedora TO fedora;
CREATE DATABASE 'riTriples';
GRANT ALL PRIVILEGES ON DATABASE 'riTriples' TO fedora;" | psql template1
wget http://downloads.sourceforge.net/fedora-commons/fcrepo-installer-3.5.jar
java -jar fcrepo-installer-3.5.jar
chown -R tomcat7:tomcat7 /opt/fedora/

git clone https://github.com/da-nrw/prepscripts.git
cd prepscripts
editor scripts/setup-policies.py
python scripts/setup-policies.py
editor /opt/fedora/pdp/conf/config-attribute-finder.xml
rm -f /opt/fedora/pdp/policies/*

apt-get install unzip
wget http://downloads.sourceforge.net/fedora-commons/oaiprovider-1.2.2.zip
unzip oaiprovider-1.2.2.zip
sudo -u tomcat cp oaiprovider-1.2.2/oaiprovider.war /opt/tomcat/webapps/
useradd proai
passwd proai
su -l postgres
echo "CREATE USER proai WITH PASSWORD 'proai';
CREATE DATABASE proai;
GRANT ALL PRIVILEGES ON DATABASE proai TO proai;" | psql template1
mkdir -p /data/danrw/proai
cd prepscripts/
editor scripts/ingest.py
python scripts/ingest.py -f config/danrw_PresentationRepository.xml
python scripts/ingest.py -f config/set_ddb.xml
cp config/proai.properties /var/lib/tomcat7/webapps/oaiprovider/WEB-INF/classes/
editor /var/lib/tomcat7/webapps/oaiprovider/WEB-INF/classes/proai.properties
/etc/init.d/tomcat7 restart

wget https://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-0.90.3.deb
dpkg -i elasticsearch-0.90.3.deb
curl -XGET 'http://localhost:9200/_cluster/state'
editor /etc/elasticsearch/elasticsearch.yml
/etc/init.d/elasticsearch restart
curl -XPUT 'http://localhost:9200/portal_test'
cd prepscripts/
curl -XPUT 'http://localhost:9200/portal_test/aggregation/_mapping' -d @config/es_mapping.json


apt-get install apache2
a2enmod proxy proxy_headers proxy_ajp proxy_http rewrite
