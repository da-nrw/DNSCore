## Ingest 

The following chapter describes manual ingestion of SIP. Automatic ingestion is possible via the 
internal interface of CB. Please refer to CB's documentation. 

### Manual ingestion of SIP 

The ingest of SIPs into the system by users is done in two steps. First users upload their
package onto a file share to their node. This step relies on technical configurations
(transport protocol: for example WEBDav, SFTP)
 of the node which the administrator provides and which are based on an agreement 
between the node owner and the contractors which deliver content to this node. 
Because of this the specific transport protocol cannot be part of the documentation of
DNSCore.
The packages are then transported via the transport protocol of choice to the 
[UserArea](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/processing_stages.md#userarea). While UserArea is the technical term for the incoming
storage space, contractors only have to think of it as their web share to the system.
The contents of their respective webshare can be seen by contractors directly when
they upload their content with a tool of choice (for example FileZilla for SFTP),
but also is presented by DAWeb, where the second step of manual ingest takes place.
Here users can mark packages as ready for ingest, which DAWeb then signals to the 
other components of the node, which then extract the package from the share for 
further processing.

#### Ingest Step by Step

##### Prerequisites

1. You have recieved log in data for the system by an administrator.
2. You have recieved log in data for the web share for your node by an administrator.
3. You have mounted the webshare on your local box or installed a file transfer program.

##### Steps

1. Choose a package for upload.
1. Mount your webshare on your box or open and log in into your file transfer program.
1. Upload the package to the incoming directory of your webshare.
1. Wait until the tranfer of the package has been completed.
1. Log into DAWeb on your node.
1. Click "Verarbeitung für abgelieferte SIP starten" on your starting screen.
1. Mark the package your uploaded and hit "Start" (see graphic below)
1. Verify that the entry for the file disappears.
1. Wait for an email which informs you about success or failure of the ingest process.


![](https://raw2.github.com/da-nrw/DNSCore/master/DAWeb/doc/ingest_1.png)
<br><sub>Start the ingest after uploading</sub>

**Note** that users are able to monitor the package while beeing processed.
This is an advanced feature which is only optional and primarily targeted at 
administrators. It is described in this (TODO link) document.
