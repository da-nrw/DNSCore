# Retrieval

Als Retrieval wird der Prozess des Zurückladens von Objekten auf die lokalen Arbeitsstation eines Nutzers bezeichnet.

Am Ende des Retrieval Vorganges erhält der Nutzer einen Einlieferunsbelegt mit dem technischen Identifier des Objektes.
Objekte können über diesen Identifier recherchiert und deren Retrieval über die DAWeb angestoßen werden. 

## Manuelles Retrieval

### Voraussetzungen

* Der Contractor hat einen Rechner mit Internetverbindung.
* Der Contractor ist mit seinen Zugangsdaten in der DAWeb Oberfläche eingeloggt.
* Der Contractor hat zu einem früheren Zeitpunkt ein SIP eingespielt. Er hat den Identifier notiert.

### Schritte

1. In der DAWeb Maske "Eingelieferte Objekte (AIP)" gibt es die Möglichkeit, die aufgelisteten Objekte per Filter einzugrenzen.

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/retrieval1.png)

2. Log in to DAWeb on your node.
2. Go to the "Eingelieferte Objekte (AIP)" section.
3. Search your object by filtering for it by object identifier (which you take from the email).

1. Click the "Anfordern" button of your object.
1. Wait for another email which confirms your object has successfully been retrieved and is ready for download
1. Go to the starting page of DAWeb again and then select "Objekt entnehmen (DIP)".
1. Click the link for your object and your browser should start do download the 
DIP for your object immediately.
![](https://raw2.github.com/da-nrw/DNSCore/master/DAWeb/doc/retrieval_2.png)
1. Alternatively you have the "Entnahme" Button which appears for your object in the "Eingelieferte Objekte (AIP)" section when your DIP can be retrieved.
![](https://raw2.github.com/da-nrw/DNSCore/master/DAWeb/doc/retrieval_3.png)
1. To save resources, the system will remove the DIP automatically from the web share/UserArea 24h after you have downloaded it.

**Note** that requested packages are visible not only on the "Objekt entnehmen (DIP)" view but also in the outgoing folder of the contractors web share.

## User Reporting via DA-Web

Although ContentBroker sends Emails on significant actions (like "archived" or "user error") you are able to call interface of DA-Web for state calls, please refer to the information provided in this folder.





