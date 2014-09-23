	/*
	  DA-NRW Software Suite | ContentBroker
	  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
	  Universität zu Köln
	
	  This program is free software: you can redistribute it and/or modify
	  it under the terms of the GNU General Public License as published by
	  the Free Software Foundation, either version 3 of the License, or
	  (at your option) any later version.
	
	  This program is distributed in the hope that it will be useful,
	  but WITHOUT ANY WARRANTY; without even the implied warranty of
	  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	  GNU General Public License for more details.
	
	  You should have received a copy of the GNU General Public License
	  along with this program.  If not, see <http://www.gnu.org/licenses/>.
	*/

# Installation ContentBroker

This document describes how to setup a node with DNSCore in the quickest possible manner, using
an existent build of the software which can be retrieved from our releases section.
This will help you to learn how to set up and configure
a version of DNSCore for production. To make things easy for you, the version you will build is a stripped
down version, without presentation and advanced storage layer with replications. But once you have installed and tested
the basic version you easily can extend the software by the other components if you install them and change a few
configuration settings. We think this is a great way for administrators to get up and running quickly while
getting an understanding of the basic behaviour and configuration possibilities of the software.

A second [tutorial](preparing_irods_for_dnscore.md) then describes
how to convert the existing DNSCore into a full-fledged installation including the iRODS storage layer.

**Note** that if you want to learn how to build a new version of the software from scratch, using the build system and the deploy scripts, look [here](development_deploy.md).

## Prerequisites

* Python > 2.7
* Oracle Java 1.6 
* PostgreSQL > 9.0

## ContentBroker

### Install the software 


Create a directory somewhere on your filesystem into which you can install your ContentBroker.

    [...]/ContentBroker/

[...] denotes some arbitrary path on your file system.

1. Download an installer for the newest stable version of the software from the 
[release section](https://github.com/da-nrw/DNSCore/releases) and put it to the a arbitrary temp dir on your box. The temp dir will be called [tmp] here.
1. Unpack it. You will then find a directory at [tmp]/installation.xyz/ from where you can install your DNSCore.
1. cd into [tmp]/installation.xyz/ 
1. Call
<pre>
    ./install.sh [...]/ContentBroker (make sure there is no trailing slash!)
</pre>
1. As feature set, choose (n)ode (explanation of different feature sets [here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/system_configuration.md))
1. Download a fake glue [script](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/bash/ffmpeg.sh.fake) 
that will ensure you don't have to install ffmpeg for now.
1. Replace [...]/ContentBroker/ffmpeg.sh by the ffmpeg.sh.fake file you downloaded

The application is then installed in [somewhere]/ContentBroker and needs further configurations in order to run
properly.

### Installation directories and properties file

Prepare your installation and storage directories

    [...]/storage/
                    userArea/
                    ingestArea/
                    workArea/
                    gridCacheAre/

The storage directory is a directory structure
which DNSCore will use to work with data packages. The subfolders correspond to the various 
[processing stages](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/processing_stages.md).
 
Now you need to configure your installer in order to make use of the created directories.
 
1. Get a properties file 
[template](https://raw.github.com/da-nrw/DNSCore/master/ContentBroker/src/main/conf/config.properties.dev). Note that this document
can change from time to time and therefore is bound to a specific version of DNSCore. If in doubt you can get a config which fits your
version in the appropriate source code branch of the release you want to install.
1. Save the file as [somewhere]/ContentBroker/conf/config.properties
1. Open the file and replace all occurrences of CONTENTBROKER_ROOT by [somewhere].

config.properties:    
    
    localNode.userAreaRootPath=[...]/storage/userArea
    localNode.ingestAreaRootPath=[...]/storage/ingestArea
    localNode.workAreaRootPath=[...]/storage/workArea
    localNode.gridCacheAreaRootPath=[...]/storage/gridCacheArea
    localNode.id = 1 

(Make sure the paths fit the recently created paths on your file system)

If your python installation is not globally visible, for example if your package system does not provide
the newest version, set the path accordingly at

    cb.bin.python=python 
 
### Database

1. Set up a new database. It will be refered to as [dbname].
2. The password is encrypted. You have to use the project's password encryptor. 
1. Create a database user [dbuser] with exactly that password and grant all privileges on [dbname] to [dbuser].

You are able to create the schema starting with the given model classes from scratch. Assuming an empty DB and valid credentials, you are able to perform the following command:
    
    java -jar ContentBroker.jar createScheme
    
Alternatively you could use this approach:

1. Download a hibernate properties file 
[template](https://raw.github.com/da-nrw/DNSCore/master/ContentBroker/src/main/xml/hibernateCentralDB.cfg.xml.postgres).
1. Save the file as [somewhere]/ContentBroker/conf/hibernateCentralDB.cfg.xml.
1. Open the file and edit the following entry to match your hostname and port (for a default postgres installation on a fresh box you can set hostname to localhost and port to 5432).
<pre>
   property: connection.url -> jdbc:postgresql://hostname:port/[dbname]
</pre>
1. In order to create the database scheme let the application know that it has the right to do so:
<pre>
    property: htm2ddl.auto -> create
</pre>

1. Create a node (in tables nodes):
<pre>
insert into nodes (id,name,urn_index) values (1,'localnode',1);
</pre>

### Start the software

Although DNS provides start& stop Scripts, they should be considered as template files for startup and shutdown. In binary insatller, they are provided as .TEMPLATE files. It is neccessary to customize them for your own linux distro and network setup (e.g. Proxies)

1. Call
<pre>
    cd [cb]/ContentBroker
    ./ContentBroker_start.sh
    tail -f log/contentbroker.log
</pre>
1. You should see your ContentBroker start working. You can verify by looking for ongoing messages that look like "No jobs in queue, nothing to do, shoobidowoo, ..."

You now have a ContentBroker installation which runs but does nothing for now.
What we now need is to configure the database and add some user directories for a test user in order to let the ContentBroker do some work.

**Important:** Now that you have a running application and created a database scheme, make sure to reedit the
following entry in your hibernateCentralDB.cfg.xml to make sure your database scheme cannot be modified anymore
by the application:

    property: htm2ddl.auto -> validate

### Adding a TEST contractor

According to the structure of the different areas in addition to the basic folder layout we need directories
for at least one user to run tests against the system. This first user (or contractor in DNSCore terminology) is typically the TEST user. Note that TEST is the  [contractors](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/object_model.md#contractor) short name (or csn for short) and that short names in our system are case sensitive. For our system to work with the TEST user, extend the directory structure like this:

    [...]/storage/
                    userArea/
                         TEST/
                              incoming/
                              outgoing/
                    ingestArea/
                         TEST/
                    workArea/
                         TEST/
                    gridCacheArea/
                         TEST  

In the database create needed contractors:
<pre>
insert into contractors (id,short_name,admin) values (1,'TEST',0)
</pre>

In order to run properly, two additional contractors are needed:
<pre>
insert into contractors (id,short_name,admin) values (2,'DEFAULT',0)
insert into contractors (id,short_name,admin) values (3,'PRESENTER',0)
</pre>

### Test your application

Now that you have created the minimum necessary database configuration for the ContentBroker to work with, restart your ContentBroker

    ./ContentBroker_start.sh

Verify that it runs with

    tail -f log/contentbroker.log
    
In addition to the shoobidoowoo message you now should see the UserAreaScannerWorker scanning the TEST folder (look for the message 
d.u.h.da.core.UserAreaScannerWorker - TEST).
    
Download a testpackage from our source code repository, for example 
[this](https://github.com/da-nrw/DNSCore/raw/master/ContentBroker/src/test/resources/manual/BagIt_META1_METS_MODS_2013.tgz) one.
Copy it to your IngestArea

    cp BagIt_META1_METS_MODS_2013.tgz [...]/storage/ingest/TEST/abc1.tgz
    
After some seconds the ContentBroker should fetch the package and you won't find it anymore under ingest/TEST.
Watch the ContentBroker working with the package with

    tail -f log/object-logs/1-[DDDDDDDDDD].log       // DDDD... simply means: some digits
    
**Troubleshooting**

If the package does not get removed from the IngestArea, have a look at

    tail -f log/ingest.log
    
If you want to restart again with another testpackage, make sure you always set a new name, e.g. abc2.tgz for the second package.
Always look for the newest object log under

    log/object-logs/

## DAWeb 

The second core component of DNSCore is DAWeb, the graphical user interface for the ContentBroker.
How to set up this component is part of this [tutorial](https://github.com/da-nrw/DNSCore/blob/master/DAWeb/doc/deploy.md).


## Congratulations

You have set up the two components of DNSCore.
Now your DNSCore is up'n'running, you are free to play around with it or convert your existing installation into a full-fledged
installation following this [tutorial](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/installation_irods_cb.md).

The tutorial will show you how to install additional components and features:

* Format conversion
* iRODS Storage layer
* Presentation Repostitory





