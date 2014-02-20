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

A second [tutorial](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/installation.md) then describes
how to convert the existing DNSCore into a full-fledged installation including the iRODS storage layer.

## Prerequisites

* Python > 2.7
* Oracle Java 1.6 
* ImageMagick > 6.7.8-10
* PostgreSQL > 9.0

## Installation directories

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

[somewhere] denotes some arbitrary path on your local box.
The ContentBroker directory then is the folder into which we will later let
our installer put the binaries. The storage directory is a directory structure
which DNSCore will use to work with data packages. 

The subfolders correspond to the various 
[processing stages](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/processing_stages.md).
According to the structure of the different areas in addition to the basic folder layout we need directories
for at least one user to run tests against the system. This first user is typically the TEST user. Every user (TODO link)
has a unique property called "contractor short name" which we most of the time call csn for short, as it is done in the
processing stages document, where [csn1],[csn2] act as placeholders for the various short names. Note that short names 
are case sensitive. For our system to work with the TESTS user, extend the directory structure like this:

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

## Database

1. Create a new database called contentbroker.
1. Create a database user called cb_usr.
1. Ask our team for a dump of a basic database schema. We'll discussing various solutions to automatize this step, 
but for the moment asking for a dump is the way to go.

## Application configuration

### config.properties

1. Get a properties file 
[template](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/conf/config.properties.dev). Note that this document
can change from time to time and therefore is bound to a specific version of DNSCore. If in doubt you can get a config which fits your
version in the appropriate source code branch of the release you want to install.
1. Save the file as config.properties.
1. Replace CONTENTBROKER_ROOT by [somewhere].

in config.properties:    
    
    localNode.userAreaRootPath=[somewhere]/storage/user
    localNode.ingestAreaRootPath=[somewhere]/storage/ingest
    localNode.workAreaRootPath=[somewhere]/storage/work
    localNode.dipAreaRootPath=[somewhere]/storage/pip
    localNode.gridCacheAreaRootPath=[somewhere]/storage/aip

If your python installation is not globally visible, for example if your package system does not provide
the newest version, set the path accordingly at

    python.bin=python

### hibernate.cfg.xml

1. Download a hibernate properties file 
[template](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/xml/hibernateCentralDB.cfg.xml.inmem).
1. Save the file as hibernateCentralDB.cfg.xml
1. Edit the following entries to match your current database settings.

in hibernateCentralDB.cfg.xml:

    <property name="connection.url">jdbc:hsqldb:mem:QueueDB</property>
    <property name="connection.username">sa</property>
    <property name="connection.password"></property>

### ffmpeg.sh

1. Download a fake glue [script](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/bash/ffmpeg.sh.fake) 
that will ensure you don't have to install ffmpeg for now.
2. Rename it to ffmpeg.sh

## Install and test the software

1. Follow the steps for a fresh installation described 
[here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/installation.md#installation--fresh-installation).

**Important** Before you run the installer, make sure you put hibernate.cfg.xml, config.properties and ffmpeg.sh
 you prepared during this tutorial to your installer before running install.sh. You will have to overwrite the existing
 ffmpeg.sh.

If your DNSCore is up'n'running, you are free to play around with it or convert your existing installation into a full-fledged
installation including the iRODS storage layer following this [tutorial](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/full_fledged_installation.md).
