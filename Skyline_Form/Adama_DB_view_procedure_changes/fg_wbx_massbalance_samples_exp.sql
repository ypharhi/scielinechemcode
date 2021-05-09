create or replace view fg_wbx_massbalance_samples_exp as
select distinct '{"id":"'||t1.SAMPLE_ID||'" ,"value":"'||t1.SAMPLENAME||decode(p.sampledesc,null,'',': ' || p.sampledesc)||'","sample_ammount":"'||p.ammount||'","sample_origin":"'||
       (nvl2( p.action_id,
               fg_funcParseJSONData((select actionname from fg_s_action_pivot where formid = p.action_id)) || --yp10062020 ADD fg_funcParseJSONData
               nvl2( p.selftest_id,
                     '/' ||
                     (select selftesttypename from fg_s_selftesttype_pivot where formid = p.selftesttype_id)
                     ,nvl2( p.workup_id,
                           '/' ||
                           (select workuptypename || '/' || StageStatusName from fg_s_workup_all_v where formid = p.workup_id)
                          ,'')
                    )
              ,p.productname)
     ) ||'"}' as display_data
       , t1.EXPERIMENT_ID as PARENTID
       ,p.experiment_id,s.RUNNUMBERDISPLAY as RUNNUMBER
from fg_s_sample_pivot p,
     ( select t.sample_id,t.SAMPLENAME,TO_CHAR(e.experiment_id) EXPERIMENT_ID
       from fg_s_sampleselect_all_v t
       ,fg_s_experiment_v e
       where t.PARENTID = e.experiment_id
       and t.ACTIVE = 1
       and t.SESSIONID is null
       union all
       select t.sample_id,t.SAMPLENAME,s.experiment_id
       from fg_s_sampleselect_all_v t
       ,fg_s_step_v s
       where t.PARENTID = s.step_id
       and t.ACTIVE = 1
       and t.SESSIONID is null
       union all
       select t.sample_id,t.SAMPLENAME,a.experiment_id
       from fg_s_sampleselect_all_v t
       ,fg_s_action_v a
       where t.PARENTID = a.action_id
       and t.ACTIVE = 1
       and t.SESSIONID is null
       union all
       select t.sample_id,t.SAMPLENAME,st.experiment_id
       from fg_s_sampleselect_all_v t
       ,fg_s_selftest_v st
       where t.PARENTID = st.selftest_id
       and t.ACTIVE = 1
       and t.SESSIONID is null
       union all
       select t.sample_id,t.SAMPLENAME,w.experiment_id
       from fg_s_sampleselect_all_v t
       ,fg_s_workup_all_v w
       where t.PARENTID = w.WORKUP_ID
       and t.ACTIVE = 1
       and t.SESSIONID is null
     ) t1,
   fg_s_step_all_v s
where p.formid = t1.SAMPLE_ID
and p.step_id = s.step_id(+);
