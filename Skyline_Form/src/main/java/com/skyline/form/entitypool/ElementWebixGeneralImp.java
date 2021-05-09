package com.skyline.form.entitypool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.dal.GeneralDao;
import com.skyline.form.entity.Element;

/**
 * 
 * @author Administrator
 * webix cell that is defined as combo or select or richselect can have disabled options as defined in the active attribute of the option.
 * 
 * ** getInitSchemaVal 
 * 		* 1.Columns, displayed as DDL in table - is necessary once for evaluate list in DDLs   
 *  
 */

public class ElementWebixGeneralImp extends Element {
	
	@Autowired
	private GeneralDao generalDao;
	private String columnsConfigView;
	private String tableDataInitView;
	private String ddlColumnID;
	private String ddlColumnView;
	private String[] ddlColumnIDArray, ddlColumnViewArray;
	private boolean useDataInitViewOverWebixData;
//	private String formCode;

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			this.formCode = formCode;
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{
				
				columnsConfigView = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "columnsConfigView");	
				tableDataInitView = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "tableDataInitView");	
				ddlColumnID = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "ddlColumnID");
				ddlColumnIDArray = ddlColumnID.split(",");
				ddlColumnView = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "ddlColumnView");
				ddlColumnViewArray = ddlColumnView.split(",");
				useDataInitViewOverWebixData = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "useDataInitViewOverWebixData"), false);
 
				if(ddlColumnIDArray.length != ddlColumnViewArray.length) 
				{
					return "Creation failed - different size of elements";
				}
				
				return "";
			}
			return "Creation failed";
		}
		catch(Exception e){
			generalUtilLogger.logWrite(e);
			return "Creation failed";
		}
	}

	@Override
	public Map<String, String> getInitHtml(long stateKey, String formId, boolean renderEmpty, String value, String userLastSaveVal, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		HashMap<String, String> html = new HashMap<String, String>();
		try 
		{
			value = (value.equals("-1")) ? "" : value;
			List<String> list = new ArrayList<String>();
			JSONArray defaultDataArr = new JSONArray();
			JSONObject obj = new JSONObject();
			JSONObject additDataObj = new JSONObject();
			String tabledivid = "", tableid = "", tableinithtml = "";
			String displayStyle = (isHidden)?"display:none;":"display:block;";
			String isNew = "1";
			String tableData = "[]";
			
			/* -------------- build DDL from DB view  ------------------- */ 
			for (int i = 0; i < ddlColumnIDArray.length; i++) 
			{
				try {
					String _view = ddlColumnViewArray[i];
					list = generalDao.getListOfStringBySql(_view);
					JSONArray arrt2 = new JSONArray();
					for(String str:list)
					{
						JSONObject objt1 = new JSONObject(str);
						arrt2.put(objt1);
					}
					additDataObj.put(ddlColumnIDArray[i], arrt2);
				} 
				catch (Exception e) 
				{
					generalUtilLogger.logWrite(e);
					e.printStackTrace();
				}
			}
			/* end*/
			
			/*------------ get columns configuration from DB view ------------------------------------------------------ */
			String columnsConfigStr = generalDao.selectSingleString("select columns_object from "+columnsConfigView +"");			
			try 
			{
				new JSONArray(columnsConfigStr);
		    } 
			catch (JSONException e) 
			{
				columnsConfigStr = "[]";
				System.out.println("Not a valid JSONArray object");
		        generalUtilLogger.logWrite(e);
				e.printStackTrace();
		    }
			/* end */
			
			/* --------------- provide table data from default view (initialization) or from fg_clob_files DB table -------------- */
			if(useDataInitViewOverWebixData || value.equals(""))
			{
				try 
				{
					if(!tableDataInitView.equals(""))
					{
						defaultDataArr = generalUtilFormState.getWebixGeneralTableData(tableDataInitView);
						tableData = defaultDataArr.toString().replace("\\", "\\\\");
					}
				} 
				catch (Exception e) {
					generalUtilLogger.logWrite(e);
					e.printStackTrace();
				}
			}
			else
			{
				String parentID = generalUtilFormState.getFormParam(stateKey, formCode, "$P{FORMID}");
				String fullData = generalUtilFormState.getStringContent(value, formCode, domId, parentID);
				if(!fullData.equals(""))
				{
					try 
					{
						obj = new JSONObject(fullData);
						tableData = obj.getJSONArray("data").toString();
						isNew = "0";
					} 
					catch (Exception e) {
						generalUtilLogger.logWrite(e);
						e.printStackTrace();
					}
				}
			}
			/* end */
			tabledivid = "tableDiv_"+domId;
			tableid = "tableID_" + domId;
			tableinithtml += "initElementDataTableWebixGeneral('" + domId + "','"+tableid+"','"+tabledivid+"','"+columnsConfigStr+"','"+tableData+"','"+additDataObj+"',"+isNew+",true);\n";
			html.put(layoutBookMark, 
					"<div id=\"" + domId + "_Parent\" style=\""+displayStyle+"\" class=\"webixTableParent\" isfirstload=\"1\" parentElement=\"" +  getParentElement()  + "\" >" + 
							"<h2 class=\"cssStaticData\">" + getLabel() + "</h2>"+	
							"<div>"
								//+"<button class=\"button\" type=\"button\" onclick=\"getOutputDataGeneral('" + domId + "')\">Get Output</button>\n"
								//+ "<button class=\"button\" type=\"button\" onclick=\"elementWebixValidation()\">Validate</button>\n"
								+ "<div name=\"parentWebixContainer\" id=\"" + domId + "\" elementID=\"" + value + "\"  element=\"" + this.getClass().getSimpleName() + "\" " + inputAttribute + ">"
								+ 		"<div class=\"webix_container\" isWebixTableHidden=\""+isHidden+"\" childTableID=\""+tableid+"\" id=\""+tabledivid+"\" style=\"margin-top: 15px;\"></div>"
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
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		
		return "";
	}
 
	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema= "schema:{ \n" + 
				"	columnsConfigView:{  \n" + 
				"		      type:'string',\n" + 
				"		      title:'Columns Configuration view (DB view name)'\n" + 
				"		   },\r\n" + 	
				"	tableDataInitView:{\r\n" + 
				"		      type:'string',\r\n" + 
				"		      title:'View that provide initialization data for table'\r\n" + 
				"		   },\r\n" +
				"	useDataInitViewOverWebixData:{\r\n" + 
				"		      type:'boolean',\r\n" + 
				"		      title:'Use initialization view over webix data'\r\n" + 
				"		   },\r\n" + 
				"	ddlColumnID : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : '1.Columns, displayed as DDL in table cell (evaluate once in the form initiation)',\r\n" +
				"		items : {\r\n" +
				"			type : 'text',\r\n" +
				"			title : 'Column ID:'\r\n" +
				"		}\r\n" +
				"	},\r\n" +
				"	ddlColumnView : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : '2.DB view used to provide data for that columns',\r\n" +
				"		items : {\r\n" +
				"			type : 'text',\r\n" +
				"			title : 'DB view:'\r\n" +
				"		}\r\n" +
				"	}\r\n" +
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}
}

