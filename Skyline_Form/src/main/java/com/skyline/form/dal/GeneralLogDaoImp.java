package com.skyline.form.dal;

import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Repository;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;

@Repository("GeneralLogDao")
@Configuration
@EnableAsync
public class GeneralLogDaoImp extends BasicDao implements GeneralLog {

	private static final Logger logger = LoggerFactory.getLogger(GeneralDaoImp.class);

//	@Async
//	@Override
//	public String logWriterDao(LevelType levelType, String comments, String formId, ActivitylogType activitylogType,
//			Map<String, String> additionalInfo, String stackTrace) {
//		String insert; 
//		insert = logWriterDao_(levelType, comments, formId, activitylogType, additionalInfo, stackTrace, null);
//		return insert;
//	}
//
//	@Async
//	@Override
//	public String logWriterDao(LevelType levelType, String comments, String formId, ActivitylogType activitylogType,
//			Map<String, String> additionalInfo) {
//		return logWriterDao_(levelType, comments, formId, activitylogType, additionalInfo, null, null);
//	}

	@Async
	@Override
	public String logWriterDao(LevelType levelType, String comments, String formId, ActivitylogType activitylogType,
			Map<String, String> additionalInfo, String stackTrace, String userId) {
		// TODO Auto-generated method stub
		return logWriterDao_(levelType, comments, formId, activitylogType, additionalInfo, stackTrace, userId);
	}

	private String logWriterDao_(LevelType levelType, String comments, String formId, ActivitylogType activitylogType,
			Map<String, String> additionalInfo, String stackTrace, String userId) {
		String insert = "0";
		String sql = "";

//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		try {
			if (activitylogType == ActivitylogType.NotificationEvent || activitylogType == ActivitylogType.Depletion) {
				sql = "insert into fg_r_messages (message_body, user_id, message_type, formid, time_stamp, additionalinfo, changed_by) "
						+ "values("
						+ (activitylogType == ActivitylogType.NotificationEvent ? ("'" + comments + "'")
								: removeUpperComma(comments, 4000))
						+ "," + userId + ","
						+ (activitylogType == ActivitylogType.NotificationEvent ? "'Notification'"
								: removeUpperComma(activitylogType.getActLogTypeName(), 500))
						+ "," + removeUpperComma(formId, 500) + "," + " sysdate,"
						+ removeUpperComma(mapToJson(additionalInfo), 4000) + "," + removeUpperComma(userId, 500)
						+ ") ";
			} else {
				sql = "insert into FG_ACTIVITY_LOG (FORMID,ACTIVITYLOGTYPE,TIMESTAMP,USER_ID,COMMENTS,ADDITIONALINFO,LEVELTYPE,STACKTRACE) "

						+ "values(" + removeUpperComma(formId, 500) + ","
						+ removeUpperComma(activitylogType.getActLogTypeName(), 500) + ",sysdate," + userId + ","
						+ handleClob(comments) + "," + ""
						+ removeUpperComma(mapToJson(additionalInfo), 4000) + ","
						+ removeUpperComma(levelType.getTypeName(), 500) + "," + removeUpperComma(stackTrace, 4000)
						+ ") ";
			}
			insert = String.valueOf(jdbcTemplate.update(sql));
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
			insert = "-1";
		}
		return insert;
	}

	private String removeUpperComma(String val, int maxLength) {
		String toReturn = val;
		if (val != null) {
			toReturn = (toReturn.equals("{}") ? "" : toReturn);
			toReturn = toReturn.replace("'", "");
			toReturn = maxLength!=-1 && (toReturn.length()) > maxLength ? toReturn.substring(0, maxLength - 4) + ".." : toReturn;
		} else {
			toReturn = "";
		}
		return "'" + toReturn + "'";
	}

//	private String getSessionUserId() {
//		// TODO Auto-generated method stub
//		try {
//			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//			HttpSession session = attr.getRequest().getSession();
//			return (String) session.getAttribute("userId");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			return "-1";
//		}
//	}

	private String mapToJson(Map<String, String> map) {
		return (new JSONObject(map)).toString();
	}
	
	public String handleClob(String val) {
		String validVal = val;
		if (val != null) {
			validVal = (validVal.equals("{}") ? "" : validVal);
			validVal = validVal.replace("'", "");
		}
		
		String toReturn = "to_clob('" + validVal + "')";
		if(validVal != null && validVal.length() > 4000) {
			toReturn = breakClob(validVal);
		} 
		return toReturn;
	}

	private String breakClob(String val) {
		String toReturn = "";
		int index = 0;
		while (index < val.length()) {
			toReturn = "CONCAT_CLOB(" + getEmpty(toReturn, "EMPTY_CLOB()") + ",'" + val.substring(index, Math.min(index + 4000,val.length())) + "')";
		    index += 4000;
		}
		return toReturn;
	}
	
	public String getEmpty(String str, String defaultString) {
		if (getNull(str).equals("")) {
			return defaultString;
		}
		return str;
	}  
	
	public String getNull(String str) {
		if (str == null || str.equals("null")) {
			return "";
		}

		return str;
	}

}
