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
	
# The delta mechanism

Users who want to use the delta mechanism should be accustomed how the concepts
of [objects](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/object_model.md#object) 
and [packages](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/object_model.md#package) 
relate to each other in the context of the DNSCore software.

## Deltas

Whenever a [SIP](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/sip_specification.md) is ingested into the DA-NRW, the resulting [AIP](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/aip_specification.md) is considered the first [package](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/object_model.md#package) of a newly created [object](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/object_model.md#object). It is possible to add more packages to the object, e. g. packages containing additional files, newer versions of files already ingested or an updated version of the contract rights. These packages are called *delta* packages. Delta packages can be built exactly like other SIPs and are processed by the ContentBroker like any other SIP (in terms of conversion, publication etc.), with one exception: No new identifier or URN will be created for the package; instead, the package is assigned to the already existing object it belongs to and inherits the corresponding identifier and URN.

## Ingest of delta packages

Delta packages doesn't differ from other SIPs in structure and can also be created via the [SIP-Builder](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/sip_specification.md#sip-builder). A package is recognized as a delta package by its filename. There a two possible naming conventions suitable for creating delta packages:

1. The package file has the same name as the first delivered package of the object.
2. The package file is named after the identifier of the object it belongs to.

A delta package should not be ingested before a previously delivered package of the same object is processed successfully by the ContentBroker.

## Use cases
### Adding files

You can add files to an object by simply delivering a delta package which contains the new files.

Example:

	myPackage.zip (First package)
		myPackage/
			bag-info.txt
			bagit.txt
			manifest-md5.txt
			tagmanifest-md5.txt
			data/
				picture1.png
				picture2.png
				premis.xml
				
	myPackage.zip (Second package, named like the first one)
		myPackage/
			bag-info.txt
			bagit.txt
			manifest-md5.txt
			tagmanifest-md5.txt
			data/
				picture3.png
				premis.xml
				
	 1-2014031047417.tar (DIP)
	 	1-2014031047417/
	 		bag-info.txt
			bagit.txt
			manifest-md5.txt
			tagmanifest-md5.txt
			data/
				picture1.tif
				picture2.tif
				picture3.tif
				premis.xml

The three PNG files are converted to TIF files during ingest. The resulting DIP contains the newest (i. e. converted) versions of the files included in package 1 (picture1.tif, picture2.tif) and the newest version of the file included in package 2 (picture3.tif) It also contains the newest PREMIS file available: in this case, the newest PREMIS file is the premis.xml included in the second package.


			

TODO replacing contracts

TODO use cases, replacing and adding files, example use cases

https://wiki1.hbz-nrw.de/display/DANOPEN/Delta-Spezifikation

When working with deltas it is necessary to understand which implication this has. Therefore
users in using the delta feature should read at least the paragraph about 
[substitution rules](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/dip_specification.md#substitution-rules-and-surface-view-of-an-object) 
carefully.
