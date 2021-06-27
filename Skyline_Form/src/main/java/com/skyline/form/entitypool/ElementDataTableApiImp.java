package com.skyline.form.entitypool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.bean.ElementInfoAuditTrailDisplay;
import com.skyline.form.bean.LookupType;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.entity.Element;
import com.skyline.form.service.FormState;

/**
 * 
 * Element Datatable API Imp: Extended JQuery's datatable which
 * filtered by struct, criteria and display (view).
 * Value is saved in CSV format: <[0] Struct in the fg_s_<struct>_<dt>_v view (FORM CODE ENTITY) ,
 * 								  [1] Criteria, // manage in the maintenance
 * 								  [2] Display the dt in the g_s_<struct>_<dt>_v view,
 * 								  [3] 0/1 link to last selection,
 * 							 	  [4] not in use,
 * 								  [5] show div (?TODO DESCRIPTION),
 * 								  [6] No of rows,
 * 								  [7] [optional] last id selected,
 * 								  [8] [Optional] removed columns >
 * 
 * some of the datatable feathers:
 * 	 ** initialize with or without buttons (new, view, edit, clone, split, add, remove and more).
 *	 ** filter another datatableapi element.
 *	 ** change struct, criteria and display dynamically,
 *	 ** bind or unbind 'link to last selection' (on parent change) dynamically.
 *	 ** hide following columns.
 * 	 ** show dynamic caption (as the struct's text).
 * 	 ** files preview with an IFRAME.
 *   ** column can have a render conventions by name <display column name>_SMART<LINK/FILE/TOOLTIP...> see SMARTS definition in docs
 *   ** There's an option to disable selecting some the rows according to a csv list(formId's) taken from the definition 'disabledList'(can get a constant or a parameter)-relevant to 'Multiple' role only.
 *   ** default FilterCatalogColumn is the <struct>_ID value.for role - Search / SharedAjax / non / MultipleAjax, row selection will update the catalog map with key <domid>.<FilterCatalogColumn> and value form the column(0) in the row (in sharedAjax column(1))
 *   ** Multiple & MultipleAjax types has a checkbox in the header for selecting/deselecting all
 *   ** Multiple & MultipleAjax can get DEFAULTSELECT values of formid's in a csv format so that these values would be checked by default,unless there were other saved values.
 *   ** Multiple & MultipleAjax don't use the keepValuesOnParentChange. The selected values are maintained by the lastMuliValues that is sent as an argument to the onElementDataTableApiChange. 
 *   ** MultipleAjax should have the formid of the row in the first column of the DT view(it should be also the first not hidden column)
 *    if the parent changed, the child values are re-evaluted by checking if the last values are contained in the new rows that affected by the parent change.
 *   ** MultipleAjax evaluates the domId_value by getting it from the value argument in the getInitHtml. if the value is empty->it takes it from the userLastValue/defaultSelectedValues
 *   ** Editable tables has an attribute disableEditable assigned to 0 for the first load. on loading the page , this attribute is assigned to 1 if the table should be disabled, for indication in the func elementDataTableApiImpGeneralDisabled.
 *   ** to achieve column titles uniqueness was added new attribute to column header 'uniqueTitle'. The unique part of uniqueTitle  should be defined in DB view or java code as part of column name inside curly brackets {},
 *   	  for example: {quantity}_uom OR quantity_{first}. This unique part is not visible to user, but used to define uniqueTitle by removing curly brackets around it, for example: quantity_uom OR quantity_first.
 * 
 * Remarks:
 * 
 *   ** In \skylineFormWebapp\js\<customer>_bl\ElementDataTableApiImpBL.JS - in the function elementDataTableApiImpBL - we make specific code a cording to the customer customization after the data table renders 
 *   ** The text of the columns and the buttons is taken from the labels.
 *   ** Every button in the datatableapi has unique class (a convenient selector in the js).
 *	 ** Additionally, the buttons 'New' and 'Add' have attribute 'dataTableApiTypeNew' (for readable code and minimum manipulation in the js).
 *	 ** On remove a call to deleteRowElementDataTableApiImp in API service is made (contains check in customer DT code and handling tmp data remove)
 *	 ** Prevent save is not use in this element - display is saved in fg_formlastsave value - if the first column is -1 it will not marked the line
 *	 ** Clicking on new button in a table invokes onNewButtonIntegration that changes the landing page formCode and do some operations according to the client requirements. Returning -1 stops the regular navigating to the new form.
 *	 ** Clicking on Add button in the table is configured by fireCustomEventNewButtonClick. If assign to False (default) then ElementDataTableApiImpOnButtonClick is fired, else - dataTableAddRow is fired which is adding a new row instead of opening a popup. customer can add additional actions in the integrationDT
 *	 ** MultipleAjax row is used for filtering the children tables according to the checked rows. remove/edit/view buttons disabling is currently doesn't refer to the multiple check. In case of additional use-can be developed.
 *	 ** implement select from edit data table (search in the code "implement select from edit data table" for all the places that need implementation)

 */
public class ElementDataTableApiImp extends Element {	
	
//	@Autowired
//	private GeneralUtilFormState generalUtilFormState;	
	
	@Autowired
	private FormDao formDao;
	
	@Autowired
	private GeneralDao generalDao;
	
	private String recordPerPage, role, hideEditButton, hideRemoveButton, onActionButtons, showAddRowButton, isTableEditable;	
	
	private String structCatalogItem/*, criteriaCatalogItem, displayCatalogItem*/;
	
	private String structCatalogItemDefaulValue, criteriaCatalogItemDefaulValue, displayCatalogItemDefaulValue;	
	
	private String structCatalogItemIsHidden, criteriaCatalogItemIsHidden, displayCatalogItemIsHidden;	
	
	private String activateLinkToLastSelection, linkToLastSelectionIsHidden, chooseRequire;
	
	private String actionButtons, cloneButton, multiCloneButton, splitButton, addOptionalButton, addLabelButton, location, hr, wfDiv;	
	
	private String tableType, hideExtras, hideButtons, dynamicCaption, popupSize, followingHiddenCols, disallowRemoveColumns;	
	
	private String uniqueValue, uniqueColumn, onerowonly, hideEmptyColumns, maximumRows;
	
	private String fireClickAlongWithDblClick; //orderByColumnName , orderByColumnAsc  yp 06012018 not in use - default order sould be made in the view
	
	private String urlCallParam;
	
	private String confirmOnRemoveAction, fireCustomEventNewButtonClick;
	
	private String disabledList ,defaultSelectedMultiple;
	
	private String enableScreenOnDataLoad;
	
	private String displayColumnsWhenNoRows;
	
	private boolean isAttachmentPopupPreview, showDragAndDrop, showAddMultipleRowsIcon, isExpandableTable;
	
	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{			   
				structCatalogItemDefaulValue = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "structCatalogItemDefaulValue");
				criteriaCatalogItemDefaulValue = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "criteriaCatalogItemDefaulValue");
				displayCatalogItemDefaulValue = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "displayCatalogItemDefaulValue");
				
				structCatalogItemIsHidden  = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "structCatalogItemIsHidden");
				criteriaCatalogItemIsHidden  = ""; //TODO clean from init json//generalUtilForm.getJsonVal(stateKey, formCode, initVal, "criteriaCatalogItemIsHidden");
				displayCatalogItemIsHidden  = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "displayCatalogItemIsHidden");
				
				structCatalogItem = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "structCatalogItem");
				//criteriaCatalogItem = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "criteriaCatalogItem");				
//				displayCatalogItem = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "displayCatalogItem");				
			
				recordPerPage = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "recordPerPage");		
				
				role = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "role");
				hideEditButton = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "hideEditButton");
				showAddRowButton = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "showAddRowButton");
				isTableEditable = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "isTableEditable");
				hideRemoveButton = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "hideRemoveButton");
				onActionButtons = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "onActionButtons");
				
				activateLinkToLastSelection = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "activateLinkToLastSelection");
				linkToLastSelectionIsHidden  = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "linkToLastSelectionIsHidden");
				
				actionButtons = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "actionButtons");
				tableType = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "tableType");		
				urlCallParam  = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "urlCallParam");
				hideExtras = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "hideExtras"); 
				disallowRemoveColumns = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "disallowRemoveColumns");
			
				dynamicCaption = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "dynamicCaption");			
				hideButtons = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "hideButtons");
				popupSize = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "popupSize");
				followingHiddenCols = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "followingHiddenCols");
				uniqueValue = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "uniqueValue");
				uniqueColumn  = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "uniqueColumn");
				
				cloneButton = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "cloneButton");
				multiCloneButton = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "multiCloneButton");
				splitButton = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "splitButton");
				addOptionalButton = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "addOptionalButton");
				addLabelButton = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "addLabelButton");
				location =  generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "location");
				hr = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "hr");
				wfDiv = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "wfDiv");
				onerowonly = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "onerowonly");
				maximumRows = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "maximumRows");
				hideEmptyColumns = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "hideEmptyColumns");
				chooseRequire = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "chooseRequire");
				
				fireClickAlongWithDblClick = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "fireClickAlongWithDblClick");
				
				enableScreenOnDataLoad = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "enableScreenOnDataLoad");
				//orderByColumnName = "";//generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "orderByColumnName"); // yp 06012018 not in use - default order sould be made in the view
				//orderByColumnAsc = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "orderByColumnAsc");  // yp 06012018 not in use - default order sould be made in the view
				
				confirmOnRemoveAction = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "confirmOnRemoveAction");
				
				fireCustomEventNewButtonClick = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "fireCustomEventNewButtonClick");
				
				disabledList = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "disabledList");
				
				defaultSelectedMultiple = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "defaultSelectedMultiple");
				
				displayColumnsWhenNoRows = "false";
				
				isAttachmentPopupPreview = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "isAttachmentPopupPreview"),false);
				
				showDragAndDrop = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "showDragAndDrop"),false);
				
				showAddMultipleRowsIcon = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "showAddMultipleRowsIcon"),false);
				
				isExpandableTable =  generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "isExpandableTable"),false);
				
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
		HashMap<String, String> html = new HashMap<String, String>();	 
//		userLastSaveVal= userLastSaveVal.replace("Slovent","Slovedddddddddddnt"); // cancel hide and see the mol for: {"value":"NA,ALL,FG_R_EXPANALYSIS_PIVOT_DT_V,1,ExpAnalysisReport,0,10,,Isolated Yield %@Moles@SloventStep 01-Solvent Nectarine PCMoles@ReactantStep 01-Reactant Material UAT CMoles","settings":[{"sort":[],"columnSearch":{},"columnWidth":{}}]}
		 
		String attachTarget = (role.equals("Attachment"))?"target=\"" + domId + "_Iframe\"": "";
		String attachment  = 
			  "<form id=\"" + domId + "_AttachmentForm\" method=\"post\" action=\"getAttachment.request\"  style=\"display:none;\"  "+attachTarget+">\n"
			+ "<input name=\"" + domId + "_FILE_ID\" type=\"hidden\">\n"
			+ "<input name=\"" + domId + "_ContentDisposition\" type=\"hidden\">\n"
			+ "</form>\n";
		
		String iframe  = "";
		if(role.equals("Attachment"))
		{
			if(isAttachmentPopupPreview){
				iframe  ="<div data-isAttachmentPopupPreview=true id=\"" + domId + "_IframePopup\" style=\";width: 100%;height: 100%;\">"
						+"<iframe   name=\"" + domId + "_Iframe\" style=\"display:none;width: 100%;height: 100%;margin: 0;\" src=\"about:blank\"></iframe>"
						+"</div>\n";
			}else if(!isAttachmentPopupPreview){
				iframe  = "<iframe name=\"" + domId + "_Iframe\" style=\"width: calc(100% - 900px);height: calc(100vh - 400px);display:none;margin-left: 10px;\" src=\"about:blank\"></iframe>\n";	
			}
		}else
			iframe = "";
		
		String dragAndDropDiv = (showDragAndDrop)?"<div style=\"width:185px;display: inline-table;margin-top: -5px;\" id=\"" + domId + "_destinationDocUpload\"></div>":"";		
		String hrHtml = (!hr.equals("True")) ? "" : "<hr style=\"margin:40px 40px;\" class=\"style14\">\n";		
		String wfDivHtml = (!wfDiv.equals("True")) ? "" : " <div id=\"" + domId + "_wfDiv\" class=\"row small-collapse expanded upper-filters-parent\" ></div>\n";		
		String flagClass = "";
		String onButtonClickFunction = "";
		if(onActionButtons.equals("Full Screen"))
		{
			onButtonClickFunction = "confirmWithOutSave(ElementDataTableApiImpOnButtonClick,[this]);";
			flagClass = "fullScreenOpenBtn";
		}
		else
		{
			onButtonClickFunction = "ElementDataTableApiImpOnButtonClick(this)";
		}
		
		String onNewClick = fireCustomEventNewButtonClick.equals("True")?"dataTableAddRow('"+domId+"')":onButtonClickFunction;
		String edit =  (hideEditButton.equals("True")) ? "" : "<button type=\"button\" class=\"button dataTableApiButton dataTableApiEdit "+flagClass+"\" onclick=\"" + onButtonClickFunction + "\">" + generalUtil.getSpringMessagesByKey("Edit", "") + "</button>\n";		
		String remove = (hideRemoveButton.equals("True")) ? "" : "<button type=\"button\" class=\"button dataTableApiButton dataTableApiRemove\">" + generalUtil.getSpringMessagesByKey("Remove", "") + "</button>\n";
		String clone = (!cloneButton.equals("True")) ? "" : "<button type=\"button\" class=\"button dataTableApiButton dataTableApiClone\" onclick=\"" + onButtonClickFunction + "\">" + generalUtil.getSpringMessagesByKey("Clone", "") + "</button>\n";//style=\"margin-right:15px;\"
		String multiClone = (!multiCloneButton.equals("True")) ? "" : "<button type=\"button\" class=\"button dataTableApiButton dataTableApiMultiClone\" onclick=\"" + onButtonClickFunction + "\">" + generalUtil.getSpringMessagesByKey("Clone", "") + "</button>\n";//style=\"margin-right:15px;\"
		String split = (!splitButton.equals("True")) ? "" : "<button type=\"button\" class=\"button dataTableApiButton dataTableApiSplit\" onclick=\"" + onButtonClickFunction + "\">" + generalUtil.getSpringMessagesByKey("Split", "") + "</button>\n";//style=\"margin-right:15px;\"
		String listOfNumAddRow = "<div class=\"rowAdditionCount dropdown-content\" style=\"width: 110px;\"><div class=\"\">\n"
				+ "<a tabindex=\"0\" aria-controls=\"firstTable\" class=\"dataTableApiAddMultiRows\" href=\"#\" onclick=\"openInsetRowsDialog('"+domId+"')\"><span>Add Multiple Rows</span></a>\n"
				+ "</div></div>";
		String addRow = "";
		if(generalUtil.getNull(showAddRowButton).equals("True")){
			if(showAddMultipleRowsIcon){
				addRow ="<div class=\"dropdown multiRowsDiv\" style=\"margin-top:0\">\n"
							+" <button type=\"button\" class=\"button dataTableApiButton dataTableApiNew dataTableAddRowButton\" dataTableApiTypeNew onclick=\"dataTableAddRow('"+domId+"')\">" + generalUtil.getSpringMessagesByKey("Add Row", "") + "</button>\n"
							+ listOfNumAddRow
						+ "</div>";
			} else {
				addRow =  "<button type=\"button\" class=\"button dataTableApiButton dataTableApiNew dataTableAddRowButton\" dataTableApiTypeNew onclick=\"dataTableAddRow('"+domId+"')\">" + generalUtil.getSpringMessagesByKey("Add Row", "") + "</button>\n";
			}
		}
							
		String addOptional;
		String labelSectionFormSection = "";
		try {
			addOptional = (addOptionalButton.equals("True"))?"<button type=\"button\" class=\"button dataTableApiButton dataTableApiOptional1 \" dataTableApiTypeNew onclick=\"\" >ADD_OPTIONAL_1</button>\n":"";
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			addOptional = "";
		}
		
		String addLabel;
		try {
			addLabel = (addLabelButton.equals("True"))?"<button type=\"button\" class=\"button dataTableApiButton dataTableApiLabel \" onclick=\"outPutLabelDTWrapper(this)\" >Label</button>\n":"";
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			addLabel = "";
		}
		
		
		if (addLabelButton.equals("True")) {
			labelSectionFormSection  = 
				"<form id=\"" + domId + "_FormLabelSection\" method=\"post\" action=\"outPutLabel.request\"  style=\"display:none;\"  "+attachTarget+">\n"
				+ "<input name=\"_labelCode\" type=\"hidden\">\n"
				+ "<input name=\"_labelData\" type=\"hidden\">\n"
				+ "</form>\n";
		}
		// Every button in the datatableapi has unique class (a convenient selector in the js).
		// Additionally, the buttons 'New' and 'Add' have attribute 'dataTableApiTypeNew' (for readable code and minimum manipulation in the js)
		String buttons = (actionButtons.equals("Horizontal")) ?  
			 "<div id=\""+ domId +"_dataTableStructButtons\" buttonslayout=\"" + actionButtons + "\" tableid=\"" + domId + "\" class=\"table-buttons float-left dataTableStructButtons\"  >\n"
				+ addRow				
				+ "<button type=\"button\" class=\"button dataTableApiButton dataTableApiNew "+flagClass+"\" dataTableApiTypeNew onclick=\"" + onButtonClickFunction + "\">" + generalUtil.getSpringMessagesByKey("New", "") + "</button>\n"
				+ "<button type=\"button\" class=\"button dataTableApiButton dataTableApiView "+flagClass+"\" onclick=\"" + onButtonClickFunction + "\">" + generalUtil.getSpringMessagesByKey("View", "") + "</button>\n"
			    + split
			    + clone
			    + multiClone
				+ addOptional
				+ remove
				+ addLabel
				+ dragAndDropDiv
				+ "</div>\n" 
				
				:					
			
				"<div id=\""+ domId +"_dataTableStructButtons\" buttonslayout=\"" + actionButtons + "\" tableid=\"" + domId + "\" class=\"dataTableStructButtons\">\n"
				+ dragAndDropDiv
				+ "<button type=\"button\" class=\"button dataTableApiButton dataTableApiAdd "+flagClass+"\" dataTableApiTypeNew onclick=\"" + onNewClick + "\">" + generalUtil.getSpringMessagesByKey("Add", "") + "</button>\n"				
				+ addOptional				
				+ addRow
				//+ addOptional
				+ edit
				+ remove				
				+ addLabel
				+ "</div>\n";
		if (!hideButtons.equals("")) {
            if(addLabelButton.equals("True")) {
                buttons = "<div id=\""+ domId +"_dataTableStructButtons\" buttonslayout=\"" + actionButtons + "\" tableid=\"" + domId + "\" class=\"table-buttons  dataTableStructButtons\"  >\n"//style=\"text-align:center;margin-top: 5px;margin-right: 10px;\"
                    + addLabel
                    + "</div>\n";
            } else
            {
            	buttons = "";
            }
        }
		
		if(role.equals("Shared") || role.equals("SharedAjax")){
			//String onNewClick = fireCustomEventNewButtonClick.equals("True")?"dataTableAddRow('"+domId+"')":onButtonClickFunction;
			String additionalBtn = getAdditionalButtons(domId); 
			buttons = "<div id=\""+ domId +"_dataTableStructButtons\" buttonslayout=\"" + actionButtons + "\" tableid=\"" + domId + "\" class=\"dataTableStructButtons\">\n"
					+ addOptional
					+ addLabel
					+ additionalBtn					
					+ "</div>\n";			
		}  
		
		String multipleValue ="";
		
		if(role.equals("Multiple")) {
			if(value.isEmpty()){
				multipleValue = defaultSelectedMultiple;
			} else {
				multipleValue = value;
				value = "";
			}
			/*if(userLastSaveVal!=null){
				userLastSaveVal = generalDao.getSingleStringFromClobNoException("select file_content from fg_clob_files where file_id = '"+userLastSaveVal+"'");
			}*/
		} else if (role.equals("MultipleAjax")){
			List<String> inputValList = new ArrayList<>();
			String[] inputValArray = value.split(",");
			if(inputValArray.length >=8){
				String[] inputValFormId = inputValArray[7].split("@");
				for(int i = 0; i<inputValFormId.length; i++){
					inputValList.add(inputValFormId[i]);
				}
			}
			//adib 19032019 fixed bug of search. if there were last user saved values->they will be checked by default even if the value is empty(because of empty parent )
			if(inputValList.isEmpty() && userLastSaveVal != null){
				String lastValMulti = generalUtil.getJsonValById(userLastSaveVal.toString(),"value");
				inputValArray = lastValMulti.split(",");
				if(inputValArray.length >=8){
					String[] inputValFormId = inputValArray[7].split("@");
					for(int i = 0; i<inputValFormId.length; i++){
						inputValList.add(inputValFormId[i]);
					}
				}
			}
			
			/*List<String> inputValList = new ArrayList<>();
			String[] inputValArray = generalDao.getSingleStringFromClobNoException("select file_content from fg_clob_files where file_id = '"+value+"'").split(",");
			if(inputValArray.length >=8){
				String[] inputValFormId = inputValArray[7].split("@");
				for(int i = 0; i<inputValFormId.length; i++){
					inputValList.add(inputValFormId[i]);
				}
			}
			//adib 19032019 fixed bug of search. if there were last user saved values->they will be checked by default even if the value is empty(because of empty parent )
			if(inputValList.isEmpty() && userLastSaveVal != null){
				userLastSaveVal = generalDao.getSingleStringFromClobNoException("select file_content from fg_clob_files where file_id = '"+userLastSaveVal+"'");
				String lastValMulti = generalUtil.getJsonValById(userLastSaveVal,"value");
				inputValArray = lastValMulti.split(",");
				if(inputValArray.length >=8){
					String[] inputValFormId = inputValArray[7].split("@");
					for(int i = 0; i<inputValFormId.length; i++){
						inputValList.add(inputValFormId[i]);
					}
				}
			}*/
			if(inputValList.isEmpty()){
				multipleValue = defaultSelectedMultiple;
			} else {
				//adib 271118----------------
				multipleValue = generalUtil.listToCsv(inputValList);
				value = "";
			}
		}
		
		//last user save
		boolean isSaveDisplay = true;
		String userVal_ = generalUtil.getNull(userLastSaveVal);
		String userSettings_ = "";
		JSONObject userLastSaveValObject = null;
		if(generalUtil.getNull(userLastSaveVal).trim().startsWith("{")) {
			 try {
				userLastSaveValObject= new JSONObject(userLastSaveVal);
				
				userVal_ = userLastSaveValObject.getString("value");
				userSettings_ = userLastSaveValObject.getJSONArray("settings").toString();
			} catch (Exception e) {
				
			}
		} else {
			userVal_ = value; // support old data (also in formdao we get this values in main screens in order to support old data)
			isSaveDisplay = false; // fix bug 8980 - if not save display do not use the hidden column that comes from value
		}
		
		String userValueArray[] = generalUtil.getNull(userVal_).split(",");
		String u_lastStruct = "";
		String u_lastCriteria = "";
		String u_lastDisplay = "";
		String u_lastLinktoLastSelected = activateLinkToLastSelection.equals("True") ? "1" :"";
		String u_showDiv = "0";
		String u_lastFormId = "";
		String u_cols = "";
		String u_lastPageLength = "";


		if(userValueArray.length >= 7) {
			u_lastStruct = userValueArray[0];
			u_lastCriteria = userValueArray[1];
			u_lastDisplay = userValueArray[2];
			if((linkToLastSelectionIsHidden).equals("")) {
				u_lastLinktoLastSelected = userValueArray[3];		
			}
			u_showDiv = userValueArray[5];
			u_lastPageLength = userValueArray[6];
		}
		if(isSaveDisplay && userValueArray.length >= 8) {	
			List<String> inputValList = new ArrayList<>();
			String[] userValFormId = userValueArray[7].split("@");
			for(int i = 0; i<userValFormId.length; i++){
				inputValList.add(userValFormId[i]);
			}
			//adib 271118----------------
			u_lastFormId = generalUtil.listToCsv(inputValList);
			if(u_lastFormId != null && u_lastFormId.equals("-1")) {
				u_lastFormId = "";
			}
		}
		if(isSaveDisplay && userValueArray.length >= 9) {		
			u_cols = userValueArray[8]; // for removeColumnDatatable...
		}
		 String expandClass = "";
		 String expandIcon = "";
		if(isExpandableTable) {
			expandClass = "section-parent";
			expandIcon = "<span class=\"span-collapse-expand \"><i  onclick=\"toggleSectionCollapse(this)\" style=\"cursor: pointer;\" title=\"Collapse\" class=\"fa fa-angle-up collapse-icon\"></i></span>" +" ";
		}
		//default value usually passed in the URL parameters contains different definitions in each context
		// in this case we keep the criteria filter (of not all) and the formid
		String dval_ = getDefaultValue(stateKey, formId, formCode);
		String defaultValueArray[] = generalUtil.getNull(dval_).split(",");
		String d_lastCriteria = "";
		String d_lastFormId = "";
		if(defaultValueArray.length >= 2) {
			d_lastCriteria = defaultValueArray[1];
			if(formCode.equals("MaterialSlctSearch") || generalUtil.getNull(d_lastCriteria).toLowerCase().trim().equals("all")) { //yp 09112020 add OR formCode.equals("MaterialSlctSearch") PATCH - we pass the list of ID's as default value and it is not "real" criteria
				d_lastCriteria = "";
			} 
		}
		if(defaultValueArray.length >= 8) {		
			d_lastFormId = defaultValueArray[7];
		}
		
		//table configuration from val or user
		String lastStruct = generalUtil.getEmpty(u_lastStruct ,"");
		String lastCriteria = generalUtil.getEmpty(u_lastCriteria,d_lastCriteria);
//		if(formCode.equals("MaterialSearch")) {
//			lastCriteria = (userLastSaveVal == null || userLastSaveVal.isEmpty())?"all":u_lastCriteria;
//		}
		String lastDisplay = generalUtil.getEmpty(u_lastDisplay,"");
		String lastLinktoLastSelected = generalUtil.getEmpty(u_lastLinktoLastSelected,"");
		String showDiv = generalUtil.getEmpty(u_showDiv,"1");
 		String lastFormId = ((role.equals("Search")) ? generalUtil.getEmpty(d_lastFormId, "") : generalUtil.getEmpty(u_lastFormId,d_lastFormId));
		String lastPageLength = generalUtil.getEmpty(u_lastPageLength,"");
		lastPageLength = (lastPageLength.equals("undefined"))?"":lastPageLength; // when dataTable pageLength ddl is hidden
		
		//lastPageLength
//		String structCatalogItemDefaulValueReflaction = generalUtilFormState.getReflactionString(formCode, structCatalogItemDefaulValue, structCatalogItemDefaulValue);
		
		lastStruct = lastStruct.equals("") ? structCatalogItemDefaulValue : lastStruct;
		lastCriteria = lastCriteria.equals("") ? criteriaCatalogItemDefaulValue: lastCriteria;
		lastDisplay = lastDisplay.equals("") ? displayCatalogItemDefaulValue : lastDisplay;
		String cols = generalUtil.getEmpty(u_cols, generalUtil.getNull(userLastSaveVal).equals("") ? getDTDefaultHiddenColumns(stateKey, lastStruct) : ""); // the hidden column array (spared by @)

		
		String structCatalogItemData = (structCatalogItem.equals("") || ((!structCatalogItemDefaulValue.equals("")) && (!(structCatalogItemIsHidden).equals(""))))? structCatalogItemDefaulValue : structCatalogItem;
		//String criteriaCatalogItemData = (criteriaCatalogItem.equals("") || ((!criteriaCatalogItemDefaulValue.equals("")) && (!(criteriaCatalogItemIsHidden).equals(""))))? criteriaCatalogItemDefaulValue : generalUtilFormState.getFormCatalogItem(stateKey, formCode, criteriaCatalogItem.split("\\.")[0], criteriaCatalogItem.split("\\.")[1], impCode);
		//String criteriaCatalogItemData =  generalUtilFormState.getFormCatalogItem(stateKey, formCode, criteriaCatalogItem.split("\\.")[0], criteriaCatalogItem.split("\\.")[1], impCode);
//		String displayCatalogItemData = displayCatalogItemDefaulValue;
		
		String structSelectValue =  getSelectOptions(structCatalogItemData,lastStruct);
		//String criteriaSelectValue = getSelectOptions(/*criteriaCatalogItemData*/"ALL",lastCriteria);
		StringBuilder selectedCriteria = new StringBuilder();
		String criteriaSelectValue = getCriteriaOptions(stateKey, lastCriteria,generalUtil.getEmpty(lastStruct, generalUtil.getFirstCsv(structCatalogItemData)), displayCatalogItemDefaulValue , selectedCriteria);

		lastCriteria = selectedCriteria.toString();
		
		//adib 120219 fix bug 7228
//		if(!Arrays.asList(displayCatalogItemDefaulValue).contains(lastDisplay)){//if lastDisplay is not in the option list anymore then replace it with the new one
//			lastDisplay = displayCatalogItemDefaulValue;
//		} --> getSelectDisplayOptions will returned the selected value that will be set as lastDisplay ->
		JSONObject displayObj = getSelectDisplayOptions(stateKey, lastStruct, displayCatalogItemDefaulValue, lastDisplay);
		String displaySelectValue = (String)displayObj.get("selectHtml");
		lastDisplay =  (String)displayObj.get("selectVal");
		
		String structItemHidden = (structCatalogItemIsHidden).equals("") ? "":"display:none;";
		String addMargin = (structCatalogItemIsHidden).equals("") ? "":"";
		String structSelect = 
			"<label style=\""+ structItemHidden + "\" class=\"text-right datatableapiselectloadinglabel \">Level:\n"
				+ "<select id=\"" + domId + "_structCatalogItem\" class=\"" + domId +"_select datatableapiselect datatableapiselectloading\" style=\"" + structItemHidden + "\" isStruct=\"true\" >\n"
					+  structSelectValue
				+ "</select>\n"
			+ "</label>\n";
		
		String criteriaItemHidden = (criteriaCatalogItemIsHidden).equals("") ? "":"display:none;";		
		
		String criteriaSelect = 
			"<label style=\"" + addMargin + criteriaItemHidden + "\" class=\"text-right datatableapiselectloadinglabel \">Show:\n" 
				+ "<select id=\"" + domId + "_criteriaCatalogItem\" style=\"" + criteriaItemHidden + "\" class=\"" + domId +"_select datatableapiselect datatableapiselectloading\">\n"
					+ criteriaSelectValue
				+ "</select>\n"
			+ "</label>\n";
		 
		String displayCatalogItemHidden = (displayCatalogItemIsHidden).equals("") ? "":"display:none;";
		
		String displaySelect = 
			"<label style=\"" + addMargin + displayCatalogItemHidden + "\" class=\"text-right datatableapiselectloadinglabel \">Level:\n"
				+ "<select id=\"" + domId + "_displayCatalogItem\" style=\"" + displayCatalogItemHidden + "\" class=\"" + domId +"_select datatableapiselect datatableapiselectloading\">\n"
				+ displaySelectValue
				+ "</select>\n"
			+ "</label>\n";
		
		String isLinkToLastSelectionHidden = (linkToLastSelectionIsHidden).equals("") ? "":"display:none;";
		String isdynamicCaption = (dynamicCaption).equals("") ? "display:none;":"";
		String isLabel = getLabel().equals("") || getLabel().startsWith("{") ? "display:none;":"";
		String chooseRequireAttr  = (chooseRequire.equals("True")) ? " chooserequire " : "";
		
		//ab12032019 TODO: a value of this option should be received from FormBuilder 
		if(formCode.equals("Step") &&
				(domId.equals("reactants") || domId.equals("solvents") || domId.equals("products"))
			)
		{
			displayColumnsWhenNoRows = "true";
		}
		
		String loadingDiv = " <div id=\"hiddenformMessage_"+domId+"\"  style=\"display: block;text-align: center;\">"
							+   "<h2>Loading...</h2>"
							+" <img style=\"width:32px;height:32px;\" src=\"../skylineFormWebapp/images/circular.gif\">"
							+"</div>";
		
		try {
			html.put(layoutBookMark,
			"<div id=\"" + domId + "_Parent\" " + chooseRequireAttr + " isfirstload=\"1\" parentElement=\"" +  getParentElement()  + "\" class=\"dataTableParent dataTableParentWidth "+expandClass+"\">"
				+ loadingDiv	
				+ " <div id=\""+domId+"_tableFilterControls\" class=\"tableFilterControls\">"
				+ " <h2 style=\"" + isLabel + "\">"+expandIcon+ getLabel() + "</h2>"	
				+ " <h2 id=\"" + domId + "_Caption\" style=\"" + isdynamicCaption + "\" ></h2>"					
				+ " <div id=\"" + domId + "_selectDiv\" class=\"row small-collapse expanded cssSelectDiv\"  formCode=\"" + formCode + "\" stateKey=\"" + stateKey + "\" thisFormId=\"" + formId + "\">"
					+ "<div class=\"data-select-controls\">"
							+ structSelect 
							+ displaySelect
							+ criteriaSelect 
							+ "<label class=\"cssLinkToLastSelection datatableapiselectloadinglabel\" style=\"" + isLinkToLastSelectionHidden + "\">\n"
								+ "<input type=\"checkbox\" id=\"" + domId + "_LinkToLastSelection\" class=\"datatableapiselectloadingcheckbox\" style=\"" + isLinkToLastSelectionHidden + "\" >\n"
							+ "Link To Last Selection</label>"
					+ "</div>"
				+ " </div>"
				+ wfDivHtml
				+" <div id=\"" + domId + "_displayTopRows\" class=\"tableDisplayTopRows\"></div>"
				+ "</div>"
				+ buttons
				+ " <input type=\"hidden\" name=\"" + domId + "_metaData\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_formId\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_dataTableOptions\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_showDiv\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_tableType\" value=\"" + tableType + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_urlCallParam\" value=\"" + urlCallParam + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_sourceElementImpCode\" value=\"" + impCode + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_onActionButtons\" value=\"" + onActionButtons + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_isPreview\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_popupSize\" value=\"" + popupSize + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_role\" value=\"" + role + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_value\" value=\"" + multipleValue + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_colsArray\" value=\"" + cols + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_lastPageLength\" value=\"" + lastPageLength + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_lastStruct\" value=\"" + lastStruct + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_oneRowOnly\" value=\"" + onerowonly + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_maximumRows\" value=\"" + maximumRows + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_confirmOnRemoveAction\" value=\"" + confirmOnRemoveAction + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_userLastSaveSettings\" value='" +  userSettings_ + "'>\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_disabledList\" value=\"" + disabledList + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_sharedFormId\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_structSelection\" value=\"" + (((structCatalogItem).contains(","))?"1":"0") + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_editableLastFocusField\" value=\"\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_showDragAndDrop\" value=\"" + showDragAndDrop + "\">\n"
				+ " <input type=\"hidden\" id=\"" + domId + "_isPageInitFlag\" value=\"true\">\n"
				
			    + "<table id=\"" + domId + "\" class=\"display unstriped "+(generalUtil.getNull(isTableEditable).equals("True")?"editable":"")+"\""
			    + (generalUtil.getNull(isTableEditable).equals("True")?" disableEditable = \"0\"":"")
			    //+ (role.equals("MultipleAjax")?" saveType = \"clobForSelect\"":"")
			    // 99 in Attachment to fix the x scroll ->
			    +" location=\"" + location + "\" width=" + (role.equals("Attachment")?"99%":"100%") + " " + inputAttribute +" element=\"" + this.getClass().getSimpleName() + "\" >"			
				+ "</table>\n"			
				+ attachment
				+ labelSectionFormSection
				+ iframe
				+ hrHtml
				+ "</div>\n"			
				+ "<script>\n"	
				+ " $(document).ready(function() { \n"
				//+ "$('div.dragAndDrop').load(initDragAndDropHadle($(this)));\r\n"
				+ "$('[id=\"" + domId + "_structCatalogItem\"][isStruct=\"true\"]').on(\"change\", function(){\r\n" +
				"    onLevelSelectedChange('" + domId + "',undefined,true);\r\n" + 
				"});\n"
				//+ "$('[id=\"" + domId + "_selectDiv\"]').on(\"change\", function(){\r\n" +
				+ "$('[id=\"" + domId + "_selectDiv\"][isStruct!=\"true\"]').on(\"change\", function(){\r\n" +
				"    onElementDataTableApiChange('" + domId + "',undefined,true);\r\n" + 
				"});\n"
				+ " initElementDataTableApi('" + domId + "','" + dataTableOptions( isHidden,  isDisabled,  isMandatory,renderEmpty,doOnChangeJSCall,true) 
				+ "','" + lastStruct + "','" + lastCriteria + "','" + lastDisplay  + "','" + lastLinktoLastSelected  + "','" + showDiv + "','" + lastFormId  + "','" + lastPageLength + "');\n"
				+ " });\n"
				+ "<" + "/" + "script>"
				);			
		}
		catch(Exception e){
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return html;
	}
	
	private String getSelectOptions(String options,String lastSelected){
		StringBuilder sb = new StringBuilder();
		String[] optionsArray  = options.split(",");
		for(String option : optionsArray){	
				if(lastSelected.equals(option)) {
					sb.append("<option selected value=\""+option+"\">"+option+"</option>\n");
				} else {
					sb.append("<option value=\""+option+"\">"+option+"</option>\n");
				}
		}
		return sb.toString();
	}
	
	private JSONObject getSelectDisplayOptions(long stateKey, String lastStruct, String displayCatalogItemDefaulValue, String lastDisplay) {
		
		FormState formState_ = generalUtilFormState.getFormState(stateKey, formCode);
		JSONObject obj = formState_.customerDTDisplayViewList(formCode, impCode, lastStruct, displayCatalogItemDefaulValue, lastDisplay, false);

		return obj;
	}
	
	private String getDTDefaultHiddenColumns(long stateKey, String lastStruct) {
		FormState formState_ = generalUtilFormState.getFormState(stateKey, formCode);
		return formState_.customerDTDefaultHiddenColumns(formCode, impCode, lastStruct);
	}
	
	private String getCriteriaOptions(long stateKey, String options, String struct, String displayCatalog, StringBuilder selectedCriteria) {
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
			if(!sqlPoolMap.get("IGNORE").equals("1")){
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
	 
	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp	
		String htmlBody="";		
		try{			
			htmlBody = "onElementDataTableApiChange('" + domId + "','" + dataTableOptions( isHidden,  isDisabled,  isMandatory,renderEmpty,doOnChangeJSCall,false) + "');";
		}
		catch(Exception e){
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return htmlBody;
	
	}
	private String dataTableOptions(boolean isHidden, boolean isDisabled, boolean isMandatory, boolean renderEmpty,String doOnChangeJSCall,boolean fromInitTable)
	{
		JSONObject jo = new JSONObject();		
		if(!recordPerPage.equals(""))
		{
			Set<Integer> sortedSet = new HashSet<Integer>(Arrays.asList(10,25,50,100,Integer.parseInt(recordPerPage)));
			String sorted = new ArrayList<Integer>(new TreeSet<Integer>(sortedSet)).toString();			
			jo.put("pageLength", recordPerPage);
			jo.put("lengthMenu", "[" + sorted +", "+ sorted + "]");
		}		
		jo.put("isHidden", (isHidden) ? "true": "false");
		jo.put("clearDataWhenEmpty", (renderEmpty) ? "true": "false");	
		if(!role.equals("")) {
			jo.put("role", role);
		}
		if(actionButtons.equals("Vertical")) {
			jo.put("actionButtons", "Vertical");
		}		
		if(hideExtras.equals("True")) {
			jo.put("hideExtras", "True");
		}
		if(!followingHiddenCols.equals("")) {
			jo.put("followingHiddenCols",followingHiddenCols);
		}
		if(!uniqueValue.equals("")) {
			jo.put("uniqueValue",uniqueValue);
		}
		if(!uniqueColumn.equals("")) {
			jo.put("uniqueColumn",uniqueColumn);
		}
		if(onerowonly.equals("True")) {
			jo.put("oneRowOnly", "True");
		}
		if(!maximumRows.equals("")) {
			jo.put("maximumRows", maximumRows);
		}
		if(!confirmOnRemoveAction.equals("")) {
			jo.put("confirmOnRemoveAction", confirmOnRemoveAction);
		}
		if(hideEmptyColumns.equals("True")) {
			jo.put("hideEmptyColumns", "True");
		}
		if(isDisabled) {
			jo.put("isDisabled",isDisabled);
		}
		if(isMandatory) {
			jo.put("isMandatory",isMandatory);
		}
//		if(!orderByColumnName.equals(""))
//		{
//			jo.put("orderByColumnName",orderByColumnName);
//			jo.put("orderByColumnAsc",orderByColumnAsc);
//		}
		jo.put("enableScreenOnDataLoad", enableScreenOnDataLoad);
		jo.put("fireClickAlongWithDblClick",fireClickAlongWithDblClick);
		jo.put("doOnChangeJSCall", doOnChangeJSCall.replace("'", ""));
		jo.put("displayColumnsWhenNoRows", displayColumnsWhenNoRows);
		jo.put("disallowRemoveColumns", disallowRemoveColumns);
		jo.put("isTableEditable", isTableEditable);
		if(!fromInitTable){
			jo.put("fromInitTable", "false");
			jo.put("clearSelected","true");
		} else {
			jo.put("fromInitTable", "true");
		}
		return jo.toString();
	}

	@Override
	public String getDefaultValue(long stateKey, String formId, String formCode) {
		String dv_ = super.getDefaultValue(stateKey, formId, formCode);  
		if(dv_.startsWith("'") && dv_.endsWith("'")) {
			dv_ = dv_.substring(1, dv_.length() - 1);
		}
		return dv_; 
	}

	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema = "schema:{\n" + 			
				"role : {\n" +
				"	type : 'string',\n" +
				//"	title : 'Role [Empty - default, Attachment - display attachment preview, Multiple - select rows (saved as SCV), Shared - edit all shared rows of Multiple ]',\n" +	
				"	title : 'Role [Attachment: file preview, Multiple: multi selection, Shared: multi edit (SharedAjax support ajax api call)]',\n" +		
				"	enum : ['','Attachment','Shared','SharedAjax','Multiple','Search','MultipleAjax'],\n" +
				"},\n" +
				"isAttachmentPopupPreview:{\r\n" + 
				" 	type: 'boolean',\r\n" + 
				" 	title: 'Is attachment popup preview'\r\n" + 
				"},\r\n" +
				"showDragAndDrop : {\r\n" +				
				" 	type: 'boolean',\r\n" + 
				" 	title: 'Show drag and drop(also need to configure in upload document element)'\r\n" + 
				"},\n" +
				"tableType : {\n" +
				"	type : 'string',\n" +
				"	title : 'Table Type (pass as url param)',\n" +
				"},\n" +	
				"urlCallParam : {\n" +
				"	type : 'string',\n" +
				"	title : 'URL Call parameters (js object [use upper comma] with key as parameter name for the target form [@ID@ replace with rowid])',\n" +
				"},\n" +
				"disabledList: {\n" +
				"	type : 'string',\n" +
				"	title : 'Disabled list parameter (relevant to Multiple role only) ',\n" +
				"},\n" +
				"hideEmptyColumns : {\n" +
				"	type : 'string',\n" +
				"	title : 'Hide Empty Columns',\n" +
				"	enum : ['','True'],\n" +
				"},\n" +
				"hideButtons : {\n" +
				"	type : 'string',\n" +
				"	title : 'Hide Buttons',\n" +
				"	enum : ['','True'],\n" +
				"},\n" +	
				"followingHiddenCols :{\n" + 
				"     type:'string',\n" + 
				"     title:'Following Hidden Columns (in role Multiple the last hidden should be the ID) ',\n" + 			
				" },\n" + 
//				"orderByColumnName :{\n" + 
//				"     type:'string',\n" + 
//				"     title:'Order By Column Name (executed on table load) ',\n" + 			
//				" },\n" + 
//				"orderByColumnAsc : {\n" +
//				"	type : 'string',\n" +
//				"	title : 'Is order by ascending order (considered only if -Order By Column Name- is not empty)',\n" + 
//				"	enum : ['True','False'],\n" +
//				"},\n" +
				"popupSize : {\n" +
				"	type : 'string',\n" +
				"	title : 'Popup size',\n" +
				"	enum : ['','Small','Medium'],\n" +
				"},\n" +				
				"hr : {\n" +
				"	type : 'string',\n" +
				"	title : 'Html hr(horizontal rule/underline)',\n" +		
				"	enum : ['','True'],\n" +
				"},\n" +
				"dynamicCaption : {\n" +
				"	type : 'string',\n" +
				"	title : 'Dynamic Caption',\n" +		
				"	enum : ['','True'],\n" +
				"},\n" +						
				"actionButtons : {\n" +
				"	type : 'string',\n" +
				"	title : 'Action Buttons (Horizontal[for full scree] - view and new, Vertical[for popup] - add, edit/view, remove)',\n" +		
				"	enum : ['Horizontal','Vertical'],\n" +
				"},\n" +			
				"onActionButtons : {\n" +
				"	type : 'string',\n" +
				"	title : 'On Action Buttons',\n" +		
				"	enum : ['PopUp','Full Screen'],\n" +
				"},\n" +
				"chooseRequire : {\n" +
				"	type : 'string',\n" +
				"	title : 'Choose Require (select a row is must)',\n" +		
				"	enum : ['','True'],\n" +
				"},\n" +
				"hideEditButton : {\n" +
				"	type : 'string',\n" +
				"	title : 'Hide Edit Button',\n" +		
				"	enum : ['','True'],\n" +
				"},\n" +
				"hideRemoveButton : {\n" +
				"	type : 'string',\n" +
				"	title : 'Hide Remove Button',\n" +		
				"	enum : ['','True'],\n" +
				"},\n" +
				"showAddRowButton : {\n" +
				"	type : 'string',\n" +
				"	title : 'Show AddRow button',\n" +		
				"	enum : ['','True'],\n" +
				"},\n" +
				"showAddMultipleRowsIcon : {\n" +
				"	type : 'string',\n" +
				"	title : 'Show Add Multiple Rows icon',\n" +		
				"	enum : ['','True'],\n" +
				"},\n" +
				"isTableEditable : {\n" +
				"	type : 'string',\n" +
				"	title : 'Set table as Editable (add to table inline-edit functionality)',\n" +		
				"	enum : ['','True'],\n" +
				"},\n" +
				"hideExtras : {\n" +
				"	type : 'string',\n" +
				"	title : 'Hide Extras',\n" +		
				"	enum : ['','True'],\n" +
				"},\n" +
				"disallowRemoveColumns : {\n" +
				"	type : 'string',\n" +
				"	title : 'Disallow Remove Columns possibility',\n" +		
				"	enum : ['','True'],\n" +
				"},\n" +
				"structCatalogItem : {\n" +
				"	type : 'string',\n" +
				"	title : 'Struct (levels) as CSV'\n" +		
				"},\n" +
				"structCatalogItemDefaulValue:{\n" + 
				"     type:'string',\n" + 
				"     title:'Struct (levels) Default Value',\n" + 			
				" },\n" + 
				"structCatalogItemIsHidden:{\n" + 
				"     type:'string',\n" + 
				"     title:'Struct (levels) Is Hidden',\n" + 
				"	enum : ['','True'],\n" +
				" },\n" + 
				//yk
				/*"criteriaCatalogItem : {\n" +
				"	type : 'string',\n" +
				"	title : 'Criteria From List',\n" +		
				"	enum : [''].concat(getResourceValueByType('CATALOG_ITEM_%')),\n" +
				"},\n" + */
				"criteriaCatalogItemDefaulValue:{\n" + 
				"     type:'string',\n" + 
				"     title:'Criteria (filter) Default Catalog Value (override maintenance configuration)',\n" + 			
				" },\n" +
				"criteriaCatalogItemIsHidden:{\n" + 
				"     type:'string',\n" + 
				"     title:'Criteria (filter) Is Hidden (not in use)',\n" + 
				"	enum : ['','True'],\n" +
				" },\n" + 
//				"displayCatalogItem : {\n" +
//				"	type : 'string',\n" +
//				"	title : 'Catalog display Item List',\n" +		
//				"	enum : [''].concat(getResourceValueByType('CATALOG_ITEM_%')),\n" +
//				"},\n" +
				"displayCatalogItemDefaulValue:{\n" + 
				"     type:'string',\n" + 
				"     title:'Display (View Item) Default Value (if the struct Default Value is NA [THIS] will be the display table else fg_s_[STRUCT]_[THIS]_v)',\n" + 			
				" },\n" +  
				"displayCatalogItemIsHidden:{\n" + 
				"     type:'string',\n" + 
				"     title:'Display (View Item) Is Hidden',\n" + 
				"	enum : ['','True'],\n" +
				" },\n" + 
				" isDistinct:{\n" + 
				"     type:'string',\n" + 
				"     title:'Hide Duplication',\n" + 
				"     'enum':[\n" + 
				"         'False',\n" + 
				"         'True'\n" + 
				"     ]\n" + 
				" },\n" +
				" defaultSelectedMultiple:{\n" + 
				"     type:'string',\n" + 
				"     title:'Default Selected Rows (rellevant to Multiple or MultipleAjax role only. csv of formids)',\n" + 
				" },\n" +
				"uniqueValue :{\n" + 
				"     type:'string',\n" + 
				"     title:'Unique Value',\n" + 			
				" },\n" +
				"uniqueColumn :{\n" + 
				"     type:'string',\n" + 
				"     title:'Unique Column',\n" + 			
				" },\n" +
				"cloneButton:{\n" + 
				"     type:'string',\n" + 
				"     title:'Show Clone Button',\n" + 
				"	enum : ['','True'],\n" +
				" },\n" +				
				"multiCloneButton:{\n" + 
				"     type:'string',\n" + 
				"     title:'Multi Clone Button',\n" + 
				"	enum : ['','True'],\n" +
				" },\n" +
				"splitButton:{\n" + 
				"     type:'string',\n" + 
				"     title:'Show Split Button',\n" + 
				"	enum : ['','True'],\n" +
				" },\n" +	
				"addOptionalButton:{\n" + 
				"     type:'string',\n" + 
				"     title:'add Optional Button (customize the click in ElementDataTableApiImpBL)',\n" + 
				"	enum : ['','True'],\n" +
				" },\n" +	
				"addLabelButton:{\n" + 
				"     type:'string',\n" + 
				"     title:'add Label Button (call outPutLabelDTWrapper in ElementDataTableApiImpBL) ',\n" + 
				"	enum : ['','True'],\n" +
				" },\n" +	
				"wfDiv:{\n" + 
				"     type:'string',\n" + 
				"     title:'WF Div',\n" + 
				"	enum : ['','True'],\n" +
				" },\n" + 
				" recordPerPage:{\n" + 
				"     type:'string',\n" + 
				"     title:'Record Per Page',\n" + 			
				" },\n" + 				
				"activateLinkToLastSelection:{\n" + 
				"     type:'string',\n" + 
				"     title:'Activate Link To Last Selection',\n" + 
				"	enum : ['','True'],\n" +
				" },\n" + 
				"linkToLastSelectionIsHidden:{\n" + 
				"     type:'string',\n" + 
				"     title:'Hide Link To Last Selection',\n" + 
				"	enum : ['','True'],\n" +
				" },\n" + 
				"location :{\n" + 
				"     type:'string',\n" + 
				"     title:'location (for sync toggle)',\n" + 			
				" },\n" +	
				" isExpandableTable : {\n" +
				"	type : 'boolean',\n" +
				"	title : 'Is Expandable Table',\n" +
				"},\n" + 
				"onerowonly : {\n" +
				"	type : 'string',\n" +
				"   title:'One Row Only',\n" + 		
				"	enum : ['','True'],\n" +
				"},\n" +
				"maximumRows : {\n" +
				"	type : 'string',\n" +
				"	title : 'Maximum Rows (depleted if one row only is defined)',\n" +
				"},\n" +
				"enableScreenOnDataLoad : {\n" +
				"	type : 'boolean',\n" +
				"	title : 'Enable user operation on the screen when data loaded',\n" +
				"},\n" +
				"confirmOnRemoveAction : {\n" +
				"	type : 'string',\n" +
				"	title : 'Confirm OnRemove Action (if not empty call confirmDeleteRowElementDataTableApiImp service)',\n" +
				"},\n" +
				"fireCustomEventNewButtonClick : {\n" +
				"	type : 'string',\n" +
				"	title : 'Fire Custom Event On Add Action (if true call dataTableAddRow in integrationDT, else call ElementDataTableApiImpOnButtonClick. )',\n" +
				"   enum : ['False','True'],\n" +
				"},\n" +
				"fireClickAlongWithDblClick:{\r\n" + 
				" type: 'boolean',\r\n" + 
				" title: 'Fire Click Along With Double Click(e.g. for filter and view/edit)'\r\n" + 
				"   },\n" +
				" layoutBookMarkItem:{\n" + 
				"     type:'string',\n" + 
				"     title:'Layout BookMark Item',\n" + 
				"     'enum':getResourceValueByType('LAYOUT_ITEM_TEXT')\n" + 
				" } " + 
				(schema.equals("") ? "" : ",\n" + schema) +
				"\n}";
		
		return schema;
	} 
	
	@Override
	public Map<String, String> getCatalogItemFilterMapByInputVal(String inputVal, boolean isCurrentElementChange) {
		Map<String, String> filterMap = new HashMap<String, String>();
		String[] inputValArray;
		String formId, struct;		
		if(!generalUtil.getNull(inputVal).trim().equals("")) {
			inputValArray = inputVal.split(",");			
//			if(inputValArray.length == 2) {		
//				formId = inputValArray[1];
//				struct = inputValArray[0];
//				filterMap.put(impCode + "." + generalUtil.getEmpty(getFilterCatalogColumn(), struct + "_id"), formId);
//			} else 
			if(inputValArray.length >= 8) {		
				//adib 271118-----------------
				List<String> inputValList = new ArrayList<>();
				String [] inputValFormId = inputValArray[7].split("@");
				for(int i = 0; i<inputValFormId.length; i++){
					inputValList.add(inputValFormId[i]);
				}
				//adib 271118----------------
				formId = generalUtil.listToCsv(inputValList);//inputValArray[7];adib 271118
				struct = inputValArray[0];
				filterMap.put(impCode + "." + generalUtil.getEmpty(getFilterCatalogColumn(), struct + "_id"),  generalUtil.getEmpty(formId,"-1"));
			} 
			//yp 04032020 - with the workaround made in 27022020 - for solving bugs in the main screen
			else if(formCode != null && (formCode.equals("Main") || formCode.equals("InvItemSamplesMain"))) {
				struct = inputValArray[0];
				filterMap.put(impCode + "." + generalUtil.getEmpty(getFilterCatalogColumn(), struct + "_id"),  "-1");
			}
		}		
		return filterMap;
	}
	
	@Override
	public Map<String, String> getCurrentParameMapByInputVal(String inputVal) {
		
		Map<String, String> parMap = new HashMap<String, String>();
		String[] inputValArray;
		String formId;		
		if(!generalUtil.getNull(inputVal).trim().equals("")) {
			inputValArray = inputVal.split(",");			
//			if(inputValArray.length == 2) {		
//				formId = inputValArray[1];
//				struct = inputValArray[0];
//				parMap.put("CURRENT_ROW_" + impCode, formId);
//			} else 
			if(inputValArray.length >= 8) {		
				formId = inputValArray[7];
//				struct = inputValArray[0];
				parMap.put("CURRENT_ROW_" + impCode, formId);
			}			
		} else {
			parMap.put("CURRENT_ROW_" + impCode, "");
		}
		return parMap;
	} 
	
	@Override
	public ElementInfoAuditTrailDisplay getAuditTrailValue(String formCode, String key, String postSaveValue, String originValue, String uiDisplayValue)
	{
		ElementInfoAuditTrailDisplay elementValueJobFlag = null;
		try {
			if(postSaveValue.equals(originValue))
			{
				elementValueJobFlag = new ElementInfoAuditTrailDisplay(uiDisplayValue, "0");
			}
			else
			{
				if(!postSaveValue.isEmpty()) {
					List<String> lval = generalDao.getListOfStringBySql(" select t.formidname from fg_sequence t where t.id in (" + postSaveValue + ")");
					//List<String> lval = generalDao.getListOfStringBySql(" select t.formidname from fg_sequence t where t.id in (" + postSaveValue + ")");
					elementValueJobFlag = new ElementInfoAuditTrailDisplay(generalUtil.listToCsv(lval), "0");
				} else {
					elementValueJobFlag = new ElementInfoAuditTrailDisplay("", "0");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			elementValueJobFlag = new ElementInfoAuditTrailDisplay("", "1");
		}
		return elementValueJobFlag;
	}
	
	
	
//	@Override
//	public boolean isValExists(String lastsSelectedValue) {
//		// TODO Auto-generated method stub
//		boolean toReturn = false;
//		String[] inputValArray;
//		String formId = "";
//		if(!generalUtil.getNull(lastsSelectedValue).trim().equals("")) {
//			inputValArray = lastsSelectedValue.split(",");			
////			if(inputValArray.length == 2) {		
////				formId = inputValArray[1];
////				struct = inputValArray[0];
////				parMap.put("CURRENT_ROW_" + impCode, formId);
////			} else 
//			if(inputValArray.length >= 8) {		
//				formId = inputValArray[7];
//			}		
//			
//			toReturn = !generalUtil.getNull(formId).trim().equals("");
//		}
//		return toReturn;
//	}
	private String getAdditionalButtons(String domId ){
		String additionalBtn = "";
		String remove = "<button type=\"button\" class=\"button dataTableApiButton dataTableApiRemove\">" + generalUtil.getSpringMessagesByKey("Remove", "") + "</button>\n";
		String listOfNumAddRow = "<div class=\"rowAdditionCount dropdown-content\" style=\"width: 110px;\"><div class=\"\">\n"
				+ "<a tabindex=\"0\" aria-controls=\"firstTable\" class=\"dataTableApiAddMultiRows\" href=\"#\" onclick=\"openInsetRowsDialog('"+domId+"')\"><span>Add Multiple Rows</span></a>\n"
				+ "</div></div>";String addRow = "";
		if(generalUtil.getNull(showAddRowButton).equals("True")){
			if(showAddMultipleRowsIcon){
				addRow ="<div class=\"dropdown multiRowsDiv\" style=\"margin-top:0\">\n"
							+" <button type=\"button\" class=\"button dataTableApiButton dataTableApiNew dataTableAddRowButton\" dataTableApiTypeNew onclick=\"dataTableAddRow('"+domId+"')\">" + generalUtil.getSpringMessagesByKey("Add Row", "") + "</button>\n"
							+ listOfNumAddRow
						+ "</div>";
			} else {
				addRow = "<button type=\"button\" class=\"button dataTableApiButton dataTableApiNew dataTableAddRowButton\" dataTableApiTypeNew onclick=\"dataTableAddRow('"+domId+"')\">" + generalUtil.getSpringMessagesByKey("Add Row", "") + "</button>\n";
			}
			
		}
		String editShared = "<button type=\"button\" class=\"button dataTableApiButton dataTableApiEditShared\" dataTableApiTypeNew onclick=\"ElementDataTableApiImpOnButtonClick(this)\">" + generalUtil.getSpringMessagesByKey("Edit", "") + "</button>\n";
		
		if(formCode.equals("SelfTest") && ( domId.equals("instruments") || domId.equals("columns")|| domId.equals("samples") )){
			additionalBtn = addRow + editShared + remove;
		}
		else
		{
			additionalBtn = editShared;
		}
		
		return additionalBtn;
	}
}

