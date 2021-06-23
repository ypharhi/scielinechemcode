create or replace view fg_s_experimenttype_dt_v as
select "EXPERIMENTTYPE_ID",
       "EXPERIMENTTYPENAME" as "Experiment Type Name",
        t.EXPERIMENTVIEWNAME "Expr. view (if no instrument)",
        (select distinct listagg(ev.EXPERIMENTVIEWNAME,',')within group (order by ev.EXPERIMENTVIEWNAME) over () from fg_s_ExperimentView_all_v ev  where instr(',' || t.EXPERIMENTVIEWLIST || ',',',' || ev.EXPERIMENTVIEW_ID || ',') > 0 )  as "Expr. views (instruments)",
       T.PROTOCOLTYPENAME AS "Protocol Type",
       (select distinct listagg(u.UNITSNAME || ' (' || u.SITENAME || ')',',')within group (order by u.UNITSNAME) over () from fg_s_units_all_v u  where instr(',' || T.UNITS_ID || ',',',' || u.UNITS_ID || ',') > 0 ) as "Protocol Type Units",
       T.REQUESTTYPENAME AS "Request Type", 
       t.REQUESTVIEWNAME "Request View",
       (select distinct listagg(u.UNITSNAME,',')within group (order by u.UNITSNAME) over () from fg_s_units_all_v u  where instr(',' || rt.UNITS_ID || ',',',' || u.UNITS_ID || ',') > 0 )  as "Request Type Units",
       decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
from FG_S_EXPERIMENTTYPE_ALL_V t,
     fg_s_requesttype_all_v rt
where nvl(t.ACTIVE,'1') <> '0'
and  t.REQUESTTYPE_ID = rt.requesttype_id(+);
