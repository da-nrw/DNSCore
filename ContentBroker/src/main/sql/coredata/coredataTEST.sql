begin;
insert into conversion_routines(id, target_suffix, type,name,params,psystem_id) values(11,'pdf','de.uzk.hki.da.convert.CLIConversionStrategy','LZA_DOCX','convert input +compress output',1);



insert into conversion_policies(id, presentation, source_format,conversion_routine_id,psystem_id) values(11,false,'fmt/40',11,1);

commit;