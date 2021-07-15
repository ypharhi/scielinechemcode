create or replace function fg_get_component_list(selected_component_id_in in varchar) return clob as
 toReturn clob;
 selectedList_ varchar2(32767);
 materialList_ clob:= empty_clob();
 id_ varchar2(32767);
 name_ varchar2(32767);
begin
--materialList_ := materialList_ || ',' || '{"ID":"-1","VAL":"Choose","TOOLTIP":""}';

for r in (select distinct t.INVITEMMATERIAL_ID,t.INVITEMMATERIALNAME
         ,'{"ID":"'||t.INVITEMMATERIAL_ID||'","displayName":"'||t.INVITEMMATERIALNAME||'"}' as material_link
         ,'{"ID":"'||t.INVITEMMATERIAL_ID||'","VAL":"'||t.INVITEMMATERIALNAME||'","TOOLTIP":"'||t.INVITEMMATERIALNAME||'"}' as material_ddl
        from FG_I_CONN_MATERIAL_TYPE_V t
        --and (t.MATERIALTYPENAME = 'Active Ingredient' or t.MATERIALTYPENAME = 'Impurity')
        where upper(t.materialtypename) in ('ACTIVE INGREDIENT', 'FINAL PRODUCT', 'IMPURITY', 'INTERMEDIATE','PRODUCT')   
        )

loop

  if r.INVITEMMATERIAL_ID is not null and r.INVITEMMATERIAL_ID = selected_component_id_in and instr(','||selectedList_||',', ','||r.material_link||',')<=0 then
    selectedList_ := selectedList_ || ',' || r.material_link;
  end if;

  if r.INVITEMMATERIAL_ID is not null and instr(','||materialList_||',', ','||r.material_ddl||',')<=0 then
    if materialList_ = empty_clob() then
          materialList_ := fg_concat_clob(materialList_, r.material_ddl);
      else
          materialList_ := fg_concat_clob(materialList_, ',' || r.material_ddl);
      end if;
  --end if;
  elsif r.INVITEMMATERIAL_ID is not null and r.INVITEMMATERIAL_ID = selected_component_id_in then
    if materialList_ = empty_clob() then
          materialList_ := fg_concat_clob(materialList_, r.material_ddl);
      else
          materialList_ := fg_concat_clob(materialList_, ',' || r.material_ddl);
      end if;
  end if;
end loop;


if selectedList_ is not null then
  selectedList_ := substr(selectedList_,2);
elsif selected_component_id_in <> -1 then -- in case the selectd value is not in the list ->
   --get data
   select t.INVITEMMATERIAL_ID, t.INVITEMMATERIALNAME
          into  id_,name_
   from FG_S_InvItemMaterial_all_V t
   where t.INVITEMMATERIAL_ID = selected_component_id_in;

   --add it to selectedList_
   selectedList_ := '{"ID":"'||id_||'","displayName":"'||name_||'"}';

   --add it to materialList_
    if materialList_ = empty_clob() then
          materialList_ := fg_concat_clob(materialList_, '{"ID":"'||id_||'","VAL":"'||name_||'","TOOLTIP":"'||name_||'"}');
      else
          materialList_ := fg_concat_clob('{"ID":"'||id_||'","VAL":"'||name_||'","TOOLTIP":"'||name_||'"}', ',' || materialList_);
      end if;
end if;


toReturn := '{"displayName":[' || selectedList_ || '],'||
            ' "htmlType":"select","displayAsLink":"true","allowSingleDeselect":"false","autoSave":"true","renderTableAfterSave":"true",'||
            ' "dbColumnName":"INVITEMMATERIAL_ID","formCode":"'||'InvItemMaterial'||'","formId":"'||selected_component_id_in||'",'||
            ' "fullList":['|| materialList_ ||']}';

return toReturn;

exception
  when others then -- show the selected id if exists
    if selected_component_id_in is not null then
       --get data
       select t.INVITEMMATERIAL_ID, t.INVITEMMATERIALNAME
              into  id_,name_
       from FG_S_InvItemMaterial_all_V t
       where t.INVITEMMATERIAL_ID = selected_component_id_in;
       
       --add it to selectedList_
       selectedList_ := '{"ID":"'||id_||'","displayName":"'||name_||'"}';
       materialList_ := '{"ID":"'||id_||'","VAL":"'||name_||'","TOOLTIP":"'||name_||'"}';

end if;
    --end if;

    toReturn := '{"displayName":[' || selectedList_ || '],'||
            ' "htmlType":"select","displayAsLink":"true","allowSingleDeselect":"false","autoSave":"true","renderTableAfterSave":"true",'||
            ' "dbColumnName":"INVITEMMATERIAL_ID","formCode":"'||'InvItemMaterial'||'","formId":"'||selected_component_id_in||'",'||
            ' "mandatory":"true", "fullList":['|| materialList_ ||']}';

    return toReturn;
end;
/
