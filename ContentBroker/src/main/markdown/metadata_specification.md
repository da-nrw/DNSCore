# Accepted Metadata formats of DNS

There are several metadata formats that the DNS Suite will accept in SIPs.
Though basically any binary and metadata format will be subject to long term preservation in DNSCore,
these formats which we call "accepted" garantuee that the PIPs generated in a side workflow will
be properly processed for access by viewers. 

At the moment we accept four formats:

* METS/MODS
* EAD - with METS
* LIDO
* XMP - Sidecar

## General rule

Refrences inside metadata

## METS/MODS

A SIP with METS metadata must conform to the following directory layout

    data/[METS].xml
    data/[somefile1.xyz]
    data/[subfolder]/[somefile2.xyz]
    
1. The [METS] placeholder stands for a random string and can be chosen freely.
2. The [METS] file must be on the top level of the directory structure, i.e. directly below the data folder.
3. There must be only one [METS] file. Packages with more than one will get rejected and the user gets informed.    
4. The [subfolder] and [somefile*.xyz] placeholders are files and folders placed in an arbitrary manner by the user.

## EAD - with METS

A SIP with EAD metadata must conform to the following directory layout

    data/[EAD].xml
    data/[somefile1.xyz]
    data/[subfolder]/[somefile2.xyz]

1. The [EAD] placeholder stands for a random string and can be chosen freely.
2. The [EAD] file must be on the top level of the directory structure, i.e. directly below the data folder.
3. There must be only one [EAD] file. Packages with more than one will get rejected and the user gets informed.
4. The [subfolder] and [somefile*.xyz] placeholders are files and folders placed in an arbitrary manner by the user.


## LIDO

## XMP - Sidecar
