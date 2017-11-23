begin;

ALTER TABLE preservation_system ADD COLUMN license_validation_test_csn INTEGER;

update preservation_system SET license_validation_test_csn=1 WHERE license_validation_test_csn IS NULL;
commit;