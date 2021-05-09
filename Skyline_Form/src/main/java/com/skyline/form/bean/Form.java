package com.skyline.form.bean;

/**
 * DB entity that holds the list of form defined by the form builder in the application
 * 
 * @author YPharhi
 *
 */
public class Form {

	// DB id
	private Long id;
	// formCode - getter all FormEntity under one Form
	private String formCode;
	private String description;
	private String active;
	// FormType as in the Enum com.skyline.form.bean.FormType (it is a string because this Form class is a DB entity)
	private String formType;
	private String title;
	private String subtitle;
	private String useAsTemplate;
	private String groupName;
	private int numerOfOrder;
	private String formCodeEntity;
	private String ignoreNav; // ignore navigation (prevent push into the navigation stack)
	private String useCache;
	
	public Form() {

	}

	public Form(String formCode, String description, String active, String formType, String title, String subtitle,
			String useAsTemplate, String groupName, int numerOfOrder, String formCodeEntity, String ignoreNav,
			String useCache) {
		super();
		this.formCode = formCode;
		this.description = description;
		this.active = active;
		this.formType = formType;
		this.title = title;
		this.subtitle = subtitle;
		this.setUseAsTemplate(useAsTemplate);
		this.setGroupName(groupName);
		this.setNumerOfOrder(numerOfOrder);
		this.setFormCodeEntity(formCodeEntity);
		this.setIgnoreNav(ignoreNav);
		this.useCache = useCache;

	}

	public Form(Long id, String formCode, String description, String active, String formType, String title,
			String subtitle, String useAsTemplate,String groupName,int numerOfOrder, String formCodeEntity, String ignoreNav, String useCache) {
		super();
		this.id = id;
		this.formCode = formCode;
		this.description = description;
		this.active = active;
		this.formType = formType;
		this.title = title;
		this.subtitle =subtitle;
		this.setUseAsTemplate(useAsTemplate);
		this.setGroupName(groupName);
		this.setNumerOfOrder(numerOfOrder);
		this.setFormCodeEntity(formCodeEntity);
		this.setIgnoreNav(ignoreNav);
		this.useCache = useCache;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getTitle() {
		if(title != null && title.equals("SelfTest")) {
			title = "Self-Test";
		}
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUseAsTemplate() {
		return useAsTemplate;
	}

	public void setUseAsTemplate(String useAsTemplate) {
		this.useAsTemplate = useAsTemplate;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getNumerOfOrder() {
		return numerOfOrder;
	}

	public void setNumerOfOrder(int numerOfOrder) {
		this.numerOfOrder = numerOfOrder;
	}

	public String getFormCodeEntity() {
		return formCodeEntity;
	}

	public void setFormCodeEntity(String formCodeEntity) {
		this.formCodeEntity = formCodeEntity;
	}

	public String getIgnoreNav() {
		return ignoreNav;
	}

	public void setIgnoreNav(String ignoreNav) {
		this.ignoreNav = ignoreNav;
	}

	public String getUseCache() {
		return useCache;
	}

	public void setUseCache(String useCache) {
		this.useCache = useCache;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Form other = (Form) obj;
		if (formCode == null) {
			if (other.formCode != null)
				return false;
		} else if (!formCode.equals(other.formCode))
			return false;
		return true;
	}

}
