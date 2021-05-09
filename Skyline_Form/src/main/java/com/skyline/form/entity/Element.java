package com.skyline.form.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.ElementInfoAuditTrailDisplay;
import com.skyline.form.bean.LevelType;
import com.skyline.form.service.GeneralUtilForm;

public abstract class Element extends Entity implements ElementInterface {

	//init
	
//	private String confirm;
	
	protected boolean confirmBeforeAjax;
	
	protected String confirmBeforeAjaxMessageCode;
	
	protected String label;	
	
	protected boolean hideAlways;	
	
//	public String hideScript;
	
//	public String hiddenState;
	
	protected boolean disableAlways;
	
//	public String disableScript;

//	public String disabledState;	

	protected boolean mandatory;
	
	protected boolean mandatoryAlways;
	
//	public String mandatoryScript;

	protected String layoutBookMark;
 
	protected String parentElement;
	
//	private String defaultValue;
 
	protected String doOnChangeJSCall;

	protected String inputAttribute;
	
	protected boolean keepValueOnParentChange;
	
	protected boolean preventSave;
	
	protected boolean additionalData;
	
	protected boolean resultData;
	
	protected String catalogItem;
	
	private String filterCatalogColumn;
	
	protected boolean IsAjaxDataFlowElement;
	
	protected String saveType;
	
	protected boolean tooltip;
	
	protected boolean displayAdditInfo;
	
	protected String additInfoCustomerFunc;
	 
	protected String additInfoAction;
	
	//	public boolean invIncludeInGrig;
//	
//	public boolean invIncludeInFilter;
	  
	public boolean getIsAjaxDataFlowElement() {
		return IsAjaxDataFlowElement;
	}

	public void setIsAjaxDataFlowElement(boolean isAjaxDataFlowElement) {
		this.IsAjaxDataFlowElement = isAjaxDataFlowElement;
	}
	
	public String getSaveType() {
		return saveType;
	}

	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}

	@Autowired
	public GeneralUtilForm generalUtilForm;
	
//	@Autowired
//	public GeneralUtilFormState generalUtilFormState;
	
	@Value("${developFocusElementList:}")
	private String developFocusElementList = "";

	private static final Logger logger = LoggerFactory.getLogger(Element.class);

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		//{"placeHolder":"","catalogItem":"runC1.SITE","parentElement":"runE1","layoutBookMarkItem":"bookmark11","defaultValue":"","Hidden[0]":"runE1","Hidden[1]":"productE1","Disable[0]":"runE1","Disable[1]":"","Mandatory":"True"}
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{
				//hidden
				hideAlways = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "hideAlways"),false);
//				hideScript = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, initVal, "hideScript"),false);
//				hideScript = generalUtilForm.getJsonVal(stateKey, formCode, initVal, "hideScript");
//				hiddenState = generalUtilForm.getJsonVal(stateKey, formCode, initVal, "hidden"); //when form builder used by programmer its not needed
//				hiddenState = ""; 
				
				//disable
				disableAlways = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "disableAlways"),false);
//				disableScript = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "disableScript"),false);
//				disableScript = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "disableScript");
//				disabledState = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "disable"); //when form builder used by programmer its not needed
//				disabledState = "";
				
				//mandatory
				mandatoryAlways = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "mandatoryAlways"),false);
				mandatory = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "mandatory"),false);
//				mandatoryScript = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "mandatoryScript");

				layoutBookMark = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "layoutBookMarkItem");
//				defaultValue = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "defaultValue");
				label = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "label");
				setParentElement(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "parentElement"));
				keepValueOnParentChange = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "keepValueOnParentChange"), false);
				preventSave = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "preventSave"),false);
				additionalData = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "additionalData"),false);
				resultData = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "resultData"),false);
				tooltip = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "tooltip"),false);
				displayAdditInfo = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "displayAdditInfo"),false);
				additInfoAction = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "additInfoAction");
				additInfoCustomerFunc = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "additInfoCustomerFunc");
				
				setFilterCatalogColumn(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "filterCatalogColumn"));
				
				//inv - develop (for demo)
//				invIncludeInGrig = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "invIncludeInGrig"),false);
//				invIncludeInFilter = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "invIncludeInFilter"),false);
				confirmBeforeAjaxMessageCode = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "confirmBeforeAjaxMessageCode");
				confirmBeforeAjax = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "confirmBeforeAjax"),false);
				return "";
			}
			return "Creation failed";
		}
		catch(Exception e)
		{
			generalUtilLogger.logWrite(e);
			return "Creation failed";
		}
	}
	  
	@Override
	public List<DataBean> getImpResourceForFormBuilder(String formCode, String impCode, String initVal) {
		List<DataBean> dataBeanList = super.getImpResourceForFormBuilder( formCode,  impCode,  initVal); //super return the bean
		dataBeanList.add(new DataBean(impCode, impCode, BeanType.ELEMENT_IMP_CODE, "Info: " + impCode));
		return dataBeanList;
	}

	@Override
	public String getInitSchemaVal() {
		//hidden / disable using array of elements -> The rule: if we have element and his value is empty ("") we disable this element in run time
		String schema = super.getInitSchemaVal();
		schema += 	
				"	layoutBookMarkItem:{  \r\n" +
				" type:'string',\r\n" +
				" title:'Layout BookMark Item',\r\n" +
				" 'enum':getResourceValueByType('LAYOUT_ITEM_TEXT')\r\n" +
				"   },\r\n" +
				"   label:{  \r\n" +
				" type:'string',\r\n" +
				" title:'Label (should match the text in the label element)'\n" +
				"   },\r\n" +
				"	defaultValue:{   \r\n" +
				"		type:'string', \r\n" +
				"		title:'Default Value' \r\n" +
				"	}, \r\n" +
				"	filterCatalogColumn:{   \r\n" +
				"		type:'string', \r\n" +
				"		title:'Filter Catalog Item Name [using the element name when empty] (filtering by matching with column names of lower level elements SQLs)' \r\n" +
				"	}, \r\n" +
				"   parentElement:{  \r\n" + 
				" type:'string',\r\n" + 
				" title:'Parent Element',\r\n" + 
				" 'enum':[''].concat(getResourceValueByType('ELEMENT_IMP_CODE'))\r\n" + 
				"   },\r\n" +
				"	confirmBeforeAjax:{\r\n" + 
				" type: 'boolean',\r\n" + 
				" title: 'Confirm Before Change'\r\n" + 
				"   },\r\n" +
				"	confirmBeforeAjaxMessageCode:{\r\n" + 
				" type: 'string',\r\n" + 
				" title: 'Confirm Before Change Message Code (ELEMENT_GENERAL_CONFIRM as default @ELEMENT_DISPLAY_VALUE@ will replace with element display value)'\r\n" + 
				"   },\r\n" +
				"	keepValueOnParentChange:{\r\n" + 
				" type: 'boolean',\r\n" + 
				" title: 'Keep Value on parent change'\r\n" + 
//				" 'default': 'true'\r\n" + 
				"   },\r\n" +
				"	preventSave:{\r\n" + 
				" type: 'boolean',\r\n" + 
				" title: 'Prevent save (if checked will be taken from the defualt value)'\r\n" +
				"   },\r\n" +
				"	searchId:{\r\n" + 
				" type: 'boolean',\r\n" + 
				" title: 'Search ID holder (for hidden element that holds a search ID)'\r\n" +
				"   },\r\n" +
				"	additionalData: { \r\n" +
				" type: 'boolean',\r\n" + 
				" title: 'Is additional data (if checcked will be saved as additional data in FG_FORMADDITIONALDATA)'\r\n" + 
				"	},\r\n" +
				"	resultData: { \r\n" +
				" type: 'boolean',\r\n" + 
				" title: 'Is save as result (default false)'\r\n" + 
				"	},\r\n" +
				"	hideAlways:{\r\n" + 
				" type: 'boolean',\r\n" + 
				" title: 'Hide Always'\r\n" + 
				"   },\r\n" +
				"	hideScript:{   \r\n" +
				"		type:'string', \r\n" +
				"		title:'Hide script (evaluate on init and parent change)' \r\n" +
				"	}, \r\n" +
				"	hideComment:{   \r\n" +
				"		type:'string', \r\n" +
				"		title:'Hide reason comment (use message code) append to authz element info.' \r\n" +
				"	}, \r\n" +
//				"	hidden : {\r\n" +
//				"		type : 'array',\r\n" +
//				"		title : 'Hidden',\r\n" +
//				"		items : {\r\n" +
//				"			enum : [''].concat(getResourceValueByType('ELEMENT_IMP_CODE')),\r\n" +
//				"			title : 'When No value in parent element:'\r\n" +
//				"		}\r\n" +
//				"	},\r\n" +
				"	disableAlways:{\r\n" + 
				" type: 'boolean',\r\n" + 
				" title: 'Disabled Always'\r\n" + 
				"   },\r\n" +
				"	disableScript:{   \r\n" +
				"		type:'string', \r\n" +
				"		title:'Disabled script (evaluate on init and parent change)' \r\n" +
				"	}, \r\n" +
				"	disableComment:{   \r\n" +
				"		type:'string', \r\n" +
				"		title:'Disabled reason comment (use message code) append to authz element info.' \r\n" +
				"	}, \r\n" +
//				"	disable : {\r\n" +
//				"		type : 'array',\r\n" +
//				"		title : 'Disabled',\r\n" +
//				"		items : {\r\n" +
//				"			enum : [''].concat(getResourceValueByType('ELEMENT_IMP_CODE')),\r\n" +
//				"			title : 'When No value in parent element:'\r\n" +
//				"		}\r\n" +
//				"	}"
				"	mandatoryAlways: { \r\n" +
				" type: 'boolean',\r\n" + 
				" title: 'Mandatory Always'\r\n" + 
				"	}, \r\n" +
				"mandatory : { \r\n" +
				" type: 'boolean',\r\n" + 
				" title: 'Mandatory (when element enabled)'\r\n" + 
				"	}, \r\n" +
				"mandatoryScript:{   \r\n" +
				"		type:'string', \r\n" +
				"		title:'Mandatory script (evaluate on init and parent change)' \r\n" +
				"   },\r\n" + 
				"   tooltip:{  \r\n" + 
				"      type: 'boolean',\r\n" + 
				"      title:'Tooltip(value)',\r\n" +			
				"   },\r\n" +
				"displayAdditInfo:{  \r\n" + 
				"		type: 'boolean',\r\n" +  
				"		title:'Display additional info'\r\n" + 
				"   }, \r\n" +
				" additInfoCustomerFunc:{  \r\n" + 
				" 	type:'string',\r\n" + 
				" 	title:'Additional info function (evaluate if Display Additional Info checked)',\r\n" + 		
				" },\r\n" +	 
				" additInfoAction:{  \r\n" + 
				" 	type:'string',\r\n" + 
				" 	title:'Additional info action parameter',\r\n" + 		
				" }"
//				"elementFS:{  \r\n" +
//				" type:'textarea',\r\n" +
//				//" format: 'textarea',\r\n" +
//				" title:'Element FS'\r\n" +
//				"   },\r\n" +
//				"elementFSGap:{  \r\n" +
//				" type:'textarea',\r\n" +
//				" title:'Element FS GAP'\r\n" +
//				"   },\r\n" +
//				"elementComment:{  \r\n" +
//				" type:'textarea',\r\n" +
//				" title:'Element Comment'\r\n" +
//				"   }\r\n";
//				//inventory...
//				"	invIncludeInGrig:{\r\n" + 
//				" type: 'boolean',\r\n" + 
//				" title: 'Invntory - include in grid'\r\n" + 
////				" 'default': 'true'\r\n" + 
//				"   },\r\n" +
//				"	invIncludeInFilter:{\r\n" + 
//				" type: 'boolean',\r\n" + 
//				" title: 'Invntory - include in filter'\r\n" + 
////				" 'default': 'true'\r\n" + 
//				"   }\r\n"
				;
		return schema;
	}

	@Override
	public String getLayoutBookMark() {
		return layoutBookMark;
	}

	@Override
	public void setLayoutBookMark(String layoutBookMark) {
		this.layoutBookMark = layoutBookMark;
	}

//	public String getHiddenState() {
//		return hiddenState;
//	}
//
//	public String getDisabledState() {
//		return disabledState;
//	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	public Map<String, String> getInitHtml(long stateKey, String formId, boolean renderEmptyData, String inputVal, String userLastSaveVal, String domId,
			String inputAttribute, String doOnChangeJSCall) {
		logger.info("getInitHtml element=" + impCode + ", inputVal=" + inputVal + ", jsonInit=" + initVal);
		
		//for develop use (focus on element list)
		if(developFocusElementList != null && !developFocusElementList.isEmpty()) {
			if(!developFocusElementList.contains(domId)) {
				return new HashMap<String,String>();
			}
		}
		
		boolean isHiddenHolder;
		boolean isDisabledHolder;
		boolean ismandatoryHolder;
		
		boolean isHidden = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "hideScript"), false, impCode) ||
				hideAlways;
		isHiddenHolder = isHidden;
		boolean isDisabled = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "disableScript"), false, impCode) ||
				disableAlways;
		isDisabledHolder = isDisabled;
		boolean ismandatory = mandatoryAlways || (!isHidden && !isDisabled && (mandatory || generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "mandatoryScript"), false, impCode)));
		ismandatoryHolder = ismandatory;
		logger.debug("getInitHtml summary element=" + impCode + ", value=" + inputVal + ", isHidden=" + isHidden + ", isDisabled="+ isDisabled + ", isMandatory = " + ismandatory);
		if(isShowElementInfo()) { //change to system in order to show all
			logger.info("system sees ALL!");
			isHidden = isOverrideHMD("h", isHidden);
			isDisabled = isOverrideHMD("d", isDisabled);
			ismandatory = isOverrideHMD("m", ismandatory);
			/*displayAdditInfo = true;
			if(initVal.contains("domElementType") && additInfoCustomerFunc.isEmpty())//authz
			{
				additInfoCustomerFunc = "getGeneralInfo";
			}*/
		}
		//confirmObject 
		if(confirmBeforeAjax) {
//			String message_ = "('<div style=\"font-weight: bold;\">" + generalUtil.getSpringMessagesByKey(generalUtil.getEmpty(confirmBeforeAjaxMessageCode, "ELEMENT_GENERAL_CONFIRM"), "") + "</div>').replace('@ELEMENT_DISPLAY_VALUE@', getElementsDisplayValue('" + domId + "'))";
//			String mandatoryMessage_ = "('<div style=\"font-weight: bold;\">This field is mandatory</div>')";

//			String doOnChangeJSCallMandatoryEmpty_ = "openConfirmDialog({'onConfirm': onChangeAjax,"
//					+ "'message':" + mandatoryMessage_ + ","
//					+ "'title':'Warning',"
//					+ "'onConfirm': elementRollBack,"
//					+ "'onConfirmParams':'" + domId + "'},true)";
			
//			String doOnChangeJSCall_ = "openConfirmDialog({'onConfirm': onChangeAjax,"
//					+ "'message':" + message_+ ","
//					+ "'title':'Warning',"
//					+ "'onConfirmParams':'" + domId + "',"
//					+ "'onCancel':elementRollBack,"
//					+ "'onCancelParams':'" + domId + "'})";
			
//			doOnChangeJSCall = ismandatory? "(getElementsDisplayValue('" + domId + "') == '')?" + doOnChangeJSCallMandatoryEmpty_ + ":" + doOnChangeJSCall_:doOnChangeJSCall_;
			 
			
			String message_ = "('<div style=\"font-weight: bold;\">" + generalUtil.getSpringMessagesByKey(generalUtil.getEmpty(confirmBeforeAjaxMessageCode, "ELEMENT_GENERAL_CONFIRM"), "") + "</div>').replace('@ELEMENT_DISPLAY_VALUE@', (getElementsDisplayValue('" + domId + "') == null || getElementsDisplayValue('" + domId + "')) == '' ? 'NA' : getElementsDisplayValue('" + domId + "'))";
			String doOnChangeJSCall_ = "openConfirmDialog({'onConfirm': onChangeAjax,"
					+ "'message':" + message_+ ","
					+ "'title':'Warning',"
					+ "'onConfirmParams':'" + domId + "',"
					+ "'onCancel':elementRollBack,"
					+ "'onCancelParams':'" + domId + "'});";
			doOnChangeJSCall = doOnChangeJSCall_;
			 
		}
				
		Map<String,String> intHtml = new HashMap<String,String>();
		intHtml = getInitHtml(stateKey, formId, renderEmptyData, inputVal, userLastSaveVal, domId, inputAttribute, doOnChangeJSCall, isHidden, isDisabled,
				ismandatory); //(doOnChangeJSCall.toLowerCase().equals("true") ? true : false) -> generalUtil.getBooleanByExpression(mandatoryState) (yp TODO - ask eyal why doOnChangeJSCall.toLowerCase().equals("true") ? true : false))
				
		if(displayAdditInfo)
		{
			String additInfoBookMark = intHtml.get(layoutBookMark);			
			if(additInfoCustomerFunc.equals("getGeneralInfo"))
			{
				/* info icon display code separated because of display problem when icon added in jspTemplate(near "New" button) */
				additInfoBookMark = generalUtil.getNull(additInfoBookMark) + "\r\n" + 
									"<span class=\"iconAdditCustomInfo\"><i  onclick=\"customInfoClickEvent('" + additInfoCustomerFunc + "','" + additInfoAction + "')\" style=\"cursor: pointer;\" title=\"Additional Info\" class=\"fa fa-info\"></i></span>";
			}
			else
			{
				additInfoBookMark = generalUtil.getNull(additInfoBookMark) + "\r\n" + 
						"<div class=\"divAdditCustomInfo\">"+
							"<span><i  onclick=\"customInfoClickEvent('" + additInfoCustomerFunc + "','" + additInfoAction + "')\" style=\"cursor: pointer;\" title=\"Status Info\" class=\"fa fa-info\"></i></span>"+ //color: #2779aa;font-size: 13pt;
						"</div>";
			}
		    intHtml.put(layoutBookMark, additInfoBookMark);
		}
		
		String thislayoutBookMark = intHtml.get(layoutBookMark);
		
		String sqlFormEntityScript= "/* EXECUTE IMMEDIATE ' CREATE table skyline_form_server.fg_formentity_' || to_char(sysdate,'DDMMHH24MISS') || ' as select * from skyline_form_server.fg_formentity where t.formcode = ''' || " + formCode + " || ''' and t.entityimpcode = ''' || " + impCode + " || ''' ';\r\n" + 
				"delete from skyline_form_server.fg_formentity t where t.formcode = '" + formCode + "' and t.entityimpcode = '" + impCode + "'\r\n" + 
				"insert into skyline_form_server.fg_formentity select t.* from skyline_form.fg_formentity t where t.formcode = '" + formCode + "' and t.entityimpcode = '" + impCode + "' */";
		 
		// element info data
		try {
			if(isShowElementInfo()) {
				if(showInfoByClassName() && !isOverrideHMD("h", isHidden)) {
					thislayoutBookMark = generalUtil.getNull(thislayoutBookMark) + "\r\n" + 
	//						"<a id=\"" + impCode + "_infoElement\" ATTR_INFO_ELEMENT_HREF=\"1\" impCode=\"" + impCode + "\"  runat=\"server\" href=\"#\">" + "[" + impCode + "]" + generalUtil.getNull(getLabel()) + ((parentElement.equals(""))?"":"[->" + parentElement + "]") + ((isHiddenHolder)?"[h]":"") + ((ismandatoryHolder)?"[m]":"") + ((isDisabledHolder)?"[d]":"") + ((preventSave)?"[!s]":"[s]") + "</a>" +
							"<a id=\"" + impCode + "_infoElement\" ATTR_INFO_ELEMENT_HREF=\"1\" impCode=\"" + impCode + "\"  runat=\"server\" href=\"#\">" +  impCode + ((parentElement.equals(""))?"":"[->" + parentElement + "]") + ((isHiddenHolder)?"[h]":"") + ((ismandatoryHolder)?"[m]":"") + ((isDisabledHolder)?"[d]":"") + ((preventSave)?"[!s]":"[s" + (isAdditionalData()?"a":"") + "]") + "</a>" +
							"<div style=\"overflow-y: auto;\" infoElementSql=\"\" id=\"" + impCode + "_infoElemnetDialog\" ATTR_INFO_ELEMENT_DIV=\"1\" title=\"" + formCode + "." + impCode + "\">\r\n" + 
							"    <p style=\"color:blue;\">init json:</p><p>" + initVal.replace(",", ", ") + "</p>    \r\n" + 
							"    <p style=\"color:blue;\">form builder SQL:</p><p>select t.*,t.rowid from fg_formentity t where t.formcode = '" + formCode + "' and t.entityimpcode = '" + impCode + "'</p>\r\n" + 
							"    <p style=\"color:blue;\">form builder SQL (set server fg_formentity script):</p><p>" + sqlFormEntityScript.replace("\n", "<br />") + "</p>\r\n" + 
							"    <p style=\"color:blue;\">form builder Configuration:</p><p>/Adama/skylineForm/demoFormBuilderInit.request?formCode=" + formCode + "&update=true</p>\r\n" + 
							"    <p style=\"color:blue;\">inputVal:</p><p>" + inputVal + "</p>\r\n" +
							"    <p style=\"color:blue;\">isHidden/ isDisabled/ ismandatory:</p><p>isHidden=" + isHidden + ", isDisabled="+ isDisabled + ", isMandatory = " + ismandatory + "</p>\r\n" + 
							"    <p style=\"color:blue;\">catalog SQL:</p><p id=\"CATALOG_SQL_INFO\">" + generalUtil.getNull(intHtml.get(domId + "_elementSQLInfo")) + "</p>\r\n" + 
							"    <p style=\"color:blue;\">data table sql:</p><p id=\"DATA_TABLE_SQL_INFO\"></p>\r\n" +
							"    <p style=\"color:blue;\">from param (render time info):</p><p>" + generalUtil.mapToString("", generalUtilFormState.getFormParam(stateKey, formCode)).replace("\n", "<br />") + "</p>\r\n" + 
							"    <p style=\"color:blue;\">from catalog filter (render time info):</p><p>" + generalUtil.mapToString("", generalUtilFormState.getFormCatalog(stateKey, formCode, null)).replace("\n", "<br />") + "</p>\r\n" + 
							"    <p style=\"color:blue;\">from value (render time info):</p><p>" + generalUtil.mapToString("", generalUtilFormState.getFormValue(stateKey, formCode)).replace("\n", "<br />") + "</p>\r\n" + 
							//				"    <p style=\"color:blue;\">from catalog filter (render time info):</p><p>" + generalUtilFormState.getSummary(formCode,true).replace("\n", "<br />") + "</p>\r\n" + 
							"</div>";
					intHtml.put(layoutBookMark, thislayoutBookMark);
				}
			}
		} catch (Exception e) {
			logger.warn("Error in adding element info! e=" + e.toString());
		} 
		
		return intHtml;
	}


	public String getHtmlBody(long stateKey, String formId, boolean renderEmptyData, String inputVal, String domId, String inputAttribute,
			String doOnChangeJSCall) {
		//for develop use (focus on element list)
		if(developFocusElementList != null && !developFocusElementList.isEmpty()) {
			if(!developFocusElementList.contains(domId)) {
				return "";
			}
		}
		
		logger.debug("getHtmlBody element=" + impCode + ", inputVal=" + inputVal);
		boolean isHidden = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "hideScript"), false, impCode) ||
				hideAlways;
		boolean isDisabled = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "disableScript"), false, impCode) ||
				disableAlways;
		boolean ismandatory =  mandatoryAlways || (!isHidden && !isDisabled && (mandatory || generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "mandatoryScript"), false, impCode)));
		logger.debug("getHtmlBody summary element=" + impCode + ", value=" + inputVal + ", isHidden=" + isHidden + ", isDisabled="+ isDisabled + ", isMandatory = " + ismandatory);
		if(generalUtil.getSessionUserName().equalsIgnoreCase("system")) { //change to system in order to show all
			logger.info("system sees ALL!");
			isHidden = isOverrideHMD("h", isHidden);
			isDisabled = isOverrideHMD("d", isDisabled);
			ismandatory = isOverrideHMD("m", ismandatory);
		}
		return getHtmlBody(stateKey, formId, renderEmptyData, inputVal, domId, inputAttribute, doOnChangeJSCall, isHidden, isDisabled,
				ismandatory); //(doOnChangeJSCall.toLowerCase().equals("true") ? true : false) -> generalUtil.getBooleanByExpression(mandatoryState) (yp TODO - ask eyal why doOnChangeJSCall.toLowerCase().equals("true") ? true : false))
	
	}

	public String isLabel(boolean isHidden){
//		if(!getLabel().equals("")){			
//			return "<div class='labelDiv cssStaticData' style='text-align:left;" + ((isHidden) ? "visibility:hidden;":"") + "'><span>&nbsp;"+ getLabel() +":</span><br>";
//		}
		return "";
	}
	public String isLabelEnd(){
//		if(!getLabel().equals("")){			
//			return "</div>";
//		}
		return "";
	}
	
	public String getDefaultValue(long stateKey, String formId, String formCode) {
		String dv_ = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "defaultValue");
		if(dv_.contains("$P{")) {
			generalUtilLogger.logWrite(LevelType.DEBUG, "default value not found for element[impCode]=" + impCode + "value=" + dv_, "", ActivitylogType.InfoLookUp, null);
			dv_= "";
		}
		return dv_;
	}
	
	public String getParentElement() {
		// TODO Auto-generated method stub
		return parentElement;
	}
 
	public boolean isKeepValueOnParentChange() {
		return keepValueOnParentChange;
	}
	
	public boolean isTooltip() {
		return tooltip;
	}
	
	public boolean isPreventSave() {
		return preventSave;
	}
	
	public boolean isAdditionalData() {
		return additionalData;
	}
	 
	@Override
	public Map<String, String> getCatalogItemFilterMapByInputVal(String inputVal, boolean isCurrentElementChange) {
		// TODO Auto-generated method stub
		return new HashMap<String, String>();
	}
	
	@Override
	public Map<String, String> getCurrentParameMapByInputVal(String inputVal) {
		return new HashMap<String,String>(); 
	}
	
//	@Override
//	public boolean isCatalogFlowElement() {
//		return false;
//	}
	

	
//	@Override
//	public void setReflectionByInputVal(String inputVal) {
//		//do nothing
//	} 
	
	@Override
	public ElementInfoAuditTrailDisplay getAuditTrailValue(String formCode, String key, String postSaveValue, String originValue, String uiDisplayValue) {
		return new ElementInfoAuditTrailDisplay(postSaveValue,"0");
	}
	
//	@Override
//	public ElementInfoAuditTrailDisplay getAuditTrailValue(String postSaveValue, boolean isSaveForm) {
//		return new ElementInfoAuditTrailDisplay(postSaveValue,"1");
//	}

	public void setParentElement(String parentElement) {
		this.parentElement = parentElement;
	}

	public String getFilterCatalogColumn() {
		return filterCatalogColumn;
	}

	public void setFilterCatalogColumn(String filterCatalogColumn) {
		this.filterCatalogColumn = filterCatalogColumn;
	}

	public boolean isValExists(String lastsSelectedValue) {
		// TODO Auto-generated method stub
		return (lastsSelectedValue != null && !lastsSelectedValue.trim().equals(""));
	}
	
	public boolean isHideAlways() {
		return hideAlways;
	}
	
	private boolean isOverrideHMD(String mdh, boolean defaultVal) {
		boolean toReturn = defaultVal;
		try {
			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			String[] params_ = attr.getRequest().getParameterValues("override" + mdh);
			if (params_ != null && params_[0].equals("1")) {
				toReturn = false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return toReturn;
	}
	
	private boolean isShowElementInfo() {
		boolean toReturn = false;
		try {
			toReturn = generalUtil.getSessionUserName().equalsIgnoreCase("system");
			if(!toReturn) {
				ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
				String[] params_ = attr.getRequest().getParameterValues("showinfo");
				toReturn = params_ != null && params_[0].equals("1");
			}
		} catch (Exception e) {
			// do nothing
		}
		return toReturn;
	}
	
	private boolean showInfoByClassName() {
		boolean toReturn = true;
		try {
			String calssName = super.getClass().getSimpleName();
			if(calssName.equalsIgnoreCase("ElementLabelImp")) {
				toReturn = false;
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			// do nothing
		}
		return toReturn;
	}
	
}
