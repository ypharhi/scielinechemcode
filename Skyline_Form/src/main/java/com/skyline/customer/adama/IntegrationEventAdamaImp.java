package com.skyline.customer.adama;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import com.skyline.form.bean.LookupType;
import com.skyline.form.dal.FormDao;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.IntegrationEvent;

@Service
public class IntegrationEventAdamaImp implements IntegrationEvent {
	
	@Autowired
	GeneralUtilFormState generalUtilFormState;
	
	@Autowired
	FormDao formDao;

	@Override
	public List<String> getUpdateCacheFormList(String formCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String chemDoodleReactionTabEvent(String reactionMrv, String parentId, String formCode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String chemDoodleReactionTabUp(String reactionMrv, String parentId, String formCode)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String generalEvent(long stateKey, Map<String, String> elementValueMap, String formCode, String formId,
			String userId, String isNew, String eventAction) throws Exception {
		// TODO Auto-generated method stub
		if (eventAction.equals("executeSQLGenerator")) { //used in SQLGenerator and in DynamicReportSql forms
			generalUtilFormState.setFormParam(stateKey, formCode, "QUERY_TEXT",
					elementValueMap.get("sqlText"));
			return "1";
		} else if (eventAction.equals("generateDynamicReport")) {
			String sqlTextContent = formDao.getFromInfoLookup("DynamicReportSql", LookupType.ID, elementValueMap.get("DYNAMICREPORTSQL_ID"),
					"SQLTEXT_CONTENT");
			generalUtilFormState.setFormParam(stateKey, formCode, "QUERY_TEXT",
					sqlTextContent);
			return "1";
		} if (eventAction.equals("getFormCodeBySeqId")) {
			return formDao.getFormCodeBySeqIdNoException(formId);
		}
		return null;
	}

	@Override
	public void onClobAndResultEvent(String formCode, String resultValue, String output, String parent_id,
			Map<String, String> elementValueMap, String tableNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String chemDoodleCanvasUpdateData(String ID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void copyReactionFromPrevStep(String formId, String prevStepFormId, String userId) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getExcelComponentList(String parentId) {
		// TODO Auto-generated method stub
		return null;
	}

	
}

