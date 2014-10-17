# Fehleranalyse & -bereinigung

Landet ein eingeliefertes Paket in einem Fehlerstatus, ist eine Reaktion seitens des Knoten-Admins gefragt. Unabhängig vom Fehlerstatus müssen immer folgende zwei Schritte durchgeführt werden:

1. Sicherstellen der Funktion des Systems
2. Fehleranalyse

Dabei ist die Reihenfolge der genannten Schritte entscheidend. Es macht keinen Sinn, einen Fehler zu debuggen, wenn das System als Ganzes möglicherweise falsch konfiguriert ist. Die einzelnen Schritte werden im Folgenden näher erläutert.

## 1. Sicherstellen der Funktion des Systems

### Diagnostics Modus in ContentBroker - Smoke test

Bei Auftreten von Fehlern in der Paketverarbeitung sollte immer zuerst der sogenannte Smoke Test durchgeführt werden.

    cd DSNCore/Contentbroker
    java -jar ContentBroker.jar diagnostics

Der Test Überprüft eine Reihe von Verbindungen, z.b. Datenbank-Verbindung, iRODS, Fedora usw.

Erst wenn der Test positiv ausfällt und der Fehlerstatus in der Paketverarbeitung immer noch bestehen bleibt , macht es Sinn, die Fehlersuche fortzusetzen. 

## 2. Fehleranalyse

Die Datenverarbeitung in DNSCore ist in kleine logische in sich abgeschlossene Einheiten – Actions – unterteilt. Jeder Workflow, ob Einlieferung ([ingest] (https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/resources/META-INF/beans-workflow.ingest.xml)), das Wiederabrufen ([retrieval] (https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/resources/META-INF/beans-workflow.retrieval.xml)) oder Präsentation ([presentation] (https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/resources/META-INF/beans-workflow.presentation.xml)) besteht aus einer festgelegten Abfolge verschiedener Actions.

### Action & Status

Abhängig von der jeweiligen Aufgabe werden in einer Action unterschiedliche Änderungen am Objekt vorgenommen. Dabei wird das Stadium der Verarbeitung eines Objekts im Bearbeitungsstatus ausgedrückt. Die Angabe des Bearbeitungsstatus eines jeden eingelieferten Pakets befindet sich in der DAWEB in der Spalte „Status“. Anhand des Status lässt sich zu jeder Zeit sowohl die aktuell ausgeführte Action als auch der Zustand der Verarbeitung innerhalb der Action ablesen.

Für jede Action wurde ein Anfangs- sowie ein End-Status definiert. Der Anfangs-Status ist konstant, der End-Status kann je nach Verlauf der Action (erfolgreich oder nicht erfolgreich) variieren. Im Fall einer erfolgreichen Verarbeitung des Objekts (durchlaufen des gesamten Workflows der Action) endet die Action mit der Überführung des Objekts in den definierten End-Status. Im Falle eines Fehlers bleibt das Objekt in einem (Anfangsstatus <) Fehlerstatus (> Endstatus). Der Fehlerstatus variiert je nach Fehlerart.

### Definierte Status

Der Status ist definiert als eine dreistellige dezimale Zahl. Dabei kodieren die ersten beiden Ziffern (von links nach rechts) zu jeder Zeit die aktuell für die Verarbeitung der Daten eingesetzte Action. Die dritte Ziffer kennzeichnet den Verlauf der Bearbeitung innerhalb der jeweiligen Action.

Die Zuordnung der Status zu den Actions kann jederzeit anhand der ersten beiden Ziffern sowie der oben angeführten beans nachvollzogen werden. Die Interpretation der letzten Ziffer des Status wird im folgenden erläutert.

#### xx0

Die Null am Ende des Status bedeutet, dass das Objekt sich in einem konsistenten, gemäß der DNS-Spezifikation wohlgeformten Zustand befindet und aktuell von keiner Action verarbeitet wird. Das Objekt wartet darauf, von einer passenden Action (Status = Anfangsstatus der Action) abgeholt zu werden. 

#### xx1

Jeder Status, der mit einer Eins endet, kennzeichnet einen Fehler in der Verarbeitung. Desweiteren bedeutet die Eins, dass das Objekt in einen konsistenten Zustand (xx1-1) zurückgeführt werden konnte. Demnach korrspondieren beispielsweise die Status 120 und 121 zu ein und demselben physischen File auf dem Dateisystem sowie in der Datenbank.  

#### xx2

Die Zwei am Ende bedeutet, dass das Objekt gerade von der aktuell aktiven Action bearbeitet wird. Je nach Größe und Komplexität des Pakets kann dieser Prozess einige Zeit dauern. 

#### xx4

Die Vier am Ende des Staus bedeutet einen Userfehler. Der User bekommt in diesem Fall eine Email mit der entsprechenden Exception aus dem Object-Logfile. Darüber hinaus erscheint in der DAWeb neben dem Fehlerstatus ein neuer Button. 

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/Delete_button.PNG)

Das Betätigen des Buttons vom Admin führt zur Löschung des Objekts sowhl aus der Datenbank als auch vom Speicher. Der Orig_name kann somit wieder verwendet werden.

Sollte es sich beim eingelieferten Paket um ein Delta handeln, wird nur das neuste Paket gelöscht. Das Originalobjekt bleibt erhalten.

### Rollback


## 3. Löschen von Objekten

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

    rm -rf /zone/aip/csn/oid
    






