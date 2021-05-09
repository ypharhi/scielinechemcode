create or replace view fg_r_src_hst_v as
select c.displaylabel || ' [' || c.entityimpcode || ']' as  entityimpcode, --first is hidden use for filter
       c.elementclass,
      c.FORMCODE as FORMCODE,
      h.formcode_entity as "ENTITY",
      h.formid,
      NULL as "POPUP FORMID",
      NULL as "POPUP FORMCODE",
      c.displaylabel || ' [' || c.entityimpcode || ']' as "Field Label [code]",
      --h.entityimpcode AS "Field Code",
      S_id.FORMIDNAME as "Current Value(For IDs)",
      decode(c.elementclass,'ElementRichTextEditorImp',fg_get_richtext_display_clob(h.entityimpvalue),h.displayvalue) as "Display Value(On Save Form)",
      h.entityimpvalue as "Feild Value",
      u.username as "Change by",
      h.change_date as "Change Date_SMARTTIME"
 from FG_FORMLASTSAVEVALUE_HST h,
      FG_FORMELEMENTINFOATMETA_MV c,
      fg_sequence s,
      fg_sequence s_id,
      fg_s_user_pivot u
 where h.formid = to_char(s.id)
 and  decode(nvl(h.is_idlist,0),0,NULL,to_char(h.entityimpvalue)) = to_char(s_id.id(+))
 and upper(s.formcode) = upper(c.formcode)
 and upper(c.entityimpcode(+)) = upper(h.entityimpcode)
 and h.formcode_entity<>'WebixAuditTrail'--need to be discussed
 and nvl(h.ACTIVE,1)=1
 and u.formid(+) = h.change_by
 and h.is_idlist<>'2'
 union all
 select c.displaylabel || ' [' || c.entityimpcode || ']' as  entityimpcode, --first is hidden use for filter
       c.elementclass,
      s_parent.formcode as FORMCODE,
      f.formcode_entity as "ENTITY",
      h.path_id as formid,
      h.formid as "POPUP FORMID",
       c.FORMCODE as "POPUP FORMCODE",
      c.displaylabel || ' [' || c.entityimpcode || ']' as "Field Label [code]",
      --h.entityimpcode AS "Field Code",
      S_id.FORMIDNAME as "Current Value(For IDs)",
      decode(c.elementclass,'ElementRichTextEditorImp',fg_get_richtext_display_clob(h.entityimpvalue),h.displayvalue) as "Display Value(On Save Form)",
      h.entityimpvalue as "Feild Value",
      u.username as "Change by",
      h.change_date as "Change Date_SMARTTIME"
 from FG_FORMLASTSAVEVALUE_HST h,
      FG_FORMELEMENTINFOATMETA_MV c,
      fg_sequence s,
      fg_sequence s_id,
      fg_sequence s_parent,
      fg_s_user_pivot u,
      fg_form f
 where h.formid = to_char(s.id)
 and  decode(nvl(h.is_idlist,0),0,NULL,to_char(h.entityimpvalue)) = to_char(s_id.id(+))
 and upper(s.formcode) = upper(c.formcode)
 and upper(c.entityimpcode(+)) = upper(h.entityimpcode)
 and h.formcode_entity<>'WebixAuditTrail'--need to be discussed
 and nvl(h.ACTIVE,1)=1
 and u.formid(+) = h.change_by
 and s_parent.id = h.path_id
 and f.formcode = s_parent.formcode
 and h.is_idlist<>'2';
