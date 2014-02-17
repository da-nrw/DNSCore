When dealing with data packages in DNSCore, it is necessary to understand how the 
data of an object is structured on the different storage media or, more precisely,
on the different processing areas of a node which runs DNSCore. The easiest way to do so is to
take a look at how a data package makes its way through the system during ingest.

![](https://raw2.github.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/processing_stages.jpg)

First the SIP is uploaded by the user to the UserArea. At this point the SIP is packaged as a container of one of
the accepted container format types. Then the system moves (TODO!) the data to the IngestArea.
The IngestArea is monitored for new packages all the time by the ContentBroker. If the ContentBroker decides
that is has free resources to deal with the SIP, it pulls it from the IngestArea and unpacks it 
onto the WorkArea. There is a lot of processing done there and we will omit the details here. 
After the processing has taken place, the processed data (or part of) get repacked into a new container, conceptually the AIP, again, 
which will then be send to the grid. When sending data to the grid, 
the ContentBroker first puts them to the GridCacheArea and then writes it to the LZAArea from where it gets
distributed geographically for long term preservation.

Given this general overview and having ommitted as much detail as possible for clarity, it is now time to have
a closer look at each of the processing stages and the transitions between them.

#### UserArea
The UserArea is where the partition or file system where the user data arrives at the system. A UserArea
has to be installed on every node which offers ingest services to its users. The UserArea must be a separate partition or
filemount. It MUST not be on the same partition as the WorkArea, GridCacheArea, LZAArea.
Operators of nodes are free to choose the size and type of the media they want to use. But since this is a space users
push their data to and the ContentBroker pulls data from it should have a reasonable amount of space.

TODO organistation of the user area

#### IngestArea

TODO organisation of the ingest area

#### WorkArea
The WorkArea is the one and only place where the ContentBroker manipulates the "contents" of objects. When there is
sufficient memory free on the WorkArea, the ContentBroker fetches new objects from the IngestArea.
#### GridCacheArea

#### LZAArea

## Basic concepts

## Logging

log/contentbroker.log
log/grid.log
log/object-logs/[oid].log

## Controlling


