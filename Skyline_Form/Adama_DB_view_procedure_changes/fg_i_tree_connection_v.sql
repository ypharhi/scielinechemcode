create or replace view fg_i_tree_connection_v as
select--the fifth element represents the order in which the records should be ordered by 16072020 adib->bugs 8372,8379
       'Root@-1@Expand Project@Project' as Root,
       'Project@' || p.formid || '@' || replace(p.PROJECTNAME,'@',' ') || '@SUBPROJECT@' || lower(replace(p.PROJECTNAME,'@',' ')) as project,
       'SubProject@' || sp.formid || '@' || replace(sp.subProjectName,'@',' ') || '@SE@'|| lower(replace(sp.subProjectName,'@',' ')) as subProject,
       'Experiment@' || e.formid || '@' || replace(trim(e.ExperimentName || ' ' || e.DESCRIPTION),'@',' ') || decode(NVL(upper(pt.PROTOCOLTYPENAME),'NA'),'CONTINUOUS PROCESS','@RUN','@STEP') as SE, --experiment --add child run level if CONTINUOUS PROCESS protocol type
       'Experiment@' || e.formid || '@' || replace(trim(e.ExperimentName || ' ' || e.DESCRIPTION),'@',' ') || '@RUN' as experiment,
        e.SUBSUBPROJECT_ID as subSubProject,
        -- add run level. e.formid || '#' || step.runnumberdisplay make it unique
        -- 1) in the ElementTreeImp.js -> function initTree we use e.formid for the navigation on dclick
        -- 2) in getNavigationProject event we add the run data between experiment and step (for the popup tree in the struct screens)
       'Run@' || e.formid || '#' || step.runnumberdisplay || '@Run ' || step.runnumberdisplay || '@STEP' as run,
       'Step@' || step.formid || '@' || step.STEPNAME || '@ACTION@'||step.formnumberid as step,
       'Action@' || ac.formid || '@' || ac.ACTIONNAME || '@SW' as action,
        sw.formcode||'@' || sw.formid || '@' || sw.TYPENAME || '@NA' as sw,
        sw.formCode||'@' || sw.formid || '@' || sw.TYPENAME || '@NA' as selftest,
        sw.formCode||'@' || sw.formid || '@' || sw.TYPENAME || '@NA' as workup,
        p.PROJECT_ID,sp.subPROJECT_ID,e.SUBSUBPROJECT_ID,e.experiment_id,e.experiment_id as se_id
from fg_s_project_all_v p,
     fg_s_subproject_all_v sp,
     FG_S_EXPERIMENT_V e,fg_s_protocoltype_v pt,
     fg_s_step_all_v step,
     fg_s_action_all_v ac,
     fg_i_stest_workup_v sw
where p.project_id = sp.project_id(+)
and sp.SUBPROJECT_ID = e.SUBPROJECT_ID (+)
and e.SUBSUBPROJECT_ID is null
and e.experiment_id = step.EXPERIMENT_ID (+)
and step.STEP_ID = ac.STEP_ID (+)
and ac.ACTION_ID = sw.ACTION_ID (+)
and e.TEMPLATEFLAG is null
and e.PROTOCOLTYPE_ID = pt.PROTOCOLTYPE_ID(+)
union all
select
       'Root@-1@Expand Project@Project' as Root,
       'Project@' || p.formid || '@' ||  replace(p.PROJECTNAME,'@',' ') || '@SUBPROJECT@' || lower( replace(p.PROJECTNAME,'@',' ')) as project,
       'SubProject@' || sp.formid || '@' ||  replace(sp.subProjectName,'@',' ') || '@SE@' || lower( replace(sp.subProjectName,'@',' ')) as subProject,
       'SubSubProject@' || ssp.formid || '@' ||  replace(ssp.subsubprojectName,'@',' ') || '@EXPERIMENT@'|| lower( replace(ssp.subsubprojectName,'@',' ')) as SE,-- subsubproject,
       'Experiment@' || e.formid || '@' || replace(trim(e.ExperimentName || ' ' || e.DESCRIPTION),'@',' ') || decode(NVL(upper(pt.PROTOCOLTYPENAME),'NA'),'CONTINUOUS PROCESS','@RUN','@STEP') as experiment,
       'SubSubProject@' || ssp.formid || '@' || ssp.subsubprojectName || '@EXPERIMENT@'|| lower(ssp.subsubprojectName) as subsubproject,
       'Run@' || e.formid || '#' || step.runnumberdisplay || '@Run ' || step.runnumberdisplay || '@STEP' as run,
       'Step@' || step.formid || '@' || step.STEPNAME || '@ACTION@'||step.formnumberid as step,
       'Action@' || ac.formid || '@' || ac.ACTIONNAME || '@SW' as action,
       sw.formCode||'@' || sw.formid || '@' || sw.TYPENAME || '@NA' as sw,
       sw.formCode||'@' || sw.formid || '@' || sw.TYPENAME || '@NA' as selftest,
       sw.formCode||'@' || sw.formid || '@' || sw.TYPENAME || '@NA' as workup,
       p.PROJECT_ID,sp.subPROJECT_ID,to_char(ssp.SUBSUBPROJECT_ID),e.experiment_id,ssp.SUBSUBPROJECT_ID as se_id
from fg_s_project_all_v p,
     fg_s_subproject_all_v sp,
     fg_s_subsubproject_all_v ssp,
     FG_S_EXPERIMENT_V e,fg_s_protocoltype_v pt,
     fg_s_step_all_v step,
     fg_s_action_all_v ac,
     fg_i_stest_workup_v sw
where p.project_id = sp.project_id(+)
and sp.SUBPROJECT_ID = ssp.SUBPROJECT_ID (+)
and ssp.SUBSUBPROJECT_ID = e.SUBSUBPROJECT_ID (+)
and e.experiment_id = step.EXPERIMENT_ID (+)
and step.STEP_ID = ac.STEP_ID (+)
and ac.ACTION_ID = sw.ACTION_ID (+)
and e.TEMPLATEFLAG is null
and e.PROTOCOLTYPE_ID = pt.PROTOCOLTYPE_ID(+);
