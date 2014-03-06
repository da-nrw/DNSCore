---
layout: index
---


# DNSCore - Testing the application

## Testing concepts

At the moment there exist two general testing strategies. The first one
is maintained by the development team and is the subject of discussion here. 
Additional testing is done by people not attached to the development team. They do
testing on the basis of the formal project specs like use case documents and "Leistungsbeschreibung".
However, with the exception of the unit tests, these tests and acceptance tests done
by the dev team can intersect. 

There are three types of tests maintained by the dev team:

1. Unit Tests
1. Automated Acceptance Tests
1. Manual Acceptance Tests

While the Unit tests are developement-centric, the automated as well as the manual acceptance tests
test business value. Business value means either value directly visibly to end users but also value 
which is related to the domain of long term archival and understandable und measurable in the terms of this
domain (one example: structure of PREMIS as a success condition of Use Case Ingest). 

The manual acceptance tests are always extended when a new feature gets developed. This is the easiest way
to do because the natural language written specification can act as a design spec while development. When
we think the test can easily be automated and fits its purpose well we will transform the manual test into
an automated acceptance test. In contrast to the unit tests were it is done in many cases there isn't a test first approach here.


### Building realase candidates - testing in context of versioning

An automated build system builds a release candidate by first passing the unit tests and then the
automated acceptance tests. Only when all tests have passed, the release candidate is available for
manual testing. If it passes the manual tests, it can be released. Insofar, there never is a final 
state of the software or a version in the classical way. Every relase candidate which passes the tests
can be realeased at every moment.


## Manual Testing Catalogue

The testing chart for release candidates is available [here]
(https://docs.google.com/spreadsheet/ccc?key=0Ash-t_YI6jRrdGhDTUNveGdLSHlwaVRZaDFRVjY4a3c#gid=0)

Most of the testpackages which are small enough to host in github are put to src/test/resources/manual/

(Our manual testing instructions are available in german only)

### ME1 - Publikationsmetadatentest 1 - METS/MODS

**testpackage:** BagIt_META1_METS_MODS_2013.tgz

**Akzeptanzkriterien**

Nachdem es im Fedora angekommen ist, müssen die folgenden Daten verifiziert werden:
* Wurden DC-Metadaten (z.B. Titel) generiert? Wenn es nicht funktioniert, sieht man die URN als Titel.
* Sind die Pfade in METS angepasst? File Section -> Pfade mit den ersetzem Dateinamen (data.danrw.....hash.jpg)
* Wurden EDM-Metadaten generiert? Sind diese korrekt, d.h. sind im RDF auf den ersten Blick sinnvolle Felder und Werte erkennbar?
* Ist das Objekt im Suchindex vorhanden? D.h. kann es über elasticsearch gefunden werden?
** Aufruf nach dem Muster http://da-nrw-vm6.hki.uni-koeln.de/search/portal_test/aggregation/_search?q=<id oder urn>

### ME2 - Publikationsmetadatentest 2 - EAD

**testpackage:** BagIt_META2_EAD_2012.tgz

**Akzeptanzkriterien**

Nachdem das Paket komplett verarbeitet wurde, müssen die folgenden Daten verifiziert werden:

* Wurden DC-Metadaten (z.B. Titel) generiert? Wenn es nicht funktioniert, sieht man die URN als Titel.
* Sind die Pfade in EAD angepasst? daoloc-Element -> Pfade mit ersetzten Dateinamen (data.danrw.....hash.jpg)
* Wurden EDM-Metadaten generiert? Sind diese korrekt, d.h. sind im RDF auf den ersten Blick sinnvolle Felder und Werte erkennbar?

### ME3 - Publikationsmetadatentest 3 - XMP

**testpackage:** BagIt_META3_XMP_2012.tgz

**Akzeptanzkriterien**

Nachdem es im Fedora angekommen ist, müssen die folgenden Daten verifiziert werden:
* Sind XMP-Metadaten (ein XMP-Datastream) vorhanden? Sind diese korrekt, d.h. sind im RDF auf den ersten Blick sinnvolle Felder und Werte erkennbar?
* Wurden EDM-Metadaten generiert? Sind diese korrekt, d.h. sind im RDF auf den ersten Blick sinnvolle Felder und Werte erkennbar?


### ME4 - Publikationsmetadatentest 4 - LIDO

**testpackage:** BagIt_META4_LIDO_2012.tgz

**Akzeptanzkriterien**

Nachdem es im Fedora angekommen ist, müssen die folgenden Daten verifiziert werden:
* Wurden DC-Metadaten (z.B. Titel) generiert? Wenn es nicht funktioniert, sieht man die URN als Titel.  

### TESTCASE ME5 – Publikationsmetadatentest 5 – XMP

**testpackage:** MultiXMP.tgz

**Akzeptanzkriterien**

Nachdem es im Fedora angekommen ist, müssen die folgenden Daten verifiziert werden:
* Sind XMP-Metadaten (ein XMP-Datastream) vorhanden? Sind diese korrekt, d.h. sind im RDF auf den ersten Blick sinnvolle Felder und Werte erkennbar? Sind Einträge für mehrere Dateien im XMP-Datastream?
* Sind die Pfade in den XMP-Metadaten korrekt? D.h. existieren die Dateien, auf die in rdf:Description/@rdf:about verwiesen wird?
* Wurden EDM-Metadaten generiert? Sind diese korrekt, d.h. sind im RDF auf den ersten Blick sinnvolle Felder und Werte erkennbar?

### ME6 – Publikationsmetadatentest 6 – EAD+METS

**testpackage:** BagIt_META6_EAD_METS_2013.tgz

**Akzeptanzkriterien**

Nachdem es im Fedora angekommen ist, müssen die folgenden Daten verifiziert werden:
* Sind EAD-Metadaten vorhanden?
* Sind die Pfade zu den METS-Dateien in den EAD-Metadaten korrekt? D.h. existieren die Dateien, auf die in den daoloc-Elementen verwiesen wird?
* Sind die METS-Dateien auf die in EAD verwiesen wird vorhanden und korrekt? Existieren die Dateien, auf die in den METS-Dateien verwiesen wird?
* Wurden EDM-Metadaten generiert? Sind diese korrekt, d.h. sind im RDF auf den ersten Blick sinnvolle Felder und Werte erkennbar?


### RI1 - Publikationsrechte 

**testpackages:**

* Als Grundlage dienen drei Pakete
  * das erste ('''DEL3_Bagit_TestPaket1.tgz''') enthält die Datei bild1.bmp. Ohne Restriktionen für alle zugänglich.
  * das zweite ('''DEL3_Bagit_TestPaket2.tgz''') enthält kein Bild. Die Contracteinstellungen sind aber so geändert, dass dabei für die Öffentlichkeit eine niedrigere Ansichts-Auflösung gewählt ist.
  * das dritte ('''DEL3_Bagit_TestPaket3.tgz''') enthält auch kein Bild. Die Rechte sind aber so geändert, dass das Bild überhaupt nicht zur Publikation freigegeben ist.

**Ablauf**

1. Das erste Paket wird eingeliefert.
1. Prüfung des Ergebnisses im PR
1. Das zweite Paket wird entsprechend dem Namen des ersten Pakets benannt eingeliefert.
1. Prüfung des Ergebnisses im PR
1. Das dritte Paket wird entsprechend dem Namen des ersten Pakets benannt eingeliefert.
1. Prüfung des Ergebnisses im PR

**Akzeptanzkriterien**
* zu 2: Objekt im PR vorhanden, ein JPEG mit voller Auflösung (ca. 2000x3000px) vorhanden.
* zu 4: Objekt im PR vorhanden, ein JPEG mit begrenzter Auflösung (ca. 200x300) vorhanden.
* zu 6: Objekt im PR nicht mehr vorhanden

### SC1 - special cases 1 - Lange Dateinamen

In diesem Test sollen auftretende Probleme mit langen Dateinamen oder Pfaden auf dem Weg in das Presentation Repository aufgedeckt werden.

**Ablauf**

1. **LangeDateinamen.tgz** wird eingespielt und landet erfolgreich, wenn auch unter anderem Dateinamen im Presentation Repository
1. **LangerDateipfad.tgz** wird eingespielt und landet erfolgreich, wenn auch unter anderem Pfadnamen (in den Metadaten) im Presentation Repository

**Akzeptanzkriterien**

* Die Pakete sind im PR einsehbar.
* Sie enthalten jeweils 3 JPGs, deren Namen hashcodiert sind.

### MA1 - Massentest- Threading

Dieser Test ist als Stresstest gedacht, der mögliche Probleme mit dem Threading-Konzept aufdecken soll.

**Ablauf**

1. Ein Paket '''testpackage_klein_und_muss_durchlaufen_public.tgz''' wird insgesamt 4 x 20 mal jeweils unter neuem Namen auf der vm6 eingespielt (testbar per mass1.sh).

**Akzeptanzkriterien**

1. Alle Pakete müssen erfolgreich durchlaufen.
1. Niemals während der gesamten Verarbeitung dürfen pro Action-Typ mehr als 3 Threads gleichzeitig laufen (= Status xx2 haben).

### BI1 - big - Großes Paket mit vielen kleinen Dateien

Dieser Test zielt drauf ab, ob ein sehr großes Paket mit vielen Dateien korrekt verarbeitet wird und der ob der dabei benötigte Speicherplatz auch wieder freigegeben wird.

**Ablauf**

1. Den belegten Speicherplatz mit df -h prüfen.
1. Das Testpaket **83gb_package_kleine_dateien.tgz** wird eingespielt und anschließend retrieved.
1. Das Paket hat nach dem Abrufen den Status 960.
1. Der Belegte Speicherplatz wird erneut geprüft, es sollten nicht mehr als etwa 100gb mehr als in Punkt 1. verbraucht werden.
1. Das Paket wird nach einer Stunde vom System gelöscht.
1. Der Speicherplatz wird erneut geprüft.


### BI2 - Großes Paket mit einer großen Datei

Dieser Test prüft ob einzelne sehr große Datei korrekt verarbeitet werden kann.
Bitte vorher überprüfen, ob das Testpaket an der Kommandozeile richtig entpackt werden kann. 
* ''4GB Testdatei''' Hinweis: die alte Datei scheint ein korruptes Bild zu enthalten. Besser ein Video verwneden!!
 wird eingespielt und anschließend retrieved.
 

### BI3 - Rheinländer Paket - Test mit Realdaten

* Es handelt sich um ein EAD-Testpaket, welches auf viele eingebettete Mets-Dateien referenziert.
* Das Testpaket hat unter anderem die Eigenschaften, viele Multipage-Tiffs zu beinhalten.

### PR1 - presentation - Presentation

Dieser Test prüft die Bildmanipulation durch imagemagick.

**Ablauf**

1. **testpackage_watermark.tgz** einpielen.
1. **testpackage_footertext.tgz** einspielen.
1. anschl. Ergebnisse im PR überprüfen

**Akzeptanzkriterien:**

* Die Informationen sind im PR angekommen.
* Das Paket Watermark enthält Bilder mit einem Wasserzeichen über jedem Bild
* Das Paket Footertext enthält Bilder mit jeweils einer Fußzeile. 

### PR2 - presentation Format Restrictions

**testpackage:** BagIt_RIGHT4_divSizeRestr_2012.tgz

* Enthalten sind jeweils eine JPEG, TIF, BMP, PNG, GIF und PDF-Datei.
* Die Originale haben die Auflösung 1791x2048
* Dabei sind für die Öffentlichkeit niedrige Auflösungen bei der Vorschaubegrenzung gewählt.

**Ablauf**

* Testpaket per IngestArea einspielen.
* Paketdurchlauf abwarten
* Unter eingelieferte Objekte dem Link zu den öffentlichen Derivaten im PresRep folgen.

**Akzeptanzkriterien**

* Das Paket soll im PresRep eingesehen werden können.
* Es enthält
** fünf JPGs, die jeweils den Ursprungsformaten zugeordnet werden können.
** ein PDF
** Jede dieser Dateien muss eine "niedrige" Auflösung (maximal 480x360px) haben

### AU1 - Audit Checksummen-Test, hier: Hintergrundprozess konfiguriert und läuft

innerhalb des IntegrationTests wird die Funktionalität des Audits bereits getestet (AuditAction). Deiser Test dient nur der Verifikation des Hintergrundprozesses ("Worker"), der die Objekte durchgeht. Dies könnte auch in einem autom. Tests erfolgen. 

**Vorbedingung** Eine fehlerhafte Datei erzeugen. Nun wird die fehlerhafte Datei mittels des CB integrity check aufgespürt.

**Akzeptanzkriterien:**

* Es wird eine Email an den Auftraggeber (Nodeadmin) gesendet. Diese enthält die Bezeichnung des kaputten Objekts.
* Die ausgabe erfolgt auch im integrity.log unter /Contentbroker/logs/  

### MSG1 - messages

Meldungen der autom. Schnittstelle testen der DA-WEB. (Menüpunkt "Ansteuerung über externe Systeme") 
Jeweils wird die Statusschnittstelle abgefragt. Es ist ein erneuter Login erforderlich.


**Vorbedingung**
* Es gibt ein fertig eingeliefertes, archiviertes Objekt, welches keinen Queue Eintrag mehr hat
* Es gibt ein fertig eingeliefertes, archiviertes Objekt, welches ein Delta oder Retrieval bekommt
* Es gibt ein noch nicht fertig eingeliefertes Objekt, welches im Fehlerstatus ist. 

**Akzeptanzkriterien:**

* Der JSON Response Text gibt den korrekten status wieder: 
* Ein fehlerhaftes Objekt in Bearbeitung ist in Status : in failure (error state)
* Ein Objekt welches archiviert ist, hat den Status archived
* Ein Objekt, zu dem ein Delta angeliefert wird "archived - but in progress"

### RE1 - retrieval

Ein Objekt wird mittels JSON Request zum Retrieval angefragt. Dazu gibt es einen "Browser" im 
(Menüpunkt "Ansteuerung über externe Systeme"), Erstellung von Retrievalanfragen. Es ist ein erneuter Login erforderlich.
Dieser löst JSON POST Aktionen aus, die einen Retrievalrequest erzeugen.  

**Vorbedingung**

Es gibt mindestens ein bereits archiviertes Objekt. Der Identifier des Objekts ist bekannt. 

**Akzeptanzkriterien:**

Es wird auf Anfrage ein Retrievalrequest erzeugt. 

### AD1 - administration

**Vorbedingung**

Login als DA-Admin ist an der DA-Web erfolgreich. Der Punkt "Adminfunktion" erscheint im Hauptmenü.
Nacheinander werden alle Buttons der DA-WEB zur Administration des CB gedrückt. 

**Akzeptanzkriterien:**

Der CB meldet jeweils das Resultat erfolgreich zurück. 
Fehlermeldungen in der Paketverarbeitung erscheinen im unteren Fenster. 

### MS 1 : Microsoft Office Formate

Dieser Test überprüft die autom. Konvertierung eines Office Formats (DOCX) in ein PDF/A 

**Vorbedingung**
Der Webservice auf dediziertem Server muss erreichbar sein und laufen.

**testPackage_doxc.tgz** wird eingespielt und anschl. zurückgeladen.

**Akzeptanzkriterien:**

Der Ingest verläuft erfolgreich, das Paket wird archiviert. 
Das DIP enthält nach Download ein PDF/A der DOCX Datei. 







