create or replace view fg_s_requesttype_dt_v as
select DISTINCT "REQUESTTYPE_ID", "REQUESTTYPENAME" as "Request Type",
       (select distinct listagg(u.UNITSNAME || ' (' || u.SITENAME || ')',',')within group (order by u.UNITSNAME) over () from fg_s_units_all_v u  where instr(',' || T.UNITS_ID || ',',',' || u.UNITS_ID || ',') > 0 ) as "Request Type Units",
       decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
              from FG_S_REQUESTTYPE_ALL_V t;
