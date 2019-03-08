begin;

/*ALTER TABLE objects DROP COLUMN IF EXISTS aip_size;*/
ALTER TABLE objects ADD COLUMN aip_size BIGINT;

update aip_size SET aip_size=-1 WHERE aip_size IS NULL;
commit;
