package com.skyline.customer.adama;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.LookupType;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.dal.UploadFileDao;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;

@Service
public class FormIdCalc {

	@Autowired
	private FormDao formDao;

	//	@Autowired
	//	private FormService formService;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	protected UploadFileDao uploadFileDao;

	@Autowired
	protected GeneralUtilLogger generalUtilLogger;

	@Value("${firstChemistryProjectNum:8000}")
	private String firstChemistryProjectNum;

	@Value("${firstFromulationProjectNum:2500}")
	private String firstFromulationProjectNum;

	private final String projectIdFormat = "%04d";
	private final String subProjectIdFormat = "%02d";
	private final String subSubProjectIdFormat = "%02d";
	private final String experimemntIdFormat = "%02d";
	private final String idDelimiter = "-";
	private final String invItemBatchIdFormat = "%04d";
	private final String invItemMaintCalIdFormat = "%03d";
	private final String actionIdFormat = "%02d";
	private final String requestIdFormat = "%05d";
	private final String templateIdFormat = "%03d";
	private final String experimentGroupIdFormat = "%04d";

	/**
	 * 
	 * @param formCode
	 * @param formId
	 * @param elementValueMap
	 * @param userId
	 * @param pivotTable
	 * @return next formNumberId in case that currentFormNumberId is empty
	 */
	public String getNextFormNumberIdByFormId(String formCode, String formId, Map<String, String> elementValueMap,
			String userId, String pivotTable) {
		String currentNumberId = formDao.getFormNumberIdByFormId(pivotTable, formId);
		if (generalUtil.getNull(currentNumberId).equals("")) {
			String nextFormNumberId = getNextId(elementValueMap, formCode, formId, userId, pivotTable);
			return nextFormNumberId.equals("") ? null : nextFormNumberId;
		}
		return "";//returns empty string in case of existing formNumberId
	}

	public String getNextId(Map<String, String> elementValueMap, String formCode, String formId, String userId,
			String table) {
		String currentFormNumberId = "";
		String nextFormNumberId = "";
		String wherePart = "";
		String formatter = "";
		if (formCode.toLowerCase().equals("project".toLowerCase())) {
			formatter = projectIdFormat;
			String projectTypeId = generalUtil.getNull(elementValueMap.get("PROJECTTYPE_ID"));
			wherePart = " and PROJECTTYPE_ID = '" + projectTypeId + "'";
		} else if (formCode.toLowerCase().equals("subProject".toLowerCase())) {
			formatter = subProjectIdFormat;
			wherePart = " and projectNumber = '" + generalUtil.getNull(elementValueMap.get("projectNumber")) + "'";
		} else if (formCode.toLowerCase().equals("subSubProject".toLowerCase())) {
			formatter = subSubProjectIdFormat;
			wherePart = " and subprojectNumber = '" + generalUtil.getNull(elementValueMap.get("subProjectNumber"))
					+ "'";
		} else if (formCode.toLowerCase().equals("invItemBatch".toLowerCase())) {
			String formnumberId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, formId, "NAME");
			wherePart = " and SOURCE_ID = '" + elementValueMap.get("SOURCE_ID") + "' and substr(INVITEMBATCHNAME,-1) = "
					+ generalUtil.getEmpty(formnumberId, "B1").split("B")[1];
			formatter = invItemBatchIdFormat;
		} else if (formCode.toLowerCase().equals("invItemCalibration".toLowerCase())
				|| formCode.toLowerCase().equals("invItemMaintenance".toLowerCase())) {
			formatter = invItemMaintCalIdFormat;
			wherePart = " and INVITEMINSTRUMENT_ID = '"
					+ generalUtil.getNull(elementValueMap.get("INVITEMINSTRUMENT_ID")) + "'";
		} else if (formCode.equals("Action")) {
			formatter = actionIdFormat;
			wherePart = " and STEP_ID = '" + generalUtil.getNull(elementValueMap.get("STEP_ID")) + "'";
		} else if (formCode.toLowerCase().equals("Experiment".toLowerCase())) {
			formatter = projectIdFormat;
			wherePart = "and SUBPROJECT_ID = '" + generalUtil.getNull(elementValueMap.get("SUBPROJECT_ID")) + "'";// and LABORATORY_ID = '" + generalUtil.getNull(elementValueMap.get("LABORATORY_ID")) + "'";
		} else if (formCode.toLowerCase().equals("Request".toLowerCase())) {
			formatter = requestIdFormat;
			wherePart = "and SUBPROJECT_ID = '" + generalUtil.getNull(elementValueMap.get("SUBPROJECT_ID")) + "'";
		} else if (formCode.toLowerCase().equals("RequestMain".toLowerCase())) {
			formatter = requestIdFormat;
			wherePart = "and SUBPROJECT_ID = '" + generalUtil.getNull(elementValueMap.get("SUBPROJECT_ID")) + "'";
		} else if (formCode.toLowerCase().equals("Sample".toLowerCase())
				|| formCode.toLowerCase().equals("SampleMain".toLowerCase())) {
			formatter = experimemntIdFormat;
			wherePart = " and (EXPERIMENT_ID = '" + generalUtil.getNull(elementValueMap.get("EXPERIMENT_ID")) + "'"
					+" or batch_id in (select formid from fg_s_invitembatch_v where experiment_id =  '" + generalUtil.getNull(elementValueMap.get("EXPERIMENT_ID")) + "'))";//fix bug 9002
		} else if (formCode.toLowerCase().equals("BatchMain".toLowerCase())) {
			formatter = "%d";
			wherePart = " and EXPERIMENT_ID = '" + generalUtil.getNull(elementValueMap.get("EXPERIMENT_ID"))
					+ "' and SOURCE_ID = '" + elementValueMap.get("SOURCE_ID") + "'";
		} else if (formCode.toLowerCase().equals("PreperationRef".toLowerCase())) {
			formatter = actionIdFormat;
			wherePart = " and SAMPLE_ID = '" + generalUtil.getNull(elementValueMap.get("SAMPLE_ID")) + "'";
		}
		/***********************************************************************************************************************/
		else if (formCode.toLowerCase().equals("ExpSeriesMain".toLowerCase())) {
			formatter = requestIdFormat;
			wherePart = "and SUBPROJECT_ID = '" + generalUtil.getNull(elementValueMap.get("SUBPROJECT_ID")) + "'";
		} else if (formCode.toLowerCase().equals("Template".toLowerCase())) {
			formatter = templateIdFormat;
			wherePart = " and TEMPLATE_ID = '" + generalUtil.getNull(elementValueMap.get("TEMPLATE_ID")) + "'";
		} else if (formCode.toLowerCase().equals("ExperimentGroup".toLowerCase())) {
			formatter = experimentGroupIdFormat;
			//wherePart = " and TEMPLATE_ID = '" + generalUtil.getNull(elementValueMap.get("TEMPLATE_ID")) + "'";
		} else {
			return "NA";
		}

		if (formCode.endsWith("Project")) {
			currentFormNumberId = generalUtil.getEmpty(formDao.getCurrentFormNumberId(table, wherePart),
					getDefaultFormNumberId(formCode, table, elementValueMap));
		} else if (formCode.equals("InvItemBatch")) {
			currentFormNumberId = generalUtil.getEmpty(
					getCurrentBatchNumber(formCode, formId, table, "External", wherePart),
					getDefaultFormNumberId(formCode, table, elementValueMap));
		} else if (formCode.equals("InvItemCalibration") || formCode.equals("InvItemMaintenance")) {
			currentFormNumberId = generalUtil.getEmpty(formDao.getCurrentFormNumberId(table, wherePart),
					getDefaultFormNumberId(formCode, table, elementValueMap));
		} else if (formCode.equals("Action") || formCode.equals("PreperationRef")) {
			currentFormNumberId = generalUtil.getEmpty(formDao.getCurrentFormNumberId(table, wherePart),
					getDefaultFormNumberId(formCode, table, elementValueMap));
		} else if (formCode.equals("Experiment")) {
			currentFormNumberId = generalUtil.getEmpty(getCurrentExperimentNumber(table, elementValueMap, wherePart),
					getDefaultFormNumberId(formCode, table, elementValueMap));
		} else if (formCode.equals("Request")) {
			currentFormNumberId = generalUtil.getEmpty(formDao.getCurrentFormNumberId(table, wherePart),
					getDefaultFormNumberId(formCode, table, elementValueMap));
		} else if (formCode.equals("RequestMain")) {
			currentFormNumberId = generalUtil.getEmpty(formDao.getCurrentFormNumberId(table, wherePart),
					getDefaultFormNumberId(formCode, table, elementValueMap));
		} else if (formCode.equals("Sample") || formCode.equals("SampleMain")) {
			if (elementValueMap.get("parentId").equals(elementValueMap.get("BATCH_ID"))
					&& !elementValueMap.get("BATCH_ID").isEmpty()) {
				currentFormNumberId = getCurrentSampleNumFromBatch(formCode, formId, table,
						elementValueMap.get("BATCH_ID"));
			} else {
				currentFormNumberId = generalUtil.getEmpty(formDao.getCurrentFormNumberId(table, wherePart),
						getDefaultFormNumberId(formCode, table, elementValueMap));
			}
		} else if (formCode.equals("BatchMain")) {
			currentFormNumberId = generalUtil.getEmpty(
					getCurrentBatchNumber(formCode, formId, table, "Internal", wherePart),
					getDefaultFormNumberId(formCode, table, elementValueMap));
		} else if (formCode.equals("Template") || formCode.equals("TemplateMain")) {
			currentFormNumberId = generalUtil.getEmpty(formDao.getCurrentFormNumberId(table, wherePart),
					getDefaultFormNumberId(formCode, table, elementValueMap));

	    } else if (formCode.equals("ExperimentGroup") ) {
	    	 currentFormNumberId = generalUtil.getEmpty(generalDao.selectSingleString(" select max(substr(groupNumber,11,14)) from " + table
					+ " where groupNumber is not null and groupNumber like '____-____-____' " + wherePart),
	    			getDefaultFormNumberId(formCode, table, elementValueMap));
		  /* currentFormNumberId = generalUtil.getEmpty(generalDao.selectSingleString(" select max(groupNumber) from " + table
					+ " where groupNumber is not null and groupNumber not like '-%' and nvl(active,'1')='1'" + wherePart),*/
			
	    }
		String[] strArray = currentFormNumberId.split(idDelimiter);
		int numericId = Integer.parseInt(strArray[strArray.length - 1]);
		if (formCode.equals("InvItemBatch")) {
			if (numericId == 9999) {
				numericId = Integer.parseInt(strArray[strArray.length - 2]);
				if (numericId == 999) {
					numericId = Integer.parseInt(strArray[strArray.length - 3]);
					numericId += 1;
					strArray[strArray.length - 3] = String.format("%02d", numericId);
				} else {
					numericId += 1;
					strArray[strArray.length - 2] = String.format("%03d", numericId);
				}
			} else {
				numericId += 1;
				strArray[strArray.length - 1] = String.format(formatter, numericId);
			}
		} else {
			numericId += 1;
			strArray[strArray.length - 1] = String.format(formatter, numericId);
		}
		for (int i = 0; i < strArray.length - 1; i++) {
			String s = strArray[i];
			nextFormNumberId += s;
			nextFormNumberId += idDelimiter;
		}
		nextFormNumberId += strArray[strArray.length - 1];
		return nextFormNumberId;
	}

	private String getDefaultFormNumberId(String formCode, String table, Map<String, String> elementValueMap) {
		if (formCode.toLowerCase().equals("project".toLowerCase())) {
			//			String projectType = generalUtil.getNull(elementValueMap.get("PROJECTTYPE_ID"), formDao.getFromInfoLookup("ProjectType", "name", "Chemistry","id")); //temporary Chemistry (DEVELOP ONLY!!!)
			String projectType = formDao.getFromInfoLookup("ProjectType", LookupType.ID,
					generalUtil.getNull(elementValueMap.get("PROJECTTYPE_ID")), "name");
			if (projectType.equals("Chemistry")) {
				return firstChemistryProjectNum;
			} else if (projectType.equals("Formulation")) {
				return firstFromulationProjectNum;
			}
		} else if (formCode.toLowerCase().equals("subProject".toLowerCase())) {
			String formNumberId = generalUtil.getEmpty(elementValueMap.get("formNumberId"),
					formDao.getFormNumberIdByFormId("fg_s_project_all_v", elementValueMap.get("PROJECT_ID")));
			return formNumberId + "-00";
		} else if (formCode.toLowerCase().equals("subSubProject".toLowerCase())) {
			String formNumberId = generalUtil.getEmpty(elementValueMap.get("formNumberId"),
					formDao.getFormNumberIdByFormId("fg_s_subproject_all_v", elementValueMap.get("SUBPROJECT_ID")));
			return formNumberId + "-00";
		} else if (formCode.toLowerCase().equals("InvItemBatch".toLowerCase())) {
			return "00-000-0000";
		} else if (formCode.toLowerCase().equals("InvItemCalibration".toLowerCase())
				|| formCode.toLowerCase().equals("InvItemMaintenance".toLowerCase())) {
			return "000";
		} else if (formCode.toLowerCase().equals("Action".toLowerCase())
				|| formCode.toLowerCase().equals("PreperationRef".toLowerCase())) {
			return "00";
		} else if (formCode.toLowerCase().equals("Experiment".toLowerCase())) {

			return formDao.getFromInfoLookup("SUBPROJECT", LookupType.ID, elementValueMap.get("SUBPROJECT_ID"),
					"fORMNUMBERID") + "-"
					+ formDao.getFromInfoLookup("LABORATORY", LookupType.ID, elementValueMap.get("LABORATORY_ID"),
							"fORMNUMBERID")
					+ "-0000";
		} else if (formCode.toLowerCase().equals("Request".toLowerCase())) {
			return formDao.getFromInfoLookup("SUBPROJECT", LookupType.ID, elementValueMap.get("SUBPROJECT_ID"),
					"fORMNUMBERID") + "-00000";
		} else if (formCode.toLowerCase().equals("RequestMain".toLowerCase())) {
			return formDao.getFromInfoLookup("SUBPROJECT", LookupType.ID, elementValueMap.get("SUBPROJECT_ID"),
					"fORMNUMBERID") + "-00000";
		} else if (formCode.toLowerCase().equals("SampleMain".toLowerCase())
				|| formCode.toLowerCase().equals("Sample".toLowerCase())) {
			return formDao.getFromInfoLookup("EXPERIMENT", LookupType.ID, elementValueMap.get("EXPERIMENT_ID"),
					"fORMNUMBERID") + "-00";
		} else if (formCode.equals("BatchMain")) {
			return "0";
		} else if (formCode.equals("TemplateMain")) {
			return "-000";
		
	   } else if (formCode.equals("ExperimentGroup")) {
		return "0000";
	   }
		return "-1";
	    }

	private String getCurrentBatchNumber(String formCode, String formId, String table, String source,
			String wherePart) {
		String currentBatchNumber = "";
		if (source.equals("External")) {
			currentBatchNumber = generalDao.selectSingleString(
					" select max(INVITEMBATCHNAME) from FG_S_InvItemBatch_PIVOT where INVITEMBATCHNAME is not null and INVITEMBATCHNAME like '____-__-___-____-B%' "
							+ wherePart);

			currentBatchNumber = currentBatchNumber != null && currentBatchNumber.length() > 0
					? currentBatchNumber.substring(5, 16) : "";
		} else {//internal batch
			currentBatchNumber = generalDao
					.selectSingleString("select INVITEMBATCHNAME from (select INVITEMBATCHNAME from  " + table
							+ " where INVITEMBATCHNAME like '____-__-___-____-B%' " + wherePart
							+ " order by TO_NUMBER(substr(INVITEMBATCHNAME,instr(INVITEMBATCHNAME,'B')+1)) desc)  where rownum=1 ");//FORMNUMBERID
			currentBatchNumber = currentBatchNumber != null && currentBatchNumber.length() > 0
					? currentBatchNumber.substring(currentBatchNumber.indexOf("B") + 1) : "";
		}
		return currentBatchNumber;
	}

	private String getCurrentExperimentNumber(String table, Map<String, String> elementValueMap, String wherePart) {
		String currentExpNumber = generalDao.selectSingleString(" select max(substr(formNumberId,13,15)) from " + table
				+ " where formNumberId is not null and formNumberId like '____-__-___-____' " + wherePart);
		String currentFormNumberId = "";
		if (currentExpNumber != null && !currentExpNumber.isEmpty()) {
			currentFormNumberId = formDao.getFromInfoLookup("SUBPROJECT", LookupType.ID,
					elementValueMap.get("SUBPROJECT_ID"), "fORMNUMBERID") + "-"
					+ formDao.getFromInfoLookup("LABORATORY", LookupType.ID, elementValueMap.get("LABORATORY_ID"),
							"fORMNUMBERID")
					+ "-" + currentExpNumber;
		}

		return currentFormNumberId;
	}

	private String getCurrentSampleNumFromBatch(String formCode, String formId, String table, String parentBatchId) {
		String currentSampleNumber = "";
		String batchname = formDao.getFromInfoLookup("invitembatch", LookupType.ID, parentBatchId, "name");
		if (batchname.isEmpty()) {
			String source = formDao.getFromInfoLookup("invitembatch", LookupType.ID, parentBatchId, "SOURCENAME");
			if (source.equals("Internal")) {
				String experiment_id = formDao.getFromInfoLookup("invitembatch", LookupType.ID, parentBatchId,
						"experiment_id");
				String experimentname = formDao.getFromInfoLookup("experiment", LookupType.ID, experiment_id, "name");
				currentSampleNumber = generalDao
						.selectSingleStringNoException("select SAMPLENAME from (select SAMPLENAME from  " + table
								+ " where SAMPLENAME like '" + experimentname + "-__' "
								+ " order by TO_NUMBER(substr(SAMPLENAME,instr(SAMPLENAME,'-',-1)+1)) desc)  where rownum=1 ");
				currentSampleNumber = !currentSampleNumber.isEmpty() && currentSampleNumber.length() > 0
						? currentSampleNumber : experimentname + "-00";
			}
			if (currentSampleNumber.isEmpty()) {
				currentSampleNumber = "-00";
			}
		} else {
			currentSampleNumber = generalDao
					.selectSingleStringNoException("select SAMPLENAME from (select SAMPLENAME from  " + table
							+ " where SAMPLENAME like '" + batchname.split("B")[0] + "__' "
							+ " order by TO_NUMBER(substr(SAMPLENAME,instr(SAMPLENAME,'-',-1)+1)) desc)  where rownum=1 ");
			currentSampleNumber = !currentSampleNumber.isEmpty() && currentSampleNumber.length() > 0
					? currentSampleNumber : batchname.split("B")[0] + "00";
		}
		return currentSampleNumber;
	}
	public String getExperimentGroupNumber(String table, Map<String, String> elementValueMap,String formId,String parentId,String userId) {
		
		String projectNumber = generalDao.selectSingleString("select t.PROJECTNUMBER from fg_s_experiment_all_v t where t.EXPERIMENT_ID="+elementValueMap.get("parentId"));
		Date d = new Date();
	    DateFormat dateFormat = new SimpleDateFormat("yyMM");
	    String yearMonth=dateFormat.format(d);
		String runnigNumber=getNextFormNumberIdByFormId("ExperimentGroup", formId, elementValueMap, userId, "fg_s_experimentgroup_pivot");
		if(runnigNumber.contains("-")) {
			runnigNumber=runnigNumber.split("-")[2];
		}
  		return projectNumber+"-"+yearMonth+"-"+runnigNumber;
		
		
	}
}
