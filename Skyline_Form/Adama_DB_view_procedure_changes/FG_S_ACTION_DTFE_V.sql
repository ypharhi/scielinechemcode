CREATE OR REPLACE VIEW FG_S_ACTION_DTFE_V AS
with FILT_IN_DISTINCT as -- DTFE F -> FORMULATION
(select distinct "ACTION_ID", t.FORMID,t."FORM_TEMP_ID",t."TIMESTAMP",t."ACTIVE",STEP_ID, t.FORMNUMBERID, t.FORMCODE,
        t.OBSERVATION,t.INSTRUCTION,t.ACTIONNAME, t.STEPSTATUSNAME, t.ENDTIME,t.ENDDATE, t.STARTTIME ,t.STARTDATE,
        t.DOCUMENTS,t.cloneid, mp.formid as "MPFORMID", mp.parammonitoringobj, t.SELFTESTDEFAULTDATAHOLDER, t.selftestidholder
 from FG_S_ACTION_ALL_V t, FG_S_PARAMMONITORING_PIVOT mp
  where 1=1
  and t.FORMID = mp.parentid(+)
  and mp.sessionid(+) is null
  and mp.Active(+) = 1
  and t.ACTIVE = 1
  order by FORMNUMBERID
)
select t1."ACTION_ID",t1."FORM_TEMP_ID",t1."TIMESTAMP",t1."ACTIVE",t1."STEP_ID",t1."FORMNUMBERID",t1."MPFORMID",
       --  '{"default_initial_width":"20em","FORMNUMBER":"2em","Set Before":"20px","Sample":"20em"}' as "SMARTWIDTH",
        '{"FORMNUMBER":"20px","Set Before":"80px","Sample":"16em"}' as "SMARTWIDTH",
       '{"request":"@' || t1.ACTION_ID || '@",'||
        '"sample":{"icon":"fa fa-plus-square ignor_data_change","funcName":"createNewSample","tooltip":"Create Sample","cellType":"list","width":"100%","params":["'||t1.ACTION_ID||'", "'||t1.FORMCODE||'"]},'||
        '"documents":{"icon":"fa fa-plus-square","funcName":"attachNewFile","tooltip":"Attach file","cellType":"link","width":"100%","params":["'||t1.ACTION_ID||'","'||t1.FORMCODE||'"]},'||
        decode(fg_selftest_allow_insert_doc(selftestidholder),0,'','"self-test docs":{"icon":"fa fa-plus-square","funcName":"attachNewFile","tooltip":"Attach file","cellType":"link","width":"100%","params":["'||nvl(t1.selftestidholder,'-1')||'","SelfTest","chromatograms"]},') ||
        fg_get_selftest_action_icons(t1.action_id,
                                     t1.formcode,
                                     t1.SELFTESTDEFAULTDATAHOLDER) || -- if displayName":[] => no sample
        ',"rowValidation":{"mandatoryForRow":[{"columnId":"ACTIONNAME", "colDisplayName":"Action"}]}'||
        '}' as "SMARTACTIONS",
       t1.action_id as "_SMARTSELECTALLNONE",
       t1."{FORMNUMBER}_SMARTEDIT",t1."Action_SMARTEDIT",t1."Set Before_SMARTEDIT",t1."Instruction_SMARTEDIT",
       t1."Observation_SMARTEDIT",t1."MPE_SMARTMONPARAM",t1."Start Date_SMARTDATE",
       t1."Start Time_SMARTTIME",t1."Finish Date_SMARTDATE",
       t1."Finish Time_SMARTTIME",--t1."Sample_SMARTEDIT",
       '@'||t1.action_id||'@' as "Sample_SMARTSAMPLELIST",
       t1."Request_SMARTLINK",t1."Documents_SMARTFILE"
       --t1."Self-Test Docs_SMARTFILE",
       --t1."Self-Test_SMARTLINK"
       ,t1."RESULT_SMARTPIVOTSQL"
from (
select  ------- HIDDEN FIELDS ---------
        "ACTION_ID",t."FORM_TEMP_ID",t."TIMESTAMP",t."ACTIVE",STEP_ID, t.FORMNUMBERID, "MPFORMID"
        ------- SMARTACTIONS ----------
        /*,'{"request":"@' || t.ACTION_ID || '@",'||
          '"sample":{"icon":"fa fa-plus-square ignor_data_change","funcName":"createNewSample","tooltip":"Create Sample","cellType":"list","width":"100%","params":["'||t.ACTION_ID||'", "'||t.FORMCODE||'"]},'||
          '"documents":{"icon":"fa fa-plus-square","funcName":"attachNewFile","tooltip":"Attach file","cellType":"link","width":"100%","params":["'||t.ACTION_ID||'","'||t.FORMCODE||'"]},'||
          decode(nvl(selftestidholder,'-1'),'-1','','"self-test docs":{"icon":"fa fa-plus-square","funcName":"attachNewFile","tooltip":"Attach file","cellType":"link","width":"100%","params":["'||nvl(selftestidholder,'-1')||'","SelfTest","chromatograms"]},') ||
          fg_get_selftest_action_icons(t.action_id, t.formcode, t.SELFTESTDEFAULTDATAHOLDER) ||
          ',"rowValidation":{"mandatoryForRow":[{"columnId":"ACTIONNAME", "colDisplayName":"Action"}]}'||
          '}' as "SMARTACTIONS"*/
       ,t.SELFTESTDEFAULTDATAHOLDER, t.formcode, t.selftestidholder
       ----------COLUMNS----------
       ,'{"displayName":"'||nvl(t.FORMNUMBERID,'')|| '","htmlType":"button","icon":"fa fa-trash ignor_data_change","tooltip":"Remove action","onclick":"deleteAction", "formCode":"'||t.FORMCODE||'","formNumberID":"'||t.FORMNUMBERID||'","autoSave":"true","dbColumnName":"FORMNUMBERID"}' as "{FORMNUMBER}_SMARTEDIT"
       ,'{"displayName":"'||nvl("ACTIONNAME",'')|| '","saveType":"text","htmlType":"editableDiv","mandatory":"true","autoSave":"true","dbColumnName":"ACTIONNAME","formCode":"'||t.FORMCODE||'","formNumberID":"'||t.FORMNUMBERID||'"}' as "Action_SMARTEDIT"
       ,'{"displayName":"","htmlType":"select","excludeID":"'||t.FORMNUMBERID||'","renderTableAfterSave":"true","autoSave":"true","dbColumnName":"SetBefore","placeHolder":"Set Before","formCode":"'||t.FORMCODE||'","formNumberID":"'||t.FORMNUMBERID||'","fullList":['||
           listagg('{"ID":"'||t.FORMNUMBERID||'","VAL":"'||t.FORMNUMBERID||'"}',',')within group(order by t.FORMNUMBERID)OVER (partition by t.STEP_ID) ||']}' as "Set Before_SMARTEDIT"
       ,'{"displayName":"'||nvl("INSTRUCTION",'')|| '","saveType":"text","htmlType":"editableDiv","autoSave":"true","dbColumnName":"INSTRUCTION","formCode":"'||t.FORMCODE||'","formNumberID":"'||t.FORMNUMBERID||'"}' as "Instruction_SMARTEDIT"
       ,'{"displayName":"'||regexp_replace(fg_get_richtext_display_clob(t.OBSERVATION),'"','\"')|| '","tooltip":"'||fg_get_richtext_display(OBSERVATION)||'","htmlType":"'||decode(nvl(t.STEPSTATUSNAME,''),'Active','richtext','')||'","autoresize":"true","autoSave":"true","dbColumnName":"OBSERVATION","formCode":"'||t.FORMCODE||'","formNumberID":"'||t.FORMNUMBERID||'"}' as "Observation_SMARTEDIT"
       ,nvl(t.parammonitoringobj,'{}') as "MPE_SMARTMONPARAM"
       ,'{"displayName":"'||nvl(t.STARTDATE,'')|| '","htmlType":"date","dbColumnName":"STARTDATE","autoSave":"true","formCode":"'||t.FORMCODE||'","formNumberID":"'||t.FORMNUMBERID||'"}' as "Start Date_SMARTDATE"
       ,'{"displayName":"'||nvl(t.STARTTIME,'')|| '","htmlType":"time","dbColumnName":"STARTTIME","autoSave":"true","formCode":"'||t.FORMCODE||'","formNumberID":"'||t.FORMNUMBERID||'"}' as "Start Time_SMARTTIME"
       ,'{"displayName":"'||nvl(t.ENDDATE,'')|| '","htmlType":"date","dbColumnName":"ENDDATE","autoSave":"true","formCode":"'||t.FORMCODE||'","formNumberID":"'||t.FORMNUMBERID||'"}' as "Finish Date_SMARTDATE"
        ,'{"displayName":"'||nvl(t.ENDTIME,'')|| '","htmlType":"time","dbColumnName":"ENDTIME","autoSave":"true","formCode":"'||t.FORMCODE||'","formNumberID":"'||t.FORMNUMBERID||'"}' as "Finish Time_SMARTTIME"
      ,fg_get_sample_list(t.ACTION_ID) as "Sample_SMARTEDIT"
     /*,(select distinct '[' || listagg( '{"displayName":"' || r.RequestName || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Request' || '"  ,"formId":"' ||  r.request_id || '","tab":"' || '' || '" }' , ', ') within group (order by  r.RequestName) OVER (partition by r.PARENTID) || ']' --as "Request_SMARTLINK"
         from fg_s_request_v r
         where t.ACTION_ID = r.PARENTID and r.ISINUSE='1')as  "Request_SMARTLINK"*/
         ,fg_get_request_list(t.action_id) as  "Request_SMARTLINK" -- fix bug 9105
      ,FG_GET_SMART_LINK_OBJECT(t.DOCUMENTS ,'Action' ,t.FORMID ,'Documents','file',1 )AS "Documents_SMARTFILE" --NOTE! IT WILL BE EDITABLE ONLY IF THE NAME OF THE COLUMN IS DEFINED IN SMARTACTIONS
      --,FG_GET_SMART_LINK_OBJECT(t.DOCUMENTS ,'SelfTest', nvl(selftestidholder,'-1') ,'Documents','file',1 )AS "Self-Test Docs_SMARTFILE" --TODO add to action last selftest created (and put the ID instead of -1)
      --,'' as "Self-Test_SMARTLINK"
      ------- PIVIVOT DATA ---------
      --,'select result_SMARTPIVOT from fg_p_action_res_opr_v where step_id = ' || t.STEP_ID as result_SMARTPIVOTSQL
      --,'select result_SMARTPIVOT from fg_p_action_res_opr_v1 where step_id = ' || t.STEP_ID as result_SMARTPIVOTSQL
      --,'select result_SMARTPIVOT from fg_p_action_res_opr_v2 where step_id = ' || t.STEP_ID as result_SMARTPIVOTSQL
      --,'select result_SMARTPIVOT from fg_p_action_res_opr_v4 where step_id = ' || t.STEP_ID as result_SMARTPIVOTSQL
      ,'select COLUMN_VALUE AS result_SMARTPIVOT from table(fn_p_action_res_opr_v5(' || t.STEP_ID || '))' as result_SMARTPIVOTSQL
      from FILT_IN_DISTINCT t
 ) t1;
