begin;
alter table users add column use_public_mets boolean;

update users set use_public_mets = false;

commit;
