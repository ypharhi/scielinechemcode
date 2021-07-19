create or replace view fg_s_paramref_all_v as
select t."PARAMREF_ID",t."FORM_TEMP_ID",t."PARAMREF_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."PLANNEDVAL1",t."PLANNED_CRITERIA_ID",t."VAL1",t."PARENTID",t."VAL2",t."CRITERIA_ID",t."PARAMREFNAME",t."PLANNEDVAL2",t."UOM_ID",t."PARAMETER_ID",t."TABLETYPE",t."ORIGINFORMID"
--t.* end! edit only the code below...
          ,mp.mpname as parametername,
          c1.PARAMETERSCRITERIANAME,
          c2.PARAMETERSCRITERIANAME as "PLANNEDPARAMETERSCRITERIANAME",
          u.UOMNAME,
          u.UOMTYPENAME,
          nvl(ex.experiment_id,s.EXPERIMENT_ID) as experiment_id,
          s.step_id, s.STEPNAME, s.FORMNUMBERID as STEPNUMBER, s.PREPARATION_RUN,
          nvl2(c1.NUMOFARG,decode(c1.NUMOFARG,'1',
                            decode(c1.PARAMETERSCRITERIANAME,'=',t.VAL1||' '||u.UOMNAME,c1.PARAMETERSCRITERIANAME||' '||t.VAL1||' '||u.UOMNAME),
                            'From '||t.VAL1||' to'||t.VAL2||' '||u.UOMNAME)
                         ,decode(c1.PARAMETERSCRITERIANAME,'=',t.VAL1||' '||u.UOMNAME,c1.PARAMETERSCRITERIANAME||' '||t.VAL1||' '||u.UOMNAME)) as EXPRESSION,
          ex.FORMNUMBERID as EXP_FORMNUMBERID
          from FG_S_PARAMREF_V t,
               fg_s_parameterscriteria_all_v c1,
                fg_s_parameterscriteria_all_v c2,
               fg_s_uom_all_v u,
               FG_S_MP_V mp,
               fg_s_step_v s,
               fg_s_experiment_v ex
          where t.PARAMETER_ID = mp.mp_id(+)
          and   t.CRITERIA_ID = c1.PARAMETERSCRITERIA_ID(+)
          and   t.PLANNED_CRITERIA_ID = c2.PARAMETERSCRITERIA_ID(+)
          and   t.UOM_ID = u.UOM_ID(+)
          and t.PARENTID = ex.experiment_id(+)
          and t.PARENTID = s.step_id(+);
