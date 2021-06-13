create or replace function fg_get_RichText_display ( RichText_id_in in varchar, displayType_in in number default 1) return varchar2 as
 toReturn varchar2(4000);
begin
   --DBMS_OUTPUT.put_line('CALL fg_get_RichText_display'); --kd 24022020 commented this row because was error (not enough space in the buffer)

  if RichText_id_in is null then
    return null;
  end if;
--&lt;
  if displayType_in = 1 then -- 1: simple text top 4000 
    select trim(
                replace(
                    replace(
                        regexp_replace(
                                       regexp_replace(
                                                       regexp_replace(
                                                                      regexp_replace(
                                                                                     DBMS_LOB.substr(t.file_content, 4000),
                                                                                     --DBMS_LOB.substr(fg_no_html(t.file_content), 4000), 
                                                                                    -- '<.*?>',' ')
                                                                                     '<.*?>' , '', 1, 0, 'n')
                                                                      ,'\&lt;.*?\&gt;',' ')
                                                       ,'[[:space:]]',' ')
                                       ,' {2,}', ' ')
                        ,'&nbsp;','')
                    ,'"','\"')
                ) into toReturn
    from fg_richtext t
    where  t.file_id = RichText_id_in;
   elsif displayType_in = 2 then  -- 2: return text (richtext getText())
    select t.file_content_text
    into toReturn
    from fg_richtext t
    where  t.file_id = RichText_id_in;
  end if;  

  /*if displayType_in = 2 then -- 1: simple text top 1000
    select DBMS_LOB.substr(fg_no_html(t.file_content), 1000) into toReturn
    from fg_richtext t
    where  t.file_id = RichText_id_in;
  end if; --fg_get_richtext_isnull*/

   return toReturn;
exception
  when others then
    return null;
end;
/
