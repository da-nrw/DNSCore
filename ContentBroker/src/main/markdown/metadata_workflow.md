# Metadata workflow in DNSCore

Descriptive metadata providing centextual information about the archived objects plays an important role in different stages of the DNS workflow. First a set of metadata is stored with every AIP in order to ensure that the preserved objects contain machine-readable information about the archived data in order to be able to be able to process and understand retrieved objects. Furthermore this metadata is stored in the PIPs and used to facilitate access to published objects in the presentation layer.

## Supported Formats

The current version of DNS implements routines for four different types of packages with regards to their metadata formats and structure:
* METS/MODS
* EAD (with optional referenced METS metadata)
* XMP
* LIDO

These four metadata formats are the only ones currently officially supported by the DNS and are stored in the AIPs so that they can be read and processed should the objects be restored or if migrations to other future standards should be necessary.

Additionaly the DNS currently implements crosswalks for these standards in order to be able to make them accessible in a unified presentation environment. These crosswalks create the following formats:
* DC
* EDM

## Technical workflow

In the standard case the ContentBroker assumes that only one metadata file exists per package. This facilitates implementing crosswalks as XSLTs and consitutes an easily expandable mechanism.

For structurally more complex cases (like XMP sidecar files or METS files referenced in EAD) some more logic is required. This logic is implemented in Java code in the respective actions.

The following actions trigger or implement operations on metadata:

### RegisterURNAction

Trys to read URN information from the metadata file (currently only implemented for METS).

### UpdateMetadataAction

1 Ensures that file names referenced in the metadata are updated according to changes caused by format conversions and renames that happened before.
2 XMP sidecar files are collected to a single "XMP manifest" as a prerequisite for XSL transformations.
3 DC metadata is created by executing the corresponding XSLTs.
4 If a URL prefix is given in the beans configuration references to files in the metadata are updated in order to allow web resolution in the presentation layer.
5 If the switch is set in the beans configuration the package type is written to the DC file.

### CreateEDMAction

1 Retrieves the main metadata file from the presentation repository.
2 EDM (RDF/XML) metadata is created by executing the corresponding XSLTs.
3 The EDM metadata is ingested into the presentation repository.

# IndexESAction

1 Retrieves the EDM metadata from the presentation repository.
2 Indexes the EDM metadata as JSON in the elasticsearch index.
