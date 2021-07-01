create or replace view fg_s_reportfilterref_dte_v as
select "REPORTFILTERREF_ID",t."FORM_TEMP_ID",t."FORMID",t.PARENTID, t.ACTIVE, t.TABLETYPE,
t.SESSIONID, -1 as DUUMY, ROWSTATEKEY
,fg_get_repref_rulename_list(T.RULENAME) as "Rule Name_SMARTEDIT" -- do it like the Step Name_SMARTEDIT (in generaldaoimp code)
,t.STEPNAME as "Step Name_SMARTEDIT"
       --,FG_S_REPORTFILTERREF_pivot s
  FROM FG_S_REPORTFILTERREF_ALL_V T
  where 1=1--t.PARENTID = s.STEP_ID(+)
  and t.ACTIVE=1
  order by "REPORTFILTERREF_ID";
