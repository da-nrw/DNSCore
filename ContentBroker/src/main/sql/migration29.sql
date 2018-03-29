begin;

alter table users add column provider_type varchar(16);

/*update users set provider_type = 'Bibliothek' where provider_type is NULL;*/
update users set provider_type = 'Bibliothek' where short_name IN ('DE-5','DE-6','DE-61');
commit;