## Administration federated mode

### Introduction

For more separated and independent "nodes" of DNSCore and to avoid overhead of administration at the Master ICAT Zone Server in the integrated
mode, you could decide to run DNSCore in the so called "federated mode". In the federated mode you have several distinct Master ICAT Zones 
forming a Federation in terms of iRODS Servers. 

In this topology your nodes should be able to administer iRODS Master servers and the federation itself
(See iRODS documentation about this: https://irods.sdsc.edu/index.php/Federation_Administration)

Although the iRODS servers are more separated, they share some common infrastructure (Object-DB)



### Prerequisites

1. Running iRODS Server > 3.2 at zoneA ([here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/installation_irods_cb.md))
1. Running iRODS Server > 3.2 at zoneB
1. Running ContentBroker
1. Running Federation between zoneA and zoneB https://irods.sdsc.edu/index.php/Federation_Administration) 
1. Running iRODS Resource Monitoring  https://irods.sdsc.edu/index.php/Resource_Monitoring_System

### Changes needed to ContentBroker

    cb.implementation.grid=federatedGridFacade
    cb.implementation.distributedConversion=irodsFederatedDistributedConversionAdapter
 
 
### Changes needed to iRODS

Create directory for federated items at zoneA

    imkdir /zoneA/federated

Set the rights for federated copies at least to "own" for the federated folders
e.g.

     ichmod -r own rods#zoneB /zoneA/federated/CONTZONEB
     
Please consider the most restrictive permissions you are able to set for this.

You should have a RescGroup "lza" containing your long term storage resource at each node
Please give a full "own" rights to user rods#zoneB recursively to folder /zoneA/pips
   
    ichmod -r own rods#zoneB /zoneA/pips

Please set inherit mode to enabled
 
 	ichmod -r inherit /zoneA/pips
 	
Enable the Resource Monitoring framework for using the load balancing of implemented in IRODS.
Rules could be found at: https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/rules/federation/

Load the federated danrw.re file from ([here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/rules/federation/danrw.re))

Start the Federation service, which could be found ([here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/rules/federation/federate.r))

    irule -F federate.r

### Administer Federation


 