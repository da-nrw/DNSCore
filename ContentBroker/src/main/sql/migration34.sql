begin;


ALTER TABLE queue ADD COLUMN error_text text;

commit;
