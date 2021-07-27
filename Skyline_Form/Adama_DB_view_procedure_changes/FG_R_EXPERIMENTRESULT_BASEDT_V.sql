CREATE OR REPLACE VIEW FG_R_EXPERIMENTRESULT_BASEDT_V AS
SELECT r.SAMPLE_ID,
r.EXPERIMENT_ID as EXPERIMENTDEST_ID,
con.EXPERIMENTORIGIN_ID,
'{"displayName":"' || s.SAMPLENAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Sample' || '"  ,"formId":"' || s.SAMPLE_ID || '","tab":"' || '' || '" }' as "Sample_SMARTLINK",
'{"displayName":"' || s.BATCHNAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'InvItemBatch' || '"  ,"formId":"' || s.BATCH_ID || '","tab":"' || '' || '" }' as "Batch_SMARTLINK",
con.REQUEST_ID as REQUEST_ID,
con.REQUESTNUMBER as REQUESTNUMBER,
r.RESULT_TEST_NAME,
e.EXPERIMENTNAME as EXPERIMENTDESTNAME,
r.RESULT_NAME,--EXPERIMENTTYPENAME
nvl2(r.RESULT_MATERIAL_ID,'{"displayName":"' || m.INVITEMMATERIALNAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"'
        || 'InvItemMaterial' || '"  ,"formId":"' || m.INVITEMMATERIAL_ID || '","tab":"' || '' || '" }',r.RESULT_MATERIALNAME) as "Material_SMARTLINK",
r.RESULT_UOM_ID,
r.RESULT_VALUE,
r.RESULT_MATERIALNAME as MATERIALNAME_R,
m.DENSITY,
m.INVITEMMATERIAL_ID,
u.UOMNAME as "RESULT_UOM",
u.UOM_ID,
e.SUBPROJECT_ID,
decode(instr(upper(r.RESULT_NAME),'ASSAY'),0,'',DECODE(IS_NUMERIC(r.RESULT_VALUE),1,TO_NUMBER(r.RESULT_VALUE),0)/100*TO_NUMBER(m.DENSITY)) as "Adjusted Value",
decode(instr(upper(r.RESULT_NAME),'ASSAY'),0,'','%') as "ADJUSTED_UOM",
'' as "File",
r.RESULT_COMMENT,
r.RESULT_IS_WEBIX,
e.EXPERIMENTSTATUSNAME,--status of the dest experiment
e.FORMCODE,--destination experiment formcode
s.SAMPLETYPENAME,
s.AMMOUNT,
e.EXPERIMENTTYPENAME,
--e.EXPERIMENTTYPENAME, return back
/*fg_adama.get_sample_path( sampleId_in => r.SAMPLE_ID,
                          delimeter_in => '/') as "SamplePath",*/
r.resultref_id,
r.RESULT_ID
      from fg_i_connection_reqsmp_odexp_v con,
      fg_i_result_all_v r,
      fg_s_invitemmaterial_all_v m,
      fg_s_uom_all_v u,
      fg_s_sample_all_v s,
      fg_s_experiment_all_v e
where con.EXPERIMENTDEST_ID(+) = r.EXPERIMENT_ID
      and con.REQUEST_ID(+) = r.RESULT_REQUEST_ID
      and r.EXPERIMENT_ID = e.EXPERIMENT_ID(+)
      and r.RESULT_MATERIAL_ID = m.INVITEMMATERIAL_ID(+)
      and r.result_uom_id = u.UOM_ID(+)
      and decode(r.RESULT_NAME,'Impurity Identification',nvl2(r.SAMPLE_ID,decode(con.SAMPLE_ID(+),r.SAMPLE_ID,1,0),1),decode(con.SAMPLE_ID(+),r.SAMPLE_ID,1,0))=1
      and r.SAMPLE_ID = s.SAMPLE_ID(+)
      and r.RESULT_IS_ACTIVE = '1';
