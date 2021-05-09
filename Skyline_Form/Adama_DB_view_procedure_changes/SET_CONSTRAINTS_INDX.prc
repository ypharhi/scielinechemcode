create or replace procedure SET_CONSTRAINTS_INDX as

begin
  SET_SINGLE_CONSTRAINTS_INFO('PROJECT_NAME_UNIQUE', 'FG_S_PROJECT_PIVOT', 'PROJECTNAME', 'PROJECTNAME', 1);
  SET_SINGLE_CONSTRAINTS_INFO('SUBPROJECT_NAME_UNIQUE','FG_S_SUBPROJECT_PIVOT','SUBPROJECTNAME','SUBPROJECTNAME,PROJECT_ID',1);
  SET_SINGLE_CONSTRAINTS_INFO('SUBSUBPROJECT_NAME_UNIQUE','FG_S_SUBSUBPROJECT_PIVOT','SUBSUBPROJECTNAME','SUBSUBPROJECTNAME,PROJECT_ID',1);
  /*
   SET_SINGLE_CONSTRAINTS_INFO('MATERIAL_NAME_UNIQUE','FG_S_INVITEMMATERIAL_PIVOT','INVITEMMATERIALNAME','INVITEMMATERIALNAME,ACTIVE',1);
   --> USE THIS SPECIFIC INDEX..
   CREATE UNIQUE INDEX MATERIAL_NAME_UNIQUE ON FG_S_INVITEMMATERIAL_PIVOT(TRIM(UPPER(DECODE(UPPER(MATERIALPROTOCOLTYPE),'CHEMICAL MATERIAL',INVITEMMATERIALNAME||','||COFORMULANT||','||ACTIVE,'FORMID_' || FORMID))))
  */
  SET_SINGLE_CONSTRAINTS_INFO('COLUMN_NAME_UNIQUE','FG_S_INVITEMCOLUMN_PIVOT','INVITEMCOLUMNNAME','INVITEMCOLUMNNAME,ACTIVE',1);
  SET_SINGLE_CONSTRAINTS_INFO('USER_NAME_UNIQUE','FG_S_USER_PIVOT','USERNAME','USERNAME',1);
  SET_SINGLE_CONSTRAINTS_INFO('STEP_NAME_UNIQUE','FG_S_STEP_PIVOT','STEPNAME','STEPNAME,EXPERIMENT_ID,TEMPLATEFLAG,CLONEID,RUNNUMBER',1);   
  SET_SINGLE_CONSTRAINTS_INFO('INSTRUMENT_NAME_SERIAL_UNIQUE','FG_S_INVITEMINSTRUMENT_PIVOT','INVITEMINSTRUMENTNAME','INVITEMINSTRUMENTNAME,SERIALNUMBER,CLONEID,ACTIVE',1);
  --SET_SINGLE_CONSTRAINTS_INDX('ITEM_ID_UNIQUE', 'FG_S_FORMULANTREF_PIVOT', 'ITEMID', 'ITEMID',1);
  SET_SINGLE_CONSTRAINTS_INFO('LABORATORY_ID_UNIQUE', 'FG_S_LABORATORY_PIVOT', 'FORMNUMBERID', 'FORMNUMBERID',1);
  SET_SINGLE_CONSTRAINTS_INFO('MATERIAL_UNIQUE', 'FG_S_FORMULANTREF_PIVOT','MATERIALID','PARENTID,MATERIALID,CLONEID,TABLETYPE',1);
  SET_SINGLE_CONSTRAINTS_INFO('UOM_UNIQUE', 'FG_S_UOM_PIVOT','UOMNAME','ACTIVE,UOMNAME,TYPE',1);
  --SET_SINGLE_CONSTRAINTS_INFO('MATERIAL_UNIQUE', 'FG_S_COMPONENT_PIVOT','MATERIALID','ACTIVE,MATERIALID',1); --yp 24102019 added in last versions (we do not know why and if needed why the scope is without parentid (!?)) - I commented this line untill we get answers...
  SET_SINGLE_CONSTRAINTS_INFO('SPECIFICATION_UNIQUE', 'FG_S_SPECIFICATIONREF_PIVOT','SPECIFICATION','ACTIVE,SUBPROJECT_ID,SPECIFICATION,SESSIONID',1);  
  SET_SINGLE_CONSTRAINTS_INFO('EXPERIMENT_UNIQUE', 'FG_S_EXPERIMENT_PIVOT','FORMID','FORMID',1);
  SET_SINGLE_CONSTRAINTS_INFO('BATCHNAME_UNIQUE','FG_S_INVITEMBATCH_PIVOT','INVITEMBATCHNAME','INVITEMBATCHNAME,ACTIVE',1);
  SET_SINGLE_CONSTRAINTS_INFO('MANUFACTURER_UNIQUE','FG_S_MANUFACTURER_PIVOT','MANUFACTURERNAME','MANUFACTURERNAME,ACTIVE',1);
  --SET_SINGLE_CONSTRAINTS_INFO('SPREADSHEETTEMPLATE_UNIQUE','FG_S_SPREADSHEETTEMPLA_PIVOT','SPREADSHEETTEMPLANAME,ACTIVE',1);
  SET_SINGLE_CONSTRAINTS_INFO('FUNCTIONNAME_UNIQUE','FG_S_MATERIALFUNCTION_PIVOT','MATERIALFUNCTIONNAME','MATERIALFUNCTIONNAME,ACTIVE',1);
  --SET_SINGLE_CONSTRAINTS_INFO('GROUPNUMBER_UNIQUE','FG_S_EXPERIMENTGROUP_PIVOT','GROUPNUMBER','GROUPNUMBER,ACTIVE',1);
   --maintenance part(general,1 value) 
   
   for maint in (
     select upper(formcode_entity) as formcode_entity --'SET_SINGLE_CONSTRAINTS_INDX(''' || upper(formcode_entity) || '_UNIQUE'',''FG_S_' || upper(formcode )||'_PIVOT'','''|| upper(formcode_entity) || 'NAME'','''|| upper(formcode_entity) || 'NAME,ACTIVE'',1);' as CONSTRAINT_MAINT
     from FG_FORM t
     where t.form_type = 'MAINTENANCE' 
     and t.group_name = 'General' and 12 in ( select count(*) from user_tab_columns where table_name= 'FG_S_' || upper(formcode )||'_PIVOT')
     )
     loop
       SET_SINGLE_CONSTRAINTS_INFO( maint.formcode_entity || '_UNIQUE','FG_S_' || maint.formcode_entity||'_PIVOT', maint.formcode_entity || 'NAME',  maint.formcode_entity || 'NAME,ACTIVE',1);
      -- EXECUTE IMMEDIATE  (maint.constraint_maint);
      -- dbms_output.put_line( 'SET_SINGLE_CONSTRAINTS_INDX('''|| maint.formcode_entity || '_UNIQUE'',''FG_S_' || maint.formcode_entity||'_PIVOT'','''||  maint.formcode_entity || 'NAME'','''||  maint.formcode_entity || 'NAME,ACTIVE'',1);'
     end loop;
      
      for r in (
           select distinct TABLE_NAME, 
                   UPPER(REPLACE(replace(TABLE_NAME,'FG_S_',''),'_PIVOT','')) AS TABLE_FORM_CODE,
                   DECODE(F.FORM_TYPE,'MAINTENANCE','FORMID,SESSIONID,ACTIVE,CLONEID,TIMESTAMP','FORMID,SESSIONID,ACTIVE,CLONEID') AS COL_LIST
            from user_tables t,
                 FG_FORM F
            where 1=1
            AND   UPPER(F.FORMCODE_ENTITY) = UPPER(REPLACE(REPLACE(T.TABLE_NAME,'FG_S_'),'_PIVOT'))
            AND   'DUP_' || UPPER(REPLACE(replace(TABLE_NAME,'FG_S_',''),'_PIVOT','')) NOT IN (SELECT USER_CONSTRAINTS.constraint_name FROM USER_CONSTRAINTS)
           
      )
      loop
          /*EXECUTE  IMMEDIATE ('ALTER TABLE ' || r.TABLE_NAME ||
                         ' ADD CONSTRAINT ' || constraint_name_in || ' UNIQUE (FORMID,SESSIONID,ACTIVE,CLONEID)');*/
        --SET_DUP_CONSTRAINTS('DUP_' || R.TABLE_FORM_CODE, r.TABLE_NAME, R.COL_LIST);
       dbms_output.put_line( 'DUP_' || R.TABLE_FORM_CODE);
       END LOOP;
       
      -- Formnumberid Constraints
      SET_SINGLE_CONSTRAINTS_INFO('FORMNUMBERID_FORMAT_BATCH','FG_S_INVITEMBATCH_PIVOT','REGEXP_LIKE(formnumberid,''^....-..-...-....-B[1-9][0-9]*'')','',1,1);
      --  execute immediate 'alter table FG_S_INVITEMBATCH_PIVOT add constraint FORMNUMBERID_FORMAT_BATCH check (REGEXP_LIKE(formnumberid,''....-..-...-....-B[1-9][0-9]*''))';
      SET_SINGLE_CONSTRAINTS_INFO('FORMNUMBERID_FORMAT_SUBPROJECT','FG_S_SUBPROJECT_PIVOT','REGEXP_LIKE(FORMNUMBERID,''^....-..$'')','',1,1); 
      -- EXECUTE IMMEDIATE 'ALTER TABLE FG_S_SUBPROJECT_PIVOT ADD CONSTRAINT FORMNUMBERID_FORMAT_SUBPROJECT CHECK (REGEXP_LIKE(FORMNUMBERID,''....-..$''))';
      SET_SINGLE_CONSTRAINTS_INFO('FORMNUMBERID_FORMAT_SSPROJECT','FG_S_SUBSUBPROJECT_PIVOT','REGEXP_LIKE(FORMNUMBERID,''^....-..-..$'')','',1,1); 
      -- EXECUTE IMMEDIATE 'ALTER TABLE FG_S_SUBSUBPROJECT_PIVOT ADD CONSTRAINT FORMNUMBERID_FORMAT_SSPROJECT CHECK (REGEXP_LIKE(FORMNUMBERID,''....-..-..$''))';
      SET_SINGLE_CONSTRAINTS_INFO('FORMNUMBERID_FORMAT_EXPERIMENT','FG_S_EXPERIMENT_PIVOT','REGEXP_LIKE(FORMNUMBERID,''^....-..-...-....$'')','',1,1); 
      -- EXECUTE IMMEDIATE 'ALTER TABLE FG_S_EXPERIMENT_PIVOT ADD CONSTRAINT FORMNUMBERID_FORMAT_EXPERIMENT CHECK (REGEXP_LIKE(FORMNUMBERID,''....-..-...-....$''))';
      SET_SINGLE_CONSTRAINTS_INFO('FORMNUMBERID_FORMAT_STEP','FG_S_STEP_PIVOT','REGEXP_LIKE(FORMNUMBERID,''^..$'')','',1,1); 
       --EXECUTE IMMEDIATE 'ALTER TABLE FG_S_STEP_PIVOT ADD CONSTRAINT FORMNUMBERID_FORMAT_STEP CHECK (REGEXP_LIKE(FORMNUMBERID,''..$''))';
      SET_SINGLE_CONSTRAINTS_INFO('FORMNUMBERID_FORMAT_ACTION','FG_S_ACTION_PIVOT','REGEXP_LIKE(FORMNUMBERID,''^..$'')','',1,1); 
       --EXECUTE IMMEDIATE 'ALTER TABLE FG_S_ACTION_PIVOT ADD CONSTRAINT FORMNUMBERID_FORMAT_ACTION CHECK (REGEXP_LIKE(FORMNUMBERID,''..$''))';
      SET_SINGLE_CONSTRAINTS_INFO('FORMNUMBERID_FORMAT_REQUEST','FG_S_REQUEST_PIVOT','REGEXP_LIKE(REQUESTNAME,''....-..-.....(-OP[1-9][0-9]*)??'')','',1,1); 
       --EXECUTE IMMEDIATE 'ALTER TABLE FG_S_REQUEST_PIVOT ADD CONSTRAINT FORMNUMBERID_FORMAT_REQUEST CHECK (REGEXP_LIKE(REQUESTNAME,''....-..-.....(-OP[1-9][0-9]*)??''))';
      SET_SINGLE_CONSTRAINTS_INFO('FORMNUMBERID_FORMAT_SAMPLE','FG_S_SAMPLE_PIVOT','REGEXP_LIKE(FORMNUMBERID,''^....-..-...-....-..$'')','',1,1); 
       --EXECUTE IMMEDIATE 'ALTER TABLE FG_S_SAMPLE_PIVOT ADD CONSTRAINT FORMNUMBERID_FORMAT_SAMPLE CHECK (REGEXP_LIKE(FORMNUMBERID,''....-..-...-....-..$''))';
      --SET_SINGLE_CONSTRAINTS_INFO('GROUPNUMBER_FORMAT','FG_S_EXPERIMENTGROUP_PIVOT','REGEXP_LIKE(GROUPNUMBER,''....-....-....'')','',1,1); 

end/* SET_CONSTRAINTS */;
/
