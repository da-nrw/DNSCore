begin;

/*ALTER TABLE objects DROP COLUMN IF EXISTS quality_flag;*/
ALTER TABLE objects ADD COLUMN quality_flag INTEGER;

/*old objects have quality level 3 becouse there were already migrateable in past*/
update objects SET quality_flag=3 WHERE quality_flag IS NULL;
commit;


begin;
ALTER TABLE users ADD COLUMN required_ingest_quality INTEGER;
update users SET required_ingest_quality=0 WHERE required_ingest_quality IS NULL;
commit;


begin;
ALTER TABLE conversion_policies ADD COLUMN  format_type
commit;


/*remove unused depricated column*/
begin;
ALTER TABLE conversion_policies DROP contractor_id;
commit;


/*clean same entries after column remove*/
/*
begin;
DELETE FROM conversion_policies
LEFT OUTER JOIN (
   SELECT MIN(id) as id, source_format, conversion_routine_id, presentation, psystem_id 
   FROM conversion_policies 
   GROUP BY source_format, conversion_routine_id, presentation, psystem_id
) as KeepRows ON
   MyTable.id = KeepRows.id
WHERE
   KeepRows.id IS NULL  
commit;
*/

