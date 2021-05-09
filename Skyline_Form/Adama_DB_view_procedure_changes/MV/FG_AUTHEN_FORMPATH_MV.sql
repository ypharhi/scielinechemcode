create materialized view FG_AUTHEN_FORMPATH_MV
refresh force on demand
as
select * from FG_AUTHEN_FORMPATH_TMP;
