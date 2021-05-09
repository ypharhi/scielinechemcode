create or replace view fg_wbx_massbalance_sampls_step as
select '{"id":"'||t.SAMPLE_ID||'" ,"value":"'||t.SAMPLENAME|| decode(p.sampledesc,null,'',': ' || p.sampledesc) ||'","sample_ammount":"'||p.ammount||'","sample_origin":"'||
       ( nvl2( p.action_id,
               fg_funcParseJSONData((select actionname from fg_s_action_pivot where formid = p.action_id)) || --yp07062020 ADD fg_funcParseJSONData
               nvl2( p.selftest_id,
                     '/' ||
                     (select selftesttypename from fg_s_selftesttype_pivot where formid = p.selftesttype_id)
                     ,nvl2( p.workup_id,
                           '/' ||
                           (select workuptypename || '/' || StageStatusName from fg_s_workup_all_v where formid = p.workup_id)
                          ,'')
                    )
              ,p.productname)
     ) ||'"}' as display_data, t.PARENTID
from fg_s_sampleselect_all_v t, fg_s_sample_pivot p
where p.formid = t.SAMPLE_ID
and t.ACTIVE = 1
and t.SESSIONID is null;
