package com.skyline.notification.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.general.bean.ComboBasic;
import com.skyline.general.bean.DataTableParamModel;
import com.skyline.notification.bean.Notification;
import com.skyline.notification.bean.NotificationAddressee;
import com.skyline.notification.bean.NotificationAvailableField;
import com.skyline.notification.bean.NotificationCondition;
import com.skyline.notification.bean.NotificationMessageType;
import com.skyline.notification.bean.NotificationModule;
import com.skyline.notification.bean.NotificationOnSave;
import com.skyline.notification.bean.NotificationScheduler;
import com.skyline.notification.bean.NotificationTriggerType;
import com.skyline.notification.dal.DNotificationMng;

@Service
public class NotificationManagement /*extends BaseBL*/{

	@Autowired
	private GeneralUtil generalUtil;
	
	@Autowired
	public GeneralUtilLogger generalUtilLogger;
	
	private int tableRowNumber = 0;
	
	JSONObject jsonResponse = null;
	
	@Autowired
    private DNotificationMng dNotification;
	
	public JSONObject getNotificationListTable(final DataTableParamModel param, int activeStatus, int selectedRowID)
			throws JSONException {
		List<JSONArray> data = new LinkedList<JSONArray>(); // data that will be
															// shown in the
															// table
		List<Notification> results = getNotificationArray(activeStatus, selectedRowID);

		for (Notification notif : results) {
			JSONArray row = new JSONArray();
			row.put(notif.NOTIFICATION_ID).put(notif.MODULE_NAME).put(notif.DESCRIPTION).put(notif.IS_ACTIVE)
					.put(notif.notificationRowOrder);
			data.add(row);
		}

		jsonResponse = dNotification.getTableModel(data, param);
		dNotification.disconnect();

		return jsonResponse;
	}

	public List<Notification> getNotificationArray(int activeStatus, int idForSelect) {
		List<Notification> notificationList = new LinkedList<Notification>();
		ResultSet rs = null;
		int i = 0;

		try {
			rs = dNotification.getNotificationList(activeStatus);
			while (rs.next()) {
				Notification notification = new Notification();
				notification.NOTIFICATION_ID = rs.getInt("NOTIFICATION_ID");
				notification.MODULE_NAME = generalUtil.getNull(rs.getString("MODULE_NAME"));
				notification.MODULE_ID = rs.getInt("MODULE_ID");
				notification.DESCRIPTION = generalUtil.getNull(rs.getString("DESCRIPTION"));
				notification.IS_ACTIVE = rs.getInt("ISACTIVE");

				notification.notificationRowOrder = i;
				if (notification.NOTIFICATION_ID == idForSelect) {
					tableRowNumber = i;
				}
				i++;

				notificationList.add(notification);
			}
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqlEx) {
				System.out.println(sqlEx.toString());
				generalUtilLogger.logWrite(sqlEx);
			}
		}

		return notificationList;
	}

	public String deleteNotification(int notificationID, int userID, String comment) {
		String retVal;
		retVal = dNotification.deleteNotification(notificationID, userID, comment);
		return retVal;
	}

	public List<NotificationModule> getNotificationModuleArray() {
		List<NotificationModule> moduleList = new LinkedList<NotificationModule>();
		ResultSet rs = null;
		int i = 0;

		try {
			rs = dNotification.getNotificationModuleList();
			while (rs.next()) {
				NotificationModule module = new NotificationModule();
				module.MODULE_ID = rs.getInt("MODULE_ID");
				module.MODULE_NAME = generalUtil.getNull(rs.getString("MODULE_NAME"));
				moduleList.add(module);
			}
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqlEx) {
				System.out.println(sqlEx.toString());
				generalUtilLogger.logWrite(sqlEx);
			}
		}

		return moduleList;
	}

	public Notification getNotificationInfo(int notificationID) {
		Notification notification = new Notification();
		ResultSet rs = null;

		try {
			rs = dNotification.getNotificationData(notificationID);
			if (rs.next()) {
				notification.NOTIFICATION_ID = rs.getInt("NOTIFICATION_ID");
				notification.MODULE_ID = rs.getInt("MODULE_ID");
				notification.MESSAGE_TYPE_ID = rs.getInt("MESSAGE_TYPE_ID");
				notification.EMAIL_SUBJECT = rs.getString("EMAIL_SUBJECT");
				notification.DESCRIPTION = rs.getString("DESCRIPTION");
				notification.SCHEDULER_ID = rs.getInt("SCHEDULER_ID");
				notification.TRIGGER_TYPE_ID = rs.getInt("TRIGGER_TYPE_ID");
				notification.SCHEDULER_NAME = rs.getString("SCHEDULER_NAME");
				notification.ON_SAVE_ID = rs.getInt("ON_SAVE_ID");
				notification.SCHEDULER_INTERVAL = rs.getInt("SCHEDULER_INTERVAL");
				notification.RESEND = rs.getInt("RESEND");
				notification.IS_ACTIVE = rs.getInt("ISACTIVE");
				notification.COPY_TO_PRODUCTION = rs.getInt("COPY_TO_PRODUCTION");
				notification.INCLUDE_ATTACHMENT = rs.getInt("ADD_ATTACHMENTS");
				notification.EMAIL_BODY = rs.getString("EMAIL_BODY");
				notification.COLUMN_NUMBER = rs.getInt("COLUMN_NUMBER");
			}
		} catch (Exception ex) {
			System.out.println("NotificationManagement.getNotificationInfo(): " + ex.toString());
			generalUtilLogger.logWrite(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				dNotification.disconnect();
			} catch (SQLException sqlEx) {
				System.out.println(sqlEx.toString());
				generalUtilLogger.logWrite(sqlEx);
			}
		}

		return notification;
	}

	public List<NotificationMessageType> getNotificationMessageTypeArray() {
		List<NotificationMessageType> messageTypeList = new LinkedList<NotificationMessageType>();
		ResultSet rs = null;
		int i = 0;

		try {
			rs = dNotification.getNotificationMessageTypeList();
			while (rs.next()) {
				NotificationMessageType messageType = new NotificationMessageType();
				messageType.MESSAGE_TYPE_ID = rs.getInt("MESSAGE_TYPE_ID");
				messageType.MESSAGE_TYPE_NAME = generalUtil.getNull(rs.getString("MESSAGE_TYPE_NAME"));
				messageType.MESSAGE_TEMPLATE = generalUtil.getNull(rs.getString("MESSAGE_TEMPLATE"));
				messageTypeList.add(messageType);
			}
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqlEx) {
				System.out.println(sqlEx.toString());
				generalUtilLogger.logWrite(sqlEx);
			}
		}

		return messageTypeList;
	}

	public List<NotificationScheduler> getNotificationSchedulerArray() {
		List<NotificationScheduler> schedulerList = new LinkedList<NotificationScheduler>();
		ResultSet rs = null;
		int i = 0;

		try {
			rs = dNotification.getNotificationSchedulerList();
			while (rs.next()) {
				NotificationScheduler scheduler = new NotificationScheduler();
				scheduler.SCHEDULER_ID = rs.getInt("SCHEDULER_ID");
				scheduler.SCHEDULER_NAME = generalUtil.getNull(rs.getString("SCHEDULER_NAME"));
				scheduler.HOUR_INTERVAL = rs.getInt("HOUR_INTERVAL");
				schedulerList.add(scheduler);
			}
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqlEx) {
				System.out.println(sqlEx.toString());
				generalUtilLogger.logWrite(sqlEx);
			}
		}
		return schedulerList;
	}
	
	public List<NotificationTriggerType> getNotificationTriggerTypeArray() {
		List<NotificationTriggerType> triggerTypeList = new LinkedList<NotificationTriggerType>();
		ResultSet rs = null;
		
		try {
			rs = dNotification.getNotificationTriggerTypeList();
			while (rs.next()) {
				NotificationTriggerType triggerType = new NotificationTriggerType();
				triggerType.TRIGGER_TYPE_ID = rs.getInt("TRIGGER_TYPE_ID");
				triggerType.TRIGGER_TYPE_NAME = generalUtil.getNull(rs.getString("TRIGGER_TYPE_NAME"));
				triggerTypeList.add(triggerType);
			}
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqlEx) {
				System.out.println(sqlEx.toString());
				generalUtilLogger.logWrite(sqlEx);
			}
		}
		return triggerTypeList;
	}
	
	public List<NotificationOnSave> getNotificationOnSaveArray() {
		List<NotificationOnSave> onSaveList = new LinkedList<NotificationOnSave>();
		ResultSet rs = null;
		int i = 0;

		try {
			rs = dNotification.getNotificationOnSave();
			while (rs.next()) {
				NotificationOnSave onSave = new NotificationOnSave();
				onSave.ON_SAVE_ID = rs.getInt("ON_SAVE_ID");
				onSave.ON_SAVE_NAME = generalUtil.getNull(rs.getString("ON_SAVE_NAME"));
				onSaveList.add(onSave);
			}
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqlEx) {
				System.out.println(sqlEx.toString());
				generalUtilLogger.logWrite(sqlEx);
			}
		}
		return onSaveList;
	}

	public String addNewNotification(int moduleID, int messageTypeID, int colNumber, String description,
			int schedulerID, int triggerTypeID, int onSaveID, int interval, int resend, int active, int addAttachment, String userID, int copyToProduction) {
		String retVal;
		retVal = dNotification.saveNewNotification(moduleID, messageTypeID, colNumber, description, schedulerID, triggerTypeID, onSaveID,
				interval, resend, active, addAttachment, userID, copyToProduction);
		return retVal;
	}

	public String updateNotification(int notificationID, int moduleID, int messageTypeID, int colNumber,
			String description, int schedulerID, int triggerTypeID, int onSaveID, int interval, int resend, int active, int addAttachment,
			String userID, int copyToProduction) {
		String retVal;
		retVal = dNotification.updateNotification(notificationID, moduleID, messageTypeID, colNumber, description,
				schedulerID, triggerTypeID, onSaveID, interval, resend, active, addAttachment, userID, copyToProduction);
		return retVal;
	}

	public String updateNotificationMessage(int notificationID, String subject, String msgBodt, String userID) {
		String retVal;
		retVal = dNotification.updateNotificationMessage(notificationID, subject, msgBodt, userID);
		return retVal;
	}

	public List<NotificationAvailableField> getNotificationAvailableFieldArray(int notificationID) {
		List<NotificationAvailableField> fieldsList = new LinkedList<NotificationAvailableField>();
		ResultSet rs = null;

		try {
			rs = dNotification.getNotificationAvailableFields(notificationID);
			while (rs.next()) {
				NotificationAvailableField field = new NotificationAvailableField();
				field.FIELD_ID = rs.getInt("FIELD_ID");
				field.MODULE_ID = rs.getInt("MODULE_ID");
				field.DISPLAY_NAME = generalUtil.getNull(rs.getString("DATA_NAME"));
				field.DATA_TYPE = generalUtil.getNull(rs.getString("DATATYPE"));
				field.FIELD_NAME = generalUtil.getNull(rs.getString("ORIGINAL_FIELD_NAME"));
				fieldsList.add(field);
			}
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqlEx) {
				System.out.println(sqlEx.toString());
				generalUtilLogger.logWrite(sqlEx);
			}	
		}

		return fieldsList;
	}

	/* get table */
	public JSONObject getNotificationCriteriaTable(final DataTableParamModel param, int notificationID)
			throws JSONException {
		String sEcho = param.sEcho;
		int iTotalRecords; // total number of records (unfiltered)
		int iTotalDisplayRecords; // value will be set when code filters levels
									// by keyword
		JSONArray data = new JSONArray(); // data that will be shown in the
											// table

		List<NotificationCondition> results = getNotificationConditionArray(notificationID);
		iTotalRecords = results.size();
		iTotalDisplayRecords = results.size(); // number of rows that match
												// search criterion should be
												// returned

		if (results.size() < param.iDisplayStart + param.iDisplayLength) {
			results = results.subList(param.iDisplayStart, results.size());
		} else {
			results = results.subList(param.iDisplayStart, param.iDisplayStart + param.iDisplayLength);
		}

		try {
			jsonResponse = new JSONObject();
			jsonResponse.put("sEcho", sEcho);
			jsonResponse.put("iTotalRecords", iTotalRecords);
			jsonResponse.put("iTotalDisplayRecords", iTotalDisplayRecords);
			jsonResponse.put("iDisplayStart", param.iDisplayStart);

			for (NotificationCondition condition : results) {
				JSONArray row = new JSONArray();
				row.put(condition.CONDITION_ID).put(condition.CONDITION_TYPE).put(condition.FIELD_NAME)
						.put(condition.FIELD_ID).put(condition.OPERATOR).put(condition.OPERATOR_ID)
						.put(condition.VALUE);
				data.put(row);
			}

			jsonResponse.put("aaData", data);
			dNotification.disconnect();
		} catch (JSONException ex) {
			generalUtilLogger.logWrite(ex);
		}

		return jsonResponse;
	}

	public List<NotificationCondition> getNotificationConditionArray(int notificationID) {
		List<NotificationCondition> conditionList = new LinkedList<NotificationCondition>();
		ResultSet rs = null;

		try {
			rs = dNotification.getNotificationConditionList(notificationID);
			while (rs.next()) {
				NotificationCondition condition = new NotificationCondition();
				condition.CONDITION_ID = rs.getInt("CONDITION_ID");
				condition.CONDITION_TYPE = generalUtil.getNull(rs.getString("CONDITION_TYPE"));
				condition.FIELD_ID = rs.getInt("FIELD_ID");
				condition.FIELD_NAME = rs.getString("FIELD");
				condition.OPERATOR_ID = rs.getInt("OPERATOR_ID");
				condition.OPERATOR = generalUtil.getNull(rs.getString("OPERATOR"));
				condition.VALUE = rs.getString("CRITERIA_VALUE");

				conditionList.add(condition);
			}
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqlEx) {
				System.out.println(sqlEx.toString());
				generalUtilLogger.logWrite(sqlEx);
			}
		}

		return conditionList;
	}

	public List<ComboBasic> getNotificationConditionOperatorArray(int fieldID) {
		List<ComboBasic> fieldsList = new LinkedList<ComboBasic>();
		ResultSet rs = null;

		try {
			rs = dNotification.getNotificationConditionOperators(fieldID);
			while (rs.next()) {
				ComboBasic operator = new ComboBasic(1);

				operator.id = generalUtil.getNull(rs.getString("operator_id"));
				operator.text = generalUtil.getNull(rs.getString("CRITERIA"));
				operator.attributes[0] = rs.getString("VALUE_EXPECTED");

				fieldsList.add(operator);
			}
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqlEx) {
				System.out.println(sqlEx.toString());
				generalUtilLogger.logWrite(sqlEx);
			}
		}

		return fieldsList;
	}

	public String addNewCondition(int notificationID, String conditionType, int fieldID, int operatorID,
			String conditionValue, String userID) {
		String retVal;
		retVal = dNotification.addNewCondition(notificationID, conditionType, fieldID, operatorID, conditionValue,
				userID);
		return retVal;
	}

	public String updateNotificationCondition(int notificationID, int conditionID, String conditionType, int fieldID,
			int operatorID, String conditionValue, String userID) {
		String retVal;
		retVal = dNotification.updateNotificationCondition(notificationID, conditionID, conditionType, fieldID,
				operatorID, conditionValue, userID);
		return retVal;
	}

	public String deleteNotificationCondition(int notificationID, int conditionID, String userID, String comment) {
		String retVal;
		retVal = dNotification.deleteNotificationCondition(notificationID, conditionID, userID, comment);
		return retVal;
	}

	/* get table */
	public JSONObject getNotificationDistributionTable(final DataTableParamModel param, int notificationID)
			throws JSONException {
		String sEcho = param.sEcho;
		int iTotalRecords; // total number of records (unfiltered)
		int iTotalDisplayRecords; // value will be set when code filters levels
									// by keyword
		JSONArray data = new JSONArray(); // data that will be shown in the
											// table

		List<NotificationAddressee> results = getNotificationAddresseeArray(notificationID);
		iTotalRecords = results.size();
		iTotalDisplayRecords = results.size(); // number of rows that match
												// search criterion should be
												// returned

		Collections.sort(results, new Comparator<NotificationAddressee>() {
			@Override
			public int compare(NotificationAddressee addr1, NotificationAddressee addr2) {
				int result = 0;
				for (int i = 0; i < param.iSortingCols; i++) {
					int sortBy = param.iSortCol[i];
					if (param.bSortable[sortBy]) {
						switch (sortBy) {
						case 1:
							result = addr1.ADDRESS_TYPE_NAME.compareToIgnoreCase(addr2.ADDRESS_TYPE_NAME)
									* (param.sSortDir[i].equals("asc") ? -1 : 1);
							break;
						case 3:
							result = addr1.USER_NAME.compareToIgnoreCase(addr2.USER_NAME)
									* (param.sSortDir[i].equals("asc") ? -1 : 1);
							break;
						case 5:
							result = addr1.SEND_TYPE.compareToIgnoreCase(addr2.SEND_TYPE)
									* (param.sSortDir[i].equals("asc") ? -1 : 1);
							break;
						default:
							result = 0;
							break;
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

		if (results.size() < param.iDisplayStart + param.iDisplayLength) {
			results = results.subList(param.iDisplayStart, results.size());
		} else {
			results = results.subList(param.iDisplayStart, param.iDisplayStart + param.iDisplayLength);
		}

		try {
			jsonResponse = new JSONObject();
			jsonResponse.put("sEcho", sEcho);
			jsonResponse.put("iTotalRecords", iTotalRecords);
			jsonResponse.put("iTotalDisplayRecords", iTotalDisplayRecords);
			jsonResponse.put("iDisplayStart", param.iDisplayStart);

			for (NotificationAddressee addressee : results) {
				JSONArray row = new JSONArray();
				row.put(addressee.ADDRESSEE_ID).put(addressee.ADDRESS_TYPE_NAME).put(addressee.ADDRESS_TYPE_ID)
						.put(addressee.USER_NAME).put(addressee.USER_ID).put(addressee.SEND_TYPE).put(addressee.IS_MESSAGE_ONLY);
				data.put(row);
			}

			jsonResponse.put("aaData", data);
			dNotification.disconnect();
		} catch (JSONException ex) {
			generalUtilLogger.logWrite(ex);
		}

		return jsonResponse;
	}

	public List<NotificationAddressee> getNotificationAddresseeArray(int notificationID) {
		List<NotificationAddressee> addresseeList = new LinkedList<NotificationAddressee>();
		ResultSet rs = null;

		try {
			rs = dNotification.getNotificationAddresseeList(notificationID);
			while (rs.next()) {
				NotificationAddressee addressee = new NotificationAddressee();
				addressee.ADDRESSEE_ID = rs.getInt("NOTIFICATION_ADDRESSEE_ID");
				addressee.USER_ID = rs.getInt("USER_ID");
				addressee.USER_NAME = generalUtil.getNull(rs.getString("USER_NAME"));
				addressee.SEND_TYPE = generalUtil.getNull(rs.getString("SEND_TYPE"));
				addressee.ADDRESS_TYPE_NAME = generalUtil.getNull(rs.getString("ADDRESS_TYPE"));
				addressee.ADDRESS_TYPE_ID = rs.getInt("ADDRESSEE_TYPE_ID");
				addressee.IS_MESSAGE_ONLY = rs.getInt("IS_MESSAGE_ONLY");

				addresseeList.add(addressee);
			}
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqlEx) {
				System.out.println(sqlEx.toString());
				generalUtilLogger.logWrite(sqlEx);
			}
		}

		return addresseeList;
	}

	public List<ComboBasic> getAddressTypeArray() {
		List<ComboBasic> addressTypeList = new LinkedList<ComboBasic>();
		ResultSet rs = null;

		try {
			rs = dNotification.getAddressTypeList();
			while (rs.next()) {
				ComboBasic type = new ComboBasic();
				type.id = generalUtil.getNull(rs.getString("TYPE_ID"));
				type.text = generalUtil.getNull(rs.getString("TYPE_NAME"));

				addressTypeList.add(type);
			}
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqlEx) {
				System.out.println(sqlEx.toString());
				generalUtilLogger.logWrite(sqlEx);
			}
		}

		return addressTypeList;
	}

	public List<ComboBasic> getAddresseeUserArray(int notificationID, int addressTypeID) {
		List<ComboBasic> userList = new LinkedList<ComboBasic>();
		ResultSet rs = null;

		try {
			rs = dNotification.getAddresseeUserList(notificationID, addressTypeID);
			while (rs.next()) {
				ComboBasic user = new ComboBasic();
				user.id = generalUtil.getNull(rs.getString("USER_ID"));
				user.text = generalUtil.getNull(rs.getString("USER_FULL_NAME"));

				userList.add(user);
			}
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqlEx) {
				System.out.println(sqlEx.toString());
				generalUtilLogger.logWrite(sqlEx);
			}
		}

		return userList;
	}

	public String deleteNotificationAddressee(int addresseeID, String userID, String comment) {
		String retVal;
		retVal = dNotification.deleteNotificationAddressee(addresseeID, userID, comment);
		return retVal;
	}

	public String updateNotificationAddressee(int addresseeID, String sendType, int addressTypeID, int addrUserID,
			String userID, String addrExternal, int messageOnly) {
		String retVal;
		retVal = dNotification.updateNotificationAddressee(addresseeID, sendType, addressTypeID, addrUserID, userID, addrExternal, messageOnly);
		return retVal;
	}

	public String addNewAddressee(int notificationID, String sendType, int addressTypeID, int addrUserID,
			String userID, String addrExternal, int messageOnly) {
		String retVal;
		retVal = dNotification.addNewAddressee(notificationID, sendType, addressTypeID, addrUserID, userID, addrExternal, messageOnly);
		return retVal;
	}
}
