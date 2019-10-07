begin;
/*Ausgehend von dem UAK-DNS Beschluss vom 2019-07-03: "JPGs werden entsprechend nicht mehr in JPG2000 umgewandelt."*/

DELETE FROM conversion_policies WHERE source_format LIKE 'fmt/41' AND presentation ='f';
DELETE FROM conversion_policies WHERE source_format LIKE 'fmt/42' AND presentation ='f';
DELETE FROM conversion_policies WHERE source_format LIKE 'fmt/43' AND presentation ='f';
DELETE FROM conversion_policies WHERE source_format LIKE 'fmt/44' AND presentation ='f';
DELETE FROM conversion_policies WHERE source_format LIKE 'x-fmt/398' AND presentation ='f';
DELETE FROM conversion_policies WHERE source_format LIKE 'x-fmt/390' AND presentation ='f';
DELETE FROM conversion_policies WHERE source_format LIKE 'x-fmt/391' AND presentation ='f';

commit;
