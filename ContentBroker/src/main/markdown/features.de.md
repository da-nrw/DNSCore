# Liste der Features und Leistungsmerkmale der Anwendung DNSCore

* SIPBuilder - Graphical user interface and CLI for End-Users for building SIP - Grafische Benutzeroberfläche für Endkunden zur Erzeugung von SIP für das DNS. Möglichkeit zur Einbettung in Drittsysteme durch Command Line Interface (CLI). 
* DAWeb Webinterface - Grafische Benutzeroberfläche zur Steuerung des Archivs durch Benutzer und Administratoren. Abfragemöglichkeit für techn. Drittsysteme.
* Storage Layer - Geographische Verteilung und Replikation der Daten.
* Self validating - Selbstprüfung unter Maßstäben der Bitstream-Preservation.
* Format identification - Formatidentifikation auf Basis der PRONOM Identifier. Nutzung von JHOVE.
* Codec identification - Erkennung von Codecs auf Basis der installierten Module.
* Automatic format conversion - Konvertierung verstandener Formate durch hinterlegte Routinen. 
* PREMIS Logging - Alle Aktionen werden in der PREMIS hinterlegt.
* PIP (Presentation Information Package) creation - Anlage eines Präsentationsderivats für öffentlcihe und institutionelle Portale. 

<b>Genauere Beschreibungen der einzelnen Leistungsmerkmale:</b>

<c>Pre-Ingest, SIP Erstellung:</c>
* Leistungsmerkmal [Ein SIP aus dem Quellverzeichnis erstellen](../../../../SIP-Builder/src/manual/feature_create_sip_single.md)   
* Leistungsmerkmal [SIPs aus Unterordnern des Quellverzeichnisses erstellen](../../../../SIP-Builder/src/manual/feature_create_sips_multiple.md)   
* Leistungsmerkmal [SIPs aus verzweigter Ordnerstruktur erstellen (nur METS)](../../../../SIP-Builder/src/manual/feature_create_sips_nested.md)

<c>Erstellung von AIP, Präsentationsderivat und Archivverwaltung:</c>
* Leistungsmerkmal [Validierung der Einlieferung](feature_ingest_validation.md)
* Leistungsmerkmal [Vergabe von Identifiern](feature_identifier_assignment.md)
* Leistungsmerkmal [Ingest und Retrieval](feature_ingest_retrieval.md)
* Leistungsmerkmal [Delta Ingest und Retrieval](feature_delta_ingest_retrieval.md)
* Leistungsmerkmal [Anpassungen der Metadatenformate](feature_metadata_updates.md)
* Leistungsmerkmal [Umgang mit fehlerhaften IFD Tags bei TIFF](feature_tiff_problem_detection.md)
* Leistungsmerkmal [Migrationsbedingungen](feature_migration_right.md)
* Leistungsmerkmal [Integritätsprüfung](feature_integrity_check.md)
* Leistungsmerkmal [Publikation](feature_publication.md) 
* Leistungsmerkmal [Publikation Delta](feature_publication_delta.md)
* Leistungsmerkmal [Automatisierte Abfragen (Status und Retrieval)](feature_automated_queries.md) 
