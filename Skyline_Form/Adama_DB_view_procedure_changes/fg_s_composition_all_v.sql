create or replace view fg_s_composition_all_v as
select t."COMPOSITION_ID",t."FORM_TEMP_ID",t."COMPOSITION_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."PLANNEDTOEXPBATCH",t."DOCUMENTS",t."DELTA",t."MATERIALINFO",t."COMPOSITIONNAME",t."ACTUALUOM_ID",t."ROWTYPE",t."MANUFACTURER_ID",t."BATCH_ID",t."PARENTID",t."INVITEMMATERIAL_ID",t."MAINARG",t."ORIGIN",t."DIFFERENCE",t."PURITY",t."PLANNEDTOBATCH",t."FUNCTION_ID",t."ORIGINRECIPE_ID",t."DESCRIPTION",t."ACTUAL",t."ALTERNATIVE",t."CASNUMBER",t."WV_GRL",t."ISCOPIEDFROMPLANNED",t."WW_GRK",t."WW_P",t."FILLER",t."ASSAY",t."TABLETYPE"
--t.* end! edit only the code below...
                    /*,nvl(m.MaterialFunctionName,
                         (select distinct first_value(f.MATERIALFUNCTIONName)over(partition by mat.formid)
                         from FG_S_MATERIALFUNCTION_V f
                         where INSTR(','||mat.MATERIALFUNC_ID||',',','||f.MATERIALFUNCTION_ID(+)||',')>0))as MaterialFunctionName*/ -- > correct + performance taken first from csv and connect to thr name ->
                     ,nvl(m.MaterialFunctionName,
                         (select fg_s_materialfunction_v.materialfunctionname from fg_s_materialfunction_v where to_char(materialfunction_id) = REGEXP_SUBSTR(mat.MATERIALFUNC_ID, '[^,]+', 1, 1)) -- return the 1 from csv (the last arg)
                      ) as MaterialFunctionName
                     /*, nvl(m.MaterialFunction_id,
                     (select distinct first_value(f.MATERIALFUNCTION_id)over(partition by mat.formid)
                     from FG_S_MATERIALFUNCTION_V f
                     where INSTR(','||mat.MATERIALFUNC_ID||',',','||f.MATERIALFUNCTION_ID(+)||',')>0))as MaterialFunction_id*/  -- > correct + performance taken first from csv ->
                     ,nvl(m.MaterialFunction_id,
                          to_number(REGEXP_SUBSTR(mat.MATERIALFUNC_ID, '[^,]+', 1, 1)) -- return the 1 from csv (the last arg)
                     ) as MaterialFunction_id
                    ,mat.MANUFACTURER_ID as material_manufacturer_id,mat.CASNUMBER as material_casnumber,mat.COMMENTS as material_description
                    ,mat.INVITEMMATERIALNAME,mat.MANUFACTURERNAME material_manufacturername,b.MANUFACTURER_ID batch_manufacturer_id,b.ManufacturerName batch_manufacturername
                    ,nvl(b.ManufacturerName,mat.MANUFACTURERNAME) as MANUFACTURERNAME
                    ,nvl(t.PURITY,nvl(b.PURITY,100)) as PURITYINFO
                    ,nvl(t.ACTUALUOM_ID,fg_get_uom_by_uomtype('actualWeight')) ACTUALWEIGHTUOM_ID
              from FG_S_COMPOSITION_V t,
              fg_s_materialfunction_v m,
              fg_s_invitemmaterial_all_v mat,
              fg_s_invitembatch_all_v b
              where t.FUNCTION_ID = m.materialfunction_id(+)
              and t.INVITEMMATERIAL_ID = mat.INVITEMMATERIAL_ID(+)
              and t.BATCH_ID = b.invitembatch_id(+);
