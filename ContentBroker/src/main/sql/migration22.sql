begin;

ALTER TABLE nodes DROP COLUMN IF EXISTS retrieval_remain_time;
ALTER TABLE nodes ADD COLUMN retrieval_remain_time INTEGER;

update nodes SET retrieval_remain_time=2 WHERE retrieval_remain_time IS NULL;
commit;