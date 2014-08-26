	/*
	  DA-NRW Software Suite | ContentBroker
	  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
	  Universität zu Köln
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

# SIP Specification

[German](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/specification_sip.de.md) Version.

This document specifies how a SIP must be structured so that any node running DNSCore can ingest it. Following these specifications, a developer should be able to let existing domain specific software output SIPs suitable for getting ingested into DNSCore nodes. It should even be possible doing it manually. 

However, there is a special tool called "SIP-Builder", which is also part of "DA-NRW" project on github. Its source code can be found [here](https://github.com/da-nrw/SIP-Builder). This tool helps users build SIPs providing a comfortable graphical user interface. Power users can also run the SIP builder in headless mode via command line. 

Users can download the latest precomiled version of the SIP-Builder [here](http://www.danrw.de/?page_id=9).

## SIP - General structure

The basic structure of a SIP as accepted by the system looks like this:

    mySIP.(tgz|zip|tar)
        mySIP/
        	bag-info.txt
        	bagit.txt
        	manifest-md5.txt
        	tagmanifest-md5.txt
        	data/
        		premis.xml
        		someFile1.x
        		subfolder/someFile.x
    
    
### Container formats   

DNSCore supports three container formats. Tar with and without gzip compression and Zip. The file extensions which the system accepts are ".tgz", "tar" and "zip".

### Container name

The name of the container is refered to as container name. It is the full name of the file including the extension. The name of the container without its extension is refered to as original name, which will be described further in one of the following paragraphs.

The container contains exactly a single entry. Its name must be the original name, i.e. the name without the container extension.

### Bagit

For the purpose of making sure the transfer of a SIP from the client system to a DNSCore node has happened without corruption of the files, all SIPs conform to the BagIt standard, which is described [here](http://tools.ietf.org/html/draft-kunze-bagit-06). 

The first level entry with the orig name inside the container contains exactly five entries. Four of them
are text files which are required by BagIt and then you have one data folder. The data folder contains the user data and this is what the part "Guidelines for structuring your SIPs" is all about.

### PREMIS

Inside the data folder there must be at least one file which is the premis.xml.
The premis.xml, which adheres to a standard described [here](http://www.loc.gov/standards/premis/v2/premis-2-2.pdf) contains some object specific rights settings which will control the publication settings
for the object. A detailed specification of the expected PREMIS elements and the used vocabulary can be found [here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/premis_specification.md).

### Original Name

Every original name is unique per contractor. This allows the system for 
recognizing several SIPs as belonging to the same logical object. So make sure, if you want
to ingest content into the system, which you would define as an entirely new intellectual entity (the object in
our terms), choose a name which you have not used before (you can check this if you do a quick filtered search via DAWeb). If you want to add contents to the existing object, name the subsequent SIPs with the appropriate original
name and the system will make sure the SIPs get recognized as part of an existing object. For more information
on the delta system, look [here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/the_delta_feature.md).

### Encoding

File names inside the SIP must be UTF-8 encoded. The file separator must be the unix style slash ("/").


## Guidelines for structuring your SIPs

In addition to the premis.xml the user is free to put any data
of any formats and in any structure (hierarchical ordering in folders) into the 
data folder. 

Though it is entirely up to you, the user, what to put into your SIP, there are several details
to take into consideration when assembling the contents for a SIP.


#### File names


If you take away the extension away from a file name, you have what is considered a "document" by the system.
Documents have to be unique which means you shouldn't place two files with the same document name into a SIP:

    forbidden:
    data/abc.jpg
    data/abc.tif

For more information see [documents](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/object_model.md#document).
    
Typically, with choosing the same base names for different files the users wants to signal a correspondence of
two or more files (e.g. same content but encoded in different formats). 

However, as our system sees it, the document name of both files is [abc] and the system can't distinguish between them when doing conversions or handling deltas. If you really want to have both files inside your SIP, consider doing it as follows:

    better:
    data/jpgs/abc.jpg
    data/tifs/abc.tif

Which leads us to another point: File names and their relative paths below data/ are a combined to a single property
relative path, which has a special meaning to the system. So

    not good:
    data/images/abc.jpg
    data/images/abc.tif
    
again would have the same document name [images/abc] which is based on their respective relative paths

    images/abc.jpg
    images/abc.tif
   
If you need the subfolder structure, do it as follows:

    images/jpg/abc.jpg
    images/tif/abc.tif


#### Formats - Planning for long term preservation

TODO

#### Publication - Preparation of Metadata

In addition to the basic SIP format there are some metadata formats/structures which are
supported in a special way, optimized for the publication feature of DNSCore.

For a specification of the metadata formats accepted by DNSCore, have a look at [this](metadata_specification.md) document.
