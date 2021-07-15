create or replace view FG_I_CONN_MATERIAL_TYPE_V AS
select distinct t.MATERIALTYPE_ID, t.invitemmaterial_id, t.InvItemMaterialName, mt.materialtypename
from FG_S_INVITEMMATERIAL_V T,
     fg_s_materialtype_v mt
where instr(',' || t.MATERIALTYPE_ID || ',', ',' || mt.MATERIALTYPE_ID || ',') > 0
order by t.invitemmaterial_id
