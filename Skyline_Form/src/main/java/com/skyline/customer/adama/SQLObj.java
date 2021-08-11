package com.skyline.customer.adama;

public class SQLObj {
	private String with = "";
	private String select = "";
	private String hiddenselect = "";
	private String from = "";
	private String where = "";
	private String jsonColNames = "";

	public SQLObj(String with, String hiddenselect, String select, String from, String where, String jsonColNames) {
		super();
		this.with = with;
		this.setHiddenselect(hiddenselect);
		this.select = select;
		this.from = from;
		this.where = where;
		this.jsonColNames = jsonColNames;
	}

	public String getWith() {
		return with;
	}

	public void setWith(String with) {
		this.with = with;
	}

	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getHiddenselect() {
		return hiddenselect;
	}

	public void setHiddenselect(String hiddenselect) {
		this.hiddenselect = hiddenselect;
	}
	
	public boolean isEmpty() {
		return with == null || with.isEmpty();
	}

	public String getJsonColNames() {
		return jsonColNames;
	}

	public void setJsonColNames(String jsonColNames) {
		this.jsonColNames = jsonColNames;
	}

	@Override
	public String toString() {
		return "SQLObj [with=" + with + ", select=" + select + ", hiddenselect=" + hiddenselect + ", from=" + from
				+ ", where=" + where + ", jsonColNames=" + jsonColNames + "]";
	}
	
}
