	/*
	  DA-NRW Software Suite | ContentBroker
	  Copyright (C) 2014 LVRInfoKom
	  Landschaftsverband Rheinland
	
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

## Administration of DNS in iRODS-Federation

### Introduction

There are two modes in which you might run DNScore with iRODS as a storage layer.

First, so called "one-zone-approach" (aka integrated mode) in which you decide to use one ICAT-enabled server and serveral resoure hosts. In this approach resources are managed in a more centralized way. Second, 
in multiple "zone-approach" (aka federated mode) in which the iRODS servers have mostly their own resource management at each site. 

In order to do "load balancing" between nodes, having more nodes then just three - and  to avoid overhead of administration at the Master ICAT Zone Server in the single zone 
mode, you could decide to run DNSCore and iRODS in the so called "federated mode". In the federated mode you have several distinct Master ICAT Zones 
forming a releativley loosley coupled Federation in terms of iRODS Servers, with all the advantages and disadvantages this may impose to you. 

In this topology your nodes admins should be able to administer iRODS Master servers and the federation itself
(See iRODS documentation about this: https://irods.sdsc.edu/index.php/Federation_Administration). 

Most of the effort has to be done once during setup. The daily maintenance is quite easy and does not differ by scales from the single zone administration effort at the nodes.

Although the iRODS servers are more separated, they still share some common infrastructure (e.g. Object-DB, Formats to convert), they form still a "domain" of shared functionalities.

The feature "iRODS as federated storage layer" described below are compatible to a landscape in which "integrated", "one zone" approach has worked before, though the "federated" mode sits "on top" the integrated mode.

But you can't mix both modes in one Domain yet. Each node will become a "zone" with its own Postgres DB. 

### Definitions

The node on which the itmes are stored first is the "primary copy node", or "the responsible node" for that dedicated item. It's supposed to be the primary node for inquires about data, sending deltas to etc.

All other nodes having copies of the stored items are therefore called "secondary copy nodes". They serve as backup in case of data loss or bit courruption at the primary one. 

### Prerequisites

As needed by iRODs Installation you will need PostgreSQL Database support at each node.

1. Running iRODS Server > 3.2 at zoneA ([here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/installation_irods_cb.md))
1. Running iRODS Server > 3.2 at zoneB
1. Running ContentBroker 
1. Running Federation between zoneA and zoneB https://irods.sdsc.edu/index.php/Federation_Administration) 
1. Federated RuleSet dns.re from ([here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/rules/irodsFederatedGridFacade/dns.re)) activated 
in server.config. 

### Changes needed to ContentBroker

Change implementation of grid drivers to:

    cb.implementation.grid=federatedGridFacade
    cb.implementation.distributedConversion=irodsFederatedDistributedConversionAdapter
 
### Changes needed to iRODS

Create directory for federated items of other zones at zoneA

    imkdir /zoneA/federated

Set the rights for federated copies at least to "own" for the federated folders
e.g.

     ichmod -r own rods#zoneB /zoneA/federated/CONTRACTOR_ZONEB
     
Please consider the most restrictive permissions you are able to set for this.

You should have a RescGroup "lza" containing your long term storage resource at each node. The name of the RescGroup must be same for all nodes. 

     atrg lza <yourLongtermResourceName>

Please give a full "own" rights to user rods#zoneB recursively to folder /zoneA/pips . This folder contains all locally produced Presentation Information Package PIP for the time they needed to be replicated to the presentation node. 
   
    ichmod -r own rods#zoneB /zoneA/pips

Please set inherit mode to enabled
 
 	ichmod -r inherit /zoneA/pips
 
Load the federated dns.re file from ([here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/rules/irodsFederatedGridFacade/dns.re))
and install it to folder 

	iRODS/server/config/reConfigs 
	
Make sure you have enabled the reSet in the server.config as described ([here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/installation_irods_cb.md)) and you have tested your still working installation by typing at least an 
"ils" command. 

You should be at least familiar with basic icommands such as :

	irsync 
	imeta
	iadmin mkresc
	ichmod
	ils -A
	ils -L
	itrim
	irule
	
Please refer to the iRODS Docs as well!

Please don't forget do do the itrim on your cache devices after some time at each node!

       itrim -age 2000 -N1 -S name_of_cache_resc_at_zone -r /zoneA/aip
       

### How does it work?

Imagine having an AIP in logical namespace

	/zoneA/aip/TEST/123545/123545_pack.1.tar

While ingesting items with ContentBroker, it will initiate the request to fulfill the federation to all connected and writable other zones. It's the iRODS server's obligation to the copies in loosely coupled manner. All information about this process are being logged in CB's grid.log.
	
	/zoneB/federated/zoneA/aip/TEST/123545/123545_pack.1.tar
	
As you might already noticed: The path beneath folder federated is (logically) same as on zoneA.

### Synchronizing Service

For all objects, which aren't successfully copied by the gridFacade an independent synchronzing service tries to fulfill 
the needed copies. The Synchronizing service works permanently on time based schedule in IRODs, bt it needs to be started once. It tries to copy ("federate") your stored AIP like a "cron" daemon, which 

Start the Synchronizing service, which could be found ([here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/rules/irodsFederatedGridFacade/synchronize.r))

  
    irule -F synchronize.r
    
Take a look at the reLog (Rule engine log file) which could be found at 
	
	iRODs/server/logs/
	
The Federation service should claim: 

	--started Synchronize service---
	--ended Synchronize service---

Also you can control working rules by typing

    iqstat

Please be sure having only one synchronizing job! 

What it does is (same as being fired by the gridFacade as client action): 

1. Takes into account given forbidden nodes settings stored by CB after registry of AIP.
1. Asks all Servers on your Grid for their already stored items. Measured by counting items sizes beneath "aip" folders and on longterm storage resources (which have are be member of resgroup lza).
2. If server isn't available, next server is being taken. 
It takes into account all "own" and already federated items. This should do a load basic balancing between federated zones.
2. Order them ascending, the lowest filled resource first.
3. Trying to copy the items to all reachable nodes (zones) until reached numCopy setting stored by contentbroker - Or if not available, until the given minimal number by the federation service itself is being reached. 
1. Store the original computed checksum to the copied AIP for reference at each zone. 
5. Retry until reached and copied with equality of checksums. (synchronize)

### Administer Synchronizing

Once activated federation service runs, even iRODS Server is restarted.

Start 

	irule -F synchronize.r

The Service asks for some settings after start:

	Default *destResc="lza"  
    New *destResc=
	Default *homezone="zone"
    New *homezone=
	Default *min_copies=3
    New *min_copies=
    	Default *retryOlderThanHours=24
    New *retryOlderThanHours=

destResc : The resource group name, the syncing should go to,
homezone : The own zone name 
min_copies : The minimal copies need if not overruled by Clients (CB client does this in its preservation system)
retryOlderThanHours: Retry all not fulfilled copies older than given hours.

For easier mantaintng these actions you might pass your settings directly to the job!
	
check if Synchronzing Service is running

	iqstat 

Command should list at least the Federation service

Delete Synchronzing service 

	iqstat
	iqdel <ruleId>
	
Check Logfile for errors : reLog. 
If Synchronzing service prints out any error numbers, you might evaluate the error codes to their corresponding textual textual representation with 

e.g.

	ierror -333000

As stated in the AVU section (see belaow) of this document, re-synchronizing is possible manually by doing the equivalent irsync command or (the better way) let the synchronizing service do that for you:

    imeta set -d 1-20141007788.pack_1.tar FEDERATED 0
	
This changes the AVU to "federation not yet performed"

### Audit Infrastructure

To perform Audit (integrity checking) of AIP iRODS each node must at least provide the time based check 
service of federated copies. 

In this Service checkFederatedAip.r ([code of service here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/rules/irodsFederatedGridFacade/checkFederatedAip.r))all federated copies MD5 checksums stored for others at "my zone" are recalculated 
on time basis. 

This is defined to be a "trust" between all servers of the zone.

     irule -F checkFederatedAip.r
     Default *zone="zone"
     New *zone=
	Default *admin="test@test.de"
	New *admin=
	Default *numbersPerRun=5
	New *numbersPerRun=
	Default *trustYears=0
	New *trustYears=

zone: The zone which this service should run-
admin: The Admin which should be infrmed on errors
numbersPerRun: Amount of Items being checked each time the service runs
trustYears: Value in years we trust a checksum before recomputation. 0 means each time recalculate, 0.5 means half a year etc. 

If the responsible node (which means the "primary copy node") is being asked for the integrity of AIP e.g. acIsValid() in dns.re, it does the following:

1. Get The MD5 checksum for local primary copy from the ICAT.
2. Compare stored ICAT value with checksum computed at creation
3. Verify federated Checksums (relies on running service) 
4. Deep Verify (recompute) local Checksum
5. Returns 0 is case of failure
6. Returns 1 if AIP is valid. 

### AVU Metadata of iRODS Objects in DNS (AIP/DataObjects) 

Attribute Value Unit (AVU) Metadata are stored in each ICAT. They could be listed with 

	imeta ls -d 1-20141007788.pack_1.tar
	attribute: FEDERATED
	value: 1
	units: 
	----
	attribute: chksum
	value: 37996b3bedcfabf476a5b9c44b90d45b
	units: 
	----
	attribute: SYNCHRONIZED_TO
	value: lvr,
	units: 
	----
	attribute: replicate_to
	value: lza
	units: 
	----
	attribute: MIN_COPIES
	value: 2
	units: 
	----
	attribute: SYNCHRONIZE_EVENT
	value: 01412675041
	units: 

attribute: FEDERATED
AVU indicates if AIP was synchronized successfully 

You might be able to trigger re-federation if desired, just by setting this to 0

     imeta mod -d 1-20141007788.pack_1.tar FEDERATE 0

**attribute: chksum**
stores originally computed checksum

This value has never to changed!

**attribute: SYNCHRONIZED_TO**
zones with secondary copies

**attribute: replicate_to**
Resc group names the item was replicated to after registry in local zone

**attribute: MIN_COPIES**
Minimal copies to reach (otherwise federation service's default, 3 is being taken)
 
**attribute: SYNCHRONIZE_EVENT**
timestamp of last synchronizing event
