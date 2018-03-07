begin;

<<<<<<< HEAD
ALTER TABLE packages ADD COLUMN delta int;
ALTER TABLE packages ADD COLUMN repair int;
UPDATE packages SET delta = name::int;

ALTER TABLE copies ADD COLUMN repair int;

=======
ALTER TABLE preservation_system ADD COLUMN license_validation_test_csn INTEGER;

update preservation_system SET license_validation_test_csn=1 WHERE license_validation_test_csn IS NULL;
>>>>>>> master
commit;