begin;
alter table queue add column dynamic_nondisclosure_limit_institution character varying(255);
alter table queue add column static_nondisclosure_limit_institution timestamp without time zone;

alter table objects add column dynamic_nondisclosure_limit_institution character varying(255);
alter table objects add column static_nondisclosure_limit_institution timestamp without time zone;
rollback;
