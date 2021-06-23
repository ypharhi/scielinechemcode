create or replace view fg_s_protocoltype_dt_v as
select DISTINCT "PROTOCOLTYPE_ID", "PROTOCOLTYPENAME" AS "Protocol Type Name"
        ,(select distinct listagg(u.UNITSNAME || ' (' || u.SITENAME || ')',',')within group (order by u.UNITSNAME) over () from fg_s_units_all_v u  where instr(',' || T.UNITS_ID || ',',',' || u.UNITS_ID || ',') > 0 ) as "Units"
        ,(select distinct listagg(pt.ProjectTypeName,',')within group (order by pt.ProjectTypeName) over () from FG_S_PROJECTTYPE_V pt  where instr(',' || T.PROJECTTYPE_ID || ',',',' || pt.projecttype_id || ',') > 0 ) as "Project Type"
        ,decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
              from FG_S_PROTOCOLTYPE_ALL_V t 
