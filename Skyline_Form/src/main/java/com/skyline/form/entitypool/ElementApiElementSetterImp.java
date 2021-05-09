package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Map;

import com.skyline.form.entity.Element;

/**
 * API Element Setter - used to retrieve data from API and update elements
 * 
 * The API element setter collects data from all elements in the page,
 * sends it to the server for processing (the process retrieves data from API or calculates the data),
 * and update the selected elements in the page with the new data.
 * 
 * The API element setter can be displayed as
 * 1) textbox with icon
 * 2) textbox only 
 * 3) icon only
 * 4) checkbox only
 * 
 *  the textbox can be declared as numbers only input
 *  
 *  
 *  The schema's matchElements declare the elements that will be updated with the new data
 *  The schema's apiCodes declare the elements that will be sent to the API
 *  
 *  The order is important because we update the match's Elements by the order of apiCodes' elements
 *
 *  The apiCodes' elements list is declared in getApiCodes() in this class
 *  
 */
public class ElementApiElementSetterImp extends Element {

	private String placeHolder, width, apiCodes, matchElements, type;//, minValue; // minValue not in use
	
	private boolean hideIcon, hideText;
	
    private String precision, min,max,parentMin,parentMax;	 
	
	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try {
			if (super.init(stateKey, formCode, impCode, initVal).equals("")) {
				placeHolder = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "placeHolder");				
				width = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "width");
				apiCodes = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "apiCodes");
				matchElements = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "matchElements");
				hideIcon = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "hideIcon"),false);
				hideText = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "hideText"),false);
				type = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "type");
				precision = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "precision");
				min = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "min");
				max = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "max");
				parentMin = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "parentMin");
				parentMax = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "parentMax");
				
				return "";
			}
			return "Creation failed";
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			return "Creation failed";
		}
	}

	@Override
	public Map<String, String> getInitHtml(long stateKey, String formId, boolean renderEmpty, String value, String userLastSaveVal, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		Map<String, String> html = new HashMap<String, String>();
		if (renderEmpty) {
			if (generalUtil.getNull(type).equals("Checkbox")) {
				value = "0";
			} else{
				value = "";
			}
		}		
		
		String mandatory = (isMandatory) ? " required " : "";
		String hidden = (isHidden) ? "visibility: hidden;" : "";
		String function = "getValuesFromApi('" + domId + "')";
		String onclick = function; 
		String disableClass = "", iconHidden = "", onkeyup = "", textHidden = "", attributes = "";		
		String inputWidth = "width:90%;";
		
	    String type_ = type;
	    type_ = (type.equals("")) ? "text" : type;		
	    String width_ = width;
		width_ = (width.equals("")) ? ""
				: (width.indexOf("%") != -1) || (width.indexOf("px") != -1) ? "width:" + width + ";"
						: "width:" + width + "px;";
		
		if(isDisabled){		
			onclick = "";
			disableClass = " disabledclass ";			
		}
		if (hideIcon) {
			inputWidth = "width:100%;";
			iconHidden = "display:none;";
			onkeyup = "onkeyup=\"" + function + "\"";
		}
		if(type_.equals("Checkbox")){
			inputWidth = "";
			iconHidden = "display:none;";
			onkeyup = "onclick=\"if($(this).is(':checked')){" + function + "}\"";
		}
		textHidden = (hideText) ? "display:none;":"";
		
		//
		if (generalUtil.getNull(type_).equals("Number")){				
			attributes = " class=\"" + disableClass + "\"  precision=\"" + precision + "\" ";	
			attributes += (min.equals("") ? ((parentMin.equals("")) ? " min=\"0\" ":" min=\"" + parentMin + "\" ") : " min=\"" + min + "\" ");
			attributes += (max.equals("") ? ((parentMax.equals("")) ? "":" max=\"" + parentMax + "\" ") : " max=\"" + max + "\" ");
			if(!value.equals("") && !precision.equals("")){				
				int integerPlaces = value.indexOf('.');
				int decimalPlaces = value.length() - integerPlaces - 1;
				if (decimalPlaces > Integer.valueOf(precision)){
					value = String.format("%." + precision + "f", Double.valueOf(value));
				}
			}
		}
		else{
			attributes = " class=\"alphanumInputForm" + disableClass + "\" ";
		}		
		
		String elementHTML = "<div id=\"" + domId + "_wrapper\" style=\"white-space:nowrap;" + hidden + width_ + "\">\n"
				+ "<input id=\"" + domId + "_apiCodes\" type=\"hidden\" value=\"" + apiCodes + "\">\n"
				+ "<input id=\"" + domId + "_matchElements\" type=\"hidden\" value=\"" + matchElements + "\">\n"
				+ "<input " + attributes + " id=\"" + domId + "\" type=\"" + type_ + "\" mainArg=\"" + value + "\" value=\"" + value
				+ "\" style=\"" + inputWidth +  textHidden +";display:inline-block;\" maxlength=\"100\" " + mandatory + " placeholder=\"" + placeHolder
				+ "\" " + onkeyup + " element=\"" + this.getClass().getSimpleName() + "\" " + inputAttribute  + ">\n"
				+ "<i style=\"" + iconHidden + "margin-left: 5px;cursor: pointer;color: #2779aa;\" class=\"fa fa-calculator" + disableClass + "\" function=\""
				+ function + "\" onclick=\"" + onclick + "\"></i>"
				+ "</div>\n";

		html.put(layoutBookMark, elementHTML);
		//html.put(layoutBookMark + "_ready", "");

		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		String htmlBody = "";
		if (renderEmpty) {
			value = "";
		}
		htmlBody = "upDateElement({'domId':'" + domId + "','isMandatory':'" + isMandatory + "','isHidden':'"
				+ isHidden + "','isDisabled':'" + isDisabled + "','type':'apiElementSetter'});";
		return htmlBody;
	}

	@Override
	public Map<String, String> getCurrentParameMapByInputVal(String inputVal) {
		Map<String, String> mIdVal = new HashMap<String, String>();
		mIdVal.put("CURRENT_" + impCode, inputVal);
		return mIdVal;
	}

	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema= "schema:{ \n" + 
				"	placeHolder:{  \n" + 
				"		      type:'string',\n" + 
				"		      title:'PlaceHolder',\n" + 
				"		      'default':'Enter'\n" + 
				"		   },\n" +
				"	type:{  \n" + 
				"		      type:'string',\n" + 
				"		      title:'Type',\n" + 
				"		      'enum':['','Text','Number','Checkbox'] \n" + 
				"		   },\n" + 
				"	precision:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Precision (for type number)',\r\n" +			
				"   },\r\n" + 
				"	min:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Min value (default value for numbers is 0)',\r\n" +			
				"   },\r\n" + 
				"	max:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Max value',\r\n" +			
				"   },\r\n" + 
				"   parentMin:{  \r\n" + 
				" type:'string',\r\n" + 
				" title:'Parent min value (when min value is empty)',\r\n" + 
				" 'enum':[''].concat(getResourceValueByType('ELEMENT_IMP_CODE'))\r\n" + 
				"   },\r\n" +
				"   parentMax:{  \r\n" + 
				" type:'string',\r\n" + 
				" title:'Parent max value (when max value is empty)',\r\n" + 
				" 'enum':[''].concat(getResourceValueByType('ELEMENT_IMP_CODE'))\r\n" + 
				"   },\r\n" +
				" hideIcon:{\r\n" + 
				" type: 'boolean',\r\n" + 
				" title: 'Hide Icon'\r\n" + 				
				"   },\r\n" +
				" hideText:{\r\n" + 
				" type: 'boolean',\r\n" + 
				" title: 'Hide Text'\r\n" + 				
				"   },\r\n" +
				"	width:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Width',\r\n" +
				"	   'default':'100%'\n" + 
				"   },\r\n" +
				"	apiCodes : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : 'Api Codes',\r\n" +
				"		items : {\r\n" +
				"			enum : [''," + getApiCodes() + "],\r\n" +
				"			title : 'Code:'\r\n" +
				"		}\r\n" +
				"	},\r\n" +
				"	matchElements : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : 'Match Elements',\r\n" +
				"		items : {\r\n" +
				"			enum : [''].concat(getResourceValueByType('ELEMENT_IMP_CODE')),\r\n" +
				"			title : 'Element:'\r\n" +
				"		}\r\n" +
				"	}\r\n" +
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}
	
	/**
	 * csv of api.id of codes from api,
	 * the order of choosing the codes in the schema
	 * is important because we adjust the matchElements 
	 * and the api values by order for each code
	 * example:    return "'CAS.casName'";
	 * CAS is the API,
	 * casName is the code of the value that return from the api
	 * @return CSV
	 */
	private String getApiCodes(){
		return "'MaterialTripletCalc.NA',"
				+"'TestCalcDemo.NA',"
				+"'CalcFormulationProperties.NA'";
			
	}	
	
}
