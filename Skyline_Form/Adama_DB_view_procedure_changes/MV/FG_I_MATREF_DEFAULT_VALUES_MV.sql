create materialized view FG_I_MATREF_DEFAULT_VALUES_MV
refresh force on demand
as
select * from FG_I_DEFAULT_VALUES_MATREF_V;
