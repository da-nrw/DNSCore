	/*
	  DA-NRW Software Suite | ContentBroker
	  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
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

# Der DNSCore-Installer

DNSCore kann zu jedem Zeitpunkt mithilfe der Maven Skripte gebaut werden. Als Ergebnis dessen steht am Ende ein Installer,
welcher auf die Zielmaschine kopiert wird. Dieser Installer enthält das Script

    $INSTALLER_HOME/install.sh

Das Script kann zur vollständigen Neuinstallation des CB, 
jedoch genauso zum Update einer bestehenden Installation des CB verwendet werden.

In jedem Fall muss vor dem Ausführen des Scriptes das Zielverzeichnis, hier bezeichnet mit

    $CB_HOME
    
vorhanden sein. Wenn es nicht existiert, sollte es zuvor erstellt werden.

Wenn der Installer dann ausgeführt wird mittels

    $INSTALLER_HOME/install.sh
    
wird man aufgefordert, das gewünschte Featureset anzugeben. Hier hat man die Auswahl zwischen den Optionen (f)ull, (p)res und
(n)ode. Die Erklärung für diese Modi findet sich [hier](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/administration-dnscore-modes.de.md).

## Anpassen einer frischen Installation

Im Falle einer Erstinstallation befindet sich in

    $CB_HOME
    
nun eine vollständige ContentBroker Installation. Der ContentBroker muss nun konfiguriert werden über die Dateien
    
    conf/hibernateCentralDB.cfg.xml
    conf/config.properties

## Update einer bestehenden Installation

Das Installationskript liefert immer aktuelle Versionen der folgenden Dateien mit

    $CB_HOME/ContentBroker_start.sh.template
    $CB_HOME/ContentBroker_stop.sh.template
    $CB_HOME/conf/logback.xml.template

Diese werden bei der Erstinstallation automatisch umkopiert in

    $CB_HOME/ContentBroker_start.sh
    $CB_HOME/ContentBroker_stop.sh
    $CB_HOME/logback.xml
    
Dem Administrator steht es frei, diese Dateien nach seinem Gusto zu modifizieren. Z.B. können in der logback.xml
die Loggereinstellungen angepasst werden. 

Bei einem Update werden die letztgenannten Dateien nicht überschrieben, sondern so belassen, wie sie sind. Der Installer
liefert lediglich die template-Dateien nach, so dass der Administrator bei Bedarf die neuesten Stände dieser Dateien zur Verfügung hat.

## Installation - Vorgehensweise

1. Zunächst sollte ein bestehender CB-Prozess heruntergefahren werden mit

    ./ContentBroker_stop.sh
    
2. Ausführen des Installers

    ./install.sh $CB_HOME
    
3. Auswahl des Feature Sets.

4. Test des CB mittels

    cd $CB_HOME
    java -jar ContentBroker.jar diagnostics

5. Wenn das durchläuft, hochfahren des ContentBroker mittels

    ./ContentBroker_start.sh

6. Nachschauen, ob alles läuft mit

    tail -f log $CB_HOME/log/contentbroker.log
    
Wenn es läuft, sucht er nach Jobs.

7. Ausführen des Basistests: Einspielen und Retrieven eines Paketes.
  
