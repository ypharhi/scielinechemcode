create or replace function fg_get_batch_list(selected_material_id_in in varchar,selected_batch_id_in in varchar,is_disabled_in varchar default 'false',is_autosave_in varchar default 'false') return clob as
 toReturn clob;
 selectedList_ varchar2(32767);
 batchList_ varchar2(32767);
begin
--yp 25/10/2020 fix bug 8264 replace CHR(10) in batch comments
for r in (select distinct t.invitembatch_id,t.InvItemBatchName
         ,'{"ID":"'||t.invitembatch_id||'","displayName":"'||t.InvItemBatchName||'"}' as batch_link
         ,'{"ID":"'||t.invitembatch_id||'","VAL":"'||t.InvItemBatchName||'","ACTIVE":"' || decode(nvl(t.INUSEDEPLETED,'In Use'),'In Use',1,0) || '","TOOLTIP":"Purity: '||'\"'||fg_get_num_display(nvl(t.purity,100),4)||''||fg_get_Uom_display (t.PURITYUOM_ID)||'\", '||' Receipt Date: '||'\"'||nvl(t.RECEIPTDATE,to_char(t.CREATION_DATE,'DD/MM/YYYY'))||'\"'||NVL2(TO_CHAR(m.ManufacturerName),', Manufacturer: '||'\"'||m.ManufacturerName||'\"','')||NVL2(TO_CHAR(t.COMMENTS),','||
              ' Comments: '||'\"'|| replace(replace(replace(t.COMMENTS,'\','\\'),'"','\"'),CHR(10),' ') ||'\"','')||
              '"}' as batch_ddl
        from fg_s_invitembatch_v t, fg_s_manufacturer_v m
        where selected_material_id_in is not null
        and decode(t.INVITEMMATERIAL_ID,selected_material_id_in,1,decode(t.recipeformulation_id,selected_material_id_in,1,0) )= 1-- t.INVITEMMATERIAL_ID = selected_material_id_in
        and decode(t.invitembatch_id,selected_batch_id_in,'In Use',nvl(t.INUSEDEPLETED,'In Use')) = 'In Use'
        and t.MANUFACTURER_ID = m.manufacturer_id(+)
        order by t.InvItemBatchName)
loop
  if r.invitembatch_id is not null and r.invitembatch_id = selected_batch_id_in and instr(','||selectedList_||',', ','||r.batch_link||',')<=0 then
    selectedList_ := selectedList_ || ',' || r.batch_link;
  end if;
  if r.invitembatch_id is not null and instr(','||batchList_||',', ','||r.batch_ddl||',')<=0 then
    batchList_ := batchList_ || ',' || r.batch_ddl;
  end if;
end loop;

if batchList_ is not null then
  batchList_ := substr(batchList_,2);
end if;

if selectedList_ is not null then
  selectedList_ := substr(selectedList_,2);
end if;

toReturn := '{"displayName":[' || selectedList_ || '],'||
            ' "htmlType":"select","isDisabled":"'||is_disabled_in||'","displayAsLink":"true","colCalcId":"BATCH_ID","customFuncName":"onChangeBatch",'||
            ' "dbColumnName":"batch_id","formCode":"'||'InvItemBatch'||'","formId":"'||selected_batch_id_in||'",'||
            ' "fullList":['|| batchList_ ||'],"autoSave":"'||is_autosave_in||'"}';
return toReturn;
end;
/
