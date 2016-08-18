# Leistungsmerkmal: SIP-Bildung ohne Erstellung von tar-Files

#Beschreibung

## Hintergrund

Der Kunde hat seine Daten in Verzeichnissen zusammengetragen und möchte sie jeweils als Pakete in DNS langzeitarchivieren.
Dafür muss er zunächst mit dem SIP-Builder SIPs erstellen.
Der SIP-Builder überprüft vor der Bildung des SIP die Struktur des Pakets sowie die darin enthaltenen Metadaten. Bei identifizierten Abweichungen sieht der User entsprechende Fehlermeldungen.

Zur Zeit gibt es zwei Kategorien von Fehlermeldungen:
* Abweichungen, die zwangsläufig zu Problemen im ContentBroker führen werden.
* Abweichungen, die unter bestimmten Bedingungen zu einem Fehler im ContentBroker führen werden. Der Fehler wird dem Kunden per Email mitgeteilt und ist in der DAWEB ebenfalls vermerkt.

#### Vorbedingung:

* Der User hat den SIP-Builder mit der Build-Nr. >= 1691.

## Szenario AT-BS-NOTAR-1: Bilden von SIPs ohne Erstellung von tar - Files

#### Kontext:

*[ATSipBuilderNoTar](../test/java/de/uzk/hki/da/at/ATSipBuilderNoTar.java).testMultipleFiles()

#### Testpaket:   

* [ATSipBuilderNoTar](../test/resources/at/ATSipBuilderNoTar/ATSipBuilderNoTarMultiple)

#### Durchführung:

1. Download des Testpakets
1. Erstellen des SIPs mittels Aufruf des SIP-Builders im CLI
1. Folgende Parameter müssen beim Aufruf mitgegeben werden:  
	-source="[Pfad zum Verzeichnis der Quelldateien]"  
    -destination="[Pfad, in dem das erzeugt SIP abgelegt werden soll]"  
 	-noTar : dieser Paramater gibt an, dass kein tar erzeugt werden soll.  
 	         Die Daten bleiben in der Verzeichnisstruktur liegen 

#### Akzeptanzkriterien:

1. Der ausgewählte Zielordner enthält zwei Unterordner:  
```  
 noTar1: enthält folgende Dateien:   
      tagmanifest-md5.txt   
      manifest-md5.txt  
      bagit.txt  
      bag-info.txt  
      data/premis.xml  
      data/NoTar1.bmp    
 noTar2: enthält folgende Dateien:     
      tagmanifest-md5.txt   
      manifest-md5.txt  
      bagit.txt  
      bag-info.txt  
      data/premis.xml  
      data/NoTar2.bmp   
``` 

## Szenario AT-BS-NOTAR-2: SIP-Bildung ohne Erstellung von tar-Files - mit Angabe Zielverzeichnis

#### Kontext:

* [ATSipBuilderNoTar](../test/java/de/uzk/hki/da/at/ATSipBuilderNoTar.java).testDestDir()

#### Testpaket:   

* [ATSipBuilderNoTar](../test/resources/at/ATSipBuilderNoTar/ATSipBuilderNoTarDestDir)

#### Vorbedingungen

* Siehe Hintergrund.

#### Durchführung:

1. Download des Testpakets
1. Erstellen des SIPs mittels Aufruf des SIP-Builders im CLI
1. Folgende Parameter müssen beim Aufruf mitgegeben werden:  
	-source="[Pfad zum Verzeichnis der Quelldateien]"  
    -destination="[Pfad, in dem das erzeugt SIP abgelegt werden soll]"  
 	-noTar : dieser Paramater gibt an, dass kein tar erzeugt werden soll.  
 	         Die Daten bleiben in der Verzeichnisstruktur liegen  
 	-destDir="[Name des zu erstellenden Verzeichnisses]"

#### Akzeptanzkriterien:

1.  Der ausgewählte Zielordner "test" enthält folgende Dateien, wenn man z.B. davon ausgeht, dass  
    im Parameter -destDir="test" steht
```  
  test/data/destDir/tagmanifest-md5.txt   
  test/data/destDir/manifest-md5.txt  
  test/data/destDir/bagit.txt  
  test/data/destDir/bag-info.txt  
  test/data/destDir/data/premis.xml  
  test/data/destDir/data/NoTar.bmp 
``` 

