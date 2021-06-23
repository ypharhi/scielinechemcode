create or replace function fg_clob_files_confexcel_insert (file_content_in clob) return varchar as
  to_return  varchar(1000);
begin
  to_return:=FG_GET_STRUCT_FILE_ID('SysConfExcelData.excelFile');

  insert into fg_clob_files (file_id,file_name,file_content)
  values (to_return,'',file_content_in);

  return to_return;
end;
/
