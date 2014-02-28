	/*
	  DA-NRW Software Suite | ContentBroker
	  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
	  Universität zu Köln
	
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
	
# Components Connectors

![](https://raw.github.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/components_connectors.jpg)

The sketch describes the main components of a 
[system](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/object_model.md#system). Not every 
[node](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/object_model.md#node) has to operate all of the components.
There are different congigurations possible for a node to participate in the system.

Some components are shown more than once to signal that nodes of the system can provide services and share components with other nodes. The arrows indicate where one component consumes services of another component.

## Normal Node

A normal node consists of the components DAWeb, ContentBroker and iRODS. 
It does not host the iRODS database nor the ObjectDB/ProcessDB.

## Master node

A master node consists of the components DAWeb, ContentBroker, iRODS, ObjectDB/ProcessDB and iCAT.
Since it hosts the main database and the iCAT, it acts as a service provider not only for components
running on this node, but for components of other nodes as well.

## Presentation Node

A typical presentation node hosts the components ContentBroker, iRODS, elasticsearch and Fedora. It is also
possible for a "normal" node to get extended by presentation facilities. Such a node would then also run DAWeb.

## iRODS topologies

It is not necessary that a master node hosts both databases, the ObjectDB/ProcessDB and the iCAT.
One can also split up the master functionality by letting one node host the first and another node host the other 
database.

Note that DNSCore also supports the federation topology of iRODS, in which it is necessary for every node to
host an iCAT database.






