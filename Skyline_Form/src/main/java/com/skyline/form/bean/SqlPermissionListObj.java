package com.skyline.form.bean;

public class SqlPermissionListObj {

	private String sql;
	private String objectId;
	
    public SqlPermissionListObj() {
		
	}
    
    public SqlPermissionListObj(String sql, String objectId) {
		this.sql = sql;
		this.objectId = objectId;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
}
