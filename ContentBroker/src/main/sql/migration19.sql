begin;
# Config Script for the delta feature behaviour

update users set delta_on_urn=FALSE;

commit;