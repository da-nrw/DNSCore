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

### Prerequisites

- Oracle Java >= 1.8 
- Tomcat 7 or higher
- Grails 3.2.11 and Groovy 2.4.11 and Gradle 3.4.1  (for building the project)
- phantomJS for functional testing

### Configure Runtime Settings of DA-Web

Several runtime settings are needed by DA-Web. All the parameters needed for the app have to reside under the Tomcat Server's home in folder .grails, assuming the Tomcat's servers home at /home/tomcat/, there must be a file *called /home/tomcat/.grails/daweb3_properties.groovy*.
Most of the parameters are the same as in config.properties of ContentBroker.
A documented template can be found here: [daweb3_properties.groovy](./daweb3_properties.groovy)

### Build DA-Web WAR

In normal build processes this is done automatically by the install processes called in
the maven build process. 

If you want to build DA-Web as isolated project, you will need to have GRAILS installed on your command line, the project itself is mavenized.
Builds without having a related build of CB are strongly discouraged, while the both
applications share the same model.
The command	*mvn install*  the war-file will build the target file for you.
Pay attention to config files! After the frist checkout some "magic" Script of maven grails plugin may alter them. Please consider a git reset hard after first checkout!

If you don't want to build with maven grails 3.2.11 brings gradle. SO you can clear your project with the command *gradle clean*. With the command *gradle build* you will make a full build for the project. The created files are lying under */DAWeb/build*. The created war-file could be find */DAWeb/build/lib*

### Deploy and Running DAWeb locally

The DaWeb interface could be executed locally with command *grails run-app* but befor you can run it you have to build it like described below.

### Encode Database Password

To encode your own DB Password for production, you must have a groovy compiler (and at least a checkout of the class) run
groovy grails-app/utils/de/uzk/hki/da/utils/DESCodec.groovy <your password>