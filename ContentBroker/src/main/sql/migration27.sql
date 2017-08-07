begin;

/*ALTER TABLE objects DROP COLUMN IF EXISTS quality_flag;*/
ALTER TABLE objects ADD COLUMN quality_flag INTEGER;

/*old objects have quality level 3 becouse there were already migrateable in past*/
update objects SET quality_flag=3 WHERE quality_flag IS NULL;

commit;




begin;

/*ALTER TABLE users DROP COLUMN IF EXISTS required_ingest_quality;*/
ALTER TABLE users ADD COLUMN required_ingest_quality INTEGER;

update objects SET required_ingest_quality=0 WHERE required_ingest_quality IS NULL;

commit;