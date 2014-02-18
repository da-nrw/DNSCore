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
	
## Automated Retrieval 

Retrieval requests by external systems can be issued by POST requests to an RESTful interface 
which is available at the URL  https://Servername/daweb3/automatedRetrieval/queueForRetrievalJSON

The JSON POST Data must at least contain one of the following fields: 
URN, IDENTIFIER, ORGINALNAME. 

The original name should be the name, the item is listed in your own domain, while 
the other identifiers (identifier and urn) are build during the ingest process.  

Example: 

{"urn":"urn:nbn:de:danrw-131614-2013111519609","origName":"testPackage_docx99","identifier":"131614-2013111519609"}