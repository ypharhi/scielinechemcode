create materialized view FG_I_FILESOBJECT_CONNECTION_MV
refresh force on demand
as
select * from fg_i_filesobject_connection;
