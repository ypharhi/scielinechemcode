create or replace package body form_tool is
  
   function addFormLabel(formCode_in varchar, bookmarkPrefix_in varchar, noEntityimpcodeList_in varchar) return number as
   begin
          --bu
          EXECUTE IMMEDIATE ' CREATE table FG_FORMENTITY_' || to_char(sysdate,'DDMMHH24MISS') || ' as select * from FG_FORMENTITY ';

          --insert lable before elements: 'ElementAutoCompleteIdValDDLImp','ElementInputImp','ElementAutoCompleteDDLImp' with label text as source element on one baook mark left
          insert into FG_FORMENTITY (FORMCODE,NUMBEROFORDER,ENTITYTYPE,ENTITYIMPCODE,ENTITYIMPCLASS,ENTITYIMPINIT)
          select * from 
          (
          
              select formCode_in, --FORMCODE
                     0, --NUMBEROFORDER
                     'Element', --ENTITYTYPE
                     'l' || t.entityimpcode,--ENTITYIMPCODE
                     'ElementLabelImp',--ElementLabelImp
                     -- the expression 
                     case
                       when REGEXP_INSTR(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9][0-9][0-9]') > 0 then --4 DIGIT
                          '{"text":"' || nvl(fg_get_value_from_json(t.entityimpinit,'label',null),initcap(t.entityimpcode)) || '","elementName":"' || t.entityimpcode || '","layoutBookMarkItem":"' || bookmarkPrefix_in  || (to_number(substr(t.entityimpinit, instr(t.entityimpinit,bookmarkPrefix_in) + length(bookmarkPrefix_in),4)) -1) || '","keepValueOnParentChange":false,"preventSave":false,"hideAlways":false,"disableAlways":false,"mandatory":false,"invIncludeInGrig":false,"invIncludeInFilter":false}'--ENTITYIMPINIT
                       when REGEXP_INSTR(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9][0-9]') > 0 then -- 3 DIGIT
                          '{"text":"' || nvl(fg_get_value_from_json(t.entityimpinit,'label',null),initcap(t.entityimpcode)) || '","elementName":"' || t.entityimpcode || '","layoutBookMarkItem":"' || bookmarkPrefix_in  || (to_number(substr(t.entityimpinit, instr(t.entityimpinit,bookmarkPrefix_in) + length(bookmarkPrefix_in),3)) -1) || '","keepValueOnParentChange":false,"preventSave":false,"hideAlways":false,"disableAlways":false,"mandatory":false,"invIncludeInGrig":false,"invIncludeInFilter":false}'--ENTITYIMPINIT
                       when REGEXP_INSTR(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9]') > 0 then --2 DIGIT
                          '{"text":"' || nvl(fg_get_value_from_json(t.entityimpinit,'label',null),initcap(t.entityimpcode)) || '","elementName":"' || t.entityimpcode || '","layoutBookMarkItem":"' || bookmarkPrefix_in  || (to_number(substr(t.entityimpinit, instr(t.entityimpinit,bookmarkPrefix_in) + length(bookmarkPrefix_in),2)) -1) || '","keepValueOnParentChange":false,"preventSave":false,"hideAlways":false,"disableAlways":false,"mandatory":false,"invIncludeInGrig":false,"invIncludeInFilter":false}'--ENTITYIMPINIT
                       when REGEXP_INSTR(t.entityimpinit,bookmarkPrefix_in || '[0-9]') > 0 then --1 DIGIT
                            '{"text":"' || nvl(fg_get_value_from_json(t.entityimpinit,'label',null),initcap(t.entityimpcode)) || '","elementName":"' || t.entityimpcode || '","layoutBookMarkItem":"' || bookmarkPrefix_in  || (to_number(substr(t.entityimpinit, instr(t.entityimpinit,bookmarkPrefix_in) + length(bookmarkPrefix_in),1)) -1) || '","keepValueOnParentChange":false,"preventSave":false,"hideAlways":false,"disableAlways":false,"mandatory":false,"invIncludeInGrig":false,"invIncludeInFilter":false}'--ENTITYIMPINIT
                     
                     end init_json
              from FG_FORMENTITY t
              where t.formcode = formCode_in
              and   t.entityimpclass in ('ElementAutoCompleteIdValDDLImp','ElementInputImp','ElementAutoCompleteDDLImp','ElementTextareaImp','ElementRichTextEditorImp')
              and   (t.formcode,'l' || t.entityimpcode) not in (select FORMCODE,ENTITYIMPCODE from FG_FORMENTITY)
              and   instr(',' || noEntityimpcodeList_in || ',', ',' || t.entityimpcode || ',') = 0 --not in list
          )
          where init_json is not null;
          commit;
          return 1;
   end;
   
   procedure shiftElementsBookmarks(formCode_in varchar, bookmarkPrefix_in varchar, noEntityimpcodeList_in varchar, addNumber number) as
   begin  
          --bu
          EXECUTE IMMEDIATE ' CREATE table FG_FORMENTITY_' || to_char(sysdate,'DDMMHH24MISS') || ' as select * from FG_FORMENTITY ';

          --shift
          update FG_FORMENTITY t
          set t.ENTITYIMPINIT = case
                               when REGEXP_INSTR(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9][0-9][0-9]') > 0 then --4 DIGIT
                                 REGEXP_REPLACE(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9][0-9][0-9]',bookmarkPrefix_in || (to_number(substr(t.entityimpinit, instr(t.entityimpinit,bookmarkPrefix_in) + length(bookmarkPrefix_in),4)) + addNumber))
                               when REGEXP_INSTR(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9][0-9]') > 0 then -- 3 DIGIT
                                 REGEXP_REPLACE(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9][0-9]',bookmarkPrefix_in || (to_number(substr(t.entityimpinit, instr(t.entityimpinit,bookmarkPrefix_in) + length(bookmarkPrefix_in),3)) + addNumber))                               
                               when REGEXP_INSTR(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9]') > 0 then --2 DIGIT
                                 REGEXP_REPLACE(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9]',bookmarkPrefix_in || (to_number(substr(t.entityimpinit, instr(t.entityimpinit,bookmarkPrefix_in) + length(bookmarkPrefix_in),2)) + addNumber))                             
                               end 
          where t.formcode = formCode_in
          and   t.entitytype = 'Element'
          and   t.entityimpinit like '%' || bookmarkPrefix_in || '_%'
          and   instr(',' || noEntityimpcodeList_in || ',', ',' || t.entityimpcode || ',') = 0; --not in list
          
          commit;
   end;
   
   procedure setAllStructTables as
     result number;
   begin
          for r in (
            /*select distinct t.formcode
            from FG_FORM t
            where t.form_type in ('STRUCT','ATTACHMENT','STRUCT')*/
            select distinct t.formcode_entity
            from FG_FORM t where t.formcode <> t.formcode_entity
            and t.formcode_entity in (select distinct  t1.formcode from FG_FORM t1)
          )
          loop  
              result := FG_SET_STRUCT_PIVOT_TABLE(r.formcode_entity, 1);
          end loop;
          
       /*     begin
         \* for r in (
            select distinct t.formcode
            from FG_FORM t
            where t.form_type in ('STRUCT','ATTACHMENT')
            and t.formcode in (select t1.formcode from fg_formlastsavevalue t1) 
          )
          loop  
            execute immediate ' delete from  FG_S_' || upper(r.formcode) || '_PIVOT ';
            execute immediate ' delete from  fg_formlastsavevalue where formcode = ''' || r.formcode || ''' ';
             
          end loop;*\
          
           for r in (
            select distinct t.formcode
            from FG_FORM t
            where t.form_type in ('GENERAL')
            and t.formcode in (select t1.formcode from fg_formlastsavevalue t1) 
          )
          loop  
            execute immediate ' delete from  fg_formlastsavevalue where formcode = ''' || r.formcode || ''' ';
             
          end loop;
          
          commit;
   end;*/
          
          commit;
   end;
   
   procedure cleanAllData as
      dummy number;
   begin
          /*
          CREATE TABLE FG_FORMLASTSAVEVALUE AS
          select * from FG_FORMLASTSAVEVALUE_13072017 t
          */ 
          
         /* DELETE from FG_FORMLASTSAVEVALUE t where UPPER(t.FORMCODE_ENTITY) NOT in (SELECT UPPER(T1.FORMCODE) FROM FG_FORM T1 WHERE T1.FORM_TYPE = 'MAINTENANCE');
          
          UPDATE FG_FORMLASTSAVEVALUE T SET T.FORMIDSCRIPT = T.FORMID;
          
          update FG_FORMLASTSAVEVALUE t set t.formid = ( select ranki 
                                                          from (
                                                                 select t1.*, dense_rank() over (order by formid) + 1000 as ranki from FG_FORMLASTSAVEVALUE t1  
                                                                ) s
                                                          where s.id = t.id
                                                         ); 
                                                         
          UPDATE FG_FORMLASTSAVEVALUE T SET T.ENTITYIMPVALUE = (SELECT DISTINCT T1.FORMID FROM FG_FORMLASTSAVEVALUE T1 WHERE T1.FORMIDSCRIPT = T.ENTITYIMPVALUE)  WHERE T.FORMCODE_ENTITY LIKE '%_ID' OR T.ENTITYIMPCODE = 'formId' OR (T.ENTITYIMPCODE = 'type' and t.FORMCODE_ENTITY = 'UOM');
          */
          
          for r in (
           select t.TABLE_NAME
            from user_tables t
            where 1=1
            AND (t.TABLE_NAME like 'FG_S_%_PIVOT'
            OR    T.TABLE_NAME IN ('FG_S_FORMULANTREF_ALL_V_PLAN',
                                  'CREATE_SERIES_INDX_DATA_SS',
                                  'FG_ACCESS_LOG',
                                  'FG_ACTIVITY_LOG',
                                  'FG_AUTHEN_REPORT_V',
                                  'FG_CHEM_DOODLE_DATA',
                                  'FG_CLOB_FILES',
                                  'FG_DEBUG',
                                  'FG_FILES',
                                  'FG_FORMID_UNPIVOT_LIST_TMP',
                                  'FG_FORMTEST_DATA',
                                  'FG_R_DEMO_TEST_DATA',
                                  'FG_R_EXPERIMENT_CHEMDRAW_V_PLN',
                                  'FG_RICHTEXT',
                                  'FG_R_SYSTEM_VIEW',
                                  'FG_TOOL_INF_ALL_DATA',
                                  'FG_RESULTS',
                                  'FG_FORMADDITIONALDATA',
                                  'FG_FORMADDITIONALDATA_HST',
                                  'FG_FORMMONITOPARAM_DATA',
                                  'FG_DYNAMICPARAMS'))
            AND   UPPER(REPLACE(REPLACE(T.TABLE_NAME,'FG_S_'),'_PIVOT')) not IN (SELECT UPPER(F.FORMCODE_ENTITY) FROM FG_FORM F WHERE F.FORM_TYPE = 'MAINTENANCE' or f.formcode = 'PermissionSRef')
          )
          loop  
               truncate_it(r.TABLE_NAME);
          end loop;
          
          delete from fg_sequence t where upper(t.formcode) not  in ( SELECT distinct UPPER(F.Formcode) FROM FG_FORM F WHERE F.FORM_TYPE = 'MAINTENANCE'
                                                                      union
                                                                      SELECT distinct UPPER(F.Formcode_Entity) FROM FG_FORM F WHERE F.FORM_TYPE = 'MAINTENANCE');
          
          delete from fg_formlastsavevalue t where t.formid not in (select id from fg_sequence);
          
           truncate_it('FG_FORMLASTSAVEVALUE_HST');
           truncate_it('FG_FORMLASTSAVEVALUE_INF');
           
         /* for r in (
            SELECT  T1.FORMCODE FROM FG_FORM T1 WHERE T1.FORM_TYPE = 'MAINTENANCE'
          )
          loop  
              dummy := FG_SET_STRUCT_PIVOT_TABLE(r.formcode);
          end loop;
          
          delete from FG_SEQUENCE t;
          
          completeData;
          
          initFormlastsavevalueHst;*\*/
          
          commit;
          
          --update FG_SEQUENCE_system_SEQ to max + 1 in FG_SEQUENCE
          --update FG_SEQUENCE_SEQ to 100000
           
          
         /* delete from fg_form t 
          where lower(t.formcode) like '%eyal%' or lower(t.formcode) like '%yaron%' or lower(t.formcode) like '%alex%' and lower(t.formcode) not like 'protocoltypeeyal';

          delete from fg_formentity t 
          where t.formcode not in (select formcode from fg_form);
          
          --unlinked values
          delete from fg_formlastsavevalue t
          where t.formcode not in (select formcode from fg_form);
          
          --developers data (developers will eneter <form>Name value 
          delete from fg_formlastsavevalue t
          where t.formid in 
          (
            select formid
            from fg_formlastsavevalue t
            where 1=1
            and   t.formcode <> 'User'
            and   lower(t.entityimpcode) = lower(t.formcode) || 'name'  
            and   (
                     lower(t.entityimpvalue) like '%eyal%'  
                     or lower(t.entityimpvalue) like '%yaron%' 
                     or t.entityimpvalue in ('Ptest1')
                  )
          );

          --unlinked doc
          delete from fg_formlastsavevalue t
          where t.formid in (
            select t.formid 
            from fg_s_document_all_v t
            where t.PARENTID not in (
            select formid
            from fg_formlastsavevalue t
            )
          );
            
          --unlinked file
          delete from fg_files t
          where t.file_id not in (
            select t.formid 
            from fg_formlastsavevalue t
          );
          
          commit;
          
          --MORE CLEAN SELECT...
          \*
          SELECT * FROM FG_RESOURCE T WHERE UPPER(T.CODE) NOT IN (
          select UPPER(T1.VIEW_NAME) from FG_R_SYSTEM_VIEW t1) AND UPPER(T.CODE) NOT IN  (
          SELECT T1.TABLE_NAME FROM USER_TABLES T1 
          ) --AND UPPER(T.CODE) NOT IN ( SELECT FG_FORMENTITY.ENTITYIMPINIT FROM FG_FORMENTITY)
          ;

          select * 
          from FG_RESOURCE t,
          FG_FORMENTITY F
          WHERE INSTR(UPPER(F.ENTITYIMPINIT),UPPER(T.CODE))  > 0
          AND T.TYPE IS NULL ;
          *\*/
   end;
   
   procedure printTablesRowCount (show_greater_than number) as
 
      cursor c_tables_list is
      select TABLE_NAME from user_tables order by TABLE_NAME;

      r_tables_list c_tables_list%rowtype;

      flagCounter number := 0;

      tmpMinusQuery varchar(4000) := '';

   begin 
  
      for r_tables_list in c_tables_list
      loop
        tmpMinusQuery := ' select
                              (select count(*) from ' || r_tables_list.TABLE_NAME || ')
                          from dual ';
        execute immediate tmpMinusQuery  into flagCounter;
        
        if flagCounter > nvl(show_greater_than,0) then
           dbms_output.put_line(  r_tables_list.TABLE_NAME || ' - ' || NVL(flagCounter,0) || ' rows.' );
        end if;
        
      end loop;
 
   end;
   
   procedure removeFormEntityIntProp(formCode varchar, initProp varchar) as
   begin
     dbms_output.put_line('dummy' || formCode || initProp); --run update sql as below
     --update FG_FORMENTITY t set t.entityimpinit = replace(t.entityimpinit,'"invIncludeInGrig":false,"invIncludeInFilter":false','') where t.entityimpinit like '%,"invIncludeInGrig":false,"invIncludeInFilter":false%'
   end;
   
   procedure favoriteSqls as
   begin
     
     dbms_output.put_line('dummy');
     
     --session process info
     /* SELECT  S.USERNAME, S.MACHINE, s.LOGON_TIME, s.LOCKWAIT
      FROM V$SESSION_CONNECT_INFO SC , V$SESSION S--, v$process p
      WHERE S.SID = SC.SID
      --and S.MACHINE = 'COMPLYPC161'
      --and s.paddr = p.addr
     */
     --number of main objects per project - month
    /*select  distinct t1.PROJECT_ID, T1.PROJECTNAME, t1.ProjectTypeName, t1.CREATION_DATE AS PROJECT_CREATION_DATE, u.UserName as PROJECT_CREATED_BY,
            t1.formcodeentitylabel entity_name, t1.entity_month,
            count(distinct t1.id) over (partition by t1.PROJECT_ID,lower(t1.formcodeentitylabel), t1.entity_month) as num_entities_project_month
    from (
    select distinct to_char(t.insertdate,'yyyy-MM') as entity_month, t.id, p.PROJECT_ID, p.PROJECTNAME, m.formcodeentitylabel, p.ProjectTypeName, p.CREATION_DATE , p.CREATED_BY
    from FG_SEQUENCE t,
         fg_s_project_all_v p,
         FG_FORMELEMENTINFOATMETA_MV m
    where t.search_match_id1 = p.project_id
    and t.formcode = m.formcode
    --AND   EXISTS (SELECT 1  FROM FG_S_EXPERIMENT_V E where e.experiment_id = t.id and e.TEMPLATEFLAG is null)
    and exists (select 1 from fg_formlastsavevalue_inf f where f.formid = t.id AND F.ACTIVE = 1)
    and m.formcodeentitylabel in ('SubProject','SubSubProject','Experiment','Step','Action','Self-Test','Workup')
    ) t1, fg_s_user_v u where u.user_id = t1.CREATED_BY
    ORDER BY T1.PROJECT_ID, t1.formcodeentitylabel, T1.entity_month;*/

    --number of main objects per project
    /*select  distinct t1.PROJECT_ID, T1.PROJECTNAME, t1.ProjectTypeName, t1.CREATION_DATE, u.UserName as createdby,
            t1.formcodeentitylabel entity_name,
            count(distinct t1.id) over (partition by t1.PROJECT_ID,lower(t1.formcodeentitylabel)) as num_entities_in_project
    from (
    select distinct t.id, p.PROJECT_ID, p.PROJECTNAME, m.formcodeentitylabel, p.ProjectTypeName, p.CREATION_DATE , p.CREATED_BY
    from FG_SEQUENCE t,
         fg_s_project_all_v p,
         FG_FORMELEMENTINFOATMETA_MV m
    where t.search_match_id1 = p.project_id
    and t.formcode = m.formcode
    --AND   EXISTS (SELECT 1  FROM FG_S_EXPERIMENT_V E where e.experiment_id = t.id and e.TEMPLATEFLAG is null)
    and exists (select 1 from fg_formlastsavevalue_inf f where f.formid = t.id AND F.ACTIVE = 1)
    and m.formcodeentitylabel in ('SubProject','SubSubProject','Experiment','Step','Action','Self-Test','Workup')
    ) t1, fg_s_user_v u where u.user_id = t1.CREATED_BY
    ORDER BY T1.PROJECT_ID, t1.formcodeentitylabel;*/ 
     
     --user entity files
     /*select distinct t.FORMID,t.PATH_ID, trunc(s.insertdate) as "Entity Creation Date",  trunc(t.CHANGE_DATE), s.formcode, t.formcode_entity,  
                      m.formcodeentitylabel as Entity,  unew.UserName as UserName,  
                      p.ProjectName, up.UserName as "Project creator",
                      round(DBMS_LOB.getlength(f.file_content)/1024) AS "FILE SIZE [KB]"
      from fg_formlastsavevalue_inf_v t
           ,fg_files f
           ,fg_s_user_v uchange
           ,fg_s_user_v unew
           ,fg_s_user_v up
           ,fg_sequence s
           ,FG_FORMELEMENTINFOATMETA_MV m
           ,fg_s_project_v p
      where 1=1--to_number(t.formid) >= 317101
      and t.ACTIVE = 1
      and t.IS_FILE = 1
      and t.userid = unew.user_id
      and t.CHANGE_BY = uchange.user_id
      and t.ENTITYIMPVALUE = f.file_id
      and t.PATH_ID = s.id
      and p.project_id(+) = s.search_match_id1
      and p.CREATED_BY = up.user_id(+)
      and upper(m.formcode) = upper(s.formcode)
      order by to_number(t.FORMID);*/

    --user entity projects
    /*select distinct t.FORMID,t.PATH_ID, trunc(s.insertdate) as "Entity Creation Date", trunc(t.CHANGE_DATE) as "Change Date", t.formcode_entity AS Entity, 
                    p.ProjectName, up.UserName as "Project creator", 
                    --m.formcodeentitylabel as Entity,  
                    unew.UserName as "Created By", unew.active as "Is Active", uchange.UserName as "Changed By", uchange.active as "Is Active"
    from fg_formlastsavevalue_inf_v t
         ,fg_s_user_v uchange
         ,fg_s_user_v unew
         ,fg_s_user_v up
         ,fg_sequence s
         ,fg_s_project_v p
         --,FG_FORMELEMENTINFOATMETA_MV m
    where  1=1 --to_number(t.formid) >= 317101
    and t.ACTIVE = 1
    and t.PATH_ID is null
    and t.userid = unew.user_id
    and t.CHANGE_BY = uchange.user_id
    and t.FORMID = s.id
    and p.project_id(+) = s.search_match_id1
    and p.CREATED_BY = up.user_id(+);*/
     
     /* -- csv to table (listtotable/csvtotable)
    SELECT to_number(regexp_substr('1,77,3', '[^,]+', 1, commas.column_value)) as id_
    FROM table(cast(multiset
                      (SELECT LEVEL
                       FROM dual CONNECT BY LEVEL <= LENGTH (regexp_replace('1,77,3', '[^,]+')) + 1) AS sys.OdciNumberList)) commas
    
    */
     /*
     -- move init dif between forms
     update fg_formentity t 
     set t.entityimpinit = (select t1.entityimpinit from fg_formentity t1 where t1.formcode = 'YYY' and t1.entityimpcode = 'action')
     where t.formcode = 'XXXX'
     and t.entityimpcode = 'action'
     */
     
     /*
     --performance
     select t.timestamp as "Time Stamp_SMARTTIME",
             T.FORMID,
             T.USER_ID,
             t.comments, 
             fg_get_numeric(nvl(fg_get_value_from_json(t."ADDITIONALINFO",'SQL_TIME','-1'),fg_get_value_from_json(t."ADDITIONALINFO",'EXE_TIME','-1'))) as "Runtime_NUM" from FG_ACTIVITY_LOG t
      where t.activitylogtype = 'PerformanceSQL'
      and   t.timestamp > sysdate - 1
      and   UPPER(t.comments) not like '%REFRESH%'
      AND   UPPER(T.COMMENTS) NOT LIKE '%FG_FORMLASTSAVEVALUE_INF%'
      AND   UPPER(T.COMMENTS) NOT LIKE '%FG_SET_INF_INIT_DATA_ALL%'
      AND   UPPER(T.COMMENTS) NOT LIKE '%FG_N_%'
      AND   UPPER(T.COMMENTS) NOT LIKE '%FG_R_%'
      AND   UPPER(T.COMMENTS) NOT LIKE '%_DT%' --?
      AND   UPPER(T.COMMENTS) NOT LIKE '%DB_CLEANUP%'
      AND   T.COMMENTS  NOT LIKE '%insert into fg_form_change_list (formcode)%' --?
      AND   T.COMMENTS  NOT LIKE '%FG_SET_SERACH_HANDEL_INF_ID%' --?
      and    T.COMMENTS  NOT LIKE'select DISTINCT f.formcode from user_tab_columns t, FG_FORM F where %'
 */
     
     /*
     -- FILES TABLE SIZE
      select round(sum(DBMS_LOB.GETLENGTH(t.file_content))/1024/1024,3) || ' MB' as blob_size from FG_FILES_SRC t;
     */
     
     /*
     --MV refresh time duration
     SELECT-1 as dummy,  mview_name, last_refresh_date, fullrefreshtim, increfreshtim
     FROM user_mview_analysis
*/
     /*
     --delete experiment CP run data
     --workup
    delete from fg_s_workup_pivot where action_id in (
    select t1.formid from fg_s_action_pivot t1 where t1.step_id in (
    select formid from fg_s_step_pivot t where t.runnumber is not null)
    );

    --self test
    delete from fg_s_selftest_pivot where action_id in (
    select t1.formid from fg_s_action_pivot t1 where t1.step_id in (
    select formid from fg_s_step_pivot t where t.runnumber is not null)
    );

    --action
    delete from fg_s_action_pivot t1 where t1.step_id in (
    select formid from fg_s_step_pivot t where t.runnumber is not null);

    --material ref
    delete from fg_s_materialref_pivot t1 where t1.parentid in (
    select formid from fg_s_step_pivot t where t.runnumber is not null);

    --step
    delete from fg_s_step_pivot t where t.runnumber is not null;
    
    --EXPRUNPLANNING
    delete from FG_S_EXPRUNPLANNING_PIVOT;
    */

     /*
     -- SMART COLUMN
      SELECT * FROM (
      SELECT DISTINCT SUBSTR(T.COLUMN_NAME,INSTR(T.COLUMN_NAME, '_', -1)) AS SMART_NAME
      ,T.TABLE_NAME,T.COLUMN_NAME
      FROM USER_TAB_COLUMNS T WHERE T.COLUMN_NAME LIKE '%_SMART%');
*/
     /*
     --SEQUENCE validation
     select max(t.id) from FG_SEQUENCE_FILES t; --less than -> FG_SEQUENCE_FILES_SEQ
     select max(t.id) from FG_SEQUENCE t; --less than -> FG_SEQUENCE_SEQ
     select max(to_number(t.result_id)) from fg_results t;--less than -> FG_RESULTS_SEQ
*/
     /*
     -- open connection  
     select * from (
        select distinct count(*) over () c_all, count(*) over (partition by t.OSUSER) c_user ,t.OSUSER --,t.* 
        from sys.V_$SESSION t 
      ) order by c_user
      
     */
     -- clone with minus  active value struct tables
     /*DECLARE
      C_ NUMBER;
      begin
       FOR r in  (
               
                select distinct t.TABLE_NAME
                from user_tables t, USER_TAB_COLUMNS C
                where t.TABLE_NAME like 'FG_S_%_PIVOT'
                AND T.TABLE_NAME = C.TABLE_NAME
                AND C.COLUMN_NAME = 'ACTIVE')
       LOOP
         EXECUTE IMMEDIATE ' SELECT COUNT(*) FROM ' || R.TABLE_NAME || ' WHERE NVL(ACTIVE,0) < 0' into C_;
         IF C_ > 0 THEN
           DBMS_OUTPUT.put_line('TABLE ' || R.TABLE_NAME || ', COUNT MINUS ACTIVE = ' || C_);
         END IF;
       END LOOP;
      END;*/
     
     ---------------------------- update maintenance form to close on save if aftersave is missing
     /*
      UPDATE fg_formentity FE SET FE.ENTITYIMPINIT = REPLACE(FE.ENTITYIMPINIT,',"templateType"', ',"afterSave":"Close","templateType"' )
      WHERE FE.ID IN (
      select ID from fg_formentity t 
      where t.formcode in (select formcode from fg_form f where f.form_type = 'MAINTENANCE') 
      AND ENTITYTYPE = 'Layout'
      AND T.ENTITYIMPINIT NOT LIKE '%afterSave%'
      AND T.ENTITYIMPINIT LIKE '%,"templateType"%')
*/
     --------------------------- correct template flag
     /*
      --experiment
      update fg_s_experiment_pivot t set t.templateflag = 1  where t.formid in (
      select t1.sourceexpno_id from fg_s_template_pivot t1
      )
      and nvl(t.templateflag,0) =  0;

      --step
      update fg_s_step_pivot t set t.templateflag = 1 where t.experiment_id in (
             select t1.formid from fg_s_experiment_pivot t1 where nvl(t1.templateflag,0) = 1
      )
      and nvl(t.templateflag,0) =  0;

      --action
      update fg_s_action_pivot t set t.templateflag = 1 where t.experiment_id in (
             select t1.formid from fg_s_experiment_pivot t1 where nvl(t1.templateflag,0) = 1
      )
      and nvl(t.templateflag,0) =  0;
      
      - --experiment
      update fg_s_experiment_pivot t set t.templateflag = 1  where t.formid in (
      select t1.sourceexpno_id from fg_s_template_pivot t1
      )
      and nvl(t.templateflag,0) =  0;

      --step
      update fg_s_step_pivot t set t.templateflag = 1 where t.experiment_id in (
             select t1.formid from fg_s_experiment_pivot t1 where nvl(t1.templateflag,0) = 1
      )
      and nvl(t.templateflag,0) =  0;

      --workup
      update fg_s_workup_pivot t set t.templateflag = 1 where t.experiment_id in (
             select t1.formid from fg_s_workup_pivot t1 where nvl(t1.templateflag,0) = 1
      )
      and nvl(t.templateflag,0) =  0;
      
      
      --selftest
      update fg_s_selftest_pivot t set t.templateflag = 1 where t.experiment_id in (
             select t1.formid from fg_s_selftest_pivot t1 where nvl(t1.templateflag,0) = 1
      )
      and nvl(t.templateflag,0) =  0;
*/
     /* --------- ************** check layout entity bug(1) (on same form with dif config)
     --create table yaron_check_jsp as
      select --formcode_entity,
             --formcode,
             distinct
             entityimpcode,
             count(htmlName || ',' || afterSave || ',' ||templateName  || ',' || templateType) over (partition by entityimpcode )c1,
             count(distinct htmlName || ',' || afterSave || ',' ||templateName  || ',' || templateType)over (partition by entityimpcode )c2
             from (
            -- htmlName,afterSave,templateName,templateType from (
      select * from (
      select t.entityimpcode, f.formcode_entity, f.formcode, fg_get_value_from_json(t.entityimpinit,'htmlName','na') as htmlName, 
      fg_get_value_from_json(t.entityimpinit,'afterSave','na')as afterSave,
      fg_get_value_from_json(t.entityimpinit,'templateName','na') as templateName,
      fg_get_value_from_json(t.entityimpinit,'templateType','na') as templateType,
      t.entityimpinit , count(rownum) over(partition by t.entityimpcode) as counter_ from FG_FORMENTITY t,fg_form f where t.entitytype = 'Layout' and t.formcode = f.formcode )
      where counter_ > 1);

      --------- **************check layout entity bug(2) (on same form with dif config)
      select * from fg_formentity t where t.entityimpcode in (
      select t1.entityimpcode from YARON_CHECK_JSP t1  where t1.c2 > 1 );
*/


/*    yp - not worked as expected !
     create or replace view fg_i_formentity_label_v as
select formcode, entityimpcode, element_code,
       replace(replace(replace(initcap(replace(replace(nvl(nvl(label_element, label_config),label_impcode_parse),'_Id',''),':','')),'_',''),'type',' Type'),'  ',' ') as element_label
from (
    select t.formcode, t.entityimpcode,
           t.formcode || '.' || t.entityimpcode as element_code,
           nullif(fg_get_value_from_json(t.init_l,'text','na'),'na') label_element,
           nullif(fg_get_value_from_json(t.init_e ,'label','na'),'na') label_config,
           DECODE(t.entityimpcode,upper(t.entityimpcode),initcap(replace(lower(t.entityimpcode),'_id','')),fn_tool_camel_to_label(replace(t.entityimpcode,'_Id',''))) label_impcode_parse
    from (
    select e.formcode, e.entityimpcode, el.entityimpinit as init_l, e.entityimpinit as init_e
    from FG_FORMENTITY el,
         FG_FORMENTITY e
    where 1=1--t.formcode = formCode_in
    and   el.entityimpclass(+) = 'ElementLabelImp'
    and   fg_get_value_from_json(el.entityimpinit(+) ,'elementName','na') = e.entityimpcode
    and   el.formcode(+) = e.formcode
    and   UPPER(fg_get_value_from_json(e.entityimpinit ,'hideAlways','na')) <> 'TRUE'
    and   e.entityimpclass not in ('ElementLabelImp',\*'ElementUOMImp'*\'ElementAuthorizationImp','ElementAsyncIframeImp')
    and   e.entitytype = 'Element') t )
where 1=1*/
     
     --for automatic tests - check if there is missing data in the manual results
     /*select distinct count(*)
    from FG_I_CONNECTION_REQSMPLDEXP_V smplReq, 
    fG_S_SAMPLESELECT_ALL_V smpl,
    fg_s_component_all_v c,
    fg_s_manualresultsRef_v mr,
    fg_s_experiment_v ex
    where smpl.PARENTID = smplReq.EXPERIMENTDEST_ID
    and smpl.SAMPLE_ID = smplReq.SAMPLE_ID 
    and smpl.PARENTID = ex.experiment_id
    and smpl.sessionid is null and nvl(smpl.active,'1')='1'
    and c.parentId = ex.experiment_id 
    and c.sessionid is null and nvl(c.active,'1') = '1'
    and mr.SAMPLE_ID = smpl.SAMPLE_ID
    and mr.COMPONENT_ID = c.COMPONENT_ID 
    and mr.parentid = ex.experiment_id
    and nullif(smplReq.REQUEST_ID,to_number(mr.REQUEST_ID))is not null)*/
     
     --check all file ids connected to fg_files
/*     select * from
 (
 select instractionsFile as FILE_ID from fg_s_selftesttype_pivot
 where sessionid is null
 and nvl(active,'1')='1'
 union all
  select spectrum from fg_s_component_pivot
 where sessionid is null
 and nvl(active,'1')='1'
 union all
  select attachment from fg_s_manualresultsref_pivot
 where sessionid is null
 and nvl(active,'1')='1' 
 union all
  select attachment from fg_s_prmanualresultref_pivot
 where sessionid is null
 and nvl(active,'1')='1' 
 union all
   select attachment from fg_s_manualresultsmsref_pivot
 where sessionid is null
 and nvl(active,'1')='1'
 union all
   select spectrum from fg_s_invitemmaterial_pivot
   union all
   (
   select d.documentupload from fg_s_Document_pivot d where 1=1 and d.LINK_ATTACHMENT = 'Attachment' 
 and d.sessionid is null 
 and d.active=1
   )
 union all
   select attachment from fg_s_operationtype_pivot
 where sessionid is null
 and nvl(active,'1')='1'
 ) where file_id is not null 
 and file_id not  in (select f.file_id from fg_files f)
 */
    /* 
    
    delete from FG_ACTIVITY_LOG t where t.activitylogtype not in ('Depletion','NotificationEvent')
    
    --compare FG_FORMENTITY
 select t.formcode,t.entityimpcode,t.entityimpinit from FG_FORMENTITY t where 1=1 and lower(t.formcode) like lower('MaterialRef') and lower(t.entityimpcode) like lower('%purityInf')
 minus
 select t.formcode,t.entityimpcode,t.entityimpinit from skyline_form_server.FG_FORMENTITY t where 1=1 and lower(t.formcode) like lower('MaterialRef') and lower(t.entityimpcode) like lower('%purityInf')
 */
     /*
     --UPDATE ON SERVER fg_formentity rows
     update FG_FORMENTITY t set t.comments = null where t.comments like 'UPDATE ON SERVER CHANGE FROM%';
     select * from FG_FORMENTITY t where t.comments like 'UPDATE ON SERVER CHANGE FROM%';
     */
     /*
      --FORMCODE <> FORMCODEENTITY
      --select * from FG_FORM t where t.formcode <> t.formcode_entity AND t.form_type = 'STRUCT' AND UPPER(FORMCODE) NOT LIKE '%YARON%' AND UPPER(FORMCODE) NOT LIKE '%MAIN'


     --Edit FG_FORME by formcode
select t.*, t.rowid from FG_FORM t where UPPER(t.formcode) like UPPER('%xxx%');
               
--Edit FG_FORMENTITY by formcode
select t.*, t.rowid from FG_FORMENTITY t where UPPER(t.formcode) like UPPER('%xxx%');
               
--Edit FG_FORMENTITY by init
select t.*, t.rowid from FG_FORMENTITY t where UPPER(t.entityimpinit) like UPPER('%xxx%');
               
--Edit FG_FORMENTITY by formcode
select t.*, t.rowid from FG_FORMENTITY t where UPPER(t.entityimpinit) like UPPER('%xxx%');
               
--Edit FG_RESOURCE by CODE
select t.*, t.rowid from FG_RESOURCE t where UPPER(t.code) like UPPER('%xxx%');
               
--Edit FG_RESOURCE by type
select t.*, t.rowid from FG_RESOURCE t where UPPER(t.type) like UPPER('%xxx%');

--Edit FG_FORMLASTSAVEVALUE by type
select t.*, t.rowid from FG_FORMLASTSAVEVALUE t where UPPER(t.formcode) like UPPER('%xxx%');

--SEQ
select * from fg_sequence_plus_v t where t.id = to_number('xxx');

-- INSERT VIEW TO RESOURCE
INSERT INTO FG_RESOURCE (TYPE,CODE,VALUE,INFO)
select 'INFORMATION_TABLE', T.VIEW_NAME, T.VIEW_NAME, 'TODO DESC'
from user_views t
where t.VIEW_NAME like 'FG__%'
AND SUBSTR(T.VIEW_NAME,4,1) IN ('M','R','I')
AND SUBSTR(T.VIEW_NAME,5,1) = '_' 
AND UPPER(T.VIEW_NAME) NOT IN (SELECT UPPER(T1.CODE) FROM FG_RESOURCE T1)

-- no id in seq
select * from FG_TOOL_INF_ALL_V where  id not in (select id from fg_sequence);
-- 	NAME  ID  TABLENAME
--4  Room  26885  FG_S_SAMPLETYPE_INF_V
--3  Oven  26884  FG_S_SAMPLETYPE_INF_V
--2	Drying 2	20799	FG_S_MP_INF_V
--1	Drying1	20797	FG_S_MP_INF_V  
     */
     
     
     --select * from FG_TOOL_INF_ALL_V where  id not in (select id from fg_sequence)
/*	NAME  ID  TABLENAME
4  Room  26885  FG_S_SAMPLETYPE_INF_V
3  Oven  26884  FG_S_SAMPLETYPE_INF_V
2	Drying 2	20799	FG_S_MP_INF_V
1	Drying1	20797	FG_S_MP_INF_V*/
/*
1	Oven	26884	FG_S_SAMPLETYPE_INF_V
2	Room	26885	FG_S_SAMPLETYPE_INF_V*/

--CREATE TABLE YARON_CHECK_SEQ AS
--select * from FG_TOOL_INF_ALL_V T where  UPPER(T.tablename)  not in (select UPPER('FG_S_' || f.formcode || '_INF_V')  from fg_sequence s, fg_form f where  f.formcode = s.formcode AND T.ID = S.ID );
/*
select * from (
select t.*,  fg_get_numeric (t.entityimpvalue) numres
from fg_formlastsavevalue t 
where t.active = 1 )
where numres > 1000
and numres is not null
and numres not in (select id from fg_sequence)
and formcode_entity not in ( 'ExperimentSeries' ,'ExpInSeries')
and entityimpcode not in ('formNumberId','SESSIONID','factor','projectNumber','viscosity','quantity','serialNumber')*/


/*DDL INF WHAT IS IN USE...

select fg_get_value_from_json(t.entityimpinit ,'parentElement','na'), t.* from FG_FORMENTITY t  
--from FG_FORMENTITY t
where 1=1--t.formcode like 'ExperimentPr%'
--AND   t.formcode <> 'ExperimentPr'
and t.entityimpclass in ('ElementAutoCompleteIdValDDLImp')
--and t.entityimpinit  like '%defaultValue%'
--and t.entityimpinit   like '%disableScript%'
and fg_get_value_from_json(t.entityimpinit ,'parentElement','na')<>'na'
and t.entityimpinit like '%$P{%'*/

--smartsearch element
/*select DISTINCT  f.formcode_entity, T.FORMCODE, T.ENTITYIMPCODE,T.ENTITYIMPCLASS ,T1.ENTITYIMPCODE,T1.ENTITYIMPCLASS 
from fg_formentity t,
     fg_formentity t1,
     fg_form f
where t.formcode = t1.formcode
and instr(upper(t1.entityimpinit),upper(t.entityimpcode)) > 0
and   t1.entityimpclass like '%ElementSmartSearchImp%'
 and  t.entityimpclass in ( 'ElementAutoCompleteIdValDDLImp' ,'ElementAutoCompleteIdValDDLImp' )
 and t.formcode = f.formcode*/
 
 /*--numeric calculation
 select  * from FG_FORMENTITY t  
--from FG_FORMENTITY t
where 1=1--t.formcode like 'ExperimentPr%'
--AND   t.formcode <> 'ExperimentPr'
and t.entityimpclass in ('ElementInputImp','ElementApiElementSetterImp')
--and t.entityimpinit  like '%defaultValue%'
 --and t.entityimpinit   like '%disableAlways%'
 and fg_get_value_from_json(t.entityimpinit ,'disableAlways','na') = 'true'
 and fg_get_value_from_json(t.entityimpinit ,'precision','-1') > 0
 and (fg_get_value_from_json(t.entityimpinit ,'type','na') = 'Number' or t.entityimpclass in ('ElementApiElementSetterImp'))
*/

     /*  
     -- smart serach sql
     select * 
       from FG_FORMENTITY p 
       -- elemet that parent is used by search (not ElementInputImp) that has suns
       where (p.formcode, p.entityimpcode) in (
       select f.formcode , fg_get_value_from_json(f.entityimpinit ,'parentElement','na')
       from FG_FORMENTITY f,
       (
      select t.formcode, t.entityimpinit EL From FG_FORMENTITY t  
      --from FG_FORMENTITY t
      where 1=1--t.formcode like 'ExperimentPr%' 
      and t.entityimpclass in ('ElementSmartSearchImp')
      --and t.entityimpinit  like '%defaultValue%'
       --and t.entityimpinit   like '%disableAlways%'
       and t.formcode not in ('xxx','ExperimentPr')
       --and fg_get_value_from_json(t.entityimpinit ,'parentElement','na') <> 'na'   
       ) s -- serach element with suns
       where
       s.formcode = f.formcode
       and f.entityimpclass not in ('ElementInputImp') -- the trigger needed to be work on
       and instr(s.el,f.entityimpcode) > 0 ) */
       
      /* 
      -- data wf sql
        select * 
       from FG_FORMENTITY p where (p.formcode, p.entityimpcode) in (
       select f.formcode , fg_get_value_from_json(f.entityimpinit ,'parentElement','na')
       from FG_FORMENTITY f,
       (
      select t.formcode, t.entityimpinit EL From FG_FORMENTITY t  
      --from FG_FORMENTITY t
      where 1=1--t.formcode like 'ExperimentPr%' 
      and t.entityimpclass in ('ElementSmartSearchImp')
      --and t.entityimpinit  like '%defaultValue%'
       --and t.entityimpinit   like '%disableAlways%'
       and t.formcode not in ('xxx','ExperimentPr')
       --and fg_get_value_from_json(t.entityimpinit ,'parentElement','na') <> 'na'   
       ) s
       where
       s.formcode = f.formcode
       and instr(s.el,f.entityimpcode) > 0 )*/
       
       /*
       -- element  preventSave true -- for search
        select *
          From FG_FORMENTITY t  
      --from FG_FORMENTITY t
      where 1=1--t.formcode like 'ExperimentPr%' 
     -- and t.entityimpclass in ('ElementSmartSearchImp')
      --and t.entityimpinit  like '%defaultValue%'
       --and t.entityimpinit   like '%disableAlways%'
       and t.formcode not in ('xxx','ExperimentPr')
        and lower(fg_get_value_from_json(t.entityimpinit ,'preventSave','na')) = 'true' 
        and lower(fg_get_value_from_json(t.entityimpinit ,'hideAlways','na')) <> 'true' 
        and t.entityimpclass not in ('ElementAsyncIframeImp','ElementRadioImp','ElementChemDoodleImp','chemDoodleSearch')
        */
       /*
       --diff in formentity
       select t.formcode,t.entityimpcode,t.entityimpinit from FG_FORMENTITY t
        minus
        select t.formcode,t.entityimpcode,t.entityimpinit from skyline_form_server.FG_FORMENTITY t;

        select t.formcode,t.entityimpcode,t.entityimpinit from skyline_form_server.FG_FORMENTITY t
        minus
        select t.formcode,t.entityimpcode,t.entityimpinit from FG_FORMENTITY t;

        select t.formcode,t.entityimpcode,t.entityimpinit from FG_FORMENTITY t
        minus
        select t.formcode,t.entityimpcode,t.entityimpinit from skyline_form_unittest.FG_FORMENTITY t;

        select t.formcode,t.entityimpcode,t.entityimpinit from skyline_form_unittest.FG_FORMENTITY t
        minus
        select t.formcode,t.entityimpcode,t.entityimpinit from FG_FORMENTITY t;
 */
 /*
        -- columns compare
        select t.COLUMN_NAME from dba_tab_columns t where t.TABLE_NAME = 'FG_S_ACTION_PIVOT' and t.owner = 'SKYLINE_FORM_SERVER'
        minus
        select t.COLUMN_NAME from user_tab_columns t where t.TABLE_NAME = 'FG_S_ACTION_PIVOT'
 */
        -- set all password to 1234 and exp + year
        --update fg_s_user_pivot t set t.password = '81dc9bdb52d04dc20036dbd8313ed055', t.lastpassworddate = TO_CHAR(SYSDATE + 365,'dd/MM/yyyy')
   end;
   
   procedure completeData as
      sql_ varchar2(32767);
      newline varchar(10) :=  CHR(13) || CHR(10);
      return_ number;
   begin
        --******************************************
        --********** UPDATE fg_tool_inf_all_v FOR fill FG_TOOL_INF_ALL_DATA with id / name / inf_v view name (for the inf_v in the system)
        --******************************************
        --create sql for fg_tool_inf_all_v
        dbms_output.put_line('create sql for fg_tool_inf_all_v');
        for r in (
        select distinct t.sql_ from FG_TOOL_INF_V_SUMMARY t where 1=1 --rownum < 5
        ) loop
        sql_ := sql_ || r.sql_ || newline;
        end loop;
        sql_ := sql_ || ' select null as name, null as id, null as tablename from dual where 1=2 ';
        
        --create view fg_tool_inf_all_v
        dbms_output.put_line('create view fg_tool_inf_all_v');
        execute immediate ' create or replace view fg_tool_inf_all_v as ' || newline || sql_;
        
        --delete FG_TOOL_INF_ALL_DATA
        dbms_output.put_line('delete FG_TOOL_INF_ALL_DATA');
        delete from FG_TOOL_INF_ALL_DATA;
        
        --insert data to FG_TOOL_INF_ALL_DATA
        /*dbms_output.put_line('insert data to FG_TOOL_INF_ALL_DATA');
        insert into FG_TOOL_INF_ALL_DATA (NAME,ID,TABLENAME)
        select t.name,t.id,t.tablename
        from fg_tool_inf_all_v t;*/
        
        --FG_SEQUENCE_INSERT_TRIG disable
        /*dbms_output.put_line('FG_SEQUENCE_INSERT_TRIG disable');
        execute immediate (' ALTER TRIGGER FG_SEQUENCE_INSERT_TRIG disable ');

        --******************************************
        --********** UPDATE fg_sequence with names
        --******************************************
        --add missing rows in fg_sequence
        dbms_output.put_line('add missing rows in fg_sequence');
        insert into fg_sequence (id,formcode,insertdate,formidname)
        select distinct t.id, f.formcode, sysdate, max(nvl(name,'NA')) over (partition by t.id) 
        from FG_TOOL_INF_ALL_DATA t,
             (SELECT distinct * FROM FG_FORM T1 WHERE T1.FORMCODE = T1.FORMCODE_ENTITY and lower(T1.FORMCODE) not like '%yaron%') f 
        where 1=1 
        AND   upper(REPLACE(REPLACE(T.TABLENAME,'FG_S_',''),'_INF_V')) = upper(f.Formcode)
        and   t.id is not null 
        AND   t.id not in (select id from fg_sequence);
        
        --FG_SEQUENCE_INSERT_TRIG enable
        dbms_output.put_line('FG_SEQUENCE_INSERT_TRIG enable');
        execute immediate (' ALTER TRIGGER FG_SEQUENCE_INSERT_TRIG enable ');*/

        --Done!
        dbms_output.put_line('Done!');
        
        --put all system view in table
        return_ := FG_output_system_struct_V;
        
        --for admin report...
        dbms_mview.refresh('FG_R_FORMENTITY_V'); 
        
        --add formcode to maintenance
        COMMIT;
        
    EXCEPTION
      WHEN OTHERS THEN
        --FG_SEQUENCE_INSERT_TRIG enable IN EXCEPTION
        --dbms_output.put_line('FG_SEQUENCE_INSERT_TRIG enable IN EXCEPTION');
        --execute immediate (' ALTER TRIGGER FG_SEQUENCE_INSERT_TRIG enable ');
        
        --Done WITH ERROR
        dbms_output.put_line('Done WITH ERROR');
    
    end;
    
----------------------

function FG_output_STRUCT_ALL_V return varchar  as
  
  --l_search varchar2(1000) := 'union';
  l_char varchar2(32767);
  --toReturn varchar2(32767);

begin
  --for rec in (select text from user_views where VIEW_NAME = upper(viewName_in)  ) 
for rec in (select text, VIEW_NAME from user_views where VIEW_NAME like 'FG_S_%_ALL_V'  )
  loop
    l_char := rec.text;
  /*  if instr(l_char,'--t.* end! edit only the code below...') > 0 then
       toReturn :=
      'create or  replace view ' || upper(viewName_in) || ' as ' || chr(10) || '--t.* ' || chr(10) || 'select t.*, '  || chr(10) ||
      substr(l_char, 
             instr(l_char,
                   '--t.* end! edit only the code below...')
             );
             dbms_output.put_line(toReturn);
      EXECUTE IMMEDIATE toReturn;  
    ELSE*/
               dbms_output.put_line('create or  replace view ' || rec.VIEW_NAME || ' as ' || chr(10) ||
                                     'select t.*' || chr(10) ||
                                     '--t.* end! edit only the code below...' || chr(10) ||
                                     ',' || chr(10) || chr(10) ||  
                                     l_char || chr(10) || chr(10));     
    /*end if;*/
   
     
  end loop;

  return 1;

end;

function FG_output_system_struct_V return varchar  as
  
  --l_search varchar2(1000) := 'union';
  l_char clob;
  --toReturn varchar2(32767);

begin
  delete from fg_r_system_view;
  --for rec in (select text from user_views where VIEW_NAME = upper(viewName_in)  ) 
for rec in (select text, VIEW_NAME from user_views where VIEW_NAME like '%'  )
  loop
    l_char := rec.text;
  /*  if instr(l_char,'--t.* end! edit only the code below...') > 0 then
       toReturn :=
      'create or  replace view ' || upper(viewName_in) || ' as ' || chr(10) || '--t.* ' || chr(10) || 'select t.*, '  || chr(10) ||
      substr(l_char, 
             instr(l_char,
                   '--t.* end! edit only the code below...')
             );
             dbms_output.put_line(toReturn);
      EXECUTE IMMEDIATE toReturn;  
    ELSE*/
               /*dbms_output.put_line('create or  replace view ' || rec.VIEW_NAME || ' as ' || chr(10) ||
                                     'select t.*' || chr(10) ||
                                     '--t.* end! edit only the code below...' || chr(10) ||
                                     ',' || chr(10) || chr(10) ||  
                                     l_char || chr(10) || chr(10));     */
                                      --dbms_output.put_line(rec.VIEW_NAME);
                                     if rec.VIEW_NAME <> 'FG_TOOL_INF_ALL_V' then
                                     
                                       /*if length(l_char) <= 4000 then*/
                                         INSERT INTO fg_r_system_view (db_name,view_name,view_code,view_snapshot_date)
                                         VALUES('SKYLINE_FORM',rec.VIEW_NAME,l_char,TO_CHAR(SYSDATE,'dd/MM/yyyy hh24:mi:ss' ));
                                       /*else
                                         INSERT INTO fg_r_system_view (db_name,view_name,view_code,view_snapshot_date)
                                         VALUES('SKYLINE_FORM',rec.VIEW_NAME, l_char || '...',TO_CHAR(SYSDATE,'dd/MM/yyyy hh24:mi:ss' ));
                                       end if;*/
                                     
                                     end if;
                                     
    /*end if;*/
   
     
  end loop;
  commit;

  return 1;

end;

--function called from GeneralUtilVersionData
--It is used for put long field to clob field of service table (FG_R_MATERIALIZED_VIEW). Temp table is used for create version data script

function fg_output_materialized_view_v (db_name_in varchar) return varchar  as
  
  l_char clob;

begin
  delete from FG_R_MATERIALIZED_VIEW;


for rec in (select query, mview_name from sys.all_mviews where lower(owner) = db_name_in order by staleness/* and mview_name like 'DUMMY_MV%'*/ )
  loop
    l_char := rec.query;
  
           INSERT INTO FG_R_MATERIALIZED_VIEW (db_name,view_name,view_code,view_snapshot_date)
           VALUES(db_name_in,rec.mview_name,l_char,TO_CHAR(SYSDATE,'dd/MM/yyyy hh24:mi:ss' ));
                                   
  end loop;
  commit;

  return 1;

end;


FUNCTION FG_SET_ALL_STRUCT_ALL_V return number as

    toReturn number; 
    
begin
  
     FOR r in  (
         /*select distinct t.formcode
          from fg_form t
          where t.form_type in ('STRUCT','INVITEM','ATTACHMENT','REF','SELECT','MAINTENANCE')
          AND T.FORMCODE NOT IN (select FORMCODE from FG_FORM t WHERE T.FORMCODE <> T.FORMCODE_ENTITY AND T.FORM_TYPE = 'MAINTENANCE')
         and lower(t.formcode) not like 'yaron%'
          and lower(t.formcode) not like 'alex%'
           and lower(t.formcode) not like 'xxx%'
          and lower(t.formcode) not like 'experiment%'
          and lower(t.formcode) not like 'selftestfrmain%'
          and lower(t.formcode) not like 'templateexpselect'
          and lower(t.formcode) not like 'templateselect'
          and lower(t.formcode) not like '%[not in use]%'
          and lower(t.Group_Name) not like 'reftemplate%'
          and lower(t.formcode) not like 'failuretype%'
           and lower(t.formcode) not like 'unittest%'
           and lower(t.formcode) not like 'syseventhandler%'
             and lower(t.formcode) not like 'subprojecttype%'
              and lower(t.formcode_entity) not like 'formtestname%'
                            and lower(t.formcode_entity) not like 'testedentity%'
                               and lower(t.formcode_entity) not like 'customer%'
                               and lower(t.formcode_entity) not like 'prexpimmersion%'
                                and lower(t.formcode_entity) not like 'source%'*/
                            select distinct t.formcode
          from fg_form t
          where t.form_type in ('STRUCT','INVITEM','ATTACHMENT','REF','SELECT','MAINTENANCE')
          AND T.FORMCODE NOT IN (select FORMCODE from FG_FORM t WHERE T.FORMCODE <> T.FORMCODE_ENTITY AND T.FORM_TYPE = 'MAINTENANCE')
        and t.FORMCODE_ENTITY in (select t.formcode_entity from fg_formlastsavevalue t)
        --and EXISTS (select 1 from user_tables t1 where t1.TABLE_NAME = 'FG_S_' || UPPER(t.formcode_entity) || '_PIVOT')
                and lower(t.formcode) not like 'yaron%'
          and lower(t.formcode) not like 'alex%'
           and lower(t.formcode) not like 'xxx%'     
              
              
             
           
          
         )
     LOOP 
       dbms_output.put_line(r.formcode);
       toReturn := fg_set_struct_pivot_table(r.formcode, 1);
     END LOOP;
                
    return toReturn;
end;

procedure initFormlastsavevalueHst as
  adminUserId number;
begin
  DBMS_OUTPUT.put_line('todo');
/*  select min(t.USER_ID) into adminUserId from FG_S_USER_ALL_V t where t.UserRoleName = 'Admin';
  
  delete from fg_formlastsavevalue_hst;
  
  insert into fg_formlastsavevalue_hst (
    FORMID,
    FORMCODE_ENTITY,
    ENTITYIMPCODE,
    ENTITYIMPVALUE,
    USERID,
    change_comment,
    change_by,
    change_type,
    change_date,
    sessionid,
    ACTIVE )
select 
    FORMID,
    FORMCODE_ENTITY,
    ENTITYIMPCODE,
    ENTITYIMPVALUE,
    USERID,
    'Init hst',
    adminUserId,
    'I',
    sysdate,
    sessionid,
    ACTIVE
from fg_formlastsavevalue;*/
end;
    

function updateFormlastsavevalueFromHst (formId_in varchar) RETURN NUMBER as
begin
  dbms_output.put_line('todo');
 delete from fg_formlastsavevalue t where t.formid = formId_in;

insert into fg_formlastsavevalue(formid,FORMCODE_ENTITY,entityimpcode,entityimpvalue,userid,active)
select formid,FORMCODE_ENTITY,entityimpcode,entityimpvalue,userid,active
from (
select
    FORMID,
    FORMCODE_ENTITY,
    ENTITYIMPCODE,
    ENTITYIMPVALUE,
    USERID,
    t.change_date,
    sessionid,
    active,
    max(t.change_date) over (partition by t.formid) as maxDate
from fg_formlastsavevalue_hst t 
where  t.formid  = formId_in 
)
where maxDate = change_date;
--commit;
RETURN 1;
end;

PROCEDURE updateServerMaintenanceData AS
SQL_ VARCHAR2(4000);
SQL_TEMPLATE_ VARCHAR2(4000) := '/*INSERT INTO FG_S_@FORMCODE@_PIVOT (@COL@)*/ SELECT count(*) /*@COL@*/ FROM skyline_form_server.FG_S_@FORMCODE@_PIVOT S WHERE S.@FORMCODE@NAME NOT IN (SELECT @FORMCODE@NAME FROM FG_S_@FORMCODE@_PIVOT);';
SQL_COL_ VARCHAR2(4000);
SQL_COL_LIST_ VARCHAR2(4000);
SQL_COL_TEMPLATE_ VARCHAR2(4000) := '
SELECT LISTAGG(COLUMN_NAME, '','') WITHIN GROUP(ORDER BY COLUMN_NAME) FROM (
SELECT DISTINCT COLUMN_NAME FROM (
select  t.COLUMN_NAME, COUNT(t.COLUMN_NAME) OVER (PARTITION BY t.COLUMN_NAME) AS VC
from  all_tab_columns t
where t.TABLE_NAME = ''FG_S_@FORMCODE@_PIVOT''
and  t.OWNER  IN (''SKYLINE_FORM_COPY2'',''SKYLINE_FORM_SERVER'')
)
WHERE VC = 2)';

BEGIN
  FOR R IN (
  SELECT UPPER(T1.FORMCODE) AS FROMCODE FROM FG_FORM T1 WHERE T1.FORM_TYPE = 'MAINTENANCE'
  )
  LOOP
    SQL_COL_ := REPLACE(SQL_COL_TEMPLATE_,'@FORMCODE@',R.FROMCODE);
    EXECUTE IMMEDIATE SQL_COL_ into SQL_COL_LIST_;
    IF SQL_COL_LIST_ IS NOT NULL THEN
      SQL_ := REPLACE(REPLACE(SQL_TEMPLATE_,'@FORMCODE@',R.FROMCODE),'@COL@',SQL_COL_LIST_);
      DBMS_OUTPUT.put_line(SQL_);
    END IF;
  END LOOP;

END;

function getNextBookMark (str_in varchar) RETURN varchar as
  toretrun varchar(300);
  begin 
    select substr(str_in ,1, length(str_in) - 1) || (substr(str_in ,length(str_in)) + 1) into toretrun from dual;
    return toretrun;
exception
  when others then
    return 'NA';
  
end;

PROCEDURE setLabelElementByNextBookMark as
  begin
       update FG_FORMENTITY f set f.entityimpinit = (
        select /*s.id,*/ REPLACE(s.entityimpinit,'"layoutBookMarkItem','"elementName":"' || t.entityimpcode || '","layoutBookMarkItem') AS GOOD_LABEL_INIT  
        from FG_FORMENTITY t,
             (select *  from FG_FORMENTITY t where   1=1 /*and t.entityimpinit not like'%elementName%'*/ and t.entityimpclass in ('ElementLabelImp')) s
        where 1=1 
        and t.entityimpclass in ('ElementAutoCompleteIdValDDLImp','ElementInputImp','ElementAutoCompleteDDLImp','ElementTextareaImp')
        and s.formcode = t.formcode
        --and '%' || lower(t.entityimpcode) || '%' not like lower(s.entityimpinit)
        and s.entityimpinit not like'%elementName%'
        and form_tool.getNextBookMark(fg_get_value_from_entity_int(s."FORMCODE",s.entityimpcode,'layoutBookMarkItem','')) = fg_get_value_from_entity_int(t."FORMCODE",t.entityimpcode,'layoutBookMarkItem','')
        and f.id = s.id
        ), f.comments = entityimpinit
  where f.id in (
        select s.id
        from/* FG_FORMENTITY t,*/
             (select *  from FG_FORMENTITY t where   1=1 /*and t.entityimpinit not like'%elementName%'*/ and t.entityimpclass in ('ElementLabelImp')) s
        where 1=1 
       /* and t.entityimpclass in ('ElementAutoCompleteIdValDDLImp','ElementInputImp','ElementAutoCompleteDDLImp','ElementTextareaImp')
        and s.formcode = t.formcode*/
        --and '%' || lower(t.entityimpcode) || '%' not like lower(s.entityimpinit)
        and s.entityimpinit not like'%elementName%'
/*        and form_tool.getNextBookMark(fg_get_value_from_entity_int(s."FORMCODE",s.entityimpcode,'layoutBookMarkItem','')) = fg_get_value_from_entity_int(t."FORMCODE",t.entityimpcode,'layoutBookMarkItem','')
*/        


          ) ;

          update FG_FORMENTITY t1 set t1.entityimpinit = t1.comments where t1.entityimpinit is null;
          commit;
    end;

    function deleteFormData (formCode_in varchar, deleteFormDef_in number default 0) return number as
      formcodeEntity_in varchar2(1000);
      is_pivot number;
      begin 
        select nvl(t.formcode_entity,t.formcode) into formcodeEntity_in
        from fg_form t
        where t.formcode = formCode_in;
        --Delete Data
        delete from fg_sequence t where lower(t.formcode) =  lower(formCode_in); --TODO deep delete
        delete from fg_formlastsavevalue t where lower(t.FORMCODE_ENTITY) =  lower(formCode_in);
        delete from fg_formlastsavevalue_hst t where lower(t.FORMCODE_ENTITY) =  lower(formCode_in);
        delete from fg_formlastsavevalue_inf t where lower(t.FORMCODE_ENTITY) =  lower(formCode_in);
        select count(*) into is_pivot from user_tables t where t.TABLE_NAME = upper('fg_s_' || formcodeEntity_in || '_pivot');
        if (is_pivot > 0) then
            execute immediate('delete from fg_s_' || formcodeEntity_in || '_pivot'); 
        end if;
        --Delete Config (if deleteFormDef_in=1)
        if nvl(deleteFormDef_in,0) = 1 then
          --fg_form
          --EXECUTE IMMEDIATE ' CREATE table fg_form_' || to_char(sysdate,'DDMMHH24MISS') || ' as select * from fg_form ';
          --fg_formentity
          --EXECUTE IMMEDIATE ' CREATE table fg_formentity_' || to_char(sysdate,'DDMMHH24MISS') || ' as select * from fg_formentity ';
          --fg_resource
          --EXECUTE IMMEDIATE ' CREATE table fg_resource_' || to_char(sysdate,'DDMMHH24MISS') || ' as select * from fg_resource ';
          
          delete from fg_formentity t where lower(t.formcode) =  lower(formCode_in);
          delete from fg_form t where lower(t.formcode) = lower(formCode_in);
          delete from fg_resource t where lower(t.code) = lower('fg_s_' || formCode_in || '_all_v');
          delete from fg_resource t where lower(t.code) = lower('fg_s_' || formCode_in || '_dt_v');
        end if;
        
        commit;
        return 1;
  end;
  
procedure cleanInvalidData (formCodeIn varchar) as
      formCodeEntity_ varchar2(500);
      invalidSuffix_ varchar2(500);
 begin
        --init formCodeEntity_  
        select t.formcode_entity into formCodeEntity_ from fg_form t where upper(t.formcode) = upper(formCodeIn);
        --select '[inv ' || to_char(sysdate,'ddMMyyyHH24MISS') || ']' into invalidSuffix_ from dual;  
        select '[inv '' || FORMID || '']' into invalidSuffix_ from dual;      
         
        --inactive form FG_FORMLASTSAVEVALUE
        --EXECUTE IMMEDIATE 
        dbms_output.put_line ( 
        ' update fg_formlastsavevalue set entityimpvalue = entityimpvalue || ''' || invalidSuffix_ || ''' where upper(entityimpcode) = upper(''' || formCodeEntity_|| 'Name'') and formid in (select formid from FG_S_' || upper(formCodeIn) || '_INVLD);'
        );
        
        dbms_output.put_line ( 
        ' update fg_formlastsavevalue set active=0 where formid IN (select formid from FG_S_' || upper(formCodeIn) || '_INVLD);'
        );
        
        --inactive form pivot
        --EXECUTE IMMEDIATE 
        dbms_output.put_line ( 
        ' update FG_S_' || upper(formCodeEntity_) || '_PIVOT set active = 0, ' || formCodeEntity_|| 'Name = ' || formCodeEntity_|| 'Name ||''' || invalidSuffix_ || ''' where formid in (select formid from FG_S_' || upper(formCodeIn) || '_INVLD);'
        );
           
        --commit;
 end; 
 
   function removeFromIdFromDB(formId_in varchar, formCodeEntity_in varchar,  ts_in varchar) return number as 
      inuse number;
      sql_ varchar2(5000);
   begin
    select count(*) into inuse from fg_sequence t where t.id = formId_in and t.formcode in (select formcode from fg_form f where f.form_type = 'MAINTENANCE' or f.formcode = 'FormulantRef');
    insert into fg_debug (comment_info) values('sql_1 = ' || sql_);
    if inuse <> 1 then
        return 0;
     end if;
     
     sql_ := 'delete from FG_S_' || formCodeEntity_in || '_PIVOT where formid = ''' || formId_in || ''' ';
     --insert into fg_debug (comment_info) values('sql_2 = ' || sql_);

     execute immediate sql_; 
     delete from fg_formlastsavevalue t where t.formid = formId_in;
     delete from fg_formlastsavevalue_hst t where t.formid = formId_in;
     delete from fg_formlastsavevalue_inf t where t.formid = formId_in;
     delete from fg_sequence t where t.id = formId_in; 
     
     return 1;
   end;
  
procedure unpivotFromUnitTestConf as
      newid varchar(100);
  begin
      --unpivot from  FG_S_UNITTESTCONFIG_PIVOT_TMP to FG_FORMLASTSAVEVALUE_231017_KD; formid as is
      /*insert into FG_FORMLASTSAVEVALUE_231017_KD (formid, formcode_entity, entityimpcode,entityimpvalue, userid, active)
      select  formid, 'UnitTestConfig' as formcode_entity, entityimpcode, entityimpvalue, 23830 as userid, 1 as active
      from FG_S_UNITTESTCONFIG_PIVOT_TMP unpivot-- include nulls
        (entityimpvalue for(entityimpcode) in
          (  
          ENTITYIMPNAME as 'entityImpName',
          FIELDVALUE as 'fieldValue',
          IGNORETEST as 'ignoreTest',
          ORDEROFEXECUTION as 'orderOfExecution',
          TESTINGFORMCODE as 'testingFormCode',
          UNITTESTACTION as 'unitTestAction',
          UNITTESTCONFIGNAME as 'unitTestConfigName',
          UNITTESTGROUP_ID as 'UNITTESTGROUP_ID',
          WAITINGTIME as 'waitingTime'
          )
         )
      order by formid, entityimpcode;
      commit;*/
      
      --copy from temp table FG_FORMLASTSAVEVALUE_231017_KD to fg_formlastsavevalue; insert with new formid
      --declare
        --newid varchar(100);
      --begin
     /*   for r in (
          select distinct t.formid from FG_FORMLASTSAVEVALUE_231017_KD t
        )
        loop
          newid := fg_get_struct_form_id ('UnitTestConfig');
          insert into fg_formlastsavevalue (formid,formcode_entity,entityimpcode,entityimpvalue,userid,active)
          select newid,formcode_entity,entityimpcode,entityimpvalue,userid,active
          from FG_FORMLASTSAVEVALUE_231017_KD t
          where t.formid = r.formid;
        end loop;
        commit;*/
      --end;
      
      --select * from FG_FORMLASTSAVEVALUE_231017_KD t
      --delete from fg_sequence t where t.formcode = 'UnitTestConfig'

      --delete from fg_formlastsavevalue t where t.formcode_entity = 'UnitTestConfig'
      
      /*select t.*, t.rowid from fg_formlastsavevalue t 
      where 1=1
      and t.formcode_entity like '%UnitTestConfig'
      and t.formid in (select e.formid from fg_formlastsavevalue e where e.entityimpcode = 'testingFormCode' and e.entityimpvalue like 'ExperimentMain%')
      and t.entityimpcode = 'ignoreTest'*/
      /*--declare
        newid varchar(100);
      --begin
        for r in (
          select distinct t.formid from fg_formlastsavevalue_kd161117 t
        )
        loop
          newid := fg_get_struct_form_id ('UnitTestConfig');
          insert into fg_formlastsavevalue (formid,formcode_entity,entityimpcode,entityimpvalue,userid,active)
          select newid,formcode_entity,entityimpcode,entityimpvalue,userid,active
          from fg_formlastsavevalue_kd161117 t
          where 1=1
          and t.formid = r.formid
          and t.formcode_entity like '%UnitTestConfig'
          and t.formid in (select e.formid from fg_formlastsavevalue e where e.entityimpcode = 'testingFormCode' and e.entityimpvalue like 'SubProject')
          and t.formid in (select e.formid from fg_formlastsavevalue e where e.entityimpcode = 'UNITTESTGROUP_ID' and e.entityimpvalue like '25319')
          and t.formid in (select e.formid from fg_formlastsavevalue e where e.entityimpcode = 'orderOfExecution' and (e.entityimpvalue > 250 and e.entityimpvalue < 260));
        end loop;
        --commit;
      --end;*/
            dbms_output.put_line('done!');
  end; 
  
  procedure removeDTRemoveButtons as 
  begin
    --set the "Horizontal" button table to hide remove buttons except the maintenance table
    update FG_FORMENTITY t set t.entityimpinit = substr(t.entityimpinit ,1,length(t.entityimpinit)-1) || ',"hideRemoveButton":"True"}', t.comments = 'remove non maintenance remove buttens from DT. BU=' || t.entityimpinit
    where t.entityimpclass = 'ElementDataTableApiImp' 
    and fg_get_value_from_json(t.entityimpinit,'hideRemoveButton') = 'NA'
    AND t.entityimpinit LIKE '%}'
    --and t.entityimpinit not like '%"hideRemoveButton":"%'
    --And  t.formcode in (select formcode from fg_form t1 where t1.form_type <> 'MAINTENANCE')
    AND  t.entityimpinit like  '%"actionButtons":"Horizontal"%'
    AND  t.entityimpinit not like  '%"hideButtons":"True"%'
    --and t.entityimpinit not like '%"hideRemoveButton":"True"}'
    And  t.formcode <> 'Maintenance';
  end;
  
  procedure tool_check_data(db_name_in varchar) as -- db_name_in = db_name to be check against this current user
      --r_tables_list c_tables_list%rowtype; 
      --flagCounter number := 0; 
     tmpMinusQuery varchar2(4000) := ''; 
   -- collist_ varchar2 (4000); 
     --collistAs_ varchar2 (4000);
     -- validateFromFormId varchar2(100);
      counter_ number;
       
       collist_ varchar2 (4000);
       collistAs_ varchar2 (4000);
     -- validateFromFormId varchar2(100); 
     
   begin
    
      counter_ := '0';
      delete from fg_debug; 
      for r in (
            select TABLE_NAME
            from user_tables t
            where 1=1
            AND   UPPER(REPLACE(REPLACE(T.TABLE_NAME,'FG_S_'),'_PIVOT'))
            IN (
                  SELECT UPPER(F.FORMCODE_ENTITY)
                  FROM FG_FORM F
                  WHERE F.FORM_TYPE = 'MAINTENANCE'
                  and f.group_name in ('_System Event Handler','_System Configuration Pool','_System Configuration Report')
                )
      )
      loop
        collistAs_:= gettablecolumnlistnoid(r.TABLE_NAME,'FORMID,TIMESTAMP,CHANGE_BY,ACTIVE,FORMCODE_ENTITY,FORMCODE,CHANGE_BY,SESSIONID,CREATED_BY,CREATION_DATE,EXCELDATA,SQLTEXT',1);
        collist_:= gettablecolumnlistnoid(r.TABLE_NAME,'FORMID,TIMESTAMP,CHANGE_BY,ACTIVE,FORMCODE_ENTITY,FORMCODE,CHANGE_BY,SESSIONID,CREATED_BY,CREATION_DATE,EXCELDATA,SQLTEXT');
        --tmpMinusQuery := ' select count(T1.formid) as formid_counter from ' || r.TABLE_NAME || ' T1 WHERE (' || collist_ || ') IN (' || chr(10) ||
         tmpMinusQuery := ' select count(*) from (' || chr(10) ||
                         ' select ' || collistAs_ || ' from ' || r.TABLE_NAME || chr(10) ||
                         ' minus  ' || chr(10) ||
                         ' select ' || collistAs_ || ' from ' || db_name_in || '.' || r.TABLE_NAME || ')';
        --dbms_output.put_line(tmpMinusQuery);
        insert into fg_debug (comment_info,comments) values ( 'sql maintenanace check', tmpMinusQuery);

        execute immediate tmpMinusQuery into counter_;

        if counter_ > 0 then
          dbms_output.put_line('Warning! ' || r.TABLE_NAME || ' has problem in copy data to the server.');
        end if;

      end loop;
      
      --fg_form
      tmpMinusQuery := ' select count(*) from (' || chr(10) ||
                         ' select formcode, description, active, form_type, title, subtitle, use_as_template, group_name, numberoforder, formcode_entity, ignore_nav, usecache, change_date from fg_form ' || chr(10) ||
                         ' minus  ' || chr(10) ||
                         ' select formcode, description, active, form_type, title, subtitle, use_as_template, group_name, numberoforder, formcode_entity, ignore_nav, usecache, change_date from ' || db_name_in || '.fg_form )';
      --dbms_output.put_line(tmpMinusQuery);
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! fg_form data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql fg_form check', tmpMinusQuery);
       
      --fg_formentity
      tmpMinusQuery := ' select count(*) from (' || chr(10) ||
                         ' select formcode, numberoforder, entitytype, entityimpcode, entityimpclass, entityimpinit, comments, fs, fs_gap from fg_formentity ' || chr(10) ||
                         ' minus  ' || chr(10) ||
                         ' select formcode, numberoforder, entitytype, entityimpcode, entityimpclass, entityimpinit, comments, fs, fs_gap from ' || db_name_in || '.fg_formentity )';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! fg_formentity data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql fg_formentity check', tmpMinusQuery);
      
      --fg_resource
      tmpMinusQuery := ' select count(*) from (' || chr(10) ||
                         ' select type, code, value, info from fg_resource ' || chr(10) ||
                         ' minus  ' || chr(10) ||
                         ' select type, code, value, info from ' || db_name_in || '.fg_resource )';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! fg_resource data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql fg_resource check', tmpMinusQuery);
      
      
      
    /*--d_notification_message->the process of the inserting is made in the server according to a condition
     tmpMinusQuery := ' select count(*) from (' || chr(10) ||
      ' select "D_NOTIFICATION_MESSAGE_ID", "NOTIFICATION_MODULE_ID", "MESSAGE_TYPE_ID", "DESCRIPTION",
      "EMAIL_SUBJECT",to_char("EMAIL_BODY") as "EMAIL_BODY", "NOTIFICATION_SCHEDULER_ID","SCHEDULER_INTERVAL",
      replace(replace(to_char("WHERE_STATEMENT"), chr(10), ''''),chr(13), '''') as "WHERE_STATEMENT",to_char("SELECTED_FIELDS") as "SELECTED_FIELDS",
      "RESEND","ISACTIVE","ISVISIBLE","ATTACHED_REPORT_NAME","ATTACHED_REPORT_TYPE","ADD_ATTACHMENTS","UPDATED_BY","TIME_STAMP","AUDIT_COMMENT","COLUMN_NUMBER",
      "TRIGGER_TYPE_ID","ON_SAVE_ID" from d_notification_message where isactive = 1 ' || chr(10) ||
      ' minus  ' || chr(10) ||
      ' select "D_NOTIFICATION_MESSAGE_ID", "NOTIFICATION_MODULE_ID", "MESSAGE_TYPE_ID", "DESCRIPTION",
      "EMAIL_SUBJECT",to_char("EMAIL_BODY") as "EMAIL_BODY", "NOTIFICATION_SCHEDULER_ID","SCHEDULER_INTERVAL",
      replace(replace(to_char("WHERE_STATEMENT"), chr(10), ''''),chr(13), '''') as "WHERE_STATEMENT",to_char("SELECTED_FIELDS") as "SELECTED_FIELDS",
      "RESEND","ISACTIVE","ISVISIBLE","ATTACHED_REPORT_NAME","ATTACHED_REPORT_TYPE","ADD_ATTACHMENTS","UPDATED_BY","TIME_STAMP","AUDIT_COMMENT","COLUMN_NUMBER",
      "TRIGGER_TYPE_ID","ON_SAVE_ID" from ' || db_name_in || '.d_notification_message where isactive = 1 )';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! d_notification_message data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql d_notification_message check', tmpMinusQuery);*/
      
      tmpMinusQuery := ' select count(*) from (' || chr(10) ||
      ' select addressee_group_id, module_id, addressee_group_title, to_char(addressee_group_select), 
      to_char(params_field_names) from p_notification_listaddresgroup ' || chr(10) ||
      ' minus  ' || chr(10) ||
      ' select addressee_group_id, module_id, addressee_group_title, to_char(addressee_group_select), 
      to_char(params_field_names) from ' || db_name_in || '.p_notification_listaddresgroup )';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! p_notification_listaddresgroup data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql p_notification_listaddresgroup check', tmpMinusQuery);
      
      tmpMinusQuery := ' select count(*) from (' || chr(10) ||
      ' select p_notification_module_type_id, module_name, to_char(view_name), isvisible, uniq_id_filed, to_char(order_by), attached_report_name, 
      attached_report_type from p_notification_module_type ' || chr(10) ||
      ' minus  ' || chr(10) ||
      ' select p_notification_module_type_id, module_name, to_char(view_name), isvisible, uniq_id_filed, to_char(order_by), attached_report_name, 
      attached_report_type from ' || db_name_in || '.p_notification_module_type )';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! p_notification_module_type data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql p_notification_module_type check', tmpMinusQuery);
      
      --FG_NOTIFICATION_COMPARE (summerize  all the notification)
      tmpMinusQuery := ' select count(*) from (' || chr(10) ||
      ' select d_notification_message_id,notification_module_id,message_type_id,description,trigger_type_id,email_subject,email_body,scheduler_interval,where_statement,resend,p_notification_module_type_id,module_name,select_statement,msguniqueidname,order_by,addressee_type_id,send_type,addressee_user_id,params_field_names,addressee_group_select,
        add_attachments,attached_report_name,attached_report_type,isactive from FG_NOTIFICATION_COMPARE '  || chr(10) ||
      ' minus  ' || chr(10) ||
      ' select d_notification_message_id,notification_module_id,message_type_id,description,trigger_type_id,email_subject,email_body,scheduler_interval,where_statement,resend,p_notification_module_type_id,module_name,select_statement,msguniqueidname,order_by,addressee_type_id,send_type,addressee_user_id,params_field_names,addressee_group_select,
        add_attachments,attached_report_name,attached_report_type,isactive from ' || db_name_in ||  '.FG_NOTIFICATION_COMPARE)';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! FG_NOTIFICATION_COMPARE data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql FG_NOTIFICATION_COMPARE check', tmpMinusQuery);
      
      --REPORT_CATEGORY
      tmpMinusQuery := ' select count(*) from (' || chr(10) ||
      ' select id, REPORT_CATEGORY, report_sql, report_description, change_by, active, timestamp, report_user_id, report_scope, report_style, 
      report_name, report_save_data, meta_data from fg_report_list where system_row = 1' || chr(10) ||
      ' minus  ' || chr(10) ||
      ' select id, REPORT_CATEGORY, report_sql, report_description, change_by, active, timestamp, report_user_id, report_scope, report_style, 
      report_name, report_save_data, meta_data from ' || db_name_in || '.fg_report_list  where system_row = 1)';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! fg_report_list data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql fg_report_list check', tmpMinusQuery);
      
     /* tmpMinusQuery := ' select count (*) from (' || chr(10) ||
      ' select  distinct c.parent_id from ' || db_name_in || '.fg_s_invitemmaterial_v m, fg_chem_doodle_data c
      where m.STRUCTURE = c.parent_id and m.active = 1 and c.reaction_all_data_link is not null and c.smiles_data is not null' || chr(10) ||
      'minus ' || chr(10) ||
      ' select distinct t.elementid from ' || db_name_in || '.fg_chem_search t)';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Warning! Table fg_chem_doodle_data presumably contains ' || counter_ || ' records with a duplicated structure');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql fg_s_invitemmaterial_v, fg_chem_search check', tmpMinusQuery);*/
      
      /*
      tmpMinusQuery := ' select count(*) from (' || chr(10) ||
      ' select formid, timestamp, creation_date, cloneid, templateflag, change_by, created_by, sessionid, active, formcode_entity, formcode, 
      unittestconfigcomments, groupname, unittestconfigname, unittestaction, orderofexecution, ignoretest, waitingtime, entityimpname, 
      testingformcode, fieldvalue from fg_s_unittestconfig_pivot  ' || chr(10) ||
      ' minus  ' || chr(10) ||
      ' select formid, timestamp, creation_date, cloneid, templateflag, change_by, created_by, sessionid, active, formcode_entity, formcode, 
      unittestconfigcomments, groupname, unittestconfigname, unittestaction, orderofexecution, ignoretest, waitingtime, entityimpname, 
      testingformcode, fieldvalue from ' || db_name_in || '.fg_s_unittestconfig_pivot )';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! fg_s_unittestconfig_pivot data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql fg_s_unittestconfig_pivot check', tmpMinusQuery);
      
      tmpMinusQuery := ' select count(*) from (' || chr(10) ||
      ' select formid, timestamp, change_by, sessionid, active, formcode_entity, formcode, unittestgroupname, orderofexecution, ignore, 
      unittestlevels, comments, cloneid, templateflag, created_by, creation_date from fg_s_unittestgroup_pivot  ' || chr(10) ||
      ' minus  ' || chr(10) ||
      ' select formid, timestamp, change_by, sessionid, active, formcode_entity, formcode, unittestgroupname, orderofexecution, ignore, 
      unittestlevels, comments, cloneid, templateflag, created_by, creation_date from ' || db_name_in || '.fg_s_unittestgroup_pivot )';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! fg_s_unittestgroup_pivot data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql fg_s_unittestgroup_pivot check', tmpMinusQuery);
      */
      
      commit;
      
      dbms_output.put_line('If all you see is this line we are OK.');
    
      -- show list of all_v / dt_v views with formid duplication (without views from select forms like userscrew)
      /*   for r in (
            select VIEW_NAME 
            from ALL_VIEWS t
          where UPPER(t.VIEW_NAME) like UPPER('fg_s%all_v') or UPPER(t.VIEW_NAME) like UPPER('fg_s%dt_v')
           
      )
       loop
         tmpMinusQuery := ' select count(*) from (' || chr(10) ||
                         ' select  count(*) as c, count(distinct t.formid) as cd from ' || r.VIEW_NAME ||' t) t1' ||chr(10)||
                         ' where t1.c <> t1.cd ; ' ;
        execute immediate tmpMinusQuery into counter_; 
        if counter_ > 0 then
          dbms_output.put_line('Warn: duplication found in '|| r.VIEW_NAME);
        end if;
        
      end loop;  
      */   
      -- show list of tables (pivot) with formid that are not in FG_SEQUENCE (name of table and count of formid that are not in FG_SEQUENCE) [expect empty list]
     /* for r in (
            select TABLE_NAME 
            from user_tables t
          where UPPER(t.TABLE_NAME) like UPPER('fg_s%pivot')
           
      )
      loop
         tmpMinusQuery := ' select  count(*) from ' || db_name_in || '.' || r.TABLE_NAME ||' t where t.formid not in (select f.id from FG_SEQUENCE f )' ;
         execute immediate tmpMinusQuery into counter_; 
         if counter_ > 0 then
            dbms_output.put_line('Warn: table with missing formid in FG_SEQUENCE:'||r.table_name||'('||counter_||')');
         end if;
      end loop;*/  
  
  
      -- show list of tables (pivot) with missing form code (we check from validateFromFormId)
     /* for r in (
            select TABLE_NAME 
            from user_tables t
          where UPPER(t.TABLE_NAME) like UPPER('fg_s%pivot')
           
      )
       loop
        tmpMinusQuery := 'select count(*) from ' || db_name_in || '.'||r.TABLE_NAME ||' t where  t.formcode IS NULL';
        execute immediate tmpMinusQuery into counter_; 
        
        if counter_ > 0 then
         dbms_output.put_line('Warn: table with missing formcode :'||r.table_name||'('||counter_||')');
        end if;
      end loop; */
      
      -- show list of tables (pivot) with missing form code entity (we check from validateFromFormId)
     /* for r in (
            select TABLE_NAME 
            from user_tables t
          where UPPER(t.TABLE_NAME) like UPPER('fg_s%pivot')
           
      )
      loop
         tmpMinusQuery := 'select count(*) from ' || db_name_in || '.'||r.TABLE_NAME ||' t where t.formcode_entity IS NULL';
        execute immediate tmpMinusQuery into counter_; 
        
        if counter_ > 0 then
         dbms_output.put_line('Warn: table with missing formcode_entity :'||r.table_name||'('||counter_||')');
        end if;
      end loop; */
      
      -- show list of FG_SEQUENCE with formcode not from fg_form or from special element (see FG_GET_STRUCT_FORM_ID call in java and DB)
      --(?) list of invalid DB object
      /*   FOR r IN (select  OBJECT_NAME  from dba_objects where status=upper('invalid')) 
       
         loop
            DBMS_OUTPUT.PUT_LINE(r.OBJECT_NAME ||',');
         end loop;
      */
   end;
   
   
      
end;
/
