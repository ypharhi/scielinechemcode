create materialized view FG_S_PERMISSIONSREF_INF_MV
refresh force on demand
as
select distinct * from FG_S_PERMISSIONSREF_INF_V p
where ',' || p.PERMISSION || ',' like '%,'|| 'Read' || ',%';
