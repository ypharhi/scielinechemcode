package com.skyline.form.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.skyline.form.service.FormApiElementsGeneralService;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;

@Controller
@RequestMapping("/skylineForm")
public class FormApiElementsGeneralCode {

	@Autowired
	private FormApiElementsGeneralService formApiElementsGeneralService;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	public GeneralUtilLogger generalUtilLogger;
	
	private static final Logger logger = LoggerFactory.getLogger(FormApiElementsGeneralCode.class);

	@RequestMapping(value = "/getStabValuesFromApi.request", method = { RequestMethod.GET, RequestMethod.POST })
	public void getStabValuesFromApi(/*@RequestParam Map<String, String> allRequestParams, */HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.info("getStabValuesFromApi call");

		int userId = generalUtil.getUserId(request);
		String product = request.getParameter("ajProduct");
		//        String spIdList = request.getParameter("ajSpIdList");
		if (product != null) {
			formApiElementsGeneralService.getStabValuesFromApi(userId, /*allRequestParams*/product, response);
		}
	}
}
