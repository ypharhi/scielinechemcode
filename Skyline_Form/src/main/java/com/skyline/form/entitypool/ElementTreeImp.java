package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import com.skyline.form.bean.LookupType;
import com.skyline.form.entity.Element;
import com.skyline.form.service.FormState;

/**
 * 
 * Tree view element
 *
 * The tree gets the data from view (see Adama's view: fg_i_tree_connection_v).
 * Every record of the view should looks like this: Project@100897@Yaron1@SUBPROJECT.
 * Project - current sturct
 * 100897 - formId
 * Yaron1 - sturct Name
 * SUBPROJECT - next struct 
 * 
 * some of the Tree feathers:
 * ** Filter projects -> ddl with categories of projects. It can filter tree itself
 * 			getCriteriaOptions - returns row of project's categories for Filter project ddl on the tree. 
 * 			This row uses in getInitHtml for creating ddl. Selected in ddl value are teken from fg_formlastsavevalue 
 * 			(if display was Saved) of 'My Items" is selected by default.
 * 			This work with Project according to value was set in form builder -> tree element ->  Root Object.
 * 
 * ** the first column of the view is the root and should be named as 'ROOT'
 * 
 * 
 */
public class ElementTreeImp extends Element {	
	
	private String catalogItem;
	private String rootObject;
	
	@Value("${tree.treeSearchSizeLimit:1000}")
	private int treeSearchSizeLimit;  // if not set in prop the default value should match FormApiElementsService variable (-1 for no limit)

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try {
			if (super.init(stateKey, formCode, impCode, initVal).equals("")) {
				catalogItem = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "catalogItem");
				rootObject = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "rootObject");
				
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
		HashMap<String, String> html = new HashMap<String, String>();
		if (catalogItem.equals("")) {
			return html;
		}
		if (rootObject.equals("")) {
			return html;
		}
		String tableName = generalUtilFormState.getFormCatalogDBTable(stateKey, formCode, catalogItem.substring(0,catalogItem.indexOf(".")));		
		
		String lastCriteria = "";
		String lastSecondCriteria ="";
		String searchRow = "";
		String searchInputName = "_input";
		try {
			try {
				JSONObject objValue = new JSONObject(value);
				lastCriteria = objValue.getString("_filter").toString();
				lastSecondCriteria = objValue.has("_filter_2")?objValue.getString("_filter_2").toString():"";
				searchRow = objValue.getString("_searchRow").toString();
		    } catch (Exception ex) {
		        try {
		        	new JSONArray(value);
		        } catch (Exception ex1) {
		        	value = "";
		        }
		    }
			
			StringBuilder selectedCriteria = new StringBuilder();
			String criteriaSelectValue = getCriteriaOptions(stateKey, lastCriteria,generalUtil.getEmpty(rootObject, generalUtil.getFirstCsv("")), "" , selectedCriteria,"");
			String experimentCriteria =  getCriteriaOptions(stateKey, lastSecondCriteria,"Experiment", "" , selectedCriteria,"tree");
			
			String criteriaSelect = 
					"<label style=\"" + "\" class=\"text-left " + domId +"-select datatableapiselectloadinglabel \">Filter projects: " 
					+ "<select id=\"" + domId + "_ddlFilterProject\" style=\"margin-left:0px;\"" + "\" class=\"" + domId +"-select datatableapiselect datatableapiselectloading\""+ "onchange=\"onChangeProjectFilter("+domId+".id,this.id,'Project');\" >\n"
							+ criteriaSelectValue 
						+ "</select>\n"
						+"</label>"
						+"<label style=\"" + "\" class=\"text-left " + domId +"-select datatableapiselectloadinglabel \">Filter experiments: " 
						+ "<select id=\"" + domId + "_ddlFilterExperiment\" style=\"margin-left:0px;\"" + "\" class=\"" + domId +"-select datatableapiselect datatableapiselectloading\""+ "onchange=\"onChangeProjectFilter("+domId+".id,this.id,'Experiment');\" >\n"
						+ experimentCriteria 
					+ "</select>\n"
					+"</label>"
					+ "<div><input id=\"" + domId + searchInputName +"\" style= \"width: 225px;\" class=\"" + domId +"-select alphanumInputForm datatableapiselect datatableapiselectloading\" alphanumallowchars=\"34,44\" autocomplete=\"off\" value="+searchRow+">\n"
						+ "<i title=\"Search " + (treeSearchSizeLimit > 0 ?"(last " + treeSearchSizeLimit + " results)":"") + " in the tree\" id=\"search_button"+searchInputName+"\" onclick=\"onClickFindButton("+domId+".id,this.id);\" style=\"margin-left: 5px;cursor: pointer;color: #2779aa;margin-top:5px;font-size:larger;\" class=\"fa fa-search \"></i>"
						+ "<i class=\"fa fa-trash \" title=\"Remove search\" style=\"cursor:pointer;font-size:1.5em;\" onclick=\"onClickDeleteSearch("+domId+".id);\"></i>"
					+ "</><div>\n";	
			String addKeyUpEventSearchOnEnter = "setTimeout(function () {\r\n" + 
					"    	$(\"#" + domId + searchInputName + "\").keyup(function(e) {\r\n" + 
					"    		if(e.which == 13){ //on enter r\n" + 
					"    			onClickFindButton('"+impCode+"','search_button" + searchInputName + "');\r\n" + 
					"    		}\r\n" + 
					"    	});\r\n" + 
					"	},100);";
			
			//JSON.parse(data)._tree_lastValue.split(',');
			String initTree = "";
			if (searchRow.equals("")) {
				initTree = "	initTree('" + impCode + "',\"" + doOnChangeJSCall + "\",'" + value + "','" + formCode + "');\n";
			} else
			{
				initTree = " onClickFindButton('" + impCode + "', 'search_button" + searchInputName + "', '" + value + "');\n";
			}
			html.put(layoutBookMark, criteriaSelect
					+ "<div id='" + impCode + "' "+ inputAttribute + " element=\"" + this.getClass().getSimpleName() + "\" ></div>"
					+ "<input type=\"hidden\" id=\"" + impCode + "_catalog_hidden\" value=\"" + tableName + "\">"
					+ "<input type=\"hidden\" id=\"" + impCode + "_tree_lastValue\" value=\"\">"
					+ "<input type=\"hidden\" id=\"" + impCode + "_selected\" value=\"\">"
					//+ "<input type=\"hidden\" id=\"" + impCode + "_filter\" value=\"\">"
					+ "<input type=\"hidden\" id=\"" + impCode + "_firstTime\" value=\"0\">"				
					+ "<input type=\"hidden\" id=\"" + impCode + "_doOnChangeJSCall\" value=\"" + doOnChangeJSCall + "\">"
					+ "<script>"					
					+ initTree
					+ addKeyUpEventSearchOnEnter
					+ "<" + "/" + "script>");
			
			//html.put(layoutBookMark + "_ready","");

		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		String htmlBody ="upDateElement({'domId':'" + domId + "'});";
		try {

		}
		catch(Exception e){
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return htmlBody;
	
	}
 
	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		//the function removeObjectFromJsonSchema - Causes bug in ui (SyntaxError: Invalid or unexpected token)
//		schema = generalUtil.removeObjectFromJsonSchema(generalUtil.removeObjectFromJsonSchema(schema, "hidden"),"disable");
		schema = "schema:{\r\n" +	
				"	catalogItem : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : 'Table fields',\r\n" +
				"		items : {\r\n" +
				"			enum : [''].concat(getResourceValueByType('CATALOG_ITEM_%')),\r\n" +
				"			title : 'field:'\r\n" +
				"		}\r\n" +
				"	},\r\n" +
				"    layoutBookMarkItem:{\r\n" + 
				"        type:'string',\r\n" + 
				"        title:'Layout BookMark Item',\r\n" + 
				"        'enum':getResourceValueByType('LAYOUT_ITEM_TEXT')\r\n" + 
				"	},\r\n " +
				" 	rootObject : {\r\n" +
				"		type:'string',\r\n" + 
				"       title:'Root Object',\r\n" +
				"    } " + 
				(schema.equals("") ? "" : ",\n" + schema) +
				"\r\n}";		
		return schema;
	}

	private String getCriteriaOptions(long stateKey, String options, String struct, String displayCatalog, StringBuilder selectedCriteria,String additionalMatchInfo) {
		List<Map<String, String>> sqlCustomMapList = null;
		//get criteria from the maintenance
		List<Map<String, String>> sqlPoolMapList =  formDao.getFromInfoLookupAllContainsVal("SysConfSQLCriteria", LookupType.NAME, "%."+ (struct.equals("NA")?displayCatalog:struct) +"."+formCode);
	
		//get parent map if exists and custom the maintenance criteria
		FormState formState_ = generalUtilFormState.getFormState(stateKey, formCode);
		Map<String,String> currentFormMap = generalUtilFormState.getFormParam(stateKey, formCode);
		String parentFormCode = currentFormMap.get("$P{PARENT_FORMCODE}");
		String parentId = currentFormMap.get("$P{PARENT_ID}");
		if(generalUtil.getNull(parentFormCode).isEmpty() && !generalUtil.getNull(parentId).isEmpty() && !parentId.equals("-1")){
			parentFormCode = formDao.getFormCodeBySeqId(parentId);
		}
		if(parentFormCode != null) {
			try{
				Map<String,String> parentFormMap = generalUtilFormState.getFormParam(stateKey, parentFormCode);
			    sqlCustomMapList = formState_.customCriteriaList(impCode, struct, stateKey, sqlPoolMapList, parentFormMap, currentFormMap);
			}
			catch(Exception e){
				sqlCustomMapList = sqlPoolMapList;
			}
			
		} else {
			sqlCustomMapList = sqlPoolMapList;
		}
		
		//start build criteria
		String criteriaOption = "";
		boolean flag = false;
		for (int i = 0; i < sqlCustomMapList.size(); i++) 
		{
			Map<String, String> sqlPoolMap = sqlCustomMapList.get(i);
			if(!sqlPoolMap.get("IGNORE").equals("1") && (additionalMatchInfo.isEmpty() || sqlPoolMap.get("ADDITIONALMATCHINFO").equals(additionalMatchInfo))){
				String option = sqlPoolMap.get("SYSCONFSQLCRITERIANAME");
				String isDefault = sqlPoolMap.get("ISDEFAULT");
				if( options != null && !options.isEmpty()) {
					 if(option.equals(options)) {
						 criteriaOption +="<option selected value=\""+option+"\">"+option+"</option>\n";
						 selectedCriteria.append(option);
						 flag = true;
					 }
					 else {
						 criteriaOption += "<option value=\""+option+"\">"+ option+"</option>\n";
					 }
				 } else {
					 if(isDefault.equals("1") && !flag ) {
						 criteriaOption +="<option selected value=\""+option+"\">"+option+"</option>\n";
						 selectedCriteria.append(option);
						 flag = true;
					 }  else {
						 criteriaOption += "<option value=\""+option+"\">"+ option+"</option>\n";
					 }
				 }
			}
		}
		if(formCode.equals("Maintenance")) {
			criteriaOption ="<option value=\"ALL\">ALL</option>\n" + criteriaOption;	 
			criteriaOption +="<option selected value=\"Active\">Active</option>\n";
            selectedCriteria.append("Active");
		}
		else if(sqlCustomMapList.isEmpty() || !flag)	{ 
			 criteriaOption ="<option selected value=\"ALL\">ALL</option>\n" + criteriaOption;	 
			 selectedCriteria.append("ALL");
		 } else {
	 		 criteriaOption ="<option value=\"ALL\">ALL</option>\n" + criteriaOption;
	 	 }
		return criteriaOption;
	}
	
//	@Override
//	public Map<String, String> getCatalogItemFilterMapByInputVal(String inputVal, boolean isCurrentElementChange) {
//		Map<String, String> filterMap = new HashMap<String, String>();			
//		String[] inputValArray;	
//		if(!generalUtil.getNull(inputVal).trim().equals("")) {
//			JSONObject o = new JSONObject(inputVal);
//			String _selected = o.getString("_selected");
//			if(_selected.equals(""))
//				return filterMap;
//			inputValArray = _selected.split(",");		
//			if(inputValArray.length >=2) {
////				filterMap.put(inputValArray[1] + "_id", inputValArray[0]);	
//				filterMap.put(impCode + "." + inputValArray[1] + "_id", inputValArray[0]);	
//			}				
//		}		
//		return filterMap;
//	}
}
