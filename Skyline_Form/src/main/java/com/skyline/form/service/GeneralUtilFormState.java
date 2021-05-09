package com.skyline.form.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyline.form.dal.UploadFileDao;
import com.skyline.form.entity.Catalog;
import com.skyline.form.entity.CatalogDBInterface;
import com.skyline.form.entity.Element;
import com.skyline.form.entity.Entity;
import com.skyline.form.entity.Layout;

@Service
public class GeneralUtilFormState {

	@Autowired
	FormStateManager formStateManager;

	@Autowired
	public GeneralUtil generalUtil;

	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	@Autowired
	public GeneralUtilForm generalUtilForm;

	@Autowired
	public UploadFileDao obj;

	private static final Logger logger = LoggerFactory.getLogger(GeneralUtilFormState.class);

	/**
	 * replace the parameters in the value arg with values from the form param map.
	 * @param stateKey
	 * @param formCode
	 * @param value
	 * @param cleanParam - if false return the arg value without replacement at all in case one of the parameters not found, else (true) return empty string
	 * @return the arg value after form parameter replacement
	 * Note: if one of the parameters is not exists in the map the return value will be according the cleanParam value (use replaceFormParamSelectively for replace only the parameter that are exist in the form) 
	 */
	public String replaceFormParam(long stateKey, String formCode, String value, boolean cleanParam) {
		String toReturn = value;
		try {
			Pattern pattern = Pattern.compile("(\\$P\\{)(.*?)(\\})");
			Matcher matcher = pattern.matcher(value);
			while (matcher.find()) {
				//log before
				logger.debug("replaceFormParam before replace:" + value);

				String paramCode = matcher.group(0);
				String paramValue = "";
				if (paramCode.contains(".")) {
					String overrideParamValue = "";
					String overrideFormCode = paramCode.split("\\.")[0].replace("$P{", "");
					if (overrideFormCode.equalsIgnoreCase("PARENT_FORMCODE")) {
						overrideFormCode = formStateManager.getFormParam(stateKey, formCode, "$P{PARENT_FORMCODE}");
						overrideParamValue = paramCode.replace("PARENT_FORMCODE.", "");
					} else {
						overrideParamValue = paramCode.replace(overrideFormCode + ".", "");
					}
					try {
						paramValue = formStateManager.getFormParam(stateKey, overrideFormCode, overrideParamValue);
					} catch (Exception e) {
						return toReturn;
					}
				} else {
					paramValue = formStateManager.getFormParam(stateKey, formCode, paramCode);
				}

				if (paramValue == null) {
					if (cleanParam) {
						toReturn = "";
					} else {
						toReturn = value;
					}
					//log break
					logger.debug("replaceFormParam NO replace. return: " + toReturn);
					break;
				} else {
					toReturn = toReturn.replace(paramCode, paramValue);
				}
				//log after
				logger.debug("replaceFormParam after replace. return: " + toReturn);
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return toReturn;
	}

	/**
	 * replace the parameters in the value arg with values from the form param map.
	 * @param stateKey - tab context
	 * @param formCode
	 * @param value
	 * @return the arg value after form parameter replacment (for the parameter that exists in the form)
	 * Note: "Selectively" meaning: if some of the parameters aren't exist in the map, there string pattern will not be changed in the return value (the replacement is made only on the parameters exists in the map)
	 */
	public String replaceFormParamSelectively(long stateKey, String formCode, String value) {
		String toReturn = value;
		try {
			Pattern pattern = Pattern.compile("(\\$P\\{)(.*?)(\\})");
			Matcher matcher = pattern.matcher(value);
			while (matcher.find()) {
				//log before
				logger.debug("replaceFormParam before replace:" + value);

				String paramCode = matcher.group(0);
				String paramValue = "";
				try {
					if (paramCode.contains(".")) {
						String overrideParamValue = "";
						String overrideFormCode = paramCode.split("\\.")[0].replace("$P{", "");
						if (overrideFormCode.equalsIgnoreCase("PARENT_FORMCODE")) {
							overrideFormCode = formStateManager.getFormParam(stateKey, formCode, "$P{PARENT_FORMCODE}");
							overrideParamValue = paramCode.replace("PARENT_FORMCODE.", "");
						} else {
							overrideParamValue = paramCode.replace(overrideFormCode + ".", "");
						}
						paramValue = formStateManager.getFormParam(stateKey, overrideFormCode, overrideParamValue);
					} else {
						paramValue = formStateManager.getFormParam(stateKey, formCode, paramCode);
					}
				} catch (Exception e) {
					continue;
				}

				if (paramValue == null) {
					continue;
				} else {
					toReturn = toReturn.replace(paramCode, paramValue);
				}
				//log after
				logger.debug("replaceFormParam after replace. return: " + toReturn);
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return toReturn;
	}

	public boolean getBooleanByExpression(long stateKey, String fomrCode, String desginExpression) { //TODO yp handle "[" ???

		boolean toReturn = false;

		String desginExpressionArray[] = desginExpression.split(",", -1);
		for (String sExp : desginExpressionArray) {
			if (!generalUtil.getNull(sExp).equals("")
					&& (generalUtil.getNull(formStateManager.getFormValue(stateKey, fomrCode, sExp),
							sExp.toLowerCase().equals("true") ? "" : "false").equals(""))) {
				toReturn = true;
				break;
			}

		}

		return toReturn;
	}

	public String getFormCatalogDBSql(long stateKey, String formCode, String catalog, String isDistinct,
			String sourceElementImpCode, String dataType) {
		String sql_ = "";
		Entity eCat = (Entity) formStateManager.getFormBean(stateKey, formCode, catalog);
		if (eCat instanceof CatalogDBInterface) {
			sql_ = ((CatalogDBInterface) eCat).getSql(stateKey,
					getFormCatalog(stateKey, formCode, sourceElementImpCode), formStateManager.getFormTempDataMap(),
					isDistinct, dataType);
		}
		return sql_;
	}

	public Element getElementBean(long stateKey, String formCode, String elementName) {
		Element element = null;
		Entity entity = (Entity) formStateManager.getFormBean(stateKey, formCode, elementName);
		if (entity instanceof Element) {
			element = (Element) entity;
		}
		return element;
	}

	public String getFormCatalogItem(long stateKey, String formCode, String catalog, String item,
			String sourceElementImpCode, StringBuilder info) {
		String data_ = "";
		Entity eCat = (Entity) formStateManager.getFormBean(stateKey, formCode, catalog);
		if (eCat instanceof CatalogDBInterface) {
			data_ = ((Catalog) eCat).getItem(stateKey, getFormCatalog(stateKey, formCode, sourceElementImpCode),
					formStateManager.getFormTempDataMap(), item, info);
		}
		return data_;
	}

	public List<String> getFormCatalogItemList(long stateKey, String formCode, String catalog, String item,
			String sourceElementImpCode, StringBuilder info) {
		List<String> data_ = null;
		Entity eCat = (Entity) formStateManager.getFormBean(stateKey, formCode, catalog);
		if (eCat instanceof CatalogDBInterface) {
			data_ = ((Catalog) eCat).getItemArray(stateKey, getFormCatalog(stateKey, formCode, sourceElementImpCode),
					formStateManager.getFormTempDataMap(), item, info);
		}
		return data_;
	}

	public String getFormCatalogDBTable(long stateKey, String formCode, String catalog) {
		String tableName = "";
		Entity eCat = (Entity) formStateManager.getFormBean(stateKey, formCode, catalog);
		if (eCat instanceof CatalogDBInterface) {
			tableName = ((CatalogDBInterface) eCat).getTableName();
		}
		return tableName;
	}

	public String getLayoutJsp(long stateKey, String formCode, String layout) {
		String jspName = "";
		Layout eLayout = (Layout) formStateManager.getFormBean(stateKey, formCode, layout);
		jspName = eLayout.getJspName();
		return jspName;
	}

	public String getCurrentUserName(long stateKey, String formCode) {
		String userName = "";
		try {
			userName = generalUtil.removeSurroundedUpperComma(
					formStateManager.getFormParam(stateKey, formCode, "$P{USER_INFO_USERNAME}"));
		} catch (Exception e) {
			logger.warn("fail to get $P{NAME} in from parameters");
		}
		return userName;
	}

	public Map<String, String> getFormCatalog(long stateKey, String formCode, String sourceElementImpCode) {
		// TODO Auto-generated method stub
		return formStateManager.getFormCatalog(stateKey, formCode, generalUtil.getNull(sourceElementImpCode));
	}

	public void initForm(String formCode, String userId) {
		formStateManager.initForm(true, new HashMap<String, String>(), 0l, formCode, userId, "-1", "", "",
				new HashMap<String, String[]>(), new HashMap<String, String>());

	}

	public void initForm(boolean isNewFormId, Map<String, String> lastSaveValMap, long stateKey, String formCode,
			String userId, String formId, String nameId, String urlCallParam, Map<String, String[]> requestMap,
			Map<String, String> outParamMap) {
		formStateManager.initForm(isNewFormId, lastSaveValMap, stateKey, formCode, userId, formId, nameId, urlCallParam,
				requestMap, outParamMap);

	}

	public List<Element> getElementList(long stateKey, String formCode) {
		// TODO Auto-generated method stub
		return formStateManager.getElementList(stateKey, formCode);
	}

	public List<Layout> getLayoutList(long stateKey, String formCode) {
		// TODO Auto-generated method stub
		return formStateManager.getLayoutList(stateKey, formCode);
	}

	public void cleanFormCatalog(long stateKey, String formCode) {
		formStateManager.cleanFormCatalog(stateKey, formCode);

	}

	public void setFormValue(long stateKey, String formCode, Map<String, String> initElementValueMapByRequestAction) {
		formStateManager.setFormValue(stateKey, formCode, initElementValueMapByRequestAction);

	}

	public String getFormValue(long stateKey, String formCode, String impCode) {
		// TODO Auto-generated method stub
		return formStateManager.getFormValue(stateKey, formCode, impCode);
	}

	public void setFormValue(long stateKey, String formCode, String impCode, String lastsSelectedValue) {
		formStateManager.setFormValue(stateKey, formCode, impCode, lastsSelectedValue);

	}

	public void setFormCatalog(long stateKey, String formCode, Map<String, String> catalogItemFilterMapByInputVal) {
		formStateManager.setFormCatalog(stateKey, formCode, catalogItemFilterMapByInputVal);

	}

	public void setFormParam(long stateKey, String formCode, String key, String val) {
		formStateManager.setFormParam(stateKey, formCode, key, val);
	}

	public void setFormParam(long stateKey, String formCode, Map<String, String> map) {
		formStateManager.setFormParam(stateKey, formCode, map);
	}

	public String getSummary(long stateKey, String formCode, boolean showParam) {
		return formStateManager.getSummary(stateKey, formCode, showParam);

	}

	public String getFormId(long stateKey, String formCode) {
		return formStateManager.getFormId(stateKey, formCode);
	}

	public Map<Integer, List<Element>> getElementMapTree(long stateKey, String formCode) {
		return formStateManager.getElementMapTree(stateKey, formCode);
	}

	public int getCurrentLevelByElementCode(long stateKey, String formCode, String currentElementCode) {
		return formStateManager.getCurrentLevelByElementCode(stateKey, formCode, currentElementCode);
	}

	public boolean isParenteElement(long stateKey, String formCode, Element element, String currentElementCode) {
		return formStateManager.isParenteElement(stateKey, formCode, element, currentElementCode);
	}

	public String getStringContent(String ID, String formCode, String domID, String formID) {
		String str = obj.getStringContent(ID, formCode, domID, formID);
		return str;
	}

	public String getWebixContent(String ID, boolean isPln) {
		String str = obj.getWebixContent(ID, isPln);
		return str;
	}

	public String getRichTextContent(String ID) {
		return obj.getRichTextContent(ID);
	}
	
	public String getDiagramContent(String ID) {
		return obj.getDiagramContent(ID);
	}

	public Map<String, String> getFormParam(long stateKey, String formCode) {
		return formStateManager.getFormParam(stateKey, formCode);
	}

	public String getFormParam(long stateKey, String formCode, String key) {
		return (formStateManager.getFormParam(stateKey, formCode)).get(key);
	}

	public Element getAuthorizationElement(long stateKey, String formCode) {
		// TODO Auto-generated method stub
		return formStateManager.getAuthorizationElement(stateKey, formCode);
	}

	public String getFileName(String FILE_ID) {
		return obj.getFileName(FILE_ID);
	}

	public List<JSONArray> getWebixAnalytCalcData(String parentID, boolean isUpload) {
		return obj.getWebixAnalytCalcData(parentID, false);
	}

	public JSONArray getWebixFormulCalcData(String parentID) {
		return obj.getWebixFormulCalcData(parentID);
	}

	public JSONArray getWebixGeneralTableData(String sql) {
		return obj.getWebixGeneralTableData(sql);
	}

	public JSONArray getWebixExperimentStepCalcData(String parentID) {
		return obj.getWebixExperimentStepCalcData(parentID);
	}

	public void cleanForm(long stateKey, String formCode) {
		formStateManager.cleanForm(stateKey, formCode);

	}

	public Map<String, String> getFormValue(long stateKey, String formCode) {
		// TODO Auto-generated method stub
		return formStateManager.getFormValue(stateKey, formCode);
	}

	public String popFromBackNavigationStack(long stateKey, HttpServletRequest request, String formCode) {
		// TODO Auto-generated method stub
		return formStateManager.popFromBackNavigationStack(stateKey, request, formCode, "");
	}

	public String popFromBackNavigationStack(long stateKey, HttpServletRequest request, String formCode_doBack,
			String formCode_request) {
		// TODO Auto-generated method stub
		return formStateManager.popFromBackNavigationStack(stateKey, request, formCode_doBack, formCode_request);
	}

	public void pushIntoBackNavigationStack(long stateKey, String formId, String formCode, String tab, String info,
			String url, String userId) {
		// TODO Auto-generated method stub
		formStateManager.pushIntoBackNavigationStack(stateKey, formId, formCode, tab, info, url, userId);
	}

	public String restoreURLFromLastSavedBreadcrumb(String userID, long stateKey) {
		// TODO Auto-generated method stub
		return formStateManager.restoreURLFromLastSavedBreadcrumb(userID, stateKey);
	}

	public String getBreadCrumbHtml(long stateKey) {
		return generalUtil.getEmpty(formStateManager.getSessionAttr(stateKey, "breadcrumb"), "&nbsp");
	}

	public List<String> getFormCodeTransactionList(String formId, String formCode) {
		// TODO Auto-generated method stub
		return formStateManager.getFormCodeTransactionList(formId, formCode);
	}

	public boolean isOpenTransaction(String formId) {
		// TODO Auto-generated method stub
		return formStateManager.isOpenTransaction(formId);
	}

	public boolean openTransaction(long stateKey, String formCode, String userId, String formId) {
		return formStateManager.openTransaction(stateKey, formCode, userId, formId);
	}

	public String checkAndReturnSessionId(String formCode, String parentId) {
		// TODO Auto-generated method stub
		return formStateManager.checkAndReturnSessionId(formCode, parentId);
	}

	public String getWherePartForTmpData(String formCode, String parentId) {
		// TODO Auto-generated method stub
		return formStateManager.getWherePartForTmpData(formCode, parentId);
	}

	public String getWherePartForTmpDataByFormId(String formCode, String parentId) {
		// TODO Auto-generated method stub
		return formStateManager.getWherePartForTmpDataByFormId(formCode, parentId);
	}
	
	public String getSessionId(String formId) {
		// TODO Auto-generated method stub
		return formStateManager.getSessionId(formId);
	}

	public void closeTransaction(String formId) {
		formStateManager.closeTransaction(formId);
	}
	
	public byte[] getByteArrayFromBlobWrapper(String fileID) {
		return formStateManager.getByteArrayFromBlobWrapper(fileID);
	}
	
	public FormState getFormState(long stateKey, String formCode) {
		return formStateManager.getFormState(stateKey, formCode);
	}
}
