create or replace view fg_s_sample_dt_v as
select t."FORMID",t.SAMPLE_ID,
       '{"displayName":"' || t.SAMPLENAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Sample' || '"  ,"formId":"' || t.SAMPLE_ID || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Sample Number"  }' as "Sample Number_SMARTLINK",
       '{"displayName":"' || t.sampleDesc || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Sample' || '"  ,"formId":"' || t.SAMPLE_ID || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Sample Description" }' as "Sample Description_SMARTLINK",
       t.SampleStatusName as "Status",
       --t.CREATIONDATE as "Creation Date",
       t.creation_date as "Creation Date_SMARTTIME",
     '{"displayName":"' ||  t.EXPERIMENTNAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Experiment' || '"  ,"formId":"' || t.EXPERIMENT_ID || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Experiment Number"  }' as "Experiment Number_SMARTLINK",
       s.STEPNAME as "Step",
       t.CREATOR as "Sample Creator",
      -- t."SamplePath" as "Origin",
       t.SAMPLETYPENAME as "Sample Type",
       fg_get_richtext_display(t.COMMENTSFORCOA) as "Comments"
       ,t.PRODUCTNAME_MATERIAL as "Material/Product"
       ,p.PROJECTNAME as "Project Name"
       ,p.SUBPROJECTNAME as "Subproject Name"
       from FG_S_SAMPLE_DTBASIC_V t,fg_s_step_all_v s,fg_s_subproject_all_v p
       where t.STEP_ID = s.STEP_ID(+)
       and t.SUBPROJECT_ID = P.SUBPROJECT_ID(+)
       order by t.creation_date desc;
