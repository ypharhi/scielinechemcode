package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.skyline.form.entity.Element;

/**
 * Async Iframe 
 * 
 * Loads Iframes with image components by catalog
 *
 */
public class ElementAsyncIframeImp extends Element {
	
	private String width, height, catalogItem, catalogFormItem;
	//private boolean chemdoodle;
	
	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{
				width = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "width");
				height = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "height");
				catalogItem = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "catalogItem");
				catalogFormItem = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "catalogFormItem");
//				chemdoodle = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, initVal, "chemdoodle"),false);
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
		StringBuilder innerHTML = new StringBuilder();
		StringBuilder script = new StringBuilder();
		JSONArray ja = null;
		JSONObject jo;
		int length;
		String title, fileId, catalogItemData ="";
		String hidden = (isHidden)? "display:none;":"";
		
		if (renderEmpty) {
			return new HashMap<String, String>();
		} 
		
		String catItem_ = catalogItem + catalogFormItem; //catItem_ will hold the one that isn't empty...
		
		try {
			catalogItemData = generalUtilFormState.getFormCatalogItem(stateKey, formCode, catItem_.split("\\.")[0],
					catItem_.split("\\.")[1], impCode, null);
			ja = new JSONArray("[" + catalogItemData + "]");
			length = ja.length();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new HashMap<String, String>();
		}		
		
		String width_ = "";
		String height_ = "";
		width_ = (width.equals("")) ? "width:100%;" : (width.indexOf("%") != -1) || (width.indexOf("px") != -1) ? "width:" + width + ";": "width:" + width + "px;";
		height_ = (height.equals("")) ? "height:700px;" : (height.indexOf("%") != -1) || (height.indexOf("px") != -1) ? "height:" + height + ";": "height:" + height + "px;";
	
		//****** Img iframes....
		if (catalogItem != null && !catalogItem.isEmpty()) {
			innerHTML.append("<div id=\"" + domId + "_Parent\" style=\"text-align:center;" + hidden + width_ + "\">\n"
					+ "<form id=\"" + domId + "_AsyncIframeAttachmentForm\" method=\"post\" "
					+ "action=\"getAttachment.request\" target=\"" + domId + "_AsyncIframe\" "
					+ "style=\"display:none;\">\n" + "<input name=\"" + domId
					+ "_FILE_ID\" type=\"hidden\" value=\"\">\n" + "<input name=\"" + domId
					+ "_ContentDisposition\" type=\"hidden\" value=\"inline\">\n" + "</form>\n");

			for (int i = 0; i < length; i++) {
				jo = ja.getJSONObject(i);
				fileId = jo.getString("ID");
				if (!generalUtil.getNull(fileId).equals("")) {
					title = jo.getString("TITLE");
					title = (title.indexOf(".") != -1) ? title.substring(0, title.indexOf(".")) : title;

					innerHTML.append("<div id=\"" + domId + "_AsyncIframeParent_" + i + "\" style=\"margin-bottom:20px;"
							+ width_ + "\">\n"
							+ "<div style=\"text-align:left;font-size: 10pt;color: grey;margin-bottom: 5px;font-family: Verdana, Tahoma, Sans-Serif;\" class=\"cssStaticData\">"
							+ title + ":</div>\n" + "<iframe id=\"" + domId + "_AsyncIframe_" + i + "\" name=\"" + domId
							+ "_AsyncIframe_" + i
							+ "\" src=\"about:blank\" class=\"asyncIframe\" style=\"overflow:hidden;display:none;"
							+ width_ + height_ + "\"" + " onload=\"fixIframeContent('" + domId + "_AsyncIframe_" + i
							+ "','" + domId + "_Svg_" + i + "')\">" + "</iframe>\n" + "<img id=\"" + domId + "_Svg_" + i
							+ "\" src=\"../skylineFormWebapp/CSS/comply_theme/loading.svg\">\n"
							+ "<input type=\"hidden\" id=\"" + domId + "_AsyncIframe_" + i
							+ "_FILE_ID\" name=\"fileId\" value=\"" + fileId + "\"> " + "</div>\n");
					// ab 13032018: workaround for open pdf files on page load in IE: function is
					// called before iframe's onload function
					script.append("setTimeout(function(){fixIframeContentIE('" + domId + "_AsyncIframe_" + i + "','"
							+ domId + "_Svg_" + i + "');\n");
					////
					script.append("$('#" + domId + "_AsyncIframeAttachmentForm').attr('target','" + domId
							+ "_AsyncIframe_" + i + "');\n" + "$('[name=\"" + domId + "_FILE_ID\"]').val('" + fileId
							+ "');\n" + "$('#" + domId + "_AsyncIframeAttachmentForm').submit();\n}," + 100 * i + ");"); //added timeout in order to avoid synchronous issues when calling the server 
				}
			}
			
			innerHTML.append("</div>\n<script>\n");
			innerHTML.append(script.toString());
			innerHTML.append("<" + "/" + "script>\n");	
		}
		
		// ****** Form iframes....
		if (catalogFormItem != null && !catalogFormItem.isEmpty()) { 
			
			innerHTML.append(getCollapseExpandButtons(domId));
			String defaultCube = generalUtilFormState.getFormParam(stateKey, formCode, "$P{CUBE}"); // default Cube
			String cubeVal_ = "";
			String frameFormCode_ = "";
			for (int i = 0; i < length; i++) {
				jo = ja.getJSONObject(i);
				fileId = jo.getString("ID"); 
				cubeVal_ = jo.getString("VAL"); 
				frameFormCode_ = jo.getString("FORMCODE"); 
				innerHTML.append("<div id=\"parent_" + domId + "_" +  fileId + "\" style=\"text-align:center;\">\n");

				String url = "init.request?formCode=" + frameFormCode_ + "&formId=" + fileId + "&userId=" + generalUtil.getSessionUserId() + "&stateKey=" + i + stateKey;
				String defaultCubeAttr = generalUtil.getNull(defaultCube).equals(fileId)?" DEFAULT_CUBE_ATTR=1 ":"";
				
				if(defaultCubeAttr.length() > 0) {
					System.out.println("defaultCubeAttr=" + defaultCubeAttr);
				}
				String deleteIcon = "<i class=\"fa fa-trash ignor_data_change\" title=\"Remove " + cubeVal_ + "\" style=\"cursor:pointer;font-size:1.5em;\" onclick=\"onClickdeleteCube('" + domId + "','" + fileId + "')\"></i>";
				
				innerHTML.append("<button id=\"button_" + domId + "_" + fileId + "\" " + defaultCubeAttr + " class=\"collapsible_iframes\">" + deleteIcon + cubeVal_ + "</button>\r\n"
						+ "<div class=\"asyn_iframe_content\">\r\n" + "<iframe id=\"AsyncIframe_" + domId + "_" + fileId + "\" name=\"AsyncIframe_" + domId + "_" + fileId + "\" src=\"" + url
						+ "\" class=\"asyncIframe\" style=\"overflow:hidden;" + width_ + height_ + "\"\"></iframe>\r\n"
						+ "</div>\r\n" + "<p>&nbsp</p>");
				innerHTML.append("</div>\n");
			}
			innerHTML.append("<div id=\"" + domId + "_asyncPlaceHolder\"></div>\n");
			html.put(layoutBookMark + "_ready","makeCollapsibleIframes();");
		}

		html.put(layoutBookMark,innerHTML.toString());
//		html.put(layoutBookMark + "_ready","makeCollapsibleIframes();");
		return html;
	}

	private String getCollapseExpandButtons(String domId) {
		// TODO Auto-generated method stub
		String html = "";
		html = "<div class=\"asyn_iframe_CollapseExpandButtonsDiv\">" + "<button id=\"" + domId
				+ "_Collapse\" type=\"button\" class=\"button collapsible_button\" style=\"margin-left: 0px;\" onclick=\"closeCollapsibleIframes();\">Collapse All</button>" + "<button id=\"" + domId
				+ "_Expand\" type=\"button\" class=\"button collapsible_button\" onclick=\"openAllIframes();\">Expand All</button>" + "</div>";
		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { 
		String htmlBody ="";
		htmlBody = "upDateElement({'domId':'" + domId + "','isHidden':'" + isHidden + "','type':'asyncIframe'});";
		return htmlBody;
	}
	
	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema= "schema:{ \n" + 
				"	width:{  \n" + 
				"		      type:'string',\n" + 
				"		      title:'Width'\n" + 
				"		   },\n" +
				"	height:{  \n" + 
				"		      type:'string',\n" + 
				"		      title:'Height'\n" + 
				"		   },\n" +
				"	catalogItem:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Catalog Img Column',\r\n" + 
				"      'enum':getResourceValueByType('OBJIMG')\r\n" + 
				"   }\r\n," +
				"	catalogFormItem:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Catalog Forms IDs',\r\n" + 
				"      'enum':getResourceValueByType('OBJIDVAL')\r\n" + 
				"   }\r\n" + 
//				"	chemdoodle:{\r\n" + 
//				" 		type: 'boolean',\r\n" + 
//				" 		title: 'Chem Doodle'\r\n" +
//				"   }\r\n" +
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}
}
