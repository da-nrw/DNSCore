# DIP Specification

## Substitution rules and surface view of an object

When accessing content in DNSCore, either in its form as DIP or in its form as PIP, what users get
in most situations is a so called surface view of an object. This surface view gets automatically generated
by the system based on the data contained within the AIP(s). 

What a user might get when retrieving an object might look like:

    [oid].tar
        [oid]/
              bagit.txt
              manifest-md5.txt
              bag-info.txt
              tagmanifest-md5.txt
              data/
                   premis.xml
                   abc.tif
                   efg.tif
                   subdir/cde.tif
                   
What we have here is a tar container named after the objects id. As a first level entry you find a folder named
like the oid, too. In this folder there are four text files and one data folder which belong to BagIt. In the data
folder the user finds the desired contents of the objects.
                
Lets compare this to the contents of the AIP the DIP has been created from:

    [oid].pack_1.tar
        [oid].pack_1/
              bagit.txt
              manifest-md5.txt
              bag-info.txt
              tagmanifest-md5.txt
              data/
                   2014_01_03+04_05+a/	
                                      premis.xml
                                      abc.jpg
                                      efg.tif
                                      subdir/cde.jpg
                   2014_01_03+04_05+b/	
                                      premis.xml
                                      abc.tif
                                      subdir/cde.tif

As you see, you only get one premis.xml and the abc.tif. 
The questions are why you get only tif files and which one of the two premis.xml you get.
Lets look at the AIPs content from a different perspective:

    2014_01_03+04_05+a/data/            premis.xml         abc.jpg           subdir/cde.jpg            efg.tif
                                            |                |                    |                       |
    2014_01_03+04_05+b/data/            premis.xml         abc.tif           subdir/cde.tif               -
                                                                
So what a user gets in his DIP is the latest version of each **document**.

A document is a logical description of several files which share the relative path 
(see [DAFile](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/object_model.md#dafile)) minus the extension
(everything after the last dot including the dot). So abc.jpg and abc.tif are considered the same document, one could
name it the document with the identifier "abc". The identifiers for the other documents are "premis", "subdir/cde" and "efg" 
respectively. The files which get packaged into the DIP then are determined by the system by ordering all representations of the object 
alphabetically and then taking the last version for each document.

## TODO premis.xml

.. contains events ..

## Sidecar files


## PIPs
