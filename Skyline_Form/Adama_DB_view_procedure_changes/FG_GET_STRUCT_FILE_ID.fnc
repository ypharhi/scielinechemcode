create or replace function FG_GET_STRUCT_FILE_ID(form_code_in varchar, formid_in varchar default null, ts_in varchar default '0') return number as
 nextValNumber number;
begin
  insert into FG_SEQUENCE_FILES (FORMCODE,INSERTDATE,SOURCE_FORMID) 
  --values (decode (form_code_in,'ExperimentMain','Experiment','RequestMain','Request','ExpSeriesMain','ExperimentSeries',form_code_in),sysdate); -- we make it in postsave event here its just ->
  values (form_code_in,sysdate,formid_in);

  select FG_SEQUENCE_FILES_SEQ.CURRVAL into nextValNumber from dual;
     
  --insert into fg_debug(comments) values('FG_GET_STRUCT_FILE_ID (with timestamp): '|| nextValNumber || ts_in || ', nextValNumber=' || nextValNumber); --commit;
  
  return nextValNumber;
end;
/
