begin;
 /*QualityLevel Fake Conversions, ONLY FOR Q-GRID*/
 /*Relevant nur für automatiserte Tests, somit nur für Q*/
INSERT INTO conversion_routines(id, target_suffix, type,name,params,psystem_id) values(12,'pdf','de.uzk.hki.da.convert.CLIConversionStrategy','LZA_CHI','QuAl1ty level test conversion instruction',1);
INSERT INTO conversion_policies(id, presentation, source_format,conversion_routine_id,psystem_id) values(59,false,'fmt/300',12,1);
commit;

