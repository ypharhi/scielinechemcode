create or replace view fg_s_subsubproject_dtm_v as
select "SUBSUBPROJECT_ID",t.formid,
 '{"displayName":"'||t.formid||'","saveType":"text","htmlType":"checkbox_star","dbColumnName":"favorite","customFuncName":"onChangefavoritestar","smartType":"SMARTEDIT", "title":"Favorite"}' as "Favorite_SMARTEDIT",
 "SUBSUBPROJECTNAME" as "Sub Sub Project Name",
 s.SiteName as "Site",
 PS.StatusName aS "Status",
 to_date(t.CREATIONDATE,'dd/MM/yyyy') as "Creation Date",
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
from FG_S_SUBSUBPROJECT_V t,
     FG_S_STATUS_V PS,
     fg_s_site_v s,
     fg_richtext rt
where   t.STATUS_ID = PS.status_id(+)
and t.site_id = s.site_id(+)
and t.SSPCONCLUSION = rt.file_id(+);
