package com.skyline.form.service;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.dal.GeneralDao;

@Service
public class FormApiUIReportService { // TODO interface FormService and this should be FormServiceImp (in case we want to switch between different behaviors/algorithm) 

	private static final Logger logger = LoggerFactory.getLogger(FormApiService.class);

	@Value("${jdbc.url}")
	private String DB_URL;

	@Value("${jdbc.username}")
	private String DB_USER;

	@Value("${jdbc.password}")
	private String DB_PASSWORD;

	@Value("${ireportPath}")
	private String DIR_JASPER_XML;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	@Autowired
	public GeneralUtil generalUtil;

	@Autowired
	private JsonToSql fromFilterToSQL;
	
	@Autowired
	private GeneralUtilConfig generalUtilConfig;
	
	private final String hideEmptyColumns = "";

	//connect with db
	private Connection con = null;
	private CallableStatement stmt = null;

	public void uireportInit(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("../skylineFormWebapp/jsp/webix_reports_main.jsp").forward(request, response);
	}

	public List<JSONObject> getUIReportData(String requestType, String data, String reportSettings, String reportId) {
		List<JSONObject> toReturn = new ArrayList<JSONObject>();
		JSONObject toReturnJson = new JSONObject();
		String sql4 = "", sql = "", sql2 = "", sql3 = "";
		String sqlRepSaveData = "";
		JSONObject result4 = new JSONObject(), result = new JSONObject(), result2 = new JSONObject(),
				result3 = new JSONObject();

		if (requestType.equals("REPORT_LIST")) // Open report list window
		{
			sql4 = "select * from FG_REPORT_LIST"; // where REPORT_STYLE = 'SIMPLE'";
			//			sql4 = "select * from FG_REPORT_LIST where CHANGE_BY = " + generalUtil.getSessionUserId(); // where REPORT_STYLE = 'SIMPLE'";
			try {
				result4 = generalDao.getJSONObjectOfDateTable(sql4, hideEmptyColumns, "-1", null);
				//@FG_REPORT_USER_mt_v@
				toReturnJson.put("data", result4.get("data"));
				toReturnJson.put("columns", result4.get("columns"));
				toReturn.add(toReturnJson);
			} catch (Exception e) {
				logger.error("getUIReportData Exception!");
				generalUtilLogger.logWrite(LevelType.ERROR, "Exception", "-1", ActivitylogType.UiReport, null, e);
			}
		} else if (requestType.equals("REPORT_METADATA")) // Open simple report window
		{
			sqlRepSaveData = String.format("select t.report_save_data from FG_REPORT_LIST t where t.id = '%1$s'",
					reportId); //, t.\"REPORT_category\" 
			String viewName = "";
			try {
				con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				stmt = con.prepareCall(sqlRepSaveData);
				stmt.execute();
				ResultSet rs = stmt.getResultSet();
				while (rs.next()) {
					System.out.println("getString(1) is: " + rs.getString(1));
					toReturnJson.put("values", rs.getString(1));
				}
				viewName = getViewName(reportId)[0].split(":")[1];
				sql = "select * from " + viewName + "_md_inf_v";
				sql2 = "select * from " + viewName + "_md_col_v";
				sql3 = "select * from " + viewName + "_md_grp_v";

				result = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1",null);
				toReturnJson.put("data", result.get("data"));

				result2 = generalDao.getJSONObjectOfDateTable(sql2, hideEmptyColumns, "-1",null);
				toReturnJson.put("displayFields", result2.get("data"));
				toReturnJson.put("filterFields", result2.get("data"));
				toReturnJson.put("groupFields", result2.get("data"));

				result3 = generalDao.getJSONObjectOfDateTable(sql3, hideEmptyColumns, "-1", null);
				toReturnJson.put("summaryFields", result3.get("data"));
				toReturn.add(toReturnJson);
			} catch (Exception e) {
				logger.error("getUIReportData Exception!");
				generalUtilLogger.logWrite(LevelType.ERROR, "Exception", "-1", ActivitylogType.UiReport, null, e);
			} finally {
				con = null;
			}

		} else if (requestType.equals("REPORT_FILTER")) // Push button generate in simple report window
		{
			//sqlRepSaveData = String.format("select t.report_sql from FG_REPORT_LIST t where t.id = '%1$s'", reportId); 
			String viewName = getViewName(reportId)[0].split(":")[1];
			try {
				sql = "select distinct * from " + viewName + "_v";
				JSONObject resMetadata = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1", null);
				Map<String, String> mapMetaData = new HashMap<>();// = new JSONObject();
				for (int i = 0; i < resMetadata.getJSONArray("metaData").length(); i++) {
					mapMetaData.put(resMetadata.getJSONArray("metaData").get(i).toString().split(":")[0],
							resMetadata.getJSONArray("metaData").get(i).toString().split(":")[1]);
				}
				//				String strForSQL = fromFilterToSQL.getFromFGReportUserRole(viewName+"_v", reportSettings, "Simple", null); //kd 15072018
				String strForSQL = fromFilterToSQL.getFromFGReportUserRole(viewName + "_v", reportSettings, "Simple",
						mapMetaData);

				//				sql2 = "select * from " + viewName + "_md_inf_v";
				Map<String, String> mapMdInfMetaData = mapInfMetadata("select * from " + viewName + "_md_inf_v");
				result = generalDao.getJSONObjectOfDateTableUIReport(strForSQL, hideEmptyColumns, mapMdInfMetaData);
			} catch (Exception e) {
				logger.error("getUIReportData Exception!");
				generalUtilLogger.logWrite(LevelType.ERROR, "Exception", "-1", ActivitylogType.UiReport, null, e);
			}
			toReturn.add(result);
		} else if (requestType.equals("REPORT_FILTER_CUSTOM")) // Open custom report window
		{
			sqlRepSaveData = String.format("select t.report_save_data from FG_REPORT_LIST t where t.id = '%1$s'",
					reportId); //, t.report_sql
			String viewName = getViewName(reportId)[0].split(":")[1];
			try {
				con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				stmt = con.prepareCall(sqlRepSaveData);
				stmt.execute();
				ResultSet rs = stmt.getResultSet();
				while (rs.next()) {
					System.out.println("getString(1) is: " + rs.getString(1));
					toReturnJson.put("values", rs.getString(1));
				}
				sql = "select distinct * from " + viewName + "_v";
				//				sql2 ="select render from FG_REPORT_USER_ROLE_RENDER_V t";
				sql2 = "select render, render2,render3, render4, render5 from " + viewName + "_RENDER_V t";
				sql3 = "select sections,sections2 from " + viewName + "_section_v t";

				//uncomment next two rows and comment one previous 
				Map<String, String> mapMdInfMetaData = mapInfMetadata("select * from " + viewName + "_md_inf_v");
				result = generalDao.getJSONObjectOfDateTableUIReport(sql, hideEmptyColumns, mapMdInfMetaData);

				toReturnJson.put("catalog", result);
				result2 = generalDao.getJSONObjectOfDateTable(sql2, hideEmptyColumns, "-1", null);
				result3 = generalDao.getJSONObjectOfDateTable(sql3, hideEmptyColumns, "-1", null);

				//section result3
				JSONObject section = new JSONObject();
				section.put("data", result3.get("data"));
				toReturnJson.put("sections", section);

				//rend result2
				//				Map<String,StringBuilder> rend = new HashMap<>();
				JSONObject rend = new JSONObject();
				rend.put("data", result2.get("data"));

				//				rend.put("data","[\"0\",\"1\",\"DDL-AUTO-COMPLETE\",\"Section1\",\"role\"]");
				//				rend.put("data","[0,1,DDL-AUTO-COMPLETE,Section1,role]");
				toReturnJson.put("render", rend);
				toReturn.add(toReturnJson);

			} catch (Exception e) {
				logger.error("getUIReportData Exception!");
				generalUtilLogger.logWrite(LevelType.ERROR, "Exception", "-1", ActivitylogType.UiReport, null, e);
			} finally {
				con = null;
			}
		} else if (requestType.equals("REPORT_RESULT_CUSTOM")) // Push button generate in custom report window
		{
			String[] names = getViewName(reportId);
			String viewName = names[0].split(":")[1];
			//			render from FG_REPORT_USER_ROLE_RENDER_V t";
			//			sections from fg_report_user_role_section_v t";
			try {
				sql = "select distinct * from " + viewName + "_v";
				sql2 = "select render, render2,render3, render4, render5 from " + viewName + "_RENDER_V t";
				JSONObject resMetadata = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1", null);
				JSONObject renderMetadata = generalDao.getJSONObjectOfDateTable(sql2, hideEmptyColumns, "-1", null);
				Map<String, String> mapMetaData = new HashMap<>();// = new JSONObject();
				Map<String, String> mapRenderMetaData = new HashMap<>();

				for (int i = 0; i < resMetadata.getJSONArray("metaData").length(); i++) {
					mapMetaData.put(resMetadata.getJSONArray("metaData").get(i).toString().split(":")[0],
							resMetadata.getJSONArray("metaData").get(i).toString().split(":")[1]);
				}
				for (int i = 0; i < renderMetadata.getJSONArray("data").length(); i++) {
					mapRenderMetaData.put(renderMetadata.getJSONArray("data").getJSONArray(i).get(0).toString(),
							renderMetadata.getJSONArray("data").getJSONArray(i).get(3).toString());
				}
				String strForSQL = fromFilterToSQL.getFromFGReportUserRole(viewName + "_v", reportSettings, "Custom",
						/* mapMetaData, */mapRenderMetaData);
				Map<String, String> mapMdInfMetaData = mapInfMetadata("select * from " + viewName + "_md_inf_v");
				result = generalDao.getJSONObjectOfDateTableUIReport(strForSQL, hideEmptyColumns, mapMdInfMetaData);

				mapMdInfMetaData = mapInfMetadata("select * from " + viewName + "_md_inf2_v");
				sql2 = "select distinct t.* from " + viewName + "_T2_V t";//"_TABLE2_V t"; //Check and USE Another Name //FG_REPORT_USER_ROLE

				result2 = generalDao.getJSONObjectOfDateTableUIReport(sql2, hideEmptyColumns, mapMdInfMetaData);
				JSONObject json1 = new JSONObject();
				JSONObject json2 = new JSONObject();
				json1.put("tableData", result);
				json1.put("tableName", names[0].split(":")[0]);
				Object jsonArrResultData2 = null;
				try {
					jsonArrResultData2 = result2.get("data");
				} catch (JSONException je) {
					System.out.println("table 2 not found!");
				}

				if (jsonArrResultData2 != null && names[1] != null) {
					System.out.println("There is Table 2!");
					json2.put("tableData", result2);
					json2.put("tableName", names[1].split(":")[0]);

					JSONObject[] merged = new JSONObject[] { json1, json2 };
					toReturn.add(merged[0]);
					toReturn.add(merged[1]);
					System.out.println(toReturn);
				} else {
					toReturn.add(json1);
				}
				//				
			} catch (Exception e) {
				logger.error("getUIReportData Exception!");
				generalUtilLogger.logWrite(LevelType.ERROR, "Exception", "-1", ActivitylogType.UiReport, null, e);
			}
		} else if (requestType.equals("SAVE_REPORT")) // Push button Save Definition in any report window
		{
			//update
			if (data != null) //TEMP Comment
			{
				String reportName = "", reportDescription = "", reportScope = "", reportStyle = "", reportData = "",
						reportCategory = "", reportSQL = "";
				String resOFSave;
				boolean isInsert = false;
				generalUtilLogger.logWrite(LevelType.DEBUG, "SAVE_REPORT. receivedId id: '" + reportId + "'", "-1",
						ActivitylogType.UiReport, null);
				reportName = generalUtil.getJsonValById(data, "reportName").replaceAll("'", "''");
				reportDescription = generalUtil.getJsonValById(data, "reportDescription").replaceAll("'", "''");
				reportStyle = generalUtil.getJsonValById(data, "REPORT_STYLE");
				reportScope = generalUtil.getJsonValById(data, "REPORT_SCOPE");
				reportSQL = generalUtil.getJsonValById(data, "REPORT_SQL");
				reportCategory = generalUtil.getJsonValById(data, "REPORT_CATEGORY");
				reportData = generalUtil.getJsonValById(data, "data");

				if (reportId.equals("-1")) {
					//sql  = String.format("insert into FG_REPORT_LIST (REPORT_DESCRIPTION) values ('%1$s'||%2$s)", "Users in the system. Time Stamp: ","TO_CHAR(SYSTIMESTAMP, 'HH:MI:SS')");
					sql = String.format(
							"insert into FG_REPORT_LIST (CHANGE_BY, ACTIVE, TIMESTAMP, REPORT_DESCRIPTION, REPORT_NAME, REPORT_SCOPE, REPORT_STYLE, REPORT_SQL, REPORT_CATEGORY, REPORT_SAVE_DATA, META_DATA) values ('%8$s', 1, SYSDATE, '%1$s', trim('%2$s'), '%3$s', '%4$s', '%5$s', '%6$s', '%7$s', 'not in use')",
							reportDescription, reportName, reportScope, reportStyle, reportSQL, reportCategory,
							reportData, generalUtil.getSessionUserId());
					isInsert = true;
				} else {
					StringBuilder setString = new StringBuilder();
					if (!reportDescription.equals("")) {
						setString.append(", REPORT_DESCRIPTION = '" + reportDescription + "'");
					}
					if (!reportName.equals("")) {
						setString.append(", REPORT_NAME = '" + reportName + "'");
					}
					if (!reportStyle.equals("")) {
						setString.append(", REPORT_STYLE = '" + reportStyle + "'");
					}
					if (!reportScope.equals("")) {
						setString.append(", REPORT_SCOPE = '" + reportScope + "'");
					}
					if (!reportCategory.equals("")) {
						setString.append(", REPORT_CATEGORY = '" + reportCategory + "'");
					}
					if (!reportSQL.equals("")) {
						setString.append(", REPORT_SQL = '" + reportSQL + "'");
					}
					if (!reportData.equals("")) {
						setString.append(", REPORT_SAVE_DATA = '" + reportData + "'");
					}

					if (!setString.equals("")) {
						sql = String.format(
								//"update FG_REPORT_LIST set REPORT_DESCRIPTION = '%1$s', REPORT_NAME = trim('%2$s'), REPORT_SCOPE ='%3$s', REPORT_STYLE ='%4$s', REPORT_SAVE_DATA ='%5$s' where id = %6$s", 
								"update FG_REPORT_LIST set CHANGE_BY = %1$s, ACTIVE = 1, TIMESTAMP = SYSDATE"
										+ setString + " where id = %2$s",
								//reportDescription, reportName, reportScope, reportStyle, reportData, 
								generalUtil.getSessionUserId(), reportId);
					}
				}
				try {
					resOFSave = generalDao.updateSingleString(sql);
					if (!resOFSave.equals("-1")) // in cases of INSERT or UPDATE passed
					{
						if (isInsert) {
							sqlRepSaveData = String.format(
									"select t.id from FG_REPORT_LIST t where t.report_name = '%1$s'", reportName);
							String viewName = "";
							try {
								con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
								stmt = con.prepareCall(sqlRepSaveData);
								stmt.execute();
								ResultSet rs = stmt.getResultSet();
								while (rs.next()) {
									System.out.println("getString(1) is: " + rs.getString(1));
									viewName = rs.getString(1);
								}
							} catch (Exception e) {
								logger.error("getUIReportData Exception!");
							} finally {
								con = null;
							}
							result4.put("report_id", viewName);
						} else {
							result4.put("", "Ok");
						}
					} else // INSERT or UPDATE return exception
					{
						result4.put("data", "Error! Report name already exists!");
					}
					toReturn.add(result4);
				} catch (Exception e) {
					logger.error("getUIReportData Exception!");
					generalUtilLogger.logWrite(LevelType.ERROR, "Exception", "-1", ActivitylogType.UiReport, null, e);
				}
			}
		} else if (requestType.equals("DELETE_REPORT")) // Push button Remove in report list window
		{
			//delete
			//if parameter = some id what we passed before:
			if (!reportId.equals("")) //TEMP Comment
			{
				generalUtilLogger.logWrite(LevelType.DEBUG, "DELETE_REPORT. receivedId: '" + reportId + "'", "-1",
						ActivitylogType.UiReport, null);
				sql = String.format("delete from FG_REPORT_LIST where id = %1$s and nvl(system_row, 0) = 0", reportId);

				String resOFSave;
				try {
					resOFSave = generalDao.updateSingleString(sql);
					if (resOFSave.equals("1")) // in cases of DELETE passed
					{
						result4.put("", "Deleted Ok");
					} else if (resOFSave.equals("0")) // in cases of DELETE not possible because it's system (master) report
					{
						result4.put("data", "This record can not be deleted");
					} else // DELETE return exception (-1) 
					{
						result4.put("data", "Error! Record was not deleted.");
					}
					toReturn.add(result4);

				} catch (Exception e) {
					result4.put("data", "Error! Record was not deleted!");
					logger.error("getUIReportData Exception!");
				}
			}
		}
		return toReturn;
	}

	private Map<String, String> mapInfMetadata(String sql) {
		Map<String, String> mapMdInfMetaData = new HashMap<>();
		try {
			JSONObject mdInfMetadata = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1",null);
			for (int i = 0; i < mdInfMetadata.getJSONArray("data").length(); i++) {
				mapMdInfMetaData.put(
						mdInfMetadata.getJSONArray("data").getJSONArray(i).get(0).toString()
								.split(Pattern.quote("."))[0],
						mdInfMetadata.getJSONArray("data").getJSONArray(i).get(0).toString()
								.split(Pattern.quote("."))[2]);
			}
		} catch (Exception e) {
			logger.error("mapInfMetadata Exception!");
			generalUtilLogger.logWrite(LevelType.ERROR, "Exception", "-1", ActivitylogType.UiReport, null, e);
		}
		return mapMdInfMetaData;
	}

	private String[] getViewName(String reportId) //return [0] - TABLE_TITLE:TABLE_VIEW for 1st table [1] for second table in custom report
	{
		//		String sqlRepSaveData = String.format("select t.report_save_data, t.report_sql from FG_REPORT_LIST t where t.id = '%1$s'", reportId); 
		String sql = String.format(
				//"select TABLE_TITLE, TABLE_VIEW from FG_REPORT_LIST_NAMES_V where REPORT_CATEGORY = '%1$s' and REPORT_STYLE ='%2$s' order by 'TABLE_NUMBER'",
				"select n.TABLE_TITLE, n.TABLE_VIEW from FG_REPORT_LIST rl, FG_REPORT_LIST_NAMES_V n where rl.id = '%1$s' and n.REPORT_CATEGORY = rl.REPORT_CATEGORY and n.REPORT_STYLE = rl.REPORT_STYLE",
				reportId);
		String[] result = new String[2]; // Increase quantity of element in case change requirements in part of quantity of tables in custom report
		try {
			con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
			stmt = con.prepareCall(sql);
			stmt.execute();
			ResultSet rs = stmt.getResultSet();
			int i = 0;
			while (rs.next()) {
				System.out.println("getString(1) is: " + rs.getString(1) + " : getString(2) is: " + rs.getString(2));
				result[i] = rs.getString(1) + ":" + rs.getString(2);
				i++;
			}
		} catch (Exception e) {
			logger.error("getViewName Exception!");
			generalUtilLogger.logWrite(LevelType.ERROR, "Exception", "-1", ActivitylogType.UiReport, null, e);
		} finally {
			con = null;
		}
		return result; //return TABLE_TITLE:TABLE_VIEW
	}
	
	public String onUIReportRuleListChange(String reportName, String fieldName) {
		System.out.println("TODO get list for this call in Report UI (if it will be needed) like in getSqlCriteria for data tables");
		return "";
	}
}