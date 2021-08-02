package com.skyline.form.controller;

import java.net.URLDecoder;
import java.util.LinkedHashMap;
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
import com.skyline.form.bean.FormSaveTaskInfo;
import com.skyline.form.service.FormApiElementsService;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;

@Controller
@RequestMapping("/skylineForm")
public class FormApiElements {

	private static final Logger logger = LoggerFactory.getLogger(FormController.class);

	@Autowired
	private FormApiElementsService formApiElementsService;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	public GeneralUtilLogger generalUtilLogger;
	
	@RequestMapping(value = "/ireport.request", method = { RequestMethod.GET, RequestMethod.POST })
	public void doRenderIreport(@RequestParam("irFormCode") String formCode, @RequestParam("irImpCode") String impCode,
			@RequestParam("irFileName") String fileName, @RequestParam("irPrintTemplate") String printTemplate,
			@RequestParam("irReportType") String reportType, @RequestParam("irCatalog") String catalog,
			@RequestParam("irCatalogadditionaldata") String catalogadditionaldata,
			@RequestParam("irIsDistinct") String isDistinct, @RequestParam("irTitle") String title,
			@RequestParam("irSubtitle") String subtitle, @RequestParam("irSubReportFileList") String subReportFileList,
			@RequestParam("irSubReportCatalogList") String subReportCatalogList,
			@RequestParam("irElementsDisplayValues") String displayValuesObj, @RequestParam("irUrl") String displayUrl,
			@RequestParam("irStateKey") String stateKey, //TODO key CHECK
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		displayValuesObj = new String(displayValuesObj.getBytes("ISO-8859-1"), "UTF-8");//decode the displayed values

		logger.info("doRenderIreport call formCode=" + formCode + ", impCode=" + impCode + ", fileName=" + fileName
				+ ", printTemplate=" + printTemplate + ", reportType=" + reportType + ", catalog=" + catalog
				+ ", catalogadditionaldata=" + catalogadditionaldata + ", isDistinct=" + isDistinct + ", title=" + title
				+ ", subtitle=" + subtitle + ", irSubReportFileList=" + subReportFileList + ", irsubReportCatalogList="
				+ subReportCatalogList + ", displayUrl=" + displayUrl + ",stateKey=" + stateKey);

		Map<String, String> displayValuesMap = new LinkedHashMap<String, String>();
		displayValuesMap = generalUtil.stringToLnkHashMap("IREPORT_PRINT_", displayValuesObj, true);//"val":null
		formApiElementsService.renderIreport(generalUtil.getNullLong(stateKey), formCode, impCode, fileName + ".xml",
				printTemplate, reportType, catalog, catalogadditionaldata, isDistinct, title, subtitle,
				subReportFileList, subReportCatalogList, displayUrl, displayValuesMap, request.getSession().getId(),
				response);
		return;
	}

	@RequestMapping(value = "/getTreeRoot.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String getTreeRoot(@RequestParam("id") String id, @RequestParam("view") String view) {
		logger.info("getTreeRoot call: /id:" + id);
		return formApiElementsService.getTreeRoot(view);
	}
	
	@RequestMapping(value = "/getSpecificTreeRoot.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String getSpecificTreeRoot(@RequestParam("id") String id, @RequestParam("view") String view,  @RequestParam("metaData") String metaData,  @RequestParam("wherePart") String wherePart
			,  @RequestParam("stateKey") Long stateKey,  @RequestParam("formId") String parentFormId) {
		logger.info("getTreeRoot call: /id:" + id);
		return formApiElementsService.getSpecificTreeRoot(metaData,view,wherePart,parentFormId,stateKey);
	}
	
	/**
	 * 
	 * @param id - id of the chosen struct
	 * @param column - next struct
	 * @param view - db view
	 * @param parents - csv of parents id
	 * @param structs csv of parent structs
	 * @param currentstruct - value of chosen struct
	 * @return
	 */
	@RequestMapping(value = "/getTreeChildren.request", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "text/html; charset=UTF-8")
	public @ResponseBody String getTreeChildren(@RequestParam("id") String id, @RequestParam("column") String column,
			@RequestParam("view") String view, @RequestParam("parents") String parents,
			@RequestParam("structs") String structs, @RequestParam("currentstruct") String currentstruct,
			@RequestParam("formId") String currentformId, @RequestParam("stateKey") Long stateKey,
			@RequestParam("criteria") String criteria) {
		try {
			id = URLDecoder.decode(id, "UTF-8");
			structs = URLDecoder.decode(structs, "UTF-8");
			parents = URLDecoder.decode(parents, "UTF-8");
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			id = "-1";
		}
		logger.info("getTreeChildren call: /id:" + id + ", parents=" + parents);
		return formApiElementsService.getTreeChildren(column, view, id, parents, structs, currentstruct, currentformId, stateKey, criteria);
	}
	
	@RequestMapping(value = "/getTreeSearchResult.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getTreeSearchResult(@RequestBody ActionBean actionBean, HttpServletRequest request) {

		logger.info("getTreeSearchResult call");
//		String formId = request.getParameter("formId");
//		String formCode = request.getParameter("formCode");
		String userId = request.getParameter("userId");
		long stateKey = generalUtil.getNullLong(request.getParameter("stateKey"));
		 
		List<DataBean> dataBeanList = actionBean.getData();
		String searchText = dataBeanList.get(0).getVal();
		String searchCriteria = dataBeanList.get(1).getVal();
		
		String result = "";
		String error = "";
		try {
			result = formApiElementsService.getTreeSearchResult(searchText, searchCriteria, userId, stateKey);
		} catch (Exception e) {
			error= e.getMessage();
		}

		return new ActionBean("no action needed", generalUtil.StringToList(result), error);
	}

	@RequestMapping(value = "/onElementWebixAnalytCalcUpload.request", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody ActionBean onElementWebixAnalytCalcUpload(@RequestParam("formId") String formId,
			@RequestParam("domId") String domId) {
		logger.info("onElementWebixAnalytCalcUpload call");
		String toReturn = formApiElementsService.getWebixAnalytCalcUpdatedData(formId, domId);
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}

	@RequestMapping(value = "/onElementWebixFormulCalcUpload.request", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody ActionBean onElementWebixFormulCalcUpload(@RequestParam("formId") String formId,
			@RequestParam("domId") String domId) {
		logger.info("onElementWebixFormulCalcUpload call");
		String toReturn = formApiElementsService.getWebixCalcUpdatedData(formId, domId, "formulation");
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}

	@RequestMapping(value = "/onElementWebixExpStepCalcUpload.request", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody ActionBean onElementWebixExpStepCalcUpload(@RequestParam("formId") String formId,
			@RequestParam("domId") String domId) {
		logger.info("onElementWebixExpStepCalcUpload call");
		String toReturn = formApiElementsService.getWebixCalcUpdatedData(formId, domId, "experimentStep");
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}
	
	@RequestMapping(value = "/onElementWebixMassBalanceCalcResUpload.request", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody ActionBean onElementWebixMassBalanceCalcResUpload(@RequestParam("formId") String formId,
			@RequestParam("domId") String domId, @RequestParam("sampleId") String sampleId) {
		logger.info("onElementWebixMassBalanceCalcResUpload call");
		String toReturn = formApiElementsService.getWebixCalcUpdatedData(formId, domId, "massBalanceResult",sampleId);
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}

	@RequestMapping(value = "/onElementWebixMassBalanceCalcUpdate.request", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody ActionBean onElementWebixMassBalanceCalcUpdate(@RequestParam("sampleID") String sampleID,
			@RequestParam("domId") String domId) {
		logger.info("onElementWebixMassBalanceCalcUpdate call");
		String toReturn = formApiElementsService.getWebixCalcUpdatedData(sampleID, domId, "massBalance");
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}

	@RequestMapping(value = "/updateWebixMassBalanceSamplesList.request", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody ActionBean updateWebixMassBalanceSamplesList(@RequestParam("samplesScope") String samplesScope,
			@RequestParam("parentID") String parentID,@RequestParam("runNumber") String runNumber) {
		logger.info("updateWebixMassBalanceSamplesList call");
		String toReturn = formApiElementsService.updateWebixMassBalanceSamplesList(samplesScope, parentID,runNumber);
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}
	
	@RequestMapping(value = "/getTooltipForWebixMassBalanceSamplesField.request", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody ActionBean getTooltipForWebixMassBalanceSamplesField(@RequestParam("sampleID") String sampleID
			) {
		logger.info("getTooltipForWebixMassBalanceSamplesField call");
		String toReturn = formApiElementsService.getTooltipForWebixMassBalanceSamplesField(sampleID);
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}
	
	@RequestMapping(value = "/updateWebixResultTypeList.request", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody ActionBean updateWebixResultTypeList(@RequestBody ActionBean actionBean) {
		logger.info("xxxx call");
		List<DataBean> dataBeanList = actionBean.getData();
		String toReturn = formApiElementsService.updateWebixResultTypeList(dataBeanList);
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}
	
	@RequestMapping(value = "/getWebixMassBalanceStepsInfo.request", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody ActionBean getWebixMassBalanceStepsInfo(@RequestParam("formId") String formId, @RequestParam("domId") String domId, @RequestParam("runNumber") String runNumber) {
		logger.info("getWebixMassBalanceStepsInfo call");
		String toReturn = formApiElementsService.getWebixMassBalanceStepsInfoData(formId, domId, "Experiment",runNumber);
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}

	@RequestMapping(value = "/updateElementBody.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean updateElementBody(@RequestParam("elementName") String elementName,@RequestParam("value") String value, HttpServletRequest request) {
		logger.info("updateElementBody call");

		String formId = request.getParameter("formId");
		String formCode = request.getParameter("formCode");

		ActionBean updateElementBodyResult = null;
		
		long stateKey = generalUtil.getNullLong(request.getParameter("stateKey")); //TODO key check
		updateElementBodyResult = formApiElementsService.updateElementBody(stateKey, formCode, formId, elementName,value);

		return updateElementBodyResult;
	}
}
