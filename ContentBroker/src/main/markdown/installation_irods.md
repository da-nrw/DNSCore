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

The version described here is "community iRODS Version (3.X)". There are already newer releases (4.X), you may consider to update but this not tested with DNSCore.

Several hardware platforms are supported by iRODS "out-of-the-box", but having a standard "mount-point" (unix file system) is always a good start. Tape devices not being able to provide such, may be connected via MSS compound devices and may need special configuration.



## Installation instructions

### Prerequisites


1. You have read the documenation available under www.irods.org (e.g. the read the e-Book "iRODS Primer")
1. You have already experimented with iRODS at a local developer box and you are able to use it.
1. You should be familiar with


    iRODS Cli-commands esp. 
    irepl, ils, iput, irsync, iget
    iadmin

1. You are able to create resources (Please take a look at documentation at https://irods.org/ how to create iRODS resources). 

### iRODS Installation

Set up a basic iRODS > 3.2 installation. Make sure the installation is installed as ICAT-Enabled. The installer
will ask you for entering certain properties to which we will refer later.

Read [connect CB and iRODS](installation_irods_cb.md) 

Be sure to have at least one dns.re file configured at server.config reSet !!!
This depends on your selected topology

### Setting up a node topology

Please note: iRODS can be setup to use a "federation" of iRODS Servers forming a mostly independent "zones" as well as the concept of 
having one Zone with several resource servers. Please refer to the iRODS Documentation about this. For working with DNSCore in Federations plese read [here](administration_federated.md).

DNS encourages usage of [federation at all nodes](administration_federated.md).

Each Zone needs at least one database (so called ICAT Server). The use of Postgres is encouraged here. 

DNSCore supports both operational modes, depending on your

    cb.grid.implemenation 
    
Parameters in your config.properties. Possible values (one of them) are so far:

    cb.grid.implemenation=fakeGridFacade
    cb.grid.implemenation=irodsGridFacade
    cb.grid.implemenation=federatedGridFacade
    
fakeGridFacade is an implematation only needed in developer testing environments where a fully flavored iRODS Grid isn't available or necessary.
irodsGridFacade is the "one zone, only replications" mode. federatedGridFacade is being chosen, if a fully federated iRODS Datagrid is built up.  
    
    
    cb.implementation.distributedConversion=fakeDistributedConversionAdapter
    cb.implementation.distributedConversion=irodsDistributedConversionAdapter
    cb.implementation.distributedConversion=irodsFederatedDistributedConversionAdapter

The given settings for distributedConversion must be used the same way like the grid implementation. (Don't mix them) 
In case of having an isolated presentation node, you might only have to define the respective  cb.implementation.distributedConversion setting .


## Upgrade iRODS

iRODS installation could be upgraded, if necessary. The following list describes an upgrade from 3.2 to 3.3.1. The upcoming 4.X releases of iRODS are not deeply tested with the code yet.
The upgrade process differs slightly if your are acting as Master Server ad is more complex, due to have the ICAT Databse as well. Described below you'll find describtion applicable to a Master. 

1. stop ContentBroker
2. stop iRODS
3. delete symbolic link to old installation
4. backup your old ICAT DB, if you are master node.
5. move old Installation to iRODS_3.2
6. Download & unpack irods-3.3.1.tgz
7. Create symbolic link ln -s iRODS-3.3.1  iRODS
9. copy old iRODS_old/config/irods.config to iRODS/config
10.Run skript ./irodsupgrade.sh
11. Use existing irods.config yes
12. Start make
13. Test Server with ils
14. In case, you are Master: execute nesessary patches found below server/icat/patches/
15. Copy old rule file from iRODS_old/server/config/reConfigs/dns.re iRODS/server/config/reConfigs/
16. Register rule file in iRODS/server/config/server.config ("ruleSet dns,core")
17. Remember setting addtional Server config: e.g. irodsHost
18. Restart iRODS
19. ils
20. Restart ContentBroker

We're using iRODS since Version 2.5. Up to iRODS 3.3.1. 
Due to a breaking change in the way iRODS organizes its resources beginning in the 4.X releases (from flat resource groups to a resource tree), this might imply some significant changes to the code and the rule set being used. Even upgrade process might be more difficult.  

