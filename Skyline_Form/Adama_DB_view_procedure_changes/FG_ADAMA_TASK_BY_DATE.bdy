create or replace package body FG_ADAMA_TASK_BY_DATE is


  function FG_GETLASTRUNFORMID return number as
    num_ number;
    lastRunDate_ date;
  begin
    /*select min(id) as lasdtformId into num_ from fg_sequence t where t.changedate > sysdate - 2;
    return num_;*/
    select max(t.start_date_success_holder) into lastRunDate_ from FG_SYS_SCHED t where upper(t.sched_name) = 'CORRECTRECENTSEARCHDATA';

    if lastRunDate_ is not null then
      select min(id) into num_ from fg_sequence f where f.insertdate >= lastRunDate_;
    end if;

    return num_;
  EXCEPTION
        WHEN OTHERS THEN
          return null;
  end;

---------------
-- FG_SET_INF_MISSING_ROW_DATA
---------------
  FUNCTION FG_SET_INF_MISSING_ROW_DATA (formCode_in varchar2, DB_TRANSACTION_ID_IN VARCHAR2, formDateExp_ varchar2, is_popup_in number, is_reset_in number default 0) return number as

      col_list_pivot_statment varchar2(32767);

      sql_pivot varchar2(32767);

      delete_pivot varchar2(32767);

      formCodeEntity varchar2(100);

      formTypeParam varchar2(100);

      systemuser_ varchar2(100);

      counter_ number;

      fromFormIdWherePart varchar2(500) := ' ';

      notInIfWherePart varchar2(500) := ' ';

      parentIdExp varchar2(500) := 'null';


  begin

      --delete from fg_debug;
      --insert into fg_debug(comment_info,comments) values('start create table for formcode =' || formCode_in ,''); --commit;

      select nvl(f.formcode_entity,f.formcode),f.form_type into formCodeEntity,formTypeParam from fg_form f where f.formcode = formCode_in;

      select t.userrole_id into systemuser_ from fg_s_user_pivot t where t.username = 'system';

      if formDateExp_ is not null then
         fromFormIdWherePart := ' and timestamp >= ' || formDateExp_;
      end if;

      execute immediate 'select count(*) from FG_S_' || upper(formCodeEntity) || '_PIVOT where 1=1 ' || fromFormIdWherePart || ' and rownum = 1' into counter_;

      if counter_ = 0 then
        return 1;
      end if;

      --notInIfWherePart := ' and not exists (select 1 from FG_FORMLASTSAVEVALUE_INF f where f.formid = t1.formid and upper(f.entityimpcode) = upper(substr(t1.entityimpcode,3))) ';
      if is_reset_in = 0 then
        delete_pivot := 'delete from FG_FORMLASTSAVEVALUE_INF where upper(formcode_entity) = ''' || upper(formCodeEntity) || ''' and not exists (select formid from FG_S_' || upper(formCodeEntity) || '_PIVOT where FG_FORMLASTSAVEVALUE_INF.formid = FG_S_' || upper(formCodeEntity) || '_PIVOT.formid )';
        insert into fg_debug(comments,comment_info) values (delete_pivot,'delete_pivot ' || formCodeEntity);
        commit;
        execute immediate delete_pivot;
        commit;
      end if;


      -------------------------------------
      --arrange columns
      -------------------------------------
      col_list_pivot_statment := null;
      for cVal in (
                          select distinct t.entityimpcode as val_, nvl(m.islistid,0) || nvl(m.isparentpathid,1) || t.entityimpcode as as_val_
                          from FG_FORMENTITY t,
                               FG_FORMELEMENTINFOATMETA_MV m
                          where nvl(m.additionaldata,0) = 0
                          and   upper(m.formcode) = upper(formCode_in)
                          and   upper(m.formcode) = upper(t.formcode)
                          and   upper(m.entityimpcode) = upper(t.entityimpcode)
                          and   m.additionaldata = 0
                          and   ('FG_S_' || upper(m.formcode_entity) || '_PIVOT', UPPER(t.entityimpcode)) IN (SELECT USER_TAB_COLUMNS.TABLE_NAME, USER_TAB_COLUMNS.COLUMN_NAME FROM USER_TAB_COLUMNS)
                          and   m.elementclass not in ('ElementLabelImp','ElementIreportImp','ElementSmartSearchImp')
                    )
      LOOP
        if is_popup_in = 1 and UPPER(cVal.val_) = 'PARENTID' then
          parentIdExp := 'parentid'; -- make parentIdExp parentid and not include it in the pivot
        else
          if col_list_pivot_statment is null then
            col_list_pivot_statment := cVal.val_ || ' as ''' ||  cVal.as_val_ || '''';
          else
            col_list_pivot_statment := col_list_pivot_statment || ',' || cVal.val_ || ' as ''' ||  cVal.as_val_ || '''';
          end if;
        end if;
      END LOOP;


     truncate_it('FG_FORMLASTSAVEVALUE_INF_GTMP');

     -- SET MISSING FG_FORMLASTSAVEVALUE_INF USING FG_FORMLASTSAVEVALUE_INF_GTMP
     sql_pivot := '
        insert into FG_FORMLASTSAVEVALUE_INF_GTMP
                    ( id,
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
                      displayvalue,
                      updatejobflag,
                      displaylabel,
                      is_idlist,
                      path_id,
                      is_file,
                      DB_TRANSACTION_ID
                    )
        select  null,
                formid,
                ''' || formCodeEntity || ''' as formcode,
                substr(entityimpcode,3) as entityimpcode,
                entityimpvalue,
                nvl(change_by,''' || systemuser_ || ''') as userid,
                null as change_comment,
                null as change_id,
                nvl(change_by,''' || systemuser_ || ''') as change_by,
                ''U'' as change_type,
                timestamp as change_date,
                null as sessionid,
                active,
                entityimpvalue as displayvalue,
                1 as updatejobflag,
                substr(entityimpcode,3) as displaylabel,
                substr(entityimpcode,1,1) as is_idlist,
                ' || parentIdExp || ' as path_id,
                null as is_file,
                ''' || DB_TRANSACTION_ID_IN || '''
        from ( select t.* from FG_S_' || upper(formCodeEntity) || '_PIVOT t where nvl(t.active,0) >= 0 and t.sessionid is null ' || fromFormIdWherePart || '  ) unpivot include nulls
         (entityimpvalue for(entityimpcode) in
           (
                   ' ||       col_list_pivot_statment || '
           )
         ) t1 where 1=1 ' || notInIfWherePart;
          -- and (formid,entityimpcode) not in (select formid,entityimpcode from FG_FORMLASTSAVEVALUE_INF)
          insert into fg_debug(comments,comment_info) values (sql_pivot,'FG_SET_SERACH_BASIC_INIT_DATA.sql_pivot -' || formCode_in);commit;
          execute immediate sql_pivot;
          COMMIT;

          --MERGE FG_FORMLASTSAVEVALUE_INF_GTMP
          if is_reset_in = 0 then
            merge into FG_FORMLASTSAVEVALUE_INF p using ( select * FROM FG_FORMLASTSAVEVALUE_INF_GTMP) t1
            on( p.formid=t1.formid and p.entityimpcode = t1.entityimpcode )
            when not matched then insert (FORMID,FORMCODE_ENTITY,ENTITYIMPCODE,ENTITYIMPVALUE,USERID,CHANGE_COMMENT,CHANGE_ID,CHANGE_BY,CHANGE_TYPE,CHANGE_DATE,SESSIONID,ACTIVE,DISPLAYVALUE,UPDATEJOBFLAG,DISPLAYLABEL,PATH_ID,IS_IDLIST) values (t1.FORMID,t1.FORMCODE_ENTITY,t1.ENTITYIMPCODE,t1.ENTITYIMPVALUE,t1.USERID,t1.CHANGE_COMMENT,t1.CHANGE_ID,t1.CHANGE_BY,'I',t1.CHANGE_DATE,t1.SESSIONID,t1.ACTIVE,t1.DISPLAYVALUE,t1.UPDATEJOBFLAG,t1.DISPLAYLABEL,t1.PATH_ID,t1.IS_IDLIST)
             when matched then update set
             p.entityimpvalue = t1.entityimpvalue,
             p.DISPLAYVALUE = decode(p.entityimpvalue,t1.entityimpvalue,p.DISPLAYVALUE,t1.DISPLAYVALUE), -- yp 12032020 do not chage display if no change in entityimpvalue
             p.UPDATEJOBFLAG = t1.UPDATEJOBFLAG,
             p.ACTIVE = t1.ACTIVE,
             p.CHANGE_TYPE = 'U',
             p.CHANGE_DATE = t1.CHANGE_DATE,
             p.change_by = t1.userid,
             p.PATH_ID = t1.PATH_ID,
             p.IS_IDLIST = t1.IS_IDLIST--,
             --p.DB_TRANSACTION_ID = null
            where nvl(p.IS_IDLIST,0) <> 2;
          else
            insert into FG_FORMLASTSAVEVALUE_INF (FORMID,FORMCODE_ENTITY,ENTITYIMPCODE,ENTITYIMPVALUE,
                                                  USERID,CHANGE_COMMENT,CHANGE_ID,CHANGE_BY,CHANGE_TYPE,
                                                  CHANGE_DATE,SESSIONID,ACTIVE,DISPLAYVALUE,UPDATEJOBFLAG,
                                                  DISPLAYLABEL,PATH_ID,IS_IDLIST) 
             select t1.FORMID,t1.FORMCODE_ENTITY,t1.ENTITYIMPCODE,t1.ENTITYIMPVALUE,
                    t1.USERID,t1.CHANGE_COMMENT,t1.CHANGE_ID,t1.CHANGE_BY,'I',
                    t1.CHANGE_DATE,t1.SESSIONID,t1.ACTIVE,t1.DISPLAYVALUE,t1.UPDATEJOBFLAG,
                    t1.DISPLAYLABEL,t1.PATH_ID,t1.IS_IDLIST
             from   FG_FORMLASTSAVEVALUE_INF_GTMP t1;
          end if;

          --commit all;
          commit;

      return 1;
  EXCEPTION
        WHEN OTHERS THEN
          rollback;
          --FG_SEQUENCE_INSERT_TRIG enable IN EXCEPTION
          insert into fg_debug(comments,comment_info) values (sql_pivot,'sql_pivot (error) ' || formCodeEntity);
          --dbms_output.put_line('FG_SET_INF_MISSING_ROW_DATA - exception in form ' || formCodeEntity);
          commit;
          return -1;
  end;

---------------
-- FG_SET_INF_COMPLETE_DATA
---------------
  FUNCTION FG_SET_INF_COMPLETE_DATA (formCode_in varchar2, is_popup number, formDateExp_ varchar2, indexDebug number default 0) return number as

      sql_path varchar2(32767);

      sql_check varchar2(32767);

      formCodeEntity varchar2(100);

      formTypeParam varchar2(100);

      systemuser_ varchar2(100);

      count_ number;

      fromFormIdWherePart varchar2(500) := ' ';

      --lastTransactionRun_ date;

  begin
      --delete from fg_debug;
      --insert into fg_debug(comment_info,comments) values('start create table for formcode =' || formCode_in ,''); --commit;

      select nvl(f.formcode_entity,f.formcode),f.form_type into formCodeEntity,formTypeParam from fg_form f where f.formcode = formCode_in;

      select t.userrole_id into systemuser_ from fg_s_user_pivot t where t.username = 'system';

      if formDateExp_ is not null then
         fromFormIdWherePart := ' and t.timestamp >= ' || formDateExp_ || ' ';
      end if;

      sql_check := 'select count(*) from FG_S_' || upper(formCodeEntity) || '_PIVOT t where rownum = 1 ' || fromFormIdWherePart;
      insert into fg_debug(comments,comment_info) values (sql_check,'sql_check formCode: ' || formCode_in); commit;
      execute immediate sql_check into count_;

      if count_ = 0 then
        return 1;
      end if;

    if is_popup = 1 then
       if formDateExp_ is not null then
        --SET PATH_ID (PARENTID) IN INF_V
        sql_path := '
        update fg_formlastsavevalue_inf f set f.path_id = (
        select distinct t.parentid
        from fg_s_' || formCode_in || '_pivot t
        where t.formid = f.formid
        and   f.active = 1 and t.active = 1
        ' || fromFormIdWherePart || '
        and   t.parentid is not null
        and   nvl(f.path_id,1) = 1)
        where exists ( select 1
               from fg_s_' || formCode_in || '_pivot t
               where t.formid = f.formid
               and   f.active = 1 and t.active = 1
               ' || fromFormIdWherePart || '
               and   t.parentid is not null
               and   nvl(f.path_id,1) = 1)';
        insert into fg_debug(comments,comment_info) values (sql_path,'sql_path(popup) formCode: ' || formCode_in);
        commit;
        execute immediate sql_path;
      end if;

      select COUNT(*) INTO count_ from user_tab_columns t where t.TABLE_NAME = 'FG_S_' || UPPER(formCodeEntity) || '_PIVOT' AND T.COLUMN_NAME = 'TABLETYPE';

      if count_ > 0 then
        --set name / type / CHANGEDATE
        sql_path := '
        update Fg_Sequence f set (f.formidname,f.formtabletype,f.formcode,f.comments) = (
        select distinct t.' || formCodeEntity || 'name,t.tabletype,NVL(f.formcode,t.formCode),''system task '' || decode(f.formcode,'' '',''update from code from pivot because of null'')
        from fg_s_' || formCodeEntity || '_pivot t
        where to_char(t.formid) = to_char(f.id)
        AND(f.formidname || '','' || f.formcode) <> (nvl(t.' || formCodeEntity || 'name,''NA'') || '','' || NVL(f.formcode,t.formCode))
        ' || fromFormIdWherePart || '
        and t.sessionid is null and active =1
        )
        where exists ( select 1
               from fg_s_' || formCodeEntity || '_pivot t
               where to_char(t.formid) = to_char(f.id)
               AND(f.formidname || '','' || f.formcode) <> (nvl(t.' || formCodeEntity || 'name,''NA'') || '','' || NVL(f.formcode,t.formCode))
               ' || fromFormIdWherePart || '
               and   t.sessionid is null and active =1 )';
        insert into fg_debug(comments,comment_info) values (sql_path,'sql_path2 (popup - table type update) formCode: ' || formCode_in);
        commit;
        execute immediate sql_path;
     ELSE
        dbms_output.put_line('FG_SET_INF_MISSING_ROW_DATA - WARNING NO TABLETYPE in FG_S_' || UPPER(formCodeEntity) || '_PIVOT');
         --set name / type / CHANGEDATE
        sql_path := '
        update Fg_Sequence f set (f.formidname,f.formtabletype,f.formcode,f.comments) = (
        select distinct nvl(substr(t.' || formCodeEntity || 'name,400),''NA''),''NA'',NVL(f.formcode,t.formCode),''system task '' || decode(f.formcode,'' '',''update from code from pivot because of null'')
        from fg_s_' || formCodeEntity || '_pivot t
        where to_char(t.formid) = to_char(f.id)
        AND(f.formidname || '','' || f.formcode) <> (nvl(t.' || formCodeEntity || 'name,''NA'') || '','' || NVL(f.formcode,t.formCode))
        ' || fromFormIdWherePart || '
        and t.sessionid is null and active =1
        )
        where exists ( select 1
               from fg_s_' || formCodeEntity || '_pivot t
               where to_char(t.formid) = to_char(f.id)
               AND(f.formidname || '','' || f.formcode) <> (nvl(t.' || formCodeEntity || 'name,''NA'') || '','' || NVL(f.formcode,t.formCode))
               ' || fromFormIdWherePart || '
               and   t.sessionid is null and active =1 )';
        insert into fg_debug(comments,comment_info) values (sql_path,'sql_path2 (popup - NO table type update) formCode: ' || formCode_in);
        commit;
        execute immediate sql_path;
      end if;

    else
      --set name / CHANGEDATE
      sql_path := '
      update Fg_Sequence f set (f.formidname,f.formcode, f.comments) = (
      select distinct t.' || formCodeEntity || 'name,NVL(f.formcode,t.formCode),''system task '' || decode(f.formcode,'' '',''update from code from pivot because of null'')
      from fg_s_' || formCodeEntity || '_pivot t
      where to_char(t.formid) = to_char(f.id)
      AND(f.formidname || '','' || f.formcode) <> (nvl(t.' || formCodeEntity || 'name,''NA'') || '','' || NVL(f.formcode,t.formCode))
      ' || fromFormIdWherePart || '
      and t.sessionid is null and active =1)
      where exists ( select 1
             from fg_s_' || formCodeEntity || '_pivot t
             where to_char(t.formid) = to_char(f.id) and t.active = 1
             AND(f.formidname || '','' || f.formcode) <> (nvl(t.' || formCodeEntity || 'name,''NA'') || '','' || NVL(f.formcode,t.formCode))
             ' || fromFormIdWherePart || '
             and   t.sessionid is null)';
      insert into fg_debug(comments,comment_info) values (sql_path,'sql_path (STRUCT) formCode: ' || formCode_in);
      commit;
      execute immediate sql_path;

    end if;
    commit;

    return 1;
  EXCEPTION
        WHEN OTHERS THEN
          rollback;
          --FG_SEQUENCE_INSERT_TRIG enable IN EXCEPTION
          insert into fg_debug(comments,comment_info) values (null,'FG_SET_INF_COMPLETE_DATA (error) - exception in form ' || formCode_in); commit;
          dbms_output.put_line('FG_SET_INF_COMPLETE_DATA (error) - exception in form ' || formCodeEntity); commit;
          return -1;
  end;

---------------
-- UPDATE_AUTHEN_FORMPATH_TMP/_G -- UPDATE UPDATE_AUTHEN_FORMPATH_TMP(_G - global temp table for each session if fromFormId_in defined)
---------------
  PROCEDURE UPDATE_AUTHEN_FORMPATH_TMP(formCodeCsv_in varchar2,  formDateExp_ varchar2) AS
    sql_ varchar2(32767);
   -- sql2_ varchar2(32767);
    nl_ varchar2(10) := chr(10);
    optionalWherePart varchar2(500);
    lastFormId_ number;
  BEGIN


      if formDateExp_ is not null then
        lastFormId_ := FG_GETLASTRUNFORMID;
        if lastFormId_ is null then
           insert into fg_debug(comments,comment_info) values(null,'UPDATE_AUTHEN_FORMPATH_TMP no update needed!'); commit;
           return;
        end if;
        optionalWherePart := ' and t.@ENTITY@_ID >= ' || lastFormId_;
      end if;

      for r in (
         select distinct t1.TABLE_NAME, upper(f.formcode_entity) as table_entity, f.formcode
         from
         (
           select t.TABLE_NAME, replace(replace(t.TABLE_NAME,'FG_AUTHEN_',''),'_V','') as form_code from user_tab_columns t
           where t.TABLE_NAME like 'FG_AUTHEN_%_V' AND T.COLUMN_NAME = 'FORMPATH'
           and   t.TABLE_NAME <> 'FG_AUTHEN_SELFTESTMAIN_V'
         ) t1,
         fg_form f
         where upper(f.formcode) = upper(t1.form_code)
         and   instr(',' || upper(NVL(nullif(formCodeCsv_in,'ALL'),f.formcode)) || ',',',' || upper(f.formcode) || ',') <> 0
      )
      loop
        sql_ :=  sql_ || ' select distinct t.' || r.table_entity || '_ID as formPath_id, ''' || r.formcode || ''' as formPath_formcode, t.formPath as formPath_value from ' || r.table_name || ' t where 1=1 ' || REPLACE(optionalWherePart,'@ENTITY@',r.table_entity) || ' union all ' || nl_;
        --dbms_output.put_line ('select distinct t.' || r.table_entity || '_ID as formPath_id, ''' || r.formcode || ''' as formPath_formcode, t.formPath as formPath_value from ' || r.table_name || ' t union all');
      end loop;

      if sql_ is not null and LENGTH(sql_) > 0 then
            sql_ := substr(sql_,0,LENGTH(sql_) - LENGTH('union all ' || nl_));

            truncate_it('FG_AUTHEN_FORMPATH_TMP_G');
            sql_ := 'INSERT INTO FG_AUTHEN_FORMPATH_TMP_G(formPath_id,formPath_formcode,formPath_value) SELECT formPath_id,formPath_formcode,formPath_value FROM (' || sql_ || ')';

            insert into fg_debug(comments,comment_info) values(sql_,'FG_AUTHEN_FORMPATH_TMP_G insert'); commit;
            EXECUTE IMMEDIATE sql_;
            commit;
      end if;
  end;

---------------
-- FG_SET_SERACH_INIT_DATA_ALL
---------------
  FUNCTION FG_SET_INF_INIT_DATA_ALL (DB_TRANSACTION_ID_IN VARCHAR2,
                                     exe_missing_row_in number default 0,
                                     exe_complete_data_in number default 0,
                                     onLastChangesFlag_in number default 0,
                                     correct_all_name_path_obj_in number default 0) return number as
  dummyNum number;
  formCodeCsv_in varchar2(30000) := 'NA';
  --formDate_in date := formDateInput_in;
  fromFormNumId number;
  formDateExp_ varchar2(100) := null;
  c_ number;
  indexDebug number := 1;
  isReset number := 0;

  begin
    truncate_it('FG_DEBUG');

    if onLastChangesFlag_in = 1 then

      -- mark the formcode we work on...
      update fg_form_change_list set UPDATE_FLAG = 1;

      --get from ids that created from last change
      fromFormNumId := FG_GETLASTRUNFORMID;

      update fg_sequence t set t.formidname = 'NA' where t.formidname is null;

      --get formDateExp
      select count(*) into c_ from FG_SYS_SCHED t where upper(t.sched_name) = 'CORRECTRECENTSEARCHDATA';   --CORRECTRECENTSEARCHDATA
      if c_ > 0 then
         select 'to_date(''' || to_char(max(t.start_date_success_holder),'dd/MM/yyyy HH24:MI:SS')|| ''',''dd/MM/yyyy HH24:MI:SS'')' into formDateExp_ from FG_SYS_SCHED t where upper(t.sched_name) = 'CORRECTRECENTSEARCHDATA';
      else
        formDateExp_ := '(sysdate - 1/24)';
      end if;

      --work only on forms that changed
      FOR C in  (
          select distinct FORMCODE from (
          SELECT upper(FORMCODE) as FORMCODE FROM fg_form_change_list where UPDATE_FLAG = 1
          union all
          select upper(t.formcode) from fg_sequence t where t.id >= fromFormNumId)
      )
      LOOP
          formCodeCsv_in := formCodeCsv_in || ',' || C.FORMCODE;
      END LOOP;
    else
          formCodeCsv_in := 'ALL';
          EXECUTE IMMEDIATE ' ALTER TRIGGER FG_FORMLASTSAVE_INF_I_TRIG disable ';
          EXECUTE IMMEDIATE ' ALTER TRIGGER FG_FORMLASTSAVE_INF_U_TRIG disable ';
    end if;

    DBMS_OUTPUT.put_line('formCodeCsv_in = ' || formCodeCsv_in);
    insert into FG_DEBUG (COMMENT_INFO) values ('formCodeCsv_in = ' || formCodeCsv_in);
    
    --if no rows in FG_FORMLASTSAVEVALUE_INF loop with reset 1;
    select count(*) INTO C_ from FG_FORMLASTSAVEVALUE_INF t where rownum = 1;
    if C_ = 0 then
     isReset := 1;
    end if;

    FOR r in  (
            select * from (
              select distinct NVL(T.FORMCODE_ENTITY,t.formcode) AS FORMCODE_ENTITY,  
                     t.formcode,decode(t.form_type,'ATTACHMENT',1,'REF',1,'SELECT',1,0) as is_popup, 
                     DECODE(formCodeCsv_in,'ALL',1,instr(',' || upper(formCodeCsv_in) || ',',',' || upper(t.formcode) || ',')) as isFromFormIdFormCode
              from fg_form t,
                   USER_TABLES UT
              where t.form_type in ('STRUCT','INVITEM','ATTACHMENT','REF','SELECT','MAINTENANCE')
              AND UT.TABLE_NAME = 'FG_S_' || upper(T.FORMCODE_ENTITY) || '_PIVOT'
              and t.formcode <> 'BatchFrSelect'
              --and t.formcode not in ( 'ElementDemo1','SpecificationRef','ExcelTemplate') -- not need we check inside the function if the tables are empty - this was the purpose of this line
              --AND DECODE(formCodeCsv_in,'ALL',1,instr(',' || upper(formCodeCsv_in) || ',',',' || upper(t.formcode) || ',')) <> 0
            ) order by is_popup -- we want the main forms before the popups
      )
      LOOP
        --insert into FG_DEBUG (COMMENT_INFO) values ('loop log inf all - formcode =' || r.formcode); commit;
        --missing_row
       
        insert into FG_DEBUG (COMMENT_INFO) values ((indexDebug + 1000) || ' [loop - 1-start - fromcode:' ||  r.formcode || ', is popup:' || r.is_popup || ',formDateExp_ =' || formDateExp_ ||  ',isReset=' || isReset || ']');
        commit;
        if upper(R.FORMCODE_ENTITY) = upper(R.FORMCODE) and exe_missing_row_in = 1 and (r.isFromFormIdFormCode <> 0 or correct_all_name_path_obj_in = 1) then
          --if formCodeCsv_in <> 'ALL' then -- !!! this should be run only one time on ALL the data (and that is if some mager changes made in the form builder)
            DBMS_OUTPUT.put_line('LOOP FG_SET_INF_MISSING_ROW_DATA FORMCODE=' || r.formcode);
            dummyNum:= FG_SET_INF_MISSING_ROW_DATA(r.formcode, nvl(DB_TRANSACTION_ID_IN,'999'), formDateExp_, r.is_popup, isReset);
          --end if;
        end if;

        --complete data
        if exe_complete_data_in = 1 then
           DBMS_OUTPUT.put_line('LOOP FG_SET_INF_COMPLETE_DATA FORMCODE=' || r.formcode);
           if correct_all_name_path_obj_in = 1 then
              dummyNum:=FG_SET_INF_COMPLETE_DATA(r.formcode, r.is_popup, null, indexDebug);
           else
              dummyNum:=FG_SET_INF_COMPLETE_DATA(r.formcode, r.is_popup, formDateExp_, indexDebug);
           end if;
        end if;
        insert into FG_DEBUG (COMMENT_INFO) values ((indexDebug + 1000) || ' [loop - 2-end - fromcode:' ||  r.formcode || ']');
        DBMS_OUTPUT.put_line('LOOP ' || r.formcode);
        indexDebug:= indexDebug + 1;
      END LOOP;
      
      EXECUTE IMMEDIATE ' ALTER TRIGGER FG_FORMLASTSAVE_INF_I_TRIG enable ';
      EXECUTE IMMEDIATE ' ALTER TRIGGER FG_FORMLASTSAVE_INF_U_TRIG enable ';

      if correct_all_name_path_obj_in = 1 then
         formCodeCsv_in := 'ALL';
         formDateExp_ := null;
      end if;

      FG_ADAMA_TASK_BY_DATE.UPDATE_AUTHEN_FORMPATH_TMP (formCodeCsv_in,formDateExp_);

      update fg_sequence t set t.formpath = (
         select distinct p.formpath_value
         from FG_AUTHEN_FORMPATH_TMP_G p
         where t.id = p.formpath_id
         --and   t.formcode = formCode_in
         and   DECODE(formCodeCsv_in,'ALL',1,instr(',' || upper(formCodeCsv_in) || ',',',' || upper(t.formcode) || ',')) <> 0
         and   upper(t.formcode) = upper(p.formpath_formcode)
         and   p.formpath_id is not null
         and   nvl(t.formpath,'na1') <> nvl(p.formpath_value,'na2') -- if one null so update
         --and   t.changedate >= formDate_in
      ) where exists (
         select distinct p.formpath_value
         from FG_AUTHEN_FORMPATH_TMP_G p
         where t.id = p.formpath_id
         --and   t.formcode = formCode_in
         and   DECODE(formCodeCsv_in,'ALL',1,instr(',' || upper(formCodeCsv_in) || ',',',' || upper(t.formcode) || ',')) <> 0
         and   upper(t.formcode) = upper(p.formpath_formcode)
         and   p.formpath_id is not null
         and   nvl(t.formpath,'na1') <> nvl(p.formpath_value,'na2') -- if one null so update
         --and   t.id >= formDate_in
      );
      COMMIT;

      --fg_form_change_list
      delete from fg_form_change_list where fg_form_change_list.update_flag = 1;
      commit;

      return 1;
  end;

-----------------
-- FG_FIX_CHEM_DOODLE_DATA
-----------------
  function FG_FIX_CHEM_DOODLE_DATA return number as
       counter_ number;
  begin
       select count(*) into counter_
       from FG_CHEM_DOODLE_DATA t
       where t.reaction_all_data_link is null
       and t.reaction_all_data is not null;

      if counter_ > 0 then
          --update field reaction_all_data_link in fg_chem_doodle_data with unique numbers
          update FG_CHEM_DOODLE_DATA t
          set t.reaction_all_data_link = FG_GET_STRUCT_FILE_ID('MOL_DATA_ID')
          where t.reaction_all_data_link is null
                and t.reaction_all_data is not null;

          --insert data to fg_clob_files
          insert into fg_clob_files (file_id,file_content,

                                     --TODO: delete next field after Testing
                                     content_type

                                     )
          select t.reaction_all_data_link, t.reaction_all_data,

                 --TODO: delete next value after Testing
                 'test'

          from FG_CHEM_DOODLE_DATA t
          where t.reaction_all_data is not null;

          --remove data from field reaction_all_data in table fg_chem_doodle_data
          update FG_CHEM_DOODLE_DATA t
          set t.reaction_all_data = null
          where t.reaction_all_data_link is not null
                and t.reaction_all_data is not null;

          commit;
      end if;
      return 1;
  end;

  FUNCTION FG_HANDLE_INFID_FOR_SEARCH return number AS
  BEGIN
    RETURN 1;
  END;


 FUNCTION POST_SAVE_AT (formCode_in VARCHAR2,
                         formCodeEntity_in VARCHAR2,
                         formId_in VARCHAR2,
                         userId_in VARCHAR2,
                         dbTransactionId_in VARCHAR2,
                         auditTrailChangeType_in VARCHAR2 default null) return number
  aS
       count_ number;
       ver_ varchar(100);
       --unittestFileCounter number;
     -- is_path number;
       --sql_path varchar(1000);
 begin
    dbms_output.put_line('start...formCode_in=' || formCode_in || ' dbTransactionId_in=' || dbTransactionId_in);

    /*--SET PATH IN FG_SEQUENCE if exists
    --..check if exists
    select COUNT(*) INTO is_path from user_tab_columns t where t.TABLE_NAME = 'FG_AUTHEN_' || UPPER(formCode_in) || '_V' AND T.COLUMN_NAME = 'FORMPATH';

    --..set the path
    if is_path > 0 then
      sql_path :=
      'UPDATE FG_SEQUENCE S SET S.FORMPATH = (SELECT distinct A.formPath FROM FG_AUTHEN_' || formCode_in || '_V A WHERE TO_CHAR(A.' || formCodeEntity_in || '_ID ) =  TO_CHAR(S.ID))
      WHERE TO_CHAR(S.ID) = ''' || formId_in || '''';
      execute immediate sql_path;
    end if;
    commit;*/

    --IF UPPER(formCodeEntity_in) = 'DOCUMENT' THEN
      /*if nvl(userId_in,0) = 100 then
        select count(*) into unittestFileCounter from fg_s_document_pivot t where 1=1\*t.formid = formId_in*\ and t.documentupload is null and t.link_attachment = 'Attachment' and t.change_by = 100;
        if unittestFileCounter > 0 then
          update fg_s_document_pivot t set t.documentupload = 100, t.description = '_unittest update in POST_SAVE_AT DB Procedure' where 1=1\* t.formid = formId_in*\ and t.documentupload is null and t.link_attachment = 'Attachment' and t.change_by = 100;
          update fg_formlastsavevalue_inf t set t.entityimpvalue = 100 where lower(t.entityimpcode) = 'documentupload' and t.formid = formid_in and t.change_by = 100 and t.entityimpvalue is null;
          update fg_formlastsavevalue_inf t set t.entityimpvalue = '_unittest update in POST_SAVE_AT DB Procedure' where lower(t.entityimpcode) = 'description' and t.change_by = 100;
        end if;
      end if;*/
    --END IF;

    if UPPER(formCodeEntity_in) = 'EXPERIMENT' THEN

      -- check if we need to save data on new version change Note: the version is the new version (before changes)
      select count(*) into count_
      from fg_s_experiment_pivot t
      where t.formid = formId_in
      and nvl(t.experimentversion ,'01') <> '01'
      and (t.formid, t.experimentversion) not in (
          select t1.formid, t1.comments
          from FG_FORMLASTSAVEVALUE_INF_SSM t1
          where t1.snapshot_type ='EXPERIMENT_VERSION'
          and   t1.formid = formId_in
      );

      if count_ > 0 then
                --get the new versaion number
                select t.experimentversion into ver_
                from fg_s_experiment_pivot t
                where t.formid = formId_in;

                --insert the formid of all of level under experiment
                insert into FG_FORMLASTSAVEVALUE_INF_SSM (formid,formid_ref,comments,SNAPSHOT_TYPE,change_date,change_by)
                select distinct formId_in, t.formid,ver_,'EXPERIMENT_VERSION', sysdate, userId_in
                from fg_formlastsavevalue_inf t
                where  upper(t.entityimpcode) = 'EXPERIMENT_ID'
                and   nvl(t.entityimpvalue,-1) = TO_CHAR(formId_in)
                and   upper(t.formcode_entity) in ('STEP','ACTION','SELFTEST','WORKUP');

                --insert the experiment id
                insert into FG_FORMLASTSAVEVALUE_INF_SSM (formid,formid_ref,comments,SNAPSHOT_TYPE,change_date,change_by)
                values( formId_in, formId_in,ver_,'EXPERIMENT_VERSION', sysdate, userId_in);

                --insert all ref forms
                insert into FG_FORMLASTSAVEVALUE_INF_SSM (formid,formid_ref,comments,SNAPSHOT_TYPE,change_date,change_by)
                select distinct formId_in, t.formid,ver_,'EXPERIMENT_VERSION', sysdate, userId_in
                from fg_formlastsavevalue_inf t
                where 1=1
                and exists (select 1
                              from  FG_FORMLASTSAVEVALUE_INF_SSM sm1
                              where nvl(t.Path_Id,-1) = sm1.formid_ref
                              and   sm1.formid = formId_in
                              and   sm1.comments = ver_ )
                and   upper(t.formcode_entity) not in ('STEP','ACTION','SELFTEST','WORKUP');


                -- insert to ss all the data
                insert into fg_formlastsavevalue_inf_ss ( id,
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
                                                            displayvalue,
                                                            updatejobflag,
                                                            displaylabel,
                                                            path_id,
                                                            is_file,
                                                            is_idlist,
                                                            db_transaction_id,
                                                            tmp_entityimpvalue,
                                                            tmp_displayvalue,
                                                            experiment_id,
                                                            log_date,
                                                            log_version)
                  select null,
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
                          displayvalue,
                          updatejobflag,
                          displaylabel,
                          path_id,
                          is_file,
                          is_idlist,
                          null,
                          null,
                          null,
                          formId_in,
                          sysdate,
                          ver_
                  from fg_formlastsavevalue_inf_t_v t
                  where exists ( select 1
                                      from FG_FORMLASTSAVEVALUE_INF_SSM t1
                                      where t1.snapshot_type = 'EXPERIMENT_VERSION'
                                      and   t1.comments = ver_
                                      and   t1.formid = formId_in
                                      and   t.formid = t1.formid_ref);
      end if;
    END IF;
    commit;
    return 1;
  end;

 function FG_SET_DIPLAY_VALUE (onLastChangesFlag_in number) RETURN NUMBER as
 begin
   --empty rich text when onLastChangesFlag_in = 0 (it can be improve by do it also when 1)
   if onLastChangesFlag_in = 0 then
          update FG_FORMLASTSAVEVALUE_inf f set f.displayvalue = null, f.change_comment = 'system: display value update (to empty) by task (FG_SET_DIPLAY_VALUE)' where f.id in (
          select t.id
          from FG_FORMLASTSAVEVALUE_inf t,
               FG_FORMELEMENTINFOATMETA_MV m,
               fg_richtext r
          where 1=1
          and t.active = 1
          and upper(m.formcode_entity) = upper(t.formcode_entity)
          and upper(m.entityimpcode) = upper(t.entityimpcode)
          and t.entityimpvalue is not null
          and t.displayvalue is not null
          and t.displayvalue = t.entityimpvalue
          and m.elementclass IN ('ElementRichTextEditorImp')
          and r.file_id = t.entityimpvalue
          and dbms_lob.compare(r.file_content_text,empty_clob()) = 0);
          commit;

          truncate_it('FG_FORMLASTSAVEVALUE_INF_RT');

          insert into FG_FORMLASTSAVEVALUE_INF_RT (ID,RT_TEXT)
          select distinct t.id ,fg_get_richtext_display(t.entityimpvalue) as rt_text
          from FG_FORMLASTSAVEVALUE_inf t,
               FG_FORMELEMENTINFOATMETA_MV m,
               fg_richtext r
          where 1=1
          and t.active = 1
          and upper(m.formcode_entity) = upper(t.formcode_entity)
          and upper(m.entityimpcode) = upper(t.entityimpcode)
          and t.entityimpvalue is not null
          and t.displayvalue is not null
          and t.displayvalue = t.entityimpvalue
          and m.elementclass IN ('ElementRichTextEditorImp')
          and r.file_id = t.entityimpvalue
          and dbms_lob.compare(r.file_content_text,empty_clob()) <> 0;
          commit;

          update FG_FORMLASTSAVEVALUE_inf t set t.change_comment = 'system: display value update by task (FG_SET_DIPLAY_VALUE)', t.displayvalue = (
          select r.rt_text
          from FG_FORMLASTSAVEVALUE_inf_rt r
          where r.id = t.id)
          where exists (select 1
                        from FG_FORMLASTSAVEVALUE_inf_rt r
                        where r.id = t.id);
          commit;
  end if;

  return 1;
 end;

 function FG_SET_SERACH_HANDEL_INF_ID (DB_TRANSACTION_ID_IN VARCHAR2) RETURN NUMBER as
begin
  delete from FG_FORMLASTSAVEVALUE_INF_IDTMP;
  delete from FG_FORMLASTSAVEVALUE_INF where is_idlist = 2;

  INSERT INTO FG_FORMLASTSAVEVALUE_INF_IDTMP
  SELECT * FROM FG_FORMLASTSAVEVALUE_INF T
  WHERE 1=1--T.CHANGE_DATE > SYSDATE - 3
  AND   nvl(T.is_idlist,0) = 1
  and   T.entityimpvalue is not null
  AND  T.ENTITYIMPVALUE LIKE '%,%';

 for r in (
   select * from FG_FORMLASTSAVEVALUE_INF_IDTMP t where 1=1
 )
 loop
   insert into FG_FORMLASTSAVEVALUE_INF (
      --id,
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
      displayvalue,
      updatejobflag,
      displaylabel,
      path_id,
      is_file,
      is_idlist,
      DB_TRANSACTION_ID
   )
   select -- r.id,
      r.formid,
      r.formcode_entity,
      r.entityimpcode,
      t1.column_value,  -- column_value as entityimpvalue
      r.userid,
     -- to_char(r.id) || '-' || r.entityimpvalue,--
      r.change_comment,
      r.change_id,
      r.change_by,
      r.change_type,
      r.change_date,
      r.sessionid,
      r.active,
      null displayvalue,
      r.updatejobflag,
      r.displaylabel,
      r.path_id,
      r.is_file,
      2 is_idlist,
      r.DB_TRANSACTION_ID
   from  table(FN_LIST_TO_TABLE(r.ENTITYIMPVALUE)) t1;
 end loop;
 return 1;
end;

 function DB_CLEANUP return number as
 begin
  -- UNLOCK SYSTEM / ADMIN USERS
  update FG_S_USER_PIVOT t
  set t.passworddate = to_char(sysdate,'dd/MM/yyyy'),
      t.chgpassworddate = to_char(sysdate,'dd/MM/yyyy'),
      t.lastpassworddate = to_char(sysdate,'dd/MM/yyyy'),
      t.locked = 0,
      t.retrycount = 0
  where t.username in ('system','admin');
  commit;

  -- MOVE OLD LOG TO HST (2 WEEKS) and hold hst for last 3 month
  delete from fg_activity_log_hst t where TRUNC(T.TIMESTAMP) < TRUNC(SYSDATE) - 90;
  insert into fg_activity_log_hst select * from fg_activity_log t where upper(t.leveltype) not in('OTHER') and TRUNC(T.TIMESTAMP) < TRUNC(SYSDATE) - 5;
  delete from fg_activity_log t where upper(t.leveltype) not in('OTHER') and TRUNC(T.TIMESTAMP) < TRUNC(SYSDATE) - 5;
  commit;
   
  truncate_it('FG_FORMLASTSAVEVALUE_INF_SSTMP');
  
  INSERT INTO FG_FORMLASTSAVEVALUE_INF_SSTMP
  SELECT DISTINCT * FROM FG_FORMLASTSAVEVALUE_INF_SS;
  
  COMMIT;
  
  truncate_it('FG_FORMLASTSAVEVALUE_INF_SS');
  
  INSERT INTO FG_FORMLASTSAVEVALUE_INF_SS
  SELECT * FROM FG_FORMLASTSAVEVALUE_INF_SSTMP;
  
  truncate_it('FG_FORMLASTSAVEVALUE_INF_SSTMP');
  
  COMMIT; 
  
  -- clean FG_RECIPE_MATERIAL_FUNC_REPORT - yp 29032021
  delete from FG_RECIPE_MATERIAL_FUNC_REPORT t where t.timestamp < sysdate -1;
  
  COMMIT; 

  --DONE!
  return 1;
 end;


end;
/
