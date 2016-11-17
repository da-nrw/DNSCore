begin;

ALTER TABLE objects DROP COLUMN IF EXISTS quality_flag;
ALTER TABLE objects ADD COLUMN quality_flag INTEGER;

/*old objects have quality level 3 becouse there were already migrateable in past*/
update objects SET quality_flag=3 WHERE quality_flag IS NULL;
commit;