	/*
	  DA-NRW Software Suite | ContentBroker
	  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung, Universität zu Köln
	  Copyright (C) 2014 LVRInfoKom, Landschaftsverband Rheinland
	
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

# Installing iRODS (for DNSCore)

## iRODS as a storage layer

The storage layer is separated of ContentBroker's internal business logic. The interface is composed by the
GridFacade abstract class and its respective implementations, to separate concerns. By use of this inteface the business code can access objects via logical names without knowing of the underlying storage system (which in this case is iRODS).

As ContentBroker has now an extended and comfortable interface for interacting with 
iRODS Servers (federated and single zone based architectures) based on the JARGON interface provided by RENCI (see https://code.renci.org/gf/project/jargon/) and our implematations of GridFacade, DNSCore
is deployed without the need for installed C-microservices for iRODS anymore.

DNScore uses iRODS as storage layer. The reasons why we have choosen iRODS as a storage layer framework were

1. It is open source
2. broadly being used in academic projects at large data scales 
3. being able to connect heterogenous existing hardware systems (act as abstraction layer)
4. "out-of-the-box" capabilities for replication, maintenance and low-level bitstream verification.
5. has a vivid community

The version described here is community iRODS Version (3.X). There are already newer releases, you may consider to update but this not tested with DNSCore.

Several hardware platforms are supported by iRODS "out-of-the-box", but having a standard "mount-point" (unix file system) is always a good start. Tape devices not being able to provide such, may be connected via MSS compound devices and may need special configuration.



## Installation instructions

### Prerequisites


1. You have read the documenation available under www.irods.org (e.g. the read the e-Book "iRODS Primer")
1. You have already experimented with iRODS at a local developer box and you are able to use it.
1. You should be familiar with


    iRODS Cli-commands esp. 
    irepl, ils, iput, irsync, iget
    iadmin

1. You are able to create resources (Please take a look at documentation at www.irods.org how to create iRODS resources). 

### iRODS Installation

Set up a basic iRODS > 3.2 installation. Make sure the installation is installed as ICAT-Enabled. The installer
will ask you for entering certain properties to which we will refer later.

Copy the [danrw.re] (../rules/danrw.re) to server/config/reConfigs/danrw.re
Edit server/config/server.config to load danrw.re

    reRuleSet danrw,core

Restart iRODS server.

### Setting up a node topology

Please note: iRODS can be setup to use a "federation" of iRODS Servers forming a mostly independent "zones" as well as the concept of 
having one Zone with several resource servers. Please refer to the iRODS Documentation about this. 

DNSCore supports both operational modes, depending on your

    grid.implemenation 
    
Parameter in your config.properties. Possible values are so far:

    grid.implementation=fakeGridFacade
    grid.implementation=irodsGridFacade
    grid.implementation=federatedGridFacade

Each Zone needs at least one database (so called ICAT Server). The use of Postgres is encouraged here. 



