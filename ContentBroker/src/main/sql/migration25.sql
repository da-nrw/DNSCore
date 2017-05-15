begin;

ALTER TABLE users ADD  COLUMN friendly_file_exts varchar(256);

commit;