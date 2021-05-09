create or replace view fg_r_experiment_at_v as
select "FORMID","FORMNUMBERID",
        decode(length(ver_number),1,'0'||ver_number,ver_number) as  "Version Number",
        decode(upper("FORMCODE_ENTITY"),'MATERIALREF','Reaction','USERSCREW','Crew','GROUPSCREW','Groups Crew',"FORMCODE_ENTITY") as "Entity",
        "Field Name",
        "Value"/*,elementclass*/
from (
select distinct t.FORMCODE_ENTITY, e.formid,E.Formnumberid, E.Formnumberid as "Experiment No.", nvl((to_number(T.LOG_VERSION) - 1),0) AS ver_number, m.elementclass, m.displaylabel as "Field Name", nvl(t.DISPLAYVALUE,t.current_displayvalue) as "Value"
from FG_FORMLASTSAVEVALUE_INF_SS_v t,
     FG_FORMELEMENTINFOATMETA_MV M,
     --FG_FORMLASTSAVEVALUE_INF_SSM T1,
     FG_S_EXPERIMENT_PIVOT E
WHERE T.FORMCODE_ENTITY = M.FORMCODE_ENTITY
AND   T.ENTITYIMPCODE = M.ENTITYIMPCODE
AND   T.EXPERIMENT_ID = E.FORMID
AND   NVL(E.TEMPLATEFLAG,0) <> 1
AND   M.ISSEARCHELEMENT = 1
AND   nvl(t.DISPLAYVALUE,t.current_displayvalue) IS NOT NULL
) order by "Version Number","Entity";
