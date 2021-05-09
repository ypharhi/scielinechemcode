package com.skyline.form.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyline.form.dal.GeneralDao;

import oracle.jdbc.OracleTypes;

@Service
public class FormApiElementsGeneralService { // TODO interface FormService and this should be FormServiceImp (in case we want to switch between different behaviors/algorithm) 

	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	@Autowired
	private GeneralDao generalDao;

	public void getStabValuesFromApi(int userId, /*Map<String, String> allRequestParams*/String product,
			HttpServletResponse response) {
		try { 
			String spIdList = ""; //request.getParameter("ajSpIdList");
			String htmlStage = "", stageIDList = "";

			if (!product.equals("-1")) { 
				{ //stability
					StringBuilder sbStageList = new StringBuilder();
					htmlStage = getHtmlStageSpMatrix(userId, product, spIdList, sbStageList);
					stageIDList = sbStageList.toString();

					if (stageIDList.length() > 0) { // remove last comma from list
						stageIDList = stageIDList.substring(0, stageIDList.length() - 1);
					}
				} 
			} 
			try {
				response.setCharacterEncoding("UTF-8");
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("tdStage", htmlStage);
				jsonObj.put("txtStageID", stageIDList);
				response.getWriter().print(jsonObj);
			} catch (JSONException e) {
				response.getWriter().write("-1");
				generalUtilLogger.logWrite(e);
				e.printStackTrace();
			}
			return;
	 

		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
	}

	public String getHtmlStageSpMatrix(int userId, String productID, String stabilitySpIdList,
			StringBuilder stageList) {
		String toReturn = "";
		String sql = "";
		//	Connection con = dbBean.getConnection();
		CallableStatement proc_stmt = null;
		StringBuffer sb = new StringBuffer();
		ResultSet rs = null;
		ResultSet rsMap = null;
		int spCounter = 0;
		boolean dataFoundFlag = false;

		try {
			Connection conn = generalDao.getConnectionFromDataSurce();
			sql = "call SP_GET_STAGE_SP_MATRIX (?,?,?,?)";

			//rs = generalDao.getResultSet(conn, sql);

			proc_stmt = conn.prepareCall(sql);
			proc_stmt.setInt(1, userId);//dbBean.getUser()); 
			//proc_stmt.setString(2,sessionId); 
			proc_stmt.setString(2, productID);

			proc_stmt.registerOutParameter(3, OracleTypes.CURSOR);
			proc_stmt.registerOutParameter(4, OracleTypes.CURSOR);
			proc_stmt.execute();
			rs = (ResultSet) proc_stmt.getObject(3);
			rsMap = (ResultSet) proc_stmt.getObject(4);

			Hashtable<String, String> htMap = new Hashtable<String, String>();
			while (rsMap.next()) {
				dataFoundFlag = true;
				String keyField = rsMap.getString("key_field");
				String colVal = rsMap.getString("value_field");
				if (colVal == null)
					colVal = "0";
				htMap.put(keyField, colVal);
				//		        if (colVal != null) {
				//		        	htMap.put(keyField,colVal);
				spCounter++;
				//		        }
			}

			//start build the html 
			//start main table
			sb.append("<table id='stabilityTable' style='table-layout:fixed'>");
			//select all/non line
			String selectAllNon = "<tr>\n" + "     <td style='width:100%'>\n"
					+ "     <table width='100%' border='0'><tr><td width='100%'>\n"
					+ "     <a href=\"#\" onclick=\"allClick();\" title=\"Select All Records\" class=\"hrefLink\" >Select All</a> \n"
					+ "     &nbsp;\n"
					+ "     <a href=\"#\" onclick=\"noneClick();\" title=\"Select None\" class=\"hrefLink\">Select None</a>  \n"
					+ "     </td></tr></table>\n" + "     </td>\n" + "</tr>";
			sb.append(selectAllNon);
			//blank row
			//sb.append("<tr><td>&nbsp;</td></tr>");

			//selection matrix table
			sb.append("<tr>");
			sb.append("<td>");
			//sb.append("<div style='width:100%;overflow-x:auto;overflow-y:auto;'><table border='0' width='" + ((spCounter+1) * 120) + "px'>"); 
			sb.append("<div class='DivDataStability'><table border='0' width='" + ((spCounter + 1) * 120) + "px'>");

			sb.append("<TR class='cssTableColumnFirstRowMain'>");
			//header row..
			//first column
			sb.append("<TD class='TDHMain'  style='width:120px'>&nbsp;</td>\n");
			//column loop
			Vector v = new Vector(htMap.keySet());
			Collections.sort(v);
			for (Enumeration e = v.elements(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String ColumnName = (String) htMap.get(key);
				sb.append("<TD class='TDHMain'  style='width:120px'>" + ColumnName + "</TD>\n ");
			}
			sb.append("</tr>");

			while (rs.next()) {
				sb.append("<tr>");
				for (int i = 2; i <= spCounter + 2; i++) {
					String currentSp = rs.getString(i);
					if (i == 2) {
						sb.append("<TD class='TDVMainLine' onclick='openCreateSampleDialog();' id='"
								+ rs.getString("row_id") + "' checkStageFlag='1'>" + currentSp + "</TD>\n");
						stageList.append(rs.getString("row_id") + ",");
					} else if (currentSp == null) {
						sb.append("<TD class='TDHMain'>&nbsp;</TD>\n");
					} else {
						if (stabilitySpIdList != null
								&& ("," + stabilitySpIdList + ",").contains("," + currentSp + ",")) {
							sb.append("<TD id='" + currentSp
									+ "' class='TDVMainLine' onclick='checkSp(this);'  checkSpFlag='1'  align='center'><img border='0' src='images/available.png' width='13px' height='13px'></TD>\n");
						} else {
							sb.append("<TD id='" + currentSp
									+ "' class='TDVMainLine' onclick='checkSp(this);'  checkSpFlag='0'  align='center'>&nbsp;</TD>\n");
						}
					}
				}
				sb.append("</tr>\n");
			}
			// close the html (table and div)
			sb.append("</table></div>");
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("</table>");

			toReturn = sb.toString();
			if (!dataFoundFlag) {
				toReturn = getHtmlStageSpMatrixNoData();
			}
		} catch (Exception ex) {
			toReturn = getHtmlStageSpMatrixNoData();
			//	    LG.write(ex.toString(), "DBLookup getHtmlStageSpMatrix(1)", ex);

			generalUtilLogger.logWrite(ex);
			ex.printStackTrace();

			System.out.println("DBLookup getHtmlStageSpMatrix: " + ex.toString());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (proc_stmt != null) {
					proc_stmt.close();
				}

				//	      dbBean.disconnect();

			} catch (Exception ex) {
				// do nothing
			}
		}

		return toReturn;
	}

	private String getHtmlStageSpMatrixNoData() {
		return "<table width=100%><tr><td class='cssStaticData_1'></td></tr></table>"; //No condition found
	}

}
