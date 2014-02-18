When dealing with data packages in DNSCore, it is necessary to understand how the 
object data is structured on the different processing areas of a node which runs DNSCore. 
In order to get a general overview of which processing areas exist on a node it is a good starting point to have a look at
how a data package makes its way through the system during ingest, going from area to area.

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
filemount, together with the IngestArea. It MUST not be on the same partition as the WorkArea, GridCacheArea, LZAArea.
Operators of nodes are free to choose the size and type of the media they want to use. But since this is a space users
push their data to and the ContentBroker pulls data from it should have a reasonable amount of space.

    /mountpoint/[UserAreaFolder]/
	                           [csn1]/
	                                  package1.tgz
	                                  package2.tgz
	                                  ...
	                           [csn2]/
	                         	      package1.tgz
	                         		  package2.tgz
	                         		  ...
	                           ...
#### IngestArea

The IngestArea is the place where DNSCore looks for new packages to ingest, in case it has free resources to do so.
It scans the contractor folders and creates jobs for every new package it encounters. When DNSCore finally selects 
a package for ingest it copies the package to the WorkArea and deletes the package from the ingest area if the copy
procedure succeeded.

    /mountpoint/[IngestAreaFolder]/
	                             [csn1]/
	                                    package1.tgz
	                                    package2.tgz
	                                    ...
	                             [csn2]/
	                             		package1.tgz
	                             		package2.tgz
	                             		...
	                             ...

If you add new contractors to the system, make sure there is a contractor folder named after the contractor short name under
[IngestAreaRootFolder]. To recognize the new contractor folder the ContentBroker has to be restarted.

The reason for separating the UserArea and the IngestArea and at the same time the reason why they must me on the same partition
is that the system needs a means to detect when a transfer of an object is complete. Since there was no specific transfer technology
which provided this feature and has proved usable for end users of the system it has been decided to solve the problem in a different way.
The fastest way move an object from a to b on a file system and from a user perspective is to make use of the "move" command. Using it
for transfering files from UserArea to IngestArea ensures that there are always only complete objects in the IngestArea. This is the purely 
technical part of the solution. The other part affects the user interaction. Since it is the user who knows when the transfer of an object
is finished, he can signal the system via DAWeb that the transfer has been
finished. DAWeb then makes the "move" to to IngestArea. For bigger orders which are executed by the administrator of a node directly it is
the administrator who makes the "move" because he is the one who knows when the transfer is ready to ingest.

#### WorkArea

The WorkArea is the one and only place where the ContentBroker manipulates the "contents" of objects. When there is
sufficient memory free on the WorkArea, the ContentBroker fetches new objects from the IngestArea. 

    [WorkAreaRootFolder]/
                         [csn1]/
                                [oid1]/data/
                                            [rep1+a]
                                            [rep1+b]
                                            [rep2+a]
                                            [rep2+b]
                                [oid2]/data/
                                            [rep1+a]
                                            ...
                                ... 
                         [csn2]/
                                [oid3]/...
                                [oid4]/...
                         ...

The WorkArea has to be under the vault path of the irods cache area or working resource. TODO link.

#### DIPArea

    [DIPAreaRootPath]/
                      public/
                             [csn1]/
                                    [oid1]_[jobid]/
                                                   file1.txt
                                                   file2.txt
                                                   ...
                             [csn2]/
                                    [oid2]_[jobid]/
                                                   file1.txt
                                                   file2.txt
                                                   ...
                             ...
                      institution/
                             [csn1]/
                                    [oid1]_[jobid]/
                                                   file1.txt
                                                   file2.txt
                                                   ...
                             [csn2]/
                                    [oid5]_[jobid]/
                                                   file3.txt
                                                   file4.txt
                                                   ...
                                    [oid6]_[jobid]/
                                                   file5.txt
                                                   file6.txt
                             ...

The DIPArea has to be under the vault path of the irods cache area or working resource. TODO link.


#### GridCacheArea

Before we explain how the packages are sorted in the GridCacheArea we first have to 
make clear our perspective on "the grid". From the standpoint of the application code of
DNSCore the grid abstracts away the complete storage layer. The application code simply acts
as a user of the grid and puts data into it. The data we put into it are as a matter of fact only
tar encoded containers which are packages belonging to objects. When the application code puts data
into the grid it references the containers with path names which begin with a zoneName prefix and continue
with a path like [csn]/[oid]/[oid].pack_[packname].tar, independently where the file is really stored on on the 
file system or storage media. The application code can address objects in the grid only under this grid adress.

    [zoneName]
    or
    [GridCacheAreaRootFolder]/
                             [csn1]/
                                    [oid1]/
                                           [oid1].pack_[packagename].tar
                                    [oid2]/
                                           [oid2].pack_[packagename].tar
                                    ...
                                    
But, when the grid component gets a file to store from the application code it first stores it on the local
file system in the GridCacheArea which is structured like you see in the box above. From here the component
replicates the data across the "real" grid to other boxes and storage media. 

Note that the GridCacheArea is an optional construct of DNSCore which depends heavily on the implementation of 
the GridFacade implementation in use! At the moment all implementations make use of the GridCacheArea, but there 
may be future implementation which will behave different.

Due to the fact that there was a paradigm shift during development from a storage layer focused to a more application layer focused
approach the GridCacheArea at the moment is located at the irods cache resource which more consistently should be on an own resource.
Since this is rather a logical problem and has no practical implementations it will only get refactored later when the concrete 
requirements for new storage architectures come in to play.

#### LZAArea

The LZAArea is a logical construct which abstracts away all concrete details on how the files area actually stored. It only 
knows a logical organisation of files in the following manner.

    [zoneName]/
                             [csn1]/
                                    [oid1]/
                                           [oid1].pack_[packagename].tar
                                    [oid2]/
                                           [oid2].pack_[packagename].tar
                                    ...

The storage media, the number of replications, the order on the file system are up to the system/grid configuration and are in the 
responsibility of the node owner and/or administrator of the respective node.

TODO link to irods documentation





