package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.entity.Element;
import com.skyline.form.service.GeneralUtilFormState;

//////////////////////// NOT IN USE ////////////////////////////////////// yp 11062018
public class ElementWebixExperimentStepCalcImp extends Element {	
	
	@Autowired
	private GeneralUtilFormState generalUtilFormState;	

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{			   				
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
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) 
	{
		HashMap<String, String> html = new HashMap<String, String>();
		try 
		{
			value = (value.equals("-1")) ? "" : value;
			JSONArray fullDataArr = new JSONArray();
			String tabledivid = "", tableid = "", tableinithtml = "", solidVal = "";
			String isNew = "1";
			String tableData = "[]";
			
			if(value.equals(""))
			{
				String parentID = generalUtilFormState.getFormParam(stateKey, "StepFr", "$P{FORMID}");
				fullDataArr = generalUtilFormState.getWebixExperimentStepCalcData(parentID);
				tableData = fullDataArr.toString();
			}
			else
			{
				JSONObject obj = new JSONObject();
				String fullData = generalUtilFormState.getStringContent(value,"","","");
				if(!fullData.equals(""))
				{
					try 
					{
						obj = new JSONObject(fullData);
						tableData = obj.getJSONArray("data").toString();
						solidVal = obj.optString("solid"); 
						isNew = "0";
					} 
					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			tabledivid = "tableDiv_"+domId;
			tableid = "tableID_" + domId;
			tableinithtml += "initElementDataTableWebixExpStep('" + domId + "','"+tableid+"','"+tabledivid+"','"+tableData+"',"+isNew+",true);\n";
			
			html.put(layoutBookMark, 
					"<div id=\"" + domId + "_Parent\" style=\"min-width:700px;margin-left: 2px;border:1px solid #bdbdbd;background-color:white;padding:28px 31px;border-radius:3px;border-collapse:collapse;border-spacing:0;margin-bottom:20px\" isfirstload=\"1\" parentElement=\"" +  getParentElement()  + "\" >" + //class=\"dataTableParent\"
							"<h2 class=\"cssStaticData\">" + getLabel() + "</h2>"+	
							"<div>"
								//+"<button class=\"button\" type=\"button\" onclick=\"getOutputDataExpStep('"+domId+"')\">getOutputData</button>\n"
								//+"<button class=\"button\"  type=\"button\" onclick=\"calcAllExpStep('"+domId+"')\" style=\"margin-left: 15px;\">Calculate all</button>\n" 
								+"<button class=\"button\" type=\"button\" name=\"webixContainerButtons\" onclick=\"uploadExpStepData('" + domId + "')\" style=\"margin-left: 0;\">Upload</button>\n"								
								+ "<div style=\"display: inline-block;margin-left:50px;\">"
								+ 		"<label style=\"display: inline-block;text-align: right;margin-right:10px\">%Solid</label>"
								+ 		"<input id=\"solid_" + domId + "\" value=\""+solidVal+"\" type=\"text\" readonly style=\"display: inline-block;width:150px;\" class=\"disabledclass\" />"
								+ "</div>\n"
								+ "<div name=\"parentWebixContainer\" id=\"" + domId + "\" elementID=\"" + value + "\"  element=\"" + this.getClass().getSimpleName() + "\" " + inputAttribute + ">"
								+ 		"<div class=\"webix_container\" isWebixTableHidden=\"false\" childTableID=\""+tableid+"\" id=\""+tabledivid+"\" style=\"margin-top: 15px;\"></div>"
								+ "</div>"
							+ "</div>"
				+ "</div>");									
			html.put(layoutBookMark + "_ready", "" + tableinithtml);	
		}
		catch(Exception e){
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return html;
	}
	
	
	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp				
		String htmlBody;
		
		String parentID = generalUtilFormState.getFormParam(stateKey, "StepFr", "$P{FORMID}");
		boolean isPln = (generalUtilFormState.getFormParam(stateKey, "StepFr", "$P{CURRENT_PLANNED_ACTUAL}").equals("Planned"))?true:false;
		
		String fullData = "";
		try {
			fullData = generalUtilFormState.getWebixContent(parentID, isPln);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			fullData = generalUtilFormState.getWebixContent(parentID, false);
			e.printStackTrace();
		}
		
		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "','isMandatory':'"
				+ isMandatory + "','val':'"+fullData+"', 'domId':'" + domId + "','type':'webixExpStepCalc'});";
		return htmlBody;
	}
	 
	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema = "schema:{\n" + 			
				" layoutBookMarkItem:{\n" + 
				"     type:'string',\n" + 
				"     title:'Layout BookMark Item',\n" + 
				"     'enum':getResourceValueByType('LAYOUT_ITEM_TEXT')\n" + 
				" } " + 
				(schema.equals("") ? "" : ",\n" + schema) +
				"\n}";
		
		return schema;
	} 
	
}

