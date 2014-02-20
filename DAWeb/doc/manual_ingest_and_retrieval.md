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
	
# Ingest and Retrieval

## Ingest 

The following chapter describes manual ingestion of SIP. Automatic ingestion is possible via the 
internal interface of CB. Please refer to CB's documentation. 

### Manual ingestion of SIP 

The ingest of SIPs into the system by users is done in two steps. First users upload their
package onto a file share to their node. This step relies on technical configurations
(transport protocol: for example WEBDav, SFTP)
 of the node which the administrator provides and which are based on an agreement 
between the node owner and the contractors which deliver content to this node. 
Because of this the specific transport protocol cannot be part of the documentation of
DNSCore.
The packages are then transported via the transport protocol of choice to the 
[UserArea](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/processing_stages.md#userarea). While UserArea is the technical term for the incoming
storage space, contractors only have to think of it as their web share to the system.
The contents of their respective webshare can be seen by contractors directly when
they upload their content with a tool of choice (for example FileZilla for SFTP),
but also is presented by DAWeb, where the second step of manual ingest takes place.
Here users can mark packages as ready for ingest, which DAWeb then signals to the 
other components of the node, which then extract the package from the share for 
further processing.

#### Ingest Step by Step

##### Prerequisites

1. You have recieved log in data for the system by an administrator.
2. You have recieved log in data for the web share for your node by an administrator.
3. You have mounted the webshare on your local box or installed a file transfer program.

#####

1. Choose a package for upload.
1. Mount your webshare on your box or open and log in into your file transfer program.
1. Upload the package to the incoming directory of your webshare.
1. Wait until the tranfer of the package has been completed.
1. Log into DAWeb on your node.
1. Click "Verarbeitung für abgelieferte SIP starten" on your starting screen.
1. Mark the package your uploaded and hit "Start" (see graphic below)
1. Verify that the entry for the file disappears.
1. Wait for an email which informs you about success or failure of the ingest process.

![](https://raw2.github.com/da-nrw/DNSCore/master/DAWeb/doc/ingest_1.png)

## Retrieval 

Once you have ingested a package into the system successfully, which you know by
recieving an email report for the object, you can search the system for it and retrieve
it. How this is done is described by the following steps

#### Steps

1. Log in to DAWeb on your node.
2. Go to the "Eingelieferte Objekte (AIP)" section.
3. Search your object by filtering for it by object identifier (which you take from the email)
1. Click the "Anfordern" button of your object.
1. Wait for another email which confirms your object has successfully been retrieved and is ready for download
1. Go to the starting page of DAWeb again and then select "Objekt entnehmen (DIP)".
1. Click the link for your object and your browser should start do download the 
DIP for your object immediately.
1. To save resources, the system will remove the DIP automatically from the web share/UserArea 24h after you have downloaded it.

Note that requested packages are visible not only on the "Objekt entnehmen (DIP)" view but also in the outgoing folder of the contractors web share.


## User Reports via Email

TODO for ingest
TODO for errors during ingest
TODO troobleshooting when no email





