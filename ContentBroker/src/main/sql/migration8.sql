begin;

alter table dafile add column userexceptionid integer;
alter table packages add column pruneexceptions boolean;

rollback;
