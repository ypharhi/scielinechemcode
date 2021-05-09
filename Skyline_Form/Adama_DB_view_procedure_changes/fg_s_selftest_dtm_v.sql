create or replace view fg_s_selftest_dtm_v as
select "SELFTEST_ID",t.EXPERIMENT_ID,
       '{"displayName":"'||t.formid||'","saveType":"text","htmlType":"checkbox_star","dbColumnName":"favorite","customFuncName":"onChangefavoritestar","smartType":"SMARTEDIT", "title":"Favorite"}' as "Favorite_SMARTEDIT",
       t.SELFTESTTYPENAME as "Test Type",
       trim(
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
                ) as "Description",
       t.SELFTESTSTATUSNAME as "Status Name",
       to_date(t.CREATIONDATE,'dd,MM,yyyy') as "Creation Date",
       to_date(t.STARTDATE,'dd,MM,yyyy') as "Start Date",
       e.FORMNUMBERID as "Experiment Number",
       decode(nvl(t.USEASDEFAULTDATA,'0'),'0','NO','YES') as "Default Self Test"
      from FG_S_SELFTEST_ALL_V t,
           fg_s_experiment_v e,
           fg_richtext f
      --, Fg_s_Selftesttype_All_v stt
      where /*t.TYPE_ID = stt.SELFTESTTYPE_ID and*/ t.TEMPLATEFLAG is null
      and   t.experiment_id = e.experiment_id
      and   t.DESCRIPTION = f.file_id(+)
      order by t.STARTDATE;
