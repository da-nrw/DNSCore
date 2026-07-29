begin;
/*schema: subformat_identification_strategy_puid_mappings (id integer not null,format_puid varchar(255),subformat_identification_strategy_name varchar(255));*/

/* QuickTime 1 = x-fmt/384 */
insert into subformat_identification_strategy_puid_mappings(id,format_puid,subformat_identification_strategy_name) values (2,'x-fmt/384','de.uzk.hki.da.format.FFmpegSubformatIdentifier');
/* Material Exchange Format = fmt/200 */
insert into subformat_identification_strategy_puid_mappings(id,format_puid,subformat_identification_strategy_name) values (3,'fmt/200','de.uzk.hki.da.format.FFmpegSubformatIdentifier');
/* Audio/Video Interleaved Format = fmt/5 */
insert into subformat_identification_strategy_puid_mappings(id,format_puid,subformat_identification_strategy_name) values (4,'fmt/5','de.uzk.hki.da.format.FFmpegSubformatIdentifier');
/* TIF/TIFF = x-fmt/353 */
insert into subformat_identification_strategy_puid_mappings(id,format_puid,subformat_identification_strategy_name) values (5,'x-fmt/353','de.uzk.hki.da.format.ImageMagickSubformatIdentifier');
/* MPEG-1 Program Stream (avi + mpeg4) = x/fmt=385*/
insert into subformat_identification_strategy_puid_mappings(id,format_puid,subformat_identification_strategy_name) values (6,'x-fmt/385','de.uzk.hki.da.format.FFmpegSubformatIdentifier');

commit;

