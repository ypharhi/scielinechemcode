package com.skyline.customer.adama;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;

// @Override
// public ActionBean doCalc(List<DataBean> dataBeanList, String formCode, String formId, String userId) {
// // TODO Auto-generated method stub
// return null;
// }

/**
 * stepCalc
 * 
 * @param api
 * @param mainArgCode
 * @param mainArgVal
 * @param elementValueMap
 * @param apiCodesArray
 * @param elementsMatchArray
 * @param formCode
 * @param formId
 * @param userId
 * @return
 */
@Component
public class CalcStep extends CalcBasic {
	// private static final Logger logger = LoggerFactory.getLogger(GenerealDaoImp.class);
	@Override
	public String doCalc(String api, String mainArgCode, String mainArgVal, Map<String, String> elementValueMap,
			String[] apiCodesArray, String[] elementsMatchArray, String formCode, String formId, String userId) {

		String volFactor = elementValueMap.get("volFactor");
		String reactorVolume = generalUtil.getEmpty(elementValueMap.get("reactorVolume"),"0");
		String normalReactorVolume ="";
		double volFactorCalc = (Double.valueOf(volFactor) + 100) / 100;
		String stepId = formId;
		List<Map<String, Object>> returnListOfFMap;
		String plannedCompVolume = "";
		String actCompVolume = "";
		String plannedCompMass = "";
		String actCompMass = "";
		String actualReactorVol = "";
		String plannedReactorVol = "";
		Double sumValume = (double) 0;
		Double sumMass = (double) 0;
		Double normalValume;
		Double normalMass;
		String sql = "";
		Double normalValumePlanned;
		Double normalMassPlanned;
		String plannedRetentionTime = "";
		String actualRetentionTime = "";

		String grSql = "select distinct fg_get_uom_by_uomtype(uom_type,default_value) from fg_i_uom_metadata_v where formcode = 'Step' and column_name = 'MASSUOM'";
		String mlSql = "select distinct fg_get_uom_by_uomtype(uom_type,default_value) from fg_i_uom_metadata_v where formcode = 'Step' and column_name = 'COMPVOL_ID'";
		String uomGr = !generalUtil.getNull(elementValueMap.get("MASSUOM")).isEmpty() ? elementValueMap.get("MASSUOM")
				: generalDao.selectSingleStringNoException(grSql);
		String uomMl = !generalUtil.getNull(elementValueMap.get("COMPVOL_ID")).isEmpty()
				? elementValueMap.get("COMPVOL_ID") : generalDao.selectSingleStringNoException(mlSql);
		String reactorVolSql = "select distinct fg_get_uom_by_uomtype(uom_type,default_value) from fg_i_uom_metadata_v where formcode = 'Step' and column_name = 'REACTORVOL_ID'";
		String uomReactorVol = !generalUtil.getNull(elementValueMap.get("REACTORVOL_ID")).isEmpty()
				? elementValueMap.get("REACTORVOL_ID") : generalDao.selectSingleStringNoException(reactorVolSql);

		String retentionTimeSql = "select distinct fg_get_uom_by_uomtype(uom_type,default_value) from fg_i_uom_metadata_v where formcode = 'Step' and column_name = 'RETENTIONTIMEUOM_ID'";
		String grRateSql = "select distinct fg_get_uom_by_uomtype(uom_type,default_value) from fg_i_uom_metadata_v where formcode = 'Step' and column_name = 'COMPMASSRATE_UOM_ID'";
		String mlRateSql = "select distinct fg_get_uom_by_uomtype(uom_type,default_value) from fg_i_uom_metadata_v where formcode = 'Step' and column_name = 'COMPVOLUMERATE_UOM_ID'";
		String uomGrRate = !generalUtil.getNull(elementValueMap.get("COMPMASSRATE_UOM_ID")).isEmpty() ? elementValueMap.get("COMPMASSRATE_UOM_ID")
				: generalDao.selectSingleStringNoException(grRateSql);
		String uomMlRate = !generalUtil.getNull(elementValueMap.get("COMPVOLUMERATE_UOM_ID")).isEmpty()
				? elementValueMap.get("COMPVOLUMERATE_UOM_ID") : generalDao.selectSingleStringNoException(mlRateSql);
		String uomRetentionTime = !generalUtil.getNull(elementValueMap.get("RETENTIONTIMEUOM_ID")).isEmpty()
				? elementValueMap.get("RETENTIONTIMEUOM_ID") : generalDao.selectSingleStringNoException(retentionTimeSql);
		String uomReactorVolumeCP = !generalUtil.getNull(elementValueMap.get("REACTORVOLUMEUOM_ID")).isEmpty()
				? elementValueMap.get("REACTORVOLUMEUOM_ID") : generalDao.selectSingleStringNoException(reactorVolSql);
				
		if (mainArgCode.equals("OnSaveSS")) {

			sumValume = (double) 0;
			sumMass = (double) 0;
			Double sumMassPlanned = (double) 0;
			Double sumVolumePlanned = (double) 0;

			if(elementValueMap.get("PROTOCOLTYPENAME").equals("Continuous Process")){
				String lastStatusName = formDao.getFromInfoLookup("STEPSTATUS", LookupType.ID, elementValueMap.get("LASTSTATUS_ID"), "name");
				sql = "select distinct ac.QUANTITYRATE ACTUAL_QUANTITY, pln.QUANTITYRATE planned_quantity, ac.QUANTITYRATE_UOM ACTUAL_QUANTITYUOM, pln.QUANTITYRATE_UOM PLANNED_QUANTITYUOM"
						+ ", ac.VOLUMERATE actual_volume, pln.VOLUMERATE planned_volume, ac.VOLRATEUOM_ID ACTUAL_VOLUMEUOM, pln.VOLRATEUOM_ID PLANNED_VOLUMEUOM"
						+ ", pln.DENSITYINF, pln.DENSITYUOM_ID_INF"
						+ " from fg_s_materialref_dtbasic_v pln, fg_s_materialref_dtbasic_v ac"
						+ " where pln.STEPSTATUSNAME(+) = 'Planned'" + " and ac.STEPSTATUSNAME = 'Actual'"
						+ " and ac.step_id = pln.step_id(+)" + " and ac.materialref_id = pln.materialref_id(+)"
						+ " and ac.invitemmaterial_id = pln.invitemmaterial_id(+)" + " and pln.parentId = '" + stepId + "'";
				returnListOfFMap = generalDao.getListOfMapsBySql(sql);
				Double normalDensity = (double) 0;
				for (Map<String, Object> map : returnListOfFMap) {
					normalValume = getNormalNumber(
							map.get("ACTUAL_VOLUME") == null ? "" : map.get("ACTUAL_VOLUME").toString(),
							map.get("ACTUAL_VOLUMEUOM") == null ? "" : map.get("ACTUAL_VOLUMEUOM").toString(), (double) 0);
					sumValume += normalValume == Double.parseDouble("-1") ? 0 : normalValume;
					normalMass = getNormalNumber(
							map.get("ACTUAL_QUANTITY") == null ? "" : map.get("ACTUAL_QUANTITY").toString(),
							map.get("ACTUAL_QUANTITYUOM") == null ? "" : map.get("ACTUAL_QUANTITYUOM").toString(),
							(double) 0);
					sumMass += normalMass;
	
					normalValumePlanned = getNormalNumber(
							map.get("PLANNED_VOLUME") == null ? "" : map.get("PLANNED_VOLUME").toString(),
							map.get("PLANNED_VOLUMEUOM") == null ? "" : map.get("PLANNED_VOLUMEUOM").toString(),
							(double) 0);
					normalDensity = getNormalNumber(map.get("DENSITYINF") == null ? "" : map.get("DENSITYINF").toString(),
							map.get("DENSITYUOM_ID_INF") == null ? "" : map.get("DENSITYUOM_ID_INF").toString(), (double) 0);
					sumVolumePlanned += (normalValumePlanned == Double.parseDouble("-1") || normalDensity == Double.parseDouble("-1")? 0 : normalValumePlanned*normalDensity);
										
					/*normalMassPlanned = getNormalNumber(
							map.get("PLANNED_QUANTITY") == null ? "" : map.get("PLANNED_QUANTITY").toString(),
							map.get("PLANNED_QUANTITYUOM") == null ? "" : map.get("PLANNED_QUANTITYUOM").toString(),
							(double) 0);
					sumMassPlanned += normalMassPlanned;*/
				}
				
				//if(lastStatusName.equals("Planned")){
				Map<String, String> run_uom_default = generalDao.sqlToHashMap("select * from fg_i_calcrun_uom_override_v");
				
				    String defaultReactorVolumeUomId = run_uom_default.get("DEFAULT_REACTORVOLUME_UOM_ID");
				    String defaultTimeUomId = run_uom_default.get("DEFAULT_TIME_UOM_ID");
					String defaultVol = run_uom_default.get("DEFAULT_VOLUME_UOM_ID");
					plannedRetentionTime = String.valueOf(getCustomNormalNumber(elementValueMap.get("retentionTime"), uomRetentionTime,defaultTimeUomId, new StringBuilder()));
					//	normalReactorVolume = String.valueOf(getNormalNumber(reactorVolume, uomReactorVolumeCP));
					if(!uomReactorVolumeCP.equals(defaultReactorVolumeUomId)){
					normalReactorVolume = getCustomNormalNumber(reactorVolume, uomReactorVolumeCP, defaultReactorVolumeUomId, new StringBuilder()).toString();
					}else
					{
						normalReactorVolume = reactorVolume;
					}
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Planned Total Compound Volume Rate Calculation =></br>");
					sbInfo.append("Normal Reactor Volume/Normal Planned Retention Time = "+ normalReactorVolume + "/"+ plannedRetentionTime +"</br>");
					if(generalUtil.getNull(plannedRetentionTime).isEmpty() || Double.valueOf(plannedRetentionTime)==0){
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on total compounds volume rate. Planned Retention Time =" + elementValueMap.get("retentionTime"),
								ActivitylogType.Calculation, formId);
					}else{
						plannedCompVolume = getCustomNormalNumber(String.valueOf(Double.valueOf(normalReactorVolume)/Double.valueOf(plannedRetentionTime)),
								defaultVol, uomMlRate, sbInfo).toString();//fixed bug 8325
					}
					elementValueMap.put("compVolumeRate", plannedCompVolume);
		
					sbInfo = new StringBuilder();
					sbInfo.append("Planned Total Compound Mass Rate Calculation =></br>");
					sbInfo.append("Sum of (Normal Volume rate * Normal Density) for each reagents and solvents as these were entered in status Planned </br>");
					plannedCompMass = getFromNormalNumber(sumVolumePlanned.toString(), uomGrRate, sbInfo);
					elementValueMap.put("compMassRate", plannedCompMass);
				//}
					
				sbInfo = new StringBuilder();
				sbInfo.append("Actual Total Compound Volume Rate Calculation =></br>");
				sbInfo.append("sums all the volume rates as these were entered\\modified in status Active</br>");
				actCompVolume = getFromNormalNumber(sumValume.toString(), uomMlRate, sbInfo);
				elementValueMap.put("actCompVolumeRate", actCompVolume);
				normalReactorVolume = String.valueOf(getNormalNumber(reactorVolume, uomReactorVolumeCP));				
				
				sbInfo = new StringBuilder();
				sbInfo.append("Actual Retention Time Calculation =></br>");
				sbInfo.append(
						"Normal Reactor Volume/Total Compound Volume Rate = " + normalReactorVolume + "/" + sumValume + "</br>");
				if(sumValume == 0){
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on Actual Retention Time. Total Compound Volume Rate=0",
							ActivitylogType.Calculation, formId);
				} else {
					actualRetentionTime = getFromNormalNumber(
							String.valueOf(Double.valueOf(normalReactorVolume) / sumValume), uomRetentionTime, sbInfo);
				}
				elementValueMap.put("retentionTimeActual", actualRetentionTime);
				
				sbInfo = new StringBuilder();
				sbInfo.append("Actual Compound Mass Calculation =></br>");
				sbInfo.append("sums all the mass as these were entered\\modified in status Active</br>");
				actCompMass = getFromNormalNumber(sumMass.toString(), uomGrRate, sbInfo);
				elementValueMap.put("actCompMassRate", actCompMass);
				
				JSONObject json = new JSONObject();
				json.put("actCompMassRate", elementValueMap.get("actCompMassRate"));
				json.put("actCompVolumeRate", elementValueMap.get("actCompVolumeRate"));
				//if(lastStatusName.equals("Planned")){
					json.put("compVolumeRate", elementValueMap.get("compVolumeRate"));
					json.put("compMassRate", elementValueMap.get("compMassRate"));
				//}
				json.put("retentionTimeActual", elementValueMap.get("retentionTimeActual"));
				return json.toString();
			} else {
				/*sql = "select t.VOLUME,t.VOLUOM_ID,t.QUANTITY,t.QUANTITYUOM_ID from FG_S_MATERIALREF_V t where t.PARENTID = '"
						+ stepId + "' "+generalUtilFormState.getWherePartForTmpData("MaterialRef", formId);*/
				sql = "select distinct ac.QUANTITY ACTUAL_QUANTITY, pln.QUANTITY planned_quantity, ac.quantityuom_id ACTUAL_QUANTITYUOM, pln.quantityuom_id PLANNED_QUANTITYUOM"
						+ ", ac.VOLUME actual_volume, pln.VOLUME planned_volume, ac.voluom_id ACTUAL_VOLUMEUOM, pln.voluom_id PLANNED_VOLUMEUOM"
						+ " from fg_s_materialref_dtbasic_v pln, fg_s_materialref_dtbasic_v ac"
						+ " where pln.STEPSTATUSNAME(+) = 'Planned'" + " and ac.STEPSTATUSNAME = 'Actual'"
						+ " and ac.step_id = pln.step_id(+)" + " and ac.materialref_id = pln.materialref_id(+)"
						+ " and ac.invitemmaterial_id = pln.invitemmaterial_id(+)" + " and pln.parentId = '" + stepId + "'";
				returnListOfFMap = generalDao.getListOfMapsBySql(sql);
				for (Map<String, Object> map : returnListOfFMap) {
					normalValume = getNormalNumber(
							map.get("ACTUAL_VOLUME") == null ? "" : map.get("ACTUAL_VOLUME").toString(),
							map.get("ACTUAL_VOLUMEUOM") == null ? "" : map.get("ACTUAL_VOLUMEUOM").toString(), (double) 0);
					sumValume += normalValume == Double.parseDouble("-1") ? 0 : normalValume;
					normalMass = getNormalNumber(
							map.get("ACTUAL_QUANTITY") == null ? "" : map.get("ACTUAL_QUANTITY").toString(),
							map.get("ACTUAL_QUANTITYUOM") == null ? "" : map.get("ACTUAL_QUANTITYUOM").toString(),
							(double) 0);
					sumMass += normalMass;
	
					normalValumePlanned = getNormalNumber(
							map.get("PLANNED_VOLUME") == null ? "" : map.get("PLANNED_VOLUME").toString(),
							map.get("PLANNED_VOLUMEUOM") == null ? "" : map.get("PLANNED_VOLUMEUOM").toString(),
							(double) 0);
					sumVolumePlanned += normalValumePlanned == Double.parseDouble("-1") ? 0 : normalValumePlanned;
					normalMassPlanned = getNormalNumber(
							map.get("PLANNED_QUANTITY") == null ? "" : map.get("PLANNED_QUANTITY").toString(),
							map.get("PLANNED_QUANTITYUOM") == null ? "" : map.get("PLANNED_QUANTITYUOM").toString(),
							(double) 0);
					sumMassPlanned += normalMassPlanned;
				}
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Actual Compound Volume Calculation =></br>");
				sbInfo.append("sums all the volumes as these were entered\\modified in status Active</br>");
				actCompVolume = getFromNormalNumber(sumValume.toString(), uomMl, sbInfo);
				sbInfo = new StringBuilder();
				sbInfo.append("Planned Compound Volume Calculation =></br>");
				sbInfo.append("sums all the volumes as these were entered in status Planned</br>");
				plannedCompVolume = getFromNormalNumber(sumVolumePlanned.toString(), uomMl, sbInfo);
				elementValueMap.put("actCompVolume", actCompVolume);
				elementValueMap.put("plannedCompVolume", plannedCompVolume);
	
				sbInfo = new StringBuilder();
				sbInfo.append("Actual Compound Mass Calculation =></br>");
				sbInfo.append("sums all the mass as these were entered\\modified in status Active</br>");
				actCompMass = getFromNormalNumber(sumMass.toString(), uomGr, sbInfo);
				sbInfo = new StringBuilder();
				sbInfo.append("Planned Compound Mass Calculation =></br>");
				sbInfo.append("sums all the mass as these were entered in status Planned</br>");
				plannedCompMass = getFromNormalNumber(sumMassPlanned.toString(), uomGr, sbInfo);
				elementValueMap.put("plannedCompMass", plannedCompMass);
				elementValueMap.put("actCompMass", actCompMass);
	
				/*Double.toString((sumValume/uomMlFactor));
				actCompMass = Double.toString((sumMass/uomGrFactor));*/
				//actCompVolume = sumValume.toString();
				//actCompMass = sumMass.toString();
				sbInfo = new StringBuilder();
				sbInfo.append("Actual Estimated Reactor Volume Calculation =></br>");
				sbInfo.append(
						"Total Compound Volume * (100+reactor factor)/100 = " + sumValume + "*" + volFactorCalc + "</br>");
				actualReactorVol = String.valueOf(
						volFactorCalc * Double.valueOf(getFromNormalNumber(sumValume.toString(), uomReactorVol, sbInfo)));
				plannedReactorVol = String.valueOf(volFactorCalc
						* Double.valueOf(getFromNormalNumber(sumVolumePlanned.toString(), uomReactorVol, sbInfo)));
	
				elementValueMap.put("actualReactorVol", actualReactorVol);
				sbInfo = new StringBuilder();
				sbInfo.append("Planned Estimated Reactor Volume Calculation =></br>");
				sbInfo.append(
						"Total Compound Volume * (100+reactor factor)/100 = " + sumValume + "*" + volFactorCalc + "</br>");
				elementValueMap.put("plannedReactorVol", plannedReactorVol);
	
				JSONObject json = new JSONObject();
				json.put("actCompMass", elementValueMap.get("actCompMass"));
				json.put("actCompVolume", elementValueMap.get("actCompVolume"));
				json.put("plannedCompMass", elementValueMap.get("plannedCompMass"));
				json.put("plannedCompVolume", elementValueMap.get("plannedCompVolume"));
				json.put("actualReactorVol", elementValueMap.get("actualReactorVol"));
				json.put("plannedReactorVol", elementValueMap.get("plannedReactorVol"));
				return json.toString();
			}

		} else {// step status=planned
			JSONObject json = new JSONObject();
			if(elementValueMap.get("PROTOCOLTYPENAME").equals("Continuous Process")){
				sql = "select t.DENSITYINF,t.DENSITYUOM_ID_INF,t.VOLUMERATE,t.VOLRATEUOM_ID,t.QUANTITYRATE,t.QUANTITYRATE_UOM from FG_S_MATERIALREF_V t where t.PARENTID = "
						+ stepId + " and tabletype in ('Reactant','Solvent')" + generalUtilFormState.getWherePartForTmpData("MaterialRef", formId);
				returnListOfFMap = generalDao.getListOfMapsBySql(sql);
				Double normalDensity = 0.0;
				for (Map<String, Object> map : returnListOfFMap) {
					normalValume = getNormalNumber(map.get("VOLUMERATE") == null ? "" : map.get("VOLUMERATE").toString(),
							map.get("VOLRATEUOM_ID") == null ? "" : map.get("VOLRATEUOM_ID").toString(), (double) 0);
					normalDensity = getNormalNumber(map.get("DENSITYINF") == null ? "" : map.get("DENSITYINF").toString(),
							map.get("DENSITYUOM_ID_INF") == null ? "" : map.get("DENSITYUOM_ID_INF").toString(), (double) 0);
					sumValume += (normalValume == Double.parseDouble("-1")||normalDensity == Double.parseDouble("-1") ? 0 : normalValume*normalDensity);
					/*normalMass = getNormalNumber(map.get("QUANTITYRATE") == null ? "" : map.get("QUANTITYRATE").toString(),
							map.get("QUANTITYRATE_UOM") == null ? "" : map.get("QUANTITYRATE_UOM").toString(), (double) 0);
					sumMass += normalMass;*/
				}
				
				plannedRetentionTime = String.valueOf(getNormalNumber(elementValueMap.get("retentionTime"), uomRetentionTime));
				normalReactorVolume = String.valueOf(getNormalNumber(reactorVolume, uomReactorVolumeCP));
				
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Planned Total Compound Volume Rate Calculation =></br>");
				sbInfo.append("Normal Reactor Volume/Normal Planned Retention Time = "+ normalReactorVolume + "/"+ plannedRetentionTime +"</br>");
				if(generalUtil.getNull(plannedRetentionTime).isEmpty() || Double.valueOf(plannedRetentionTime)==0){
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on total compounds volume rate. Planned Retention Time =" + elementValueMap.get("retentionTime"),
							ActivitylogType.Calculation, formId);
				} else {
					plannedCompVolume = getFromNormalNumber(String.valueOf(Double.valueOf(normalReactorVolume)/Double.valueOf(plannedRetentionTime))
							, uomMlRate, sbInfo);
				}
				elementValueMap.put("compVolumeRate", plannedCompVolume);
	
				sbInfo = new StringBuilder();
				sbInfo.append("Planned Total Compound Mass Rate Calculation =></br>");
				sbInfo.append("Sum of (Normal Volume rate * Normal Density) for each reagents and solvents as these were entered in status Planned </br>");
				plannedCompMass = getFromNormalNumber(sumValume.toString(), uomGrRate, sbInfo);
				elementValueMap.put("compMassRate", plannedCompMass);
				
				json.put("compMassRate", elementValueMap.get("compMassRate"));
				json.put("compVolumeRate", elementValueMap.get("compVolumeRate"));
			} else {
				sql = "select t.VOLUME,t.VOLUOM_ID,t.QUANTITY,t.QUANTITYUOM_ID from FG_S_MATERIALREF_V t where t.PARENTID = "
						+ stepId + generalUtilFormState.getWherePartForTmpData("MaterialRef", formId);
				returnListOfFMap = generalDao.getListOfMapsBySql(sql);
				for (Map<String, Object> map : returnListOfFMap) {
					normalValume = getNormalNumber(map.get("VOLUME") == null ? "" : map.get("VOLUME").toString(),
							map.get("VOLUOM_ID") == null ? "" : map.get("VOLUOM_ID").toString(), (double) 0);
					sumValume += normalValume == Double.parseDouble("-1") ? 0 : normalValume;
					normalMass = getNormalNumber(map.get("QUANTITY") == null ? "" : map.get("QUANTITY").toString(),
							map.get("QUANTITYUOM_ID") == null ? "" : map.get("QUANTITYUOM_ID").toString(), (double) 0);
					sumMass += normalMass;
				}
	
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Planned Compound Volume Calculation =></br>");
				sbInfo.append("sums all the volumes as these were entered in status Planned</br>");
				plannedCompVolume = getFromNormalNumber(sumValume.toString(), uomMl, sbInfo);
				elementValueMap.put("plannedCompVolume", plannedCompVolume);
	
				sbInfo = new StringBuilder();
				sbInfo.append("Planned Compound Mass Calculation =></br>");
				sbInfo.append("sums all the mass as these were entered in status Planned </br>");
				plannedCompMass = getFromNormalNumber(sumMass.toString(), uomGr, sbInfo);
				elementValueMap.put("plannedCompMass", plannedCompMass);
	
				/*plannedCompVolume =  Double.toString((sumValume/uomMlFactor));
				plannedCompMass = Double.toString((sumMass/uomGrFactor));*/
				//plannedCompVolume = sumValume.toString();
				//plannedCompMass = sumMass.toString();
	
				actCompVolume = "0";
				actCompMass = "0";
				actualReactorVol = "0";
	
				sbInfo = new StringBuilder();
				sbInfo.append("Planned Estimated Reactor Volume Calculation =></br>");
				sbInfo.append(
						"Total Compound Volume * (100+reactor factor)/100 = " + sumValume + "*" + volFactorCalc + "</br>");
				plannedReactorVol = String.valueOf(
						volFactorCalc * Double.valueOf(getFromNormalNumber(sumValume.toString(), uomReactorVol, sbInfo)));
				/*elementValueMap.put("plannedCompVolume", plannedCompVolume);
				elementValueMap.put("actCompVolume", actCompVolume);
				elementValueMap.put("plannedCompMass", plannedCompMass);
				elementValueMap.put("actCompMass", actCompMass);*/
				elementValueMap.put("actualReactorVol", actualReactorVol);
				elementValueMap.put("plannedReactorVol", plannedReactorVol);
				
				json.put("plannedCompMass", elementValueMap.get("plannedCompMass"));
				json.put("plannedCompVolume", elementValueMap.get("plannedCompVolume"));
				json.put("plannedReactorVol", elementValueMap.get("plannedReactorVol"));
			}
			return json.toString();
		}

	}

}
