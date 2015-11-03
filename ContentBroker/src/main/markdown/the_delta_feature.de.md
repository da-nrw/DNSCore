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

Die ausführliche Beschreibung der Nutzungsszenarien Delta-Einlieferung sowie der [einfachen](feature_delta_ingest_retrieval.md#szenario-at-dir-1-ingest-und-retrieval) und [versionierten](feature_delta_ingest_retrieval.md#szenario-at-dir-2-versioniertes-retrieval-alle-packages) Entnahme finden Sie [hier](feature_delta_ingest_retrieval.md).

## Richtilinien für Delta-SIPs

Das Metadatenset muss in jedem einzelnen SIP komplett sein und alle Primärdateien des Objekts (Original-SIP mit allen dazugehörigen Deltas) referenzieren. Im Einzelnen bedeutet es, dass
Daraus ergibt sich, dass    
1. die Referenzen in der Metadaten der Ersteinlieferung immer auf die im SIP tatsächlich vorhandenen Primärdateien zeigen müssen &       
2. die Delta-SIPs durchaus Referenzen in den Metadaten enthalten können, die auf keine Primärdateien des konkreten SIP zeigen, jedoch auf die in den vorhergegangenen Paketen des Objekts.   

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


Die PNG-Files wurden für im Zuge der Langzeitarchivierung nach TIFF konvertiert. Das resultierende DIP enthält alle neusten Filerepräsentationen, nämlich picture1.tif, picture2.tif aus der Ersteinlieferung und picture3.tif aus dem Delta.

Darüber hinaus enthält das DIP die neuste Repräsentation der PREMIS.xml. Die PREMIS.xml aus der Ersteinlieferung wurde mit der aus dem Delta überschrieben. 

Das [Versionierte Retrieval](feature_delta_ingest_retrieval.md#szenario-at-dir-2-versioniertes-retrieval-alle-packages) erlaubt jedoch die Entnahme der älteren Datenbestände. 

### Ersetzung von Dateien

Es ist möglich, durch Delta-Einlieferung die ursprünglichen Dateien durch neue zu ersetzen. Voraussetzung ist, dass diese denselben Dateinamen tragen. Ältere Datenbestände werden in DNS niemals gelöscht und sind stets über [Versionierte Retrieval](feature_delta_ingest_retrieval.md#szenario-at-dir-2-versioniertes-retrieval-alle-packages) verfügbar. Über das [einfache Retrieval](feature_delta_ingest_retrieval.md#szenario-at-dir-1-ingest-und-retrieval) bekommt man im DIP stets die Summe der neusten Versionen aller Dateien:

Beispiel:

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
				
	myPackage.zip (Delta mit demselben Namen wie die Ersteinlieferung)
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
				picture1.tif	(Package 1 Version)
				picture2.tif	(Package 2 Version)
				premis.xml

Bitte beachten Sie, dass die Dateiendung bei der Ersetzung keine Rolle spielt. Es zählt lediglich der Dateiname. Das bedeutet, dass bei Einlieferung einer Datei "document.pdf" im Delta die zuvor eingelieferte Datei "document.doc" ersetzt wird. Für die korrekte Erzeugung der Delta-Pakete lesen Sie bitte unsere [Richtlinien](specification_dip.md#substitution-rules-and-surface-view-of-an-object).

### Ersetzung der Einstellungen

Jedes SIP, ob Delta oder nicht, muss eine Premis.xml enthalten, in der alle Einstellungen für das Paket, sei es Rechte-, Migrations- oder Publikaitionseinstellungen, festgehalten sind.

Bitte beachten Sie, dass im Fall einer Delta-Lieferung die Einstellungen aus der zuletzt eingelieferten Premis für das Gesamtobjekt gelten. Wenn Sie diese also durch eine Deltaleiferung nicht verändern wollen, müssen Sie daran denken, beim Delta-SIP dieselben Einstellungen vorzunehmen wie bei der Ersteinlieferung. 

## Deltas und Metadaten

Die [Metadaten](specification_metadata.de.md) dienen der Beschreibung und Referenzierung von Primärdaten. Im Fall der Einlieferung von Metadaten des Typs EAD, METS oder LIDO werden die entsprechenden SIPs auf ihr Konsistenz hin überprüft. 
Im Fall Delta gilt, ähnlich wie bei Premis.xml, die zuletzt eingelieferte Metadatendatei für das gesamte Objekt. Das bedeutet, dass sie nicht nur die aktuell mitgelieferten Dateien referenzieren und beschreiben soll, sondern auch alle vorangegangenen. Das bedeutet, dass jeder Dateiname genau ein Mal als Referenz angegeben werden muss.

Für das Metadatenformat XMP gilt, für jedes Digitalisat muss das SIP eine Metadatendatei enthalten, s. [SIP-Spezifikation für XMP](specification_sip.md#xmp). 

## Deltas and long term preservation

As mentioned above, already ingested data is only deleted under exceptional circumstances. To ensure that delta files never overwrite existing files, each package of an object is stored as a separate AIP. They are combined inside the working directory at certain occasions (e. g. retrieval, transfer to presentation repository), but are kept at different locations on the storage device.  
However, there is one downside to this approach: In case of a fatal database damage, the objects may need to be reconstructed manually out of the different package files. Therefore it is planned to merge the packages of an object to a single AIP at regular intervals (without deleting the original packages). The feature will be implemented in a future version of the ContentBroker.
