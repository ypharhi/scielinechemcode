CREATE OR REPLACE VIEW FG_S_REQUEST_DTREREQ_V AS
select
  t.REQUEST_ID,t.PARENTID,t.REQUESTSTATUSNAME,t.DESTUNIT_ID,t.form_temp_id
  ,'{"displayName":"' || t.REQUESTNAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Request' || '"  ,"formId":"' || t.REQUEST_ID || '","tab":"' || '' || '","smartType":"SMARTLINK", "title":"Request #" }' as "Request #_SMARTLINK"
  --, t.REQUESTNAME as "Request #"
  ,p.projectname as "Project"
  ,sp.subprojectname as "Subproject"
  ,listagg(op.EXPERIMENTTYPENAME,';') within group ( order by op.EXPERIMENTTYPENAME) over (partition by op.OPT_PARENTID) as "Operation Type"
  --,fg_get_operation_list(t.REQUEST_ID) as "Operation Type"
  ,t.REQUESTTYPENAME as "Type"
  ,t.REQUESTSTATUSNAME as "Status"
  ,t.SOURCELABORATORYNAME as "Source Lab"
  ,t.DESTLABORATORYNAME as "Destination Lab"
  ,t.PRIORITYNAME as "Priority"
  --,t.CREATIONDATE as "Creation Date"
  ,t.CREATION_DATE as "Creation Date_SMARTTIME"
  -- icon display start
  ,decode(mv.request_id,
          null,
          '',
         '{"displayName":"' || '~1 yes' || '" ,"icon":"' || 'fa fa-warning' || '", "tooltip":"'||'Warning:experiment is already created on this request base'||'","smartType":"SMARTICON", "title":"Experiment Exist"}'
   ) "Experiment Exist_SMARTICON"
   -- icon display end
from FG_S_REQUEST_ALL_V t,fg_s_subproject_all_v sp,fg_s_project_all_v p
      ,fg_i_conn_request_optype_v op,
      --start mv as fg_i_request_destexp_current_v sql without experiment in the result to prevent duplication.
      -- this sql is made for replacing the icon evaluation using:
      /*
      case
         when (select count(*) from fg_i_request_destexp_current_v s where s.request_id = t.REQUEST_ID ) > 0
           then
              '{"displayName":"' || '' || '" ,"icon":"' || 'fa fa-warning' || '", "tooltip":"'||'Warning:experiment is already created on this request base'||'"}'
           else ''
      end as "Experiment Exist_SMARTICON"
      */
      -- the use of the mv sql is much more faster! (although it is ugly :()
      (
        select distinct
                rslct.singleReq_id as REQUEST_ID--,
                --rslct.PARENTID as EXPERIMENT_ID
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
              and instr(',' || eslct.EXPERIMENT_ID|| ',',',' || e.EXPERIMENT_ID || ',') <= 0
      ) mv -- end mv sql
where t.project_id = p.PROJECT_ID(+)
and t.SUBPROJECT_ID = sp.SUBPROJECT_ID(+)
and t.REQUEST_ID = op.REQUEST_ID
and mv.request_id(+) = t.REQUEST_ID;
