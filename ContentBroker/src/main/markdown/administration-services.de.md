	/*
	  DA-NRW Software Suite | ContentBroker
	  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
	  Universität zu Köln
	  Copyright (C) 2014 LVRInfoKom
	  Landschaftsverband Rheinland
	
	  This program is free software: you can redistribute it and/or modify
	  it under the terms of the GNU General Public License as published by
	  the Free Software Foundation, either version 3 of the License, or
	  (at your option) any later version.
	
	  This program is distributed in the hope that it will be useful,
	  but WITHOUT ANY WARRANTY; without even the implied warranty of
	  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	  GNU General Public License for more details.
	
	  You should have received a copy of the GNU General Public License
	  along with this program.  If not, see <http://www.gnu.org/licenses/>.
	*/


# Administrationsservices

Um den ContentBroker zu verwalten, existieren einige Schnittstellen. Dies ist natürlich zum einen die DAWeb, mit der der ContentBroker zusammen die DNSCore bildet. Zum anderen existieren eine Reihe von Tools, mit der sich der ContentBroker von der Kommandozeile aus steuern lässt.

    java -jar $JAVA_OPTS ContentBroker.jar
    ./ContentBroker_start.sh
    ./ContentBroker_stop.sh
    java -jar $JAVA_OPTS ContentBroker.jar diagnostics
    java -jar $JAVA_OPTS ContentBroker.jar createSchema (careful)
    ./cbTalk.sh STOP_FACTORY
    ./cbTalk.sh START_FACTORY
    ./cbTalk.sh SHOW_ACTIONS
    ./cbTalk.sh SHOW_VERSION
    ./cbTalk.sh GRACEFUL_SHUTDOWN
    
Vor jedem Start des ContentBroker sollte aufgerufen werden:

    java -jar $JAVA_OPTS ContentBroker.jar diagnostics
    
diagnostics checkt alle wichtigen Subsysteme im Vorfeld durch und gibt somit Aufschluss darüber, ob
die Umgebung (Konfiguration der Maschine, Konfiguration der Applikation) ordnungsgemäß eingerichtet und
der ContentBroker betriebsbereit ist. Diagnostics sollte in jedem Fall sauber durchlaufen, bevor der 
ContentBroker gestartet wird.
    
Um den ContentBroker testweise zu starten, kann er prinzipiell gestartet werden mit 

    java -jar $JAVA_OPTS ContentBroker.jar
    
Die für den Produktivbetrieb einzusetzende Variante ist jedoch

    ./ContentBroker_start.sh
    
ein Wrapperskript mit ein wenig zusätzlicher Funktionalität. Dieses Skript startet den ContentBroker als einen Hintergrundprozess. Ein so gestarteter Prozess kann heruntergefahren werden mit

    ./ContentBroker_stop.sh

**Achtung:** Diese Variante reisst den ContentBroker hart herunter. In Bearbeitung befindliche Jobs werden jäh unterbrochen. Es ist daher dafür zu sorgen, dass vorher alle Jobs sauber heruntergefahren werden.

Dies kann automatisch geschehen durch den Aufruf von

    ./cbTalk.sh GRACEFUL_SHUTDOWN
    
Wenn dieses Kommando aufgerufen wird, wartet der ContentBroker so lange, bis er alle angefangenen Jobs auch ordnungsgemäß beendet hat, und fährt sich dann selbständig herunter.

Um einen Überblick über die laufenden Jobs zu erhalten, kann das cbTalk Skript auch aufgerufen werden mit

    ./cbTalk.sh SHOW_ACTIONS
    
Es ist weiterhin möglich, den ContentBroker zwar eingeschaltet zu lassen, jedoch das anfassen neuer Jobs zu unterbinden

    ./cbTalk.sh STOP_FACTORY
    
sobald kein Job mehr in Arbeit ist, kann er nun entweder heruntergefahren werden, oder veranlasst werden, die Arbeit wieder aufzunehmen mittels

    ./cbTalk.sh START_FACTORY
    
Einige zusätzliche Informationen über den gerade laufenden ContentBroker bekommt man mit

    ./cbTalk.sh SHOW_VERSION

Sollte es sich um eine Erstinstallation der DNSCore handeln, muss für das Gesamtsystem eine zentrale Datenbank eingerichtet werden. Das ContentBroker.jar kann auch verwendet werden, um
das erforderliche Datenbankschema zu kreieren.

        java -jar $JAVA_OPTS ContentBroker.jar createSchema (careful)
        
Voraussetzung ist, dass unter 

    $CONTENTBROKER_HOME/conf/hibernateCentralDB.cfg.xml
    
die Zieldatenbank [ordnungsgemäß](administration-interfaces.md#application-database-configuration) angelegt und beschreibbar ist. Sobald ein Schema kreiert ist, sollte der Administrator die Tabellen gegen weiteren Schreibzugriff sichern.

Eine template der Konfigurationsdatei kann jederzeit heruntergeladen werden:
[hibernateCentralDB.cfg.xml](../xml/hibernateCentralDB.cfg.xml.ci)
    

## Starten und Stoppen der Gesamtapplikation

In einer typischen Knotenumgebung hängt der ContentBroker von weiteren Services ab. Die empfohlene Start- und Stopsequenz lautet daher:

**Starten:**

1. Starten der Datenbank.
2. Starten des Tomcat Application Servers.
2. Starten von iRODS.
3. Ausführen von Diagnostics.
4. Starten des ContentBroker.

**Stoppen:**

1. Warten, bis alle Jobs beendet sind.
2. Herunterfahren des ContentBroker.
3. Herunterfahren von iRODS
4. Herunterfahren des Tomcat Application Server.
5. Herunterfahren der Datenbank.



## Monitoring (TODO translate)

As for now, you have to consider the Queue via at least once a day for only a few minutes to be sure all of your jobs are performing as expected. Most errors on your node are shown at the admin interface of DA-WEB in a very convienent way.  

Please monitor the availibilty on the ports 1247, 8080 . The command 

    ./cbTalk SHOW_VERSION
    
should show some kind of useful information. 

As node admin it you to check your long term resources and the throughput of your whole system. As watermark: there should be not much jobs (less than 20 packages) in your queue in failed or error states where your system has care of. (initial_node)  

