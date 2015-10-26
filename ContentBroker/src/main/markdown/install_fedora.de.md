# Presentation Repository installieren

Das Presentation Repository des DA-NRW zur Bereitstellung der konvertierten browserfähigen Objekte und zum Abruf durch Portale läuft nur an einem Knoten des Grids und zwar an der Universität zu Köln. Momentan wird als Repository-Software Fedora 3.5 eingesetzt.

Im folgenden wird davon ausgegangen dass die Installationspfade wie folgt lauten (diese müssen beim Abarbeiten der Dokumentation ggf. entsprechend angepasst werden):
* Fedora: /opt/fedora
* Tomcat: /opt/tomcat

Im Source-Code-Tree von DNSCore finden sich Skripte und Config-Dateien zu Fedora:

    https://github.com/da-nrw/DNSCore/tree/master/ContentBroker/src/main/python
    
## Voraussetzungen

### Prerequsites 

#### Tomcat

Tomcat 6 oder 7

Sicherstellen, dass Tomcat genügend Speicher allozieren kann, z.B. indem in der Datei `CATALINA_BASE/bin/sentenv.sh` (anlegen, wenn Sie nicht existiert) folgendes eingetragen wird:

    JAVA_OPTS="-Djava.awt.headless=true -server -Xms48m -Xmx1024M -XX:MaxPermSize=512m"

#### PostgreSQL

Nutzer `fedora` und Datenbank `fedora` anlegen:

    CREATE USER fedora WITH PASSWORD 's3cr3t';
    CREATE DATABASE fedora;
    GRANT ALL PRIVILEGES ON DATABASE fedora TO fedora;

Datenbank `riTriples` für den Resource Index in PostgreSQL anlegen (Achtung: case sensitive name -> quotes verwenden!):

    CREATE DATABASE "riTriples";
    GRANT ALL PRIVILEGES ON DATABASE "riTriples" TO fedora;
    
## Fedora

### Installation

Fedora Installer in der Version 3.5 downloaden:

    wget http://downloads.sourceforge.net/fedora-commons/fcrepo-installer-3.5.jar

Im Repository befindet sich die Datei `https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/conf/install.properties`. Dort müssen die entsprechenden Passwörter und ggf. Einstellungen für die Datenbank, den Servernamen und die Installationspfade angepasst werden.

Installer ausführen:

    sudo java -jar fcrepo-installer-3.5.jar config/install.properties

Die Datei fedora.fcfg mit den entsprechenden Einstellungen befindet sich auch im Repository unter `config/fedora.fcfg`.

Ggf. die vom Installer erstellte server.xml in den Tomcat-Ordner conf kopieren. 

Eventuell müssen die Berechtigungen so angepasst werden, dass der User, unter dem Tomcat läuft, Schreibzugriff auf den Home-Ordner von Fedora hat:

    chown -R tomcat7:tomcat7 /opt/fedora/

`/opt/fedora/server/config/fedora.fcfg` öffnen.

Ggf. adminEmailList und repositoryName anpassen.

Im Modul `org.fcrepo.oai.OAIProvider` ggf. adminEmails setzen, außerdem:

    repositoryDomainName = danrw.de
    repositoryName = DA-NRW Presentation Repository

Im Modul org.fcrepo.server.resourceIndex.ResourceIndex den Paramter datastore auf localPostgresMPTTriplestore setzen.

Ggf. die Datenbankeinstellungen (weiter unten in `fedora.fcfg`) im datastore `localPostgresMPTTriplestore` anpassen.
Tomcat neustarten.

Fedora kann jetzt unter folgenden URLs erreicht werden:
* Generelle Informationen: http://<servername>:8080/fedora
* Suchinterface: http://<servername>:8080/fedora/objects
* Admininterface: http://<servername>:8080/fedora/admin

### Policies

Um den Zugriff auf Objekte zu verhindern, die nicht öffentlich zugänglich sein sollen, müssen die entsprechenden XACML-Policies wie folgt installiert werden:
    
Skripte und Policy-Dateien aus dem bazaar-Repository laden

    git clone https://github.com/da-nrw/prepscripts.git

Die Policy-Objekte liegen unter `policies` und können mit dem Skript `scripts/setup-policies.py` geladen werden. Das Package python-httplib2 muss dazu installiert sein. Vor der Ausführung sollte die FedoraClient-URL in den Skriptdateien angepasst werden. Ansclipeßend das Skript ausführen (im Wurzelverzeichnis des prepscripts-Repository:

    python scripts/setup-policies.py

Damit die Policy für die Beschränkung des Zugriffs auf nicht-öffentliche Objekte richtig funktioniert muss folgende Änderung in der Datei `/opt/fedora/pdp/conf/config-attribute-finder.xml` vorgenommen werden:

Die Zeile

    <attribute designator="resource" name="info:fedora/fedora-system:def/model#ownerId"/>
    
muss ersetzt werden durch:

    <attribute designator="resource" name="info:fedora/fedora-system:def/model#ownerId">
        <config name="target" value="object"/>
    </attribute>
    
Dadurch wird sichergestellt, dass die in der Policy `fedora-policy_access-closed-collection.xml` referenzierte Eigenschaft `ownerId` sich bei Datastreams auf das übergeordnete Objekt bezieht. Erst dadurch ist es möglich, dass dem Besitzer eines Objekts auch die zugehörigen Datastreams angezeigt werden.

Default Bootstrap-Policies löschen (sonst werden diese bei jedem Neustart geladen)

    sudo rm -f /opt/fedora/pdp/policies/*

Tomcat neustarten.



