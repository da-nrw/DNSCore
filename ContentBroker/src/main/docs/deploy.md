ci@machine = continuous integration server
developer@machine = development workstation
DNSCore = clone of the git repository

# Continuous Delivery Workflow

* Commit and push regularly. Ideally after every small task. If necessary divide bigger tasks in smaller subtasks.
* Go through your test suite before commiting.
* This means you should execute at least all your unit tests and depending on your task at hand some acceptance tests.
* Even better: Take a short break and execute all your acceptance tests locally.
* For every change in GitHub the whole test suite should be executed on ci@machine and a binary should get build and stored in the maven repository.

## Create a local installation for development

1. goto [developer@machine]
1. if !exists [CBInstallDir], mkdir [CBInstallDir]
1. cd DNSCore/ContentBroker
2. ./build.sh dev [CBInstallDir]
3. ./install.sh dev [CBInstallDir]
5. Wait for message "INFO  de.uzk.hki.da.core.ContentBroker - ContentBroker is up and running". Then CTRL-C.
6. Run all acceptance tests with: mvn failsafe:integration-test
7. Run single acceptance test with: mvn failsafe:integration-test -Dit.test=ATUseCaseX

## Build the release candidate
At the moment we do it manually. Jenkins integration is planned for may14.

1. goto [ci@machine]
1. cd development/DNSCore
2. git -- check out the branch/revision you want to build
3. ./build.sh vm3
4. ./install.sh vm3
5. Wait for message "INFO  de.uzk.hki.da.core.ContentBroker - ContentBroker is up and running". Then CTRL-C.
6. mvn failsafe:integration-test

With the current setup, make sure CB is stopped at vm2!

10. goto vm2:ContentBroker; ./ContentBroker_start.sh
11. Manuelles Testen (testpackage_klein_und_muss_durchlaufen.*)

3. ??? Wenn letzter Schritt erfolgreich, dann ./integration.sh
9. ??? Wenn letzter Schritt erfolgreich, dann ./deliver.sh vm2

## Deploy to production nodes
Every fully tested release candidate can be rolled out following this workflow:

1. goto ci@machine
1. cd DNSCore/ContentBroker
1. ./build.sh [type]
1. cd DNSCore
1. tar cf DNSCore-[version]-installer.tar
1. scp DNSCore-[version]-installer.tar [target@machine]:/tmp
1. goto [target@machine]
1. cd /tmp
1. tar xf DNSCore-[version]-installer.tar
1. cd installation
1. ./install.sh [CBInstallDir]
1. cd [CBInstallDir]
1. ./ContentBroker_start.sh

[type] can have one of the following values:

* full (e.g. for vm6)
* pres (e.g. for vm2,danrw)
* node (e.g. for prod,lvr,eunomia )


## Query your hsqldb easily on developer@machine
1. ./sqlrequest "[SQL-Abfrage]"


## Integrationstests in den Testbeds ausführen
1. lokal die neuesten Changes mit mvn package bauen
2. rsync zum Testbed
3. im Testbed: 
4. ./integration.sh

## Ausführen einzelner IntegrationTests auf der vm3 (Testbed oder Trunk)
1. ./deliver.sh integration
2. mvn test -Dtest=IT...

