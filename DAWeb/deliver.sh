#/bin/bash
# author: Jens Peters 

VERSION=0.6.2
if [ $# -eq 0 ]
then
        echo you have to specify your target environment
        exit
fi

if [ "$1" = "dev" ]
then
        if [ $# -ne 2 ]
        then
                echo you chose a development environment as target environment. call
                echo "./deliver.sh dev <resinInstallationRootPath>"
                exit
        fi
        INSTALL_PATH=$2

        if [ ! -d "$INSTALL_PATH" ]; then
                echo Error: $INSTALL_PATH is not a directory.
                exit
        fi
	echo "Building WAR $1"
	grails dev war 
	tar xzf ./test/integration/resin-4.0.37.tar.gz --strip-components 1 -C $2 
        echo "Attempt to stop resin server"	
	$2/bin/resin.sh stop
	rm -rf $2/webapps/daweb3
	rm -rf $2/webapps/daweb3.war
	cp target/daweb3-$VERSION.war $2/webapps/daweb3.war
	$2/bin/resin.sh start
	echo "waiting for Resin to deploy webapp"
	while [ ! -d $2/webapps/daweb3/WEB-INF/classes ]
	do
  		sleep 2
	done
	cp -f ./daweb3_properties.groovy.$1 $2/webapps/daweb3/WEB-INF/classes/daweb3_properties.groovy
	echo "Restart Resin Server"
	$2/bin/resin.sh restart
	echo "RESIN now listens on 8080 - If you don't see any DA-NRW LOGO, this might be an issue of configurations stored in $2/webapps/daweb3/WEB-INF/classes/daweb3_properties.groovy"
	wget http://localhost:8080 >/dev/null
fi

if [ "$1" = "vm2" ]
then
	echo "building WAR dev"
	grails dev war
	echo "Deliver to VM 2"
	scp target/daweb3-$VERSION.war irods@da-nrw-vm2.hki.uni-koeln.de:/home/irods/daweb3.war
	scp ./daweb3_properties.groovy.$1 irods@da-nrw-vm2.hki.uni-koeln.de:/home/irods/daweb3_properties.groovy
	echo "Please copy now file to webapp dir and restart server manually"
fi
if [ "$1" = "vm6" ]
then
	echo "building test WAR"
	grails test war
	echo "Deliver to VM 6"
        scp target/daweb3-$VERSION.war irods@da-nrw-vm6.hki.uni-koeln.de:/home/irods/daweb3.war
        scp ./daweb3_properties.groovy.$1 irods@da-nrw-vm6.hki.uni-koeln.de:/home/irods/daweb3_properties.groovy
        echo "Please copy now file to webapp dir and restart server manually"
fi
