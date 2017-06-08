# Leistungsmerkmal: SIP-Bildung ohne Erstellung eines bagit


#Beschreibung

## Hintergrund

Der Kunde hat seine Daten in Verzeichnissen zusammengetragen und möchte sie jeweils als Pakete in DNS langzeitarchivieren.
Dafür muss er zunächst mit dem SIP-Builder SIPs erstellen.
Der SIP-Builder überprüft vor der Bildung des SIP die Struktur des Pakets sowie die darin enthaltenen Metadaten. Bei identifizierten Abweichungen sieht der User entsprechende Fehlermeldungen.

Zur Zeit gibt es zwei Kategorien von Fehlermeldungen:
* Abweichungen, die zwangsläufig zu Problemen im ContentBroker führen werden.
* Abweichungen, die unter bestimmten Bedingungen zu einem Fehler im ContentBroker führen werden. Der Fehler wird dem Kunden per Email mitgeteilt und ist in der DAWEB ebenfalls vermerkt.

#### Vorbedingung:

* Der User hat den SIP-Builder mit der Build-Nr. >= 1889.

## Szenario AT-BS-NOBAGIT: Bilden von SIPs ohne Erstellung eines bagit

#### Kontext:

*[ATSipBuilderNoBagit](../test/java/de/uzk/hki/da/at/ATSipBuilderNoBagit.java).test()

#### Testpaket:   

* [ATSipBuilderNoBagit](../test/resources/at/ATSipBuilderNoBagit/testNoBagit)

#### Durchführung:

1. Download des Testpakets
1. Erstellen des SIPs mittels Aufruf des SIP-Builders im CLI
1. Folgende Parameter müssen beim Aufruf mitgegeben werden:  
	-source="[Pfad zum Verzeichnis der Quelldateien]"  
    -destination="[Pfad, in dem das erzeugt SIP abgelegt werden soll]"  
 	-noTar : dieser Paramater gibt an, dass kein tar erzeugt werden soll. Die Daten bleiben in der Verzeichnisstruktur liegen.  
 	-noBagit: es wird kein Bagit erstellt. Es git nur den Unterordner data mit der premis.xml und den Bilddateien  

#### Akzeptanzkriterien:

1. Der ausgewählte Zielordner enthält einen Unterordner:  
```  
testNoBagit: enthält folgende Dateien:     
      data/premis.xml    
      data/test1.jpg  
      data/test2.jpg  
      data/test3.jpg    
``` 
