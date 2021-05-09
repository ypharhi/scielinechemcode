package com.skyline.form.bean;

import org.json.JSONObject;

import com.skyline.form.entity.Entity;

/**
 * DB entity that holds the list of the form Entities. 
 * formCode is used to getter multiple entities under the same form (formCode is a unique identifier of a Form).
 * entity
 * @author YPharhi
 *
 */
public class FormEntity { 
	// DB id
	private Long id;
	//refers to Form (getter multiple entities under the same form)
	private String formCode;
	private int order;
	// entityType: Catalog / Layout / Element. Note: Entity is the upper class in the hierarchy (implements common behavior of the Catalog, Layout & Element entityImpClass) 
	private String entityType;
	//entityImpCode: the class from the pool that implements the entity (defined by the form builder [form builder = A user in the application with the authorization to create a form])
	private String entityImpClass;
	//entityImpCode: a unique name for the entityImpClass instance, given by the form builder.
	private String entityImpCode;
	// the initiation value (Json string) for the entityImpClass instance, defined by the form builder.
	private String entityImpInit; 
	
	private Entity entity = null;
	
	private String entityLabel = "";
	
	private boolean globalInit = false;
	
//Entity entity = entityFactory.getEntity(formEntity.getEntityImpClass());
	public FormEntity() {

	}

	public FormEntity(String formCode, int order, String entityType, String entityImpCode, String entityImpClass,
			String entityImpInit) {
		super();
		this.formCode = formCode;
		this.order = order;
		this.entityType = entityType;
		this.entityImpCode = entityImpCode;
		this.entityImpClass = entityImpClass;
		this.entityImpInit = entityImpInit;
		try {
			if(entityType != null && entityType.equals("Element") && entityImpInit != null && entityImpInit.trim().startsWith("{")) { //TODO not start with { when can be if the call is for the formbuilder table using formentity_v (not need label but better fix it)
				JSONObject jsonObjectiEntityImpInit = new JSONObject(entityImpInit);
				if(jsonObjectiEntityImpInit.has("label")) {
					this.entityLabel = (String)jsonObjectiEntityImpInit.get("label");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public FormEntity(Long id, String formCode, int order, String entityType, String entityImpCode,
			String entityImpClass, String entityImpInit) {
		super();
		this.id = id;
		this.formCode = formCode;
		this.order = order;
		this.entityType = entityType;
		this.entityImpCode = entityImpCode;
		this.entityImpClass = entityImpClass;
		this.entityImpInit = entityImpInit;
		try {
			if(entityType != null && entityType.equals("Element") && entityImpInit != null && entityImpInit.trim().startsWith("{")) { //TODO not start with { when can be if the call is for the formbuilder table using formentity_v (not need label but better fix it)
				JSONObject jsonObjectiEntityImpInit = new JSONObject(entityImpInit);
				if(jsonObjectiEntityImpInit.has("label")) {
					this.entityLabel = (String)jsonObjectiEntityImpInit.get("label");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFormCode() {
		return formCode;
	}

	public void setFormCode(String formCode) {
		this.formCode = formCode;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getEntityImpCode() {
		return entityImpCode;
	}

	public void setEntityImpCode(String entityImpCode) {
		this.entityImpCode = entityImpCode;
	}

	public String getEntityImpClass() {
		return entityImpClass;
	}

	public void setEntityImpClass(String entityImpClass) {
		this.entityImpClass = entityImpClass;
	}

	public String getEntityImpInit() {
		return entityImpInit;
	}

	public void setEntityImpInit(String entityImpInit) {
		this.entityImpInit = entityImpInit;
	}

	@Override
	public String toString() {
		return "FormEntity [id=" + id + ", formCode=" + formCode + ", order=" + order + ", entityType=" + entityType
				+ ", entityImpCode=" + entityImpCode + ", entityImpClass=" + entityImpClass + ", entityImpInit="
				+ entityImpInit + "]";
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public boolean isGlobalInit() {
		return globalInit;
	}

	public void setGlobalInit(boolean globalInit) {
		this.globalInit = globalInit;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entityImpCode == null) ? 0 : entityImpCode.hashCode());
		result = prime * result + ((formCode == null) ? 0 : formCode.hashCode());
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
		FormEntity other = (FormEntity) obj;
		if (entityImpCode == null) {
			if (other.entityImpCode != null)
				return false;
		} else if (!entityImpCode.equals(other.entityImpCode))
			return false;
		if (formCode == null) {
			if (other.formCode != null)
				return false;
		} else if (!formCode.equals(other.formCode))
			return false;
		return true;
	}

	public String getEntityLabel() {
		return !entityLabel.isEmpty() && !entityLabel.equalsIgnoreCase("na") ? entityLabel : parseEntityImpCodeTooLabel();
	}

	private String parseEntityImpCodeTooLabel() {
		// TODO if needed
		return entityImpCode;
	}
}
