	/*
	  DA-NRW Software Suite | ContentBroker
	  Copyright (C) 2014 LVRInfoKom
	  Landschaftsverband Rheinland
	
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


# Services Administration

There are certain artifacts that are designed to control DNSCore from the command line.
 
    ./ContentBroker_start.sh.template
    ./ContentBroker_stop.sh template
    ./cbTalk.sh
    




## Starting the ContentBroker

In order to start the ContentBroker, other services have to be started anteriour.






Please stop the following applications in sequence: (when available) 

1. graceful shutdown on ActionFactory (via DAWEB or ./cbTalk GRACEFUL_SHUTDOWN)
2. Be sure, no jobs are currently processed.
1. stop Tomcat
1. stop ContentBroker
2. stop iRODS
3. stop Database


Please start the following applications in sequence:

1. start Database
1. start iRODS
2. start ContentBroker
1. start Tomcat


## Monitoring

As for now, you have to consider the Queue via at least once a day for only a few minutes to be sure all of your jobs are performing as expected. Most errors on your node are shown at the admin interface of DA-WEB in a very convienent way.  

Please monitor the availibilty on the ports 1247, 8080 . The command 

    ./cbTalk SHOW_VERSION
    
should show some kind of useful information. 

As node admin it you to check your long term resources and the throughput of your whole system. As watermark: there should be not much jobs (less than 20 packages) in your queue in failed or error states where your system has care of. (initial_node)  

