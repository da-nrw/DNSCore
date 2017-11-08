begin;

ALTER TABLE packages ADD COLUMN delta int;
ALTER TABLE packages ADD COLUMN repair int;
UPDATE packages SET delta = name::int;

ALTER TABLE copies ADD COLUMN repair int;

commit;