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
	
# Der Delta-Mechanismus

## Vorkenntnisse

Zum besseren Verständis des Delta-Mechanismus empfehlen wir zunächst die Auseinanderesetzung mit den DNS-Konzepten [Object](object_model.md#object) 
und [Package](object_model.md#package). 

## Nutzungsszenarien

Das Delta-Feature dient der nachträglichen Ergänzung von bereits eingelieferten SIPs. Nutzen Sie das Feature, um 

1. neue Primärdaten hinzuzufügen,   
1. bereits eingelieferte Primärdaten in anderer Auflösung oder einem neuen Format einzuliefern,   
1. Ihre Metadaten mit neuen oder geänderten Informationen anzureichen oder   
1. die bereits vorgenommennen Einstellungen wie etwa die Zustimmung über Migration oder auch Angaben zu Publikation zu aktualisieren.  

Es ist möglich, beliebig viele Deltas nacheinander einzuliefern. Zu beachten ist, dass die Verarbeitung eines vorangegangen Pakets abgeschlossen sein muss, bevor eine Delta-Einlieferung vorgenommen werden kann.

Bitte beachten Sie unsere Richtlinien für Delta-Einlieferungen, um mögliche Ablehnungen wegen Nicht-Validität zu vermeiden.

## Funktionsweise

Bei Einlieferung eines Delta-[SIP](specification_sip.de.md) werden alle vorherigen Pakete des [Objekts](object_model.md#object) aus dem Langzeitspeicher kopiert und zusammen mit den neu eingelieferten Daten zu einem neuen [AIP](aip_specification.md) zusammengefasst. 

Dabei wird das Gesamtpaket ([Object](object_model.md#object)), d.h. Original mir n Deltas, auf seine Validität geprüft. 

Das [Object](object_model.md#object) behält auch nach Delta-Einlieferungen seine ursprünglieche Object-ID sowie die URN.

Die ausführliche Beschreibung der Nutzungsszenarien Delta-Einlieferung sowie der einfachen und versionierten Entnahme finden Sie [hier](feature_delta_ingest_retrieval.md).

## Richtilinien für Delta-SIPs

Das Metadatenset muss in jedem einzelnen SIP komplett sein und alle Primärdateien des Objekts (Original-SIP mit allen dazugehörigen Deltas) referenzieren. Im Einzelnen bedeutet es, dass
Daraus ergibt sich, dass 
1. die Referenzen in der Metadaten der Ersteinlieferung immer auf die im SIP tatsächlich vorhandenen Primärdateien zeigen müssen.    
2. Die Delta-SIPs durchaus Referenzen in den Metadaten enthalten können, die auf keine Primärdateien des konkreten SIP zeigen, jedoch auf die in den vorhergegangenen Paketen des Objekts.   

Eine Delta-Einlieferung wird im System anhand von zwei Kriterien erkannt. Entweder hat das SIP denselben Originalnamen wie das SIP der Ersteinlieferung, oder der Name entspricht der OPbject-ID der Ersteinlieferung.

## Beschreibung der einzelnen Nutzungsszenarien
### Ergänzung eines Objekts um neue Digitalisate

Beispiel: Object-ID = 1-2014031047417

	myPackage.zip (Ersteinlieferung)
		myPackage/
			bag-info.txt
			bagit.txt
			manifest-md5.txt
			tagmanifest-md5.txt
			data/
				picture1.png
				picture2.png
				premis.xml
				
	myPackage.zip (Delta)
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


Die PNG-Files wurden für im Zuge der Langzeitarchivierung nach TIF konvertiert. Das resultierende DIP enthält alle neusten Filerepräsentationen, nämlich picture1.tif, picture2.tif aus der Ersteinlieferung und picture3.tif aus dem Delta.

Darüber hinaus enthält das DIP die neuste Repräsentation der PREMIS.xml. Die PREMIS.xml aus der Ersteinlieferung wurde mit der aus dem Delta überschrieben. 

Das [Versionierte Retrieval](feature_delta_ingest_retrieval.md#szenario-at-dir-2-versioniertes-retrieval-alle-packages) erlaubt jedoch die Entnahme der älteren Datenbestände. 

### Replacing files

It is also possible to replace existing files with updated versions of these files. As already ingested files should never be deleted in a long term preservation system, the older file is not deleted from the storage device, but replaced *logically* by the newer file: DIPs will only contain the newer file, and only the newer file will be published.

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
				picture2.png
				premis.xml
				
	 1-2014031047417.tar (DIP)
	 	1-2014031047417/
	 		bag-info.txt
			bagit.txt
			manifest-md5.txt
			tagmanifest-md5.txt
			data/
				picture1.tif	(Package 1 version)
				picture2.tif	(Package 2 version)
				premis.xml

If a delta package contains a file named like a file already existing in a previously delivered package, the older file is replaced logically by the newer file. Please note that the file extension is not considered part of the file name in this case: If two files have different file extensions while sharing the same base name (e. g. "document.pdf" and "document.doc"), these files are considered homonymous. Before planning to replace files using the delta feature, please read the paragraph about [substitution rules](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/dip_specification.md#substitution-rules-and-surface-view-of-an-object) carefully.

### Replacing the contract

When building delta packages, it is important to know that only the premis.xml delivered in the newest package is used for determining the contract rights of an object.  
Just like every other SIP, each delta package needs to contain a valid PREMIS file. If you want to keep the settings of the originally delivered package, just copy the rights section from the original premis.xml, or choose the same rights settings again when building the SIP via the SIP-Builder. Alternatively, you can include a PREMIS file with different settings in order to change the contract rights (e. g. add a footer text or allow publication). Please note that the new settings will be valid for the contents of the whole object and not just for the contents of the delta package!  
It is not necessary to add data files to a delta package if you just want to change the contract. A SIP containing just a single premis.xml is a valid SIP (while a SIP containing just other data and no premis.xml is not).

## Deltas and metadata

If the originally delivered package contains a metadata file, an updated version of the file needs to be contained in the delta package. Just like the premis.xml, the new metadata file has to apply to the whole object and not just the contents of the delta package (e. g. the *fileSec* element of a mets.xml file needs to reference the files of the original package *and* the delta package(s)).  
However, this is not true for sidecar files (like [XMP files](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/sip_specification.md#xmp)): You only have to include sidecar files if the corresponding base files are also included in the delta package.

## Deltas and long term preservation

As mentioned above, already ingested data is only deleted under exceptional circumstances. To ensure that delta files never overwrite existing files, each package of an object is stored as a separate AIP. They are combined inside the working directory at certain occasions (e. g. retrieval, transfer to presentation repository), but are kept at different locations on the storage device.  
However, there is one downside to this approach: In case of a fatal database damage, the objects may need to be reconstructed manually out of the different package files. Therefore it is planned to merge the packages of an object to a single AIP at regular intervals (without deleting the original packages). The feature will be implemented in a future version of the ContentBroker.
