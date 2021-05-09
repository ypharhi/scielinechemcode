package com.skyline.form.bean;

public class ElementInfoAuditTrailMeta {
	private String formCode;
	private String entityImpCode;
	private String elementClass;
	private String label;
	private boolean isParentPathId = false;
	private boolean isAdditionalData = false;
	private boolean isHidden = false;
	private boolean isSearchIdHolder = false;
	private boolean isIdList = false;
	private boolean isSearchElement = false;
	private DataType dataType;
	
	public ElementInfoAuditTrailMeta(String formCode, String entityImpCode, String elementClass, DataType dataType, String label, boolean isParentPathId, boolean isAdditionalData, boolean isHidden, boolean isSearchIdHolder, boolean isIdList) {
		this.formCode = formCode;
		this.entityImpCode = entityImpCode;
		this.dataType = dataType;
		this.elementClass = elementClass;
		this.label = label.replace("{", "").replace("}", "");
		this.isParentPathId = isParentPathId;
		this.isAdditionalData = isAdditionalData;
		this.isHidden = isHidden;
		this.isSearchIdHolder = isSearchIdHolder;
		this.isIdList = isIdList;
	}
	
	public String getElementClass() {
		return elementClass;
	}
	public void setElementClass(String elementClass) {
		this.elementClass = elementClass;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isParentPathId() {
		return isParentPathId;
	}

	public void setParentPathId(boolean isParentPathId) {
		this.isParentPathId = isParentPathId;
	}

	public boolean isAdditionalData() {
		return isAdditionalData;
	}

	public void setAdditionalData(boolean isAdditionalData) {
		this.isAdditionalData = isAdditionalData;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}
 
	public boolean isSearchIdHolder() {
		return isSearchIdHolder;
	}

	public void setSearchIdHolder(boolean isSearchIdHolder) {
		this.isSearchIdHolder = isSearchIdHolder;
	}

	public String getFormCode() {
		return formCode;
	}

	public void setFormCode(String formCode) {
		this.formCode = formCode;
	}

	public String getEntityImpCode() {
		return entityImpCode;
	}

	public void setEntityImpCode(String entityImpCode) {
		this.entityImpCode = entityImpCode;
	}

	public boolean isIdList() {
		return isIdList;
	}

	public void setIdList(boolean isIdList) {
		this.isIdList = isIdList;
	}

	/**
	 * @return true if inf data is needed for search 
	 *  Basically we put all the save struct data into the _inf/_at tables include the hidden fields (to have all of the information of "what happened" in the system) 
	 *  On the user search form we don't need all of the inf data. 
	 *  In this function we return true if the element will be included in a search from.
	 */
	public boolean isSearchElement() {
		return !isHidden || isSearchIdHolder;
//		// tune it using this ->
//		-- THE LIST OF SEARCH ELEMENTS
//		select T.FORMCODE, T.ENTITYIMPCODE, T.ELEMENTCLASS, T.DISPLAYLABEL 
//		from EDIT_LABEL t 
//		where 1=1
//		(t.formcode, T.ENTITYIMPCODE) IN (
//		SELECT  FG_FORMELEMENTINFOATMETA_MV.FORMCODE, FG_FORMELEMENTINFOATMETA_MV.ENTITYIMPCODE
//		FROM FG_FORMELEMENTINFOATMETA_MV WHERE FG_FORMELEMENTINFOATMETA_MV.ISSEARCHELEMENT = 1)
//		--- WE NEED TO SEE THAT WE DON'T MISS
//		/*OR
//		    (
//		      UPPER(t.displaylabel) Like '%SAMPLE%' OR  
//		       UPPER(t.displaylabel) Like '%REQUEST%' OR  
//		        UPPER(t.displaylabel) Like '%BATCH%' OR  
//		         UPPER(t.displaylabel) Like '%MATERIAL%'
//		    )  */
//
//		--- WE NEED TO SEE THAT WE DON'T NEED
//		OR (
//		      UPPER(t.displaylabel)  Like t.displaylabel  OR
//		      t.displaylabel = UPPER(t.displaylabel)
//		   }
//		)
	}

	public String getDataType() {
		return dataType.getTypeName();
	}

}
