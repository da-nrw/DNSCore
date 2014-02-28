# The format module of DNSCore

The DNSCore system has facilities to automatically convert files contained in SIPs during ingest.
This is done with the help of various converters which can be plugged in via global system configurations.
The converters to be plugged in are required to be able to run as unix command line tools.

The basic pattern for a conversion is as follows:

    2012_12_12+12_12+a/input.jpg -> 2012_12_12+12_12+b/input.tif

We see the input and output files to reside in different
[representation](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/aip_specification.md#representations----restructuring-contents-with-representations) 
folders (also have a look at [representations](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/object_model.md#representation) 
in the context of our object model). While the +a-representation contains the original files, the +b-representation contains 
the converted file. The example you see above is to demonstrate the basic point of the conversion system how it is implemented for now:
The system mainly does conversions in the form "one in one out". Merging audio and video
streams to a single format, converting dvd or video cd contents to simple video containers are not supported yet. Deviances from the
standard behaviour are only supported for some special cases.

## How it works - A simple example

To comprehend the whole conversion mechanism it is best to imagine the simplest possible scenario.
Lets have a close look which concepts you have to understand, which steps the system takes and which configurations are necessary
in order to perform the simple conversion (jpg->tif) from the above mentioned example. 

The first step the ContentBroker (via FIDO) performs is to determine the [PRONOM](http://www.nationalarchives.gov.uk/PRONOM/Default.aspx) 
PUID for every file found in the +a-representation. For a file in the jpg format version 1.00
FIDO will report an identifier fmt/42 back to the ContentBroker, which in turn uses this identifier to check if there is a special
[ConversionPolicy](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/object_model.md#conversionpolicy) 
matching this identifier. Here is an example of a matching **ConversionPolicy** which demonstrates their basic form:

    id: 1
    source_format: fmt/42
    contractor_id: 1
    conversion_routine_id: 1

This means that if the system has a file for which an identifier matching a certain policy could be found, the system
will perform a [ConversionRoutine](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/object_model.md#conversionroutine) 
with a certain id during the conversion phase. The corresponding **ConversionRoutine** could
have the following form:

    id: 1
    name: CLI
    type: de.uzk.hki.da.convert.CLIConversionStrategy
    target_suffix: tif
    params: convert input output

The system will look for a **ConversionRoutine** with the conversion_routine_id specified in the policy. It will then
execute a special method in a java class with the class name de.uzk.hki.da.convert.CLIConversionStrategy. This
class can handle calls to the command line. It will replace the placeholders input and output by actual filenames and
will replace the suffix of the original file by the given target_suffix. The order which the class then sends
to the command line will look something like that convert 2012_12_12+12_12+a/input.jpg 2012_12_12+12_12+b/input.tif.
This is a call to ImageMagick which does the actual conversion work, resulting in a new file placed in the 
+b-representation. The class acted as wrapper for ImageMagick in this scenario, so to speak. 

TODO conversion instruction

TODO summary - what is needed


## The different Types of ConversionRoutines

* TODO

## Adding a ConversionRoutine - Step by Step

1. A domain expert has to make the decision that an additional conversion facility is needed.
1. If the conversion can be done on command line, he tests it first locally and specifies the needed **params**.
1. The domain expert selects the appropriate **ConversionStrategy** and/or command line tool for the job.

1. To test the new routine on a test system, the database configuration has to be made:
<pre>   
    INSERT INTO conversion_routines (id,type,params,target_suffix)
    id -> new id
    type -> choose a type from the available ConversionStragegies
    params -> params needed to configure the selected ConversionStrategy
    target_suffix -> Some routines can be configured with an entry here which determines the target format automatically. Others won't need it.
</pre>
1. In order to get visibly to the applications running on the nodes, 
the ConversionRoutine has to be connected to the nodes. this has to be done for **every** node in a system.
<pre>
    INSERT INTO conversion_routines_nodes (conversion_routines_id,nodes_id)
    conversion_routines_id -> the id of the new routine
    nodes_id -> the node id 
</pre>
1. The domain expert tests the routine on a test machine.   
1. He documents the new ConversionRoutine.
1. For production: In case a new command line tool is required, it has to be installed on **all** nodes of the system by the node admins.
1. The database configuration on the production system has to made accordingly.

## Adding a ConversionPolicy - Step by Step

1. A domain expert has to make the decision that all files of a given format have to be converted automatically by the system during ingest.
1. He has to identify all the pronom PUIDs which belong to the format.
1. For **every** PUID he has to create one ConversionRoutine.
1. The system admin has to create the database entries 
<pre>
    INSERT INTO conversion_policies (id,puid,conversion_routines_id,contractor_id)
    id -> new id
    puid -> the format identifier
    conversion_routines_id -> the link to a existing ConversionRoutine
    contractor_id -> dependet of wheter it is used to create conversions for archival or publication, the contractor_id has to be either
    the one of DEFAULT or presenter.
</pre>

## Special Cases

* TODO multiple page tiffs


