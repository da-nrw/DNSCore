# Fehleranalyse & -bereinigung

Landet ein eingeliefertes Paket in einem Fehlerstatus, ist eine Reaktion seitens des Knoten-Admins gefragt. Unabhängig vom Fehlerstatus müssen immer folgende zwei Schritte durchgeführt werden:

1. Sicherstellen der Funktion des Systems
2. Fehleranalyse

Dabei ist die Reihenfolge der genannten Schritte entscheidend. Es macht keinen Sinn, einen Fehler zu debuggen, wenn das System als Ganzes möglicherweise falsch konfiguriert ist. Die einzelnen Schritte werden im Folgenden näher erläutert.

## Sicherstellen der Funktion des Systems

### Diagnostics Modus in ContentBroker - Smoke test

Bei Auftreten von Fehlern in der Paketverarbeitung sollte immer zuerst der sogenannte Smoke Test durchgeführt werden.

```bash
cd DSNCore/Contentbroker
java -jar ContentBroker.jar diagnostics
```

Der Test Überprüft eine Reihe von Verbindungen, z.b. Datenbank-Verbindung, iRODS, Fedora usw.

Erst wenn der Test positiv ausfällt und der Fehlerstatus in der Paketverarbeitung immer noch bestehen bleibt , macht es Sinn, die Fehlersuche fortzusetzen. 

## Paketverarbeitung Fehleranalyse && Fehlerbehebung

Die Datenverarbeitung in DNSCore ist in kleine logische in sich abgeschlossene Einheiten – Actions – unterteilt. Jeder Workflow, ob Einlieferung ([ingest] (https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/resources/META-INF/beans-workflow.ingest.xml)), das Wiederabrufen ([retrieval] (https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/resources/META-INF/beans-workflow.retrieval.xml)) oder Präsentation ([presentation] (https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/resources/META-INF/beans-workflow.presentation.xml)) besteht aus einer festgelegten Abfolge verschiedener Actions.

### Action & Status

Abhängig von der jeweiligen Aufgabe werden in einer Action unterschiedliche Änderungen am Objekt vorgenommen. Dabei wird das Stadium der Verarbeitung eines Objekts im Bearbeitungsstatus ausgedrückt. Die Angabe des Bearbeitungsstatus eines jeden eingelieferten Pakets befindet sich in der DAWEB in der Spalte „Status“. Anhand des Status lässt sich zu jeder Zeit sowohl die aktuell ausgeführte Action als auch der Zustand der Verarbeitung innerhalb der Action ablesen.

Für jede Action wurde ein Anfangs- sowie ein End-Status definiert. Der Anfangs-Status ist konstant, der End-Status kann je nach Verlauf der Action (erfolgreich oder nicht erfolgreich) variieren. Im Fall einer erfolgreichen Verarbeitung des Objekts (durchlaufen des gesamten Workflows der Action) endet die Action mit der Überführung des Objekts in den definierten End-Status. Im Falle eines Fehlers bleibt das Objekt in einem (Anfangsstatus <) Fehlerstatus (> Endstatus). Der Fehlerstatus variiert je nach Fehlerart.

### Definierte Status

Der Status ist definiert als eine dreistellige dezimale Zahl. Dabei kodieren die ersten beiden Ziffern (von links nach rechts) zu jeder Zeit die aktuell für die Verarbeitung der Daten eingesetzte Action. Die dritte Ziffer kennzeichnet den Verlauf der Bearbeitung innerhalb der jeweiligen Action.

Die Zuordnung der Status zu den Actions kann jederzeit anhand der ersten beiden Ziffern sowie der oben angeführten beans nachvollzogen werden. Die Interpretation der letzten Ziffer des Status wird im folgenden erläutert.

#### xx0 - WAITING

Die Null am Ende des Status bedeutet, dass das Objekt sich in einem konsistenten, gemäß der DNS-Spezifikation wohlgeformten Zustand befindet und aktuell von keiner Action verarbeitet wird. Das Objekt wartet darauf, von einer passenden Action (Status = Anfangsstatus der Action) abgeholt zu werden. 

#### xx1 - ERROR_PROPERLY_HANDLED

Jeder Status, der mit einer Eins endet, kennzeichnet einen Fehler in der Verarbeitung. 
Desweiteren bedeutet die Eins, dass das Objekt in einen konsistenten Zustand (xx1-1) zurückgeführt werden konnte. 
Demnach korrspondieren beispielsweise die Status 120 und 121 zu ein und demselben physischen File auf dem Dateisystem sowie in der Datenbank.  
Der Administrator kann das Objekt zurücksetzen mittels Klick auf den Button "Zurücksetzen"

#### xx2 - WORKING

Die Zwei am Ende bedeutet, dass das Objekt gerade von der aktuell aktiven Action bearbeitet wird. 
Je nach Größe und Komplexität des Pakets kann dieser Prozess einige Zeit dauern. Ob die Action tatsächlich arbeitet,
kann anhand fortlaufender Logmeldungen im Objekt-Log-File nachgesehen werden.

#### xx3 - ERROR_BAD_ROLLBACK

Die drei am Ende bedeutet, dass ein Rollback nicht durchgeführt werden konnte, entweder, weil er nicht implementiert ist, oder
weil ein Fehler während der Durchführung des Rollbacks aufgetreten ist. 
Zwischen 123 und 323 kann der Administrator das Objekt per Button "Gesamten Workflow zurücksetzen". 

#### xx4 - USER_ERROR

Die Vier am Ende des Staus bedeutet einen Userfehler. Der User bekommt in diesem Fall eine Email mit der entsprechenden Exception aus dem Object-Logfile. 
Darüber hinaus erscheint in der DAWeb neben dem Fehlerstatus ein neuer Button. 

Da dies bedeutet, dass die Eingangsdaten fehlerhaft sind. und berichtigt und neu eingespielt werden müssen. 
Es wird kein Rollback durchgeführt.
Daher muss der Administrator
anschließen das Objekt löschen. Dazu gibt es den "Objekt löschen"-Button.

#### xx5 - ERROR_MODEL_INCONSISTENT

Eine fünf am Ende bedeutet, dass ein kritischer Fehler bezüglich des Datenmodells aufgetreten ist. Dies kann mit der Verknüpfung zwischen Actions, 
Jobs, Usern und Objekten bzw. deren Eigenschaften zusammenhängen. Im Falle solcher Fehler bitten wir Nutzer der Software, 
sich direkt an die Entwickler zu wenden, da diese Kategorie von Fehlern vergleichsweise selten auftritt und genauster Analyse bedarf.
Im Normalfall kann die Inkonsistenz datenbankseitig bereinigt werden und per "Zurücksetzen"- bzw. "Gesamten Workflow zurücksetzen"- 
Button zurückgesetzt werden.

#### xx6 - ERROR_PRECONDITIONS_NOT_MET

Eine sechs am Ende bedeutet, dass die Eingangsbedingungen für die Bearbeitung eines Paketes in einem bestimmten Status nicht gegeben sind. 
Dass heisst, dass die dem Status entsprechende Action das Paket nicht so vorfindet, wie sie es benötigt, um es ordnungsgemäß verarbeiten zu können. 
Das Paket kann in  solch einem Fall lediglich gelöscht werden durch Klick auf den "Objekt Löschen"-Button bzw. zurückgerollt durch den 
Button "Gesamten Workflow zurücksetzen." 
Solcherlei Fehler können z.B. durch fehlerhaft implementierte Rollbacks entstehen.
Im Falle eines solchen Fehlers sollten die Entwickler kontaktiert werden.

#### xx7 - ERROR_BAD_CONFIGURATION

Konfigurationsfehler. Sollte nur während der Entwicklung oder Einrichtungsphase eines Systems auftreten. Ein End-To-End Test eines Paketes
auf einem Knoten während der Einrichtungsphase wird alle potentiellen 7er Status aufdecken. Nach Behebung des Zurücksetzen Buttons kann der Administrator
das Paket wie gewohnt per "Zurücksetzen"-Button zurücksetzen.


#### xx8 - UP_TO_ROLLBACK

Actions, welche sich durch einen jähen Abruch des ContentBroker in undefinierten Zuständen befinden, können vom Gesamtsystem-Administrator
manuell mit einer abschließenden 8 versehen werden, um dem System zu signalisieren, dass der Rollback nachgeholt werden muss. Nach dem Rollback wird, 
falls dieser erfolgreich ist, die Action automatisch zurück in der 0er Status gesetzt. D.h. dass sie sich in der Warteschlange zur Bearbeitung 
befindet.

### Der "Objekt Löschen"-Button. Automatisiertes Löschen von Paketen.

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/Delete_button.PNG)

Das Betätigen des Buttons vom Admin führt zur Löschung des Objekts sowohl aus der Datenbank als auch vom Speicher. 
Der Orig_name kann somit wieder verwendet werden.
Sollte es sich beim eingelieferten Paket um ein Delta handeln, wird nur das neuste Paket gelöscht. Das Originalobjekt bleibt erhalten.

### Der "Gesamte Workflow zurücksetzen"-Button - Rollback

TODO screenshot

### Der "Zurücksetzen Button - Retry

TODO screenshot

### Automatischen Anhalten der ActionFactory.

Unter bestimmten Umständen ist dem ContentBroker nicht möglich, wie vorgesehen zu operieren. 

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


## Manuelles Löschen von bereits archiveirten Objekten unter speziellen Voraussetzungen

Das Löschen eines bereits archivierten Objektes ist, so wie LZA aus DNSCore-Sicht konzeptioniert ist, nicht vorgesehen. Sollte es dennoch (Stichwort "Deletion under exceptional circumstances"), z.B. in Testsystemen erforderlich sein, muss dies manuell erfolgen. Im folgenden sind die notwendigen Schritte zusammengefasst.

### Vollständiges Löschen eines Objektes.

#### Löschen eines Objektes aus der Datenbank

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

#### Löschen eines Objektes auf den LZA-Medien.

Akteur: **PS-ADMIN**

Sollen diese Objekte auch von den Medien gelöscht werden, müssen diese auch aus dem Grid gelöscht werden. In der iRODS-Zonen Architektur kann ein Objekt gelöscht werden mittels

    irm -rf /zone/aip/csn/oid
    irm -rf /zone/federated/<CN>/aip/csn/oid

    






