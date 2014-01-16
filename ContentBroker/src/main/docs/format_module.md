Dieses Dokument beschreibt sämtliche Aspekte der Formaterkennung, Formatkonversion der DNSCore


Zum Betrieb des DA-NRW sind verschiedene Tools im Einsatz, die hier kurz beschrieben werden sollen. Alle diese Tools besitzen CLI (Command Line interfaces) und werden allesamt aus IRODS aufgerufen.
Es werden verschiedene Libraries innerhalb der DA-NRW Suite eingesetzt, die die eingelieferten Dateien in langzeitarchiv-fähige Dateiformate konvertieren sollen. Ihre Verwendung hängt erheblich davon ab, wie zuverlässig sich die resultierenden Dateien validieren und verarbeiten lassen. So wurde festgestellt, dass ältere Versionen von Ghostscript (vor 9.0) keine validen PDF/a-Dateien produzierten und somit der Zweck der digitalen Langzeitarchivierung nicht erfüllt war. Folgende Tools kommen momentan in der DA-NRW Suite zum Einsatz:


## Bild Konvertierung
Für die Konvertierung von Bildformaten wird imagemagick eingesetzt. Hier kann eine Version aus der Distribution verwendet werden.
Test: Auf der Kommandozeile sollte der Befehl convert eine Ausgabe produzieren; außerdem sollte der Befehl identify -list format u.a. Einträge für die Formate TIFF, JPEG und PNG zurückliefern.

## PDF Konvertierung
Zur PDF Konvertierung wird Ghostscript 9.0 benötigt. Da in den vergangenen Distributionen lediglich 8.7 bereitgestellt wird, ist Ghostscript selbst zu kompilieren.
./configure
Die PDF/A Einstellungen sind in der Configfile PDF_A.ps unter conf/ zu finden. Hier muss ggf. der Pfad lokalen Farbprofilen angepasst werden.

## Audio-Konvertierung
Relativ neu ist die Implementation der Audio-Konversionsroutine für Browser-Formate. Hierzu wird SoX 14.4.0 verwendet. Sollte der Bedarf nach Anpassung der gekürzten Abspielzeiten (je nach rechtlichen Einstellungen) bestehen. So lautet der Befehl:
sox eingabe.mp3 ausgabe.wav trim 01:00 00:20
Hier wird ein 20-sekündiger Ausschnitt ab Minute 1 extrahiert.
