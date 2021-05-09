create or replace function fg_get_material_list (selected_material_id_in in varchar,
                                                 top_num_in number, 
                                                 table_type_in in varchar, 
                                                 is_disabled_in varchar default 'false',
                                                 is_autosave_in varchar default 'false',
                                                 formcode_in varchar default null,
                                                 project_id_scope_in varchar default null)
return clob as
 toReturn clob := empty_clob();
 selectedList_ clob := empty_clob();
 materialList_ clob:= empty_clob();
begin
if table_type_in = 'Premix Recipe' or table_type_in = 'Recipe' then
  for r in (
        select distinct * from
            (select distinct t.recipeformulation_id,t.RecipeFormulationName
            ,'{"ID":"'||t.recipeformulation_id||'","displayName":"'||t.RecipeFormulationName||'"}' as recipe_link
            ,'{"ID":"'||t.recipeformulation_id||'","VAL":"'||t.RecipeFormulationName||'","ACTIVE":"1"}' as recipe_ddl
            from fg_s_recipeformulation_v t
            where nvl(t.PREMIXRECIPE,0) = decode(table_type_in,'Premix Recipe',1,0)
            and rownum <= top_num_in
            union all
            select distinct t.recipeformulation_id,t.RecipeFormulationName
            ,'{"ID":"'||t.recipeformulation_id||'","displayName":"'||t.RecipeFormulationName||'"}' as recipe_link
            ,'{"ID":"'||t.recipeformulation_id||'","VAL":"'||t.RecipeFormulationName||'","ACTIVE":"1"}' as recipe_ddl
            from fg_s_recipeformulation_v t
            where t.recipeformulation_id = selected_material_id_in)
          order by RecipeFormulationName
        )
  loop
    if r.recipeformulation_id is not null and r.recipeformulation_id = selected_material_id_in /*and instr(','||selectedList_||',', ','||r.material_link||',')<=0*/ then
       if selectedList_ = empty_clob() then
          selectedList_ := fg_concat_clob(selectedList_, r.recipe_link);
       else
          selectedList_ := fg_concat_clob(selectedList_, ',' || r.recipe_link);
       end if;
    end if;

    if r.recipeformulation_id is not null /*and instr(','||materialList_||',', ','||r.material_ddl||',')<=0 */then
      if materialList_ = empty_clob() then
          materialList_ := fg_concat_clob(materialList_, r.recipe_ddl);
      else
          materialList_ := fg_concat_clob(materialList_, ',' || r.recipe_ddl);
      end if;
    end if;
  end loop;

  toReturn := '{"displayName":[' || selectedList_ || '],"htmlType":"select","isDisabled":"'||is_disabled_in||'","maxShownResults":"500","displayAsLink":"true","customFuncName":"onChangeMaterial",'||
           ' "colCalcId":"INVITEMMATERIAL_ID","dbColumnName":"invitemmaterial_id","formCode":"'||'RecipeFormulation'||'","formId":"'||selected_material_id_in||'",'||
           ' "mandatory":"true", "fullList":['|| materialList_ ||'],"autoSave":"'||is_autosave_in||'"}';
           
  return toReturn;
end if;
for r in (
        select distinct * from
          (select * from
            (select distinct t.INVITEMMATERIAL_ID,t.INVITEMMATERIALNAME
            ,'{"ID":"'||t.invitemmaterial_id||'","displayName":"'||t.INVITEMMATERIALNAME||'"}' as material_link
            ,'{"ID":"'||t.invitemmaterial_id||'","VAL":"'||t.INVITEMMATERIALNAME||'","ACTIVE":"1"}' as material_ddl --yp 08032021 -> not needed (we show it in the union all below) -> active: '||decode(ms.MATERIALSTATUSNAME,'Cancelled','0',decode(nvl(t.active,'1'),'1','1','0'))||'
            from fg_s_invitemmaterial_v t,
            fg_s_materialtype_v m,
            fg_s_materialstatus_v ms
            where instr(','||t.MATERIALTYPE_ID||',',','||m.materialtype_id||',')>0
            and ((table_type_in = 'Reactant' and lower(m.MaterialTypeName) in ('reagent','reactant','intermediate'))
                  or (table_type_in = 'Solvent' and lower(m.MaterialTypeName) in ('solvent'))
                  or (table_type_in = 'Product' and  lower(m.MaterialTypeName) in ('product','final product','intermediate'))
                  
                  --This condition section is for the materials in the composition table(recipe/ExperimentFor)--
                  or(table_type_in = 'Active Ingredient' and lower(m.MaterialTypeName) in lower('Active Ingredient') and nvl(t.COFORMULANT,'0') = '0')
                  or(table_type_in = 'Co-Formulants' and t.COFORMULANT = '1')
                  or(table_type_in = 'Chemicals' and MATERIALPROTOCOLTYPE = 'Chemical Material' and nvl(t.COFORMULANT,'0') = '0' and lower(m.MaterialTypeName) not in lower('Active Ingredient'))
                  or(table_type_in = 'Allowed Additive' and lower(m.MaterialTypeName) in lower('Wetting of WG'))
                  or(table_type_in in('Premix Material','Step (Premix) material') and MATERIALPROTOCOLTYPE = 'Premix')
                  or(table_type_in = 'Formulation' and MATERIALPROTOCOLTYPE = 'Formulation')
                  --TODO:add handle premix material&Formulation
              )
            and ',' || nvl(project_id_scope_in,t.PROJECT_ID) || ',' like '%,' || t.PROJECT_ID || ',%'
            and t.STATUS_ID = ms.materialstatus_id
            and ms.MATERIALSTATUSNAME <> 'Cancelled'
            and t.active = 1
            order by t.INVITEMMATERIALNAME)
          where rownum <= top_num_in
          union all
          select distinct t.INVITEMMATERIAL_ID,t.INVITEMMATERIALNAME
          ,'{"ID":"'||t.invitemmaterial_id||'","displayName":"'||t.INVITEMMATERIALNAME||'"}' as material_link
          ,'{"ID":"'||t.invitemmaterial_id||'","VAL":"'||t.INVITEMMATERIALNAME||'","ACTIVE":"'||decode(ms.MATERIALSTATUSNAME,'Cancelled','0',decode(nvl(t.active,'1'),'1','1','0'))||'"}' as material_ddl
          from fg_s_invitemmaterial_v t,
          fg_s_materialstatus_v ms
          where t.invitemmaterial_id = selected_material_id_in
          and t.STATUS_ID = ms.materialstatus_id(+)
          )
        order by INVITEMMATERIALNAME
      )
loop
  if r.INVITEMMATERIAL_ID is not null and r.Invitemmaterial_Id = selected_material_id_in /*and instr(','||selectedList_||',', ','||r.material_link||',')<=0*/ then
     if selectedList_ = empty_clob() then
        selectedList_ := fg_concat_clob(selectedList_, r.material_link);
     else
        selectedList_ := fg_concat_clob(selectedList_, ',' || r.material_link);
     end if;
  end if;

  if r.invitemmaterial_id is not null /*and instr(','||materialList_||',', ','||r.material_ddl||',')<=0 */then
    if materialList_ = empty_clob() then
        materialList_ := fg_concat_clob(materialList_, r.material_ddl);
    else
        materialList_ := fg_concat_clob(materialList_, ',' || r.material_ddl);
    end if;
  end if;
end loop;

/*if materialList_ is not null then
  materialList_ := substr(materialList_,2);
end if;

if selectedList_ is not null then
  selectedList_ := substr(selectedList_,2);
end if;*/

toReturn := '{"displayName":[' || selectedList_ || '],"htmlType":"select","isDisabled":"'||is_disabled_in||'","maxShownResults":"20","displayAsLink":"true","customFuncName":"onChangeMaterial",'||
         ' "colCalcId":"INVITEMMATERIAL_ID","dbColumnName":"invitemmaterial_id","formCode":"'||'InvItemMaterial'||'","formId":"'||selected_material_id_in||'",'||
         ' "mandatory":"true", "fullList":['|| materialList_ ||'],"autoSave":"'||is_autosave_in||'"}';

return toReturn;
end;
/
