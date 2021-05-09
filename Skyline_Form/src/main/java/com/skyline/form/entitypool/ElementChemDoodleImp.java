package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.dal.ChemDao;
import com.skyline.form.entity.Element;

/**
 * 
 * ChemDoodle component
 *
 * Use server side Marvin Chem (not chemdoodle) 
 * Limitation: only one element of ElementChemDoodleImp can be use in a form unless the name of the the element is end with Pln (workaround for Adama organic step form)
 * ElementChemDoodleImp is define in the general javascript as callback element because of the async promise calls that uses marvin in order to get the chem info 
 * 
 */

public class ElementChemDoodleImp extends Element 
{	
	private String width, height;
	
	@Autowired
	private ChemDao chemDao;

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{						
				width = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "width");
				height = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "height");

				return "";
			}
			return "Creation failed";
		}
		catch (Exception e) {
			generalUtilLogger.logWrite(e);
			return "Creation failed";
		}
	}

	@Override
	public Map<String, String> getInitHtml(long stateKey, String formId, boolean renderEmpty, String value, String userLastSaveVal, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		Map<String, String> html = new HashMap<String, String>();
		
		String width_ = width;
		String height_ = height;
		width_ = (width.equals("")) ? "550px" : (width.indexOf("%") != -1) || (width.indexOf("px") != -1) ? width: width + "px;";
		height_ = (height.equals("")) ? "450px" : (height.indexOf("%") != -1) || (height.indexOf("px") != -1) ? height: height + "px;";		
		String hidden = (isHidden)? "visibility:hidden;display:none;":"";
		
		String mrvData = "";
		String savedCanvas = "";
		String disabled = (isDisabled) ? " class=\"disabledChemDoodle\" " : "";
		value = (value.equals("-1")) ? "" : value;
		if(!value.equals(""))
		{
            mrvData = chemDao.getMRVContent(value);
            if(mrvData.length() > 0)
            {
                mrvData = StringEscapeUtils.escapeHtml4(mrvData);
                savedCanvas = " canvas=\"" + mrvData + "\"";
            }
		}

		html.put(layoutBookMark,
				"<div chemdoodle id=\"" + domId + "\" elementID=\"" + value 
						+ "\" style=\"" + hidden + "\" element=\"" + this.getClass().getSimpleName() + "\" "
						+ inputAttribute + disabled + ">" + "<iframe  id=\"marvin_js"
						+ "\" src=\"../skylineFormWebapp/marvin/editor.html?interframe=true\""
						+ " data-toolbars=\"reaction\"" + savedCanvas + " style=\"overflow: hidden; min-width: " + width_
						+ "; min-height: " + height_ + ";\"" + "></iframe></div>");

		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp
		String htmlBody;
		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "','isMandatory':'"
				+ isMandatory + "','val':'','domId':'" + domId + "','type':'chemdoodle'});";
		return htmlBody;
	}

	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema= "schema:{ \n" + 
				
				"	width:{  \r\n" + 
				"      		type:'string',\r\n" + 
				"      		title:'Width'\r\n" +
				"   },\r\n" + 
				"	height:{  \r\n" + 
				"      		type:'string',\r\n" + 
				"      		title:'Height'\r\n" +
				"   },\r\n" +
				"	singleMolecule:{\r\n" + 
				" 			type: 'boolean',\r\n" + 
				" 			title: 'Single Molecule (not implemented)'\r\n" + 
				"   },\r\n" +
				"	includeToolbar:{\r\n" + 
				" 			type: 'boolean',\r\n" + 
				" 			title: 'Include Toolbar (not implemented)'\r\n" + 
				"   }\r\n" +
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}

}
