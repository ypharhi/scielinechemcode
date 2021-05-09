create or replace view fg_s_invitembatch_dtset_v as
select
t."INVITEMMATERIAL_ID" AS "MATERIAL_ID",
t.INVITEMBATCH_ID,
nvl2(nullif(t.PURITY,'0'),'','{"displayName":"' || '~1 yes' || '" ,"icon":"' || 'fa fa-warning' || '", "tooltip":"'||'Warning! Batch has no purity'||'"}')
 "Purity Warning_SMARTICON",
/*NVL(t.INVITEMBATCHNAME,t.EXTERNALBATCHNUMBER) as "Batch Number",*/
t.BATCHNUMBER as "Batch Number",
t.EXTERNALBATCHNUMBER as "External Batch Number",
t.MATERIALNAME as "Material Name"
from FG_S_INVITEMBATCH_ALL_V t;
