CREATE OR REPLACE VIEW FG_S_COMPOSITION_STEPDTE_V AS
with composition_data as
(
     select DISTINCT t."COMPOSITION_ID",t."FORM_TEMP_ID",t."FORMID",t."PARENTID",t.FORMCODE,t.ROWTYPE,t.invitemmaterial_id
     ,t.BATCH_ID,t.PURITY,t.PLANNEDTOBATCH,t.DELTA,t.ACTIVE
     ,"MATERIALFUNCTION_ID",t.MaterialFunctionName
     --,(SELECT LISTAGG('{"ID":"'||"MATERIALFUNCTION_ID"||'","VAL":"'||"MATERIALFUNCTIONNAME"||'"}',',')WITHIN GROUP(ORDER BY 1) FROM fg_s_materialfunction_v) "MATERIALFUNCTION_LIST"
    -- ,S.COMPOSITIONTYPE COMPOSITIONTYPE
     ,s.EXPERIMENTSTATUSNAME,s.STEPSTATUSNAME
     ,m."CASNUMBER",m.description,m.manufacturername
     ,t.WV_GRL,t.WW_GRK,t.WW_P,t.FILLER,t.sessionid,t.MAINARG
     ,S.FORMCODE as parent_formcode
     ,t.ACTUAL,
     t.alternative
     ,T.PLANNEDTOEXPBATCH
     ,T.MATERIALINFO
     ,t.DOCUMENTS
     ,t.TABLETYPE
     ,t.ISCOPIEDFROMPLANNED
      from FG_S_COMPOSITION_ALL_V t,
      fg_s_invitemmaterial_all_v m,
      fg_s_step_all_v S
      where t.INVITEMMATERIAL_ID = m.INVITEMMATERIAL_ID(+)
      and t.PARENTID = S.STEP_ID(+)
      and t.TABLETYPE = 'stepComposition'
)
select c."COMPOSITION_ID",c."FORM_TEMP_ID",c."FORMID",c."PARENTID"
,'{'||
    '"rowValidation":{"mandatoryForRow":[{"columnId":"invitemmaterial_id", "colDisplayName":"Material","nonAffectedColumns":["rowType"]}]}'
    ||',"documents":{"icon":"fa fa-plus-square","funcName":"attachNewFile","tooltip":"Attach file","cellType":"link","width":"100%","params":["'||c.composition_id||'","'||c.FORMCODE||'"]}'
  ||'}' as "SMARTACTIONS"
,c.composition_id as "_SMARTSELECTALLNONE"
,fg_get_compositionRowType_list(c.rowtype,c.tabletype,decode(c.rowtype,'Allowed Additive','true',decode(c.iscopiedfromplanned,1,'true','false')),c.formcode) "Row Type_SMARTEDIT"
/*,'{"displayName":'||'[{"ID":"'||c.ROWTYPE||'","displayName":"'||c.ROWTYPE||'"}'||']'||',"htmlType":"select","dbColumnName":"rowType","autoSave":"true","formCode":"'||c.FORMCODE||'", '||
                        ' "fullList":['||
                        (SELECT LISTAGG('{"ID":"'||"ROWTYPE"||'","VAL":"'||"ROWTYPE"||'","ACTIVE":"1"}',',')WITHIN GROUP(ORDER BY 1) FROM FG_I_MATERIALCOMPOSITIONTYPE_V)
                       || '],"customFuncName":"onChangeRowType", "customFuncParams":["'||'Composition'||'","'||'rowType'||'","'||'calcRowTypeOnChange'||'"]'||'}' "Row Type_SMARTEDIT"*/
,fg_get_material_list(c.invitemmaterial_id,5000, c.ROWTYPE,'false',is_autosave_in => 'true',formcode_in => c.formcode) as "Material_SMARTEDIT"
,'{"displayName":"'||c."CASNUMBER"|| '","dbColumnName":"casNumber"}' as "CAS Number_SMARTEDIT"
,'{"displayName":"'||nvl(c.description,'')|| '","dbColumnName":"description"}' as "Description_SMARTEDIT"
/*,'{"displayName":'||'[{"ID":"'||"MATERIALFUNCTION_ID"||'","displayName":"'||c.MaterialFunctionName||'"}'||'],"htmlType":"select","dbColumnName":"FUNCTION_ID","isDisabled":"true","autoSave":"true","formCode":"'||c.FORMCODE||'", '||
                        ' "fullList":['||MATERIALFUNCTION_LIST ||']}' "Function_SMARTEDIT"*/
, c.MaterialFunctionName as "Function"                
,'{"displayName":"'||C.manufacturername|| '","dbColumnName":"MANUFACTURER_ID"}' "Manufacturer_SMARTEDIT"
,'{"displayName":"'||nvl(c.FILLER,0)|| '","saveType":"text","htmlType":"checkbox","isDisabled":"'||decode(c.EXPERIMENTSTATUSNAME,'Planned','false','Active','false','true')||'","dbColumnName":"filler","colCalcId":"filler","autoSave":"true","formCode":"'||c.FORMCODE||'",'||
    ' "customFuncName":"onChangeFiller", "customFuncParams":["'||'Composition'||'","'||'filler'||'","'||'calcFillerOnChange'||'"]}' as "Filler_SMARTEDIT"
,'{"displayName":"'||fg_get_num_display(C.WV_GRL,4)||'","htmlType":"text"'
    ||',"customFuncName":"onChangeCalcField", "customFuncParams":["'||'Composition'||'","'||'WV_GRL'||'","'||'calcWvOnChange'||'"]'
    ||',"dbColumnName":"WV_GRL","style":"'||decode(c.mainArg,'WV_GRL','font-weight: bold;background: lightgreen;height: 100%','')||'","colCalcId":"WV_GRL","convertDecimalToExponential":"true","minVal":"0","autoSave":"true","formCode":"'||c.FORMCODE||'",'
    ||'"isDisabled":"'||DECODE(C.STEPSTATUSNAME,'Planned',decode(c.filler,'1','true','false'),'Active',decode(c.filler,'1','true','false'),'true')||'"}'"w/v [gr/L]_SMARTNUM"
,'{"displayName":"'||fg_get_num_display(c.WW_GRK,4)||'","htmlType":"text"'
    ||',"customFuncName":"onChangeCalcField", "customFuncParams":["'||'Composition'||'","'||'WW_GRK'||'","'||'calcWwGrkOnChange'||'"]'
    ||',"dbColumnName":"WW_GRK","style":"'||decode(c.mainArg,'WW_GRK','font-weight: bold;background: lightgreen;height: 100%','')||'","colCalcId":"WW_GRK","convertDecimalToExponential":"true","maxVal":"1000","minVal":"0","autoSave":"true","formCode":"'||c.FORMCODE||'",'
    ||'"isDisabled":"'||decode(c.STEPSTATUSNAME,'Planned',decode(c.filler,'1','true','false'),'Active',decode(c.filler,'1','true','false'),'true')||'"}' "w/w [gr/Kg]_SMARTNUM"
,'{"displayName":"'||fg_get_num_display(c.WW_P,4)||'","htmlType":"text"'
    ||',"customFuncName":"onChangeCalcField", "customFuncParams":["'||'Composition'||'","'||'WW_P'||'","'||'calcWwPOnChange'||'"]'
    ||',"dbColumnName":"WW_P","style":"'||decode(c.mainArg,'WW_P','font-weight: bold;background: lightgreen;height: 100%','')||'","colCalcId":"WW_P","convertDecimalToExponential":"true","maxVal":"100","minVal":"0","autoSave":"true","formCode":"'||c.FORMCODE||'",'
    ||'"isDisabled":"'||decode(c.STEPSTATUSNAME,'Planned',decode(c.filler,'1','true','false'),'Active',decode(c.filler,'1','true','false'),'true')||'"}'"w/w% _SMARTNUM"
--,'{"displayName":"'||nvl(c.PLANNEDTOEXPBATCH,'')|| '","dbColumnName":"plannedToExpBatch","colCalcId":"plannedToExpBatch"}' as "Planned for X fbatch_SMARTEDIT"
,'{"displayName":"'||nvl(c.PLANNEDTOBATCH,'')|| '","dbColumnName":"plannedToBatch","colCalcId":"plannedToBatch"}' as "Planned for X Sbatch_SMARTEDIT"
,'{"displayName":"'||nvl(c.plannedToExpBatch,'')|| '","dbColumnName":"plannedToExpBatch","colCalcId":"plannedToExpBatch"}' as "Planned for X Fbatch_SMARTEDIT"
,'{"displayName":"'||nvl(c.actual,'')|| '","htmlType":"text","dbColumnName":"actual","autoSave":"true","customFuncName":"onChangeCalcField", "customFuncParams":["'||'Composition'||'","'||'actual'||'","'||'calcActualOnChange'||'"]'
    ||',"isDisabled":"'||decode(c.EXPERIMENTSTATUSNAME,'Planned','true','false')||'","colCalcId":"actual"}' as "Actual Weight_SMARTNUM"
,'{"displayName":"'||nvl(c.materialInfo,'')|| '","saveType":"text","htmlType":"editableDiv","dataMaxLength":"100","autoSave":"true","dbColumnName":"MATERIALINFO","formCode":"'||C.FORMCODE||'"}' as "Material Info_SMARTEDIT"
,FG_GET_SMART_LINK_OBJECT(c.DOCUMENTS ,c.formcode ,c.FORMID ,'','file',1 )AS "Documents_SMARTFILE" --NOTE! IT WILL BE EDITABLE ONLY IF THE NAME OF THE COLUMN IS DEFINED IN SMARTACTIONS
from composition_data c
order by formid;
