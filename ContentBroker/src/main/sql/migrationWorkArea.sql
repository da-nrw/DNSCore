begin;
/*Ausgehend von dem UAK-DNS Beschluss vom 2019-07-03: "JPGs werden entsprechend nicht mehr in JPG2000 umgewandelt."*/

DELETE FROM conversion_policies WHERE source_format LIKE 'fmt/41' AND presentation ='f';
DELETE FROM conversion_policies WHERE source_format LIKE 'fmt/42' AND presentation ='f';
DELETE FROM conversion_policies WHERE source_format LIKE 'fmt/43' AND presentation ='f';
DELETE FROM conversion_policies WHERE source_format LIKE 'fmt/44' AND presentation ='f';
DELETE FROM conversion_policies WHERE source_format LIKE 'x-fmt/398' AND presentation ='f';
DELETE FROM conversion_policies WHERE source_format LIKE 'x-fmt/390' AND presentation ='f';
DELETE FROM conversion_policies WHERE source_format LIKE 'x-fmt/391' AND presentation ='f';



/*Einrichten neuer Presentation Conversion-Policies, damit JPG in die PIP weitergereicht werden*/
INSERT INTO conversion_policies(id,source_format,conversion_routine_id,presentation,psystem_id) VALUES( 61,'fmt/41',8,'t',1 );
INSERT INTO conversion_policies(id,source_format,conversion_routine_id,presentation,psystem_id) VALUES( 62,'fmt/42',8,'t',1 );
INSERT INTO conversion_policies(id,source_format,conversion_routine_id,presentation,psystem_id) VALUES( 63,'fmt/44',8,'t',1 );
INSERT INTO conversion_policies(id,source_format,conversion_routine_id,presentation,psystem_id) VALUES( 64,'x-fmt/390',8,'t',1 );
INSERT INTO conversion_policies(id,source_format,conversion_routine_id,presentation,psystem_id) VALUES( 65,'x-fmt/391',8,'t',1 );
INSERT INTO conversion_policies(id,source_format,conversion_routine_id,presentation,psystem_id) VALUES( 66,'x-fmt/398',8,'t',1 );

/*dummy row mit einer geigneten id insert*/
INSERT INTO conversion_policies(id,source_format,presentation) SELECT MAX(id)+1,'fmt/41','t' FROM conversion_policies;
/*dummy row korrecctur*/
UPDATE conversion_policies t1, conversion_policies t2 SET t1.conversion_routine_id=t2.conversion_routine_id,t1.psystem_id=t2.psystem_id where t1.source_format LIKE 'fmt/41' AND t1.presentation ='t' AND t1.source_format LIKE 'fmt/43' AND t1.presentation ='f';
INSERT INTO conversion_policies(source_format,contractor_id,conversion_routine_id,presentation,psystem_id) SELECT 'fmt/41',contractor_id,conversion_routine_id,'t',psystem_id FROM conversion_policies where source_format='fmt/43' AND presentation ='t';




commit;
