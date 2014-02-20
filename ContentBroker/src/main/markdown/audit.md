Gemäß dem Paradigma der Bitstream-Preservation als Minimallösung eines Langzeitarchivs, 
unterstützt eine Funktion namens “Audit”  genau dieses Paradigma durch regelmäßige Überprüfung der 
Datenintegrität auf allen einzelnen Hardware-Knoten.
Hier bestehen zwei unterschiedliche Optionen:
Zum einen führt das DA-NRW System automatisch einmal im Jahr eine Überprüfung eines 
jeden Pakets anhand seiner hinterlegten Prüfsumme durch. Zu anderen besteht die Möglichkeit, dass eine 
Administrator über die Webkonsole des DA-NRW ein manuelles, so genanntes “deep” Audit durchführt.
Durch Anklicken des Lupen-Symbols in der Ansicht der eingelieferten Objekte auf der DA-NRW Weboberfläche ist es einem 
Administrator möglich, die Unversehrtheit der eingelieferten Daten zu überprüfen.
Abbildung 19: Ansicht der DA-NRW Weboberfläche für die Administratorrolle..

Wenn der Audit begonnen hat, erscheint ein Uhrsymbol in der Spalte “Objektstatus”. 
In der Paketstatus-Queue kann der Nutzer verfolgen, welche Schritte bei der Überprüfung der 
Checksums unternommen werden. Sollte eine Überprüfung fehlerhaft verlaufen, wird also eine Inkonsistenz 
beim Vergleich der Prüfsummen festgestellt, so wird der Administrator eines Betreiber-Knotens benachrichtigt, 
damit die nötigen Schritte zur Reparatur der Paket-Integrität eingeleitet werden können.
