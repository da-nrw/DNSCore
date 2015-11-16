begin;
alter table users add column mails_pooled boolean;

update users set mails_pooled = true;

create table pending_mail (
    id integer constraint pending_mails_pk primary key,
	from_address character varying(255),
	to_address character varying(255),
	subject character varying(255),
    message TEXT,
    pooled boolean, 
    retries integer,
	created  timestamp without time zone,
	last_try timestamp without time zone,
	node_name character varying(255)
);
commit;
