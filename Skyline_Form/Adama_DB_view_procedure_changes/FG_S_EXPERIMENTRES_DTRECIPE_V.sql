CREATE OR REPLACE VIEW FG_S_EXPERIMENTRES_DTRECIPE_V AS
select --Sample #  Sample origin  Sample type  Sample amount  Batch  Destination experiment  Protocol  Experiment type
t.uniquerow,t.SAMPLE_ID,t.BATCH_ID as INVITEMBATCH_ID,
t.EXPERIMENT_ID SAMPLE_EXPERIMENT_ID,
s.runnumberdisplay as RUNNUMBER,
t.SAMPLE_ID as "_SMARTSELECTALLNONE",
t."Sample #_SMARTLINK",
fg_get_RichText_display(t.COMMENTSFORCOA) as "Comments",
'{"displayName":"' || fg_adama.get_sample_path( sampleId_in => t.SAMPLE_ID,
                          delimeter_in => '/')
                           || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || (select s.formcode from fg_sequence s where s.id = t.SAMPLE_ORIGIN)
                           || '"  ,"formId":"' || t.sample_origin || '","tab":"' || '' || '" }' as "Sample Origin_SMARTLINK",
t.SAMPLETYPENAME "Sample Type",
t.AMMOUNT ||nvl2(t.AMMOUNT, ' ' ||t.AMOUNT_UOM,'') as "Sample Amount",
--t."Batch_SMARTLINK",
'{"displayName":"' || t.EXPERIMENTDESTNAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || t.FORMCODE || '"  ,"formId":"' || t.EXPERIMENTDEST_ID || '","tab":"' || '' || '" }' as "Experiment #_SMARTLINK",
t.protocoltypename as "Protocol",
t.EXPERIMENTTYPENAME as "Experiment Type",
'SELECT result_SMARTPIVOT FROM FG_P_EXPERIMENTRESULTS_V where batch_ID='||t.batch_id AS RESULT_SMARTPIVOTSQL
from fg_r_experimentresult_noreq_v t,
fg_s_step_all_v s
where t.EXPERIMENTSTATUSNAME = 'Approved'
and t.SAMPLE_STEP_ID = s.step_id(+)
order by "Sample #_SMARTLINK";
