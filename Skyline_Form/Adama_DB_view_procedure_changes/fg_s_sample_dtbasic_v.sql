create or replace view fg_s_sample_dtbasic_v as
select t.PARENTID,
       t."FORMID",
       t.SAMPLE_ID,
       t.SAMPLEDESC,
       t.EXPERIMENT_ID,
       t.AMMOUNT,
       t.EXPERIMENTNAME,
       t.STEP_ID,
       t.STATUS_ID,
       t.SAMPLENAME,
       t.BATCH_ID,
       t.BATCHNAME,
       t.COMMENTSFORCOA,
       t.PROJECT_ID,
       t.SUBPROJECT_ID,
       t.ACTION_ID,
       --fg_adama.get_sample_path(t.parentId,'/') as "SamplePath",
       t.CREATIONDATE,
       t.SAMPLETYPENAME,
       t.CREATION_DATE,
       t.SAMPLESTATUSNAME,
       '{"displayName":"' || t.EXPERIMENTNAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || (select sq.formcode from fg_sequence sq where sq.id=t.EXPERIMENT_ID) || '"  ,"formId":"' || t.EXPERIMENT_ID || '","tab":"' || '' || '" }' as "ExperimentNumber_SMARTLINK",
       '{"displayName":"' || t.SAMPLENAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Sample' || '"  ,"formId":"' || t.SAMPLE_ID || '","tab":"' || '' || '" }' as "SampleNumber_SMARTLINK",
       '{"displayName":"' || t.BATCHNAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'InvItemBatch' || '"  ,"formId":"' || t.BATCH_ID || '","tab":"' || '' || '" }' as "BatchNumber_SMARTLINK",
       t.USERNAME as "CREATOR",
       nvl2(t.ORIGINEXPERIMENTID,'Experiment',nvl2(t.ORIGINSTEPID,'Step',nvl2(t.ORIGINACTIONID,'Action',nvl2(t.ORIGINSELFTESTID,'SelfTest','Workup')))) as "OriginFormcode",
       t.parentid as "OriginId",
       t.REFERENCE,
       T.FORMCODE,
       T.PRODUCTNAME AS PRODUCTNAME_MATERIAL -- YP 14102020
 from FG_S_SAMPLE_ALL_V t
--where t.SAMPLESTATUSNAME<>'Canceled'; --ta 310518 task 16010;