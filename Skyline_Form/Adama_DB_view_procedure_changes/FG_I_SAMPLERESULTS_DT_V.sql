CREATE OR REPLACE VIEW FG_I_SAMPLERESULTS_DT_V AS
select distinct--Sample #  Sample origin  Sample type  Sample amount  Batch  Destination experiment  Protocol  Experiment type
t.UNIQUEROW,
t.EXPERIMENT_ID SAMPLE_EXPERIMENT_ID,
t.SAMPLE_ID,
t."Sample #_SMARTLINK",
fg_get_RichText_display(t.COMMENTSFORCOA) as "Comments",
t.SAMPLEDESC "Sample Description",
'{"displayName":"' || fg_adama.get_sample_path( sampleId_in => t.SAMPLE_ID,
                          delimeter_in => '/')
                           || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || (select s.formcode from fg_sequence s where s.id = t.SAMPLE_ORIGIN)
                           || '"  ,"formId":"' || t.sample_origin || '","tab":"' || '' || '" }' as "Sample Origin_SMARTLINK",
t.SAMPLETYPENAME "Sample Type",
t.AMMOUNT ||nvl2(t.AMMOUNT, ' ' ||t.AMOUNT_UOM,'') as "Sample Amount",
t."Batch_SMARTLINK",
'{"displayName":"' || t.EXPERIMENTDESTNAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || t.FORMCODE || '"  ,"formId":"' || t.EXPERIMENTDEST_ID || '","tab":"' || '' || '" }' as "Experiment #_SMARTLINK",
t.protocoltypename as "Protocol",
t.EXPERIMENTTYPENAME as "Experiment Type",
fg_get_richtext_display(t.EXPERIMENTDESC) "Experiment Description",
'SELECT result_SMARTPIVOT FROM FG_P_EXPERIMENTRESULTS_V' AS RESULT_SMARTPIVOTSQL
from fg_r_experimentresult_noreq_v t
where t.EXPERIMENTSTATUSNAME = 'Approved';
