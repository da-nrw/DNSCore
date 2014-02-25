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

* TODO Lieferung

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
The premis.xml, which adheres to a standard described (elsewhere TODO) contains
some object specific rights settings which will control the publication settings
for the object. In addition to the premis.xml the user is free to put any data
of any formats and in any structure (hierarchical ordering in folders) into the 
data folder though some formats and structures are somewhat more supported than
others as we'll see soon (TODO).

### PREMIS - specification

* TODO

### SIPBuilder 

* TODO link to documentation
* TODO link to other artifacts?
* TODO explain SIP builder in this context

### Other 

* TODO mets ist doch nicht mehr unterstützt oder?

## SIP - special formats

In addition to the basic SIP format the are some metadata formats/structures which are
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
