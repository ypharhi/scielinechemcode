package com.skyline.form.service;

import java.io.IOException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.LookupType;
import com.skyline.form.bean.ValidationCode;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.dal.UploadFileDao; 

@Service
public class FormApiExcelService {

	private static final Logger logger = LoggerFactory.getLogger(FormApiExcelService.class);

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private IntegrationEvent integrationEvent;

	@Autowired
	private IntegrationValidation integrationValidation;

	@Autowired
	private FormDao formDao;

	@Autowired
	private FormSaveDao formSaveDao;

	@Autowired
	private UploadFileDao uploadFileDao;

	@Autowired
	private GeneralDao generalDao;

	@Transactional
	public String saveSpreadsheet(DataBean dataBean,String isNew, String formCode, String formId) throws Exception{
		String elementId = generalUtil.getJsonValById(dataBean.getVal(), "elementID");//get the saved value
		String isChangedflag = "1";
		try{
			isChangedflag = generalUtil.getJsonValById(dataBean.getVal(),"isChangedflag");
		} catch(Exception e){
			e.printStackTrace();
		}
		
		String formcodeEntity = formDao.getFormCodeEntityBySeqId(formCode, formId);
		String table = "fg_s_"+formcodeEntity+"_pivot";
		
		if(isChangedflag.equals("1") || isNew.equals("1") 
				|| generalUtil.getNull(elementId).equals("")|| generalUtil.getNull(elementId).equals("-1")) { // changed / new (after cone)  / empty are condition to get a  new elementid. Note: if isNnew and isChangedflag = 0 with elementId (NOT EMPTY) then it is clone 
			elementId = formSaveDao.getStructFileId(formCode + "." + dataBean.getCode());//always get new elementID
			String value = generalUtil.getJsonValById(dataBean.getVal(), "value");
			
			if(formCode.equals("ExperimentAn") && dataBean.getCode().equals("spreadsheetResults")) {
				String experimentTypeName = formDao.getFromInfoLookup("Experiment", LookupType.ID, formId, "EXPERIMENTTYPENAME");
				if(experimentTypeName.equals("General")) {
					//1. checks if there was an error that found on the client size
					String spreadsheetResultsData = value;
					JSONObject js = new JSONObject();
					if(!generalUtil.getNull(spreadsheetResultsData).isEmpty()){

						js = new JSONObject(spreadsheetResultsData);
						String validationMessage = (String) js.get("validationMessage");
						if(!validationMessage.isEmpty()) {
							throw new Exception(validationMessage);
						}
						
						//2. checks if the result type is missing
						JSONObject jsspreadsheetData = (JSONObject)js.get("output");
						
						if(jsspreadsheetData.has("0")){
							JSONArray arr = new JSONArray(jsspreadsheetData.get("0").toString());
							for(int i = 0;i<arr.length();i++) {
								JSONObject sampleMaterialPair = arr.getJSONObject(i);
								String material = generalUtil.getNull(sampleMaterialPair.getString("Material"));
								String manualMaterial = generalUtil.getNull(sampleMaterialPair.getString("Unknown Materials"));
								String resultType = generalUtil.getNull(sampleMaterialPair.getString("Results Type")) ;
								String rt = generalUtil.getNull(sampleMaterialPair.getString("RT")) ;
								if(material.isEmpty() && manualMaterial.isEmpty()) {
									continue;
								}
								if(resultType.isEmpty()) {
									integrationValidation.validate(ValidationCode.INVALID_SPREADSHEETRESULT_MISSING_DATA, formCode, formId, "", new StringBuilder());
								}
							}
						}
					}
				}
			}
			
			String elementImpCode_ = dataBean.getCode();
			uploadFileDao.saveStringAsClob(elementId, value);
		
			String sql = "update "+table+"\n"
					+ " set "+elementImpCode_+" = '"+elementId+"'\n"
					+ " where formId = '"+formId+"'";
			formSaveDao.updateStructTableByFormId(sql, table, Arrays.asList(elementImpCode_), formId);
			
		}
		if (elementId.equals("-1")) {
			throw new Exception(generalUtil.getSpringMessagesByKey("FAILED_SAVE_CLOB",
					"Save failed. Please, try again or call your administrator."));
		}
		
		JSONObject retObj = new JSONObject();
		retObj.put("elementId", elementId);
		String timeStamp = generalDao.selectSingleStringNoException("select to_char(TIMESTAMP,'dd/MM/yyyy  HH24:MI:SS')\n"
				+ " from "+table+"\n"
				+ " where formId = '"+formId+"'");
		retObj.put("lastChangeDate", timeStamp);
		return retObj.toString();
	}

	public ActionBean getExcelComponentList(String formId, ActionBean requestAction) {
		//String parentId = requestAction.getData().get(1).getVal();
		logger.info("getExcelComponentList");
		ActionBean action = null;
		try {
			action = new ActionBean("no action needed", generalUtil
					.StringToList(integrationEvent.getExcelComponentList(formId)), "");
		} catch (Exception e) {
			action = new ActionBean("no action needed", generalUtil.StringToList(""), e.getMessage());
		}
		return action;
	}

	public ActionBean getExcelDataById(long stateKey, String formId, String formCode, String domId, String fileId) {
		fileId = generalUtil.getEmpty(fileId, "-1");
		JSONObject js = new JSONObject();
		if (!fileId.equals("-1")) {
			String sql = "select t.file_content from fg_clob_files t where t.file_id = '" + fileId + "'";
			String elementData = generalDao.getSingleStringFromClob(sql);			
			if(!generalUtil.getNull(elementData).isEmpty()){
				js = new JSONObject(elementData);
			}
		}
		return new ActionBean("no action needed", generalUtil.StringToList(js.toString()), "");
	}
}
