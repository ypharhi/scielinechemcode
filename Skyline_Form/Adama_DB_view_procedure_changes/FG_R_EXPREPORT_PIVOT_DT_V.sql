create or replace view fg_r_expreport_pivot_dt_v as
select distinct t.experiment_id,--important: every change of the column names here or in the fg_p_experimentanalysis_v should be taken in account in the design development and in the js func' getWidthByNameForExpAnalysisReport
       s.SAMPLE_ID as smplid_,  -- do not add sample_id - because we don't want to filter by sample
       t.ExperimentName,
       s.SAMPLE_ID || '_' || t.experiment_id as UNIQUEROW_S_E,
       TO_CHAR(t.experiment_id) AS UNIQUEROW_E,
       '{"displayName":"' || t.ExperimentName || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || t.FORMCODE || '"  ,"formId":"' || t.EXPERIMENT_ID || '","tab":"' || '' || '" }' as "Experiment Number_SMARTLINK",
       t.DESCRIPTION as "Experiment Description"
from fg_s_experiment_v t,  fg_s_sample_all_v s
where  t.experiment_id = s.experiment_id(+)
order by t.ExperimentName,  s.SAMPLE_ID;
