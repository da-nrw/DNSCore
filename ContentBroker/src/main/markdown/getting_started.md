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

# Getting started

The purpose of this document is to demonstrate the quickest possible way of getting started with the 
software, allowing for an evaluation of the software. To achieve this the software is installed and configured
in a minimalistic way, providing only the core feature set, without presentation and advanced storage layer with 
replications and so on. It is an easy single node installation which should work on every box running mac or linux.
Though we will provide a VM in near future for evaluation purposes, it is necessary to fully understand the installation
process in detail if you want to use the software in production later. This course will give future admins the easiest possible
walkthrough in setting up and understanding the basic behaviour and configuration possibilities of the system.

## Prerequisites

* Python > 2.7
* Java 1.6
* ImageMagick

## Preparation

1. Download an installer for the newest stable version of the software from the releases section of this repository.
1. Put it somewhere onto your machine, for example into /tmp
1. Prepare your installation and storage directories

[somewhere]/ContentBroker/
[somewhere]/storage/
                    user/
                    ingest/
                    work/
                    dips/
                    grid/                  

## Configure the application

1. Download a blank config.properties
1. Fill in the information

localNode.userAreaRootPath=[somewhere]/storage/user
localNode.ingestAreaRootPath=[somewhere]/storage/ingest
localNode.workAreaRootPath=[somewhere]/storage/work
localNode.dipAreaRootPath=[somewhere]/storage/dip
localNode.gridCacheAreaRootPath=[somewhere]/storage/grid


TODO fake ffmpeg.sh

## Prepare the database with minimal configuration

1. Create a new database (postgresql only at the moment) called contentbroker.
1. Create a database user called cb_usr.
1. Ask our team for a dump of a basic database schema. We'll discussing various solutions to automatize this step, but for the moment asking
   for a dump is the way to go.

1. Download hibernate.cfg.xml.inmem TODO
1. Rename it to hibernate.cfg.xml
1. Edit the following entries to match your current database settings.

<property name="connection.url">jdbc:hsqldb:mem:QueueDB</property>
<property name="connection.username">sa</property>
<property name="connection.password"></property>

## Install and test the software

1. Install the software using the install script according to the steps documented at TODO link to Installation
1. Test the software with a testpackage.
  1. Ingest a package
  1. Retrieve a package
