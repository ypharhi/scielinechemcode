package com.skyline.form.entitypool;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.skyline.form.entity.Element;

/**
 * ElementRangeFilterImp: date range filter element
 * 
 * saves the data as "00/00/0000;00/00/0000;<option item val>"
 * 
 * getHtmlBody (ajax is not implemented so we always keep value on parent change)
 *
 */
public class ElementRangeFilterImp extends Element {
	
	private String catalogItem;
	private String dateOptionList;
	private String columnDateFilter;
	private String min;
	private String max;
	
	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if (super.init(stateKey, formCode, impCode, initVal).equals("")) { 
				catalogItem = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "catalogItem");
				dateOptionList = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "dateOptionList");
				columnDateFilter = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "columnDateFilter");			
				min = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "min");
				max = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "max");
				keepValueOnParentChange = true;
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
		String onReadyScript = "", firstInput, lastInput;		
		String wrapperDivBegin = "", wrapperDivEnd= "", autocomplete = " autocomplete=\"off\" ";
		String dateSelectValue =  getSelectOptions(dateOptionList,value);
		String dateSelect = "";
		if(dateOptionList.isEmpty()&&!columnDateFilter.isEmpty()){
			dateSelect = " <input type=\"hidden\" id=\"" + domId + "_optionItem\" value=\""+columnDateFilter+"\">\n";
		} else {
			dateSelect = 
				"<label style=\"float:left;margin-left: 10px;margin-right: 10px; margin-top:0;padding-top:0;\" class=\"text-right \">Date type: \n"
					+ "<select id=\"" + domId + "_optionItem\"  style=\"float:right;\" onchange=\""+doOnChangeJSCall+"\" >\n"
						+  dateSelectValue
					+ "</select>\n"
				+ "</label>\n";
		}
		String disabled = (isDisabled) ? " disabledclass " : "";		
		String hidden = (isHidden) ? "visibility:hidden;" : "";
		String labelFrom = "<label class=\"cssStaticData\" style=\"margin-left: 10px;float:left;margin-right: 10px;padding-top:0;margin-top:0;\" >From: </label>";
		String labelTo = "<label class=\"cssStaticData\" style=\"margin-left: 10px;float:left;margin-right: 10px;padding-top:0;margin-top:0;\">To:</label>";
		value = getformattedDate(value);
		wrapperDivBegin = "<div class=\"dateInput " + disabled + " elementrange\" element=\"" + this.getClass().getSimpleName() + "\" "
				+ inputAttribute + " id= \"" + domId + "\" style=\"white-space:nowrap;" + hidden + "\">";
		wrapperDivEnd = "</div>";
		DateFormat formatToDate = new SimpleDateFormat(generalUtil.getConversionDateFormat());
		SimpleDateFormat formatter = new SimpleDateFormat(generalUtil.getUserDateFormatServer(), Locale.ENGLISH);
		String attributes = "";
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
		attributes += !min.isEmpty() ? " min=\"" + min + "\" ":"";
		attributes += !max.isEmpty() ? " max=\"" + max + "\" ":"";
		onReadyScript = " initRangeDatePicker(\"" + generalUtil.getDatepickerFormat() + "\",\"" + doOnChangeJSCall+ "\");\n ";
		//'date-from'  class used in initRangeDatePicker
		firstInput = "<input  class=\"date-picker date-from\" " + autocomplete + attributes + " type=\"text\" value=\""
				+ value.split(";", -1)[0] + "\" style=\"width:150px !important;\" >";

		lastInput = "<input class=\"date-picker\" " + autocomplete + attributes + " type=\"text\" value=\""
				+ value.split(";", -1)[1] + "\" style=\"width:150px !important;\" >";
		
		html.put(layoutBookMark, wrapperDivBegin +dateSelect+ labelFrom + firstInput + labelTo + lastInput +wrapperDivEnd);				
		html.put(layoutBookMark + "_ready", onReadyScript);			
		return html;
	}	

	@Override 
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { 
		String htmlBody ="";
		if (renderEmpty) {			
			value = "00/00/0000;00/00/0000;NA";
		}
		return htmlBody;
	}

	private String getformattedDate(String value) {
		value = generalUtil.getEmpty(value, "00/00/0000;00/00/0000");
		String firstValue = value.split(";", -1)[0];
		String secondValue = value.split(";", -1)[1];
		try {			
			DateFormat formatToDate = new SimpleDateFormat(generalUtil.getConversionDateFormat());
			SimpleDateFormat formatter = new SimpleDateFormat(generalUtil.getUserDateFormatServer(), Locale.ENGLISH);
			Date date;
			if (!firstValue.equals("00/00/0000")) {
				date = formatToDate.parse(firstValue);
				firstValue = formatter.format(date);
			}
			if (!secondValue.equals("00/00/0000")) {
				date = formatToDate.parse(secondValue);
				secondValue = formatter.format(date);
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
			return "00/00/0000;00/00/0000";
		}
		return firstValue + ";" + secondValue;
	}
	
	
	@Override 
	/**
	 * put in the filter map expression <ARG1;ARG2> date expressions separated by ; [using in SQL where to_date(column) between <ARG1> and <ARG2>]
	 */
	public Map<String, String> getCatalogItemFilterMapByInputVal(String inputVal, boolean isCurrentElementChange) {
		Map<String, String> filterMap = new HashMap<String, String>();
		if(!generalUtil.getNull(catalogItem).equals("") && !generalUtil.getNull(inputVal).equals("")) {
			//TODO
			StringBuilder filterValue = new StringBuilder();		
			if (!(inputVal.split(";", -1)[0]).equals("00/00/0000") && !(inputVal.split(";", -1)[0]).equals("Invalid date") ) {
				filterValue.append("TO_DATE('" + inputVal.split(";")[0] + "','" + generalUtil.getConversionDateFormat() + "')");
			}
			filterValue.append(";");
			if (!(inputVal.split(";", -1)[1]).equals("00/00/0000") && !(inputVal.split(";", -1)[1]).equals("Invalid date") ) {
				filterValue.append("TO_DATE('" + inputVal.split(";")[1] + "','" + generalUtil.getConversionDateFormat() + "')");
			}			
			filterMap.put(impCode + "." + generalUtil.getEmpty(getFilterCatalogColumn(), catalogItem.split("\\.")[1]), filterValue.toString());				
		} 
		return filterMap;
	}
	
	@Override
	public Map<String, String> getCurrentParameMapByInputVal(String inputVal) {
		Map<String, String> mIdVal = new HashMap<String,String>();
		mIdVal.put("CURRENT_" + impCode, inputVal);
		return  mIdVal;
	}

	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema= "schema:{ \n" +  
				"	catalogItem:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Catalog range Column',\r\n" + 
				"      'enum':getResourceValueByType('OBJDATERANGE')\r\n" + 
				"   },\r\n" +
				"dateOptionList: {\n" +
				"	type : 'string',\n" +
				"	title : 'Dates options as CSV (ColumnName:Name display,ColumnName:Name display...)'\n" +		
				"}\r\n,"+
				"columnDateFilter: {\n" +
				"	type : 'string',\n" +
				"	title : 'Date ColumnName to filter by(leave empty if the Dates options definition has values)'\n" +		
				"}\r\n," +
				"	min:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Min value (Expected format for date is dd/mm/yyyy)',\r\n" +			
				"   },\r\n" + 
				"	max:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Max value (expected format for date is dd/mm/yyyy)',\r\n" +			
				"   }\r\n"+
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}
	private String getSelectOptions(String options,String value){
		StringBuilder sb = new StringBuilder();
		if(options == null || !options.contains(":")) {
			options = "NA:Choose";
		}
		String[] optionsArray  = options.split(",");
		
		String valSeslection = "";
		if(value != null && value.contains(";")) {
			String[] valueAarry = value.split(";",-1);
			if(valueAarry.length > 2) {
				valSeslection = valueAarry[2];
			}
		}
		
		for(String option : optionsArray){
			String id=option.split(":")[0];
			String val=option.split(":")[1];
				if(valSeslection.equals(id)) {
					sb.append("<option selected value=\""+id+"\">"+val+"</option>\n");
				} else {
					sb.append("<option value=\""+id+"\">"+val+"</option>\n");
				}
		}
		return sb.toString();
	}
	
//	@Override
//	public String getAuditTrailValue(String inputVal) {
//		if(inputVal.equals("") || inputVal.equals("00/00/0000;00/00/0000")){
//			return "";
//		}
//		inputVal = generalUtil.getEmpty(inputVal, "00/00/0000;00/00/0000");
//		String firstValue = inputVal.split(";", -1)[0];
//		String secondValue = inputVal.split(";", -1)[1];
//		try {			
//			DateFormat formatToDate = new SimpleDateFormat(generalUtil.getConversionDateFormat());
//			SimpleDateFormat formatter = new SimpleDateFormat(generalUtil.getUserDateFormatServer(), Locale.ENGLISH);
//			Date date;
//			if (!firstValue.equals("00/00/0000")) {
//				date = formatToDate.parse(firstValue);
//				firstValue = formatter.format(date);
//			}
//			if (!secondValue.equals("00/00/0000")) {
//				date = formatToDate.parse(secondValue);
//				secondValue = formatter.format(date);
//			}
//		} catch (Exception e) {
//			generalUtilLogger.logWrite(e);
//			e.printStackTrace();
//			return "";
//		}
//		return "From " + firstValue + " to " + secondValue;
//	}
}
