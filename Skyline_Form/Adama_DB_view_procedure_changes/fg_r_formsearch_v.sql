create or replace view fg_r_formsearch_v as
select
      ------ debug -------
      formid,
      path_id,
      /*m_form as m_formcode,"ENTITYIMPCODE",
      m_element_name,
      elementclass as m_elementclass,
      m_form_type,*/
      s_element_id,
      search_value,
      search_display_value,
      search_file_value,
      elementclass,
      PROJECT_ID,
      SUBPROJECT_ID,
      SUBSUBPROJECT_ID,
      is_idlist,
      file_id,
      ----- display ------
      --nvl(path_id,formid) as "#",--tmp only for test
      p_project_name as "Project",
      m_form as "Entity",
      nvl(m_form_type,s_element_id_tabletype) as "Entity Type",
      m_element_name as "Name",
      decode(file_id,null,display_value,s_element_id_name) as "Value",
      nvl2(file_id,'{"displayName":"' || s_element_id_name || '" ,"icon":"" ,"fileId":"' || file_id || '","formCode":"' || 'Document' || '"  ,"formId":"' || s_element_id || '","tab":"' || '' || '" }','') as "File_SMARTFILE",
      --file_id as "File1",
      --decode(ishidden,1,decode(issearchidholder,0,'true',''),'') as "to delete",
      "path_SMARTPATH" as "Path_SMARTPATH"
from
(
 select --names
        p.projectname as p_project_name,
        m.formcodeentitylabel as m_form,
        m.formcodetyplabel as m_form_type,
        m.displaylabel as m_element_name,
        s_element.formidname as s_element_id_name,
        s_element.formtabletype as s_element_id_tabletype,
        --search values
        t.displayvalue as display_value,
        t.entityimpvalue as search_value, -- search on this
        t.displayvalue as search_display_value,
        f.file_content as search_file_value,
        t.is_idlist,
        --path
        s_path.formPath as "path_SMARTPATH",
        s_path.search_match_id1 as PROJECT_ID,
        s_path.search_match_id2 as SUBPROJECT_ID,
        s_path.search_match_id3 AS SUBSUBPROJECT_ID,
        --id's
        f.file_id,
        s_element.id as s_element_id,
        --more info...
        m.elementclass,
        t.formid,
        t.path_id,
        m.issearchidholder,
        m.ishidden,
        m.entityimpcode
 from  fg_formlastsavevalue_inf_v t, -- main key value table
      FG_FORMELEMENTINFOATMETA_MV m, -- form entity data
      fg_sequence s_path, -- id data for path
      fg_sequence s_element, -- id data for formcode ,formid value, table type
      fg_s_project_pivot p,
      fg_files_src/*fg_files_src*//*yaron7(select* from  fg_files_src t where 1=1 and contains(t.file_content,'%123%',1)>0)*/ f --files data - need to run initSearchData task to have current data
 where 1=1
 and t.formid = s_element.id
 and upper(m.formcode) = upper(s_element.formcode)
 and upper(m.entityimpcode) = upper(t.entityimpcode)
 and nvl(t.path_id,t.formid) = to_char(s_path.id) -- FG_FORMLASTSAVEVALUE_inf path_id holds the formid of the parent element in popup forms.
 and s_path.SEARCH_MATCH_ID1 = p.formid
 and decode(nvl(t.is_file,0),1,to_char(t.entityimpvalue),null) = to_char(f.file_id(+))
 and f.parentid(+) = t.FORMID
 and m.formcode<>'WebixAuditTrail'--need to be discussed
 and m.issearchelement=1
 and nvl(t.ACTIVE,1)=1
 --and t.change_date > sysdate - 1/24 -- FOR TEST ONLY
)
where 1=1
--and contains(search_file_value,'%Agan%',1) + instr(search_display_value,'Agan') > 0 -- FOR TEST ONLY
and 1=1;
