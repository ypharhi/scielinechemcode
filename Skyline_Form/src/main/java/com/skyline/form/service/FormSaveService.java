package com.skyline.form.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.FormSaveTaskInfo;

public interface FormSaveService {

	ActionBean doSave(Long stateKey, List<DataBean> dataBeanList, String formCode, String formId, String userId, String isNew, String SaveAction, String saveName, String useLoginsessionidScopeFlag, String description, String formPathInfo, FormSaveTaskInfo formSaveTaskInfo, String lastChangeUserId, String lastChangeDate) throws Exception;
 
	void doSaveTask(FormSaveTaskInfo formSaveTaskInfo);

	String saveFile(MultipartFile file, String formCodeFull, String formId);

	String saveFileAsClob(MultipartFile file, String FORM_ID); 
	
	public String getNextFormId(String formCode, String parentFormId);

	String doSplit(String formId, String currentQuantity, String splitQuantity, String splitQuantityUom) throws Exception;

	String doClone(String formId);

	String doMultiClone(String formId, String cloneQuantity);

	String updateFormSeqWithName(String formCode, String formId);

	String updateUnpivotFormIdList(String formId);

	String generateFromId(String formCode);

	String doCloneBySaveDefaultData(String formCode, String parentId);
}
