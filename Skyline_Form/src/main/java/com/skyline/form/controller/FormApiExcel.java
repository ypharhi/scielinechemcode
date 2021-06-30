package com.skyline.form.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.service.FormApiExcelService;
import com.skyline.form.service.GeneralUtilLogger;

@Controller
@RequestMapping("/skylineForm")
public class FormApiExcel {

	private static final Logger logger = LoggerFactory.getLogger(FormApiExcel.class);

	@Autowired
	private FormApiExcelService formApiExcelService; 

	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	@RequestMapping(value = "/getExcelComponentList.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getExcelComponentList(@RequestBody ActionBean actionBean, HttpServletRequest request) {
		logger.info("getExcelComponentList call: /actionBean=" + actionBean);
		String formId = request.getParameter("formId");
		return formApiExcelService.getExcelComponentList(formId,actionBean);
	}
}
