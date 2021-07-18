CREATE OR REPLACE VIEW FG_R_EXPREPORT_PIVOT_DT_V AS
select distinct t.experiment_id,--important: every change of the column names here or in the fg_p_experimentanalysis_v should be taken in account in the design development and in the js func' getWidthByNameForExpAnalysisReport
       t.SAMPLE_ID,
       t.UNIQUEROW,
       --t.SAMPLE_ID || '_' || t.experiment_id as uniquerow,
       '{"displayName":"' || e.ExperimentName || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || t.FORMCODE || '"  ,"formId":"' || t.EXPERIMENT_ID || '","tab":"' || '' || '" }' as "Experiment Number_SMARTLINK",
       e.DESCRIPTION as "Experiment Description"
        /*t.experimentstatusname as "Experiment Status",
       fg_get_richtext_display(t.AIM) as "Experiment Aim",
       fg_get_richtext_display(t.CONCLUSSION) as "Experiment Conclusions",
       t.description as "Experiment Description",
       t.finalproduct as "Final Product",
       to_number(fg_get_num_display(t.quantity)) as "Quantity",
       nvl2(t.quantity,fg_get_uom_display(t.quantity_uom),'') as "Quantity UOM",
       t.moles as "Moles",
       nvl2(t.moles,fg_get_uom_display(t.moleuom_id),'') as "Moles UOM",*/
       --,'SELECT result_SMARTPIVOT FROM FG_P_EXPREPORT_V' AS RESULT_SMARTPIVOTSQL
from /*fg_s_experiment_v t,
     FG_I_RESULT_ALL_V r*/
    fg_r_experimentresult_noreq_v t, fg_s_experiment_v e  
where t.experiment_id = e.experiment_id 
order by t.experiment_id, t.SAMPLE_ID
