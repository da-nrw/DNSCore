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
	                           		  incoming/
	                                           package1.tgz
	                                           package2.tgz
	                                           ...
	                                  outgoing/
	                                           oid1.tar
	                                           oid2.tar
	                                           ...
	                           [csn2]/
	                                  incoming/
	                         	               package1.tgz
	                         		           package2.tgz
	                         		           ...
	                         		  outgoing/
	                         		           oid3.tar
	                         		           oid4.tar
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

The WorkArea is the place where the ContentBroker unpacks the typical OAIS model packages (AIP,SIP) in order to manipulate the contents of the objects. More so, it is the only place on a node where contents of objects can be seen and manipulated, since AIPs and SIPs always relate to data packed in container formats.

The WorkArea is subdivided into two further sections, the WorkSection where processing on the contents of packages is done, and the PipsSections, which is used to exchange between regular nodes of the system and the node of the system hosting the presentation repository. Each of the subssections will be discussed in the following paragraphs.

##### WorkSection of the WorkArea

The actions of the ContentBroker's workflows execute well-defined steps on the material. That means that if a job is a non-transitory (e.g. ending with 0) state, you'll find the corresponding object in a well defined state in the WorkArea.

    [WorkAreaRootFolder]/
    			work/
                           [csn1]/
                                [oid1]
                                [oid2]
                           [csn2]/
                                [oid3]
                                [oid4]
                           ...
                        dips/
                       

Below work there are contractor folders. One for each contractor serviced by this node. Below the contractor folder there are all the objects which are subject to a workflow right now (ingest, retrieval, ...). Typically when debugging objects, the administrator has the oid belonging to the object that is connected to the job. With this object id you find the unpacked object in the WorkArea.

Typically, an unpacked object contains all representations from all packages that are available. This can either a first  representation derived directly from the SIP, or a representation created by conversion processes during ingest or migration, or representations from older AIPs belonging to the same object. You'll find the unpacked objects in this form

    [oid1]/data/
                [rep1+a]
                [rep1+b]
                [rep2+a]
                [rep2+b]
    [oid2]/data/
                [rep1+a]
                ...

which looks almost like an AIP with the exception that it usually contains all the representations belonging to the object. That is the reason why we always should talk of objects instead of packages in the context of the WorkArea.

###### Notes on the dataflow

Now that we've already mentioned different types of sources material (AIPs, SIPs) for the unpacked objects, we should discuss where they come from, in order to understand the dataflow. SIPs come from the IngestArea. When the ContentBroker decides there is sufficient free memory on the WorkArea (search the document [here](administration-interfaces.md) for "IngestGate" to find information about how to configure that), it fetches SIPs from the IngestArea in order to do work on them. It may be then, in case the package is a delta to an existing object, that additional AIPs from the long term resources are fetched. These data and the SIPs data get unpacked before starting the workflow. Workflows as Retrieval or PIPGen start without SIP. Instead they base entirely on the AIPs they fetch from long term resources.

##### PipsSection of the WorkArea

The WorkArea is connected to a subsystem which allows for replication of the working states of those objects in transitory states between nodes of the system. At the moment this is used for moving PIPs around. The subsystem is represented by the [DistributedConversionAdapter.java](../java/de/uzk/hki/da/grid/DistributedConversionAdapter.java). On iRODS based nodes, this system talks to resources which map to the WorkArea file system paths. Have a look at the document [here](administration-interfaces.md) and look for "distributedConversionAdapter" to understand how to set up the subsystem properly.

    [WorkAreaRootFolder]/
    		 dips/
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
                 work/


During the Ingest- and PIPGen- Workflows PIPs are generated on the node responsible for the object. When the PIP generation is finished, the two versions of the DIP (public, institution), get moved to the PipsSection so that the rest of the object can be processed independently. The PipsSection is devided into a public and an institution folder, both of which host different subfolders for the different contractors. The Pips get marked with their object identifier plus the jobs database primary key. 

The presentation repository node gets a job for every PIPs which has been placed at one of the nodes PipsSection. It can then fetch it from there as soon as it has free capacities to do so.

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





