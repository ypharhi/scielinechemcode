create or replace view fg_s_reportfilterref_dte_v as
select "REPORTFILTERREF_ID",t."FORM_TEMP_ID",t."FORMID",t.PARENTID, t.ACTIVE, t.TABLETYPE,
t.SESSIONID, ROWSTATEKEY
,dense_rank() over (partition by t.ROWSTATEKEY, t.TABLETYPE order by t.formid) as "No._SMARTNUM"
--,fg_get_repref_rulename_list(T.RULENAME) as "Rule Name_SMARTEDIT" -- do it like the Step Name_SMARTEDIT (in generaldaoimp code)
,t.RULENAME as "Rule Name_SMARTEDIT"
,t.RULECONDITION as "Rule Condition_SMARTEDIT"
,t.columnsSelection as "Columns Selection_SMARTEDIT"
,t.STEPNAME as "Step No._SMARTEDIT"
,t.ColumnName as "Column Name_SMARTEDIT"
       --,FG_S_REPORTFILTERREF_pivot s
  FROM FG_S_REPORTFILTERREF_ALL_V T
  where 1=1--t.PARENTID = s.STEP_ID(+)
  and t.ACTIVE=1
  order by to_number(formid);
