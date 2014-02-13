### State calls to DAWEB

The software is able to send back some information on the process being executed when a
SIP is ingested to the DNS. As well as the "final state" residing as AIP on the storage layer 
is being fed back, you are able to determine the URN being assigned to your SIP. 

https://Servername/daweb3/status/index?urn=[TheURN]
https://Servername/daweb3/status/index?origName=[Original name the SIP was ingested]
https://Servername/daweb3/status/index?identifier=[DNS identifier] 

The response looks like (JSON): 

{"urn":"urn:nbn:de:danrw-131614-2013111519609","contractor":"TEST","origName":
"testPackage_docx99","identifier":"131614-2013111519609","status":"archived - but in progress","packages":["1“]}

"Status" could be one of:

archived 
archived - but check needed
archived - but in progress

in progress waiting ([internal state code])
in progress failure ([internal state code])
in progress working ([internal state code])
