create or replace view fg_e_expangn_material_v as
select id,name,t.PROJECT_ID
from fg_s_invitemmaterial_inf_v t
where t.MaterialStatusName <> 'Cancelled'
order by name; 
