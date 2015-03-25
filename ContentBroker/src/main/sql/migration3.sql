-- # DATABASE MIGRATION SCRIPTS FOR POSTGRES DATABASES
-- # FOR DATABASE MIGRATION FROM SCHEMA VERSION V1.2 TO V1.3
-- # @author : Daniel M. de Oliveira

BEGIN;
CREATE TABLE cooperating_nodes (node_id int, cooperating_node_id int);
CREATE TABLE copies (id serial, checksum varchar(255), checksumdate timestamp without time zone, path varchar(255), pkg_id integer, node_id integer);
ALTER TABLE copies ADD CONSTRAINT fk90d05272e5034e4b FOREIGN KEY (pkg_id) REFERENCES packages(id);
ALTER TABLE copies ADD CONSTRAINT fk90d05272e5033e4a FOREIGN KEY (node_id) REFERENCES nodes(id);
ALTER TABLE dafiles ADD COLUMN chksum varchar(255);
ALTER TABLE dafiles ADD COLUMN mimetype varchar(255);
CREATE TABLE messages (id serial, a varchar(255), q varchar(255), acknowledgeddate timestamp without time zone, date timestamp without time zone, expirationdate timestamp without time zone, msg_short varchar(2048),ref_identifier varchar(255), ref_identifier_type varchar(255),user_id integer);
ALTER TABLE nodes ADD COLUMN identifier varchar(255);
ALTER TABLE queue ADD COLUMN action_name varchar(255);
ALTER TABLE messages ADD CONSTRAINT fk_j3v4l57l24nd1rt1nm8c27l4g FOREIGN KEY (user_id) REFERENCES users(id);
ROLLBACK;