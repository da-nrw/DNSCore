This document desribes how to install DNSCore on your target environment.

## Installation

1. The ContentBroker installation dir will simply be named [CB] here.
1. Download the installer DNSCore-v[VERSION].tar from the latest stable release from https://github.com/da-nrw/DNSCore/releases
1. Put the downloaded container to the /tmp dir of your machine.
1. Unpack it. You will then find a directory at /tmp/installation from where you can install your DNSCore. cd into it.
1. Depending on your type of installation read the appropriate paragraph below.

### Installation / Update

1. Run ./install.sh [CB]

### Installation / Fresh Installation

1. If this is your first installation 
on the target machine, create a directory for the ContentBroker installation. This is the directory we
referenced with [CB] earlier.
1. You will have to get some configuratione files (listed with descriptions below).
1. Put them in your /tmp/installer directory
1. Run ./install.sh [CB] then.

The config files you need for a fresh installation can be found in the github source code repository
for DNSCore. Take out the following two files:
1. DNSCore/ContentBroker/src/main/conf/config.properties.dev
1. DNSCore/ContentBroker/src/main/conf/hibernateCentralDB.cfg.xml.inmem
and put them to your installer. You will have to remove the suffixes (inmem.dev) so you'll have these
files in your installer now before installing:
/tmp/installation/config.properties
/tmp/installation/hibernateCentralDB.cfg.xml
Please make sure when you download the sources of the config files that you pick the configs for
the exact version you want to deploy. To achieve this easily follow the links to the source code in the
github release section for the version you want to install.
Before you can deploy, you will have to configure your target system via the downloaded config files.

Learn more about how to configure your system at system_configuration.md!

## Starting the ContentBroker

1. Go to [CB]
1. Run ./ContentBroker_start.sh
1. Watch if the ContentBroker comes up with tail -f log/contentbroker.log
1. If everything goes well, you will see him greedily searching for jobs soon.

