create materialized view FG_I_RESUSINGTOUPDATE_MV
refresh force on demand
as
select * from FG_I_RESUSINGTOUPDATE_V;
