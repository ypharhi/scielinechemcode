create or replace view fg_authen_invitemmaterial_v as
select t."INVITEMMATERIAL_ID",t."FORM_TEMP_ID",t."INVITEMMATERIAL_OBJIDVAL",t."FORMID",t."TIMESTAMP",
       t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."CHEMICALFORMULA",t."STORAGECONDITION",
       t."COMMENTS",t."CATEGORY_ID",t."VISCOSITY_UOM_ID",t."FORMNUMBERID",t."DENSITY_UOM_ID",t."MSDS",
       t."MATERIALTYPE_ID",t."MW",t."DENSITY",t."IUPACNAME",t."QUANTITY",t."INVITEMMATERIALNAME",
       t."MW_UOM_ID",t."QUANTITY_UOM_ID",t."MINSTOCKLEVEL_UOM_ID",t."CASNUMBER",t."CASNAME",t."VISCOSITY",
       t."MINSTOCKLEVEL",t."TRAINING",
       /* nvl(fg_get_num_display(num_in =>  sum(fg_get_num_normal(pt.QUANTITY,nvl(pt.QUANTITYUOM_ID,fg_get_uom_by_uomtype('weight',(select default_value from fg_i_uom_metadata_v where formcode = 'InvItemBatch' and column_name = 'QUANTITYUOM_ID'))))) OVER (PARTITION BY pt.INVITEMMATERIAL_ID)
                         ,display_type_in => '2'
                         ,uom_id_in => fg_get_uom_by_uomtype('weight')
                         ,convert_uom_id_in => fg_get_uom_by_uomtype('weight',(select default_value from fg_i_uom_metadata_v where formcode = 'InvItemMaterial' and column_name = 'QUANTITY_UOM_ID'))
                         ,convert_uom_type_in => 'weight' ),0) as "BATCH_QUANTITY"*/ --yp 20022020 - performance ->
       nvl(fg_get_num_display(num_in =>  (select sum(fg_get_num_normal(pt.QUANTITY,nvl(pt.QUANTITYUOM_ID,fg_get_uom_by_uomtype('weight',(select default_value from fg_i_uom_metadata_v where formcode = 'InvItemBatch' and column_name = 'QUANTITYUOM_ID'))))) from FG_S_INVITEMBATCH_ALL_V pt where t.INVITEMMATERIAL_ID  = pt.INVITEMMATERIAL_ID)
                         ,display_type_in => '2'
                         ,uom_id_in => fg_get_uom_by_uomtype('weight')
                         ,convert_uom_id_in => fg_get_uom_by_uomtype('weight',(select default_value from fg_i_uom_metadata_v where formcode = 'InvItemMaterial' and column_name = 'QUANTITY_UOM_ID'))
                         ,convert_uom_type_in => 'weight' ),0) as "BATCH_QUANTITY"
      --for checking only the sum part:
      --(select sum(fg_get_num_normal(pt.QUANTITY,nvl(pt.QUANTITYUOM_ID,fg_get_uom_by_uomtype('weight',(select default_value from fg_i_uom_metadata_v where formcode = 'InvItemBatch' and column_name = 'QUANTITYUOM_ID'))))) from FG_S_INVITEMBATCH_ALL_V pt where t.INVITEMMATERIAL_ID  = pt.INVITEMMATERIAL_ID(+))
      ,t."SYNONYMS", t."SYNONYMSADAPTED", t."ITEMID",t.MaterialStatusName as "STATUSNAME",t.STATUS_ID
     ,'{"path":[{"id":"'||t.INVITEMMATERIAL_ID||'","name":"'||t.FORMCODE||':'||t.INVITEMMATERIALNAME||'"}]}' as formPath
      --,'{"path":[{"id":"'||t.INVITEMMATERIAL_ID||'","name":"'||t.FORMCODE||':'||regexp_replace(t.INVITEMMATERIALNAME,'"','\"')||'"}]}' as formPath
from FG_S_INVITEMMATERIAL_ALL_V t--, FG_S_INVITEMBATCH_ALL_V pt --yp 20022020 - performance remove FG_S_INVITEMBATCH_ALL_V
where 1=1--t.INVITEMMATERIAL_ID  = pt.INVITEMMATERIAL_ID(+);
