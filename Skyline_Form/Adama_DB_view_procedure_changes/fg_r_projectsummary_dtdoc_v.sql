create or replace view fg_r_projectsummary_dtdoc_v as
select distinct --!!!! changes in this view must be coordinated with the IntegrationDTAdamaImp.onElementDataTableApiChange code
 t."FORMID",
 t."DOCUMENTUPLOAD" as "FILE_ID",
 t.TABLETYPE,
 t.PARENTID,
 t."FORM_TEMP_ID",
 t.ACTIVE,
 NVL("DOCUMENTNAME",FILE_NAME) AS "Title",
 to_char(S.id) as Entity_ID,
 S.SEARCH_MATCH_ID1,
 "LINK_ATTACHMENT" AS "Type",
 t.DESCRIPTION as "Description",
 null as "Sample Number",
 m.formcodeentitylabel || ' ' || m.formcodetyplabel || /*|| decode(t.TABLETYPE,null,'',' (' || t.TABLETYPE || ')')*/
 CASE
   WHEN t.TABLETYPE IS NOT NULL THEN
   ' (' || LOWER(DECODE(t.TABLETYPE, 'expChromatogramsSample', 'Chromatograms Sample',
                    'expDocumentsSample', 'Documents Sample',
                    'safetySheets', 'Safety Sheets',
                    'userManuals','user Manuals',
                    'TemplateDoc', 'Template',
                    t.TABLETYPE)) || ')'
 END
 as "Entity Type",
 --s.formidname as "Entity Name",
 null as "Entity Name_SMARTLINK", --- > instead of
 --'{"displayName":"' || s.formidname || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || s.formcode || '"  ,"formId":"' || s.id || '","tab":"' || decode(t.TABLETYPE,'ip','IP',upper(substr(t.TABLETYPE,1,1))||substr(t.TABLETYPE,2)) || '","smartType":"SMARTLINK"}' as "Entity Name_SMARTLINK",
 s.formpath as "Entity Path_SMARTPATH",
 decode(lower(s.formcode || '_' || t.TABLETYPE)
        --project
        ,'project_ip',2
        ,'project_reports',2
        ,'project_registration',3
        ,'project_marketing',3
        --sub project
        ,'subproject_ip',2
        ,'subproject_reports',2
        --sub sub project
        ,'subsubproject_ip',2
        ,'subsubproject_reports',2
        ,'subsubproject_suppliers',2
        ,0) as sensitivity_permisiion
--d.FORMDESC as "Entity Description_SMARTHTML"
from FG_S_DOCUMENT_all_V t,
     fg_sequence s,
     FG_FORMELEMENTINFOATMETA_MV m
     --FG_FORM_ID_DESC d
where t.PARENTID = s.id
and m.formcode = s.formcode
and t.SESSIONID is null
and t.ACTIVE = 1
--and to_char(s.id) =  d.formid(+)
order by T.DOCUMENT_ID desc;
