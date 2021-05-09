package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.skyline.form.dal.ChemDao;
import com.skyline.form.entity.Element;

/**
 * ChemDoodleSearch: used to invoke doOnChangeJSCall for the chemdoodle
 * component
 *
 * Regular chemdoodle component not invokes doOnChangeJSCall at AJAX change,
 * therefore you can use ChemDoodleSearch as the parent of other elements.
 * 
 * for_ is the id of the chemdoodle component (used in
 * ElementChemDoodleSearchImp.js). text is the text of the button.
 * 
 * Note: the search type can be and defined in chem.searchType prop (smiles[default]/inchi/mol[has a problem because of the location coordinates that effects the matrix - don't use it]) that are already exists in the tables we look at as columns
 */
public class ElementChemDoodleSearchImp extends Element {

	private String for_, text; 

	@Autowired
	private ChemDao chemDao;
	
	@Value("${chem.searchType:smiles}") 
	private String chemSearchType; //"smiles"
	
	@Value("${jdbc.username}")
	private String DB_USER;
	
	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal)
	{
		try
		{
			if (super.init(stateKey, formCode, impCode, initVal).equals(""))
			{
//				type_ = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "type_");
				for_ = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "for_");
				text = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "text");
				return "";
			}
			return "Creation failed";
		} catch (Exception e)
		{
			generalUtilLogger.logWrite(e);
			return "Creation failed";
		}
	}

	@Override
	public Map<String, String> getInitHtml(long stateKey, String formId, boolean renderEmpty, String value, String userLastSaveVal, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory)
	{
		Map<String, String> html = new HashMap<String, String>();
		if (renderEmpty)
		{
			value = "";
		}

		String hidden = (isHidden) ? "visibility:hidden;" : "";
		String disabled = (isDisabled) ? "disabledclass" : "";

		html.put(layoutBookMark,
				"<button type=\"button\" id=\"" + domId + "\" for_=\"" + for_ + "\" class=\"button " + " " + disabled + "\" "
						+ inputAttribute + " " + " style=\"" + hidden + "\" onclick=\"" + doOnChangeJSCall
						+ "\" element=\"" + this.getClass().getSimpleName() + "\">"
						+ generalUtil.getSpringMessagesByKey(text.replaceAll(" ", "_"), text) + "</button>");

		//html.put(layoutBookMark + "_ready", "");
		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory)
	{
		String htmlBody;
		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "','val':'','domId':'"
				+ domId + "','type':'NA'});";
		return htmlBody;
	}

	@Override
	public Map<String, String> getCurrentParameMapByInputVal(String inputVal)
	{
		Map<String, String> mIdVal = new HashMap<String, String>();
		mIdVal.put("CURRENT_" + impCode, inputVal);
		return mIdVal;
	}

	@Override
	public String getInitSchemaVal()
	{
		String schema = super.getInitSchemaVal();
		schema = "schema:{ \n" + " for_:{  \r\n" + "  type:'string',\r\n"
				+ "     title:'For (the id of the chemdoodle component)'\r\n" + " },\r\n" + " text:{  \r\n"
				+ "     type:'string',\r\n" + "     title:'Text (the text of the button)'\r\n" + " }\r\n"
				+ (schema.equals("") ? "" : ",\n" + schema) + "     }";
		return schema;
	}

	@Override
	public Map<String, String> getCatalogItemFilterMapByInputVal(String inputVal, boolean isCurrentElementChange)
	{
		Map<String, String> filterMap = new HashMap<String, String>();
		filterMap.put(impCode + "." + generalUtil.getEmpty(getFilterCatalogColumn(), chemSearchType), "'" +  chemDao.getSmailsByStructVal(inputVal,"ALL") + "'");
		return filterMap;
	}

//	private boolean isEmpty(String inputVal)
//	{
//		if (generalUtil.getNull(inputVal).contains("ALL") || generalUtil.getNull(inputVal).equals(""))
//		{
//			return true;
//		}
//		return false;
//	}
}
