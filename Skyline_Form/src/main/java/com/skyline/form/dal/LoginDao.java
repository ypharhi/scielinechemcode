package com.skyline.form.dal;

import java.util.List;

public interface LoginDao {

	String authenticateUser(String userName, String password, boolean isLogin, String stationIpAddress);

	String getLDAPNameByUserName(String userName);

	String getUserIdByUserName(String userName);

	String changePassword(String userId, String oldPass, String newPass);

	void writeLDAPInfo(String info, Throwable ex);

	void writeToAccessLog(String userName, String stationIpAddress, boolean status);

	List<String> getUserFavoriteList(String userId);

}
