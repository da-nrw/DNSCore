### Automated Retrieval 

Retrieval requests by external systems could issued by POST requests to an RESTful interface 
which is available at the URL  https://Servername/daweb3/automatedRetrieval/queueForRetrievalJSON

The JSON POST Data must at least contain one of the following fields: 
URN, IDENTIFIER, ORGINALNAME. 

Example: 

{"urn":"urn:nbn:de:danrw-131614-2013111519609","origName":"testPackage_docx99","identifier":"131614-2013111519609"}