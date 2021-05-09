package com.skyline.form.controller;

import java.io.IOException;

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
import com.skyline.form.service.FormApiChemService;
import com.skyline.form.service.GeneralUtilLogger;

@Controller
@RequestMapping("/skylineForm")
public class FormApiChem {

	private static final Logger logger = LoggerFactory.getLogger(FormApiChem.class);

	@Autowired
	private FormApiChemService formApiChemService; 

	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	//"./chemDoodleReactionTabEvent.request"
	@RequestMapping(value = "/chemDoodleReactionTabEvent.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean chemDoodleReactionTabEvent(@RequestBody ActionBean actionBean)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		logger.info("chemDoodleReactionTabEvent call: /actionBean=" + actionBean);
		return formApiChemService.chemDoodleReactionTabEvent(actionBean);
	}

	//"./chemDoodleReactionTabUp.request"
	@RequestMapping(value = "/chemDoodleReactionTabUp.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean chemDoodleReactionTabUp(@RequestBody ActionBean actionBean)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		logger.info("chemDoodleReactionTabEvent call: /actionBean=" + actionBean);
		return formApiChemService.chemDoodleReactionTabUp(actionBean);
	}

	@RequestMapping(value = "/chemDoodleCanvasUpdateData.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean chemDoodleCanvasUpdateData(@RequestBody ActionBean actionBean) {
		logger.info("chemDoodleCanvasUpdateData call: /actionBean=" + actionBean);
		return formApiChemService.chemDoodleCanvasUpdateData(actionBean);
	}

}
