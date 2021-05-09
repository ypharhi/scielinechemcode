package com.skyline.form.bean;

import java.io.IOException;
import java.io.Serializable;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class SearchTreeBean implements Serializable {

	private static final long serialVersionUID = 1L;

	class SearchTreeAttrBean {

		String formId;
		String struct;
		String title;

		public SearchTreeAttrBean(String formId, String struct, String title) {
			super();
			this.formId = formId;
			this.struct = struct;
			this.title = title;
		}

		public String getFormId() {
			return formId;
		}

		public void setFormId(String formId) {
			this.formId = formId;
		}

		public String getStruct() {
			return struct;
		}

		public void setStruct(String struct) {
			this.struct = struct;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}

	String id;
	String parent;
	String text;
	String formcode_entity;
	SearchTreeAttrBean a_attr;

	public SearchTreeBean(String id, String parent, String text, String attrFormId, String attrStruct,
			String attrTitle) {
		super();
		this.id = id;
		this.parent = parent;
		this.text = text;
		this.a_attr = new SearchTreeAttrBean(attrFormId, attrStruct, attrTitle);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public SearchTreeAttrBean getA_attr() {
		return a_attr;
	}

	public void setA_attr(SearchTreeAttrBean a_attr) {
		this.a_attr = a_attr;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getFormCode_entity() {
		return formcode_entity;
	}

	public void setFormCode_entity(String formcode_entity) {
		this.formcode_entity = formcode_entity;
	}

	public String toJsonString() {
		String toReturn = "";
		try {
			toReturn = new ObjectMapper().writeValueAsString(this);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toReturn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchTreeBean other = (SearchTreeBean) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
