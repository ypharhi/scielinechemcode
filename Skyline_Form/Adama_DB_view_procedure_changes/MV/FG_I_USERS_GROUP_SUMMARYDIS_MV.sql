create materialized view FG_I_USERS_GROUP_SUMMARYDIS_MV
refresh force on demand
as
select distinct parentid, user_id, username
from fg_i_users_group_summary_v;
