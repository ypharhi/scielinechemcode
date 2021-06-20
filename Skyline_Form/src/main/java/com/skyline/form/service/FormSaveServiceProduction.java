package com.skyline.form.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.ElementUIKeyValueDisplay;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.FormSaveTaskInfo;
import com.skyline.form.bean.FormType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.dal.GeneralTaskDao;

@Service
public class FormSaveServiceProduction extends FormSaveBasic implements FormSaveService {

	@Autowired
	protected GeneralUtilPermission generalUtilPermission;

	@Autowired
	protected GeneralTaskDao generalTaskDao;

	// !!! WARNING !!! dropAndCreateTableFlag should be one only during DEVELOP when
	// we want recreate tables based on form builder configurations !!!!! (there is
	// another protection inside the DB procedure that check if in
	// fg_sys_param.is_develop = 1 before this recreate tables action)
	@Value("${dropAndCreateTableFlag:0}")
	private int dropAndCreateTableFlag;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ActionBean doSave(Long stateKey, List<DataBean> dataBeanList, String formCode, String formId, String userId,
			String isNew, String saveAction, String saveName, String useLoginsessionidScopeFlag, String description,
			String formPathInfo, FormSaveTaskInfo formSaveTaskInfo, String lastChangeUserId, String lastChangeDate) throws Exception {
		
		generalUtilLogger.logWriter(LevelType.DEBUG,"Start saving.... formCode=" + formCode + ", formId=" + formId,
				ActivitylogType.PerformanceJava, formId);
		
		String update = "";
		String pivotTable = "";
		String errMsg = "";
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		String dbTransactionId = generalUtil.getDBTransaction();
		StringBuilder sbInfo = new StringBuilder();
		List<String> preventSaveElementList = new ArrayList<String>();
		List<DataBean> additinalDataSaveList = new ArrayList<DataBean>();
		Map<String, String> elementAdditinalDataoMap = new HashMap<String, String>();
		Map<String, ElementUIKeyValueDisplay> elementUIKeyValueDisplayMap = new HashMap<String, ElementUIKeyValueDisplay>();
		List<String> tempPivotTableHolder = new ArrayList<String>();
		List<DataBean> dataBeanReturnList = new ArrayList<DataBean>();

		if (saveAction.equals("REMOVE_FORM_AND_USER_SETTINGS")) {
			resetUserDataAndSettings(dataBeanList, formCode, formId, userId, saveName, useLoginsessionidScopeFlag,
					description);
			return new ActionBean("no action needed", generalUtil.StringToList(update, sbInfo.toString()), errMsg);
		}

		if (saveAction.equals("SAVE_USER_SETTINGS")) {
			saveUserSettings(dataBeanList, formCode, formId, userId, saveName, useLoginsessionidScopeFlag,
					description);
			return new ActionBean("no action needed", generalUtil.StringToList(update, sbInfo.toString()), errMsg);
		}

		if (saveAction.equals("SAVE_FORM_AND_USER_SETTINGS")
				|| saveAction.equals("SAVE_FORM_AND_USER_SETTINGS_BY_NAME")) {
			saveUserSettings(dataBeanList, formCode, formId, userId, saveName, useLoginsessionidScopeFlag,
					description);
		}

		if (saveAction.equals("SAVE_DESIGN_REPORT")) {
			saveUserSettings(dataBeanList, formCode, formId, userId, saveName, useLoginsessionidScopeFlag,
					description);
		}

		Map<String, String> elementValueMap = initElementValueMapByDataBeanList(dataBeanList, preventSaveElementList,
				additinalDataSaveList, elementAdditinalDataoMap, elementUIKeyValueDisplayMap, formCode, formId, userId,
				isNew);
		// elementValueMap.put("rowDeleted", "0");
		// elementValueMap.put("rowSessionId", sessionId);
		boolean isStructTable = generalUtilForm.isStructFromByFormCode(formCode);
		if (isStructTable) {

			if (dropAndCreateTableFlag == 1) { // drop and create pivot tables
				// !!! WARNING !!! this should be execute only during DEVELOP!!!!! (there is
				// another protection inside the DB procedure that check if in
				// fg_sys_param.is_develop = 1)
				update = formSaveDao.doSaveDropAndCreatePivot(formCode, formId, userId,
						removePreventSaveElements(elementValueMap, preventSaveElementList), null, saveName, "",
						description);
				if(generalUtil.getNullInt(update,0) > 0) {
					formSaveDao.createStructPivotTable(formCode, formId);
				}
			}

			String sessionId = null;
			try {
				pivotTable = "FG_S_" + generalUtil.getNull(form.getFormCodeEntity(), formCode).toUpperCase() + "_PIVOT";
				elementValueMap.put("formId", formId);
				
				generalUtilLogger.logWriter(LevelType.DEBUG,"Start preFormSaveEvent formCode=" + formCode + ", formId=" + formId,
						ActivitylogType.PerformanceJava, formId);
				
				int preFormSaveEvent = integrationSaveForm.preFormSaveEvent(stateKey, formCode, formId, elementValueMap,
						elementAdditinalDataoMap, userId, isNew, saveAction, sbInfo);// , pivotTable, isStructTable);
				String auditTrailChangeType = (preFormSaveEvent == 1) ? "I" : "U";// audit trail change_type is 'update'
																					// or 'insert'
				// integrationEvent.preFormSaveEvent(formCode, formId, elementValueMap, userId,
				// pivotTable, isStructTable);

				// handle tmp form records... (Note: the transaction in the below code is a term
				// we use for the child from records data that needed to be save with the parent
				// from [it is not related to the @Transactional procee])
				String parentId = elementValueMap.get("parentId");
				sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, parentId);
				if (sessionId == null) {
					if (generalUtilFormState.isOpenTransaction(formId)) {
						List<String> formCodeList = generalUtilFormState.getFormCodeTransactionList(formId, formCode);
						if (formCodeList != null) {
							for (String formCode_ : formCodeList) {
								generalUtilLogger.logWriter(LevelType.DEBUG,"Start save tmp data popup formcode=" + formCode_ + ", formcode=" + formCode + ", formId=" + formId,
										ActivitylogType.PerformanceJava, formId);
								
								formSaveDao.doSaveTmpDataProduction(formCode_, formId,
										generalUtilFormState.getSessionId(formId), userId);
								if (!tempPivotTableHolder.contains(formCode_)) {
									tempPivotTableHolder.add(formCode_);
								}
							}
						}
					}
				}
				// tmp form Done!

				// save additional info
				generalUtilLogger.logWriter(LevelType.DEBUG,"Start save saveAdditionalData formcode=" + formCode + ", formId=" + formId,
						ActivitylogType.PerformanceJava, formId);
				formSaveDao.saveAdditionalData(elementValueMap, additinalDataSaveList, formCode, formId);

				// copy elementValueInfATMap for inf AT
				Map<String, String> elementValueInfATMap = new HashMap<String, String>();
				for (Map.Entry<String, String> entry : elementValueMap.entrySet()) {
					elementValueInfATMap.put(entry.getKey(), entry.getValue());
				}

				generalUtilLogger.logWriter(LevelType.DEBUG,"Start doSaveStruct formcode=" + formCode + ", formId=" + formId,
						ActivitylogType.PerformanceJava, formId);
				update = formSaveDao.doSaveStruct(form, formId, userId,
						removePreventSaveElements(elementValueMap, preventSaveElementList, pivotTable), pivotTable,
						sessionId, isNew, lastChangeUserId, lastChangeDate);

				if (update.equals("0")) {
					// throw id save failed
					FormType formType = generalUtilForm.getFromType(formCode);
					if((formType.equals(FormType.STRUCT)||formType.equals(FormType.INVITEM)) && isNew.equals("0")){
						String sql = "select CHANGE_BY from "+pivotTable+"\n"
								+ " where formid = '"+formId+"'\n"
								+ " and (CHANGE_BY <> '"+lastChangeUserId+"'\n"
								+ " or to_char(TIMESTAMP,'"+generalUtil.getConversionDateTimeSecondsFormat()+"') <> '"+lastChangeDate+"')";
						String simultaneousWorkFailUser = generalDao.selectSingleStringNoException(sql);
						if(!simultaneousWorkFailUser.isEmpty()){
							throw new Exception(generalUtil.getSpringMessagesByKey("SAVE_SIMULTANEOUS_WORK_FAILED",new Object[]{formDao.getFromInfoLookup("User", LookupType.ID, simultaneousWorkFailUser, "name") }, "Save failed"));
						}
					}
					throw new Exception(generalUtil.getSpringMessagesByKey("SAVE_FAILED", "Save failed"));
				}
				
				//save system excel as clob
			    saveSysConfExcelAsClob(formCode, formId);
				

				// post save
				generalUtilLogger.logWriter(LevelType.DEBUG,"Start postFormSaveEvent formcode=" + formCode + ", formId=" + formId,
						ActivitylogType.PerformanceJava, formId);
				
				integrationSaveForm.postFormSaveEvent(stateKey, formCode, formId, elementValueMap,
						elementAdditinalDataoMap, userId, isNew, saveAction, dataBeanReturnList, elementValueInfATMap,
						sbInfo);
				
				generalUtilLogger.logWriter(LevelType.DEBUG,"End saving! formCode=" + formCode + ", formId=" + formId,
						ActivitylogType.PerformanceJava, formId);

				// permissiom
				if (generalUtil.getNull(isNew, "1").equals("1")
						&& !generalUtilPermission.isCreatePermissionPostSave(formCode, formId, userId)) {
					throw new Exception(
							generalUtil.getSpringMessagesByKey("PERMISSION_NO_CREATE_ALLOWED", "Creation not allowed"));
				}

				if (formSaveTaskInfo != null) {
					formSaveTaskInfo.setForm(form);
					formSaveTaskInfo.setFormId(formId);
					formSaveTaskInfo.setIsNew(isNew);
					formSaveTaskInfo.setPivotTable(pivotTable);
					formSaveTaskInfo.setTempPivotTableHolder(tempPivotTableHolder);
					formSaveTaskInfo.setSessionId(sessionId);
					formSaveTaskInfo.setUserId(userId);
					formSaveTaskInfo.setElementValueInfATMap(elementValueInfATMap);
					formSaveTaskInfo.setElementUIKeyValueDisplayMap(elementUIKeyValueDisplayMap);
					formSaveTaskInfo.setAuditTrailChangeType(auditTrailChangeType);
					formSaveTaskInfo.setDbTransactionId(dbTransactionId);
				}

			} catch (Exception e) {
				e.printStackTrace();
				// handel transaction on failure
				generalUtilLogger.logWriter(LevelType.WARN,
						"WARN - error in Save event of formId=" + formId + ",formCode = " + formCode,
						ActivitylogType.SaveException, formId, e, null);
				generalTaskDao.onTransactionFailure(formCode, formId, dbTransactionId);
				throw new Exception(e.getMessage());
			}
		} else {
			update = formSaveDao.doSaveFormLastSaveValues(formCode, formId, userId,
					removePreventSaveElements(elementValueMap, preventSaveElementList, ""), saveName,
					useLoginsessionidScopeFlag, description);
			// generalTask.updateCach(integrationEvent.getUpdateCacheFormList(formCode));
			generalTaskDao.updateCach(form);
		}
		// return
		dataBeanReturnList.add(0, new DataBean("", update, BeanType.SAVE_FORM, sbInfo.toString()));
		return new ActionBean("no action needed", dataBeanReturnList, errMsg);
	}

	/**
	 * upload excel spreadsheet json from file by saving the excel json as clob in the SYSCONFEXCELDATA form (delete the data from the upload file content)
	 * @param formCode
	 * @param formId
	 */
	private void saveSysConfExcelAsClob(String formCode, String formId) {
		if (formCode != null && formCode.equalsIgnoreCase("SYSCONFEXCELDATA")) {
			try {
				String fileId = generalDao.selectSingleString(
						"select EXCELFILE from FG_S_SYSCONFEXCELDATA_V WHERE FORMID = '" + formId + "'");
				if (fileId != null && !fileId.isEmpty()) {
					generalDao.updateSingleString(
							"insert into fg_clob_files (file_id,file_name,file_content,content_type)\n"
									+ "select t.file_id, t.file_name, CLOBFROMBLOB(t.file_content), t.content_type\n"
									+ "from fg_files t \n" + "where t.file_id = '" + fileId + "'");
					generalDao.updateSingleString(
							"update FG_S_SYSCONFEXCELDATA_PIVOT SET EXCELFILE = NULL, EXCELDATA = '" + fileId + "'");
					generalDao.updateSingleString("DELETE FROM fg_files where file_id = '" + fileId + "'");
				}
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	@Override
	public void doSaveTask(FormSaveTaskInfo formSaveTaskInfo) {
		if (formSaveTaskInfo == null || formSaveTaskInfo.getForm() == null) {
			return;
		}
		// init vars using formSaveInfo
		Form form = formSaveTaskInfo.getForm();
		String formCodeEntity = form.getFormCodeEntity();
		String formCode = form.getFormCode();
		String formId = formSaveTaskInfo.getFormId();
		String isNew = formSaveTaskInfo.getIsNew();
		String pivotTable = formSaveTaskInfo.getPivotTable();
		List<String> tempPivotTableHolder = formSaveTaskInfo.getTempPivotTableHolder();
		String sessionId = formSaveTaskInfo.getSessionId();
		String userId = formSaveTaskInfo.getUserId();
		Map<String, String> elementValueInfATMap = formSaveTaskInfo.getElementValueInfATMap();
		Map<String, ElementUIKeyValueDisplay> elementUIKeyValueDisplayMap = formSaveTaskInfo
				.getElementUIKeyValueDisplayMap();
		String auditTrailChangeType = formSaveTaskInfo.getAuditTrailChangeType();
		String dbTransactionId = formSaveTaskInfo.getDbTransactionId();

		// *************** tasks - after transaction completed .....
		generalTaskDao.exeNotificationEvent(formId, formCodeEntity);
		// fix formCode
		generalTaskDao.correctFgSeqTableFormCode(form, formId);
		// update cache forms (update the maps for the forms marked as chache in the
		// from builder from there DB inf_v (in Adama it is the maintenance forms))
		generalTaskDao.updateCach(form);

		if (sessionId == null) {
			// refresh mv
			generalTaskDao.updateMVByPivotTable(form.getFormType(), pivotTable, formCode, 0, "",
					generalUtil.getNull(isNew, "1").equals("1") ? "I" : "U");
			for (String formCode_ : tempPivotTableHolder) { // and for every save form
				Form form_ = formDao.getFormInfoLookup(formCode_, "%", true).get(0);
				String formCodeEntity_ = form_.getFormCodeEntity();
				generalTaskDao.updateMVByPivotTable(form_.getFormType(), "FG_S_" + ((formCodeEntity_ != null && !formCodeEntity_.isEmpty())?formCodeEntity_:formCode_) + "_PIVOT", formCode_, 1, "",
						generalUtil.getNull(isNew, "1").equals("1") ? "I" : "U");
			}

			// form tmp data
			generalUtilFormState.closeTransaction(formId);

			// inf and audit trail update - last in the save process!!!
			generalTaskDao.doSaveInfoAndAuditTrail(form, formId, userId, elementValueInfATMap,
					elementUIKeyValueDisplayMap, auditTrailChangeType, dbTransactionId);
		}
	}

	private Map<String, String> removePreventSaveElements(Map<String, String> elementValueMap,
			List<String> preventSaveElementList, String tableName) {
		try {

			// if not in table also remove
			if (!generalUtil.getNull(tableName).equals("")) {
				Map<String, String> tableMeatData = generalDao.getMetaData(tableName.toUpperCase());
				for (Map.Entry<String, String> entry : elementValueMap.entrySet()) {
					if (!tableMeatData.containsKey(entry.getKey().toUpperCase())) {
						preventSaveElementList.add(entry.getKey());
					}
				}
			}

			for (String elm : preventSaveElementList) {
				elementValueMap.remove(elm);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return elementValueMap;
	}

	private Map<String, String> removePreventSaveElements(Map<String, String> elementValueMap,
			List<String> preventSaveElementList) {
		try {
			for (String elm : preventSaveElementList) {
				elementValueMap.remove(elm);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return elementValueMap;
	}
}
