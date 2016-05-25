begin;

create table jhove_parameter_mapping (
id           int constraint jhove_parameter_mapping_pkey primary key,
map_parameter        character varying(255),
mime_type      character varying(255)
);


commit;