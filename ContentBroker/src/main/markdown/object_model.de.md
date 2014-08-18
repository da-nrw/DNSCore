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

# Objektmodell-Referenz

Die Java [Object](../java/de/uzk/hki/da/model/Object.java) Klasse.

### Object - Das Objekt

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/object_model_objects_packages.jpg)

Die fundamentale Klasse des Datenmodells heisst einfach "Object". Ein Objekt repräsentiert eine logisch zusammenhängende Sammlung von Dateien. Den Zusammenhang der Dateien legt dabei der User fest (siehe pre_ingest.md). Jedes Objekt innerhalb innerhalb eines PreservationSystems trägt einen eindeutigen technischen Identifier, der zum Identifizieren und Auffinden von Objekten innerhalb des PreservationSystems dienen kann. Dieser technische Identifier wird einem User daher  (Contractor) am Ende eines Ingest-Workflows übermittelt.

### Package - Das Informationspaket

### User - Der Benutzer

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/object_model_object_users.jpg)

### System - Das Gesamtsystem
