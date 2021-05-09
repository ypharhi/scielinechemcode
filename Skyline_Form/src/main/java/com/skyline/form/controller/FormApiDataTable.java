package com.skyline.form.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.skyline.form.service.FormApiDataTableService;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;

@Controller
@RequestMapping("/skylineForm")
public class FormApiDataTable {
	
	@Autowired
	private FormApiDataTableService formApiDataTableService;
	
	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	public GeneralUtilLogger generalUtilLogger;
	
	private static final Logger logger = LoggerFactory.getLogger(FormApiDataTable.class);
 
	//yk
		@RequestMapping(value = "/onLevelSelectedChange.request", method = { RequestMethod.GET, RequestMethod.POST })
		public @ResponseBody ActionBean onLevelSelectedChange(@RequestBody ActionBean actionBean) {
			logger.info("onLevelSelectedChange call: /actionBean=" + actionBean);
			return formApiDataTableService.onLevelSelectedChange(actionBean);
		}

		@RequestMapping(value = "/onElementDataTableApiChange.request", method = { RequestMethod.GET, RequestMethod.POST })
		public @ResponseBody ActionBean onElementDataTableApiChange(@RequestBody ActionBean actionBean) {
			logger.info("onElementDataTableApiChange call: /actionBean=" + actionBean);
			return formApiDataTableService.onElementDataTableApiChange(actionBean);
		}
		
		@RequestMapping(value = "/deleteRowElementDataTableApiImp.request", method = { RequestMethod.GET,
				RequestMethod.POST })
		public @ResponseBody ActionBean deleteRowElementDataTableApiImp(@RequestBody ActionBean actionBean) {
			logger.info("deleteRowElementDataTableApiImp call: /actionBean=" + actionBean);
			return formApiDataTableService.deleteRowElementDataTableApiImp(actionBean);
		}

		@RequestMapping(value = "/confirmDeleteRowElementDataTableApiImp.request", method = { RequestMethod.GET,
				RequestMethod.POST })
		public @ResponseBody ActionBean confirmDeleteRowElementDataTableApiImp(@RequestBody ActionBean actionBean) {
			logger.info("confirmDeleteRowElementDataTableApiImp call: /actionBean=" + actionBean);
			return formApiDataTableService.confirmDeleteRowElementDataTableApiImp(actionBean);
		}
		
		@RequestMapping(value = "/dataTableAddRow.request", method = { RequestMethod.GET, RequestMethod.POST })
		public @ResponseBody ActionBean dataTableAddRow(@RequestBody ActionBean actionBean, HttpServletRequest request) {

			logger.info("dataTableAddRow call: /actionBean=" + actionBean);
			long stateKey = generalUtil.getNullLong(request.getParameter("stateKey")); //TODO key check		 
			List<DataBean> dataBeanList = actionBean.getData();
			return formApiDataTableService.dataTableAddRow(stateKey, dataBeanList, actionBean);
		}

		@RequestMapping(value = "/onChangeDataTableCell.request", method = { RequestMethod.GET, RequestMethod.POST })
		public @ResponseBody ActionBean onChangeDataTableCell(@RequestBody ActionBean actionBean,
				HttpServletRequest request) {

			logger.info("onChangeDataTableCell call: /actionBean=" + actionBean);
			//TODO key check
			long stateKey = generalUtil.getNullLong(request.getParameter("stateKey"));
			return formApiDataTableService.onChangeDataTableCell(stateKey, actionBean);
		}
		

		@RequestMapping(value = "/getRichTextContent.request", method = { RequestMethod.GET, RequestMethod.POST })
		public @ResponseBody ActionBean getRichTextContent(@RequestParam("formId") String formId,
				@RequestParam("formCode") String formCode, @RequestParam("dbColName") String dbColName) {
			logger.info("getRichTextContent call");
			return formApiDataTableService.getRichTextContent(formId, formCode, dbColName);
		}
}
