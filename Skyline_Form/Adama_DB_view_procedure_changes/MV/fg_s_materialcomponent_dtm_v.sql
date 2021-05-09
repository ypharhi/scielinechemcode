create or replace view fg_s_materialcomponent_dtm_v as
select "MATERIALCOMPONENT_ID","FORM_TEMP_ID",t.FORMID,"PARENTID",
'{"rowValidation":{"mandatoryForRow":[{"columnId":"invitemmaterial_id", "colDisplayName":"Material"}]}}' as "SMARTACTIONS",
decode(t.SESSIONID, null, decode(t.MATERIAL_ID,null,null,dense_rank() over (partition by t.PARENTID order by nvl(t.active,1) desc, t.SESSIONID nulls first, t.formid))) as "No._SMARTNUM",
--decode(t.MATERIAL_ID,null,null,dense_rank() over (partition by t.PARENTID order by nvl(t.active,1) desc, t.SESSIONID nulls first, t.formid)) as "No._SMARTNUM",
fg_get_material_list(t.MATERIAL_ID,5000,'Active Ingredient','false','true')  as "Material_SMARTEDIT",
'{"displayName":"'||fg_get_num_display(t.CONCENTRATION,0,3)|| '","htmlType":"text","dbColumnName":"concentration","autoSave":"true","minVal":"0"}' as "Concentration %_SMARTNUM"
              from FG_S_MATERIALCOMPONENT_ALL_V t,fg_s_invitemmaterial_pivot m where t.MATERIAL_ID=m.formid(+) 
              order by "MATERIALCOMPONENT_ID";
