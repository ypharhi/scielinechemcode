package com.skyline.form.bean;

public class FormUser {

	private int userId;
	private String userName;
	private String role;

	public FormUser() {
		super();
		this.setUserName("");
		this.setRole("");
	}

	public FormUser(int userId, String userName, String role) {
		super();
		this.setUserId(userId);
		this.setUserName(userName);
		this.setRole(role);
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
