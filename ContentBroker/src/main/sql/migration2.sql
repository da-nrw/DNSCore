-- # DATABASE MIGRATION SCRIPTS FOR POSTGRES DATABASES
-- # FOR DATABASE MIGRATION FROM SCHEMA VERSION V1.1 TO V1.2
-- # @author : Daniel M. de Oliveira

BEGIN;
UPDATEs conversion_routines SET type='de.uzk.hki.da.convert.CLIConversionStrategy'  WHERE type='de.uzk.hki.da.format.CLIConversionStrategy';
UDPATE conversion_routines SET type='de.uzk.hki.da.convert.TiffConversionStrategy' WHERE type='de.uzk.hki.da.format.TiffConversionStrategy';
UPDATE conversion_routines SET type='de.uzk.hki.da.convert.PublishAudioConversionStrategy' WHERE type='de.uzk.hki.da.format.PublishAudioConversionStrategy';
UPDATE conversion_routines SET type='de.uzk.hki.da.convert.PublishVideoConversionStrategy' WHERE type='de.uzk.hki.da.format.PublishVideoConversionStrategy';
UPDATE conversion_routines SET type='de.uzk.hki.da.convert.PublishImageConversionStrategy' WHERE type='de.uzk.hki.da.format.PublishImageConversionStrategy';
UPDATE conversion_routines SET type='de.uzk.hki.da.convert.PublishPDFConversionStrategy'   WHERE type='de.uzk.hki.da.format.PublishPDFConversionStrategy';

ALTER TABLE dafiles RENAME file_format TO format_puid;
ALTER TABLE dafiles RENAME format_second_attribute TO subformat_identifier;
ALTER TABLE dafiles ADD COLUMN previousdafile_id integer;
ALTER TABLE dafiles ADD CONSTRAINT previousdafile_id_c FOREIGN KEY (previousdafile_id) REFERENCES dafiles (id);

DROP SEQUENCE second_stage_scans_id_seq;
DROP TABLE second_stage_scans;
CREATE TABLE subformat_identification_strategy_puid_mappings (id integer not null,format_puid varchar(255),subformat_identification_strategy_name varchar(255));

create table documents (id integer not null,doc varchar(255),lastdafile_id integer,object_id integer);
ALTER TABLE documents ADD PRIMARY KEY (id);
ALTER TABLE documents ADD CONSTRAINT object_id_c FOREIGN KEY (object_id) REFERENCES objects (data_pk);
ALTER TABLE documents ADD CONSTRAINT lastdafile_id_c FOREIGN KEY (lastdafile_id) REFERENCES dafiles (id);
ALTER TABLE documents ADD COLUMN doc_name varchar(255);
ROLLBACK;
