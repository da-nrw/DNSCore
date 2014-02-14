

While this is the conceptual model, we deal with these types of data packages in a slightly different terminology
which is more technical and related to day to day administration needs. When a user sends a selection of data which he considers
to be a single intelectual entity to the system, it is required that this data is structured in a certain way. 
It must adhere to the bagit standard and must be contained inside a single container of one of three momentarily 
accepted container formats (zip,tgz,tar). At this stage the data corresponds to the SIP in OAIS-terminology.
The system will then unpack and process the data (e.g. doing conversions, adding metadata) and finally pack it into a container again. 
This newly generated container gets replicated across the system on a specified minimum number of nodes to ensure the geographical
distribution criteria of long term preservation is met. At this stage we would speak of the data as AIP. If the data gets
accessed by a user, the data gets loaded back from long term resources, gets unpacked and a new container only with the 
data needed in the actual access operation gets build and send back to the user. Here we have our DIP, finally.

### Processing stages
So, what is of main interest here is how to administrate the lifecycle phase of objects while they get processed
by the ContentBroker, which is 1. in unpacked state most of the time and 2. not on long term resources. We have to 
have a close look how our data looks like at different stages of processing:

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


