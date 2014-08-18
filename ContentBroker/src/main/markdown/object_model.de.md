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

Ein Benutzer ist ein Anwender der Applikation. Endnutzer verwenden das System auf verschiedene Art und Weise, je nach Rolle. Derzeit sind drei Benutzerrollen möglich:

* PreservationSystem Administrator - Fachlicher Ansprechpartner für ein PreservationSystem
* Node Administrator - Technischer Ansprechpartner für einen Knoten
* Contractor - 

Contractoren sind die Endanwender der Applikation. Sie liefern ihre Pakete immer auf jeweils einem Knoten ein.

### PreservationSystem - Das Gesamtsystem

Die Klasse PreservationSystem repräsentiert das Gesamtsystem. Es besteht aus einzelnen Knoten, auf denen Contractoren ihre Objekte einliefern. Das Gesamtsystem hält die global gültigen Regeln zur Anwendung von Konversionen vor und bestimmt, welche Konversionsroutinen von allen Knoten gleichermaßen angeboten werden. 

### Node - Der Knoten

An einem Knoten liefern Contractoren ihre Objekte ein. Der Knoten steht dabei für ein konkretes technisches System am Standort eines Betreibers. Verschiedene Knoten verschiedener Betreiber an verschiedenen Standorten formen ein PreservationSystem. 

### ConversionRoutine - Die Konversionsroutine

### ConversionPolicy - Die Regel zur Anwendung einer Konversion

### DAFile
