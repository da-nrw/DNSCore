Dieses Dokument beschreibt den Build-Prozess von DNSCore

# Continuous Delivery Workflow

* Regelmäßig committen (Richtwert z.b. 1 mal stündlich), um Integrationsschwierigkeiten vorzubeugen
* Dazu, wenn nötig, größere Tasks in kleinere Schritte unterteilen.
vor einem Commit
* je nachdem woran gearbeitet wird
 * einzelne oder alle AkkzeptanzTests lokal ausführen oder
 * die IntegrationTests auf dem Testbed ausführen
 * oder beides.
* dann committen
* nach einem Commit: postcommit.sh,integration.sh,acceptance.sh ausführen

## Vorbereitung einer neuen lokalen Development Umgebung
1. Auf der lokalen Maschine einen leeren Ordner für den ContentBroker anlegen, damit die Deploy-Scripte dahin ausrollen können.
2. Dieser Ordner wird oben mit (pathToLocalCBInstallation) referenziert

## Auf einer lokalen Development Umgebung:
1. cd DNSCore/ContentBroker
2. ./build.sh dev pathToLocalCBInstallation
3. ./install.sh dev pathToLocalCBInstallation
4. Warten auf folgende Message: INFO  de.uzk.hki.da.core.ContentBroker - ContentBroker is up and running 
5. Wenn die Meldung kommt, dann mit ctrl-c abbrechen. Der ContentBroker läuft jetzt und es wurde eine Instanz der HSQLDB gestartet
6. Ausführen aller Akzeptanztests: mvn failsafe:integration-test
7. Ausführen einzelner Akzeptanztests: mvn failsafe:integration-test -Dit.test=ATUseCaseX

## Auf der VM3: Bauen des Release Candidate und manuelles Testen
1. cd development/DNSCore
2. git -- je nach Bedarf die entsprechende Version auschecken
3. ./build.sh vm3
3. ??? Wenn letzter Schritt erfolgreich, dann ./integration.sh
4. ./install.sh vm3
5. Warten auf folgende Message: INFO  de.uzk.hki.da.core.ContentBroker - ContentBroker is up and running 
6. Wenn die Meldung kommt, dann mit ctrl-c abbrechen. Der ContentBroker läuft jetzt.
7. ContentBroker auf der VM2 stoppen! (acceptance wartet auf Status 540, das Paket darf nicht von der VM2 gefetched werden)
8. mvn failsafe:integration-test
9. ??? Wenn letzter Schritt erfolgreich, dann ./deliver.sh vm2
10. goto vm2:ContentBroker; ./ContentBroker_start.sh
11. Manuelles Testen (testpackage_klein_und_muss_durchlaufen.*)

## Deployment auf anderen Maschinen 
1. Zunächst müssen nach Auswahl eines geeigneten Release-Kandidaten alle manuellen Akzeptanztests auf der QS,
so wie sie in den Testkatalogen spezifiziert sind, durchgeführt werden.
* vm6
	* ./deliver.sh full
	* deliverable.full.$VERSION.tar -> vm6; unpack in CB_ROOT; ./configure.sh
2. Danach kann ausgerollt werden.
* danrw
	* ./deliver.sh pres
	* deliverable.pres.$VERSION.tar -> danrw; unpack in CB_ROOT; ./configure.sh
* Prod und andere Knoten
	* ./deliver.sh node
	* deliverable.node.$VERSION.tar -> prod; unpack in CB_ROOT; ./configure.sh

## Datenbankabfrage auf Developer-Maschine
1. ./sqlrequest "[SQL-Abfrage]"


## Integrationstests in den Testbeds ausführen
1. lokal die neuesten Changes mit mvn package bauen
2. rsync zum Testbed
3. im Testbed: 
4. ./integration.sh

## Ausführen einzelner IntegrationTests auf der vm3 (Testbed oder Trunk)
1. ./deliver.sh integration
2. mvn test -Dtest=IT...

