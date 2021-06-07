package com.skyline.form.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilCalc;
import com.skyline.form.service.GeneralUtilForm;
import com.skyline.form.service.GeneralUtilLogger;

import oracle.sql.CLOB;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SearchSqlDaoImp extends BasicDao implements SearchSqlDao {

	private static final Logger logger = LoggerFactory.getLogger(SearchSqlDaoImp.class);

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private GeneralUtilForm generalUtilForm;

	@Autowired
	private GeneralUtilCalc generalUtilCalc;

	@Value("${jdbc.driverClassName}")
	private String driverClassName;
	@Value("${jdbc.username}")
	private String username;
	@Value("${jdbc.url}")
	private String url;
	@Value("${jdbc.password}")
	private String password;

	@Value("${dataTableTopRowsNum:10000}")
	private String dataTableTopRowsNum;

	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	final PreparedStatement[] stmt = new PreparedStatement[2];

	@Autowired
	private GeneralDao generalDao;

	@Override
	public void stopQuery() {
		try {
			if (stmt[0] != null && !stmt[0].isClosed()) {
				stmt[0].cancel();
			}

			if (stmt[1] != null && !stmt[1].isClosed()) {
				stmt[1].cancel();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JSONObject getJSONObjectOfDataTable(final String sql, String hideEmptyColumns, String topRowsNum,
			final int indx) {
		logger.info("getJSONObjectOfDateTable sql=" + sql);

		if (!topRowsNum.equals("-1")) {
			dataTableTopRowsNum = topRowsNum;
		}
		JSONObject toReturn = new JSONObject();
		List<String> listOfMetaData;
		JSONArray jsonArrayOfColumns;
		try {

			List<Map<String, Object>> rows = jdbcTemplate.query(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					stmt[indx] = connection.prepareStatement(sql);
					return stmt[indx];
				}
			}, new ResultSetExtractor<List<Map<String, Object>>>() {
				@Override
				public List<Map<String, Object>> extractData(ResultSet rs) throws SQLException, DataAccessException {

					List<Map<String, Object>> currentRows = new ArrayList<Map<String, Object>>();
					try {
						ResultSetMetaData rsmd = rs.getMetaData();
						int columnCount = rsmd.getColumnCount();
						List<String> colTypes = new ArrayList<>(columnCount);
						for (int i = 1; i <= columnCount; i++) {
							colTypes.add(rsmd.getColumnTypeName(i));
						}
						while (rs.next()) {
							Map<String, Object> metaDataMap = new LinkedCaseInsensitiveMap<Object>(columnCount);
							for (int i = 1; i <= columnCount; i++) {
								metaDataMap.put(rsmd.getColumnName(i), getColumnValue(colTypes.get(i - 1), i, rs));
							}
							currentRows.add(metaDataMap);
						}

					} catch (SQLTimeoutException e) {
						currentRows.clear();
					} catch (Exception e) {
						generalUtilLogger.logWrite(LevelType.ERROR, "Error in DataTable pivot data perparation!", "-1",
								ActivitylogType.SQLError, null, e);
					}
					return currentRows;
				}
			});

			/********Param Monitoring handler *****************************************************************/

			List<String> colAfterParm = new ArrayList<String>();//12122018 ab: used for "_SMARTMONPARAM" for now

			//12122018 ab: paramCols changed to Map object, because there may be more than one special SMARTs in the table
			Map<String, List<String>> paramColMap = getSmartsCol(rows);
			//			boolean hasColMonParam = false;

			for (Map.Entry<String, List<String>> smartsEntry : paramColMap.entrySet()) {
				String paramCol = smartsEntry.getKey();
				colAfterParm = smartsEntry.getValue();
				if (!paramCol.isEmpty() && paramCol.endsWith("MONPARAM")) {
					//					hasColMonParam = true;
					List<String> paramsArray = new ArrayList<String>();
					JSONArray paramMonitoringNamesArray;
					JSONObject paramMonitoringNameJSONObject, tempObject, tempUomObject;
					String paramMonitoringValue;
					boolean flag;

					for (int i = 0; i < rows.size(); i++) {
						String currRowMPVal = (rows.get(i).get(paramCol) == null) ? "{}"
								: rows.get(i).get(paramCol).toString();
						paramMonitoringNameJSONObject = new JSONObject(currRowMPVal);
						paramMonitoringNamesArray = paramMonitoringNameJSONObject.names(); // get jsonObject keys as array
						if (paramMonitoringNamesArray == null) {
							continue;
						}
						for (int j = 0; j < paramMonitoringNamesArray.length(); j++) {
							paramMonitoringValue = paramMonitoringNamesArray.get(j).toString(); // paramMon current name/key
							if (paramMonitoringValue.contains("_uom")) {
								continue;
							}
							tempObject = new JSONObject(
									paramMonitoringNameJSONObject.get(paramMonitoringValue).toString()); //paramMon value of current name/key
							if (tempObject.get("val").equals("")) {
								continue;
							}

							//kd 22032018 this check remove Parameter from table Monitoring Parameters of workup if parameter was delete from Parameters in Maintenance 	
							if (generalUtilForm.getCurrrentNameInfoAllContainsId("MP", tempObject.getString("formid"))
									.size() > 0) {
								if (generalUtilForm
										.getCurrrentNameInfoAllContainsId("MP", tempObject.getString("formid")).get(0)
										.get("ID") == null /*(sequence != null) && !sequence.contains(tempObject.getString("formid"))*/) {
									continue;
								}
							} else {
								continue;
							}

							flag = true;
							// prevent from duplicates
							for (int k = 0; k < paramsArray.size(); k++) {
								if (paramsArray.get(k).equals(paramMonitoringNamesArray.get(j))) {
									flag = false;
									break;
								}
							}
							if (flag) {
								paramsArray.add(paramMonitoringNamesArray.get(j).toString()); // add parameters to display as column names
							}
						}
					}
					if (paramsArray.size() > 0) {
						//sort parameter names alphabetically
						Collections.sort(paramsArray, new Comparator<String>() {
							@Override
							public int compare(String s1, String s2) {
								return s1.toLowerCase().compareTo(s2.toLowerCase());
							}
						});
					}

					for (int i = 0; i < rows.size(); i++) {
						Map<String, Object> currRow = rows.get(i);
						String currRowMPVal = (currRow.get(paramCol) == null) ? "{}" : currRow.get(paramCol).toString();
						paramMonitoringNameJSONObject = new JSONObject(currRowMPVal);
						currRow.remove(paramCol); // remove '_SMARTMONPARAM' column from current row
						for (int j = 0; j < paramsArray.size(); j++) {
							String currColName = paramsArray.get(j);
							String currColNameUOM = "{" + currColName + "}_uom"; //add unique part to UOM column 
							try {
								// 13122018 ab: used 'optString' instead 'getString' to display empty value in case there isn't such parameter in current row (avoid from falling into a catch())
								tempObject = new JSONObject(paramMonitoringNameJSONObject.optString(currColName, "{}")); // value of current key/name
								tempUomObject = new JSONObject(
										paramMonitoringNameJSONObject.optString(currColName + "_uom", "{}")); // uom value of current key/name
								// param value
								String currValue = tempObject.optString("val");
								// param id
								String currValueId = tempObject.optString("formid");
								// param UOM id
								String currUOMVal = tempUomObject.optString("val");
								// param UOM value
								String currUOMText = tempUomObject.optString("text");

								//define SMARTMONPARAM as editable cells
								if (paramCol.equals("MPE_SMARTMONPARAM")) {
									if (currRow.containsKey("MPFORMID")) {
										String currMPFormID = (currRow.get("MPFORMID") == null) ? ""
												: currRow.get("MPFORMID").toString();
										if (!currMPFormID.equals("")) {
											JSONObject editableTextObj = new JSONObject();
											JSONObject editableUOMObj = new JSONObject();
											editableTextObj.put("displayName", currValue);
											editableTextObj.put("mp_formid", currMPFormID);
											editableTextObj.put("mp_name", currColName);
											editableTextObj.put("is_uom", "0");
											editableTextObj.put("formNumberID", currRow.get("FORMNUMBERID"));
											editableTextObj.put("htmlType", "text");
											editableTextObj.put("saveType", "monitoringParams");

											JSONObject selUomObj = new JSONObject();
											StringBuilder defaultSelUomStr = new StringBuilder();
											JSONArray uomFullList = getMPUOMArr(currValueId, currColName,
													defaultSelUomStr);
											if (!currUOMVal.equals("") && !currUOMText.equals("")) {
												selUomObj.put("ID", currUOMVal);
												selUomObj.put("displayName", currUOMText);
											} else {
												try {
													JSONObject defaultSelUomObj = new JSONObject(
															defaultSelUomStr.toString());
													selUomObj.put("ID", defaultSelUomObj.optString("ID"));
													selUomObj.put("displayName", defaultSelUomObj.optString("VAL"));
												} catch (Exception e) {
													selUomObj.put("ID", uomFullList.optJSONObject(0).optString("ID"));
													selUomObj.put("displayName",
															uomFullList.optJSONObject(0).optString("VAL"));
													generalUtilLogger.logWrite(e);
													e.printStackTrace();
												}
											}

											editableUOMObj.put("displayName", new JSONArray().put(selUomObj));
											//editableUOMObj.put("defaultUOMVal", currUOMVal);
											editableUOMObj.put("mp_formid", currMPFormID);
											editableUOMObj.put("mp_name", currColName);
											editableUOMObj.put("is_uom", "1");
											editableUOMObj.put("fullList", uomFullList);
											editableUOMObj.put("htmlType", "select");
											editableUOMObj.put("saveType", "monitoringParams");
											editableUOMObj.put("formNumberID", currRow.get("FORMNUMBERID"));

											currRow.put(currColName + "_SMARTEDIT", editableTextObj.toString());
											currRow.put(currColNameUOM + "_SMARTEDIT", editableUOMObj.toString());
										} else {
											currRow.put(currColName + "_SMARTEDIT", "");
											currRow.put(currColNameUOM + "_SMARTEDIT", "");
										}
									} else {
										currRow.put(currColName, currValue);
										currRow.put(currColNameUOM, ((currValue.equals("")) ? "" : currUOMText));
										generalUtilLogger.logWriter(LevelType.ERROR,
												"Column 'MPFORMID' is not defined in sql view",
												ActivitylogType.GeneralError, "EDITABLE MONITORING PARAMETER");
									}
								} else {
									currRow.put(currColName, currValue);
									currRow.put(currColNameUOM, ((currValue.equals("")) ? "" : currUOMText));
								}
							} catch (Exception e) {
								//kd 22032018 added this catch to avoid show data in wrong column (avoid shifting data left in the row)
								currRow.put(currColName, "");
								currRow.put(currColName + "_uom", "");
								System.out.println("Failed " + e);
								generalUtilLogger.logWrite(e);
							}
						}

						if (paramsArray.size() > 0) // check if there are parameters to display
						{
							// move colAfterParm after the paramCol (their original position in the sql) yp 02052018
							for (String col_ : colAfterParm) {
								Object o_ = currRow.get(col_);
								currRow.remove(col_);
								currRow.put(col_, o_);
							}
						}
					}

				}
				/**************************************************************************************************/

				/******** DyanamicParams handler *****************************************************************/

				else if (!paramCol.isEmpty() && paramCol.endsWith("DYNPARAM")) {
					JSONObject mobilePhaseComposNameJSONObject, jo;
					String val, uom, parentId = rows.get(0).get("PARENTID").toString();
					List<Map<String, Object>> lastDynamicParamsListOfMaps = generalDao.getListOfMapsBySql(
							"select * from FG_DYNAMICPARAMS t where PARENT_ID = '" + parentId + "' order by ORDER_");
					Map<String, String> columnsMap = new HashMap<String, String>();
					for (int i = 0; i < lastDynamicParamsListOfMaps.size(); i++) {
						if (lastDynamicParamsListOfMaps.get(i).get("ACTIVE").toString().equals("1")) {
							columnsMap.put(lastDynamicParamsListOfMaps.get(i).get("ORDER_").toString(),
									lastDynamicParamsListOfMaps.get(i).get("LABEL").toString());
						}
					}
					for (int i = 0; i < rows.size(); i++) {
						mobilePhaseComposNameJSONObject = new JSONObject(rows.get(i).get(paramCol).toString());
						rows.get(i).remove(paramCol);
						for (Map.Entry<String, String> entry : columnsMap.entrySet()) {
							if (mobilePhaseComposNameJSONObject.has(String.valueOf(entry.getKey()))) {
								jo = mobilePhaseComposNameJSONObject.getJSONObject(entry.getKey());
								val = jo.getString("val");
								uom = jo.getString("uom");
							} else {
								val = "";
								uom = "";
							}
							if (entry.getKey().equals("0")) {
								rows.get(i).put(entry.getValue(), generalUtilCalc.getNumberRoundedDispaly(val, "3"));
								rows.get(i).put("UOM", generalUtilForm.getCurrrentIdInfo(uom).get("NAME"));
							} else {
								rows.get(i).put(entry.getValue(),
										(val.equals("")) ? ""
												: generalUtilCalc.getNumberRoundedDispaly(val, "3") + " ["
														+ generalUtilForm.getCurrrentIdInfo(uom).get("NAME") + "]");
							}
						}
					}

					//sort by Time Point
					Collections.sort(rows, new Comparator<Map<String, Object>>() {
						public int compare(final Map<String, Object> o1, final Map<String, Object> o2) {
							int compReturn = 0;
							try {
								compReturn = Integer.valueOf(o1.get("Time Point").toString())
										.compareTo(Integer.valueOf(o2.get("Time Point").toString()));
							} catch (NumberFormatException e) {
								//return 0 on failure
							}
							return compReturn;
						}
					});

				}

				/******** SMARTPIVOT / SMARTPIVOTSQL handler *****************************************************************/
				else if (!paramCol.isEmpty()
						&& (paramCol.endsWith("SMARTPIVOT") || paramCol.endsWith("SMARTPIVOTSQL"))) {
					List<String> columnNames = new ArrayList<>();
					Map<String, JSONArray> pivotDataMap = new HashMap<String, JSONArray>();
					String smartPivotSql = "";
					String smartPivotSqlIdName = "";
					boolean prepareFailureFlag = false;

					try {
						// prepare data and columns
						List<String> objListColTmp = null;
						List<String> objListCol = null;
						if (paramCol.endsWith("SMARTPIVOTSQL")) { // SMARTPIVOTSQL the items are in the SQL (the column order is made in the SQL)
																		// Example: select result_SMARTPIVOT from fg_p_action_result_v where step_id = 117945
																	// contains all the pivot item under this SQL scope with the same format as SMARTPIVOT
																	// the column order by is inside the SQL (in this example inside fg_p_action_result_v view)
																	// duplicate values (more then one value under the sane column nmae in the same row) will be represent under additional column <column name>.<index>
																	// get the sql from the first row (the SQL is the same in every row)
							smartPivotSql = rows.get(0).get(paramCol).toString();
							//System.out.println("SMARTPIVOTSQL=" + smartPivotSql);

							//init lists
							if (objListColTmp == null) {
								objListCol = new ArrayList<String>();
								objListColTmp = generalDao.getListOfStringBySql(smartPivotSql);

								for (int k = 0; k < objListColTmp.size(); k++) {

									//									JSONArray ja_ = null;
									JSONObject jo_ = new JSONObject(objListColTmp.get(k));
									try {
										JSONArray colArray = jo_.getJSONArray("column");
										JSONArray valArray = jo_.getJSONArray("val");
										JSONArray groupArray = null;
										try {
											groupArray = jo_.getJSONArray("group");
										} catch (Exception e) {
										}

										for (int m = 0; m < colArray.length(); m++) {
											JSONObject joSingle_ = new JSONObject(objListColTmp.get(k));
											String colSingle_ = (String) colArray.get(m);
											String valSingle_ = (String) valArray.get(m);
											String valGroup_ = "";
											if (groupArray != null) {
												valGroup_ = (String) groupArray.get(m);
											}
											joSingle_.put("column", colSingle_);
											joSingle_.put("val", valSingle_);
											if (groupArray != null) {
												joSingle_.put("group", valGroup_);
											}
											objListCol.add(joSingle_.toString());
										}
									} catch (Exception e) {
										objListCol.add(objListColTmp.get(k));
									}
								}
							}

							for (int k = 0; k < objListCol.size(); k++) {
								JSONArray ja_ = null;
								JSONObject jo_ = new JSONObject(objListCol.get(k));
								String id_ = (String) jo_.get("pivotkey");
								String name_ = (String) jo_.get("column");
								String group_ = "@PIVOT_GROUP@";
								if (jo_.has("group")) {
									group_ = (String) jo_.get("group") + group_;
								}
								name_ = group_ + name_;
								jo_.put("column", name_);
								jo_.put("origin_column", name_);
								if (smartPivotSqlIdName.equals("")) {
									smartPivotSqlIdName = (String) jo_.get("pivotkeyname");
								}
								if (!pivotDataMap.containsKey(id_)) {
									ja_ = new JSONArray();
									if (!columnNames.contains(name_)) { // first item in the pivot row - we check only if the column name is not in columnNames list from previous rows
										columnNames.add(name_);
									}
								} else {
									ja_ = pivotDataMap.get(id_);
									if (!columnNames.contains(name_)) {
										columnNames.add(name_);
									} else { // handle duplication using evalPivotColumnName
										name_ = evalPivotColumnName(name_, ja_);
										jo_.put("column", name_);
										if (!columnNames.contains(name_)) {
											columnNames.add(name_);
										}
									}
								}
								ja_.put(jo_);
								pivotDataMap.put(id_, ja_);
							}
						}
					} catch (Exception e) {
						prepareFailureFlag = true;
						generalUtilLogger.logWrite(LevelType.ERROR, "Error in DataTable pivot data perparation!", "-1",
								ActivitylogType.SQLError, null, e);
					}

					// Handle data (if no prepareFailureFlag)
					if (!prepareFailureFlag) {
						try {
							for (int i = 0; i < rows.size(); i++) {
								// remove the pivot column
								rows.get(i).remove(paramCol);
								if (columnNames == null || columnNames.isEmpty()) {
									// do nothing
								} else {
									String id_ = null;
									JSONArray ja_ = null;
									if (!generalUtil.getNull(smartPivotSqlIdName).trim().equals("")) {
										id_ = rows.get(i).get(smartPivotSqlIdName).toString();
										ja_ = pivotDataMap.get(id_);
									}

									if (ja_ != null) {
										for (int k = 0; k < ja_.length(); k++) {

											JSONObject o_ = ja_.getJSONObject(k);

											String val = ((JSONObject) o_).getString("val");
											String column = ((JSONObject) o_).getString("column");

											for (String col : columnNames) {
												if (col.equals(column)) {
													rows.get(i).put(col, val);
												} else if (!rows.get(i).containsKey(col)) {
													rows.get(i).put(col, "");
												}
											}
										}
									} else {
										for (String col : columnNames) {
											rows.get(i).put(col, "");
										}
									}

								}

								// 12122018 ab: prevent from relocate pivot, must be the latest column in the table
								// move colAfterParm after the paramCol (their original position in the sql)
								/*for (String col_ : colAfterParm) {
								Object o_ = rows.get(i).get(col_);
								rows.get(i).remove(col_);
								rows.get(i).put(col_, o_);
								}*/
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							generalUtilLogger.logWrite(LevelType.ERROR, "Error in DataTable pivot data!", "-1",
									ActivitylogType.SQLError, null, e);
							return getJSONObjectOfDataTable(
									jsonSqlErrorMsg("Error in data table pivot SQL! [" + sql + "]"), "", "-1", indx);
						}
					}
				}
				/******** SMARTPATH handler *****************************************************************/
				else if (!paramCol.isEmpty() && paramCol.endsWith("SMARTPATH")) {
					String delimiter = ">";
					String smartPath = "";
					ArrayList<Integer> al = new ArrayList<>();//kd 14042019 fixed bug-7395 Use this var for identify rows which has wrong path and shoild't be show in search result
					for (int i = 0; i < rows.size(); i++) {
						try {
							if (rows.get(i).get(paramCol) != null) {
								smartPath = rows.get(i).get(paramCol).toString();
								boolean flag = true; // kd 12042019 This flag
														// show if path not
														// right
								JSONObject json = new JSONObject(smartPath);
								// JSONArray jarr = new JSONArray();
								JSONArray pathList = json.getJSONArray("path");
								/*
								 * String formC =
								 * generalUtil.getJsonValById(pathList.get(0).
								 * toString(), "name").split(":")[0]; int j=0;
								 * if (formC.equals("Project")){ j=1; }
								 */
								List<String> pathSmartLink = new ArrayList<String>();
								for (int j = 0; j < pathList.length(); j++) {
									String p = pathList.get(j).toString();
									String id = generalUtil.getNull(generalUtil.getJsonValById(p, "id"));
									if (id.equals("")) {
										continue;
									}
									String checkName = generalUtil.getNull(generalUtil.getJsonValById(p, "name")
											.substring(generalUtil.getJsonValById(p, "name").indexOf(":") + 1));
									if (checkName.equals("")) {
										flag = false;
										continue;
									}
									String name = generalUtil.getJsonValById(p, "name").split(":")[1];
									String formCode = generalUtil.getJsonValById(p, "name").split(":")[0];
									pathSmartLink.add("{\"displayName\":\"" + name
											+ "\",\"icon\":\"\",\"fileId\":\"\",\"formCode\":\"" + formCode
											+ "\",\"formId\":\"" + id + "\",\"tab\":\"\" ,\"delimiter\":\"" + delimiter
											+ "\"}");
								}
								if (flag) {
									rows.get(i).remove(paramCol);
									rows.get(i).put(paramCol.replace("SMARTPATH", "SMARTLINK"),
											pathSmartLink.isEmpty() ? "" : pathSmartLink.toString());
								} else {
									al.add(i);
								}
							}

						} catch (Exception e) {
							System.out.println(e);
							generalUtilLogger.logWrite(LevelType.WARN,
									"Error in search sql DataTable smartpath data perparation. smartPath=" + smartPath + ", sql=" + sql, "-1",
									ActivitylogType.SQLError, null, e);
						}
					}
					// kd 14042019 iterate rows and check if row has wrong path then it should be removed from result
					int i = 0;
					for (Iterator<Map<String, Object>> iter = rows.listIterator(); iter.hasNext();) {
						iter.next();
						if (al.contains(i)) { //It is checking current line (from rows) with array, which have wrong lines and shouldn't be in result 
							iter.remove();
						}
						i++;
					}
				}
				/**************************************************************************************************/
			}

			if (hideEmptyColumns.equals("true")) {
				// remove columns with empty data
				removeEmptyColumnsFromRows(rows);
			}

			jsonArrayOfColumns = getJSONArrayOfColumns(rows);

			Map<String, String> metaData = generalDao.getMetaData(sql); //tableName
			metaData = getMetaDataFromJSONArrayOfColumns(metaData, jsonArrayOfColumns);
			listOfMetaData = generalDao.getListOfNameAndTypeFromMetaData(metaData);
			List<String> dateList = generalDao.getDateListFromMetaData(metaData);
			JSONArray jsonArrayOfData = getJSONArrayOfData(rows, dateList);

			if (Integer.parseInt(dataTableTopRowsNum) != -1 && rows.size() > Integer.parseInt(dataTableTopRowsNum)) {
				toReturn.put("displayTopRows", dataTableTopRowsNum);
			} else if (rows.isEmpty()) {
				toReturn.put("displayTopRows", "-2");
			} else {
				toReturn.put("displayTopRows", "-1");
			}

			toReturn.put("columns", jsonArrayOfColumns);
			toReturn.put("data", jsonArrayOfData);
			toReturn.put("metaData", listOfMetaData);
		} catch (Exception e) {
			String errMessage = "General error in data table SQL";
			if (e.getCause() instanceof SQLException) {
				int errCode = ((SQLException) e.getCause()).getErrorCode();
				if (errCode == 1013) {//request cancel
					errMessage = "Search Has Been Stopped";
				} else if (errCode == 29902 || errCode == 20000) { // oracle error codes when search in files
					errMessage = "SearchFilesFailed";
				}
			}
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
			//			toReturn.put("columns", getJSONArrayOfColumns(null));
			return getJSONObjectOfDataTable(jsonSqlErrorMsg(errMessage), "", "-1", indx);
		}
		return toReturn;
	}

	/**
	 * 
	 * @param columnType - the column type of the current value
	 * @param index - the column index
	 * @param rs - the result set of the current row
	 * @return the value of the column. If the column type is a Clob then it's converted into a plain text.
	 * @throws SQLException
	 */
	private Object getColumnValue(String columnType, int index, ResultSet rs) throws SQLException {
		Object toReturn;
		if (columnType.equals("CLOB")) {
			CLOB clob = (CLOB) rs.getClob(index);
			toReturn = clob.getSubString(1, (int) clob.length());
		} else {
			toReturn = rs.getString(index);
		}
		return toReturn;
	}

	private JSONArray getMPUOMArr(String mpID, String mpName, StringBuilder defaultSelUomStr) {
		JSONArray retval;
		try {
			String arrString = "";

			List<Map<String, String>> mpListMap = generalUtilForm.getCurrrentNameInfoAllContainsName("MP", mpName);
			arrString = generalUtil.getNull(mpListMap.get(0).get("PARAM_UOM_OBJ"), "");
			defaultSelUomStr.append(generalUtil.getNull(mpListMap.get(0).get("DEFAULT_UOM_OBJ"), "{}"));
			System.out.println("defaultUom=" + defaultSelUomStr.toString());

			if (!arrString.equals("")) {
				retval = new JSONArray(arrString);
			} else {
				retval = new JSONArray();
			}
		} catch (JSONException e) {
			retval = new JSONArray();
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}

		return retval;
	}

	private Map<String, List<String>> getSmartsCol(List<Map<String, Object>> rows) {
		Map<String, List<String>> returnMap = new LinkedHashMap<String, List<String>>();
		List<String> colAfterParm = new ArrayList<String>();
		List<String> smartsList = new ArrayList<String>();
		String hasPivot = "";
		int arrInx = 0;

		if ((!rows.isEmpty())) {
			Set<String> rowKeySet = rows.get(0).keySet(); // get all column names
			for (String k : rowKeySet) {
				if (k.endsWith("_SMARTMONPARAM") || k.endsWith("_SMARTDYNPARAM") || k.endsWith("_SMARTPATH")) {
					smartsList.add(arrInx++, k);
				} else if (k.endsWith("_SMARTPIVOT") || k.endsWith("_SMARTPIVOTSQL")) {
					hasPivot = k;
				}
			}
			boolean isFind = false;
			for (String s : smartsList) {
				for (String k : rowKeySet) {
					if (k.equals(s)) {
						isFind = true;
					} else if (isFind) {
						colAfterParm.add(k);
					}
				}
				isFind = false;
				returnMap.put(s, colAfterParm);
				colAfterParm = new ArrayList<String>();
			}
			// 12122018 ab: pivot must be displayed as latest columns in the table and ignore position of given sql view 
			if (!hasPivot.equals("")) {
				returnMap.put(hasPivot, new ArrayList<String>());
			}
		}
		return returnMap;
	}

	private Map<String, String> getMetaDataFromJSONArrayOfColumns(Map<String, String> metaData,
			JSONArray JSONArrayOfColumns) {
		Map<String, String> toReturn = new HashMap<String, String>();
		try {
			if (metaData != null) {
				for (int i = 0; i < JSONArrayOfColumns.length(); i++) {
					if (JSONArrayOfColumns.getJSONObject(i).has("title")) {
						for (Map.Entry<String, String> entry : metaData.entrySet()) {
							if (entry.getKey().equals(JSONArrayOfColumns.getJSONObject(i).get("title"))) {
								toReturn.put(entry.getKey(), entry.getValue());
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return toReturn;
	}

	/**
	 * Remove Empty Columns
	 * @param rows
	 */
	private void removeEmptyColumnsFromRows(List<Map<String, Object>> rows) {
		if (!rows.isEmpty()) {
			boolean indexFlagArray[] = new boolean[rows.get(0).size()];
			int count;
			List<Integer> indexFlagArrayList = new ArrayList<Integer>();
			List<String> removeList = new ArrayList<String>();
			//fill a boolean array of empty columns
			int indexRow = 0;
			for (Map<String, Object> row : rows) {
				count = 0;
				for (Entry<String, Object> entry : row.entrySet()) {
					if (entry.getValue() != null && !entry.getValue().equals("")) {
						indexFlagArray[count] = true;
					}
					count++;
				}
				if (Integer.parseInt(dataTableTopRowsNum) != -1
						&& ++indexRow >= Integer.parseInt(dataTableTopRowsNum)) {
					break;
				}
			}
			for (int i = 0; i < indexFlagArray.length; i++) {
				if (indexFlagArray[i] == false) {
					indexFlagArrayList.add(i);
				}
			}
			//remove empty columns
			if (!indexFlagArrayList.isEmpty()) {
				count = 0;
				for (Entry<String, Object> entry : rows.get(0).entrySet()) {
					for (int index : indexFlagArrayList) {
						if (index == count) {
							removeList.add(entry.getKey());
						}
					}
					if (removeList.size() == indexFlagArrayList.size()) {
						break;
					}
					count++;
				}
				for (Map<String, Object> row : rows) {
					for (String remove : removeList) {
						row.remove(remove);
					}
				}
			}
		}
	}

	private JSONArray getJSONArrayOfColumns(List<Map<String, Object>> rows) {
		JSONArray JSONArrayOfColumns = new JSONArray();
		JSONObject json = null;
		try {
			if ((rows != null) && (!rows.isEmpty())) {
				Set<String> colSet = rows.get(0).keySet();
				for (String col : colSet) {
					json = new JSONObject();
					json.put("showPageFilter", "0");

					// check if column name has suffix "_SMART" and remove it. 
					int _ind = col.indexOf("_SMART");
					String _colNameWithoutSmart = (_ind > 0) ? col.substring(0, _ind) : col;
					//check if column name contains "{additional_title}" to set uniqueness for title
					int _bInd = _colNameWithoutSmart.indexOf("{");
					int _eInd = _colNameWithoutSmart.indexOf("}");
					String _unqTitle = _colNameWithoutSmart;
					if (_bInd > -1 && _eInd > 0) {
						String _unqPart = _colNameWithoutSmart.substring(_bInd + 1, _eInd);
						_unqTitle = _colNameWithoutSmart.replace("{" + _unqPart + "}", _unqPart);
						col = col.replace("{" + _unqPart + "}", "");
						System.out.println(_unqTitle);
					}

					col = col.replaceAll(".*@PIVOT_GROUP@", "");
					_unqTitle = _unqTitle.replaceAll(".*@PIVOT_GROUP@", "");
					json.put("title", col);
					json.put("uniqueTitle", _unqTitle);
					JSONArrayOfColumns.put(json);
				}
			} else {
				json = new JSONObject();
				json.put("showPageFilter", "0");
				json.put("title", "");
				json.put("uniqueTitle", "");
				JSONArrayOfColumns.put(json);
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return JSONArrayOfColumns;
	}

	private JSONArray getJSONArrayOfData(List<Map<String, Object>> rows, List<String> dateList) {
		boolean isDateExists = (dateList.isEmpty()) ? false : true;
		JSONArray JSONArrayOfData = null;
		List<List<String>> ListOfList = new ArrayList<List<String>>();
		/* pattern to find '<' sign with non-digit signs combination and replace it with his html code '&lt;';
		 * code added because of issue in datatable to render '<' sign as cell value
		 */
		Pattern r = Pattern.compile("<[^0-9]+");
		Matcher m = null;
		List<String> rowList;
		String value;
		int rowIndex = 0;
		try {
			for (Map<String, Object> row : rows) {
				rowList = new ArrayList<String>();
				for (Entry<String, Object> entry : row.entrySet()) {
					value = (entry.getValue() == null) ? "" : entry.getValue().toString();
					if ((isDateExists) && (!value.equals(""))) {
						for (String date : dateList) {
							if (entry.getKey().equals(date)) {
								SimpleDateFormat fromUser, myFormat;
								if (entry.getKey().contains("_SMARTTIME")) {
									fromUser = new SimpleDateFormat(
											generalUtil.getSelectDateQueryDateFormat() + " HH:mm", Locale.ENGLISH);
									myFormat = new SimpleDateFormat(generalUtil.getUserDateFormatServer() + " HH:mm",
											Locale.ENGLISH);
								} else {
									fromUser = new SimpleDateFormat(generalUtil.getSelectDateQueryDateFormat(),
											Locale.ENGLISH);
									myFormat = new SimpleDateFormat(generalUtil.getUserDateFormatServer(),
											Locale.ENGLISH);
								}
								value = myFormat.format(fromUser.parse(value));
								break;
							}
						}
					}
					if (entry.getKey().contains("_SMARTDATE")) {
						String displayName = generalUtil.getJsonValById(value, "displayName");
						if (!displayName.equals("")) {
							SimpleDateFormat fromUser = new SimpleDateFormat(generalUtil.getConversionDateFormat(),
									Locale.ENGLISH);
							SimpleDateFormat myFormat = new SimpleDateFormat(generalUtil.getUserDateFormatServer(),
									Locale.ENGLISH);
							String convertedValue = myFormat.format(fromUser.parse(displayName));

							value = generalUtil.updateJsonValById(value, "displayName", convertedValue);
						}
					}
					if (entry.getKey().contains("_SMARTRANGE") || entry.getKey().contains("_SMARTICON")) {
						m = r.matcher(value);
						while (m.find()) {
							value = generalUtil.getNull(value).replace("<", "&lt;");
						}
					}
					if (value.contains("displayPath")) {
						String delimiter = "/";
						/*String path = generalUtil.getJsonValById(value, "displayPath");
						String displayName = generalUtil.getJsonValById(value, "displayName");*/
						String path = value.substring(value.indexOf("{[") - 1, value.indexOf("]}") + 3);
						value = value.substring(0, value.indexOf("displayPath") - 2)
								+ value.substring(value.indexOf("]}") + 3, value.length());
						String displayName = generalUtil.getJsonValById(value, "displayName");/**/
						String pathToDisplay = displayName + delimiter
								+ generalUtil.showFormPathDisplay(path, delimiter);
						value = value.replace(displayName, pathToDisplay);
					}
					rowList.add(value);
				}
				ListOfList.add(rowList);

				if (Integer.parseInt(dataTableTopRowsNum) != -1
						&& ++rowIndex >= Integer.parseInt(dataTableTopRowsNum)) {
					break;
				}
			}

			JSONArrayOfData = new JSONArray(ListOfList);
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return JSONArrayOfData;
	}

	//return sql with error message for the data table
	private String jsonSqlErrorMsg(String msg) {
		// TODO Auto-generated method stub
		return "select -1 as ID, '" + msg + "' as \"SQL Message\" from dual";
	}

	/**
	 * 
	 * @param name_ colum name
	 * @param ja_ JSONArray contains previous column name in the pivot data row
	 * @return unique column name 
	 */
	private String evalPivotColumnName(String name_, JSONArray ja_) {
		int counter = 0;
		for (int i = 0; i < ja_.length(); i++) {
			String colName_ = ja_.getJSONObject(i).get("origin_column").toString();
			if (colName_.equals(name_)) {
				counter++;
			}
		}
		if (counter != 0) {
			if (name_ != null && name_.contains("_SMART")) {
				name_ = name_.replace("_SMART", "." + counter + "_SMART");
			} else {
				name_ += "." + counter;
			}
		}
		return name_;
	}

}