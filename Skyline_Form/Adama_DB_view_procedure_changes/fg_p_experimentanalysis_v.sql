create or replace view fg_p_experimentanalysis_v as
select "EXPERIMENT_ID","ORDER_","STEP_ID","MATERIALREF_ID","TABLETYPE","RESULT_ID","RESULT_SMARTPIVOT","FORMNUMBERID" from
--Important comment: result_smartpivot is converted to clob since the concatenation string is rarely too long.
------------------- It is sufficient to cast to clob only a single component in the concatenation in order to get the whole ccolumn as a clob.
(
  ------------------------------------
  --experiment mass balance
  ------------------------------------
  select experiment_id,
  100 + (tab_index * 10) + name_order  as order_,
  '' as step_id,
  '' as materialref_id,
  '' as tabletype,
  '' as result_id,
  '' as formnumberid,
  '{pivotkey:"'|| experiment_id||'",pivotkeyname:"experiment_id"'||
  ',group:["Experiment"]'||
  ',column:["' || decode(name_,'chemicalYield','Chemical yield,%','chemicalYield2','Chemical yield,%','chemicalYield3','Chemical yield,%',
                              'isolatedYield','Isolated yield,%','isolatedYield2','Isolated yield,%','isolatedYield3','Isolated yield,%',
                              'conversion','Conversion,%','conversion2','Conversion,%','conversion3','Conversion,%',
                              'Summary,%')
  ||'"]'||
  ',val:["' ||to_clob( value_ )||'"]}' as result_SMARTPIVOT
  from (
          select distinct t1.experiment_id,
                 t1.name_,
                 t1.value_,
                 t1.tab_index,
                 t1.name_order
          from fg_i_expanalysis_exp_massbal_v t1--select te tab index of the characteristic mass ballance
          where t1.tab_index in
                 (select t2.tab_index
                  from   fg_i_expanalysis_exp_massbal_v t2
                  where  t2.name_ in('chkCharacterMassBalance','chkCharacterMassBalance2','chkCharacterMassBalance3')
                  and    t2.value_ = '1'
                  and t1.experiment_id = t2.experiment_id)
          and t1.name_ not in ('chkCharacterMassBalance','chkCharacterMassBalance2','chkCharacterMassBalance3')
  ) t
  union all--for each experiment only one of the two sections(above and following) is full with data(characteristic sample is single to the whole experiment)
  select experiment_id,
  100 as order_,
  '' as step_id,
  '' as materialref_id,
  '' as tabletype,
  '' as result_id,
  '' as formnumberid,
  '{pivotkey:"'|| experiment_id||'",pivotkeyname:"experiment_id"'||
        ',group:["Experiment","Experiment","Experiment","Experiment"]'||
        ',column:["Conversion,%","Chemical yield,%","Isolated yield,%","Summary,%"]'||
        ',val:["' || to_clob(t.CONVERSION) ||'","'||t.CHEMICALYIELD||'","'||t.ISOLATEDYIELD||'","'||t.SUMMARY||'"]}' as result_SMARTPIVOT
  from fg_s_step_v t
  where t.CHKCHARACTERMASSBALANCE = 1
  union all
  -----------------------------------
  --Experiment Parameters
  -----------------------------------
  select to_char(p.experiment_id),
  150 as order_,
  '' as step_id,
  '' as materialref_id,
  '' as tabletype,
  to_char(p.PARAMREF_ID) as result_id,--for the order of the parameters
  '' as FORMNUMBERID,
  '{pivotkey:"'|| p.experiment_id||'",pivotkeyname:"experiment_id"'||
        ',group:["Experiment '|| p.parametername ||'","Experiment '|| p.parametername ||'","Experiment '|| p.parametername ||'","Experiment '|| p.parametername ||'","Experiment '|| p.parametername ||'"]'||
        ',column:["{'|| p.parametername ||'}Parameter","{'|| p.parametername ||'}Sign","{'|| p.parametername ||'}Value 1","{'|| p.parametername ||'}Value 2","{'|| p.parametername ||'}Uom"]'||--["' || p.parametername  ||'"]'||
        ',val:["'|| to_clob(p.parametername) ||'","'||p.PARAMETERSCRITERIANAME||'","'||p.VAL1||'","'||p.VAL2||'","'||p.UOMNAME||'"]}' as result_SMARTPIVOT
  from fg_s_paramref_all_v p,
  fg_s_experiment_v e
  where p.PARENTID = e.experiment_id
  and p.ACTIVE='1'
  and p.SESSIONID is null
  and p.VAL1 is not null
  union all
  ------------------------------------
  --Experiment analytical data
  ------------------------------------
  select to_char(t.experiment_id),
  200 as order_,
  '' as step_id,
  '' as materialref_id,
  '' as tabletype,
  s.RESULT_ID,
  '' formnumberid,
  '{pivotkey:"'|| t.experiment_id||'",pivotkeyname:"experiment_id"'||
  ',group:["Experiment","Experiment","Experiment","Experiment"]'||
  ',column:["Characteristic Sample","Analysis Source","Analytical Experiment","Assay %"]'||
  ',val:["' || to_clob(smpl.SampleName) ||'","'||decode(s.result_test_name,'Organic','Self-test','Analytical')||'","'|| decode(s.result_test_name,'Analytical',e_dest.experimentname)||'","'||s.RESULT_VALUE||'"]}' as result_SMARTPIVOT
  from fg_s_experiment_v t,--fg_i_sampleresults_v_f s,
  --SAMPLERESULTS_VIEW s,  --
  fg_s_sample_v smpl,
  fg_i_selectedresults_v s,--main results only
  fg_s_experiment_v e_dest
  where t.CHARACTERIZEDSAMPLE = s.SAMPLE_ID(+)
  and t.CHARACTERIZEDSAMPLE = smpl.sample_id
  and instr(s.RESULT_NAME(+),'Assay')>0
  --and s.RESULT_NAME(+) = 'Assay'
  --and(s.SELFTEST_ID is not null or exists(select sample_id from fg_i_selectedresults_v sr where sr.RESULT_ID = s.result_id))
  --and s.result_id = sl.result_id--main results only
  and s.experiment_id = e_dest.experiment_id
  union all
  ------------------------------------
  --impurity Experiment
  ------------------------------------
  select experiment_id,
  300 as order_,
  '' as step_id,
  '' as materialref_id,
  '' as tabletype,
  result_id,
  '' as formnumberid,
  '{pivotkey:"'|| experiment_id||'",pivotkeyname:"experiment_id",group:["'|| invitemmaterialname||'"]'||
  ',column:["Impurity '||invitemmaterialname||',%"]'||--,"{Step '||FORMNUMBERID||'}Impurity concentration %"]'||
  ', val:["' || to_clob(result_value) ||'"]}' as result_SMARTPIVOT
  from (
      select distinct
             t1.experiment_id,
             t1.result_id,
             t1.invitemmaterialname,
             t1.result_value
      from fg_i_expanalysis_impurity_ex_v t1
      where t1.result_value is not null
  )
  union all
  -----------------------------------
  --Step general data
  -----------------------------------
  select t.EXPERIMENT_ID,
  400 as order_,
  to_char(t.step_id),
  '' as materialref_id,
  'A' as tabletype,--for the order of the sections in the step
  '' as result_id,
  t.FORMNUMBERID,
  '{pivotkey:"'||t.EXPERIMENT_ID||'",pivotkeyname:"experiment_id"'||
         ',group:["","Step'||t.FORMNUMBERID||'","Step'||t.FORMNUMBERID||'","Step'||t.FORMNUMBERID||'"]'||
         ',column:["Step '||t.FORMNUMBERID ||'","{Step '||t.FORMNUMBERID||'}Aim","{Step '||t.FORMNUMBERID||'}Description","{Step '||t.FORMNUMBERID||'}Conclusion"]'||
         ',val:["Step '||to_clob(t.FORMNUMBERID)||' '||t.STEPNAME||'","'||fg_get_richtext_display(t.AIM)||'","'||t.SHORTDESCRIPTION||'","'||fg_get_richtext_display(t.CONCLUSSION)||'"]}' as result_SMARTPIVOT
  from fg_s_step_v t
  union all
  ------------------------------------
  ---Step mass balance
  ------------------------------------
  select experiment_id,
  400 as order_,
  to_char(t.step_id) as step_id,
  '' as materialref_id,
  'B' as tabletype,--for the order of the sections in the step
  '' as result_id,
  t.formnumberid,
  '{pivotkey:"'|| experiment_id||'",pivotkeyname:"experiment_id"'||
  ',group:["Step'||t.FORMNUMBERID||'","Step'||t.FORMNUMBERID||'","Step'||t.FORMNUMBERID||'","Step'||t.FORMNUMBERID||'"]'||
  ',column:["{Step '||t.FORMNUMBERID||'}Conversion,%","{Step '||t.FORMNUMBERID||'}Chemical yield,%","{Step '||t.FORMNUMBERID||'}Isolated yield,%","{Step '||t.FORMNUMBERID||'}Summary,%"]'||
  ',val:["' || to_clob(t.CONVERSION) ||'","'||t.CHEMICALYIELD||'","'||t.ISOLATEDYIELD||'","'||t.SUMMARY||'"]}' as result_SMARTPIVOT
  from fg_s_step_v t
  union all
  ------------------------------------
  --Step Parameters
  ------------------------------------
  select to_char(p.experiment_id),
  400 as order_,
  to_char(p.step_id) as step_id,
  '' as materialref_id,
  'C' as tabletype,
  to_char(p.PARAMREF_ID) as result_id,
  s.FORMNUMBERID,
  '{pivotkey:"'|| p.experiment_id||'",pivotkeyname:"experiment_id"'||
  ',group:["Step '||s.FORMNUMBERID|| p.parametername ||'","Step '||s.FORMNUMBERID|| p.parametername ||'","Step '||s.FORMNUMBERID|| p.parametername ||'","Step '||s.FORMNUMBERID|| p.parametername ||'","Step '||s.FORMNUMBERID|| p.parametername ||'"]'||
  ',column:["{Step '||s.FORMNUMBERID|| p.parametername ||'}Parameter","{Step '||s.FORMNUMBERID|| p.parametername ||'}Sign","{Step '||s.FORMNUMBERID|| p.parametername ||'}Value 1","{Step '||s.FORMNUMBERID|| p.parametername ||'}Value 2","{Step '||s.FORMNUMBERID|| p.parametername ||'}Uom"]'||
  ',val:["'|| to_clob(p.parametername) ||'","'||p.PARAMETERSCRITERIANAME||'","'||p.VAL1||'","'||p.VAL2||'","'||p.UOMNAME||'"]}' as result_SMARTPIVOT
  from fg_s_paramref_all_v p,
  fg_s_step_v s
  where p.PARENTID = s.step_id
  and p.ACTIVE = '1'
  and p.SESSIONID is null
  and p.VAL1 is not null
  union all
  ------------------------------------
  --Step analytical data
  ------------------------------------
  select t.experiment_id,
  400 as order_,
  to_char(t.step_id) as step_id,
  '' as materialref_id,
  'D' as tabletype,--for the order of the sections in the step
  s.RESULT_ID,
  t.formnumberid,
  '{pivotkey:"'|| t.experiment_id||'",pivotkeyname:"experiment_id"'||
  ',group:["Step'||t.FORMNUMBERID||'","Step'||t.FORMNUMBERID||'","Step'||t.FORMNUMBERID||'","Step'||t.FORMNUMBERID||'"]'||
  ',column:["{Step '||t.FORMNUMBERID||'}Characteristic Sample","{Step '||t.FORMNUMBERID||'}Analysis Source","{Step '||t.FORMNUMBERID||'}Analytical Experiment","{Step '||t.FORMNUMBERID||'}Assay %"]'||
  ',val:["' || to_clob(smpl.SampleName) ||'","'||decode(s.result_test_name,'Organic','Self-test','Analytical')||'","'|| decode(s.result_test_name,'Analytical',e_dest.ExperimentName)||'","'||s.RESULT_VALUE||'"]}' as result_SMARTPIVOT
  from fg_s_step_v t,--fg_i_sampleresults_v_f s,
  --SAMPLERESULTS_VIEW s,--
  fg_s_sample_v smpl,
  fg_i_selectedresults_v s,--main results only
  fg_s_experiment_v e_dest
  where t.CHARACTERIZEDSAMPLE = s.SAMPLE_ID(+)
  and t.CHARACTERIZEDSAMPLE = smpl.sample_id
  and instr(s.RESULT_NAME(+),'Assay')>0
  --and s.RESULT_NAME(+) = 'Assay'
  --and(s.SELFTEST_ID is not null or exists(select sample_id from fg_i_selectedresults_v sr where sr.RESULT_ID = s.result_id))
  --and s.result_id = sl.result_id--main results only
  and s.EXPERIMENT_ID = e_dest.experiment_id
  union all
  ------------------------------------
  --impurity Step
  ------------------------------------
  select experiment_id,
  400 as order_,
  step_id as step_id,
  '' as materialref_id,
  'E' as tabletype,--for the order of the sections in the step
  result_id,
  formnumberid as formnumberid,
  '{pivotkey:"'|| experiment_id||'",pivotkeyname:"experiment_id",group:["'|| invitemmaterialname||'"]'||
  ',column:["{Step '||FORMNUMBERID||'}Impurity '||invitemmaterialname||',%"]'||--,"{Step '||FORMNUMBERID||'}Impurity concentration %"]'||
  ', val:["' || to_clob(result_value) ||'"]}' as result_SMARTPIVOT
  from (
      select distinct
             to_char(t1.step_id)as step_id,
             t1.formnumberid,
             t1.experiment_id,
             t1.result_id,
             t1.invitemmaterialname,
             t1.result_value
      from fg_i_expanalysis_impurity_st_v t1
      where t1.result_value is not null
  )
  union all
  ------------------------------------
  --Step reactant
  ------------------------------------
  select experiment_id,
  400 as order_,
  step_id,
  materialref_id,
  tabletype,
  '',
  formnumberid,
  '{pivotkey:"'|| experiment_id||'",pivotkeyname:"experiment_id"'||
                  ',group:["'|| formnumberid ||'Reactant'||invitemmaterialname||'","'|| formnumberid ||'Reactant'||invitemmaterialname||'","'|| formnumberid ||'Reactant'||invitemmaterialname||'"'||
                                ',"'|| formnumberid ||'Reactant'||invitemmaterialname||'","'|| formnumberid ||'Reactant'||invitemmaterialname||' Quantity"'||
                                ',"'|| formnumberid ||'Reactant'||invitemmaterialname||'","'|| formnumberid ||'Reactant'||invitemmaterialname||' Volume"'||
                                ',"'|| formnumberid ||'Reactant'||invitemmaterialname||'","'|| formnumberid ||'Reactant'||invitemmaterialname||' Moles"'||
                                ',"'|| formnumberid ||'Reactant'||invitemmaterialname||'","'|| formnumberid ||'Reactant'||invitemmaterialname||'","'|| formnumberid ||'Reactant'||invitemmaterialname||'","'|| formnumberid ||'Reactant'||invitemmaterialname||'"]'||
                  ',column:["{Reactant }Step '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'","{ReactantStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'}Batch","{ReactantStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'}Purity %"'||
                                       ',"{ReactantStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'}Quantity","{ReactantStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||' Quantity}Uom"'||
                                       ',"{ReactantStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'}Volume","{ReactantStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||' Volume}Uom"'||
                                       ',"{ReactantStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'}Moles","{ReactantStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||' Moles}Uom"'||
                                       ',"{ReactantStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'}Equivalent","{ReactantStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'}Water Content,%","{ReactantStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'}Catalyst","{ReactantStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'}Comments"]'||
                  ', val:["' || to_clob(invitemmaterialname) ||'","'||batch||'","'||purity||'"'||
                             ',"'||quantity||'","'||nvl2(quantity,' '||quantityuomname,'')||'"'||
                             ',"'||volume||'","'||nvl2(volume,' '||voluomname,'')||'"'||
                             ',"'||mole||'","'||nvl2(mole,' '||moleuomname,'')||'"'||
                             ',"'||equivalent||'","'||WaterContent||'","'||CATALYST||'","'||COMMENTS||'"]}' as result_SMARTPIVOT
  from (select distinct
             t2.experiment_id,
             t2.step_id,
             t2.materialref_id,
             t2.tabletype,
             '' result_id,
             t2.invitemmaterialname,
             '' result_value,
             t2.batch,
             t2.formnumberid,
             t2.PURITY,
             t2.quantity,
             t2.quantityuomname,
             t2.volume,
             t2.voluomname,
             t2.mole,
             t2.Equivalent,
             t2.WaterContent,
             t2.catalyst,
             t2.comments,
             t2.moleuomname
        from fg_i_expanalysis_stepreact_v t2
        where nullif(t2.stepstatusname,'Planned') is not null) t
  where step_id is not null
  and tabletype = 'Reactant'
  union all
  ------------------------------------
  --Step Solvent
  ------------------------------------
  select experiment_id,
  400 as order_,
  step_id,
  materialref_id,
  tabletype,
  '',
  formnumberid,
  '{pivotkey:"'|| experiment_id||'",pivotkeyname:"experiment_id"'||
                  ',group:["'|| formnumberid ||'Slovent'||invitemmaterialname||'"'||
                  ',"'|| formnumberid ||'Slovent'||invitemmaterialname||'","'|| formnumberid ||'Slovent'||invitemmaterialname||' Quantity"'||
                  ',"'|| formnumberid ||'Slovent'||invitemmaterialname||'","'|| formnumberid ||'Slovent'||invitemmaterialname||' Volume"'||
                  ',"'|| formnumberid ||'Slovent'||invitemmaterialname||'","'|| formnumberid ||'Slovent'||invitemmaterialname||'Moles"'||
                  ',"'|| formnumberid ||'Slovent'||invitemmaterialname||'","'|| formnumberid ||'Slovent'||invitemmaterialname||'","'|| formnumberid ||'Slovent'||invitemmaterialname||'","'|| formnumberid ||'Slovent'||invitemmaterialname||'"]'||
                  ',column:["{Slovent }Step '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'"'||
                  ',"{SloventStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'}Quantity","{SloventStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||' Quantity}Uom"'||
                  ',"{SloventStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'}Volume","{SloventStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||' Volume}Uom"'||
                  ',"{SloventStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'}Moles","{SloventStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'Moles}Uom"'||
                  ',"{SloventStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'}Purity %","{SloventStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'}Actual Ratio to limiting agent","{SloventStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'}Water Content,%","{SloventStep '|| formnumberid ||'-'||tabletype||' '||invitemmaterialname||'}Comments"]'||
                  ', val:["' || to_clob(invitemmaterialname) ||'"'||
                  ',"'||quantity||'","'||nvl2(quantity,' '||quantityuomname,'')||'"'||
                  ',"'||volume||'","'||nvl2(volume,' '||voluomname,'')||'"'||
                  ',"'||mole||'","'||nvl2(mole,' '||moleuomname,'')||'"'||
                  ',"'||purity||'","'||t.ratio||'","'||WaterContent||'","'||comments||'"]}' as result_SMARTPIVOT
  from (select distinct
             t2.experiment_id,
             t2.step_id,
             t2.materialref_id,
             t2.tabletype,
             '' result_id,
             t2.invitemmaterialname,
             '' result_value,
             t2.batch,
             t2.formnumberid,
             t2.PURITY,
             t2.quantity,
             t2.quantityuomname,
             t2.volume,
             t2.voluomname,
             t2.mole,
             t2.moleuomname,
             t2.Equivalent,
             t2.WaterContent,
             t2.ratio,
             t2.comments
        from fg_i_expanalysis_stepreact_v t2
        where nullif(t2.stepstatusname,'Planned') is not null) t
  where step_id is not null
  and tabletype = 'Solvent'
) order by order_, TO_NUMBER(formnumberid),tabletype, result_id, materialref_id,experiment_id;
