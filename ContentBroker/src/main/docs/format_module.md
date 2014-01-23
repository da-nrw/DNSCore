
## The conversion mechanism - general observations and thoughts

The DNSCore system has facilities to automatically convert files contained in SIPs during ingest.
This is done with the help of various converters which can be plugged in via global system configurations.
The converters to be plugged in are required to be able to run as unix command line tools.

The basic pattern for a conversion is as follows:

2012_12_12+12_12+a/input.jpg -> 2012_12_12+12_12+b/input.tif

Here we have to note different things. 

First of all, we see the input and output files to reside in different
folders. We call them representation folders and they constitute every AIP. In the so called +a-representation
you find all the files which come from the SIP. They are unchanged except that they were moved to the +a-representation folder,
preserving everything including the original tree structure (subfolders). The +b-representation which gets added during
conversion then resembles the tree structure of the original, but instead of the original files you will find converted
versions of your original files. The +b-representation contains only the files which could be converted, resulting most of
the times in a representation which contains less files than the original representation. The important thing to note here
is that we interpret the files with the same basename (that means excluding the suffix) as the same logical document. That enables
us to build one complete representation which contains one version of each document. If there are two or more files in different
representations which have the same basename, the newest one (determined by ordering the representation names alphabetically)
is considered to be the actual representation for this specific logical document. In the case above it would be the input.tif.

The second point worth mentioning is, that due to the mechanism outlined in the previous paragraph, the system is restricted
to doing conversions which conform (more or less, as we'll see soonly) to the pattern one in one out. Merging audio and video
streams to a single format, converting dvd or video cd contents to simple video containers are not supported. Deviances from the
standard behaviour are only supported for some special cases.

# TODO publish
# TODO multiple page tiffs

## The conversion mechanism in its simplest form - a stepthrough.

To comprehend the whole conversion mechanism it is best to image the simplest possible scenario.
Lets have a close look which concepts you have to understand, which steps the system takes and which configurations are necessary
in order to perform the simple conversion (jpg->tif) from the above mentioned example. 

The first step the ContentBroker (via FIDO) performs is to determine the PRONOM puid for every file found in the +a-representation. For more
information about PRONOM identifiers see http://www.nationalarchives.gov.uk/PRONOM/Default.aspx. For a file in the jpg format version 1.00
FIDO will report an identifier fmt/42 back to the ContentBroker, which in turn uses this identifier to check if there is a special
policy matching this identifier. Here is an example of a matching policy which demonstrates their basic form:

id: 1
source_format: fmt/42
contractor_id: 1
conversion_routine_id: 1

This means that if the system has a file for which an identifier matching a certain policy could be found, the system
will perform a ConversionRoutine with a certain id during the conversion phase. The corresponding ConversionRoutine could
have the following form:

id: 1
name: CLI
type: de.uzk.hki.da.convert.CLIConversionStrategy
target_suffix: tif
params: convert input output

The system will look for a conversion routine with the conversion_routine_id specified in the policy. It will then
execute a special method in a java class with the class name de.uzk.hki.da.convert.CLIConversionStrategy. This
class can handle calls to the command line. It will replace the placeholders input and output by actual filenames and
will replace the suffix of the original file by the given target_suffix. The order which the class then sends
to the command line will look something like that convert 2012_12_12+12_12+a/input.jpg 2012_12_12+12_12+b/input.tif.
This is a call to ImageMagick which does the actual conversion work, resulting in a new file placed in the 
+b-representation. The class acted as wrapper for ImageMagick in this scenario, so to speak. 

To summarize, what is necessary in terms of configuration ... TODO











Dieses Dokument beschreibt sämtliche Aspekte der Formaterkennung, Formatkonversion der DNSCore

## Bild Konvertierung
Für die Konvertierung von Bildformaten wird imagemagick eingesetzt. Hier kann eine Version aus der Distribution verwendet werden.
Test: Auf der Kommandozeile sollte der Befehl convert eine Ausgabe produzieren; außerdem sollte der Befehl identify -list format u.a. Einträge für die Formate TIFF, JPEG und PNG zurückliefern.

## PDF Konvertierung
Zur PDF Konvertierung wird Ghostscript 9.0 benötigt. Da in den vergangenen Distributionen lediglich 8.7 bereitgestellt wird, ist Ghostscript selbst zu kompilieren.
./configure
Die PDF/A Einstellungen sind in der Configfile PDF_A.ps unter conf/ zu finden. Hier muss ggf. der Pfad lokalen Farbprofilen angepasst werden.

## Audio-Konvertierung
Relativ neu ist die Implementation der Audio-Konversionsroutine für Browser-Formate. Hierzu wird SoX 14.4.0 verwendet. Sollte der Bedarf nach Anpassung der gekürzten Abspielzeiten (je nach rechtlichen Einstellungen) bestehen. So lautet der Befehl:
sox eingabe.mp3 ausgabe.wav trim 01:00 00:20
Hier wird ein 20-sekündiger Ausschnitt ab Minute 1 extrahiert.
