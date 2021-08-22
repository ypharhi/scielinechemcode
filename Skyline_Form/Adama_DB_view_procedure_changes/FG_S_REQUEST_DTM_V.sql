CREATE OR REPLACE VIEW FG_S_REQUEST_DTM_V AS
select distinct t."REQUEST_ID",t."REQUEST_ID" as formid
,t.REQUEST_ID as "_SMARTSELECT"
,'{"displayName":"'||t."REQUEST_ID"||'","saveType":"text","htmlType":"checkbox_star","dbColumnName":"favorite","customFuncName":"onChangefavoritestar","smartType":"SMARTEDIT", "title":"Favorite"}' as "Favorite_SMARTEDIT"
,t.PROJECTNAME as "Project"
,t.SUBPROJECTNAME as "Sub Project"
--,listagg(t.EXPERIMENTTYPENAME,';' ) within group ( order by t.EXPERIMENTTYPENAME) over (partition by t.OPT_PARENTID) as "Operation Type"
,t."Operation Type"
--,t.sample_list as "Sample number_SMARTLINK"
,'[' || listagg( '{"displayName":"' || sd.SAMPLENAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Sample' || '"  ,"formId":"' ||  sd.SAMPLEID || '","tab":"' || '' || '", "smartType":"SMARTLINK", "title":"Sample number"}' , ', ') within group (order by  sd.parentid) OVER (partition by t."REQUEST_ID") || ']' as "Sample number_SMARTLINK"
,listagg( smpl.SAMPLEDESC,','||chr(10)) within group (order by  sd.parentid) OVER (partition by t."REQUEST_ID") as "Samples Description"
--,to_date(t.CREATIONDATE,'dd/MM/yyyy') as "Creation Date"
,t.creation_date as "Creation Date_SMARTTIME"
,t.REQUESTTYPENAME as "Request Type"
,t.USERNAME as "Sender"
,t.REQUESTSTATUSNAME  as "Status"
,t.REQUESTNAME as "Request #"
,t.DESTLABORATORYNAME as "Destination Lab"
,decode(t.USEASDEFAULTDATA,'1','Yes','No') as "Default Request"
,t.COMMENTS as "Comments"
,'{"displayName":"' || s.formidname || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || s.formcode || '"  ,"formId":"' || s.id || '","tab":"' || 'Samples' || '","smartType":"SMARTLINK", "title":"Source"}' as "Source_SMARTLINK"
 from
 (select distinct t."REQUEST_ID",t.ORIGINEXPERIMENTID
  ,p.PROJECTNAME
  ,sp.SUBPROJECTNAME
  --,op.EXPERIMENTTYPENAME
  ,op.PARENTID as OPT_PARENTID
  ,listagg(op.EXPERIMENTTYPENAME,';' ) within group ( order by op.EXPERIMENTTYPENAME) over (partition by op.PARENTID) as "Operation Type"
  --, '[' || listagg( '{"displayName":"' || sd.SAMPLENAME || '" ,"icon":"' || '' || '" ,"fileId":"' || '' || '","formCode":"' || 'Sample' || '"  ,"formId":"' ||  sd.SAMPLEID || '","tab":"' || '' || '" }' , ', ') within group (order by  sd.parentid) OVER (partition by t."REQUEST_ID") || ']' SAMPLE_LIST
  --,to_date(t.CREATIONDATE,'dd/MM/yyyy') as "Creation Date"
  ,t.creation_date
  ,t.REQUESTTYPENAME
  ,u.USERNAME
  ,t.REQUESTSTATUSNAME
  ,t.REQUESTNAME
  ,t.DESTLABORATORYNAME
  ,t.USEASDEFAULTDATA
  --,fg_get_RichText_display (t.COMMENTS) as COMMENTS  -->
  ,trim(
                replace(
                        regexp_replace(
                                       regexp_replace(
                                                       regexp_replace(
                                                                      regexp_replace(
                                                                                      DBMS_LOB.substr(f.file_content, 4000),
                                                                                     '<.*?>','')
                                                                      ,'\&lt;.*?\&gt;',' ')
                                                       ,'[[:space:]]',' ')
                                       ,' {2,}', ' ')
                        ,'&nbsp;','')
                ) as "COMMENTS"
 ,t.PARENTID
  from FG_S_REQUEST_ALL_V t,fg_s_user_all_v u,fg_s_project_all_v p, fg_s_subproject_all_v sp
     -- ,fg_s_sampledataref_all_v sd
      ,fg_s_operationtype_all_v op
      ,fg_richtext f
      where t.ISINUSE ='1'
      and t.CREATOR_ID = u.USER_ID(+)
      and t.PROJECT_ID = p.PROJECT_ID(+)
      and t.SUBPROJECT_ID = sp.SUBPROJECT_ID(+)
      /*and t.REQUEST_ID = sd.parentid(+)
      and sd.SESSIONID(+) is null
      and sd.active(+)=1*/
      and op.PARENTID(+) = t.REQUEST_ID
      and op.SESSIONID(+) is null
      and   t.COMMENTS = f.file_id(+)) t
 ,fg_s_sampledataref_all_v sd,fg_sequence s, fg_s_sample_v smpl
      where t.REQUEST_ID = sd.parentid(+)
     and s.id=nvl(t.ORIGINEXPERIMENTID,t.parentid)
      and sd.SESSIONID(+) is null
      and sd.active(+)=1
      and sd.SAMPLEID = smpl.sample_id(+)
 order by "Creation Date_SMARTTIME" desc;
