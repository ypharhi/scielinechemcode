CREATE OR REPLACE VIEW FG_S_COMPOSITION_EXPDTE_V AS
with composition_data as --note!!!  the columns of this table has specific width in bl_getColumnDefaultWidth (js function) if you add or remove columns please make the width adjustments in this function
(
     select DISTINCT t."COMPOSITION_ID",t."FORM_TEMP_ID",t."FORMID",t."PARENTID",t.FORMCODE,t.ROWTYPE,t.invitemmaterial_id
     ,t.BATCH_ID,nvl(t.PURITY,nvl(b.PURITY,100)) as PURITY,t.PLANNEDTOBATCH,t.DELTA,t.ACTIVE
     ,"MATERIALFUNCTION_ID",t.MaterialFunctionName
    -- ,(SELECT LISTAGG('{"ID":"'||"MATERIALFUNCTION_ID"||'","VAL":"'||"MATERIALFUNCTIONNAME"||'"}',',')WITHIN GROUP(ORDER BY 1) FROM fg_s_materialfunction_v) "MATERIALFUNCTION_LIST"
     ,e.COMPOSITIONTYPE COMPOSITIONTYPE
     ,e.EXPERIMENTSTATUSNAME
     ,m."CASNUMBER",m.description,nvl(b.MANUFACTURERNAME,m.manufacturername) as manufacturername
     ,t.WV_GRL,t.WW_GRK,t.WW_P,t.FILLER,t.sessionid,t.MAINARG
     ,e.FORMCODE as parent_formcode
     ,t.ACTUAL,
     t.alternative
     ,t.TABLETYPE
     ,is_numeric(nvl(t.PURITY,100)) is_single_assay
     ,m.COFORMULANT
     ,ACTUALWEIGHTUOM_ID
     --,listagg(r.MATERIALNAME_RESULTREF||':' || nvl2(r.VALUE,r.VALUE||'%','NO ASSAY'),','||chr(10)) within group(order by r.MATERIALNAME_RESULTREF)over (partition by r.PARENTID) assay_results
      from FG_S_COMPOSITION_ALL_V t,
      fg_s_invitemmaterial_all_v m,
      fg_s_experiment_all_v e,
      fg_s_invitembatch_all_v b
      where t.INVITEMMATERIAL_ID = m.INVITEMMATERIAL_ID(+)
      and t.PARENTID = e.experiment_id(+)
      and t.BATCH_ID = b.INVITEMBATCH_ID(+)
      and t.TABLETYPE = 'expComposition'
)
select c."COMPOSITION_ID",c."FORM_TEMP_ID",c."FORMID",c."PARENTID"
,'{'||
    '"rowValidation":{"mandatoryForRow":[{"columnId":"invitemmaterial_id", "colDisplayName":"Material","nonAffectedColumns":["rowType"]}]}'
    ||decode(c.coformulant,'1',',"alternative":{"icon":"fa fa-bars","funcName":"openAlternativeMaterials","tooltip":"Choose an Alternative Material","cellType":"text","width":"100%","params":["'||c.formcode||'","'||c.invitemmaterial_id||'"]}','')
    ||decode(is_single_assay,1,'',',"purity %":{"icon":"fa fa-bars","funcName":"openPurityList","tooltip":"Set Purity Values","cellType":"text","width":"100%","params":["'||c.formcode||'","'||c.batch_id||'"]}')
  ||'}' as "SMARTACTIONS"
  ,C.COMPOSITION_ID AS "_SMARTSELECTALLNONE"
,fg_get_compositionRowType_list(c.rowtype,c.tabletype,decode(c.rowtype,'Allowed Additive','true','false'),c.formcode) "Row Type_SMARTEDIT"
/*'{"displayName":'||'[{"ID":"'||c.ROWTYPE||'","displayName":"'||c.ROWTYPE||'"}'||']'||',"htmlType":"select","dbColumnName":"rowType","autoSave":"true","formCode":"'||c.FORMCODE||'", '||
                        ' "fullList":['||
                        (SELECT LISTAGG('{"ID":"'||"ROWTYPE"||'","VAL":"'||"ROWTYPE"||'","ACTIVE":"1"}',',')WITHIN GROUP(ORDER BY 1) FROM FG_I_MATERIALCOMPOSITIONTYPE_V)
                       || '],"customFuncName":"onChangeRowType", "customFuncParams":["'||'Composition'||'","'||'rowType'||'","'||'calcRowTypeOnChange'||'"]'||'}' "Row Type_SMARTEDIT"*/
,fg_get_material_list(c.invitemmaterial_id,5000, c.ROWTYPE,'false',is_autosave_in => 'true',formcode_in => c.formcode) as "Material_SMARTEDIT"
,'{"displayName":"'||c."CASNUMBER"|| '","dbColumnName":"casNumber"}' as "CAS Number_SMARTEDIT"
,'{"displayName":"'||nvl(c.description,'')|| '","dbColumnName":"description"}' as "Description_SMARTEDIT"
,fg_get_batch_list(selected_material_id_in => c.invitemmaterial_id,selected_batch_id_in => c.batch_id,is_autosave_in => 'true') as "Batch_SMARTEDIT"
,fg_get_function_list(selected_material_id_in => c.invitemmaterial_id,selected_function_id_in => c.MATERIALFUNCTION_ID
                     ,is_autosave_in => 'true',is_disabled_in => decode(c.coformulant,'1','false','true'), formCode_in => c.FORMCODE) as "Function_SMARTEDIT"
/*,'{"displayName":'||'[{"ID":"'||"MATERIALFUNCTION_ID"||'","displayName":"'||c.MaterialFunctionName||'"}'||'],"htmlType":"select","isDisabled":"'||decode(c.rowtype,'Co-Formulants','false','true')||'"'
                        ||',"dbColumnName":"FUNCTION_ID","autoSave":"true","formCode":"'||c.FORMCODE||'", '||
                        ' "fullList":['||MATERIALFUNCTION_LIST ||']}' "Function_SMARTEDIT"*/
,'{"displayName":"'||C.manufacturername|| '","dbColumnName":"MANUFACTURER_ID","colCalcId":"manufacturername"}' "Manufacturer_SMARTEDIT"
,'{"displayName":"'||nvl(c.alternative,'')|| '","dbColumnName":"alternative"}' as "Alternative_SMARTEDIT"
 ,decode(is_single_assay,1,
  '{"displayName":"'||fg_get_num_display(nvl(c.purity,100),0,3)|| '","autoSave":"true","htmlType":"text","dbColumnName":"purity","colCalcId":"purity"'
  ||',"customFuncName":"onChangeCalcField", "customFuncParams":["'||'Composition'||'","'||'purity'||'","'||'calcPurityOnChange'||'"]'
  ||',"isDisabled":"'||decode(c.filler,'1','true','false')||'"}'
  ,fg_get_batch_purity_list(c.PURITY)) as "Purity %_SMARTEDIT"
--,'{"displayName":"'||fg_get_num_display(nvl(c.purity,100),0,3)|| '","htmlType":"text","dbColumnName":"purity","colCalcId":"purity"}' as "Purity_SMARTNUM"
,'{"displayName":"'||nvl(c.FILLER,0)|| '","saveType":"text","htmlType":"checkbox","isDisabled":"'||decode(c.EXPERIMENTSTATUSNAME,'Planned','false','Active','false','true')||'","dbColumnName":"filler","colCalcId":"filler","autoSave":"true","formCode":"'||c.FORMCODE||'",'||
    ' "customFuncName":"onChangeFiller", "customFuncParams":["'||'Composition'||'","'||'filler'||'","'||'calcFillerOnChange'||'"]}' as "Filler_SMARTEDIT"
,'{"displayName":"'||fg_get_num_display(C.WV_GRL,4)||'","htmlType":"text"'
    ||',"customFuncName":"onChangeCalcField", "customFuncParams":["'||'Composition'||'","'||'WV_GRL'||'","'||'calcWvOnChange'||'"]'
    ||',"dbColumnName":"WV_GRL","style":"'||decode(c.mainArg,'WV_GRL','font-weight: bold;background: lightgreen;height: 100%','')||'","colCalcId":"WV_GRL","convertDecimalToExponential":"true","minVal":"0","autoSave":"true","formCode":"'||c.FORMCODE||'",'
    ||'"isDisabled":"'||DECODE(C.EXPERIMENTSTATUSNAME,'Planned',decode(c.filler,'1','true','false'),'Active',decode(c.filler,'1','true','false'),'true')||'"}'"w/v [gr/L]_SMARTNUM"
,'{"displayName":"'||fg_get_num_display(c.WW_GRK,4)||'","htmlType":"text"'
    ||',"customFuncName":"onChangeCalcField", "customFuncParams":["'||'Composition'||'","'||'WW_GRK'||'","'||'calcWwGrkOnChange'||'"]'
    ||',"dbColumnName":"WW_GRK","style":"'||decode(c.mainArg,'WW_GRK','font-weight: bold;background: lightgreen;height: 100%','')||'","colCalcId":"WW_GRK","convertDecimalToExponential":"true","maxVal":"1000","minVal":"0","autoSave":"true","formCode":"'||c.FORMCODE||'",'
    ||'"isDisabled":"'||decode(c.EXPERIMENTSTATUSNAME,'Planned',decode(c.filler,'1','true','false'),'Active',decode(c.filler,'1','true','false'),'true')||'"}' "w/w [gr/Kg]_SMARTNUM"
,'{"displayName":"'||fg_get_num_display(c.WW_P,4)||'","htmlType":"text"'
    ||',"customFuncName":"onChangeCalcField", "customFuncParams":["'||'Composition'||'","'||'WW_P'||'","'||'calcWwPOnChange'||'"]'
    ||',"dbColumnName":"WW_P","style":"'||decode(c.mainArg,'WW_P','font-weight: bold;background: lightgreen;height: 100%','')||'","colCalcId":"WW_P","convertDecimalToExponential":"true","maxVal":"100","minVal":"0","autoSave":"true","formCode":"'||c.FORMCODE||'",'
    ||'"isDisabled":"'||decode(c.EXPERIMENTSTATUSNAME,'Planned',decode(c.filler,'1','true','false'),'Active',decode(c.filler,'1','true','false'),'true')||'"}'"w/w% _SMARTNUM"
 ,'{"displayName":"'||nvl2(c.PLANNEDTOBATCH,fg_get_num_display(c.PLANNEDTOBATCH,0),'')|| '","dbColumnName":"plannedToBatch","colCalcId":"plannedToBatch"}' as "Planned for X batch_SMARTEDIT"
 ,'{"displayName":"'||nvl2(c.delta,fg_get_num_display(c.delta,0),'')|| '","dbColumnName":"delta","colCalcId":"delta","icon":"'||case when c.delta<0 then 'fa fa-warning red' else '' end ||'"}' as "Delta w/v_SMARTICON"
 ,'{"displayName":"'||nvl2(c.actual,fg_get_num_display(c.actual,0),'')|| '","dbColumnName":"actual","colCalcId":"actual"}' as "Actual_SMARTEDIT"
 ,'{"displayName":[{"ID":"'||c.ACTUALWEIGHTUOM_ID||'","displayName":"'||fg_get_Uom_display(c.ACTUALWEIGHTUOM_ID)||'"}],"htmlType":"select","dbColumnName":"ACTUALUOM_ID",'||
                      '"allowSingleDeselect":"false", "autoSave":"true", "fullList":'||fg_get_Uom_key_val_list_byname('actualWeight')||'}' as "Uom_SMARTEDIT"
from composition_data c
order by formid;
