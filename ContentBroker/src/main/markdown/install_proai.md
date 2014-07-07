# OAI Provider

## Installation

Der OAI Provider ist ein separates Servlet, dass die in Fedora bereits vorhandene OAI-PMH Schnittstelle durch eine stabilere und besser konfigurierbare Version ersetzt. Er wird wie folgt installiert:
oaiprovider.war herunterladen

    wget http://downloads.sourceforge.net/fedora-commons/oaiprovider-1.2.2.zip

entpacken

    unzip oaiprovider-1.2.2.zip

war in Tomcat deployen

    sudo -u tomcat cp oaiprovider-1.2.2/oaiprovider.war /opt/tomcat/webapps/

Datenbank und Datenbankuser proai mit Passwort proai anlegen

    adduser proai
    passwd proai
    psql -U postgres template1
    CREATE USER proai WITH PASSWORD 'proai';
    CREATE DATABASE proai;
    GRANT ALL PRIVILEGES ON DATABASE proai TO proai;
    
Ordner für PrOAI-Daten anlegen, z.B:

    mkdir /data/danrw/proai
    

## Konfiguration

Objekt zur Identifikation der OAI-PMH-Schnittstelle in Fedora anlegen (ggf. Passwort in `ingest.py` anpassen).

    python scripts/ingest.py -f config/danrw_PresentationRepository.xml

Objekt zur Beschreibung des DDB-Sets in Fedora anlegen:

    python scripts/ingest.py -f config/set_ddb.xml

Konfigurationsdatei `/opt/tomcat/webapps/oaiprovider/WEB-INF/classes/proai.properties` bearbeiten.

Folgende Einstellungen vornehmen:

    proai.cacheBaseDir = /data/danrw/proai/cache
    proai.sessionBaseDir = /data/danrw/proai/sessions
    proai.schemaDir = /data/danrw/proai/schemas
    driver.fedora.queryFactory = fedora.services.oaiprovider.MPTQueryFactory
    driver.fedora.pass = Passwort des Fedora Admins
    driver.fedora.identify = http://localhost:8080/fedora/objects/\
     danrw:PresentationRepository/datastreams/Identify.xml/content
    driver.fedora.itemID = http://www.openarchives.org/OAI/2.0/identifier
    driver.fedora.md.formats = oai_dc mets ead epicur
    driver.fedora.md.format.mets.loc = http://www.loc.gov/standards/\
     mets/version18/mets.xsd
    driver.fedora.md.format.ead.loc = http://www.landesarchiv-bw.de/sixcms/media.php/\
     120/55633/EAD_DDB_1.1_Findbuch_XSD1.1.xsd
    driver.fedora.md.format.epicur.loc = http://www.persistent-identifier.de/xepicur/version1.0/xepicur.xsd
    driver.fedora.md.format.mets.uri = http://www.loc.gov/METS/
    driver.fedora.md.format.ead.uri = urn:isbn:1-931666-22-9
    driver.fedora.md.format.epicur.uri = urn:nbn:de:1111-2004033116
    driver.fedora.md.format.oai_dc.dissType = info:fedora/*/DC
    driver.fedora.md.format.mets.dissType = info:fedora/*/METS
    driver.fedora.md.format.ead.dissType = info:fedora/*/EAD
    driver.fedora.md.format.epicur.dissType = info:fedora/*/epicur.xml
    driver.fedora.mpt.jdbc.user = fedora
    driver.fedora.mpt.jdbc.password = Password des DB-Users für die Tabelle riTriples

Die Datei mit den entsprechenden Einstellungen befindet sich auch im Bazaar-Repository unter `config/proai.properties`

Tomcat neustarten.


## Logging

Der OAI Provider loggt standardmäßig zu stdout, d.h. in die Datei `/opt/tomcat/log/catalina.out`. Das Loggingverhalten kann in `/opt/tomcat/webapps/oaiprovider/WEB-INF/classes/log4j.xml` angepasst werden.

