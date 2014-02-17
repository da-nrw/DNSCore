# Object model reference

### Object

The most fundamental entity in our data model is simply called the "object". 
An object as a concept helps to describe a logically coherent set of files (possibly ordered within folder structures). 
What makes these files coherent is totally up to the user. It can be a single picture with some metadata or a database 
serialized in an xml format or a book consisting of jpg files and bound together by a METS metadata file. Or whatever
the user considers a unit of works worth preserving.

While the software is based on the OAIS reference model, the OAIS model is not sufficient to describe certain
of the architectural and conceptual aspects of the concrete software implementation DNSCore. Leaving the PIPs (TODO)
aside for a moment, this is mainly due to the concepts of deltas which is was a strongly wanted by the community in which
the software grew up and henceforth deeply integrated into the heart of the software from its beginnings. A delta lets 
users make additions to objects and this leads to the situation that an object can consist of many AIPs at a time,
build up by ingesting many SIPs. The user is then able to retrieve the object as a DIP or PIP which is in our technical
terms a view of an object. 

So now that we know what an object should model, we should discuss which artifacts actually constitute an object
and were we find them.

Developers find the the object implementation [here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/java/de/uzk/hki/da/model/Object.java).


---------------
A data package
which resided inside the system is called an AIP and one that is send back to the user is called a DIP.
We devided DIPs further for our purposes into DIPs and PIPs.
---------------

Todo oid and csn + orig_name
plays a role with deltas

### Package

### Representation

a representation is a model entity of second order so to speak and
is not modeled as a java class in its own right. one can find them
only as folders in the file system ...

### DAFile

### Contractor

### ConversionRoutine

### ConversionPolicy



