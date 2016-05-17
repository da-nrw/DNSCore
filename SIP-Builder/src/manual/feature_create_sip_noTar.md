# Leistungsmerkmal: Ein SIP aus dem Quellverzeichnis erstellen

#Beschreibung

## Hintergrund

Der Kunde hat seine Daten in einem Verzeichnis zusammengetragen und möchte sie in dieser Form in DNS langzeitarchivieren.
Dafür muss er zunächst mit dem SIP-Builder ein SIP erstellen.
Der SIP-Builder überprüft vor der Bildung des SIP die Struktur des Pakets sowie die darin enthaltenen Metadaten. Bei identifizierten Abweichungen sieht der User entsprechende Fehlermeldungen.

Zur Zeit gibt es zwei Kategorien von Fehlermeldungen:
* Abweichungen, die zwangsläufig zu Problemen im ContentBroker führen werden.
* Abweichungen, die unter bestimmten Bedingungen zu einem Fehler im ContentBroker führen werden. Der Fehler wird dem Kunden per Email mitgeteilt und ist in der DAWEB ebenfalls vermerkt.

#### Vorbedingung:

* Der User hat den SIP-Builder mit der Build-Nr. >= XXXX.

## Szenario AT-BS-NOTAR-ONEFILE: Bilden eines einzelnen SIPs ohne Erstellung eines tar - Files

#### Kontext:

* [ATSipBuilderNoTarTest](../test/java/de/uzk/hki/da/at/ATSipBuilderNoTarTest.java).testOneFile()

#### Testpaket:   

* [ATSipBuilderNoTarTest](../test/resources/at/ATSipBuilderNoTar/ATSipBuilderNoTarSingle)

#### Vorbedingungen

* Siehe Hintergrund.

#### Durchführung:

1. Download des Testpakets
1. Erstellen des SIPs mittels Ausfruf des SIP-Builders im CLI
1. Folgende Parameter müssen beim Aufruf mitgegeben werden:  
	-source="[Pfad zum Verzeichnis der Quelldateien]"  
    -destination="[Pfad, in dem das erzeugt SIP abgelegt werden soll]"  
 	-noTar : dieser Paramater gibt an, dass kein tar erzeugt werden soll.  
 	         Die Daten bleiben in der Verzeichnisstruktur liegen 

#### Akzeptanzkriterien:

1. Der ausgewählte Zielordner enthält folgende Dateien
```  
  /data/noTar/tagmanifest-md5.txt   
  /data/noTar/manifest-md5.txt  
  /data/noTar/bagit.txt  
  /data/noTar/bag-info.txt  
  /data/noTar/data/premis.xml  
  /data/noTar/data/NoTar.bmp 
``` 

## Szenario AT-BS-NOTAR-MULT-FILES: Bilden mehrerer SIPs ohne Erstellung eines tar - Files

#### Kontext:

*[ATSipBuilderNoTarTest](../test/java/de/uzk/hki/da/at/ATSipBuilderNoTarTest.java).testMultipleFiles()

#### Testpaket:   

* [ATSipBuilderNoTarTest](../test/resources/at/ATSipBuilderNoTar/ATSipBuilderNoTarMultiple)

#### Vorbedingungen

* Siehe Hintergrund.

#### Durchführung:

1. Download des Testpakets
1. Erstellen des SIPs mittels Ausfruf des SIP-Builders im CLI
1. Folgende Parameter müssen beim Aufruf mitgegeben werden:  
	-source="[Pfad zum Verzeichnis der Quelldateien]"  
    -destination="[Pfad, in dem das erzeugt SIP abgelegt werden soll]"  
 	-noTar : dieser Paramater gibt an, dass kein tar erzeugt werden soll.  
 	         Die Daten bleiben in der Verzeichnisstruktur liegen 

#### Akzeptanzkriterien:

1. Der ausgewählte Zielordner enthält folgende zwei Unteriordner Dateien  
```  
 <b> noTar1</b> : enthält folgende Dateien:   
      tagmanifest-md5.txt   
      manifest-md5.txt  
      bagit.txt  
      bag-info.txt  
      data/premis.xml  
      data/NoTar1.bmp    
 <b>  noTar2 </b>: enthält folgende Dateien:     
      tagmanifest-md5.txt   
      manifest-md5.txt  
      bagit.txt  
      bag-info.txt  
      data/premis.xml  
      data/NoTar2.bmp   
``` 

## Szenario AT-BS-NOTAR-DEST-DIR

#### Kontext:

* [ATSipBuilderNoTarTest](../test/java/de/uzk/hki/da/at/ATSipBuilderNoTarTest.java).testDestDir()

#### Testpaket:   

* [ATSipBuilderNoTarTest](../test/resources/at/ATSipBuilderNoTar/ATSipBuilderNoTarDestDir)

#### Vorbedingungen

* Siehe Hintergrund.

#### Durchführung:

1. Download des Testpakets
1. Erstellen des SIPs mittels Ausfruf des SIP-Builders im CLI
1. Folgende Parameter müssen beim Aufruf mitgegeben werden:  
	-source="[Pfad zum Verzeichnis der Quelldateien]"  
    -destination="[Pfad, in dem das erzeugt SIP abgelegt werden soll]"  
 	-noTar : dieser Paramater gibt an, dass kein tar erzeugt werden soll.  
 	         Die Daten bleiben in der Verzeichnisstruktur liegen  
 	-destDir="[Name des zu erstellenenden Verzeichnisses]"

#### Akzeptanzkriterien:

1.  Der ausgewählte Zielordner enthält folgende Dateien, wenn man z.B. davon ausgeht, dass  
    im Parameter -destDir="test" steht
```  
  test/data/destDir/tagmanifest-md5.txt   
  test/data/destDir/manifest-md5.txt  
  test/data/destDir/bagit.txt  
  test/data/destDir/bag-info.txt  
  test/data/destDir/data/premis.xml  
  test/data/destDir/data/NoTar.bmp 
``` 

