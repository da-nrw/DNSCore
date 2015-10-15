begin;
alter table objects add column last_publication_try timestamp without time zone;
rollback;
