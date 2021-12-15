create or replace view fg_authen_experiment_v as
select t."EXPERIMENT_ID",t."FORM_TEMP_ID",t."EXPERIMENT_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."PRODUCTMW",t."CREATIONDATETIME",t."ESTIMATEDSTARTDATE",t."ACTUALSTARTDATE",t."COMPLETIONDATE",t."APPROVALDATE",t."LASTMODIFDATE",t."CREATOR_ID",t."OWNER_ID",t."APPROVER_ID",t."DOCUMENTS",t."CONCLUSSION",t."EXPERIMENTNAME",t."EXPERIMENTVERSION",t."STATUS_ID",t."FORMNUMBERID",t."PROJECT_ID",t."SUBPROJECT_ID",t."SUBSUBPROJECT_ID",t."PROTOCOLTYPE_ID",t."EXPERIMENTSERIES",t."LABORATORY_ID",t."EXPERIMENTGROUP",t."SELFTESTS",t."GROUPSCREW",t."YIELD",t."LIMITINGREACTANTMOLES",t."EQUIVALENTPERMOLE",t."TOTALCHEMICALYIELD",t."PRODUCTMWUOM_ID",t."YIELDUOM_ID",t."REACTANTMOLESUOM_ID",t."PERMOLEUOM_ID",t."USERSCREW",t."CHEMICALYIELDUOM_ID",t."INSTRUMENTS",t."LASTSTATUS_ID",t."STEPS",t."SAFETYCOMMENTS",t."MATERIALS",t."EQUIPTPREPARATIONINSTRUCTION",t."MASSBALLANCETYPE_ID",t."PRODUCTNAME_ID",t."ACTION",t."SAMPLES",t."WORKUPS",t."PLANNED_ACTUAL",t.REACTIONSUMASYNCIMGACT,t."REACTIONTABLE",t."REQUEST",t."AIM",t."DESCRIPTION",t."EXPERIMENTMAIN_ID",t."EXPERIMENTSTATUSNAME",t."PARENT_SUB_PROJECT_ID"
,/*FG_ADAMA.IS_INVENTORY_FAMILIAR(EXPERIMENT_ID)*/0 as "Familiarity",t.EXPERIMENTTYPE_ID,t.EXPERIMENTTYPENAME, T.NUMBER_OF_STEPS,t.LASTSTATUSNAME
,t."TEMPLATEFLAG"
,v.EXPERIMENTVIEWNAME
,t. "EXPERIMENTSERIESNAME"
,t."ORIGINFORMULANTPROPREF"
,t.PROTOCOLTYPENAME
,case when /*nvl2(pln.experiment_id,'1','0') = '0' or*/ nvl(t.EXPERIMENTSTATUSNAME,'Planned') = 'Planned' then 'Planned' else 'Actual' end PLANNED_ACTUAL_STATUS_DEFAULT
--LEVEL NAMES
,t.SUBSUBPROJECTNAME,
t.SUBPROJECTNAME,
t.PROJECTNAME,
t.PROJECTNUMBER,
t.SUBPROJECTNUMBER,
t.SubSubProjectNumber,
nvl(t.ISENABLESPREADSHEET,t.SP_ISENABLESPREADSHEET) as ISENABLESPREADSHEET,
t.ISLOCKSPREADSHEET,t.ENABLESPREADSHEET,t.SPREADSHEETTEMPLATE_ID,
--count(gc.GROUP_ID) over (partition by gc.GROUP_ID)  as "HASGROUP",
fg_is_crewgroup_exists(t.EXPERIMENT_ID)as "HASGROUP",
t.ISRUNSTARTED,
t.SEARCHBY,
t.RECIPEFORMULATION_ID,
'{"path":[{"id":"'||t.project_id||'","name":"Project:'||t.PROJECTNAME||'"},{"id":"'||t.subproject_id||'","name":"SubProject:'||t.subPROJECTNAME||'"}'||nvl2(t.SUBSUBPROJECT_ID,
   ',{"id":"'||t.subsubproject_id||'","name":"SubSubProject:'||t.subsubPROJECTNAME||'"}','')||',{"id":"'||t.EXPERIMENT_ID||'","name":"'||t.FORMCODE||':'||t.EXPERIMENTNAME||nvl2(t.TEMPLATEFLAG,'(Template)','')||'"}]}' as formPath,
   t.EXPERIMENTGROUP_ID
from FG_S_EXPERIMENT_ALL_V t
,fg_s_experimentview_all_v v
--,fg_s_groupscrew_all_v gc
where t.EXPERIMENTVIEW_ID = v.EXPERIMENTVIEW_ID(+)
--and gc.PARENTID(+)= t.EXPERIMENT_ID and gc.SESSIONID(+) is null;