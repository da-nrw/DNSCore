begin;
alter table users add column use_virus_scan boolean;

update users set use_virus_scan = true;

commit;
