create or replace view fg_s_subproject_dtm_v as
select t.subPROJECT_ID,t.formid,
       '{"displayName":"'||t.formid||'","saveType":"text","htmlType":"checkbox_star","dbColumnName":"favorite","customFuncName":"onChangefavoritestar","smartType":"SMARTEDIT", "title":"Favorite"}' as "Favorite_SMARTEDIT",
       t.SubProjectName as "Sub Project Name",
       s.SiteName as "Site",
       nvl(pt.subProjectTypeName,'NA') as "Sub Project Type",
       to_date(t.CREATIONDATE,'dd/MM/yyyy') as "Creation Date",
       u.USERNAME as "Created By",
       to_date(t.ESTIMATEDSTARTDATE,'dd/MM/yyyy') as "Estimated start date",
       PS.StatusName AS "Status",
       t.MCWCODE as "MCW Code",
       t.VOLUME as "Volume",
       t.WEIGHT as "Weight",
       decode(nvl(rt.job_flag,0), 1, rt.file_content_text_no_tables,
               (trim(
                replace(
                        regexp_replace(
                                       regexp_replace(
                                                       regexp_replace(
                                                                      regexp_replace(
                                                                      regexp_replace(
                                                                                      rt.file_content,
                                                                                      '<td>.*?</td>','')--fixed bug 9389
                                                                                     ,'<.*?>',' ')
                                                                      ,'\&lt;.*?\&gt;',' ')
                                                       ,'[[:space:]]',' ')
                                       ,' {2,}', ' ')
                        ,'&nbsp;','')
      ))) as "Conclusion"
from FG_S_SUBPROJECT_V t, --,(select level l from dual connect by level <= 1500),
     FG_S_subPROJECTTYPE_V pt,
     FG_S_STATUS_V PS,
     fg_s_user_v u,
     fg_s_site_v s,
     FG_RICHTEXT rt
where t.subPROJECTTYPE_ID = pt.subprojecttype_id(+)
and   t.STATUS_ID = PS.status_id(+)
and t.CREATEDBY_ID= u.USER_ID(+)
and t.SITE_ID = s.site_id(+)
and t.SPCONCLUSION = rt.file_id(+);
