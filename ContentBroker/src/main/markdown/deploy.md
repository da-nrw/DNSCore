# Build process

## Build DNS

This paragraph describes how you build and test DNS (essentially ContentBroker+DAWeb with some plugins) 
on a development workstation.
Following the steps will enable you to check out a local copy of the code, modify it
and build and test the modified application. 

### Prerequisites

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

### Explanation of how maven build process details

Here are some custom scripts which are plugged in to the maven lifecycle which you find under src/main/scripts.
For more control you can directly make use of them if you follow these steps
TODO


### Build and acceptance test the application on a development workstation

    if !exists [appHome], mkdir [appHome]

All you need then to do for a build that gets automatically deployed locally and acceptance tested
is to run 

1. cd DNSCore/ContentBroker
1. mvn clean && mvn verify -Pdev -DappHome=[appHome]

-Pdev 
this is the environment setting of the install script which indicates we're on a development workstation
-DappHome=[appHome] 
make sure there is no ending slash!!!
 
#### Executing single steps
 
1. cd DNSCore/ContentBroker
1. mvn package -Pdev -DappHome=[CBInstallDir] (this will build an installer at DNSCore/installation)
1. src/main/scripts/pre-integration-test.sh 
    (src/main/bash/pre-integration-test.sh?) (this will a) install the CB to [CBInstallDir] and b) prepare the testing environment)
1. mvn failsafe:integration-test (Running all acceptance tests)
1. mvn failsafe:integration-test -Dit.test=ATUseCaseX (Run a single acceptance test)
 
### Build and acceptance test the application on a Continuous Integration machine

All you need then to do for a build that gets automatically deployed locally and acceptance tested
is to run 

1. cd DNSCore/ContentBroker
1. mvn clean && mvn deploy -Pvm3 

-Pvm3 
this is the environment setting of the install script which indicates we're on a development workstation


### Maven build lifecycle and local installation for development
T
If you only want do deploy the CB locally without the need for acceptance testing you can do it like this:

1. cd DNSCore/ContentBroker
1. mvn package -Pdev -DappHome=[CBInstallDir]
1. cd DNSCore/installation
1. ./install.sh [CBInstallDir] (installing the CB)
1. cd [CBInstallDir]
1. ./ContentBroker_start.sh
11. Manuelles Testen (testpackage_klein_und_muss_durchlaufen.*)

9. ??? Wenn letzter Schritt erfolgreich, dann ./deliver.sh vm2

## Continuous Delivery

### Workflow

* Commit and push regularly. Ideally after every small task. If necessary divide bigger tasks in smaller subtasks.
* Go through your test suite before commiting.
* This means you should execute at least all your unit tests and depending on your task at hand some acceptance tests.
* Even better: Take a short break and execute all your acceptance tests locally.
* For every change in GitHub the whole test suite should be executed on [ci@machine] and a binary should get build and stored in the maven repository.



### Query your hsqldb easily on [developer@machine]

1. cd DNSCore/ContentBroker
1. src/main/bash/sqlrequest.sh "[SQL-Query]"

