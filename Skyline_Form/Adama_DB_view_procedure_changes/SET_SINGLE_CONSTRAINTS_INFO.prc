create or replace procedure SET_SINGLE_CONSTRAINTS_INFO(constraint_name_in varchar2, table_in varchar2,  column_in varchar2, uniqe_columns_in varchar2, drop_always_in number default 0, is_check_constraint_in number default 0) as
   is_constraint_exist integer;
   uniqe_columns varchar2(1000);
   command varchar2(32767);
begin
   
   --finding out if the constraint is already defined
   SELECT count(*)
      INTO is_constraint_exist
   FROM user_constraints
   WHERE constraint_name = constraint_name_in;

   if is_constraint_exist = 1 and drop_always_in = 1 THEN
      EXECUTE IMMEDIATE ' alter table ' || table_in || ' drop CONSTRAINT ' || UPPER(constraint_name_in);
   end if;

   if is_check_constraint_in = 0 then
     --finding out if the indx is already defined
     SELECT count(*)
        INTO is_constraint_exist
     FROM USER_indexes
     WHERE UPPER(USER_indexes.index_name) = UPPER(constraint_name_in);

     if is_constraint_exist = 1 and drop_always_in = 1 THEN
        EXECUTE IMMEDIATE ' drop index ' || UPPER(constraint_name_in);
     end if;
    end if;

   --alter table
   /*for i in (
     SELECT  regexp_substr(uniqe_columns_in, '[^,]+', 1,LEVEL ) str
     FROM  dual
       CONNECT BY regexp_substr(uniqe_columns_in , '[^,]+', 1,LEVEL) IS NOT NULL
     )
     loop
       EXECUTE IMMEDIATE (' alter table ' || table_in || ' modify ' || i.str || ' VARCHAR2(500) ');
     end loop;
  \* if instr(uniqe_columns_in,',') > 0 then
     EXECUTE IMMEDIATE  ' alter table ' || table_in || ' modify ' || substr(uniqe_columns_in,1,instr(uniqe_columns_in,',') - 1)  || ' VARCHAR2(1000) ';
     EXECUTE IMMEDIATE  ' alter table ' || table_in || ' modify ' || substr(uniqe_columns_in, instr(uniqe_columns_in,',') + 1) || ' VARCHAR2(1000) ';
   else
     EXECUTE IMMEDIATE  ' alter table ' || table_in || ' modify ' || uniqe_columns_in || ' VARCHAR2(1000) ';
   end if;*\*/
  
   --create only indx that allow case insensitive
   if is_constraint_exist = 0 or drop_always_in = 1 THEN
      BEGIN
         FOR i IN
         (SELECT  regexp_substr(uniqe_columns_in, '[^,]+', 1,LEVEL ) str
          FROM  dual
          CONNECT BY regexp_substr(uniqe_columns_in , '[^,]+', 1,LEVEL) IS NOT NULL
         )
          LOOP
             uniqe_columns:= uniqe_columns || 'TRIM(UPPER('||(i.str) || ')),' ;
          END LOOP;
      
          uniqe_columns:= SUBSTR(uniqe_columns,1,LENGTH(uniqe_columns)-1);
             
          if is_check_constraint_in = 0 then
            command := 'CREATE UNIQUE INDEX ' || constraint_name_in ||
                           ' ON ' || table_in || '('||uniqe_columns||')';        
            EXECUTE IMMEDIATE (command);
          else
            command := 'ALTER TABLE ' || table_in ||
                           ' ADD CONSTRAINT ' || constraint_name_in || ' CHECK ' || '('||column_in||')';
            EXECUTE IMMEDIATE (command);
          end if;
          COMMIT;
           
       EXCEPTION
          when others then
          DBMS_OUTPUT.put_line('--FAILURE IN SETTING COMMAND:');
          DBMS_OUTPUT.put_line(command);     
       END;
   END IF; 
     
end;
/
