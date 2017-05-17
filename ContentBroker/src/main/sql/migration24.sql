begin;

ALTER TABLE queue ADD COLUMN created_at timestamp without time zone;
update queue set created_at = timestamp '1970-01-01 00:00:00+00' + (date_created::bigint || ' second')::interval; 

ALTER TABLE queue ADD COLUMN modified_at timestamp without time zone;
update queue set  modified_at = timestamp '1970-01-01 00:00:00+00' + (date_modified::bigint || ' second')::interval; 

ALTER TABLE objects ADD COLUMN created_at timestamp without time zone;
update objects set created_at = timestamp '1970-01-01 00:00:00+00' + (date_created::bigint / 1000 || ' second')::interval; 

ALTER TABLE objects ADD COLUMN modified_at timestamp without time zone;
update objects set modified_at = timestamp '1970-01-01 00:00:00+00' + (date_modified::bigint / 1000 || ' second')::interval; 

commit;