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
	
## Configure Runtime Settings of DA-Web

Several runtime settings are needed by DA-Web. All the parameters needed for the app have to reside under 
the Tomcat Server's home in folder .grails, assuming the Tomcat's servers home at /home/tomcat/, there must be a file
called /home/tomcat/.grails/daweb3_properties.groovy. 
Most of the parameters are the same as in config.properties of ContentBroker. 
A template can be found here: [daweb3_properties.groovy](daweb3_properties.groovy.dev)
	
## Deploy Da-Web3 WAR

### Build Da-Web3

You may need to copy the application.properties.template file to application.properties if you want 
to build DA-Web on your own.

In normal build processes this is done automatically by the install processes called in
the maven build process. If you want to build DA-Web as isolated project, you will need 
to have GRAILS installed on your command line, while the project itself is not mavenized 
yet. 


Builds without having a related build of CB are strongly discouraged, while the both 
applications share the same model. 

The command 
<pre>grails war prod</pre>
war will build the target file for you. 

The build.sh script found in the main dir of the appication is called during maven build on
ContentBroker. 

### Running DAWeb locally

The DaWeb interface could be executed locally with command 
<pre>grails dev run-app</pre>
