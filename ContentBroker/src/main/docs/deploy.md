*Markdown*

# Continuous Delivery Workflow

* regelmäßig committen (Richtwert z.b. 1 mal stündlich), um Integrationsschwierigkeiten vorzubeugen
* dazu, wenn nötig, größere Tasks in kleinere Schritte unterteilen.
vor einem Commit
* je nachdem woran gearbeitet wird
** die AkkzeptanzTests lokal ausführen oder
** die IntegrationTests auf dem Testbed ausführen
** oder beides.
* dann committen
nach einem Commit
* postcommit.sh,integration.sh,acceptance.sh ausführen

# Auf der Developer-Maschine: (lokal, nicht dev-grid!)==
1. cd ContentBrokerTrunk
2. mvn package (Um aus den neuesten Änderungen ein Compilat zu bauen und !Wichtig! die Unit Tests auszuführen, um Fehlern schon hier vorzubeugen)
3. ./deliver.sh dev dev (pathToLocalCBInstallation - ohne abschließendes Slash!)
4. Warten auf folgende Message: INFO  de.uzk.hki.da.core.ContentBroker - ContentBroker is up and running 
5. Wenn die Meldung kommt, dann mit ctrl-c abbrechen. Der ContentBroker läuft jetzt und es wurde eine Instanz der HSQLDB gestartet
6. ./acceptance.sh dev dev (pathToLocalCBInstallation - ohne abschließendes Slash!)

# Integrationstests in den Testbeds ausführen==
1. lokal die neuesten Changes mit mvn package bauen
2. rsync zum Testbed
3. im Testbed: 
4. ./integration.sh

# Ausführen einzelner IntegrationTests auf der vm3 (Testbed oder Trunk)
1. ./deliver.sh integration
2. mvn test -Dtest=IT...

# Auf der VM3: Bauen des Release Candidate und manuelles Testen
1. cd development/ContentBrokerTrunk; . ./predeploy.sh
2. ./postcommit.sh (!!!Achtung postcommit bloß nicht lokal auf der Developer-Maschien ausführen. Da steckt ein bzr revert drin!!!)
3. Wenn letzter Schritt erfolgreich, dann ./integration.sh
4. Wenn letzter Schritt erfolgreich, dann ./deliver.sh vm3
5. Warten auf folgende Message: INFO  de.uzk.hki.da.core.ContentBroker - ContentBroker is up and running 
6. Wenn die Meldung kommt, dann mit ctrl-c abbrechen. Der ContentBroker läuft jetzt.
7. ContentBroker auf der VM2 stoppen! (acceptance wartet auf Status 540, das Paket darf nicht von der VM2 gefetched werden)
8. Wenn letzter Schritt erfolgreich, dann ./acceptance.sh vm3
9. Wenn letzter Schritt erfolgreich, dann ./deliver.sh vm2
10. goto vm2:ContentBroker; ./ContentBroker_start.sh
11. Manuelles Testen (testpackage_klein_und_muss_durchlaufen.*)

# Deployment auf anderen Maschinen 
* vm6
	* ./deliver.sh full
	* deliverable.full.$VERSION.tar -> vm6; unpack in CB_ROOT; ./configure.sh
* danrw
	* ./deliver.sh pres
	* deliverable.pres.$VERSION.tar -> danrw; unpack in CB_ROOT; ./configure.sh
* prod und andere Knoten
	* ./deliver.sh node
	* deliverable.node.$VERSION.tar -> prod; unpack in CB_ROOT; ./configure.sh

# Vorbereitung einer neuen Development Umgebung
1. Auf der lokalen Maschine einen leeren Ordner für den ContentBroker anlegen, damit die Deploy-Scripte dahin ausrollen können.
2. Dieser Ordner wird oben mit (pathToLocalCBInstallation) referenziert.

# Datenbankabfrage auf Developer-Maschine
1. ./sqlrequest "[SQL-Abfrage]"

