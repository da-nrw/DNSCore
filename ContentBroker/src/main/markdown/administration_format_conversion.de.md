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

# Formatkonversion mit DNSCore

Formatkonversionen in DNSCore basieren auf einem Modell von [Konversionsrichtlinien](object_model.de.md#conversionpolicy---die-regel-zur-anwendung-einer-konversion) (ConversionPolicies) und [Konversionsroutinen](object_model.de.md#conversionroutine---die-konversionsroutine). Konversionsroutinen beschreiben ein Verfahren, mit dessen Hilfe eine Datei eines bestimmten Formates in ein anderes Zielformat konvertiert werden kann. Konversionsrichtlinien hingegen legen fest, welche Konversionsroutinen für Dateien mit bestimmten Dateiformaten durchzuführen sind, nachdem ebendiese Dateiformate vom System erkannt wurden.

![Bild](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/object_model_object_users.jpg)

Sowohl **Konversionsrichtlinien** als auch **Konversionsroutinen** sind Eigenschaften des **Gesamtsystems**. Wenn eine Konversionsroutine im System angemeldet wird, so muss sichergestellt werden, dass alle **Knoten** des Systems diese unterstützen. 
(TODO was heisst das , Konverter vs. Java-Konversionen)

## Einrichten / DB

## Hinweise zum Testen

## Workflow des Systems zur Formatkonversion.

![Formatkonversionsworkflow](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/format_conversion_workflow.jpg)

Ingest
Migration
PIPGenerierung

## Funktionsweise

Generierung der Konversionsinstruktionen



