CREATE OR REPLACE VIEW FG_S_SAMPLESELECT_DTEXP_V AS
select distinct "SAMPLESELECT_ID",
b.SAMPLE_ID as SAMPLE_ID_,
t.PARENTID,t.FORM_TEMP_ID,s.runnumberdisplay RUNNUMBER,s.EXPERIMENT_ID
,b.SAMPLE_ID as "_SMARTSELECTALLNONE" --"_SMARTSELECT"
,b."SampleNumber_SMARTLINK" as "Sample Number_SMARTLINK"
,'{"displayName":"' || b.SAMPLEDESC || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Sample' || '"  ,"formId":"' || b.SAMPLE_ID || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Sample Description" }' as "Sample Description_SMARTLINK"
,b.SAMPLESTATUSNAME as "Status"
,b."BatchNumber_SMARTLINK" as "Batch Number_SMARTLINK"
,fg_adama.get_sample_path( sampleId_in => t.SAMPLE_ID , delimeter_in =>'/') as "Origin_SMARTHTML"
--,'{"displayName":"' || b."SamplePath" || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || b."OriginFormcode" || '"  ,"formId":"' || b."OriginId" || '","tab":"' || '' || '" }' as "Origin _SMARTLINK"
--,to_date(b.CREATIONDATE,'dd/MM/yyyy') as "Creation Date"
,'{"displayName":"","htmlType":"checkCharSample","dbColumnName":"characterizedSample","formCode":"'||s.FORMCODE||'","formId":"'||s.FORMID||'","group":"true","disabled":"'||decode(e.EXPERIMENTSTATUSNAME,'Completed','true','Approved','true','false')||'"}' as "Character Sample_SMARTEDIT"
,b.CREATION_DATE as "Creation Date_SMARTTIME"
,b.sampletypename as "Type"
--,s.stepname as "Step"
,s."Step Name{RUN}_SMARTSPLIT"
,fg_get_richtext_display(b.COMMENTSFORCOA) as "Comments"
              from FG_S_SAMPLESELECT_ALL_V t,
              fg_s_sample_dtbasic_v b,
              fg_s_step_all_v s,
              Fg_s_Experiment_All_v e
              where t.SAMPLE_ID = b.SAMPLE_ID(+)
              and b.STEP_ID = s.STEP_ID(+)
              and t.PARENTID = e.EXPERIMENT_ID
--03012019 fixed order by
              order by b."SampleNumber_SMARTLINK";
