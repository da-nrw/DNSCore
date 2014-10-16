# Fehleranalyse & -bereinigung

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

Die Zwei 

xx4

These are states where an error occured due to imcomplete or inconsistent data caused by the user. The xx4 states always result of an action throwing a UserException. In case of such an exception the system autogenerates an error reports which gets instantly delivered to the user via email. If DAWeb encounters an action in a xx4 state, it presents a "delete" button to the admins who are free then to clean up the object from the queue. Depending if the object is a new one or a delta to an existing one either the newest package or the whole objects gets deleted from the database if one clicks the delete button. Also the WorkArea gets clean up. In the case it was a new object the orig name is reusable again. The urn which was given to the object is waste.

### Rollback


