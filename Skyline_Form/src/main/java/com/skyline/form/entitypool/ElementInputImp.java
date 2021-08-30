package com.skyline.form.entitypool;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.ElementInfoAuditTrailDisplay;
import com.skyline.form.bean.LevelType;
import com.skyline.form.entity.Element;

/**
 * ElementInputImp (also called Generic Input)
 * 
 * the element can be declared as TextBox, Number, Date,
 * Hidden, Disabled, Password ,Email, Checkbox, Time
 * 
 */
public class ElementInputImp extends Element {

	private String placeHolder, type, htmlType, width, precision, maxLength, allowFollowingChars,numericType,tooltipPlaceHolder,isCalculated;	
	
	private String min, max, parentMin, parentMax, linkId;
	
	private boolean isUsedAsLink = false;

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{
				placeHolder = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "placeHolder");
				type = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "type");
				numericType = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "numericType");
				isCalculated = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "isCalculated");
				if(generalUtil.getNull(type).equals("Date") || generalUtil.getNull(type).equals("Disabled")
						|| generalUtil.getNull(type).equals("Email") || generalUtil.getNull(type).equals("Time")) {
					htmlType = "Text";
				} else {
					htmlType = type;
				}
				width = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "width");
				precision = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "precision");
				maxLength = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "maxLength");
				min = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "min");
				max = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "max");
				parentMin = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "parentMin");
				parentMax = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "parentMax");
				allowFollowingChars = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "allowFollowingChars");
				isUsedAsLink = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "useAsLink"),false);;
				linkId = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "linkParameter");
				tooltipPlaceHolder=generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "tooltipPlaceHolder");
				
				return "";
			}
			return "Creation failed";
		}
		catch (Exception e) {
			generalUtilLogger.logWrite(e);
			return "Creation failed";
		}
	}

	@Override
	public Map<String, String> getInitHtml(long stateKey, String formId, boolean renderEmpty, String value, String userLastSaveVal, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		Map<String, String> html = new HashMap<String, String>();		
		if (renderEmpty) {
			if (generalUtil.getNull(type).equals("Date")) {
				value = "00/00/0000";
			} else if (generalUtil.getNull(type).equals("Checkbox")) {
				value = "0";
			} else{
				value = "";
			}
		}				
		 
		String attributes = "", inlineStyles = "", onReadyScript = "";			
		String autocomplete = "autocomplete=\"off\"";		
		String maxlength = " maxlength=\"" + (preventSave ? maxLength : generalUtil.getEmpty(maxLength, "500")) + "\" ";
		String wrapperDivBegin = "", wrapperDivEnd= "";
		String disabled = (isDisabled)? " disabledclass " : "";
		String classString = (isDisabled)? " class=\"" + disabled + "\" " : "";
		String hidden = (isHidden) ? "display:none;" : "";
		String script = "";
		String placeHolder_ = placeHolder;
		boolean isNumericFieldCalculated = false;
		String additInfoIcon = "";	
		String title = tooltip? "title=\"" +value+"\"" : "";
		String tooltipFreeText = generalUtil.getNull(tooltipPlaceHolder);
		
		String width_ = "";
		width_ = (width.equals("")) ? "" : (width.indexOf("%") != -1) || (width.indexOf("px") != -1) ? "width:" + width + ";": "width:" + width + "px;";
		
	
		if(generalUtil.getNull(type).equals("Date")) {
			wrapperDivBegin = "<div class=\"dateInput " + disabled + "\" style=\"white-space:nowrap;" + hidden + "\">";
			wrapperDivEnd = "</div>";
			attributes = " class=\"date-picker" + disabled + "\" ";	
			DateFormat formatToDate = new SimpleDateFormat(generalUtil.getConversionDateFormat());
			SimpleDateFormat formatter = new SimpleDateFormat(generalUtil.getUserDateFormatServer(), Locale.ENGLISH);
			if(!min.isEmpty()){
				try {
					Date date = formatToDate.parse(min);
					min = formatter.format(date);	
				} catch (ParseException e) {
					generalUtilLogger.logWrite(e);
					e.printStackTrace();
				}
			}
			if(!max.isEmpty()){
				try {
					Date date = formatToDate.parse(max);
					max = formatter.format(date);	
				} catch (ParseException e) {
					generalUtilLogger.logWrite(e);
					e.printStackTrace();
				}
			}
			attributes += (min.equals("") ? ((parentMin.equals("")) ? "":" min=\"" + parentMin + "\" ") : " min=\"" + min + "\" ");
			attributes += (max.equals("") ? ((parentMax.equals("")) ? "":" max=\"" + parentMax + "\" ") : " max=\"" + max + "\" ");
			value = generalUtil.getEmpty(value, "00/00/0000");
			try {
				if(!value.equals("00/00/0000")){
					Date date = formatToDate.parse(value);					
					value = formatter.format(date);					
				}
			} catch (Exception e) {
				generalUtilLogger.logWrite(e);
				e.printStackTrace();
			}		
			classString = "";
		}
		else if(generalUtil.getNull(type).equals("Email")) {			
			onReadyScript ="onEmailInputKeyUp('" + domId + "');\n $('#" + domId + "').attr('type_','email');\n";
		}
		else if(generalUtil.getNull(type).equals("Checkbox")) {			
			onReadyScript ="initCheckBox('" + domId + "');\n $('#" + domId + "').on('change', function(){" + doOnChangeJSCall + "});\n ";
		}
		else if(generalUtil.getNull(type).equals("Password")) {		
			autocomplete = "autocomplete=\"new-password\"";
			value="";
		}
		else if(generalUtil.getNull(type).equals("Time")) {
			placeHolder_ = "--:--";
			attributes = (min.equals("") ? ((parentMin.equals("")) ? " min=\"0\" ":" min=\"" + parentMin + "\" ") : " min=\"" + min + "\" ");
			attributes += (max.equals("") ? ((parentMax.equals("")) ? "":" max=\"" + parentMax + "\" ") : " max=\"" + max + "\" ");
			inlineStyles = "text-align:center;";
			onReadyScript ="onTimeInputKeyUp('" + domId + "');\n $('#" + domId + "').attr('maxlength','5');\n $('#" + domId + "').attr('type_','time');\n";
		}				
		else if (generalUtil.getNull(type).equals("Number"))
		{
			classString = "";
			if(generalUtil.getNull(numericType).equals("Integer")){
				attributes = " class=\"" + disabled + "\" ";	
				attributes += (min.equals("") ? ((parentMin.equals("")) ? "":" min=\"" + parentMin + "\" ") : " min=\"" + min + "\" ");
				attributes += (max.equals("") ? ((parentMax.equals("")) ? "":" max=\"" + parentMax + "\" ") : " max=\"" + max + "\" ");
				attributes += " realvalue=\"" + value + "\" ";
				attributes += "onkeypress=\"if(event.keyCode == 45 ||(event.keyCode > 47 && event.keyCode < 58)){ }else return false;\" ";
			} else if(generalUtil.getNull(numericType).equals("Natural")){
				attributes = " class=\"" + disabled + "\" ";	
				if(!generalUtil.getNull(min).toLowerCase().equals("na")) {
					attributes += (min.equals("") ? ((parentMin.equals("")) ? " min=\"1\" ":" min=\"" + parentMin + "\" ") : " min=\"" + min + "\" ");
				}
				attributes += (max.equals("") ? ((parentMax.equals("")) ? "":" max=\"" + parentMax + "\" ") : " max=\"" + max + "\" ");
				attributes += " realvalue=\"" + value + "\" ";
				attributes += "onkeypress=\"if(event.keyCode > 47 && event.keyCode < 58){ }else return false;\" ";
			} else {
				boolean isDouble = generalUtil.getNull(numericType).equals("Double");
				//if isDouble The number is a Double else (default) the number Double >=0
				attributes = " class=\"" + disabled + "\"  precision=\"" + precision + "\" ";	
				if(!generalUtil.getNull(min).toLowerCase().equals("na")) {
					attributes += (min.equals("") ? ((parentMin.equals("")) ? " min=\"" + (isDouble?"":"0") + "\" ":" min=\"" + parentMin + "\" ") : " min=\"" + min + "\" ");
				}
				attributes += (max.equals("") ? ((parentMax.equals("")) ? "":" max=\"" + parentMax + "\" ") : " max=\"" + max + "\" ");
				attributes += " realvalue=\"" + value + "\" ";
				attributes += "onkeypress=\"if(" + ((isDouble)?"event.keyCode == 45 ||":"") + " event.keyCode == 46 || event.keyCode== 101 ||(event.keyCode > 47 && event.keyCode < 58)){ }else return false;\" ";
				if (!value.equals("") && !precision.equals("")) {
					// int integerPlaces = value.indexOf('.');
					// int decimalPlaces = value.length() - integerPlaces - 1;
					// if (decimalPlaces > Integer.valueOf(precision)){
					String valueHolder = value;
					try {
						value = String.format("%." + precision + "f", Double.valueOf(value));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
						value = valueHolder;
					}
					// }
				}
			}
			/** in assumption that, field considered 'calculated field' if it has Number type AND
			 *  his parameter 'disableAlways' is true OR field has parameter 'isCalculated'
			 */
			isNumericFieldCalculated = (disableAlways || generalUtil.getNull(isCalculated).equals("True"));
			if(isNumericFieldCalculated && (value == null || value.equals("")) && !isHidden
					&& (generalUtilFormState.replaceFormParam(stateKey, formCode, "$P{ISNEW}", false).equals("0")))
			{
				additInfoIcon = "<div class=\"divAdditCustomInfo\">"+
									"<span><i  onclick=\"getCalculatedFieldsInfo()\" style=\"cursor: pointer;\" title=\"Field Info\" class=\"fa fa-info\"></i></span>"+ 
								"</div>";
				width_ = (width.equals("")||width.equals("100%") || width.indexOf("px") !=-1)? "width:95%;":width_;
			}
		}
		else
		{
			String linkHtml = "";
			if (isUsedAsLink && !linkId.isEmpty()){
				String formCode = formDao.getFormCodeBySeqId(linkId);
				if(!formCode.isEmpty()){
					linkHtml += "$('[id=\"" + domId + "\"]').parent('td').click(function(){checkAndNavigate(['"+linkId+"' ,'"+formCode+"','', true]);});\n";
					linkHtml += "$('[id=\"" + domId + "\"]').parent('td').addClass('linkElement');\n";
				}
			}
			classString = "";
			attributes = " class=\"alphanumInputForm" + disabled +  "\" alphanumAllowChars=\""+allowFollowingChars.trim()+"\" ";
			onReadyScript ="onTextInputKeydown('" + domId + "');\n"+linkHtml;
		}	
		
		if(tooltip){
			onReadyScript +="onKeyUpTooltip('" + domId + "');\n"; 
		}else if(!tooltipFreeText.isEmpty()){
			onReadyScript +="onKeyUpTooltipFreeText('" + domId +"','" +tooltipFreeText+"');\n"; 
		}
		
		
		html.put(layoutBookMark, wrapperDivBegin + 
				isLabel(isHidden) + additInfoIcon + "<input " + classString + attributes + autocomplete + " style=\"" + inlineStyles
						+ hidden + width_ + "\" type=\"" + htmlType + "\" id=\"" + domId
						+ "\"  placeholder=\"" + placeHolder_ + "\" " + inputAttribute + " "
						+ getAttributes(isDisabled, isMandatory, isHidden) + " value=\"" + value + "\" " + maxlength
						+ " element=\"" + this.getClass().getSimpleName() + "\" onchange=\"hideAdditInfoDialog()\" " + title + ">" + isLabelEnd() + wrapperDivEnd
						+ script
				);			
		
		html.put(layoutBookMark + "_ready",onReadyScript);
		return html;
	}
	
	@Override
	public String getDefaultValue(long stateKey, String formId, String formCode) {
		String dv_ = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "defaultValue");
		if(dv_.contains("$P{")) {
			generalUtilLogger.logWrite(LevelType.DEBUG, "default value not found for element[impCode]=" + impCode + "value=" + dv_, "", ActivitylogType.InfoLookUp, null);
			dv_= "";
		}
		if(generalUtil.getNull(type).equals("Date")){
			try {
				if(dv_.isEmpty()){
					return dv_;
				}
				String pattern= generalUtil.getDateFormat(dv_); //org.apache.commons.lang.time.DateUtils.parseDate(dv_, formatss);				
				DateFormat formatToDate = new SimpleDateFormat(pattern);
				Date date = formatToDate.parse(dv_);
				SimpleDateFormat formatter = new SimpleDateFormat(generalUtil.getConversionDateFormat(), Locale.ENGLISH);
				String value = formatter.format(date);
				dv_ = value;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return dv_;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp
		String htmlBody ="";
		if (renderEmpty) {
			if (generalUtil.getNull(type).equals("Date")) {
				value = "00/00/0000";
			} else if (generalUtil.getNull(type).equals("Checkbox")) {
				value = "0";
			} else{
				value = "";
			}
		}
		if (generalUtil.getNull(type).equals("Date")) {
			value = generalUtil.getEmpty(value, "00/00/0000");
			if(!value.equals("00/00/0000")){
				try {
					DateFormat formatToDate = new SimpleDateFormat(generalUtil.getConversionDateFormat());
					Date date = formatToDate.parse(value);					
					SimpleDateFormat formatter = new SimpleDateFormat(generalUtil.getUserDateFormatServer(), Locale.ENGLISH);
					value = formatter.format(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}					
			}
		}		
		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "','isMandatory':'"
				+ isMandatory + "','val':'','domId':'" + domId + "','type':'text','value_':'" + value + "'});";
		return htmlBody;
	}
	
	private String getAttributes(boolean isDisabled, boolean isMandatory, boolean isHidden) {
		StringBuilder sb = new StringBuilder();
//		if (isDisabled)
//			sb.append("disabled ");
		if (isMandatory) {
			sb.append("required ");
		}
		return sb.toString();
	}
	 
	@Override
	public Map<String, String> getCurrentParameMapByInputVal(String inputVal) {
		Map<String, String> mIdVal = new HashMap<String,String>();
		mIdVal.put("CURRENT_" + impCode, inputVal);
		return  mIdVal;
	}
	
	@Override
	public Map<String, String> getCatalogItemFilterMapByInputVal(String inputVal, boolean isCurrentElementChange) {
		Map<String, String> filterMap = new HashMap<String,String>();
		
		if(!generalUtil.getEmpty(getFilterCatalogColumn(), "").equals("")) {
			if(generalUtil.getEmpty(inputVal,"").trim().equals("")) {
				filterMap.put(impCode + "." + getFilterCatalogColumn(),"");
			} else {
				filterMap.put(impCode + "." + getFilterCatalogColumn(), "'" + inputVal.replace(",", "','") + "'");
			}
		}
		return  filterMap;
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
				"	width:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Width',\r\n" +
				"	   'default':'100%'\n" + 
				"   },\r\n" + 
				"	precision:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Precision (for numbers)',\r\n" +			
				"   },\r\n" + 
				"	maxLength:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Max length (for text) 500 is the max limit for save input',\r\n" +			
				"   },\r\n" +
				"	min:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Min value (default value for numbers is 0 and Natural 1 NA for igonre check [check is only on enabled]. Expected format for date is dd/mm/yyyy)',\r\n" +			
				"   },\r\n" + 
				"	max:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Max value (expected format for date is dd/mm/yyyy)',\r\n" +			
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
				"	type:{  \n" + 
				"		      type:'string',\n" + 
				"		      title:'Type',\n" + 
				"		      'enum':['Text','Number','Date','Hidden','Disabled','Password','Email','Checkbox','Time'] \n" + 
				"		   },\r\n" +
				"	numericType:{  \n" + 
				"		      type:'string',\n" + 
				"		      title:'Numeric type (when type is number, define a specific Number type [Non negtive Double if empty])',\n" + 
				"		      'enum':['','Integer','Natural','Double'] \n" + 
				"		   },\r\n" +
				" isCalculated : {\n" +
				"	type : 'string',\n" +
				"	title : 'Is Calculated field',\n" +
				"	enum : ['','True'],\n" +
				"},\n" +
				"  allowFollowingChars:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Allow following chars (for text)',\r\n" +			
				"   },\r\n" +
				"   useAsLink:{  \r\n" + 
				"		type:'boolean',\r\n" + 
				"		title:'Use as link (clicking on the element, will navigate to the appropriate form with the formid/ID of the current value)',\r\n" + 
				"   },\r\n" +
				"   linkParameter:{  \r\n" + 
				"		type:'string',\r\n" + 
				"		title:'Link ID (parameter or constant that contains the ID to be navigated to on click event. If it is empty then ignore the link)',\r\n" + 
				"   },\r\n" +
				"   tooltipPlaceHolder:{  \r\n" + 
				"      type: 'string',\r\n" + 
				"      title:'Tooltip (free text)',\r\n" +			
				"   }\n" +
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}
	
	@Override
	public ElementInfoAuditTrailDisplay getAuditTrailValue(String formCode, String key, String postSaveValue, String originValue, String uiDisplayValue)
	{
		ElementInfoAuditTrailDisplay elementValueJobFlag = null;
		String uiDisplayValueUpdated = "";

		try {
			if (postSaveValue.equals(originValue)) {
				elementValueJobFlag = new ElementInfoAuditTrailDisplay(uiDisplayValue, "0");
			} else {
				if (generalUtil.getNull(type).equals("Checkbox")) {
					uiDisplayValueUpdated = (postSaveValue.equals("1")) ? "Yes" : "No";
				} else if (generalUtil.getNull(type).equals("Date") && !postSaveValue.equals("")) {
					try {
						DateFormat formatToDate = new SimpleDateFormat(generalUtil.getConversionDateFormat());
						Date date = formatToDate.parse(postSaveValue);
						SimpleDateFormat formatter = new SimpleDateFormat(generalUtil.getUserDateFormatServer(),
								Locale.ENGLISH);
						uiDisplayValueUpdated = formatter.format(date);
					} catch (Exception e) {
						generalUtilLogger.logWrite(e);
						e.printStackTrace();
					}
				} else {
					uiDisplayValueUpdated = postSaveValue;
				}
				elementValueJobFlag = new ElementInfoAuditTrailDisplay(uiDisplayValueUpdated, "0");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			elementValueJobFlag = new ElementInfoAuditTrailDisplay("", "1");
		}

		return elementValueJobFlag;
	}
	
//	@Override
//	public String getAuditTrailValue(String inputVal) {
//		if (generalUtil.getNull(type).equals("Checkbox")) {
//			return (inputVal.equals("1")) ? "Yes" : "No";
//		}
//		if (generalUtil.getNull(type).equals("Date") && !inputVal.equals("00/00/0000")) {
//			try {
//				DateFormat formatToDate = new SimpleDateFormat(generalUtil.getConversionDateFormat());
//				Date date = formatToDate.parse(inputVal);
//				SimpleDateFormat formatter = new SimpleDateFormat(generalUtil.getUserDateFormatServer(),
//						Locale.ENGLISH);
//				inputVal = formatter.format(date);
//			} catch (Exception e) {
//				generalUtilLogger.logWrite(e);
//				e.printStackTrace();
//			}
//		}
//		return inputVal;
//	}
}
