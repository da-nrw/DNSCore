# dns-vagrant
Die Skripte installieren eine CI - CentOS7 Box mit GUI (über RDP) und shared
Folder zum einfachen Datenaustausch.

Eclipse bitte selber installieren.

vagrant 2.2.3 (2.1.2) ist zu installiereren.

## 1a CNTLM-Proxy  
Entweder mittels CNTML Server auf Winows, als Vordergrundprozess

cntnlm.bat:
+ SETX http_proxy http://localhost:3128
+ SETX https_proxy http://localhost:3128
+ cntlm -f -c cntlm.ini

## 1b ENV-VARS Proxy 
Oder: Als alternative Lösung: setzen der folgenden Variablen:
+ set VAGRANT_HTTP_PROXY="http://danr...vrintern.lvr.de:8080/"
+ set HTTP_PROXY="http://danr...vrintern.lvr.de:8080/"
+ set VAGRANT_HTTPS_PROXY="http://danr...vrintern.lvr.de:8080/"
+ set HTTPS_PROXY="http://danr...vrintern.lvr.de:8080/"


## 2 Vagrant Plugins
vagrant plugins:

+ vagrant plugin install vagrant-proxyconf
+ vagrant plugin install vagrant-vbguest

## 3 Box hochfahren und nutzen
+ vagrant up (--provider=virtualbox)
+ vagrant ssh

## 4 Neustart der Box
+ vagrant halt
+ vagrant up

## 5 DNS CI installieren
+ cd /vagrant
+ sudo ./installDNS.sh

## 6 Maven CI Tests als irods starten
+ sudo bash
+ su - irods