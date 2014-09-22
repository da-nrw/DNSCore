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

## template-Dateien



## Starting the ContentBroker

    cd [ContentBroker]
    ./ContentBroker_start.sh
    
1. Watch if the ContentBroker comes up with tail -f log/contentbroker.log
1. If everything goes well, you will see him greedily searching for jobs soon.

## Test the ContentBroker

1. Test the software with a testpackage.
  1. Ingest a package
  1. Retrieve a package
  
