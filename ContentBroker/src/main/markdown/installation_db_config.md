	/*
	  DA-NRW Software Suite | ContentBroker
	  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
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
	
# Database configuration in DNSCore

The ContentBroker needs a database in order to run.
The connection to this database is configurable via 
conf/hibernateCentralDB.cfg.xml (to see a sample file, click [here](../conf/hibernateCentralDB.cfg.xml.inmem)).

## Encrypting a password



## Creating the database schema

If you are setting up your grid for the first time, you can let the ContentBroker help you
in creating the database schema by calling

    java -jar ContentBroker.jar createSchema
    

 


