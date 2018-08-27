begin;

alter table users add column use_mets_urn boolean;
alter table users column use_mets_urn set default false; 

update users set use_mets_urn = false;
commit;