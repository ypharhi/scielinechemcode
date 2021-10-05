CREATE OR REPLACE VIEW FG_S_PARAMREF_DTEEXP_ACT_V AS
select "PARAMREF_ID",t."FORM_TEMP_ID",t."FORMID",t.PARENTID, t.SESSIONID
      ,'{"rowDisabledClass":"'||nvl2(t.STEPNAME, 'authorizationDisabled', '')||'"}' as "SMARTACTIONS"
      ,'' as "ROW_SELECTION_HELPER"
      ,nvl2(t.STEPNAME, t.parametername, fg_get_parameters_list(t.PARAMETER_ID)) as "Name_SMARTEDIT"
      ,'{"displayName":"'||nvl(PARAMETERDESC,'')|| '","htmlType":"text","autoSave":"true","dbColumnName":"PARAMETERDESC"}' as "Description_SMARTEDIT"
      ,'{"displayName":"'||nvl2(t.STEPNAME, t.STEPNAME, 'Experiment')|| '","htmlType":""}' as "Level_SMARTEDIT"
      ,'{"displayName":"'||nvl2(t.STEPNAME,nvl2(t.isPlanned,t.PARAMETERSCRITERIANAME,t.PLANNEDPARAMETERSCRITERIANAME),t.PLANNEDPARAMETERSCRITERIANAME)|| '","htmlType":""}' as "Planned Sign_SMARTEDIT"
      ,'{"displayName":"'||nvl2(t.STEPNAME,nvl2(t.isPlanned,t.VAL1,t.PLANNEDVAL1),t.PLANNEDVAL1)|| '","htmlType":""}' as "Planned Value 1_SMARTNUM"
      ,'{"displayName":"'||nvl2(t.STEPNAME,nvl2(t.isPlanned,t.VAL2,t.PLANNEDVAL2),t.PLANNEDVAL2)|| '","htmlType":""}' as "Planned Value 2_SMARTNUM"
        ,nvl2(t.STEPNAME, nvl2(t.isPlanned,'',t.PARAMETERSCRITERIANAME), '{"displayName":[{"ID":"'||t.CRITERIA_ID||'","displayName":"'||t.PARAMETERSCRITERIANAME||'"}],"htmlType":"select","dbColumnName":"CRITERIA_ID","allowSingleDeselect":"false", "autoSave":"true",'||
                        ' "customFuncName":"onChangeDDL", "customFuncParams":["VAL2","><"],'||
                        ' "fullList":'||fg_get_criteria_list()||'}') as "Actual Sign_SMARTEDIT"
       ,nvl2(t.STEPNAME,nvl2(t.isPlanned,'',t.VAL1),'{"displayName":"'||t.VAL1|| '","htmlType":"text","dbColumnName":"VAL1","autoSave":"true"}') as "Actual Value 1_SMARTNUM"
       ,nvl2(t.STEPNAME, nvl2(t.isPlanned,'',t.VAL2), '{"displayName":"'||t.VAL2|| '","htmlType":"text","dbColumnName":"VAL2","autoSave":"true", "colCalcId":"VAL2",'||
                         ' "isDisabled":"'||decode(lower(t.PARAMETERSCRITERIANAME),'><','false','true')||'"}') as "Actual Value 2_SMARTNUM"
       ,nvl2(t.STEPNAME, fg_get_Uom_display (t.UOM_ID), '{"displayName":[{"ID":"'||t.UOM_ID||'","displayName":"'||fg_get_Uom_display (t.UOM_ID)||'"}],"htmlType":"select","dbColumnName":"UOM_ID", "allowSingleDeselect":"false", "autoSave":"true",'||
                       ' "fullList":'||fg_get_uom_key_val_list_byMP(t.PARAMETER_ID)||'}') as "UOM_SMARTEDIT"
 from (
      select "PARAMREF_ID",pr."FORM_TEMP_ID",pr."FORMID",pr.experiment_id as PARENTID, pr.SESSIONID
             , st.STEPSTATUSNAME, s.STEPNAME,pr.PARAMETERDESC
             ,decode(st.STEPSTATUSNAME,'Planned',1,null) isPlanned
             ,pr.PARAMETER_ID, pr.parametername
             , pr.PLANNED_CRITERIA_ID, pr.PLANNEDPARAMETERSCRITERIANAME
             , fg_get_num_display(pr.PLANNEDVAL1,0,3) as PLANNEDVAL1, fg_get_num_display(pr.PLANNEDVAL2,0,3) as PLANNEDVAL2
             ,pr.CRITERIA_ID, pr.PARAMETERSCRITERIANAME
             ,fg_get_num_display(pr.VAL1,0,3) as VAL1, fg_get_num_display(pr.VAL2,0,3) as VAL2
             ,pr.UOM_ID
        from FG_S_PARAMREF_ALL_V pr,
             FG_S_STEP_ALL_V s, fg_s_stepstatus_v st
        where s.STATUS_ID = st.STEPSTATUS_ID(+)
        and pr.PARENTID = s.STEP_ID(+)
        and pr.ACTIVE=1
        and decode(upper(s.PROTOCOLTYPENAME),'CONTINUOUS PROCESS',nvl(s.PREPARATION_RUN,'Preparation'),'Preparation')='Preparation'--display preparation steps only in the CP experiment
        order by "PARAMREF_ID"
 ) t;
