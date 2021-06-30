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
public class FormApiExcelService {

	private static final Logger logger = LoggerFactory.getLogger(FormApiExcelService.class);

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private IntegrationEvent integrationEvent;

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
}
