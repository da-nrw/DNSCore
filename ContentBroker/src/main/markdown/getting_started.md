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

## Prerequisites

* Python > 2.7
* Java 1.6
* ImageMagick
* PostgreSQL

## Preparation

Prepare your installation and storage directories

    [somewhere]/ContentBroker/
    [somewhere]/storage/
                    user/
                    ingest/
                    work/
                    dips/
                    grid/                  

[somewhere] denotes some arbitrary path on your local box.
The ContentBroker directory then is the folder into which we will later let
our installer put the binaries. The storage directory is a directory structure
which DNSCore will use to work with data packages. The subfolders correspond to the various 
[processing stages](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/processing_stages.md).

## Configure the application

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
    localNode.dipAreaRootPath=[somewhere]/storage/dip
    localNode.gridCacheAreaRootPath=[somewhere]/storage/grid

TODO fake ffmpeg.sh

## Prepare the database with minimal configuration

1. Create a new database called contentbroker.
1. Create a database user called cb_usr.
1. Ask our team for a dump of a basic database schema. We'll discussing various solutions to automatize this step, but for the moment asking
   for a dump is the way to go.

1. Download a hibernate properties file 
[template](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/xml/hibernateCentralDB.cfg.xml.inmem).
1. Save the file as hibernateCentralDB.cfg.xml
1. Edit the following entries to match your current database settings.

in hibernateCentralDB.cfg.xml:

    <property name="connection.url">jdbc:hsqldb:mem:QueueDB</property>
    <property name="connection.username">sa</property>
    <property name="connection.password"></property>

## Install and test the software

1. Follow the steps for a fresh installation described 
[here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/installation.md#installation--fresh-installation).
2. Use the hibernate.cfg.xml and config.properties you prepared during this tutorial.
