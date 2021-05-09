create or replace view fg_s_materialcomponent_dtb_v as
select "MATERIALCOMPONENT_ID",t."FORM_TEMP_ID",t.FORMID,"PARENTID",t.SESSIONID,
decode(t.SESSIONID, null, decode(t.MATERIAL_ID,null,null,dense_rank() over (partition by t.PARENTID order by nvl(t.active,1) desc, t.CLONEID,"MATERIALCOMPONENT_ID", t.SESSIONID nulls first, t.formid))) as "No._SMARTNUM",
--decode(t.MATERIAL_ID,null,null,dense_rank() over (partition by t.PARENTID order by nvl(t.active,1) desc, t.SESSIONID nulls first, t.formid)) as "No._SMARTNUM"
'{"displayName":"'||t.APPROVALDATE|| '","dbColumnName":"approvalDate","colCalcId":"approvalDate","isDisabled":"true"}' as "Approval Date_SMARTEDIT"
,fg_get_material_list(t.MATERIAL_ID,5000,nvl2(t.MATERIAL_ID,nvl2(m.invitemmaterial_id,'Active Ingredient','Recipe'),'Active Ingredient'),'false','true')  as "Material_SMARTEDIT",
'{"displayName":"'||fg_get_num_display(t.CONCENTRATION,0,3)|| '","htmlType":"text","dbColumnName":"concentration","colCalcId":"concentration","renderTableAfterSave":"true","autoSave":"true","minVal":"0"}' as "Concentration %_SMARTNUM"
,t.MaterialFunctionName as "Function"
              from FG_S_MATERIALCOMPONENT_ALL_V t,
              fg_s_invitemmaterial_v m
              where t.MATERIAL_ID=m.formid(+)
              order by t.CLONEID,"MATERIALCOMPONENT_ID";
