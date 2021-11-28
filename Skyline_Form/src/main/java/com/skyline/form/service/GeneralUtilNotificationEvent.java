package com.skyline.form.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.GeneralDao;

@Service
public class GeneralUtilNotificationEvent {

	@Autowired
	protected GeneralDao generalDao;

	@Autowired
	protected FormSaveDao formSaveDao;

	@Autowired
	protected FormDao formDao;

	@Autowired
	protected GeneralUtilLogger generalUtilLogger;

	@Autowired
	public GeneralUtilForm generalUtilForm;

	@Autowired
	public GeneralUtil generalUtil;
	
	/**
	 *  see doc General\Doc\Notification for overview description
	*/

	/**
	 * Executing notifications. Adding the notification messages to the activity log table aimed to destination users
	 * @param formId formId of the form that called the notification event
	 * @param formCode formCode of the form that called the notification event
	 */
	public void exeNotificationEvent(String formId, String formCode) {
		try {
			//			System.out.println("kd test notification on Save"); // kd 03052018 temp  for debug 
			String sql = "Select * from fg_n_config_v" + " where TRIGGER_TYPE_ID = '2'"//validating that the notification is triggered to on_save event
					+ " and ISACTIVE = '1'" + " and ON_SAVE_FORMCODE =  '" + formCode + "'"
					+ " and EMAIL_BODY is not null";//gets the notifications that include email body
			List<Map<String, Object>> notificationOnSaveList = generalDao.getListOfMapsBySql(sql);
			for (Map<String, Object> notificationDef : notificationOnSaveList) {

				//*******GET AND EXECUTE THE NOTIFICATION QUERY******//
				sql = (notificationDef.get("SELECT_STATEMENT") instanceof String)
						? notificationDef.get("SELECT_STATEMENT").toString() : "";
				if (!sql.isEmpty()) {
					sql += " where FORMID = '" + formId + "'"; // gets the data of the current formId
					sql += (notificationDef.get("WHERE_STATEMENT") instanceof String)
							? (" and " + notificationDef.get("WHERE_STATEMENT").toString()) : "";
				}
				List<Map<String, Object>> allFormsElementValues = generalDao.getListOfMapsBySql(sql);
				String message = "";
				Map<String, String> additionalData = new HashMap<String, String>();
				additionalData.put("d_notification_message_id",
						notificationDef.get("d_notification_message_id").toString());

				//****PASS THROUGH THE RECORDS FOUND IN THE INTERNAL QUERY OF THE NOTIFICATION****//
				for (Map<String, Object> formValues : allFormsElementValues) {

					//converts the Map<String,Object> to <String,String>
					Map<String, String> elementValueMap = new HashMap<>();
					for (Map.Entry<String, Object> entry : formValues.entrySet()) {
						//if (entry.getValue() instanceof String) { // kd 06052018 TEMP: kd commented this because there are fields which are not String type.
						elementValueMap.put(entry.getKey(),
								(entry.getValue() != null) ? entry.getValue().toString() : "");
						//}
					}

					//***********GET THE USERS TO BE NOTIFIED*********//
					List<String> userList = new ArrayList<>();
					if (notificationDef.get("ADDRESSEE_TYPE_ID") != null) {
						switch (notificationDef.get("ADDRESSEE_TYPE_ID").toString()) {
							case "1":// single user
								//if(notificationDef.get("ADDRESSEE_USER_ID") instanceof String) {  // kd 03052018 TEMP: kd commented this because id is't String. The condition id not satisfied
								userList.add(notificationDef.get("ADDRESSEE_USER_ID").toString());
								//}
								break;
							case "3":// group
								userList = getUserListByProcedure(notificationDef, elementValueMap);
								break;
						}
					}

					if (!userList.isEmpty()) {//there are addressee //NOT CHECKED -and there exists data selected by the current notification query

						//***********GET NOTIFICATION MESSAGE*********//
						if (notificationDef.get("EMAIL_SUBJECT") instanceof String) {
							message = "Subject: " + getMessageString(notificationDef.get("EMAIL_SUBJECT").toString(),
									elementValueMap);
						}
						if (notificationDef.get("EMAIL_BODY") instanceof String) {
							message += ("</br> Body: "
									+ getMessageString(notificationDef.get("EMAIL_BODY").toString(), elementValueMap));
						}
						for (String userId : userList) {
							insertMessageToActivityLog(formId, message, userId, additionalData);
						}
					}
				}
			}

		} catch (Exception ex) {
			generalUtilLogger.logWrite(LevelType.WARN,
					"The notification was not performed on save event of '" + formCode + "' ", formId,
					ActivitylogType.NotificationEvent, null, ex);
		}
	}
	
	/**
	 * 
	 * @param csvList using as the message arg
	 * @param userIdList distribution user id list
	 * @param messageCode
	 */
	public void sendAdHocNotificationByObjIdVal(List<String> csvList, List<String> userIdList, String messageCode) {
		if(csvList != null) {
			for (String csvVal : csvList) {
				Object[] argArray = csvVal.split(",",-1);
				String message = generalUtil.getSpringMessagesByKey(messageCode, argArray, messageCode);
				for (String userId : userIdList) {
					insertMessageToActivityLog(formSaveDao.getStructFileId("AdHocNotification","-1"),generalUtil.replaceDBUpdateVal(message),userId,null);
				}
			}
		}
	}

	/**
	 * Executing a DB stored procedure, that returns list of users.
	 * @param notificationDef a map of notification elements and values
	 * @param elementValueMap a map of elements and values resulted by the query in the select_statement+wherepart of the referenced notification
	 * @return list of addressee users. Returns empty list if there are no addresse
	 */
	private List<String> getUserListByProcedure(Map<String, Object> notificationDef,
			Map<String, String> elementValueMap) {
		List<String> userList = new ArrayList<>();
		List<Map<String, Object>> result = new ArrayList<>();
		if (notificationDef.get("PARAMS_FIELD_NAMES") instanceof String) {
			String[] params = notificationDef.get("PARAMS_FIELD_NAMES").toString().split(";");
			for (int i = 0; i < params.length; i++) {//replaces the parameter fields in the suitable values from the map of the current record
				//params[i] = params[i].replaceFirst(params[i], "123"/*elementValueMap.get(parameter)*/); // kd commented
				params[i] = params[i].replaceFirst(params[i], generalUtil.getNull(elementValueMap.get(params[i]))); // 
			}
			if (notificationDef.get("ADDRESSEE_GROUP_SELECT") instanceof String) {
				result = generalDao.callProcedureReturnsOutObject(
						notificationDef.get("ADDRESSEE_GROUP_SELECT").toString(), Arrays.asList(params));
			}
		}
		if (!result.isEmpty()) {
			for (Map<String, Object> resultRecord : result) {
				userList.add(resultRecord.get("USER_ID").toString());
			}
		}
		return userList;
	}

	/**
	 * Adding a record to the activity log table of type 'NotificationEvent'
	 * @param formId formId of the form 
	 * @param message message to be displayed in the notification
	 * @param userId userId of the notified user
	 */
	private void insertMessageToActivityLog(String formId, String message, String userId,
			Map<String, String> additionalData) {
		// TODO Auto-generated method stub
		generalUtilLogger.logWriter(LevelType.Other, message, formId, ActivitylogType.NotificationEvent, userId,
				additionalData);
	}

	/**
	 * Replacing the @@placeholders_filelds@@ expressions with the suitable element in the elementValueMap
	 * @param placeHolderMessage the message text
	 * @param elementValueMap map of elements and values to be located in the message text instead of the placeholders_filelds in accordance
	 * @return message text after replacement of placeholders_filelds with the suitable values  
	 */
	private String getMessageString(String placeHolderMessage, Map<String, String> elementValueMap) {
		Pattern pattern = Pattern.compile("\\@\\@(.+?)\\@\\@");
		Matcher m = pattern.matcher(placeHolderMessage);
		while (m.find()) {
			String param = m.group(1);
			if (elementValueMap.containsKey(param)) {
				placeHolderMessage = placeHolderMessage.replace("@@" + param + "@@", elementValueMap.get(param));
			}
		}
		return placeHolderMessage;
	}

	public String getMessageCount(String userId, boolean forceCheck) {
		String num = getSessionMessageCount();
		if(num == null || num.isEmpty()) {
			num = "0";
			setSessionMessageCount(num);
		}
		
		Map<String, String> userMap = generalDao.sqlToHashMap(String.format(
				"select nvl(t.messagecheckinterval,20) as messagecheckinterval, t.lastnotificationcheck from fg_s_user_pivot t where formid = '%1$s'",
				userId));
		String messageCheckInterval = userMap.get("MESSAGECHECKINTERVAL");
		setSessionMessageCheckInterval(messageCheckInterval);
		String lastCheck = userMap.get("LASTNOTIFICATIONCHECK");
		String sql = String.format(
				"update fg_s_user_pivot t set t.lastnotificationcheck = TO_CHAR(sysdate,'dd/MM/yyyy HH24:MI'), messagecheckinterval = '" + messageCheckInterval + "' where formId =  %1$s",
				userId);

		if (generalUtil.getNull(lastCheck).isEmpty()) { //will happen only once for a new user
			forceCheck = true;
		}
		 

		String intervalfromLastCheck = generalDao.selectSingleString(String
				.format("select ( sysdate - TO_date('%1$s','dd/MM/yyyy HH24:MI'))* 24 * 60 from dual", lastCheck));
		if (forceCheck || Double.parseDouble(intervalfromLastCheck) >= Double.parseDouble(messageCheckInterval)) {
			num = generalDao.selectSingleString("SELECT count(t.ID) FROM FG_R_MESSAGES_V t "
					+ " WHERE  NOT EXISTS (SELECT s.message_id " + " FROM fg_r_messages_state s "
					+ " WHERE t.id = s.message_id and s.is_readed = 1) and t.user_id = " + userId);
			setSessionMessageCount(num);
			generalDao.updateSingleString(sql);
		}
		return num;
	}

	private void setSessionMessageCheckInterval(String interval) {
		// Add sessionId to the map
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession();
		session.setAttribute("messageCheckInterval", interval);
	}

	private String getSessionMessageCount() {
		// Add sessionId to the map
		String messageCount = "0";
		try {
			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			HttpSession session = attr.getRequest().getSession();
			messageCount = (String) session.getAttribute("messageCount");
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return messageCount;
	}

	private void setSessionMessageCount(String messageCount) {
		// Add sessionId to the map
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession();
		session.setAttribute("messageCount", messageCount);
	}

}
