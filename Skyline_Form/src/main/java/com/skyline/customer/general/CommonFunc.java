package com.skyline.customer.general;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.Form;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.dal.GeneralTaskDao;
import com.skyline.form.service.GeneralUtilFormState;

@Service
public class CommonFunc {

	@Autowired
	FormDao formDao;
	
	@Autowired
	FormSaveDao formSaveDao;
	
	@Autowired
	GeneralDao generalDao;
	
	@Autowired
	GeneralUtilFormState generalUtilFormState;
	
	@Autowired
	GeneralTaskDao generalTaskDao;
	
	public String doRemove(String formCode, String formId, String userId) {
		String toReturn = "";
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		// system USER will remove it totally from the DB
//		if (formCode.equals("XXX")) {
//			toReturn = formDao.removeFromDB(formId, form.getFormCodeEntity());
//		} 
//		else {
			if (generalDao.checkIfColumnExists("fg_s_" + form.getFormCodeEntity() + "_v", "parentid")) {
				String parentId = formDao.getFormParentId(formCode, formId);
				String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, parentId);
				if (sessionId != null) {
					toReturn = formSaveDao.doRemoveTmpDataProduction(formCode, formId, sessionId, userId);
				} else {
					formSaveDao.doRemoveProduction(formCode, formId, userId);
				}
			} else {
				formSaveDao.doRemoveProduction(formCode, formId, userId);
			}
//		}

		//update cache
		try {
			//generalTask.updateCach(integrationEvent.getUpdateCacheFormList(formCode));
			generalTaskDao.updateCach(form);
			
			generalTaskDao.updateMVByPivotTable(form.getFormType(), "FG_S_" + form.getFormCodeEntity().toUpperCase() + "_PIVOT", formCode, 0, "", "D");
		} catch (Exception e) {
			// DO NOTHING
		}

		return toReturn;
	}
 
	
}