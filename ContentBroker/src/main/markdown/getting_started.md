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

# Getting started

There are two ways of getting started really quickly with DNSCore. 
The first way of getting a running version of DNSCore on your
local machine it to build a version from scratch via the 
[deploy scripts](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/deploy.md), 
which build and install the software,
even preparing the configuration settings and starting a preconfigured lightweight database in the background. The second
way is slightly more complex and is described in this document.

What will be discussed here is how to set up the software in the quickest way possible, but 
without the help of the build scripts. This will help you to learn how to set up and configure
a version of DNSCore for production. To make things easy for you, the version you will build is a stripped
down version, without presentation and advanced storage layer with replications. But once you have installed and tested
the basic version you easily can extend the software by the other components if you install them and change a few
configuration settings. We think this is a great way for administrators to get up and running quickly while
getting an understanding of the basic behaviour and configuration possibilities of the software.

A second [tutorial](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/preparing_irods_for_dnscore.md) then describes
how to convert the existing DNSCore into a full-fledged installation including the iRODS storage layer.

## Prerequisites

* Python > 2.7
* Oracle Java 1.6 
* PostgreSQL > 9.0

## ContentBroker

### Installation directories and properties file

Prepare your installation and storage directories

    [somewhere]/ContentBroker/
    [somewhere]/storage/
                    user/
                    ingest/
                    work/
                    pip/
                        institution/
                        public/
                    aip/
                    grid/

[somewhere] denotes some arbitrary path on your local box.
The ContentBroker directory then is the folder into which we will later let
our installer put the binaries. The storage directory is a directory structure
which DNSCore will use to work with data packages. The subfolders correspond to the various 
[processing stages](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/processing_stages.md).
 
Now you need to configure your installer in order to make use of the created directories.
 
1. Get a properties file 
[template](https://raw.github.com/da-nrw/DNSCore/master/ContentBroker/src/main/conf/config.properties.dev). Note that this document
can change from time to time and therefore is bound to a specific version of DNSCore. If in doubt you can get a config which fits your
version in the appropriate source code branch of the release you want to install.
1. Save the file as config.properties in an arbitrary temporary directory on your local box.
1. Replace CONTENTBROKER_ROOT by [somewhere].

in config.properties:    
    
    localNode.userAreaRootPath=[somewhere]/storage/user
    localNode.ingestAreaRootPath=[somewhere]/storage/ingest
    localNode.workAreaRootPath=[somewhere]/storage/work
    localNode.dipAreaRootPath=[somewhere]/storage/pip
    localNode.gridCacheAreaRootPath=[somewhere]/storage/aip

(Please check if the paths correspond to the created paths on your system before)

If your python installation is not globally visible, for example if your package system does not provide
the newest version, set the path accordingly at

    python.bin=python 
 
### Database

1. Setup Database for accepting incoming connections 
1. Create a new database called contentbroker. 
1. Ask our team for the decrypted password for the irods database user.
1. Create a database user with exactly that password.
1. Get the actual schema [dump](https://raw.github.com/da-nrw/DNSCore/master/ContentBroker/src/main/conf/postgres_schema.dump)
and use it to create your database schema for the database contentbroker.
1. Download a hibernate properties file 
[template](https://raw.github.com/da-nrw/DNSCore/master/ContentBroker/src/main/xml/hibernateCentralDB.cfg.xml.postgres).
1. Save the file as hibernateCentralDB.cfg.xml in a temporary directory on your local box.
1. Edit the following entry to match your hostname and port.
<pre> 
    <property name="connection.url">jdbc:postgresql://hostname:port/contentbroker</property>

### Install the software

1. Download an installer for the newest stable version of the software from the 
[release section](https://github.com/da-nrw/DNSCore/releases) and put it to the a temp dir on your box. The temp dir will be called [tmp] here.
1. Unpack it. You will then find a directory at [tmp]/installation.xyz/ from where you can install your DNSCore.
1. Put hibernate.cfg.xml and config.properties you have prepared during this tutorial to your installer before running install.sh. 
1. Call
<pre>
    ./install.sh [somewhere]/ContentBroker (make sure there is no trailing slash!)
</pre>
1. As feature set, choose (n)ode (TODO: explain different modes)
1. Download a fake glue [script](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/bash/ffmpeg.sh.fake) 
that will ensure you don't have to install ffmpeg for now.
1. Replace [somewhere]/ContentBroker/ffmpeg.sh by the ffmpeg.sh.fake file you downloaded
1. Call
<pre>
    cd [somewhere]/ContentBroker
    ./ContentBroker_start.sh
    tail -f log/contentbroker.log
</pre>
1. You should see your ContentBroker start working. You can verify by looking for ongoing messages that look like "No jobs in queue, nothing to do, shoobidowoo, ..."

You now have a ContentBroker installation which runs but does nothing for now.
What we now need is to configure the database and add some user directories for a test user in order to let the ContentBroker do some work.

### Adding user directories

According to the structure of the different areas in addition to the basic folder layout we need directories
for at least one user to run tests against the system. This first user (or contractor in DNSCore terminology) is typically the TEST user. 
Every  [contractor](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/object_model.md#contractor)
has a unique property called "contractor short name" which we most of the time call csn for short, as it is done in the
processing stages document, where [csn1],[csn2] act as placeholders for the various short names. Note that short names 
are case sensitive. For our system to work with the TEST user, extend the directory structure like this:

    [somewhere]/storage/
                    user/
                         TEST/
                              incoming/
                              outgoing/
                    ingest/
                         TEST/
                    work/
                         TEST/
                    pip/
                        institution/
                               TEST/
                        public/
                               TEST/
                    aip/
                         TEST  


### Configuring the database

Create needed contractors:
<pre>
insert into contractors (id,short_name,admin) values (1,'DEFAULT',0)
insert into contractors (id,short_name,admin) values (2,'PRESENTER',0)
insert into contractors (id,short_name,admin) values (3,'TEST',0)
insert into nodes (id,name,urn_index) values (1,[domainNameOfYourNode],1)
</pre>
### Test your application

Now that you have created the minimum necessary database configuration for the ContentBroker to work with, restart your ContentBroker

    ./ContentBroker_start.sh

Verify that it runs with

    tail -f log/contentbroker.log
    
Download a testpackage from our source code repository, for example 
[this](https://github.com/da-nrw/DNSCore/raw/master/ContentBroker/src/test/resources/manual/BagIt_META1_METS_MODS_2013.tgz) one.
Copy it to your IngestArea

    cp BagIt_META1_METS_MODS_2013.tgz [somewhere]/storage/ingest/TEST/abc1.tgz
    
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
installation following this [tutorial](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/preparing_irods_for_dnscore.md).

The tutorial will show you how to install additional components and features:

* Format conversion
* iRODS Storage layer
* Presentation Repostitory





