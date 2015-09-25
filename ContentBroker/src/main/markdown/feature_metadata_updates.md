## Feature: Anpassungen der Metadatenformate

Dieser Basistest zeigt, ob über den gesamten Workflow hinweg, die korrekten Metadatananpassungen für die&nbsp;*Präsentation*&nbsp;vorgenommen wurden. Dies ist wichtig, um abschätzen zu können, ob die Metadaten den Anforderungen genügen, um von externen Viewern, im Falle von METS/MODS DFGViewer, angezeigt werden zu können.

#### Kontext

* [https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/2015-01-14_Metadaten_in_DA-NRW.pdf?raw=true]

## Hintergrund: 

gilt für alle Szenarien!

#### Vorbedingungen:

* Der User hat einen Account und ist unter der Rolle "Contractor" eingeloggt in der DA-WEB.
* Der User hat einen Webshare mit Incoming Order, in den er Pakete legen kann. DA-WEB zeigt den Inhalt dieses Ordners in der Maske&nbsp;"Verarbeitung für abgelieferte SIP starten" an.

#### Durchführung:

1. Das jeweilige Tespaket wird im Incoming Order abelegt und die Verarbeitung gestartet "Verarbeitung für abgelieferte SIP starten"
1. Warten auf die Bestätigungsemail für die erfolgreiche Einlieferung.
1. Einsichtnahme in die DAWeb Ansicht "eingelieferte Objekte"

## Szenario AT-ADM-1 METS/MODS

#### Kontext:

* [ATUseCaseIngestMetsMods](../../test/java/de/uzk/hki/da/at/ATUseCaseIngestMetsMods.java).testLZA()
* [ATUseCaseIngestMetsMods](../../test/java/de/uzk/hki/da/at/ATUseCaseIngestMetsMods.java).testPres()

#### Testpaket(e):

* ATUseCaseUpdateMetadataLZA_METS
```` 
  export_mets.xml (METS Datei)
  image/ enthält die referenzierten Metadaten
    801636.bmp
    weitere bmps
````

Auszug aus der export_mets.xml:

```xml
<mets:file MIMETYPE="image/x-ms-bmp" 
  CHECKSUM="03f9e40fb0594c903a2cd22465697624cdedc1d3" CREATED="2011-11-08T17:37:37Z" 
  CHECKSUMTYPE="SHA-1" SIZE="15619556" ID="IMG801636">
  <mets:FLocat xlink:href="image/801636.bmp" LOCTYPE="URL"/>
</mets:file>
```

#### Vorbedingungen:

* s.o.

#### Durchführung:

1. s.o
1. Das Objekt zum Retrieval anfordern, die Bestätigungsmail dafür abwarten.
1. Das DIP aus dem Outgoing-Ordner entnehmen und lokal enpacken.
1. Einsichtnahme in die export_mets.xml Datei nehmen
1. Per Klick auf das Publikations Icon in das Fedora gehen und das öffentliche PIP begutachten
1. Einsichtnahme in den Metadatenstream METS.xml

#### Akzeptanzkriterien:

* Das DIP&nbsp;*enthält*&nbsp;die Datei&nbsp;image/801636.tif
* Das DIP enthält&nbsp;*NICHT*&nbsp;die Datei image/801636.bmp
* In der export_mets.xml wurde die entsprechende Referenz angepasst (Achten auf image/801636.*tif*&nbsp;und*&nbsp;*image/*tiff*):

```xml
<mets:file MIMETYPE="image/tiff" ....... >
  <mets:FLocat xlink:href="image/801636.tif" LOCTYPE="URL"/>
</mets:file>
``` 

* Das Publikations Icon in der Ansicht "eingelieferte Objekte" ist gesetzt. Über den Link kann das öffentliche PIP angesehen werden.
* Das PIP enthält einen Metadatenstream&nbsp;_bee84f142bba34a1036ecc4667b54615.jpg
* Das PIP enthält den Metadatenstream METS.xml
* Der Metadatenstream METS.xml enthält die angepasste Referenz:

```xml 
<mets:file MIMETYPE="image/jpeg" ...... >
  <mets:FLocat xlink:href="http://servername/file/[identifier]/_bee84f142bba34a1036ecc4667b54615.jpg" ... />
</mets:file>
``` 
* Das //identifier// ist der technische Identifier des Objektes.

## Szenario AT-ADM-2 EAD+METS

#### Kontext:

#### Testpaket(e):

* ATUseCaseUpdateMetadataLZA_EAD
** Inhalt:
*** TODO

#### Vorbedingungen:

* s.o

#### Durchführung:

1. s.o
1. Das Objekt zum Retrieval anfordern, die Bestätigungsmail dafür abwarten.
1. Das DIP aus dem Outgoing-Ordner entnehmen und lokal enpacken.
1. Einsichtnahme in die TODO Datei nehmen

#### Akzeptanzkriterien:

TODO

## Szenario AT-ADM-3 XMP

#### Kontext:

* ATUseCaseIngestXMP.testLZA()

#### Testpaket(e):

* ATUseCaseUpdateMetadataLZA_XMP
```
  LVR_ILR_0000008126.xmp (XMP-Sidecar-File)
  LVR_ILR_0000008126.bmp (Primärdatum)
```

#### Vorbedingungen:

* s.o.

#### Durchführung:

1. s.o
1. Das Objekt zum Retrieval anfordern, die Bestätigungsmail dafür abwarten.
1. Das DIP aus dem Outgoing-Ordner entnehmen und lokal enpacken.
1. Die XMP.xml Datei in einem Texteditor öffnen

#### Akzeptanzkriterien:

* Das Paket enthält die Datei&nbsp;XMP.xml
* Das Paket enthält die konvertierte Datei&nbsp;LVR_ILR_0000008126.tif.
* Das Paket enthält NICHT die ursprüngliche Datei LVR_ILR_0000008126.bmp
* Die Datei XMP.xml enthält einen Verweis auf&nbsp;*"LVR_ILR_0000008126.tif"*

## Szenario AT-ADM-4 LIDO

#### Kontext:

* [https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/2015-01-14_Metadaten_in_DA-NRW.pdf?raw=true]
* ATUseCaseIngestLIDO.testLZA()

####

#### Testpaket(e):

ATUseCaseUpdateMetadataLZA_LIDO
```` 
LIDO-Testexport2014-07-04-FML-Auswahl.xml
```` 

#### Vorbedingungen:

* &nbsp;

#### Durchführung:

1. s.o
1. Das Objekt zum Retrieval anfordern, die Bestätigungsmail dafür abwarten.
1. Das DIP aus dem Outgoing-Ordner entnehmen und lokal enpacken.
1. Die Datei&nbsp;LIDO-Testexport2014-07-04-FML-Auswahl.xml&nbsp;in einem Texteditor öffnen

#### Akzeptanzkriterien:

Die Datei TODO
