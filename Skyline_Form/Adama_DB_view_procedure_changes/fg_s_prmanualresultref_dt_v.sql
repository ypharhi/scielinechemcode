create or replace view fg_s_prmanualresultref_dt_v as
select "PRMANUALRESULTREF_ID","FORM_TEMP_ID","PARENTID"
,'{"displayName":"' || t.SampleName || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Sample' || '"  ,"formId":"' || t.SAMPLE_ID || '","tab":"' || '' || '" }' as "Sample #_SMARTLINK"
,'{"displayName":"' || t.RequestName || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Request' || '"  ,"formId":"' || t.REQUEST_ID || '","tab":"' || '' || '" }' as "Request #_SMARTLINK"
,t.PRGNRESULTTYPENAME as "Result Type"
,'{"displayName":"' || t.InvItemMaterialName || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'InvItemMaterial' || '"  ,"formId":"' || t.MATERIAL_ID || '","tab":"' || '' || '" }' as "Material Name_SMARTLINK"
,fg_get_num_display(t.RESULT,0,3) as "Value_SMARTNUM"
,fg_get_uom_display(t.UOM_ID, 1) as "UOM"
--,nvl2(f.file_id,'{"displayName":"' ||  t.ATTACHMENT || '" ,"icon":"' || 'fa fa-paperclip' || '" ,"fileId":"' ||  t.ATTACHMENT || '","formCode":"' || 'OperationType' || '"  ,"formId":"' || t.FORMID || '","tab":"' || '' || '" }','') as "File_SMARTFILE"
,t.COMMENTS as "Comments"
from FG_S_PRMANUALRESULTREF_ALL_V t, fg_files f
  where t.attachment = f.file_id(+)
  order by "Sample #_SMARTLINK";
