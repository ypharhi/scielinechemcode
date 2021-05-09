package com.skyline.form.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.dal.UploadFileDao;

@Service
public class FormApiService { // TODO interface FormService and this should be FormServiceImp (in case we want to switch between different behaviors/algorithm) 

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private GeneralUtilFormState generalUtilFormState;

	@Autowired
	private UploadFileDao uploadFileDao;

	@Autowired
	private IntegrationCalc integrationCalc;

	@Autowired
	private IntegrationEvent integrationEvent;

	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	public void getAttachment(Map<String, String> allRequestParams, HttpServletResponse response) {
		try {
			String FILE_ID = "";
			String _ContentDisposition = "attachment";
			for (Map.Entry<String, String> entry : allRequestParams.entrySet()) {
				if (entry.getKey().indexOf("FILE_ID") != -1) {
					FILE_ID = entry.getValue();

				} else if (entry.getKey().indexOf("_ContentDisposition") != -1) {
					_ContentDisposition = entry.getValue();
				}
			}

			if (generalUtil.getNull(FILE_ID).equals("")) {
				return;
			}

			OutputStream out = response.getOutputStream();
			StringBuilder originalFilename = new StringBuilder();
			InputStream inputStream = null;
			inputStream = uploadFileDao.getContent(FILE_ID, originalFilename);

			//response.setContentType(contentType.toString());		
			//response.setHeader("Content-Disposition", "attachment;filename=\"" + originalFilename.toString() + "\"");
			if (_ContentDisposition.equals("inline")) {
				boolean isDisplayyContent_ = uploadFileDao.isDisplayContent(FILE_ID);
				if (isDisplayyContent_) {
					originalFilename = new StringBuilder();
					inputStream = uploadFileDao.getContentDisplay(FILE_ID, originalFilename);
				}

				if (!((originalFilename.toString().toLowerCase().endsWith(".pdf"))
						|| (originalFilename.toString().toLowerCase().endsWith(".txt"))
						|| (originalFilename.toString().toLowerCase().endsWith(".jpg"))
						|| (originalFilename.toString().toLowerCase().endsWith(".png"))
						|| (originalFilename.toString().toLowerCase().endsWith(".bmp"))
						|| (originalFilename.toString().toLowerCase().endsWith("gif"))
						|| (originalFilename.toString().toLowerCase().endsWith(".jpeg")))) {
					return;
				}

			}

			response.setHeader("Content-Disposition", generalUtil.getEmpty(_ContentDisposition, "attachment")
					+ ";filename=\"" + originalFilename.toString() + "\"");
			IOUtils.copy(inputStream, out);

			out.flush();
			out.close();
			if (inputStream != null) {
				inputStream.close();
			}

		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
	}

	//	/**	 
	//	 * Get values from api 
	//	 * element 'ApiElementSetter' use this service
	//	 * @param requestAction
	//	 * @return ActionBean
	//	 */
	//	public ActionBean getValuesFromApi(ActionBean requestAction) {
	//		String value = requestAction.getData().get(0).getVal(); // casNumber
	//		String apiValues = requestAction.getData().get(1).getVal();	// the elements that we get from the API  (CSV)
	//		String elements = requestAction.getData().get(2).getVal();	//id of elements which defined in the form (CSV)	
	//		String[] apiValuesArray = apiValues.split(",");
	//		String[] elementsArray = elements.split(",");
	//		String dummy ="CAS name from API";		
	//		if(apiValuesArray[0].indexOf(".") == -1){
	//			return new ActionBean("no action needed", generalUtil.StringToList("-1"), "");
	//		}
	//		String API = apiValuesArray[0].substring(0, apiValuesArray[0].indexOf("."));
	//		StringBuilder sb = new StringBuilder();
	//		sb.append("{");
	//		for(int i=0;i<elementsArray.length-1;i++){
	//				sb.append("\""  +elementsArray[i] + "\":\"" + dummy + "\",");			
	//		}
	//		sb.append("\""  +elementsArray[elementsArray.length-1] + "\":\"" + dummy + "\"");
	//		sb.append("}");
	//		return new ActionBean("no action needed", generalUtil.StringToList(sb.toString()), "");
	//	}

	public ActionBean getValuesFromApi(List<DataBean> dataBeanList, String formCode, String formId, String userId) {
		// TODO Auto-generated method stub
		String mainArg = "";
		String mainArgCode = "";
		String[] apiCodesArray = null;
		String[] elementsMatchArray = null;
		Map<String, String> elementValueMap = new HashMap<String, String>();

		for (DataBean dataBean : dataBeanList) {
			BeanType beanType = dataBean.getType();

			switch (beanType) {
				case AJAX_API_MAINARG: {
					mainArg = dataBean.getVal();
				}
					break;
				case AJAX_API_MAINARGCODE: {
					mainArgCode = dataBean.getVal();
				}
					break;
				case AJAX_API_CODELIST: {
					apiCodesArray = dataBean.getVal().split(",");
				}
					break;
				case AJAX_API_MATCHELEMENTLIST: {
					elementsMatchArray = dataBean.getVal().split(",");
				}
					break;
				case AJAX_BEAN: {
					elementValueMap.put(dataBean.getCode(), generalUtil.getNull(dataBean.getVal()));
				}
					break;

				default:
					break;
			}
		}

		if (apiCodesArray != null && apiCodesArray.length > 1 && apiCodesArray[0].indexOf(".") == -1) {
			return new ActionBean("no action needed", generalUtil.StringToList("-1"), "");
		}
		String API = apiCodesArray[0].substring(0, apiCodesArray[0].indexOf("."));

		String jsonReturn = integrationCalc.doCalc(API, mainArgCode, mainArg, elementValueMap, apiCodesArray,
				elementsMatchArray, formCode, formId, userId);

		return new ActionBean("no action needed", generalUtil.StringToList(jsonReturn), "");
	}

	public ActionBean getFileStringContent(ActionBean requestAction) {
		String jsonObj = requestAction.getData().get(0).getVal();
		return new ActionBean("no action needed",
				generalUtil.StringToList(generalUtilFormState.getStringContent(jsonObj, "", "", "")), "");
	}

	public ActionBean generalEvent(long stateKey, List<DataBean> dataBeanList, String formCode, String formId,
			String userId, String isNew, String eventAction) {
		// TODO Auto-generated method stub
		String retVal = "";
		try {
			retVal = integrationEvent.generalEvent(stateKey, generalUtil.initElementValueMapByBeanList(dataBeanList), formCode,
					formId, userId, isNew, eventAction);
			return new ActionBean("no action needed", generalUtil.StringToList(retVal), "");
		} catch (Exception ex) {
			return new ActionBean("no action needed", generalUtil.StringToList(retVal), ex.getMessage());
		}
	}
}
