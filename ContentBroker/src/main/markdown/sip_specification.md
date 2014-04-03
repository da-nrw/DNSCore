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

# SIP Specification

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
        		
Inside a container of one of the supported container formats there is
a single entry which resembles the filename of the container without the extension.
Inside this first level entry you will find exactly five entries. Four of them
are text files which are required by BagIt and then you have one data folder.
Inside the data folder there must be at least one file which is the premis.xml.
The premis.xml, which adheres to a standard described [here](http://www.loc.gov/standards/premis/v2/premis-2-2.pdf) contains some object specific rights settings which will control the publication settings
for the object. A detailed specification of the expected PREMIS elements and the used vocabulary can be found [here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/premis_specification.md).
In addition to the premis.xml the user is free to put any data
of any formats and in any structure (hierarchical ordering in folders) into the 
data folder. 

### Guidelines for structuring your SIPs

Though it is entirely up to you, the user, what to put into your SIP, there are several details
to take into consideration when assembling the contents for a SIP.

#### Naming conventions and deltas

##### Original Name

The name of the packaged SIP without the extension (which would be simply "mySIP" in the example above)
is called original name. Every original name is unique per contractor. This allows the system for 
recognizing several SIPs as belonging to the same logical object. So make sure, if you want
to ingest content into the system, which you would define as an entirely new intellectual entity (the object in
our terms), choose a name which you have not used before (you can check this if you do a quick filtered search via DAWeb). If you want to add contents to the existing object, name the subsequent SIPs with the appropriate original
name and the system will make sure the SIPs get recognized as part of an existing object. For more information
on the delta system, look [here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/the_delta_feature.md).

##### File names

If you take away the extension away from a file name, you have what is considered a "document" by the system.
Documents have to be unique which means you shouldn't place two files with the same document name into a SIP:

    forbidden:
    data/abc.jpg
    data/abc.tif
    
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

#### Publication - Preparation of Metadata


In addition to the basic SIP format there are some metadata formats/structures which are
supported in a special way, optimized for the publication feature of DNSCore.

##### METS

##### EAD

##### EAD / METS

##### XMP

    data/
        premis.xml
        abc1.tif
        abc1.xmp
        abc2.tif
        abc2.xmp
        cde1.jpg
        subfolder/abc3.tif
        subfolder/abc3.xmp
        subfolder/cde1.jpg

The system will recognize the correspondence of files and sidecar-files by matching filenames ignoring
the extension of the file. In the example this would mean that we have three pairs (abc1,abc2,abc3) of
file and sidecar file.

### LIDO

## SIP-Builder 

The [SIP-Builder](https://github.com/da-nrw/SIP-Builder) is a Java tool capable of creating SIPs and collections that adhere to the structure explained above. The tool can be used in two modes:
* In GUI mode, the user is led through a number of consecutive steps in order to determine the data and rights settings. This mode is meant primarily for unexperienced users and users who want to process a rather small amount of data. It can also be used to create the contract rights in a comfortable way.
* In CLI mode, the necessary settings are handed over to the application as command line parameters. This mode is useful for integrating the SIP-Builder into automated processes.

A German manual explaining the steps in GUI mode and all of the possible parameters in CLI mode can be found [here] (https://github.com/da-nrw/SIP-Builder/blob/master/src/manual/SIP-Builder%20Anleitung.pdf?raw=true).

For more information please refer to
* the [Java API documentation](https://da-nrw.github.com/SIP-Builder/apidocs)
* the [Java test documentation](https://da-nrw.github.com/SIP-Builder/testapidocs)

## Collections

**Warning: Requirements for this feature are yet to be collected.**

Developer only documentation can be found in the internal wiki.

