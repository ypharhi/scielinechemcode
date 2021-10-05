CREATE OR REPLACE VIEW FG_S_PARAMREF_DTEEXP_V AS
select "PARAMREF_ID",t."FORM_TEMP_ID",t."FORMID",t.experiment_id as PARENTID, t.SESSIONID
    --'"hideColumns":{"condition":"Planned","cols":["Planned Sign_SMARTEDIT","Planned Value 1_SMARTNUM","Planned Value 2_SMARTNUM"]}
    ,'{"rowDisabledClass":"'||nvl2(s.STEPNAME, 'authorizationDisabled', '')||'"}' as "SMARTACTIONS"
    ,'' as "ROW_SELECTION_HELPER"
    ,nvl2(s.STEPNAME, t.parametername,fg_get_parameters_list(t.PARAMETER_ID)) as "Name_SMARTEDIT"
     ,'{"displayName":"'||nvl(PARAMETERDESC,'')|| '","htmlType":"text","autoSave":"true","dbColumnName":"PARAMETERDESC"}' as "Description_SMARTEDIT"
     --,t.PARAMETERDESC as "Description_SMARTEDIT"
    ,'{"displayName":"'||nvl2(s.STEPNAME, s.STEPNAME, 'Experiment')|| '","htmlType":""}' as "Level_SMARTEDIT"
      ,nvl2(s.STEPNAME, t.PARAMETERSCRITERIANAME, '{"displayName":[{"ID":"'||t.CRITERIA_ID||'","displayName":"'||t.PARAMETERSCRITERIANAME||'"}],"htmlType":"select","dbColumnName":"CRITERIA_ID","allowSingleDeselect":"false", "autoSave":"true",'||
                      ' "customFuncName":"onChangeDDL", "customFuncParams":["VAL2","><"],'||
                      ' "fullList":'||fg_get_criteria_list()||'}') as "Planned Sign_SMARTEDIT"
     ,nvl2(s.STEPNAME, fg_get_num_display(t.VAL1,0,3), '{"displayName":"'||fg_get_num_display(t.VAL1,0,3)|| '","htmlType":"text","dbColumnName":"VAL1","autoSave":"true"}') as "Planned Value 1_SMARTNUM"
     ,nvl2(s.STEPNAME, fg_get_num_display(t.VAL2,0,3), '{"displayName":"'||fg_get_num_display(t.VAL2,0,3)|| '","htmlType":"text","dbColumnName":"VAL2","autoSave":"true", "colCalcId":"VAL2",'||
                       ' "isDisabled":"'||decode(lower(t.PARAMETERSCRITERIANAME),'><','false','true')||'"}') as "Planned Value 2_SMARTNUM"
     ,nvl2(s.STEPNAME, fg_get_Uom_display (t.UOM_ID), '{"displayName":[{"ID":"'||t.UOM_ID||'","displayName":"'||fg_get_Uom_display (t.UOM_ID)||'"}],"htmlType":"select","dbColumnName":"UOM_ID", "allowSingleDeselect":"false", "autoSave":"true",'||
                     ' "fullList":'||fg_get_uom_key_val_list_byMP(t.PARAMETER_ID)||'}') as "UOM_SMARTEDIT"
      from FG_S_PARAMREF_ALL_V t,
           fg_s_step_all_v s
      where t.PARENTID = s.STEP_ID(+)
      and decode(upper(s.PROTOCOLTYPENAME),'CONTINUOUS PROCESS',nvl(s.PREPARATION_RUN,'Preparation'),'Preparation')='Preparation'--display preparation steps only in the CP experiment
      and t.ACTIVE=1
      order by t."PARAMREF_ID";
