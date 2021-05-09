package com.skyline.form.entity;

import java.util.Map;

import com.skyline.form.bean.ElementInfoAuditTrailDisplay;

public interface ElementInterface extends EntityInterface {

	//html init  - body, js functions, book marks and more...
		/** Init Call flow description
		 *  On page load -
		 * -> a call is made for all the html elements. Each element returns a map for the ModelandView.
		 * -> The map contains html / js code that holds the display / functionality of each element according the following parameters:
		 * @param renderEmpty - if true the element should be display with not data
		 * @param inputVal - element value for display. The value can be the default value (form the builder configuration set by the implementation) or the last saved DOM value returned from this implementation in element_js.<ElementImpCode>.js  ( generalFunc.js -> getformData -> element_js.<ElementImpCode> override DOM value)
		 * @param domId - should be set as the element DOM ID [the content of this parameter is transparent to this implementation]
		 * @param inputAttribute - should be set as the element attribute [the content of this parameter is transparent to this implementation]
		 * @param doOnChangeJSCall - should be set on change action [the content of this parameter is transparent to this implementation]
		 * @param isHidden - hide it if true
		 * @param isDisabled - disable the element html if true
		 * @param isMandatory - add asterix if true
		 * @return
		 */
		public Map<String, String> getInitHtml(long stateKey, String formId, boolean renderEmpty, String inputVal, String userLastSaveVal, String domId, String inputAttribute,
				String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory);

		/**Ajax Call flow description: 
		 * - element make doOnChangeJSCall (see parameter) [the content of this parameter is transparent to this implementation]
		 * -> a call is made for all the html elements in the screen that has inputAttribute attribute [the content of this parameter is transparent to this implementation]
		 * -> each child element (by the the parent id element definition) element return string used in js eval function for having html body display as follow:
		 * @param renderEmpty - if true the element should be display with not data
		 * @param inputVal - element value for display. The value can be the default value (form the builder configuration set by the implementation) or the DOM value returned from this implementation in element_js.<ElementImpCode>.js  ( generalFunc.js -> getformData -> element_js.<ElementImpCode> override DOM value)
		 * @param domId - the html element id
		 * @param inputAttribute - NOT IN USE
		 * @param doOnChangeJSCall - NOT IN USE
		 * @param isHidden - hide it if true
		 * @param isDisabled - disable the element html if true
		 * @param isMandatory - add asterix if true
		 * @return argument for js eval function that should render the element according to the parameters  
		 */
		public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String inputVal, String domId, String inputAttribute, String doOnChangeJSCall,
				boolean isHidden, boolean isDisabled, boolean isMandatory);
 
	//label
	String getLabel();

	void setLabel(String label);

	//bookMark layout
	String getLayoutBookMark();

	void setLayoutBookMark(String layoutBookMark); 
	
	public Map<String, String> getCatalogItemFilterMapByInputVal(String inputVal, boolean isCurrentElementChange);
	
	public  Map<String, String> getCurrentParameMapByInputVal(String inputVal);
 
	public ElementInfoAuditTrailDisplay getAuditTrailValue(String formCode, String key, String postSaveValue, String originValue, String uiDisplayValue);
	

}
