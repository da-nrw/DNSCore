begin;

alter table users add column provider_type varchar(16);

update users set provider_type = 'Bibliothek';
commit;