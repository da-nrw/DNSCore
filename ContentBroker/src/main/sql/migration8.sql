begin;

alter table dafiles add column userexceptionid integer;
alter table packages add column pruneexceptions boolean;

rollback;
