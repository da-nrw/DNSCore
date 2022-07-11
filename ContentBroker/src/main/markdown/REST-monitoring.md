#DNS Monitoring via REST             

Zum Monitoring des DNS dient ein per HTTP ansprechbares API.
Die Anforderungsparameter werden in der URL übergeben. Wichtig ist die Angabe auf welchen Knoten sich die Anfrage bezieht.

## Aufruf
Die URL lautet:

	<Daweburl>/status/monitor?node=<Knotenname>

z.B.
	https://da-nrw.lvr.de/daweb3/status/monitor?node=lvr

## Rückgabe
Rückgabe ist eine JASON-Struktur mit folgendem Aufbau:

* requestStatus:          Status, ob die Anfrage erfolgreich verarbeitet wurde, Klartext "OK" oder „Failed“
* Message:                Eine Fehlermeldung im Klartext
* CountLastHour:          Anzahl der in der letzten Stunde erfolgreich archivierten Pakete
* SizeLastHour:           Gesamtgröße der in der letzten Stunde erfolgreich archivierten Pakete in Bytes
* CountLastDay:           Anzahl der in den letzten 24 Stunden erfolgreich archivierten Pakete
* SizeLastDay:            Gesamtgröße der in den letzten 24 Stunden erfolgreich archivierten Pakete
* result:                 Ein Array der Status der in Verarbeitung befindlichen Pakete mit dem Inhalt:
* status:                 Der Statuscode
* count:                  Die Anzahl der Pakete in diesem Status
* first:                  Zeitstempel des ältesten Pakets in diesem Status
* last:                   Zeitstempel des neuesten Pakets in diesem Status

Die Anzahl der Einträge der Status kann eingestellt werden:

	<Daweburl>/status/monitor?node=<Knotenname>&count=<Anzahl>

Default ist 10.
