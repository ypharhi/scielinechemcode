create or replace view fg_form_id_desc as
select t.formid, TO_CLOB(T.DESCRIPTION) AS FORMDESC from FG_S_EXPERIMENT_v t UNION ALL
select t.FORMID, TO_CLOB(t.ProjectName) from FG_S_PROJECT_v t UNION ALL
select t.FORMID, TO_CLOB(t.subProjectName) from FG_S_SUBPROJECT_v t UNION ALL
select t.FORMID, TO_CLOB(t.subsubProjectName) from FG_S_SUBSUBPROJECT_v t UNION ALL
select t.formid, TO_CLOB(T.SHORTDESCRIPTION) from FG_S_STEP_v t  UNION ALL
select t.formid, TO_CLOB('NA') from FG_S_ACTION_v t  UNION ALL
select t.formid, r.file_content  from FG_S_SELFTEST_v t, fg_richtext r where t.DESCRIPTION = r.file_id  UNION ALL
select t.formid, r.file_content from FG_S_WORKUP_v t, fg_richtext r where t.COMMENTS = r.file_id;
