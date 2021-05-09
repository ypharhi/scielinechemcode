package com.skyline.form.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.FormEntity;
import com.skyline.form.bean.FormType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormResourceLookupDao;
import com.skyline.form.dal.GeneralDao;

@Service
public class GeneralUtilForm {

	@Autowired
	private FormDao formDao;

	@Autowired
	private FormResourceLookupDao formResourceLookupDao;

	@Autowired
	public GeneralUtil generalUtil;

	@Autowired
	public GeneralDao generalDao;

	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	@Autowired
	public GeneralUtilFormState generalUtilFormState;

	private static final Logger logger = LoggerFactory.getLogger(GeneralUtilForm.class);

	public String getJsonVal(long stateKey, String fromCode, JSONObject jsonObject, String id) {
		String value = "";
		try {
			value = generalUtil.getJsonValById(jsonObject, "", id);

			if (value.startsWith("[") && value.endsWith("]")) {
				value = (generalUtil.replaceLast(value.replaceFirst("\\[", ""), "]", "").replace(", ", ",")
						.replaceAll("\"", ""));
				while (value.endsWith(",")) {
					value = generalUtil.replaceLast(value, ",", "");
				}

				String[] valueArray = value.split(",", -1);
				value = "";
				for (String val_ : valueArray) {
					if (val_.contains("$P{")) {

						value += generalUtilFormState.replaceFormParam(stateKey, fromCode, val_, false) + ",";
					} else {
						value += val_ + ",";
					}
				}

				if (value.endsWith(",")) {
					value = value.substring(0, value.length() - 1);
				}
			} else if (value.contains("$P{")) {
				value = generalUtilFormState.replaceFormParam(stateKey, fromCode, value, false);
			}

			if (value.contains("_EnumDelimiter_")) {
				value = value.split("_EnumDelimiter_")[0];
			}

			if (value.contains("SELECT_SQL_")) {
				value = generalDao.selectSingleStringNoException(value.split("SELECT_SQL_")[1]);
			}
			//			value = generalUtilFormState.getReflactionString(fromCode, value, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			generalUtilLogger.logWrite(e);
			value = "";
		}
		return value;
	}
	
	public String getJsonValReplaceParamClean(long stateKey, String fromCode, JSONObject jsonObject, String id) {
		String value = "";
		try {
			value = generalUtil.getJsonValById(jsonObject, "", id);

			if (value.startsWith("[") && value.endsWith("]")) {
				value = (generalUtil.replaceLast(value.replaceFirst("\\[", ""), "]", "").replace(", ", ",")
						.replaceAll("\"", ""));
				while (value.endsWith(",")) {
					value = generalUtil.replaceLast(value, ",", "");
				}

				String[] valueArray = value.split(",", -1);
				value = "";
				for (String val_ : valueArray) {
					if (val_.contains("$P{")) {

						value += generalUtilFormState.replaceFormParam(stateKey, fromCode, val_, true) + ",";
					} else {
						value += val_ + ",";
					}
				}

				if (value.endsWith(",")) {
					value = value.substring(0, value.length() - 1);
				}
			} else if (value.contains("$P{")) {
				value = generalUtilFormState.replaceFormParam(stateKey, fromCode, value, true);
			}

			if (value.contains("_EnumDelimiter_")) {
				value = value.split("_EnumDelimiter_")[0];
			}

			if (value.contains("SELECT_SQL_")) {
				value = generalDao.selectSingleStringNoException(value.split("SELECT_SQL_")[1]);
			}
			//			value = generalUtilFormState.getReflactionString(fromCode, value, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			generalUtilLogger.logWrite(e);
			value = "";
		}
		return value;
	}

	public String getJsonValReplaceParamSelectively(long stateKey, String fromCode, JSONObject jo, String id) {
		String value = "";
		try {
			value = generalUtil.getJsonValById(jo, "", id);

			if (value.startsWith("[") && value.endsWith("]")) {
				value = (generalUtil.replaceLast(value.replaceFirst("\\[", ""), "]", "").replace(", ", ",")
						.replaceAll("\"", ""));
				while (value.endsWith(",")) {
					value = generalUtil.replaceLast(value, ",", "");
				}

				String[] valueArray = value.split(",", -1);
				value = "";
				for (String val_ : valueArray) {
					if (val_.contains("$P{")) {

						value += generalUtilFormState.replaceFormParamSelectively(stateKey, fromCode, val_) + ",";
					} else {
						value += val_ + ",";
					}
				}

				if (value.endsWith(",")) {
					value = value.substring(0, value.length() - 1);
				}
			} else if (value.contains("$P{")) {
				value = generalUtilFormState.replaceFormParamSelectively(stateKey, fromCode, value);
			}

			if (value.contains("_EnumDelimiter_")) {
				value = value.split("_EnumDelimiter_")[0];
			}
			//			value = generalUtilFormState.getReflactionString(fromCode, value, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			generalUtilLogger.logWrite(e);
			value = "";
		}
		return value;
	}

	public String getResourceByName(String resourceCode) {
		String resourceValue = "";
		List<DataBean> resourceList;
		try {
			resourceList = formResourceLookupDao.resourceLookUp("", resourceCode);
			for (DataBean DataBean : resourceList) {
				resourceValue = generalUtil.getNull(DataBean.getVal());
				break;
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return resourceValue;
	}

	public ActionBean getResourceCodeValueInfoByType(ActionBean requestAction) {
		List<DataBean> resourceList;
		List<String> StringList = new ArrayList<String>();
		try {
			resourceList = formResourceLookupDao.resourceLookUp(requestAction.getData().get(0).getVal(),
					requestAction.getData().get(0).getCode());

			for (DataBean dataBean : resourceList) {
				StringBuilder str = new StringBuilder();
				str.append(dataBean.getCode() + ";");
				str.append(generalUtil.getNull(dataBean.getVal()) + ";");
				str.append(dataBean.getInfo());
				StringList.add(str.toString());
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		JSONArray jsonList = new JSONArray(StringList);
		return new ActionBean("no action needed", generalUtil.StringToList(jsonList.toString()), "");
	}

	public ActionBean getResourceValueByType(ActionBean requestAction) {
		String resourceString = "";
		List<DataBean> resourceList;
		try {
			resourceList = formResourceLookupDao.resourceLookUp(requestAction.getData().get(1).getVal(),
					requestAction.getData().get(0).getVal());
			List<String> valueList = new ArrayList<String>();
			for (DataBean DataBean : resourceList) {
				valueList.add(DataBean.getVal());
			}
			if (requestAction.getData().get(1).getVal().equals("LAYOUT_ITEM_TEXT")) {
				valueList = elementFilter(valueList, requestAction.getData().get(0).getVal(), "layoutBookMarkItem",
						requestAction.getData().get(2).getVal());
			} else if (requestAction.getData().get(1).getVal().equals("ELEMENT_IMP_CODE")) {
				valueList.remove(requestAction.getData().get(2).getVal());
			}
			resourceString = (generalUtil.replaceLast(valueList.toString().replaceFirst("\\[", ""), "]", "")
					.replace(", ", ","));
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
			resourceString = "";
		}
		return new ActionBean("no action needed", generalUtil.StringToList(resourceString), "");
	}

	public String getFormTypeValues() {
		return generalUtil.replaceLast(Arrays.asList(FormType.values()).toString().replaceFirst("\\[", ""), "]", "")
				.replace(", ", ",");
	}

	private List<String> elementFilter(List<String> valueList, String formCode, String propertyToRemove,
			String elementCode) {
		List<FormEntity> formEntityList = formDao.getFormEntityInfoLookup(formCode, "Element");
		List<String> exist = new ArrayList<String>();
		try {
			for (FormEntity formEntity : formEntityList) {
				if (!formEntity.getEntityImpCode().equals(elementCode)) {
					JSONObject jsonObj = new JSONObject(formEntity.getEntityImpInit());
					if (jsonObj.has(propertyToRemove)) {
						exist.add(jsonObj.get(propertyToRemove).toString());
					} else {
						logger.warn("layoutBookMarkItem is missing in this element: " + formEntity.getEntityImpClass());
					}
				}
			}

		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		valueList.removeAll(exist);
		return valueList;
	}

	public boolean isStructFromByFormCode(String formCode) {
		boolean toReturn = false;
		FormType formType;
		try {
			List<Form> form = formDao.getFormInfoLookup(formCode, "%", true);
			String formTypeName = form.get(0).getFormType();
			formType = FormType.valueOf(formTypeName);
			toReturn = formType.getStructureForm();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
		}
		return toReturn;
	}

	public FormType getFromType(String formCode) {
		FormType toReturn = null;
		try {
			List<Form> form = formDao.getFormInfoLookup(formCode, "%", true);
			String formTypeName = form.get(0).getFormType();
			toReturn = FormType.valueOf(formTypeName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
		}
		return toReturn;
	}

	//	public String getFormNameValByFormId(String formCode, String id) {
	//		return formDao.getFormNameValByFormId(formCode, id);
	//	}

	public Map<String, String> getCurrrentIdInfo(String formId) {
		// TODO Auto-generated method stub
		return formDao.getFromInfoLookupAll("", LookupType.ID, formId);
	}

	public Map<String, String> getCurrrentNameInfo(String formCode, String name) {
		// TODO Auto-generated method stub
		return formDao.getFromInfoLookupAll(formCode, LookupType.NAME, name);
	}

	public List<Map<String, String>> getCurrrentNameInfoAllContainsName(String formCode, String name) { // TODO change name to getCurrrentNameInfoAllContainsName
		// TODO Auto-generated method stub
		return formDao.getFromInfoLookupAllContainsVal(formCode, LookupType.NAME, name);
	}

	public List<Map<String, String>> getCurrrentNameInfoAllContainsId(String formCode, String id) {
		// TODO Auto-generated method stub
		return formDao.getFromInfoLookupAllContainsVal(formCode, LookupType.ID, id);
	}

	public String getCurrrentIdSingleStringInfo(String formCode, String lookupVal, String elementName) {
		// TODO Auto-generated method stub
		return formDao.getFromInfoLookup(formCode, LookupType.ID, lookupVal, elementName);
	}
	
	public String getCurrrentINameSingleStringInfo(String formCode, String lookupVal, String elementName) {
		// TODO Auto-generated method stub
		return formDao.getFromInfoLookup(formCode, LookupType.NAME, lookupVal, elementName);
	}

	public Map<String, String> getCurrrentInfoById(String formCode, String id) {
		// TODO Auto-generated method stub
		return formDao.getFromInfoLookupAll(formCode, LookupType.ID, id);
	}

	/**
	 * 
	 * @param formCode
	 * @return list with pairs of <element name>,<uom name (NA if not set)>
	 */
	public List<String> getResultElementList(String formCode) {

		List<String> resultList = new ArrayList<String>(); // to return

		List<FormEntity> formEntityList = formDao.getFormEntityInfoLookup(formCode, "Element");
		List<String> tmpResultList_ = new ArrayList<String>(); // tmp list with result only
		Map<String, String> uomConfigMap = new HashMap<String, String>();

		//update tmp list and uomConfigMap with result element as key uom element as values
		for (FormEntity formEntity : formEntityList) {
			if (generalUtil.getJsonValById(formEntity.getEntityImpInit(), "resultData").equals("true")
					&& !formEntity.getEntityImpClass().equals("ElementUOMImp")) { //!formEntity.getEntityImpClass().equals("ElementUOMImp") to be on safe side
				tmpResultList_.add(formEntity.getEntityImpCode());
			} else if (formEntity.getEntityImpClass().equals("ElementUOMImp")) {
				uomConfigMap.put(generalUtil.getJsonValById(formEntity.getEntityImpInit(), "elementId"),
						formEntity.getEntityImpCode());
			}
		}

		//update final list with pairs of element name, uom name (NA if not found)
		for (String resultElement : tmpResultList_) {
			resultList.add(resultElement + "," + generalUtil.getNull(uomConfigMap.get(resultElement), "NA"));
		}

		return resultList;
	}

	/*
	 * 
	 */
	public String getDefaultValueOfElement(String formCode, String elementName) {
		List<FormEntity> formEntityList = formDao.getFormEntityInfoLookup(formCode, "Element");

		//update tmp list and uomConfigMap with result element as key uom element as values
		for (FormEntity formEntity : formEntityList) {
			if (!formEntity.getEntityImpCode().equals(elementName)) {
				continue;
			}
			if (formEntity.getEntityImpClass().equals("ElementUOMImp")) {
				String type = generalUtil.getJsonValById(formEntity.getEntityImpInit(), "uomTypeName");
				String uomName = generalUtil.getJsonValById(formEntity.getEntityImpInit(), "defaultValue");
				List<String> uomList = formDao.getFromInfoLookupElementData("UOM", LookupType.NAME, uomName, "ID");
				for (String uomId : uomList) {
					Map<String, String> uomData = formDao.getFromInfoLookupAll("UOM", LookupType.ID, uomId);
					if (uomData.get("UOMTYPENAME").equalsIgnoreCase(type)) {
						return uomId;
					}
				}
			}
			return generalUtil.getJsonValById(formEntity.getEntityImpInit(), "defaultValue");
		}
		return "";
	}

	public boolean isActiveForm(String formId, String formCode) {
		String sql = "select active from fg_s_" + formCode + "_all_v where formid = '" + formId + "'";
		String active = generalUtil.getNull(generalDao.selectSingleStringNoException(sql));
		if (active.equals("0")) {
			return false;
		}
		return true;
	}
	
	public List<String> getStepList() {
		String sql = "select STEP_NUMBER from FG_I_NUMSTEPDESIGN";
//		String active = generalDao.getListOfStringBySql(sql);
		List<String> result = generalDao.getListOfStringBySql(sql);
//		if (active.equals("0")) {
//			return result;
//		}
		return result;
	}
	
}
