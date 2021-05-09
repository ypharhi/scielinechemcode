create materialized view FG_I_REQUEST_DESTEXP_MV
refresh force on demand
as
select distinct
       rslct.singleReq_id as REQUEST_ID,
       rslct.PARENTID as EXPERIMENT_ID
from
--fg_s_requestselect_all_v
(
  WITH SELECT_VIEW AS
    (SELECT t.*,
            regexp_substr(t.REQUEST_ID, '[^,]+', 1, commas.column_value) AS single_val
     FROM FG_S_REQUESTSELECT_V t,
          table(cast(multiset
                       (SELECT LEVEL
                        FROM dual CONNECT BY LEVEL <= LENGTH (regexp_replace(t.REQUEST_ID, '[^,]+')) + 1) AS sys.OdciNumberList)) commas)
  SELECT DISTINCT SELECT_VIEW."REQUESTSELECT_ID",SELECT_VIEW."FORM_TEMP_ID",SELECT_VIEW."REQUESTSELECT_OBJIDVAL",SELECT_VIEW."FORMID",SELECT_VIEW."TIMESTAMP",SELECT_VIEW."CREATION_DATE",SELECT_VIEW."CLONEID",SELECT_VIEW."TEMPLATEFLAG",SELECT_VIEW."CHANGE_BY",SELECT_VIEW."CREATED_BY",SELECT_VIEW."SESSIONID",SELECT_VIEW."ACTIVE",SELECT_VIEW."FORMCODE_ENTITY",SELECT_VIEW."FORMCODE",SELECT_VIEW."REQUEST_ID",SELECT_VIEW."PARENTID",SELECT_VIEW."DISABLED",SELECT_VIEW."REQUESTSELECTNAME"
         ,r.REQUESTNAME as REQUESTNUMBER,r.SOURCELABORATORYNAME,r.DESTLABORATORYNAME,r.REQUESTSTATUSNAME,r.PRIORITYNAME,r.REQUEST_ID as singleReq_id,r.ISINUSE
  FROM SELECT_VIEW,
       fg_s_request_all_v r
  WHERE SELECT_VIEW.single_val = r.REQUEST_ID
  AND SELECT_VIEW.single_val <> 'null'
  AND SELECT_VIEW.single_val IS NOT NULL
) rslct,
--fg_s_experimentselect_all_v
(
  WITH SELECT_VIEW AS
    (SELECT t.*,
            regexp_substr(t.EXPERIMENT_ID, '[^,]+', 1, commas.column_value) AS single_val
     FROM FG_S_EXPERIMENTSELECT_V t,
          table(cast(multiset
                       (SELECT LEVEL
                        FROM dual CONNECT BY LEVEL <= LENGTH (regexp_replace(t.EXPERIMENT_ID, '[^,]+')) + 1) AS sys.OdciNumberList)) commas)
  SELECT DISTINCT--SELECT_VIEW.* without single_val
        SELECT_VIEW."EXPERIMENTSELECT_ID",SELECT_VIEW."FORM_TEMP_ID",SELECT_VIEW."EXPERIMENTSELECT_OBJIDVAL",SELECT_VIEW."FORMID",SELECT_VIEW."TIMESTAMP",SELECT_VIEW."CLONEID",SELECT_VIEW."TEMPLATEFLAG",SELECT_VIEW."CHANGE_BY",SELECT_VIEW."SESSIONID",SELECT_VIEW."ACTIVE",SELECT_VIEW."FORMCODE_ENTITY",SELECT_VIEW."FORMCODE",SELECT_VIEW."PARENTID",SELECT_VIEW."EXPERIMENTSELECTNAME",SELECT_VIEW."DISABLED",SELECT_VIEW."EXPERIMENT_ID"
        ,ex.EXPERIMENTNAME ,ex.EXPERIMENT_ID as singleExp_id
  FROM SELECT_VIEW,
       fg_s_experiment_v ex
  WHERE SELECT_VIEW.single_val = ex.EXPERIMENT_ID
  AND SELECT_VIEW.single_val <> 'null'
  AND SELECT_VIEW.single_val IS NOT NULL
) eslct,
FG_S_EXPERIMENT_V e
where rslct.PARENTID = e.EXPERIMENT_ID
      and eslct.PARENTID(+) = rslct.singleReq_id
      and instr(',' || eslct.EXPERIMENT_ID|| ',',',' || e.EXPERIMENT_ID || ',') <= 0;
