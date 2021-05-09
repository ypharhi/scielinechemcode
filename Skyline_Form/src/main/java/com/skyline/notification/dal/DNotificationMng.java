package com.skyline.notification.dal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.general.bean.DataTableParamModel;

import oracle.jdbc.OracleTypes;

@Repository("DNotificationMng")
public class DNotificationMng /* extends BaseDAL */
{
	//private LogWriter LG = null;
	private Connection _conn;
	
	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	@Autowired
	private GeneralUtil generalUtil;

	@Value("${jdbc.username}")
	private String _conn_Usr;
	@Value("${jdbc.url}")
	private String _connUrl;
	@Value("${jdbc.password}")
	private String _connPass;

	public static final int UniqueConstraintORA00001 = 1;

	public DNotificationMng() {
		super();
	}

	/*
	 * public DNotificationMng(String conURL,String conUSER,String conPASS, String path) { setConnUrl(conURL); setConn_Usr(conUSER); setConnPass(conPASS); //LG = new LogWriter(path); }
	 */

	/*
	 * private void setConnUrl(String conURL) { _connUrl = conURL; } private void setConn_Usr(String conUSER) { _conn_Usr = conUSER; } private void setConnPass(String conPASS) { _connPass = conPASS; }
	 */

	/* Table data */
	public ResultSet getNotificationList(int activeStatus) {
		CallableStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		Connection conn = getConnection();

		try {
			sql = "call SP_GET_NOTIFICATION_LIST(" + generalUtil.formatParamInSigns(2) + ")";
			stmt = conn.prepareCall(sql);
			stmt.setInt(1, activeStatus);
			stmt.registerOutParameter(2, OracleTypes.CURSOR);
			stmt.execute();
			rs = (ResultSet) stmt.getObject(2);
		} catch (Exception sqlEx) {
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		}

		return rs;
	}

	/**
	 * Note: functions getConnection and disconnect are taken from Skyline class BaseDAL
	 * @return
	 */
	public Connection getConnection() {
		try {
			if (_conn == null || _conn.isClosed()) {
				//String classpath = System.getProperty("java.class.path");
				Class.forName("oracle.jdbc.OracleDriver");
				_conn = DriverManager.getConnection(_connUrl, _conn_Usr, _connPass);
			}
			try {
				_conn.prepareStatement("alter session set nls_sort=binary_ci").execute();
				_conn.prepareStatement("alter session set nls_date_language=american").execute();
			} catch (Exception e) {
				System.out.println("");
				generalUtilLogger.logWrite(e);
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
			generalUtilLogger.logWrite(ex);
		}

		return _conn;
	}

	public void disconnect() {

		try {

			if (_conn != null) {
				_conn.close();
				_conn = null;
			}
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		}

	}

	/**
	 * Note: function getTableModel is taken from Skyline class Base BL
	 * @return
	 */
	public JSONObject getTableModel(List<JSONArray> arrList, final DataTableParamModel param) {
		String sEcho = param.sEcho;
		int iTotalRecords; // total number of records (unfiltered)
		int iTotalDisplayRecords; //value will be set when code filters levels by keyword
		JSONObject jsonResponse = null;

		try {
			List<JSONArray> resultsList = arrList;//data that will be shown in the table
			iTotalRecords = resultsList.size();
			List<JSONArray> currentList = new LinkedList<JSONArray>();

			if (param.sSearchKeyword != null && !param.sSearchKeyword.equals("")) {
				for (int i = 0; i < resultsList.size(); i++) {
					JSONArray row = resultsList.get(i);
					for (int j = 0; j < row.length(); j++) {
						if (param.bSearchable[j]
								&& row.optString(j).toLowerCase().contains(param.sSearchKeyword.toLowerCase())) {
							currentList.add(row);
							break;
						}
					}
				}
				resultsList = currentList;
				currentList = new LinkedList<JSONArray>();
			}

			if (resultsList.size() > 0) {
				for (int i = 0; i < param.sSearch.length; i++) {
					if ((param.sSearch[i] != null && !param.sSearch[i].equals("")) && param.bSearchable[i]) {
						for (int j = 0; j < resultsList.size(); j++) {
							JSONArray row = resultsList.get(j);
							if (row.optString(i).toLowerCase().contains(param.sSearch[i].toLowerCase())) {
								currentList.add(row);
							}
						}
						resultsList = currentList;
						currentList = new LinkedList<JSONArray>();
						//System.out.println(resultsList.toString());
					}
				}
			}

			iTotalDisplayRecords = resultsList.size();// number of rows that match search criterion should be returned

			Collections.sort(resultsList, new Comparator<JSONArray>() {
				@Override
				public int compare(JSONArray obj1, JSONArray obj2) {
					int result = 0;
					String dateFormat;
					for (int i = 0; i < param.iSortingCols; i++) {
						int sortBy = param.iSortCol[i];
						if (param.bSortable[sortBy]) {
							try //string, numeric, date, double
							{
								if (param.sSortType[i] != null && !param.sSortType[i].equals("")) {
									if (param.sSortType[i].equals("numeric")) {
										result = new Integer(obj1.optInt(sortBy))
												.compareTo(new Integer(obj2.optInt(sortBy)))
												* (param.sSortDir[i].equals("asc") ? -1 : 1);
										break;
									} else if (param.sSortType[i].equals("date")) {
										dateFormat = (param.sDateFormat[i].length() == 0) ? param.defaultDateFormat
												: param.sDateFormat[i];
										if (obj1.optString(sortBy).equals("") || obj2.optString(sortBy).equals("")) {
											result = obj1.optString(sortBy).compareToIgnoreCase(obj2.optString(sortBy))
													* (param.sSortDir[i].equals("asc") ? -1 : 1);
										} else {
											try {
												result = new SimpleDateFormat(dateFormat).parse(obj1.optString(sortBy))
														.compareTo(new SimpleDateFormat(dateFormat)
																.parse(obj2.optString(sortBy)))
														* (param.sSortDir[i].equals("asc") ? -1 : 1);
											} catch (Exception e) {
												System.out.println("BaseBL Exception:  " + e.getMessage());
												generalUtilLogger.logWrite(e);
											}
										}
										break;
									} else if (param.sSortType[i].equals("double")) {
										result = new Double(obj1.optDouble(sortBy))
												.compareTo(new Double(obj2.optDouble(sortBy)))
												* (param.sSortDir[i].equals("asc") ? -1 : 1);
										break;
									} else {
										result = obj1.optString(sortBy).compareToIgnoreCase(obj2.optString(sortBy))
												* (param.sSortDir[i].equals("asc") ? -1 : 1);
										break;
									}
								} else {
									result = obj1.optString(sortBy).compareToIgnoreCase(obj2.optString(sortBy))
											* (param.sSortDir[i].equals("asc") ? -1 : 1);
									break;
								}

							} catch (Exception e) {
								System.out.println(e.getMessage());
								generalUtilLogger.logWrite(e);
							}
						}
						if (result != 0)
							return result;
						else
							continue;
					}
					return result;
				}
			});

			/** case table use scroll iDisplayLength = -1 **/
			if (param.iDisplayLength != -1) {
				if (resultsList.size() < param.iDisplayStart + param.iDisplayLength) {
					resultsList = resultsList.subList(param.iDisplayStart, resultsList.size());
				} else {
					resultsList = resultsList.subList(param.iDisplayStart, param.iDisplayStart + param.iDisplayLength);
				}
			}

			System.out.println("resultsList.toString(): " + resultsList.toString());

			jsonResponse = new JSONObject();
			jsonResponse.put("aaData", resultsList);
			jsonResponse.put("sEcho", sEcho);
			jsonResponse.put("iTotalRecords", iTotalRecords);
			jsonResponse.put("iTotalDisplayRecords", iTotalDisplayRecords);

			System.out.println("jsonResponse.toString(): " + jsonResponse.toString());
		} catch (Exception e) {
			System.out.println("BaseBL.getTableModel(): " + e.toString());
			generalUtilLogger.logWrite(e);
		}
		return jsonResponse;
	}

	public String deleteNotification(int notificationID, int userID, String comment) {
		String sql = "";
		Connection con = null;
		CallableStatement stmt = null;
		String returnValue;

		try {
			con = getConnection();
			sql = " call SP_DEL_NOTIFICATION(" + generalUtil.formatParamInSigns(3) + ")";
			stmt = con.prepareCall(sql);
			stmt.setInt(1, notificationID);
			stmt.setInt(2, userID);
			stmt.setString(3, comment);

			stmt.execute();
			returnValue = "0";
		} catch (Exception sqlEx) {
			returnValue = "-1";
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				disconnect();
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
				// do nothing
			}
		}
		return returnValue;
	}

	/* DDL data */
	public ResultSet getNotificationModuleList() {
		CallableStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		Connection conn = getConnection();

		try {
			sql = "call SP_GET_NOTIFICATION_MOD_LIST(" + generalUtil.formatParamInSigns(1) + ")";
			stmt = conn.prepareCall(sql);
			stmt.registerOutParameter(1, OracleTypes.CURSOR);
			stmt.execute();
			rs = (ResultSet) stmt.getObject(1);
		} catch (Exception sqlEx) {
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		}

		return rs;
	}

	public ResultSet getNotificationData(int notificationID) {
		CallableStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		Connection conn = null;

		try {
			conn = getConnection();
			sql = "call SP_GET_NOTIFICATION_DATA(" + generalUtil.formatParamInSigns(2) + ")";
			stmt = conn.prepareCall(sql);
			stmt.setInt(1, notificationID);
			stmt.registerOutParameter(2, OracleTypes.CURSOR);
			stmt.execute();
			rs = (ResultSet) stmt.getObject(2);
		} catch (Exception sqlEx) {
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		}

		return rs;
	}

	/* DDL data */
	public ResultSet getNotificationMessageTypeList() {
		CallableStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		Connection conn = getConnection();

		try {
			sql = "call SP_GET_NOTIF_MSG_TYPE_LIST(" + generalUtil.formatParamInSigns(1) + ")";
			stmt = conn.prepareCall(sql);
			stmt.registerOutParameter(1, OracleTypes.CURSOR);
			stmt.execute();
			rs = (ResultSet) stmt.getObject(1);
		} catch (Exception sqlEx) {
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		}

		return rs;
	}

	/* DDL data */
	public ResultSet getNotificationSchedulerList() {
		CallableStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		Connection conn = getConnection();

		try {
			sql = "call SP_GET_NOTIFICATION_SCHEDULERS(" + generalUtil.formatParamInSigns(1) + ")";
			stmt = conn.prepareCall(sql);
			stmt.registerOutParameter(1, OracleTypes.CURSOR);
			stmt.execute();
			rs = (ResultSet) stmt.getObject(1);
		} catch (Exception sqlEx) {
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		}

		return rs;
	}
	
	/* DDL data */
	public ResultSet getNotificationTriggerTypeList() {
		CallableStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		Connection conn = getConnection();

		try {
			sql = "call SP_GET_NOTIFICATION_TRIG_TYPE(" + generalUtil.formatParamInSigns(1) + ")";
			stmt = conn.prepareCall(sql);
			stmt.registerOutParameter(1, OracleTypes.CURSOR);
			stmt.execute();
			rs = (ResultSet) stmt.getObject(1);
		} catch (Exception sqlEx) {
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		}

		return rs;
	}
	
	/* DDL data */
	public ResultSet getNotificationOnSave() {
		CallableStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		Connection conn = getConnection();

		try {
			sql = "call SP_GET_NOTIFICATION_ON_SAVE(" + generalUtil.formatParamInSigns(1) + ")";
			stmt = conn.prepareCall(sql);
			stmt.registerOutParameter(1, OracleTypes.CURSOR);
			stmt.execute();
			rs = (ResultSet) stmt.getObject(1);
		} catch (Exception sqlEx) {
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		}

		return rs;
	}

	public String saveNewNotification(int moduleID, int messageTypeID, int colNumber, String description,
			int schedulerID, int triggerTypeID, int onSaveID, int interval, int resend, int active, int addAttachment, String userID, int copyToProduction) {
		CallableStatement stmt = null;
		String sql = "";
		String returnValue = "";
		Connection conn = null;

		try {
			conn = getConnection();
			sql = "call SP_INSERT_NEW_NOTIFICATION(" + generalUtil.formatParamInSigns(14) + ")";
			stmt = conn.prepareCall(sql);
			stmt.setInt(1, moduleID);
			stmt.setInt(2, messageTypeID);
			stmt.setInt(3, colNumber);
			stmt.setString(4, description);
			stmt.setInt(5, schedulerID);
			stmt.setInt(6, triggerTypeID);
			stmt.setInt(7, onSaveID);
			stmt.setInt(8, interval);
			stmt.setInt(9, resend);
			stmt.setInt(10, active);
			stmt.setInt(11, addAttachment);
			stmt.setString(12, userID);
			stmt.setInt(13, copyToProduction);
			stmt.registerOutParameter(14, java.sql.Types.INTEGER);
			stmt.execute();

			returnValue = String.valueOf(stmt.getInt(14));
		} catch (Exception sqlEx) {
			returnValue = "-1";
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}

				disconnect();
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
				// do nothing
			}
		}
		return returnValue;
	}

	public String updateNotification(int notificationID, int moduleID, int messageTypeID, int colNumber,
			String description, int schedulerID, int triggerTypeID, int onSaveID, int interval, int resend, int active, int addAttachment,
			String userID, int copyToProduction) {
		CallableStatement stmt = null;
		String sql = "";
		String returnValue = "0";
		Connection conn = null;

		try {
			conn = getConnection();
			sql = "call SP_UPDATE_NOTIFICATION(" + generalUtil.formatParamInSigns(14) + ")";
			stmt = conn.prepareCall(sql);
			stmt.setInt(1, notificationID);
			stmt.setInt(2, moduleID);
			stmt.setInt(3, messageTypeID);
			stmt.setInt(4, colNumber);
			stmt.setString(5, description);
			stmt.setInt(6, schedulerID);
			stmt.setInt(7, triggerTypeID);
			stmt.setInt(8, onSaveID);
			stmt.setInt(9, interval);
			stmt.setInt(10, resend);
			stmt.setInt(11, active);
			stmt.setInt(12, addAttachment);
			stmt.setString(13, userID);
			stmt.setInt(14, copyToProduction);
			stmt.execute();
		} catch (Exception sqlEx) {
			returnValue = "-1";
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}

				disconnect();
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
				// do nothing
			}
		}
		return returnValue;
	}

	public ResultSet getNotificationAvailableFields(int notificationID) {
		CallableStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		Connection conn = null;

		try {
			conn = getConnection();
			sql = "call SP_GET_NOTIFICATION_AVL_FIELDS(" + generalUtil.formatParamInSigns(2) + ")";
			stmt = conn.prepareCall(sql);
			stmt.setInt(1, notificationID);
			stmt.registerOutParameter(2, OracleTypes.CURSOR);
			stmt.execute();
			rs = (ResultSet) stmt.getObject(2);
		} catch (Exception sqlEx) {
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		}

		return rs;
	}

	public String updateNotificationMessage(int notificationID, String subject, String msgBody, String userID) {
		CallableStatement stmt = null;
		String sql = "";
		String returnValue = "0";
		Connection conn = null;

		try {
			conn = getConnection();
			sql = "call SP_UPDATE_NOTIFICATION_MESSAGE(" + generalUtil.formatParamInSigns(4) + ")";
			stmt = conn.prepareCall(sql);
			stmt.setInt(1, notificationID);
			stmt.setString(2, subject);
			stmt.setString(3, msgBody);
			stmt.setString(4, userID);
			stmt.execute();
		} catch (Exception sqlEx) {
			returnValue = "-1";
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}

				disconnect();
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
				// do nothing
			}
		}
		return returnValue;
	}

	/* Table data */
	public ResultSet getNotificationConditionList(int notificationID) {
		CallableStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		Connection conn = getConnection();

		try {
			sql = "call SP_GET_NOTIFICATION_CONDITIONS(" + generalUtil.formatParamInSigns(2) + ")";
			stmt = conn.prepareCall(sql);
			stmt.setInt(1, notificationID);
			stmt.registerOutParameter(2, OracleTypes.CURSOR);
			stmt.execute();
			rs = (ResultSet) stmt.getObject(2);
		} catch (Exception sqlEx) {
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		}

		return rs;
	}

	public ResultSet getNotificationConditionOperators(int fieldID) {
		CallableStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		Connection conn = getConnection();

		try {
			sql = "call SP_GET_NOTIFICATION_OPERATORS(" + generalUtil.formatParamInSigns(2) + ")";
			stmt = conn.prepareCall(sql);
			stmt.setInt(1, fieldID);
			stmt.registerOutParameter(2, OracleTypes.CURSOR);
			stmt.execute();
			rs = (ResultSet) stmt.getObject(2);
		} catch (Exception sqlEx) {
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		}

		return rs;
	}

	public String addNewCondition(int notificationID, String conditionType, int fieldID, int operatorID,
			String conditionValue, String userID) {
		CallableStatement stmt = null;
		String sql = "";
		String returnValue = "";
		Connection conn = null;

		try {
			conn = getConnection();
			sql = "call SP_NEW_NOTIFICATION_CONDITION(" + generalUtil.formatParamInSigns(7) + ")";
			stmt = conn.prepareCall(sql);
			stmt.setInt(1, notificationID);
			stmt.setString(2, conditionType);
			stmt.setInt(3, fieldID);
			stmt.setInt(4, operatorID);
			stmt.setString(5, conditionValue);
			stmt.setString(6, userID);
			stmt.registerOutParameter(7, java.sql.Types.INTEGER);
			stmt.execute();

			returnValue = String.valueOf(stmt.getInt(7));
		} catch (Exception sqlEx) {
			returnValue = "-1";
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}

				disconnect();
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
				// do nothing
			}
		}
		return returnValue;
	}

	public String updateNotificationCondition(int notificationID, int conditionID, String conditionType, int fieldID,
			int operatorID, String conditionValue, String userID) {
		CallableStatement stmt = null;
		String sql = "";
		String returnValue = "0";
		Connection conn = null;

		try {
			conn = getConnection();
			sql = "call SP_EDIT_NOTIFICATION_CONDITION(" + generalUtil.formatParamInSigns(7) + ")";
			stmt = conn.prepareCall(sql);
			stmt.setInt(1, notificationID);
			stmt.setInt(2, conditionID);
			stmt.setString(3, conditionType);
			stmt.setInt(4, fieldID);
			stmt.setInt(5, operatorID);
			stmt.setString(6, conditionValue);
			stmt.setString(7, userID);
			stmt.execute();
		} catch (Exception sqlEx) {
			returnValue = "-1";
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}

				disconnect();
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
				// do nothing
			}
		}
		return returnValue;
	}

	public String deleteNotificationCondition(int notificationID, int conditionID, String userID, String comment) {
		CallableStatement stmt = null;
		String sql = "";
		String returnValue = "0";
		Connection conn = null;

		try 
		{
			conn = getConnection();
			sql = "call SP_DEL_NOTIFICATION_CONDITION(" + generalUtil.formatParamInSigns(4) + ")";
			stmt = conn.prepareCall(sql);
			stmt.setInt(1, notificationID);
			stmt.setInt(2, conditionID);
			stmt.setString(3, userID);
			stmt.setString(4, comment);
			stmt.execute();
		} catch (Exception sqlEx) 
		{
			returnValue = "-1";
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}

				disconnect();
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
				// do nothing
			}
		}
		return returnValue;
	}

	/* Table data */
	public ResultSet getNotificationAddresseeList(int notificationID) {
		CallableStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		Connection conn = getConnection();

		try {
			sql = "call SP_GET_NOTIFICATION_ADDRESSEES(" + generalUtil.formatParamInSigns(2) + ")";
			stmt = conn.prepareCall(sql);
			stmt.setInt(1, notificationID);
			stmt.registerOutParameter(2, OracleTypes.CURSOR);
			stmt.execute();
			rs = (ResultSet) stmt.getObject(2);
		} catch (Exception sqlEx) {
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		}

		return rs;
	}

	public ResultSet getAddressTypeList() {
		CallableStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		Connection conn = getConnection();

		try {
			sql = "call SP_GET_NOTIFICATION_ADDR_TYPES(" + generalUtil.formatParamInSigns(1) + ")";
			stmt = conn.prepareCall(sql);
			stmt.registerOutParameter(1, OracleTypes.CURSOR);
			stmt.execute();
			rs = (ResultSet) stmt.getObject(1);
		} catch (Exception sqlEx) {
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		}

		return rs;
	}

	public ResultSet getAddresseeUserList(int notificationID, int addressTypeID) {
		CallableStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		Connection conn = getConnection();

		try {
			sql = "call SP_GET_NOTIFICATION_USERS_LIST(" + generalUtil.formatParamInSigns(3) + ")";
			stmt = conn.prepareCall(sql);
			stmt.setInt(1, notificationID);
			stmt.setInt(2, addressTypeID);
			stmt.registerOutParameter(3, OracleTypes.CURSOR);
			stmt.execute();
			rs = (ResultSet) stmt.getObject(3);
		} catch (Exception sqlEx) {
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		}

		return rs;
	}

	public String deleteNotificationAddressee(int addresseeID, String userID, String comment) {
		CallableStatement stmt = null;
		String sql = "";
		String returnValue = "0";
		Connection conn = null;

		try {
			conn = getConnection();
			sql = "call SP_DEL_NOTIFICATION_ADDRESSEE(" + generalUtil.formatParamInSigns(3) + ")";
			stmt = conn.prepareCall(sql);
			stmt.setInt(1, addresseeID);
			stmt.setString(2, userID);
			stmt.setString(3, comment);
			stmt.execute();
		} catch (Exception sqlEx) {
			returnValue = "-1";
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}

				disconnect();
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
				// do nothing
			}
		}
		return returnValue;
	}

	public String updateNotificationAddressee(int addresseeID, String sendType, int addressTypeID, int addrUserID,
			String userID, String addrExternal, int messageOnly) {
		CallableStatement stmt = null;
		String sql = "";
		String returnValue = "0";
		Connection conn = null;

		try {
			conn = getConnection();
			sql = "call SP_EDIT_NOTIFICATION_ADDRESSEE(" + generalUtil.formatParamInSigns(7) + ")";
			stmt = conn.prepareCall(sql);
			stmt.setInt(1, addresseeID);
			stmt.setString(2, sendType);
			stmt.setInt(3, addressTypeID);
			stmt.setInt(4, addrUserID);
			stmt.setString(5, userID);
			stmt.setString(6, addrExternal);
			stmt.setInt(7, messageOnly);
			stmt.execute();
		} catch (Exception sqlEx) {
			returnValue = "-1";
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}

				disconnect();
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
				// do nothing
			}
		}
		return returnValue;
	}

	public String addNewAddressee(int notificationID, String sendType, int addressTypeID, int addrUserID,
			String userID, String addrExternal, int messageOnly) {
		CallableStatement stmt = null;
		String sql = "";
		String returnValue = "0";
		Connection conn = null;

		try {
			conn = getConnection();
			sql = "call SP_NEW_NOTIFICATION_ADDRESSEE(" + generalUtil.formatParamInSigns(7) + ")";
			stmt = conn.prepareCall(sql);
			stmt.setInt(1, notificationID);
			stmt.setString(2, sendType);
			stmt.setInt(3, addressTypeID);
			stmt.setInt(4, addrUserID);
			stmt.setString(5, userID);
			stmt.setString(6, addrExternal);
			stmt.setInt(7, messageOnly);
			stmt.execute();
		} catch (Exception sqlEx) {
			returnValue = "-1";
			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(sqlEx);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}

				disconnect();
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
				// do nothing
			}
		}
		return returnValue;
	}
}