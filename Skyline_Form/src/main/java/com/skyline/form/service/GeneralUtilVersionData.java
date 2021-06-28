package com.skyline.form.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.skyline.form.dal.GeneralDao;

@Service
public class GeneralUtilVersionData {

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private GeneralUtilLogger generalUtilLogger;

	@Value("${scriptPath:C:/logs/DB_data_script.sql}")
	private String scriptPath;

	@Value("${scriptPathMaterialized:na}") // C:/logs/DB_data_script_materialized.sql
	private String scriptPathMaterialized;

	@Value("${jdbc.username}")
	private String dbUsername;
	
	@Value("${diffFormCodeEntityDB:SKYLINE_FORM_SERVER}")
	private String diffFormCodeEntityDB;
	
	private final String DB_TIME_FORMAT = "yyyy-mm-dd hh24:mi:ss";

	public void makeVersionData() {
		try {
			completeNotificationMetaData("NOTIF_SERVICE_TEST");
			createInsertScript(null);
		} catch (Exception e) {
		}
	}
	
	public void makeVersionDataDiff() {
		if(diffFormCodeEntityDB != null && !diffFormCodeEntityDB.isEmpty()) {
			createInsertScript(diffFormCodeEntityDB);
		}
	}

	private String createIndex() {
		return "exec set_constraints_indx;\n";

	}

	private String makeSystemAndAdminUsers() { 
		return "-- script created from DB: " + dbUsername + " at " + generalUtil.getCurrentDateByFromat("dd/MM/yyyy HH:mm:ss") + "\n exec FORM_TOOL_COPY_ADAMA_DATA.updateVerionData;\n set define off;\n";
	}

	/**
	 * taken a list of tables from FG_FORMDATA_V and add it to insert sql script
	 */
	private void createInsertScript(String diffFormCodeEntityDB) {
		// TODO Auto-generated method stub
		List<String> formCodeTableList = generalDao
				.getListOfStringBySql("select t.formcode_table from FG_FORMDATA_V t");
		String tableName_ = "";
		String formCode_ = "";
		String comment_ = "";
		String wherePart = "";
		String viewName_ = "";
		Map<String, String> scriptMap = null;
		StringBuilder sbScript = new StringBuilder();

		sbScript.append(makeSystemAndAdminUsers());
		sbScript.append(backupScript());
		sbScript.append(triggerScript(true));
		for (String codeTable : formCodeTableList) {

			// init
			formCode_ = codeTable.split(";")[0];
			tableName_ = codeTable.split(";")[1];
			viewName_ = codeTable.split(";")[2];
			comment_ = codeTable.split(";")[3];
			wherePart = " where 1=1 ";
			scriptMap = new HashMap<String, String>();

			if (!comment_.equals("NA")) {
				sbScript.append("prompt " + comment_ + " \n");
			}

			if (!tableName_.equals("NA")) {
				if (!formCode_.equals("NA")) {
					wherePart += " and formcode = '" + formCode_ + "'";
				}
				List<Map<String, Object>> dataAll;

				if (!viewName_.equals("NA")) { // kd 17042018 use viewName_ instead tableName_ for get data. This is made for get data from CLOB fields 
					dataAll = generalDao.getListOfMapsBySql("select * from " + viewName_ + wherePart);
				} else {
					if(diffFormCodeEntityDB != null && tableName_.equalsIgnoreCase("fg_formentity")) {
						String deleteDiffIds =  getFormEntityDiffInsertId(diffFormCodeEntityDB);
						if(deleteDiffIds == null || deleteDiffIds.isEmpty()) {
							deleteDiffIds = "-1";
						}
						wherePart += " and id in (" + deleteDiffIds  + ")";
					}
					dataAll = generalDao.getListOfMapsBySql("select * from " + tableName_ + wherePart);
				}

				if (tableName_.equals("FG_REPORT_LIST")) {
					sbScript.append(deleteRowsRepList(tableName_, formCode_));
				} else if (tableName_.startsWith("D_NOTIFICATION") && !viewName_.equals("NA")) {
					sbScript.append(deleteRowsNotification(tableName_, viewName_));
				} else {
					String deleteDiffIds = null;
					if(diffFormCodeEntityDB != null && tableName_.equalsIgnoreCase("fg_formentity")) {
						deleteDiffIds =  getFormEntityDiffDeleteId(diffFormCodeEntityDB);
						if(deleteDiffIds == null || deleteDiffIds.isEmpty()) {
							deleteDiffIds = "-1";
						}
					}
					sbScript.append(deleteRows(tableName_, formCode_, deleteDiffIds));
				}
				for (Map<String, Object> dataRow : dataAll) {

					for (Map.Entry<String, Object> entry : dataRow.entrySet()) {
						String col_ = entry.getKey();
						String val_ = String.valueOf(entry.getValue());
						scriptMap.put(col_, getValByFormCol(formCode_, col_, val_));
					}
					sbScript.append(getScriptByMap(tableName_, scriptMap));
				}
			}

		}
		sbScript.append(triggerScript(false));
		sbScript.append(createIndex());
		sbScript.append(createNotificationCompareTable());
		sbScript.append("\ncommit;\n");
		sbScript.append(
				"prompt RUN FORM_TOOL.tool_check_data from " + dbUsername + " DB [The output include difference in the version data between " + dbUsername + " and target DB (use as parameter)] \n");

		materializedViewsCreate();

		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream((diffFormCodeEntityDB == null)?scriptPath:scriptPath.replace(".sql","_diff_from_" + diffFormCodeEntityDB + "_DB_(optional).sql"), false));
			writer.println(sbScript.toString());
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String getFormEntityDiffDeleteId(String diffFormCodeEntityDB) {
		String sql = "select id from fg_formentity where id in (\r\n" + 
				"  with minus_ as (\r\n" + 
				"   select formcode, numberoforder, entitytype, entityimpcode, entityimpclass, entityimpinit, comments, fs, fs_gap from fg_formentity \r\n" + 
				"   minus  \r\n" + 
				"   select formcode, numberoforder, entitytype, entityimpcode, entityimpclass, entityimpinit, comments, fs, fs_gap from " + diffFormCodeEntityDB + ".fg_formentity\r\n" + 
				"  ) \r\n" + 
				"  select t.id\r\n" + 
				"  from fg_formentity t,\r\n" + 
				"       minus_\r\n" + 
				"  where minus_.formcode = t.formcode\r\n" + 
				"  and   minus_.entityimpcode = t.entityimpcode\r\n" + 
				")" + 
				"";
		
		return generalDao.getCSVBySql(sql, false);
	}

	private String getFormEntityDiffInsertId(String diffFormCodeEntityDB) {
		String sql = "select id from " + diffFormCodeEntityDB + ".fg_formentity where id in (\r\n" + 
				"  with minus_ as (\r\n" + 
				"   select formcode, numberoforder, entitytype, entityimpcode, entityimpclass, entityimpinit, comments, fs, fs_gap from " + diffFormCodeEntityDB + ".fg_formentity \r\n" + 
				"   minus  \r\n" + 
				"   select formcode, numberoforder, entitytype, entityimpcode, entityimpclass, entityimpinit, comments, fs, fs_gap from fg_formentity\r\n" + 
				"  ) \r\n" + 
				"  select t.id\r\n" + 
				"  from " + diffFormCodeEntityDB + ".fg_formentity t,\r\n" + 
				"       minus_\r\n" + 
				"  where minus_.formcode = t.formcode\r\n" + 
				"  and   minus_.entityimpcode = t.entityimpcode\r\n" + 
				")";
		return generalDao.getCSVBySql(sql, false);
	}

	private void materializedViewsCreate() {
		if(scriptPathMaterialized != null && !scriptPathMaterialized.equalsIgnoreCase("na")) {
			
			StringBuilder sbScript = new StringBuilder();
			
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("db_name_in", dbUsername);
			
			generalDao.callPackageFunction("FORM_TOOL", "FG_output_materialized_view_v", parameters);
			
			// 		String sql = String.format("select t.mview_name || ',' || t.query as definition from sys.all_mviews t where lower(t.owner) = '%1$s' and t.mview_name like 'DUMMY_MV%%' order by t.mview_name;", dbUsername);
			String sql = "select view_name || ';' || view_code as definition from FG_R_MATERIALIZED_VIEW";
			
			List<String> listOfMaterializedViews = generalDao.getListOfStringBySql(sql);
			StringBuilder result = new StringBuilder();
			Iterator<String> updateNeedIterator;
			updateNeedIterator = listOfMaterializedViews.iterator();
			String[] arrMaterialViewRow;
			// loop on sys.all_mviews. Checking for data was created. 
			// If not then insert, if type was changed, then update, if there obsolete data, then remove them
			while (updateNeedIterator.hasNext()) {
				arrMaterialViewRow = updateNeedIterator.next().split(";");
				
				result.append("DROP MATERIALIZED VIEW " + arrMaterialViewRow[0] + ";\r\n ");
				
				result.append("CREATE MATERIALIZED VIEW " + arrMaterialViewRow[0] + " REFRESH FORCE ON DEMAND AS "
						+ arrMaterialViewRow[1] + "; \r\n");
			}
			sbScript.append(result);
			
			try {
				PrintWriter writer = new PrintWriter(new FileOutputStream(scriptPathMaterialized, false));
				writer.println(sbScript.toString());
				writer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private String createNotificationCompareTable() {
		String scriptDelete = "delete from fg_notification_compare";
		String scriptInsert = "insert into fg_notification_compare\r\n"
				+ "select to_char(\"D_NOTIFICATION_MESSAGE_ID\") as D_NOTIFICATION_MESSAGE_ID,\r\n"
				+ "to_char(\"NOTIFICATION_MODULE_ID\") as NOTIFICATION_MODULE_ID,\r\n"
				+ "to_char(\"MESSAGE_TYPE_ID\") as MESSAGE_TYPE_ID,\r\n"
				+ "to_char(\"DESCRIPTION\") as DESCRIPTION,\r\n"
				+ "to_char(\"TRIGGER_TYPE_ID\") as TRIGGER_TYPE_ID,\r\n"
				+ "to_char(\"ON_SAVE_FORMCODE\") as ON_SAVE_FORMCODE,\r\n"
				+ "to_char(\"EMAIL_SUBJECT\")as EMAIL_SUBJECT,\r\n" + "to_char(\"EMAIL_BODY\") as EMAIL_BODY,\r\n"
				+ "to_char(\"SCHEDULER_INTERVAL\") as SCHEDULER_INTERVAL,\r\n"
				+ "to_char(\"WHERE_STATEMENT\") as WHERE_STATEMENT,\r\n" + "to_char(\"RESEND\") as RESEND,\r\n"
				+ "to_char(\"P_NOTIFICATION_MODULE_TYPE_ID\") as P_NOTIFICATION_MODULE_TYPE_ID,\r\n"
				+ "to_char(\"MODULE_NAME\") as MODULE_NAME,\r\n"
				+ "to_char(\"SELECT_STATEMENT\") as SELECT_STATEMENT,\r\n"
				+ "to_char(\"MSGUNIQUEIDNAME\") as MSGUNIQUEIDNAME,\r\n" + "to_char(\"ORDER_BY\") as ORDER_BY,\r\n"
				+ "to_char(\"ADDRESSEE_TYPE_ID\") as ADDRESSEE_TYPE_ID,\r\n"
				+ "to_char(\"SEND_TYPE\") as SEND_TYPE,\r\n"
				+ "to_char(\"ADDRESSEE_USER_ID\") as ADDRESSEE_USER_ID,\r\n"
				+ "to_char(\"PARAMS_FIELD_NAMES\") as PARAMS_FIELD_NAMES,\r\n"
				+ "to_char(\"ADDRESSEE_GROUP_SELECT\") as ADDRESSEE_GROUP_SELECT,\r\n"
				+ "to_char(\"ADD_ATTACHMENTS\") as ADD_ATTACHMENTS,\r\n"
				+ "to_char(\"ATTACHED_REPORT_NAME\") as ATTACHED_REPORT_NAME,\r\n"
				+ "to_char(\"ATTACHED_REPORT_TYPE\") as ATTACHED_REPORT_TYPE,\r\n"
				+ "to_char(\"ISACTIVE\") as ISACTIVE \r\n" + "from fg_n_config_v";
		generalDao.updateSingleString(scriptDelete);
		generalDao.updateSingleString(scriptInsert);
		return scriptDelete + ";\n" + scriptInsert + ";";
	}

	private String triggerScript(boolean isDisabled) {
		// TODO Auto-generated method stub
		String toReturn = "-----" + (isDisabled ? "disable" : "enable") + " triggers\n"
				+ " ALTER TRIGGER FG_FORM_INSERT_TRIG " + (isDisabled ? "disable" : "enable") + ";\n"
				+ " ALTER TRIGGER FG_FORM_UPDATE_TRIG " + (isDisabled ? "disable" : "enable") + ";\n"
				+ " ALTER TRIGGER FG_FORMENTITY_UPDATE_TRIG " + (isDisabled ? "disable" : "enable") + ";\n"
				+ " ALTER TRIGGER FG_RESOURCE_INSERT_TRIG " + (isDisabled ? "disable" : "enable") + ";\n"
				+ " ALTER TRIGGER FG_FORMENTITY_INSERT_TRIG " + (isDisabled ? "disable" : "enable") + ";\n"
				+ " ALTER TRIGGER D_NOTIFICATION_CRITERIA_I_TRIG " + (isDisabled ? "disable" : "enable") + ";\n"
				+ " ALTER TRIGGER D_NOTIFICATION_CRITERIA_U_TRIG " + (isDisabled ? "disable" : "enable") + ";\n"
				+ " ALTER TRIGGER D_NOTIF_ADDRESSEE_I_TRIG " + (isDisabled ? "disable" : "enable") + ";\n"
				+ " ALTER TRIGGER D_NOTIF_ADDRESSEE_U_TRIG " + (isDisabled ? "disable" : "enable") + ";\n"
				+ " ALTER TRIGGER D_NOTIF_MESSAGE_IN_TRIG " + (isDisabled ? "disable" : "enable") + ";\n"
				+ " ALTER TRIGGER D_NOTIF_MESSAGE_UPDATE_TRIG " + (isDisabled ? "disable" : "enable") + ";\n"
				+ " ALTER TRIGGER P_NOTIF_LISTADDRESGROUP_TRIG " + (isDisabled ? "disable" : "enable") + ";\n"
				+ " ALTER TRIGGER P_NOTIF_MODULE_TYPE_TRIG " + (isDisabled ? "disable" : "enable") + ";\n"
				+ " ALTER TRIGGER FG_REPORT_LIST_INSERT_TRIG " + (isDisabled ? "disable" : "enable") + ";\n";
		return toReturn;

	}

	private String backupScript() {
		String toReturn = " DELETE FROM FG_REPORT_LIST_BU2;\n"
				+ "INSERT INTO FG_REPORT_LIST_BU2 SELECT * FROM FG_REPORT_LIST;\n";
		return toReturn;
	}

	private String deleteRows(String tableName_, String formCode_, String deleteDiffIds) {
		String toReturn = "";
		if (formCode_.equals("NA")) {
			toReturn = "-----\n--" + tableName_
					+ "[use command window and enter popup parameters && if exists]\n-----\ndelete from " + tableName_ + (deleteDiffIds == null?"":" where id in (" + deleteDiffIds + ") ")
					+ ";\n";
		} else {
			toReturn = "-----\n--" + tableName_ + "\n-----\ndelete from " + tableName_ + " where formcode = '"
					+ formCode_ + (deleteDiffIds == null?"":" and id in (" + deleteDiffIds + ") ") + "';\n";
		}
		return toReturn;

	}

	private String deleteRowsRepList(String tableName_, String formCode_) {
		String toReturn = "";
		if (formCode_.equals("NA")) {
			toReturn = "-----\n--" + tableName_
					+ "[use command window and enter popup parameters && if exists]\n-----\ndelete from " + tableName_
					+ " ;\n" + "--where SYSTEM_ROW = 1;\n";
		}
		return toReturn;

	}

	private String deleteRowsNotification(String tableName_, String viewName_) {
		String toReturn = "";
		//deleting the records that will be inserted only, ie the ones that copy_to_production field is 1 for them, as it is written in the views
		if (tableName_.equals("D_NOTIFICATION_CRITERIA")) {
			toReturn = "-----\n--" + tableName_
					+ "[use command window and enter popup parameters && if exists]\n-----\ndelete from " + tableName_
					+ " \n" + " where NOTIFICATION_CRITERIA_ID in \n"
					//+ "(select NOTIFICATION_CRITERIA_ID from "+viewName_+");\n";
					+ "(" + generalDao.getCSVBySql("select NOTIFICATION_CRITERIA_ID from " + viewName_, true) + ");\n";
		} else if (tableName_.equals("D_NOTIFICATION_MESSAGE")) {
			toReturn = "-----\n--" + tableName_
					+ "[use command window and enter popup parameters && if exists]\n-----\ndelete from " + tableName_
					+ " \n" + " where D_NOTIFICATION_MESSAGE_ID in \n"
					//+ "(select D_NOTIFICATION_MESSAGE_ID from "+viewName_+");\n";
					+ "(" + generalDao.getCSVBySql("select D_NOTIFICATION_MESSAGE_ID from " + viewName_, true) + ");\n";
		} else if (tableName_.equals("D_NOTIFICATION_ADDRESSEE")) {
			toReturn = "-----\n--" + tableName_
					+ "[use command window and enter popup parameters && if exists]\n-----\ndelete from " + tableName_
					+ " \n" + " where NOTIFICATION_ADDRESSEE_ID in \n"
					//+ "(select NOTIFICATION_ADDRESSEE_ID from "+viewName_+");\n";
					+ "(" + generalDao.getCSVBySql("select NOTIFICATION_ADDRESSEE_ID from " + viewName_, true) + ");\n";
		}
		return toReturn;
	}

	private Object getScriptByMap(String tableName, Map<String, String> scriptMap) {
		String insertLine = "insert into " + tableName + "(";
		String valueLine = "values (";

		for (Map.Entry<String, String> entry : scriptMap.entrySet()) {
			insertLine += entry.getKey() + ",";
			valueLine += entry.getValue() + ",";
		}
		return generalUtil.replaceLast(insertLine, ",", ")") + "\n" + generalUtil.replaceLast(valueLine, ",", ")")
				+ ";\n";
	}

	private String getValByFormCol(String formCode_, String col_, String val_) {
		String toReturn = "''";
		if (generalUtil.getNull(val_).equals("")) {
			return "null";
		}
		if (formCode_.equals("NA")) {
			switch (col_) {
				case "CHANGE_DATE": {
					toReturn = "to_date('" + val_.replace(".0", "") + "','" + DB_TIME_FORMAT + "')";
				}
					break;
				case "UPDATED_ON": {
					toReturn = "sysdate";
				}
					break;
				//kd 17042018 added 2 cases for fields of DATE type
				case "TIME_STAMP": {
					toReturn = "to_date('" + val_.replace(".0", "") + "','" + DB_TIME_FORMAT + "')";
				}
					break;
				case "LAST_RUN": {
					toReturn = "to_date('" + val_.replace(".0", "") + "','" + DB_TIME_FORMAT + "')";
				}
					break;
				case "TIMESTAMP": {
					toReturn = "to_date('" + val_.replace(".0", "") + "','" + DB_TIME_FORMAT + "')";
				}
					break;
				//kd end 17042018	
				default:
					toReturn = "'" + val_.replace("'", "''").replace("\n", "' || chr(10) || '") + "'";
			}
		} else {
			switch (col_) {
				case "FORMID": {
					toReturn = "FG_GET_STRUCT_FORM_ID('" + formCode_ + "')";
				}
					break;
				case "TIMESTAMP": {
					toReturn = "to_date('" + val_.replace(".0", "") + "','" + DB_TIME_FORMAT + "')";
				}
					break;
				case "CREATION_DATE": {
					toReturn = "to_date('" + val_.replace(".0", "") + "','" + DB_TIME_FORMAT + "')";
				}
					break;
				case "CHANGE_BY": {
					toReturn = "(select formid from fg_s_user_pivot where username ='system')";
				}
					break;
				case "CREATED_BY": {
					toReturn = "(select formid from fg_s_user_pivot where username ='system')";
				}
					break;
				case "EXCELDATA": { // clob file ref - need to be ignored in form_tool.tool_check_data DB check data procedure
					if(formCode_.equalsIgnoreCase("SysConfExcelData")) {
						String excelData = generalDao.selectSingleString("select file_content from fg_clob_files where file_id = '" + val_ + "'");
						toReturn = "FG_CLOB_FILES_CONFEXCEL_INSERT(" + generalUtil.handleClob(excelData.replace("'", "''")) + ")" ;
					}
				}
					break;
				case "SQLTEXT": { // clob file ref - need to be ignored in form_tool.tool_check_data DB check data procedure
					if(formCode_.equalsIgnoreCase("DynamicReportSql")) {
						String sqltext = generalDao.selectSingleString("select file_content from fg_clob_files where file_id = '" + val_ + "'");
						toReturn = "FG_CLOB_FILES_CONFEXCEL_INSERT(" + generalUtil.handleClob(sqltext.replace("'", "''").replace("\n", "' || chr(10) || '")) + ")" ;
					}
				}
					break;
				default:
					toReturn = "'" + val_.replace("'", "''").replace("\n", "' || chr(10) || '") + "'";
			}
		}
		return toReturn;
	}

	public void completeNotificationMetaData(String table_name) {
		List<String> listConfigTableOriginal = new ArrayList<>();
		Map<String, String> metaDataMap = new HashMap<String, String>();
		int updateInsert = 0; //0 - insert, 1 - update, 2 - not update, not insert
		String notificationTableName = "P_NOTIFICATION_LISTSYSTEMDATA"; //table for update
		String sqlSelect = "select distinct t.P_Notification_Module_Type_ID || ',' || t.msgUniqueIDName || ',' || t.Select_Statement from fg_n_config_v t";
		String sqlInsert = "INSERT INTO %1$s (MODULE_ID,DATA_NAME, DATATYPE, ORIGINAL_FIELD_NAME, NOTIFICATION_LISTSYSTEMDATA_ID, ISUPDATED) VALUES (%2$d,'%3$s','%4$s','%3$s',%5$d, %6$d)";
		String sqlUpdateDataType = "UPDATE %1$s SET DATATYPE = '%2$s', ISUPDATED = 1 WHERE NOTIFICATION_LISTSYSTEMDATA_ID = %3$d";
		String sqlUpdateIsUpdated = "UPDATE %1$s SET ISUPDATED = %2$d WHERE NOTIFICATION_LISTSYSTEMDATA_ID = %3$d";
//		String sqlDelete = "DELETE FROM %1$s WHERE 	ISUPDATED = 0";
		String[] strOfNotifModule;
		String nameIdForIgnore, sqlValue;
		String key, value, type, strInsertUpdateDelete = new String();
		Integer id, maxNotifId, notificationId = -1;
		try {
			listConfigTableOriginal = generalDao.getListOfStringBySql(sqlSelect);
			Iterator<String> configTableIterator = listConfigTableOriginal.iterator();

			List<String> listForMaxId = generalDao.getListOfStringBySql(
					String.format("select max(notification_listsystemdata_id) from %1$s", notificationTableName));

			List<String> listForUpdateNeedChecking = generalDao.getListOfStringBySql(String.format(
					"select NOTIFICATION_LISTSYSTEMDATA_ID || ',' || MODULE_ID || ',' || DATA_NAME || ',' || DATATYPE from %1$s",
					notificationTableName));
			Iterator<String> updateNeedIterator;

			if (listForMaxId.get(0) != null) {
				maxNotifId = Integer.parseInt(listForMaxId.get(0));
			} else {
				maxNotifId = 0;
			}

			// loop on fg_n_config_v table
			while (configTableIterator.hasNext()) {
				strOfNotifModule = configTableIterator.next().split(",");
				id = Integer.parseInt(strOfNotifModule[0]);
				nameIdForIgnore = strOfNotifModule[1];
				sqlValue = strOfNotifModule[2];
				metaDataMap = generalDao.getMetaData(sqlValue);

				//loop on metadata in one view (query)
				for (Map.Entry<String, String> entry : metaDataMap.entrySet()) {
					if (!(key = entry.getKey().toString()).equals(nameIdForIgnore)) {
						value = entry.getValue().toString();
						type = (value == "DATE") ? "D" : (value == "NUMBER") ? "N" : "T";

						updateNeedIterator = listForUpdateNeedChecking.iterator();
						// loop on NOTIFICATION_LISTSYSTEMDATA_ID. Checking for data was created. 
						// If not then insert, if type was changed, then update, if there obsolete data, then remove them
						while (updateNeedIterator.hasNext()) {
							strOfNotifModule = updateNeedIterator.next().split(",");
							if ((strOfNotifModule[1] + strOfNotifModule[2]).equals(id.toString() + key)) {
								if ((strOfNotifModule[1] + strOfNotifModule[2] + strOfNotifModule[3])
										.equals(id.toString() + key + type)) {
									updateInsert = 2;
								} else {
									updateInsert = 1;
								}
								notificationId = Integer.parseInt(strOfNotifModule[0]);
							}
						}
						if (updateInsert == 0) {
							strInsertUpdateDelete = String.format(sqlInsert, notificationTableName, id, key, type,
									++maxNotifId, 1); // string for insert
						} else if (updateInsert == 1) {
							strInsertUpdateDelete = String.format(sqlUpdateDataType, notificationTableName, type,
									notificationId); // string for update data type
						} else if (updateInsert == 2) {
							strInsertUpdateDelete = String.format(sqlUpdateIsUpdated, notificationTableName, 1,
									notificationId); // string for update ISPDATED field. Mean that data is good and not need to update or delete 
						}
						generalDao.updateSingleString(strInsertUpdateDelete);
						updateInsert = 0;
					}
				}
			}

			/*
			 * kd 09042018
			 * use next 2 row only for clean up database. If use these strings designed to delete data in usual cases 
			 * then metadata which are using in NOTIFICATION CRITERIA will disappears from UI, but will still left on database  
			 */
			/*
			strInsertUpdateDelete = String.format(sqlDelete, notificationTableName);
			generalDao.updateSingleString(strInsertUpdateDelete);
			*/

		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			metaDataMap = null;
			e.printStackTrace();
		} finally {
			generalDao.updateSingleString("UPDATE P_NOTIFICATION_LISTSYSTEMDATA SET ISUPDATED = 0"); //set ISUPDATED = 0 for use in next time when update P_NOTIFICATION_LISTSYSTEMDATA
		}
	}
}
