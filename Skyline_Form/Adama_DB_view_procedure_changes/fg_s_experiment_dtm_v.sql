create or replace view fg_s_experiment_dtm_v as
with
 stepList as (
    select distinct t1.experiment_id as step_experiment_id,
           REPLACE(
             '[' || listagg( case
                               when step_count_ < 20 then
                                 '{"displayName":"' || decode(is_long_step_name,0,t1.STEPNAME,substr(t1.STEPNAME,1,30) || '...') || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Step' || '"  ,"formId":"' ||  t1.STEP_ID || '","tab":"' || '' || '", "smartType":"SMARTLINK", "title":"Steps"}'
                               else '' end ,
                            ', ')
             within group (order by t1.StepName, t1.step_id) OVER (partition by t1.EXPERIMENT_ID) || ']'
             ,'[]','There are more than 20 steps (' || step_count_ || ' in this experimetn). Use the step level in the table below for having the steps details')
           as steps_
    from (
      select distinct e.experiment_id, s.STEPNAME, s.STEP_ID, case when length(s.STEPNAME) > 30 then 1 else 0 end is_long_step_name,
              count(s.STEP_ID) OVER (partition by e.EXPERIMENT_ID) step_count_
      from fg_s_step_v s,
           fg_s_experiment_v e,
           fg_s_protocoltype_v p
      where s.experiment_id <> -1
      and s.EXPERIMENT_ID = e.experiment_id
      and e.PROTOCOLTYPE_ID = p.PROTOCOLTYPE_ID
      and p.ProtocolTypeName in ('Organic','Formulation')
    ) t1
    where 1=1 --t1.step_count_ < X -- we wont limit in order know on overflow (not likely to have so many steps in orgaminc and formulation)
),
 lastbatch as (
    select distinct b.FORMNUMBERID as batchname, b.invitembatch_id as batchid, b.EXPERIMENT_ID
    from FG_S_invitembatch_v b
    where exists (
                    select last_batch_id
                    from (
                            select max(bl.invitembatch_id) over (partition by bl.experiment_id) as last_batch_id
                            from FG_S_invitembatch_v bl
                            where  bl.experiment_id is not null
                            and    bl.active = 1 ) bt
                    where bt.last_batch_id = b.invitembatch_id
    )
)
select t."EXPERIMENT_ID",t.formid,
--t.CREATED_BY as PERM_CREATED_BY,
'{"displayName":"'||t.formid||'","saveType":"text","htmlType":"checkbox_star","dbColumnName":"favorite","customFuncName":"onChangefavoritestar","smartType":"SMARTEDIT", "title":"Favorite"}' as "Favorite_SMARTEDIT",
t.formnumberid as "Experiment Number",
steps_ as "Steps_SMARTLINK",
t.EXPERIMENTSTATUSNAME as "Status",
u.USERNAME as "Owner",
t.DESCRIPTION as "Description_SMARTELLIPSIS",
t.PROTOCOLTYPENAME as "Protocol Type",
t.EXPERIMENTTYPENAME AS "Experiment Type",
/*sp.PROJECTNAME*/t.PROJECTNAME as "Project",
/*sp.SUBPROJECTNAME*/ t.SUBPROJECTNAME as "Sub Project",
/*ssp.SUBSUBPROJECTNAME*/ t.SUBSUBPROJECTNAME as "Sub Sub Project",
--to_date(to_char(CREATIONDATETIME),'dd/MM/yyyy') as "Creation Date",
--to_date(t.CREATION_DATE) as "Creation Date_SMARTTIME",
t.CREATION_DATE as "Creation Date_SMARTTIME",
/*case
  when t.PROTOCOLTYPENAME = 'Formulation' then
    to_date('15/01/2018 01:32','dd/MM/yyyy HH24:MI')
  when t.PROTOCOLTYPENAME = 'Parametric' then
    to_date('15/02/2018 01:32','dd/MM/yyyy HH24:MI')
  when t.PROTOCOLTYPENAME = 'Analytical' then
    to_date('15/08/2018 20:01','dd/MM/yyyy HH24:MI')
  when t.PROTOCOLTYPENAME = 'Organic' then
    to_date('15/08/2018 11:40','dd/MM/yyyy HH24:MI')
end "Creation Date_SMARTTIME",*/
to_date(t.ESTIMATEDSTARTDATE,'dd/MM/yyyy') as "Estimated Start Date",
to_date(t.actualStartTimeStamp,'dd/MM/yyyy HH24:MI') as "Actual Start Date_SMARTTIME",
--to_date(t.ACTUALSTARTDATE ,'dd/MM/yyyy')as "Actual Start Date",
--to_date(t.COMPLETIONDATE ,'dd/MM/yyyy') as "Completion Date",
to_date(t.completionTimeStamp,'dd/MM/yyyy HH24:MI') as "Completion Date_SMARTTIME",
us.USERNAME as "Approver",
--to_date(t.APPROVALDATE ,'dd/MM/yyyy') as "Approval Date",
to_date(t.approvalTimeStamp,'dd/MM/yyyy HH24:MI') as "Approval Date_SMARTTIME",
eg.ExperimentGroupName as "Experiment Group",
t.EXPERIMENTVERSION as "Experiment Version", -- yp 30122019 add for tests
l.SITENAME as "Site", -- yp 08092020 add site
nvl(u.UnitsName,l.UNITSNAME) as "Unit",
l.LaboratoryName as "Lab",
'{"displayName":"' || lastbatch.batchname || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'InvItemBatch' || '"  ,"formId":"' || lastbatch.batchid || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Batch #" }' as "Last Batch Created_SMARTLINK",
t.EXTERNALCODE as "External Code",
fg_get_richtext_display(t.CONCLUSSION) as "Conclusion",
--t.EXPERIMENTSERIESNAME as "Series Name"
--17012018 ta column change
case
  when t.FORMCODE in ('Experiment', 'ExperimentCP') /*and nvl(status.EXPERIMENTSTATUSNAME,'Planned') <> 'Planned'*/ then -- show also in plan from v>1.503 --YP 12012012 ExperimentCP DEVELOP IF NEEDED
       FG_GET_SMART_LINK_OBJECT('' ,t.FORMCODE ,t."EXPERIMENT_ID" ,'','report')
  else
       ''
  end "Report_SMARTFILE"
      from FG_S_EXPERIMENT_all_V t,fg_s_user_v u, fg_s_user_v us, fg_s_experimentstatus_all_v status--/*,fg_s_project_v p*/,fg_s_subproject_all_v sp,fg_s_subsubproject_v ssp
           ,fg_s_laboratory_all_v l, fg_s_units_v u, stepList,
           fg_s_experimentgroup_v eg, lastbatch
      where nvl(t.CREATIONDATETIME,'na') <> 'Invalid date'
      and t.TEMPLATEFLAG is null
      and t.OWNER_ID = u.USER_ID(+)
      and t.APPROVER_ID = us.USER_ID(+)
      and t.STATUS_ID = status.EXPERIMENTSTATUS_ID
      and t.LABORATORY_ID = l.LABORATORY_ID(+)
      and t.UNITS_ID = u.units_id(+)
      and t.experiment_id = stepList.step_experiment_id(+)
      and t.EXPERIMENTGROUP_ID = to_char(eg.experimentgroup_id(+))
      and t.FORMID = lastbatch.EXPERIMENT_ID(+)
      --and t.PROJECT_ID = p.PROJECT_ID(+)
     -- and t.SUBPROJECT_ID = sp.SUBPROJECT_ID(+)
     -- and t.SUBSUBPROJECT_ID = ssp.SUBSUBPROJECT_ID(+)
     order by t.CREATION_DATE desc;
