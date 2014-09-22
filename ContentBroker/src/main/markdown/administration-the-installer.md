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

# Installing DNSCore

## Installation

1. Download an installer for the newest stable version of the software from the 
[release section](https://github.com/da-nrw/DNSCore/releases) and put it to the a temp dir on your box. The temp dir will be called [tmp] here.
1. Unpack it. You will then find a directory at [tmp]/installation.xyz/ from where you can install your DNSCore.
1. The installer requires there is a folder where the ContentBroker can be installed into. It can be either an empty folder for a first installation or 
the folder of an existing installation which gets updated then. We will refer to this installation directory as [ContentBroker] in this
document.
1. Depending on your type of installation read the appropriate paragraph below.

## Prerequisites

* database
* python > 2.7

## Installation / Fresh Installation

* Make sure
  * [ContentBroker] exists. If not, create [ContentBroker] now!
  * [tmp]/config.properties exists (if not, look 
  [here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/getting_started.md))
  * [tmp]/hibernateCentralDB.cfg.xml exists (if not, 
  look [here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/getting_started.md))

## Installation

    cd [ContentBroker]
    ./install.sh [ContentBroker]

Choose your 
[feature set](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/system_configuration.md).

## Starting the ContentBroker

    cd [ContentBroker]
    ./ContentBroker_start.sh
    
1. Watch if the ContentBroker comes up with tail -f log/contentbroker.log
1. If everything goes well, you will see him greedily searching for jobs soon.

## Test the ContentBroker

1. Test the software with a testpackage.
  1. Ingest a package
  1. Retrieve a package
  
