package com.skyline.form.entitypool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.dal.GeneralDao;
import com.skyline.form.entity.Element;

/**
 * 
 * ElementDataTableImp : 
 * 
 * data table, columns can be chosen from the schema by catalog
 *
 */
public class ElementDataTableImp extends Element {	
	
	@Autowired
	private GeneralDao generalDao;
	
//	@Autowired
//	private GeneralUtil generalUtil;

	private String catalog;
	private String isDistinct;
	private String recordPerPage;	
	private String catalogItem;

	private static final Logger logger = LoggerFactory.getLogger(ElementDataTableImp.class);

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{			
				//catalog = generalUtil.getJsonVal(formCode, initVal, "catalog");
				catalogItem = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "catalogItem");
				catalog = (catalogItem.equals("") ? "" : catalogItem.substring(0,catalogItem.indexOf(".")));
				isDistinct = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "isDistinct");				
				recordPerPage = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "recordPerPage");					
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
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) { //TODO support more than 1 ireport (make unique ids in form)
		HashMap<String, String> html = new HashMap<String, String>();
		String hidden = (isHidden) ? " $('[id=\"" + domId + "_wrapper\"]').css('visibility','hidden'); ": "";
		JSONArray jsonColumnsList;
		List<String> metaDataList = getMetaData(stateKey);
		List<String> dateList = getDateList(metaDataList);
		String clearDataWhenEmpty ="";
		try {
			JSONObject tableData = renderEmpty?null:tableData(stateKey, dateList);
			if (tableData == null) {
				clearDataWhenEmpty = "table.clear().draw(); ";
				tableData = new JSONObject();
				List<List<String>> listOfList = new ArrayList<List<String>>();
				List<String> rowList = new ArrayList<String>();
				
				
				jsonColumnsList = new JSONArray();
				JSONObject json = new JSONObject();
				if (metaDataList.isEmpty()){
					json.put("showPageFilter", "0");
					json.put("title", "No Data Found");
					jsonColumnsList.put(json);				
				}
				else {
					for(String mdl :metaDataList){
						json.put("showPageFilter", "0");
						json.put("title",mdl.split(":")[0]);
						jsonColumnsList.put(json);
						rowList.add("No Data Found");
						json = new JSONObject();
					}
				}
				tableData.put("columns", jsonColumnsList);
				listOfList.add(rowList);
				JSONArray jsonDataList = new JSONArray(listOfList);
				tableData.put("data", jsonDataList);
			}			
				
			jsonColumnsList = new JSONArray(tableData.get("columns").toString());//tfoot size
			
			html.put(layoutBookMark, "<input type=\"hidden\" name=\"metaData\" value=\"" + metaDataList.toString() + "\">" +
					"<table id=\"" + domId + "\" class=\"display\" width=\"100%\" " + inputAttribute + " element=\"" + this.getClass().getSimpleName() + "\"><caption>" + getLabel()
							+ "</caption></table>" + "<script>" + " $('[id=\"" + domId + "\"]').append('"+tfoot(jsonColumnsList.length())+"'); "
				+ " var table = $('[id=\"" + domId + "\"]').DataTable({\"bDestroy\": true,\"pagingType\": \"full_numbers\",dom: 'Blfrtip', buttons: ['copy', 'csv', 'excel', 'pdf', 'print'],"+ dataTableOptions() +" data: "+tableData.get("data").toString()
				+",columns: "+tableData.get("columns").toString()+"}); $('table').css('width','100%'); "
				
				+" $('#" + domId + " tfoot th').each( function () { " 
				+" $(this).html('"
				+ "<select style=\"margin-top:10px;\" onchange=\"optionChanged(this)\" >" + 
				"	<option title=\"Contain\" value=\"co\" selected>|*|</option>" + 
				"	<option title=\"Not Contain\" value=\"cn\" >| |</option>" + 
				"	<option title=\"Equal\" value=\"eq\">=</option>" + 
				"	<option title=\"Equal\" value=\"ne\">&lt;&gt;</option>" + 
				"	<option title=\"Greater than\" value=\"gt\">&gt;</option>" + 
				"	<option title=\"Greater than or Equal\" value=\"ge\">&gt;=</option>" + 
				"	<option title=\"Less than\" value=\"lt\">&lt;</option>" + 
				"	<option title=\"Less than or Equal\" value=\"le\">&lt;=</option>" + 
				"</select><span class=\"break\"></span>"				
				+ "<input type=\"text\" class=\"firstString\" onkeypress=\"if ( event.which == 59 || event.keyCode == 59) return false;\">"				
				+ "');"		
				//+ "$(this).find('input').css('width',$(this).find('select').css('width'))"
				+" }); "
				+ "table.columns().every( function () { "
				+"	var that = this;  searchDatatableOld(that); });"				
				+ " datatableStyles('"+domId+"',table); " + clearDataWhenEmpty 				
				+ hidden
				+ "<" + "/" + "script>");
		
		
		
		html.put(layoutBookMark + "_ready", "$('#" + domId + "').on('change', function(){" + doOnChangeJSCall + "});");
				
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
		String htmlBody="";
		try{			
			List<String> metaDataList = getMetaData(stateKey);
			List<String> dateList = getDateList(metaDataList);
			JSONObject tableData = renderEmpty?null:tableData(stateKey, dateList);
			if (tableData == null) { //yp 20112016 fix bug when no data found (also in js general func)
				htmlBody = "upDateElement({'isHidden':'" + isHidden + "','domId':'" + domId + "','type':'dataTable_clear'});";
			} else {
				htmlBody = "upDateElement({'isHidden':'" + isHidden + "','val':{"+dataTableOptions()+"'data':'"+tableData.get("data").toString()+"','columns':'"+tableData.get("columns").toString()+"'}"
						+ ",'domId':'" + domId + "','type':'dataTable'});";
			} 
		}
		catch(Exception e){
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return htmlBody;
	
	}
	private String dataTableOptions()
	{
		String options="";	
		if(!recordPerPage.equals(""))
		{		
			Set<Integer> sortedSet = new HashSet<Integer>(Arrays.asList(10,25,50,100,Integer.parseInt(recordPerPage)));
			String sorted = new ArrayList<Integer>(new TreeSet<Integer>(sortedSet)).toString();			
			options+= " \"pageLength\": "+ recordPerPage + ",";			
			options+= " \"lengthMenu\": ["+sorted+", "+sorted+"]" + ",";			
		}
		return options;
	}
	private JSONObject tableData(long stateKey, List<String> dateList)
	{
		if(catalog.equals("")) {
			return null;
		}
//		String sql = generalUtil.getReflactionString(formCode, "$C{" + catalog + ".getSql."
//				+ isDistinct.toLowerCase() + ".false}"); //use reflection only if no other way
		String sql = generalUtilFormState.getFormCatalogDBSql(stateKey, formCode, catalog, isDistinct.toLowerCase(), impCode, "ALL");
		
		logger.info("Data table SQL: " + sql);
		
		return generalDao.getJSONObjectForDTBySql("select " + catalogListToValidSelect(catalogItem) + " from ("+ sql +") " + catalog + " where ROWNUM <=1000",dateList);	
	}
	
	private String catalogListToValidSelect(String catalogItem) {
		// TODO better for version > 9.6
		return catalogItem.replace(".", ".\"").replace(",", "\",") + "\"";
	}

	private List<String> getMetaData(long stateKey){
		List<String> col = new ArrayList<String>();
		if(catalog.equals("")) {
			return col;
		}
//		String tableName = generalUtil.getReflactionString(formCode, "$C{" + catalog + ".getTableName}");	
		String tableName = generalUtilFormState.getFormCatalogDBTable(stateKey, formCode, catalog);	
		String[] catalogItemArray = catalogItem.split(",");		
		Map<String, String> returnMap;
		returnMap = generalDao.getMetaData(tableName);
		if (returnMap != null) {
			for(String str : catalogItemArray)
			{
				for (Map.Entry<String, String> entry : returnMap.entrySet()) {
					if(str.substring(str.indexOf(".")+1).equals(entry.getKey())){
						col.add(str.substring(str.indexOf(".")+1) + ":" + entry.getValue());
						break;
					}
				}
			}
		}
		return col;
	}
	private List<String> getDateList(List<String> metaData){
		List<String> dateList = new ArrayList<String>();
		for(String str : metaData){
				String[] col = str.split(":");
			if(col[1].equals("DATE")) {
				dateList.add(col[0]);
			}
		}
		return dateList;
	}
	
	private String tfoot(int size)
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<size;i++) {
			sb.append("<th></th>");
		}		
		return "<tfoot><tr>" + sb.toString() + "</tr></tfoot>";
	}	
 
	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		//the function removeObjectFromJsonSchema Causes bug in ui (SyntaxError: Invalid or unexpected token)
//		schema = generalUtil.removeObjectFromJsonSchema(generalUtil.removeObjectFromJsonSchema(schema, "hidden"),"disable");
		schema = "schema:{\r\n" + 
//				"    catalog:{\r\n" + 
//				"        type:'string',\r\n" + 
//				"        title:'Catalog',\r\n" + 
//				"        'enum':getResourceValueByType('CATALOGDB_IMP_CODE')\r\n" + 
//				"    },\r\n" + 
//				"	catalogItem : {\r\n" +
//				"		type : 'array',\r\n" +
//				"		title : 'Table fields',\r\n" +
//				"		items : {\r\n" +
//				"			enum : [''].concat(getResourceValueByType('CATALOG_ITEM_%')),\r\n" +
//				"			title : 'Field:'\r\n" +
//				"		}\r\n" +
//				"	},\r\n" +
				"	catalogItem : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : 'Table fields',\r\n" +
				"		items : {\r\n" +
				"			enum : [''].concat(getResourceValueByType('CATALOG_ITEM_%')),\r\n" +
				"			title : 'field:'\r\n" +
				"		}\r\n" +
				"	},\r\n" +
				"    isDistinct:{\r\n" + 
				"        type:'string',\r\n" + 
				"        title:'Hide Duplication',\r\n" + 
				"        'enum':[\r\n" + 
				"            'False',\r\n" + 
				"            'True'\r\n" + 
				"        ]\r\n" + 
				"    },\r\n" + 
				"    recordPerPage:{\r\n" + 
				"        type:'string',\r\n" + 
				"        title:'Record Per Page',\r\n" + 			
				"    },\r\n" + 
				"    layoutBookMarkItem:{\r\n" + 
				"        type:'string',\r\n" + 
				"        title:'Layout BookMark Item',\r\n" + 
				"        'enum':getResourceValueByType('LAYOUT_ITEM_TEXT')\r\n" + 
				"    } " + 
				(schema.equals("") ? "" : ",\n" + schema) +
				"\r\n}";
		
		
//		schema = "schema:{\r\n" + 			
//				"    pageLength:{\r\n" + 
//				"        type:'string',\r\n" + 
//				"        title:'Page Length',\r\n" + 			
//				"    }\r\n" + 			
//				(schema.equals("") ? "" : ",\n" + schema) +
//				"\r\n}";
		
		
		return schema;
	} 
	///
	
//	@Override
//	public boolean isCatalogFlowElement() {
//		return true;
//	}

}
