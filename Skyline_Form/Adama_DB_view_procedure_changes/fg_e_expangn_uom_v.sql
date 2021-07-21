create or replace view fg_e_expangn_uom_v as
select ID,NAME from fg_s_uom_inf_v t
where lower(t.UOMTYPENAME) = 'weight'
union all
select ID,NAME from fg_s_uom_inf_v t
where lower(t.UOMTYPENAME) = 'percentage';
