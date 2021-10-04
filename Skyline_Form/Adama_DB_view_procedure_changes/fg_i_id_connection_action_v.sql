create or replace view fg_i_id_connection_action_v as
select distinct
       --id
       constep.project_id,
       constep.subproject_id,
       constep.subsubproject_id,
       constep.experiment_id,
       constep.STEP_ID,
       ac.ACTION_ID,
       --name
       constep.projectName,
       constep.subProjectName,
       constep.subSubProjectName,
       constep.ExperimentName,
       constep.STEPNAME,
       ac.ACTIONNAME,
       ac.ACTIONNAME as actionnamefordesc,
       constep.PROTOCOLTYPENAME
from fg_s_action_all_v ac,
     fg_i_id_connection_step_v constep
where ac.STEP_ID(+) = constep.step_id;
