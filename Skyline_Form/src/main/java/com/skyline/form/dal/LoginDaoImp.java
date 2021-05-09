package com.skyline.form.dal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;

@Repository("LoginDao")
public class LoginDaoImp extends BasicDao implements LoginDao {

	@Autowired
	public GeneralUtil generalUtil;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	private static final Logger logger = LoggerFactory.getLogger(LoginDaoImp.class);

	@Override
	public String authenticateUser(String userName, String password, boolean isLogin, String stationIpAddress) {
		Map<String, String> parameters;
		try {
			parameters = new HashMap<String, String>();
			parameters.put("user_name_in", userName);
			parameters.put("enc_pwd_in", password);
			parameters.put("is_login_in", isLogin ? "1" : "0");
			parameters.put("ip_address_in", stationIpAddress);
			//			System.out.println(parameters);
			return generalDao.callPackageFunction("", "FG_AUTHENTICATE_USER", parameters);
		} catch (Exception ex) {
			logger.info("login failure (-1) username=" + userName + ", exception=" + extractMethodNameToString(ex));
			generalUtilLogger.logWrite(ex);
			return "-2";
		}
	}

	/**
	 * 
	 * @param userName
	 *            - login user name
	 * @return ldap name if exists or empty string
	 */
	@Override
	public String getLDAPNameByUserName(String userName) {
		String sql = "select USERLDAP from FG_S_USER_PIVOT where USERNAME = ?";
		String toReturn = "";
		try {
			toReturn = (String) jdbcTemplate.queryForObject(sql, new Object[] { userName }, String.class);
		} catch (Exception e) {
			// DO NOTHING
			toReturn = "";
		}
		return (toReturn == null) ? "" : toReturn;
	}

	/**
	 * 
	 */
	@Override
	public String getUserIdByUserName(String userName) {
		String sql = "select formid from FG_S_USER_PIVOT where USERNAME = ?";
		String toReturn = (String) jdbcTemplate.queryForObject(sql, new Object[] { userName }, String.class);
		return (toReturn == null) ? "" : toReturn;
	}

	/**
	 * 
	 * @param userId
	 * @param oldPass
	 * @param newPass
	 * @return
	 */
	@Override
	public String changePassword(String userId, String oldPass, String newPass) {
		String sql = "update FG_S_USER_PIVOT t set t.password = '" + newPass
				+ "', t.lastpassworddate = TO_CHAR(SYSDATE, '" + generalUtil.getConversionDateFormat()
				+ "') where t.formid = '" + userId + "' and t.password = '" + oldPass + "'";
		String update;
		try {
			update = String.valueOf(jdbcTemplate.update(sql));
		} catch (Exception e) {
			logger.info("changePassword failure (-1) userId=" + userId);
			generalUtilLogger.logWrite(e);
			update = "-1";
		}
		return update;
	}

	@Override
	public void writeLDAPInfo(String info, Throwable ex) {
		try {
			String sql = "insert into fg_activity_log (timestamp,comments,activitylogtype,leveltype,stacktrace)\r\n"
					+ "values (sysdate," + removeUpperComma(info, 4000) + ",'" + LevelType.ERROR.getTypeName() + "','"
					+ ActivitylogType.GeneralError.getActLogTypeName() + "',"
					+ removeUpperComma(extractMethodNameToString(ex), 4000) + ")";

			jdbcTemplate.update(sql);
		} catch (Exception e) {
			// do nothing
		}

	}

	private String removeUpperComma(String val, int maxLength) {
		String toReturn = val;
		if (val != null) {
			toReturn = (toReturn.equals("{}") ? "" : toReturn);
			toReturn = toReturn.replace("'", "");
			toReturn = (toReturn.length()) > maxLength ? toReturn.substring(0, maxLength - 3) + ".." : toReturn;
		} else {
			toReturn = "";
		}
		return "'" + toReturn + "'";
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

	@Override 
	public void writeToAccessLog(String userName, String stationIpAddress, boolean status) {
		Map<String, String> parameters;
		try {
			parameters = new HashMap<String, String>();
			parameters.put("user_name_in", userName);
			parameters.put("ip_address_in", stationIpAddress);
			if (status == true)
			{
				parameters.put("status_in", "YES");
			} else
			{
				parameters.put("status_in", "NO");
			}
			//			System.out.println(parameters);
			generalDao.callPackageFunction("", "FG_AUTHENTICATE_ACCESS", parameters);
		} catch (Exception ex) {
			//logger.info("login failure (-1) username=" + userName + ", exception=" + extractMethodNameToString(ex));
			generalUtilLogger.logWrite(ex);
			
		}
	}

	@Override
	public List<String> getUserFavoriteList(String userId) {
		// TODO Auto-generated method stub
		return generalDao.getListOfStringBySql("select t.object_id from fg_favorite t where t.creator_id = '" + userId + "'");
	}

}
