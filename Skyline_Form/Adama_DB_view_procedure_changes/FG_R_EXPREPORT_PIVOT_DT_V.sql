CREATE OR REPLACE VIEW FG_R_EXPREPORT_PIVOT_DT_V AS
select t.experiment_id,--important: every change of the column names here or in the fg_p_experimentanalysis_v should be taken in account in the design development and in the js func' getWidthByNameForExpAnalysisReport
       '{"displayName":"' || t.ExperimentName || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || t.experiment_formcode || '"  ,"formId":"' || t.EXPERIMENT_ID || '","tab":"' || '' || '" }' as "Experiment Number_SMARTLINK",
       t.experimentstatusname as "Experiment Status",
       fg_get_richtext_display(t.AIM) as "Experiment Aim",
       fg_get_richtext_display(t.CONCLUSSION) as "Experiment Conclusions",
       t.description as "Experiment Description",
       t.finalproduct as "Final Product",
       to_number(fg_get_num_display(t.quantity)) as "Quantity",
       nvl2(t.quantity,fg_get_uom_display(t.quantity_uom),'') as "Quantity UOM",
       t.moles as "Moles",
       nvl2(t.moles,fg_get_uom_display(t.moleuom_id),'') as "Moles UOM",
       'SELECT result_SMARTPIVOT FROM FG_P_EXPREPORT_V' AS RESULT_SMARTPIVOTSQL
from fg_i_experimentanalysis_v t;
