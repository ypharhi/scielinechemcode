CREATE OR REPLACE VIEW FG_S_SAMPLESELECT_DTEFOR_V AS
select distinct "SAMPLESELECT_ID",
b.SAMPLE_ID as SAMPLE_ID_,
t.PARENTID,t.FORM_TEMP_ID
,bat.invitembatch_id
,b.SAMPLE_ID as "_SMARTSELECTALLNONE" --"_SMARTSELECT"
,b."SampleNumber_SMARTLINK" as "Sample Number_SMARTLINK"
,'{"displayName":"' || b.SAMPLEDESC || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Sample' || '"  ,"formId":"' || b.SAMPLE_ID || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Sample Description" }' as "Sample Description_SMARTLINK"
--,b.REFERENCE as "Reference"
,'{"displayName":"'||b.REFERENCE|| '","htmlType":"text","mandatory":"false","autoSave":"true","dbColumnName":"REFERENCE","formCode":"Sample","formNumberID":"'||b.SAMPLE_ID||'"}' as "Reference_SMARTEDIT"
,b.SAMPLESTATUSNAME as "Status"
,b."BatchNumber_SMARTLINK" as "Batch Number_SMARTLINK"
--,fg_adama.get_sample_path( sampleId_in => t.SAMPLE_ID , delimeter_in =>'/') as "Origin_SMARTHTML"
,'{"displayName":"' || fg_adama.get_sample_path( sampleId_in => t.SAMPLE_ID , delimeter_in =>'/') || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || b."OriginFormcode" || '"  ,"formId":"' || b."OriginId" || '","tab":"' || '' || '" }' as "Origin _SMARTLINK"--fixed bug 8897
--,to_date(b.CREATIONDATE,'dd/MM/yyyy') as "Creation Date"
,b.CREATION_DATE as "Creation Date_SMARTTIME"
,b.sampletypename as "Type"
,s.stepname as "Step"
,fg_get_richtext_display(b.COMMENTSFORCOA) as "Comments"
              from FG_S_SAMPLESELECT_ALL_V t,
              fg_s_sample_dtbasic_v b,
              fg_s_step_all_v s,fg_s_invitembatch_v bat
              where t.SAMPLE_ID = b.SAMPLE_ID(+)
              and b.STEP_ID = s.STEP_ID(+)
              and b.BATCH_ID = bat.invitembatch_id(+)
              order by b."SampleNumber_SMARTLINK";
