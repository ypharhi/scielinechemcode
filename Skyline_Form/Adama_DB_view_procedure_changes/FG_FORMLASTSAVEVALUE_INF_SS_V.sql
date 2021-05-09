CREATE OR REPLACE VIEW FG_FORMLASTSAVEVALUE_INF_SS_V AS
select "ID","FORMID","FORMCODE_ENTITY","ENTITYIMPCODE","ENTITYIMPVALUE","USERID","CHANGE_COMMENT","CHANGE_ID","CHANGE_BY","CHANGE_TYPE","CHANGE_DATE","SESSIONID","ACTIVE","DISPLAYVALUE","UPDATEJOBFLAG","DISPLAYLABEL","PATH_ID","IS_FILE","IS_IDLIST","CURRENT_DISPLAYVALUE","LOG_VERSION","EXPERIMENT_ID" from (
SELECT --THIS VIEW REMOVE 1)  REMOVE ROWS WITH ID CSV LIST AND SHOW SINGLE ID
       --                 2)  POSTSAVE VALUE THAT DIDN'T REVERT ON ROLLBACK (THE BECAUSE THE ASYNC MERGE FG_FORMLASTSAVEVALUE_INF SQL IS NOT PART OF THE TRANSACTION)
      null as ID,
      formid,
      formcode_entity,
      entityimpcode,
      entityimpvalue,
      userid,
      change_comment,
      change_id,
      change_by,
      change_type,
      change_date,
      sessionid,
      active,
      decode(S.id,null,displayvalue,decode(S.id,nvl(displayvalue,S.id),s.formidname,displayvalue)) as displayvalue,
      NVL(displayvalue,S.FORMIDNAME) AS current_displayvalue,
      updatejobflag,
      displaylabel,
      path_id,
      is_file,
      is_idlist,
      --db_transaction_id,
      --tmp_entityimpvalue,
      --tmp_displayvalue
      EXPERIMENT_ID,
      LOG_VERSION
FROM  FG_FORMLASTSAVEVALUE_INF_SS t,
      FG_SEQUENCE S
WHERE 1=1 --IS_IDLIST <> 1
AND   decode(nvl(t.is_idlist,0),0,NULL,to_char(t.entityimpvalue)) = to_char(S.id(+))
AND   entityimpvalue <> 'null'
)
where DECODE(IS_IDLIST,1,instr(entityimpvalue,','),0) = 0
--and formcode_entity = 'Project'
--ORDER BY CHANGE_DATE DESC;
