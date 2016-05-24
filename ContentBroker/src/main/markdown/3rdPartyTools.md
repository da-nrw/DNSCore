# The DNSCore 3rd party libraries and tools

Aim of document is to provide contractors with information about the tools used for 
validation and conversion of submitted content. Therefore the document does not list all 3rd party software in use.

1. Third party libraries in binary form, automatically pulled from the net via maven. You'll find all the used libraries and their version numbers used in the maven configuration file, section "dependencies" at [pom.xml](https://github.com/da-nrw/DNSCore/blob/master/pom.xml), at [DNSCommon/pom.xml](https://github.com/da-nrw/DNSCore/blob/master/DNSCommon/pom.xml), at [ContentBroker/pom.xml](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/pom.xml) at [DAWeb/pom.xml](https://github.com/da-nrw/DNSCore/blob/master/DAWeb/pom.xml) and at [SIPBuilder/pom.xml](https://github.com/da-nrw/DNSCore/blob/master/SIP-Builder/pom.xml)
2. Third party tools in modified and unmodified version which are delivered as part of this git source code repository
3. Third party tools which are not delivered as part of this git source code repository and are considered optional. 

## Table of relevant third party tools

<table><thead>
<tr>
<th>Tool</th>
<th>Version</th>
<th>Description</th>
<th>License</th>
<tr>
<td>bagit</td>
<td>4.8</td>
<td>SIP container format</td>
<td><a href="https://github.com/LibraryOfCongress/bagit-java/blob/master/LICENSE.txt" >License for BAGIT Library (BIL)</a></td>
</tr>
<tr>
<td>pdfbox</td>
<td>1.8.2</td>
<td>PDF validation and conversion</td>
<td>Apache License, Version 2.0</td>
</tr>
</tr>
</thead><tbody>
<tr>
<td>JHOVE</td>
<td>Rev. 1.9b3</td>
<td>Format Extraction and Validation</td>
<td>GNU Lesser General Public License (LGPL).</td>
</tr>
<tr>
<td>FIDO</td>
<td>v1.3.1</td>
<td>Format identification</td>
<td>Apache License Version 2.0, January 2004</td>
</tr>
<tr>
<td>FFmpeg</td>
<td>0.6.5</td>
<td>Video codec extracion</td>
<td>GNU Lesser General Public License (LGPL) version 2.1</td>
</tr>
<tr>
<td>HandBrakeCLI</td>
<td>0.9.9</td>
<td>Video conversion</td>
<td>GNU General Public License, version 2</td>
</tr>
<tr>
<td>tar</td>
<td>1.23</td>
<td>unpacking/packing</td>
<td>GNU General Public License v3 or later</td>
</tr>
<tr>
<td>ImageMagick</td>
<td>6.7.8-10</td>
<td>Picture Manipulation</td>
<td>ImageMagick License <a href="http://www.imagemagick.org/script/license.php">http://www.imagemagick.org/script/license.php</a></td>
</tr>
<tr>
<td>SoX</td>
<td>v14.3.2</td>
<td>Audio Codecs and conversion</td>
<td>GNU Lesser General Public License</td>
</tr>
<tr>
<td>Ghostscript</td>
<td>9.04</td>
<td>PDF conversion</td>
<td>GNU Affero General Public License</td>
</tr>
<tr>
<td>elasticSearch</td>
<td>0.90.3</td>
<td>Indexed Search</td>
<td>Apache 2 license</td>
</tr>
<tr>
<td>Fedora Commons</td>
<td>3.5</td>
<td>PIP Repository</td>
<td>Apache License, Version 2.0</td>
</tr>
<tr>
<td>Groovy</td>
<td>2.3</td>
<td>DA-Web GUI</td>
<td>Apache License, Version 2.0</td>
</tr>
<tr>
<td>Grails</td>
<td>2.3.8</td>
<td>DA-Web GUI</td>
<td>Apache License, Version 2.0</td>
</tr>
</tbody></table>
