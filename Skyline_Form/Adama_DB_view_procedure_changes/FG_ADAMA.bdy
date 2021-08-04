create or replace package body FG_ADAMA is

  /*FUNCTION FG_UNPIVOT_MP_TABLE(foo varchar2)
        RETURN UNPIVOT_MP_TABLE
        PIPELINED IS

        return_table UNPIVOT_MP;

        cursor c is
         select regexp_substr (str, '[^},]+', 1, rownum) as item
         from (select list_comma_separated_in as str from dual)
         connect by level <= length (regexp_replace (str, '[^,]+'))+ 1;

    BEGIN
        return_table := UNPIVOT_MP();

        for r in c
  loop
    return_table.extend;
    return_table(return_table.last) := r.item;
  end loop;

        SELECT '1','2','3'
          INTO rec
          FROM DUAL;

        -- you would usually have a cursor and a loop here
        PIPE ROW (rec);

        RETURN;
    END;
*/


  -- return 1 if all user are trained else 0
  function IS_CREW_TRAINED (materialId_in number, experimentId_in number) return number as
      counter number;
  begin
      select count(*) into counter FROM (
       -- (select distinct USERID from -- yp 17012017 distinct not needed
          /*(select to_char(t.USER_ID_SINGLE) as "USERID"
          from FG_S_USERSCREW_ALL_V t
          where 1=1
          and t.PARENTID = experimentId_in
          and t.sessionId is null
          union
          select to_char(u.USER_ID_SINGLE)
          from FG_S_GROUPSCREW_ALL_V g,
          FG_S_USERSCREW_ALL_V u
          where g.PARENTID = experimentId_in
          and g.SESSIONID is null
          and u.PARENTID = g.GROUP_ID_SINGLE
          and u.SESSIONID is null)*/ --> yp 17012017 change to -> (it was OK when the users where inside group in the maintenance in adama v1.1 the groups are inside users)

          --userse crew + groups
          select t.user_id as USERID
          from FG_I_USERS_GROUP_SUMMARY_V t
          where t.parentid = experimentId_in
          MINUS
          -- users trained
          select pt.USERID
          from fg_s_training_all_v pt
          where 1=1
          and pt.SESSIONID is null
          and pt.PARENTID = materialId_in
      );

      if counter > 0 then
         return 0;
      else
         return 1;
      end if;
  end;

   function IS_INVENTORY_FAMILIAR (experimentId_in number,stepId_in number default -1) return number as
      familiar number;
  begin
    --RETURN 1; -- use it to disable familarity check - for develop only
      familiar:='1';
      --organic,formulation
      for mat in (
        select mat.invitemmaterial_id
        from fg_s_materialref_all_v mat
        ,  fg_s_experiment_all_v expr
        ,  FG_S_INVITEMMATERIAL_V invm -- yp 16072018 - fix bug in adama production (remove clone maerials)
        WHERE mat.EXPERIMENT_ID= expr.EXPERIMENT_ID and mat.SESSIONID is null and nvl(mat.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
        and invm.invitemmaterial_id = mat.invitemmaterial_id and invm.SESSIONID is null and nvl(invm.ACTIVE,'1') = '1' -- yp 16072018 - fix bug in adama production (remove clone maerials)
        and (mat.PARENTID = stepId_in or mat.STEPSTATUSNAME not in ('Planned','Cancelled'))

      )
      loop
        select min("familiarity") into familiar from
        (select IS_CREW_TRAINED(mat.invitemmaterial_id,experimentId_in) as "familiarity" from dual
        union all select familiar from dual);
      end loop;

      for inst in (
        select inst.inviteminstrument_id
        from fg_s_instrumentref_all_v inst
        ,  fg_s_experiment_all_v expr
        WHERE inst.EXPERIMENT_ID= expr.EXPERIMENT_ID and inst.SESSIONID is null and nvl(inst.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
        and (inst.PARENTID = stepId_in or inst.PARENTSTATUSNAME not in ('Planned', 'Cancelled'))
         and inst.statusname <> 'Disabled'

      )
      loop
        select min("familiarity") into familiar from
        (select IS_CREW_TRAINED(inst.inviteminstrument_id,experimentId_in) as "familiarity" from dual
        union all select familiar from dual);
      end loop;

      --040618 task 16546
      --for instrument in selftest
      -- yp 20062018 remove selftest from FAMILIAR check - task ...
      /* for inst in (
      select IR.inviteminstrument_id
      from fg_s_InstrumentSelect_all_v IR,
         fg_s_selftest_all_v s
      where IR.PARENTID = S.SELFTEST_ID AND IR.SESSIONID IS NULL AND NVL(IR.ACTIVE,'1') = '1'
      and s.EXPERIMENT_ID = experimentId_in
      and (s.STEP_ID = stepId_in or s.STEP_STATUS not in ('Planned', 'Cancelled'))
      )
      loop
        select min("familiarity") into familiar from
        (select IS_CREW_TRAINED(inst.inviteminstrument_id,experimentId_in) as "familiarity" from dual
        union all select familiar from dual);
      end loop;  */

      --for analytical,parametric experiments
     /* for cln in (
        select cln.INVITEMCOLUMN_ID
        from fg_s_columnselect_all_v cln
        ,  fg_s_experiment_all_v expr
        WHERE cln.PARENTID= expr.EXPERIMENT_ID and cln.SESSIONID is null and nvl(cln.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
      )
      loop
        select min("familiarity") into familiar from
        (select IS_CREW_TRAINED(cln.INVITEMCOLUMN_ID,experimentId_in) as "familiarity" from dual
        union all select familiar from dual);
      end loop;   */

      for inst in (
        select inst.inviteminstrument_id
        from fg_s_instrumentref_all_v inst
        ,  fg_s_experiment_all_v expr
        WHERE inst.PARENTID= expr.EXPERIMENT_ID and inst.SESSIONID is null and nvl(inst.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
        and inst.statusname <> 'Disabled'
      )
      loop
        select min("familiarity") into familiar from
        (select IS_CREW_TRAINED(inst.inviteminstrument_id,experimentId_in) as "familiarity" from dual
        union all select familiar from dual);
      end loop;

      --for analytical,formulation
      for b in (
        select b.invitemmaterial_id
        from fg_s_batchselect_all_v b
        ,  fg_s_experiment_all_v expr
        WHERE b.PARENTID= expr.EXPERIMENT_ID and b.SESSIONID is null and nvl(b.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
      )
      loop
        select min("familiarity") into familiar from
        (select IS_CREW_TRAINED(b.invitemmaterial_id,experimentId_in) as "familiarity" from dual
        union all select familiar from dual);
      end loop;

      --for analytical experiment
      for c in (
        select c.MATERIALID
        from fg_s_component_all_v c
        ,  fg_s_experiment_all_v expr
        WHERE c.PARENTID= expr.EXPERIMENT_ID and c.SESSIONID is null and nvl(c.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
      )
      loop
        select min("familiarity") into familiar from
        (select IS_CREW_TRAINED(c.MATERIALID,experimentId_in) as "familiarity" from dual
        union all select familiar from dual);
      end loop;
      /*for b in (
        select b.invitemmaterial_id
        from fg_s_batchfrselect_all_v b
        ,  fg_s_experiment_all_v expr
        WHERE b.PARENTID= expr.EXPERIMENT_ID and b.SESSIONID is null
        and expr.EXPERIMENT_ID = experimentId_in
      )
      loop
        select min("familiarity") into familiar from
        (select IS_CREW_TRAINED(b.material_id,experimentId_in) as "familiarity" from dual
        union all select familiar from dual);
      end loop; */

      --for formulation experiment
      for fr in (
        select fr.invitemmaterial_id
        from fg_s_formulantref_all_v fr
        ,  fg_s_experiment_all_v expr
        WHERE fr.EXPERIMENT_ID= expr.EXPERIMENT_ID and fr.SESSIONID is null and nvl(fr.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
        and (fr.PARENTID = stepId_in or fr.STEPSTATUSNAME not in ('Planned', 'Cancelled'))

      )
      loop
        select min("familiarity") into familiar from
        (select IS_CREW_TRAINED(fr.invitemmaterial_id,experimentId_in) as "familiarity" from dual
        union all select familiar from dual);
      end loop;

      --for parametric experiment
      for expr in (
        select expr.material_id as "MATERIAL_ID"
        from fg_s_experimentprcr_all_v expr
        where expr.EXPERIMENT_ID = experimentId_in
        and expr.material_id is not null and nvl(expr.ACTIVE,'1') = '1'
        union all
        select expr.BMATERIALID
        from fg_s_experimentprbt_all_v expr
        where expr.EXPERIMENT_ID = experimentId_in
        and expr.bmaterialid is not null and nvl(expr.ACTIVE,'1') = '1'
        union all
        select expr.VSMATERIALID
        from fg_s_experimentprvs_all_v expr
        where expr.EXPERIMENT_ID = experimentId_in
        and expr.vsmaterialid is not null and nvl(expr.ACTIVE,'1') = '1'
      )
      loop
        select min("familiarity") into familiar from
        (select IS_CREW_TRAINED(expr.MATERIAL_ID,experimentId_in) as "familiarity" from dual
        union all select familiar from dual);
      end loop;
      return familiar;
  end;

  function GET_ORIGINSBYDELEMETER(sampleId_in number, delimeter_in char) return varchar2 as
   origin varchar2(500);
   begin
     /* select decode(t.ORIGINEXPERIMENTID,null,
              decode(t.ORIGINSTEPID,null,
               decode(t.ORIGINACTIONID,null,
                decode(t.ORIGINSELFTESTID,null
                ,t.EXPERIMENTNAME || delimeter_in || tst.STEPNAME || delimeter_in || tst.ACTIONNAME|| delimeter_in || wrk.WORKUPTYPE || wrk.StageStatusName
                ,t.EXPERIMENTNAME || delimeter_in || tst.STEPNAME || delimeter_in || tst.ACTIONNAME|| delimeter_in || tst.SELFTESTTYPENAME)
               ,t.EXPERIMENTNAME || delimeter_in || tst.STEPNAME || delimeter_in || tst.ACTIONNAME)
              ,t.EXPERIMENTNAME || delimeter_in || tst.STEPNAME )
             ,t.EXPERIMENTNAME) into origin
      from fg_s_sample_all_v t,
      fg_i_id_connection_work_v wrk,
      fg_i_id_connection_test_v tst
      where (t.ORIGINEXPERIMENTID is not null and tst.experiment_id = t.ORIGINEXPERIMENTID
      or t.ORIGINSTEPID is not null and tst.STEP_ID = t.ORIGINACTIONID
      or t.ORIGINACTIONID is not null and tst.ACTION_ID = t.ORIGINACTIONID
      or t.ORIGINWORKUPID is not null and wrk.WORKUP_ID = t.ORIGINWORKUPID
      or t.ORIGINSELFTESTID is not null and tst.SELFTEST_ID = t.ORIGINSELFTESTID)
      and t.sample_id = sampleId_in;*/

     select decode(ORIGINEXPERIMENTID,null,
              decode(t.ORIGINSTEPID,null,
               decode(t.ORIGINACTIONID,null,
                decode(t.ORIGINSELFTESTID,null
                ,t.EXPERIMENTNAME || delimeter_in || (select st.stepname from fg_s_step_all_v st where st.STEP_ID = t.STEP_ID) ||  delimeter_in  ||(select act.FORMNUMBERID from fg_s_action_all_v act where act.ACTION_ID = t.ACTION_ID)||  delimeter_in  || (select wrk.WORKUPTYPE from fg_s_workup_all_v wrk where wrk.WORKUP_ID = t.ORIGINWORKUPID) || delimeter_in || (select wrk.StageStatusName from fg_s_workup_all_v wrk where wrk.WORKUP_ID = t.ORIGINWORKUPID)
                ,t.EXPERIMENTNAME ||  delimeter_in  || (select st.stepname from fg_s_step_all_v st where st.STEP_ID = t.STEP_ID) ||  delimeter_in  ||(select act.FORMNUMBERID from fg_s_action_all_v act where act.ACTION_ID = t.ACTION_ID)||  delimeter_in  || (select tst.SELFTESTTYPENAME from fg_s_selftest_all_v tst where tst.SELFTEST_ID = t.ORIGINSELFTESTID))
               ,t.EXPERIMENTNAME ||  delimeter_in  || (select st.stepname from fg_s_step_all_v st where st.STEP_ID = t.STEP_ID) ||  delimeter_in  ||(select act.FORMNUMBERID from fg_s_action_all_v act where act.ACTION_ID = t.ORIGINACTIONID))
              ,t.EXPERIMENTNAME ||  delimeter_in  || (select st.stepname from fg_s_step_all_v st where st.STEP_ID = t.ORIGINSTEPID))
             ,t.EXPERIMENTNAME) into origin
      from  fg_s_sample_all_v t
      where t.SAMPLE_ID = sampleId_in;
     return origin;
   end;

   function GET_SAMPLE_PATH(formId_in number, delimeter_in char) return varchar2 as
   path varchar2(500);
   formCode varchar(500);
   query varchar2(500);
   wherePart varchar2(200);
   begin
     select t.formcode_entity into formCode
     from fg_form t,fg_sequence sq
     where sq.formcode = t.formcode
     and formId_in = sq.id;
     if upper(formCode) like '%WORKUP%' then
       select nvl2(t.experiment_id
       ,t.ExperimentName||nvl2(t.STEP_ID
              ,delimeter_in||t.STEPNAME||nvl2(t.ACTION_ID
                 ,delimeter_in||t.ACTIONNAME||nvl2(t.WORKUP_ID
                      ,delimeter_in||t.WORKUPTYPE||delimeter_in||t.StageStatusName,''),''),''),'')
       into path
       from fg_i_id_connection_workup_v t
       where t.WORKUP_ID = formId_in;
     else
       if upper(formCode) = 'EXPERIMENTPR' then
         formCode:= 'EXPERIMENT';
         end if;
       wherePart:=upper(formCode)||'_ID';
       query :='select distinct decode(:1,''Experiment''
       ,ExperimentName,ExperimentName||decode(:1,''Step''
              ,:2||STEPNAME,:2||STEPNAME||decode(:1,''Action''
                 ,:2||ACTIONNAME,:2||ACTIONNAME||decode(:1,''SelfTest''
                      ,:2||SELFTESTTYPENAME,:3))))
       from fg_i_id_connection_selftest_v
        where '|| wherePart||'= :5';
        execute immediate  query
        into path
        using formCode,formCode,delimeter_in,delimeter_in,formCode,delimeter_in,delimeter_in,formCode,delimeter_in,'', formId_in ;

     end if;
     return path;
   EXCEPTION
      WHEN OTHERS THEN
        return null;
   end;

   function GET_SAMPLE_PATH(sampleId_in number, delimeter_in char) return varchar2 as
   path varchar2(500);
   begin
     select nvl2(t.experiment_id,
                 (select experimentname from fg_s_experiment_pivot where formid = t.experiment_id) ||
                 nvl2( t.step_id,
                       delimeter_in ||
                      (select stepname||nvl2(runnumber,' Run #'||runnumber,'') from fg_s_step_v where formid = t.step_id) ||
                       nvl2( t.action_id,
                             delimeter_in ||
                            (select actionname from fg_s_action_pivot where formid = t.action_id) ||
                             nvl2( t.selftest_id,
                                   delimeter_in ||
                                   (select selftesttypename from fg_s_selftesttype_pivot where formid = t.selftesttype_id)
                                   ,nvl2( t.workup_id,
                                         delimeter_in ||
                                         (select workuptypename || delimeter_in || StageStatusName from fg_s_workup_all_v where formid = t.workup_id)
                                        ,'')
                                  )
                            ,'')
                      ,'')
                 ,nvl2(t.parentid,
                       nvl((select invitembatchname from fg_s_invitembatch_v where formid = t.parentid),'')
                      ,''))
     into path
     from fg_s_sample_pivot t
     where t.formid = sampleId_in;
     return path;
   EXCEPTION
      WHEN OTHERS THEN
        return null;
   end;

   function GET_SAMPLE_USE_PATH(formId_in number, delimeter_in char) return varchar2 as
     path varchar2(500);
     formCode varchar2(400);
   begin

     select t.formcode_entity into formCode
     from fg_form t,fg_sequence sq
     where sq.formcode = t.formcode
     and formId_in = sq.id;

     select decode(formCode,'Experiment',
                   (select experimentname from fg_s_experiment_pivot where formid = formId_in)
                   ,decode(formCode,'Step',
                           (select experimentname|| delimeter_in ||stepname  from fg_s_step_all_v where formid = formId_in)
                          ,decode(formCode,'Action',
                                  (select (select experimentname from fg_s_experiment_pivot where formid = t.EXPERIMENT_ID)|| delimeter_in ||t.stepname|| delimeter_in ||t.ACTIONNAME  from fg_s_action_all_v t where formid = formId_in)
                                  ,decode(formCode,'SelfTest',
                                          (select (select experimentname from fg_s_experiment_pivot where formid = s.EXPERIMENT_ID)|| delimeter_in ||s.stepname|| delimeter_in ||s.ACTIONNAME|| delimeter_in ||s.SELFTESTTYPENAME from fg_s_selftest_all_v s where formId = formId_in)
                                          ,decode(formCode,'Workup',
                                                  (select (select experimentname from fg_s_experiment_pivot where formid = w.EXPERIMENT_ID)|| delimeter_in ||(select stepname|| delimeter_in ||ACTIONNAME from fg_s_action_all_v where formid = w.ACTION_ID)|| delimeter_in ||w.WORKUPTYPENAME from fg_s_workup_all_v w where formId = formId_in)
                                                  ,decode(formCode,'InvItemBatch',
                                                          (select invitembatchname from fg_s_invitembatch_pivot where formid = formId_in)
                                                          )
                                                   )
                                           )
                                   )
                            )
                     )

     into path
     from dual t;
     return path;
   EXCEPTION
      WHEN OTHERS THEN
        return null;
   end;


  function CREATE_EXPERIMENT_MATERIAL_SS (stepId_in number) return number as --SS FOR Snap Shout
    id_ NUMBER;
    isFirstSS NUMBER;
    experimentId number;
    lastExperimentStatusName varchar2(50);
  begin
      SELECT count(*) INTO isFirstSS
      FROM  FG_S_MATERIALREF_ALL_V_PLAN t
      WHERE t.step_id = stepId_in;

      if isFirstSS > 0 then
        return 0; -- do nothing
      end if;

      SELECT max(t.EXPERIMENT_ID) INTO experimentId
      FROM  FG_S_STEP_V t
      WHERE t.step_id = stepId_in;

      -- SAVE MATERIAL REF
      INSERT INTO FG_S_MATERIALREF_ALL_V_PLAN ( catalyst,
                                                materialref_id,
                                                form_temp_id,
                                                materialref_objidval,
                                                formid,
                                                timestamp,
                                                change_by,
                                                sessionid,
                                                active,
                                                tabletype,
                                                massuom_id,
                                                mass,
                                                yield,
                                                purityinf,
                                                actualpurity,
                                                densityinf,
                                                limitingagent,
                                                equivalent,
                                                casnamberinf,
                                                casnameinf,
                                                iupacnameinf,
                                                mwinf,
                                                watercontent,
                                                quantratiototal,
                                                materialnameinf,
                                                synonymsinf,
                                                smilesinf,
                                                formulainf,
                                                batchinf,
                                                quantity,
                                                volume,
                                                mole,
                                                volratiototal,
                                                quantityuom_id,
                                                voluom_id,
                                                purityuom_id_inf,
                                                moleuom_id,
                                                actpurityuom_id,
                                                densityuom_id_inf,
                                                mwuom_id_inf,
                                                watercontuom_id,
                                                parentid,
                                                materialrefname,
                                                yielduom_id,
                                                batch_id,
                                                waterconuomname,
                                                mwuomname,
                                                densityuomname,
                                                actpurityuomname,
                                                moleuomname,
                                                purityuomname,
                                                voluomname,
                                                quantityuomname,
                                                massuomname,
                                                yielduomname,
                                                invitemmaterialname,
                                                structure,
                                                casnumber,
                                                casname,
                                                synonyms,
                                                density,
                                                iupacname,
                                                mw,
                                                mw_uom_id,
                                                density_uom_id,
                                                invitemmaterial_id,
                                                chemicalformula,
                                                smiles,
                                                invitembatchname,
                                                purity,
                                                purityuom_id,
                                                EXPERIMENT_ID,
                                                STEP_ID,
                                                EXPERIMENTSTATUSNAME,
                                                STEPSTATUSNAME,
                                                isPlannedSnapShout,
                                                comments,
                                                ratio,
                                                ratiotype_id,
                                                reactantmaterial_id,
                                                sample_id,
                                                concinreactionmass,
                                                resultid_holder,
                                                QUANTITYRATE,
                                                QUANTITYRATE_UOM,
                                                VOLUMERATE,
                                                VOLRATEUOM_ID,
                                                MOLERATE,
                                                MOLERATEUOM_ID,
                                                PREPARATION_RUN
                                              )
      select  catalyst,
              materialref_id,
              form_temp_id,
              materialref_objidval,
              formid,
              timestamp,
              change_by,
              sessionid,
              active,
              tabletype,
              massuom_id,
              mass,
              yield,
              purityinf,
              actualpurity,
              densityinf,
              limitingagent,
              equivalent,
              casnamberinf,
              casnameinf,
              iupacnameinf,
              mwinf,
              watercontent,
              quantratiototal,
              materialnameinf,
              synonymsinf,
              smilesinf,
              formulainf,
              batchinf,
              quantity,
              volume,
              mole,
              volratiototal,
              quantityuom_id,
              voluom_id,
              purityuom_id_inf,
              moleuom_id,
              actpurityuom_id,
              densityuom_id_inf,
              mwuom_id_inf,
              watercontuom_id,
              parentid,
              materialrefname,
              yielduom_id,
              batch_id,
              waterconuomname,
              mwuomname,
              densityuomname,
              actpurityuomname,
              moleuomname,
              purityuomname,
              voluomname,
              quantityuomname,
              massuomname,
              yielduomname,
              invitemmaterialname,
              structure,
              casnumber,
              casname,
              synonyms,
              density,
              iupacname,
              mw,
              mw_uom_id,
              density_uom_id,
              invitemmaterial_id,
              chemicalformula,
              smiles,
              invitembatchname,
              purity,
              purityuom_id,
              EXPERIMENT_ID,
              STEP_ID,
              EXPERIMENTSTATUSNAME,
              'Planned' as STEPSTATUSNAME,
              1 as isPlannedSnapShout,
              comments,
              ratio,
              ratiotype_id,
              reactantmaterial_id,
              sample_id,
              concinreactionmass,
              resultid_holder,
              QUANTITYRATE,
              QUANTITYRATE_UOM,
              VOLUMERATE,
              VOLRATEUOM_ID,
              MOLERATE,
              MOLERATEUOM_ID,
              PREPARATION_RUN
      from FG_S_MATERIALREF_ALL_V t
      WHERE t.step_id = stepId_in;

       --update fg_s_paramref_pivot for step
        update fg_s_paramref_pivot t
        set t.plannedval1 = t.val1,
            t.plannedval2 = t.val2,
            t.planned_criteria_id = t.criteria_id
        where t.parentid = stepId_in;

        --clean the actual values
        update fg_s_paramref_pivot t
        set t.val1 = null,
            t.val2 = null,
            t.criteria_id = null
        where t.parentid = stepId_in;

      select nvl(lower(t.LASTSTATUSNAME),'planned')
      into lastExperimentStatusName
      from FG_S_EXPERIMENT_ALL_V t
      where t.EXPERIMENT_ID = experimentId;

      if lastExperimentStatusName = 'planned' then
          --update fg_s_paramref_pivot for experiment;
          update fg_s_paramref_pivot t
          set t.plannedval1 = t.val1,
              t.plannedval2 = t.val2,
              t.planned_criteria_id = t.criteria_id
          where t.parentid = experimentId;

          --clean the actual values
          update fg_s_paramref_pivot t
          set t.val1 = null,
              t.val2 = null,
              t.criteria_id = null
          where t.parentid = experimentId;
      end if;

      RETURN 0;
  end;
  function CREATE_EXPERIMENT_FORMULANT_SS (stepId_in number) return number as --SS FOR Snap Shout
    --id_ NUMBER;
    isFirstSS NUMBER;
  begin
      SELECT count(*) INTO isFirstSS
      FROM  FG_S_FORMULANTREF_ALL_V_PLAN t
      WHERE t.step_id = stepId_in;

      if isFirstSS > 0 then
        return 0; -- do nothing
      end if;



      -- SAVE Formulant REF
INSERT INTO FG_S_FORMULANTREF_ALL_V_PLAN (
                                                formulantref_id,
form_temp_id,
formulantref_objidval,
formid,
timestamp,
change_by,
sessionid,
active,
formcode,
materialname,
solid,
batch_id,
BATCHNUMBER,
parentid,
expseriesplannum,
itemid,
batchestable,
formulantrefname,
ai,
casnumber,
tabletype,
materialid,
invitemmaterialname,
invitemmaterial_id,
formnumberid,
invitembatch_id,
externalbatchnumber,
step_id,
experiment_id,
EXPERIMENTSTATUSNAME,
STEPSTATUSNAME,
WEBIXEXPSTEP_ID
--isPlannedSnapShout
 )
      select  formulantref_id,
              form_temp_id,
              formulantref_objidval,
              formid,
              timestamp,
              change_by,
              sessionid,
              active,
              formcode,
              materialname,
              solid,
              batch_id,
              BATCHNUMBER,
              parentid,
              expseriesplannum,
              itemid,
              batchestable,
              formulantrefname,
              ai,
              casnumber,
              tabletype,
              materialid,
              invitemmaterialname,
              invitemmaterial_id,
              formnumberid,
              invitembatch_id,
              externalbatchnumber,
              step_id,
              experiment_id,
              EXPERIMENTSTATUSNAME,
              'Planned' as STEPSTATUSNAME,
              webixexpstep_id
              -- 1 as isPlannedSnapShout
      from FG_S_FORMULANTREF_ALL_V t
      WHERE t.step_id = stepId_in;

      SELECT count(*) INTO isFirstSS
      FROM  FG_S_FORMULANTREF_ALL_V_PLAN t
      WHERE t.step_id = stepId_in;

      if isFirstSS = 0 then
       INSERT INTO FG_S_FORMULANTREF_ALL_V_PLAN (FORMULANTREF_ID,Formid,Step_Id,Active,Tabletype,PARENTID) values ('0','0',stepId_in,0,'SNAPSHOT_FLAG',stepId_in);
      end if;

      insert into FG_I_WEBIX_OUTPUT_ALL_V_SS (material_id,
                                              result_id,
                                              experiment_id,
                                              step_id,
                                              batch_id,
                                              mass,
                                              result_value,
                                              isstandart,
                                              result_uom_id)
      select material_id,
                                              result_id,
                                              experiment_id,
                                              step_id,
                                              batch_id,
                                              mass,
                                              result_value,
                                              isstandart,
                                              result_uom_id
     from FG_I_WEBIX_OUTPUT_ALL_V
     where  step_id = stepId_in;

      RETURN 0;
  end;


  function CHECK_SPECIFICATION_VALIDATION(value1_in varchar ,value2_in varchar,criteria1_in varchar,criteria2_in varchar) return number as
           IS_VALIDSPEC number;
  begin
    select
  case when criteria1_in like '<' and (criteria2_in like '>' or criteria2_in like '=' or criteria2_in like '>=') and to_number(value1_in) <= to_number(value2_in) then 0
       when criteria1_in like '>' and (criteria2_in like '<' or criteria2_in like '=' or criteria2_in like '<=') and to_number(value2_in) <= to_number(value1_in) then 0
       when criteria1_in like '<>' and criteria2_in like '=' and to_number(value2_in) = to_number(value1_in) then 0

       when criteria1_in like '=' and criteria2_in like '<' and to_number(value1_in) >= to_number(value2_in) then 0
       when criteria1_in like '=' and criteria2_in like '=' and to_number(value2_in) <> to_number(value1_in) then 0
       when criteria1_in like '=' and criteria2_in like '<=' and to_number(value1_in) < to_number(value2_in) then 0
       when criteria1_in like '=' and criteria2_in like '>'  and to_number(value1_in) <= to_number(value2_in) then 0
       when criteria1_in like '=' and criteria2_in like'<>' and to_number(value2_in) = to_number(value1_in) then 0
       when criteria1_in like '=' and criteria2_in like '>=' and to_number(value1_in) > to_number(value2_in) then 0

       when criteria1_in like '<=' and (criteria2_in like '>' or criteria2_in like '=' or criteria2_in like '>=') and to_number(value1_in) < to_number(value2_in) then 0
       when criteria1_in like '>=' and (criteria2_in like '<' or criteria2_in like '=' or criteria2_in like '<=') and to_number(value1_in) > to_number(value2_in) then 0
       else 1
         end
         into IS_VALIDSPEC
         from dual;
    return IS_VALIDSPEC;
  end ;


  function CREATE_SERIES_INDX_DATA_SS (series_id_in varchar, index_in varchar, userid_in varchar, ts_in varchar) return number as
      experimentIdParam varchar(200);
      originFormulantPropRefParm varchar(200);
  begin
    --set parameters...
    --experimentIdParam:
    experimentIdParam := FG_GET_STRUCT_FORM_ID('ExperimentFor',ts_in); --todo version 1.1 makr it for all experiment type

    --originFormulantPropRefParm:
    select t.FORMULATIONPROPREF_ID into originFormulantPropRefParm
    from FG_S_FORMULATIONPROPREF_ALL_V t
    where t.EXPERIMENTINDEX = index_in
    and   t.PARENTID = series_id_in
    and   t.SESSIONID is null;

    -- create experiment
    insert into fg_s_experiment_pivot (formid,PROJECT_ID,SUBPROJECT_ID,SUBSUBPROJECT_ID,protocoltype_id,laboratory_id,aim, change_by, originFormulantPropRef)
    select experimentIdParam, t.PROJECT_ID, t.SUBPROJECT_ID, t.SUBSUBPROJECT_ID, t.PROTOCOL_ID, t.LABORATORY_ID, t.SERIESAIM, userid_in, originFormulantPropRefParm--, t.FORMCODE, t.FORMCODE_ENTITY
    from fg_s_experimentseries_all_v t
    WHERE t.EXPERIMENTSERIES_ID = series_id_in;

    --FG_I_SERIES_INDX_DATA_V_SS for "webix" experiment
    insert into FG_I_SERIES_INDX_DATA_V_SS (formulationpropref_id,
                                            form_temp_id,
                                            formulationpropref_objidval,
                                            formid,
                                            timestamp,
                                            cloneid,
                                            templateflag,
                                            change_by,
                                            sessionid,
                                            active,
                                            formcode_entity,
                                            formcode,
                                            factor,
                                            parentid,
                                            uomtotalmass_id,
                                            expseriesplannum,
                                            uomtheototalmass_id,
                                            formulationproprefname,
                                            density,
                                            caltheototalmass,
                                            calcidentifier,
                                            parametermonitoring,
                                            caltotalmass,
                                            densityuominf,
                                            experimentindex,
                                            tabletype,
                                            formulationpropindex_objidval,
                                            value,
                                            uom_id,
                                            solid,
                                            maxCalcIdentifier,
                                            experiment_id,
                                            FORMULANTREF_ID,
                                            MATERIAL_BATCH_ID)
    select  formulationpropref_id,
            form_temp_id,
            formulationpropref_objidval,
            formid,
            timestamp,
            cloneid,
            templateflag,
            change_by,
            sessionid,
            active,
            formcode_entity,
            formcode,
            factor,
            parentid,
            uomtotalmass_id,
            expseriesplannum,
            uomtheototalmass_id,
            formulationproprefname,
            density,
            caltheototalmass,
            calcidentifier,
            parametermonitoring,
            caltotalmass,
            densityuominf,
            experimentindex,
            tabletype,
            formulationpropindex_objidval,
            value,
            uom_id,
            solid,
            "maxCalcIdentifier",
            experimentIdParam,
            FORMULANTREF_ID,
            MATERIAL_BATCH_ID
    from fg_i_series_indx_data_v t
    where t.FORMULATIONPROPREF_ID = originFormulantPropRefParm
    and   t.SESSIONID is null
    and   nvl(t.VALUE,0) <> 0;

    --return experiment_id
    return experimentIdParam;

  end;
/*function CHECK_SELFTEST_VALIDATION (stepId number) return number as
  IS_VALIDSTEP NUMBER;
   begin
SELECT
min(CASE t.SELFTESTTYPENAME
WHEN 'Appearance' THEN 1
WHEN 'Density' THEN
  decode(t.dilutionfactor,null,'0','1')
 * decode(t.result,null,'0','1')
 * decode(t.CALCULATIONRES,null,'0','1')
WHEN 'pH' THEN
  decode(t.PHRESULTS,null,0,1)
WHEN 'Foaming' THEN
   decode(t.WATERTYPE_ID,null,'0','1')
 * decode(t.resultZero,null,'0','1')
 * decode(t.resultMinute,null,'0','1')
 * decode(t.temperature,null,'0','1')


WHEN 'Flash Point' THEN
   decode(t.FLASHPOINTTEMPERATURE,null,0,1)
WHEN 'Wet Sieve' THEN
   decode(t.WATERTYPE_ID,null,'0','1')
* decode(t.WetSieve,null,'0','1')

WHEN 'Pourability' THEN
   decode(t.startTime,null,'0','1')
* decode(t.finishTime ,null,'0','1')
* decode(t.cylinderWeight ,null,'0','1')
* decode(t.weightBStorage ,null,'0','1')
* decode(t.weightAStorage ,null,'0','1')
* decode(t.weightARinsing ,null,'0','1')
* decode(t.resultAWashing ,null,'0','1')
* decode(t.resultAFeeding ,null,'0','1')
WHEN 'Suspensibility' THEN 1
-- decode(t.INSTRUMENTTYPE_ID,null,'0','1')
-- decode(t.USEDPERCENT ,null,'0','1')
-- decode(t.MASS25ML ,null,'0','1')
-- decode(t.MASSFRACTUALLYCYLINDER ,null,'0','1')
WHEN 'Particle Size' THEN
  decode(t.INSTRUMENTTYPE_ID,null,0,1)
WHEN 'Viscosity' THEN
  decode(t.SPINDLE,null,'0','1')
  * decode(t.RPM,null,'0','1')
  * decode(t.RESULT,null,'0','1')
WHEN 'Cold Test' THEN
  decode(t.CRYSTAL,null,'0','1')
    * decode(t.FREEZING,null,'0','1')
      * decode(t.COLDTESTRESULTS,null,'0','1')
       * decode(t.CRYSTALLIZATION,null,'0','1')
WHEN 'Emulsion Stability' THEN
--decode(t.GLASSWARE,null,'0','1')
decode(t.INITIALEMULSIFICATION,null,'0','1')
 * decode(t.CONCENTRATION,null,'0','1')
* decode(t.STABILITYRESULTS,null,'0','1')
END)/* over (partition by t.SELFTEST_ID)*//* as "isValid"
INTO IS_VALIDSTEP
from fg_s_selftest_all_v t
where t.step_id=stepId;
Return IS_VALIDSTEP;
     end; */


/*procedure SET_CONSTRAINTS as
  begin
    EXECUTE IMMEDIATE 'ALTER TABLE FG_S_PROJECT_PIVOT
                       ADD CONSTRAINT PROJECT_NAME_UNIQUE UNIQUE (PROJECTNAME)';
   end;*/

function CHECK_SELFTEST_VALIDATION (stepId number) return varchar2 is
  IS_VALIDSTEP varchar2(30000);
begin

begin
select distinct replace(listagg(missingFields,'')WITHIN GROUP (ORDER BY missingFields),', ;',CHR(10)) INTO IS_VALIDSTEP
 FROM (
 select * from (
--WHEN 'Density' THEN
  select distinct case when dilutionfactor is null or t.result is null or CALCULATIONRES is null then '[' || t.selftest_id || '] Density: ' ||
   nvl2(dilutionfactor,null,'Dilution Factor, ')|| nvl2(t.result,null,'Measured value, ')|| nvl2(CALCULATIONRES,null,'Result, ')||';' end  missingFields
                  from fg_s_selftest_all_v t
                  where t.STEP_ID = stepId
                  and UPPER(nvl(t.SELFTESTSTATUSNAME,'NA')) <> 'CANCELLED'
                  and t.SELFTESTTYPENAME  = 'Density'
                  union all
--WHEN 'pH' THEN
  select distinct case when PHRESULTS is null then '[' || t.selftest_id || '] pH:' ||
   nvl2(PHRESULTS,null,'pHResults, ')||';' end  missingFields
                  from fg_s_selftest_all_v t
                  where t.STEP_ID = stepId
                  and UPPER(nvl(t.SELFTESTSTATUSNAME,'NA')) <> 'CANCELLED'
                  and t.SELFTESTTYPENAME  = 'pH'
                  union all
--WHEN 'Foaming' THEN
  select distinct case when WATERTYPE_ID is null or t.resultZero is null or resultMinute is null or temperature is null then '[' || t.selftest_id || '] Foaming: ' ||
   nvl2(WATERTYPE_ID,null,'Water Type, ')||nvl2(resultZero,null,'Result (zero point), ')|| nvl2(t.resultMinute,null,'Result (1 minute), ')|| nvl2(temperature,null,'Temperature, ')||';' end  missingFields
                  from fg_s_selftest_all_v t
                  where t.STEP_ID = stepId
                  and UPPER(nvl(t.SELFTESTSTATUSNAME,'NA')) <> 'CANCELLED'
                  and t.SELFTESTTYPENAME  = 'Foaming'
                 union all
--WHEN 'Flash Point' THEN
  select distinct case when FLASHPOINTTEMPERATURE is null then '[' || t.selftest_id || '] Flash Point: ' ||
   nvl2(FLASHPOINTTEMPERATURE,null,'Flash point temperature, ')||';' end  missingFields
                  from fg_s_selftest_all_v t
                  where t.STEP_ID = stepId
                  and UPPER(nvl(t.SELFTESTSTATUSNAME,'NA')) <> 'CANCELLED'
                  and t.SELFTESTTYPENAME  = 'Flash Point'
                  union all
--WHEN 'Wet Sieve' THEN
  select distinct case when WATERTYPE_ID is null or WetSieve is null then '[' || t.selftest_id || '] Wet Sieve: ' ||
   nvl2(WATERTYPE_ID,null,'Water Type, ')|| nvl2(WetSieve,null,'Wet Sieve, ')||';' end  missingFields
                  from fg_s_selftest_all_v t
                  where t.STEP_ID = stepId
                  and UPPER(nvl(t.SELFTESTSTATUSNAME,'NA')) <> 'CANCELLED'
                  and t.SELFTESTTYPENAME  = 'Wet Sieve'
                  union all
--WHEN 'Pourability' THEN
  select distinct case when startTime is null or finishTime is null or cylinderWeight is null or weightBStorage is null or weightAStorage is null or weightARinsing is null or resultAWashing is null or resultAFeeding is null then '[' || t.selftest_id || '] Pourability: ' ||
   nvl2(startTime,null,'start Time, ')||nvl2(finishTime,null,'finish Time, ')|| nvl2(t.cylinderWeight,null,'Cylinder Weight, ')|| nvl2(weightBStorage,null,'Weight before storage, ')
   ||nvl2(weightAStorage,null,'Weight after storage, ')|| nvl2(t.weightARinsing,null,'Weight after rinsing, ')|| nvl2(resultAWashing,null,'Result after washing, ')||nvl2(resultAFeeding,null,'Result after feeding, ')||';' end  missingFields
                  from fg_s_selftest_all_v t
                  where t.STEP_ID = stepId
                  and UPPER(nvl(t.SELFTESTSTATUSNAME,'NA')) <> 'CANCELLED'
                  and t.SELFTESTTYPENAME  = 'Pourability'
                  union all
--WHEN 'Particle Size' THEN
  select distinct case when INSTRUMENTTYPE_ID is null then '[' || t.selftest_id || '] Particle Size: ' ||
   nvl2(INSTRUMENTTYPE_ID,null,'Instrument Type, ')||';' end  missingFields
                  from fg_s_selftest_all_v t
                  where t.STEP_ID = stepId
                  and UPPER(nvl(t.SELFTESTSTATUSNAME,'NA')) <> 'CANCELLED'
                  and t.SELFTESTTYPENAME  = 'Particle Size'
                  union all
--WHEN 'Viscosity' THEN-
  select distinct case when SPINDLE is null or RPM is null or t.RESULT is null then '[' || t.selftest_id || '] Viscosity: ' ||
   nvl2(SPINDLE,null,'Spindle, ')|| nvl2(t.RPM,null,'RPM, ')|| nvl2(t.RESULT,null,'Result, ')||';' end  missingFields
                  from fg_s_selftest_all_v t
                  where t.STEP_ID = stepId
                  and UPPER(nvl(t.SELFTESTSTATUSNAME,'NA')) <> 'CANCELLED'
                  and t.SELFTESTTYPENAME  = 'Viscosity'
                  union all
--WHEN 'Cold Test' THEN
  select distinct case when CRYSTAL is null or Freezing is null or COLDTESTRESULTS is null or CRYSTALLIZATION is null then '[' || t.selftest_id || '] Cold Test: ' ||
   nvl2(CRYSTAL,null,'Crystal, ')||nvl2(FREEZING,null,'Freezing, ')|| nvl2(t.COLDTESTRESULTS,null,'coldTestResults, ')|| nvl2(CRYSTALLIZATION,null,'Crystallization, ')||';' end  missingFields
                  from fg_s_selftest_all_v t
                  where t.STEP_ID = stepId
                  and UPPER(nvl(t.SELFTESTSTATUSNAME,'NA')) <> 'CANCELLED'
                  and t.SELFTESTTYPENAME  = 'Cold Test'
                  union all
--WHEN 'Emulsion Stability' THEN
  select distinct case when INITIALEMULSIFICATION is null or CONCENTRATION is null or STABILITYRESULTS is null then '[' || t.selftest_id || '] Emulsion Stability: '  ||
   nvl2(INITIALEMULSIFICATION,null,'Initial Emulsification, ')|| nvl2(t.CONCENTRATION,null,'Percentage of Use, ')|| nvl2(t.STABILITYRESULTS,null,'StabilityResults, ') ||';' end  missingFields
                  from fg_s_selftest_all_v t
                  where t.STEP_ID = stepId
                  and UPPER(nvl(t.SELFTESTSTATUSNAME,'NA')) <> 'CANCELLED'
                  and t.SELFTESTTYPENAME  = 'Emulsion Stability'
                  )
 where missingFields is not null
 and rownum <= 15);

 exception
   when others then IS_VALIDSTEP:= 'various fields found';
   end;

Return IS_VALIDSTEP;
     end;

function GET_OOS_SMARTICON_OBJ(specificationId_in number,resultvalue_in varchar, resultuom_id_in varchar, subproject_id_in varchar) return varchar2 as
  specificationName varchar2(400);
  specificationCriteria varchar2(400);
  smartIconObj varchar2(400);
  is_OOS varchar(100);
begin
  if specificationId_in is null then
    return '';
  end if;
  select distinct t.CRITERIA into specificationCriteria--description
  from fg_s_specificationref_all_v t
  where t.SPECIFICATIONREF_ID = specificationId_in
  and t.ACTIVE=1
  and t.SESSIONID is null;

  select distinct t.SPECIFICATIONSNAME into specificationName--spec name
  from fg_s_specificationref_all_v t
  where t.SPECIFICATIONREF_ID = specificationId_in
  and t.ACTIVE=1
  and t.SESSIONID is null;

  select fg_get_result_is_oos(specification_name_in => specificationName,
                                      resultvalue_in => resultvalue_in,
                                      resultuom_id_in => resultuom_id_in,
                                      subproject_id_in => subproject_id_in) into is_OOS from dual;

  smartIconObj := '{"displayName":"' || '' || '" ,"icon":"' ||
                  case is_OOS
                    when '1' then 'fa fa-warning-red'
                    else ''
                   end
                    || '", "tooltip":"'||''||'", "description":"'||specificationCriteria||'"}';
  return smartIconObj;
end;



function REFRESH_DATA_TABLES(formType_in varchar, pivot_table_in varchar, formCode_in varchar default null,
                             contextType_in varchar default null, eventContextCode_in varchar default null,
                             auditTrailChangeType_in varchar default null) return number as
  tableFormName varchar2(500);
  is_path number;
  sql_path varchar2(32767);
  formCodeEntity varchar2(100);
  formTypeParam varchar2(100);
begin
  DBMS_OUTPUT.put_line('START...');

  if upper(formType_in) = 'MAINTENANCE' then
    -- **** MAINTENANCE
    if   /*upper(pivot_table_in) = 'FG_S_GROUPSCREW_PIVOT' or
         upper(pivot_table_in) = 'FG_S_USERSCREW_PIVOT' or*/
         upper(pivot_table_in) = 'FG_S_GROUP_PIVOT' or
         upper(pivot_table_in) = 'FG_S_USER_PIVOT' or
         --...
         upper(pivot_table_in) = 'FG_S_PERMISSIONSCHEME_PIVOT' or
         upper(pivot_table_in) = 'FG_S_SITE_PIVOT' or
         upper(pivot_table_in) = 'FG_S_UNITS_PIVOT' or
         upper(pivot_table_in) = 'FG_S_LABORATORY_PIVOT'
    then
      dbms_mview.refresh('FG_I_USERS_GROUP_SUMMARYDIS_MV');
      dbms_mview.refresh('FG_I_USERSGROUP_SUMMARYLIST_MV');
      dbms_mview.refresh('FG_S_PERMISSIONSREF_INF_MV');
      dbms_mview.refresh('FG_S_PERMISSIONSREF_INFA_MV');
    end if;
  else
    -- **** NOT MAINTENANCE
    if   upper(pivot_table_in) = 'FG_S_GROUPSCREW_PIVOT' or
         upper(pivot_table_in) = 'FG_S_USERSCREW_PIVOT' or
         upper(pivot_table_in) = 'FG_S_GROUP_PIVOT' or
         upper(pivot_table_in) = 'FG_S_USER_PIVOT'
    then
         dbms_mview.refresh('FG_I_USERS_GROUP_SUMMARYDIS_MV');
         dbms_mview.refresh('FG_I_USERSGROUP_SUMMARYLIST_MV');
    end if;
  end if;



  return 1;
end;

function CREW_UNTRAINED(materialId_in number, experimentId_in number,lastValues varchar) return varchar2 as
      userList varchar2(2000);
      toReturn varchar2(2000);
  begin
   -- userList:='';
  select listagg(username,',') WITHIN GROUP (ORDER BY username) into userList FROM (
       --userse crew + groups
          select t.user_id,t.username --as USERID
          from FG_I_USERS_GROUP_SUMMARY_V t
          where t.parentid = experimentId_in
          MINUS
          -- users trained
          select pt.USERID,u.UserName
          from fg_s_training_all_v pt,fg_s_user_v u
          where 1=1
          and pt.SESSIONID is null
          and pt.PARENTID = materialId_in
          and pt.USERID = u.user_id
      );
      if(userList is not null)
      then
        toReturn:=lastValues||materialId_in||'<user_list>'||userList||'<end>';
      else
        toReturn:=lastValues;end if;
     return toReturn;
  end;

function get_inventory_unfamiliar_list(experimentId_in number,stepId_in number default -1,formCode_in varchar default null,materialList_in varchar default null) return varchar2 as
     -- familiar number;
      toReturn varchar2(2000);
  begin
    if materialList_in is not null then
      execute immediate 'insert into material_temp_data select invitemmaterial_id from fg_s_invitemmaterial_v where formid in ('||materialList_in||')';
      end if;
      --organic
      if (lower(formCode_in) = 'experiment' or lower(formCode_in) = 'step' or lower(formCode_in) = 'experimentcp' )
      THEN
      for mat in (
        select distinct * from(
        select mat.invitemmaterial_id
        from fg_s_materialref_all_v mat
        ,  fg_s_experiment_v expr
        ,  FG_S_INVITEMMATERIAL_V invm
        WHERE mat.EXPERIMENT_ID= expr.EXPERIMENT_ID and mat.SESSIONID is null and nvl(mat.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
        and invm.invitemmaterial_id = mat.invitemmaterial_id and invm.SESSIONID is null and nvl(invm.ACTIVE,'1') = '1'
        and (mat.PARENTID = stepId_in or mat.STEPSTATUSNAME not in ('Planned','Cancelled'))
        union all
         select inst.inviteminstrument_id
        from fg_s_instrumentref_all_v inst
        ,  fg_s_experiment_v expr
        WHERE inst.EXPERIMENT_ID= expr.EXPERIMENT_ID and inst.SESSIONID is null and nvl(inst.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
        and (inst.PARENTID = stepId_in or inst.PARENTSTATUSNAME not in ('Planned', 'Cancelled'))
         and inst.statusname <> 'Disabled'
         union all
          select inst.inviteminstrument_id
        from fg_s_instrumentref_all_v inst
        ,  fg_s_experiment_v expr
        WHERE inst.PARENTID= expr.EXPERIMENT_ID and inst.SESSIONID is null and nvl(inst.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
        and inst.statusname <> 'Disabled'
        union all
        select invitemmaterial_id from material_temp_data)

      )
      loop
        select CREW_UNTRAINED(mat.invitemmaterial_id,experimentId_in,toReturn) into toReturn from dual;

      end loop;
       END IF;

      --for analytical experiment
      if (lower(formCode_in) = 'experimentan')
        THEN
      for c in (
        select distinct * from(
        select c.MATERIALID
        from fg_s_component_all_v c
        ,  fg_s_experiment_v expr
        WHERE c.PARENTID= expr.EXPERIMENT_ID and c.SESSIONID is null and nvl(c.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
        union all
        select to_char(col.INVITEMCOLUMN_ID)
        from fg_s_columnselect_all_v col
        ,  fg_s_experiment_all_v expr
        WHERE col.PARENTID= expr.EXPERIMENT_ID and col.SESSIONID is null and nvl(col.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
        union all
        select b.invitemmaterial_id
        from fg_s_batchselect_all_v b
        ,  fg_s_experiment_v expr
        WHERE b.PARENTID= expr.EXPERIMENT_ID and b.SESSIONID is null and nvl(b.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
        union all
        select inst.inviteminstrument_id
        from fg_s_instrumentref_all_v inst
        ,  fg_s_experiment_all_v expr
        WHERE inst.PARENTID= expr.EXPERIMENT_ID and inst.SESSIONID is null and nvl(inst.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
        and inst.statusname <> 'Disabled'
        union all
        select invitemmaterial_id from material_temp_data )
      )
      loop
          select CREW_UNTRAINED(c.MATERIALID,experimentId_in,toReturn) into toReturn from dual;
      end loop;
      END IF;

      --for formulation experiment
      if(lower(formCode_in) = 'experimentfor' or lower(formCode_in) = 'stepfr')
      THEN
      for fr in (
        select distinct * from(
         select  to_char(t.INVITEMMATERIAL_ID)  as invitemmaterial_id
        from FG_S_INVITEMMATERIAL_ALL_V t,
             fg_i_Composition_exp_step_v MR
        WHERE T.ACTIVE = 1
        AND   T.INVITEMMATERIAL_ID = MR.INVITEMMATERIAL_ID
        and 1=1
        and mr.EXPERIMENT_ID = experimentId_in
        and mr.sessionId is null
        union all
        select inst.inviteminstrument_id
        from fg_s_instrumentref_all_v inst
        WHERE inst.parentid= experimentId_in and inst.SESSIONID is null and nvl(inst.ACTIVE,'1') = '1'
        and inst.statusname <> 'Disabled'
        /*select to_char(fr.invitemmaterial_id) as invitemmaterial_id
        from fg_s_formulantref_all_v fr
        ,  fg_s_experiment_v expr
        WHERE fr.EXPERIMENT_ID= expr.EXPERIMENT_ID and fr.SESSIONID is null and nvl(fr.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
        and (fr.PARENTID = stepId_in or fr.STEPSTATUSNAME not in ('Planned', 'Cancelled'))
        union all
        select mat.invitemmaterial_id
        from fg_s_materialref_all_v mat
        ,  fg_s_experiment_v expr
        ,  FG_S_INVITEMMATERIAL_V invm
        WHERE mat.EXPERIMENT_ID= expr.EXPERIMENT_ID and mat.SESSIONID is null and nvl(mat.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
        and invm.invitemmaterial_id = mat.invitemmaterial_id and invm.SESSIONID is null and nvl(invm.ACTIVE,'1') = '1'
        and (mat.PARENTID = stepId_in or mat.STEPSTATUSNAME not in ('Planned','Cancelled'))
        union all
        select b.invitemmaterial_id
        from fg_s_batchselect_all_v b
        ,  fg_s_experiment_v expr
        WHERE b.PARENTID= expr.EXPERIMENT_ID and b.SESSIONID is null and nvl(b.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
        union all
        select inst.inviteminstrument_id
        from fg_s_instrumentref_all_v inst
        ,  fg_s_experiment_v expr
        WHERE inst.EXPERIMENT_ID= expr.EXPERIMENT_ID and inst.SESSIONID is null and nvl(inst.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
        and (inst.PARENTID = stepId_in or inst.PARENTSTATUSNAME not in ('Planned', 'Cancelled'))
         and inst.statusname <> 'Disabled'
        union all
        select invitemmaterial_id from material_temp_data*/)
      )
      loop
          select CREW_UNTRAINED(fr.invitemmaterial_id,experimentId_in,toReturn) into toReturn from dual;
      end loop;
      END IF;

      --for parametric experiment
      if lower(formCode_in) like 'experimentpr%'
        THEN
      for expr in (
        select distinct * from(
        select expr.material_id as "MATERIAL_ID"
        from fg_s_experimentprcr_all_v expr
        where expr.EXPERIMENT_ID = experimentId_in
        and expr.material_id is not null and nvl(expr.ACTIVE,'1') = '1'
        union all
        select expr.BMATERIALID
        from fg_s_experimentprbt_all_v expr
        where expr.EXPERIMENT_ID = experimentId_in
        and expr.bmaterialid is not null and nvl(expr.ACTIVE,'1') = '1'
        union all
        select expr.VSMATERIALID
        from fg_s_experimentprvs_all_v expr
        where expr.EXPERIMENT_ID = experimentId_in
        and expr.vsmaterialid is not null and nvl(expr.ACTIVE,'1') = '1'
        union all
        select inst.inviteminstrument_id
        from fg_s_instrumentref_all_v inst
        ,  fg_s_experiment_all_v expr
        WHERE inst.PARENTID= expr.EXPERIMENT_ID and inst.SESSIONID is null and nvl(inst.ACTIVE,'1') = '1'
        and expr.EXPERIMENT_ID = experimentId_in
        and inst.statusname <> 'Disabled'
        union all
        select invitemmaterial_id from material_temp_data)
      )
      loop
         select CREW_UNTRAINED(expr.MATERIAL_ID,experimentId_in,toReturn) into toReturn from dual;
      end loop;
      END IF;
  return toReturn;
end;

--create job script example
/*  BEGIN
dbms_scheduler.create_job
(
    job_name        => 'UPDATE_DATA_5MIN',
    job_type        => 'STORED_PROCEDURE',
    job_action      => 'FG_ADAMA.REFRESH_DATA_TABLES_JOB_5MIN',
    enabled         => true,
    comments        => 'Transform Skyline data',
    start_date      => to_date('18/10/2010 11:58:00','dd/MM/yyyy HH24:MI:SS'),
    repeat_interval => 'freq=MINUTELY;interval=5'
);
END;*/
  FUNCTION INIT_STEPFR_DATA(EXPERIMENT_ID_IN VARCHAR, USER_ID_IN VARCHAR, DB_DATE_FORMAT_IN varchar default 'DD/MM/YYYY') RETURN VARCHAR AS
     newstepId_ varchar(500);
     expStatusName_ varchar(500);
     stepStatusId_ varchar(500);
     stepname_ varchar(500);
     stepNameCounter_ number;
     number_of_steps number;
     defaultProductMatProtocolType_ varchar(500);
     defaultProductMatRowType_ varchar(500);
    BEGIN
      -- ma
      select distinct lower(t.EXPERIMENTSTATUSNAME) into expStatusName_ from fg_s_experiment_all_v t where t.EXPERIMENT_ID = EXPERIMENT_ID_IN;
      if expStatusName_ = 'active' or expStatusName_ = 'planned' then
        select t.stepstatus_id into stepStatusId_ from fg_s_stepstatus_v t where lower(t.StepStatusName) = expStatusName_;
      else
        raise_application_error( -20001, 'Error in creating step - no experiment status (planned or active found)!');
      end if;

      newstepId_:=FG_GET_STRUCT_FORM_ID('StepFr');

      select 'STEP ' || t.STEPSEQUENCE into stepname_
      FROM fg_authen_stepFr_v t
      WHERE t.EXPERIMENT_ID = EXPERIMENT_ID_IN
      AND rownum < 2;

      -- check if name exists
      select count(*) into stepNameCounter_
      from fg_s_step_v t
      where upper(trim(t.StepName)) = upper(trim(stepname_))
      and t.EXPERIMENT_ID = EXPERIMENT_ID_IN;

      -- make unique name
      if stepNameCounter_ > 0 then
        stepname_ := stepname_ || '(' || newstepId_ || ' Please change step name)';
      end if;


      insert into fg_s_step_pivot (
           TIMESTAMP,
           CREATION_DATE,
           CHANGE_BY,
           CREATED_BY,
           ACTIVE,
           FORMCODE_ENTITY,
           FORMCODE,
           SELFTESTINSTRUCTIONS,
           ACTUALSTARTDATE,
           --COMPVOL_ID,
           CREATOR_ID,
           STEPNAME,
           CREATIONDATETIME,
           FORMID,
           SUBSUBPROJECT_ID,
           SUBPROJECT_ID,
           PROJECT_ID,
           EXPERIMENTVERSION,
           EXPERIMENT_ID,
           FORMNUMBERID,
           STATUS_ID
    )
    SELECT
           SYSDATE AS TIMESTAMP,
           SYSDATE CREATION_DATE,
           USER_ID_IN CHANGE_BY,
           USER_ID_IN CREATION_BY,
           1 ACTIVE,
           'Step' FROMCODE_ENTITY,
           'StepFr' FORMCODE,
           NULL AS SELFTESTINSTRUCTIONS,
           DECODE(expStatusName_,'active',to_char(sysdate,DB_DATE_FORMAT_IN),NULL) AS ACTUALSTARTDATE,
           --COMPVOL_ID,
           USER_ID_IN CREATOR_ID,
           stepname_ STEPNAME,
           to_char(sysdate,DB_DATE_FORMAT_IN) AS CREATIONDATETIME,
           newstepId_ as FORMID,
           SUBSUBPROJECT_ID,
           SUBPROJECT_ID,
           PROJECT_ID,
           EXPERIMENTVERSION,
           EXPERIMENT_ID,
           STEPSEQUENCE AS FORMNUMBERID,
           stepStatusId_
    FROM fg_authen_stepFr_v t
    WHERE t.EXPERIMENT_ID = EXPERIMENT_ID_IN
    AND rownum < 2;

    select count(*) into number_of_steps from fg_s_step_pivot where experiment_id = EXPERIMENT_ID_IN;

    -- raise error if no insert
    if number_of_steps = 0 then
       raise_application_error( -20001, 'Error in step creation!');
    end if;

    --insert premix material for first step else formulation
    if number_of_steps = 1 then
      defaultProductMatProtocolType_ := 'premix';
      defaultProductMatRowType_ := 'Premix Material';
    else
      defaultProductMatProtocolType_ := 'formulation';
      defaultProductMatRowType_ := 'Formulation';
    end if;

   INSERT INTO FG_S_composition_PIVOT ( TIMESTAMP,
                                         CHANGE_BY,
                                         ACTIVE,
                                         FORMID,
                                         PARENTID,
                                         FORMCODE,
                                         FORMCODE_ENTITY,
                                         CREATED_BY,
                                         CREATION_DATE,
                                         ROWTYPE,
                                         ORIGIN,
                                         TABLETYPE,
                                         INVITEMMATERIAL_ID)
    SELECT DISTINCT sysdate as TIMESTAMP,
                    USER_ID_IN as CHANGE_BY,
                    1 as ACTIVE,
                    to_char(FG_GET_STRUCT_FORM_ID('Composition')) as FORMID,
                    s.step_id PARENTID,
                    'Composition' AS FORMCODE,
                    'Composition' AS FORMCODE_ENTITY,
                    USER_ID_IN AS CREATED_BY,
                    SYSDATE AS CREATION_DATE,
                    defaultProductMatRowType_ ROWTYPE,
                    NULL ORIGIN,
                    'productComposition' AS TABLETYPE,
                    T.INVITEMMATERIAL_ID AS INVITEMMATERIAL_ID
    FROM fg_s_invitemmaterial_v t,
         fg_s_stepfr_all_v s,
         FG_S_MATERIALSTATUS_V ms
    WHERE s.STEP_ID = newstepId_
    AND t.SOURCEPROJECTID=s.project_id
    AND LOWER(T.MATERIALPROTOCOLTYPE) = defaultProductMatProtocolType_
    AND T.STATUS_ID = ms.materialstatus_id
    and lower(ms.MaterialStatusName) <> 'cancelled';


    RETURN newstepId_;
  END;

    FUNCTION INIT_STEPCP_DATA(EXPERIMENT_ID_IN VARCHAR, USER_ID_IN VARCHAR, DB_DATE_FORMAT_IN varchar default 'DD/MM/YYYY') RETURN VARCHAR AS
     newstepId_ varchar(500);
     expStatusName_ varchar(500);
     stepStatusId_ varchar(500);
    BEGIN
      -- ma
      select distinct lower(t.EXPERIMENTSTATUSNAME) into expStatusName_ from fg_s_experiment_all_v t where t.EXPERIMENT_ID = EXPERIMENT_ID_IN;
      if expStatusName_ = 'active' or expStatusName_ = 'planned' then
        select t.stepstatus_id into stepStatusId_ from fg_s_stepstatus_v t where lower(t.StepStatusName) = expStatusName_;
      else
        raise_application_error( -20001, 'Error in creating step - no experiment status (planned or active found)!');
      end if;

      --as in init_step_data (see function below) +  PREPARATION_RUN as 'Preparation' by default
      newstepId_:=FG_GET_STRUCT_FORM_ID('Step');
        insert into fg_s_step_pivot (
           TIMESTAMP,
           CREATION_DATE,
           CHANGE_BY,
           CREATED_BY,
           ACTIVE,
           FORMCODE_ENTITY,
           FORMCODE,
           SELFTESTINSTRUCTIONS,
           ACTUALSTARTDATE,
           --COMPVOL_ID,
           CREATOR_ID,
           STEPNAME,
           CREATIONDATETIME,
           FORMID,
           SUBSUBPROJECT_ID,
           SUBPROJECT_ID,
           PROJECT_ID,
           EXPERIMENTVERSION,
           EXPERIMENT_ID,
           FORMNUMBERID,
           STATUS_ID,
           PLANNED_ACTUAL_STATUS,
           PREPARATION_RUN
    )
    SELECT
           SYSDATE AS TIMESTAMP,
           SYSDATE CREATION_DATE,
           USER_ID_IN CHANGE_BY,
           USER_ID_IN CREATION_BY,
           1 ACTIVE,
           'Step' FROMCODE_ENTITY,
           'Step' FORMCODE,
           SELFTESTINSTRUCTIONS,
           DECODE(expStatusName_,'active',to_char(sysdate,DB_DATE_FORMAT_IN),NULL) AS ACTUALSTARTDATE,
           --COMPVOL_ID,
           USER_ID_IN CREATOR_ID,
           'STEP 01' STEPNAME,
           to_char(sysdate,DB_DATE_FORMAT_IN) AS CREATIONDATETIME,
           newstepId_ as FORMID,
           SUBSUBPROJECT_ID,
           SUBPROJECT_ID,
           PROJECT_ID,
           EXPERIMENTVERSION,
           EXPERIMENT_ID,
           '01' AS FORMNUMBERID,
           stepStatusId_,
           'Planned',
           'Preparation'
    FROM fg_authen_step_v t
    WHERE t.EXPERIMENT_ID = EXPERIMENT_ID_IN
    AND rownum < 2;

    RETURN newstepId_;
  END;

    FUNCTION INIT_STEP_DATA(EXPERIMENT_ID_IN VARCHAR, USER_ID_IN VARCHAR, DB_DATE_FORMAT_IN varchar default 'DD/MM/YYYY') RETURN VARCHAR AS
     newstepId_ varchar(500);
     expStatusName_ varchar(500);
     stepStatusId_ varchar(500);
    BEGIN
      -- ma
      select distinct lower(t.EXPERIMENTSTATUSNAME) into expStatusName_ from fg_s_experiment_all_v t where t.EXPERIMENT_ID = EXPERIMENT_ID_IN;
      if expStatusName_ = 'active' or expStatusName_ = 'planned' then
        select t.stepstatus_id into stepStatusId_ from fg_s_stepstatus_v t where lower(t.StepStatusName) = expStatusName_;
      else
        raise_application_error( -20001, 'Error in creating step - no experiment status (planned or active found)!');
      end if;

      -- the defarence between this function and creation by the user is in the fields
      -- (after another save it is look the same after the save)
     /* select formid, SELFTESTINSTRUCTIONS,CHKCHARACTERMASSBALANCE,REACTORVOL_ID,
      REACTIONTABLEDATA,COMPVOL_ID,SUMMARY,REACTORVOLUMEUOM_ID,COMPVOLUMERATE_UOM_ID,
      SUMMARYOM_ID,CHEMDOODLEACT,CONVERSIONUOM_ID,SAMPLINGTIMEUOM_ID,AIM,CHEMICALYIELDUOM_ID,
      VOLFACTOR,CONCLUSSION,ISOLATEDYIELDUOM_ID,LIMITINGMOLEUOM_ID,CONVERSION,WEBIXMASSBALANCETABLE,
      CHKMANUALUPDATE,MASSUOM,CHEMICALYIELD,
      ISOLATEDYIELD,RETENTIONTIMEUOM_ID,COMPMASSRATE_UOM_ID
      FROM FG_S_STEP_PIVOT t WHERE EXPERIMENT_ID = <experiment id>*/


      newstepId_:=FG_GET_STRUCT_FORM_ID('Step');
        insert into fg_s_step_pivot (
           TIMESTAMP,
           CREATION_DATE,
           CHANGE_BY,
           CREATED_BY,
           ACTIVE,
           FORMCODE_ENTITY,
           FORMCODE,
           SELFTESTINSTRUCTIONS,
           ACTUALSTARTDATE,
           --COMPVOL_ID,
           CREATOR_ID,
           STEPNAME,
           CREATIONDATETIME,
           FORMID,
           SUBSUBPROJECT_ID,
           SUBPROJECT_ID,
           PROJECT_ID,
           EXPERIMENTVERSION,
           EXPERIMENT_ID,
           FORMNUMBERID,
           STATUS_ID,
           PLANNED_ACTUAL_STATUS
    )
    SELECT
           SYSDATE AS TIMESTAMP,
           SYSDATE CREATION_DATE,
           USER_ID_IN CHANGE_BY,
           USER_ID_IN CREATION_BY,
           1 ACTIVE,
           'Step' FROMCODE_ENTITY,
           'Step' FORMCODE,
           SELFTESTINSTRUCTIONS,
           DECODE(expStatusName_,'active',to_char(sysdate,DB_DATE_FORMAT_IN),NULL) AS ACTUALSTARTDATE,
           --COMPVOL_ID,
           USER_ID_IN CREATOR_ID,
           'STEP 01' STEPNAME,
           to_char(sysdate,DB_DATE_FORMAT_IN) AS CREATIONDATETIME,
           newstepId_ as FORMID,
           SUBSUBPROJECT_ID,
           SUBPROJECT_ID,
           PROJECT_ID,
           EXPERIMENTVERSION,
           EXPERIMENT_ID,
           '01' AS FORMNUMBERID,
           stepStatusId_,
           'Planned'
    FROM fg_authen_step_v t
    WHERE t.EXPERIMENT_ID = EXPERIMENT_ID_IN
    AND rownum < 2;

    RETURN newstepId_;
  END;

  function GET_UPDATE_P_EXPREPORT_DATA(statekey_in varchar, resulttype_in VARCHAR,
                                           characteristicMassBalan_in VARCHAR,
                                           imputityMatIds_in VARCHAR,
                                           sampleComments_in VARCHAR,
                                           sampleCreator_in VARCHAR,
                                           sampleAmount_in VARCHAR,
                                           sampleIds_in clob) -- to_clob("," || sampleIdsCsv || ',')
                                           return varchar2 as
  begin

    /*ACRETE OR REPLACE VIEW FG_P_EXPREPORT_RESULT_V1 AS
   --FG_P_EXPREPORT_DATA_TMP -- FG_P_EXPREPORT_DATA_TMP1  -- sbPivotSql.append("SELECT distinct " + stateKey + " as stateKey , order_, order2,  result_SMARTPIVOT \n FROM FG_P_EXPREPORT_RESULT_V \n where SAMPLE_ID in (" + (sampleIds.isEmpty()?"-1":sampleIds) + ") ");
      */  --sbSelectSql.append(",'SELECT result_SMARTPIVOT FROM FG_P_EXPREPORT_DATA_TMP where statekey=''" + stateKey + "'' order by order_, order2' AS RESULT_SMARTPIVOTSQL\n" );
--SELECT result_SMARTPIVOT FROM FG_P_EXPREPORT_DATA_TMP where statekey=''" + stateKey + "'' order by order_, order2' AS RESULT_SMARTPIVOTSQL

  insert into FG_P_EXPREPORT_DATA_TMP (statekey, order_, order2, result_SMARTPIVOT)
  SELECT distinct statekey_in as statekey, "ORDER_","ORDER2","RESULT_SMARTPIVOT" from (
    -----------------------
    --sample name (link)
    -----------------------
    select distinct to_char(t.SAMPLE_ID) as SAMPLE_ID,
      t.EXPERIMENT_ID,
      10000 as order_,
      CAST(NULL AS varchar2(500)) as order2,-- assay results appear first
      '{pivotkey:"'|| t.SAMPLE_ID||'_'||t.EXPERIMENT_ID||'",pivotkeyname:"UNIQUEROW",'
      ||'column:"Sample #_SMARTLINK",'
      ||'val:'|| '{"displayName":"' || t.SAMPLENAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Sample' || '"  ,"formId":"' || t.SAMPLE_ID || '","tab":"' || '' || '" }'
      ||'}' as result_SMARTPIVOT
    from fg_s_sample_v t
    where DBMS_LOB.INSTR( sampleIds_in, ',' || t.sample_id || ',' ) > 0
    union all
     -----------------------
    --sample description
    -----------------------
    select distinct to_char(t.SAMPLE_ID) as SAMPLE_ID,
    t.EXPERIMENT_ID,
    10001 as order_,
    CAST(NULL AS varchar2(500)) as order2,-- assay results appear first
    '{pivotkey:"'|| t.SAMPLE_ID||'_'||t.EXPERIMENT_ID||'",pivotkeyname:"UNIQUEROW",'
    ||'column:"Sample Description",'
    ||'val:"'|| t.sampleDesc
    ||'"}' as result_SMARTPIVOT
   from fg_s_sample_v t
   where 1=1
   and  t.sampleDesc is not null
   and DBMS_LOB.INSTR( sampleIds_in, ',' || t.sample_id || ',' ) > 0
   union all
    -----------------------
    --sample amount
    -----------------------
    select distinct to_char(t.SAMPLE_ID) as SAMPLE_ID,
    t.EXPERIMENT_ID,
    10002 as order_,
    CAST(NULL AS varchar2(500)) as order2,-- assay results appear first
    '{pivotkey:"'|| t.SAMPLE_ID||'_'||t.EXPERIMENT_ID||'",pivotkeyname:"UNIQUEROW",'
    ||'column:"Sample Amount",'
    ||'val:"'|| t.AMMOUNT
    ||'"}' as result_SMARTPIVOT
   from fg_s_sample_v t
   where 1=1
   and  t.AMMOUNT is not null
   and sampleAmount_in = '1'
   and DBMS_LOB.INSTR( sampleIds_in, ',' || t.sample_id || ',' ) > 0
   union all
    -----------------------
    --sample comment (link) if sampleComments_in = 1
    -----------------------
    select distinct to_char(t.SAMPLE_ID) as SAMPLE_ID,
      t.EXPERIMENT_ID,
      10003 as order_,
      CAST(NULL AS varchar2(500)) as order2,-- assay results appear first
      '{pivotkey:"'|| t.SAMPLE_ID||'_'||t.EXPERIMENT_ID||'",pivotkeyname:"UNIQUEROW",'
      ||'column:"Sample Comment",'
      ||'val:"'||
       to_char(f.file_content_text)
      ||'"}' as result_SMARTPIVOT
    from fg_s_sample_v t, fg_richtext f
    where t.COMMENTSFORCOA = f.file_id(+)
    and DBMS_LOB.INSTR( sampleIds_in, ',' || t.sample_id || ',' ) > 0
    and sampleComments_in = '1'
    union all
   -----------------------
    --sample creator if sampleComments_in = 1
    -----------------------
    select distinct to_char(t.SAMPLE_ID) as SAMPLE_ID,
    t.EXPERIMENT_ID,
    10004 as order_,
    CAST(NULL AS varchar2(500)) as order2,-- assay results appear first
    '{pivotkey:"'|| t.SAMPLE_ID||'_'||t.EXPERIMENT_ID||'",pivotkeyname:"UNIQUEROW",'
    ||'column:"Sample Creator",'
    ||'val:"'|| u.UserName
    ||'"}' as result_SMARTPIVOT
   from fg_s_sample_v t, fg_s_user_v u
   where t.CREATOR_ID = u.user_id(+)
   and sampleCreator_in = '1'
   and DBMS_LOB.INSTR( sampleIds_in, ',' || t.sample_id || ',' ) > 0
   union all
   -----------------------
   --sample result
   -----------------------
   select t1.SAMPLE_ID,
         t1.EXPERIMENT_ID,
         10005 as order_,
         t1.name_ as order2,
         '{pivotkey:"'|| t1.SAMPLE_ID||'_'||t1.EXPERIMENT_ID||'",pivotkeyname:"UNIQUEROW",'
         ||'column:"'|| t1.name_ || '",'
         ||'val:"'|| t1.val_ ||'"}' as result_SMARTPIVOT
   from (
          --*************
          -- sampleComments_in
          --*************
          /*select t.SAMPLE_ID,
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
          and DBMS_LOB.INSTR( sampleIds_in, ',' || t.sample_id || ',' ) > 0
          union all*/
          --************
          -- selected IMPURITY
          --************
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
          and DBMS_LOB.INSTR( sampleIds_in, ',' || t.sample_id || ',' ) > 0
          and upper(t.RESULT_NAME) like '%IMPURITY%'
          --and instr(nvl(imputityMatIds_in,'-1') ,t.RESULT_MATERIAL_ID) > 0
          and instr(decode(imputityMatIds_in,'ALL',t.RESULT_MATERIAL_ID,null,'-1',imputityMatIds_in) ,t.RESULT_MATERIAL_ID) > 0
          union all
          --************
          -- reult type (not IMPURITY)
          --************
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
          and DBMS_LOB.INSTR( sampleIds_in, ',' || t.sample_id || ',' ) > 0
          and upper(t.RESULT_NAME) not like '%IMPURITY%'
          and instr(decode(resulttype_in,'ALL',t.RESULT_NAME,null,'-1',resulttype_in) ,t.RESULT_NAME) > 0
              ) t1 where t1.val_ is not null
  );
  return '1';

  end;

  function REFRESH_DATA_PERM_MAINTENANCE RETURN VARCHAR AS
  BEGIN
    dbms_mview.refresh('FG_S_PERMISSIONSREF_INF_MV'); --17082020 instr fix
    dbms_mview.refresh('FG_S_PERMISSIONSREF_INFA_MV'); --17082020 instr fix (use all_mv code)
    RETURN '1';
  END;

  procedure REFRESH_DATA_TABLES_JOB AS
    sysdateHolder date := sysdate;
  BEGIN
    DBMS_OUTPUT.put_line('START...');
    dbms_mview.refresh('FG_S_SAMPLESELECT_ALL_MV'); --17082020 instr fix
    dbms_mview.refresh('FG_S_SAMPLESELECT_EXP_MV'); --17082020 instr fix (use all_mv code)
    dbms_mview.refresh('FG_S_REQUESTSELECT_ALL_MV'); --17082020 instr fix
    dbms_mview.refresh('FG_S_EXPERIMENTSELECT_ALL_MV'); --17082020 instr fix
    dbms_mview.refresh('FG_I_REQUEST_DESTEXP_MV'); --17082020 IMPROVE INED VIEW (use all_mv code)
    --dbms_mview.refresh('FG_I_CONNECTION_REQUEST_EXPR_V'); IN REFRESH_DATA_TABLES_JOB_5MIN
    dbms_mview.refresh('FG_I_USERS_GROUP_SUMMARYDIS_MV');
    dbms_mview.refresh('FG_I_USERSGROUP_SUMMARYLIST_MV');
    --refresh in the code after UOM change (we make it also here to be on safe side)->
    dbms_mview.refresh('FG_I_MATREF_DEFAULT_VALUES_MV');
    UPDATE FG_SYS_PARAM T SET T.Last_Refresh_Mv = sysdateHolder;
    --update fg_results set sample_id=null where nvl(sample_id,'dummy')='null'; a patch that was added in the production until the next installation
     commit;
  END;

  procedure REFRESH_DATA_TABLES_JOB_5MIN AS
    sysdateHolder date := sysdate;
  BEGIN
    DBMS_OUTPUT.put_line('START...');

    --use fg_i_connection_request_expr_t as temp table because it is faster then using directly in the view
    truncate_it('FG_I_CONNECTION_REQUEST_EXPR_T');

    INSERT INTO FG_I_CONNECTION_REQUEST_EXPR_T
    SELECT * FROM FG_I_CONNECTION_REQUEST_EXPR_C;

    dbms_mview.refresh('FG_I_CONNECTION_REQUEST_EXPR_V'); --17082020 instr fix (use all_mv code)

    UPDATE FG_SYS_PARAM T SET T.LAST_REFRESH_MV_5MIN = sysdateHolder;
    --update fg_results set sample_id=null where nvl(sample_id,'dummy')='null'; a patch that was added in the production until the next installation
     commit;
  END;

end;
/
