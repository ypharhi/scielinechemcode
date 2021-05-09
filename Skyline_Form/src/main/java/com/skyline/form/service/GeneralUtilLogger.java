package com.skyline.form.service;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.dal.GeneralLog;

@Service
public class GeneralUtilLogger {

	@Autowired
	GeneralLog generalLog;

	@Value("${dbLevelType:Error,Other,System Error,Warn,Info}")
	private String dbLevelType;

//	@Value("${dbActivitylogType:Calculation;Info,Calculation;Debug,Permission;Info,Permission;Debug,ChemMol;Info,ChemMol;Debug,PerformanceSQL;Debug}")
//	@Value("${dbActivitylogType:ALL}")
//	private String dbActivitylogType;

	// for cleaning the log: (AS IN FG_UNITTEST.REMOVE_UNITTESTTSER_DATA cleanup) -
	// leveltype OTHER is used by the system
//	insert into fg_activity_log_hst select * from fg_activity_log t where upper(t.leveltype) not in('OTHER');
//    delete from fg_activity_log t where upper(t.leveltype) not in('OTHER');  

	private static final Logger logger = LoggerFactory.getLogger(GeneralUtilLogger.class);

	public void logWriter(LevelType level, String msg, ActivitylogType logType, String formId) {
		logWriter(level, msg, logType, formId, null);
	}

	public void logWriter(LevelType level, String msg, ActivitylogType logType, String formId, String userId) {
		logWriter(level, msg, logType, formId, null, userId);
	}

	public void logWriter(LevelType level, String msg, String formId, ActivitylogType activitylogType, String userId,
			Map<String, String> additionalInfo) {
		logWrite(level, msg, formId, activitylogType, userId, additionalInfo);
	}

	public void logWrite(Throwable error) {
		logWriter(LevelType.SYSTEM_ERROR, "System Error", ActivitylogType.GeneralError, "", error, null);
		// integration.LogWriterDB("Error", extractMethodName);
	}

	public void logWriter(LevelType levelType, ActivitylogType activitylogType, String comments, String formId,
			StringBuilder msgStr) {

		msgStr.append("<p>").append(comments).append("</p>\r\n");
	}

	public void logWriter(LevelType level, String msg, ActivitylogType logType, String formId, Throwable e,
			String userId) {
		String logMsg = msg;
		String eStack = "";
		if (e != null) {
			eStack += "error=" + extractMethodNameToString(e);
		}

		switch (level) {
		case INFO:
			logger.info(logMsg + eStack);
			break;
		case DEBUG:
			logger.debug(logMsg + eStack);
			break;
		case WARN:
			logger.warn(logMsg + eStack);
			break;
		case ERROR:
			logger.error(logMsg + eStack);
			break;
		case SYSTEM_ERROR:
			logger.error(logMsg + eStack);
			break;
		default:
			break;
		}

		if (logToDb(level)) {
			try {
				generalLog.logWriterDao(level, msg, getRequestContextFormId(formId), logType, null, eStack,
						getSessionUserId(userId));
			} catch (Exception e1) {
				// do nothing
			}
		}
	}

	private boolean logToDb(LevelType thisLevelType) {
		boolean toReturn = false;

		try {
			if (dbLevelType == null || ("," + dbLevelType.toLowerCase().trim() + ",")
					.contains("," + thisLevelType.getTypeName().toLowerCase() + ",")) {
				toReturn = true;
			}
		} catch (Exception e) {
			toReturn = true;
		}

		return toReturn;
	}

	public String logWrite(LevelType level, String comments, String formId, ActivitylogType activitylogType,
			Map<String, String> additionalInfo, Throwable e) {
		// TODO Auto-generated method stub
		String toReturn = "";
		String logMsg = comments + mapToString("info", additionalInfo);
		String eStack = "";
		if (e != null) {
			eStack += "error=" + extractMethodNameToString(e);
		}

		switch (level) {
		case INFO:
			logger.info(logMsg + eStack);
			break;
		case DEBUG:
			logger.debug(logMsg + eStack);
			break;
		case WARN:
			logger.warn(logMsg + eStack);
			break;
		case ERROR:
			logger.error(logMsg + eStack);
			break;
		case SYSTEM_ERROR:
			logger.error(logMsg + eStack);
			break;
		case ASPECT_EXCEPTION:
			logger.error(logMsg + eStack);
			break;
		default:
			break;
		}

		if (logToDb(level)) {
			try {
				toReturn = generalLog.logWriterDao(level, comments, getRequestContextFormId(formId), activitylogType,
						additionalInfo, eStack, getSessionUserId(null));
			} catch (Exception e1) {
				// do nothing
			}
		}
		return toReturn;
	}

	public String logWrite(LevelType level, String msg, String formId, ActivitylogType activitylogType,
			Map<String, String> additionalInfo) {
		String toReturn = "";
		String logMsg = msg + mapToString("info", additionalInfo);

		switch (level) {
		case INFO:
			logger.info(logMsg);
			break;
		case DEBUG:
			logger.debug(logMsg);
			break;
		case WARN:
			logger.warn(logMsg);
			break;
		case ERROR:
			logger.error(logMsg);
			break;
		case SYSTEM_ERROR:
			logger.error(logMsg);
			break;
		case ASPECT_EXCEPTION:
			logger.error(logMsg);
			break;
		default:
			break;
		}

		if (logToDb(level)) {
			try {
				toReturn = generalLog.logWriterDao(level, msg, getRequestContextFormId(formId), activitylogType,
						additionalInfo, null, getSessionUserId(null));
			} catch (Exception e) {
				// do nothing
			}
		}

		return toReturn;
	}

	public String logWrite(LevelType level, String msg, String formId, ActivitylogType activitylogType, String userId,
			Map<String, String> additionalInfo) {
		String toReturn = "";
		String logMsg = msg + mapToString("info", additionalInfo);

		switch (level) {
		case INFO:
			logger.info(logMsg);
			break;
		case DEBUG:
			logger.debug(logMsg);
			break;
		case WARN:
			logger.warn(logMsg);
			break;
		case ERROR:
			logger.error(logMsg);
			break;
		case SYSTEM_ERROR:
			logger.error(logMsg);
			break;
		case ASPECT_EXCEPTION:
			logger.error(logMsg);
			break;
		default:
			break;
		}

		if (logToDb(level)) {
			try {
				toReturn = generalLog.logWriterDao(level, msg, getRequestContextFormId(formId), activitylogType,
						additionalInfo, null, getSessionUserId(userId));
			} catch (Exception e) {
				// do nothing;
			}
		}
		return toReturn;
	}

	private String mapToString(String title, Map<String, String> map) {
		StringBuilder toReturn = new StringBuilder(title);
		try {
			if (map != null) {
				for (Map.Entry<String, String> entry : map.entrySet()) {
					toReturn.append("\nid: " + entry.getKey() + ", value: " + entry.getValue());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toReturn.toString();
	}

	private String extractMethodNameToString(Throwable ex) {
		String toReturn = "";
		String smsg_ = "";
		try {
			if (ex != null) {
				String methodFormat = "%s\n";
				StackTraceElement[] st = ex.getStackTrace();
				StringBuilder mName = new StringBuilder();
				mName.append(String.format(methodFormat, "Exception: " + ex.toString()));
				for (int i = 0; i < st.length; i++) {
					smsg_ = String.format(methodFormat, st[i].toString());
					if (smsg_.contains("com.skyline")) {
						mName.append(String.format(methodFormat, st[i].toString()));
					}
				}

				toReturn = mName.toString();
			} else {
				toReturn = "StackTrace(logger) - no exception to trace!";
			}
		} catch (Exception e) {
			toReturn = "StackTrace(logger) - Error!";
		}
		return toReturn;
	}

	/**
	 * get request from Id if formId is missing and the logger call was part of form
	 * request
	 * 
	 * @param formId
	 * @return the formId of the request
	 */
	private String getRequestContextFormId(String formId) {
		String toReturn = formId;
		// TODO Auto-generated method stub
		try {
			if (formId == null || formId.equals("") || formId.equals("0") || formId.equals("-1")) {
				ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder
						.currentRequestAttributes();
				toReturn = attr.getRequest().getParameter("formId");
			}
		} catch (Exception e) {
			// DO Nothing
			toReturn = formId;
		}
		return toReturn;
	}

	private String getSessionUserId(String userId) {
		String toReturn = userId;
		if (userId == null || userId.isEmpty() || userId.equalsIgnoreCase("na") || userId.equalsIgnoreCase("-1")) {
			try {
				ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder
						.currentRequestAttributes();
				HttpSession session = attr.getRequest().getSession();
				toReturn = (String) session.getAttribute("userId");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				toReturn = "-1";
			}
		}
		return toReturn;
	}
}
