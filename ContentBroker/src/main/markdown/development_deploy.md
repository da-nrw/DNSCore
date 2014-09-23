# Building and testing DNSCore.

Following the steps will enable you to check out a local copy of the code and build and test the DNSCore. Building
DNSCore gives you an installer, which can be used to install DNSCore on other machines.

DNSCore gets build following the guidelines of the maven build cycle. There are a lot of scripts working in the 
background which are mapped to the maven lifecycle phases. 

In general, DNSCore gets build in one of two modes, 

    ci  ( fully installed, node-like, machine with irods, fedora, elasticsearch )
    dev ( local development workstation with less prerequisites )

The **"ci" mode** is there for doing continuous integration. For continuous integration, the environment has to be production-similar. This means that like on a real "node", iRODS and a some other subssystems have to be installed prior to beeing able to executing the tests and creating builds.

The **"dev" mode** somewhat lightweight compared to the "ci" mode. The internal connectors to the subsystem are configured in a way that fake version of the connectors are used while doing tests and creating builds. This is useful for development purposes, where one wants to check out the sources, modify them, and see if they run on a local laptop, for example.

### Common prerequisites

Wheter in the dev or ci environment, to build DNS Core successfully you'll need a machine with

* JAVA 1.6
* MAVEN
* GIT
* Grails 2.3.8
* Imagemagick 6.7.8 (with jasper, to use jpg2000, with tiff)

Please ensure, the shells (bash and sh) of your workstation run in UTF-8 mode:
    
    export MAVEN_OPTS='-Dfile.encoding=UTF-8'
    export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
    export LANG='de_DE.UTF-8'
  
### ci - prerequisites.
  
How to set up a node for continuous integration is part of [this](installation_ci.md) tutorial.

### dev - Getting the source code.
    
Of course you need a clean checkout of our source repo containing both DA-Web and ContentBroker

    git clone https://github.com/da-nrw/DNSCore [...]/DNSCore
    
where 

    [...]/DNSCore
    
is a place somewhere on your file system, where your local clone of the DNSCore gets placed.

### Build and acceptance test the application on a development workstation

In order to run the tests on a development workstation and to reduce the dependencies to the workstation, one can
execute the acceptance tests against a version of the ContentBroker which runs with fake connectors which replace the
real external systems. So a developer is able to run the tests quickly but is not forced to install the whole bunch of external software DNSCore would need to speak to on a real node. 

To build the software from source and run the unit and acceptance tests follow these steps:

    1. if !exists [appHome], mkdir [appHome]
    2. cd [...]/DNSCore/ContentBroker
    3. mvn clean -Pdev && mvn verify -Pdev -DappHome=[appHome]

This will automatically build and test the whole application which should result in a message "BUILD SUCCESS" stated by Maven. 

Remarks:

    -Pdev this is the environment setting of the install script which indicates we're on a development workstation. 
       In this case the ContentBroker gets configured so that it gets provided with fake versions of the necessary
       adapters to the storage and presentation layer.
    -DappHome=[appHome]  **no ending slash!!!**

    [appHome] - appHome is the full physical path to a local installation of the ContentBroker
        which automatically gets installed by the test system 
        in order to run the automated acceptance tests.

### Build and acceptance test the application on a continuous integration machine

The build process on a dedicated build machine works more or less the same, but easier, since all paths are preconfigured on a ci machine.

    mvn clean -Pci && mvn verify -Pci 

Remarks:

    * -Pci
        this is the environment setting of the install script which indicates we're on a development workstation. This
        leads to a ContentBroker configuration which provides access to the real iRODS storage layer and the real
        Fedora presentation layer. 


### Getting the build

The ContentBroker which gets tested is automatically installed from the installer which is build at 

    [...]/DNSCore/ContentBroker/target/installation
    
or 

    /ci/DNSCore/ContentBroker/target/installation
    
After the test have passed successfully, you can use this installer to set up a ContentBroker on another machine.

### Running application at the build machine

If the build passes, a running ContentBroker installation is present on your system, which you can find at

    [appHome] 
    /ci/ContentBroker
    
You can verify that it runs by observing the main log file at

    [appHome]/log/contentbroker.log
    /ci/ContentBroker/log/contentbroker.log

You can ingest a testpackage to 

    [appHome]/storage/IngestArea/TEST
    /ci/ContentBroker/storage/IngestArea/TEST
    
and observe the ContentBroker fetching it in

    [appHome]/log/ingest.log
    /ci/ContentBroker/log/ingest.log

### Debugging and Development

Sometimes it is necessary to have more fine grained control over the build and test process. For example,
if you want to bugfix a certain acceptance test or if you write a new acceptance test. 

    mvn clean -Pdev && mvn pre-integration-test -Pdev -DappHome=[appHome] **no ending slash!!!**
    mvn clean -Pci && mvn pre-integration-test -Pci

**Note** For quick acceptance test runs you can add params to deactivate the building of DAWeb and execution of JUnit tests. The switches are

    -DDAWeb=skip
    -Dskip.surefire.tests=true

If you run mvn pre-integration-test, the applications gets unit tested, build and installed automatically
to [appHome]. There you can debug the running ContentBroker manually or you can run single acceptance tests
by calling

    mvn failsafe:integration-test -Dit.test=AT[TestName]

## Understanding the test system

![](https://raw.github.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/blackbox_whitebox.jpg)

To understand how acceptance tests are done with DNSCore, you have to keep in mind, that you have your source code repository location

    [...]/DNSCore/ContentBroker

and your target location, which is either

    [appHome]/ContentBroker

or    

    /ci/ContentBroker
    
depending which environment you choose. The repository location is the place where the sources are and where the build is unit tested (white box testing) and packaged.

In addition to the unit tests, another set of tests, which is there to verify that business criteria are matched, can be executed. This is done by running "mvn verify", which builds and installer and installs the ContentBroker to one of the aforementioned target locations. This acceptance test communicate to the then running ContentBroker from the outside, through its interfaces (database, incoming and outgoing folders), as any other client would do (black box testing).

While always testing against the interfaces of the ContentBroker, depending on the mode (ci,dev) you work in, the application works with real connectors to underlying subsystems (ci) or with fake connectors (dev) to non existing subsystems. In either case it is worth mentioning that the focus of the acceptance tests is mainly the business code, which is fully tested even in dev mode, while the ci mode tests the business code working on top of the underlying subsystems.



The build system is based on the standard maven build lifecycle. Here is a short summary of the phases that are of interest in the current
context:

    test - The JUnit tests get executed
    package (including src/main/bash/package.sh) - The binaries are build and bundled to an installer.
    pre-integration-test (including src/main/bash/pre-integration-test.sh) - The installer gets called automatically to install the 
        freshly build ContentBroker to [appHome]. A lightweight database is set up. The source code configures itself so it knows 
        how to speak to the ContentBroker at [appHome].
    integration-test (including src/main/bash/integration-test.sh) - The acceptance tests (src/main/java/de/uzk/hki/da/at/AT*) are executed.
    verify - The results of the acceptance tests get evaluated and summarized by maven.

Just to repeat an important fact, if one calls for example "mvn verify" all previous maven build lifecycle phases until verify get executed, one by one.
