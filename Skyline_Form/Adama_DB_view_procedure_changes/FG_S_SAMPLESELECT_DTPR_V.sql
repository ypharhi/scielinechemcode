CREATE OR REPLACE VIEW FG_S_SAMPLESELECT_DTPR_V AS
select "SAMPLESELECT_ID"
,b.SAMPLE_ID as SAMPLE_ID_
,t.PARENTID,FORM_TEMP_ID
,b.SAMPLE_ID as "_SMARTSELECTALLNONE"--"_SMARTSELECT"
,b."SampleNumber_SMARTLINK" as "Sample Number_SMARTLINK",
'{"displayName":"' || b.SAMPLEDESC || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Sample' || '"  ,"formId":"' || b.SAMPLE_ID || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Sample Description" }' as "Sample Description_SMARTLINK"
,b.SAMPLESTATUSNAME as "Status"
,b."BatchNumber_SMARTLINK" as "Batch Number_SMARTLINK"
--,to_date(b.CREATIONDATE,'dd/MM/yyyy') as "Creation Date"
,b.creation_date as "Creation Date_SMARTTIME"
,b.sampletypename as "Sample type"
,fg_get_richtext_display(b.COMMENTSFORCOA) as "Comments"
              from FG_S_SAMPLESELECT_ALL_V t,
              fg_s_sample_dtbasic_v b
              where t.SAMPLE_ID = b.SAMPLE_ID(+)
--03012019 fixed order by
              order by b."SampleNumber_SMARTLINK";
