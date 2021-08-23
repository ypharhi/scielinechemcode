CREATE OR REPLACE FUNCTION FG_SET_STRUCT_PIVOT_TABLE (formCode_in varchar2, dropAndCreateTable_in number default 1, formid_in varchar2 default null) return number as

    is_pivot_exists number;
    
    is_pivot_bu_exists number;

    col_list_pivot_statment varchar2(32767);

    --TYPE ref_cursor IS REF CURSOR;

    --cur REF_CURSOR;

    --cVal_ varchar(100);

    sql_pivot varchar2(32767);

    sql_create_BASIC_v varchar2(32767);

    sql_create_v varchar2(32767);

    isViewExists number;

    CREATE_VIEW_FLAG number := 0;
    CREATE_VIEW_FORMCODE_CSV_LIST varchar2(32767) := '';

    result number;

    formCodeEntity varchar2(100);

    formTypeParam varchar2(100);

    isDevelop number;

    formidToScript varchar2(100);
    
    systemuser_ varchar2(100);

    /*l_ varchar2(32767);
    lsql_ varchar2(32767);*/
    
    duplicationCheck number;
    
    --rollbackFlag number := 0;

begin
    delete from fg_debug;
    insert into fg_debug(comment_info,comments) values('start create table for formcode =' || formCode_in ,''); --commit;

    select nvl(f.formcode_entity,f.formcode),f.form_type into formCodeEntity,formTypeParam from fg_form f where f.formcode = formCode_in;
    
    select t.userrole_id into systemuser_ from fg_s_user_pivot t where t.username = 'system';

    select count(*) into is_pivot_exists
    from user_tables t
    where upper(t.TABLE_NAME) = upper('FG_S_' || upper(formCodeEntity) || '_PIVOT');

    select nvl(t.is_develop,0) into isDevelop from fg_sys_param t;
    if /*override_is_develop_flag = 1 or*/ isDevelop <> 1 then
      insert into fg_debug(comments) values('not develop');
      raise_application_error( -20001, 'Error in FG_SET_STRUCT_PIVOT_TABLE! try to re-create table when fg_sys_param.isDevelop is not in develop mode');
      return 0;
    end if;
    
    select COUNT(*) into duplicationCheck from (
    select distinct  t.id, t.entityimpcode, t.formcode,
    count(*) over (partition by upper(t.entityimpcode)) c1, count(*) over (partition by t.entityimpcode) c2 
    from fg_formentity t 
    where t.formcode in (select f.formcode from fg_form f where UPPER(f.formcode_entity) = upper(formCodeEntity))
    and t.entitytype = 'Element'
    )
    where c1 <> c2;
    
    if duplicationCheck > 0 then
      insert into fg_debug(comments) values('there is duplication the in column names');
      raise_application_error( -20001, 'Error in FG_SET_STRUCT_PIVOT_TABLE! there is duplication in the column names on the same formcode_entity!');
      return 0;
    end if;
    
    if is_pivot_exists = 1 and dropAndCreateTable_in = 1  then
    -------------------------------------
    -- updates the pivot table to get the additional columns that has been added to the form
    -------------------------------------
    for col in(
        select distinct upper(t.entityimpcode) as col_name
            from FG_FORMENTITY t
            where t.entitytype = 'Element'
            --remove not needed entityimpclass
            and t.entityimpclass not in ('ElementLabelImp','ElementIreportImp','ElementSmartSearchImp')--decode(formTypeParam,'General','NA','Report','NA','ElementDataTableApiImp'))
            -- incluse element also from other forms which "mapped" to this from (using the same formcode_entity)
            and t.formcode = formCode_in
            minus
            select upper(tc.COLUMN_NAME) from user_tab_columns tc where tc.TABLE_NAME = 'FG_S_' || upper(formCodeEntity) || '_PIVOT'
         )
         loop
           if col_list_pivot_statment is null then 
             col_list_pivot_statment:=col.col_name ||' VARCHAR2(' || fg_get_col_size(formCodeEntity, col.col_name)||')';
             else
              col_list_pivot_statment:=col_list_pivot_statment||','||col.col_name ||' VARCHAR2(' || fg_get_col_size(formCodeEntity, col.col_name)||')'; 
           end if;
         end loop;
         if col_list_pivot_statment is not null then
           insert into fg_debug(comment_info,comments) values('add columns to table FG_S_' || formCodeEntity||'_PIVOT' 
           ,'alter table FG_S_' || upper(formCodeEntity) || '_PIVOT add ('|| col_list_pivot_statment||')'); --commit;
           execute immediate 'alter table FG_S_' || upper(formCodeEntity) || '_PIVOT'
           || ' add ('|| col_list_pivot_statment||')';
           commit;
         end if;
        /*for cVal in (
          select distinct * from
            (select distinct upper(t.entityimpcode) as col_name
            from FG_FORMENTITY t
            where t.entitytype = 'Element'
            --remove not needed entityimpclass
            and t.entityimpclass not in ('ElementLabelImp','ElementIreportImp','ElementSmartSearchImp')--decode(formTypeParam,'General','NA','Report','NA','ElementDataTableApiImp'))
            -- incluse element also from other forms which "mapped" to this from (using the same formcode_entity)
            and t.formcode = formCode_in
            union all
            select upper(tc.COLUMN_NAME) from user_tab_columns tc where tc.TABLE_NAME = 'FG_S_' || upper(formCodeEntity) || '_PIVOT'
            )
          )
        LOOP
          ----dbms_output.put_line(  cVal.val_ );
          if col_list_pivot_statment is null then
            col_list_pivot_statment := cVal.col_name || ' as ''' ||  cVal.col_name || '''';
          else
            col_list_pivot_statment := col_list_pivot_statment || ',' || cVal.col_name || ' as ''' ||  cVal.col_name || '''';
          end if;
        END LOOP;*/
    end if;
    
    col_list_pivot_statment := null;
    
    -------------------------------------
    --arrange last save data with production <-> develop correction
    -------------------------------------
    for cVal in (
                    select distinct t.entityimpcode as val_
                    from FG_FORMENTITY t
                    where t.entitytype = 'Element'
                    --remove not needed entityimpclass
                    and t.entityimpclass not in ('ElementLabelImp','ElementIreportImp','ElementSmartSearchImp')--decode(formTypeParam,'General','NA','Report','NA','ElementDataTableApiImp'))
                    -- incluse element also from other forms which "mapped" to this from (using the same formcode_entity)
                    and  (t.formcode in (select f1.formcode from fg_form f1 where f1.formcode_entity = formCodeEntity)
                         OR t.formcode = formCode_in)
                    -- remove element with ADDITIONALDATA form builder definition
                    and not exists (select 1 from FG_FORMENTITY t2 WHERE t.id = t2.id and upper(T2.ENTITYIMPINIT) LIKE '%"ADDITIONALDATA":TRUE%')
                    and (
                          is_pivot_exists = 0
                          or
                          1 = (select decode(count(*),0,0,1) from user_tab_columns tc where tc.TABLE_NAME = 'FG_S_' || upper(formCodeEntity) || '_PIVOT' and tc.COLUMN_NAME = upper(t.entityimpcode))
                        )
                  )
    LOOP
      ----dbms_output.put_line(  cVal.val_ );
      if col_list_pivot_statment is null then
        col_list_pivot_statment := cVal.val_ || ' as ''' ||  cVal.val_ || '''';
      else
        col_list_pivot_statment := col_list_pivot_statment || ',' || cVal.val_ || ' as ''' ||  cVal.val_ || '''';
      end if;

     -- insert into fg_debug(comments) values (cVal.val_ || ' as ''' ||  cVal.val_ || '''');
      --commit;
    END LOOP;

    delete from FG_FORMLASTSAVEVALUE_UNPIVOT t where t.formcode_entity = formCodeEntity and t.formid <> nvl(formid_in,'-1');

    --commit;

        if is_pivot_exists = 1 then
      
      execute immediate '
      update FG_FORMLASTSAVEVALUE_UNPIVOT t set (t.cloneid,t.templateflag) = (
      select t1.cloneid,t1.templateflag from fg_s_' || formCodeEntity || '_pivot t1 where t1.formid = ''' || formid_in || ''' )
      where t.formid = ''' || formid_in || '''';
      commit;

      if formid_in is null then
        formidToScript := '-1';
      else
        formidToScript := formid_in;
      end if;

      sql_pivot := '
      insert into FG_FORMLASTSAVEVALUE_UNPIVOT
                  (id,
                  formid,
                  formcode_entity,
                  entityimpcode,
                  entityimpvalue,
                  userid,
                  sessionid,
                  active,
                  --formidscript,
                  formcode_name,
                  created_by,
                  creation_date,
                  timestamp,
                  change_by,
                  CLONEID,
                  TEMPLATEFLAG )
      select  null,
              formid,
              ''' || formCodeEntity || ''',
              entityimpcode,
              entityimpvalue,
              nvl(change_by,''' || systemuser_ || ''') as userid,
              sessionid,
              active,
              --formidscript,
              formcode,
              created_by,
              creation_date,
              timestamp,
              change_by,
              CLONEID,
              TEMPLATEFLAG
      from FG_S_' || upper(formCodeEntity) || '_PIVOT unpivot include nulls
       (entityimpvalue for(entityimpcode) in
         (
                 ' ||       col_list_pivot_statment || '
         )
        ) where active >= 0 and formid <> ' || formidToScript || ' ';
        insert into fg_debug(comments) values (sql_pivot); commit;
        execute immediate sql_pivot;
    end if;

    --clean col_list_pivot_statment
    col_list_pivot_statment := '';

    --commit;

    -------------------------------------
    -- done arrange last save
    -------------------------------------


    --update CREATE_VIEW_FLAG
    select decode(instr( ',' || CREATE_VIEW_FORMCODE_CSV_LIST || ','
                        , ',' || formCode_in || ',')
                  ,0,0,1) into CREATE_VIEW_FLAG
    from dual;

    for cVal in (
                    select distinct t.entityimpcode as val_
                    from FG_FORMENTITY t
                    where t.entitytype = 'Element'
                    --remove not needed entityimpclass
                    and t.entityimpclass not in ('ElementLabelImp','ElementIreportImp','ElementSmartSearchImp')--decode(formTypeParam,'General','NA','Report','NA','ElementDataTableApiImp'))
                    -- incluse element also from other forms which "mapped" to this from (using the same formcode_entity)
                    and  (t.formcode in (select f1.formcode from fg_form f1 where f1.formcode_entity = formCodeEntity)
                         OR t.formcode = formCode_in)
                    -- remove element with ADDITIONALDATA form builder definition
                    and not exists (select 1 from FG_FORMENTITY t2 WHERE t.id = t2.id and upper(T2.ENTITYIMPINIT) LIKE '%"ADDITIONALDATA":TRUE%')
                  )
    LOOP
      ----dbms_output.put_line(  cVal.val_ );
      col_list_pivot_statment := col_list_pivot_statment || ',''' || cVal.val_ || ''' as ' ||  cVal.val_;
    END LOOP;


    sql_pivot := '
    SELECT * FROM
    (select "FORMID" as formId,
           first_value(t.TIMESTAMP) over (partition by "FORMID" order by t.id desc) as "TIMESTAMP",
           first_value(t.CREATION_DATE) over (partition by "FORMID" order by t.id desc)  as CREATION_DATE,
           CLONEID,
           TEMPLATEFLAG,
           CAST(first_value(CHANGE_BY) over (partition by "FORMID" order by t.id desc) AS varchar2(100) ) as "CHANGE_BY",
           CAST(first_value(CREATED_BY) over (partition by "FORMID" order by t.id desc) AS varchar2(100) ) as "CREATED_BY",
           CAST(null AS varchar2(100) ) as "SESSIONID",
           first_value("ACTIVE") over (partition by "FORMID" order by t.id desc) AS "ACTIVE",
           CAST(first_value(FORMCODE_ENTITY) over (partition by "FORMID" order by t.id desc) AS varchar2(100) ) as FORMCODE_ENTITY,
           CAST(first_value(t.formcode_name) over (partition by "FORMID" order by t.id desc) AS varchar2(100) ) as "FORMCODE",
           "ENTITYIMPCODE" as col_,
           "ENTITYIMPVALUE" as val_
    from FG_FORMLASTSAVEVALUE_UNPIVOT t
    where t.FORMCODE_ENTITY = ''' || formCodeEntity || ''')
    PIVOT (max(val_) FOR col_ IN (' || substr(col_list_pivot_statment,2) || '))';

    --dbms_output.put_line('sql_pivot=' || sql_pivot);

      --  if col_list_pivot is not null then
      if is_pivot_exists = 1 and dropAndCreateTable_in = 1  then
          EXECUTE IMMEDIATE ' CREATE table ' || 'FG_S_' || upper(formCodeEntity) || '_BU AS SELECT * FROM FG_S_' || upper(formCodeEntity) || '_PIVOT';
          EXECUTE IMMEDIATE 'drop table ' || 'FG_S_' || upper(formCodeEntity) || '_PIVOT';
          --rollbackFlag := 1;
      end if;

      if dropAndCreateTable_in = 1 then
        insert into fg_debug(comment_info,comments) values(' CREATE table ' || 'FG_S_' || upper(formCodeEntity) || '_PIVOT' || ' AS ...', sql_pivot);--commit;
        EXECUTE IMMEDIATE ' CREATE table ' || 'FG_S_' || upper(formCodeEntity) || '_PIVOT' || ' AS ' || sql_pivot;
        --rollbackFlag := 0;
        
        select count(*) into is_pivot_bu_exists
        from user_tables t
        where upper(t.TABLE_NAME) = upper('FG_S_' || upper(formCodeEntity) || '_BU');
        
        if is_pivot_bu_exists = 1 then
          EXECUTE IMMEDIATE 'drop table ' || 'FG_S_' || upper(formCodeEntity) || '_BU';
        end if;
        
      end if;

      --commit;

      select count(*) into is_pivot_exists
      from user_tables t
      where upper(t.TABLE_NAME) = upper('FG_S_' || upper(formCodeEntity) || '_PIVOT');

      if is_pivot_exists = 0 then
        return 0; -- not exists
      end if;

      fg_set_col_size(formCodeEntity, 'FG_S_' || upper(formCodeEntity) || '_PIVOT');


      sql_create_BASIC_v := '
      create or replace view FG_S_' || upper(formCodeEntity) || '_V as
      select to_number(t.formid) as ' || lower(formCodeEntity) || '_id,
             t.formid || decode(nvl(t.sessionId,''-1''),''-1'',null, ''-'' || t.sessionId) || decode(nvl(t.active,1),0,''-0'') as form_temp_id,
             ''{"VAL":"'' || t.' || formCodeEntity || 'Name || ''","ID":"'' || t.formid || ''", "ACTIVE":"'' || nvl(t.active,1) || ''"}'' as ' || formCodeEntity || '_objidval,
             t.*
      from ' || 'FG_S_' || upper(formCodeEntity) || '_PIVOT' || ' t ';

    EXECUTE IMMEDIATE sql_create_BASIC_v;

    --update from code loop for views >= all_v
    for r in (
            select distinct upper(t.formcode) as formCode_uppercase,
                            t.formcode as formCode_
            from FG_FORM T
            WHERE 1=1
            -- incluse element also from other forms which "mapped" to this from (using the same formcode_entity)
            and  (t.formcode in (select f1.formcode from fg_form f1 where f1.formcode_entity = formCodeEntity)
                         OR t.formcode = formCode_in)
    )
    loop
            --CREATE 'ALL' VIEW IF NOT EXISTS (to avoid override views we develop!!!)
            select COUNT(*) into isViewExists
            from user_views t
            where t.VIEW_NAME = upper('FG_S_' || r.formCode_uppercase || '_ALL_V');

            if CREATE_VIEW_FLAG = 1 or isViewExists = 0 then
              sql_create_v := '
              create or replace view FG_S_' || r.formCode_uppercase || '_ALL_V as
              select t.*
                     --t.* end! edit only the code below...
              from FG_S_' || upper(formCodeEntity) || '_V t ';
              EXECUTE IMMEDIATE sql_create_v;
            else
              result := fg_set_struct_all_v('FG_S_' || r.formCode_uppercase || '_ALL_V');
              --dbms_output.put_line(result);
            end if;

            --.. insert to FG_RESOURCE
            select COUNT(*) into isViewExists
            from FG_RESOURCE t
            where t.code = 'FG_S_' || r.formCode_uppercase || '_ALL_V';

            if isViewExists = 0 then
                 INSERT INTO FG_RESOURCE (TYPE,CODE,VALUE,INFO) VALUES ('CATALOG_ORACLE_TABLE','FG_S_' || r.formCode_uppercase || '_ALL_V','FG_S_' || r.formCode_uppercase || '_ALL_V', r.formCode_uppercase || ' view. insert to FG_RESOURCE by FG_SET_STRUCT_PIVOT_TABLE func as ' || to_char(sysdate,'dd/MM/yyyy HH24:MI'));
            end if;


            --CREATE 'DT' (Data table) VIEW IF NOT EXISTS (to avoid override views we develop!!!)
            select COUNT(*) into isViewExists
            from user_views t
            where t.VIEW_NAME = upper('FG_S_' || r.formCode_uppercase || '_DT_V');

            if CREATE_VIEW_FLAG = 1 or isViewExists = 0 then
              sql_create_v := '
              create or replace view FG_S_' || r.formCode_uppercase || '_DT_V as
              select *
              from FG_S_' || r.formCode_uppercase || '_ALL_V t ';
              EXECUTE IMMEDIATE sql_create_v;
            end if;

            --CREATE 'INF' (Data table) VIEW IF NOT EXISTS (to avoid override views we develop!!!)
            select COUNT(*) into isViewExists
            from user_views t
            where t.VIEW_NAME = upper('FG_S_' || r.formCode_uppercase || '_INF_V');

            if CREATE_VIEW_FLAG = 1 or isViewExists = 0 then
              sql_create_v := '
              create or replace view FG_S_' || r.formCode_uppercase || '_INF_V as
              select ''' || r.formCode_ || ''' as formCode, t.formid as id, t.' || formCodeEntity || 'Name as name
              from FG_S_' || r.formCode_uppercase || '_ALL_V t ';
              EXECUTE IMMEDIATE sql_create_v;
            end if;

            --.. insert to FG_RESOURCE
            select COUNT(*) into isViewExists
            from FG_RESOURCE t
            where t.code = 'FG_S_' || r.formCode_uppercase || '_DT_V';

            if isViewExists = 0 then
                 INSERT INTO FG_RESOURCE (TYPE,CODE,VALUE,INFO) VALUES ('CATALOG_ORACLE_TABLE','FG_S_' || r.formCode_uppercase || '_DT_V','FG_S_' || r.formCode_uppercase || '_DT_V', r.formCode_uppercase || ' view. insert to FG_RESOURCE by FG_SET_STRUCT_PIVOT_TABLE func as ' || to_char(sysdate,'dd/MM/yyyy HH24:MI'));
            end if;

             --CREATE AUTHEN VIEW (Data table) VIEW IF NOT EXISTS (to avoid override views we develop!!!) --FG_AUTHEN_PROJECT_V
            select COUNT(*) into isViewExists
            from user_views t
            where t.VIEW_NAME = upper('FG_AUTHEN_' || r.formCode_uppercase || '_V');

            if CREATE_VIEW_FLAG = 1 or isViewExists = 0 then
              sql_create_v := '
              create or replace view FG_AUTHEN_' || r.formCode_uppercase || '_V as
              select *
              from FG_S_' || r.formCode_uppercase || '_ALL_V ';
              EXECUTE IMMEDIATE sql_create_v;
            end if;

            --.. insert to FG_RESOURCE
            /*select COUNT(*) into isViewExists
            from FG_RESOURCE t
            where t.code = 'FG_AUTHEN_' || upper(formCode_in) || '_V';

            if isViewExists = 0 then
                 INSERT INTO FG_RESOURCE (TYPE,CODE,VALUE,INFO) VALUES ('CATALOG_ORACLE_TABLE','FG_AUTHEN_' || upper(formCode_in) || '_V','FG_AUTHEN_' || upper(formCode_in) || '_V', upper(formCode_in) || ' view. insert to FG_RESOURCE by FG_SET_STRUCT_PIVOT_TABLE func as ' || to_char(sysdate,'dd/MM/yyyy HH24:MI'));
            end if;*/
    end loop; 
    
    return 0;
/*EXCEPTION
    when others then
      if rollbackFlag = 1 then
        EXECUTE IMMEDIATE ' CREATE table ' || 'FG_S_' || upper(formCodeEntity) || '_PIVOT AS SELECT * FROM FG_S_' || upper(formCodeEntity) || '_BU';
        EXECUTE IMMEDIATE ' DROP TABLE ' || 'FG_S_' || upper(formCodeEntity) || '_BU';
        raise_application_error( -20001, 'Error in FG_SET_STRUCT_PIVOT_TABLE ROLL BACK DATA WAS MADE');
        return 0;
      end if;*/
end;
/
