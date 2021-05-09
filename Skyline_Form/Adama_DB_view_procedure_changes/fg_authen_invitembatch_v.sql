create or replace view fg_authen_invitembatch_v as
select t."INVITEMBATCH_ID",t."FORM_TEMP_ID",t."INVITEMBATCH_OBJIDVAL",
t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",
t."MATERIALNAME",
t."PURITYUOM_ID",t."EXPIRYDATE",
t."RECEIPTDATE",t."SITE_ID",t."PROJECT_ID",t."SUBSUBPROJ_ID",
t."SHELF",t."FORMNUMBERID",t."SOURCE_ID",/*pt."STRUCTURE",*/t."MANUFACTURER_ID",
t."PURITY",t."QUANTITY",t."QUANTITYUOM_ID",t."INVITEMBATCHNAME",
t."MINSTOCKLEVEL",t."MINSTOCKLEVUOM_ID",t."PREPARATIONDATE",
t."COA",t."ORDQUANTITYUOM_ID",t."ORDEREDQUANTITY",t."LABORATORY_ID",t."SUBPROJECT_ID",t."INUSEDEPLETED",
t."COMMENTS",t."ISSTANDART"
,pt."STRUCTURE",pt.INVITEMMATERIALNAME,pt."MW_UOM_ID"
,pt."CASNUMBER",pt."INVITEMMATERIAL_ID",pt."MW",t.EXPERIMENT_ID, t.SAMPLE_ID
,fg_get_num_display(pt."MW",fixed_precision_in => 3, display_type_in => 2,uom_id_in => pt."MW_UOM_ID",convert_uom_name_in => 'gr/mole',convert_uom_type_in => 'molar weight') as "MW_GR_MOLE"
,u.UserName as creator_user_name
,pt.MATERIALPROTOCOLTYPE
,e.EXPERIMENTGROUP_ID as default_experimentgroup_id
,s.SourceName as source_id_name
,decode((select distinct count(*) from fg_i_sampleresults_v r
					where r.INVITEMMATERIAL_ID = t.INVITEMMATERIAL_ID
					and r.SAMPLE_ID = t.SAMPLE_ID
					and r.RESULT_NAME='Assay'),0,0,1) ISSAMPLEDEFAFFECT
,'{"path":[{"id":"'||pt.INVITEMMATERIAL_ID||'","name":"'||pt.FORMCODE||':'||pt.INVITEMMATERIALNAME||'"},{"id":"'||t.INVITEMBATCH_ID||'","name":"'||t.FORMCODE||':'||t.INVITEMBATCHNAME||'"}]}' as formPath
--,'{"path":[{"id":"'||pt.INVITEMMATERIAL_ID||'","name":"'||pt.FORMCODE||':'||regexp_replace(pt.INVITEMMATERIALNAME,'"','\"')||'"},{"id":"'||t.INVITEMBATCH_ID||'","name":"'||t.FORMCODE||':'||t.INVITEMBATCHNAME||'"}]}' as formPath
from FG_S_INVITEMBATCH_ALL_V t,
     fg_s_invitemmaterial_v pt,
     fg_s_user_v u,
     fg_s_experiment_v e,
     fg_s_source_v s
where t.INVITEMMATERIAL_ID(+)= pt.INVITEMMATERIAL_ID
and t.CHANGE_BY = u.user_id(+)
and t.EXPERIMENT_ID = e.experiment_id(+)
and t.SOURCE_ID = s.source_id(+);
