create or replace view fg_i_expanalysisreport_main_v as
select distinct t.save_name_id
, t.save_name
, u.USERNAME
, trunc(t.creation_date) CREATION_DATE
, to_char(substr(v.entityimpvalue,
                       instr(v.entityimpvalue,',',1,7)+1,
                       decode(instr(v.entityimpvalue,'@'),0,instr(v.entityimpvalue,',',1,8),instr(v.entityimpvalue,'@'))-instr(v.entityimpvalue,',',1,7)-1
                )
         ) as FIRST_PROJECT_SP
, decode(instr(v.entityimpcode,'subproject'),0,'Project','SubProject') as entity
from fg_formlastsavevalue_name t,
fg_s_user_all_v u,
fg_formlastsavevalue v
where t.active = 1
and t.created_by = u.USER_ID
and t.save_name_id = v.save_name_id(+)
and v.entityimpcode(+) in ('projectTable','subprojectTable')
and t.formcode_name = 'ExpAnalysisReport';
