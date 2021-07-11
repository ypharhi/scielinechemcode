package com.skyline.customer.adama;

public class SQLObj {
	private String with = "";
	private String select = "";
	private String from = "";
	private String where = "";

	public SQLObj(String with, String select, String from, String where) {
		super();
		this.with = with;
		this.select = select;
		this.from = from;
		this.where = where;
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
	
	@Override
	public String toString() {
		return "SQLObj [with=" + with + ", select=" + select + ", from=" + from + ", where=" + where + "]";
	}
	
	public boolean isEmpty() {
		return with == null || with.isEmpty();
	}
}
