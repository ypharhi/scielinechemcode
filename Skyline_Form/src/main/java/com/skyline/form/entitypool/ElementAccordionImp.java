package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.entity.Element;
import com.skyline.form.service.GeneralUtil;

/**
 * Accordion element
 *  
 * 
 * currently works only in the maintenance form (formCode = 'Maintenance',
 * not form with type = maintenance) 
 * 
 *
 */
public class ElementAccordionImp extends Element {
	
	@Autowired
	private GeneralUtil generalUtil;
	  
	private String catalogItem;
	
	private String currentVal;
	
	private String permissionScreenList;
	
	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{			
				catalogItem = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "catalogItem");
				permissionScreenList = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "permissionScreenList");
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
		currentVal = value;
		HashMap<String, String> html = new HashMap<String, String>();		
		List<String> catalogItemData = null;
		if(!renderEmpty) {
			catalogItemData = generalUtilFormState.getFormCatalogItemList(stateKey, formCode, catalogItem.split("\\.")[0], catalogItem.split("\\.")[1], impCode, null);
		} 
		try {
			
			
			String script =				
					"$('#accordion').accordion();\n"
					+ "firstTime = 1;\n"					
					+ "$('.ComplyAccordion li').click(function(e){\n"
					+ "if(e.originalEvent.detail > 1) return;\n"		       
					+ "$('#accordion li').removeClass('ReportItemSelected');\n"
					+ " $(this).addClass('ReportItemSelected');\n"
					+ "});\n";		
			
			html.put(layoutBookMark,  "<div id=\"accordion\">\n" + getAccordionInfo(catalogItemData) + "</div>\n"
					+ "<input type=\"hidden\" id=\"accordion_hidden\" value=\"" + script + "\">\n"						
					);
			
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
		currentVal = value;
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
		//the function removeObjectFromJsonSchema  Causes bug in ui (SyntaxError: Invalid or unexpected token)
//		schema = generalUtil.removeObjectFromJsonSchema(generalUtil.removeObjectFromJsonSchema(schema, "hidden"),"disable");
		schema = "schema:{\r\n" +
				"	catalogItem:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Catalog Column',\r\n" + 
				"      'enum':getResourceValueByType('CATALOG_ITEM_TEXT')\r\n" + 
				"   },\r\n" + 
				"    layoutBookMarkItem:{\r\n" + 
				"        type:'string',\r\n" + 
				"        title:'Layout BookMark Item',\r\n" + 
				"        'enum':getResourceValueByType('LAYOUT_ITEM_TEXT')\r\n" + 
				"   },\r\n " + 
				"	permissionScreenList:{  \n" + 
				"		type:'string',\n" + 
				"		title:'Session Param name with permission Screen csv',\n" + 
				"   }\r\n" +
				(schema.equals("") ? "" : ",\n" + schema) +
				"\r\n}";		
		return schema;
	}
	private String getAccordionInfo(List<String> catalogItemData){	
		String userName = generalUtil.getSessionUserName();
		String screenListCsv = generalUtil.getSessionParam(permissionScreenList);
		String script = "var speed;\n"
					  + "if($('#eMaintenanceTableApi_structCatalogItem option').val() == $(this).attr('id')){\n"
					  + "	return;"
					  + "}\n"					 
					  + "if(firstTime == 1){\n"
					  + "	firstTime = 0;\n"
					  + "	speed = 0;\n"
					  + "}\n"
					  + "else{\n"
					  + "	speed = 'fast';\n"
					  + "}\n"
					  +  "if($(this).attr('id')!='PermissionScheme'&&$(this).attr('id')!='MaterialFunction'){ \n"
					  + "	$('.dataTableApiClone').css('display', 'none'); \n"
					  + "}\n"
					  + "else{\n"
					  + "   $('.dataTableApiClone').css('display', 'inline');\n"
					  + "}\n"
					  + "$('#eMaintenanceTableApi_structCatalogItem option').val($(this).attr('id'));\n "
					  + "$('#eMaintenanceTableApi_structCatalogItem option').text($(this).attr('id'));\n"
				      + "$('#eMaintenanceTableApi_Parent').fadeToggle(speed, function() {\r\n" + 							 
							  "   onElementDataTableApiChange('eMaintenanceTableApi');\r\n" + 
						      "   $('#eMaintenanceTableApi_Parent').fadeToggle('slow');\r\n"	
					  + "  });\n";
					
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<catalogItemData.size();i++){
			String[] array = catalogItemData.get(i).split(",");
			if(showMaintenanceGroupByUser(array[0], userName)) {
				sb.append("<h3>" + generalUtil.getSpringMessagesByKey(array[0].replace(" ", ""),array[0]) + "</h3>\n");			
				sb.append("<div class=\"ComplyAccordion\">\n");			
				sb.append("<ul>\n");
				for(int j=1;j<array.length;j++){
					sb.append("<li>\n");				
					sb.append(" <a class=\"tabref\" id=\"" + array[j] + "\" href=\"#settingContent\"  onclick=\"" + script + 
							"$('#pageTitle').text('Maintenance - ' + $( this ).text());\n" + // kd 25022020 fixed bug-7636: added subtitle to Maintenance screen
							"\">" + generalUtil.getSpringMessagesByKey(array[j],"") + "</a>\n");
					sb.append("</li>\n");
				}
				sb.append("</ul>\n");
				sb.append("</div>\n");
			}
		}
		return sb.toString();
	}

	private boolean showMaintenanceGroupByUser(String groupName, String userName) {
		boolean toReturn = false;
		if(groupName.toLowerCase().startsWith("_system") || groupName.toLowerCase().startsWith("system")) {
			if(userName.equalsIgnoreCase("system")) {
				toReturn = true;
			}
		}  else if (groupName.toLowerCase().startsWith("admin")) { 
			if (userName.equalsIgnoreCase("admin") || userName.equalsIgnoreCase("system")){
				toReturn = true;
			}
		} else {
			toReturn = true;
		}
		return toReturn;
	}

	@Override
	public Map<String, String> getCatalogItemFilterMapByInputVal(String inputVal, boolean isCurrentElementChange) {
		return new HashMap<String, String>();
	}

	@Override
	public Map<String, String> getCurrentParameMapByInputVal(String inputVal) {
		Map<String, String> mIdVal = new HashMap<String, String>();
		mIdVal.put("CURRENT_" + impCode, currentVal);
		return mIdVal;
	}
	
}
