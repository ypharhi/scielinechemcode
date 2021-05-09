package com.skyline.form.service;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.skyline.form.bean.ActionBean; 

@Service
public class FormApiChemService {

	private static final Logger logger = LoggerFactory.getLogger(FormApiChemService.class);

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private IntegrationEvent integrationEvent;

	public ActionBean chemDoodleReactionTabEvent(ActionBean requestAction) {
		String reactionMrv = requestAction.getData().get(0).getVal();
		String parentId = requestAction.getData().get(1).getVal();
		logger.info("chemDoodleReactionTabEvent MRV: " + reactionMrv);
		ActionBean action = null;
		try {
			action = new ActionBean("no action needed", generalUtil
					.StringToList(integrationEvent.chemDoodleReactionTabEvent(reactionMrv, parentId, "MaterialRef")), "");
		} catch (Exception e) {
			action = new ActionBean("no action needed", generalUtil.StringToList(""), e.getMessage());
		}
		return action;
	}

	public ActionBean chemDoodleReactionTabUp(ActionBean requestAction)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		String reactionMrv = requestAction.getData().get(0).getVal();
		String parentId = requestAction.getData().get(1).getVal();
		logger.info("chemDoodleReactionTabEvent MRV: " + reactionMrv);
		return new ActionBean("no action needed", generalUtil
				.StringToList(integrationEvent.chemDoodleReactionTabUp(reactionMrv, parentId, "MaterialRef")), "");
	}

	public ActionBean chemDoodleCanvasUpdateData(ActionBean requestAction) {
		String structureID = requestAction.getData().get(0).getVal();
		return new ActionBean("no action needed",
				generalUtil.StringToList(integrationEvent.chemDoodleCanvasUpdateData(structureID)), "");
	}

}
