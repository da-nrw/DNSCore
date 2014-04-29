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
	
### Administration of DNS (using DA-Web)

Users being "Administrators" are able to perform several tasks a normal user can't.
Some of the features being available are listed below.

#### View CB Error Messages 

Error Messages of CB are listed in the frontend being available as "Adminstrative Funktionen" 
on Homepage in case you are logged in as admin role. 

The newest message is on top. The reason for listing is to make debugging 
of errors more easily and (in case you've more than one node to administer) to view errors 
of other nodes as well. 

#### Starting / Stopping CB-Factory 

In case of shutting down CB, you should use the Stop Factory button, 
avoiding uncontrolled interruption of work done by CB. After hitting the button "stop factory "
CB will stop working after having performed all running tasks completely. 

#### Show Actions 

To view what your CB process actually is performing you may hit the "show actions" button. 

#### Graceful CB shutdown 

Hitting this button will cause CB to stop main execution thread after having completed all 
running actions. Please notice : There might be still processes called 

<pre>java -jar ContenBroker.jar</pre>

Please execute after doing graceful shutdown
<pre>ContentBroker_stop.sh</pre> otherwise starting ContentBroker will be denied.
 
#### Reload PIP

For convinence Admins can perform a rebuild of PIP (Presentation  Information packages). 
The PIP is being built on basis of the latest version, including all deltas. This feature is accessible on the "list objects" view for admin users only.

#### Reindex Elasticsearch

For convinence Admins can perform a rebuild of elastic search index insertion on basis of latest
PIP. This feature is accessible on the "list objects" view for admin users only.

#### Manually checking status of AIP

Although automated service is carrying out integrity checks on AIP stored in the repository all 
the time, administrators can perform checks on demand as well. This feature is accessible on the "list objects" view for admin users only.


#### Recover and Deletion of entries

Admins can perform adequate recover processes if they could be carried out by the system.  This is being indicated by buttons.  This feature is accessible on the "queue list" view for admin users only.
