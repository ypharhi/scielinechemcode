create materialized view FG_FORMELEMENTINFOATMETA_ID_MV
refresh force on demand
as
select distinct m.islistid , m.formcode_entity, m.entityimpcode
  from   FG_FORMELEMENTINFOATMETA_MV m
  where 1=1;
