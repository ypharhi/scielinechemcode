package com.skyline.form.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skyline.form.bean.Form;
import com.skyline.form.bean.FormType;

//@Service
//@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class FormTempData {
	
	private Map<String,String> transactionSessionIdMap = null;
	private Map<String,List<String>> transactionFormCodeMap = null;
	private Map<String,String> formIdCodeHolder = null;
	
	public FormTempData() {
		transactionSessionIdMap = new HashMap<String,String>();
		transactionFormCodeMap = new HashMap<String,List<String>>();
		formIdCodeHolder = new HashMap<String, String>();
	}
  
	public void openTransaction(String formId, String formCode) { 
		transactionSessionIdMap.put(formId, String.valueOf((new Date()).getTime()));
		transactionFormCodeMap.put(formId, new ArrayList<String>());
		formIdCodeHolder.put(formId, formCode);
		
	}
	
	public void closeTransaction(String formId) {
		transactionSessionIdMap.remove(formId);
		transactionFormCodeMap.remove(formId);
		formIdCodeHolder.remove(formId);
	}
	
	public boolean isOpenTransaction(String formId) {
		return transactionSessionIdMap.containsKey(formId);
	}
	
	public String getSessionId(String formId) {
		return transactionSessionIdMap.get(formId);
	}
	 
	public List<String> getFormCodeTransactionList(String formId, String formCode) {
		return transactionFormCodeMap.get(formId);
	}
	
	public boolean isFormCodeinTransaction(String formId, String formCode) {
		boolean toReturn = false;
		List<String>formCodeList = transactionFormCodeMap.get(formId);
		if(formCodeList != null && formCodeList.contains(formCode)) {
			toReturn = true;
		}
		return toReturn;
	}
	
	public void addFormCodeToTransaction(String formId, String formCode) {
		List<String>formCodeList = transactionFormCodeMap.get(formId);
		if(!formCodeList.contains(formCode)) {
			formCodeList.add(formCode);
		}
	} 
	
	/**
	 * 
	 * @param form
	 * @param parentId
	 * @return tmp data sessionId or null in case its not a tmp data
	 */
	public String checkAndReturnSessionId (Form form, String parentId)  {
		
//		List<Form> formList = formDao.getFormInfoLookup(formCode, "%", true);
//		Form f_ = formList.get(0);
//		this.formCodeEntity = generalUtil.getNull(form.getFormCodeEntity(),formCode);
		FormType formType = FormType.valueOf(form.getFormType());
		
		if ((form.getFormCode().equals("Composition") && formIdCodeHolder.get(parentId) != null
				&& formIdCodeHolder.get(parentId).equals("StepMinFr")) || form.getFormCode().equals("FormulantRef")
				|| form.getFormCode().equals("Training")) { // yp 2305 - make training popup form constant (not temp
															// from under inventory material/column/instrument from)
			return null;
		}
		 
		String sessionId = null;
		if (parentId != null && isOpenTransaction(parentId) && !formType.equals(FormType.STRUCT) && !formType.equals(FormType.INVITEM)) {
			if (!isFormCodeinTransaction(parentId, form.getFormCode())) {
				addFormCodeToTransaction(parentId, form.getFormCode());
			}
			sessionId = getSessionId(parentId);
		}
		return sessionId;
	}
}
