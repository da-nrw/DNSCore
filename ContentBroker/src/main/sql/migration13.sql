begin;
 
create table format_mapping (
    fm_id int constraint format_mapping_pk primary key,
	puid character varying(255),
	extension character varying(255),
	mime_type character varying(255),
	format_name  character varying(255)
);
commit;
