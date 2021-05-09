CREATE OR REPLACE VIEW FG_S_SAMPLE_DTMAIN_V AS
select t.SAMPLE_ID,t.TIMESTAMP,TO_DATE(EXPIRATIONDATE,'dd/mm/yyyy') as EXPIRATIONDATE ,t.CREATION_DATE,
t.SAMPLE_ID as "_SMARTSELECTALLNONE",--"_SMARTSELECT",
'{"displayName":"' || t.SAMPLENAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Sample' || '"  ,"formId":"' || t.SAMPLE_ID || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Sample ID" }' as "Sample ID_SMARTLINK",
'{"displayName":"' || t.sampleDesc || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Sample' || '"  ,"formId":"' || t.SAMPLE_ID || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Sample Description" }' as "Sample Description_SMARTLINK",
--t.sampleDesc as "Sample Name",
t.SAMPLESTATUSNAME as "Status",
'{"displayName":"' || t.BATCHNAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'InvItemBatch' || '"  ,"formId":"' || t.BATCH_ID || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Batch #" }' as "Batch #_SMARTLINK",
'{"displayName":"' || p.projectName || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Project' || '"  ,"formId":"' || t.project_id || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Project" }' as "Project_SMARTLINK",
'{"displayName":"' || sp.subProjectName || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'SubProject' || '"  ,"formId":"' || t.SUBPROJECT_ID || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Sub-Project" }' as "Sub-Project_SMARTLINK",
'{"displayName":"' || ssp.subSubProjectName || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'SubSubProject' || '"  ,"formId":"' || t.subsubproject_id || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Sub-Sub-Project" }' as "Sub-Sub-Project_SMARTLINK",
'{"displayName":"' || t.ExperimentName || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || (select sq.formcode from fg_sequence sq where sq.id=t.EXPERIMENT_ID) || '"  ,"formId":"' || t.experiment_id || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Experiment" }' as "Experiment_SMARTLINK",
'{"displayName":"' || s.STEPNAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || (select sq.formcode from fg_sequence sq where sq.id=t.STEP_ID) || '"  ,"formId":"' || t.STEP_ID || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Step" }' as "Step_SMARTLINK",
'{"displayName":"' || ac.ACTIONNAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Action' || '"  ,"formId":"' || t.ACTION_ID || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Action" }' as "Action_SMARTLINK",
'{"displayName":"' || st.SELFTESTTYPENAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'SelfTest' || '"  ,"formId":"' || t.SELFTEST_ID || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Self-Test" }' as "Self-Test_SMARTLINK",
'{"displayName":"' || wu.WORKUPTYPENAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || (select sq.formcode from fg_sequence sq where sq.id=t.WORKUP_ID) || '"  ,"formId":"' || t.WORKUP_ID || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Workup" }' as "Workup_SMARTLINK",
t.SAMPLETYPENAME as "Sample Type",
--u.UNITSNAME as "Creator",
u.USERNAME as "Creator",
t.CREATION_DATE as "Creation Date_SMARTTIME",
t.PRODUCTNAME as "Material",
--fg_get_RichText_display(t.COMMENTSFORCOA) as "Comments", -->
trim(
                replace(
                        regexp_replace(
                                       regexp_replace(
                                                       regexp_replace(
                                                                      regexp_replace(
                                                                                      DBMS_LOB.substr(f.file_content, 4000),
                                                                                     '<.*?>','')
                                                                      ,'\&lt;.*?\&gt;',' ')
                                                       ,'[[:space:]]',' ')
                                       ,' {2,}', ' ')
                        ,'&nbsp;','')
                ) as "Comments"
--f.file_content_text as "Comments"
from fg_s_sample_all_v t,
     fg_s_project_all_v p,
     fg_s_subproject_all_v sp,
     fg_s_subsubproject_all_v ssp,
     fg_s_step_all_v s,
     fg_s_action_all_v ac,
     fg_s_selftest_all_v st,
     fg_s_workuptype_all_v wu,
     fg_s_user_all_v u,
     fg_richtext f
where t.PROJECT_ID = p.PROJECT_ID(+)
and   t.SUBPROJECT_ID = sp.SUBPROJECT_ID(+)
and   t.SUBSUBPROJECT_ID = ssp.SUBSUBPROJECT_ID(+)
and   t.STEP_ID = s.STEP_ID(+)
and   t.SELFTEST_ID = st.SELFTEST_ID(+)
and   t.ACTION_ID = ac.ACTION_ID(+)
and   t.WORKUPTYPE_ID = wu.WORKUPTYPE_ID(+)
and   t.CREATOR_ID = u.FORMID(+)
and   t.COMMENTSFORCOA = f.file_id(+)
order by t.CREATION_DATE desc;
