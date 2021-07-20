CREATE OR REPLACE VIEW FG_P_EXPREPORT_RESULT_V AS
SELECT distinct "SAMPLE_ID","EXPERIMENT_ID","ORDER_","ORDER2","RESULT_SMARTPIVOT" from
(
  select distinct to_char(t.SAMPLE_ID) as SAMPLE_ID,
  t.EXPERIMENT_ID,
  10000 as order_,
  CAST(NULL AS varchar2(500)) as order2,-- assay results appear first
  '{pivotkey:"'|| t.SAMPLE_ID||'_'||t.EXPERIMENT_ID||'",pivotkeyname:"UNIQUEROW",'
  ||'column:"Sample #_SMARTLINK",'
  ||'val:'|| '{"displayName":"' || t.SAMPLENAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Sample' || '"  ,"formId":"' || t.SAMPLE_ID || '","tab":"' || '' || '" }'
  ||'}' as result_SMARTPIVOT
from fg_s_sample_all_v t
union all
select t1.SAMPLE_ID,
       t1.EXPERIMENT_ID,
       10001 as order_,
       t1.name_ as order2,
       '{pivotkey:"'|| t1.SAMPLE_ID||'_'||t1.EXPERIMENT_ID||'",pivotkeyname:"UNIQUEROW",'
       ||'column:"'|| t1.name_ || '",'
       ||'val:"'|| t1.val_ ||'"}' as result_SMARTPIVOT
from (
        select t.SAMPLE_ID,
               s.EXPERIMENT_ID,
               t.RESULT_TYPE,
               nvl(t.RESULT_VALUE, t.RESULT_MATERIALNAME) as val_,
               decode(m.InvItemMaterialName, null, t.RESULT_NAME, m.InvItemMaterialName || ' (' || t.RESULT_NAME || ')')
               || decode(u.UOMName,null,'','[' ||u.UOMName || ']') as name_
        from fg_i_result_all_v t, fg_s_invitemmaterial_v m, fg_s_sample_all_v s, fg_s_uom_v u
        where 1=1
        and t.SAMPLE_ID = s.SAMPLE_ID
        and t.RESULT_UOM_ID = u.uom_id(+)
        and t.RESULT_MATERIAL_ID = m.invitemmaterial_id(+)
        and t.RESULT_IS_ACTIVE = 1
 ) t1
 where t1.val_ is not null
) order by order_,ORDER2;
