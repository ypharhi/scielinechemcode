create or replace view fg_authen_stepfr_v as
select distinct
case when nvl2(pln.step_id,'1','0') = '0' or nvl(t.STEPSTATUSNAME,'Planned') = 'Planned' then 'Planned' else 'Actual' end PLANNED_ACTUAL_STATUS_DEFAULT
,t."STEP_ID",t."FORM_TEMP_ID","STEP_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."STATUS_ID",t.Formnumberid,"STEPNAME",t."CREATIONDATETIME","ESTIMSTARTDATE",t."ACTUALSTARTDATE","FINISHDATE",t."CREATOR_ID","REACTSTARTTIME","REACTFINISHTIME",t.SHORTDESCRIPTION,"SELFTESTINSTRUCTIONS",t."CONCLUSSION",t."AIM",t."DOCUMENTS"
,texp.EXPERIMENT_ID
,texp.EXPERIMENTNAME
,texp.EXPERIMENTVERSION
,texp.PROJECT_ID
,texp.SUBPROJECT_ID
,texp.SUBSUBPROJECT_ID
,texp.EXPERIMENTSTATUSNAME
,t.STEPSTATUSNAME
,t.TEMPLATEFLAG
,nvl2(pln.step_id,'1','0') as SNAPSHOT_FLAG
,lpad(nvl(max(t.STEPSEQUENCE)over(partition by t.EXPERIMENT_ID)+1,'1'),2,0) as "STEPSEQUENCE",texp.PROTOCOLTYPE_ID,
texp.FORMNUMBERID as EXPERIMENT_FORMNUMBERID,
--LEVEL DATA
texp.SUBSUBPROJECTNAME,
texp.SUBPROJECTNAME,
texp.PROJECTNAME,
texp.PROJECTNUMBER,
texp.SUBPROJECTNUMBER,
texp.SubSubProjectNumber,
texp.DESCRIPTION as EXP_DESCRIPTION,
nvl(texp.WEBIXFORMULCALC,'0') as WEBIXFORMULCOPY,
ft.COMPOSITIONTYPE as "PROJECT_COMPOSITIONTYPENAME",
texp.PROTOCOLTYPENAME,
FT.FormulationTypeName,
texp.COMPOSITIONTYPENAME,
'{"path":[{"id":"'||texp.project_id||'","name":"Project:'||texp.PROJECTNAME||'"},{"id":"'||texp.subproject_id||'","name":"SubProject:'||texp.subPROJECTNAME||'"}'||nvl2(texp.SUBSUBPROJECT_ID,
   ',{"id":"'||texp.subsubproject_id||'","name":"SubSubProject:'||texp.subsubPROJECTNAME||'"}','')||',{"id":"'||texp.EXPERIMENT_ID||'","name":"'||texp.FORMCODE||':'||texp.EXPERIMENTNAME||nvl2(t.TEMPLATEFLAG,'(Template)','')||'"},{"id":"'||t.STEP_ID||'","name":"'||t.FORMCODE||':'||t.STEPNAME||'"}]}' as formPath
     --nvl(pln.chemdoodlepln,'-1') as "CHEMDOODLEPLN"
      from FG_S_STEP_ALL_V t,fg_s_experiment_all_v texp, FG_S_FORMULANTREF_ALL_V_PLAN pln,FG_S_FORMULATIONTYPE_V FT
      where t.EXPERIMENT_ID(+) = texp.EXPERIMENT_ID --yp 1704 the (+) on t to support new (?) if yes it should be change part of separate to new / pdate init sqls
      and t.STEP_ID = pln.step_id (+)
      and texp.PROJECT_FORMULATIONTYPE_ID = ft.formulationtype_id(+);
