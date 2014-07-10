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

If there are more than one metadata file found on the top level, the package gets rejected and the user gets informed.

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
    data/[METS1].xml
    data/[subfolder]/[METS2].xml
    data/[somefile1.xyz]
    data/[subfolder]/[somefile2.xyz]

1. The [EAD] placeholder stands for a random string and can be chosen freely.
1. The [EAD] file must be on the top level of the directory structure, i.e. directly below the data folder.
1. There must be only one [EAD] file. Packages with more than one will get rejected and the user gets informed.
1. The [subfolder] and [somefile*.xyz] placeholders are files and folders placed in an arbitrary manner by the user.

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
            <daoloc role="mets" title="mets_2_99"
            href="[subfolder]/[METS2].xml" />
          </daogrp>
    </c03>



## LIDO

asdf

## XMP - Sidecar

A SIP with EAD metadata must conform to the following directory layout

    data/[abc1].[audio]
    data/[abc1].xmp
    data/[abc2].[audio]
    data/[abc2].xmp
    
1. All the files have to be placed at the root level directly below the data folder.
2. There has to be a 1 to 1 correspondence between data and metadata files. 
3. The files are associated by naming convention. [abc1].[audio] and [abc1].xmp belong together.
    
