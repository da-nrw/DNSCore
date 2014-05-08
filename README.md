DNSCore
=======

-

The Core System of the DA-NRW Software Suite. 
The DNS software relies
exclusively on existing open source software components to implement a
distributed, self-validating repository, allowing depositors a high degree of control over
the methods applied to individual objects submitted for preservation and
distribution.

For Documentation see:

* the [wiki](https://github.com/da-nrw/DNSCore/wiki)
* the [Java API documentation](http://da-nrw.github.io/DNSCore/apidocs/)
* the [Java test documentation](http://da-nrw.github.io/DNSCore/testapidocs/)

### Feature list

As a result of various research programs in the recent years there are some 
software solutions available which handle preservation tasks. Since each of
these solutions was designed under a different focus and to solve different sets of
problems within the field of longterm preservation, it is worth mentioning
the two most distinctive features of DNSCore at first, before diving in into
a richer feater list.

#### Distinctive Features

The first big feature is the possibility for users to let the system automatically
generate derivates of your material destined for long term preservation which are
suited for online web presentation. The decision to integrate a presentation component
deep into the core of the software was originally politically motivated as it was
a state financed open source project and the idea to let the general public have a share
on cultural material accumulated in a state wide archive was only natural in the context of
this background. Also due to the technical possibilities of the digital domain it is relatively
easy to do so and give users access to this material in an easy and uncomplicated way and
this seems contemporary because users nowadays are used to get the information they want
as they are used to it in everyday internet life anyway. We think of it as a really great
feature.

While implementing this feature there was some confusion when speaking of DIPs as stated 
in the OAIS reference model as DIPs can designate any material accessed by the users of
such a system and the DIPs accessed from the long term component differ a lot from those
accessed via the presentation component. As a consequence we decided to reflect these
distinctions in a concise way in order to make communication easier. So we introduced
the concept of PIPs (Presentation Information Package) into our model, which is an
extension to the OAIS model specifically targeted at our needs.

The second big feature is the so called [Delta-Feature](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/the_delta_feature.md). With deltas users can make additions
to contents already present in the long term archive. This feature is also deeply integrated
into the basic architecture of the software. Also it needed to create an object model that is
in line with the OAIS reference model but is more complicated in its specifics since the
software must handle the organization of objects and packages in the background. So when we
speak of an object in the context of DNSCore as our main entity in the object model, we have
a unit of data which can consist of data of more than one package/SIP at the same time. 

#### Other Features

* DAWeb - Graphical user interface for administrators and users
* Storage Layer - geographical distribution/replication of data
* Self validating ("audit")
* Format identification
* Codec identification
* Automatic format conversion

To build streamlined SIP based based on the used SIP specifications, take a look at:

* SIPBuilder - Graphical user interface for End-Users, which allows high degree control over 
SIP submitted to the DNS. The software could be easily integrated in subsystems, due to having 
a command line interface.

For deeper information on the software please take a look at the Wiki on GitHub.  







