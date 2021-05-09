package com.skyline.form.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.DataBean;
import com.skyline.form.service.FormApiElementsService;
import com.skyline.form.service.FormApiService;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;

@Controller
@RequestMapping("/skylineForm")
public class FormApi {

	private static final Logger logger = LoggerFactory.getLogger(FormController.class);

	@Autowired
	private FormApiService formApiService;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	@Autowired
	private FormApiElementsService formApiElementsService;

	@RequestMapping(value = "/getAttachment.request", method = { RequestMethod.GET, RequestMethod.POST })
	public void getAttachment(@RequestParam Map<String, String> allRequestParams, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("getAttachment call");
		formApiService.getAttachment(allRequestParams, response);
	}

	@RequestMapping(value = "/getValuesFromApi.request", method = { RequestMethod.GET, RequestMethod.POST }) //TODO change name
	public @ResponseBody ActionBean getValuesFromApi(@RequestBody ActionBean actionBean, HttpServletRequest request) {

		logger.info("getValuesFromApi call: /actionBean=" + actionBean);

		String action = actionBean.getAction();
		String formId = request.getParameter("formId");
		String formCode = request.getParameter("formCode");
		String userId = request.getParameter("userId");

		List<DataBean> dataBeanList = actionBean.getData();

		return formApiService.getValuesFromApi(dataBeanList, formCode, formId, userId);
	}
 
	@RequestMapping(value = "/getFileStringContent.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getFileStringContent(@RequestBody ActionBean actionBean) {
		logger.info("getFileStringContent call: /actionBean=" + actionBean);
		return formApiService.getFileStringContent(actionBean);
	}

	@RequestMapping(value = "/generalEvent.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean doGeneralEvent(@RequestBody ActionBean actionBean, HttpServletRequest request) {

		String formId = request.getParameter("formId");
		String formCode = request.getParameter("formCode");
		String userId = request.getParameter("userId");
		String eventAction = request.getParameter("eventAction");
		String isNew = generalUtil.getNull(request.getParameter("isNew"));
		
		logger.info("generalEvent call: eventAction=" + eventAction + ", formid=" + formId + ", formCode=" + formCode + ", userId=" + userId + ", isNew=" + isNew + " /actionBean=" + actionBean);

		ActionBean doGeneralEventResult = null;
		List<DataBean> dataBeanList = actionBean.getData();

		if (generalUtil.getNull(eventAction).equals("ECHO")) {
			return new ActionBean("no action needed", generalUtil.StringToList("ECHO"), "");
		}
		long stateKey = generalUtil.getNullLong(request.getParameter("stateKey")); //TODO key check
		doGeneralEventResult = formApiService.generalEvent(stateKey, dataBeanList, formCode, formId, userId, isNew,
				eventAction);

		return doGeneralEventResult;
	}
	
	@RequestMapping(value = "/outPutLabel.request", method = { RequestMethod.GET, RequestMethod.POST })
	public void outPutLabel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("outPutLabel call");
		String impCode = "";
		String labelData = "";
		
		if(request.getParameter("_labelCode") != null && request.getParameter("_labelData") != null) {
			impCode = request.getParameter("_labelCode");
			labelData = request.getParameter("_labelData");
		}
		String formCode = "sample";
		String fileName = "rqrCodeLabel4callFromList"; //TODO: by impCode
				
		String printTemplate = "";
		String reportType = "PDF";
		String catalog = "";
		String catalogadditionaldata = "";
		String isDistinct = "False";
		String title ="QR Title"; //TODO: change
		String subtitle = "";
		String subReportFileList = "";
		String subReportCatalogList = "";
		String displayValuesObj = labelData;
		String displayUrl = request.getParameter("url");
		
	displayValuesObj = new String(displayValuesObj.getBytes("ISO-8859-1"), "UTF-8");//decode the displayed values

	logger.info("outPutLabel call formCode=" + formCode + ", impCode=" + impCode + ", fileName=" + fileName
			+ ", printTemplate=" + printTemplate + ", reportType=" + reportType + ", catalog=" + catalog
			+ ", catalogadditionaldata=" + catalogadditionaldata + ", isDistinct=" + isDistinct + ", title=" + title
			+ ", subtitle=" + subtitle + ", irSubReportFileList=" + subReportFileList + ", irsubReportCatalogList="
			+ subReportCatalogList + ", displayUrl=" + displayUrl);

	formApiElementsService.renderIreportSample(formCode, impCode, fileName + ".xml",
			printTemplate, reportType, catalog, catalogadditionaldata, isDistinct, title, subtitle,
			subReportFileList, subReportCatalogList, displayUrl, displayValuesObj, request.getSession().getId(),
			response);
	return;
	}
}
