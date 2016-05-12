# Fehlerhandling durch Verwender der Software

Durch Klick auf die Bearbeitungsübersicht können Informationen zu der Verarbeitung der SIP gewonnen werden. 
Die Rückmeldung geschieht durch eine Textangabe in der DA-Web GUI und durch einen Fehlercode, der in der Regel eine dreistellige Ziffer ist. 

xx0 bedeutet "wartend", xx2: bezeichnet "arbeitend" - hingegen bezeichnen xx1,xx3,xx4,xx5,xx6,xx7,xx8 einen Fehler. 


 
Genaue Angaben zu den Fehlercodes:
https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/administration-troubleshooting.de.md

Antworten der Schnittstelle:
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

