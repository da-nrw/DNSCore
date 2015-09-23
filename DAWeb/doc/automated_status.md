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
	
### State calls to DAWEB

(Partly moved to https://github.com/da-nrw/DNSCore/blob/head/ContentBroker/src/main/markdown/feature_automated_queries.md)

The software is able to send back some information on the process being executed when a
SIP is ingested to the DNS. As well as the "final state" residing as AIP on the storage layer 
is being fed back, you are able to determine the URN being assigned to your SIP. 

As far you're are using special, reserved characters (https://tools.ietf.org/html/rfc3986#section-2.2) in parameters of URIs (e.g. such as the plus sign in origNames), they have to be URL endoded as being decoded by default. 

https://Servername/daweb3/status/index?urn=[TheURN]
https://Servername/daweb3/status/index?origName=[Original name the SIP was ingested]
https://Servername/daweb3/status/index?identifier=[DNS identifier] 

The response looks like (JSON): 

{"urn":"urn:nbn:de:danrw-131614-2013111519609","contractor":"TEST","origName":
"testPackage_docx99","identifier":"131614-2013111519609","status":"archived - but in progress","packages":["1“]}

"Status" could be one of:

1. archived  : The object is fully archived and valid
1. archived - but check needed : The object is archived, but a check performed by node admin has to be carried out. 
1. archived - but in progress : The object recieves deltas or is under retrieval 

1. in progress waiting ([internal state code]) : The package is waiting for being picked up by ContenBroker
1. in progress failure ([internal state code]) : The package is in failure state
1. in progress working ([internal state code]) : The package is in working state
