CREATE OR REPLACE VIEW FG_S_PARAMREF_DTE_V AS
select "PARAMREF_ID",t."FORM_TEMP_ID",t."FORMID",t.PARENTID
,'{"hideColumns":{"condition":"Planned","cols":["Planned Sign","Planned Value 1","Planned Value 2"]}}' as "SMARTACTIONS"
,'' as "ROW_SELECTION_HELPER"
,fg_get_parameters_list(t.PARAMETER_ID,t.exp_project_id,t.exp_SUBPROJECT_ID) as "Name_SMARTEDIT"
,'{"displayName":"'||nvl(PARAMETERDESC,'')|| '","htmlType":"text","autoSave":"true","dbColumnName":"PARAMETERDESC"}' as "Description_SMARTEDIT"
/*'{"displayName":[{"ID":"'||t.PARAMETER_ID||'","displayName":"'||t.parametername||'"}],"htmlType":"select","dbColumnName":"PARAMETER_ID", "colCalcId":"PARAMETER_ID",'||
                   ' "allowSingleDeselect":"false", "autoSave":"true",'||
                   ' "fullList":'||fg_get_parameters_list()||'}' as "Name_SMARTEDIT"*/
/*,t.PLANNEDPARAMETERSCRITERIANAME as "Planned Sign"
,fg_get_num_display(t.PLANNEDVAL1,0,3) as "Planned Value 1"
,fg_get_num_display(t.PLANNEDVAL2,0,3) as "Planned Value 2"*/
  ,'{"displayName":[{"ID":"'||t.CRITERIA_ID||'","displayName":"'||t.PARAMETERSCRITERIANAME||'"}],"htmlType":"select","dbColumnName":"CRITERIA_ID","allowSingleDeselect":"false", "autoSave":"true",'||
                  ' "customFuncName":"onChangeDDL", "customFuncParams":["VAL2","><"],'||
                  ' "fullList":'||fg_get_criteria_list()||'}' as "Planned Sign_SMARTEDIT"
 ,'{"displayName":"'||fg_get_num_display(t.VAL1,0,3)|| '","htmlType":"text","dbColumnName":"VAL1","autoSave":"true"}' as "Planned Value 1_SMARTNUM"
 ,'{"displayName":"'||fg_get_num_display(t.VAL2,0,3)|| '","htmlType":"text","dbColumnName":"VAL2","autoSave":"true", "colCalcId":"VAL2",'||
                   ' "isDisabled":"'||decode(lower(t.PARAMETERSCRITERIANAME),'><','false','true')||'"}' as "Planned Value 2_SMARTNUM"
 ,'{"displayName":[{"ID":"'||t.UOM_ID||'","displayName":"'||fg_get_Uom_display (t.UOM_ID)||'"}],"htmlType":"select","dbColumnName":"UOM_ID", "allowSingleDeselect":"false", "autoSave":"true",'||
                 ' "fullList":'||fg_get_uom_key_val_list_byMP(t.PARAMETER_ID)||'}' as "UOM_SMARTEDIT"
  from FG_S_PARAMREF_ALL_V t
       --,fg_s_step_all_v s
  where 1=1--t.PARENTID = s.STEP_ID(+)
  and t.ACTIVE=1
  order by "PARAMREF_ID";
