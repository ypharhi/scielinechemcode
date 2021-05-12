create or replace view fg_composition_details_dt_v as
select distinct t.root_id as EXPERIMENT_ID,t.sessionid
,'{"displayName":"'||m.InvItemMaterialName||'"'
||',"style":"'||decode(COUNT(upper(m.ALTERNATIVEGROUP)) over (partition by t.root_id,t.sessionid,m.ALTERNATIVEGROUP),0,'',1,'','background: lightblue;height: 100%')||'","isDisabled":"true"}' as "Material_SMARTSTYLE"
,f.MaterialFunctionName
,fg_get_num_display(sum(t.ww_p*t.general_relation_val/100) over (partition by t.root_id,t.sessionid,t.invitemmaterial_id),0,3) as "W/W%"
,fg_get_num_display(sum(t.wv_grl*t.general_relation_val/100) over (partition by t.root_id,t.sessionid,t.invitemmaterial_id),0,3) as "W/V [gr/l]"
,fg_get_num_display(sum(t.Ww_Grk*t.general_relation_val/100) over (partition by t.root_id,t.sessionid,t.invitemmaterial_id),0,3) as "W/W [gr/kg]"
,decode(nvl(T.FILLER,'0'),'1','Yes','No') as "Filler"
from fg_recipe_material_func_report t,
fg_s_invitemmaterial_v m,
FG_S_MATERIALFUNCTION_V f
where t.invitemmaterial_id = m.invitemmaterial_id
AND nvl(t.rowtype,'chemical') not in ('Premix Material','Recipe','Premix Recipe','Step (Premix) material')
and m.MATERIALFUNC_ID =  f.materialfunction_id(+);
