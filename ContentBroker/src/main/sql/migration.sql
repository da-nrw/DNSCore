-- # DATABASE MIGRATION SCRIPTS FOR POSTGRES DATABASES
-- # FOR DATABASE MIGRATION FROM SCHEMA VERSION V1.0 TO V1.1
-- # @author : Daniel M. de Oliveira
-- #   under partial usage of apgdiff 2.4

BEGIN;

ALTER TABLE objects ADD CONSTRAINT unique_object_identifier UNIQUE (identifier);

drop sequence user_id_seq;
alter table contractors rename to users;
alter table users drop column admin;
alter table users drop column urn_index;
alter table users add column username varchar(255);
alter table users add column password varchar(255);
alter table users add column accountlocked boolean;
alter table users add column enabled boolean;
alter table users add column passwordexpired boolean;
alter table users add column accountexpired boolean;
alter table users add column description varchar(255);

update users set accountlocked=false;
update users set accountexpired=false;
update users set enabled=true;
update users set passwordexpired=false;
update users set
password='$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.' where
short_name='rods';
update users set username=short_name;
CREATE TABLE "role" (
        id integer NOT NULL,
        authority character varying(255)
);

CREATE TABLE user_role (
        user_id integer NOT NULL,
        role_id integer NOT NULL
);
INSERT INTO role (id,authority) values (1,'ROLE_PSADMIN');
INSERT INTO role (id,authority) values (2,'ROLE_CONTRACTOR');
INSERT INTO role (id,authority) values (3,'ROLE_NODEADMIN');

CREATE TABLE nodes_contractors (
	node_id integer NOT NULL,
	contractor_user_id integer NOT NULL
);

CREATE TABLE preservation_system (
        id integer NOT NULL,
        closed_collection_name character varying(255),
        min_repls integer NOT NULL,
        open_collection_name character varying(255),
        pres_server character varying(255),
        sidecar_extensions character varying(255),
        uris_aggr character varying(255),
        uris_cho character varying(255),
        uris_file character varying(255),
        uris_local character varying(255),
        urn_name_space character varying(255),
        admin_id integer NOT NULL
);

INSERT INTO preservation_system (id,admin_id,min_repls) VALUES (1,(select id from users
where short_name='rods'),3);

ALTER TABLE conversion_queue DROP CONSTRAINT uniqueness;

DROP TABLE conversion_routines_nodes;

DROP TABLE file_formats;

ALTER TABLE conversion_policies
	-- DROP COLUMN contractor_id,
	ADD COLUMN presentation boolean,
	ADD COLUMN psystem_id integer,
	ALTER COLUMN id DROP DEFAULT;

update conversion_policies set psystem_id=1;
update conversion_policies set presentation=false where contractor_id=(select id from users where short_name='DEFAULT');
update conversion_policies set presentation=true where contractor_id=(select id from users where short_name='PRESENTER');

ALTER TABLE conversion_policies
	ALTER COLUMN presentation SET NOT NULL;

delete from users where short_name='DEFAULT';
delete from users where short_name='PRESENTER';

insert into user_role (role_id,user_id) values (1,(select id from users where
short_name='rods'));
insert into user_role (role_id,user_id) values (2,(select id from users where
short_name='rods'));
insert into user_role (role_id,user_id) values (3,(select id from users where
short_name='rods'));
insert into user_role (role_id,user_id) select 3,id from users where
short_name!='rods';

ALTER TABLE conversion_queue
	DROP COLUMN file_name,
	DROP COLUMN "date",
	ALTER COLUMN id DROP DEFAULT;

ALTER TABLE conversion_routines
	DROP COLUMN intermediate_folder,
	ADD COLUMN psystem_id integer,
	ALTER COLUMN id DROP DEFAULT;

update conversion_routines set psystem_id=1;

ALTER TABLE dafiles
	ALTER COLUMN id DROP DEFAULT,
	ALTER COLUMN conversion_instruction_id SET NOT NULL;

ALTER TABLE events
	ALTER COLUMN id DROP DEFAULT;

ALTER TABLE nodes
	DROP COLUMN working_resource,
	DROP COLUMN num_threads,
	DROP COLUMN repl_destinations,
	ADD COLUMN admin_id integer,
	ADD COLUMN psystem_id integer,
	ALTER COLUMN id DROP DEFAULT,
	ALTER COLUMN urn_index SET NOT NULL;

update conversion_routines set psystem_id=1;

ALTER TABLE objects
	DROP COLUMN contractor_short_name,
	DROP COLUMN contractor_id,
	ADD COLUMN user_id integer,
	ALTER COLUMN data_pk SET NOT NULL,
	ALTER COLUMN object_state SET NOT NULL,
	ALTER COLUMN published_flag SET NOT NULL;

ALTER TABLE objects_packages
	ALTER COLUMN objects_data_pk SET NOT NULL,
	ALTER COLUMN packages_id SET NOT NULL;

ALTER TABLE packages
	DROP COLUMN objecturn,
	ALTER COLUMN id SET NOT NULL;

ALTER TABLE queue
	DROP COLUMN urn,
	DROP COLUMN orig_name,
	DROP COLUMN node_name,
	DROP COLUMN package_name,
	DROP COLUMN physical_path,
	DROP COLUMN packages_id,
	DROP COLUMN contractor_id,
	ADD COLUMN question character varying(255),
	ADD COLUMN answer character varying(255),
	ALTER COLUMN id DROP DEFAULT;

ALTER TABLE second_stage_scans
	ALTER COLUMN id DROP DEFAULT;


delete from queue where objects_id not in (select data_pk from objects);
delete from conversion_queue where job_id not in (select id from queue);
delete from conversion_queue where source_file_id not in (select id from dafiles);
delete from dafiles where pkg_id not in (select id from packages);
delete from events where pkg_id not in (select id from packages);
delete from events where type in ('CONVERT','COPY') and source_file_id not in (select id from dafiles);
delete from objects_packages where objects_data_pk not in (select data_pk from objects);
delete from objects_packages where packages_id not in (select id from packages);

ALTER TABLE conversion_policies
	ADD CONSTRAINT conversion_policies_pkey PRIMARY KEY (id);

ALTER TABLE conversion_queue
	ADD CONSTRAINT conversion_queue_pkey PRIMARY KEY (id);

ALTER TABLE conversion_routines
	ADD CONSTRAINT conversion_routines_pkey PRIMARY KEY (id);

ALTER TABLE dafiles
	ADD CONSTRAINT dafiles_pkey PRIMARY KEY (id);

ALTER TABLE events
	ADD CONSTRAINT events_pkey PRIMARY KEY (id);

ALTER TABLE nodes
	ADD CONSTRAINT nodes_pkey PRIMARY KEY (id);

ALTER TABLE nodes_contractors
	ADD CONSTRAINT nodes_contractors_pkey PRIMARY KEY (node_id, contractor_user_id);

ALTER TABLE objects
	ADD CONSTRAINT objects_pkey PRIMARY KEY (data_pk);

ALTER TABLE packages
	ADD CONSTRAINT packages_pkey PRIMARY KEY (id);

ALTER TABLE preservation_system
	ADD CONSTRAINT preservation_system_pkey PRIMARY KEY (id); 
ALTER TABLE queue
	ADD CONSTRAINT queue_pkey PRIMARY KEY (id);

ALTER TABLE "role"
	ADD CONSTRAINT role_pkey PRIMARY KEY (id);

ALTER TABLE second_stage_scans
	ADD CONSTRAINT second_stage_scans_pkey PRIMARY KEY (id);

ALTER TABLE user_role
	ADD CONSTRAINT user_role_pkey PRIMARY KEY (user_id, role_id);

ALTER TABLE users
	ADD CONSTRAINT users_pkey PRIMARY KEY (id);

ALTER TABLE conversion_policies
	ADD CONSTRAINT fk682236594951d234 FOREIGN KEY (conversion_routine_id) REFERENCES conversion_routines(id);

ALTER TABLE conversion_policies
	ADD CONSTRAINT fk6822365959ddd41f FOREIGN KEY (psystem_id) REFERENCES preservation_system(id);

ALTER TABLE conversion_queue
	ADD CONSTRAINT fk815618a81f42cf01 FOREIGN KEY (job_id) REFERENCES queue(id);

ALTER TABLE conversion_queue
	ADD CONSTRAINT fk815618a84951d234 FOREIGN KEY (conversion_routine_id) REFERENCES conversion_routines(id);

ALTER TABLE conversion_queue
	ADD CONSTRAINT fk815618a88013be4c FOREIGN KEY (source_file_id) REFERENCES dafiles(id);

ALTER TABLE conversion_routines
	ADD CONSTRAINT fk47ddfd3859ddd41f FOREIGN KEY (psystem_id) REFERENCES preservation_system(id);

ALTER TABLE dafiles
	ADD CONSTRAINT fk5543f41a6fdfd79b FOREIGN KEY (pkg_id) REFERENCES packages(id);

ALTER TABLE events
	ADD CONSTRAINT fkb307e1196fdfd79b FOREIGN KEY (pkg_id) REFERENCES packages(id);

ALTER TABLE events
	ADD CONSTRAINT fkb307e1197273f1c2 FOREIGN KEY (target_file_id) REFERENCES dafiles(id);

ALTER TABLE events
	ADD CONSTRAINT fkb307e1198013be4c FOREIGN KEY (source_file_id) REFERENCES dafiles(id);

ALTER TABLE nodes
	ADD CONSTRAINT nodes_admin_id_key UNIQUE (admin_id);

ALTER TABLE nodes
	ADD CONSTRAINT fk64212b159ddd41f FOREIGN KEY (psystem_id) REFERENCES preservation_system(id);

ALTER TABLE nodes
	ADD CONSTRAINT fk64212b1e5085e4f FOREIGN KEY (admin_id) REFERENCES users(id);

ALTER TABLE nodes_contractors
	ADD CONSTRAINT fk2eaa1c109cd05889 FOREIGN KEY (contractor_user_id) REFERENCES users(id);

ALTER TABLE nodes_contractors
	ADD CONSTRAINT fk2eaa1c109cd094f3 FOREIGN KEY (node_id) REFERENCES nodes(id);

ALTER TABLE objects
	ADD CONSTRAINT fk9d13c5141606d453 FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE objects_packages
	ADD CONSTRAINT objects_packages_packages_id_key UNIQUE (packages_id);

ALTER TABLE objects_packages
	ADD CONSTRAINT fk343d37b88bad65fd FOREIGN KEY (objects_data_pk) REFERENCES objects(data_pk);

ALTER TABLE objects_packages
	ADD CONSTRAINT fk343d37b8e0e854da FOREIGN KEY (packages_id) REFERENCES packages(id);

ALTER TABLE preservation_system
	ADD CONSTRAINT preservation_system_admin_id_key UNIQUE (admin_id);

ALTER TABLE preservation_system
	ADD CONSTRAINT fk90d05272e5085e4f FOREIGN KEY (admin_id) REFERENCES users(id);

ALTER TABLE queue
	ADD CONSTRAINT fk66f1911cfe5879e FOREIGN KEY (objects_id) REFERENCES objects(data_pk);

ALTER TABLE queue
	ADD CONSTRAINT fk66f1911df7d9ef4 FOREIGN KEY (parent_id) REFERENCES queue(id);

ALTER TABLE user_role
	ADD CONSTRAINT fk143bf46a1606d453 FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE user_role
	ADD CONSTRAINT fk143bf46a70dc1073 FOREIGN KEY (role_id) REFERENCES role(id);
ROLLBACK;
