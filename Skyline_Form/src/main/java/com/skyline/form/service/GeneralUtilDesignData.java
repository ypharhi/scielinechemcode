package com.skyline.form.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.skyline.form.dal.GeneralDao;


@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GeneralUtilDesignData implements Serializable {

	
	@Autowired
	private GeneralDao generalDao;

	@Autowired
	private GeneralUtil generalUtil;

	
	private Map<String, List<String>> stepDesignMapwithList;
	private Map<String, List<String>> stepImpuritiesDesignMap;
	private List<String> experimentDataList;
	private Map<String,String> additionalInfo;
	private Map<String,String> designFormElementValueMap;
	private Map<String, List<String>> parametersDataMap;
	
	
	
	public Map<String, String> getDesignFormElementValueMap() {
		return designFormElementValueMap;
	}

	public void setDesignFormElementValueMap(Map<String, String> designFormElementValueMap) {
		this.designFormElementValueMap = designFormElementValueMap;
	}
	public Map<String, String> getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public List<String> getExperimentDataList() {
		return experimentDataList;
	}

	public void setExperimentDataList(List<String> experimentDataList) {
		this.experimentDataList = experimentDataList;
	}

	public Map<String, List<String>> getParametersDataMap() {
		return parametersDataMap;
	}

	public void setParametersDataMap(Map<String, List<String>> parametersDataMap) {
		this.parametersDataMap = parametersDataMap;
	}

	
	
	public Map<String, List<String>> getStepDesignMapwithList() {
		return stepDesignMapwithList;
	}

	public void setStepDesignMapwithList(Map<String, List<String>> stepDesignMapwithList) {
		this.stepDesignMapwithList = stepDesignMapwithList;
	}

	//private static final Logger logger = LoggerFactory.getLogger(class);

	public GeneralUtilDesignData() {
		stepDesignMapwithList = new HashMap<String, List<String>>();
		stepImpuritiesDesignMap = new HashMap<String, List<String>>();
		experimentDataList=new ArrayList<String>();
		parametersDataMap=new HashMap<String, List<String>>();
		additionalInfo=new HashMap<String,String>();
		designFormElementValueMap=new HashMap<String,String>();
	}

	public void addStepListDesignToMap(List<String> stepDesign, String stepId) {
		this.stepDesignMapwithList.put(stepId, stepDesign);

	}

	public List<String> getSpesificListStepDesignToMap(String stepId) {
		return this.stepDesignMapwithList.get(stepId);
	}

	public Map<String, List<String>> getStepImpuritesDesignMap() {
		return stepImpuritiesDesignMap;
	}

	public void setStepImpuritesDesignMap(Map<String, List<String>> stepImpuritiesDesignMap) {
		this.stepImpuritiesDesignMap = stepImpuritiesDesignMap;
	}
	public void addImpuritiesListToMap(List<String> impuritiesList, String stepId) {
		this.stepImpuritiesDesignMap.put(stepId, impuritiesList);

	}
	
	public void clearImpuritiesDesignForStep(String stepId){
		if(this.stepImpuritiesDesignMap.containsKey(stepId)){
			this.stepImpuritiesDesignMap.remove(stepId);
		}
	}

	public List<String> getSpesificImpuritiesListToMap(String stepId) {
		return this.stepImpuritiesDesignMap.get(stepId);
	}
	public void addExperimentDataToList(String expData) {
		this.experimentDataList.add(expData);

	}
	public void addAdditionalInfo(String key,String val) {
		this.additionalInfo.put(key,val);

	}
	public void fillDesignDataSession(String designId) {
		//Collect data from design form 
                clearSession();
				//expDesignCsv -experiment data-;TODO change this code to query
				String sql = "select *  from Fg_s_Reportdesignexp_All_v t where t.formId='"
						+ designId + "'";
				Map<String, String> expDesignMap = new HashMap<String, String>();
				expDesignMap = generalDao.sqlToHashMap(sql);
				StringBuilder expDesignCsv = new StringBuilder();
				
				for (Map.Entry<String, String> m : expDesignMap.entrySet()) {
					if (!(m.getKey().toLowerCase().contains("step")) && (m.getValue().equals("1"))) {
						addExperimentDataToList(m.getKey());
					}
					else if(m.getValue()==null||!m.getValue().equals("0")&&!m.getValue().equals("1")&&!m.getKey().equals("parametersDesign")&&!m.getKey().equals("stepsDesign"))
					{
						addAdditionalInfo(m.getKey(), m.getValue());
					}
				}
				getAdditionalInfo();
				getExperimentDataList();


				//stepsDesignMap-steps data
				sql = "select STEPSDESIGN from Fg_s_Reportdesignexp_All_v t where t.formId="
						+ designId;
				String stepDesignId = generalDao.selectSingleString(sql);
				//Map<String, List<String>> stepsDesignMap = new HashMap<String, List<String>>();
				setStepDesignMapwithList(generalUtil.jsonStringToMapList(generalDao.selectSingleString(
						"select t.file_content from fg_clob_files t where t.file_id = '" + stepDesignId + "'")));
				
				getStepDesignMapwithList();

		       //impuritiesDesignMap-impurities data
				sql = "select IMPURITIESSTEPSDESIGN from Fg_s_Reportdesignexp_All_v t where t.formId="
						+ designId;
				String impuritiesDesignSteps = generalDao.selectSingleString(sql);
				//Map<String, List<String>> impuritiesDesignStepMap = new HashMap<String, List<String>>();
				if (impuritiesDesignSteps != null)
					setStepImpuritesDesignMap(generalUtil.jsonStringToMapList(impuritiesDesignSteps));

				
				getStepImpuritesDesignMap();
		        //impuritiesConcentrationDesignMap-impurities Concentration data
			/*	Map<String, String> maxImpuritiesMap = new HashMap<String, String>();
				List<String> maxImpuritiesList = getImpuritiesByMaxConcentration(elementValueMap.get("experimentList"));
						for (String impurity : maxImpuritiesList) {
					String imp = maxImpuritiesMap.get(impurity.split(":")[0]);
					if (imp == null)
						maxImpuritiesMap.put(impurity.split(":")[0], impurity.split(":")[1]);
					else
						maxImpuritiesMap.put(impurity.split(":")[0],
								maxImpuritiesMap.get(impurity.split(":")[0]) + ";" + impurity.split(":")[1]);

				}
	*/
		        //parametersDesignMap-parameters data
				sql = "select PARAMETERSDESIGN from Fg_s_Reportdesignexp_All_v t where t.formId="
						+ designId;
				
				String parametersDesignId = generalDao.selectSingleString(sql);
				//Map<String, List<String>> parametersDesignMap = new HashMap<String, List<String>>();
				String parameterDesignsString = "";
				if (parametersDesignId != null)
					parameterDesignsString = generalDao.selectSingleString(
							"select t.file_content from fg_clob_files t where t.file_id = '" + parametersDesignId + "'");
				if (!parameterDesignsString.equals("{}") && !parameterDesignsString.isEmpty())
					setParametersDataMap(generalUtil.jsonArrayStringToMapWithList(parameterDesignsString));
				getParametersDataMap();
				
				


	}
	public void clearSession()
	{
		getExperimentDataList().clear();
		getParametersDataMap().clear();
		getStepDesignMapwithList().clear();
		getStepImpuritesDesignMap().clear();
		getAdditionalInfo().clear();
	}
	
}
