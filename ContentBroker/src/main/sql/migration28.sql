begin;

alter table users add column use_mets_urn boolean;

update users set use_mets_urn = false;
commit;