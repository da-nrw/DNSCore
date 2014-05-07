* ci@machine = continuous integration server
* developer@machine = development workstation
* DNSCore = clone of the git repository

### Prerequisites

To build DNS Core successfully you'll need at least a developer engine with

* JAVA 1.6
* MAVEN
* GIT
* Grails 2.2.4 in order to compile DA-WEB 
* Imagemagick 6.7.8 (with jasper, to use jpg2000,  with tiff)

No other converters are needed for compile and run the acceptance tests at the developer@machine.  
In order to to build release candidates you'll need needed packages for all configured converters. 

Please ensure, your @machine's are all running in UTF-8 mode:
    
    export MAVEN_OPTS='-Dfile.encoding=UTF-8'
    export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
    export LANG='de_DE.UTF-8'

Of course you need a clean checkout of our source repo containing both DA-Web and ContentBroker

    git clone https://github.com/da-nrw/DNSCore DNSCore

### Continuous Delivery Workflow

* Commit and push regularly. Ideally after every small task. If necessary divide bigger tasks in smaller subtasks.
* Go through your test suite before commiting.
* This means you should execute at least all your unit tests and depending on your task at hand some acceptance tests.
* Even better: Take a short break and execute all your acceptance tests locally.
* For every change in GitHub the whole test suite should be executed on [ci@machine] and a binary should get build and stored in the maven repository.

### Maven build lifecycle and local installation for development

All you need to do for a build that gets automatically deployed locally and acceptance tested
is to run 

1. goto [developer@machine]
1. if !exists [CBInstallDir], mkdir [CBInstallDir]
1. cd DNSCore/ContentBroker
1. mvn verify -Pdev -DappHome=[CBInstallDir]

There are some custom scripts which are plugged in to the maven lifecycle which you find under src/main/scripts.
For more control you can directly make use of them if you follow these steps

1. cd DNSCore/ContentBroker
1. mvn package -Pdev -DappHome=[CBInstallDir] (this will build an installer at DNSCore/installation)
1. src/main/scripts/pre-integration-test.sh (src/main/bash/pre-integration-test.sh
?) (this will a) install the CB to [CBInstallDir] and b) prepare the testing environment)
1. mvn failsafe:integration-test (Running all acceptance tests)
1. mvn failsafe:integration-test -Dit.test=ATUseCaseX (Run a single acceptance test)

If you only want do deploy the CB locally without the need for acceptance testing you can do it like this:

1. cd DNSCore/ContentBroker
1. mvn package -Pdev -DappHome=[CBInstallDir]
1. cd DNSCore/installation
1. ./install.sh [CBInstallDir] (installing the CB)
1. cd [CBInstallDir]
1. ./ContentBroker_start.sh

### Build the release candidate
At the moment we do it manually. Jenkins integration is planned for may14.

1. goto [ci@machine]
1. cd development/DNSCore
2. git -- check out the branch/revision you want to build
3. mvn clean && mvn verify -Pvm3

With the current setup, make sure CB is stopped at vm2!

11. Manuelles Testen (testpackage_klein_und_muss_durchlaufen.*)

9. ??? Wenn letzter Schritt erfolgreich, dann ./deliver.sh vm2

### Deploy to production nodes
Every fully tested release candidate can be rolled out following this workflow:

1. goto [ci@machine]
1. cd DNSCore/ContentBroker
1. ./build.sh [type]
1. cd DNSCore
1. tar cf DNSCore-[version]-installer.tar
1. scp DNSCore-[version]-installer.tar [target@machine]:/tmp
1. goto [target@machine]
1. cd /tmp
1. tar xf DNSCore-[version]-installer.tar
1. if !exists [CBInstallDir], mkdir [CBInstallDir]
1. cd installation
1. ./install.sh [CBInstallDir]
1. cd [CBInstallDir]
1. ./ContentBroker_start.sh

[type] can have one of the following values:

* full (a fully fledged node including Pres Repository, also a good start to use the software)
* pres (just a Presentation Repository node, without conversion)
* node (would fit in most cases, having  n-nodes)

### Query your hsqldb easily on [developer@machine]

1. cd DNSCore/ContentBroker
1. src/main/bash/sqlrequest.sh "[SQL-Query]"

