begin;

ALTER TABLE dafiles ADD COLUMN size varchar(255);

commit;
