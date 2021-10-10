package com.skyline.form.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.ElementUIKeyValueDisplay;
import com.skyline.form.bean.FormSaveTaskInfo;
import com.skyline.form.bean.LevelType;
import com.skyline.form.dal.ChemDao;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.FormSaveElementDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.dal.UploadFileDao;

/**
 * 
 * Implements of FormSaveService is needed here only for the navigation in the code
 *
 */
public class FormSaveBasic implements FormSaveService {

//	@Autowired
//	protected GeneralTask generalTask;

	@Autowired
	protected FormDao formDao;

	@Autowired
	protected FormSaveDao formSaveDao;

	@Autowired
	protected FormSaveElementDao formSaveElementDao;

	@Autowired
	protected GeneralDao generalDao;

	@Autowired
	protected GeneralUtil generalUtil;

	@Autowired
	protected GeneralUtilForm generalUtilForm;

	@Autowired
	protected GeneralUtilFormState generalUtilFormState;

	@Autowired
	protected UploadFileDao uploadFileDao;

	@Autowired
	protected GeneralUtilLogger generalUtilLogger;

	@Autowired
	protected IntegrationEvent integrationEvent;

	@Autowired
	protected IntegrationSaveForm integrationSaveForm;

	@Autowired
	protected IntegrationCloneAndSplit integrationCloneAndSplit;

	@Autowired
	protected ChemDao chemDao;

	@Override
	public ActionBean doSave(Long stateKey, List<DataBean> dataBeanList, String formCode, String formId, String userId,
			String isNew, String saveAction, String saveName, String useLoginsessionidScopeFlag, String description,
			String formPathInfo, FormSaveTaskInfo formSaveTaskInfo, String lastChangeUserId, String lastChangeDate) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doSaveTask(FormSaveTaskInfo formSaveTaskInfo) {
		// TODO Auto-generated method stub
	}

	@Override
	/**
	 * save file and update file search
	 */
	public String saveFile(MultipartFile file, String formCodeFull) {
		String elementID = uploadFileDao.saveFile(file, formCodeFull);
		return elementID;
	}

	//
	@Override
	public String saveFileAsClob(MultipartFile file, String FORM_ID) {
		uploadFileDao.saveFileAsClob(FORM_ID, file);
		return "redirect:/skylineFormWebapp/jsp/dummy.html";
	}

	public String saveStringAsClob(String FORM_ID, String clobString) {
		String toReturn = uploadFileDao.saveStringAsClob(FORM_ID, clobString);
		return toReturn;
	}

	public String saveChemDoodleData(String formCode, String formId, String elementID, String fullDataArr,
			String formCodeFull, String isNew) throws ClassNotFoundException, ParserConfigurationException,
			SAXException, IOException, TransformerException, SQLException, Exception {
		String toReturn = chemDao.saveChemData(formCode, formId, elementID, fullDataArr, formCodeFull, isNew);
		return toReturn;
	} 
	
	@Override
	public String getNextFormId(String formCode, String parentFormId) {
		String returnId = "-1";
		boolean isStructTable = generalUtilForm.isStructFromByFormCode(formCode);
		if (isStructTable) {
			returnId = formSaveDao.getStructFormId(formCode, parentFormId);
		}
		return returnId;
	}

	@Override
	@Transactional
	public String doSplit(String formId, String currentQuantity, String splitQuantity, String splitQuantityUom)
			throws Exception {
		String cloneFormId = "-1";
		cloneFormId = integrationCloneAndSplit.splitSaveEvent(formId, currentQuantity, splitQuantity, splitQuantityUom);
		return cloneFormId;
	}

	@Override
	@Transactional
	public String doClone(String formId) {
		String cloneFormId = "-1";
		Map<String, String> replaceFieldsMap = integrationCloneAndSplit.cloneRemoveFields(formId, cloneFormId);
		cloneFormId = formSaveDao.cloneStructTable(formId, replaceFieldsMap,null);
		integrationCloneAndSplit.postCloneSaveEvent(formId, cloneFormId);
		return cloneFormId;
	}
	
	@Override
	@Transactional
	public String doCloneBySaveDefaultData(String formCode, String parentId) {
		String cloneFormId = "-1";
		cloneFormId = integrationCloneAndSplit.doCloneBySaveDefaultData(formCode, parentId);
		return cloneFormId;
	}

	@Override
	@Transactional
	public String doMultiClone(String formId, String cloneQuantity) {
		String result;
		result = integrationCloneAndSplit.MultiCloneSaveEvent(formId, cloneQuantity);
		return result;
	}

	public Map<String, String> initElementValueMapByDataBeanList(List<DataBean> dataBeanList,
			List<String> preventSaveElementList, List<DataBean> additinalDataSaveList,
			Map<String, String> elementAdditinalDataoMap,
			Map<String, ElementUIKeyValueDisplay> elementUIKeyValueDisplayMap, String formCode, String formId,
			String userId, String isNew) throws Exception {

		Map<String, String> elementValueMap = new HashMap<String, String>();
		Map<String, String> paramMonitoringValueMap, paramMonitoringUomMap, paramMonitoringFormIdMap;
		ElementUIKeyValueDisplay elementUIKeyValueDisplay;
		JSONObject jsonObject = null;
		//		Map<String, String> elementUserLastSaveValueMap = new HashMap<String, String>();
		//		List<DataBean> additinalDataSaveList = new ArrayList<DataBean>();

		for (DataBean dataBean : dataBeanList) {
			elementUIKeyValueDisplay = null;
			String beanInfo = dataBean.getInfo();

			// yp 04042018 write to log. next versions avoid saving if exception (see the log and think who to do it carefully)
			String elementImpCode_ = "";
			try {
				elementImpCode_ = dataBean.getCode();
				if (!beanInfo.isEmpty() && beanInfo.trim().startsWith("{")) {
					jsonObject = new JSONObject(beanInfo);
				}
			} catch (Exception e) {
				generalUtilLogger.logWrite(
						LevelType.WARN, "WARN - error in saving beanInfo=" + beanInfo + " for formcode=" + formCode
								+ ",elementImpCode_=" + elementImpCode_,
						formId, ActivitylogType.SaveException, null, e);

			}

			//get saveType_ from beanInfo (avoid json parse if none)
			String saveType_ = "none";
			if(beanInfo != null && !beanInfo.contains("\"saveType\":\"none\"")) {
				saveType_  = generalUtil.getJsonValById(beanInfo, "saveType");
			}
			
			//get formPreventSave_ from beanInfo (avoid json parse if 0/1/2)
			String formPreventSave_ = "";
			if(beanInfo != null && beanInfo.contains("\"formPreventSave\":\"0\"")) {
				formPreventSave_ = "0";
			} else if(beanInfo != null && beanInfo.contains("\"formPreventSave\":\"1\"")) {
				formPreventSave_ = "1";
			} else if (beanInfo != null && beanInfo.contains("\"formPreventSave\":\"2\"")) {
				formPreventSave_ = "2";
			} else {
				formPreventSave_  = generalUtil.getJsonValById(beanInfo, "formPreventSave");
			}

			if (generalUtil.getNull(formPreventSave_).equals("1")) { // don't save (put in not save list)
				preventSaveElementList.add(dataBean.getCode());
			} else if (generalUtil.getNull(formPreventSave_).equals("2")) { // don't save in form data but save as additional data
				preventSaveElementList.add(dataBean.getCode());
				additinalDataSaveList.add(dataBean);
				elementAdditinalDataoMap.put(dataBean.getCode(), dataBean.getVal());
			} else if (generalUtil.getNull(saveType_).equalsIgnoreCase("clob")) { // by savetype
				String elementId = formSaveDao.getStructFileId(formCode + "." + dataBean.getCode());//always get new elementID 
				String value = dataBean.getVal();
				String retVal = saveStringAsClob(elementId, value);
				if(retVal.equals("-1")) {
					throw new Exception(generalUtil.getSpringMessagesByKey("FAILED_SAVE_CLOB",
							"Save failed. Please, try again or call your administrator."));
				}
				dataBean.setVal(elementId);
			} else if (generalUtil.getNull(saveType_).equalsIgnoreCase("excelSheet")) { // by savetype
				String elementId = generalUtil.getJsonValById(dataBean.getVal(), "elementID");//get the saved value
				String isChangedflag = "1";
				try{
					isChangedflag = generalUtil.getJsonValById(dataBean.getVal(),"isChangedflag");
				} catch(Exception e){
					e.printStackTrace();
				}
				if(isChangedflag.equals("1") || isNew.equals("1") 
						|| generalUtil.getNull(elementId).equals("")|| generalUtil.getNull(elementId).equals("-1")) { // changed / new (after cone)  / empty are condition to get a  new elementid. Note: if isNnew and isChangedflag = 0 with elementId (NOT EMPTY) then it is clone 
					elementId = formSaveDao.getStructFileId(formCode + "." + dataBean.getCode());//always get new elementID
					String value = generalUtil.getJsonValById(dataBean.getVal(), "value");
					//check for an empty value
					if(value.isEmpty()) {
						throw new Exception("The Spreadsheet save was failed. No value was accepted for save. Please, call your administrator");
					}
					String retVal = saveStringAsClob(elementId, value);
					if(retVal.equals("-1")) {
						throw new Exception("The Spreadsheet save was failed. Please, try again or call your administrator");
					}
				}
				if (elementId.equals("-1")) {
					throw new Exception("The Spreadsheet save was failed. Please, try again or call your administrator");
				}
				dataBean.setVal(elementId);
			}else if (generalUtil.getNull(saveType_).equals("chemdoodle")) {
				String isChangedflag = "1";
				String elementId = generalUtil.getJsonValById(dataBean.getVal(), "elementId");//get the saved value
				try {
					isChangedflag = (String)jsonObject.get("isChangedflag");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(/*!formCode.equals("InvItemMaterial") ||*/ isChangedflag.equals("1") || isNew.equals("1") || generalUtil.getNull(elementId).equals("")) { // changed / new (after cone)  / empty are condition to get a  new elementid. Note: if isNnew and isChangedflag = 0 with elementId (NOT EMPTY) then it is clone - if the molecule is not empty it will be failed on duplication and we can prevent this in next version for better performance (in this code instead of jchem DB constructor) 
					String formCodeFull = generalUtil.getJsonValById(dataBean.getVal(), "formCodeFull");
					//String elementId = formSaveDao.getStructFormId(formCode + "." + dataBean.getCode());//ab 30/08/18 
					elementId = formSaveDao.getStructFileId(formCode + "." + dataBean.getCode());//get new elementID
					String fullArray = generalUtil.getJsonValById(dataBean.getVal(), "fullArray");
					dataBean.setVal(saveChemDoodleData(formCode, formId, elementId, fullArray, formCodeFull, isNew));
					elementValueMap.put("chem_struct_change_flag", "1");//flag for InvitemMaterial save process
				} else {
					dataBean.setVal(elementId);
					elementValueMap.put("chem_struct_change_flag", "0");//flag for InvitemMaterial save process
				}
			}else if(generalUtil.getNull(saveType_).equals("diagram")){
				String isChangedflag = "1";
				String elementId = generalUtil.getJsonValById(dataBean.getVal(), "elementId");
				String isHidden = generalUtil.getJsonValById(dataBean.getVal(), "isHidden");
				if(Boolean.valueOf(isHidden)){
					dataBean.setVal(elementId);
					continue;
				}
				try {
					//isChangedflag = (String)jsonObject.get("isChangedflag");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if(isChangedflag.equals("1") || isNew.equals("1") || generalUtil.getNull(elementId).equals("")) { // changed / new (after cone)  / empty are condition to get a  new elementid. Note: if isNnew and isChangedflag = 0 with elementId (NOT EMPTY) then it is clone - if the molecule is not empty it will be failed on duplication and we can prevent this in next version for better performance (in this code instead of jchem DB constructor) 
					elementId = formSaveDao.getStructFileId(formCode + "." + dataBean.getCode());//get new elementID
					dataBean.setVal(formSaveElementDao.saveDiagram(formCode, dataBean, elementId));
					//elementValueMap.put("chem_struct_change_flag", "1");//flag for InvitemMaterial save process
				} else {
					dataBean.setVal(elementId);
					//elementValueMap.put("chem_struct_change_flag", "0");//flag for InvitemMaterial save process
				}
			}
			else if (generalUtil.getNull(saveType_).equals("richText")) {
				//saveRichText(elementId, value, plainText);
				String elementId = generalUtil.getJsonValById(dataBean.getVal(), "elementID");//get the saved value
				String isChangedflag = "1";
				try{
					isChangedflag = generalUtil.getJsonValById(dataBean.getVal(),"isChangedflag");
				} catch(Exception e){
					e.printStackTrace();
				}
				if(isChangedflag.equals("1") || isNew.equals("1") || generalUtil.getNull(elementId).equals("")) { // changed / new (after cone)  / empty are condition to get a  new elementid. Note: if isNnew and isChangedflag = 0 with elementId (NOT EMPTY) then it is clone 
					elementId = formSaveElementDao.saveRichText(formCode, dataBean, true);
				} 
				if (elementId.equals("-1")) {
					throw new Exception(generalUtil.getSpringMessagesByKey("FAILED_SAVE_CLOB",
							"Save failed. Please, try again or call your administrator."));
				}
				dataBean.setVal(elementId);
			} else if (generalUtil.getNull(saveType_).equals("clobAndResult")) {
				String isTableHasRows = generalUtil.getJsonValById(dataBean.getVal(), "isTableHasRows");
				// save element only if table has at least one row
				if (isTableHasRows.equals("1")) {
					String elementId = formSaveDao.getStructFileId(formCode + "." + dataBean.getCode());//always get new elementID 
					String objToSaveAsClob = generalUtil.getJsonValById(dataBean.getVal(), "objToSaveAsClob");
					String resultValue = generalUtil.getJsonValById(dataBean.getVal(), "resultValue");
					String output = generalUtil.getJsonValById(dataBean.getVal(), "output");
					String parentID = generalUtil.getJsonValById(dataBean.getVal(), "parentID");
					String webixTableGroupNumber = generalUtil.getJsonValById(dataBean.getVal(), "webixTableGroupNumber"); // for Experiment/ExperimentCP Mass Balance

					String retval = saveStringAsClob(elementId, objToSaveAsClob);
					if (retval.equals("-1")) {
						throw new Exception(generalUtil.getSpringMessagesByKey("FAILED_SAVE_CLOB",
								"Save failed. Please, try again or call your administrator."));
					}

					integrationEvent.onClobAndResultEvent(formCode, resultValue, output, parentID, elementValueMap, webixTableGroupNumber);

					dataBean.setVal(elementId);
				} else {
					dataBean.setVal("");
				}
			}
			/**
			 * if the element is paramMonitoring, then : 1) add each of the elements inside the paramMonitoring to the elementValueMap 2) add each of the elements inside the paramMonitoring to the preventSaveElementList 3) save each of the elements inside the paramMonitoring inside fg_parammonitoring table
			 * 
			 * 
			 * ** for example: element with the id 'temperature' inside the Param Monitoring will have the next keys-values inside the elementValueMap: 1) key: temperature, value: "52" 2) key: temperature_uom_id, value: "2769" 3) key: temperature_uom_obj, value: "{"val" : "2769", "text" : "c"}"
			 * 
			 */
			else if (generalUtil.getNull(saveType_).equals("paramMonitoring")) {
				paramMonitoringValueMap = new HashMap<String, String>();
				paramMonitoringUomMap = new HashMap<String, String>();
				paramMonitoringFormIdMap = new HashMap<String, String>();
				String jsonSource = dataBean.getVal();
				JSONObject jo = new JSONObject(jsonSource);
				Iterator<?> keys = jo.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					if (jo.get(key) instanceof JSONObject) {
						if (key.contains("_uom")) {
							preventSaveElementList.add(key + "_id");
							preventSaveElementList.add(key + "_obj");
							paramMonitoringUomMap.put(key + "_id", ((JSONObject) jo.get(key)).get("val").toString());
							paramMonitoringUomMap.put(key + "_obj", jo.get(key).toString());
						} else {
							preventSaveElementList.add(key);
							paramMonitoringValueMap.put(key, ((JSONObject) jo.get(key)).get("val").toString());
							paramMonitoringFormIdMap.put(key, ((JSONObject) jo.get(key)).get("formid").toString());
						}
					}

				}
				String calcIdentifier = formSaveElementDao.saveFormMonitoParamData(paramMonitoringValueMap,
						paramMonitoringUomMap, paramMonitoringFormIdMap, formId, jsonSource);

				elementValueMap.put("calcIdentifier", calcIdentifier);
				elementValueMap.putAll(paramMonitoringValueMap);
				elementValueMap.putAll(paramMonitoringUomMap);
			}

			/**
			 * if the element type is DynamicParams
			 */
			else if (generalUtil.getNull(saveType_).equals("DynamicParams")) {
				formSaveElementDao.saveDynamicParams(dataBean.getVal());
			}

			/* check if property 'displayValue' exists in JSON, that contents value to display in AuditTrail */
			if (generalUtil.checkIfKeyExistsInJson(beanInfo, "displayValue")) {
				elementUIKeyValueDisplay = new ElementUIKeyValueDisplay(dataBean.getCode(),
						generalUtil.getNull(dataBean.getVal()), generalUtil.getJsonValById(beanInfo, "displayValue"));
			}

			//			/* check if property 'userLastSaveValue' exists in JSON, that contents DB value*/
			//			if (generalUtil.checkIfKeyExistsInJson(beanInfo,"userLastSaveValue"))
			//			{
			//				 String toSave = "";
			//				 try {
			//					 	
			//						String userLastSaveVal = generalUtil.getJsonValById(beanInfo, "userLastSaveValue");
			//						if(!generalUtil.getNull(userLastSaveVal).equals("")) {
			//							toSave = generalUtil.getJsonValById(userLastSaveVal, "value");
			//						} else {
			//							toSave = "";
			//						}
			//					} catch (Exception e) {
			//						//
			//					}
			//				 
			//				elementUserLastSaveValueMap.put(dataBean.getCode(), toSave);
			//			}

			elementUIKeyValueDisplayMap.put(dataBean.getCode(), elementUIKeyValueDisplay);
			elementValueMap.put(dataBean.getCode(), generalUtil.getNull(dataBean.getVal()));
		}

		//		formSaveDao.doSave(formCode, "-1", userId, elementUserLastSaveValueMap);

		//save additional data
		//		formSaveDao.saveAdditionalData(additinalDataSaveList, formCode, formId);

		return elementValueMap;
	}

	public Map<String, String> saveUserSettings(List<DataBean> dataBeanList, String formCode, String formId,
			String userId, String saveName, String useLoginsessionidScopeFlag, String description)
			throws ClassNotFoundException, ParserConfigurationException, SAXException, IOException,
			TransformerException, SQLException {

		Map<String, String> elementValueMap = new HashMap<String, String>();
		Map<String, String> elementUserLastSaveValueMap = new HashMap<String, String>();

		for (DataBean dataBean : dataBeanList) {
			String beanInfo = dataBean.getInfo();

			/* check if property 'userLastSaveValue' exists in JSON, that contents value for each user */
			if (generalUtil.checkIfKeyExistsInJson(beanInfo, "userLastSaveValue")) {
				String userVal = generalUtil.getJsonValById(beanInfo, "userLastSaveValue");
				elementUserLastSaveValueMap.put(dataBean.getCode() + "_USER_LAST_SAVE_VALUE", userVal);
			}
		}

		formSaveDao.doSaveFormLastSaveValues(formCode, "-1", userId, elementUserLastSaveValueMap, saveName, useLoginsessionidScopeFlag,
				description);

		return elementValueMap;
	}

	public String resetUserDataAndSettings(List<DataBean> dataBeanList, String formCode, String formId, String userId,
			String saveName, String useLoginsessionidScopeFlag, String description) {
		Map<String, String> elementResetAllValueMap = new HashMap<String, String>();

		for (DataBean dataBean : dataBeanList) {
			String beanInfo = dataBean.getInfo();

			/* check if property 'userLastSaveValue' exists in JSON, that contents value for each user */
			if (generalUtil.checkIfKeyExistsInJson(beanInfo, "userLastSaveValue")) {
				elementResetAllValueMap.put(dataBean.getCode() + "_USER_LAST_SAVE_VALUE", "");
			}
			elementResetAllValueMap.put(dataBean.getCode(), "");
		}

		return formSaveDao.doSaveFormLastSaveValues(formCode, "-1", userId, elementResetAllValueMap, saveName, useLoginsessionidScopeFlag,
				description);
	}

	@Override
	public String updateFormSeqWithName(String formCode, String formId) {
		return "";
	}

	@Override
	public String updateUnpivotFormIdList(String formId) {
		return "";
	}

	@Override
	public String generateFromId(String formCode) {
		// TODO Auto-generated method stub
		return formSaveDao.getStructFormId(formCode);
	}
}
