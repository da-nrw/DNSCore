begin;

ALTER TABLE users ADD COLUMN delta_on_urn boolean;

commit;
