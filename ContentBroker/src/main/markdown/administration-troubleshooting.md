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

As you might have already read the WorkArea is the space where the ContentBroker manipulates the contents of objects. In a step by step
transition, it transforms contents of objects. Depending on the workflow, that means adding data
(by conversion or adding metadata), or removing of data (for DIPs or presentation). In any case the main unit
of operation is an unpacked object which comes either from the IngestArea or the LZAArea or both (in case of deltas).
To achieve the goal of transforming an object into a desired final state, the object has to go through one of
the possible workflows of the ContentBroker. Any workflow consists of small, well defined steps. Because
the actions in workflows can vary slightly over time, we will try to not mention the specifics in this document.
Instead, please consider the sources if you want to know more about the structure of the workflows:

https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/resources/META-INF/beans-workflow.ingest.xml (Ingest-Workflow)

### Diagnostics mode in ContentBroker - Smoke test

To perform basis tests on your configured ContentBroker you could run the CB in diagnostics mode

    java -jar ContentBroker.jar diagnostics
    
The fature performs some basic tests on your node. The result depends on your installation scope (presentational node, simple node or fully fledged) but should always give OK results as shown below (shown from a fully fledged version)  

    Smoke test the application
    CHECKING JHOVE: OK
    CHECKING LOCAL NODE PATHS: OK
    CHECKING IRODS CONNECTION: OK
    CHECKING GRID FACADE PUT: OK
    CHECKING GRID FACADE GET: OK
    CHECKING PRONOM FORMAT IDENTIFIER: OK
    CHECKING VIDEO FORMAT IDENTIFIER: OK
    CHECKING FEDORA CONNECTIVITY: OK
    There were no errors.


### Ingest Workflow - Error handling

The reasons for an action to result in an error state could be various. Most of the times one encounters an action
in an error state xx1 or xx3 is when one of the services the ContentBroker depends on is not reachable or acts not as expected
(for example when a converter fails to convert a document). Normally it is a good advise for administrators new to the system
to see the logfile for the object before taking any further action, in order to understand the possible causes of errors better
and gain experience how to deal with certain error states. In order to find the logs for a given object, DAWeb shows the objects
technical id [oid] at the left hands side of a queue entries column. On your local file system you'll find the corresponding log at
[ContentBroker]/log/object-logs/[oid].log. 

#### xx0 
Any state ending with 0 means the object is in a well defined state, waiting for the ContentBroker to grab the object to take further
actions at any time the ContentBroker decides it has free capacities to to so.

#### xx1
Any state ending with 1 means there was an error, but the ContentBroker was able to execute the action specific rollback mechanism so
the unpacked object is an well defined state again. So, a job in state 120 and a job in state 121 correspond both to an object in the same
physical file system and database state.

#### xx2
This is the working state. Normally shown, while CB is working. 

#### xx4
These are states where an error occured due to imcomplete or inconsistent data caused by the user. The xx4 states always result 
of an action throwing a UserException. In case of such an exception the system autogenerates an error reports which gets instantly
delivered to the user via email. If DAWeb encounters an action in a xx4 state, it presents a "delete" button to the admins who are free
then to clean up the object from the queue. Depending if the object is a new one or a delta to an existing one either the newest package
or the whole objects gets deleted from the database if one clicks the delete button. Also the WorkArea gets clean up. In the case it was
a new object the orig name is reusable again. The urn which was given to the object is waste.

#### 123 - 353 (only xx3)

Failures in the error range 123-383 could be handled with DA-WEB. 
Whenever possible, Deletion buttons are shown. 

For recovering bunches of Packages you might consider a rollback via Database. 
Just set the packages in state '600' for a clean rollback, BUT check the error 
and the error codes first!

#### Deletion

Sometimes manuallay deletion of packages is needed on your system. 
Please examine the error logfiles at your node. 

For recovering bunches of Packages you might consider a rollback via Database. 
Just set the packages in state '800' for a clean deletion, BUT check the error 
and the error codes first! Only packages lower then 400 are deleteable, because otherwise they are archived!

#### Error Handling in DA-WEB

Please refer to the documents added under Daweb/docs

## Logging

(Taken from and referred to your systems logback.conf)

ContentBroker main logfile, first look here:

    log/contentbroker.log
 
The irods-ContentBroker bridge
    
    log/grid.log
    
One logfile per each Objekt(Package) refer to it from the DA-WEB Queue view. 

    log/object-logs/[oid].log
    
The audit errors are listed here

     log/audit.log
     
For an overview how to start services look [here](administration-services.md)


