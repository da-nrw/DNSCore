# Leistungsmerkmal: Ein SIP aus dem Quellverzeichnis erstellen

#Beschreibung

## Hintergrund

Der Kunde hat seine Daten in einem Verzeichnis zusammengetragen und möchte sie in dieser Form in DNS langzeitarchivieren.
Dafür muss er zunächst mit dem SIP-Builder ein SIP erstellen.
Der SIP-Builder überprüft vor der Bildung des SIP die Struktur des Pakets sowie die darin enthaltenen Metadaten. Bei identifizierten Abweichungen sieht der User entsprechende Fehlermeldungen.

Zur Zeit gibt es zwei Kategorien von Fehlermeldungen:
* Abweichungen, die zwangsläufig zu Problemen im ContentBroker führen werden.
* Abweichungen, die unter bestimmten Bedingungen zu einer fehlerfreien Verarbeitung im ContentBroker führen werden.

#### Vorbedingung:

* Der User hat den SIP-Builder mit der Build-Nr. >= 1497.

## Szenario AT-BSS-EAD-1: Bilden eines einzelnen SIPs mit einer Metadatendatei des Typs EAD

#### Kontext:

* [ATSipBuilderCliEad](../test/java/de/uzk/hki/da/at/ATSipBuilderCliEad.java).testBuildSingleSipCorrectReferences()

#### Testpaket:   

* [ATBuildSingleEadSip](../test/resources/at/ATBuildSingleEadSip)

#### Vorbedingungen

* Siehe Hintergrund.

#### Durchführung:

1. Download des Testpakets
1. Starten des SIP-Builders
1. Auswahl der Option "Ein SIP aus dem Quellverzeichnis erstellen"
1. Auswahl des Pakets ATBuildSingleEadSip als Quellordner
1. Festlegung des Zielordners
1. Erstellung des SIPs mittels GUI

#### Akkzeptanzkriterien:

1. Der ausgewählte Zielordner enthält die Datei ATBuildSingleEadSip.tgz mit folgendem Inhalt
```
  ATBuildSingleEadSip/data/premis.xml
  ATBuildSingleEadSip/data/Picture5.bmp  
  ATBuildSingleEadSip/data/Picture4.bmp  
  ATBuildSingleEadSip/data/Picture3.bmp  
  ATBuildSingleEadSip/data/Picture2.bmp  
  ATBuildSingleEadSip/data/Picture1.bmp  
  ATBuildSingleEadSip/data/mets_2_32048.xml
  ATBuildSingleEadSip/data/mets_2_32047.xml
  ATBuildSingleEadSip/data/mets_2_32046.xml
  ATBuildSingleEadSip/data/mets_2_32045.xml
  ATBuildSingleEadSip/data/mets_2_32044.xml
  ATBuildSingleEadSip/data/EAD_Export.XML
``` 

## Szenario AT-BSS-EAD-2: Keine Erstellung des SIPs aus dem Quellverzeichnis mit einer EAD-Metadatendatei mit falschen Referenzen

#### Kontext:

* [ATSipBuilderCliEad](../test/java/de/uzk/hki/da/at/ATSipBuilderCliEad.java).testBuildSingleSipErrorWrongReferences()

#### Testpaket:   

* [ATBuildSingleEadSipWrongRefError](../test/resources/at/ATBuildSingleEadSipWrongRefErrorCase/ATBuildSingleEadSipWrongRefError)

#### Vorbedingungen

* Siehe Hintergrund.

#### Durchführung:

* Siehe AT-BSS-EAD-1

#### Akkzeptanzkriterien:

1. Der User sieht in der Benutzeroberfläche folgende Fehlermeldung 

```
Aus dem Verzeichnis /home/polina/Desktop/at/ATBuildSingleEadSipWrongRefErrorCase/ATBuildSingleEadSipWrongRefError wird kein SIP erstellt. 
Die Metadatendatei /home/polina/Desktop/at/ATBuildSingleEadSipWrongRefErrorCase/ATBuildSingleEadSipWrongRefError/EAD_Export.XML enthält falsche Referenzen.
Folgende Dateien konnten nicht gefunden werden: 
[../mets_2_32045.xml]
```

2. Der ausgewählte Zielordner enthält keine Datei ATBuildSingleEadSipWrongRefError.tgz.


## Szenario AT-BSS-METS-1: Bilden eines einzelnen SIPs mit einer Metadatendatei des Typs METS

#### Kontext:

* [ATSipBuilderCliMets](../test/java/de/uzk/hki/da/at/ATSipBuilderCliMets.java).testBuildSingleSipCorrectReferences()

#### Testpaket:   

* [ATBuildSingleMetsSip](../test/resources/at/ATBuildSingleMetsSip)

#### Vorbedingungen

* Siehe Hintergrund.

#### Durchführung:

1. Download des Testpakets
1. Starten des SIP-Builders
1. Auswahl der Option "Ein SIP aus dem Quellverzeichnis erstellen"
1. Auswahl des Pakets ATBuildSingleEadSip als Quellordner
1. Festlegung des Zielordners
1. Erstellung des SIPs mittels GUI

#### Akkzeptanzkriterien:

1. Der ausgewählte Zielordner enthält die Datei ATBuildSingleMetsSip.tgz mit folgendem Inhalt
```
  ATBuildSingleMetsSip/data/premis.xml
  ATBuildSingleMetsSip/data/export_mets.xml
  ATBuildSingleEadSip/data/image/801616.bmp  
  ATBuildSingleEadSip/data/image/801618.bmp bis 801620.bmp
  ATBuildSingleEadSip/data/image/801622.bmp
  ATBuildSingleEadSip/data/image/801624.bmp bis 801640.bmp
  ATBuildSingleEadSip/data/image/801642.bmp bis 801645.bmp
  ATBuildSingleEadSip/data/image/801648.bmp
  ATBuildSingleEadSip/data/image/801650.bmp und 801651.bmp
``` 

## Szenario AT-BSS-METS-2: Keine Erstellung des SIPs aus dem Quellverzeichnis mit einer METS-Metadatendatei mit falschen Referenzen

#### Kontext:

* [ATSipBuilderCliMets](../test/java/de/uzk/hki/da/at/ATSipBuilderCliMets.java).testBuildSingleSipErrorWrongReferences()

#### Testpaket:   

* [ATBuildSingleEadSipWrongRefError](../test/resources/at/ATBuildSingleMetsSipWrongRefErrorCase/ATBuildSingleMetsSipWrongReferences)

#### Vorbedingungen

* Siehe Hintergrund.

#### Durchführung:

* Siehe AT-BSS-EAD-1

#### Akkzeptanzkriterien:

1. Der User sieht in der Benutzeroberfläche folgende Fehlermeldung 

```
Die Metadatendatei /home/polina/Desktop/at/ATBuildSingleMetsSipWrongRefErrorCase/ATBuildSingleMetsSipWrongReferences/export_mets.xml enthält falsche Referenzen.
Folgende Dateien konnten nicht gefunden werden: 
[image/801616.bmp, image/801618.bmp] 
Möchten Sie die SIP-Erstellung dennoch fortsetzen?
```

a. Der User antwortet mit nein. Der ausgewählte Zielordner enthält keine Datei ATBuildSingleMetsSipWrongReferences.tgz.  
b. Der User antwortet mit ja. Der ausgewählte Zielordner enthält die Datei ATBuildSingleMetsSipWrongReferences.tgz mit folgendem Inhalt
```
  ATBuildSingleMetsSip/data/premis.xml
  ATBuildSingleMetsSip/data/export_mets.xml
  ATBuildSingleEadSip/data/image/801619.bmp bis 801620.bmp
  ATBuildSingleEadSip/data/image/801622.bmp
  ATBuildSingleEadSip/data/image/801624.bmp bis 801640.bmp
  ATBuildSingleEadSip/data/image/801642.bmp bis 801645.bmp
  ATBuildSingleEadSip/data/image/801648.bmp
  ATBuildSingleEadSip/data/image/801650.bmp und 801651.bmp
``` 
