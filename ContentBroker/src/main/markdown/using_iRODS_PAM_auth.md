#Using PAM Auth features of iRODS in CB

Since Version 3.2 of iRODS, iRODS can authenticate users with the PAM (Pluggable authentication modules) feature
as part of the authentication process of iRODS users. 
While using normal operating system password, this needs to be encrypted with SSL. 

In case you want to authenaticate via PAM, please refer to the documentation of iRODS how to Setup PAM and SSL for PAM auth:
http://wiki.irods.org/index.php/PAM_Authentication
http://wiki.irods.org/index.php/PAM_SSL_Setup

Please install the mentioned packages and certificates.

##Configuring iRODS 

Don't forget to edit config/config.mk and change

    # PAM_AUTH = 1
    PAM_AUTH = 1
    # USE_SSL = 1
    USE_SSL = 1

Add the following packages

    openssl-devel pam-devel
    
During compilation we encountered the following error (might derive from differnet version of OpenSSL):

    iRODS/lib/core/src/sslSockComm.c: In function »int sslPostConnectionCheck(SSL*, char*)«:
    iRODS/lib/core/src/sslSockComm.c:824: Fehler: »sk_GENERAL_NAMES_num« wurde in diesem Gültigkeitsbereich nicht definiert
    iRODS/lib/core/src/sslSockComm.c:826: Fehler: »sk_GENERAL_NAMES_value« wurde in diesem Gültigkeitsbereich nicht definiert
    iRODS/lib/core/src/sslSockComm.c:835: Fehler: »sk_GENERAL_NAMES_free« wurde in diesem Gültigkeitsbereich nicht definiert
    make[1]: *** [iRODS/lib/core/obj/sslSockComm.o] Fehler 1
    iRODS/lib/core/obj/sslSockComm.o

This could be fixed by changing sk_GENERAL_NAMES_num to sk_GENERAL_NAME_ (http://groups.google.com/group/irod-chat/tree/browse_frm/month/2012-10/11ad1d04874f4959?rnum=91&_done=%2Fgroup%2Firod-chat%2Fbrowse_frm%2Fmonth%2F2012-10%3F)

##Setup of ContentBroker 

Please import the ICAT certifcate file to the java keychain:
    
    ./keytool -importcert -file /server.crt 
    
Change the paths in config.properties of ContentBroker to your settings  
    
    irods.keyStorePassword=
    irods.keyStorePath= 
    irods.trustStorePath=
