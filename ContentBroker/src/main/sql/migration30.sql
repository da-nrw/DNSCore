begin;

ALTER TABLE copies ADD  COLUMN checksum_type varchar(16);
ALTER TABLE copies ADD  COLUMN checksum_base64 varchar(255);
/*ALTER TABLE copies ADD  COLUMN checksum varchar(255);*/

commit;