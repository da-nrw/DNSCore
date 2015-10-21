#Leistungsmerkmal: SIPs aus verzweigter Ordnerstruktur erstellen (nur METS)

#Beschreibung

## Hintergrund

Der Kunde hat seine Daten in einer verzweigten Ordnerstruktur zusammengetragen und möchte sie in Form von mehreren SIPs in DNS langzeitarchivieren. Dabei sollen nur diejenigen Blätter des Verzeichnisbaums zu SIPs gepackt werden, die eine Metadatendatei des Typs METS enthalten. Als Namen sollen die SIPs die in der jeweligen METS-Datei referenzierte URN erhalten.

Der SIP-Builder überprüft vor der Bildung des SIP die Struktur aller Pakete sowie die darin enthaltenen Metadaten. Bei identifizierten Abweichungen sieht der User entsprechende Fehlermeldungen.

#### Vorbedingung:

* Der User hat den SIP-Builder mit der Build-Nr. >= 1497.

## Szenario AT-BSN-EAD-1:  SIPs aus verzweigter Ordnerstruktur trotz fehlerhaften Referenzen erstellen

#### Kontext:

* [ATSipBuilderCliNested](../test/java/de/uzk/hki/da/at/ATSipBuilderCliNested.java).testNestedStructureIgnoreWrongReferences()

#### Testpaket:   

* [nestedFolders](../test/resources/SIPFactoryTests/nestedFolders)

#### Vorbedingungen

* Siehe Hintergrund.

#### Durchführung:

1. Download des Testpakets
1. Starten des SIP-Builders
1. Auswahl der Option "SIPs aus verzweigter Ordnerstruktur erstellen (nur METS)"
1. Auswahl des Pakets nestedFolders als Quellordner
1. Festlegung des Zielordners
1. Erstellung des SIPs mittels GUI

#### Akkzeptanzkriterien:

1. Der User sieht in der Benutzeroberfläche zwei Fehlermeldung hintereinander

```
Die Metadatendatei /home/polina/Desktop/nestedFolders/testFolder2/testFolder22/testFolder221/export_mets.xml enthält falsche Referenzen.
Folgende Dateien konnten nicht gefunden werden: 
[image/801616.bmp, image/801618.bmp, image/801651.bmp, image/801619.bmp, image/801620.bmp, image/801622.bmp, image/801624.bmp, image/801625.bmp, image/801626.bmp, image/801627.bmp, image/801628.bmp, image/801629.bmp, image/801630.bmp, image/801631.bmp, image/801632.bmp, image/801633.bmp, image/801634.bmp, image/801635.bmp, image/801636.bmp, image/801637.bmp, image/801638.bmp, image/801639.bmp, image/801640.bmp, image/801642.bmp, image/801643.bmp, image/801644.bmp, image/801645.bmp, image/801648.bmp, image/801650.bmp] 
Möchten Sie die SIP-Erstellung dennoch fortsetzen?
```

und

```
Die Metadatendatei /home/polina/Desktop/nestedFolders/testFolder1/testSubfolder12/export_mets.xml enthält falsche Referenzen.
Folgende Dateien konnten nicht gefunden werden: 
[image/258096.tif, image/258097.tif, image/258098.tif, image/258099.tif, image/258102.tif, image/258103.tif, image/258104.tif, image/258105.tif, image/258106.tif, image/258107.tif, image/258108.tif, image/258109.tif, image/258110.tif, image/258111.tif, image/258112.tif, image/258113.tif, image/258114.tif, image/258115.tif, image/258116.tif, image/258117.tif, image/258118.tif, image/258119.tif, image/258120.tif, image/258121.tif, image/258122.tif, image/258123.tif, image/258124.tif, image/258125.tif, image/258126.tif, image/258127.tif, image/258128.tif, image/258129.tif, image/258130.tif, image/258131.tif, image/258132.tif, image/258133.tif, image/258134.tif, image/258135.tif, image/258136.tif, image/258137.tif, image/258138.tif, image/258139.tif, image/258140.tif, image/258141.tif, image/258142.tif, image/258143.tif, image/258144.tif, image/258145.tif, image/258146.tif, image/258147.tif, image/258148.tif, image/258149.tif, image/258150.tif, image/258151.tif, image/258152.tif, image/258153.tif, image/258154.tif, image/258155.tif, image/258156.tif, image/258157.tif, image/258158.tif, image/258159.tif, image/258160.tif, image/258161.tif, image/258162.tif, image/258163.tif, image/258164.tif, image/258165.tif, image/258166.tif, image/258167.tif, image/258168.tif, image/258169.tif, image/258170.tif, image/258171.tif, image/258172.tif, image/258173.tif, image/258174.tif, image/258175.tif, image/258176.tif, image/258177.tif, image/258178.tif, image/258179.tif, image/258180.tif, image/258181.tif, image/258182.tif, image/258183.tif, image/258184.tif, image/258185.tif, image/258186.tif, image/258187.tif, image/258188.tif, image/258189.tif, image/258190.tif, image/258191.tif, image/258192.tif, image/258193.tif, image/258194.tif, image/258195.tif, image/258196.tif, image/258197.tif, image/258198.tif, image/258199.tif, image/258200.tif, image/258201.tif, image/258202.tif, image/258203.tif, image/258204.tif, image/258205.tif, image/258206.tif, image/258207.tif, image/258208.tif, image/258209.tif, image/258210.tif, image/258211.tif, image/258212.tif, image/258213.tif, image/258214.tif, image/258215.tif, image/258216.tif, image/258217.tif, image/258218.tif, image/258219.tif, image/258220.tif] 
Möchten Sie die SIP-Erstellung dennoch fortsetzen?
```


a. Der User antwortet auf beide mit Ja. Der ausgewählte Zielordner enthält die Dateien
```
urn+nbn+de+hbz+6+1-3602.tgz
```
und

``` 
urn+nbn+de+hbz+42.tgz
```

b. Der User antwortet beide Male mit Nein und erhält hintereinander zwei Fehlermeldungen

```
Aus dem Verzeichnis []/nestedFolders/testFolder2/testFolder22/testFolder221 wird kein SIP erstellt
```
und 

```
Aus dem Verzeichnis []/nestedFolders/test1/testSubfolder12 wird kein SIP erstellt
```

Der ausgewählte Zielordner enthält keine Dateien.
