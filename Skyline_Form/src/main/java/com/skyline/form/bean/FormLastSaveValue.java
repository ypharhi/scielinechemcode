package com.skyline.form.bean;

/**
 * DB ENTITY - holds the last saved data chosen by the user using the form.
 * @author YPharhi
 *
 */
public class FormLastSaveValue {

	private Long id;
	private String formid;
	private String formcode;
	private String entityimpcode;
	private String entityimpvalue;
	private String userid;

	public FormLastSaveValue() {
	}

	public FormLastSaveValue(Long id, String formid, String formcode, String entityimpcode, String entityimpvalue,
			String userid) {
		super();
		this.id = id;
		this.formid = formid;
		this.setFormcode(formcode);
		this.setEntityimpcode(entityimpcode);
		this.setEntityimpvalue(entityimpvalue);
		this.setUserid(userid);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFormid() {
		return formid;
	}

	public void setFormid(String formid) {
		this.formid = formid;
	}

	public String getFormcode() {
		return formcode;
	}

	public void setFormcode(String formcode) {
		this.formcode = formcode;
	}

	public String getEntityimpcode() {
		return entityimpcode;
	}

	public void setEntityimpcode(String entityimpcode) {
		this.entityimpcode = entityimpcode;
	}

	public String getEntityimpvalue() {
		return entityimpvalue;
	}

	public void setEntityimpvalue(String entityimpvalue) {
		this.entityimpvalue = entityimpvalue;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	@Override
	public String toString() {
		return "FormLastSaveValue [id=" + id + ",formid=" + formid + ",formcode=" + formcode + ",entityimpcode="
				+ entityimpcode + ",entityimpvalue=" + entityimpvalue + ",userid=" + userid + "]";
	}
}
