# Fehleranalyse & -bereinigung

In bestimmten Fällen kann es zu einzelnen Paketfehlern kommen, die sich durch einen konkreten Fehlerstatus zeigen.
In besonderen Ausahmesituationen kann es passieren, dass das System scheinbar "hängt" und nicht den erwarteten Durchsatz zeigt.
Starten Sie in diesem Zusammenhang nicht einfach "neu" - bitte analysieren Sie zunächst das Fehlerbild. 

**Überlick gewinnen**

Einsicht in die [DA-Web Adminoberfläche](../../../../DAWeb/doc/contentBroker_administration.md), Anmeldung als Knotenadmin. Dort laufen alle Systemmeldungen auf.

## Das ganze System "hängt"

Bitte untersuchen Sie, ob die folgenden Bedingungen zutreffen

1. Zuviele 2er Actions: Das System hat eine Konfiguration, nicht zu viele Actions des gleichen Typs zu verarbeiten. Üblicherweise sind dies max. drei des gleichen Typs.
2. Der CB läuft grundsätzlich. Prüfen Sie ob der CB noch Logmeldungen schreibt.
3. Noch genügend Speicherplatz auf WorkingArea? Üblicherweise stoppt der CB seine Verabreitung wenn nicht ausreichend Speicherplatz frei ist. Den Wert dafür legt der Knotenadmin fest siehe beans.xml.
4. Diagnostics läuft?

### Diagnostics durchführen

Diagnostics Modus in ContentBroker - Smoke test ist auszühren
Bei Auftreten von massenhaften Fehlern oder gar keiner erkennbaren Verabreitung in der Paketverarbeitung sollte immer zuerst der sogenannte Smoke Test durchgeführt werden.

```bash
cd Contentbroker
java -jar ContentBroker.jar diagnostics
```

Der Test überprüft eine Reihe von Verbindungen, z.b. Datenbank-Verbindung, iRODS, Fedora usw.

Erst wenn der Test positiv ausfällt und der Fehlerstatus in der Paketverarbeitung immer noch bestehen bleibt , macht es Sinn, die Fehlersuche bei einzelnen Paketen fortzusetzen.

## Einzelne Pakete hängen

Die Datenverarbeitung in DNSCore ist in kleine logische in sich abgeschlossene Einheiten – Actions – unterteilt. Jeder Workflow, ob Einlieferung ([ingest] (https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/resources/META-INF/beans-workflow.ingest.xml)), das Wiederabrufen ([retrieval] (https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/resources/META-INF/beans-workflow.retrieval.xml)) oder Präsentation ([presentation] (https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/resources/META-INF/beans-workflow.presentation.xml)) besteht aus einer festgelegten Abfolge verschiedener Actions.

### Action & Status

Abhängig von der jeweiligen Aufgabe werden in einer Action unterschiedliche Änderungen am Objekt vorgenommen. Dabei wird das Stadium der Verarbeitung eines Objekts im Bearbeitungsstatus ausgedrückt. Die Angabe des Bearbeitungsstatus eines jeden eingelieferten Pakets befindet sich in der DAWEB in der Spalte „Status“. Anhand des Status lässt sich zu jeder Zeit sowohl die aktuell ausgeführte Action als auch der Zustand der Verarbeitung innerhalb der Action ablesen.

Für jede Action wurde ein Anfangs- sowie ein End-Status definiert. Der Anfangs-Status ist konstant, der End-Status kann je nach Verlauf der Action (erfolgreich oder nicht erfolgreich) variieren. Im Fall einer erfolgreichen Verarbeitung des Objekts (durchlaufen des gesamten Workflows der Action) endet die Action mit der Überführung des Objekts in den definierten End-Status. Im Falle eines Fehlers bleibt das Objekt in einem (Anfangsstatus <) Fehlerstatus (> Endstatus). Der Fehlerstatus variiert je nach Fehlerart.

### Definierte Status

Der Status ist definiert als eine dreistellige dezimale Zahl. Dabei kodieren die ersten beiden Ziffern (von links nach rechts) zu jeder Zeit die aktuell für die Verarbeitung der Daten eingesetzte Action. Die dritte Ziffer kennzeichnet den Verlauf der Bearbeitung innerhalb der jeweiligen Action.

Die Zuordnung der Status zu den Actions kann jederzeit anhand der ersten beiden Ziffern sowie der oben angeführten beans nachvollzogen werden. Die Interpretation der letzten Ziffer des Status wird im folgenden erläutert.

|Code|Kurztext|Beschreibung|Aktion
|---|---|---|---|
|xx0|WAITING| Die Null am Ende des Status bedeutet, dass das Objekt sich in einem konsistenten, gemäß der DNS-Spezifikation wohlgeformten Zustand befindet und aktuell von keiner Action verarbeitet wird. Das Objekt wartet darauf, von einer passenden Action (Status = Anfangsstatus der Action) abgeholt zu werden.| |
|xx1|ERROR_PROPERLY_HANDLED| Jeder Status, der mit einer Eins endet, kennzeichnet einen Fehler in der Verarbeitung. Desweiteren bedeutet die Eins, dass das Objekt in einen konsistenten Zustand (xx1-1) zurückgeführt werden konnte. | Der Administrator kann das Objekt zurücksetzen mittels Klick auf den Button "Zurücksetzen" 
|xx2 | WORKING| Die Zwei am Ende bedeutet, dass das Objekt gerade von der aktuell aktiven Action bearbeitet wird. Je nach Größe und Komplexität des Pakets kann dieser Prozess einige Zeit dauern. Ob die Action tatsächlich arbeitet, kann anhand fortlaufender Logmeldungen im Objekt-Log-File nachgesehen werden.| Sollten Pakete ungewöhnlich lange im Verarbeitungsstatus verbleiben ist eine Analyse sinnvoll|
| xx3 | ERROR_BAD_ROLLBACK | Die drei am Ende bedeutet, dass ein Rollback nicht durchgeführt werden konnte, entweder, weil er nicht implementiert ist, oder weil ein Fehler während der Durchführung des Rollbacks aufgetreten ist. | Zwischen 123 und 323 kann der Administrator das Objekt per Button "Gesamten Workflow zurücksetzen"|
| xx4 | USER_ERROR | Die Vier am Ende des Staus bedeutet einen Userfehler. Der User bekommt in diesem Fall eine Email mit der entsprechenden Exception aus dem Object-Logfile. Da dies bedeutet, dass die Eingangsdaten fehlerhaft sind und vom Einliefernden berichtigt und neu eingespielt werden müssen. Es wird kein Rollback durchgeführt. | Daher muss der Administrator zuvor das Objekt löschen. Dazu gibt es den "Objekt löschen"-Button.|
|xx5 | ERROR_MODEL_INCONSISTENT | Eine fünf am Ende bedeutet, dass ein kritischer Fehler bezüglich des Datenmodells aufgetreten ist. Dies kann mit der Verknüpfung zwischen Actions, Jobs, Usern und Objekten bzw. deren Eigenschaften zusammenhängen. Im Falle solcher Fehler bitten wir Nutzer der Software, sich direkt an die Entwickler zu wenden, da diese Kategorie von Fehlern vergleichsweise selten auftritt und genauster Analyse bedarf. | Entwcikler benachrichtigen. Im Normalfall kann die Inkonsistenz datenbankseitig bereinigt werden und per "Zurücksetzen"- bzw. "Gesamten Workflow zurücksetzen"- Button zurückgesetzt werden.|
|xx6 | ERROR_PRECONDITIONS_NOT_MET | Eine sechs am Ende bedeutet, dass die Eingangsbedingungen für die Bearbeitung eines Paketes in einem bestimmten Status nicht gegeben sind. Dass heisst, dass die dem Status entsprechende Action das Paket nicht so vorfindet, wie sie es benötigt, um es ordnungsgemäß verarbeiten zu können. | Das Paket kann in  solch einem Fall lediglich gelöscht werden durch  Klick auf den "Objekt Löschen"-Button bzw. zurückgerollt durch den Button "Gesamten Workflow zurücksetzen." Solcherlei Fehler können z.B. durch fehlerhaft implementierte Rollbacks entstehen. Im Falle eines solchen Fehlers sollten die Entwickler kontaktiert werden.|
|xx7 | ERROR_BAD_CONFIGURATION | Konfigurationsfehler. Sollte nur während der Entwicklung oder Einrichtungsphase eines Systems auftreten. Ein End-To-End Test eines Paketes auf einem Knoten während der Einrichtungsphase wird alle potentiellen 7er Status aufdecken.| Nach Behebung des Fehlers kann mittels des Zurücksetzen Buttons das Paket wieder aufnehmen.
|xx8| UP_TO_ROLLBACK | Actions, welche sich durch einen jähen Abruch des ContentBroker in undefinierten Zuständen befinden, können vom Gesamtsystem-Administrator manuell mit einer abschließenden 8 versehen werden, um dem System zu signalisieren, dass der Rollback nachgeholt werden muss. Nach dem Rollback wird, falls dieser erfolgreich ist, die Action automatisch zurück in der 0er Status gesetzt. D.h. dass sie sich in der Warteschlange zur Bearbeitung befindet.||

## Genaue Beschreibung aller Job-Status

Die DA-Web zeigt immer dort, wo eine entsprechende Aktion zulässig und möglich ist, dem Knotenadmin die möglichen Optionen an. 

**Die DA-Web Adminmaske ist einer Rücksetzung über die Queue-Tabelle vorzuziehen!**

Grundsätzlich sind Status <=400 immer im Gesamten rücksetzbar. Bei Status >440 nur in den vorherigen 0 er Status. Eine Gesamtrücksetzung ist bei >=400 verboten, weil die Objekte schon archiviert sind. 

**Jede andere Arbeitsweise gefährdet die Archivierung**

|Status| Aktion | Beschreibung |Fehlerbehandlung|
|---|---|---|---|
110 |IngestUnpackAction | Auspacken & Vollständigkeitstests | Rücksetzung auf 110 |
120 |IngestRestructureObjectAction |Objekt- oder Deltaerkennung, Typerkennung | Rücksetzung auf 120 |
130 |IngestValidateMetadataAction |	Validierung der Metadaten | Rücksetzung auf 600, Löschung 800 |
140 |IngestScanAction | Formaterkennung |  Rücksetzung auf 600, Löschung auf 800
150 |RegisterURNAction | Register URN in DNS |	Rücksetzung auf 600, Löschung auf 800
230 |IngestConvertAction | LZA Konvertierung | 	Rücksetzung auf 600, Löschung auf 800
250 |IngestMetadataUpdateAction | Update der Metadaten in B Repräsentation für LZA | Rücksetzung auf 600, Löschung auf 800|
260 |IngestCheckFormatAction|	Überprüfung der LZA Konvertierung| Rücksetzung auf 600, Löschung auf 800 |
270 |IngestCreatePremisAction|	Bearbeitung der PREMIS-Datei| Rücksetzung auf 600, Löschung auf 800 |
310 |IngestScanForPresentationAction | Formaterkennung für Präsentation auf Basis der LZA Formate| Rücksetzung auf 600, Löschung auf 800
320 |IngestConvertForPresentationAction| Bildung der PIPs (Präsentationsderivate) |  Rücksetzung auf 600, Löschung auf 800 |
330 |IngestPreProcessForPresentationAction | Verschieben der PIPs | Rücksetzung auf 600, Löschung auf 800
340 |IngestShortenFilenamesAction | Kürzung der PIP Dateinamen | Rücksetzung auf 600, Löschung auf 800
350 |IngestPreUpdateMetadataAction | Update der Metadaten nach PIP Erstellung| Rücksetzung auf 600, Löschung auf 800
360 |IngestPrepareSendToPresenterAction |Anmeldung der PIP bei IRODS zur Übertragung ans Pres. Repository | Rücksetzung auf 600, Löschung auf 800
370 |IngestBuildAIPAction |AIP Erstellung| Rücksetzung auf 370 oder 600 oder 800 (Löschung)
380 |IngestTarAction|	AIP Erstellung als TAR-Archiv| Rücksetzung auf 380 oder 600 oder 800 (Löschung)
400 |ArchiveReplicationAction |	Ablage auf LZA Medien und Replikation | Grid.log  object.log prüfen, iRODS/server/log/rodsLog **NICHT zurückstellen!** **NICHT löSCHEN**
 440|ArchiveReplicationCheckAction | Prüfung der Replikationen |  Grid.log, object.log prüfen, ggfs. rodsLog Gefahrlos auf 440 zurückstellbar ,**NICHT zurückstellen!** **NICHT löSCHEN**
540|FetchPIPsAction|Replikation der PIP an den Presentation Repository Knoten| Bitte auf 540 zurückstellen **NICHT 600!** **NICHT löSCHEN**
550|SendToPresenterAction|	Einspielung der PIP in das Presentation Repository | Bitte auf 550 zurückstellen **NICHT 600!** **NICHT löSCHEN**
560|CreateEDMAction| EDM Metadaten-Erstellung| Bitte auf 550 zurückstellen **NICHT 600!** **NICHT löSCHEN**
570|IndexESAction|Indizierung im Elasticsearch Suchindex | Bitte auf 550 zurückstellen **NICHT 600!** **NICHT löSCHEN**
580|FriendshipConversionAction|	Konvertierung auf anderem Knoten | Bitte auf 580 zurückstellen **NICHT 600!** **NICHT löSCHEN**|
600|RestartIngestWorkflowAction| Zurücksetzung des Ingestworkflows | Bitte auf 600 zurückstellen| 
700|PIPGenObjectToWorkareaAction| Übertragung von AIP an das Knotenarbeitsverzeichnis| Bitte auf 700 zurückstellen|
710|PIPGenScanForPresentationAction | Scannen der Präsentationsformate| Bitte auf 710 zurückstellen  |
720|PIPGenConvertForPrestationAction| Bildung der PIPs (Präsentationsderivate)| Bitte auf 720 zurückstellen |
730|PIPGenPreProcessForPresentationAction| Verschieben der PIPs | Bitte auf 730 zurückstellen |
740|PIPGenShortenFilenamesAction|Kürzung der PIP Dateinamen| Bitte auf 740 zurückstellen |
750|PIPGenPreUpdateMetadataAction|Update der Metadaten nach PIP Erstellung| Bitte auf 750 zurückstellen |
760|PIPGenPrepareSendToPresenterAction|Anmeldung der PIP zur Übertragung ans Pres. Repository| Bitte auf 760 zurückstellen |
770|PIPGenCleanWorkareaAction|Säuberung des Arbeitsverzeichnisses| Bitte auf 770 zurückstellen |
800|DeleteSIPPackageAction|Löschung des SIP vom Arbeitsverzeichnis | Bitte auf 800 zurückstellen |
900|RetrievalObjectToWorkAreaAction|Auslesen eines AIP von den LZA Medien| Bitte auf 900 zurückstellen oder Löschen aus Queue|
910|RetrievalAction|	Bildung des DIP, Übertragung an das Ausgabeverzeichnis des Contractor|  Bitte auf 900 zurückstellen oder Löschen aus Queue
950|RetrievalDeliveredDIPAction|	Warten auf Abholung durch Contractor| Löschen aus Queue |
5000|AuditAction|	Überprüfung des AIP||

# Aufräumen



## Der "Objekt Löschen"-Button. Automatisiertes Löschen von Paketen. (Status 800)

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/Delete_button.PNG)

Das Betätigen des Buttons vom Admin führt zur Löschung des Objekts sowohl aus der Datenbank als auch vom Speicher. 
Der Orig_name kann somit wieder verwendet werden.
Sollte es sich beim eingelieferten Paket um ein Delta handeln, wird nur das neuste Paket gelöscht. Das Originalobjekt bleibt erhalten.
Der Contractor bekommt eine EMail vom System

## Der "Gesamter Workflow zurücksetzen"-Button - Rollback (Status 600)

Der gesamte Workflow für das Objekt wird wiederholt. Nur gültig für Status <= 400.

## Der "Zurücksetzen Button - Retry

Bestimmte Actions können isoliert zurückgesetzt werden auf den vorherigen 0er Status. 

# Automatisches Anhalten der ActionFactory

Unter bestimmten Umständen ist dem ContentBroker nicht möglich, wie vorgesehen zu operieren. Dies führt in der Beobachtung zum scheinbar  "stehenden" ContentBroker, der keinerlei Actions durchführt. 

Dies ist meistens der Fall, wenn zu Durchführung der
Paketverarbeitung notwendige externe Systeme nicht erreichbar sind. Zu den externen Systemen zählen vor allem iRODS, Fedora und ElasticSearch, aber
auch FIDO und JHOVE. Wenn der ContentBroker während der Verarbeitung eines Paketes feststellt, dass eines dieser Subsysteme nicht erreicht werden kann,
bricht er nicht nur die Verarbeitung des aktuellen Paketes ab, sondern hält auch automatisch die ActionFactory an. Die ActionFactory ist diejenige 
Komponente, welche die Datenbank nach Jobs für den lokalen Knoten fragt und für diese Jobs dann die entsprechenden Actions anstößt. Diese 
Sicherheitsmaßnahme verhindert, dass weitere Jobs bearbeitet werden, denn es ist davon auszugehen, dass wenn die genannten Konnektoren nicht funktionieren,
dass alle nachfolgenden Jobs ebenfalls in Fehlerstatus enden.

Der zweite Fall, in dem der ContentBroker automatisch die ActionFactory anhält, ist, wenn unzureichende Informationen im Grundlegenden Datenmodell betreffend
der Konfiguration des Knotens oder des Gesamtsystemes (Node,PreservationSystem) festgestellt werden. Auch hier kann davon ausgegangen werden, dass
die notwendigen Voraussetzungen für die Durchführung jeglicher Action nicht gegeben sind. Dementsprechend greift dann die beschriebene Sicherheitsmaßnahme.

Wenn die ActionFactory angehalten wird, sieht man das als wiederkehrenden Statusmeldung in der Logdatei contentbroker.log und zusätzlich in der 
Maske "Adminfunktionen" der DA-Web. Das Problem sollte behoben
werden. Je nach Fehlerquelle sollte der ContentBroker dafür heruntergefahren werden. Danach kann der ContentBroker, oder auch einfach die ActionFactory,
wieder gestartet werden.


# Logfiles der Anwendung

Die konfigurierten Logger sind in der logback.xml unterhalb des Ordners ContentBroker/conf einsehbar. Üblicherweise sind die folgenden Logger konfiguriert :

     log/grid.log (Alle Meldungen betreffend der Gridfunktionalitäten iRODS)
     log/time_stamp_actions.csv (Optional Ausführungsdauer jeder einzelnen Action zur Perf. Analyse)
     log/contentbroker.log (Alle Meldungen des CB)
     log/stout.log (Ausgaben des Diagnostics Tools)
     log/stderr.log (Fehlerausgaben der Datenbankverbindung)
     log/object-logs/ID.log (Fehlerausgaben pro Paket)
     log/ingest.log (Ingest Scanner Worker, zuständig für die Beobachtung der Eingabeschnittstelle)
     log/events.log (Worker der Systemevents)
     log/timbebasedpublication.log (Worker zur zeitgesteuerten Veröffentlichung)
     log/mailworker.log (Versendung von Emails)
     log/decisiontimeout.log (Worker zum Ablauf der einer Kundennachfrage)

Zur Analyse sind je nach Fehlerbild alle oder ausgewählte Logfiles zu sichten.


# Manuelles Löschen von bereits archivierten Objekten unter speziellen Voraussetzungen

Das Löschen eines bereits archivierten Objektes ist, so wie LZA aus DNSCore-Sicht konzeptioniert ist, nicht vorgesehen. Sollte es dennoch (Stichwort "Deletion under exceptional circumstances"), z.B. in Testsystemen erforderlich sein, muss dies manuell erfolgen. Im folgenden sind die notwendigen Schritte zusammengefasst.

## Vollständiges Löschen eines Objektes.

### Löschen eines Objektes aus der Datenbank

Akteur: **PS-ADMIN**

Ist ein Objekt bereits vollständig archiviert, kann es nur noch händisch aus der Datenbank entfernt werden. Dabei muss wegen der Constraints der Datenbank eine bestimmte Reihenfolge bei der Löschung eingehalten werden. Die folgende Anleitung zeigt dies examplarisch für ein einzelnes Objekt. Einzelne Schritte sind in Pseude-SQL dargestellt.

Zunächst muss der technische Identifier des zu löschenden Objektes ermittelt werden, etwa durch Einsichtnahme in die DAWeb. Zu dem Objekt kann der Primärschlüssel ermittelt werden.
    
    OBJECT_IDENTIFIER = technischer Identifier des Objektes
    DATA_PK = SELECT data_pk FROM objects WHERE identifier='OBJECT_IDENTIFIER';
    
zu dem Object müssen dann die die Packages ermittelt werden. 
    
    LIST<PKG_ID>  = SELECT * FROM objects_packages WHERE objects_data_pk='DATA_PK';
	
Die Package ids müssen NOTIERT werden. Dann kann zuerst die Verknüpfung aufgehoben werden.

	DELETE FROM objects_packages WHERE objects_data_pk='DATA_PK';
	
Anschließend kann das Objekt und seine zugehörigen Packages gelöscht werden.
    
    DELETE FROM packages WHERE id IN (LIST<PKG_ID>) ;
	DELETE FROM objects WHERE data_pk='DATA_PK';
	
Dies löscht nur die Datenbankeinträge. Die physischen Objekte bleiben weiterhin auf den LZA-Medien vorhanden und belegen Platz, sind dem System gegenüber jedoch unbekannt.

### Löschen eines Objektes auf den LZA-Medien.

Akteur: **PS-ADMIN**

Sollen diese Objekte auch von den Medien gelöscht werden, müssen diese auch aus dem Grid gelöscht werden. In der iRODS-Zonen Architektur kann ein Objekt gelöscht werden mittels

    irm -rf /zone/aip/csn/oid
    irm -rf /zone/federated/<CN>/aip/csn/oid

