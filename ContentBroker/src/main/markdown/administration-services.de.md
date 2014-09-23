	/*
	  DA-NRW Software Suite | ContentBroker
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
    ./cbTalk.sh SHOW_VERSION
    ./cbTalk.sh GRACEFUL_SHUTDOWN
    
Um den ContentBroker testweise zu starten, kann er prinzipiell gestartet werden mit 

    java -jar $JAVA_OPTS ContentBroker.jar
    
Die für den Produktivbetrieb einzusetzende Variante ist jedoch

    ./ContentBroker_start.sh
    
ein Wrapperskript mit ein wenig zusätzlicher Funktionalität. Dieses Skript startet den ContentBroker als einen Hintergrundprozess. Ein so gestarteter Prozess kann heruntergefahren werden mit

    ./ContentBroker_stop.sh

**Achtung:** Diese Variante reisst den ContentBroker hart herunter. In Bearbeitung befindliche Jobs werden jäh unterbrochen. Es ist daher dafür zu sorgen, dass vorher alle Jobs sauber heruntergefahren werden (s.u.).










    
Diagnostics help to check the state of the environment and should be executed before starting the ContentBroker. Only when the diagnostics show everything is ok, the ContentBroker should be started. CreateSchema helps the admin to create the right database schema on new nodes.


## Starting the ContentBroker

In order to start the ContentBroker, other services have to be started anterior. The complete Startup sequence is:

1. Start the database.
1. Start iRODS.
1. Start the Tomcat application server.
1. cd $CONTENTBROKER_HOME
1. java -jar $JAVA_OPTS ContentBroker.jar diagnostics
1. ./ContentBroker_start.sh

## Shutting down the ContentBroker

Please stop the following applications in sequence: (when available) 

1. graceful shutdown on ActionFactory (via DAWEB or ./cbTalk GRACEFUL_SHUTDOWN)
2. Be sure, no jobs are currently processed.
1. stop Tomcat
1. stop ContentBroker
2. stop iRODS
3. stop Database

## Monitoring

As for now, you have to consider the Queue via at least once a day for only a few minutes to be sure all of your jobs are performing as expected. Most errors on your node are shown at the admin interface of DA-WEB in a very convienent way.  

Please monitor the availibilty on the ports 1247, 8080 . The command 

    ./cbTalk SHOW_VERSION
    
should show some kind of useful information. 

As node admin it you to check your long term resources and the throughput of your whole system. As watermark: there should be not much jobs (less than 20 packages) in your queue in failed or error states where your system has care of. (initial_node)  

## Creating the database Schema

If you are setting up a new node and this node should be the primary node (which is hosting the object database) of the system, then you have to create a database and create the correct schema in order for all the nodes to work within the system.

You can create the schema by 'cd'ing into the $CONTENTBROKER_HOME and calling

    java -jar $JAVA_OPTS ContentBroker.jar createSchema

Make sure that you have created a database and a user prior to calling that command. The connection to that database has to be configured in 

    $CONTENTBROKER_HOME/conf/hibernateCentralDB.cfg.xml
    
If you don't have this file, you can download the file [hibernateCentralDB.cfg.xml](../xml/hibernateCentralDB.cfg.xml.ci)
    
and use it as a template.
For more infos about how to configure that file, see this [document](administration-interfaces.md#application-database-configuration)

