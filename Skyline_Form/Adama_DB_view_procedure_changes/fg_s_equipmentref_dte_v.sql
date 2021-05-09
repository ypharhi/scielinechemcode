create or replace view fg_s_equipmentref_dte_v as
select "FORMID","EQUIPMENTREF_OBJIDVAL",t.FORM_TEMP_ID,"TIMESTAMP","PARENTID",
'{'||
    '"rowValidation":{"mandatoryForRow":[{"columnId":"equipmentRefName", "colDisplayName":"Equipment Name"}]}'
  ||'}' as "SMARTACTIONS"
,decode(t.SESSIONID, null, decode(t.FORMID,null,null,dense_rank() over (partition by t.PARENTID order by nvl(t.active,1) desc, t.SESSIONID nulls first, t.formid))) as "No._SMARTNUM",
'{"displayName":"'||EQUIPMENTREFNAME|| '","saveType":"text","htmlType":"editableDiv","dataMaxLength":"500","dbColumnName":"equipmentRefName","mandatoty":"true", "autoSave":"true"}' as "Equipment Name_SMARTEDIT"
--,"EQUIPMENTREFNAME" as "Equipment Name"
--,fg_get_RichText_display(t.DESCRIPTION) as "Equipment Description"
,'{"displayName":"'||fg_get_RichText_display(t.DESCRIPTION)|| '","htmlType":"textarea","dbColumnName":"description", "autoSave":"true"}' as "Equip. Description_SMARTEDIT"
from FG_S_EQUIPMENTREF_ALL_V t
order by EQUIPMENTREFNAME;
