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

One important guideline for all metadata formats 
Refrences inside metadata always have to reference data file with relative paths. 
These relative paths always count from the data folder. An example will help to clarify this:

    data/[somefile1.xyz]
    
must be referenced, independently of the metadata format, like this:

    href="[somefile1.xyz]"

A file in a subfolder

    data/[subfolder]/[somefile2.xyz]

must be referenced like this:

    href="[subfolder]/[somefile2.xyz]
    
**Don't do**
    
A common mistake would be referencing a file

    data/[subfolder]/[somefile3.xyz]
   
from a metadatafile

    data/[subfolder]/[metadata1].xml
   
like this:

    href="[somefile3.xyz]"
   
The idea here was, that the data file is referenced relatively to the metadata file. So the guideline to reference files
relatively to the data file has been ignored. The system would reject such a package, where the referenced file could not be resolved and report an exception to the user.




If there are more than one metadata file found on the top level, the package gets rejected and the user gets informed.

## METS/MODS

METS allows for a description of complex objects and serves as a container format for content,administrative, technical and structural metadata. More information on METS can be found [here](http://www.loc.gov/standards/mets/). For METS documents processed on nodes running DNSCore there are additional constraints which will be described in the following section.

### Directory layout

A SIP with METS metadata must conform to the following directory layout

    data/[METS].xml
    data/[somefile1.xyz]
    data/[subfolder]/[somefile2.xyz]
    
1. The [METS] placeholder stands for a random string and can be chosen freely.
2. The [METS] file must be on the top level of the directory structure, i.e. directly below the data folder.
3. There must be only one [METS] file. Packages with more than one will get rejected and the user gets informed.    
4. The [subfolder] and [somefile*.xyz] placeholders are files and folders placed in an arbitrary manner by the user.

### structural metadata

From within the file section the descriptive metadata is referenced. The attribute xlink:href must point to the relative (from the data path) path of a file.

Files can be grouped, but then only one group gets evaluated. If there is more than one group, the group marked with USE="DEFAULT" is used.

The physical and logical structure within the structMap is optional, but is processed when the attributes TYPE="LOGICAL" or TYPE="PHYSICAL" are present.

### descriptive metadata

## EAD - with METS

In general, for EAD files processable with DNSCore, the guidelines of the EAD profile for "Findbücher" of the "Deutsche Digitale Bibliothek" are followed.

In addition to that, there are several rules to follow in order to let DNSCore work properly on EAD material.

### descriptive metadata

TODO translate

Elemente zur Textstrukturierung, wie Hervorhebungen, Zeilenumbrüche und Absätze werden grundsätzlich ignoriert.

Metadaten im Element für weitergehende deskriptive Daten (<odd>) können aufgrund der fehlenden Systematik nur zur Anzeige und Volltextsuche, nicht aber zur gezielten Recherche genutzt werden.

### directory layout

A SIP with EAD metadata must conform to the following directory layout

    data/[EAD].xml
    data/[METS1].xml
    data/[subfolder]/[METS2].xml
    data/[somefile1.xyz]
    data/[subfolder]/[somefile2.xyz]

1. The [EAD] placeholder stands for a random string and can be chosen freely.
1. The [EAD] file must be on the top level of the directory structure, i.e. directly below the data folder.
1. There must be only one [EAD] file. Packages with more than one will get rejected and the user gets informed.
1. The [subfolder] and [somefile*.xyz] placeholders are files and folders placed in an arbitrary manner by the user.
2. There must be only one subfolder. Structures like data/[subfolder]/[subfolder]/[METS2].xml are not allowed.

### structural metadata

The metadata must be laid out in the following manner:

1. From the [EAD] file, several METS files somewhere inside the directory structure can be referenced. They can be named arbitrarily (placeholders [METS*]). The references have to be made via daoloc elements. The references have to be set relative from the data path of the package.
1. Any of the referenced METS files links to a single data file ([somefile*.xyz]). METS files with links to more data files lead to an exception which will reject the package and inform the user.


Here is an example of a EAD file:

    <c03 level="file">
          <did>
            <unitid type="altsignatur">(...)</unitid>
            <unitid type="Bestellnummer">4547 Bl.066</unitid>
            <unitdate normal="0000/0000">ohne Datum</unitdate>
            <origination>(...)</origination>
            <physdesc>(...)
            <extent>(...)</extent></physdesc>
            <unittitle>Mrs. N. Andres</unittitle>
            <abstract type="Enth&#195;&#164;lt">(...)</abstract>
            <abstract type="Darin">(...)</abstract>
            <note>
              <p>(...)</p>
            </note>
            <unitid type="v.num">4559</unitid>
          </did>
          <relatedmaterial>
            <p>(...)</p>
          </relatedmaterial>
          <bibliography>
            <p>(...)</p>
          </bibliography>
          <daogrp>
            <daoloc role="mets" title="[METS2]"
            href="[subfolder]/[METS2].xml" />
          </daogrp>
    </c03>

The METS file contains then

    <mets:fileSec>
    <mets:fileGrp>
    <mets:file ID="item_0" MIMETYPE=""><mets:FLocat xlink:href="[subfolder]/[somefile2.xyz]" LOCTYPE="URL"/></mets:file></mets:fileGrp>
    </mets:fileSec>




## LIDO

asdf

## XMP - Sidecar

### structural metadata

With XMP there isn't the possibility to model the structure of complex objects. The only structural information which is maintained by DNSCore is based on a 1 to 1 relation of files to metadata files. The the section directory for more information on that. This relation models that XMP metadata "belong" to documents like pictures or PDFs and so on.

### descriptive metadata

TODO translate 

Deskriptive Metadaten

Aktuell werden nur die deskriptiven Metadaten aus dem Dublin Core Namespace verarbeitet. Die Umsetzung der weiteren in XMP enthaltenen Namespaces (insbesondere iptc4xmpcore und photoshop) ist geplant, hängt aber vom noch zu erarbeitenden Schema für die Recherche im Presentation Repository ab.

### directory layout

A SIP with EAD metadata must conform to the following directory layout

    data/[abc1].[audio]
    data/[abc1].xmp
    data/[abc2].[audio]
    data/[abc2].xmp
    
1. All the files have to be placed at the root level directly below the data folder.
2. There has to be a 1 to 1 correspondence between data and metadata files. 
3. The files are associated by naming convention. [abc1].[audio] and [abc1].xmp belong together.
    
