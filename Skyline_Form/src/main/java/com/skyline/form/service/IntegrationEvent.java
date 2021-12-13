package com.skyline.form.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public interface IntegrationEvent
{
	List<String> getUpdateCacheFormList(String formCode);
 
	String chemDoodleReactionTabEvent(String reactionMrv, String parentId, String formCode)
			throws Exception;
	
	String chemDoodleReactionTabUp(String reactionMrv, String parentId, String formCode)
			throws ParserConfigurationException, SAXException, IOException, TransformerException;

	String generalEvent(long stateKey, Map<String, String> elementValueMap, String formCode, String formId, String userId, String isNew, String eventAction) throws Exception;
 

	void onClobAndResultEvent(String formCode, String resultValue, String output, String parent_id,
			Map<String, String> elementValueMap, String tableNumber);
 
	String chemDoodleCanvasUpdateData(String ID);

	void copyReactionFromPrevStep(String formId, String prevStepFormId, String userId) throws Exception;

	String getExcelComponentList(String parentId);

	String getQrCodeLabel(String formId, String structFormCode);
};