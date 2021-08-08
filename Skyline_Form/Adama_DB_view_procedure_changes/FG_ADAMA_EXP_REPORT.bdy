create or replace package body FG_ADAMA_EXP_REPORT is

  function GET_UPDATE_P_EXPREPORT_DATA(statekey_in varchar, resulttype_in VARCHAR,
                                           characteristicMassBalan_in VARCHAR,
                                           imputityMatIds_in VARCHAR, --one or more vlues from: <Conversion, Isolated Yield%, Chemical Yield%, Summary> or <All>
                                           sampleComments_in VARCHAR,
                                           sampleCreator_in VARCHAR,
                                           sampleAmount_in VARCHAR,
                                           expdIds_in clob)
                                           return varchar2 as
    characteristicMBWrapper_in varchar(1000);
    sampleCount number;
    var_rows number;
  begin
    select count(*) into sampleCount from FG_P_EXPREPORT_SAMPLE_TMP t where t.statekey = statekey_in;

    -- mb experiment
    if characteristicMassBalan_in is not null then
        --set characteristicMBWrapper_in
        if lower(characteristicMassBalan_in) = 'all' then
          characteristicMBWrapper_in := 'conversion,isolatedyield,chemicalyield,summary';
        else
          characteristicMBWrapper_in := lower(replace(replace(characteristicMassBalan_in,' ',''),'%',''));
        end if;

        insert into FG_P_EXPREPORT_DATA_TMP (statekey, order_, order2, result_SMARTPIVOT)
        select distinct * from (
              with exp_step_mb as (
                   select t_max.*, smpls.sample_id
                   from (
                       select t_all.*, max(CHARACTER_MB) over (partition by EXPERIMENT_ID) as MAX_CHARACTER_MB  from (
                               -- MB exp - with CHKCHARACTERMASSBALANCE = 1 (ONLY ONE IN EXPERIMENT SCOPE)
                               select distinct
                                       1 as CHARACTER_MB, 'exp' as entity_, t1.experiment_id,
                                       t1.name_, t1.value_, t1.tab_index, t1.name_order,
                                       null CONVERSION, null CHEMICALYIELD, null ISOLATEDYIELD, null SUMMARY
                               from fg_i_expanalysis_exp_massbal_v t1--select te tab index of the characteristic mass ballance
                               where 1=1
                               and DBMS_LOB.INSTR( expdIds_in, ',' || t1.experiment_id || ',' ) > 0
                               and t1.tab_index in
                                       (select t2.tab_index
                                        from   fg_i_expanalysis_exp_massbal_v t2
                                        where  t2.name_ in('chkCharacterMassBalance','chkCharacterMassBalance2','chkCharacterMassBalance3')
                                        and    t2.value_ = '1'
                                        and t1.experiment_id = t2.experiment_id)
                               and t1.name_ not in ('chkCharacterMassBalance','chkCharacterMassBalance2','chkCharacterMassBalance3')
                               and INSTR(characteristicMBWrapper_in,lower(replace(replace(t1.name_,'2',''),'3',''))) > 0
                               union all
                               --MB exp - with Sream
                               select distinct
                                     0 as CHARACTER_MB, 'exp' as entity_, t1.experiment_id,
                                     t1.name_, t1.value_, t1.tab_index, t1.name_order,
                                     null CONVERSION, null CHEMICALYIELD, null ISOLATEDYIELD, null SUMMARY
                               from fg_i_expanalysis_exp_massbal_v t1--select te tab index of the characteristic mass ballance
                               where 1=1
                               and DBMS_LOB.INSTR( expdIds_in, ',' || t1.experiment_id || ',' ) > 0
                               and exists (
                                  select st.experiment_id, st.step_id, st.table_group_index_mb
                                  from FG_WEBIX_OUTPUT st
                                  where st.experiment_id = t1.experiment_id
                                  and   st.table_group_index_mb = t1.tab_index
                               )
                               and t1.name_ not like 'chkCharacterMassBalance%'
                               and INSTR(characteristicMBWrapper_in,lower(replace(replace(t1.name_,'2',''),'3',''))) > 0
                               union all
                               -- MB step - with CHKCHARACTERMASSBALANCE = 1 (ONLY STEP IN EXPERIMENT SCOPE)
                               select 1 as CHARACTER_MB, 'step' as entity_, t.EXPERIMENT_ID,
                                      null name_, null value_,null tab_index, null name_order,
                                      t.CONVERSION, t.CHEMICALYIELD, t.ISOLATEDYIELD, t.SUMMARY
                               from fg_s_step_v t
                               where t.CHKCHARACTERMASSBALANCE = 1
                               and DBMS_LOB.INSTR( expdIds_in, ',' || t.experiment_id || ',' ) > 0
                               union all
                               --MB step - with Sream
                               select 0 as CHARACTER_MB, 'step' as entity_, t.EXPERIMENT_ID,
                                      null name_, null value_,null tab_index, null name_order,
                                      t.CONVERSION, t.CHEMICALYIELD, t.ISOLATEDYIELD, t.SUMMARY
                               from fg_s_step_v t
                               where 1=1
                               and DBMS_LOB.INSTR( expdIds_in, ',' || t.experiment_id || ',' ) > 0
                               and exists (
                                    select st.experiment_id, st.step_id, st.table_group_index_mb
                                    from FG_WEBIX_OUTPUT st
                                    where st.step_id = t.step_id
                               )
                       ) t_all
                  ) t_max,
                  -- and /*YARON_SAMPL*/
                  --(select column_value as sample_id from table(FN_LIST_TO_TABLE(sampleIds_in)) where column_value not like '%''%') smpls
                  (select sample_id from FG_P_EXPREPORT_SAMPLE_TMP) smpls
                  where MAX_CHARACTER_MB = CHARACTER_MB -- show CHKCHARACTERMASSBALANCE if exists in experiment else all the ones with Sream
              )
              --**** exp from exp_step_mb
              select
                  statekey_in,
                  1000 + (tab_index * 10) + name_order  as order_,
                  entity_ as order2,
                  '{pivotkey:"'|| decode(SAMPLE_ID,'-1','', SAMPLE_ID||'_') || EXPERIMENT_ID ||'",pivotkeyname:"UNIQUEROW"' ||
                  ',column:["Experiment - ' || decode(name_,'chemicalYield','Chemical yield,%','chemicalYield2','Chemical yield,%','chemicalYield3','Chemical yield,%',
                                          'isolatedYield','Isolated yield,%','isolatedYield2','Isolated yield,%','isolatedYield3','Isolated yield,%',
                                          'conversion','Conversion,%','conversion2','Conversion,%','conversion3','Conversion,%',
                                          'Summary,%')
                  ||'"]'||
                  ',val:["' ||( value_ )||'"]}' as result_SMARTPIVOT
              from exp_step_mb
              where entity_ = 'exp'
              --filter by display is made inside t_all
              union all
              --**** steps from exp_step_mb
              select statekey_in,
                    2021 as order_,
                    entity_ as order2,
                    '{pivotkey:"'|| decode(SAMPLE_ID,'-1','', SAMPLE_ID||'_') || EXPERIMENT_ID ||'",pivotkeyname:"UNIQUEROW"' ||
                    ',column:"Step - Conversion,%"'||
                    ',val:"' || (CONVERSION) ||'"}' as result_SMARTPIVOT
              from exp_step_mb
              where entity_ = 'step'
              --filter by display
              and INSTR(characteristicMBWrapper_in,'conversion') > 0
              union all
              select statekey_in,
                    2022 as order_,
                    entity_ as order2,
                    '{pivotkey:"'|| decode(SAMPLE_ID,'-1','', SAMPLE_ID||'_') || EXPERIMENT_ID||'",pivotkeyname:"UNIQUEROW"' ||
                    ',column:"Step - Chemical yield,%"'||
                    ',val:"' || (CHEMICALYIELD) ||'"}' as result_SMARTPIVOT
              from exp_step_mb
              where entity_ = 'step'
              --filter by display
              and INSTR(characteristicMBWrapper_in,'chemicalyield') > 0
              union all
              select statekey_in,
                    2023 as order_,
                    entity_ as order2,
                    '{pivotkey:"'|| decode(SAMPLE_ID,'-1','', SAMPLE_ID||'_') || EXPERIMENT_ID||'",pivotkeyname:"UNIQUEROW"' ||
                    ',column:"Step - Isolated yield,%"'||
                    ',val:"' || (ISOLATEDYIELD) ||'"}' as result_SMARTPIVOT
              from exp_step_mb
              where entity_ = 'step'
              --filter by display
              and INSTR(characteristicMBWrapper_in,'isolatedyield') > 0
              union all
              select statekey_in,
                    2024 as order_,
                    entity_ as order2,
                    '{pivotkey:"'|| decode(SAMPLE_ID,'-1','', SAMPLE_ID||'_') || EXPERIMENT_ID||'",pivotkeyname:"UNIQUEROW"' ||
                    ',column:"Step - Summary,%"'||
                    ',val:"' || (SUMMARY) ||'"}' as result_SMARTPIVOT
              from exp_step_mb
              where entity_ = 'step'
              --filter by display
              and INSTR(characteristicMBWrapper_in,'summary') > 0
      );
      var_rows := SQL%ROWCOUNT;
  end if;

  if sampleCount > 1 then
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
      from FG_P_EXPREPORT_SAMPLE_TMP t
      --where DBMS_LOB.INSTR( sampleIds_in, ',' || t.sample_id || ',' ) > 0
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
     from FG_P_EXPREPORT_SAMPLE_TMP t
     where 1=1
     and  t.sampleDesc is not null
     --and DBMS_LOB.INSTR( sampleIds_in, ',' || t.sample_id || ',' ) > 0
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
     from FG_P_EXPREPORT_SAMPLE_TMP t
     where 1=1
     and  t.AMMOUNT is not null
     and sampleAmount_in = '1'
     --and DBMS_LOB.INSTR( sampleIds_in, ',' || t.sample_id || ',' ) > 0
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
      from FG_P_EXPREPORT_SAMPLE_TMP t, fg_richtext f
      where t.COMMENTSFORCOA = f.file_id(+)
      --and DBMS_LOB.INSTR( sampleIds_in, ',' || t.sample_id || ',' ) > 0
      and sampleComments_in = '1'
      union all
     -----------------------
      --sample creator if sampleCreator_in = 1
      -----------------------
      select distinct to_char(t.SAMPLE_ID) as SAMPLE_ID,
      t.EXPERIMENT_ID,
      10004 as order_,
      CAST(NULL AS varchar2(500)) as order2,-- assay results appear first
      '{pivotkey:"'|| t.SAMPLE_ID||'_'||t.EXPERIMENT_ID||'",pivotkeyname:"UNIQUEROW",'
      ||'column:"Sample Creator",'
      ||'val:"'|| u.UserName
      ||'"}' as result_SMARTPIVOT
     from FG_P_EXPREPORT_SAMPLE_TMP t, fg_s_user_v u
     where t.CREATOR_ID = u.user_id(+)
     and sampleCreator_in = '1'
     --and DBMS_LOB.INSTR( sampleIds_in, ',' || t.sample_id || ',' ) > 0
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
            --************
            -- selected IMPURITY
            --************
            select t.SAMPLE_ID,
                   s.EXPERIMENT_ID,
                   t.RESULT_TYPE,
                   nvl(t.RESULT_VALUE, t.RESULT_MATERIALNAME) as val_,
                   decode(m.InvItemMaterialName, null, t.RESULT_NAME, m.InvItemMaterialName || ' (' || t.RESULT_NAME || ')')
                   || decode(u.UOMName,null,'','[' ||u.UOMName || ']') as name_
            from fg_i_result_all_v t, fg_s_invitemmaterial_v m, FG_P_EXPREPORT_SAMPLE_TMP s, fg_s_uom_v u
            where 1=1
            and t.SAMPLE_ID = s.SAMPLE_ID
            and t.RESULT_UOM_ID = u.uom_id(+)
            and t.RESULT_MATERIAL_ID = m.invitemmaterial_id(+)
            and t.RESULT_IS_ACTIVE = 1
            --and DBMS_LOB.INSTR( sampleIds_in, ',' || t.sample_id || ',' ) > 0
            and upper(t.RESULT_NAME) like '%IMPURITY%'
            --and instr(nvl(imputityMatIds_in,'-1') ,t.RESULT_MATERIAL_ID) > 0 --
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
            from fg_i_result_all_v t, fg_s_invitemmaterial_v m, FG_P_EXPREPORT_SAMPLE_TMP s, fg_s_uom_v u
            where 1=1
            and t.SAMPLE_ID = s.SAMPLE_ID
            and t.RESULT_UOM_ID = u.uom_id(+)
            and t.RESULT_MATERIAL_ID = m.invitemmaterial_id(+)
            and t.RESULT_IS_ACTIVE = 1
            --and DBMS_LOB.INSTR( sampleIds_in, ',' || t.sample_id || ',' ) > 0
            and upper(t.RESULT_NAME) not like '%IMPURITY%'
            and instr(decode(resulttype_in,'ALL',t.RESULT_NAME,null,'-1',resulttype_in) ,t.RESULT_NAME) > 0
                ) t1 where t1.val_ is not null
    );
    var_rows := SQL%ROWCOUNT + var_rows;
  end if;

  return to_char(var_rows);

  end;

end;
/
