# Build process

This paragraph describes how you build and test DNS (essentially ContentBroker+DAWeb with some plugins) 
on a development workstation.
Following the steps will enable you to check out a local copy of the code, modify it
and build and test the modified application. 

## Prerequisites

To build DNS Core successfully you'll need at least a developer engine with

* JAVA 1.6
* MAVEN
* GIT
* Grails 2.2.4
* Imagemagick 6.7.8 (with jasper, to use jpg2000, with tiff)

Please ensure, the shells (bash and sh) of your workstation run in UTF-8 mode:
    
    export MAVEN_OPTS='-Dfile.encoding=UTF-8'
    export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
    export LANG='de_DE.UTF-8'
    
Of course you need a clean checkout of our source repo containing both DA-Web and ContentBroker

    git clone https://github.com/da-nrw/DNSCore DNSCore
    
Before you start please note the following distinction of two different locations we'll work with

    [...]/DNSCore/ContentBroker - this is the local clone of the DNSCore git repository 
        which you can place somewhere
        onto your file system (which is what the [...] should indicate)
    [appHome]/ - appHome is the full physical path to a local installation of the ContentBroker
        which automatically gets installed by the test system 
        in order to run the automated acceptance tests.

## Testing strategies and setup

![](https://raw.github.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/blackbox_whitebox.jpg)

As stated in the previous paragraph, the build system always works on two different locations. The "DNSCore/ContentBroker" location
is your local clone of the source code. Here any sources can be modified and the source code can be build and packaged.
Also testing is done here. On the one hand JUnit tests are executed here. This is done in a classical white box manner, as the code
you test and the code thats being under test are both within the source tree. You can step debug through the code etc.
On the other hand, there is a test suite of acceptance tests which can be executed in order to verify business criteria are met.
This test suite runs against a running ContentBroker which is automatically build from the sources. So, when the acceptance tests run,
they run from the source code location !against the "appHome" location. The tests communicate with the running ContentBroker as
any other client would do. As this is done in a "black box" manner the test code speaks to the ContentBroker through the interfaces
it provides to its clients (database, incoming and outgoing folders) which also means that step debugging is not possible for example.

The build system is based on the standard maven build lifecycle. Here is a short summary of the phases that are of interest in the current
context:

    test - The JUnit tests get executed
    package (including src/main/bash/package.sh) - The binaries are build and bundled to an installer.
    pre-integration-test (including src/main/bash/pre-integration-test.sh) - The installer gets called automatically to install the 
        freshly build ContentBroker to [appHome]. A lightweight database is set up. The source code configures itself so it knows 
        how to speak to the ContentBroker at [appHome].
    integration-test (including src/main/bash/integration-test.sh) - The acceptance tests (src/main/java/de/uzk/hki/da/at/AT*) are executed.
    verify - The results of the acceptance tests get evaluated and summarized by maven.
    deploy - The build system puts the installer to another location where all succesful builds are stored (useful for continuous integration).

Just to repeat an important fact, if one calls for example "mvn verify" all previous maven build lifecycle phases until verify get executed, one by one.

## Build and acceptance test the application on a development workstation

In order to run the tests on a development workstation and to reduce the dependencies to the workstation, one can
execute the acceptance tests against a version of the ContentBroker which runs with fake connectors which replace the
real external systems. So a developer is able to run the tests quickly but is not forced to install the whole bunch of external software DNSCore would need to speak to on a real node. 

To build the software from source and run the unit and acceptance tests follow these steps:

1. if !exists [appHome], mkdir [appHome]
1. cd [...]/DNSCore/ContentBroker
1. mvn clean && mvn verify -Pdev -DappHome=[appHome]

This will automatically build and test the whole application which should result in a message "BUILD SUCCESS" stated by Maven.

Remarks:

* -Pdev 
this is the environment setting of the install script which indicates we're on a development workstation. 
In this case the ContentBroker gets configured so that it gets provided with fake versions of the necessary
adapters to the storage and presentation layer.
* -DappHome=[appHome]  **no ending slash!!!**
 
### Build and acceptance test the application on a Continuous Integration machine

The build process on a dedicated build machine works more or less the same, with a few exceptions discussed
here. To execute the build process run:

1. cd [...]/DNSCore/ContentBroker
1. mvn clean && mvn install -Pci 

Remarks:

* -Pvm3 
this is the environment setting of the install script which indicates we're on a development workstation. This
leads to a ContentBroker configuration which provides access to the real iRODS storage layer and the real
Fedora presentation layer. 
* missing -DappHome this param is not necessary when -Pvm3 is selected as the paths are preconfigured.
* mvn deploy instead of mvn verify: The one additional phase executed at the end of the build phase deploys the
completely tested release candidate to a precondigured folder were all release candidates are collected. This 
is done to support continuous integration workflows.

 
## Debugging and Development

Sometimes it is necessary to have more fine grained control over the build and test process. For example,
if you want to bugfix a certain acceptance test or if you write a new acceptance test. 

1. cd [...]/DNSCore/ContentBroker
1. mvn clean && mvn pre-integration-test -Pdev -DappHome=[appHome] **no ending slash!!!**

If you run mvn pre-integration-test, the applications gets unit tested, build and installed automatically
to [appHome]. There you can debug the running ContentBroker manually or you can run single acceptance tests
by calling

1. mvn failsafe:integration-test -Dit.test=AT[TestName]
