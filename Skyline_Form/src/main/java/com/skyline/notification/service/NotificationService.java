package com.skyline.notification.service;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.general.bean.ComboBasic;
import com.skyline.general.bean.DataTableParamModel;
import com.skyline.notification.bean.Notification;
import com.skyline.notification.bean.NotificationAvailableField;
import com.skyline.notification.bean.NotificationMessageType;
import com.skyline.notification.bean.NotificationModule;
import com.skyline.notification.bean.NotificationOnSave;
import com.skyline.notification.bean.NotificationScheduler;
import com.skyline.notification.bean.NotificationTriggerType;

@Service
public class NotificationService {
	@Value("${jdbc.username}")
	private String username;
	@Value("${jdbc.url}")
	private String url;
	@Value("${jdbc.password}")
	private String password;
	@Value("${createNotifications:0}")
	private String createNotifications;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private NotificationManagement manage;
	
	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	public void createNotif() {
		// TODO Auto-generated method stub

	}

	/**
	 * Note: Functions below (till demoNotificationModuleMainInit)
	 * 		 was in Skyline and are taken from FrmNotificationListServlet
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void demoNotificationMainInit(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = generalUtil.getNull(request.getParameter("action_id"));
		request.setAttribute("CURRENT_ACTION_VALUE", action);

		int userID = Integer.parseInt(generalUtil.getSessionUserId());
		final DataTableParamModel paramModel = getParam(request);
		JSONObject jsonResponse = null;

		if (action.equals("back")) {
			String arr = request.getParameter("displayParametersArray");
			request.setAttribute("CURRENT_DISPLAY_PARAMETERS", arr);
			request.setAttribute("CREATE_NOTIFICATION", createNotifications);
		} else if (action.equals("getNotificationTable")) {
			try {
				int activeStatus = Integer.parseInt(request.getParameter("currActive"));
				jsonResponse = getNotificationTable(paramModel/* , connUtility, logPath */, activeStatus); //TODO
				response.setHeader("Content-Type", "application/json; charset=UTF-8");
				response.getWriter().print(jsonResponse.toString());
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
			}
			return;
		} else if (action.equals("deleteNotification")) {
			try {
				int curNotificationID = Integer.parseInt(request.getParameter("currNotificationID"));
				String comment = request.getParameter("comment");

				deleteNotification(curNotificationID, userID, comment);
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
			}
			return;
		}
		request.getRequestDispatcher("../skylineFormWebapp/jsp/demoNotificationTest.jsp").forward(request, response);
	}

	private JSONObject getNotificationTable(DataTableParamModel param, int activeStatus) {
		JSONObject jsonResponse = null;
		try // TODO remove userID and selSiteID if unused
		{
			jsonResponse = manage.getNotificationListTable(param, activeStatus, 0);
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		}
		return jsonResponse;
	}

	private String deleteNotification(int notificationID, int userID, String comment) {
		String retVal = "";
		try {
			retVal = manage.deleteNotification(notificationID, userID, comment);
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		}
		return retVal;
	}

	private DataTableParamModel getParam(HttpServletRequest request) {
		try {
			if (request.getParameter("sEcho") != null && request.getParameter("sEcho") != "") {

				DataTableParamModel param = new DataTableParamModel();
				param.sEcho = request.getParameter("sEcho");
				param.sSearchKeyword = request.getParameter("sSearch");
				param.bRegexKeyword = Boolean.parseBoolean(request.getParameter("bRegex"));
				param.sColumns = request.getParameter("sColumns");
				param.iDisplayStart = Integer.parseInt(request.getParameter("iDisplayStart"));
				param.iDisplayLength = Integer.parseInt(request.getParameter("iDisplayLength"));
				param.iColumns = Integer.parseInt(request.getParameter("iColumns"));
				param.sSearch = new String[param.iColumns];
				param.bSearchable = new boolean[param.iColumns];
				param.bSortable = new boolean[param.iColumns];
				param.bRegex = new boolean[param.iColumns];
				for (int i = 0; i < param.iColumns; i++) {
					param.sSearch[i] = request.getParameter("sSearch_" + i);
					param.bSearchable[i] = Boolean.parseBoolean(request.getParameter("bSearchable_" + i));
					param.bSortable[i] = Boolean.parseBoolean(request.getParameter("bSortable_" + i));
					param.bRegex[i] = Boolean.parseBoolean(request.getParameter("bRegex_" + i));
				}

				param.iSortingCols = Integer.parseInt(request.getParameter("iSortingCols"));
				param.sSortDir = new String[param.iSortingCols];
				param.iSortCol = new int[param.iSortingCols];
				param.sSortType = new String[param.iSortingCols];
				param.sDateFormat = new String[param.iSortingCols];

				for (int i = 0; i < param.iSortingCols; i++) {
					param.sSortDir[i] = request.getParameter("sSortDir_" + i);
					param.iSortCol[i] = Integer.parseInt(request.getParameter("iSortCol_" + i));
					param.sSortType[i] = request.getParameter("sSortColType_" + i);
					param.sDateFormat[i] = request.getParameter("sSortColDateFormat_" + i);
				}
				return param;
			} else
				return null;
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
			return null;
		}
	}

	/**
	 * Note: Functions below (till end of this class)
	 * 		 was in Skyline and are taken from NotificationModuleMainServlet
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void demoNotificationModuleMainInit(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = generalUtil.getNull(request.getParameter("action_id"));
		//////////////////////
		request.setAttribute("CURRENT_ACTION_VALUE", action);

		String userID = String.valueOf(generalUtil.getSessionUserId());
		request.setAttribute("USER_ID", userID);

		final DataTableParamModel paramModel = getParam(request);
		JSONObject jsonResponse = null;

		// globals
		String arr = request.getParameter("displayParametersArray");
		request.setAttribute("CURRENT_DISPLAY_PARAMETERS", arr);
		request.setAttribute("CREATE_NOTIFICATION", createNotifications);
		String notificationID = request.getParameter("currNotificationID");
		request.setAttribute("CURRENT_NOTIFICATION_ID", notificationID);

		if (action.equals("new")) {
			action = "getDDLData";
		} else if (action.equals("edit")) {
			action = "getDDLData";
		} else if (action.equals("getNotificationInfo")) {
			try {
				String params = generalUtil.getNull(request.getParameter("pageParameters"),
						request.getParameter("displayParametersArray"));
				request.setAttribute("CURRENT_DISPLAY_PARAMETERS", params);
				int currNotificationID = Integer.parseInt(request.getParameter("currNotificationID"));

				String json = new Gson().toJson(getNotificationInfo(currNotificationID));
				response.setContentType("application/Json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().print(json);
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
			}
			return;
		} else if (action.equals("saveNotificationGeneralData")) {
			try {
				int moduleID = Integer.parseInt(request.getParameter("moduleID"));
				int messageTypeID = Integer.parseInt(request.getParameter("msgTypeID"));
				int colNumber = Integer.parseInt(generalUtil.getEmpty(request.getParameter("colNumber"), "-1"));
				String description = request.getParameter("description");
				int schedulerID = Integer.parseInt(request.getParameter("schedulerID"));
				int triggerTypeID = Integer.parseInt(request.getParameter("triggerTypeID"));
				int onSaveID = Integer.parseInt(request.getParameter("onSaveID"));
				int interval = Integer.parseInt(request.getParameter("interval"));
				int resend = Integer.parseInt(request.getParameter("resend"));
				int active = Integer.parseInt(request.getParameter("isActive"));
				int copyToProduction = Integer.parseInt(request.getParameter("copyToProduction"));
				int addAttachment = Integer.parseInt(request.getParameter("addAttachment"));

				String retVal = addNewNotification(moduleID, messageTypeID, colNumber, description, schedulerID,
						triggerTypeID, onSaveID, interval, resend, active, addAttachment, userID, copyToProduction);
				response.getWriter().print(retVal);
			} catch (Exception ex) {
				response.getWriter().print("-1");
				generalUtilLogger.logWrite(ex);
			}
			return;
		} else if (action.equals("updateNotificationGeneralData")) {
			try {
				int currNotificationID = Integer.parseInt(request.getParameter("currNotificationID"));
				int moduleID = Integer.parseInt(request.getParameter("moduleID"));
				int messageTypeID = Integer.parseInt(request.getParameter("msgTypeID"));
				int colNumber = Integer.parseInt(generalUtil.getEmpty(request.getParameter("colNumber"), "-1"));
				String description = URLDecoder.decode(request.getParameter("description"), "UTF-8");
				//String description = request.getParameter("description"); // TODO which characters are illegal?
				int schedulerID = Integer.parseInt(request.getParameter("schedulerID"));
				int triggerTypeID = Integer.parseInt(request.getParameter("triggerTypeID"));
				int onSaveID = Integer.parseInt(request.getParameter("onSaveID"));
				int interval = Integer.parseInt(request.getParameter("interval"));
				int resend = Integer.parseInt(request.getParameter("resend"));
				int active = Integer.parseInt(request.getParameter("isActive"));
				int copyToProduction = Integer.parseInt(request.getParameter("copyToProduction"));
				int addAttachment = Integer.parseInt(request.getParameter("addAttachment"));

				String retVal = updateNotification(currNotificationID, moduleID, messageTypeID, colNumber, description,
						schedulerID, triggerTypeID, onSaveID, interval, resend, active, addAttachment, userID, copyToProduction);
				response.getWriter().print(retVal);
			} catch (Exception ex) {
				response.getWriter().print("-1");
				generalUtilLogger.logWrite(ex);
			}
			return;
		} else if (action.equals("updateNotificationMessage")) {
			try {
				int currNotificationID = Integer.parseInt(request.getParameter("currNotificationID"));
				String subject = request.getParameter("subject");
				String msgBody = request.getParameter("msgBody");

				String retVal = updateNotificationMessage(currNotificationID, subject, msgBody, userID);
				response.getWriter().print(retVal);
			} catch (Exception ex) {
				response.getWriter().print("-1");
				generalUtilLogger.logWrite(ex);
			}
			return;
		} else if (action.equals("getCriteriaTable")) {
			try {
				int currNotificationID = Integer.parseInt(request.getParameter("currNotificationID"));

				jsonResponse = getCriteriaTable(paramModel, currNotificationID);
				response.setHeader("Content-Type", "application/json; charset=UTF-8");
				response.getWriter().print(jsonResponse.toString());
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
			}
			return;
		} else if (action.equals("getOperatorList")) {
			try {
				int fieldID = Integer.parseInt(request.getParameter("fieldID"));

				String json = new Gson().toJson(getOperatorDDL(fieldID));
				response.setContentType("application/Json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().print(json);
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
			}
			return;
		} else if (action.equals("addNewCondition")) {
			try {
				int currNotificationID = Integer.parseInt(request.getParameter("currNotificationID"));
				String conditionType = request.getParameter("type");
				int fieldID = Integer.parseInt(request.getParameter("fieldID"));
				int operatorID = Integer.parseInt(request.getParameter("operatorID"));
				String conditionValue = request.getParameter("condValue");

				String retVal = addNewCondition(currNotificationID, conditionType, fieldID, operatorID, conditionValue,
						userID);
				response.getWriter().print(retVal);
			} catch (Exception ex) {
				response.getWriter().print("-1");
				generalUtilLogger.logWrite(ex);
			}
			return;
		} else if (action.equals("updateNotificationCondition")) {
			try {
				int currNotificationID = Integer.parseInt(request.getParameter("currNotificationID"));
				int currConditionID = Integer.parseInt(request.getParameter("conditionID"));
				String conditionType = request.getParameter("type");
				int fieldID = Integer.parseInt(request.getParameter("fieldID"));
				int operatorID = Integer.parseInt(request.getParameter("operatorID"));
				String conditionValue = request.getParameter("condValue");

				String retVal = updateNotificationCondition(currNotificationID, currConditionID, conditionType, fieldID,
						operatorID, conditionValue, userID);
				response.getWriter().print(retVal);
			} catch (Exception ex) {
				response.getWriter().print("-1");
				generalUtilLogger.logWrite(ex);
			}
			return;
		} else if (action.equals("deleteNotificationCondition")) {
			try {
				int currNotificationID = Integer.parseInt(request.getParameter("currNotificationID"));
				int currConditionID = Integer.parseInt(request.getParameter("conditionID"));
				String comment = request.getParameter("comment");

				String retVal = deleteNotificationCondition(currNotificationID, currConditionID, userID, comment);
				response.getWriter().print(retVal);
			} catch (Exception ex) {
				response.getWriter().print("-1");
				generalUtilLogger.logWrite(ex);
			}
			return;
		} else if (action.equals("getDistributionTable")) {
			try {
				int currNotificationID = Integer.parseInt(request.getParameter("currNotificationID"));

				jsonResponse = getDistributionTable(paramModel, currNotificationID);
				response.setHeader("Content-Type", "application/json; charset=UTF-8");
				response.getWriter().print(jsonResponse.toString());
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
			}
			return;
		} else if (action.equals("getAddresseeList")) {
			try {
				int currNotificationID = Integer.parseInt(request.getParameter("notificationID"));
				int addressTypeID = Integer.parseInt(request.getParameter("addressTypeID"));

				String json = new Gson().toJson(getAddresseeDDL(currNotificationID, addressTypeID));
				response.setContentType("application/Json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().print(json);
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
			}
			return;
		} else if (action.equals("deleteNotificationAddressee")) {
			try {
				int currAddresseeID = Integer.parseInt(request.getParameter("addresseeID"));
				String comment = request.getParameter("comment");

				String retVal = deleteNotificationAddressee(currAddresseeID, userID, comment);
				response.getWriter().print(retVal);
			} catch (Exception ex) {
				response.getWriter().print("-1");
				generalUtilLogger.logWrite(ex);
			}
			return;
		} else if (action.equals("updateNotificationAddressee")) {
			try {
				int currAddresseeID = Integer.parseInt(request.getParameter("currAddresseeID"));
				String sendType = request.getParameter("sendType");
				int addressTypeID = Integer.parseInt(request.getParameter("typeID"));
				int addrUserID = Integer.parseInt(request.getParameter("addrUserID"));
				String notifAddrExternal = request.getParameter("notifAddrExternal");
				int messageOnly = Integer.parseInt(request.getParameter("messageOnly"));

				String retVal = updateNotificationAddressee(currAddresseeID, sendType, addressTypeID, addrUserID,
						userID, notifAddrExternal, messageOnly);
				response.getWriter().print(retVal);
			} catch (Exception ex) {
				response.getWriter().print("-1");
				generalUtilLogger.logWrite(ex);
			}
			return;
		} else if (action.equals("addNewAddressee")) {
			try {
				int currNotificationID = Integer.parseInt(request.getParameter("currNotificationID"));
				String sendType = request.getParameter("sendType");
				int addressTypeID = Integer.parseInt(request.getParameter("typeID"));
				int addrUserID = Integer.parseInt(request.getParameter("addrUserID"));
				String notifAddrExternal = request.getParameter("notifAddrExternal");
				int messageOnly = Integer.parseInt(request.getParameter("messageOnly"));
				
				String retVal = addNewAddressee(currNotificationID, sendType, addressTypeID, addrUserID, userID, notifAddrExternal, messageOnly);
				response.getWriter().print(retVal);
			} catch (Exception ex) {
				response.getWriter().print("-1");
				generalUtilLogger.logWrite(ex);
			}
			return;
		} else if (action.equals("getAvailableFieldsDDL")) {
			try {
				String fieldJson = new Gson().toJson(getFieldDDL(Integer.parseInt(notificationID)));
				response.setContentType("application/Json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().print(fieldJson);
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
			}
			return;
		}

		if (action.equals("getDDLData")) {
			try {
				String moduleNameJson = new Gson().toJson(getModuleDDL(/* connUtility, logPath */));
				request.setAttribute("DDL_MODULE", moduleNameJson);
				String messageTypeJson = new Gson().toJson(getMessageTypeDDL(/* connUtility, logPath */));
				request.setAttribute("DDL_MESSAGE_TYPE", messageTypeJson);
				String schedulerJson = new Gson().toJson(getSchedulerDDL(/* connUtility, logPath */));
				request.setAttribute("DDL_SCHEDULER", schedulerJson);
				
				
				String triggerTypeJson = new Gson().toJson(getTriggerTypeDDL());
				request.setAttribute("DDL_TRIGGERTYPE", triggerTypeJson);
				String onSaveJson = new Gson().toJson(getOnSaveDDL());
				request.setAttribute("DDL_ONSAVE", onSaveJson);
				
				String addressTypeJson = new Gson().toJson(getAddressTypeOptions(/* connUtility, logPath */));
				request.setAttribute("RADIO_ADDRESS_TYPE", addressTypeJson);
				String fieldJson = new Gson().toJson(getFieldDDL(Integer.parseInt(notificationID)));
				request.setAttribute("DDL_FIELD", fieldJson);
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
			}
		}

		request.getRequestDispatcher("../skylineFormWebapp/jsp/NotificationModuleMainIE.jsp").forward(request, response);
		return;
	}

	private JSONObject getCriteriaTable(DataTableParamModel param,
			/* ConnUtility connUtility, String logPath, */int notificationID) {
		JSONObject jsonResponse = null;
		try {
			jsonResponse = manage.getNotificationCriteriaTable(param, notificationID);
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		}
		return jsonResponse;
	}

	private Notification getNotificationInfo(/* ConnUtility connUtility, String logPath, */int notificationID) {
		Notification notification = null;

		try {
			notification = manage.getNotificationInfo(notificationID);
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		}
		return notification;
	}

	private List<NotificationModule> getModuleDDL(/* ConnUtility connUtility, String logPath */) {
		List<NotificationModule> listOfModules = null;
		//        NotificationManagement manage =  new NotificationManagement(connUtility.getUrl(),connUtility.getDBuser(),connUtility.getPassword(), logPath);

		try {
			listOfModules = manage.getNotificationModuleArray();
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		}
		return listOfModules;
	}

	private List<NotificationMessageType> getMessageTypeDDL(/* ConnUtility connUtility, String logPath */) {
		List<NotificationMessageType> listOfMessageTypes = null;
		try {
			listOfMessageTypes = manage.getNotificationMessageTypeArray();
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		}
		return listOfMessageTypes;
	}

	private List<NotificationScheduler> getSchedulerDDL(/* ConnUtility connUtility, String logPath */) {
		List<NotificationScheduler> listOfSchedulers = null;
		try {
			listOfSchedulers = manage.getNotificationSchedulerArray();
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		}
		return listOfSchedulers;
	}
	
	
	private List<NotificationTriggerType> getTriggerTypeDDL() {
		List<NotificationTriggerType> listOfTriggerTypes = null;
		try {
			listOfTriggerTypes = manage.getNotificationTriggerTypeArray();
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		}
		return listOfTriggerTypes;
	}
	
	private List<NotificationOnSave> getOnSaveDDL() {
		List<NotificationOnSave> listOfOnSaves = null;
		try {
			listOfOnSaves = manage.getNotificationOnSaveArray();
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		}
		return listOfOnSaves;
	}

	private List<NotificationAvailableField> getFieldDDL(
			/* ConnUtility connUtility, String logPath, */ int notificationID) {
		List<NotificationAvailableField> listOfFields = null;
		//         NotificationManagement manage =  new NotificationManagement(connUtility.getUrl(),connUtility.getDBuser(),connUtility.getPassword(), logPath);

		try {
			listOfFields = manage.getNotificationAvailableFieldArray(notificationID);
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		}
		return listOfFields;
	}

	private List<ComboBasic> getAddressTypeOptions(/* ConnUtility connUtility, String logPath */) {
		List<ComboBasic> listOfAddressTypes = null;
		try {
			listOfAddressTypes = manage.getAddressTypeArray();
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		}
		return listOfAddressTypes;
	}

	private String addNewNotification(/* ConnUtility connUtility, String logPath, */int moduleID, int messageTypeID,
			int colNumber, String description, int schedulerID, int triggerTypeID, int onSaveID, int interval, int resend, int active, int addAttachment,
			String userID, int copyToProduction) {
		String retVal = "";

		try {
			retVal = manage.addNewNotification(moduleID, messageTypeID, colNumber, description, schedulerID, triggerTypeID, onSaveID, interval,
					resend, active, addAttachment, userID, copyToProduction);
		} catch (Exception ex) {
			retVal = "-1";
			generalUtilLogger.logWrite(ex);
		}
		return retVal;
	}

	private String updateNotification(/* ConnUtility connUtility, String logPath, */int notificationID, int moduleID,
			int messageTypeID, int colNumber, String description, int schedulerID, int triggerTypeID, int onSaveID, int interval, int resend, int active,
			int addAttachment, String userID, int copyToProduction) {
		String retVal = "";

		try {
			retVal = manage.updateNotification(notificationID, moduleID, messageTypeID, colNumber, description,
					schedulerID, triggerTypeID, onSaveID, interval, resend, active, addAttachment, userID, copyToProduction);
		} catch (Exception ex) {
			retVal = "-1";
			generalUtilLogger.logWrite(ex);
		}
		return retVal;
	}

	private String updateNotificationMessage(/* ConnUtility connUtility, String logPath, */int notificationID,
			String subject, String msgBody, String userID) {
		String retVal = "";

		try {
			retVal = manage.updateNotificationMessage(notificationID, subject, msgBody, userID);
		} catch (Exception ex) {
			retVal = "-1";
			generalUtilLogger.logWrite(ex);
		}
		return retVal;
	}

	private List<ComboBasic> getOperatorDDL(/* ConnUtility connUtility, String logPath, */int fieldID) {
		List<ComboBasic> listOfOperators = null;

		try {
			listOfOperators = manage.getNotificationConditionOperatorArray(fieldID);
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		}
		return listOfOperators;
	}

	private String addNewCondition(/* ConnUtility connUtility, String logPath, */int notificationID,
			String conditionType, int fieldID, int operatorID, String conditionValue, String userID) {
		String retVal = "";

		try {
			retVal = manage.addNewCondition(notificationID, conditionType, fieldID, operatorID, conditionValue, userID);
		} catch (Exception ex) {
			retVal = "-1";
			generalUtilLogger.logWrite(ex);
		}
		return retVal;
	}

	private String updateNotificationCondition(/* ConnUtility connUtility, String logPath, */int notificationID,
			int conditionID, String conditionType, int fieldID, int operatorID, String conditionValue, String userID) {
		String retVal = "";

		try {
			retVal = manage.updateNotificationCondition(notificationID, conditionID, conditionType, fieldID, operatorID,
					conditionValue, userID);
		} catch (Exception ex) {
			retVal = "-1";
			generalUtilLogger.logWrite(ex);
		}
		return retVal;
	}

	private String deleteNotificationCondition(/* ConnUtility connUtility, String logPath, */int notificationID,
			int conditionID, String userID, String comment) {
		String retVal = "";

		try {
			retVal = manage.deleteNotificationCondition(notificationID, conditionID, userID, comment);
		} catch (Exception ex) {
			retVal = "-1";
			generalUtilLogger.logWrite(ex);
		}
		return retVal;
	}

	private JSONObject getDistributionTable(DataTableParamModel param,
			/* ConnUtility connUtility, String logPath, */int notificationID) {
		JSONObject jsonResponse = null;
		try {
			jsonResponse = manage.getNotificationDistributionTable(param, notificationID);
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		}
		return jsonResponse;
	}

	private List<ComboBasic> getAddresseeDDL(/* ConnUtility connUtility, String logPath, */int notificationID,
			int addresseeTypeID) {
		List<ComboBasic> listOfUsers = null;

		try {
			listOfUsers = manage.getAddresseeUserArray(notificationID, addresseeTypeID);
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		}
		return listOfUsers;
	}

	private String deleteNotificationAddressee(/* ConnUtility connUtility, String logPath, */int addresseeID,
			String userID, String comment) {
		String retVal = "";

		try {
			retVal = manage.deleteNotificationAddressee(addresseeID, userID, comment);
		} catch (Exception ex) {
			retVal = "-1";
			generalUtilLogger.logWrite(ex);
		}
		return retVal;
	}

	private String updateNotificationAddressee(/* ConnUtility connUtility, String logPath, */int addresseeID,
			String sendType, int addressTypeID, int addrUserID, String userID, String addrExternal, int messageOnly) {
		String retVal = "";

		try {
			retVal = manage.updateNotificationAddressee(addresseeID, sendType, addressTypeID, addrUserID, userID, addrExternal, messageOnly);
		} catch (Exception ex) {
			retVal = "-1";
			generalUtilLogger.logWrite(ex);
		}
		return retVal;
	}

	private String addNewAddressee(/* ConnUtility connUtility, String logPath, */int notificationID, String sendType,
			int addressTypeID, int addrUserID, String userID, String addrExternal, int messageOnly) {
		String retVal = "";

		try {
			retVal = manage.addNewAddressee(notificationID, sendType, addressTypeID, addrUserID, userID, addrExternal, messageOnly);
		} catch (Exception ex) {
			retVal = "-1";
			generalUtilLogger.logWrite(ex);
		}
		return retVal;
	}

	/// End from NotificationModuleMainServlet /////////////////////////	
}
