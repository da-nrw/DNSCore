# Fehlerhandling durch Verwender der Software

Durch Klick auf die Bearbeitungsübersicht können Informationen zu der Verarbeitung der SIP gewonnen werden. 
Die Rückmeldung geschieht durch eine Textangabe in der DA-Web GUI und durch einen Fehlercode, der in der Regel eine dreistellige Ziffer ist. 

xx0 bedeutet "wartend", xx2: bezeichnet "arbeitend" - hingegen bezeichnen xx1,xx3,xx4,xx5,xx6,xx7,xx8 einen Fehler. 

#### xx0 - WARTEND

Die Null am Ende des Status bedeutet, dass das Objekt sich in einem konsistenten, gemäß der DNS-Spezifikation wohlgeformten Zustand befindet und aktuell von keiner Action verarbeitet wird. Das Objekt wartet darauf, von einer passenden Action (Status = Anfangsstatus der Action) abgeholt zu werden. 

#### xx1 - VERARBEITUNG UNTERBROCHEN ABER FORTSETZBAR 

Jeder Status, der mit einer Eins endet, kennzeichnet einen Fehler in der Verarbeitung. 
Desweiteren bedeutet die Eins, dass das Objekt in einen konsistenten Zustand (xx1-1) zurückgeführt werden konnte. 
Demnach korrspondieren beispielsweise die Status 120 und 121 zu ein und demselben physischen File auf dem Dateisystem sowie in der Datenbank.  
Der zuständige Knotenadministrator kann das Objekt zurücksetzen mittels Klick auf den Button "Zurücksetzen" in der Adminoberfläche neu starten. 

#### xx2 - MOMENTAN ARBEITEND

Die Zwei am Ende bedeutet, dass das Objekt gerade von der aktuell aktiven Action bearbeitet wird. 
Je nach Größe und Komplexität des Pakets kann dieser Prozess einige Zeit dauern. Ob die Action tatsächlich arbeitet,
kann anhand fortlaufender Logmeldungen im Objekt-Log-File nachgesehen werden. 

#### xx4 - IHR PAKET WIRD NICHT AKTZEPTIERT, NEUEINLIEFERUNG NACH LÖSCHUNG MÖGLICH

Die Vier am Ende des Staus bedeutet einen Userfehler. Zumeist aktzeptiert das DNS Ihre Daten zunächst nicht.  Sie bekommen in diesem Fall eine Email mit der entsprechenden Exception aus dem Object-Logfile. 
Da dies bedeutet, dass die Eingangsdaten fehlerhaft sind. und berichtigt und neu eingespielt werden müssen. 
Es wird kein Rollback durchgeführt.
Daher muss der Knotenadministrator
anschließend das Objekt löschen. Dazu gibt es den "SIP löschen"-Button.

Genaue Angaben zu den Fehlercodes, die hier nicht gelistet wurden:
https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/administration-troubleshooting.de.md

# Antworten der Schnittstelle

Analog reagiert die RESTFul Schnittstelle

    xx0: „package in progress waiting : (<code>)“ 
    xx2: „package in progress working : (<code>)“
    xx3: „package in progress error : (<code>)“
    xx4: „package in progress error : (<code>)“
    xx5: „package in progress error : (<code>)“
    xx6: „package in progress error : (<code>)“
    xx7: „package in progress error : (<code>)“
    xx8: „package in progress error : (<code>)“

Relevant für den Abgebenden ist der Fehler xx4 (Userfehler), welcher auf ein Problem mit den abgegebenen Daten hinweist. In diesem Falle ist eine Herausnahme aus dem Worflow durch den zuständigen Knotenadmin zu veranlassen und die Datei (je nach vorliegendem Fehler) zu berichtigen und dann erneut zu senden.

Die anderen Fehlercodes sind eher seltener und sind Bereinigungsaufgaben des Knotenbetriebers. 

