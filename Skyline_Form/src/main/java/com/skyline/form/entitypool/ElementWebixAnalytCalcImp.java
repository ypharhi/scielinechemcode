package com.skyline.form.entitypool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.entity.Element;
import com.skyline.form.service.GeneralUtilFormState;


public class ElementWebixAnalytCalcImp extends Element {	
	
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
			List<JSONArray> fullDataArr = new LinkedList<JSONArray>();
			String tabledivhtml = "", tabledivid = "", tableid = "", tableinithtml = "";
			List<JSONObject> currTableData = new ArrayList<JSONObject>();
			
			String parentID = generalUtilFormState.getFormParam(stateKey, "ExperimentAn", "$P{FORMID}");
			
			if(value.equals(""))
			{
				
				fullDataArr = generalUtilFormState.getWebixAnalytCalcData(parentID, false);
				
				for(int i=0;i<fullDataArr.get(1).length();i++)
				{
					try {
						currTableData = new ArrayList<JSONObject>();
						JSONArray table = fullDataArr.get(1).getJSONArray(i);
						String tableID = table.getString(0);
						String isBasic = table.getString(1);
						String tableHeader = table.getString(2);
										
						JSONArray rows = fullDataArr.get(0);
						for(int j=0; j<rows.length();j++)
						{
							JSONObject obj = rows.getJSONObject(j);
							if(obj.get("materialid").equals(tableID))
							{
								currTableData.add(obj);
							}
						}
						
						tabledivid = "tableDiv_"+domId + "_"+i;
						tableid = "tableID_"+domId + "_" + i;
						tabledivhtml += "<div style=\"margin-top: 15px;\">"	
										//+"<button class=\"button\"  type=\"button\" onclick=\"calc('"+tableid+"')\">calculate</button>\n"
										+"<div class=\"webix_container\" isWebixTableHidden=\"false\" isBasic=\""+isBasic+"\" childTableID=\""+tableid+"\" id=\""+tabledivid+"\">"
										+ "<h2 class=\"cssStaticData\">"+tableHeader+"</h2>"
										+ "</div>"
										+ "</div>\n";
						tableinithtml += "initElementDataTableWebixAnalyt('" + domId + "','"+tableid+"','"+tabledivid+"','"+currTableData.toString()+"',1,true);\n";
					} 
					catch (Exception e) 
					{
						generalUtilLogger.logWrite(e);
						e.printStackTrace();
					}
				}
			}
			else
			{
				String fullData = generalUtilFormState.getStringContent(value, formCode, domId, parentID).replace("'","&#39;");				
				JSONArray arr = new JSONArray(fullData);
				for(int i=0;i<arr.length();i++)
				{
					try 
					{
						JSONObject obj = arr.getJSONObject(i);
						JSONArray table = obj.getJSONArray("data");
						String isBasic = "0";
						String tableHeader = "";
					
						isBasic = table.optJSONObject(0).optString("is_basic");
						tableHeader = table.optJSONObject(0).optString("component_name");										
						tabledivid = "tableDiv_"+domId + "_"+i;
						tableid = "tableID_"+domId + "_" + i;
						tabledivhtml += "<div style=\"margin-top: 15px;\">"	
										+"<div class=\"webix_container\" isWebixTableHidden=\"false\" isBasic=\""+isBasic+"\" childTableID=\""+tableid+"\" id=\""+tabledivid+"\">"
										+ "<h2 class=\"cssStaticData\">"+tableHeader+"</h2>"
										+ "</div>"
										+ "</div>\n";
						tableinithtml += "initElementDataTableWebixAnalyt('" + domId + "','"+tableid+"','"+tabledivid+"','"+obj.toString()+"',0,true);\n";
					} 
					catch (Exception e) 
					{
						generalUtilLogger.logWrite(e);
						e.printStackTrace();
					}
				}
			}
			if(tableinithtml.equals(""))
			{
				tabledivid = "tableDiv_"+domId + "_0";
				tableid = "tableID_"+domId + "_0";
				tabledivhtml += "<div style=\"margin-top: 15px;\">"	
								+"<div class=\"webix_container\" isWebixTableHidden=\"false\" isBasic=\"\" childTableID=\""+tableid+"\" id=\""+tabledivid+"\">"
								+ "<h2 class=\"cssStaticData\"></h2>"
								+ "</div>"
								+ "</div>\n";
				tableinithtml += "initElementDataTableWebixAnalyt('" + domId + "','"+tableid+"','"+tabledivid+"','[]',1,true);\n";
			}
			html.put(layoutBookMark, "<div>"+
										//"<button class=\"button\" type=\"button\" onclick=\"getOutputDataAnalyt('"+domId+"')\" style=\"margin-right: 15px;\">getOutputData</button>\n"+
										//"<button class=\"button\" type=\"button\" onclick=\"elementWebixValidation()\">Validate</button>\n" +
										"<button class=\"button\" name=\"webixContainerButtons\" type=\"button\" onclick=\"calcAllAnalyt('"+domId+"')\" style=\"margin-left: 0;\">Calculate all</button>\n" + 
										"<button class=\"button\" name=\"webixContainerButtons\" type=\"button\" onclick=\"uploadAnalyticalData('" + domId + "')\">Upload</button>\n" +
										"<div name=\"parentWebixContainer\" id=\"" + domId + "\" elementID=\"" + value + "\"  element=\"" + this.getClass().getSimpleName() + "\" " + inputAttribute + ">"
										+ tabledivhtml
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
		return "";
	
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
