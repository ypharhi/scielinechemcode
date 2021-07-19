CREATE OR REPLACE VIEW FG_R_EXPREPORT_PIVOT_DT_V AS
select distinct e.experiment_id,--important: every change of the column names here or in the fg_p_experimentanalysis_v should be taken in account in the design development and in the js func' getWidthByNameForExpAnalysisReport
       --t.SAMPLE_ID,  -- do not add sample_id - because we don't want to filter by sample
       e.ExperimentName,
       nvl(t.UNIQUEROW,e.experiment_id) as UNIQUEROW,
       '{"displayName":"' || e.ExperimentName || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || E.FORMCODE || '"  ,"formId":"' || E.EXPERIMENT_ID || '","tab":"' || '' || '" }' as "Experiment Number_SMARTLINK",
       e.DESCRIPTION as "Experiment Description"
from  fg_r_experimentresult_noreq_v t, fg_s_experiment_v e
where  e.experiment_id = t.experiment_id(+)
order by e.ExperimentName, UNIQUEROW;
