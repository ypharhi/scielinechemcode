package com.skyline.customer.adama;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;

@Component
public class CalcComponent extends CalcBasic {
	//	private static final Logger logger = LoggerFactory.getLogger(GeneralDaoImp.class);

	@Override
	public String doCalc(String api, String mainArgCode, String mainArgVal, Map<String, String> elementValueMap,
			String[] apiCodesArray, String[] elementsMatchArray, String formCode, String formId, String userId) {

		try {
			if (elementValueMap.get("isImpurity") == null || elementValueMap.get("isImpurity").isEmpty()) {
				elementValueMap.put("isImpurity", "0");
			}

			if (elementValueMap.get("isImpurity").equals("0") && !elementValueMap.get("impurity").equals("1")) {
				generalUtilLogger.logWriter(LevelType.INFO,
						"Relative Retention Time (RRT) is not calculated. Missing arg-> basic impurity rt",
						ActivitylogType.Calculation, formId);
				elementValueMap.put("rrt", null);
			} else if (elementValueMap.get("impurity").equals("0") && !elementValueMap.get("isImpurity").equals("0")) {
				Map<String, String> BasicImpurityRtData = generalDao
						.getMetaDataRowValues("select rt,OUM_ID from fg_s_component_v t where t.PARENTID = '"
								+ elementValueMap.get("parentId") + "' and t.impurity = 1" + generalUtilFormState
										.getWherePartForTmpData("component", elementValueMap.get("parentId")));
				/*String BasicImpurityRtOUM = generalDao
						.selectSingleString("select OUM_ID from fg_s_component_pivot t where t.PARENTID = '"
								+ elementValueMap.get("parentId") + "' and t.impurity = 1"+ formDao.getWherePartForTmpData("component", elementValueMap.get("parentId")));*/
				Double normalRt = getNormalNumber(elementValueMap.get("rt"), elementValueMap.get("OUM_ID"));
				Double normalBasicImpurityRt = getNormalNumber(BasicImpurityRtData.get("RT"),
						BasicImpurityRtData.get("OUM_ID"));
				if (normalRt == null || normalBasicImpurityRt == null) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on RRT. RT = " + elementValueMap.get("rt")
									+ "; Basic Impurity RT = " + generalUtil.getNull(BasicImpurityRtData.get("RT")),
							ActivitylogType.Calculation, formId);
				} else if (normalBasicImpurityRt == 0.0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed due to division by 0", ActivitylogType.Calculation,
							formId);
				} else {
					elementValueMap.put("rrt", String.valueOf(normalRt / normalBasicImpurityRt));
				}
			} else if (elementValueMap.get("impurity").equals("1")) {
				calcImpuritySiblings(elementValueMap, formId);
			}
		} catch (Exception ex) {
			generalUtilLogger.logWriter(LevelType.ERROR,
					"Relative Retention Time (RRT) is not calculated. RT = " + elementValueMap.get("rt") + ";",
					ActivitylogType.Calculation, formId);
		}

		return "";
	}
	
	public String doCalcEditableTable(String api, String mainArgCode, String mainArgVal, Map<String, String> elementValueMap,
			String[] apiCodesArray, String[] elementsMatchArray, String formCode, String formId, String userId) {

		String rrt ="";
		try {
			//String isImpurity = generalUtil.getNull(generalDao.selectSingleStringNoException("select decode(t.IMPURITY,1,1,(select decode((select distinct count(tt.IMPURITY)over(partition by tt.parentid) c from fg_s_component_v tt where t.parentid = tt.parentid and tt.IMPURITY = 1 and tt.sessionid is null and nvl(tt.active,'1')='1'),'',0,1,1,0) from dual)) from fg_s_component_v t where formId = '"+formId+"' and sessionid = '"+elementValueMap.get("sessionid")+"'"),"0");
			String isImpurity = generalUtil.getNull(generalDao.selectSingleStringNoException("select distinct 1 from fg_s_component_v t where t.PARENTID = '"
					+ elementValueMap.get("PARENTID") + "' and t.impurity = 1" + generalUtilFormState
					.getWherePartForTmpData("component", elementValueMap.get("PARENTID"))),"0");
			String sql = "select * from fg_s_component_all_v where formid = '"+formId+"' and sessionid ='"+elementValueMap.get("sessionid")+"'";
			elementValueMap = generalDao.sqlToHashMap(sql);
			elementValueMap.put("isImpurity",isImpurity);
			
			
			/*
			 * if (elementValueMap.get("isImpurity") == null || elementValueMap.get("isImpurity").isEmpty()) { elementValueMap.put("isImpurity", "0"); }
			 */

			if (elementValueMap.get("isImpurity").equals("0") && !generalUtil.getNull(elementValueMap.get("IMPURITY"),"0").equals("1")) {
				generalUtilLogger.logWriter(LevelType.INFO,
						"Relative Retention Time (RRT) is not calculated. Missing arg-> basic impurity rt",
						ActivitylogType.Calculation, formId);
				//elementValueMap.put("RRT", null);
				rrt = null;
			} else if ((generalUtil.getNull(elementValueMap.get("IMPURITY"),"0").equals("0")||generalUtil.getEmpty(elementValueMap.get("IMPURITY"),"0").equals("0")) && !elementValueMap.get("isImpurity").equals("0")) {
				Map<String, String> BasicImpurityRtData = generalDao
						.getMetaDataRowValues("select rt,OUM_ID from fg_s_component_v t where t.PARENTID = '"
								+ elementValueMap.get("PARENTID") + "' and t.impurity = 1" + generalUtilFormState
										.getWherePartForTmpData("component", elementValueMap.get("PARENTID")));
				/*String BasicImpurityRtOUM = generalDao
						.selectSingleString("select OUM_ID from fg_s_component_pivot t where t.PARENTID = '"
								+ elementValueMap.get("parentId") + "' and t.impurity = 1"+ formDao.getWherePartForTmpData("component", elementValueMap.get("parentId")));*/
				Double normalRt = getNormalNumber(elementValueMap.get("RT"), elementValueMap.get("OUM_ID"));
				Double normalBasicImpurityRt = getNormalNumber(BasicImpurityRtData.get("RT"),
						BasicImpurityRtData.get("OUM_ID"));
				if (normalRt == null || normalBasicImpurityRt == null) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on RRT. RT = " + elementValueMap.get("rt")
									+ "; Basic Impurity RT = " + generalUtil.getNull(BasicImpurityRtData.get("RT")),
							ActivitylogType.Calculation, formId);
				} else if (normalBasicImpurityRt == 0.0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed due to division by 0", ActivitylogType.Calculation,
							formId);
				} else {
					elementValueMap.put("RRT", String.valueOf(normalRt / normalBasicImpurityRt));
					rrt =  String.valueOf(normalRt / normalBasicImpurityRt);
				}
			} else if (elementValueMap.get("IMPURITY").equals("1")) {
				elementValueMap.put("rt",elementValueMap.get("RT"));
				elementValueMap.put("parentId",elementValueMap.get("PARENTID"));
				
				calcImpuritySiblings(elementValueMap, formId);
			}
		} catch (Exception ex) {
			generalUtilLogger.logWriter(LevelType.ERROR,
					"Relative Retention Time (RRT) is not calculated. RT = " + elementValueMap.get("rt") + ";",
					ActivitylogType.Calculation, formId);
		}

		return rrt;
	}

	private void calcImpuritySiblings(Map<String, String> elementValueMap, String formId) {
		Double normalBasicImpurityRt = getNormalNumber(elementValueMap.get("rt"), elementValueMap.get("OUM_ID"));

		List<String> expSiblingList = generalDao
				.getListOfStringBySql("select t.formid from fg_s_component_all_v t where t.PARENTID = '"
						+ elementValueMap.get("parentId") + "'" + generalUtilFormState
								.getWherePartForTmpData("component", elementValueMap.get("parentId")));//gets of the siblings of the current experiment
		// elementValueMap.put("rrt" ,"1");
		for (String siblingFormId : expSiblingList) {
			Map<String, String> siblingData = generalDao.getMetaDataRowValues(
					"select t.rt, t.OUM_ID from fg_s_component_all_v t where FORMID = '" + siblingFormId + "'");
			/*String uom = generalDao.selectSingleString(
					"select t.OUM_ID from fg_s_component_all_v t where FORMID = '" + siblingFormId + "'");*/
			Double normalRt = getNormalNumber(siblingData.get("RT"), siblingData.get("OUM_ID")); // TODO ADI handle normalRt null?
			if (normalRt == null || normalBasicImpurityRt == null || normalBasicImpurityRt == 0.0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on RRT of sibling component. RT = "
								+ generalUtil.getNull(siblingData.get("RT")) + ";BasicImpurityRt = "
								+ elementValueMap.get("rt"),
						ActivitylogType.Calculation, formId);
			}
			String rrt = String.valueOf(normalRt / normalBasicImpurityRt);
			formSaveDao.updateStructTableByFormId(
					"update fg_s_component_pivot t set rrt = " + rrt + " where FORMID = '" + siblingFormId + "'",
					"fg_s_component_pivot", Arrays.asList("rrt"), siblingFormId);
		}
	}
}