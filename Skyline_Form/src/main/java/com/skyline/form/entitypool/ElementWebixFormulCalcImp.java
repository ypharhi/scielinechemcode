package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.entity.Element;
import com.skyline.form.service.GeneralUtilFormState;


public class ElementWebixFormulCalcImp extends Element {	
	
	@Autowired
	private GeneralUtilFormState generalUtilFormState;	
	private String formCode;

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			this.formCode = formCode;
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
			JSONObject obj = new JSONObject();
			String tabledivid = "", tableid = "", tableinithtml = "", factorVal = "", solidVal = "", densVal = "";
			String isNew = "1";
			String tableData = "[]";
			
			String parentID = generalUtilFormState.getFormParam(stateKey, formCode, "$P{FORMID}");
			if(value.equals(""))
			{
				try 
				{
					
					if(formCode.equals("StepFr"))
					{
						fullDataArr = generalUtilFormState.getWebixExperimentStepCalcData(parentID);
					}
					else if(formCode.equals("ExperimentFor"))
					{
						fullDataArr = generalUtilFormState.getWebixFormulCalcData(parentID);
					}
					tableData = fullDataArr.toString();	
					if(fullDataArr.length() > 0)
					{
						obj = fullDataArr.optJSONObject(0);
						factorVal = obj.optString("factor"); 
						densVal = obj.optString("density");
					}
				} 
				catch (Exception e) 
				{
					generalUtilLogger.logWrite(e);
					e.printStackTrace();
				}
			}
			else
			{
				String fullData = "";
				if(!value.equals("0")) { // yp add 0 to webixExperimentCopy default value (when experiment is empty)
					fullData = generalUtilFormState.getStringContent(value, formCode, domId, parentID).replace("'","&#39;");
				} else {
					value = "";
				}
				 
				if(!fullData.equals(""))
				{
					try 
					{
						obj = new JSONObject(fullData);
						tableData = obj.getJSONArray("data").toString();
						factorVal = obj.optString("factor"); 
						solidVal = obj.optString("solid"); 
						densVal = obj.optString("density");
						isNew = "0";
					} 
					catch (Exception e) 
					{
						generalUtilLogger.logWrite(e);
						e.printStackTrace();
					}
				}
			}
			tabledivid = "tableDiv_"+domId;
			tableid = "tableID_" + domId;
			tableinithtml += "initElementDataTableWebixFormul('" + domId + "','"+tableid+"','"+tabledivid+"','"+tableData+"',"+isNew+",true);\n";
			
			html.put(layoutBookMark, 
					"<div id=\"" + domId + "_Parent\" style=\"min-width:700px;margin-left: 2px;border:1px solid #bdbdbd;background-color:white;padding:28px 31px;border-radius:3px;border-collapse:collapse;border-spacing:0;margin-bottom:20px\" isfirstload=\"1\" parentElement=\"" +  getParentElement()  + "\" >" + //class=\"dataTableParent\"
							"<h2 class=\"cssStaticData\">" + getLabel() + "</h2>"+	
							"<div>"
								+"<button class=\"button\" type=\"button\" name=\"webixContainerButtons\" onclick=\"uploadFormulationData('" + domId + "')\" style=\"margin-left: 0;\">Upload</button>\n"
								//+"<button class=\"button\" type=\"button\" onclick=\"getOutputDataFormul('" + domId + "')\">Get Output</button>\n" 
								//+"<button class=\"button\" type=\"button\" onclick=\"elementWebixValidation()\">Validate</button>\n"
								+ "<div style=\"display: inline-block;margin-left:50px;\">"
								+ 		"<label style=\"display: inline-block;text-align: right;margin-right:10px\">Factor</label>"
								+ 		"<input id=\"factor_" + domId + "\" value=\""+factorVal+"\" name=\"webixContainerInputFields\" onchange=\"calcAllFormul('"+domId+"')\" onkeypress=\"validateDecimal(event.keyCode, event);\" type=\"text\" style=\"display: inline-block;width:150px;\" />"
								+ "</div>\n"
								+ "<div style=\"display: inline-block;margin-left:50px;\">"
								+ 		"<label style=\"display: inline-block;text-align: right;margin-right:10px\">%Solid</label>"
								+ 		"<input id=\"solid_" + domId + "\" value=\""+solidVal+"\" originalValue=\""+solidVal+"\" name=\"webixContainerInputFields\" type=\"text\" readonly style=\"display: inline-block;width:150px;\" />"
								+ "</div>\n"
								+ "<div style=\"display: inline-block;margin-left:50px;\">"
								+ 		"<label style=\"display: inline-block;text-align: right;margin-right:10px\">Density(g/ml)</label>"
								+ 		"<input id=\"density_" + domId + "\" value=\""+densVal+"\" name=\"webixContainerInputFields\" onchange=\"calcAllFormul('"+domId+"')\" onkeypress=\"validateDecimal(event.keyCode, event);\"  type=\"text\" style=\"display: inline-block;width:150px;\" />"
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
		
		String htmlBody = "";
		if(formCode.equals("StepFr"))
		{
			String parentID = generalUtilFormState.getFormParam(stateKey, "StepFr", "$P{FORMID}");
			boolean isPlnState = (generalUtilFormState.getFormParam(stateKey, "StepFr", "$P{SNAPSHOT_FLAG}").equals("1"))?true:false;
			boolean isRadioPln = (generalUtilFormState.getFormParam(stateKey, "StepFr", "$P{CURRENT_PLANNED_ACTUAL}").equals("Planned"))?true:false;
			boolean isPln = isPlnState && isRadioPln;
			
			String fullData = "{}";
			try {
				fullData = generalUtilFormState.getWebixContent(parentID, isPln);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			if(fullData.equals("") && !isPlnState) {
//				fullData = generalUtilFormState.getWebixContent(parentID, false);
//			} 
			
			htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "','isMandatory':'"
					+ isMandatory + "','val':'"+ generalUtil.getEmpty(fullData, "{}") +"', 'domId':'" + domId + "','type':'formulWebixExpStepCalc'});";
		}
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
