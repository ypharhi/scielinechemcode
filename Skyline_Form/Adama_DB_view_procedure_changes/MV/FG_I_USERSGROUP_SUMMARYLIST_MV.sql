create materialized view FG_I_USERSGROUP_SUMMARYLIST_MV
refresh force on demand
as
select DISTINCT parentid, LISTAGG (user_id, ',') WITHIN GROUP (ORDER BY user_id) OVER (PARTITION BY parentid) AS user_id
from (
      select "PARENTID","USER_ID","USERNAME"
      from fg_i_users_group_summarydis_mv
);
