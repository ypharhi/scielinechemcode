package com.skyline.form.bean;

/**
 * BeanType - Determine what DataBean really is. Note: DataBean is a general type that its: value / code / info properties are fill with different data in different context.
 * @author YPharhi
 *
 */
public enum BeanType {

	ENTETY_IMP_CODE("ENTETY_IMP_CODE"), //value= Imp class
	CATALOG_IMP_CODE("CATALOG_IMP_CODE"), //value= CATALOG Imp class
	CATALOGDB_IMP_CODE("CATALOGDB_IMP_CODE"), //value= CATALOGDB Imp class
	ELEMENT_IMP_CODE("ELEMENT_IMP_CODE"), //value= Imp class
//	ELEMENTDATA_IMP_CODE("ELEMENTDATA_IMP_CODE"), //value= Imp class 
	//ELEMENTDATAFLOW_IMP_CODE("ELEMENTDATAFLOW_IMP_CODE"), //value= Imp class  
	LAYOUT_IMP_CODE("LAYOUT_IMP_CODE"), //value= Imp class
	//ENTETY - meta data (using getInitSchemaVal)
	LAYER_LAYOUT_SCHEMA("LAYER_LAYOUT_SCHEMA"), // code = Imp class, value= Imp schema
	LAYER_CATALOG_SCHEMA("LAYER_CATALOG_SCHEMA"), // code = Imp class, value= Imp schema
	LAYER_ELEMENT_SCHEMA("LAYER_ELEMENT_SCHEMA"), // code = Imp class, value= Imp schema 
	//LAYOUT
	LAYOUT_ITEM_TEXT("LAYOUT_ITEM_TEXT"), //value= impCode.itemName
	LAYOUT_JSP_FILE("LAYOUT_JSP_FILE"), //value = jsp file name 
	//CATALOG - resource bean (using getImpResourceForFormBuilder)
	VARCHAR("CATALOG_ITEM_TEXT"), //value= impCode.itemName
	CATALOG_ITEM_TEXT("CATALOG_ITEM_TEXT"), //value= impCode.itemName
	VARCHAR2("CATALOG_ITEM_TEXT"), // //value= impCode.itemName
	CHAR("CATALOG_ITEM_TEXT"), // //value= impCode.itemName
	CLOB("CATALOG_ITEM_TEXT"),// //value= impCode.itemName
	DATE("CATALOG_ITEM_DATE"), //value= impCode.itemName
	CATALOG_ITEM_DATE("CATALOG_ITEM_DATE"), //value= impCode.itemName
	NUMERIC("CATALOG_ITEM_NUMBER"), //value= impCode.itemName
	CATALOG_ITEM_NUMBER("CATALOG_ITEM_NUMBER"), //value= impCode.itemName
	NUMBER("CATALOG_ITEM_NUMBER"), //value= impCode.itemName
	
	OBJIDVAL("OBJIDVAL"), //value= impCode.itemName WITH TODO desc...
	OBJPARAM("OBJPARAM"), //value= impCode.itemName WITH TODO desc...
	OBJFORMTESTCONF("OBJFORMTESTCONF"), //value= impCode.itemName WITH TODO desc...
	OBJIMG("OBJIMG"), //value= impCode.itemName WITH '{"TITLE":"' || <file title> || '","ID":"' || <file id from FG_files> || '"}' 
	OBJDATERANGE("OBJDATERANGE"),
	//RESOURCE TABLE
	CATALOG_ORACLE_TABLE("CATALOG_ORACLE_TABLE"), //value=oracle table\view name
	CATALOG_ORACLE_CSV_COL_TABLE("CATALOG_ORACLE_CSV_COL_TABLE"), //value=oracle table\view name
	//REPORT TYPES
	FORM_CODE_REPORT("FORM_CODE_REPORT"), // type = FORM_CODE_<FormType>, value=form code by type
	FORM_CODE_GENERAL("FORM_CODE_GENERAL"), // type = FORM_CODE_<FormType>, value=form code by type
	FORM_CODE_LABEL("FORM_CODE_LABEL"), // type = FORM_CODE_<FormType>, value=form code by type 
	//PATH...
	PATH_HTML_POOL("PATH_HTML_POOL"),
	PATH_IREPORT_POOL("PATH_IREPORT_POOL"),
	PATH_JSP_REPORT("PATH_JSP_REPORT"),
	PATH_JSP_GENERAL("PATH_JSP_GENERAL"),
	PATH_JSP_LABEL("PATH_JSP_LABEL"),
	PATH_JSP_STRUCT("PATH_JSP_STRUCT"),
	PATH_JSP_MAINTENANCE("PATH_JSP_MAINTENANCE"),
	PATH_JSP_ATTACHMENT("PATH_JSP_ATTACHMENT"),
	PATH_JSP_INVITEM("PATH_JSP_INVITEM"),
	PATH_JSP_SMARTSEARCH("PATH_JSP_SMARTSEARCH"),
	PATH_JSP_SELECT("PATH_JSP_SELECT"),
	PATH_JSP_REF("PATH_JSP_REF"),
	PATH_JSP("PATH_JSP"),
	//...TODO
	API_IREPORT3_DATAFLOW("API_IREPORT3_DATAFLOW"), //value= json: fileName, title, subTitle, renderType(PDF/EXCEL)
	//NA
	NA("NA"), // When type is not needed
	
	AJAX_BEAN("AJAX_BEAN"), //code = domId , value = element vlaue , type = "ajax bean", info = json with type (attr of inout) and formPreventSave
	AJAX_API_MAINARG("AJAX_API_MAINARG"),
	AJAX_API_CODELIST("AJAX_API_CODELIST"),
	AJAX_API_MATCHELEMENTLIST("AJAX_API_MATCHELEMENTLIST"),
	AJAX_API_MAINARGCODE("AJAX_API_MAINARGCODE"), 
	SAVE_FORM("SAVE_FORM"),
	PRINT_ON_LOAD("PRINT_ON_LOAD")
	;
	private String typeName;

	private BeanType(String s) {
		typeName = s;
	}

	public String getTypeName() {
		return typeName;
	}

}
