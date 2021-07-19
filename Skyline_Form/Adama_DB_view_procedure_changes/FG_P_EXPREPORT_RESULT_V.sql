CREATE OR REPLACE VIEW FG_P_EXPREPORT_RESULT_V AS
SELECT distinct "SAMPLE_STEP_ID","SAMPLE_EXPERIMENT_ID","SAMPLE_ID","EXPERIMENTDEST_ID","ORDER_","RESULTTYPE_ORDER","RESULT_SMARTPIVOT","BATCH_ID" from
(
  -- automatic and manual results
  select t.SAMPLE_ID,t.EXPERIMENT_ID as SAMPLE_EXPERIMENT_ID,t.SAMPLE_STEP_ID,
  t.BATCH_ID,
  t.EXPERIMENTDEST_ID,
  1 as order_,
  decode(upper(t.RESULT_NAME),'ASSAY',1,2) as resulttype_order,-- assay results appear first
  '{pivotkey:"'|| t.SAMPLE_ID||'_'||t.EXPERIMENTDEST_ID||'",pivotkeyname:"UNIQUEROW",'
  ||'column:"'||t.MATERIALNAME_R||'('||t.RESULT_NAME||')_SMARTICON",'--_SMARTNUM
  ||'val:'||nvl2(t.RESULT_COMMENT,
                             '{"displayName":"'|| result_value||' '||t.RESULT_UOM || '" ,"htmlType":"span", "icon":"' || 'fa fa-comment' || '", "tooltip":"'||t.RESULT_COMMENT||'"}'
                             ,'"'||result_value||' '||t.RESULT_UOM||'"')||'}' as result_SMARTPIVOT
  from fg_r_experimentresult_noreq_v t
  where t.EXPERIMENTSTATUSNAME = 'Approved'
  and nvl(to_char(t.INVITEMMATERIAL_ID),nvl(t.RESULT_MATERIALNAME,t.RESULT_VALUE))is not null--all the results except for the chromatogram that is handled in the following section
  and t.RESULT_NAME <> 'Impurity Identification'--all the results except for the MS results that is handled in the following section
  union all
  -- MS manual results
  select t.SAMPLE_ID,t.EXPERIMENT_ID as SAMPLE_EXPERIMENT_ID,t.SAMPLE_STEP_ID,
  t.BATCH_ID,
  t.EXPERIMENTDEST_ID,
  2 as order_,
  decode(upper(t.RESULT_NAME),'ASSAY',1,2) as resulttype_order,-- assay results appear first
  '{pivotkey:"'|| t.SAMPLE_ID||'_'||t.EXPERIMENTDEST_ID||'",pivotkeyname:"UNIQUEROW",'
  ||'column:"MS_SMARTFILE",'--_SMARTNUM
  ||'val:'||nvl(FG_GET_SMART_LINK_OBJECT(t.MATERIALNAME_R ,t.FORMCODE ,
                                                          mref.STRUCTURE,
                                                           '','chemdoodle',0,'','','',t.RESULT_COMMENT ),'""')
  ||'}' as result_SMARTPIVOT
  from fg_r_experimentresult_noreq_v t,
  fg_s_manualresultsmsref_v mref
  where t.EXPERIMENTSTATUSNAME = 'Approved'
  and t.RESULT_NAME = 'Impurity Identification'
  and mref.formid(+) = t.resultref_id
  and mref.sessionId(+) is null
  and mref.active = 1
  and mref.SAMPLE_ID(+) = t.SAMPLE_ID
  and nvl(mref.structure(+),t.MATERIALNAME_R) is not null
  union all
  -- Documents & Chromatograms
  select t.SAMPLE_ID,t.EXPERIMENT_ID as SAMPLE_EXPERIMENT_ID,t.SAMPLE_STEP_ID,
  t.BATCH_ID,
  t.EXPERIMENTDEST_ID,
  3 as order_,
  3 as resulttype_order,--documents appear in the end of the table
  '{pivotkey:"'|| t.SAMPLE_ID||'_'||t.EXPERIMENTDEST_ID||'",pivotkeyname:"UNIQUEROW",'
   ||'column:"Doc/Chr_SMARTFILE",'
   ||'val:'||nvl(decode(t.FORMCODE,'ExperimentAn',
                                                FG_GET_SMART_LINK_OBJECT('' ,t.FORMCODE ,t.EXPERIMENTDEST_ID ,'Chromatograms','file',1,'','expChromatogramsSample',t.sample_id ),
                                                FG_GET_SMART_LINK_OBJECT('' ,t.FORMCODE ,t.EXPERIMENTDEST_ID ,'Documents','file',1,'','expDocumentsSample',t.sample_id )
                     ),'""'
                 )||'}' as result_SMARTPIVOT
    from fg_r_experimentresult_noreq_v t,
    fg_s_document_v d
    where t.EXPERIMENTSTATUSNAME = 'Approved'
    and d.PARENTID = t.EXPERIMENTDEST_ID
  ) order by order_,resulttype_order;
