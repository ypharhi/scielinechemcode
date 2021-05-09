create or replace view fg_i_tree_connection_proj_v as
select
       'Root@-1@Expand Project@Project' as Root,
       'Project@' || p.formid || '@' || replace(p.PROJECTNAME,'@',' ') || '@SUBPROJECT@' || lower(replace(p.PROJECTNAME,'@',' ')) as project,
       'SubProject@' || sp.formid || '@' || replace(sp.subProjectName,'@',' ') || '@SE@' || lower(replace(sp.subProjectName,'@',' ')) as subProject,
        p.PROJECT_ID,sp.subPROJECT_ID
from fg_s_project_all_v p,
     fg_s_subproject_all_v sp--,
    -- fg_s_subsubproject_all_v ssp
where p.project_id = sp.project_id(+)
--and sp.SUBPROJECT_ID = ssp.SUBPROJECT_ID (+);
