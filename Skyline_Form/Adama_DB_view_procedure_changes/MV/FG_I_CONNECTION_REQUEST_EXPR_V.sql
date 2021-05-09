create materialized view FG_I_CONNECTION_REQUEST_EXPR_V
refresh force on demand
as
select * FROM FG_I_CONNECTION_REQUEST_EXPR_T;
