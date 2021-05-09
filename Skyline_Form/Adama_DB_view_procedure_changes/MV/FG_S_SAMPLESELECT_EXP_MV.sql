create materialized view FG_S_SAMPLESELECT_EXP_MV
refresh force on demand
as
select distinct * from
(WITH
fg_id AS
  (    --select formid, experiment_id from (
       select e.formid, to_char(e.experiment_id) as experiment_id
       from  fg_s_experiment_v e
       union all
       select e.formid, e.experiment_id
       from  fg_s_step_v e
       union all
       select e.formid, e.experiment_id
       from  fg_s_action_v e
       union all
       select e.formid, e.experiment_id
       from  fg_s_selftest_v e
       union all
       select e.formid, e.experiment_id
       from fg_s_workup_v e --)
   )
SELECT
  distinct t.sample_id,t.SAMPLENAME,TO_CHAR(e.experiment_id) EXPERIMENT_ID,t.PARENTID
FROM
   --fg_s_sampleselect_all_v
   (
           WITH SELECT_VIEW AS
              (SELECT t.*,
                      regexp_substr(t.SAMPLETABLE, '[^,]+', 1, commas.column_value) AS single_val
               FROM FG_S_SAMPLESELECT_V t,
                    table(cast(multiset
                                 (SELECT LEVEL
                                  FROM dual CONNECT BY LEVEL <= LENGTH (regexp_replace(t.SAMPLETABLE, '[^,]+')) + 1) AS sys.OdciNumberList)) commas)
            SELECT DISTINCT--SELECT_VIEW.* (without single_val)
                   SELECT_VIEW."SAMPLESELECT_ID",SELECT_VIEW."FORM_TEMP_ID",SELECT_VIEW."SAMPLESELECT_OBJIDVAL",SELECT_VIEW."FORMID",SELECT_VIEW."TIMESTAMP",SELECT_VIEW."CREATION_DATE",SELECT_VIEW."CLONEID",SELECT_VIEW."TEMPLATEFLAG",SELECT_VIEW."CHANGE_BY",SELECT_VIEW."CREATED_BY",SELECT_VIEW."SESSIONID",SELECT_VIEW."ACTIVE",SELECT_VIEW."FORMCODE_ENTITY",SELECT_VIEW."FORMCODE",SELECT_VIEW."PARENTID",SELECT_VIEW."SAMPLESELECTNAME",SELECT_VIEW."DISABLED",SELECT_VIEW."SAMPLETABLE"
                   ,smpl.SAMPLE_ID
                   ,smpl.SAMPLENAME
                   ,smpl.CREATIONDATE
                   ,decode(instr(',' || SELECT_VIEW.DISABLED|| ',',',' || smpl.SAMPLE_ID || ','),0,0,1) as "IS_CHECKBOX_DISABLED"
            FROM SELECT_VIEW,
                 fg_s_sample_v smpl
            WHERE SELECT_VIEW.single_val = smpl.SAMPLE_ID
            AND SELECT_VIEW.single_val <> 'null'
            AND SELECT_VIEW.single_val IS NOT NULL
   )t,
   fg_id e
where t.PARENTID = e.formid
and t.ACTIVE = 1
and t.SESSIONID is null);
