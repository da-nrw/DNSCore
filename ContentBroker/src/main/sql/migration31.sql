begin;

/*ALTER TABLE objects DROP COLUMN IF EXISTS quality_flag;*/
ALTER TABLE objects ADD COLUMN quality_flag INTEGER;

/*old objects have quality level 3 because there were already migrateable in past*/
update objects SET quality_flag=3 WHERE quality_flag IS NULL;
commit;

begin;
ALTER TABLE users ADD COLUMN minimal_ingest_quality_level INTEGER;
update users SET minimal_ingest_quality_level=0 WHERE minimal_ingest_quality_level IS NULL;
commit;

begin;
ALTER TABLE conversion_policies ADD COLUMN  format_type varchar(20);
update conversion_policies SET format_type="LZA" WHERE presentation IS False;
update conversion_policies SET format_type="LZA" WHERE (source_format='fmt/353' or source_format='fmt/354' or source_format='fmt/5' or source_format='x-fmt/392' or source_format='fmt/141');
/*fmt/353=TIFF    fmt/354=PDF/A1b    fmt/5=AudioVideo Interleaved Format   x-fmt/392=JP2   fmt/141=WaveformAudio*/

commit;

/*remove unused depricated column*/
begin;
ALTER TABLE conversion_policies DROP contractor_id;
commit;


/*clean same entries after column remove*/
/*sehr wahrscheinlich nicht notwendig*/
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
