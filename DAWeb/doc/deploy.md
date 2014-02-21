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
	
# Deploy DAWeb

## Prerequisites

* Oracle Java 1.6 
* Tomcat6

### Installation

We added a configuration system under which you might be able to alter most of the 
parameters being used in the webapp. 
The file MUST be located under the tomcat owner's home directory (e.g. User "tomcat")
This file has to be just readable by the Tomcat process owner. 
You can find the file in the main folder daweb as well. The file lists the following properties:
Although you might be able to add three environments, in fact the productional environment 
is used in normal software releases only. Of course you might be able to use the same productional
build in several environments as it depends only on the values added to properties file. 
In most cases dropping the built war into your servlet container and having the 
<pre>daweb_properties.groovy</pre> in place would be sufficient to run the application. Please
undeploy former versions of daweb completely. Please keep in mind: without having the properties file in place, the webapp won't start 
at all. 


#### Step by step

1. Download a properties file template from 
[here](https://github.com/da-nrw/DNSCore/blob/master/DAWeb/daweb3_properties.groovy.dev).
1. Save the file as 
2. 
    /home/tomcat/.grails/daweb_properties.groovy

1. Open it in an editor.
1. Delete the blocks for the "test" and "development" environments
1. Fill in the property params. Most of the params being used must have the same values like they've in the adjacent contentbroker
file "config.properties". 
1. Put the daweb3.war container from your installer into the tomcat web-apps folder.
1. Restart your tomcat.

To encode your own DB Password, you could use the command:

    groovy de.uzk.hki.da.utils.DESCodec <your_password>
