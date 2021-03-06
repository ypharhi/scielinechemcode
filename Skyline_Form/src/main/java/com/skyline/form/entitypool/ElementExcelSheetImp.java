package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.entity.Element;
import com.skyline.form.service.GeneralUtilFormState;

/**
 * 
 * @author YPharhi
 * General: 
 * 			Excel like element uses spreadjs [http://spread.grapecity.com/spreadjs/]
 * 			*** This product need additional license for displaying the excel tool bar  
 * 			*** This product also have desktop application like excel that can be used to open excel file and export them to a json format (as the js code does)
 * 			The jsp must include (for using the spreadsheet):
 * 			ElementExcelSheetBL<version> - that is part of the includeExtendedJS.jsp
 * Save element: 
 * 			The data is saved to fg_clob_files as json contain:
 * 				- excelFullData - the excel json object
 * 				- output - data from outform if exists as key/value from columns A/B
 * 			
 * 			TODO getInitHtml implement disable / hidden for cases that got by onchangeAjax(disabled on loading the form is implemented in the disableSpreadsheet_ func)
 * 			     getHtmlBody implement disable / hidden for cases that got by onchangeAjax(disabled on loading the form is implemented in the disableSpreadsheet_ func)
 * 			     prop.dataChanged is updated in GC.Spread.Sheets.Events.EditStarting event. When the toolbar is added-we should add more events that support the change
 * *** See DemoExcel for usage example
 * *** Note:   add _SYSCONFEXCELDATA from and saveSysConfExcelAsClob function to config spreadsheet from scratch or by upload from file and make it default excel (by name) in the forms (if we need an excel with specific data / settings as default)
 */
public class ElementExcelSheetImp extends Element 
{
	
	private String width;
	
	private String height;
	
	private boolean isToolBarDisplay;
	
	@Value("${SpreadSheetsLicenseKey:192.168.10.72|82.166.142.156|skyline.comply.co.il,E578322885894476#B0Nlce8QDVq36RqZ4YLRUTOJ6UoJXa586RB3CV9N7VRN7MXx4LtVDNvtEZ4g6LMJXYyEkcJ96cxJHbzEGSnlUa9o5SGZDWQNmMlZ6UHZFdpBlZsZlQyI5T5Nmbz44KtVFRnNVNOx6YIhzTvJFUyElNOhHT5UDSTZDe9QWZi54VKJmT5YTUFNVQ69mSmpEd4cEcldETqplSSpFaKR6L4NWMvVEZ694K7EWcohTaxglZlhXeTd5VytCV8h4dJN6YWdXOwd4atB7cMd6UGhTbQR6VL3WSvRjQrhXWQNWblljRUd7UFJzY6gmZudDNMV5S7NWcx3UaBBXMCRmd5dVUolEcCZlNyM5czF5Zt94ViojITJCLiIzMzQER8kTMiojIIJCL9gTO4YDNyMTO0IicfJye35XX3JSRFlkViojIDJCLiQTMuYHIgMlSgQWYlJHcTJiOi8kI1tlOiQmcQJCLiETNwUjNwASMyQDMxIDMyIiOiQncDJCLiEjM4AjMyAjMiojIwhXRiwiIslmLvNmL9xGct36YuUmbpxWerNHL6UTMuIDNx8iN6EjLygDLycjLwEjL8YTMuITOxIiOiMXbEJCLicmbpR7clRFIsBXbvNkI0ISYONkIsUWdyRnOiwmdFJCLiYzN4QTO8UDO8IjMzgzN5IiOiQWSiwSflNHbhZmOiI7ckJye0ICbuFkI1pjIEJCLi4TPnJFTroHZRNlVTBzU6sESyFXQoNUTIhlandEaHRVTyp7bOBjQE3iauRFdEtCVkFHNvB5bsNkZEZUbThWTthVUIVkeBFkVUFTZ4RjN}")
	private String SpreadSheetsLicenseKey;

	@Value("${SpreadSheetsDesignerLicenseKey:192.168.10.72|82.166.142.156|skyline.comply.co.il,E645447126247673#B0WNLlEbvVmdhVDWlVnVuFGVkZDMHVHNV34VpdDS6tkZJx6QFZEcxtWbPh7VLFlb4l7KjlUd0pnZkRUONZ4URJlR0F4QKZWdihjZ6QjQB96cmZDSxMUY6knZVF7MyoXNHhjZvN6ailmQ8BlZvwkUGVDVGB5NntmN996dkFUbPZDSK5UUkhzMuNTW0hjY8ZUSkV7dMJzKip4TFlmaWZ7TNJEeEJkS8pmWTZ4c9dDcGF5KsdETOZ5c6Z4a9Qzar9ETVNWQUBzTjNjMBZEVDBXMtVEWvE7cF3GV6ljcwlTZs36cxgjczhzYyFWWOlFN9N4RrNzKrQkMYBFbEFncxkkcXhEd8k7YwUDZiojITJCLiMzN9cDR9kDNiojIIJCL5QDN6czMyMjN0IicfJye35XX3JSW6U4NiojIDJCLiQTMuYHIu3GZkFULyVmbnl6clRULTpEZhVmcwNlI0IiTis7W0ICZyBlIsISMxAjMxADIzATNwEjMwIjI0ICdyNkIsIyMwUDMyIDMyIiOiAHeFJCLiwWau26YukHbw56bj9SZulGb9t6csYTNx8iM4EjL6YTMuIDOsIzNuATMugjNx8iM9EjI0IyctRkIsIyZulGdzVGVg46bDJiOiEmTDJCLlVnc4pjIsZXRiwiIzcjN7QjM6ITM7QDN5QjNiojIklkI1pjIEJCLi4TPnRmNhZXUOBzK9E5clhlTl3UYv36dW34cOl6QYJFRtFmdGlXO4Z6Yp5GS4FEMrgzaC3UMq3EarIXOzZURlJTWYdTVEh6MDhUTsZVaGVVLZV}")
	private String SpreadSheetsDesignerLicenseKey;

	@Autowired
	private GeneralDao generalDao;
	
	@Value("${isAjaxExcelLoad:0}")
	private int isAjaxExcelLoad;
	
	@Autowired
	private GeneralUtilFormState generalUtilFormState;	
	

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{			
				width = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "width");
				height = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "height");
				isToolBarDisplay = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "isToolBarDisplay"),true);
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
	public Map<String, String> getInitHtml(long stateKey, String formId, boolean renderEmpty, String value, String userLastSaveVal, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		Map<String, String> html = new HashMap<String, String>();

		if(isHidden) {
			return html;
		}
		
		String width_ = width;
		String height_ = height;
		width_ = (width.equals("")) ? "calc(100vw - 50px)" : (width.indexOf("%") != -1) ? width+";" : (width.indexOf("px") != -1) ? width+";" : width+"px;";
		height_ = (height.equals("")) ? "600px" : (height.indexOf("px") != -1) ? height+";" : height+"px;";
		String hidden = (isHidden)? "visibility:hidden;":"";
		String disabled = (isDisabled) ? " disabledclass " : "";
		String spreadsheetObj = "";
		String onLoadIframeSpreadsheet = ""; 
		
		//overrideExcelLoadState is passed as URL parameter 
		//used to check performance for developers
		//if 1 - use Ajax load (override the default isAjaxExcelLoad=0)
		//if noexcel - the excel will not be loaded at all
		String overrideExcelLoadState = "";
		try {
			overrideExcelLoadState = generalUtilFormState.getFormParam(stateKey, formCode, "$P{OVERRIDEEXCELLOADSTATE}");
			if(overrideExcelLoadState != null && !overrideExcelLoadState.isEmpty()) {
				if(overrideExcelLoadState.equals("noexcel")) {
					return html;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		String defaultvalue = generalUtil.getEmpty(getDefaultValue(stateKey,formId,formCode),"-1");
		if(isAjaxExcelLoad == 1 || generalUtil.getNull(overrideExcelLoadState).equals("1")) {
			value = generalUtil.getEmpty(value, "-1");
			defaultvalue = generalUtil.getEmpty(getDefaultValue(stateKey,formId,formCode),"-1");
			onLoadIframeSpreadsheet = "onLoadIframeSpreadsheet_(1," + value +"," + defaultvalue + ",'"+domId+"',"+isToolBarDisplay+","+isDisabled+",'" + SpreadSheetsLicenseKey + "','" + SpreadSheetsDesignerLicenseKey + "');";
		} else {
			String spreadsheetData = "";
			String spreadsheetOutput = "";
			value = generalUtil.getEmpty(value, "-1");
			if (!value.equals("-1")) {
				String elementData = generalUtilFormState.getStringContent(value, formCode, domId, formId);
				JSONObject js = new JSONObject();
				if(!generalUtil.getNull(elementData).isEmpty()){
					js = new JSONObject(elementData);
				} else {
					value = getDefaultValue(stateKey,formId,formCode);
					elementData = generalUtilFormState.getStringContent(value, formCode, domId, formId);
					js = new JSONObject();
					if(!generalUtil.getNull(elementData).isEmpty()){
						js = new JSONObject(elementData);
					}
				}
				try {
					JSONObject jsspreadsheetData = (JSONObject)js.get("excelFullData");
					spreadsheetData = jsspreadsheetData.toString();
					JSONObject jsspreadsheetOutput = (JSONObject)js.get("output");
					spreadsheetOutput = jsspreadsheetOutput.toString();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			spreadsheetObj = "onLoadSpreadsheetElement(" +(spreadsheetData.isEmpty()?"{}":spreadsheetData)+","+(spreadsheetOutput.isEmpty()?"{}":spreadsheetOutput)+",'"+domId+"',"+isToolBarDisplay+","+isDisabled+");";
			onLoadIframeSpreadsheet = "onLoadIframeSpreadsheet_(0," + value + "," + defaultvalue + ",'"+domId+"',"+isToolBarDisplay+","+isDisabled+",'" + SpreadSheetsLicenseKey + "','" + SpreadSheetsDesignerLicenseKey + "');";

		}
		html.put(layoutBookMark + "_ready", spreadsheetObj);
		String iframeSpreadJS = "<div id=\"" + domId + "\"  elementID=\"" + value + "\" basicHeight=\""+height_+"\" basicWidth=\""+width_+"\" style=\"height: "+height_+"; width:"+width_+";border: 1px solid gray;" + hidden+"\" element=\"" + this.getClass().getSimpleName() + "\" " + inputAttribute + " class=\"excelSheet "+ disabled +"\">\n"
				+ "<iframe id = \""+domId+"_spreadIframe\" loading=\"lazy\" name = \""+domId+"_spreadIframe\" width=\"100%\" height = \"100%\" src=\"../skylineFormWebapp/jsp/frmSpreadsheet.jsp\" onload = \""+onLoadIframeSpreadsheet+"\"></iframe>"
				+ "</div>";
		html.put(layoutBookMark,iframeSpreadJS);
		return html;
	}
	
	@Override
	public String getDefaultValue(long stateKey, String formId, String formCode) {
		String dv_ = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "defaultValue");
		if(dv_.isEmpty()) {
			return dv_;
		} else if(dv_.contains("$P{")) {
			generalUtilLogger.logWrite(LevelType.DEBUG, "default value not found for element[impCode]=" + impCode + "value=" + dv_, "", ActivitylogType.InfoLookUp, null);
			dv_= "";
		}
		else if(!dv_.matches("-?\\d+(\\.\\d+)?")) {
			String sql = "select ExcelData\n"
					+ "from fg_s_SysConfExcelData_v\n"
					+ "where SysConfExcelDataName = '"+dv_+"'";
			dv_ = generalDao.selectSingleStringNoException(sql);
		}
		return dv_;
	}


	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp
		String htmlBody;
		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "','isMandatory':'"
				+ isMandatory + "','val':'','domId':'" + domId + "','type':'excelSheet'});";
		return htmlBody;
	}
	
//	private String getAttributes(boolean isDisabled, boolean isMandatory, boolean isHidden) {
//		StringBuilder sb = new StringBuilder();
//		if (isDisabled)
//			sb.append("disabled ");
//		if (isMandatory)
//			sb.append("required ");	
//		return sb.toString();
//	}
//
//	private String isRequired(boolean isMandatory) {
//		if (isMandatory)
//			return "<label style=\"color:#a94442;\">* </label>";
//		return "<label style='visibility: hidden;color:#a94442;'>* </label>";
//	}
	
	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema= "schema:{ \n" + 
				"	width:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Width (not in use)',\r\n" +
				"   },\r\n" + 
				"	height:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Height (not in use)',\r\n" +
				"   },\r\n" +
				"	isToolBarDisplay:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Is Toolbar Displayed',\r\n" +
				"      enum:['True','False'],\r\n" +
				"   }\r\n" 
				+(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}

}
