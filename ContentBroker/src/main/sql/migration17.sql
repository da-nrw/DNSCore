begin;

ALTER TABLE dafiles
    ALTER COLUMN format_puid TYPE varchar(64);

commit;