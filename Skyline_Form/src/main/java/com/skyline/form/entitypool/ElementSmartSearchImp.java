package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Map;

import com.skyline.form.entity.Element;

/**
 * 
 * ElementSmartSearchImp: sets elements values with data from dedicated form (search form)
 * 
 * can be displayed as button or icon
 * 
 *  The schema's elements declare the elements that will be updated with the new data
 *  The schema's Elements' Name declare the elements' id in the search form
 *  
 *  The order is important because we update the elements  by the order of Elements' Name
 *  
 *  Label attribute of parent(Element) used as tooltip to icon implementation
 *
 */
public class ElementSmartSearchImp extends Element {

	private String formCodeTarget, isIcon, elements, tableType, elementsSearchFormName,elementsName = "";
	private String action;
	private String blCustomerFunc;
	private String urlCallParam;
	private String urlCallParamReplaceIDElement;
	private String label;
	
	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try {
			if (super.init(stateKey, formCode, impCode, initVal).equals("")) {
				
				isIcon = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "isIcon");
				elements = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "elements");
				elementsSearchFormName = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "elementsSearchFormName");
				tableType = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "tableType");
				urlCallParam = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "urlCallParam");
				urlCallParamReplaceIDElement = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "urlCallParamReplaceIDElement");
				action = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "action");
				blCustomerFunc = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "blCustomerFunc");
				label = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "label");
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
		Map<String, String> html = new HashMap<String, String>();
		if (renderEmpty) {
			value = "";
		}		
		String onclick= " onclick=\"elementSmartSearchOnClick('" + domId + "')\" ";
		String onclickAfterEnable =" onclickAfterEnable=\"elementSmartSearchOnClick('" + domId + "')\" ";		
		String hidden = (isHidden)? "visibility:hidden;":"";	
		String disabledClass = "";
		if(isDisabled){		
			disabledClass = " disabledclass ";
			onclick = "";
		}
		if(!elementsSearchFormName.equals("")){
			String[] elementsSearchFormNameArray = elementsSearchFormName.split(",");
			String firstCell = elementsSearchFormNameArray[0];
			formCodeTarget = firstCell.substring(0, firstCell.indexOf("."));
			for(int i=0; i<elementsSearchFormNameArray.length; i++){
				elementsName += elementsSearchFormNameArray[i].substring(elementsSearchFormNameArray[i].indexOf(".") + 1) + ",";
			}
			if(elementsSearchFormNameArray.length > 0){
				elementsName = elementsName.substring(0, elementsName.length() - 1 );
			}
		}
		
		
		String search = (isIcon.equals("") ? "<button id=\"" + domId + "\""  + onclick + " type=\"button\" class=\"button " + disabledClass + "\" style=\"" + hidden + "\">" + ((label != null && !label.equalsIgnoreCase("na"))?label:"Search") + "</button>\n"
				: "<i title=\""+label+"\" id=\"" + domId + "\" " + onclick + onclickAfterEnable + "style=\"margin-left: 5px;cursor: pointer;color: #2779aa;margin-top:5px;font-size:larger;" + hidden + "\" class=\"fa fa-search " + disabledClass + "\"></i>\n");

		String styleForRepDesignForm = ""; //kd 06022020 for Report Design form
		if (formCode.equals("ReportDesignExp")) 
		{
			styleForRepDesignForm = "style=\"margin-left: 3px;display: inline-block;\"";
		}
		
		String elementHTML = "<div id=\"" + domId + "_wrapper\"" + styleForRepDesignForm + " >\n"
				+ "<input id=\"" + domId + "_elements\" type=\"hidden\" value=\"" + elements + "\">\n"
				+ "<input id=\"" + domId + "_formCodeTarget\" type=\"hidden\" value=\"" + formCodeTarget + "\">\n"
				+ "<input id=\"" + domId + "_tableType\" type=\"hidden\" value=\"" + tableType + "\">\n"
				+ "<input id=\"" + domId + "_elementsName\" type=\"hidden\" value=\"" + elementsName + "\">\n"
				+ "<input id=\"" + domId + "_blCustomerFunc\" type=\"hidden\" value=\"" + blCustomerFunc + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_urlCallParam\" value=\"" + urlCallParam + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_urlCallParamReplaceIDElement\" value=\"" + urlCallParamReplaceIDElement + "\">\n"
				+ "<input id=\"" + domId + "_action\" type=\"hidden\" value=\"" + action + "\">\n"
				+ search
				+ "</div>\n";


		html.put(layoutBookMark, elementHTML);
		//html.put(layoutBookMark + "_ready", "");

		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		String htmlBody = "";
		if (renderEmpty) {
			value = "";
		}
		htmlBody = "upDateElement({'domId':'" + domId + "','isDisabled':'" + isDisabled
				+ "','type':'smartSearch','isHidden':'" + isHidden + "'});";
		return htmlBody;
	}

	@Override
	public Map<String, String> getCurrentParameMapByInputVal(String inputVal) {
		Map<String, String> mIdVal = new HashMap<String, String>();
		mIdVal.put("CURRENT_" + impCode, inputVal);
		return mIdVal;
	}

	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema= "schema:{ \n" +				
				"tableType:{  \n" +
				"	type:'string',\n" +
				"	title:'Table Type  (pass in URL and become a TABLETYPE parameter)'\n" +
				"},\n" +
				"urlCallParam : {\n" +
				"	type : 'string',\n" +
				"	title : 'URL Call parameters (js object [use upper comma] with key as parameter name for the target form [@ID@ replace with this urlCallParamReplaceIDElement])',\n" +
				"},\n" +
				"urlCallParamReplaceIDElement : {\n" +
				"	type : 'string',\n" +
				"	title : 'urlCallParamReplaceIDElement - URL Call element val replace @ID@ in urlCallParam [replace in run time on search click]',\n" +
				"},\n" +
				"isIcon : {\n" +
				"	type : 'string',\n" +
				"	title : 'Is Icon',\n" +
				"	enum : ['','True']\n" +
				"},\n" + 
				" blCustomerFunc:{  \r\n" + 
				" 	type:'string',\r\n" + 
				" 	title:'Customer BL function (after search)',\r\n" + 		
				" },\r\n" +	 
				" action:{  \r\n" + 
				" 	type:'string',\r\n" + 
				" 	title:'Customer BL function (action parameter)',\r\n" + 		
				" },\r\n" +	 
				"	elementsSearchFormName : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : 'Elements Name',\r\n" +
				"		items : {\r\n" +
				"			enum : [''," + getSmartSearchFormValues() + "],\r\n" +
				"			title : 'Element Name:'\r\n" +
				"		}\r\n" +
				"	},\r\n" +
				"	elements : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : 'Match Elements',\r\n" +
				"		items : {\r\n" +
				"			enum : [''].concat(getResourceValueByType('ELEMENT_IMP_CODE')),\r\n" +
				"			title : 'Element:'\r\n" +
				"		}\r\n" +
				"	}\r\n" +
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}
	
	/**
	 * CSV of SmartSearch-form.id of elements values  and SmartSearch-form.column-name of tables 
	 * from SmartSearch-form. (type = SmartSearch),
	 * the order of choosing the values in the schema
	 * is important because we adjust the form values 
	 * and the SmartSearch-form values by order
	 * example:    return "'SmartSearch.casName'";
	 * SmartSearch is the Form,
	 * casName is the id of the value that return from the SmartSearch-form.	
	 * 
	 * in search form or DT view change getSmartSearchFormValues() and the schema last save value (inside the db) needs to update
	 * @return CSV
	 */
	private String getSmartSearchFormValues() {
		return "'MaterialSearch.Material Name',"
				+"'MaterialSearch.Batch id',"
				+"'MaterialSearch.Material id',"
				+"'MaterialSearch.Batch Number',"
				+"'MaterialSearch.External Number',"
				+"'MaterialSearch.Purity',"
				+"'MaterialSearch.Purity UOM',"
				+"'MaterialSearch.Structure',"
				+"'MaterialSearch.CAS Number',"
				+"'MaterialSearch.CAS Name',"
				+ "'MaterialSearch.Formula',"
				+ "'MaterialSearch.STORAGECONDITION',"
				+ "'MaterialSearch.PREPARATIONDATE',"
				+ "'MaterialSearch.EXPIRYDATE',"
				+ "'MaterialSearch.QUANTITY',"
				+ "'MaterialSearch.Quantity Uom Display',"
				+ "'MaterialSearch.Smiles',"
				+ "'MaterialSearch.Synonyms',"
				+ "'MaterialSearch.Density',"
				+ "'MaterialSearch.Density UOM',"
				+ "'MaterialSearch.MW',"
				+ "'MaterialSearch.MW UOM',"
				+ "'MaterialSearch.IUPAC Name',"
				+ "'MaterialSearch.INVITEMMATERIAL_ID',"
				+ "'MaterialSearch.VISCOSITY',"
				+ "'MaterialSearch.VISCOSITY_UOM_ID',"
				+ "'MaterialSearch.Viscosity UOM Name',"
				+ "'MaterialSearch.Density UOM Name',"
				+ "'MaterialSearch.Item ID',"
				+ "'MaterialSearch.SPECTRUM',"
				+ "'MaterialSearch.MATERIAL_DATA_JSON',"
				+ "'MaterialSearch.BATCH_DATA_JSON',"
				+ "'InstrumentSearch.INVITEMINSTRUMENT_ID',"
				+ "'InstrumentSearch.Serial #',"
				+ "'InstrumentSearch.Instrument Name'," 
				+ "'InstrumentSearch.Model',"
				+ "'InstrumentSearch.Manufacturer',"
				+ "'InstrumentSearch.Last Calibration Date',"
//		        + "'ColumnSelect.Description',"
//		        + "'ColumnSelect.INVITEMCOLUMN_ID',"
		        + "'UserSearch.Full Name',"
		        + "'UserSearch.user_id',"
		        + "'ColumnSearch.Description',"
		        + "'ColumnSearch.INVITEMCOLUMNNAME',"
		        + "'SampleSearch.SAMPLE_ID',"
		        + "'SampleSearch.SAMPLENAME',"
		        + "'SampleSearch.EXPERIMENTNAME',"
		        + "'templateSearch.TEMPLATE_ID',"
		        + "'RequestSearch.REQUEST_ID',"
		        + "'ReportDesignSearch.REPORTDESIGNEXP_ID',"
		        + "'ReportDesignSearch.REPORTDESIGNEXPNAME',"
		        + "'MaterialSlctSearch.INVITEMMATERIAL_ID',"
		        + "'CompositionSearch.experiment_id',"
		        + "'CompositionSearch.RECIPEFORMULATION_ID',"
		        + "'CompositionSearch.COMPOSITION_PARENTID',"
		        + "'ExpTemplateSelect.EXPERIMENT_ID'";
	}
}
