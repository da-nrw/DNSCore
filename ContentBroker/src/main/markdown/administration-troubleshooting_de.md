## Fehleranalyse & -bereinigung

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

### Rollback


### Diagnostics Modus in ContentBroker - Smoke test

Bei Auftreten von Fehlern in der Paketverarbeitung sollte immer zuerst der sogenannte Smoke Test durchgeführt werden.

    cd DSNCore/Contentbroker
    java -jar ContentBroker.jar diagnostics

Der Test Überprüft eine Reihe von Verbindungen, z.b. Datenbank-Verbindung, iRODS, Fedora usw.

Erst wenn der Test positiv ausfällt und der Fehlerstatus in der Paketverarbeitung immer noch bestehen bleibt , macht es Sinn, die Fehlersuche fortzusetzen. 
