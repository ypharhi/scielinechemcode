package com.skyline.form.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.RedirectInfo;

public interface FormService {

	//first call
	ModelAndView initForm(Boolean isNew, long stateKey, String formCode, String formId, String userId, String nameId, String urlCallParam, String urlPrintParam, Map<String, String[]> requestMap);
	
	//ajax call
	ActionBean getAction(long stateKey, String action, List<DataBean> dataBeanList, String formCode, String formId, String userId);
 
	//ajax call // TODO used by ireport element (for version > 9.6 -> delete this function and get data as in datatable element)
	ActionBean getSqlByCatalogDBBean(ActionBean requestAction);
 
	String getCorrectFormCode(String formId, String formCode);

	String getCorrectNewFormCode(String formId, String formCode, Map<String, String[]> requestMap);

	void cleanForm(long stateKey, String formCode_doBack);
  
	ModelAndView initFormParam(long stateKey, String formCode, String formId, String userId, Map<String, String[]> requestMap);

//	ActionBean getTitleAndSubtitleForm(ActionBean actionBean);

	String getFormType(String formCode);

	ActionBean updateWFState(long stateKey, List<DataBean> dataBeanList, String formCode, String formId, String userId,
			String isNew);
	
	String getFileContent(MultipartFile file, String formCodeFull, String elementId);

	RedirectInfo redirectRules(String formId, String formCode, String parentId, Boolean isNew);
}