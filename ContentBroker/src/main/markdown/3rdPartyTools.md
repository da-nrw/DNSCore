The DNSCore uses 3rd party libraries in various forms.

1. Third party libraries in binary form, automatically pulled from the net via maven. You'll find all the used libraries used in 
the maven configuration file at DNSCore/pom.xml
2. Third party tools in modified and unmodified version which are delivered as part of this git source code repository
3. Third party tools which are not delivered as part of this git source code repository and are considered optional. With help of these 
'plug-ins' the ContentBroker performs actual conversion processes.

### @2:

1. hsqldb - DNSCore/3rdParty/hsqldb
2. jhove  - DNSCore/3rdParty/jhove
3. fido   - DNSCore/3rdParty/fido

 