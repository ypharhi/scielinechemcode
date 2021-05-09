create or replace view fg_authen_samplemain_v as
select
--level names
b.invitembatch_id
,b.InvItemBatchName
,b.INVITEMMATERIAL_ID
,b.MATERIALNAME
,b.PROJECT_ID
,b.SUBPROJECT_ID
,b.SUBSUBPROJ_ID as SUBSUBPROJECT_ID
/*,AC."ACTIONNAME",
STP."STEPNAME",
EX.EXPERIMENTNAME,
SSP.SUBSUBPROJECTNAME,
SP.SUBPROJECTNAME,
P.PROJECTNAME
from FG_S_SAMPLEMAIN_ALL_V t,
               FG_S_ACTION_V AC,
               FG_S_STEP_V STP,
               FG_S_EXPERIMENT_V EX,
               FG_S_SUBPROJECT_V SP,
               FG_S_SUBSUBPROJECT_V SSP,
               FG_S_PROJECT_V P
where   t.ACTION_ID = AC.action_id(+)
AND   t.STEP_ID = STP.step_id(+)
AND   T.EXPERIMENT_ID = EX.EXPERIMENT_ID(+)
AND   T.SUBSUBPROJECT_ID = SSP.SUBSUBPROJECT_ID(+)
AND   T.SUBPROJECT_ID = SP.SUBPROJECT_ID(+)
AND   T.PROJECT_ID = P.PROJECT_ID(+)
AND   nvl(ac.active,'1')='1';*/
from fg_s_sample_all_v t,
fg_s_invitembatch_all_v b
where b.BATCH_ID = t.BATCH_ID(+);
