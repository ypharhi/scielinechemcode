CREATE OR REPLACE VIEW FG_FORMDATA_V AS
select formcode_table -- <formcode or NA;<table name or NA>;<view source data (in case if table contains clob's type fiels) or NA (in this case the data taken from table name)>;comment
from (
      SELECT distinct f.formcode || ';' || 'FG_S_' || upper(f.formcode_entity) || '_PIVOT' || ';NA' || ';NA' as formcode_table, 100 order_
      FROM FG_FORM F
      WHERE F.FORM_TYPE = 'MAINTENANCE'
      and f.group_name in ('_System Event Handler','_System Configuration Pool','_System Configuration Report'/*,'_System Unit Test Pool'*/) -- System Unit Test Pool is config in each ENV. - we can pass it easly by copy the relevant tests
      UNION ALL
      SELECT 'NA;FG_FORM;NA;NA', 1 order_  FROM DUAL
      UNION ALL
      --FG_FORMENTITY using pde (TABLE AS NA)
      /*SELECT 'NA;NA;NA;FG_FORMENTITY - NEED TO BE IMPORT BY PDE FILE (in comply is made automatically in SET_POST_SCRIPT_VERSION_DATA as part of this script)' formcode_table, 0 order_ FROM DUAL
      UNION ALL*/
      SELECT 'NA;FG_FORMENTITY;NA;NA' formcode_table, 10 order_ FROM DUAL
      UNION ALL
      SELECT 'NA;FG_RESOURCE;NA;NA;', 2 order_ FROM DUAL
      UNION ALL
      SELECT 'NA;D_NOTIFICATION_CRITERIA;D_NOTIFICATION_CRITERIA_V;NA', 3 order_ FROM DUAL
      UNION ALL
      SELECT 'NA;D_NOTIFICATION_MESSAGE;D_NOTIFICATION_MESSAGE_V;NA', 4 order_ FROM DUAL
      UNION ALL
      SELECT 'NA;P_NOTIFICATION_LISTADDRESGROUP;P_NOTIFICATION_LISTADDRESGRO_V;NA', 5 order_ FROM DUAL
      UNION ALL
      SELECT 'NA;P_NOTIFICATION_MODULE_TYPE;P_NOTIFICATION_MODULE_TYPE_V;NA', 6 order_ FROM DUAL
      UNION ALL
      SELECT 'NA;D_NOTIFICATION_ADDRESSEE;D_NOTIFICATION_ADDRESSEE_V;NA', 7 order_ FROM DUAL
      UNION ALL
      SELECT 'NA;P_NOTIFICATION_LISTSYSTEMDATA;P_NOTIFICATION_LISTSYSTEMDAT_V;NA', 8 order_ FROM DUAL
      UNION ALL
      SELECT 'NA;FG_REPORT_LIST;FG_REPORT_LIST_V;NA', 9 order_ FROM DUAL
      /*UNION ALL
      SELECT 'NA;FG_S_SYSCONFEXCELDATA_PIVOT;FG_S_SYSCONFEXCELDATA_D_V;NA', 10 order_ FROM DUAL*/


     /* UNION ALL
      SELECT 'NA;JCHEMPROPERTIES;NA;NA', 9 order_ FROM DUAL*/
      )
order by order_;
