# Aktuelle Konfiguration der Formatkonversionen in DNSCore
Zum Produktivstart wird eine Basiskonfiguration bereitgestellt. Diese kann in Abhängigkeit von dem Material und den Anforderungen der einliefernden Institutionen durch neue Konversionsrichtlinien und Routinen angepasst und erweitert werden.
In der folgenden Tabelle ist das Ergebnis der aktuellen Konfiguration für die Langzeitarchivierung (AIP) ablesbar:

![Bild](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/current-conversation-LZA.jpg)

Für die Präsentation (PIP) sind aktuell folgende Einstellungen gültig:

![Bild](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/current-conversation-presentation.jpg)

Für die Präsentation ist zu beachten, ob bei dem ersten Schritt der Langzeitarchivierung eine Konversion in ein anderes Format stattfindet. Ist dies der Fall, so ist das LZA-Format ausschlaggebend für die Umwandlung in das Präsentationsformat.

Beispiel

PNG (fmt/11) wird für die LZA ins TIFF-Format fmt/353 migriert. Da für fmt/353 das PIP-Format JPG definiert ist, wird für fmt/11 ebenfalls das  PIP-Format JPG erstellt – obwohl für das Einlieferungsformat fmt/11 kein Präsentationsformat definiert ist. 

Testdaten

In der Tabellenspalte Testdaten sind die Formate gekennzeichnet, zu denen bereits Testdaten vorliegen und somit das Verhalten des Systems qualitätsgesichert werden konnte. 
Für alle anderen Konfigurationsroutinen (vorhandene und zukünftig gewünschte) sind Testdaten von den einliefernden Institutionen erforderlich. Nur so kann eine geprüfte Aussage über das aktuelle Systemverhalten getroffen werden.
