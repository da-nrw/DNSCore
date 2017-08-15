begin;

/*ALTER TABLE objects DROP COLUMN IF EXISTS license_flag;*/
ALTER TABLE objects ADD COLUMN license_flag INTEGER;

update objects SET license_flag=-1 WHERE license_flag IS NULL;
commit;



begin;

/*ALTER TABLE preservation_system DROP COLUMN IF EXISTS license_validation;*/
ALTER TABLE preservation_system ADD COLUMN license_validation INTEGER;

update preservation_system SET license_validation=1 WHERE license_validation IS NULL;
commit;