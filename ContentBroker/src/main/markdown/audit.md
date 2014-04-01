## Audit 

In order to ensure the bitstream preservation of the stored AIP, checksums are stored for each stored AIP and 
automatically checked in periodic intervals. 
An AIP is only valid in terms of DNS, when is stored at least at three nodes and has identical checksums on all hosts. 
This is checked on each Retrieval process, too. 

### Automatic Audit

Each primary (initial) node of AIP, where the former SIP was ingested first is reponsible for checking it's AIP. 
The so called "integrity worker thread" is automatically started as Background process each time when the corresponding
ContentBroker is started. 

The results of the integrity checks are logged to 
    
     logs/integrity.log

### Manual Audit 

Nodeadmins can perform manual checking of AIP by queueing them via DA-WEB. 

### Reports on errors

"Errors" are: one of the three required nodes is down, while perform a check (minor error).
One or more checksums are not identical to the stored checksum in the Metadata catalogue ICAT (severe error).

On both errors, DNS send an email to the configured node admin. 
