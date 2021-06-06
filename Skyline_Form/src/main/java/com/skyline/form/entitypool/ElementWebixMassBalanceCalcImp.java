package com.skyline.form.entitypool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.entity.Element;


public class ElementWebixMassBalanceCalcImp extends Element {	
	
//	@Autowired
//	private GeneralUtilFormState generalUtilFormState;	
	@Autowired
	private GeneralDao generalDao;
	
	private String ddlObjectID;
	private String ddlObjectView;
	private String[] ddlObjectIDArray, ddlObjectViewArray;
	private String formCode;

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			this.formCode = formCode;
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{			   				
				ddlObjectID = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "ddlObjectID");
				ddlObjectIDArray = ddlObjectID.split(",");
				ddlObjectView = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "ddlObjectView");
				ddlObjectViewArray = ddlObjectView.split(",");

				if(ddlObjectIDArray.length != ddlObjectViewArray.length) 
				{
					return "Creation failed - different size of elements";
				}
				
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
			String tabledivhtml = "", tabledivid = "", tableid = "", tableinithtml = "";
			List<String> list = new ArrayList<String>();
			JSONObject additDataObj = new JSONObject();
			
			
			/* -------------- build DDL from DB view  ------------------- */ 
			try {
				for (int i = 0; i < ddlObjectIDArray.length; i++) 
				{
					String _view = ddlObjectViewArray[i];
					list = generalDao.getListOfStringBySql(_view);
					JSONArray arrt2 = new JSONArray();
					for(String str:list)
					{
						try 
						{
							JSONObject objt1 = new JSONObject(str + 1/0);
							arrt2.put(objt1);
						} 
						catch (JSONException e) 
						{
							generalUtilLogger.logWriter(LevelType.WARN,
									"skip expression - " + str + " from view =" + _view + ". json error - " + e.getMessage(),
									ActivitylogType.SQLError, formId);
							e.printStackTrace();
						}
					}
					additDataObj.put(ddlObjectIDArray[i], arrt2);
				}
			} catch (Exception e) {
				generalUtilLogger.logWrite(e);
				e.printStackTrace();
			}
			/* end*/
			
			if(value.equals(""))
			{
				tableinithtml += "setWebixMassBalanceInitData('"+additDataObj+"','" + domId + "');\n";
			}
			else
			{
				String parentID = generalUtilFormState.getFormParam(stateKey, formCode, "$P{FORMID}");
				tableinithtml += "setWebixMassBalanceInitData('"+additDataObj+"','" + domId + "');\n";
				String fullData = generalUtilFormState.getStringContent(value, formCode, domId, parentID);
				try 
				{
					JSONArray arr = new JSONArray(fullData);
					for(int i=0;i<arr.length();i++)
					{
						JSONObject formObj = arr.getJSONObject(i);					
						tabledivid = "tableDiv_"+domId + "_"+(i+1);
						tableid = "tableID_"+domId + "_" + (i+1);
															
						tableinithtml += "initElementDataTableWebixMassBalance('" + domId + "','"+tableid+"','"+tabledivid+"','"+formObj.toString().replace("'", "\\'")+"',0,true);\n";					
						tabledivhtml += "<div class=\"webix_container\" isWebixTableHidden=\"false\" childTableID=\""+tableid+"\" id=\""+tabledivid+"\" style=\"margin-bottom: 20px;\"></div>";
					}
				} 
				catch (Exception e) {
					generalUtilLogger.logWrite(e);
					e.printStackTrace();
				}
				
			}
			
			String infoDomId,infoDiv = "";
			if(formCode.equals("Experiment")||formCode.equals("ExperimentCP"))
			{
				infoDomId = domId+"StepsInfo";
				tableinithtml += formCode.equals("Experiment")?"getAllTablesDataForWebixMassBalanceInfo('"+infoDomId+"');\n":"";
				infoDiv = "<div id=\""+infoDomId+"Container\"></div>";
			}
			
			html.put(layoutBookMark, "<div>"+
										//"<button class=\"button\" type=\"button\" onclick=\"getOutputDataMassBalance('"+domId+"')\" style=\"margin-right: 15px;\">getOutputData</button>\n"+
										//"<button class=\"button\" type=\"button\" onclick=\"elementWebixValidation()\">Validate</button>\n" +
										"<div name=\"parentWebixContainer\" id=\"" + domId + "\" elementID=\"" + value + "\"  element=\"" + this.getClass().getSimpleName() + "\" " + inputAttribute + " style=\"margin-top: 20px;\">"
										+ tabledivhtml
										+ "</div>"
										
										+infoDiv
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
				" },\r\n " + 
				" ddlObjectID : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : '1.DDL object id, placed inside webix element',\r\n" +
				"		items : {\r\n" +
				"			type : 'text',\r\n" +
				"			title : 'Object ID:'\r\n" +
				"		}\r\n" +
				"	},\r\n" +
				" ddlObjectView : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : '2.DB view used to provide data for that objects',\r\n" +
				"		items : {\r\n" +
				"			type : 'text',\r\n" +
				"			title : 'DB view:'\r\n" +
				"		}\r\n" +
				"	}\r\n" +
				(schema.equals("") ? "" : ",\n" + schema) +
				"\n}";
		
		return schema;
	} 
	
}
