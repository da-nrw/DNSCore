# Open ports

In order for DNSCore to work properly some ports have to be opened on the firewall protecting your server.

## iRODS

iRODS uses the following ports for communication between the different nodes of a grid:
* TCP 1247
* TCP 20000-20199

## PostgreSQL

On every node that acts as a master iCAT or that holds the central object database:
* TCP 5432
