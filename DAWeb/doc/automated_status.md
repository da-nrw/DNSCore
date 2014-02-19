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

The software is able to send back some information on the process being executed when a
SIP is ingested to the DNS. As well as the "final state" residing as AIP on the storage layer 
is being fed back, you are able to determine the URN being assigned to your SIP. 

https://Servername/daweb3/status/index?urn=[TheURN]
https://Servername/daweb3/status/index?origName=[Original name the SIP was ingested]
https://Servername/daweb3/status/index?identifier=[DNS identifier] 

The response looks like (JSON): 

{"urn":"urn:nbn:de:danrw-131614-2013111519609","contractor":"TEST","origName":
"testPackage_docx99","identifier":"131614-2013111519609","status":"archived - but in progress","packages":["1“]}

"Status" could be one of:

archived 
archived - but check needed
archived - but in progress

in progress waiting ([internal state code])
in progress failure ([internal state code])
in progress working ([internal state code])
