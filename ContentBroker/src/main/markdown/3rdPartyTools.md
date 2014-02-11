The DNSCore uses 3rd party libraries in various forms.

1. Third party libraries in binary form, automatically pulled from the net via maven. You'll find all the used libraries used in 
the maven configuration file at DNSCore/pom.xml
2. Third party tools in modified and unmodified version which are delivered as part of this git source code repository
3. Third party tools which are not delivered as part of this git source code repository and are considered optional. With help of these 
'plug-ins' the ContentBroker performs actual conversion processes.

### @2:

1. hsqldb - DNSCore/3rdParty/hsqldb
2. JHOVE  - DNSCore/3rdParty/jhove
3. FIDO   - DNSCore/3rdParty/fido

The reason for putting these libraries directly under revision control is twofold. hsqlb is used for testing a slightly stripped down 
(e.g. making use of some fake connectors) version of the DNSCore locally. hsqldb is a lightweight database an can run directly from 
command line in a Java vm. On the other hand FIDO and JHOVE are tightly integrated into the ContentBroker-Workflow. 
Putting them under revision control helps us that our Continuous integration server can create builds
which are tested against the whole application including FIDO and JHOVE and their specific configuration. 

While in case of JHOVE the modifications are minimal and are constrained only to an automatical insertion of some environment properties
by our custom install script, the formats.xml of FIDO has been augmented by four new custom defined da-nrw formats to capture some of
our most used metadata formats as distinct formats. Also we detected some slight variations in FIDOs output when identifying TIFF files.
In previous version the same files were recognized as fmt/10 while in newer version they where recognized as fmt/353.

In the root directory of each of these redistributed libraries one can find a text file called LICENSE.TXT which contains 
... ehm, yeah right, ... the appropriate licence.

 