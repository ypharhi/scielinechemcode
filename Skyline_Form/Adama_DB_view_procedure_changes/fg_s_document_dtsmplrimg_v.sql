create or replace view fg_s_document_dtsmplrimg_v as
select t."FORMID", "DOCUMENTUPLOAD" as FILE_ID, t.TABLETYPE, t.PARENTID,t."FORM_TEMP_ID", t.ACTIVE, NVL("DOCUMENTNAME",FILE_NAME) AS "Title", "LINK_ATTACHMENT" AS "Type",smpl.FORMNUMBERID as "Sample Number" ,t.DESCRIPTION as "Description"
      ,case
        when t.sessionId is null and t.active = 1 and LOWER(t.FILE_CONTENT_TYPE_) LIKE '%image%' then
          '{"displayName":"' || "DOCUMENTUPLOAD" || '","htmlType":"checkbox","isDisabled":"false","dbColumnName":"EXPORTTOREPORT","autoSave":"true"}'
        else
          '{"displayName":"' || "DOCUMENTUPLOAD" || '","htmlType":"checkbox","isDisabled":"true","dbColumnName":"EXPORTTOREPORT","autoSave":"true"}'
      end as "Export to Report_SMARTEDIT"
       ,
          '{"displayName":"' || NVL(T.ADDTOTEMPLATE,'1') || '","htmlType":"checkbox","isDisabled":"FALSE","dbColumnName":"ADDTOTEMPLATE","autoSave":"true"}'
       as "Add to Template_SMARTEDIT"
from FG_S_DOCUMENT_ALL_V t,
      fg_s_sample_all_v smpl
      where t.SAMPLE_ID = smpl.SAMPLE_ID(+);
