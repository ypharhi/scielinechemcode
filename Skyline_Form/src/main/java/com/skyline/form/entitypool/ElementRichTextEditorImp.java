package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.bean.ElementInfoAuditTrailDisplay;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.entity.Element;

/**
 * 
 * ElementRichTextEditorImp: rich text
 * using https://summernote.org/
 * saves data as CLOB in fg_richtext table
 *
 */
public class ElementRichTextEditorImp extends Element 
{
	@Autowired
	private GeneralDao generalDao;
	
	private String placeHolder;
	
	private String width;
	
	private String height;
	
	private String removeButtonsList;

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{
				placeHolder = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "placeHolder");				
				width = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "width");
				height = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "height");
				removeButtonsList = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "removeButtonsList");
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
		String width_ = "";
		String height_ = "";
		width_ = (width.equals("")) ? "" : (width.indexOf("%") != -1) ? "width:"+width+";" : (width.indexOf("px") != -1) ? "width:"+width+";" : "width:"+width+"px;";
		height_ = (height.equals("")) ? "" : (height.indexOf("px") != -1) ? height.replace("px", "") : height;

		String hidden = (isHidden) ? "visibility:hidden;" : "";
		String mandatory = "", labelMandatory = "";		
		if(isMandatory){
			mandatory = "$('#" + domId + "').prop('required',true);\n";
			labelMandatory = "  if($('label[for=\"" + domId + "\"]').length > 0){\r\n" + 
					"		            $('label[for=\"" + domId + "\"]').css('visibility','visible');\r\n" + 
					"		         }\n  ";			
		}
		String disbaled = (isDisabled) ? " disabledclass " : "";
		String displayVal = value.equals("") ? "" : generalUtilFormState.getRichTextContent(value);
		displayVal = validateData(displayVal, value);
		/*String setData = " window.setTimeout(function(){"    //setTimeout should prevent "Permission denied" issue when calling setData very fast/if there are more then one richtext element on the page
						//+ "CKEDITOR.instances."+domId+".setData(\""+displayVal.replace("\n", "").replace("\"", "'") +"\", {	\n"+	
						+ "CKEDITOR.instances."+domId+".setData(\""+displayVal +"\", {	\n" +
					 			"callback: function() \n"+
					 			"{\n"+
					 'instanceReady' event should reset 'prop.dataChanged' flag, because of 'change' event activation on page load and setting 'prop.dataChanged' to 'true' 
					 			    "prop.dataChanged = false;\n"+
					 			   //" console.log('ckeditor instanceReady');\n" +	
					 			"}\n"+
					 		"} );\n"+
					 		"},10);\n";*/
		
		String heightCk = (!height_.equals("")) ?  "height: " + height_ + "," : "";
		String removeButtons = (!removeButtonsList.equals(""))? " removeButtons: ["+removeButtonsList + "],\r\n":"";
		
		html.put(layoutBookMark,"<div id=\"" + domId + "_parent\" class=\"" + disbaled + "\" style=\"" + hidden + "\">\n"			
				+ "<div autocomplete=\"off\"  id=\"" + domId + "\" elementID=\"" + value + "\" class=\"ckeditor\" placeholder=\"" + placeHolder
						+ "\" style=\"border-radius: 5px;" + width_ + height + "\" " + inputAttribute + " "
						+ getAttributes(isDisabled, isMandatory, isHidden) + " element=\""
						+ this.getClass().getSimpleName() + "\">" 
						+ displayVal 
						+ "</div>\n" 
				+ "</div>\n");
		html.put(layoutBookMark + "_ready","$('#"+domId+"').richtext({"+removeButtons+heightCk+"});\n"
				+ mandatory +  labelMandatory);
		
		/*html.put(layoutBookMark,"<div ckeditor id=\"" + domId + "_parent\" class=\"" + disbaled + "\" style=\"" + hidden + "\">\n"			
				+ "<textarea autocomplete=\"off\"  id=\"" + domId + "\" elementID=\"" + value + "\" class=\"ckeditor\" placeholder=\"" + placeHolder
						+ "\" style=\"border-radius: 5px;" + width_ + height + "\" " + inputAttribute + " "
						+ getAttributes(isDisabled, isMandatory, isHidden) + " element=\""
						+ this.getClass().getSimpleName() + "\">" 
						+ displayVal 
						+ "</textarea>\n" 
				+ "</div>\n");
		html.put(layoutBookMark + "_ready","CKEDITOR.replace( '" + domId + "', {\r\n" + 
				heightCk +
				removeButtons +
				"    on: {\r\n" + 
				"        	instanceReady: function( evt ) {\r\n" + 
				//			 set editor disabled in case not 'isNew' 
				"			 setRichTextEditorDisabled('" + domId + "',null,'INSTANCE_READY'); \n "+
//				" 			} \n " +	
								//setData + 
				" 			 prop.dataChanged = false; \n" + 
				//				mandatory +  labelMandatory +						
				"        	},\r\n" +
				"        	change: function( evt ) {\r\n" + 
				" 					if(this.checkDirty()) { \n" + 
		     	//" 						console.log('ckeditor changed');\n" +	
				"                       prop.dataChanged = true;\n" + 
				"						$('#"+domId+"').attr('is_changed_flag','1');\n "+
		     	"       				this.resetDirty(); \n" + 
		     	"        			}\r\n" +
				"        	}\r\n" +
				"    	 }\r\n" + 					
				"} );");*/
		
		return html;
	}

	/**
	 * 
	 * @param displayVal last save content
	 * @param value last save file_id (using in case of an error)
	 * @return A validate content using Jsoup:
	 * this code for example know how to fix the following input:  "<table><table>conc table with no td</table></table>tables<table>with<table>no<table>closer<table>and<p>more<tr>...:)"
	 */
	private String validateData(String displayVal, String value) {
		try {
			if(displayVal.contains("<")) {
				Document doc = Jsoup.parse(displayVal);
				displayVal = doc.body().html();
			}
		} catch (Exception e) {
			displayVal = "[" + value + "] Error while validating this content please contact your administrator for help!";
			e.printStackTrace();
		}
		return displayVal;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp
		String htmlBody;
		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "','isMandatory':'"
				+ isMandatory + "','val':'','domId':'" + domId + "','type':'CKEDITOR'});";
		return htmlBody;
	}
	
	private String getAttributes(boolean isDisabled, boolean isMandatory, boolean isHidden) {
		StringBuilder sb = new StringBuilder();
		if (isDisabled) {
			sb.append("disabled ");
		}
		if (isMandatory) {
			sb.append("required ");
		}	
		return sb.toString();
	}

	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema= "schema:{ \n" + 
				"	placeHolder:{  \n" + 
				"		      type:'string',\n" + 
				"		      title:'PlaceHolder',\n" + 
				"		      'default':'Choose:'\n" + 
				"		   },\n" + 	
				"	width:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Width',\r\n" +
				"   },\r\n" + 
				"	height:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Height',\r\n" +
				"   },\r\n" + 
				"	removeButtonsList:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Remove Buttons List (remove richtext buttons from display)',\r\n" +
				"   }\r\n" + 
			
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}
	
	
	@Override
	public ElementInfoAuditTrailDisplay getAuditTrailValue(String formCode, String key, String postSaveValue, String originValue, String uiDisplayValue)
	{
		ElementInfoAuditTrailDisplay elementValueJobFlag = null;

		try {
			if (postSaveValue.equals(originValue)) {
				elementValueJobFlag = new ElementInfoAuditTrailDisplay(uiDisplayValue, "0");
			} else {
				Map<String, String> map = new HashMap<String, String>();
				// map.put("experimentId_in", elementValueMap.get("EXPERIMENT_ID"));
				map.put("RichText_id_in", postSaveValue);
				map.put("displayType_in", "1");
				String display_ = generalDao.callPackageFunction("", "FG_GET_RICHTEXT_DISPLAY", map);
				elementValueJobFlag = new ElementInfoAuditTrailDisplay(display_, "0");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			elementValueJobFlag = new ElementInfoAuditTrailDisplay("", "1");
		}

		return elementValueJobFlag;
	}
}
