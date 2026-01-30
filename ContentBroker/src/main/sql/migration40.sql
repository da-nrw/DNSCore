ALTER TABLE objects
ADD COLUMN quality_level_recheck boolean NOT NULL DEFAULT false;

ALTER TABLE packages
ADD COLUMN is_quality_level_recheck boolean NOT NULL DEFAULT false;


