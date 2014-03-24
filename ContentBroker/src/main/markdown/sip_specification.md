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

## SIP
The basic structure of a SIP as accepted by the system looks like this:

    mySIP.(tgz|zip|tar)
        mySIP/
        	bag-info.txt
        	bagit.txt
        	manifest-md5.txt
        	tagmanifest-md5.txt
        	data/
        		premis.xml
        		someData1.x
        		subfolder/someData.x
        		
Inside a container of one of the supported container formats there is
a single entry which resembles the filename of the container without the extension.
Inside this first level entry you will find exactly five entries. Four of them
are text files which are required by BagIt and then you have one data folder.
Inside the data folder there must be at least one file which is the premis.xml.
The premis.xml, which adheres to a standard described [here](http://www.loc.gov/standards/premis/v2/premis-2-2.pdf) contains some object specific rights settings which will control the publication settings
for the object. A detailed specification of the expected PREMIS elements and the used vocabulary can be found [here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/premis_specification.md).
In addition to the premis.xml the user is free to put any data
of any formats and in any structure (hierarchical ordering in folders) into the 
data folder though some formats and structures are somewhat more supported than
others as we'll see soon (TODO).

## Collection

Multiple SIPs can be bundled to a collection before ingest. Collections have the same structure as SIPs, with two exceptions:

1. The data folder must contain only SIP container files
2. The collection folder itself is not packed to a container file

A collection containing three SIPs might look like this:

	myCollection/
		bag-info.txt
		bagit.txt
		manifest-md5.txt
		tagmanifest-md5.txt
		data/
			mySIP1.(tgz|zip|tar)
			mySIP2.(tgz|zip|tar)
			mySIP3.(tgz|zip|tar)

Collections allow the user to create a loose connection between multiple objects without merging the data into a single object. The status of every object belonging to a collection can be requested for all objects at once by searching for the collection name via DA-WEB.

## SIP-Builder 

The [SIP-Builder](https://github.com/da-nrw/SIP-Builder) is a Java tool capable of creating SIPs and collections that adhere to the structure explained above. The tool can be used in two modes:
* In GUI mode, the user is led through a number of consecutive steps in order to determine the data and rights settings. This mode is meant primarily for unexperienced users and users who want to process a rather small amount of data. It can also be used to create the contract rights in a comfortable way.
* In CLI mode, the necessary settings are handed over to the application as command line parameters. This mode is useful for integrating the SIP-Builder into automated processes.

A German manual explaining the steps in GUI mode and all of the possible parameters in CLI mode can be found [here] (https://github.com/da-nrw/SIP-Builder/blob/master/src/manual/SIP-Builder%20Anleitung.pdf?raw=true).

For more information please refer to
* the [Java API documentation](https://da-nrw.github.com/SIP-Builder/apidocs)
* the [Java test documentation](https://da-nrw.github.com/SIP-Builder/testapidocs)

## SIP - special formats

In addition to the basic SIP format there are some metadata formats/structures which are
supported in a special way, optimized for the publication feature of DNSCore.

### METS

### EAD

### EAD / METS

### XMP

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
