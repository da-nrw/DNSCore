# Open ports

In order for DNSCore to work properly some ports have to be opened on the firewall protecting your server.

## iRODS

iRODS uses out of the box the following ports for communication between the different nodes of a grid:
* TCP 1247
* TCP 20000-20199

As these Ports are configured via the irodssetup depending to your master node, or the other master nodes you want to federate data to, this might change or could be changed at all. 

## PostgreSQL

On every node that acts as a master iCAT or that holds the central object database:
* TCP 5432

## Using DNSCore in DMZ or behind firewalls (proxies)

DNSCore uses JAVS_OPTS to pass additional proxy setup to ContentBroker. Otherwise DTD lookups might fail or hang.

    export JAVA_OPTS="-Dhttp.keepAlive=true -Dhttp.proxyHost=$host -Dhttp.proxyPort=$port -Dhttp.proxyUser=$user -Dhttp.proxyPassword=$password"
