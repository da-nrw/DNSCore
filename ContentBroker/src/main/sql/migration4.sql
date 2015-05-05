-- # DATABASE MIGRATION SCRIPTS FOR POSTGRES DATABASES
-- # FOR DATABASE MIGRATION FROM SCHEMA VERSION V1.3 TO V1.4
-- # @author : Jens Peters

BEGIN;
 CREATE table copyjob (id SERIAL, dest_node_identifier VARCHAR(255), last_tried timestamp without time zone, params VARCHAR(255), source VARCHAR(255), source_node_identifier VARCHAR(255));
 ROLLBACK;