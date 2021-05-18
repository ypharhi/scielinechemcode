package com.skyline.customer.adama;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;
//import org.openscience.cdk.isomorphism.matchers.smarts.TotalRingConnectionAtom;
import org.springframework.stereotype.Component;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;

import net.sourceforge.htmlunit.cyberneko.filters.Purifier;

@Component
public class CalcMaterialTriplet extends CalcBasic {

	// private static final Logger logger = LoggerFactory.getLogger(GeneralDaoImp.class);

	@Override
	public String doCalc(String api, String mainArgCode, String mainArgVal, Map<String, String> elementValueMap,
			String[] apiCodesArray, String[] elementsMatchArray, String formCode, String formId, String userId) {

		StringBuilder removeFromMapInfo = new StringBuilder();
		if (elementsMatchArray != null) {
			for (String elmentToCalc : elementsMatchArray) {
				if (elementValueMap != null && elementValueMap.containsKey(elmentToCalc)) {
					String val_ = generalUtil.getNull(elementValueMap.get(elmentToCalc));
					if (!val_.equals("")) {
						removeFromMapInfo.append(", " + elmentToCalc);
						elementValueMap.put(elmentToCalc, "");
					}
				}
			}
		}

		if (!mainArgCode.equals("OnSave")) {
			if (elementValueMap.get("isLimited") == null || elementValueMap.get("isLimited").isEmpty()) {
				elementValueMap.put("isLimited", "0");
			}
		}
		StringBuilder sb = new StringBuilder();
		if (mainArgCode.equals("OnSaveStep")) {//todo
			elementValueMap.put("parentId", formId);
			RatioTotalCalc(elementValueMap, formId, "-1", "-1", null);
			CalcProductMole(elementValueMap,formId);
		} else if (mainArgCode.equals("OnSave")) {// ignore solvents-TODO:ask Igor- solvent has no equivalent
			if (elementValueMap.get("tableType").equals("Solvent")) {
				materialSolventOnSaveCalc(elementValueMap, formId);
			}
			if (elementValueMap.get("tableType").equals("Reactant")) {
				materialReactantOnSaveCalc(elementValueMap, formId);
			}
		} else if (mainArgCode.equals("OnSaveLimitingAgent")) {
			calcLimitingAgentSiblings(elementValueMap, formId);
		} else if (elementValueMap.get("tableType").equals("Reactant") && mainArgCode.equalsIgnoreCase("Quantity")) {
			sb = materialReactantQuantityCalc(elementValueMap, mainArgVal, formId);
		} else if (elementValueMap.get("tableType").equals("Solvent") && mainArgCode.equalsIgnoreCase("Quantity")) {
			sb = materialSolventQuantityCalc(elementValueMap, mainArgVal, formId);
		} else if (elementValueMap.get("tableType").equals("Reactant") && mainArgCode.equalsIgnoreCase("Mole")) {
			sb = materialReactanttMoleCalc(elementValueMap, mainArgVal, formId);
		} else if (elementValueMap.get("tableType").equals("Solvent") && mainArgCode.equalsIgnoreCase("Mole")) {
			sb = materialSolventMoleCalc(elementValueMap, mainArgVal, formId);
		} else if (elementValueMap.get("tableType").equals("Reactant") && mainArgCode.equalsIgnoreCase("Volume")) {
			sb = materialReactantVolumeCalc(elementValueMap, mainArgVal, formId);
		} else if (elementValueMap.get("tableType").equals("Solvent") && mainArgCode.equalsIgnoreCase("Volume")) {
			sb = materialSolventVolumeCalc(elementValueMap, mainArgVal, formId);
		} else if (elementValueMap.get("tableType").equals("Reactant") && mainArgCode.equalsIgnoreCase("QuantityRate")) {
			sb = materialReactantQuantityRateCalc(elementValueMap, mainArgVal, formId);
		} else if (elementValueMap.get("tableType").equals("Solvent") && mainArgCode.equalsIgnoreCase("QuantityRate")) {
			sb = materialSolventQuantityRateCalc(elementValueMap, mainArgVal, formId);
		} else if (elementValueMap.get("tableType").equals("Reactant") && mainArgCode.equalsIgnoreCase("MoleRate")) {
			sb = materialReactantMoleRateCalc(elementValueMap, mainArgVal, formId);
		} else if (elementValueMap.get("tableType").equals("Solvent") && mainArgCode.equalsIgnoreCase("MoleRate")) {
			sb = materialSolventMoleRateCalc(elementValueMap, mainArgVal, formId);
		} else if (elementValueMap.get("tableType").equals("Reactant") && mainArgCode.equalsIgnoreCase("VolumeRate")) {
			sb = materialReactantVolumeRateCalc(elementValueMap, mainArgVal, formId);
		} else if (elementValueMap.get("tableType").equals("Solvent") && mainArgCode.equalsIgnoreCase("VolumeRate")) {
			sb = materialSolventVolumeRateCalc(elementValueMap, mainArgVal, formId);
		} else if (elementValueMap.get("tableType").equals("Product")) {
			sb = materialProductCalc(elementValueMap, mainArgCode, formId,null);
		} else if (elementValueMap.get("tableType").equals("Reactant")
				|| elementValueMap.get("tableType").equals("Product")) {
			sb = materialReactionsGeneralcalc(elementValueMap, mainArgCode, mainArgVal, formId);
		} else {
			sb.append("{");
			sb.append("}");
		}
		return sb.toString();
	}

	private void CalcProductMole(Map<String, String> elementValueMap, String formId) {
		String normalLimitingMole = generalDao.selectSingleStringNoException("select fg_get_num_normal(mole,MOLEUOM_ID)"
				+ " from fg_s_materialref_v t"
				+ " where t.PARENTID = '"+ elementValueMap.get("parentId") 
				+ "' and t.LIMITINGAGENT = 1 " 
				+ generalUtilFormState.getWherePartForTmpData("materialref", elementValueMap.get("parentId")));
		
		List<Map<String,Object>> productList = generalDao.getListOfMapsBySql("select formid,yield"
				+ " from fg_s_materialref_v t"
				+ " where t.TABLETYPE = 'Product'"
				+ " and t.PARENTID = '"	+ elementValueMap.get("parentId") + "'" 
				+ generalUtilFormState.getWherePartForTmpData("MaterialRef", elementValueMap.get("parentId")));
		
		for(Map<String,Object> productData:productList){
			try{
			StringBuilder sb = new StringBuilder();
			sb.append("CalcProductMole - Calculate mole of product formid = "+productData.get("FORMID").toString());
			sb.append("Product Mole Calculation =></br>");
			sb.append("Formula: Limiting_agent_Mole*Yield/100.</br> Normal Args:"
					+ String.valueOf(normalLimitingMole) + "*" +(productData.get("YIELD")!=null?productData.get("YIELD").toString():"NA")
					+ "/100</br>");
			Double productMole = null;
			if(productData.get("YIELD")!=null && !generalUtil.getNull(normalLimitingMole).isEmpty()){
				productMole = Double.parseDouble(normalLimitingMole)*Double.parseDouble(productData.get("YIELD").toString())/100;
			}
			String sqlUpdate = "update fg_s_materialref_pivot"
						+ " set MOLE = '"+(productMole!=null?productMole:"")+"'"
						+ " where formid = '"+productData.get("FORMID").toString()+"'";
			formSaveDao.updateStructTableByFormId(sqlUpdate, "FG_S_MATERIALREF_PIVOT", Arrays.asList("Mole"), productData.get("FORMID").toString());
			generalUtilLogger.logWriter(LevelType.INFO,
					sb.toString() ,ActivitylogType.Calculation, "");
			generalUtilLogger.logWriter(LevelType.DEBUG,
					sb.toString() ,ActivitylogType.Calculation, "");
			}
			catch(Exception e){
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole of product formid=" + productData.get("FORMID").toString(),
						ActivitylogType.Calculation, formId);
			}
		}
	}

	private StringBuilder materialReactionsGeneralcalc(Map<String, String> elementValueMap, String mainArgCode,
			String mainArgVal, String formId) {
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"materialReactionsGeneralcalc - elementValueMap before calulation= mole:" + elementValueMap.get("mole")
						+ ",volume:" + elementValueMap.get("volume") + ",quantity:" + elementValueMap.get("quantity"),
				ActivitylogType.Calculation, formId);
		StringBuilder sb = new StringBuilder();

		if (elementValueMap.get("isLimited").equals("1") && elementValueMap.get("limitingAgent").equals("0")) {// there exists a sibling step that defined as limiting agent
			updateLimitingAgentSibling(mainArgCode, elementValueMap, formId);
			sb = createJsonMaterialCalcSB(elementValueMap);
		} else {
			sb = createJsonMaterialCalcSB(elementValueMap);
		}
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"materialReactionsGeneralcalc - elementValueMap after calulation= mole:" + elementValueMap.get("mole")
						+ ",volume:" + elementValueMap.get("volume") + ",quantity:" + elementValueMap.get("quantity"),
				ActivitylogType.Calculation, formId);
		return sb;
	}

	private void calcLimitingAgentProduct(String mainArgCode, Map<String, String> elementValueMap, String formId) {
		// TODO Auto-generated method stub
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "calcLimitingAgentProduct - elementValueMap before calulation= mass:"
						+ elementValueMap.get("mass") + ",yield:" + elementValueMap.get("yield"),
				ActivitylogType.Calculation, formId);
		Double limitingMole = 0.0;
		Double normalLimitinigMole = 0.0;
		try{
		if(generalUtil.getNull(elementValueMap.get("isRun")).equals("1")){
			if (elementValueMap.containsKey("limitingAgentMoleRate")) {
				normalLimitinigMole = getNormalNumber(elementValueMap.get("limitingAgentMoleRate"),
						elementValueMap.get("limitingAgentMolerateUomId"));
			} else {
				String limitingMoleStr = generalDao.selectSingleStringNoException(
								"select fg_get_num_normal(molerate,MOLERATEUOM_ID) from fg_s_materialref_v t where t.PARENTID = '"
										+ elementValueMap.get("parentId") + "' and t.LIMITINGAGENT = 1 "
										+ generalUtilFormState.getWherePartForTmpData("materialref",
												elementValueMap.get("parentId")));
				if (!generalUtil.getNull(limitingMoleStr).isEmpty()) {
					normalLimitinigMole = Double.parseDouble(limitingMoleStr);
				}
			}
		}else{
		 limitingMole = Double.parseDouble(generalUtil.getEmpty(elementValueMap.get("limitingAgentMole"),
				generalDao.selectSingleStringNoException("select mole from fg_s_materialref_v t where t.PARENTID = '"
						+ elementValueMap.get("parentId") + "' and t.LIMITINGAGENT = 1 " + generalUtilFormState
								.getWherePartForTmpData("materialref", elementValueMap.get("parentId")))));
		 normalLimitinigMole = getNormalNumber(limitingMole.toString(), elementValueMap.get("MOLEUOM_ID"));
		}
		}
		catch(Exception e){
			limitingMole = null;
			normalLimitinigMole = null;
		}
		if (mainArgCode.equalsIgnoreCase("Yield")) {
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Product Mass Calculation =></br>");
				//Double normalYield = getNormalNumber(elementValueMap.get("yield"), elementValueMap.get("YIELDUOM_ID"));
				Double normalYield =  generalUtil.getNull(elementValueMap.get("yield")).isEmpty()?null:Double.parseDouble(elementValueMap.get("yield"));
				Double normalMw = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
						generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
				if (normalLimitinigMole == null || normalYield == null || normalMw == null) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on mass =" + elementValueMap.get("mass"),
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Limiting_agent_Mole*Yield*MW/100.</br> Normal Args:"
							+ String.valueOf(normalLimitinigMole) + "*" + normalYield + "*" + normalMw + "/100"
							+ "</br>");
					elementValueMap.put("mass",
							getFromNormalNumber(String.valueOf(normalLimitinigMole * normalYield * normalMw / 100),
									elementValueMap.get("MASSUOM_ID"), sbInfo));// ab- 070218 fixed bug 4545
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mass =" + elementValueMap.get("mass"),
						ActivitylogType.Calculation, formId);
			}

		} else if (mainArgCode.equalsIgnoreCase("Mass")) {
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Product Yield Calculation =></br>");
				Double normalMass = getNormalNumber(elementValueMap.get("mass"), elementValueMap.get("MASSUOM_ID"));
				Double normalMw = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
						generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
				if (normalMass == null || normalLimitinigMole == null || normalLimitinigMole * normalMw == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on yield. Parameters: Mass ='"
									+ elementValueMap.get("mass") + "';Mw = '"
									+ generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw) + "'",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Mass*100/(Limiting_agent_Mole*MW).</br> Normal Args:"
							+ String.valueOf(normalMass) + "*100/(" + String.valueOf(normalLimitinigMole) + "*"
							+ normalMw.toString() + ")" + "</br>");
					elementValueMap.put("yield",String.valueOf(normalMass * 100 / (normalLimitinigMole * normalMw)));
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation, "");
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on yield =" + elementValueMap.get("yield"),
						ActivitylogType.Calculation, formId);
			}

		}
		
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "calcLimitingAgentProduct - elementValueMap after calulation= mass:"
						+ elementValueMap.get("mass") + ",yield:" + elementValueMap.get("yield"),
				ActivitylogType.Calculation, formId);
	}

	private void updateLimitingAgentSibling(String mainArgCode, Map<String, String> elementValueMap, String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(
				formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),
				"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID,
				elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
		Map<String, String> run_uom_default = generalDao.sqlToHashMap("select * from fg_i_calcrun_uom_override_v");
		
		String formCode = formDao.getFormCodeBySeqId(formId);
		Double normalLimitinigMole = 0.0;
		if(mainArgCode.contains("Rate")){
			generalUtilLogger.logWriter(LevelType.DEBUG,
					"updateLimitingAgentSibling - elementValueMap before calulation= quantity rate:"
							+ elementValueMap.get("quantityRate") + ",volume rate:" + elementValueMap.get("volumeRate"),
					ActivitylogType.Calculation, formId);
			if (elementValueMap.containsKey("limitingAgentMoleRate")) {
				normalLimitinigMole = getNormalNumber(elementValueMap.get("limitingAgentMoleRate"),
						elementValueMap.get("limitingAgentMolerateUomId"));
			} else {
				String normalMole_LA = generalDao.selectSingleString(
						"select fg_get_num_normal(molerate,MOLERATEUOM_ID) from fg_s_materialref_v t where t.PARENTID = '"
								+ elementValueMap.get("parentId") + "' and t.LIMITINGAGENT = 1"
								+ generalUtilFormState.getWherePartForTmpData(formCode, elementValueMap.get("parentId")));
				if(!generalUtil.getNull(normalMole_LA).isEmpty()){
				normalLimitinigMole = Double.parseDouble(normalMole_LA);
				}
			}
		}
		else{
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"updateLimitingAgentSibling - elementValueMap before calulation= quantity:"
						+ elementValueMap.get("quantity") + ",volume:" + elementValueMap.get("volume"),
				ActivitylogType.Calculation, formId);
		if (elementValueMap.containsKey("limitingAgentMole")) {
			normalLimitinigMole = getNormalNumber(elementValueMap.get("limitingAgentMole"),
					elementValueMap.get("limitingAgentMoleUomId"));
		} else {
			normalLimitinigMole = Double.parseDouble(generalDao.selectSingleString(
					"select fg_get_num_normal(mole,MOLEUOM_ID) from fg_s_materialref_v t where t.PARENTID = '"
							+ elementValueMap.get("parentId") + "' and t.LIMITINGAGENT = 1"
							+ generalUtilFormState.getWherePartForTmpData(formCode, elementValueMap.get("parentId"))));
		}
		}
		//Double normalLimitinigMole = getNormalNumber(limitingMole.toString(), elementValueMap.get("MOLEUOM_ID"));
		Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
		String defaultMWUomId_runStep = (run_uom_default.get("DEFAULT_MW_UOM_ID") != null?run_uom_default.get("DEFAULT_MW_UOM_ID"):"").toString();	
		Double normalmwInf_runStep = getCustomNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId),defaultMWUomId_runStep,new StringBuilder());
		Double normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected

		Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"));
		String stepStatus = generalUtil.getEmpty(elementValueMap.get("stepStatusName"),
				generalUtil.getEmpty(
						formDao.getFromInfoLookup("STEP", LookupType.ID, elementValueMap.get("parentId"), "STATUSNAME"),
						"Planned"));
		
		
		if (stepStatus.equals("Active") && !mainArgCode.equalsIgnoreCase("equivalent")) {//calculating mole using current reactant data , and the calculate equevalent
			calcLimitingAgentSiblingActiveStep(mainArgCode, elementValueMap, formId);
		} 
		else 
		{ // calculation when Step is in PLANNED STATUS
			
			/*  QUANTITY */
			if (mainArgCode.equalsIgnoreCase("quantity")) {
				try {
					StringBuilder sbInfo = new StringBuilder();
					Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"),elementValueMap.get("QUANTITYUOM_ID"));
					sbInfo.append("Limiting agent Sibling Mole Calculation in Planned Step=></br>");
					if (normalQuantity == null || normalmwInf == null || normalmwInf == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling Mole [(Quantity/MW) * (Purity/100)] was not performed . Args: Quantity ='"
										+ normalQuantity + "'; Mw = " + normalmwInf + "''; Purity = '" + normalpurityInf
										+ "';",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: (Quantity/MW) * (Purity/100). </br> Normal Args: ("
								+ String.valueOf(normalQuantity) + "/" + normalmwInf.toString() + ")*(" + normalpurityInf
								+ "/100)" + "</br>");
						String mole = getFromNormalNumber(
								String.valueOf(normalQuantity / normalmwInf * normalpurityInf / 100),
								elementValueMap.get("MOLEUOM_ID"), sbInfo);
						elementValueMap.put("mole", mole);
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on mole =" + elementValueMap.get("mole"),
							ActivitylogType.Calculation, formId);
				}

				try {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Limiting agent Sibling Volume Calculation in Planned Step =></br>");
					Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"), elementValueMap.get("QUANTITYUOM_ID"));
					if (normalQuantity == null || normalDensityInf == null || normalDensityInf == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling [quantity/density] was not performed. Args: Quantity = '"
										+ elementValueMap.get("quantity") + "'; Density = '"
										+ elementValueMap.get("densityInf") + "'",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: Quantity/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
								+ normalDensityInf.toString() + "</br>");
						elementValueMap.put("volume",
								getFromNormalNumber(String.valueOf(normalQuantity / (normalDensityInf)),
										elementValueMap.get("VOLUOM_ID"), sbInfo));
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on volume =" + elementValueMap.get("volume"),
							ActivitylogType.Calculation, formId);
				}
				try {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Limiting agent Sibling Equivalent Calculation in Planned Step =></br>");
					Double normalMole = getNormalNumber(elementValueMap.get("mole"), elementValueMap.get("MOLEUOM_ID"));
					if (normalLimitinigMole == null || normalLimitinigMole == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling [current mole/limiting agent mole] was not performed. Args: Mole = '"
										+ elementValueMap.get("mole") + "'; Limiting Agent Mole = '" + normalLimitinigMole
										+ "'",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: Current_Mole/Limiting_Agent_Mole.</br> Normal Args:"
								+ String.valueOf(normalMole) + "/" + normalLimitinigMole.toString() + "</br>");
						elementValueMap.put("equivalent", String.valueOf(normalMole / normalLimitinigMole));
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on equivalent =" + elementValueMap.get("equivalent"),
							ActivitylogType.Calculation, formId);
				}
				
			} /*  QUANTITY RATE */
			else if (mainArgCode.equalsIgnoreCase("quantityRate")) {
				try {
					StringBuilder sbInfo = new StringBuilder();
					String defaultQuantityUomId = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();	
					Double normalQuantity = getCustomNormalNumber(elementValueMap.get("quantityRate"), elementValueMap.get("QUANTITYRATE_UOM"), defaultQuantityUomId, new StringBuilder());
					
					sbInfo.append("Limiting agent Sibling Mole Calculation in Planned Step=></br>");
					if (normalQuantity == null || normalmwInf == null || normalmwInf == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling Mole [(Quantity Rate/MW) * (Purity/100)] was not performed . Args: Quantity Rate ='"
										+ normalQuantity + "'; Mw = " + normalmwInf + "''; Purity = '" + normalpurityInf
										+ "';",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: (Quantity Rate/MW) * (Purity/100). </br> Normal Args: ("
								+ String.valueOf(normalQuantity) + "/" + normalmwInf.toString() + ")*(" + normalpurityInf
								+ "/100)" + "</br>");
						String mole = getFromNormalNumber(
								String.valueOf(normalQuantity / normalmwInf_runStep * normalpurityInf / 100),
								elementValueMap.get("MOLERATEUOM_ID"), sbInfo);
						elementValueMap.put("moleRate", mole);
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on mole rate=" + elementValueMap.get("moleRate"),
							ActivitylogType.Calculation, formId);
				}

				try {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Limiting agent Sibling Volume Calculation in Planned Step =></br>");
					Double normalQuantity = getNormalNumber(elementValueMap.get("quantityRate"), elementValueMap.get("QUANTITYRATE_UOM"));
					if (normalQuantity == null || normalDensityInf == null || normalDensityInf == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling [quantity rate/density] was not performed. Args: Quantity Rate= '"
										+ elementValueMap.get("quantityRate") + "'; Density = '"
										+ elementValueMap.get("densityInf") + "'",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: Quantity Rate/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
								+ normalDensityInf.toString() + "</br>");
						elementValueMap.put("volumeRate",
								getFromNormalNumber(String.valueOf(normalQuantity / (normalDensityInf)),
										elementValueMap.get("VOLRATEUOM_ID"), sbInfo));
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on volume rate =" + elementValueMap.get("volumeRate"),
							ActivitylogType.Calculation, formId);
				}
				try {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Limiting agent Sibling Equivalent Calculation in Planned Step =></br>");
					Double normalMole = getNormalNumber(elementValueMap.get("moleRate"), elementValueMap.get("MOLERATEUOM_ID"));
					if (normalLimitinigMole == null || normalLimitinigMole == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling [current mole rate/limiting agent mole] was not performed. Args: Mole Rate= '"
										+ elementValueMap.get("moleRate") + "'; Limiting Agent Mole = '" + normalLimitinigMole
										+ "'",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: Current_Mole/Limiting_Agent_Mole.</br> Normal Args:"
								+ String.valueOf(normalMole) + "/" + normalLimitinigMole.toString() + "</br>");
						elementValueMap.put("equivalent", String.valueOf(normalMole / normalLimitinigMole));
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on equivalent =" + elementValueMap.get("equivalent"),
							ActivitylogType.Calculation, formId);
				}
				
			} 
			/* VOLUME */
			else if (mainArgCode.equalsIgnoreCase("volume")) {
				try {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Limiting agent Sibling Quantity Calculation in Planned Step =></br>");
					Double normalVolume = getNormalNumber(elementValueMap.get("volume"), elementValueMap.get("VOLUOM_ID"));
					if (normalDensityInf == null || normalVolume == null) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling [volume*density] was not performed. Args: volume = '"
										+ elementValueMap.get("volume") + "'; Density = '"
										+ elementValueMap.get("densityInf") + "'",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: Volume*Density.</br> Normal Args:" + String.valueOf(normalVolume) + "*"
								+ normalDensityInf.toString() + "</br>");
						elementValueMap.put("quantity", getFromNormalNumber(String.valueOf(normalVolume * normalDensityInf),
								elementValueMap.get("QUANTITYUOM_ID"), sbInfo));
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on volume =" + elementValueMap.get("volume"),
							ActivitylogType.Calculation, formId);
				}
				try {
					StringBuilder sbInfo = new StringBuilder();
					Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"),
							elementValueMap.get("QUANTITYUOM_ID"));
					sbInfo.append("Limiting agent Sibling Mole Calculation in Planned Step=></br>");
					if (normalQuantity == null || normalmwInf == null || normalmwInf == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling Mole [(Quantity/MW) * (Purity/100)] was not performed . Args: Quantity ='"
										+ normalQuantity + "'; Mw = " + elementValueMap.get("mwInf") + "''; Purity = '"
										+ normalpurityInf + "';",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: (Quantity/MW) * (Purity/100). </br> Normal Args: ("
								+ String.valueOf(normalQuantity) + "/" + normalmwInf.toString() + ")*(" + normalpurityInf
								+ "/100)" + "</br>");
						String mole = getFromNormalNumber(
								String.valueOf(normalQuantity / normalmwInf * normalpurityInf / 100),
								elementValueMap.get("MOLEUOM_ID"), sbInfo);
						elementValueMap.put("mole", mole);
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on mole =" + elementValueMap.get("mole"),
							ActivitylogType.Calculation, formId);
				}
				try {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Limiting agent Sibling Equivalent Calculation in Planned Step =></br>");
					Double normalMole = getNormalNumber(elementValueMap.get("mole"), elementValueMap.get("MOLEUOM_ID"));
					if (normalLimitinigMole == null || normalLimitinigMole == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling [current mole/limiting agent mole] was not performed. Args: Mole = '"
										+ elementValueMap.get("mole") + "'; Limiting Agent Mole = '" + normalLimitinigMole
										+ "'",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: Current_Mole/Limiting_Agent_Mole.</br> Normal Args:"
								+ String.valueOf(normalMole) + "/" + normalLimitinigMole.toString() + "</br>");
						elementValueMap.put("equivalent", String.valueOf(normalMole / normalLimitinigMole));
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on equivalent =" + elementValueMap.get("equivalent"),
							ActivitylogType.Calculation, formId);
				}
			}
			/* VOLUME RATE*/
			else if (mainArgCode.equalsIgnoreCase("volumeRate")) {
				try {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Limiting agent Sibling Quantity Calculation in Planned Step =></br>");
					Double normalVolume = getNormalNumber(elementValueMap.get("volumeRate"), elementValueMap.get("VOLRATEUOM_ID"));
					if (normalDensityInf == null || normalVolume == null) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling [volume*density] was not performed. Args: volume rate= '"
										+ elementValueMap.get("volumeRate") + "'; Density = '"
										+ elementValueMap.get("densityInf") + "'",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: Volume Rate*Density.</br> Normal Args:" + String.valueOf(normalVolume) + "*"
								+ normalDensityInf.toString() + "</br>");
						elementValueMap.put("quantityRate", getFromNormalNumber(String.valueOf(normalVolume * normalDensityInf),
								elementValueMap.get("QUANTITYRATE_UOM"), sbInfo));
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on volume rate=" + elementValueMap.get("volumeRate"),
							ActivitylogType.Calculation, formId);
				}
				try {
					StringBuilder sbInfo = new StringBuilder();
					Double normalQuantity = getNormalNumber(elementValueMap.get("quantityRate"),
							elementValueMap.get("QUANTITYRATE_UOM"));
					sbInfo.append("Limiting agent Sibling Mole Calculation in Planned Step=></br>");
					if (normalQuantity == null || normalmwInf == null || normalmwInf == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling Mole [(Quantity Rate/MW) * (Purity/100)] was not performed . Args: Quantity ='"
										+ normalQuantity + "'; Mw = " + elementValueMap.get("mwInf") + "''; Purity = '"
										+ normalpurityInf + "';",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: (Quantity Rate/MW) * (Purity/100). </br> Normal Args: ("
								+ String.valueOf(normalQuantity) + "/" + normalmwInf.toString() + ")*(" + normalpurityInf
								+ "/100)" + "</br>");
						String mole = getFromNormalNumber(
								String.valueOf(normalQuantity / normalmwInf * normalpurityInf / 100),
								elementValueMap.get("MOLERATEUOM_ID"), sbInfo);
						elementValueMap.put("moleRate", mole);
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on mole rate=" + elementValueMap.get("moleRate"),
							ActivitylogType.Calculation, formId);
				}
				try {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Limiting agent Sibling Equivalent Calculation in Planned Step =></br>");
					Double normalMole = getNormalNumber(elementValueMap.get("moleRate"), elementValueMap.get("MOLERATEUOM_ID"));
					if (normalLimitinigMole == null || normalLimitinigMole == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling [current mole/limiting agent mole] was not performed. Args: Mole Rate = '"
										+ elementValueMap.get("moleRate") + "'; Limiting Agent Mole = '" + normalLimitinigMole
										+ "'",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: Current_Mole/Limiting_Agent_Mole.</br> Normal Args:"
								+ String.valueOf(normalMole) + "/" + normalLimitinigMole.toString() + "</br>");
						elementValueMap.put("equivalent", String.valueOf(normalMole / normalLimitinigMole));
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on equivalent =" + elementValueMap.get("equivalent"),
							ActivitylogType.Calculation, formId);
				}
			}
			/* MOLE */
			else if (mainArgCode.equalsIgnoreCase("mole")) {
				try {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Limiting agent Sibling Quantity Calculation in Planned Step =></br>");
					Double normalMole = getNormalNumber(elementValueMap.get("mole"), elementValueMap.get("MOLEUOM_ID"));
					if (normalMole == null || normalmwInf == null || normalpurityInf == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling [Mole*MW/(Purity/100)] was not performed . Args: Mole ='"
										+ normalMole + "'; Mw = " + normalmwInf + "''; Purity = '"
										+ normalpurityInf + "';",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: Mole*MW/purity/100.</br> Normal Args:" + String.valueOf(normalMole) + "*"
								+ normalmwInf.toString() + "/(" + normalpurityInf.toString() + "/100)" + "</br>");
						String qunt_ = getFromNormalNumber(
								String.valueOf(normalMole * normalmwInf / (normalpurityInf / 100)),
								elementValueMap.get("QUANTITYUOM_ID"), sbInfo);
						elementValueMap.put("quantity", qunt_);
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on quantity =" + elementValueMap.get("quantity"),
							ActivitylogType.Calculation, formId);
				}
				try {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Limiting agent Sibling Volume Calculation in Planned Step=></br>");
					Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"),
							elementValueMap.get("QUANTITYUOM_ID"));
					if (normalQuantity == null || normalDensityInf == null || normalDensityInf == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling [quantity/density] was not performed. Args: Quantity = '"
										+ elementValueMap.get("quantity") + "'; Density = '"
										+ elementValueMap.get("densityInf") + "'",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: Quantity/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
								+ normalDensityInf.toString() + "</br>");
						elementValueMap.put("volume",
								getFromNormalNumber(String.valueOf(normalQuantity / (normalDensityInf)),
										elementValueMap.get("VOLUOM_ID"), sbInfo));
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on volume =" + elementValueMap.get("volume"),
							ActivitylogType.Calculation, formId);
				}
				try {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Limiting agent Sibling Equivalent Calculation in Planned Step =></br>");
					Double normalMole = getNormalNumber(elementValueMap.get("mole"), elementValueMap.get("MOLEUOM_ID"));
					if (normalLimitinigMole == null || normalLimitinigMole == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling [current mole/limiting agent mole] was not performed. Args: Mole = '"
										+ elementValueMap.get("mole") + "'; Limiting Agent Mole = '" + normalLimitinigMole
										+ "'",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: Current_Mole/Limiting_Agent_Mole.</br> Normal Args:"
								+ String.valueOf(normalMole) + "/" + normalLimitinigMole.toString() + "</br>");
						elementValueMap.put("equivalent", String.valueOf(normalMole / normalLimitinigMole));
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on equivalent =" + elementValueMap.get("equivalent"),
							ActivitylogType.Calculation, formId);
				}
			}
			/* MOLE RATE*/
			else if (mainArgCode.equalsIgnoreCase("moleRate")) {
				try {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Limiting agent Sibling Quantity Calculation in Planned Step =></br>");
					Double normalMole = getNormalNumber(elementValueMap.get("moleRate"), elementValueMap.get("MOLERATEUOM_ID"));
					if (normalMole == null || normalmwInf == null || normalpurityInf == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling [Mole Rate*MW/(Purity/100)] was not performed . Args: Mole ='"
										+ normalMole + "'; Mw = " + normalmwInf + "''; Purity = '"
										+ normalpurityInf + "';",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: Mole Rate*MW/purity/100.</br> Normal Args:" + String.valueOf(normalMole) + "*"
								+ normalmwInf.toString() + "/(" + normalpurityInf.toString() + "/100)" + "</br>");
						String qunt_ = getFromNormalNumber(
								String.valueOf(normalMole * normalmwInf / (normalpurityInf / 100)),
								elementValueMap.get("QUANTITYRATE_UOM"), sbInfo);
						elementValueMap.put("quantityRate", qunt_);
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on quantity rate=" + elementValueMap.get("quantityRate"),
							ActivitylogType.Calculation, formId);
				}
				try {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Limiting agent Sibling Volume Calculation in Planned Step=></br>");
					Double normalQuantity = getNormalNumber(elementValueMap.get("quantityRate"),
							elementValueMap.get("QUANTITYRATE_UOM"));
					if (normalQuantity == null || normalDensityInf == null || normalDensityInf == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling [quantity rate/density] was not performed. Args: Quantity = '"
										+ elementValueMap.get("quantity rate") + "'; Density = '"
										+ elementValueMap.get("densityInf") + "'",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: Quantity Rate/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
								+ normalDensityInf.toString() + "</br>");
						elementValueMap.put("volumeRate",
								getFromNormalNumber(String.valueOf(normalQuantity / (normalDensityInf)),
										elementValueMap.get("VOLRATEUOM_ID"), sbInfo));
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on volume rate =" + elementValueMap.get("volumeRate"),
							ActivitylogType.Calculation, formId);
				}
				try {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Limiting agent Sibling Equivalent Calculation in Planned Step =></br>");
					Double normalMole = getNormalNumber(elementValueMap.get("moleRate"), elementValueMap.get("MOLERATEUOM_ID"));
					if (normalLimitinigMole == null || normalLimitinigMole == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling [current mole rate/limiting agent mole] was not performed. Args: Mole Rate= '"
										+ elementValueMap.get("moleRate") + "'; Limiting Agent Mole = '" + normalLimitinigMole
										+ "'",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: Current_Mole/Limiting_Agent_Mole.</br> Normal Args:"
								+ String.valueOf(normalMole) + "/" + normalLimitinigMole.toString() + "</br>");
						elementValueMap.put("equivalent", String.valueOf(normalMole / normalLimitinigMole));
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on equivalent =" + elementValueMap.get("equivalent"),
							ActivitylogType.Calculation, formId);
				}
			}
			/* EQUIVALENT */
			else if (mainArgCode.equalsIgnoreCase("equivalent")) {
				try {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Limiting agent Sibling Mole Calculation in Planned Step =></br>");
					Double equivalent = getNormalNumber(elementValueMap.get("equivalent"), null);
					if (equivalent == null || normalLimitinigMole == null) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling [limiting agent mole * equivalent] was not performed. Args: Equivalent = '"
										+ elementValueMap.get("equivalent") + "'; Limiting Agent Mole = '" + normalLimitinigMole
										+ "'",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: Limiting Agent Mole * Equivalent.</br> Normal Args:"
								+ String.valueOf(normalLimitinigMole) + "*" + equivalent.toString() + "</br>");						
						String crnt_mole = getFromNormalNumber(String.valueOf(normalLimitinigMole * equivalent),elementValueMap.get("MOLEUOM_ID"), sbInfo);
						elementValueMap.put("mole", crnt_mole);
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on mole =" + elementValueMap.get("mole"),
							ActivitylogType.Calculation, formId);
				}
				try {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Limiting agent Sibling Quantity Calculation in Planned Step =></br>");
					Double normalMole = getNormalNumber(elementValueMap.get("mole"), elementValueMap.get("MOLEUOM_ID"));
					if (normalMole == null || normalmwInf == null || normalpurityInf == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling [Mole*MW/(Purity/100)] was not performed . Args: Mole ='"
										+ normalMole + "'; Mw = " + normalmwInf + "''; Purity = '"
										+ normalpurityInf + "';",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: Mole*MW/purity/100.</br> Normal Args:" + String.valueOf(normalMole) + "*"
								+ normalmwInf.toString() + "/(" + normalpurityInf.toString() + "/100)" + "</br>");
						String qunt_ = getFromNormalNumber(
								String.valueOf(normalMole * normalmwInf / (normalpurityInf / 100)),
								elementValueMap.get("QUANTITYUOM_ID"), sbInfo);
						elementValueMap.put("quantity", qunt_);
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on quantity =" + elementValueMap.get("quantity"),
							ActivitylogType.Calculation, formId);
				}
				try {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Limiting agent Sibling Volume Calculation in Planned Step=></br>");
					Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"), elementValueMap.get("QUANTITYUOM_ID"));
					if (normalQuantity == null || normalDensityInf == null || normalDensityInf == 0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting agent sibling [quantity/density] was not performed. Args: Quantity = '"
										+ elementValueMap.get("quantity") + "'; Density = '"
										+ elementValueMap.get("densityInf") + "'",
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: Quantity/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
								+ normalDensityInf.toString() + "</br>");
						elementValueMap.put("volume",
								getFromNormalNumber(String.valueOf(normalQuantity / (normalDensityInf)),
										elementValueMap.get("VOLUOM_ID"), sbInfo));
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on volume =" + elementValueMap.get("volume"),
							ActivitylogType.Calculation, formId);
				}
			}
		}
		if(mainArgCode.contains("Rate")){
			generalUtilLogger.logWriter(LevelType.DEBUG,
					"updateLimitingAgentSibling - elementValueMap after calulation= quantity rate:"
							+ elementValueMap.get("quantityRate") + ",volume rate:" + elementValueMap.get("volumeRate"),
					ActivitylogType.Calculation, formId);
		}
		else{
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"updateLimitingAgentSibling - elementValueMap after calulation= quantity:"
						+ elementValueMap.get("quantity") + ",volume:" + elementValueMap.get("volume"),
				ActivitylogType.Calculation, formId);
		}
	}

	private void calcLimitingAgentSiblingActiveStep(String mainArgCode, Map<String, String> elementValueMap,
			String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(
				formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),
				"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID,
				elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
		String formCode = formDao.getFormCodeBySeqId(formId);
		Double normalLimitinigMole = 0.0;
		if(mainArgCode.contains("Rate")){
			if (elementValueMap.containsKey("limitingAgentMoleRate")) {
				normalLimitinigMole = getNormalNumber(elementValueMap.get("limitingAgentMoleRate"),
						elementValueMap.get("limitingAgentMoleRateUomId"));
			} else {
				String normalMole_LA = generalDao.selectSingleString(
						"select fg_get_num_normal(moleRate,MOLERATEUOM_ID) from fg_s_materialref_v t where t.PARENTID = '"
								+ elementValueMap.get("parentId") + "' and t.LIMITINGAGENT = 1"
								+ generalUtilFormState.getWherePartForTmpData(formCode, elementValueMap.get("parentId")));
				if(!generalUtil.getNull(normalMole_LA).isEmpty()){
					normalLimitinigMole = Double.parseDouble(normalMole_LA);
				}
			}
	  }
		else{
			if (elementValueMap.containsKey("limitingAgentMole")) {
				normalLimitinigMole = getNormalNumber(elementValueMap.get("limitingAgentMole"),
						elementValueMap.get("limitingAgentMoleUomId"));
			} else {
				normalLimitinigMole = Double.parseDouble(generalDao.selectSingleString(
						"select fg_get_num_normal(mole,MOLEUOM_ID) from fg_s_materialref_v t where t.PARENTID = '"
								+ elementValueMap.get("parentId") + "' and t.LIMITINGAGENT = 1"
								+ generalUtilFormState.getWherePartForTmpData(formCode, elementValueMap.get("parentId"))));
			}
		}
		Map<String, String> run_uom_default = generalDao.sqlToHashMap("select * from fg_i_calcrun_uom_override_v");
		
		//Double normalLimitinigMole = getNormalNumber(limitingMole.toString(), elementValueMap.get("MOLEUOM_ID"));
		Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
		Double normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected
		Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"));
		String defaultMWUomId_runStep = (run_uom_default.get("DEFAULT_MW_UOM_ID") != null?run_uom_default.get("DEFAULT_MW_UOM_ID"):"").toString();	
		Double normalmwInf_runStep = getCustomNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId),defaultMWUomId_runStep,new StringBuilder());
		String defaultdensityUomId_runStep = (run_uom_default.get("DEFAULT_DENSITY_UOM_ID") != null?run_uom_default.get("DEFAULT_DENSITY_UOM_ID"):"").toString();	
		Double normalDensityInf_runStep = getCustomNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"), defaultdensityUomId_runStep, new StringBuilder()); 
		
		if (mainArgCode.equalsIgnoreCase("quantity")) {
			try {
				StringBuilder sbInfo = new StringBuilder();
				Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"),
						elementValueMap.get("QUANTITYUOM_ID"));
				sbInfo.append("Limiting agent Sibling Mole Calculation in Active Step=></br>");
				if (normalQuantity == null || normalmwInf == null || normalmwInf == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling Mole [(Quantity/MW) * (Purity/100)] was not performed . Args: Quantity ='"
									+ normalQuantity + "'; Mw = " + normalmwInf + "''; Purity = '" + normalpurityInf
									+ "';",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: (Quantity/MW) * (Purity/100). </br> Normal Args: ("
							+ String.valueOf(normalQuantity) + "/" + normalmwInf.toString() + ")*(" + normalpurityInf
							+ "/100)" + "</br>");
					String mole = getFromNormalNumber(
							String.valueOf(normalQuantity / normalmwInf * normalpurityInf / 100),
							elementValueMap.get("MOLEUOM_ID"), sbInfo);
					elementValueMap.put("mole", mole);
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole =" + elementValueMap.get("mole"),
						ActivitylogType.Calculation, formId);
			}

			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent Sibling Volume Calculation in Active Step =></br>");
				Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"),
						elementValueMap.get("QUANTITYUOM_ID"));
				if (normalQuantity == null || normalDensityInf == null || normalDensityInf == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling [quantity/density] was not performed. Args: Quantity = '"
									+ elementValueMap.get("quantity") + "'; Density = '"
									+ elementValueMap.get("densityInf") + "'",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Quantity/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
							+ normalDensityInf.toString() + "</br>");
					elementValueMap.put("volume",
							getFromNormalNumber(String.valueOf(normalQuantity / (normalDensityInf)),
									elementValueMap.get("VOLUOM_ID"), sbInfo));
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume =" + elementValueMap.get("volume"),
						ActivitylogType.Calculation, formId);
			}
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent Sibling Equivalent Calculation in Active Step =></br>");
				Double normalMole = getNormalNumber(elementValueMap.get("mole"), elementValueMap.get("MOLEUOM_ID"));
				if (normalLimitinigMole == null || normalLimitinigMole == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling [current mole/limiting agent mole] was not performed. Args: Mole = '"
									+ elementValueMap.get("mole") + "'; Limiting Agent Mole = '" + normalLimitinigMole
									+ "'",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Current_Mole/Limiting_Agent_Mole.</br> Normal Args:"
							+ String.valueOf(normalMole) + "/" + normalLimitinigMole.toString() + "</br>");
					elementValueMap.put("equivalent", String.valueOf(normalMole / normalLimitinigMole));
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on equivalent =" + elementValueMap.get("equivalent"),
						ActivitylogType.Calculation, formId);
			}
		} else if (mainArgCode.equalsIgnoreCase("quantityRate")) {
			try {
				StringBuilder sbInfo = new StringBuilder();
				String defaultQuantity = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();	
				Double normalQuantity = getCustomNormalNumber(elementValueMap.get("quantityRate"),
						elementValueMap.get("QUANTITYRATE_UOM"),defaultQuantity,sbInfo);
				sbInfo.append("Limiting agent Sibling Mole Calculation in Active Step=></br>");
				if (normalQuantity == null || normalmwInf_runStep == null || normalmwInf_runStep == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling Mole [(Quantity Rate/MW) * (Purity/100)] was not performed . Args: Quantity Rate='"
									+ normalQuantity + "'; Mw = " + normalmwInf_runStep + "''; Purity = '" + normalpurityInf
									+ "';",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: (Quantity Rate/MW) * (Purity/100). </br> Normal Args: ("
							+ String.valueOf(normalQuantity) + "/" + normalmwInf_runStep.toString() + ")*(" + normalpurityInf
							+ "/100)" + "</br>");
					String mole = getFromNormalNumber(
							String.valueOf((normalQuantity / normalmwInf_runStep) * (normalpurityInf / 100)),
							elementValueMap.get("MOLERATEUOM_ID"), sbInfo);
					elementValueMap.put("moleRate", mole);
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole rate =" + elementValueMap.get("moleRate"),
						ActivitylogType.Calculation, formId);
			}

			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent Sibling Volume Calculation in Active Step =></br>");
				String defaultQuantity = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();	
				Double normalQuantity = getCustomNormalNumber(elementValueMap.get("quantityRate"),
						elementValueMap.get("QUANTITYRATE_UOM"),defaultQuantity,sbInfo);
				
				if (normalQuantity == null || normalDensityInf_runStep == null || normalDensityInf_runStep == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling [quantity rate/density] was not performed. Args: Quantity Rate= '"
									+ elementValueMap.get("quantityRate") + "'; Density = '"
									+ elementValueMap.get("densityInf") + "'",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Quantity Rate/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
							+ normalDensityInf_runStep.toString() + "</br>");
					String defaultVolumeUOM = (run_uom_default.get("DEFAULT_VOLUME_UOM_ID") != null?run_uom_default.get("DEFAULT_VOLUME_UOM_ID"):"").toString();	
					String volume = getCustomNormalNumber(String.valueOf(normalQuantity / (normalDensityInf_runStep)),defaultVolumeUOM,
							elementValueMap.get("VOLRATEUOM_ID"), sbInfo).toString();
					elementValueMap.put("volumeRate",volume);
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume rate=" + elementValueMap.get("volumeRate"),
						ActivitylogType.Calculation, formId);
			}
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent Sibling Equivalent Calculation in Active Step =></br>");
				Double normalMole = getNormalNumber(elementValueMap.get("moleRate"), elementValueMap.get("MOLERATEUOM_ID"));
				if (normalLimitinigMole == null || normalLimitinigMole == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling [current mole/limiting agent mole] was not performed. Args: Mole = '"
									+ elementValueMap.get("moleRate") + "'; Limiting Agent Mole = '" + normalLimitinigMole
									+ "'",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Current_Mole/Limiting_Agent_Mole.</br> Normal Args:"
							+ String.valueOf(normalMole) + "/" + normalLimitinigMole.toString() + "</br>");
					elementValueMap.put("equivalent", String.valueOf(normalMole / normalLimitinigMole));
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on equivalent =" + elementValueMap.get("equivalent"),
						ActivitylogType.Calculation, formId);
			}
			//add equevalent calc and mole and quantity calc up to volume, and up to mole
		}else if (mainArgCode.equalsIgnoreCase("volume")) {
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent Sibling Quantity Calculation in Active Step =></br>");
				Double normalVolume = getNormalNumber(elementValueMap.get("volume"), elementValueMap.get("VOLUOM_ID"));
				if (normalDensityInf == null || normalVolume == null) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling [volume*density] was not performed. Args: volume = '"
									+ elementValueMap.get("volume") + "'; Density = '"
									+ elementValueMap.get("densityInf") + "'",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Volume*Density.</br> Normal Args:" + String.valueOf(normalVolume) + "*"
							+ normalDensityInf.toString() + "</br>");
					elementValueMap.put("quantity", getFromNormalNumber(String.valueOf(normalVolume * normalDensityInf),
							elementValueMap.get("QUANTITYUOM_ID"), sbInfo));
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume =" + elementValueMap.get("volume"),
						ActivitylogType.Calculation, formId);
			}
			try {
				StringBuilder sbInfo = new StringBuilder();
				Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"),
						elementValueMap.get("QUANTITYUOM_ID"));
				sbInfo.append("Limiting agent Sibling Mole Calculation in Active Step=></br>");
				if (normalQuantity == null || normalmwInf == null || normalmwInf == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling Mole [(Quantity/MW) * (Purity/100)] was not performed . Args: Quantity ='"
									+ normalQuantity + "'; Mw = " + elementValueMap.get("mwInf") + "''; Purity = '"
									+ normalpurityInf + "';",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: (Quantity/MW) * (Purity/100). </br> Normal Args: ("
							+ String.valueOf(normalQuantity) + "/" + normalmwInf.toString() + ")*(" + normalpurityInf
							+ "/100)" + "</br>");
					String mole = getFromNormalNumber(
							String.valueOf(normalQuantity / normalmwInf * normalpurityInf / 100),
							elementValueMap.get("MOLEUOM_ID"), sbInfo);
					elementValueMap.put("mole", mole);
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole =" + elementValueMap.get("mole"),
						ActivitylogType.Calculation, formId);
			}
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent Sibling Equivalent Calculation in Active Step =></br>");
				Double normalMole = getNormalNumber(elementValueMap.get("mole"), elementValueMap.get("MOLEUOM_ID"));
				if (normalLimitinigMole == null || normalLimitinigMole == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling [current mole/limiting agent mole] was not performed. Args: Mole = '"
									+ elementValueMap.get("mole") + "'; Limiting Agent Mole = '" + normalLimitinigMole
									+ "'",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Current_Mole/Limiting_Agent_Mole.</br> Normal Args:"
							+ String.valueOf(normalMole) + "/" + normalLimitinigMole.toString() + "</br>");
					elementValueMap.put("equivalent", String.valueOf(normalMole / normalLimitinigMole));
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on equivalent =" + elementValueMap.get("equivalent"),
						ActivitylogType.Calculation, formId);
			}
		} else if (mainArgCode.equalsIgnoreCase("volumeRate")) {
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent Sibling Quantity Calculation in Active Step =></br>");
				String defaultVolumeUOM = (run_uom_default.get("DEFAULT_VOLUME_UOM_ID") != null?run_uom_default.get("DEFAULT_VOLUME_UOM_ID"):"").toString();
				Double normalVolume = getCustomNormalNumber(elementValueMap.get("volumeRate"), elementValueMap.get("VOLRATEUOM_ID"), defaultVolumeUOM, sbInfo);
				if (normalDensityInf_runStep == null || normalVolume == null) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling [volume*density] was not performed. Args: volume rate = '"
									+ elementValueMap.get("volumeRate") + "'; Density = '"
									+ elementValueMap.get("densityInf") + "'",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Volume*Density.</br> Normal Args:" + String.valueOf(normalVolume) + "*"
							+ normalDensityInf_runStep.toString() + "</br>");
					String defaultQuantityUOM = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();
					elementValueMap.put("quantityRate", getCustomNormalNumber(String.valueOf(normalVolume * normalDensityInf_runStep),defaultQuantityUOM,
							elementValueMap.get("QUANTITYRATE_UOM"), sbInfo).toString());
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation, formId);
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume rate=" + elementValueMap.get("volumeRate"),
						ActivitylogType.Calculation, formId);
			}
			try {
				StringBuilder sbInfo = new StringBuilder();
				/*Double normalQuantity = getNormalNumber(elementValueMap.get("quantityRate"),
						elementValueMap.get("QUANTITYRATE_UOM"));*/
				String defaultQuantity = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();	
				Double normalQuantity = getCustomNormalNumber(elementValueMap.get("quantityRate"),
						elementValueMap.get("QUANTITYRATE_UOM"),defaultQuantity,sbInfo);
				sbInfo.append("Limiting agent Sibling Mole Calculation in Active Step=></br>");
				if (normalQuantity == null || normalmwInf_runStep == null || normalmwInf_runStep == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling Mole [(Quantity/MW) * (Purity/100)] was not performed . Args: Quantity ='"
									+ normalQuantity + "'; Mw = " + elementValueMap.get("mwInf") + "''; Purity = '"
									+ normalpurityInf + "';",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: (Quantity/MW) * (Purity/100). </br> Normal Args: ("
							+ String.valueOf(normalQuantity) + "/" + normalmwInf_runStep.toString() + ")*(" + normalpurityInf
							+ "/100)" + "</br>");
					String mole = getFromNormalNumber(
							String.valueOf(normalQuantity / normalmwInf_runStep * normalpurityInf / 100),
							elementValueMap.get("MOLERATEUOM_ID"), sbInfo);
					elementValueMap.put("moleRate", mole);
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole rate=" + elementValueMap.get("moleRate"),
						ActivitylogType.Calculation, formId);
			}
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent Sibling Equivalent Calculation in Active Step =></br>");
				Double normalMole = getNormalNumber(elementValueMap.get("moleRate"), elementValueMap.get("MOLERATEUOM_ID"));
				if (normalLimitinigMole == null || normalLimitinigMole == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling [current mole/limiting agent mole] was not performed. Args: Mole = '"
									+ elementValueMap.get("moleRate") + "'; Limiting Agent Mole = '" + normalLimitinigMole
									+ "'",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Current_Mole/Limiting_Agent_Mole.</br> Normal Args:"
							+ String.valueOf(normalMole) + "/" + normalLimitinigMole.toString() + "</br>");
					elementValueMap.put("equivalent", String.valueOf(normalMole / normalLimitinigMole));
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on equivalent =" + elementValueMap.get("equivalent"),
						ActivitylogType.Calculation, formId);
			}
		} else if (mainArgCode.equalsIgnoreCase("mole")) {
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent Sibling Quantity Calculation in Active Step =></br>");
				Double normalMole = getNormalNumber(elementValueMap.get("mole"), elementValueMap.get("MOLEUOM_ID"));
				if (normalLimitinigMole == null || normalmwInf == null || normalpurityInf == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling [limitedMole*MW*equivalent/(Purity/100)] was not performed . Args: Mole ='"
									+ normalLimitinigMole + "'; Mw = " + normalmwInf + "''; Purity = '"
									+ normalpurityInf + "';",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Mole*MW/purity/100.</br> Normal Args:" + String.valueOf(normalMole) + "*"
							+ normalmwInf.toString() + "/(" + normalpurityInf.toString() + "/100)" + "</br>");
					String qunt_ = getFromNormalNumber(
							String.valueOf(normalMole * normalmwInf / (normalpurityInf / 100)),
							elementValueMap.get("QUANTITYUOM_ID"), sbInfo);
					elementValueMap.put("quantity", qunt_);
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on quantity =" + elementValueMap.get("quantity"),
						ActivitylogType.Calculation, formId);
			}
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent Sibling Volume Calculation in Active Step=></br>");
				Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"),
						elementValueMap.get("QUANTITYUOM_ID"));
				if (normalQuantity == null || normalDensityInf == null || normalDensityInf == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling [quantity/density] was not performed. Args: Quantity = '"
									+ elementValueMap.get("quantity") + "'; Density = '"
									+ elementValueMap.get("densityInf") + "'",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Quantity/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
							+ normalDensityInf.toString() + "</br>");
					elementValueMap.put("volume",
							getFromNormalNumber(String.valueOf(normalQuantity / (normalDensityInf)),
									elementValueMap.get("VOLUOM_ID"), sbInfo));
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume =" + elementValueMap.get("volume"),
						ActivitylogType.Calculation, formId);
			}
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent Sibling Equivalent Calculation in Active Step =></br>");
				Double normalMole = getNormalNumber(elementValueMap.get("mole"), elementValueMap.get("MOLEUOM_ID"));
				if (normalLimitinigMole == null || normalLimitinigMole == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling [current mole/limiting agent mole] was not performed. Args: Mole = '"
									+ elementValueMap.get("mole") + "'; Limiting Agent Mole = '" + normalLimitinigMole
									+ "'",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Current_Mole/Limiting_Agent_Mole.</br> Normal Args:"
							+ String.valueOf(normalMole) + "/" + normalLimitinigMole.toString() + "</br>");
					elementValueMap.put("equivalent", String.valueOf(normalMole / normalLimitinigMole));
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on equivalent =" + elementValueMap.get("equivalent"),
						ActivitylogType.Calculation, formId);
			}
		}
		else if (mainArgCode.equalsIgnoreCase("moleRate")) {
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent Sibling Quantity Calculation in Active Step =></br>");
				Double normalMole = getNormalNumber(elementValueMap.get("moleRate"), elementValueMap.get("MOLERATEUOM_ID"));
				if (normalLimitinigMole == null || normalmwInf_runStep == null || normalpurityInf == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling [limitedMole*MW*equivalent/(Purity/100)] was not performed . Args: Mole ='"
									+ normalLimitinigMole + "'; Mw = " + normalmwInf_runStep + "''; Purity = '"
									+ normalpurityInf + "';",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Mole*MW/purity/100.</br> Normal Args:" + String.valueOf(normalMole) + "*"
							+ normalmwInf_runStep.toString() + "/(" + normalpurityInf.toString() + "/100)" + "</br>");
					String defaultQuantityUOM = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();
					String qunt_ = getCustomNormalNumber(
							String.valueOf(normalMole * normalmwInf_runStep / (normalpurityInf / 100)), defaultQuantityUOM,
							elementValueMap.get("QUANTITYRATE_UOM"), sbInfo).toString();
					elementValueMap.put("quantityRate", qunt_);
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on quantity rate =" + elementValueMap.get("quantityRate"),
						ActivitylogType.Calculation, formId);
			}
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent Sibling Volume Calculation in Active Step=></br>");
				String defaultQuantityUOM = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();	
				Double normalQuantity = getCustomNormalNumber(elementValueMap.get("quantityRate"),
						elementValueMap.get("QUANTITYRATE_UOM"),defaultQuantityUOM,sbInfo);
				
				if (normalQuantity == null || normalDensityInf_runStep == null || normalDensityInf_runStep == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling [quantity/density] was not performed. Args: Quantity Rate = '"
									+ elementValueMap.get("quantityRate") + "'; Density = '"
									+ elementValueMap.get("densityInf") + "'",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Quantity/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
							+ normalDensityInf_runStep.toString() + "</br>");
					String defaultVolumeUOM = (run_uom_default.get("DEFAULT_VOLUME_UOM_ID") != null?run_uom_default.get("DEFAULT_VOLUME_UOM_ID"):"").toString();	
					elementValueMap.put("volumeRate",
							getCustomNormalNumber(String.valueOf(normalQuantity / (normalDensityInf_runStep)),defaultVolumeUOM,
									elementValueMap.get("VOLRATEUOM_ID"), sbInfo).toString());
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume rate =" + elementValueMap.get("volumeRate"),
						ActivitylogType.Calculation, formId);
			}
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent Sibling Equivalent Calculation in Active Step =></br>");
				Double normalMole = getNormalNumber(elementValueMap.get("moleRate"), elementValueMap.get("MOLERATEUOM_ID"));
				if (normalLimitinigMole == null || normalLimitinigMole == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent sibling [current mole/limiting agent mole] was not performed. Args: Mole = '"
									+ elementValueMap.get("moleRate") + "'; Limiting Agent Mole = '" + normalLimitinigMole
									+ "'",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Current_Mole/Limiting_Agent_Mole.</br> Normal Args:"
							+ String.valueOf(normalMole) + "/" + normalLimitinigMole.toString() + "</br>");
					elementValueMap.put("equivalent", String.valueOf(normalMole / normalLimitinigMole));
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on equivalent =" + elementValueMap.get("equivalent"),
						ActivitylogType.Calculation, formId);
			}
		}
	}

	private void calcLimitingAgentSiblings(Map<String, String> elementValueMap, String formId) {
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"calcLimitingAgentSiblings - update elementValueMap in db ," + elementValueMap.toString(),
				ActivitylogType.Calculation, formId);
		Double normalLimitingMole = getNormalNumber(elementValueMap.get("mole"), elementValueMap.get("MOLEUOM_ID"));
		if (normalLimitingMole == null) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed. Missing args -> mole " + elementValueMap.get("mole"),
					ActivitylogType.Calculation, formId);
		} else {
			List<String> stepSiblingList = generalDao
					.getListOfStringBySql(
							"select t.formid from fg_s_materialref_all_v t where t.TABLETYPE='Reactant' and t.PARENTID = '"
									+ elementValueMap.get("parentId") + "'" + generalUtilFormState
											.getWherePartForTmpData("MaterialRef", elementValueMap.get("parentId"))
									+ " and formId<>'" + formId + "'");// gets of the siblings of the current step
			String stepStatus = generalDao
					.selectSingleString("Select STEPSTATUSNAME from fg_s_step_all_v where formId = '"
							+ elementValueMap.get("parentId") + "'");
			for (String siblingFormId : stepSiblingList) {
				try {
					StringBuilder sbInfo = new StringBuilder();
					Map<String, String> siblingData = generalDao.getMetaDataRowValues(
							"select distinct nvl(PURITYINF,100) as PURITY, PURITYUOM_ID_INF," + "MWINF,MWUOM_ID_INF,"
									+ "EQUIVALENT," + "DENSITYINF,DENSITYUOM_ID_INF," + "QUANTITYUOM_ID," + "VOLUOM_ID,"
									+ "MOLE,MOLEUOM_ID  from fg_s_materialref_v where FORMID = '" + siblingFormId
									+ "'");// Default value 100 when batch is not selected

					if (stepStatus.equals("Active")) {//calculating equivalent using current reactant data
						Double normalMole = getNormalNumber(siblingData.get("MOLE"), siblingData.get("MOLEUOM_ID"));
						if (normalMole == null) {
							generalUtilLogger.logWriter(LevelType.WARN,
									"The equivalent calculation of sibling " + siblingFormId
											+ " was not performed. Missing args -> mole ",
									ActivitylogType.Calculation, formId);
						} else {
							String eq = String.valueOf(normalMole / normalLimitingMole);
							formSaveDao.updateStructTableByFormId(
									"update fg_s_materialref_pivot t set EQUIVALENT = '" + eq + "' where FORMID = '"
											+ siblingFormId + "'",
									"fg_s_materialref_pivot", Arrays.asList("EQUIVALENT"), siblingFormId);
						}
					} else {
						if (generalUtil.getNull(siblingData.get("EQUIVALENT")).isEmpty()) {
							generalUtilLogger.logWriter(LevelType.WARN,
									"The mole calculation of sibling " + siblingFormId
											+ " was not performed. Missing args -> equivalent ",
									ActivitylogType.Calculation, formId);
						} else {
							String moleSibling = getFromNormalNumber(
									String.valueOf(
											normalLimitingMole * Double.parseDouble(siblingData.get("EQUIVALENT"))),
									siblingData.get("MOLEUOM_ID"), sbInfo);
							sbInfo.append("Limiting agent Sibling Mole Calculatin =></br>");
							sbInfo.append("Formula: Limiting_agent_Mole * Equivalent.</br> Normal Args:"
									+ String.valueOf(normalLimitingMole) + "*" + siblingData.get("EQUIVALENT")
									+ "</br>");
							formSaveDao.updateStructTableByFormId(
									"update fg_s_materialref_pivot t set MOLE = '" + moleSibling + "' where FORMID = '"
											+ siblingFormId + "'",
									"fg_s_materialref_pivot", Arrays.asList("MOLE"), siblingFormId);
						}
						Double normalMwSibling = getNormalNumber(siblingData.get("MWINF"),
								siblingData.get("MWUOM_ID_INF"));
						Double normalPuritySibling = getNormalNumber(siblingData.get("PURITY"),
								siblingData.get("PURITYUOM_ID_INF"), (double) 100);
						Double normaldensitysibling = getNormalNumber(siblingData.get("DENSITYINF"),
								siblingData.get("DENSITYUOM_ID_INF"));

						if (normalMwSibling == null || generalUtil.getNull(siblingData.get("EQUIVALENT")).isEmpty()) {
							generalUtilLogger.logWriter(LevelType.WARN,
									"The calculation of limiting sibling Quantity was not performed. Missing args -> MW of limiting sibling. formId="
											+ siblingFormId,
									ActivitylogType.Calculation, formId);
						} else {
							sbInfo = new StringBuilder();
							sbInfo.append("Limiting agent Sibling Quantity Calculation =></br>");
							sbInfo.append("Formula: Limiting_agent_Mole* MW*Equivalent/(purity/100).</br> Normal Args:"
									+ String.valueOf(normalLimitingMole) + "*" + normalMwSibling.toString() + "*"
									+ siblingData.get("EQUIVALENT") + "/(" + normalPuritySibling.toString() + "/100)"
									+ "</br>");
							String quantitySibling = getFromNormalNumber(String.valueOf(normalLimitingMole
									* normalMwSibling * Double.parseDouble(siblingData.get("EQUIVALENT"))
									/ (normalPuritySibling / 100)), siblingData.get("QUANTITYUOM_ID"), sbInfo);

							double normalQuantitySibling = getNormalNumber(quantitySibling,
									siblingData.get("QUANTITYUOM_ID"));
							formSaveDao.updateStructTableByFormId(
									"update fg_s_materialref_pivot t set QUANTITY = '" + quantitySibling
											+ "' where FORMID = '" + siblingFormId + "'",
									"fg_s_materialref_pivot", Arrays.asList("QUANTITY"), siblingFormId);
							try {
								sbInfo = new StringBuilder();
								if (normaldensitysibling == null || normaldensitysibling == (double) 0) {
									generalUtilLogger.logWriter(LevelType.WARN,
											"The calculation of limiting sibling Volume was not performed. Normal Args -> density of limiting sibling = "
													+ normaldensitysibling + ". formId=" + siblingFormId,
											ActivitylogType.Calculation, formId);
								} else {
									sbInfo.append("Limiting agent Sibling Volume Calculation =></br>");
									sbInfo.append("Formula: Quantity/Density.</br> Normal Args:"
											+ String.valueOf(normalQuantitySibling) + "/"
											+ normaldensitysibling.toString() + "</br>");
									String volumesibling = getFromNormalNumber(
											String.valueOf(normalQuantitySibling / normaldensitysibling),
											siblingData.get("VOLUOM_ID"), sbInfo);
									formSaveDao.updateStructTableByFormId(
											"update fg_s_materialref_pivot t set VOLUME = '" + volumesibling
													+ "' where FORMID = '" + siblingFormId + "'",
											"fg_s_materialref_pivot", Arrays.asList("VOLUME"), siblingFormId);
								}
							} catch (Exception ex) {
								generalUtilLogger.logWriter(LevelType.WARN,
										"The calculation was not performed on volume =" + elementValueMap.get("volume"),
										ActivitylogType.Calculation, formId);
							}
						}
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on quantity =" + elementValueMap.get("volume"),
							ActivitylogType.Calculation, formId);
				}
			}
			List<String> productSiblingList = generalDao.getListOfStringBySql(
					"select t.formid from fg_s_materialref_all_v t where t.TABLETYPE='Product' and t.PARENTID = '"
							+ elementValueMap.get("parentId") + "'" + generalUtilFormState
									.getWherePartForTmpData("MaterialRef", elementValueMap.get("parentId")));// gets of the siblings of the current step
			for (String siblingFormId : productSiblingList) {
				try {
					StringBuilder sbInfo = new StringBuilder();
					Map<String, String> siblingData = generalDao.getMetaDataRowValues(
							"select distinct yield, YIELDUOM_ID,mwInf,MWUOM_ID_INF," + "MASSUOM_ID,"
									+ "MOLEUOM_ID from fg_s_materialref_v where FORMID = '" + siblingFormId + "'");
					String moleSibling = getFromNormalNumber(normalLimitingMole.toString(),
							siblingData.get("MOLEUOM_ID"), sbInfo);
					sbInfo.append("Limiting agent Sibling Mole Calculatin =></br>");
					sbInfo.append("Formula: Limiting_agent_Mole.</br> Normal Args:" + String.valueOf(normalLimitingMole)
							+ "</br>");
					formSaveDao.updateStructTableByFormId(
							"update fg_s_materialref_pivot t set MOLE = '" + moleSibling + "' where FORMID = '"
									+ siblingFormId + "'",
							"fg_s_materialref_pivot", Arrays.asList("MOLE"), siblingFormId);
					/*Double normalYieldSibling = getNormalNumber(siblingData.get("YIELD"),
							siblingData.get("YIELDUOM_ID"));*/
					Double normalYieldSibling =  generalUtil.getNull(siblingData.get("yield")).isEmpty()?null:Double.parseDouble(siblingData.get("yield"));
					Double normalMw = getNormalNumber(siblingData.get("MWINF"), siblingData.get("MWUOM_ID_INF"));
					if (normalYieldSibling == null || normalMw == null) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation of limiting sibling Mass was not performed. Missing args -> Yield or MW of limiting sibling. formId="
										+ siblingFormId,
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo = new StringBuilder();
						sbInfo.append("Limiting agent Sibling Mass Calculation =></br>");
						sbInfo.append("Formula: Limiting_agent_Mole*Yield*MW/100.</br> Normal Args:"
								+ String.valueOf(normalLimitingMole) + "*" + normalYieldSibling.toString() + "</br>");
						String masssibling = getFromNormalNumber(
								String.valueOf(normalLimitingMole * normalYieldSibling * normalMw / 100),
								siblingData.get("MASSUOM_ID"), sbInfo);
						formSaveDao.updateStructTableByFormId(
								"update fg_s_materialref_pivot t set MASS = '" + masssibling + "' where FORMID = '"
										+ siblingFormId + "'",
								"fg_s_materialref_pivot", Arrays.asList("MASS"), siblingFormId);
					}
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on mass =" + elementValueMap.get("volume"),
							ActivitylogType.Calculation, formId);
				}
			}
		}
	}

	/**
	 * calc material Reactant's values on save event
	 * 
	 * @param elementValueMap
	 */
	private void materialReactantOnSaveCalc(Map<String, String> elementValueMap, String formId) {

		if (!elementValueMap.get("limitingAgent").equals("1") && !elementValueMap.get("tableType").equals("Product")) {
			generalUtilLogger.logWriter(LevelType.DEBUG,
					"materialReactantOnSaveCalc - elementValueMap before calulation= QuantRatioTotal:"
							+ elementValueMap.get("QuantRatioTotal") + ",volRatioTotal:"
							+ elementValueMap.get("volRatioTotal"),
					ActivitylogType.Calculation, formId);
		}
		RatioTotalCalc(elementValueMap, formId, "-1", "-1", null);

		/*
		 * if(elementValueMap.get("limitingAgent").equals("1")) {//if limiting agent is defined then should calculate the siblings
		 * calcLimitingAgentSiblings(elementValueMap);
		 * }
		 */
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"materialReactantOnSaveCalc - elementValueMap after calulation= QuantRatioTotal:"
						+ elementValueMap.get("QuantRatioTotal") + ",volRatioTotal:"
						+ elementValueMap.get("volRatioTotal"),
				ActivitylogType.Calculation, formId);
	}

	private JSONObject RatioTotalCalc(Map<String, String> elementValueMap, String formId, String totalQuantity,
			String totalVolume, JSONObject elementValueJson) {
		JSONObject toReturn = new JSONObject();
		Map<String, JSONObject> returnMap = new HashMap<>();
		try {
			if (totalQuantity.equals("-1")) {//invoked from the popup save
				totalQuantity = generalDao.selectSingleStringNoException(
						"select distinct nvl(sum(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) over (partition by t.PARENTID),0)"
								+ " from fg_s_materialref_v t where parentId = '" + elementValueMap.get("parentId")
								+ "'" + generalUtilFormState.getWherePartForTmpData("MaterialRef",
										elementValueMap.get("parentId")));
			}
			if (Double.parseDouble(totalQuantity) == 0.0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation of Quantity Ratio Total was not performed due to division by 0",
						ActivitylogType.Calculation, formId);
			} else {
				generalUtilLogger.logWriter(LevelType.DEBUG,
						"calcRatioToatalQuantity - update elementValueMap in db ," + elementValueMap.toString(),
						ActivitylogType.Calculation, formId);
				if (elementValueJson == null) {
					List<String> stepReactionList = generalDao.getListOfStringBySql(
							"select t.formid from fg_s_materialref_all_v t where t.TABLETYPE in ('Reactant','Solvent') and t.PARENTID = '"
									+ elementValueMap.get("parentId") + "'" + generalUtilFormState
											.getWherePartForTmpData("MaterialRef", elementValueMap.get("parentId")));// gets of the siblings of the current step
					for (String reactId : stepReactionList) {
						Map<String, String> reactData = generalDao.getMetaDataRowValues("select distinct QUANTITY,"
								+ "QUANTITYUOM_ID" + " from fg_s_materialref_v where FORMID = '" + reactId + "'");// Default value 100 when batch is not selected
						Double normalReactQuantity = getNormalNumber(reactData.get("QUANTITY"),
								reactData.get("QUANTITYUOM_ID"));
						if (normalReactQuantity == null) {
							generalUtilLogger.logWriter(
									LevelType.WARN, "The calculation was not performed on QuantRatioTotal of formId = '"
											+ reactId + "'. Missing args -> Quantity",
									ActivitylogType.Calculation, formId);
						} else {
							formSaveDao.updateStructTableByFormId(
									"update fg_s_materialref_pivot set QuantRatioTotal = "
											+ normalReactQuantity / Double.parseDouble(totalQuantity)
											+ " where formid = '" + reactId + "'",
									"fg_s_materialref_pivot", Arrays.asList("QuantRatioTotal"), formId);
						}
					}
				} else {//invoked by the UI
					Iterator<String> keysItr = elementValueJson.keys();
					while (keysItr.hasNext()) {
						String id = keysItr.next();
						JSONObject rowData = elementValueJson.getJSONObject(id);
						String tableType = generalUtil.getJsonValById(rowData.toString(), "tableType");
						String quantity = "0";
						String quantityUomId = "";
						if (tableType.equals("Reactant") || tableType.equals("Solvent")) {
							quantity = generalUtil.getJsonValById(rowData.toString(), "quantity");
							quantityUomId = generalUtil.getJsonValById(rowData.toString(), "QUANTITYUOM_ID");
							Double normalReactQuantity = getNormalNumber(quantity, quantityUomId);
							if (normalReactQuantity == null) {
								generalUtilLogger
										.logWriter(LevelType.WARN,
												"The calculation was not performed on QuantRatioTotal of formId = '"
														+ id + "'. Missing args -> Quantity",
												ActivitylogType.Calculation, formId);
							} else {
								JSONObject json = new JSONObject();
								json.put("QuantRatioTotal", normalReactQuantity / Double.parseDouble(totalQuantity));
								returnMap.put(id, json);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on QuantRatioTotal =" + elementValueMap.get("QuantRatioTotal"),
					ActivitylogType.Calculation, formId);
		}
		try {
			if (totalVolume.equals("-1")) {
				totalVolume = generalDao.selectSingleStringNoException(
						"select distinct nvl(sum(fg_get_num_normal(t.VOLUME, t.VOLUOM_ID)) over (partition by t.PARENTID),0)"
								+ " from fg_s_materialref_v t where parentId = '" + elementValueMap.get("parentId")
								+ "'" + generalUtilFormState.getWherePartForTmpData("MaterialRef",
										elementValueMap.get("parentId")));
			}
			if (Double.parseDouble(totalVolume) == 0.0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation of Volume Ratio Total was not performed due to division by 0",
						ActivitylogType.Calculation, formId);
			} else {
				generalUtilLogger.logWriter(LevelType.DEBUG,
						"calcRatioToatalVolume - update elementValueMap in db ," + elementValueMap.toString(),
						ActivitylogType.Calculation, formId);
				if (elementValueJson == null) {
					List<String> stepReactionList = generalDao.getListOfStringBySql(
							"select t.formid from fg_s_materialref_all_v t where t.TABLETYPE in ('Reactant','Solvent') and t.PARENTID = '"
									+ elementValueMap.get("parentId") + "'" + generalUtilFormState
											.getWherePartForTmpData("MaterialRef", elementValueMap.get("parentId")));// gets of the siblings of the current step
					for (String reactId : stepReactionList) {
						Map<String, String> reactData = generalDao.getMetaDataRowValues("select distinct VOLUME,"
								+ "VOLUOM_ID" + " from fg_s_materialref_v where FORMID = '" + reactId + "'");// Default value 100 when batch is not selected
						Double normalReactVolume = getNormalNumber(reactData.get("VOLUME"), reactData.get("VOLUOM_ID"));
						if (normalReactVolume == null) {
							generalUtilLogger
									.logWriter(LevelType.WARN,
											"The calculation was not performed on VolumeRatioTotal of formId = '"
													+ reactId + "'. Missing args -> Volume",
											ActivitylogType.Calculation, formId);
						} else {
							formSaveDao.updateStructTableByFormId(
									"update fg_s_materialref_pivot set volRatioTotal = "
											+ normalReactVolume / Double.parseDouble(totalVolume) + " where formid = '"
											+ reactId + "'",
									"fg_s_materialref_pivot", Arrays.asList("volRatioTotal"), formId);
						}
					}
				} else {//invoked by the UI
					Iterator<String> keysItr = elementValueJson.keys();
					while (keysItr.hasNext()) {
						String id = keysItr.next();
						JSONObject rowData = elementValueJson.getJSONObject(id);
						String tableType = generalUtil.getJsonValById(rowData.toString(), "tableType");
						String volume = "0";
						String volumeUomId = "";
						if (tableType.equals("Reactant") || tableType.equals("Solvent")) {
							volume = generalUtil.getJsonValById(rowData.toString(), "volume");
							volumeUomId = generalUtil.getJsonValById(rowData.toString(), "VOLUOM_ID");
							Double normalReactVolume = getNormalNumber(volume, volumeUomId);
							if (normalReactVolume == null) {
								generalUtilLogger
										.logWriter(LevelType.WARN,
												"The calculation was not performed on volRatioTotal of formId = '" + id
														+ "'. Missing args -> Quantity",
												ActivitylogType.Calculation, formId);
							} else {
								JSONObject json = new JSONObject();
								if (returnMap.containsKey(id)) {
									json = returnMap.get(id);
								}
								json.put("volRatioTotal", normalReactVolume / Double.parseDouble(totalVolume));
								returnMap.put(id, json);
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on volRatioTotal =" + elementValueMap.get("volRatioTotal"),
					ActivitylogType.Calculation, formId);
		}
		for (Entry<String, JSONObject> entry : returnMap.entrySet()) {
			toReturn.put(entry.getKey(), entry.getValue());
		}
		return toReturn;
	}
	
	private JSONObject RatioTotalRateCalc(Map<String, String> elementValueMap, String formId, String totalQuantity,
			String totalVolume, JSONObject elementValueJson) {
		JSONObject toReturn = new JSONObject();
		Map<String, JSONObject> returnMap = new HashMap<>();
		Map<String, String> run_uom_default = generalDao.sqlToHashMap("select * from fg_i_calcrun_uom_override_v");
		
		try {
			String defaultQuantityUOM = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();
			
			if (totalQuantity.equals("-1")) {//invoked from the popup save
				totalQuantity = generalDao.selectSingleStringNoException(
						"select distinct nvl(sum(fg_get_num_normal(t.QUANTITYRATE,t.QUANTITYRATE_UOM)) over (partition by t.PARENTID),0)"
								+ " from fg_s_materialref_v t where parentId = '" + elementValueMap.get("parentId")
								+ "'" + generalUtilFormState.getWherePartForTmpData("MaterialRef",
										elementValueMap.get("parentId")));
				totalQuantity = getFromNormalNumber(totalQuantity,defaultQuantityUOM, new StringBuilder());
			}
			if (Double.parseDouble(totalQuantity) == 0.0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation of Quantity Ratio Total was not performed due to division by 0",
						ActivitylogType.Calculation, formId);
			} else {
				generalUtilLogger.logWriter(LevelType.DEBUG,
						"calcRatioToatalQuantity - update elementValueMap in db ," + elementValueMap.toString(),
						ActivitylogType.Calculation, formId);
				if (elementValueJson == null) {
					List<String> stepReactionList = generalDao.getListOfStringBySql(
							"select t.formid from fg_s_materialref_all_v t where t.TABLETYPE in ('Reactant','Solvent') and t.PARENTID = '"
									+ elementValueMap.get("parentId") + "'" + generalUtilFormState
											.getWherePartForTmpData("MaterialRef", elementValueMap.get("parentId")));// gets of the siblings of the current step
					for (String reactId : stepReactionList) {
						Map<String, String> reactData = generalDao.getMetaDataRowValues("select distinct QUANTITYRATE,"
								+ "QUANTITYRATE_UOM" + " from fg_s_materialref_v where FORMID = '" + reactId + "'");// Default value 100 when batch is not selected
						Double normalReactQuantity = getCustomNormalNumber(reactData.get("QUANTITYRATE"),
								reactData.get("QUANTITYRATE_UOM"),defaultQuantityUOM, new StringBuilder());
						if (normalReactQuantity == null) {
							generalUtilLogger.logWriter(
									LevelType.WARN, "The calculation was not performed on QuantRatioTotal of formId = '"
											+ reactId + "'. Missing args -> Quantity",
									ActivitylogType.Calculation, formId);
						} else {
							formSaveDao.updateStructTableByFormId(
									"update fg_s_materialref_pivot set QuantRatioTotal = "
											+ normalReactQuantity / Double.parseDouble(totalQuantity)
											+ " where formid = '" + reactId + "'",
									"fg_s_materialref_pivot", Arrays.asList("QuantRatioTotal"), formId);
						}
					}
				} else {//invoked by the UI
					Iterator<String> keysItr = elementValueJson.keys();
					while (keysItr.hasNext()) {
						String id = keysItr.next();
						JSONObject rowData = elementValueJson.getJSONObject(id);
						String tableType = generalUtil.getJsonValById(rowData.toString(), "tableType");
						String quantity = "0";
						String quantityUomId = "";
						if (tableType.equals("Reactant") || tableType.equals("Solvent")) {
							quantity = generalUtil.getJsonValById(rowData.toString(), "quantityRate");
							quantityUomId = generalUtil.getJsonValById(rowData.toString(), "QUANTITYRATE_UOM");
							Double normalReactQuantity = getCustomNormalNumber(quantity, quantityUomId, defaultQuantityUOM, new StringBuilder());
							if (normalReactQuantity == null) {
								generalUtilLogger
										.logWriter(LevelType.WARN,
												"The calculation was not performed on QuantRatioTotal of formId = '"
														+ id + "'. Missing args -> Quantity",
												ActivitylogType.Calculation, formId);
							} else {
								JSONObject json = new JSONObject();
								json.put("QuantRatioTotal", normalReactQuantity / Double.parseDouble(totalQuantity));
								returnMap.put(id, json);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on QuantRatioTotal =" + elementValueMap.get("QuantRatioTotal"),
					ActivitylogType.Calculation, formId);
		}
		try {
			String defaultVolumeUOM = (run_uom_default.get("DEFAULT_VOLUME_UOM_ID") != null?run_uom_default.get("DEFAULT_VOLUME_UOM_ID"):"").toString();
			if (totalVolume.equals("-1")) {
				totalVolume = generalDao.selectSingleStringNoException(
						"select distinct nvl(sum(fg_get_num_normal(t.VOLUMERATE, t.VOLRATEUOM_ID)) over (partition by t.PARENTID),0)"
								+ " from fg_s_materialref_v t where parentId = '" + elementValueMap.get("parentId")
								+ "'" + generalUtilFormState.getWherePartForTmpData("MaterialRef",
										elementValueMap.get("parentId")));
				totalVolume = getFromNormalNumber(totalVolume,defaultVolumeUOM, new StringBuilder());
			}
			if (Double.parseDouble(totalVolume) == 0.0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation of Volume Ratio Total was not performed due to division by 0",
						ActivitylogType.Calculation, formId);
			} else {
				generalUtilLogger.logWriter(LevelType.DEBUG,
						"calcRatioToatalVolume - update elementValueMap in db ," + elementValueMap.toString(),
						ActivitylogType.Calculation, formId);
				if (elementValueJson == null) {
					List<String> stepReactionList = generalDao.getListOfStringBySql(
							"select t.formid from fg_s_materialref_all_v t where t.TABLETYPE in ('Reactant','Solvent') and t.PARENTID = '"
									+ elementValueMap.get("parentId") + "'" + generalUtilFormState
											.getWherePartForTmpData("MaterialRef", elementValueMap.get("parentId")));// gets of the siblings of the current step
					for (String reactId : stepReactionList) {
						Map<String, String> reactData = generalDao.getMetaDataRowValues("select distinct VOLUMERATE,"
								+ "VOLRATEUOM_ID" + " from fg_s_materialref_v where FORMID = '" + reactId + "'");// Default value 100 when batch is not selected
						Double normalReactVolume = getCustomNormalNumber(reactData.get("VOLUMERATE"), reactData.get("VOLRATEUOM_ID"), defaultVolumeUOM, new StringBuilder());
						if (normalReactVolume == null) {
							generalUtilLogger
									.logWriter(LevelType.WARN,
											"The calculation was not performed on VolumeRatioTotal of formId = '"
													+ reactId + "'. Missing args -> Volume Rate",
											ActivitylogType.Calculation, formId);
						} else {
							formSaveDao.updateStructTableByFormId(
									"update fg_s_materialref_pivot set volRatioTotal = "
											+ normalReactVolume / Double.parseDouble(totalVolume) + " where formid = '"
											+ reactId + "'",
									"fg_s_materialref_pivot", Arrays.asList("volRatioTotal"), formId);
						}
					}
				} else {//invoked by the UI
					Iterator<String> keysItr = elementValueJson.keys();
					while (keysItr.hasNext()) {
						String id = keysItr.next();
						JSONObject rowData = elementValueJson.getJSONObject(id);
						String tableType = generalUtil.getJsonValById(rowData.toString(), "tableType");
						String volume = "0";
						String volumeUomId = "";
						if (tableType.equals("Reactant") || tableType.equals("Solvent")) {
							volume = generalUtil.getJsonValById(rowData.toString(), "volumeRate");
							volumeUomId = generalUtil.getJsonValById(rowData.toString(), "VOLRATEUOM_ID");
							Double normalReactVolume = getCustomNormalNumber(volume, volumeUomId, defaultVolumeUOM, new StringBuilder());
							if (normalReactVolume == null) {
								generalUtilLogger
										.logWriter(LevelType.WARN,
												"The calculation was not performed on volRatioTotal of formId = '" + id
														+ "'. Missing args -> Quantity Rate",
												ActivitylogType.Calculation, formId);
							} else {
								JSONObject json = new JSONObject();
								if (returnMap.containsKey(id)) {
									json = returnMap.get(id);
								}
								json.put("volRatioTotal", normalReactVolume / Double.parseDouble(totalVolume));
								returnMap.put(id, json);
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on volRatioTotal =" + elementValueMap.get("volRatioTotal"),
					ActivitylogType.Calculation, formId);
		}
		for (Entry<String, JSONObject> entry : returnMap.entrySet()) {
			toReturn.put(entry.getKey(), entry.getValue());
		}
		return toReturn;
	}

	/**
	 * calc material solvent's values on save event
	 * 
	 * @param elementValueMap
	 */
	private void materialSolventOnSaveCalc(Map<String, String> elementValueMap, String formId) {
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"materialSolventOnSaveCalc - elementValueMap before calulation= QuantRatioTotal:"
						+ elementValueMap.get("QuantRatioTotal") + ",volRatioTotal:"
						+ elementValueMap.get("volRatioTotal"),
				ActivitylogType.Calculation, formId);
		RatioTotalCalc(elementValueMap, formId, "-1", "-1", null);

		generalUtilLogger.logWriter(LevelType.DEBUG,
				"materialSolventOnSaveCalc - elementValueMap after calulation= QuantRatioTotal:"
						+ elementValueMap.get("QuantRatioTotal") + ",volRatioTotal:"
						+ elementValueMap.get("volRatioTotal"),
				ActivitylogType.Calculation, formId);
	}

	/**
	 * calc material Reactant's values on click Quantity calc button
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */
	private StringBuilder materialReactantQuantityCalc(Map<String, String> elementValueMap, String mainArgVal,
			String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(
				formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),
				"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID,
				elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialReactantQuantityCalc - elementValueMap before calulation= mole:"
						+ elementValueMap.get("mole") + ",volume:" + elementValueMap.get("volume"),
				ActivitylogType.Calculation, formId);
		StringBuilder sb = new StringBuilder();

		Double normalQuantity = getNormalNumber(mainArgVal, elementValueMap.get("QUANTITYUOM_ID"));
		Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
		Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"));
		Double normalpurityInf;
		normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected

		if (elementValueMap.get("limitingAgent").equals("1") || elementValueMap.get("isLimited").equals("0")) {//
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent/Non Limited Mole Calculation =></br>");
				if (normalQuantity == null || normalmwInf == null || normalmwInf == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent reactant [quantity*purity/100/MW] was not performed . Args: Quantity ='"
									+ normalQuantity + "'; MW = '" + normalmwInf + "'",
							ActivitylogType.Calculation, formId);
				} else {

					sbInfo.append("Formula: Quantity*Purity/100/MW.</br> Normal Args:" + String.valueOf(normalQuantity)
							+ "*(" + normalpurityInf.toString() + "/100)/(" + normalmwInf.toString() + ")" + "</br>");
					elementValueMap.put("mole",
							getFromNormalNumber(
									String.valueOf((normalQuantity * (normalpurityInf / 100)) / normalmwInf),
									elementValueMap.get("MOLEUOM_ID"), sbInfo));
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation  of limiting agent reactant was not performed :mole ="
								+ elementValueMap.get("mole"),
						ActivitylogType.Calculation, formId);
			}
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent/Non Limited Volume Calculation =></br>");
				if (normalQuantity == null || normalmwInf == null || normalDensityInf == null
						|| normalDensityInf == 0.0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent reactant volume [quantity/density] was not performed. Args : Quantity ="
									+ elementValueMap.get("quantity") + "; Density = "
									+ elementValueMap.get("densityInf") + "; MW = "
									+ generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
							ActivitylogType.Calculation, formId);
					if(normalDensityInf == null	|| normalDensityInf == 0.0){
						elementValueMap.put("warningMsg", "The following value is missing \""+elementValueMap.get("tableType")+" - "
								+ "Density"
								+ "\". Calculation cannot be completed.");
					}
				} else {

					sbInfo.append("Formula: Quantity/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
							+ normalDensityInf + "</br>");
					elementValueMap.put("volume", getFromNormalNumber(String.valueOf(normalQuantity / normalDensityInf),
							elementValueMap.get("VOLUOM_ID"), sbInfo));
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume =" + elementValueMap.get("volume"),
						ActivitylogType.Calculation, formId);
			}

			sb = createJsonMaterialCalcSB(elementValueMap);
		} else if (elementValueMap.get("isLimited").equals("1") && elementValueMap.get("limitingAgent").equals("0")) {// there exists a sibling step that defined as limiting agent
			updateLimitingAgentSibling("Quantity", elementValueMap, formId);
			sb = createJsonMaterialCalcSB(elementValueMap);
		} else {
			sb = createJsonMaterialCalcSB(elementValueMap);
		}
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialReactantQuantityCalc - elementValueMap after calulation= mole:"
						+ elementValueMap.get("mole") + ",volume:" + elementValueMap.get("volume"),
				ActivitylogType.Calculation, formId);
		return sb;
	}
	
	private StringBuilder materialReactantQuantityRateCalc(Map<String, String> elementValueMap, String mainArgVal,
			String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(
				formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),
				"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID,
				elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialReactantQuantityRateCalc - elementValueMap before calulation= mole rate:"
						+ elementValueMap.get("moleRate") + ",volume rate:" + elementValueMap.get("volumeRate"),
				ActivitylogType.Calculation, formId);
		StringBuilder sb = new StringBuilder();

		//Double normalQuantityRate = getNormalNumber(mainArgVal, elementValueMap.get("QUANTITYRATE_UOM"));
		/*Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
		Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"));*/
		Map<String, String> run_uom_default = generalDao.sqlToHashMap("select * from fg_i_calcrun_uom_override_v");
		
		String defaultMWUomId_runStep = (run_uom_default.get("DEFAULT_MW_UOM_ID") != null?run_uom_default.get("DEFAULT_MW_UOM_ID"):"").toString();	
		String defaultQuantityUomId = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();	
		String defaultdensityUomId_runStep = (run_uom_default.get("DEFAULT_DENSITY_UOM_ID") != null?run_uom_default.get("DEFAULT_DENSITY_UOM_ID"):"").toString();	
		String defaultVolumeUOMId = (run_uom_default.get("DEFAULT_VOLUME_UOM_ID") != null?run_uom_default.get("DEFAULT_VOLUME_UOM_ID"):"").toString();	
		
		Double normalmwInf = getCustomNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId),defaultMWUomId_runStep,new StringBuilder());
		Double normalQuantityRate = getCustomNormalNumber(mainArgVal, elementValueMap.get("QUANTITYRATE_UOM"), defaultQuantityUomId, new StringBuilder());
		Double normalDensityInf = getCustomNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"), defaultdensityUomId_runStep, new StringBuilder()); 
		
		Double normalpurityInf;
		normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected

		if (elementValueMap.get("limitingAgent").equals("1") || elementValueMap.get("isLimited").equals("0")) {//
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent/Non Limited Mole Calculation =></br>");
				if (normalQuantityRate == null || normalmwInf == null || normalmwInf == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent reactant [quantity*purity/100/MW] was not performed . Args: Quantity ='"
									+ normalQuantityRate + "'; MW = '" + normalmwInf + "'",
							ActivitylogType.Calculation, formId);
				} else {

					sbInfo.append("Formula: Quantity Rate*Purity/100/MW.</br> Normal Args:" + String.valueOf(normalQuantityRate)
							+ "*(" + normalpurityInf.toString() + "/100)/(" + normalmwInf.toString() + ")" + "</br>");
					elementValueMap.put("moleRate",
							getFromNormalNumber(
									String.valueOf((normalQuantityRate * (normalpurityInf / 100)) / normalmwInf),
									elementValueMap.get("MOLERATEUOM_ID"), sbInfo));
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation  of limiting agent reactant was not performed :mole rate="
								+ elementValueMap.get("moleRate"),
						ActivitylogType.Calculation, formId);
			}
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent/Non Limited Volume Calculation =></br>");
				if (normalQuantityRate == null || normalmwInf == null || normalDensityInf == null
						|| normalDensityInf == 0.0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation of limiting agent reactant volume [quantity/density] was not performed. Args : Quantity Rate ="
									+ elementValueMap.get("quantityRate") + "; Density = "
									+ elementValueMap.get("densityInf") + "; MW = "
									+ generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
							ActivitylogType.Calculation, formId);
					if(normalDensityInf == null	|| normalDensityInf == 0.0){
						elementValueMap.put("warningMsg", "The following value is missing \""+elementValueMap.get("tableType")+" - "
								+ "Density"
								+ "\". Calculation cannot be completed.");
					}
				} else {

					sbInfo.append("Formula: Quantity Rate/Density.</br> Normal Args:" + String.valueOf(normalQuantityRate) + "/"
							+ normalDensityInf + "</br>");
					elementValueMap.put("volumeRate", getCustomNormalNumber(String.valueOf(normalQuantityRate / normalDensityInf), defaultVolumeUOMId,
							elementValueMap.get("VOLRATEUOM_ID"), sbInfo).toString());
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation, formId);
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume rate =" + elementValueMap.get("volumeRate"),
						ActivitylogType.Calculation, formId);
			}

			sb = createJsonMaterialCalcSB(elementValueMap);
		} else if (elementValueMap.get("isLimited").equals("1") && elementValueMap.get("limitingAgent").equals("0")) {// there exists a sibling step that defined as limiting agent
			updateLimitingAgentSibling("QuantityRate", elementValueMap, formId);
			sb = createJsonMaterialCalcSB(elementValueMap);
		} else {
			sb = createJsonMaterialCalcSB(elementValueMap);
		}
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialReactantQuantityRateCalc - elementValueMap after calulation= mole rate:"
						+ elementValueMap.get("moleRate") + ",volume rate:" + elementValueMap.get("volumeRate"),
				ActivitylogType.Calculation, formId);
		return sb;
	}

	/**
	 * calc material Reactant's values on click Mole calc button
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */
	private StringBuilder materialReactanttMoleCalc(Map<String, String> elementValueMap, String mainArgVal,
			String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(
				formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),
				"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID,
				elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"materialReactanttMoleCalc - elementValueMap before calulation= quantity:"
						+ elementValueMap.get("quantity") + ",volume:" + elementValueMap.get("volume"),
				ActivitylogType.Calculation, formId);
		StringBuilder sb = new StringBuilder();
		Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
		Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"));
		Double normalMole = getNormalNumber(mainArgVal, elementValueMap.get("MOLEUOM_ID"));
		Double normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected
		/*
		 * if (!generalUtil.getNull(elementValueMap.get("purityInf")).isEmpty()) {
		 * normalpurityInf = getNormalNumber(elementValueMap.get("purityInf"),
		 * elementValueMap.get("PURITYUOM_ID_INF"));
		 * } else {
		 * normalpurityInf = (float) 1;// Default value 1 when batch is not selected
		 * }
		 */

		if ((elementValueMap.get("isLimited").equals("0") || elementValueMap.get("limitingAgent").equals("1"))) {
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent/Non Limited Quantity Calculation =></br>");
				if (normalmwInf == null || normalMole == null || normalpurityInf == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on quantity = mole*mw/purity. Args: Mole ='"
									+ elementValueMap.get("mole") + "'; MW = '"
									+ generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw) + "'",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Mole*MW/(Purity/100).</br> Normal Args: (" + String.valueOf(normalMole)
							+ "*" + normalmwInf.toString() + ")/(" + normalpurityInf.toString() + "/100)" + "</br>");
					elementValueMap.put("quantity",
							getFromNormalNumber(String.valueOf((normalMole * normalmwInf) / (normalpurityInf / 100)),
									elementValueMap.get("QUANTITYUOM_ID"), sbInfo));
				}

			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on quantity =" + elementValueMap.get("quantity"),
						ActivitylogType.Calculation, formId);
			}
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent/Non Limited Volume Calculation =></br>");
				Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"),
						elementValueMap.get("QUANTITYUOM_ID"));
				if (normalQuantity == null || normalDensityInf == null || normalDensityInf == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on volume = quantity/density. Quantity ="
									+ elementValueMap.get("quantity") + "; density = "
									+ elementValueMap.get("densityInf"),
							ActivitylogType.Calculation, formId);
					if(normalDensityInf == null	|| normalDensityInf == 0){
						elementValueMap.put("warningMsg", "The following value is missing \""+elementValueMap.get("tableType")+" - "
								+ "Density"
								+ "\". Calculation cannot be completed.");
					}
				} else {
					sbInfo.append("Formula: Quantity/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
							+ normalDensityInf.toString() + "</br>");
					elementValueMap.put("volume", getFromNormalNumber(String.valueOf(normalQuantity / normalDensityInf),
							elementValueMap.get("VOLUOM_ID"), sbInfo));
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume =" + elementValueMap.get("volume"),
						ActivitylogType.Calculation, formId);
			}

			sb = createJsonMaterialCalcSB(elementValueMap);
		} else if (elementValueMap.get("isLimited").equals("1") && elementValueMap.get("limitingAgent").equals("0")) {// there exists a sibling step that defined as limiting agent
			updateLimitingAgentSibling("Mole", elementValueMap, formId);
			sb = createJsonMaterialCalcSB(elementValueMap);
		} else {
			sb = createJsonMaterialCalcSB(elementValueMap);
		}
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"materialReactanttMoleCalc - elementValueMap after calulation= quantity:"
						+ elementValueMap.get("quantity") + ",volume:" + elementValueMap.get("volume"),
				ActivitylogType.Calculation, formId);
		return sb;
	}
	
	/**
	 * calc material Reactant's values on click Mole rate calc button
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */
	private StringBuilder materialReactantMoleRateCalc(Map<String, String> elementValueMap, String mainArgVal,
			String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(
				formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),
				"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID,
				elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"materialReactanttMoleCalc - elementValueMap before calulation= quantity rate:"
						+ elementValueMap.get("quantityRate") + ",volume rate:" + elementValueMap.get("volumeRate"),
				ActivitylogType.Calculation, formId);
		StringBuilder sb = new StringBuilder();
		
        Map<String, String> run_uom_default = generalDao.sqlToHashMap("select * from fg_i_calcrun_uom_override_v");
        String defaultMWUomId_runStep = (run_uom_default.get("DEFAULT_MW_UOM_ID") != null?run_uom_default.get("DEFAULT_MW_UOM_ID"):"").toString();	
		String defaultQuantityUomId = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();	
		String defaultdensityUomId_runStep = (run_uom_default.get("DEFAULT_DENSITY_UOM_ID") != null?run_uom_default.get("DEFAULT_DENSITY_UOM_ID"):"").toString();	
		String defaultVolumeUOMId = (run_uom_default.get("DEFAULT_VOLUME_UOM_ID") != null?run_uom_default.get("DEFAULT_VOLUME_UOM_ID"):"").toString();	
		
		
		/*Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));*/
		Double normalmwInf = getCustomNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId),defaultMWUomId_runStep,new StringBuilder());
		
		/*Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"));*/
		Double normalDensityInf = getCustomNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"), defaultdensityUomId_runStep, new StringBuilder()); 
		Double normalMole = getNormalNumber(mainArgVal, elementValueMap.get("MOLERATEUOM_ID"));
		Double normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected
		/*
		 * if (!generalUtil.getNull(elementValueMap.get("purityInf")).isEmpty()) {
		 * normalpurityInf = getNormalNumber(elementValueMap.get("purityInf"),
		 * elementValueMap.get("PURITYUOM_ID_INF"));
		 * } else {
		 * normalpurityInf = (float) 1;// Default value 1 when batch is not selected
		 * }
		 */

		if ((elementValueMap.get("isLimited").equals("0") || elementValueMap.get("limitingAgent").equals("1"))) {
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent/Non Limited Quantity Calculation =></br>");
				if (normalmwInf == null || normalMole == null || normalpurityInf == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on quantity = mole*mw/purity. Args: Mole Rate ='"
									+ elementValueMap.get("moleRate") + "'; MW = '"
									+ generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw) + "'",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Mole*MW/(Purity/100).</br> Normal Args: (" + String.valueOf(normalMole)
							+ "*" + normalmwInf.toString() + ")/(" + normalpurityInf.toString() + "/100)" + "</br>");
					elementValueMap.put("quantityRate",
							getCustomNormalNumber(String.valueOf((normalMole * normalmwInf) / (normalpurityInf / 100)),defaultQuantityUomId,
									elementValueMap.get("QUANTITYRATE_UOM"), sbInfo).toString());
				}

			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on quantity rate =" + elementValueMap.get("quantityRate"),
						ActivitylogType.Calculation, formId);
			}
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent/Non Limited Volume Calculation =></br>");
				Double normalQuantity = getCustomNormalNumber(elementValueMap.get("quantityRate"),
						elementValueMap.get("QUANTITYRATE_UOM"),defaultQuantityUomId,new StringBuilder());
				if (normalQuantity == null || normalDensityInf == null || normalDensityInf == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on volume = quantity/density. Quantity ="
									+ elementValueMap.get("quantity") + "; density = "
									+ elementValueMap.get("densityInf"),
							ActivitylogType.Calculation, formId);
					if(normalDensityInf == null	|| normalDensityInf == 0){
						elementValueMap.put("warningMsg", "The following value is missing \""+elementValueMap.get("tableType")+" - "
								+ "Density"
								+ "\". Calculation cannot be completed.");
					}
				} else {
					sbInfo.append("Formula: Quantity/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
							+ normalDensityInf.toString() + "</br>");
					elementValueMap.put("volumeRate", getCustomNormalNumber(String.valueOf(normalQuantity / normalDensityInf),defaultVolumeUOMId,
							elementValueMap.get("VOLRATEUOM_ID"), sbInfo).toString());
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation, formId);
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume =" + elementValueMap.get("volumeRate"),
						ActivitylogType.Calculation, formId);
			}

			sb = createJsonMaterialCalcSB(elementValueMap);
		} else if (elementValueMap.get("isLimited").equals("1") && elementValueMap.get("limitingAgent").equals("0")) {// there exists a sibling step that defined as limiting agent
			updateLimitingAgentSibling("MoleRate", elementValueMap, formId);
			sb = createJsonMaterialCalcSB(elementValueMap);
		} else {
			sb = createJsonMaterialCalcSB(elementValueMap);
		}
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"materialReactanttMoleCalc - elementValueMap after calulation= quantity rate:"
						+ elementValueMap.get("quantityRate") + ",volume rate:" + elementValueMap.get("volumeRate"),
				ActivitylogType.Calculation, formId);
		return sb;
	}

	/**
	 * calc material Reactant's values on click Volume calc button
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */

	private StringBuilder materialReactantVolumeCalc(Map<String, String> elementValueMap, String mainArgVal,
			String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(
				formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),
				"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID,
				elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialReactantVolumeCalc - elementValueMap before calulation= mole:"
						+ elementValueMap.get("mole") + ",quantity:" + elementValueMap.get("quantity"),
				ActivitylogType.Calculation, formId);
		StringBuilder sb = new StringBuilder();

		Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
		Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"));
		Double normalVolume = getNormalNumber(mainArgVal, elementValueMap.get("VOLUOM_ID"));

		Double normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected
		/*
		 * if (!generalUtil.getNull(elementValueMap.get("purityInf")).isEmpty()) {
		 * normalpurityInf = getNormalNumber(elementValueMap.get("purityInf"),
		 * elementValueMap.get("PURITYUOM_ID_INF"));
		 * } else {
		 * normalpurityInf = (float) 1;// Default value 1 when batch is not selected
		 * }
		 */

		if ((elementValueMap.get("isLimited").equals("0") || elementValueMap.get("limitingAgent").equals("1"))) {
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent/Non Limited Quantity Calculation =></br>");
				if (normalVolume == null || normalDensityInf == null) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on quantity = volume*density. Args: Volume = "
									+ elementValueMap.get("volume") + "; Density = "
									+ elementValueMap.get("densityInf"),
							ActivitylogType.Calculation, formId);
					if(normalDensityInf == null){
						elementValueMap.put("warningMsg", "The following value is missing \""+elementValueMap.get("tableType")+" - "
								+ "Density"
								+ "\". Calculation cannot be completed.");
					}
				} else {
					sbInfo.append("Formula: Volume*Density.</br> Normal Args:" + String.valueOf(normalVolume) + "*"
							+ normalDensityInf.toString() + "</br>");
					elementValueMap.put("quantity", getFromNormalNumber(String.valueOf(normalVolume * normalDensityInf),
							elementValueMap.get("QUANTITYUOM_ID"), sbInfo));
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on quantity =" + elementValueMap.get("quantity"),
						ActivitylogType.Calculation, formId);
			}
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent/Non Limited Mole Calculation =></br>");
				Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"),
						elementValueMap.get("QUANTITYUOM_ID"));
				if (normalQuantity == null || normalmwInf == null || normalpurityInf == null || (normalmwInf) == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on mole = quantity*purity/100/mw. Quantity ="
									+ elementValueMap.get("quantity") + "; MW = "
									+ generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Quantity*Purity/100/MW.</br> Normal Args:" + String.valueOf(normalQuantity)
							+ "*(" + normalpurityInf.toString() + "/100)/(" + normalmwInf.toString() + ")" + "</br>");
					elementValueMap.put("mole",
							getFromNormalNumber(
									String.valueOf(normalQuantity * (normalpurityInf / 100) / (normalmwInf)),
									elementValueMap.get("MOLEUOM_ID"), sbInfo));
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole =" + elementValueMap.get("mole"),
						ActivitylogType.Calculation, formId);
			}

			sb = createJsonMaterialCalcSB(elementValueMap);
		} else if (elementValueMap.get("isLimited").equals("1") && elementValueMap.get("limitingAgent").equals("0")) {// there exists a sibling step that defined as limiting agent
			updateLimitingAgentSibling("Volume", elementValueMap, formId);
			sb = createJsonMaterialCalcSB(elementValueMap);
		} else {
			sb = createJsonMaterialCalcSB(elementValueMap);
		}
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialReactantVolumeCalc - elementValueMap after calulation= mole:"
						+ elementValueMap.get("mole") + ",quantity:" + elementValueMap.get("quantity"),
				ActivitylogType.Calculation, formId);
		return sb;
	}

	
	/**
	 * calc material Reactant's values on click Volume Rate calc button
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */

	private StringBuilder materialReactantVolumeRateCalc(Map<String, String> elementValueMap, String mainArgVal,
			String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(
				formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),
				"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID,
				elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialReactantVolumeCalc - elementValueMap before calulation= mole rate:"
						+ elementValueMap.get("moleRate") + ",quantity rate:" + elementValueMap.get("quantityRate"),
				ActivitylogType.Calculation, formId);
		StringBuilder sb = new StringBuilder();
		
        Map<String, String> run_uom_default = generalDao.sqlToHashMap("select * from fg_i_calcrun_uom_override_v");
		
		String defaultMWUomId_runStep = (run_uom_default.get("DEFAULT_MW_UOM_ID") != null?run_uom_default.get("DEFAULT_MW_UOM_ID"):"").toString();	
		String defaultQuantityUomId = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();	
		String defaultdensityUomId_runStep = (run_uom_default.get("DEFAULT_DENSITY_UOM_ID") != null?run_uom_default.get("DEFAULT_DENSITY_UOM_ID"):"").toString();	
		String defaultVolumeUOMId = (run_uom_default.get("DEFAULT_VOLUME_UOM_ID") != null?run_uom_default.get("DEFAULT_VOLUME_UOM_ID"):"").toString();	
		
		Double normalmwInf = getCustomNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId),defaultMWUomId_runStep,new StringBuilder());
		Double normalVolume = getCustomNormalNumber(mainArgVal, elementValueMap.get("VOLRATEUOM_ID"), defaultVolumeUOMId, new StringBuilder());
		Double normalDensityInf = getCustomNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"), defaultdensityUomId_runStep, new StringBuilder()); 
		
       /*Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
		Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"));
		Double normalVolume = getNormalNumber(mainArgVal, elementValueMap.get("VOLRATEUOM_ID"));
		*/
		
		Double normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected
		/*
		 * if (!generalUtil.getNull(elementValueMap.get("purityInf")).isEmpty()) {
		 * normalpurityInf = getNormalNumber(elementValueMap.get("purityInf"),
		 * elementValueMap.get("PURITYUOM_ID_INF"));
		 * } else {
		 * normalpurityInf = (float) 1;// Default value 1 when batch is not selected
		 * }
		 */

		if ((elementValueMap.get("isLimited").equals("0") || elementValueMap.get("limitingAgent").equals("1"))) {
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent/Non Limited Quantity Calculation =></br>");
				if (normalVolume == null || normalDensityInf == null) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on quantity = volume*density. Args: Volume Rate= "
									+ elementValueMap.get("volumeRate") + "; Density = "
									+ elementValueMap.get("densityInf"),
							ActivitylogType.Calculation, formId);
					if(normalDensityInf == null	){
						elementValueMap.put("warningMsg", "The following value is missing \""+elementValueMap.get("tableType")+" - "
								+ "Density"
								+ "\". Calculation cannot be completed.");
					}
				} else {
					sbInfo.append("Formula: Volume*Density.</br> Normal Args:" + String.valueOf(normalVolume) + "*"
							+ normalDensityInf.toString() + "</br>");
					elementValueMap.put("quantityRate", getCustomNormalNumber(String.valueOf(normalVolume * normalDensityInf),defaultQuantityUomId,
							elementValueMap.get("QUANTITYRATE_UOM"), sbInfo).toString());
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation, formId);
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on quantity rate =" + elementValueMap.get("quantityRate"),
						ActivitylogType.Calculation, formId);
			}
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Limiting agent/Non Limited Mole Calculation =></br>");
				Double normalQuantity = getCustomNormalNumber(elementValueMap.get("quantityRate"),
						elementValueMap.get("QUANTITYRATE_UOM"),defaultQuantityUomId,new StringBuilder());
				if (normalQuantity == null || normalmwInf == null || normalpurityInf == null || (normalmwInf) == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on mole = quantity*purity/100/mw. Quantity Rate ="
									+ elementValueMap.get("quantityRate") + "; MW = "
									+ generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Quantity*Purity/100/MW.</br> Normal Args:" + String.valueOf(normalQuantity)
							+ "*(" + normalpurityInf.toString() + "/100)/(" + normalmwInf.toString() + ")" + "</br>");
					elementValueMap.put("moleRate",
							getFromNormalNumber(
									String.valueOf(normalQuantity * (normalpurityInf / 100) / (normalmwInf)),
									elementValueMap.get("MOLERATEUOM_ID"), sbInfo));
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole rate =" + elementValueMap.get("moleRate"),
						ActivitylogType.Calculation, formId);
			}

			sb = createJsonMaterialCalcSB(elementValueMap);
		} else if (elementValueMap.get("isLimited").equals("1") && elementValueMap.get("limitingAgent").equals("0")) {// there exists a sibling step that defined as limiting agent
			updateLimitingAgentSibling("VolumeRate", elementValueMap, formId);
			sb = createJsonMaterialCalcSB(elementValueMap);
		} else {
			sb = createJsonMaterialCalcSB(elementValueMap);
		}
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialReactantVolumeCalc - elementValueMap after calulation= mole rate:"
						+ elementValueMap.get("moleRate") + ",quantity rate:" + elementValueMap.get("quantityRate"),
				ActivitylogType.Calculation, formId);
		return sb;
	}

	/**
	 * calc material Solvent's values on click Quantity calc button
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */
	private StringBuilder materialSolventQuantityCalc(Map<String, String> elementValueMap, String mainArgVal,
			String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(
				formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),
				"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID,
				elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventQuantityCalc - elementValueMap before calulation= mole:"
						+ elementValueMap.get("mole") + ",quantity:" + elementValueMap.get("quantity"),
				ActivitylogType.Calculation, formId);
		StringBuilder sb = new StringBuilder();
		Double normalQuantity = getNormalNumber(mainArgVal, elementValueMap.get("QUANTITYUOM_ID"));
		Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
		Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"));
		Double normalpurityInf;
		normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected
		
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Mole Calculation =></br>");
			if (normalQuantity == null || normalmwInf == null) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole. Quantity =" + elementValueMap.get("quantity")
								+ "; MW = " + normalmwInf + ";purity = " + normalpurityInf,
						ActivitylogType.Calculation, formId);
			} else if (normalmwInf == 0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole = " + normalmwInf + "(mw)*" + normalpurityInf
								+ "(purity). The dividing parameter[purity*mw] in the mole calculation is 0",
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: Quantity*Purity/100/MW.</br> Normal Args:" + String.valueOf(normalQuantity)
						+ "*(" + normalpurityInf.toString() + "/100)/" + normalmwInf.toString() + "</br>");
				elementValueMap.put("mole",
						getFromNormalNumber(String.valueOf((normalQuantity * (normalpurityInf / 100) / (normalmwInf))),
								elementValueMap.get("MOLEUOM_ID"), sbInfo));
			}

		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on mole =" + elementValueMap.get("mole"),
					ActivitylogType.Calculation, formId);
		}
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Volume Calculation =></br>");
			if (normalQuantity == null || normalDensityInf == null) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume. Quantity" + elementValueMap.get("quantity")
								+ "; Density = " + elementValueMap.get("densityInf") + ";purity = " + normalpurityInf,
						ActivitylogType.Calculation, formId);
				if(normalDensityInf == null)
				{
					elementValueMap.put("warningMsg", "The following value is missing \"Solvent - Density\". Calculation cannot be completed.");
				}
			} else if (normalDensityInf == 0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume = " + normalQuantity + "(quantity)/"
								+ normalDensityInf
								+ "(density). The dividing parameter[density] in the volume calculation is 0. "
								+ elementValueMap.get("volume"),
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: Quantity/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
						+ normalDensityInf.toString() + "</br>");
				elementValueMap.put("volume", getFromNormalNumber(String.valueOf((normalQuantity / (normalDensityInf))),
						elementValueMap.get("VOLUOM_ID"), sbInfo));
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on volume =" + elementValueMap.get("volume"),
					ActivitylogType.Calculation, formId);
		}
		
		materialSolventRatioCalc(elementValueMap, formId);
		sb = createJsonMaterialCalcSB(elementValueMap);
		
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventQuantityCalc - elementValueMap after calulation= mole:"
						+ elementValueMap.get("mole") + ",quantity:" + elementValueMap.get("quantity"),
				ActivitylogType.Calculation, formId);

		return sb;
	}
	
	/**
	 * calc material Solvent's values on click Quantity Rate calc button
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */
	private StringBuilder materialSolventQuantityRateCalc(Map<String, String> elementValueMap, String mainArgVal,
			String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(
				formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),
				"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID,
				elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventQuantityCalc - elementValueMap before calulation= mole:"
						+ elementValueMap.get("moleRate") + ",quantity:" + elementValueMap.get("quantityRate"),
				ActivitylogType.Calculation, formId);
		StringBuilder sb = new StringBuilder();
		/*Double normalQuantity = getNormalNumber(mainArgVal, elementValueMap.get("QUANTITYRATE_UOM"));
		Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
		Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"));*/
		
        Map<String, String> run_uom_default = generalDao.sqlToHashMap("select * from fg_i_calcrun_uom_override_v");
		
		String defaultMWUomId_runStep = (run_uom_default.get("DEFAULT_MW_UOM_ID") != null?run_uom_default.get("DEFAULT_MW_UOM_ID"):"").toString();	
		String defaultQuantityUomId = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();	
		String defaultdensityUomId_runStep = (run_uom_default.get("DEFAULT_DENSITY_UOM_ID") != null?run_uom_default.get("DEFAULT_DENSITY_UOM_ID"):"").toString();	
		String defaultVolumeUOMId = (run_uom_default.get("DEFAULT_VOLUME_UOM_ID") != null?run_uom_default.get("DEFAULT_VOLUME_UOM_ID"):"").toString();	
		
		Double normalmwInf = getCustomNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId),defaultMWUomId_runStep,new StringBuilder());
		Double normalQuantity = getCustomNormalNumber(mainArgVal, elementValueMap.get("QUANTITYRATE_UOM"), defaultQuantityUomId, new StringBuilder());
		Double normalDensityInf = getCustomNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"), defaultdensityUomId_runStep, new StringBuilder()); 
		
		Double normalpurityInf;
		normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected
		
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Mole Calculation =></br>");
			if (normalQuantity == null || normalmwInf == null) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole. Quantity Rate=" + elementValueMap.get("quantityRate")
								+ "; MW = " + normalmwInf + ";purity = " + normalpurityInf,
						ActivitylogType.Calculation, formId);
			} else if (normalmwInf == 0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole rate = " + normalmwInf + "(mw)*" + normalpurityInf
								+ "(purity). The dividing parameter[purity*mw] in the mole calculation is 0",
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: Quantity*Purity/100/MW.</br> Normal Args:" + String.valueOf(normalQuantity)
						+ "*(" + normalpurityInf.toString() + "/100)/" + normalmwInf.toString() + "</br>");
				elementValueMap.put("moleRate",
						getFromNormalNumber(String.valueOf((normalQuantity * (normalpurityInf / 100) / (normalmwInf))),
								elementValueMap.get("MOLERATEUOM_ID"), sbInfo));
			}

		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on mole =" + elementValueMap.get("moleRate"),
					ActivitylogType.Calculation, formId);
		}
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Volume Calculation =></br>");
			if (normalQuantity == null || normalDensityInf == null) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume. Quantity" + elementValueMap.get("quantityRate")
								+ "; Density = " + elementValueMap.get("densityInf") + ";purity = " + normalpurityInf,
						ActivitylogType.Calculation, formId);
				if(normalDensityInf == null)
				{
					elementValueMap.put("warningMsg", "The following value is missing \"Solvent - Density\". Calculation cannot be completed.");
				}
			} else if (normalDensityInf == 0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume = " + normalQuantity + "(quantity)/"
								+ normalDensityInf
								+ "(density). The dividing parameter[density] in the volume calculation is 0. "
								+ elementValueMap.get("volumeRate"),
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: Quantity/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
						+ normalDensityInf.toString() + "</br>");
				elementValueMap.put("volumeRate", getCustomNormalNumber(String.valueOf((normalQuantity / (normalDensityInf))),defaultVolumeUOMId,
						elementValueMap.get("VOLRATEUOM_ID"), sbInfo).toString());
				generalUtilLogger.logWriter(LevelType.INFO,
						sbInfo.toString() ,ActivitylogType.Calculation, formId);
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on volume rate=" + elementValueMap.get("volumeRate"),
					ActivitylogType.Calculation, formId);
		}
		
		materialSolventRatioCalc(elementValueMap, formId);
		sb = createJsonMaterialCalcSB(elementValueMap);
		
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventQuantityCalc - elementValueMap after calulation= mole:"
						+ elementValueMap.get("mole") + ",quantity:" + elementValueMap.get("quantity"),
				ActivitylogType.Calculation, formId);

		return sb;
	}

	/**
	 * calc material Solvent's values on click Mole calc button
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */
	private StringBuilder materialSolventMoleCalc(Map<String, String> elementValueMap, String mainArgVal,
			String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(
				formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),
				"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID,
				elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventMoleCalc - elementValueMap before calulation= volume:"
						+ elementValueMap.get("volume") + ",quantity:" + elementValueMap.get("quantity"),
				ActivitylogType.Calculation, formId);
		StringBuilder sb = new StringBuilder();

		Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
		Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"));
		Double normalMole = getNormalNumber(mainArgVal, elementValueMap.get("MOLEUOM_ID"));
		Double normalpurityInf;
		normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected

		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Quantity Calculation =></br>");
			if (normalMole == null || normalmwInf == null || normalpurityInf == 0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on quantity = mole*mw/purity/100. Mole" + mainArgVal
								+ "; MW = " + normalmwInf + "; Purity = " + normalpurityInf,
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: mole*Mw/Purity/100.</br> Normal Args:" + String.valueOf(normalMole) + "*"
						+ normalmwInf.toString() + "/(" + normalpurityInf.toString() + "/100)" + "</br>");
				elementValueMap.put("quantity",
						getFromNormalNumber(String.valueOf((normalMole * normalmwInf) / (normalpurityInf / 100)),
								elementValueMap.get("QUANTITYUOM_ID"), sbInfo));
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on quantity =" + elementValueMap.get("quantity"),
					ActivitylogType.Calculation, formId);
		}
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Volume Calculation =></br>");
			Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"),
					elementValueMap.get("QUANTITYUOM_ID"));
			if (normalMole == null || normalDensityInf == null || (normalDensityInf) == 0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume. Mole" + mainArgVal + "; Density = "
								+ elementValueMap.get("densityInf") + ";Purity = " + normalpurityInf,
						ActivitylogType.Calculation, formId);
				if(normalDensityInf == null)
				{
					elementValueMap.put("warningMsg", "The following value is missing \"Solvent - Density\". Calculation cannot be completed.");
				}
			} else {
				sbInfo.append("Formula: Quantity/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
						+ normalDensityInf.toString() + "</br>");
				elementValueMap.put("volume", getFromNormalNumber(String.valueOf(normalQuantity / (normalDensityInf)),
						elementValueMap.get("VOLUOM_ID"), sbInfo));
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on volume =" + elementValueMap.get("volume"),
					ActivitylogType.Calculation, formId);
		}		
		materialSolventRatioCalc(elementValueMap, formId);
		sb = createJsonMaterialCalcSB(elementValueMap);
		
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventMoleCalc - elementValueMap after calulation= volume:"
						+ elementValueMap.get("volume") + ",quantity:" + elementValueMap.get("quantity"),
				ActivitylogType.Calculation, formId);
		return sb;
	}
	
	/**
	 * calc material Solvent's values on click Mole Rate calc button
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */
	private StringBuilder materialSolventMoleRateCalc(Map<String, String> elementValueMap, String mainArgVal,
			String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(
				formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),
				"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID,
				elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventMoleCalc - elementValueMap before calulation= volume:"
						+ elementValueMap.get("volumeRate") + ",quantity:" + elementValueMap.get("quantityRate"),
				ActivitylogType.Calculation, formId);
		StringBuilder sb = new StringBuilder();

		Map<String, String> run_uom_default = generalDao.sqlToHashMap("select * from fg_i_calcrun_uom_override_v");

		String defaultMWUomId_runStep = (run_uom_default.get("DEFAULT_MW_UOM_ID") != null
				? run_uom_default.get("DEFAULT_MW_UOM_ID") : "").toString();
		String defaultQuantityUomId = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null
				? run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") : "").toString();
		String defaultdensityUomId_runStep = (run_uom_default.get("DEFAULT_DENSITY_UOM_ID") != null
				? run_uom_default.get("DEFAULT_DENSITY_UOM_ID") : "").toString();
		String defaultVolumeUOMId = (run_uom_default.get("DEFAULT_VOLUME_UOM_ID") != null
				? run_uom_default.get("DEFAULT_VOLUME_UOM_ID") : "").toString();

		Double normalmwInf = getCustomNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId), defaultMWUomId_runStep,
				new StringBuilder());
		Double normalDensityInf = getCustomNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"), defaultdensityUomId_runStep, new StringBuilder());

		/*Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
		Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"));*/
		Double normalMole = getNormalNumber(mainArgVal, elementValueMap.get("MOLERATEUOM_ID"));
		Double normalpurityInf;
		normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected

		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Quantity Calculation =></br>");
			if (normalMole == null || normalmwInf == null || normalpurityInf == 0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on quantity = mole*mw/purity/100. Mole" + mainArgVal
								+ "; MW = " + normalmwInf + "; Purity = " + normalpurityInf,
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: mole*Mw/Purity/100.</br> Normal Args:" + String.valueOf(normalMole) + "*"
						+ normalmwInf.toString() + "/(" + normalpurityInf.toString() + "/100)" + "</br>");
				elementValueMap.put("quantityRate",
						getCustomNormalNumber(String.valueOf((normalMole * normalmwInf) / (normalpurityInf / 100)),defaultQuantityUomId,
								elementValueMap.get("QUANTITYRATE_UOM"), sbInfo).toString());
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on quantity rate=" + elementValueMap.get("quantityRate"),
					ActivitylogType.Calculation, formId);
		}
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Volume Calculation =></br>");
			Double normalQuantity = getCustomNormalNumber(elementValueMap.get("quantityRate"),
					elementValueMap.get("QUANTITYRATE_UOM"),defaultQuantityUomId,new StringBuilder());
			if (normalMole == null || normalDensityInf == null || (normalDensityInf) == 0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume. Mole" + mainArgVal + "; Density = "
								+ elementValueMap.get("densityInf") + ";Purity = " + normalpurityInf,
						ActivitylogType.Calculation, formId);
				if(normalDensityInf == null)
				{
					elementValueMap.put("warningMsg", "The following value is missing \"Solvent - Density\". Calculation cannot be completed.");
				}
			} else {
				sbInfo.append("Formula: Quantity/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
						+ normalDensityInf.toString() + "</br>");
				elementValueMap.put("volumeRate", getCustomNormalNumber(String.valueOf(normalQuantity / (normalDensityInf)),defaultVolumeUOMId,
						elementValueMap.get("VOLRATEUOM_ID"), sbInfo).toString());
				generalUtilLogger.logWriter(LevelType.INFO,
						sbInfo.toString() ,ActivitylogType.Calculation, formId);
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on volume rate =" + elementValueMap.get("volumeRate"),
					ActivitylogType.Calculation, formId);
		}		
		materialSolventRatioCalc(elementValueMap, formId);
		sb = createJsonMaterialCalcSB(elementValueMap);
		
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventMoleCalc - elementValueMap after calulation= volume rate:"
						+ elementValueMap.get("volumeRate") + ",quantity rate:" + elementValueMap.get("quantityrate"),
				ActivitylogType.Calculation, formId);
		return sb;
	}

	/**
	 * calc material Solvent's values on click Volume calc button
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */
	private StringBuilder materialSolventVolumeCalc(Map<String, String> elementValueMap, String mainArgVal, String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(
				formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),
				"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID,
				elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventVolumeCalc - elementValueMap before calulation= mole:"
						+ elementValueMap.get("mole") + ",quantity:" + elementValueMap.get("quantity"),
				ActivitylogType.Calculation, formId);
		StringBuilder sb = new StringBuilder();
		Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
		Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"));
		Double normalVolume = getNormalNumber(mainArgVal, elementValueMap.get("VOLUOM_ID"));
		Double normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected
		/*
		 * if (!generalUtil.getNull(elementValueMap.get("purityInf")).isEmpty()) {
		 * normalpurityInf = getNormalNumber(elementValueMap.get("purityInf"),
		 * elementValueMap.get("PURITYUOM_ID_INF"));
		 * } else {
		 * normalpurityInf = (float) 1; // Default value 1 when batch is not selected
		 * }
		 */

		// if((elementValueMap.get("isLimited").equals("0") || elementValueMap.get("limitingAgent").equals("1"))){
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Quantity Calculation =></br>");
			if (normalVolume == null || normalDensityInf == null) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on quantity. Volume =" + elementValueMap.get("volume")
								+ "; Density = " + elementValueMap.get("densityInf"),
						ActivitylogType.Calculation, formId);
				
				if(normalDensityInf == null)
				{
					elementValueMap.put("warningMsg", "The following value is missing \"Solvent - Density\". Calculation cannot be completed.");
				}
			} else {
				sbInfo.append("Formula: Volume*Density.</br> Normal Args:" + String.valueOf(normalVolume) + "*"
						+ normalDensityInf.toString() + "</br>");
				elementValueMap.put("quantity", getFromNormalNumber(String.valueOf((normalVolume * normalDensityInf)),
						elementValueMap.get("QUANTITYUOM_ID"), sbInfo));
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on quantity =" + elementValueMap.get("quantity"),
					ActivitylogType.Calculation, formId);
		}
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Mole Calculation =></br>");
			Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"),
					elementValueMap.get("QUANTITYUOM_ID"));
			if (normalVolume == null || normalDensityInf == null || normalmwInf == null || normalmwInf == 0.0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole. Volume =" + elementValueMap.get("volume")
								+ "; Density = " + elementValueMap.get("densityInf") + ";MW ="
								+ generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: Quantity*Purity/100/MW.</br> Normal Args:" + String.valueOf(normalQuantity)
						+ "*(" + normalpurityInf.toString() + "/100)/" + normalmwInf.toString() + "</br>");
				elementValueMap.put("mole",
						getFromNormalNumber(String.valueOf(normalQuantity * (normalpurityInf / 100) / (normalmwInf)),
								elementValueMap.get("MOLEUOM_ID"), sbInfo));
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on mole = " + elementValueMap.get("mole"),
					ActivitylogType.Calculation, formId);
		}		
		materialSolventRatioCalc(elementValueMap, formId);		
		sb = createJsonMaterialCalcSB(elementValueMap);
		/*
		 * }else {
		 * sb=createJsonMaterialCalcSB(elementValueMap);
		 * }
		 */
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventVolumeCalc - elementValueMap after calulation= mole:"
						+ elementValueMap.get("mole") + ",quantity:" + elementValueMap.get("quantity"),
				ActivitylogType.Calculation, formId);
		return sb;
	}
	
	/**
	 * calc material Solvent's values on click Volume rate calc button
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */
	private StringBuilder materialSolventVolumeRateCalc(Map<String, String> elementValueMap, String mainArgVal
			, String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(
				formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),
				"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID,
				elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventVolumeCalc - elementValueMap before calulation= mole rate:"
						+ elementValueMap.get("moleRate") + ",quantity:" + elementValueMap.get("quantityRate"),
				ActivitylogType.Calculation, formId);
		StringBuilder sb = new StringBuilder();
		/*Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
		Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"));
		Double normalVolume = getNormalNumber(mainArgVal, elementValueMap.get("VOLRATEUOM_ID"));*/
		
       Map<String, String> run_uom_default = generalDao.sqlToHashMap("select * from fg_i_calcrun_uom_override_v");
		
		String defaultMWUomId_runStep = (run_uom_default.get("DEFAULT_MW_UOM_ID") != null?run_uom_default.get("DEFAULT_MW_UOM_ID"):"").toString();	
		String defaultQuantityUomId = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();	
		String defaultdensityUomId_runStep = (run_uom_default.get("DEFAULT_DENSITY_UOM_ID") != null?run_uom_default.get("DEFAULT_DENSITY_UOM_ID"):"").toString();	
		String defaultVolumeUOMId = (run_uom_default.get("DEFAULT_VOLUME_UOM_ID") != null?run_uom_default.get("DEFAULT_VOLUME_UOM_ID"):"").toString();	
		
		Double normalmwInf = getCustomNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId),defaultMWUomId_runStep,new StringBuilder());
		Double normalVolume = getCustomNormalNumber(mainArgVal, elementValueMap.get("VOLRATEUOM_ID"), defaultVolumeUOMId, new StringBuilder());
		Double normalDensityInf = getCustomNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"), defaultdensityUomId_runStep, new StringBuilder()); 
		
		
		Double normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected
		/*
		 * if (!generalUtil.getNull(elementValueMap.get("purityInf")).isEmpty()) {
		 * normalpurityInf = getNormalNumber(elementValueMap.get("purityInf"),
		 * elementValueMap.get("PURITYUOM_ID_INF"));
		 * } else {
		 * normalpurityInf = (float) 1; // Default value 1 when batch is not selected
		 * }
		 */

		// if((elementValueMap.get("isLimited").equals("0") || elementValueMap.get("limitingAgent").equals("1"))){
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Quantity Calculation =></br>");
			if (normalVolume == null || normalDensityInf == null) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on quantity. Volume Rate =" + elementValueMap.get("volumeRate")
								+ "; Density = " + elementValueMap.get("densityInf"),
						ActivitylogType.Calculation, formId);
				
				if(normalDensityInf == null)
				{
					elementValueMap.put("warningMsg", "The following value is missing \"Solvent - Density\". Calculation cannot be completed.");
				}
			} else {
				sbInfo.append("Formula: Volume*Density.</br> Normal Args:" + String.valueOf(normalVolume) + "*"
						+ normalDensityInf.toString() + "</br>");
				elementValueMap.put("quantityRate", getCustomNormalNumber(String.valueOf((normalVolume * normalDensityInf)),defaultQuantityUomId,
						elementValueMap.get("QUANTITYRATE_UOM"), sbInfo).toString());
				generalUtilLogger.logWriter(LevelType.INFO,
						sbInfo.toString() ,ActivitylogType.Calculation, formId);
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on quantity rate =" + elementValueMap.get("quantityRate"),
					ActivitylogType.Calculation, formId);
		}
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Mole Calculation =></br>");
			Double normalQuantity = getCustomNormalNumber(elementValueMap.get("quantityRate"),
					elementValueMap.get("QUANTITYRATE_UOM"),defaultQuantityUomId,new StringBuilder());
			if (normalVolume == null || normalDensityInf == null || normalmwInf == null || normalmwInf == 0.0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole. Volume Rate =" + elementValueMap.get("volumeRate")
								+ "; Density = " + elementValueMap.get("densityInf") + ";MW ="
								+ generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: Quantity*Purity/100/MW.</br> Normal Args:" + String.valueOf(normalQuantity)
						+ "*(" + normalpurityInf.toString() + "/100)/" + normalmwInf.toString() + "</br>");
				elementValueMap.put("moleRate",
						getFromNormalNumber(String.valueOf(normalQuantity * (normalpurityInf / 100) / (normalmwInf)),
								elementValueMap.get("MOLERATEUOM_ID"), sbInfo));
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on mole rate = " + elementValueMap.get("moleRate"),
					ActivitylogType.Calculation, formId);
		}		
		materialSolventRatioCalc_Rate(elementValueMap, formId);		
		sb = createJsonMaterialCalcSB(elementValueMap);
		/*
		 * }else {
		 * sb=createJsonMaterialCalcSB(elementValueMap);
		 * }
		 */
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventVolumeCalc - elementValueMap after calulation= mole rate:"
						+ elementValueMap.get("moleRate") + ",quantity rate:" + elementValueMap.get("quantityRate"),
				ActivitylogType.Calculation, formId);
		return sb;
	}
	
	private StringBuilder materialSolventRatioCalc(Map<String, String> elementValueMap, String formId) 
	{
		StringBuilder sb = new StringBuilder();
		Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"), elementValueMap.get("QUANTITYUOM_ID"));
		Double normalVolume = getNormalNumber(elementValueMap.get("volume"), elementValueMap.get("VOLUOM_ID"));
		Double normalMole = getNormalNumber(elementValueMap.get("mole"), elementValueMap.get("MOLEUOM_ID"));
		String ratioTypeUniqueName = generalUtil.getEmpty(elementValueMap.get("ratiotype_id"),"0");
		if(ratioTypeUniqueName.equals("SolventVolByReactantQnty"))
		{
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Solvent Ratio Calculation =></br>");
				//Double normalReactantQuantity = getNormalNumber(elementValueMap.get("reactantQuantity"), elementValueMap.get("reactantQuantityUOM"));//replaced by the following row up to bug 8000
				Double normalReactantQuantity = generalUtil.getNull(elementValueMap.get("reactantQuantity")).isEmpty()?null:Double.valueOf(elementValueMap.get("reactantQuantity"));
				if (normalVolume == null || normalReactantQuantity == null) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on ratio. Volume =" + elementValueMap.get("volume")
									+ "; Reactant Quantity = " + elementValueMap.get("reactantQuantity"),
							ActivitylogType.Calculation, formId);
				} else if (normalReactantQuantity == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on Ratio. "
									+ elementValueMap.get("volume") + "(volume)/" + normalReactantQuantity + "(reactant quantity)."
									+" The dividing parameter[reactant quantity] in the ratio calculation is 0. "
									+ elementValueMap.get("ratio"),
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Volume/Reactant Quantity.</br> Args:"
									+ String.valueOf(elementValueMap.get("volume")) + "/" + String.valueOf(normalReactantQuantity) + "</br>");
					elementValueMap.put("ratio",String.valueOf(Double.valueOf(elementValueMap.get("volume")) / normalReactantQuantity));//getFromNormalNumber(String.valueOf(normalVolume / normalReactantQuantity),null, sbInfo)
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation, "");
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on ratio = " + elementValueMap.get("ratio"),
						ActivitylogType.Calculation, formId);
			}
		}
		else if(ratioTypeUniqueName.equals("SolventVolByReactantMoles"))
		{
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Solvent Ratio Calculation =></br>");
				//Double normalReactantMole = getNormalNumber(elementValueMap.get("reactantMole"), elementValueMap.get("reactantMoleUOM"));replaced by the following row up to bug 8000
				Double normalReactantMole = generalUtil.getNull(elementValueMap.get("reactantMole")).isEmpty()?null:Double.valueOf(elementValueMap.get("reactantMole"));
				if (normalVolume == null || normalReactantMole == null) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on ratio. Volume =" + elementValueMap.get("volume")
									+ "; Reactant Mole = " + elementValueMap.get("reactantMole"),
							ActivitylogType.Calculation, formId);
				} else if (normalReactantMole == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on Ratio. "
									+ elementValueMap.get("volume") + "(volume)/" + normalReactantMole + "(reactant mole)."
									+" The dividing parameter[reactant quantity] in the ratio calculation is 0. "
									+ elementValueMap.get("ratio"),
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Volume/Reactant Mole.</br> Args:"
									+ elementValueMap.get("volume")+ "/" + String.valueOf(normalReactantMole) + "</br>");
					elementValueMap.put("ratio",String.valueOf(Double.valueOf(elementValueMap.get("volume")) / normalReactantMole));//getFromNormalNumber(String.valueOf(normalVolume / normalReactantMole),null, sbInfo)
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation, "");
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on ratio = " + elementValueMap.get("ratio"),
						ActivitylogType.Calculation, formId);
			}
		}
		else if(ratioTypeUniqueName.equals("SolventMolesByReactantMoles"))
		{
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Solvent Ratio Calculation =></br>");
				//Double normalReactantMole = getNormalNumber(elementValueMap.get("reactantMole"), elementValueMap.get("reactantMoleUOM"));//replaced by the following row up to bug 8000
				Double normalReactantMole = generalUtil.getNull(elementValueMap.get("reactantMole")).isEmpty()?null:Double.valueOf(elementValueMap.get("reactantMole"));
				if (normalMole == null || normalReactantMole == null) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on ratio. Mole =" + elementValueMap.get("mole")
									+ "; Reactant Mole = " + elementValueMap.get("reactantMole"),
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Mole/Reactant Mole.</br> Args:"
									+ elementValueMap.get("mole") + "/" + String.valueOf(normalReactantMole) + "</br>");
					elementValueMap.put("ratio",String.valueOf(Double.valueOf(elementValueMap.get("mole")) / normalReactantMole));//getFromNormalNumber(String.valueOf(normalMole / normalReactantMole),null, sbInfo)
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation, "");
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on ratio = " + elementValueMap.get("ratio"),
						ActivitylogType.Calculation, formId);
			}
		}
		else if(ratioTypeUniqueName.equals("SolventQntyByReactantQnty"))
		{
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Solvent Ratio Calculation =></br>");
				//Double normalReactantQuantity = getNormalNumber(elementValueMap.get("reactantQuantity"), elementValueMap.get("reactantQuantityUOM"));//replaced with the following row up to bug 8000
				Double normalReactantQuantity = generalUtil.getNull(elementValueMap.get("reactantQuantity")).isEmpty()?null:Double.valueOf(elementValueMap.get("reactantQuantity"));
				if (normalQuantity == null || normalReactantQuantity == null) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on ratio. Quantity =" + elementValueMap.get("quantity")
									+ "; Reactant Quantity = " + elementValueMap.get("reactantQuantity"),
							ActivitylogType.Calculation, formId);
				} else if (normalReactantQuantity == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on Ratio. "
									+ elementValueMap.get("quantity") + "(quantity)/" + normalReactantQuantity + "(reactant quantity)."
									+" The dividing parameter[reactant quantity] in the ratio calculation is 0. "
									+ elementValueMap.get("ratio"),
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Quantity/Reactant Quantity.</br> Args:"
									+ elementValueMap.get("quantity") + "/" + String.valueOf(normalReactantQuantity) + "</br>");
					elementValueMap.put("ratio",String.valueOf(Double.valueOf(elementValueMap.get("quantity")) / normalReactantQuantity));//getFromNormalNumber(,null, sbInfo)
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation, "");
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on ratio = " + elementValueMap.get("ratio"),
						ActivitylogType.Calculation, formId);
			}
		}
		return sb;
	}
	
	private StringBuilder materialSolventRatioCalc_Rate(Map<String, String> elementValueMap, String formId) 
	{
		StringBuilder sb = new StringBuilder();
        Map<String, String> run_uom_default = generalDao.sqlToHashMap("select * from fg_i_calcrun_uom_override_v");
		
		String defaultQuantityUomId = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();	
		String defaultVolumeUOMId = (run_uom_default.get("DEFAULT_VOLUME_UOM_ID") != null?run_uom_default.get("DEFAULT_VOLUME_UOM_ID"):"").toString();	
		
		Double normalQuantity = getCustomNormalNumber(elementValueMap.get("quantityRate"), elementValueMap.get("QUANTITYRATE_UOM"),defaultQuantityUomId,sb);
		Double normalVolume = getCustomNormalNumber(elementValueMap.get("volumeRate"), elementValueMap.get("VOLRATEUOM_ID"),defaultVolumeUOMId,sb);
		Double normalMole = getNormalNumber(elementValueMap.get("moleRate"), elementValueMap.get("MOLERATEUOM_ID"));
		String ratioTypeUniqueName = generalUtil.getEmpty(elementValueMap.get("ratiotype_id"),"0");
		if(ratioTypeUniqueName.equals("SolventVolByReactantQnty"))
		{
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Solvent Ratio Calculation =></br>");
				//Double normalReactantQuantity = getNormalNumber(elementValueMap.get("reactantQuantity"), elementValueMap.get("reactantQuantityUOM"));//replaced by the following row up to bug 8000
				Double normalReactantQuantity = generalUtil.getNull(elementValueMap.get("reactantQuantity")).isEmpty()?null:Double.valueOf(elementValueMap.get("reactantQuantity"));
				if(normalReactantQuantity != null){
					String normalReactantQuantity_runStep = getFromNormalNumber(normalReactantQuantity.toString(),defaultQuantityUomId,sb);
					if(!generalUtil.getNull(normalReactantQuantity_runStep).isEmpty()){
						normalReactantQuantity = Double.parseDouble(normalReactantQuantity_runStep);
					}
				}
				if (normalVolume == null || normalReactantQuantity == null) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on ratio. Volume =" + elementValueMap.get("volumeRate")
									+ "; Reactant Quantity = " + elementValueMap.get("reactantQuantity"),
							ActivitylogType.Calculation, formId);
				} else if (normalReactantQuantity == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on Ratio. "
									+ elementValueMap.get("volumeRate") + "(volume)/" + normalReactantQuantity + "(reactant quantity)."
									+" The dividing parameter[reactant quantity] in the ratio calculation is 0. "
									+ elementValueMap.get("ratio"),
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Volume/Reactant Quantity.</br> Args:"
									+ String.valueOf(elementValueMap.get("volumeRate")) + "/" + String.valueOf(normalReactantQuantity) + "</br>");
					elementValueMap.put("ratio",String.valueOf(Double.valueOf(elementValueMap.get("volumeRate")) / normalReactantQuantity));//getFromNormalNumber(String.valueOf(normalVolume / normalReactantQuantity),null, sbInfo)
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation, "");
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on ratio = " + elementValueMap.get("ratio"),
						ActivitylogType.Calculation, formId);
			}
		}
		else if(ratioTypeUniqueName.equals("SolventVolByReactantMoles"))
		{
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Solvent Ratio Calculation =></br>");
				//Double normalReactantMole = getNormalNumber(elementValueMap.get("reactantMole"), elementValueMap.get("reactantMoleUOM"));replaced by the following row up to bug 8000
				Double normalReactantMole = generalUtil.getNull(elementValueMap.get("reactantMole")).isEmpty()?null:Double.valueOf(elementValueMap.get("reactantMole"));
				if (normalVolume == null || normalReactantMole == null) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on ratio. Volume =" + elementValueMap.get("volumeRate")
									+ "; Reactant Mole = " + elementValueMap.get("reactantMole"),
							ActivitylogType.Calculation, formId);
				} else if (normalReactantMole == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on Ratio. "
									+ elementValueMap.get("volumeRate") + "(volume)/" + normalReactantMole + "(reactant mole)."
									+" The dividing parameter[reactant quantity] in the ratio calculation is 0. "
									+ elementValueMap.get("ratio"),
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Volume/Reactant Mole.</br> Args:"
									+ elementValueMap.get("volumeRate")+ "/" + String.valueOf(normalReactantMole) + "</br>");
					elementValueMap.put("ratio",String.valueOf(Double.valueOf(elementValueMap.get("volumeRate")) / normalReactantMole));//getFromNormalNumber(String.valueOf(normalVolume / normalReactantMole),null, sbInfo)
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation, "");
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on ratio = " + elementValueMap.get("ratio"),
						ActivitylogType.Calculation, formId);
			}
		}
		else if(ratioTypeUniqueName.equals("SolventMolesByReactantMoles"))
		{
			try {
				StringBuilder sbInfo = new StringBuilder();
				
				sbInfo.append("Solvent Ratio Calculation =></br>");
				//Double normalReactantMole = getNormalNumber(elementValueMap.get("reactantMole"), elementValueMap.get("reactantMoleUOM"));//replaced by the following row up to bug 8000
				Double normalReactantMole = generalUtil.getNull(elementValueMap.get("reactantMole")).isEmpty()?null:Double.valueOf(elementValueMap.get("reactantMole"));
				if (normalMole == null || normalReactantMole == null) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on ratio. Mole =" + elementValueMap.get("moleRate")
									+ "; Reactant Mole = " + elementValueMap.get("reactantMole"),
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Mole/Reactant Mole.</br> Args:"
									+ elementValueMap.get("moleRate") + "/" + String.valueOf(normalReactantMole) + "</br>");
					elementValueMap.put("ratio",String.valueOf(Double.valueOf(elementValueMap.get("moleRate")) / normalReactantMole));//getFromNormalNumber(String.valueOf(normalMole / normalReactantMole),null, sbInfo)
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation, "");
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on ratio = " + elementValueMap.get("ratio"),
						ActivitylogType.Calculation, formId);
			}
		}
		else if(ratioTypeUniqueName.equals("SolventQntyByReactantQnty"))
		{
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Solvent Ratio Calculation =></br>");
				//Double normalReactantQuantity = getNormalNumber(elementValueMap.get("reactantQuantity"), elementValueMap.get("reactantQuantityUOM"));//replaced with the following row up to bug 8000
				Double normalReactantQuantity = generalUtil.getNull(elementValueMap.get("reactantQuantity")).isEmpty()?null:Double.valueOf(elementValueMap.get("reactantQuantity"));
				if(normalReactantQuantity != null){
					String normalReactantQuantity_runStep = getFromNormalNumber(normalReactantQuantity.toString(),defaultQuantityUomId,sb);
					if(!generalUtil.getNull(normalReactantQuantity_runStep).isEmpty()){
						normalReactantQuantity = Double.parseDouble(normalReactantQuantity_runStep);
					}
				}
				if (normalQuantity == null || normalReactantQuantity == null) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on ratio. Quantity =" + elementValueMap.get("quantityRate")
									+ "; Reactant Quantity = " + elementValueMap.get("reactantQuantity"),
							ActivitylogType.Calculation, formId);
				} else if (normalReactantQuantity == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on Ratio. "
									+ elementValueMap.get("quantityRate") + "(quantity)/" + normalReactantQuantity + "(reactant quantity)."
									+" The dividing parameter[reactant quantity] in the ratio calculation is 0. "
									+ elementValueMap.get("ratio"),
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Quantity/Reactant Quantity.</br> Args:"
									+ elementValueMap.get("quantityRate") + "/" + String.valueOf(normalReactantQuantity) + "</br>");
					elementValueMap.put("ratio",String.valueOf(Double.valueOf(elementValueMap.get("quantityRate")) / normalReactantQuantity));//getFromNormalNumber(,null, sbInfo)
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation, "");
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on ratio = " + elementValueMap.get("ratio"),
						ActivitylogType.Calculation, formId);
			}
		}
		return sb;
	}
	
	/**
	 * update material Solvent's values
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */
	private void updateSolvenValuesMapByConnectedReactantValues (JSONObject elementValueJson, Map<String, String> elementValueMap, String formId)
	{		
		String reactantMaterialId = generalUtil.getEmpty(elementValueMap.get("reactantmaterial_id"),"0");
		String ratioTypeUniqueName = generalUtil.getEmpty(elementValueMap.get("ratiotype_id"),"0");
		if(reactantMaterialId.equals("0") || ratioTypeUniqueName.equals("0")) 
		{
			elementValueMap.put("reactantQuantity", "");
			elementValueMap.put("reactantQuantityUOM", "");
			elementValueMap.put("reactantMole", "");
			elementValueMap.put("reactantMoleUOM", "");
		}
		else
		{
			Iterator<String> keysItr = elementValueJson.keys();
			while (keysItr.hasNext()) {
				String id = keysItr.next(); //row id
				if(!id.equals(formId))
				{
					JSONObject rowData = elementValueJson.getJSONObject(id);
					String tableType = rowData.optString("tableType");
					if(tableType.equalsIgnoreCase("Reactant"))
					{
						if(reactantMaterialId.equals(rowData.optString("INVITEMMATERIAL_ID")))
						{						
							elementValueMap.put("reactantQuantity", rowData.optString("quantity"));
							elementValueMap.put("reactantQuantityUOM", rowData.optString("QUANTITYUOM_ID"));
							elementValueMap.put("reactantMole", rowData.optString("mole"));
							elementValueMap.put("reactantMoleUOM", rowData.optString("MOLEUOM_ID"));
				
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * update material Solvent's values
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */
	private void updateSolvenValuesMapByConnectedReactantRateValues (JSONObject elementValueJson, Map<String, String> elementValueMap, String formId)
	{		
		String reactantMaterialId = generalUtil.getEmpty(elementValueMap.get("reactantmaterial_id"),"0");
		String ratioTypeUniqueName = generalUtil.getEmpty(elementValueMap.get("ratiotype_id"),"0");
		if(reactantMaterialId.equals("0") || ratioTypeUniqueName.equals("0")) 
		{
			elementValueMap.put("reactantQuantity", "");
			elementValueMap.put("reactantQuantityUOM", "");
			elementValueMap.put("reactantMole", "");
			elementValueMap.put("reactantMoleUOM", "");
		}
		else
		{
			Iterator<String> keysItr = elementValueJson.keys();
			while (keysItr.hasNext()) {
				String id = keysItr.next(); //row id
				if(!id.equals(formId))
				{
					JSONObject rowData = elementValueJson.getJSONObject(id);
					String tableType = rowData.optString("tableType");
					if(tableType.equalsIgnoreCase("Reactant"))
					{
						if(reactantMaterialId.equals(rowData.optString("INVITEMMATERIAL_ID")))
						{						
							elementValueMap.put("reactantQuantity", rowData.optString("quantityRate"));
							elementValueMap.put("reactantQuantityUOM", rowData.optString("QUANTITYRATE_UOM"));
							elementValueMap.put("reactantMole", rowData.optString("moleRate"));
							elementValueMap.put("reactantMoleUOM", rowData.optString("MOLERATEUOM_ID"));
				
							break;
						}
					}
				}
			}
		}
	}
	/**
	 * calc material Solvent's values on click Ratio calc button
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */
	private StringBuilder materialSolventByRatioTypeCalc(JSONObject elementValueJson, Map<String, String> elementValueMap, String mainArgVal,
			String formId)
	{
		StringBuilder sb = new StringBuilder();
		String reactantMaterialId = generalUtil.getEmpty(elementValueMap.get("reactantmaterial_id"),"0");
		String ratioTypeUniqueName = generalUtil.getEmpty(elementValueMap.get("ratiotype_id"),"0");
		if(!reactantMaterialId.equals("0") && !ratioTypeUniqueName.equals("0")) 
		{
			if(ratioTypeUniqueName.equals("SolventVolByReactantQnty")) {
				sb = materialSolventVolumeByReactantQntyCalc(elementValueMap, mainArgVal, formId);
			} else if(ratioTypeUniqueName.equals("SolventQntyByReactantQnty")) {
				sb = materialSolventQntyByReactantQntyCalc(elementValueMap, mainArgVal, formId);
			} else if(ratioTypeUniqueName.equals("SolventVolByReactantMoles")) {
				sb = materialSolventVolumeByReactantMoleCalc(elementValueMap, mainArgVal, formId);
			} else if(ratioTypeUniqueName.equals("SolventMolesByReactantMoles")) {
				sb = materialSolventMoleByReactantMoleCalc(elementValueMap, mainArgVal, formId);
			}
		}
		else
		{
			sb = createJsonMaterialCalcSB(elementValueMap);
		}
		
		return sb;
	}
	
	/**
	 * calc material Solvent's values on click Ratio calc button
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */
	private StringBuilder materialSolventVolumeByReactantQntyCalc(Map<String, String> elementValueMap, String mainArgVal,String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
				
		Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
		Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"), elementValueMap.get("DENSITYUOM_ID_INF"));
		Double normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected
		Double normalReactantQuantity = getNormalNumber(elementValueMap.get("reactantQuantity"), elementValueMap.get("reactantQuantityUOM"));
		Double ratioVal = getNormalNumber(elementValueMap.get("ratio"), null);
		StringBuilder sb = new StringBuilder();
		
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventVolumeByReactantQntyCalc - elementValueMap before calulation= mole:"
						+ elementValueMap.get("mole") + ", quantity:" + elementValueMap.get("quantity")
						+ ", volume:" + elementValueMap.get("volume"),
				ActivitylogType.Calculation, formId);
		
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Volume Calculation By Reactant Quantity =></br>");
			if (normalReactantQuantity == null || ratioVal.equals("0")) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume. Reactant Quantity = " + elementValueMap.get("reactantQuantity") + 
							"; Ratio = " + elementValueMap.get("ratio"),
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: Ratio * Reactant Material Quantity.</br> Args:" + 
								ratioVal.toString() + "*" + String.valueOf(elementValueMap.get("reactantQuantity"))  + "</br>");
				elementValueMap.put("volume", String.valueOf(ratioVal * Double.valueOf(elementValueMap.get("reactantQuantity"))));
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on volume =" + elementValueMap.get("volume"),
					ActivitylogType.Calculation, formId);
		}
		
		Double normalVolume = getNormalNumber(elementValueMap.get("volume"), elementValueMap.get("VOLUOM_ID"));
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Quantity Calculation =></br>");
			if (normalVolume == null || normalDensityInf == null) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on quantity. Volume =" + elementValueMap.get("volume")
								+ "; Density = " + elementValueMap.get("densityInf"),
						ActivitylogType.Calculation, formId);
				
				if(normalDensityInf == null)
				{
					elementValueMap.put("warningMsg", "The following value is missing \"Solvent - Density\". Calculation cannot be completed.");
				}
			} else {
				sbInfo.append("Formula: Volume*Density.</br> Normal Args:" + String.valueOf(normalVolume) + "*"
						+ normalDensityInf.toString() + "</br>");
				elementValueMap.put("quantity", getFromNormalNumber(String.valueOf((normalVolume * normalDensityInf)),
						elementValueMap.get("QUANTITYUOM_ID"), sbInfo));
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on quantity =" + elementValueMap.get("quantity"),
					ActivitylogType.Calculation, formId);
		}
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Mole Calculation =></br>");
			Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"),elementValueMap.get("QUANTITYUOM_ID"));
			if (normalQuantity == null || normalmwInf == null || normalmwInf == 0.0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole. Quantity =" + elementValueMap.get("quantity")
								+ "; Purity = " + elementValueMap.get("purityInf") + ";MW ="
								+ generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: Quantity*Purity/100/MW.</br> Normal Args:" + String.valueOf(normalQuantity)
						+ "*(" + normalpurityInf.toString() + "/100)/" + normalmwInf.toString() + "</br>");
				elementValueMap.put("mole",
						getFromNormalNumber(String.valueOf(normalQuantity * (normalpurityInf / 100) / (normalmwInf)),
								elementValueMap.get("MOLEUOM_ID"), sbInfo));
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on mole =" + elementValueMap.get("mole"),
					ActivitylogType.Calculation, formId);
		}
		
		sb = createJsonMaterialCalcSB(elementValueMap);
		
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventVolumeByReactantQntyCalc - elementValueMap after calulation= mole:"
						+ elementValueMap.get("mole") + ",quantity:" + elementValueMap.get("quantity")
						+ ", volume:" + elementValueMap.get("volume"),
				ActivitylogType.Calculation, formId);
		
		return sb;
	}
	
	/**
	 * calc material Solvent's values on click Ratio calc button
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */
	private StringBuilder materialSolventVolumeByReactantMoleCalc(Map<String, String> elementValueMap, String mainArgVal,String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
				elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
				
		Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
		Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"), elementValueMap.get("DENSITYUOM_ID_INF"));
		Double normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected
		Double normalReactantMole = getNormalNumber(elementValueMap.get("reactantMole"), elementValueMap.get("reactantMoleUOM"));
		Double ratioVal = getNormalNumber(elementValueMap.get("ratio"), null);
		StringBuilder sb = new StringBuilder();
		
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventVolumeByReactantMoleCalc - elementValueMap before calulation= mole:"
						+ elementValueMap.get("mole") + ", quantity:" + elementValueMap.get("quantity")
						+ ", volume:" + elementValueMap.get("volume"),
				ActivitylogType.Calculation, formId);
		
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Volume Calculation By Reactant Mole =></br>");
			if (normalReactantMole == null || ratioVal.equals("0")) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume. Reactant Mole = " + elementValueMap.get("reactantMole") + 
							"; Ratio = " + elementValueMap.get("ratio"),
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: Ratio * Reactant Material Mole.</br> Args:" + 
								ratioVal.toString() + "*" + String.valueOf(elementValueMap.get("reactantMole"))  + "</br>");
				elementValueMap.put("volume", String.valueOf(ratioVal * Double.valueOf(elementValueMap.get("reactantMole"))));
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on volume =" + elementValueMap.get("volume"),
					ActivitylogType.Calculation, formId);
		}
		
		Double normalVolume = getNormalNumber(elementValueMap.get("volume"), elementValueMap.get("VOLUOM_ID"));
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Quantity Calculation =></br>");
			if (normalVolume == null || normalDensityInf == null) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on quantity. Volume =" + elementValueMap.get("volume")
								+ "; Density = " + elementValueMap.get("densityInf"),
						ActivitylogType.Calculation, formId);
				if(normalDensityInf == null)
				{
					elementValueMap.put("warningMsg", "The following value is missing \"Solvent - Density\". Calculation cannot be completed.");
				}
			} else {
				sbInfo.append("Formula: Volume*Density.</br> Normal Args:" + String.valueOf(normalVolume) + "*"
						+ normalDensityInf.toString() + "</br>");
				elementValueMap.put("quantity", getFromNormalNumber(String.valueOf((normalVolume * normalDensityInf)),
						elementValueMap.get("QUANTITYUOM_ID"), sbInfo));
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on quantity =" + elementValueMap.get("quantity"),
					ActivitylogType.Calculation, formId);
		}
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Mole Calculation =></br>");
			Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"),elementValueMap.get("QUANTITYUOM_ID"));
			if (normalQuantity == null || normalmwInf == null || normalmwInf == 0.0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole. Quantity =" + elementValueMap.get("quantity")
								+ "; Purity = " + elementValueMap.get("purityInf") + ";MW ="
								+ generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: Quantity*Purity/100/MW.</br> Normal Args:" + String.valueOf(normalQuantity)
						+ "*(" + normalpurityInf.toString() + "/100)/" + normalmwInf.toString() + "</br>");
				elementValueMap.put("mole",
						getFromNormalNumber(String.valueOf(normalQuantity * (normalpurityInf / 100) / (normalmwInf)),
								elementValueMap.get("MOLEUOM_ID"), sbInfo));
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on mole =" + elementValueMap.get("mole"),
					ActivitylogType.Calculation, formId);
		}

		sb = createJsonMaterialCalcSB(elementValueMap);
		
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventVolumeByReactantMoleCalc - elementValueMap after calulation= mole:"
						+ elementValueMap.get("mole") + ",quantity:" + elementValueMap.get("quantity")
						+ ", volume:" + elementValueMap.get("volume"),
				ActivitylogType.Calculation, formId);
		return sb;
	}
	
	/**
	 * calc material Solvent's values on click Ratio calc button
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */
	private StringBuilder materialSolventQntyByReactantQntyCalc(Map<String, String> elementValueMap, String mainArgVal,
			String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(
				formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID,elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
		
		Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
		Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"),
				elementValueMap.get("DENSITYUOM_ID_INF"));
		Double normalpurityInf;
		normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected
		Double normalReactantQuantity = getNormalNumber(elementValueMap.get("reactantQuantity"), elementValueMap.get("reactantQuantityUOM"));
		Double ratioVal = getNormalNumber(elementValueMap.get("ratio"), null);
		StringBuilder sb = new StringBuilder();
		
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventQntyByReactantQntyCalc - elementValueMap before calulation= mole:"
						+ elementValueMap.get("mole") + ", quantity:" + elementValueMap.get("quantity")
						+ ", volume:" + elementValueMap.get("volume"),
				ActivitylogType.Calculation, formId);
		
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Quantity Calculation By Reactant Quantity =></br>");
			if (normalReactantQuantity == null || ratioVal.equals("0")) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on quantity. Reactant Quantity = " + elementValueMap.get("reactantQuantity") + 
							"; Ratio = " + elementValueMap.get("ratio"),
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: Ratio * Reactant Material Quantity.</br> Args:" + 
								ratioVal.toString() + "*" + elementValueMap.get("reactantQuantity") + "</br>");
				elementValueMap.put("quantity", String.valueOf((ratioVal * Double.valueOf(elementValueMap.get("reactantQuantity")))));
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on quantity =" + elementValueMap.get("quantity"),
					ActivitylogType.Calculation, formId);
		}
		
		Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"), elementValueMap.get("QUANTITYUOM_ID"));
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Volume Calculation =></br>");
			if (normalQuantity == null || normalDensityInf == null) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume. Quantity" + elementValueMap.get("quantity")
								+ "; Density = " + elementValueMap.get("densityInf"),
						ActivitylogType.Calculation, formId);
				if(normalDensityInf == null)
				{
					elementValueMap.put("warningMsg", "The following value is missing \"Solvent - Density\". Calculation cannot be completed.");
				}
			} else if (normalDensityInf == 0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume. "
								+ normalQuantity + "(quantity)/" + normalDensityInf + "(density)."
								+" The dividing parameter[density] in the volume calculation is 0. "
								+ elementValueMap.get("volume"),
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: Quantity/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
						+ normalDensityInf.toString() + "</br>");
				elementValueMap.put("volume", getFromNormalNumber(String.valueOf((normalQuantity / (normalDensityInf))),
						elementValueMap.get("VOLUOM_ID"), sbInfo));
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on volume =" + elementValueMap.get("volume"),
					ActivitylogType.Calculation, formId);
		}
		
		
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Mole Calculation =></br>");
			if (normalQuantity == null || normalpurityInf == null || normalmwInf == null) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole. Quantity =" + elementValueMap.get("quantity")
								+ "; MW = " + normalmwInf + ";purity = " + normalpurityInf,
						ActivitylogType.Calculation, formId);
			} else if (normalmwInf == 0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole = " + normalmwInf + "(mw)*" + normalpurityInf
								+ "(purity). The dividing parameter[purity*mw] in the mole calculation is 0",
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: Quantity*Purity/100/MW.</br> Normal Args:" + String.valueOf(normalQuantity)
						+ "*(" + normalpurityInf.toString() + "/100)/" + normalmwInf.toString() + "</br>");
				elementValueMap.put("mole",
						getFromNormalNumber(String.valueOf((normalQuantity * (normalpurityInf / 100) / (normalmwInf))),
								elementValueMap.get("MOLEUOM_ID"), sbInfo));
			}

		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on mole =" + elementValueMap.get("mole"),
					ActivitylogType.Calculation, formId);
		}
		

		sb = createJsonMaterialCalcSB(elementValueMap);
		
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventQntyByReactantQntyCalc - elementValueMap after calulation= mole:"
						+ elementValueMap.get("mole") + ", quantity:" + elementValueMap.get("quantity")
						+ ", volume:" + elementValueMap.get("volume"),
				ActivitylogType.Calculation, formId);
		return sb;
	}
	
	/**
	 * calc material Solvent's values on click Ratio calc button
	 * 
	 * @param elementValueMap
	 * @param mainArgVal
	 * @return
	 */
	private StringBuilder materialSolventMoleByReactantMoleCalc(Map<String, String> elementValueMap, String mainArgVal,String formId) {
		String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID, elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
		String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID, elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
		String defaultPurity = generalUtil.getEmpty(formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITY"),"100");
		String defaultPurityUomId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, elementValueMap.get("BATCH_ID"), "PURITYUOM_ID");
				
		Double normalmwInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
				generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
		Double normalDensityInf = getNormalNumber(elementValueMap.get("densityInf"), elementValueMap.get("DENSITYUOM_ID_INF"));
		Double normalpurityInf = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("purityInf"), defaultPurity),
				generalUtil.getEmpty(elementValueMap.get("PURITYUOM_ID_INF"), defaultPurityUomId), (double) 100);// Default value 1 when batch is not selected
		Double normalReactantMole = getNormalNumber(elementValueMap.get("reactantMole"), elementValueMap.get("reactantMoleUOM"));
		Double ratioVal = getNormalNumber(elementValueMap.get("ratio"), null);
		StringBuilder sb = new StringBuilder();
		
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventMoleByReactantMoleCalc - elementValueMap before calulation= mole:"
						+ elementValueMap.get("mole") + ", quantity:" + elementValueMap.get("quantity")
						+ ", volume:" + elementValueMap.get("volume"),
				ActivitylogType.Calculation, formId);
		
		try {
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Mole Calculation By Reactant Moles =></br>");
			
			if (normalReactantMole == null || ratioVal.equals("0")) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on mole. Reactant Mole = " + elementValueMap.get("reactantMole")
						+ "; Ratio = " + elementValueMap.get("ratio"),
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: Ratio * Reactant Mole.</br> Args:" + String.valueOf(ratioVal)
						+ "*" + elementValueMap.get("reactantMole")+ "</br>");
				elementValueMap.put("mole", String.valueOf(ratioVal * Double.valueOf(elementValueMap.get("reactantMole"))));
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on mole =" + elementValueMap.get("mole"),
					ActivitylogType.Calculation, formId);
		}
		
		try {
			Double normalMole = getNormalNumber(elementValueMap.get("mole"), elementValueMap.get("MOLEUOM_ID"));
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Solvent Quantity Calculation =></br>");
			if (normalmwInf == null || normalMole == null) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on quantity. MW =" + elementValueMap.get("mwInf")
								+ "; Mole = " + elementValueMap.get("mole"),
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: MW * Mole.</br> Normal Args:" + String.valueOf(normalmwInf) + "*"
						+ normalMole.toString() + "</br>");
				elementValueMap.put("quantity", getFromNormalNumber(String.valueOf(normalmwInf * normalMole),
						elementValueMap.get("QUANTITYUOM_ID"), sbInfo));
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on quantity =" + elementValueMap.get("quantity"),
					ActivitylogType.Calculation, formId);
		}
		
		
		try {
			StringBuilder sbInfo = new StringBuilder();
			Double normalQuantity = getNormalNumber(elementValueMap.get("quantity"), elementValueMap.get("QUANTITYUOM_ID"));
			sbInfo.append("Solvent Volume Calculation =></br>");
			if (normalQuantity == null || normalDensityInf == null) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume. Quantity" + elementValueMap.get("quantity")
								+ "; Density = " + elementValueMap.get("densityInf") + ";purity = " + normalpurityInf,
						ActivitylogType.Calculation, formId);
				if(normalDensityInf == null)
				{
					elementValueMap.put("warningMsg", "The following value is missing \"Solvent - Density\". Calculation cannot be completed.");
				}
			} else if (normalDensityInf == 0) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on volume. "
								+ normalQuantity + "(quantity)/" + normalDensityInf + "(density)."
								+" The dividing parameter[density] in the volume calculation is 0. "
								+ elementValueMap.get("volume"),
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: Quantity/Density.</br> Normal Args:" + String.valueOf(normalQuantity) + "/"
						+ normalDensityInf.toString() + "</br>");
				elementValueMap.put("volume", getFromNormalNumber(String.valueOf((normalQuantity / (normalDensityInf))),
						elementValueMap.get("VOLUOM_ID"), sbInfo));
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on volume =" + elementValueMap.get("volume"),
					ActivitylogType.Calculation, formId);
		}
		
		sb = createJsonMaterialCalcSB(elementValueMap);
		
		generalUtilLogger.logWriter(
				LevelType.DEBUG, "materialSolventMoleByReactantMoleCalc - elementValueMap after calulation= mole:"
						+ elementValueMap.get("mole") + ",quantity:" + elementValueMap.get("quantity")
						+ ", volume:" + elementValueMap.get("volume"),
				ActivitylogType.Calculation, formId);
		return sb;
	}
	

	/**
	 * calc material Product
	 * 
	 * @param elementValueMap
	 * @param elementValueJson 
	 * @param mainArgVal
	 * @return
	 */
	private StringBuilder materialProductCalc(Map<String, String> elementValueMap, String mainArgCode, String formId, JSONObject elementValueJson) {
		JSONObject json = new JSONObject();
		if (elementValueMap.get("isLimited").equals("1")
				&& generalUtil.getEmpty(elementValueMap.get("limitingAgent"), "0").equals("0")) {
			calcLimitingAgentProduct(mainArgCode, elementValueMap, formId);
		}
		
		calcProductConcentration(elementValueMap,formId,elementValueJson,mainArgCode);
		calcMassRateForPP(elementValueMap,formId,elementValueJson);
		if(mainArgCode.equals("concInReactionMass")){
			calcProductYield(elementValueMap,formId,elementValueJson);
		}
		
		json.put("mass", elementValueMap.get("mass"));
		json.put("yield", elementValueMap.get("yield"));
		json.put("concInReactionMass", elementValueMap.get("concInReactionMass"));
		json.put("concentrationMole", elementValueMap.get("concentrationMole"));
		json.put("warningMsg", generalUtil.getNull(elementValueMap.get("warningMsg"),""));
		json.put("massrateforpp", elementValueMap.get("massrateforpp"));
		
		return new StringBuilder(json.toString());
	}

	/**
	 * create sb for json materials calc
	 * 
	 * @param elementValueMap
	 * @return
	 */
	private StringBuilder createJsonMaterialCalcSB(Map<String, String> elementValueMap) {
		JSONObject json = new JSONObject();
		if (elementValueMap.get("isLimited").equals("1")
				&& generalUtil.getEmpty(elementValueMap.get("limitingAgent"), "0").equals("0")
				&& (elementValueMap.get("tableType").equals("Reactant")
						|| elementValueMap.get("tableType").equals("Solvent"))) {
			json.put("mole", elementValueMap.get("mole"));
			json.put("quantity", elementValueMap.get("quantity"));
			json.put("volume", elementValueMap.get("volume"));
			json.put("moleRate", elementValueMap.get("moleRate"));
			json.put("quantityRate", elementValueMap.get("quantityRate"));
			json.put("volumeRate", elementValueMap.get("volumeRate"));
			json.put("equivalent", elementValueMap.get("equivalent"));
			json.put("ratio", elementValueMap.get("ratio"));
		} else if ((elementValueMap.get("isLimited").equals("0") || elementValueMap.get("limitingAgent").equals("1"))) {
			json.put("mole", elementValueMap.get("mole"));
			json.put("quantity", elementValueMap.get("quantity"));
			json.put("volume", elementValueMap.get("volume"));
			json.put("moleRate", elementValueMap.get("moleRate"));
			json.put("quantityRate", elementValueMap.get("quantityRate"));
			json.put("volumeRate", elementValueMap.get("volumeRate"));
			json.put("equivalent", elementValueMap.get("equivalent"));
			json.put("ratio", elementValueMap.get("ratio"));
		}
		json.put("warningMsg", generalUtil.getNull(elementValueMap.get("warningMsg"),""));
		return new StringBuilder(json.toString());
	}
	
	@Override
	public String doCalcComposition(String api, String eventAction, String mainArgCode, String mainArgVal, String mainArgLastVal,
			Map<String, String> originElementValueMap, String[] apiCodesArray, String[] elementsMatchArray,
			String formCode, String formId, String userId, JSONObject elementValueJson) {
		JSONObject computedArgsJson = new JSONObject();//ww_grk,wv_grl,ww_p
		String parentFormCode = formDao.getFormCodeBySeqId(originElementValueMap.get("parentId"));
		String compositionType = "";
		String plannedBatchSize = originElementValueMap.get("plannedBatchSize");
		String plannedBatchSizeUOM = originElementValueMap.get("plannedBatchSizeUOM");
		String batchSizeUomId = originElementValueMap.get("BATCHSIZE_UOM");
		String experimentId ="";
		if(parentFormCode.equals("StepFr")){
			experimentId = formDao.getFromInfoLookup("Step", LookupType.ID, originElementValueMap.get("parentId"), "experiment_id");
			//String formulationType = formDao.getFromInfoLookup("Experiment", LookupType.ID, experimentId, "FORMULATIONTYPE_ID");
			compositionType = originElementValueMap.get("COMPOSITIONTYPENAME");//formDao.getFromInfoLookup("FORMULATIONTYPE", LookupType.ID, formulationType, "COMPOSITIONTYPE") ;
			batchSizeUomId = originElementValueMap.get("BATCHSIZEMATERIALUOM_ID");
		} else {
			if(parentFormCode.equals("ExperimentFor")){
				experimentId = originElementValueMap.get("parentId");
			}
			compositionType = formDao.getFromInfoLookup("FORMULATIONTYPE", LookupType.ID, originElementValueMap.get("FORMULATIONTYPE_ID"), "COMPOSITIONTYPE") ;
		}
		JSONObject toReturn = new JSONObject();
		if(compositionType.isEmpty()){
			return toReturn.toString();
		}
		
		//before starting to calculate and update the values, make a validation to ensure that there's no an exception or wrong data
		JSONObject validation =validateData(formId,eventAction,mainArgCode,mainArgVal,compositionType,originElementValueMap,experimentId);
		if(validation.length()!=0){
			if(eventAction.equals("calcDensityOnChange")){
				return createReturnJsonArray(formId, new StringBuilder(validation.toString())).toString();
			}
			
			if(!eventAction.equals("checkBalance")){
				//returns back the last value
				updateEditableForm(formCode,formId,mainArgCode,mainArgLastVal,originElementValueMap.get("parentId"),userId,"");//returns the last value
				computedArgsJson.put(mainArgCode, mainArgLastVal);
				mergeActualValuesAndElementValueJson(toReturn,createReturnJsonArray(formId,new StringBuilder(computedArgsJson.toString())));
			}
			mergeActualValuesAndElementValueJson(toReturn,createReturnJsonArray(formId,new StringBuilder(validation.toString())));
			return toReturn.toString();
		}
		
		/************************************************/
		/*****************Check Balance******************/
		/************************************************/
		if(eventAction.equals("checkBalance") && originElementValueMap.get("doCheckBalance").equals("1")){
			//when deleting rows or copy materials from the planned compositions to the step or creating a new step(and the products are copied to the materials), then first just check that the operation is valid.
			//After actually making the operation(deleting rows or copy planned..), should execute the check balance
			//calculate the Actual weight
			String sql = "select *\n"
					+ " from fg_s_composition_v\n"
					+ " where parentid = '"+experimentId+"'\n"
					+ " and active = 1\n"
					+ " and tableType = 'expComposition'"
					+ generalUtilFormState.getWherePartForTmpData("Composition", experimentId);
			List<Map<String, Object>> compositionList = generalDao.getListOfMapsBySql(sql);
			for (Map<String, Object> compositionData : compositionList){
				String compositionId = compositionData.get("FORMID").toString();
				String parentId = compositionData.get("PARENTID").toString();
				//String sessionId = compositionData.get("SESSIONID").toString();
				String materialId = compositionData.get("INVITEMMATERIAL_ID").toString();
				String materialFormCode= formDao.getFormCodeBySeqId(materialId);//material/recipeFormulation
				String materialName = formDao.getFromInfoLookup(materialFormCode, LookupType.ID, materialId, "name");
				sql = "select distinct sum(actual)\n"
					+ " from fg_s_composition_v\n"
					+ " where parentId in (select step_id from fg_s_step_v where experiment_id = '"+experimentId+"')\n"
					+ " and sessionid is null"
					+ " and active = 1"
					+ " and tableType in ('stepComposition','productComposition')"
					+ " and invitemmaterial_id = '"+materialId+"'";
				String sumActual = generalUtil.getNull(generalDao.selectSingleStringNoException(sql),"0");
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Actual calculation of "+materialFormCode+" '"+materialName+"'=></br>");
				sbInfo.append("Formula:sum("+materialFormCode+" '"+materialName+"' Actual).</br> Args: "+sumActual);
				String onChangeColumnName = "actual";
				String onChangeColumnVal = sumActual;
				computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
				updateEditableForm("composition",compositionId,onChangeColumnName,onChangeColumnVal,parentId,userId,"");
				mergeActualValuesAndElementValueJson(toReturn,createReturnJsonArray(compositionId, new StringBuilder(computedArgsJson.toString())));
				computedArgsJson = new JSONObject();
			}
			//calculate the weight difference
			Set<String> invalidDifferenceProductList = new LinkedHashSet<>();//data structure with no duplicate elements
			sql = "select t.*, sum(t.WW_GRK)over(partition by t.invitemmaterial_id) as SUM_WW_GRK\n"
					+ " from fg_s_composition_v t\n"
					+ " where parentid in (select step_id from fg_s_step_v where experiment_id = '"+experimentId+"')\n"
					+ " and active = 1\n"
					+ " and tableType = 'productComposition'"
					+ " and sessionid is null";
			compositionList = generalDao.getListOfMapsBySql(sql);//first, get the sum of the specific product
			for (Map<String, Object> compositionData : compositionList){
				String compositionId = compositionData.get("FORMID").toString();
				String parentId = compositionData.get("PARENTID").toString();
				String materialId = compositionData.get("INVITEMMATERIAL_ID").toString();
				String SUM_WW_GRK = compositionData.get("SUM_WW_GRK")!=null?compositionData.get("SUM_WW_GRK").toString():"0";
				String materialName = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, materialId, "name");
				//get the sum of the product that exist  in the materials table, in order to compare it with the product sum that calculated above
				sql = "select distinct sum(WW_GRK)\n"
					+ " from fg_s_composition_v\n"
					+ " where parentId in (select step_id from fg_s_step_v where experiment_id = '"+experimentId+"')\n"
					+ " and sessionid is null"
					+ " and active = 1"
					+ " and tableType = 'stepComposition'"
					+ " and invitemmaterial_id = '"+materialId+"'";
				String sumWW_GRK = generalUtil.getNull(generalDao.selectSingleStringNoException(sql),"0");
				StringBuilder sbInfo = new StringBuilder();
				double difference = (Double.parseDouble(SUM_WW_GRK)- Double.parseDouble(sumWW_GRK));
				sbInfo.append("Difference calculation of product '"+materialName+"'=></br>");
				sbInfo.append("Formula:sum(Product '"+materialName+"' w/w[gr/kg])-sum(Material '"+materialName+"' w/w[gr/kg]).</br> Args: "+SUM_WW_GRK+"-"+sumWW_GRK);
				if(difference<0){//update the difference only if it is not negative.
					invalidDifferenceProductList.add(materialName);
				} else {
					String onChangeColumnName = "difference";
					String onChangeColumnVal = String.valueOf(difference);
					computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
					updateEditableForm("composition",compositionId,onChangeColumnName,onChangeColumnVal,parentId,userId,"");
					mergeActualValuesAndElementValueJson(toReturn,createReturnJsonArray(compositionId, new StringBuilder(computedArgsJson.toString())));
				}
				computedArgsJson = new JSONObject();
			}
			JSONObject jsonValidation = new JSONObject();
			if(!invalidDifferenceProductList.isEmpty()){
				jsonValidation.put("warningMsg", "The sum of the used Qty of "+(invalidDifferenceProductList.size()>1?"Materials: ":"Material ")+invalidDifferenceProductList.toString().replaceAll("\\[|\\]", "")+" is higher than its step-product Qty");
				mergeActualValuesAndElementValueJson(toReturn,createReturnJsonArray(formId, new StringBuilder(jsonValidation.toString())));
			}
			String lastProductId = generalDao.selectSingleStringNoException("select distinct first_value(formid) over (partition by parentid,tabletype order by formid desc)\n"
					+ "from fg_S_composition_v\n"
					+ "where parentid = '"+originElementValueMap.get("parentId")+"'\n"
					+ "and active = 1\n"
					+ "and tabletype = 'productComposition'"
					+ generalUtilFormState.getWherePartForTmpData("composition", originElementValueMap.get("parentId")));
			if(!generalUtil.getNull(lastProductId).isEmpty()){
				JSONObject productCalcRet = new JSONObject(doCalcComposition(api, "calcWWpProduct", "WW_P", "0", "0", originElementValueMap, apiCodesArray, elementsMatchArray, "composition", lastProductId, userId, elementValueJson));
				Iterator<String> keysItr = productCalcRet.keys();
				while (keysItr.hasNext()) {
					String key = keysItr.next();
					JSONObject rowData = productCalcRet.getJSONObject(key);
					if(rowData.has("warningMsg")){
						return productCalcRet.toString();
					}
				}
				mergeActualValuesAndElementValueJson(toReturn,productCalcRet);
			}
			//return createReturnJsonArray(formId, new StringBuilder(toReturn.toString())).toString();	
		}
		
		if(eventAction.equals("calcFillerOnRemovedRow")){
			toReturn = calcFillerRow(formId,formCode,originElementValueMap.get("parentId"),"WW_P",originElementValueMap,userId,eventAction,batchSizeUomId,compositionType);
		}
		else if(eventAction.equals("calcActualOnChange")){
			//calculate the Actual weight
			String sql = "select invitemmaterial_id\n"
					+ " from fg_s_composition_v\n"
					+ " where formid = '"+formId+"'"
					+ " and sessionid is null"
					+ " and active =1\n";
			String material_id = generalDao.selectSingleStringNoException(sql);
			String materialFormCode= formDao.getFormCodeBySeqId(material_id);//material/recipeFormulation
			String materialName = formDao.getFromInfoLookup(materialFormCode, LookupType.ID, material_id, "name");	
			//summarize all the actual values of the current material in the steps
			sql = "select distinct sum(actual)\n"
					+ " from fg_s_composition_v\n"
					+ " where parentId in (select step_id from fg_s_step_v where experiment_id = '"+experimentId+"')\n"
					+ " and sessionid is null"
					+ " and active = 1"
					+ " and tableType in ('stepComposition','productComposition')"
					+ " and invitemmaterial_id = '"+material_id+"'";
			String sumActual = generalUtil.getNull(generalDao.selectSingleStringNoException(sql),"0");
			sql = "select distinct composition_id\n"
					+ " from fg_s_composition_v\n"
					+ " where parentid = '"+experimentId+"'\n"
					+ " and invitemmaterial_id = '"+material_id+"'\n"
					+ " and active = 1\n"
					+ " and tableType = 'expComposition'"
					+ generalUtilFormState.getWherePartForTmpData("Composition", experimentId);
			List<String> compositionList = generalDao.getListOfStringBySql(sql);
			for (String compositionId: compositionList){
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Actual calculation of "+materialFormCode+" '"+materialName+"'=></br>");
				sbInfo.append("Formula:sum("+materialFormCode+" '"+materialName+"' Actual).</br> Args: "+sumActual);
				String onChangeColumnName = "actual";
				String onChangeColumnVal = sumActual;
				updateEditableForm("composition",compositionId,onChangeColumnName,onChangeColumnVal,experimentId,userId,"");
				computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
				mergeActualValuesAndElementValueJson(toReturn,createReturnJsonArray(compositionId, new StringBuilder(computedArgsJson.toString())));
				computedArgsJson = new JSONObject();
			}
		}
		else if(eventAction.equals("calcWWpProduct")){
			String sql = "select distinct sum(ww_p)\n"
					+ "from fg_s_composition_v\n"
					+ "where parentid = '"+originElementValueMap.get("parentId")+"'\n"
					+ "and tabletype = 'stepComposition'\n"
					+ "and active = 1\n"
					+ generalUtilFormState.getWherePartForTmpData("composition", originElementValueMap.get("parentId"));
			String sumWWpMaterials = generalUtil.getNull(generalDao.selectSingleStringNoException(sql),"0");
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Product W/W % calculation=></br>");
			sbInfo.append("Formula: sum of material w/w% of the current step.</br> Args: "+sumWWpMaterials);
			
			generalUtilLogger.logWriter(LevelType.INFO,
					sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
			
			String onChangeColumnName = "WW_P";
			String onChangeColumnVal = sumWWpMaterials;
			updateEditableForm(formCode,formId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,"ww_p");
			computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
			originElementValueMap.put("tableType", "productComposition");
			toReturn = new JSONObject(doCalcComposition(api, "calcWwPOnChange", "WW_P", onChangeColumnVal, "0", originElementValueMap, apiCodesArray, elementsMatchArray, formCode, formId, userId, elementValueJson));
			Iterator<String> keysItr = toReturn.keys();
			while (keysItr.hasNext()) {
				String key = keysItr.next();
				JSONObject rowData = toReturn.getJSONObject(key);
				if(rowData.has("warningMsg")){
					return toReturn.toString();
				}
			}
			mergeActualValuesAndElementValueJson(toReturn,createReturnJsonArray(formId, new StringBuilder(computedArgsJson.toString())));
		}
		else if(eventAction.equals("calcPurityOnChange")||eventAction.equals("getPurityAndCalcComposition")){
		/****************************************************/
		/****************MainArg->Purity******************/
		/****************************************************/
			double ww_p =0;
			if(eventAction.equals("getPurityAndCalcComposition")){
				mainArgCode = "Purity";
				mainArgVal = mainArgVal.equals("0")?"100":generalDao.selectSingleStringNoException("select nvl(purity,100)\n"
						+ "from fg_S_invitembatch_v\n"
						+ "where formid = '"+mainArgVal+"'");
			}
			try{
				/****************** W/W% CALC *****************/
				StringBuilder sbInfo = new StringBuilder();
				String current_ww_p = generalDao.selectSingleStringNoException("select nvl(ww_p,0)\n"
						+ "from fg_s_composition_v\n"
						+ "where formid = '"+formId+"'\n"
						+ "and active = 1\n"
						+ generalUtilFormState.getWherePartForTmpData("composition", experimentId));
				sbInfo.append("W/W % calculation=></br>");
				sbInfo.append("Formula:w/w% / (purity/100).</br> Args: "+current_ww_p+"/("+mainArgVal+"/100"+")");
				
				generalUtilLogger.logWriter(LevelType.INFO,
						sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
				ww_p = Double.parseDouble(current_ww_p)/(Double.parseDouble(mainArgVal)/100);//ww_p/(purity/100)
				
				String onChangeColumnName = "WW_P";
				String onChangeColumnVal = String.valueOf(ww_p);
				updateEditableForm(formCode,formId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,"ww_p");
				computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
			} catch(Exception e){
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on w/w%",
						ActivitylogType.Calculation, formId);
			}
			//add calculations of ww_grk&wv_grl
			try{
				/************** W/W [gr/kg] CALC *******************/
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("W/W [gr/kg] calculation=></br>");
				sbInfo.append("Formula:w/w% * 10.</br> Args: "+computedArgsJson.getString("WW_P")+"*10");
				generalUtilLogger.logWriter(LevelType.INFO,
						sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
				double ww_grk = Double.parseDouble(computedArgsJson.getString("WW_P"))*10;
				
				String onChangeColumnName = "WW_GRK";
				String onChangeColumnVal = String.valueOf(ww_grk);
				updateEditableForm(formCode,formId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,"WW_P");
				computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
			} catch(Exception e){
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on w/w[gr/kg]",
						ActivitylogType.Calculation, formId);
			}
		
			if(compositionType.equalsIgnoreCase("liquid")){
				try{
					/************** W/V[gr/L] CALC ************/
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("W/V[gr/L] calculation=></br>");
					sbInfo.append("Formula:w/w% * density[gr/ml]*10.</br> Args: "+computedArgsJson.getString("WW_P")+"*"+originElementValueMap.get("density")+"*10");
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
					
					if(originElementValueMap.get("density").isEmpty()){
						generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on W/V[gr/L] since the density is empty.",
						ActivitylogType.Calculation, formId);
						computedArgsJson.put("warningMsg", "The following value is missing: Density. Calculation cannot be completed.");
					} else {
						double wv_grl = Double.parseDouble(computedArgsJson.getString("WW_P"))*Double.parseDouble(originElementValueMap.get("density"))*10;						
						String onChangeColumnName = "WV_GRL";
						String onChangeColumnVal = String.valueOf(wv_grl);
						updateEditableForm(formCode,formId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,"WW_P");
						computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
					}
				} catch(Exception e){
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on w/v[gr/L]",
							ActivitylogType.Calculation, formId);
				}
			}
			if(!originElementValueMap.containsKey("docalcFiller")
					||originElementValueMap.get("docalcFiller").equals("true")){
				toReturn = calcFillerRow(formId,formCode,originElementValueMap.get("parentId"),"WW_P",originElementValueMap,userId,eventAction,batchSizeUomId,compositionType);
			}
		}
		//if(eventAction.equals("calcWvOnChange")||eventAction.equals("calcWwGrkOnChange")
			//	||eventAction.equals("calcWwPOnChange")||eventAction.equals("calcFillerOnChange"))
		else if(eventAction.equals("calcWvOnChange")){
		/****************************************************/
		/****************MainArg->W/V[gr/l]******************/
		/****************************************************/
			try{
				/****************** W/W% CALC *****************/
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("W/W % calculation=></br>");
				sbInfo.append("Formula:w/v[gr/L]/(density[gr/ml]*10).</br> Args: "+mainArgVal+"/("+originElementValueMap.get("density")+"*10"+")");
				if(originElementValueMap.get("density").isEmpty()){
					generalUtilLogger.logWriter(LevelType.WARN,
							"w/w% calculation was not performed. Missing args -> density",
							ActivitylogType.Calculation, formId);
					computedArgsJson.put("warningMsg", "The following value is missing: Density. Calculation cannot be completed.");
				} else if(Double.parseDouble(originElementValueMap.get("density")) == (double)0){
					generalUtilLogger.logWriter(LevelType.WARN,
							"w/w% calculation was not performed since density =0",
							ActivitylogType.Calculation, formId);
					computedArgsJson.put("warningMsg", "Cannot complete the calculation when "+mainArgCode +" = "+mainArgVal+" since the density id 0");
				}
				else {
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
					double ww_p = Double.parseDouble(mainArgVal)/(Double.parseDouble(originElementValueMap.get("density"))*10);
					
					String onChangeColumnName = "WW_P";
					String onChangeColumnVal = String.valueOf(ww_p);
					updateEditableForm(formCode,formId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,"WV_GRL");
					computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
				}
			} catch(Exception e){
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on w/w%",
						ActivitylogType.Calculation, formId);
			}
			try{
				/*************** W/W[gr/kg] CALC **************/
				StringBuilder sbInfo = new StringBuilder();
				if(!computedArgsJson.has("WW_P")||computedArgsJson.getString("WW_P").isEmpty()){
					generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on W/W[gr/kg]",
						ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("W/W[gr/kg] calculation=></br>");
					sbInfo.append("Formula:w/w%*10).</br> Args: "+computedArgsJson.getString("WW_P")+"*10");
		
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
					double ww_grk = Double.parseDouble(computedArgsJson.getString("WW_P"))*10;
					
					String onChangeColumnName = "WW_GRK";
					String onChangeColumnVal = String.valueOf(ww_grk);
					updateEditableForm(formCode,formId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,"WV_GRL");
					computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
				}
			} catch(Exception e){
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on w/w[gr/kg]",
						ActivitylogType.Calculation, formId);
			}
			if(!originElementValueMap.containsKey("docalcFiller")
					||originElementValueMap.get("docalcFiller").equals("true")){
				toReturn = calcFillerRow(formId,formCode,originElementValueMap.get("parentId"),"WW_P",originElementValueMap,userId,eventAction,batchSizeUomId,compositionType);
			}
		}
		else if(eventAction.equals("calcWwGrkOnChange")){
			/*****************************************************/
			/****************MainArg->W/W[gr/kg]******************/
			/*****************************************************/
			try{
				/************** W/W% CALC *******************/
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("W/W % calculation=></br>");
				sbInfo.append("Formula:w/w[gr/kg]/10.</br> Args: "+mainArgVal+"/10");
				generalUtilLogger.logWriter(LevelType.INFO,
						sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
				double ww_p = Double.parseDouble(mainArgVal)/10;
				
				String onChangeColumnName = "WW_P";
				String onChangeColumnVal = String.valueOf(ww_p);
				updateEditableForm(formCode,formId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,"WW_GRK");
				computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
			} catch(Exception e){
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on w/w%",
						ActivitylogType.Calculation, formId);
			}
			if(compositionType.equalsIgnoreCase("liquid")){
				try{
					/************** W/V[gr/L] CALC ************/
					if(!computedArgsJson.has("WW_P")||computedArgsJson.getString("WW_P").isEmpty()){
						generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on W/V[gr/L]",
							ActivitylogType.Calculation, formId);
					} else {
						StringBuilder sbInfo = new StringBuilder();
						sbInfo.append("W/V[gr/L] calculation=></br>");
						sbInfo.append("Formula:w/w% * density[gr/ml]*10.</br> Args: "+computedArgsJson.getString("WW_P")+"*"+originElementValueMap.get("density")+"*10");
						generalUtilLogger.logWriter(LevelType.INFO,
								sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
						
						if(originElementValueMap.get("density").isEmpty()){
							generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on W/V[gr/L] since the density is empty.",
							ActivitylogType.Calculation, formId);
							computedArgsJson.put("warningMsg", "The following value is missing: Density. Calculation cannot be completed.");
						} else {
							double wv_grl = Double.parseDouble(computedArgsJson.getString("WW_P"))*Double.parseDouble(originElementValueMap.get("density"))*10;
							
							String onChangeColumnName = "WV_GRL";
							String onChangeColumnVal = String.valueOf(wv_grl);
							updateEditableForm(formCode,formId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,"WW_GRK");
							computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
						}
					}
				} catch(Exception e){
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on w/v[gr/L]",
							ActivitylogType.Calculation, formId);
				}
			}
			if(!originElementValueMap.containsKey("docalcFiller")
					||originElementValueMap.get("docalcFiller").equals("true")){
				toReturn = calcFillerRow(formId,formCode,originElementValueMap.get("parentId"),"WW_P",originElementValueMap,userId,eventAction,batchSizeUomId,compositionType);
			}
		} 
		else if(eventAction.equals("calcWwPOnChange")){
			/***********************************************/
			/****************MainArg->W/W%******************/
			/***********************************************/
			try{
				/************** W/W [gr/kg] CALC *******************/
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("W/W [gr/kg] calculation=></br>");
				sbInfo.append("Formula:w/w% * 10.</br> Args: "+mainArgVal+"*10");
				generalUtilLogger.logWriter(LevelType.INFO,
						sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
				double ww_grk = Double.parseDouble(mainArgVal)*10;
				
				String onChangeColumnName = "WW_GRK";
				String onChangeColumnVal = String.valueOf(ww_grk);
				updateEditableForm(formCode,formId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,"WW_P");
				computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
			} catch(Exception e){
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on w/w[gr/kg]",
						ActivitylogType.Calculation, formId);
			}
		
			if(compositionType.equalsIgnoreCase("liquid")){
				try{
					/************** W/V[gr/L] CALC ************/
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("W/V[gr/L] calculation=></br>");
					sbInfo.append("Formula:w/w% * density[gr/ml]*10.</br> Args: "+mainArgVal+"*"+originElementValueMap.get("density")+"*10");
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
					
					if(originElementValueMap.get("density").isEmpty()){
						generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on W/V[gr/L] since the density is empty.",
						ActivitylogType.Calculation, formId);
						computedArgsJson.put("warningMsg", "The following value is missing: Density. Calculation cannot be completed.");
					} else {
						double wv_grl = Double.parseDouble(mainArgVal)*Double.parseDouble(originElementValueMap.get("density"))*10;						
						String onChangeColumnName = "WV_GRL";
						String onChangeColumnVal = String.valueOf(wv_grl);
						updateEditableForm(formCode,formId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,"WW_P");
						computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
					}
				} catch(Exception e){
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on w/v[gr/L]",
							ActivitylogType.Calculation, formId);
				}
			}
			if(!originElementValueMap.containsKey("docalcFiller")
					||originElementValueMap.get("docalcFiller").equals("true")){
				toReturn = calcFillerRow(formId,formCode,originElementValueMap.get("parentId"),"WW_P",originElementValueMap,userId,eventAction,batchSizeUomId,compositionType);
			}
		} else if(eventAction.equals("calcFillerOnChange")){
			toReturn = calcFillerRow(formId,formCode,originElementValueMap.get("parentId"),"WW_P",originElementValueMap,userId,eventAction,batchSizeUomId,compositionType);
		} else if(eventAction.equals("calcDensityOnChange")){
			/*if(!compositionType.equalsIgnoreCase("liquid")){//the density affects the w/v[gr/L] when the composition type is liquid only
				return null;
			}*/
			String sql = "select distinct * from fg_s_composition_v"
					+ " where parentId = '"+originElementValueMap.get("parentId")+"'\n"
					+ " and nvl(active,'1') = '1'\n"
					+ " and nvl(filler,'0') = '0'\n"
					//+ " and mainArg in ('WV_GRL','WW_P')\n"
					+ " and tableType = '"+originElementValueMap.get("tableType")+"'"
					+ " and '"+compositionType.toLowerCase()+"' = 'liquid'" 
					+ generalUtilFormState.getWherePartForTmpData("Composition", originElementValueMap.get("parentId"));
			if(parentFormCode.equals("ExperimentFor")){
				String compositionTypeName = originElementValueMap.get("COMPOSITIONTYPENAME");
				sql +=" union all\n"
					+ "select distinct * from fg_s_composition_v"
					+ " where parentId in (select step_id from fg_s_step_v where experiment_id = '"+originElementValueMap.get("parentId")+"' and '"+compositionTypeName.toLowerCase()+"' = 'liquid')\n"
					+ " and nvl(active,'1') = '1'\n"
					+ " and nvl(filler,'0') = '0'\n"
					//+ " and mainArg in ('WV_GRL','WW_P')\n"
					+ " and tableType in ('stepComposition','productComposition')"
					+ " and sessionid is null";
			}
			List<Map<String, Object>> compositionList = generalDao.getListOfMapsBySql(sql);
			originElementValueMap.put("docalcFiller", "false");
			String originParentId = originElementValueMap.get("parentId");
			for (Map<String, Object> compositionData : compositionList){
				String compositionId = compositionData.get("FORMID").toString();
				String mainArg = compositionData.get("MAINARG") == null?"":compositionData.get("MAINARG").toString();
				String mainArgValue = mainArg.isEmpty()?"0":compositionData.get(mainArg).toString();
				boolean isDescendantStepComp = !compositionData.get("PARENTID").toString().equals(originElementValueMap.get("parentId"));//check if the batch data was changed in the experiment and the current iteration composition is the step's
				String parentId = compositionData.get("PARENTID").toString();
					String rowEventAction = "";
					if(mainArg.equals("WV_GRL")){
						rowEventAction = "calcWvOnChange";
					} else if(mainArg.equals("WW_P")){
						rowEventAction = "calcWwPOnChange";
					} else if(mainArg.equals("WW_GRK")){
						rowEventAction = "calcWwGrkOnChange";
					}
					originElementValueMap.put("parentId", parentId);//change it in order that the updating calc values would be inserted to the right session
					String retComposCalcJson = doCalcComposition(api, rowEventAction, mainArg, mainArgValue, mainArgLastVal, originElementValueMap,
							apiCodesArray, elementsMatchArray, formCode, compositionId, userId, elementValueJson);
					mergeActualValuesAndElementValueJson(toReturn,new JSONObject(retComposCalcJson));
			}
			originElementValueMap.put("parentId", originParentId);//returns back the original parentId in case it was replaced in the loop
			//calculating the filler
			mergeActualValuesAndElementValueJson(toReturn , calcFillerRow(formId,formCode,originElementValueMap.get("parentId"),"WW_P",originElementValueMap,userId,eventAction,batchSizeUomId,compositionType));
		} else if(eventAction.equals("calcBatchSizeOnChange")){
			if(mainArgVal.isEmpty()){
				return null;
			}
			String sql = "select distinct * from fg_s_composition_v"
					+ " where parentId = '"+originElementValueMap.get("parentId")+"'\n"
					+ " and nvl(active,'1') = '1'\n"
					+ " and nvl(WV_GRL,WW_GRK) is not null\n"
					+ " and tableType = '"+originElementValueMap.get("tableType")+"'"
					+ generalUtilFormState.getWherePartForTmpData("Composition", originElementValueMap.get("parentId"));
			if(parentFormCode.equals("ExperimentFor")){
				//get all the materials of all the descendant steps
				sql += "union all\n"
						+ " select distinct * from fg_s_composition_v"
						+ " where parentId in (select step_id from fg_s_step_v where experiment_id = '"+originElementValueMap.get("parentId")+"')\n"
						+ " and nvl(active,'1') = '1'\n"
						+ " and nvl(WV_GRL,WW_GRK) is not null\n"
						+ " and tableType = 'stepComposition'"
						+ " and sessionid is null";
			}
			List<Map<String, Object>> compositionList = generalDao.getListOfMapsBySql(sql);
			originElementValueMap.put("docalcFiller", "false");
			String batchSizeUomName = formDao.getFromInfoLookup("Uom", LookupType.ID, batchSizeUomId, "name");
			
			for (Map<String, Object> compositionData : compositionList){
				String compositionId = compositionData.get("FORMID").toString();
				String WV_GRL = compositionData.get("WV_GRL") == null?"":compositionData.get("WV_GRL").toString();
				String WW_GRK = compositionData.get("WW_GRK") == null?"":compositionData.get("WW_GRK").toString();
				boolean isDescendantStepComp = !compositionData.get("PARENTID").toString().equals(originElementValueMap.get("parentId"));//check if the batch data was changed in the experiment and the current iteration composition is the step's
				String parentId = compositionData.get("PARENTID").toString();
				if(batchSizeUomName.equals("Liter")){//compositionType.equalsIgnoreCase("liquid")
					try{
						/**************Planned for X batch CALC ************/
						if(WV_GRL.isEmpty()){
							generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on Filler planned for X batch since the w/v[gr/L] is empty",
								ActivitylogType.Calculation, compositionId);
						} else {
							StringBuilder sbInfo = new StringBuilder();
							sbInfo.append("Planned for X batch calculation=></br>");
							sbInfo.append("w/v[gr/L] * batch size.</br> Args: "+WV_GRL+"*"+mainArgVal);
							generalUtilLogger.logWriter(LevelType.INFO,
									sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
							
							double plannedForBatch = Double.parseDouble(WV_GRL)*Double.parseDouble(mainArgVal);
							
							String onChangeColumnName = !isDescendantStepComp?"plannedToBatch":"plannedToExpBatch";
							String onChangeColumnVal = String.valueOf(plannedForBatch);
							updateEditableForm(formCode,compositionId,onChangeColumnName,onChangeColumnVal,parentId,userId,null);
							computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
							mergeActualValuesAndElementValueJson(toReturn,createReturnJsonArray(compositionId, new StringBuilder(computedArgsJson.toString())));
						}
					} catch(Exception e){
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on filler planned for X batch",
								ActivitylogType.Calculation, formId);
					}
				} else {//solid
					try{
						/**************Planned for X batch CALC ************/
						if(WW_GRK.isEmpty()){
							generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on the planned for X batch since the w/w[gr/kg] is empty",
								ActivitylogType.Calculation, compositionId);
						} else {
							StringBuilder sbInfo = new StringBuilder();
							sbInfo.append("Planned for X batch calculation=></br>");
							sbInfo.append("Formula: w/w[gr/kg] * batch size.</br> Args: "+WW_GRK+"*"+mainArgVal);
							generalUtilLogger.logWriter(LevelType.INFO,
									sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
							
							double plannedForBatch = Double.parseDouble(WW_GRK)*Double.parseDouble(mainArgVal);
							
							String onChangeColumnName = !isDescendantStepComp?"plannedToBatch":"plannedToExpBatch";
							String onChangeColumnVal = String.valueOf(plannedForBatch);
							updateEditableForm(formCode,compositionId,onChangeColumnName,onChangeColumnVal,parentId,userId,null);
							computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
							mergeActualValuesAndElementValueJson(toReturn,createReturnJsonArray(compositionId, new StringBuilder(computedArgsJson.toString())));
						}
					} catch(Exception e){
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on filler planned for X batch",
								ActivitylogType.Calculation, formId);
					}
				}
			}
		}
		
		if(!parentFormCode.equals("RecipeFormulation")//the "planned for X batch" exists in the experiment&stepminfr
		 &&(eventAction.equals("calcWwPOnChange") || eventAction.equals("calcWvOnChange")
			 || eventAction.equals("calcWwGrkOnChange"))){
			//in the formulation experiment and in the stepMinFr calculate the planned for X batch
			String batchSizeUomName = formDao.getFromInfoLookup("Uom", LookupType.ID, batchSizeUomId, "name");
			String plannedBatchSizeUomName = formDao.getFromInfoLookup("Uom", LookupType.ID, plannedBatchSizeUOM, "name");

			if(batchSizeUomName.equals("Liter")){//compositionType.equalsIgnoreCase("liquid")
				try{
					/**************Planned for X batch CALC ************/
					if((mainArgCode.equals("WV_GRL") && mainArgVal.isEmpty())
							||(!mainArgCode.equals("WV_GRL") 
									&& (!computedArgsJson.has("WV_GRL")||computedArgsJson.getString("WV_GRL").isEmpty()))){
						generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on the planned for X batch since the w/v[gr/L] is empty",
							ActivitylogType.Calculation, formId);
					} else if(originElementValueMap.get("batchSize").isEmpty()){
						generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on the Planned for X batch since the batch size is empty",
							ActivitylogType.Calculation, formId);
					} else {
						String WV_GRL = mainArgCode.equals("WV_GRL")?mainArgVal:computedArgsJson.getString("WV_GRL");
						StringBuilder sbInfo = new StringBuilder();
						sbInfo.append("Planned for X batch calculation=></br>");
						sbInfo.append("Formula: w/v[gr/L] * batch size.</br> Args: "+WV_GRL+"*"+originElementValueMap.get("batchSize"));
						generalUtilLogger.logWriter(LevelType.INFO,
								sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
						
						double plannedForBatch = Double.parseDouble(WV_GRL)*Double.parseDouble(originElementValueMap.get("batchSize"));
						
						String onChangeColumnName = "plannedToBatch";
						String onChangeColumnVal = String.valueOf(plannedForBatch);
						updateEditableForm(formCode,formId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,null);
						computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
					}
				} catch(Exception e){
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on the planned for X batch",
							ActivitylogType.Calculation, formId);
				}
			} else {//solid
				try{
					/**************Planned for X batch CALC ************/
					if((mainArgCode.equals("WW_GRK") && mainArgVal.isEmpty())
							||(!mainArgCode.equals("WW_GRK") && (!computedArgsJson.has("WW_GRK")||computedArgsJson.getString("WW_GRK").isEmpty()))){
						generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on the planned for X batch since the w/w[gr/kg] is empty",
							ActivitylogType.Calculation, formId);
					} else if(originElementValueMap.get("batchSize").isEmpty()){
						generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on the planned for X batch since the batch size is empty",
							ActivitylogType.Calculation, formId);
					} else {
						String WW_GRK = mainArgCode.equals("WW_GRK")?mainArgVal:computedArgsJson.getString("WW_GRK");
						StringBuilder sbInfo = new StringBuilder();
						sbInfo.append("Planned for X batch calculation=></br>");
						sbInfo.append("Formula: w/w[gr/kg] * batch size.</br> Args: "+WW_GRK+"*"+originElementValueMap.get("batchSize"));
						generalUtilLogger.logWriter(LevelType.INFO,
								sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
						
						double plannedForBatch = Double.parseDouble(WW_GRK)*Double.parseDouble(originElementValueMap.get("batchSize"));
						
						String onChangeColumnName = "plannedToBatch";
						String onChangeColumnVal = String.valueOf(plannedForBatch);
						updateEditableForm(formCode,formId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,null);
						computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
					}
				} catch(Exception e){
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on the planned for X batch",
							ActivitylogType.Calculation, formId);
				}
			}
			if(parentFormCode.equals("StepFr")){
			//compute the batch size for x formulation batch
				if(plannedBatchSizeUomName.equals("Liter")){//compositionType.equalsIgnoreCase("liquid")
					try{
						/**************Planned for X batch CALC ************/
						if((mainArgCode.equals("WV_GRL") && mainArgVal.isEmpty())
								||(!mainArgCode.equals("WV_GRL") 
										&& (!computedArgsJson.has("WV_GRL")||computedArgsJson.getString("WV_GRL").isEmpty()))){
							generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on the planned for X formulation batch since the w/v[gr/L] is empty",
								ActivitylogType.Calculation, formId);
						} else if(plannedBatchSize.isEmpty()){
							generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on the Planned for X formulation batch since the batch size is empty",
								ActivitylogType.Calculation, formId);
						} else {
							String WV_GRL = mainArgCode.equals("WV_GRL")?mainArgVal:computedArgsJson.getString("WV_GRL");
							StringBuilder sbInfo = new StringBuilder();
							sbInfo.append("Planned for X formulation batch calculation=></br>");
							sbInfo.append("Formula: w/v[gr/L] * batch size.</br> Args: "+WV_GRL+"*"+plannedBatchSize);
							generalUtilLogger.logWriter(LevelType.INFO,
									sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
							
							double plannedForBatch = Double.parseDouble(WV_GRL)*Double.parseDouble(plannedBatchSize);
							
							String onChangeColumnName = "plannedToExpBatch";
							String onChangeColumnVal = String.valueOf(plannedForBatch);
							updateEditableForm(formCode,formId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,null);
							computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
						}
					} catch(Exception e){
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on the planned for X formulation batch",
								ActivitylogType.Calculation, formId);
					}
				} else {//solid
					try{
						/**************Planned for X batch CALC ************/
						if((mainArgCode.equals("WW_GRK") && mainArgVal.isEmpty())
								||(!mainArgCode.equals("WW_GRK") && (!computedArgsJson.has("WW_GRK")||computedArgsJson.getString("WW_GRK").isEmpty()))){
							generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on the planned for X formulation batch since the w/w[gr/kg] is empty",
								ActivitylogType.Calculation, formId);
						} else if(plannedBatchSize.isEmpty()){
							generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on the planned for X formulation batch since the batch size is empty",
								ActivitylogType.Calculation, formId);
						} else {
							String WW_GRK = mainArgCode.equals("WW_GRK")?mainArgVal:computedArgsJson.getString("WW_GRK");
							StringBuilder sbInfo = new StringBuilder();
							sbInfo.append("Planned for X formulation batch calculation=></br>");
							sbInfo.append("Formula: w/w[gr/kg] * batch size.</br> Args: "+WW_GRK+"*"+plannedBatchSize);
							generalUtilLogger.logWriter(LevelType.INFO,
									sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
							
							double plannedForBatch = Double.parseDouble(WW_GRK)*Double.parseDouble(plannedBatchSize);
							
							String onChangeColumnName = "plannedToExpBatch";
							String onChangeColumnVal = String.valueOf(plannedForBatch);
							updateEditableForm(formCode,formId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,null);
							computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
						}
					} catch(Exception e){
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on the planned for X batch",
								ActivitylogType.Calculation, formId);
					}
				}
			}
			if(originElementValueMap.get("tableType").equals("stepComposition")){
				String lastProductId = generalDao.selectSingleStringNoException("select distinct first_value(formid) over (partition by parentid,tabletype order by formid desc)\n"
						+ "from fg_S_composition_v\n"
						+ "where parentid = '"+originElementValueMap.get("parentId")+"'\n"
						+ "and active = 1\n"
						+ "and tabletype = 'productComposition'"
						+ generalUtilFormState.getWherePartForTmpData("composition", originElementValueMap.get("parentId")));
				if(!generalUtil.getNull(lastProductId).isEmpty()){
					JSONObject productCalcRet = new JSONObject(doCalcComposition(api, "calcWWpProduct", "WW_P", "0", "0", originElementValueMap, apiCodesArray, elementsMatchArray, "composition", lastProductId, userId, elementValueJson));
					Iterator<String> keysItr = productCalcRet.keys();
					while (keysItr.hasNext()) {
						String key = keysItr.next();
						JSONObject rowData = productCalcRet.getJSONObject(key);
						if(rowData.has("warningMsg")){
							return productCalcRet.toString();
						}
					}
					mergeActualValuesAndElementValueJson(toReturn,productCalcRet);
				}
			}
		}

		if(computedArgsJson.length()>0){
			mergeActualValuesAndElementValueJson(toReturn,createReturnJsonArray(formId, new StringBuilder(computedArgsJson.toString())));
		}
		
		//*********Compute the difference weight in the StepFr when WW_GRK was entered/computed
		if(parentFormCode.equals("StepFr")
				&& (eventAction.equals("calcWwPOnChange") || eventAction.equals("calcWvOnChange")
			 || eventAction.equals("calcWwGrkOnChange"))){
			Set<String> invalidDifferenceProductList = new LinkedHashSet<>();
			String  material_id = generalDao.selectSingleStringNoException("select distinct invitemmaterial_id\n"
					+ " from fg_s_composition_v\n"
					+ " where formId = '"+formId+"'\n"
					+ " and active = 1\n"
					+ generalUtilFormState.getWherePartForTmpData("Composition", originElementValueMap.get("parentId")));
			String materialName = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, material_id, "name");
			//calculate the weight difference
			String sql = "select t.*, sum(t.WW_GRK)over(partition by t.invitemmaterial_id) as SUM_WW_GRK\n"
					+ " from fg_s_composition_v t\n"
					+ " where parentid in (select step_id from fg_s_step_v where experiment_id = '"+experimentId+"')\n"
					+ " and active = 1\n"
					+ " and invitemmaterial_id = '"+material_id+"'"
					+ " and tableType = 'productComposition'"
					+ " and sessionid is null";
			List<Map<String, Object>>compositionList = generalDao.getListOfMapsBySql(sql);//first, get the sum of the specific product
			for (Map<String, Object> compositionData : compositionList){
				computedArgsJson = new JSONObject();
				String compositionId = compositionData.get("FORMID").toString();
				String parentId = compositionData.get("PARENTID").toString();
				String SUM_WW_GRK = compositionData.get("SUM_WW_GRK")!=null?compositionData.get("SUM_WW_GRK").toString():"0";
				//get the sum of the product that exist  in the materials table, in order to compare it with the product sum that calculated above
				sql = "select distinct sum(WW_GRK)\n"
					+ " from fg_s_composition_v\n"
					+ " where parentId in (select step_id from fg_s_step_v where experiment_id = '"+experimentId+"')\n"
					+ " and sessionid is null"
					+ " and active = 1"
					+ " and tableType = 'stepComposition'"
					+ " and invitemmaterial_id = '"+material_id+"'";
				String sumWW_GRK = generalUtil.getNull(generalDao.selectSingleStringNoException(sql),"0");
				StringBuilder sbInfo = new StringBuilder();
				double difference = (Double.parseDouble(SUM_WW_GRK)- Double.parseDouble(sumWW_GRK));
				sbInfo.append("Difference calculation of product '"+materialName+"'=></br>");
				sbInfo.append("Formula:sum(Product '"+materialName+"' w/w[gr/kg])-sum(Material '"+materialName+"' w/w[gr/kg]).</br> Args: "+SUM_WW_GRK+"-"+sumWW_GRK);
				if(difference<0){//update the difference only if it is not negative.
					invalidDifferenceProductList.add(materialName);
				} else {
					String onChangeColumnName = "difference";
					String onChangeColumnVal = String.valueOf(difference);
					computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
					updateEditableForm("composition",compositionId,onChangeColumnName,onChangeColumnVal,parentId,userId,"");
					mergeActualValuesAndElementValueJson(toReturn,createReturnJsonArray(compositionId, new StringBuilder(computedArgsJson.toString())));
				}
			}
		}
		
		if(parentFormCode.equals("StepFr")||parentFormCode.equals("ExperimentFor")){
			computedArgsJson = new JSONObject();
			//calculate the experiment delta
			String sql = "select *\n"
					+ " from fg_s_composition_v\n"
					+ " where parentid = '"+experimentId+"'\n"
					+ " and active = 1\n"
					+ " and tableType = 'expComposition'"
					+ generalUtilFormState.getWherePartForTmpData("Composition", experimentId);
			List<Map<String, Object>> compositionList = generalDao.getListOfMapsBySql(sql);
			for (Map<String, Object> compositionData : compositionList){
				String compositionId = compositionData.get("FORMID").toString();
				String parentId = compositionData.get("PARENTID").toString();
				String plannedToBatch = compositionData.get("PLANNEDTOBATCH") !=null?generalUtil.getNull(compositionData.get("PLANNEDTOBATCH").toString(),"0"):"0";
				//String sessionId = compositionData.get("SESSIONID").toString();
				String materialId = compositionData.get("INVITEMMATERIAL_ID").toString();
				String materialFormCode= formDao.getFormCodeBySeqId(materialId);//material/recipeFormulation
				String materialName = formDao.getFromInfoLookup(materialFormCode, LookupType.ID, materialId, "name");
				sql = "select distinct sum(plannedToExpBatch)\n"
					+ " from fg_s_composition_v\n"
					+ " where parentId in (select step_id from fg_s_step_v where experiment_id = '"+experimentId+"')\n"
					+ " and sessionid is null"
					+ " and active = 1"
					+ " and tableType in ('stepComposition')"
					+ " and invitemmaterial_id = '"+materialId+"'";
				String sumPlannedToExpBatch = generalUtil.getNull(generalDao.selectSingleStringNoException(sql),"0");
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Delta calculation of "+materialFormCode+" '"+materialName+"'=></br>");
				sbInfo.append("Formula:experiment "+materialFormCode+" '"+materialName+" planned for batch-sum("+materialFormCode+" '"+materialName+"' planned for formulation batch).</br> Args: "+plannedToBatch+"-"+sumPlannedToExpBatch);
				String onChangeColumnName = "delta";
				String onChangeColumnVal = String.valueOf(Double.parseDouble(plannedToBatch)-Double.parseDouble(sumPlannedToExpBatch));
				computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
				updateEditableForm("composition",compositionId,onChangeColumnName,onChangeColumnVal,parentId,userId,"");
				mergeActualValuesAndElementValueJson(toReturn,createReturnJsonArray(compositionId, new StringBuilder(computedArgsJson.toString())));
			}
		}
		
		return toReturn.toString();
	}

	private JSONObject validateData(String formId,String eventAction, String mainArgCode, String mainArgVal, String compositionType, Map<String, String> originElementValueMap, String experimentId) {
		JSONObject jsonValidation = new JSONObject();
		/*if(eventAction.equals("clearCompositionArgs")){
			return jsonValidation;
		}*/
		if(mainArgVal.isEmpty() && !eventAction.equals("checkBalance")){
			jsonValidation.put("warningMsg", "Calculation cannot be completed. The entered value is invalid");
			return jsonValidation;
		}
		
		String stepStatusName = "";
		if(originElementValueMap.get("STATUS_ID") != null) {
			stepStatusName = formDao.getFromInfoLookup("StepStatus", LookupType.ID,
					originElementValueMap.get("STATUS_ID"), "name");
		}
		//make Planned
		if(stepStatusName == null || stepStatusName.isEmpty()) {
			stepStatusName = "Planned";
		}
		
		
		//gets the fillerId
		String fillerId = "";
		if(eventAction.equals("calcFillerOnChange")){
			fillerId = formId;
		}
		else{
			String sql = "select distinct formId from fg_s_composition_v"
						+ " where parentId = '"+originElementValueMap.get("parentId")+"'\n"
						+ " and nvl(active,'1') = '1'\n"
						+ " and nvl(filler,'0') = '1'\n"
						+ " and tableType = '"+originElementValueMap.get("tableType")+"'"
						+ generalUtilFormState.getWherePartForTmpData("Composition", originElementValueMap.get("parentId"));
			fillerId = generalDao.selectSingleStringNoException(sql);
		}
		
		double sumWW_P = 0;
		double sumWW_GRK = 0;
		JSONObject rowJson = new JSONObject();
		if(eventAction.equals("calcDensityOnChange")){
			String sql = "select distinct * from fg_s_Composition_v"
					+ " where parentId = '"+originElementValueMap.get("parentId")+"'\n"
					+ " and nvl(active,'1') = '1'\n"
					+ " and nvl(filler,'0') = '0'\n"
					+ " and tableType = '"+originElementValueMap.get("tableType")+"'"
					+ (fillerId.isEmpty()?"":" and formid <> '"+fillerId+"'")
					//+ " and mainArg in ('WV_GRL','WW_P')\n"
					+ generalUtilFormState.getWherePartForTmpData("Composition", originElementValueMap.get("parentId"));
			if(originElementValueMap.get("tableType").equals("expComposition")){//if the density was changed in the formulation experiment, then should calculate also the steps' materials&products
				String compositionTypeName = compositionType;
				sql +=" union all\n"
					+ "select distinct * from fg_s_composition_v"
					+ " where parentId in (select step_id from fg_s_step_v where experiment_id = '"+originElementValueMap.get("parentId")+"' and '"+compositionTypeName.toLowerCase()+"' = 'liquid')\n"
					+ " and nvl(active,'1') = '1'\n"
					+ " and nvl(filler,'0') = '0'\n"
					//+ " and mainArg in ('WV_GRL','WW_P')\n"
					+ " and tableType in ('stepComposition','productComposition')"
					+ " and sessionid is null";
			}
			Map<String,Double> sumWwpMaterials = new HashMap<String,Double>();
			List<Map<String, Object>> compositionList = generalDao.getListOfMapsBySql(sql);
			for (Map<String, Object> compositionData : compositionList){
				String compositionId = compositionData.get("FORMID").toString();
				String mainArg = compositionData.get("MAINARG") == null?"":compositionData.get("MAINARG").toString();
				String mainArgValue = mainArg.isEmpty()?"0":compositionData.get(mainArg).toString();
				String tableType = compositionData.get("TABLETYPE") == null?"":compositionData.get("TABLETYPE").toString();
				String parentId = compositionData.get("PARENTID") == null?"":compositionData.get("PARENTID").toString();
				if(mainArg.equals("WV_GRL")){
					try{
						/****************** W/W% CALC *****************/
						if(originElementValueMap.get("density").isEmpty()){
							jsonValidation.put("warningMsg", "The following value is missing: Density. Calculation cannot be completed.");
							return jsonValidation;
						} else if(Double.parseDouble(originElementValueMap.get("density"))==(double)0){
							jsonValidation.put("warningMsg", "The W/W% Calculation cannot be completed since the density = 0.");
							return jsonValidation;
						}else {
							double ww_p = Double.parseDouble(mainArgValue)/(Double.parseDouble(originElementValueMap.get("density"))*10);
							rowJson.put("WW_P", ww_p);
							String correctMainArgCode = mainArgCode.toLowerCase().equals("ww_p")?"w/w%":(mainArgCode.toLowerCase().equals("ww_grk")?"w/w[gr/kg]":(mainArgCode.toLowerCase().equals("wv_grl")?"w/v[gr/L]":mainArgCode));
							if(ww_p>100){
								jsonValidation.put("warningMsg", "The calculation cannot be completed when the "+correctMainArgCode+" = "+mainArgVal+" since it results in a w/w% > 100%.");
								return jsonValidation;
							}
							sumWW_P+=ww_p;
							if(tableType.equals("stepComposition")){
								if(sumWwpMaterials.containsKey(parentId)){
									sumWwpMaterials.put(parentId, sumWwpMaterials.get(parentId)+ww_p);
								} else {
									sumWwpMaterials.put(parentId, ww_p);
								}
							}
						}
					} catch(Exception e){
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on w/w%",
								ActivitylogType.Calculation, compositionId);
					}
					try{
						/*************** W/W[gr/kg] CALC **************/
						if(!rowJson.has("WW_P")||rowJson.getString("WW_P").isEmpty()){
							jsonValidation.put("warningMsg", "The following value is missing: W/W%. Calculation cannot be completed.");
							return jsonValidation;
						} else {
							double ww_grk = Double.parseDouble(rowJson.getString("WW_P"))*10;
							rowJson.put("WW_GRK", ww_grk);
							if(ww_grk>1000){
								String correctMainArgCode = mainArgCode.toLowerCase().equals("ww_p")?"w/w%":(mainArgCode.toLowerCase().equals("ww_grk")?"w/w[gr/kg]":(mainArgCode.toLowerCase().equals("wv_grl")?"w/v[gr/L]":mainArgCode));
								jsonValidation.put("warningMsg", "The calculation cannot be completed when the "+correctMainArgCode+" = "+mainArgVal+" since it results in a w/w[gr/kg]>1000.");
								return jsonValidation;
							}
							sumWW_GRK+=ww_grk;
						}
					} catch(Exception e){
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on w/w[gr/kg]",
								ActivitylogType.Calculation, compositionId);
					}				
				
					//The following validation are unnecessary:
					//- there's no need to compute the WV_GRL since there's no any limitation on it
					//- there's no need to compute the WW_GRK when the WW_P is the main since it is not depended on the density
				} else if(mainArg.equals("WW_P")){
					if(originElementValueMap.get("density").isEmpty()){
						jsonValidation.put("warningMsg", "The following value is missing: Density. Calculation of the W/V [gr/L] cannot be completed.");
						return jsonValidation;
					}
					if(tableType.equals("stepComposition")){
						if(sumWwpMaterials.containsKey(parentId)){
							sumWwpMaterials.put(parentId, sumWwpMaterials.get(parentId)+Double.parseDouble(mainArgValue));
						} else {
							sumWwpMaterials.put(parentId, Double.parseDouble(mainArgValue));
						}
					}
				} else {
					String ww_p = compositionData.get("WW_P")==null?"0":generalUtil.getEmpty(compositionData.get("WW_P").toString(),"0");
					String ww_grk = compositionData.get("WW_GRK")==null?"0":generalUtil.getEmpty(compositionData.get("WW_GRK").toString(),"0");
					sumWW_P+=Double.parseDouble(ww_p);
					sumWW_GRK+=Double.parseDouble(ww_grk);
					if(tableType.equals("stepComposition")){
						if(sumWwpMaterials.containsKey(parentId)){
							sumWwpMaterials.put(parentId, sumWwpMaterials.get(parentId)+Double.parseDouble(ww_p));
						} else {
							sumWwpMaterials.put(parentId, Double.parseDouble(ww_p));
						}
					}
				}
			}
			for(Entry<String, Double> sumWwpMaterialPerStep:sumWwpMaterials.entrySet()){
				if(sumWwpMaterialPerStep.getValue()>100){//this value calculated in order to store it in the w/w% in the products table. if the value is bigger than 100 a message appears
					String stepName = formDao.getFromInfoLookup("Step", LookupType.ID, sumWwpMaterialPerStep.getKey(),"name");
					jsonValidation.put("warningMsg", "The calculation cannot be completed when the density = "  +mainArgVal+" since it results in a w/w% > 100% in the products table of Step "+stepName+".");
					return jsonValidation;
				}
			}
			
		} else if(eventAction.equals("calcFillerOnChange")){
			if(fillerId.isEmpty()){
				return jsonValidation;
			}
			String sql = "select distinct * from fg_s_Composition_v"
					+ " where parentId = '"+originElementValueMap.get("parentId")+"'\n"
					+ " and nvl(active,'1') = '1'\n"
					+ " and nvl(filler,'0') = '0'\n"
					+ (fillerId.isEmpty()?"":" and formid <> '"+fillerId+"'")
					+ " and tableType = '"+originElementValueMap.get("tableType")+"'"
					+ generalUtilFormState.getWherePartForTmpData("Composition", originElementValueMap.get("parentId"));

			List<Map<String, Object>> compositionList = generalDao.getListOfMapsBySql(sql);
			for (Map<String, Object> compositionData : compositionList){
				String WW_GRK = compositionData.get("WW_GRK") == null ?"0":generalUtil.getEmpty(compositionData.get("WW_GRK").toString(),"0");
				String WW_P = compositionData.get("WW_P") == null ?"0":generalUtil.getEmpty(compositionData.get("WW_P").toString(),"0");
				sumWW_GRK+=Double.parseDouble(WW_GRK);
				sumWW_P+=Double.parseDouble(WW_P);
			}
		} else if(eventAction.equals("calcWvOnChange")){
			String sql = "select distinct * from fg_s_Composition_v"
					+ " where parentId = '"+originElementValueMap.get("parentId")+"'\n"
					+ " and nvl(active,'1') = '1'\n"
					+ " and nvl(filler,'0') = '0'\n"
					+ (fillerId.isEmpty()?"":" and formid <> '"+fillerId+"'")
					+ " and tableType = '"+originElementValueMap.get("tableType")+"'"
					+ generalUtilFormState.getWherePartForTmpData("Composition", originElementValueMap.get("parentId"));
			List<Map<String, Object>> compositionList = generalDao.getListOfMapsBySql(sql);
			for (Map<String, Object> compositionData : compositionList){	
				String compositionId = compositionData.get("FORMID").toString();
				/*if(fillerId.isEmpty() && !compositionId.equals(formId)){
					continue;
				} else*/ if(!compositionId.equals(formId)){
					String WW_GRK = compositionData.get("WV_GRK") == null?"0":generalUtil.getEmpty(compositionData.get("WV_GRK").toString(),"0");
					String WW_P = compositionData.get("WW_P") == null?"0":generalUtil.getEmpty(compositionData.get("WW_P").toString(),"0");
					sumWW_GRK+=Double.parseDouble(WW_GRK);
					sumWW_P+=Double.parseDouble(WW_P);
				} else{
					try{
						if(originElementValueMap.get("density").isEmpty()){
							jsonValidation.put("warningMsg", "The following value is missing: Density. Calculation of W/W% cannot be completed.");
							return jsonValidation;
						} else if(Double.parseDouble(originElementValueMap.get("density"))==(double)0){
							jsonValidation.put("warningMsg", "The W/W% Calculation cannot be completed since the density = 0.");
							return jsonValidation;
						} else {
							double ww_p = Double.parseDouble(mainArgVal)/(Double.parseDouble(originElementValueMap.get("density"))*10);
							rowJson.put("WW_P",ww_p);
							sumWW_P+=ww_p;
							if(ww_p>100){
								String correctMainArgCode = mainArgCode.toLowerCase().equals("ww_p")?"w/w%":(mainArgCode.toLowerCase().equals("ww_grk")?"w/w[gr/kg]":(mainArgCode.toLowerCase().equals("wv_grl")?"w/v[gr/L]":mainArgCode));
								jsonValidation.put("warningMsg", "The calculation cannot be completed when the "+correctMainArgCode+" = "+mainArgVal+" since it results in a w/w% > 100%.");
								return jsonValidation;
							}
						}
					} catch(Exception e){
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on w/w%",
								ActivitylogType.Calculation, formId);
					}
					try{
						/*************** W/W[gr/kg] CALC **************/
						if(!rowJson.has("WW_P")||rowJson.getString("WW_P").isEmpty()){
							jsonValidation.put("warningMsg", "The following value is missing: W/W%. Calculation cannot be completed.");
							return jsonValidation;
						} else {
							double ww_grk = Double.parseDouble(rowJson.getString("WW_P"))*10;
							if(ww_grk>1000){
								String correctMainArgCode = mainArgCode.toLowerCase().equals("ww_p")?"w/w%":(mainArgCode.toLowerCase().equals("ww_grk")?"w/w[gr/kg]":(mainArgCode.toLowerCase().equals("wv_grl")?"w/v[gr/L]":mainArgCode));
								jsonValidation.put("warningMsg", "The calculation cannot be completed when the "+correctMainArgCode+" = "+mainArgVal+" since it results in a w/w[gr/kg] > 1000.");
								return jsonValidation;
							}
							sumWW_GRK+=ww_grk;
						}
					} catch(Exception e){
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on w/w[gr/kg]",
								ActivitylogType.Calculation, formId);
					}
				}
			}
		} else if(eventAction.equals("calcWwGrkOnChange")){
			sumWW_GRK += Double.parseDouble(mainArgVal);
			String sql = "select distinct * from fg_s_Composition_v"
					+ " where parentId = '"+originElementValueMap.get("parentId")+"'\n"
					+ " and nvl(active,'1') = '1'\n"
					+ " and nvl(filler,'0') = '0'\n"
					+ (fillerId.isEmpty()?"":" and formid <> '"+fillerId+"'")
					+ " and tableType = '"+originElementValueMap.get("tableType")+"'"
					+ generalUtilFormState.getWherePartForTmpData("Composition", originElementValueMap.get("parentId"));
			List<Map<String, Object>> compositionList = generalDao.getListOfMapsBySql(sql);
			for (Map<String, Object> compositionData : compositionList){	
				String compositionId = compositionData.get("FORMID").toString();
				/*if(fillerId.isEmpty() && !compositionId.equals(formId)){
					continue;
				} else */if(!compositionId.equals(formId)){
					String WW_GRK = compositionData.get("WV_GRK") == null?"0":generalUtil.getEmpty(compositionData.get("WV_GRK").toString(),"0");
					String WW_P = compositionData.get("WW_P") == null?"0":generalUtil.getEmpty(compositionData.get("WW_P").toString(),"0");
					sumWW_GRK+=Double.parseDouble(WW_GRK);
					sumWW_P+=Double.parseDouble(WW_P);
				} else {
					try{
						double ww_p = Double.parseDouble(mainArgVal)/10;	
						rowJson.put("WW_P",ww_p);
						sumWW_P+=ww_p;
						if(ww_p>100){
							String correctMainArgCode = mainArgCode.toLowerCase().equals("ww_p")?"w/w%":(mainArgCode.toLowerCase().equals("ww_grk")?"w/w[gr/kg]":(mainArgCode.toLowerCase().equals("wv_grl")?"w/v[gr/L]":mainArgCode));
							jsonValidation.put("warningMsg", "The calculation cannot be completed when the "+correctMainArgCode+" = "+mainArgVal+" since it results in a w/w% > 100%.");
							return jsonValidation;
						}
					} catch(Exception e){
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on w/w%",
								ActivitylogType.Calculation, formId);
					}
					if(compositionType.equalsIgnoreCase("liquid")){
						if(originElementValueMap.get("density").isEmpty()){
							jsonValidation.put("warningMsg", "The following value is missing: Density. Calculation of the W/V[gr/L] cannot be completed.");
							return jsonValidation;
						} 
					}
				}
			}
		}
		else if(eventAction.equals("calcWwPOnChange")){
			sumWW_P+=Double.parseDouble(mainArgVal);
			String sql = "select distinct * from fg_s_composition_v"
					+ " where parentId = '"+originElementValueMap.get("parentId")+"'\n"
					+ " and nvl(active,'1') = '1'\n"
					+ " and nvl(filler,'0') = '0'\n"
					+ (fillerId.isEmpty()?"":" and formid <> '"+fillerId+"'")
					+ " and tableType = '"+originElementValueMap.get("tableType")+"'"
					+ generalUtilFormState.getWherePartForTmpData("Composition", originElementValueMap.get("parentId"));
			List<Map<String, Object>> compositionList = generalDao.getListOfMapsBySql(sql);
			for (Map<String, Object> compositionData : compositionList){	
				String compositionId = compositionData.get("FORMID").toString();
				/*if(fillerId.isEmpty() && !compositionId.equals(formId)){
					continue;
				} else */if(!compositionId.equals(formId)){
					String WW_GRK = compositionData.get("WW_GRK") == null?"0":generalUtil.getEmpty(compositionData.get("WW_GRK").toString(),"0");
					String WW_P = compositionData.get("WW_P") == null?"0":generalUtil.getEmpty(compositionData.get("WW_P").toString(),"0");
					sumWW_GRK+=Double.parseDouble(WW_GRK);
					sumWW_P+=Double.parseDouble(WW_P);
				} else{
					try{
						double ww_grk = Double.parseDouble(mainArgVal)*10;
						if(ww_grk>1000){
							String correctMainArgCode = mainArgCode.toLowerCase().equals("ww_p")?"w/w%":(mainArgCode.toLowerCase().equals("ww_grk")?"w/w[gr/kg]":(mainArgCode.toLowerCase().equals("wv_grl")?"w/v[gr/L]":mainArgCode));
							jsonValidation.put("warningMsg", "The calculation cannot be completed when the "+correctMainArgCode+" = "+mainArgVal+" since it results in a w/w[gr/kg] > 1000.");
							return jsonValidation;
						}
						sumWW_GRK+=ww_grk;
					} catch(Exception e){
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on w/w[gr/kg]",
								ActivitylogType.Calculation, formId);
					}
					if(compositionType.equalsIgnoreCase("liquid")){
						if(originElementValueMap.get("density").isEmpty()){
							jsonValidation.put("warningMsg", "The following value is missing: Density. Calculation of the W/V[gr/L] cannot be completed.");
							return jsonValidation;
						} 
					}
				}
			}
		} else if(eventAction.equals("calcPurityOnChange")||eventAction.equals("getPurityAndCalcComposition")){
			if(eventAction.equals("getPurityAndCalcComposition")){
				mainArgCode = "Purity";
				mainArgVal = mainArgVal.equals("0")?"100":generalDao.selectSingleStringNoException("select nvl(purity,100)\n"
						+ "from fg_S_invitembatch_v\n"
						+ "where formid = '"+mainArgVal+"'");
			}
			String sql = "select distinct * from fg_s_composition_v"
					+ " where parentId = '"+originElementValueMap.get("parentId")+"'\n"
					+ " and nvl(active,'1') = '1'\n"
					+ " and nvl(filler,'0') = '0'\n"
					+ (fillerId.isEmpty()?"":" and formid <> '"+fillerId+"'")
					+ " and tableType = '"+originElementValueMap.get("tableType")+"'"
					+ generalUtilFormState.getWherePartForTmpData("Composition", originElementValueMap.get("parentId"));
			List<Map<String, Object>> compositionList = generalDao.getListOfMapsBySql(sql);
			for (Map<String, Object> compositionData : compositionList){	
				String compositionId = compositionData.get("FORMID").toString();
				/*if(fillerId.isEmpty() && !compositionId.equals(formId)){
					continue;
				} else */if(!compositionId.equals(formId)){
					String WW_GRK = compositionData.get("WW_GRK") == null?"0":generalUtil.getEmpty(compositionData.get("WW_GRK").toString(),"0");
					String WW_P = compositionData.get("WW_P") == null?"0":generalUtil.getEmpty(compositionData.get("WW_P").toString(),"0");
					sumWW_GRK+=Double.parseDouble(WW_GRK);
					sumWW_P+=Double.parseDouble(WW_P);
				} else{
					try{
						String WW_P = compositionData.get("WW_P") == null?"0":generalUtil.getEmpty(compositionData.get("WW_P").toString(),"0");
						double ww_p = Double.parseDouble(WW_P)/(Double.parseDouble(mainArgVal)/100);//ww_p/(purity/100)
						double ww_grk = ww_p*10;
						if(ww_grk>1000){
							String correctMainArgCode = mainArgCode.toLowerCase().equals("ww_p")?"w/w%":(mainArgCode.toLowerCase().equals("ww_grk")?"w/w[gr/kg]":(mainArgCode.toLowerCase().equals("wv_grl")?"w/v[gr/L]":mainArgCode));
							jsonValidation.put("warningMsg", "The calculation cannot be completed when the "+correctMainArgCode+" = "+mainArgVal+" since it results in a w/w[gr/kg] > 1000.");
							return jsonValidation;
						}
						sumWW_GRK+=ww_grk;
						sumWW_P+=ww_p;
					} catch(Exception e){
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on w/w[gr/kg]",
								ActivitylogType.Calculation, formId);
					}
					if(compositionType.equalsIgnoreCase("liquid")){
						if(originElementValueMap.get("density").isEmpty()){
							jsonValidation.put("warningMsg", "The following value is missing: Density. Calculation of the W/V[gr/L] cannot be completed.");
							return jsonValidation;
						} 
					}
				}
			}
		} 
		if(eventAction.equals("checkBalance")
				|| eventAction.equals("calcWwPOnChange") || eventAction.equals("calcWvOnChange")
			 || eventAction.equals("calcWwGrkOnChange")){
			String formIdToDelete = originElementValueMap.containsKey("formIdToDelete")?originElementValueMap.get("formIdToDelete"):"";
			String productId = originElementValueMap.containsKey("formIdToDelete")?
					generalDao.selectSingleStringNoException("select distinct listagg(invitemmaterial_id,',')within group(order by invitemmaterial_id) productId\n"
							+ " from fg_s_composition_v\n"
							+ " where formid in ("+formIdToDelete+")\n"
							+ " and sessionid is null\n"
							+ " and active = 1"):"";
			//calculate the weight difference
			Set<String> invalidDifferenceProductList = new LinkedHashSet<>();//data structure with no duplicate elements
			String sql = "select t.*, sum("+generalUtil.getEmpty(mainArgCode,"ww_grk")+")over(partition by t.invitemmaterial_id) as SUM_WW_GRK\n"
					+ " from fg_s_composition_v t\n"
					+ " where parentid in (select step_id from fg_s_step_v where experiment_id = '"+experimentId+"')\n"
					+  (!formIdToDelete.isEmpty()?" and formid not in ("+formIdToDelete+")":"")
					+ (!productId.isEmpty()?" and invitemmaterial_id in ("+productId+")":"")
					+ " and active = 1\n"
					+ " and tableType = 'productComposition'"
					+ " and sessionid is null";
			List<Map<String, Object>> compositionList = generalDao.getListOfMapsBySql(sql);
			for (Map<String, Object> compositionData : compositionList){
				String materialId = compositionData.get("INVITEMMATERIAL_ID") ==null?"":compositionData.get("INVITEMMATERIAL_ID").toString();
				if(materialId.isEmpty()){
					continue;
				}
				String SUM_WW_GRK = compositionData.get("SUM_WW_GRK")!=null?compositionData.get("SUM_WW_GRK").toString():"0";
				String compositionId = compositionData.get("FORMID") ==null?"":compositionData.get("FORMID").toString();
				String parentId = compositionData.get("PARENTID") ==null?"":compositionData.get("PARENTID").toString();
				if((eventAction.equals("calcWwPOnChange") || eventAction.equals("calcWvOnChange")
						|| eventAction.equals("calcWwGrkOnChange"))
						&& parentId.equals(originElementValueMap.get("parentId")) && originElementValueMap.get("tableType").equals("stepComposition")){
					//if there was a  change in the materials table- then should calculate the sum of the changed argument and update the product table.
					//therefore, the sum of the current product is not relevant and we do the calculation according to the changed argument, and then check if there was no a deviation
					String compositionMainArgVal = compositionData.get(generalUtil.getEmpty(mainArgCode.toUpperCase(),"WW_GRK"))!=null?compositionData.get(generalUtil.getEmpty(mainArgCode.toUpperCase(),"WW_GRK")).toString():"0";
					SUM_WW_GRK = String.valueOf(Double.parseDouble(SUM_WW_GRK)-Double.parseDouble(compositionMainArgVal));
					String sumMaterialsSameStep = generalDao.selectSingleStringNoException("select sum("+generalUtil.getEmpty(mainArgCode,"ww_grk")+")\n"
							+"from fg_S_composition_v\n"
							+ "where parentid='"+originElementValueMap.get("parentId")+"'\n"
							+ "and active = 1\n"
							+ "and tabletype = 'stepComposition'"
							+ generalUtilFormState.getWherePartForTmpData("composition", parentId));
					sumMaterialsSameStep = generalUtil.getNull(sumMaterialsSameStep,"0");
					SUM_WW_GRK = String.valueOf(Double.parseDouble(SUM_WW_GRK)+Double.parseDouble(sumMaterialsSameStep));
				}
				String materialName = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, materialId, "name");
				sql = "select distinct sum("+generalUtil.getEmpty(mainArgCode,"ww_grk")+")\n"
					+ " from fg_s_composition_v\n"
					+ " where parentId in (select step_id from fg_s_step_v where experiment_id = '"+experimentId+"')\n"
					+ " and sessionid is null"
					+ " and active = 1"
					+ " and tableType = 'stepComposition'"
					+ " and invitemmaterial_id = '"+materialId+"'";
				String sumWW_GRKforBalance = generalUtil.getNull(generalDao.selectSingleStringNoException(sql),"0");
				StringBuilder sbInfo = new StringBuilder();
				double difference = (Double.parseDouble(SUM_WW_GRK)- Double.parseDouble(sumWW_GRKforBalance));
				sbInfo.append("Difference calculation of product '"+materialName+"'=></br>");
				sbInfo.append("Formula:sum(Product '"+materialName+"' w/w[gr/kg])-sum(Material '"+materialName+"' w/w[gr/kg]).</br> Args: "+SUM_WW_GRK+"-"+sumWW_GRK);
				if(difference<0){//update the difference only if it is not negative.
					invalidDifferenceProductList.add(materialName);
				}
			}
			if(!invalidDifferenceProductList.isEmpty()){
				jsonValidation.put("warningMsg", "The sum of the used Qty of "+(invalidDifferenceProductList.size()>1?"Materials: ":"Material ")+invalidDifferenceProductList.toString().replaceAll("\\[|\\]", "")+" is higher than its step-product Qty");
			}
		}
		if(!fillerId.isEmpty()){
			String correctMainArgCode = mainArgCode.toLowerCase().equals("ww_p")?"w/w%":(mainArgCode.toLowerCase().equals("ww_grk")?"w/w[gr/kg]":(mainArgCode.toLowerCase().equals("wv_grl")?"w/v[gr/L]":mainArgCode));
			correctMainArgCode = mainArgCode.toLowerCase().equals("filler")?mainArgCode:correctMainArgCode;
			if(sumWW_GRK>1000){
				jsonValidation.put("warningMsg", "The calculation cannot be completed when the "+correctMainArgCode
						+(mainArgCode.toLowerCase().equals("filler")?" is "+(mainArgVal.equals("1")?"true":"false"):" = "+mainArgVal)
						+" since it results in a negative filler w/w[gr/kg].");
			}
			if(sumWW_P>100){
				jsonValidation.put("warningMsg", "The calculation cannot be completed when the "+correctMainArgCode
						+(mainArgCode.toLowerCase().equals("filler")?" is "+(mainArgVal.equals("1")?"true":"false"):" = "+mainArgVal)
						+" since it results in a negative filler w/w%.");
			}
		} else {
			String productCount= generalDao.selectSingleStringNoException("select count(*)\n"
					+ "from fg_s_composition_v\n"
					+ "where parentid = '"+originElementValueMap.get("parentId")+"'\n"
					+ "and tableType = 'productComposition'\n"
					+ generalUtilFormState.getWherePartForTmpData("composition", originElementValueMap.get("parentId")));
			if(generalUtil.getNull(originElementValueMap.get("tableType")).equals("stepComposition") 
					&& (eventAction.equals("calcWwPOnChange") || eventAction.equals("calcWvOnChange")
							 || eventAction.equals("calcWwGrkOnChange"))
					&& !productCount.equals("0") && sumWW_P>100){//if there is at least one product and the sum of the materials exceeded the 100%
				String stepName = formDao.getFromInfoLookup("step", LookupType.ID, originElementValueMap.get("parentId"), "name");
				String correctMainArgCode = mainArgCode.toLowerCase().equals("ww_p")?"w/w%":(mainArgCode.toLowerCase().equals("ww_grk")?"w/w[gr/kg]":(mainArgCode.toLowerCase().equals("wv_grl")?"w/v[gr/L]":mainArgCode));
				jsonValidation.put("warningMsg", "The calculation cannot be completed when the "+correctMainArgCode+" = "+mainArgVal+" since it results in a w/w% > 100% in the product table of Step "+stepName+".");
			}
		}
		return jsonValidation;
		
	}

	//calc the filler row
	//returns a json object of the calculated columns and their values {"WW_P":8,"WW_GRK":"9"...}
	private JSONObject calcFillerRow(String formId,String formCode,String parentId,String mainArg,Map<String, String> originElementValueMap,String userId, String eventAction, String batchSizeUomId,String compositionType) {
		String plannedBatchSize = originElementValueMap.get("plannedBatchSize");
		String plannedBatchSizeUOM = originElementValueMap.get("plannedBatchSizeUOM");
		String batchSizeUomName = formDao.getFromInfoLookup("Uom", LookupType.ID, batchSizeUomId, "name");
		JSONObject toReturn  = new JSONObject();
		JSONObject computedArgsJson =  new JSONObject();
		String fillerId = "";
		if(eventAction.equals("calcFillerOnChange")){
			fillerId = formId;
		} else {
			 fillerId = generalDao.selectSingleStringNoException("select distinct formId\n"
				+ " from fg_s_composition_v\n"
				+ " where parentId = '"+parentId+"'\n"
				+ " and nvl(filler,'0') = '1'\n"
				+ " and nvl(active,'1') = '1'"
				+ " and tableType = '"+originElementValueMap.get("tableType")+"'"
				+ generalUtilFormState.getWherePartForTmpData("Composition", parentId));
		}
		if(fillerId.isEmpty()){
			return toReturn;
		}
		try{
			/**************Filler W/W% CALC *******************/
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Filler W/W % calculation=></br>");
			sbInfo.append("Formula: 100% minus (sum (w/w %) of all other materials).");
			generalUtilLogger.logWriter(LevelType.INFO,
					sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
			String sql = "select distinct sum(nvl(ww_p,0)) over (partition by parentid) sum\n"
					+ " from fg_s_composition_v \n"
					+ " where parentid = '"+parentId+"'\n"
					+ (fillerId.isEmpty()?"":" and formId <>'"+fillerId+"'\n")
					+ " and nvl(active,'1') = '1'"
					+ " and tableType = '"+originElementValueMap.get("tableType")+"'"
					+ generalUtilFormState.getWherePartForTmpData("Composition", parentId);
			Double  ww_p = 100-Double.parseDouble(generalUtil.getEmpty(generalDao.selectSingleStringNoException(sql),"0"));
			
			String onChangeColumnName = "WW_P";
			String onChangeColumnVal = String.valueOf(ww_p);
			updateEditableForm(formCode,fillerId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,mainArg);
			computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
		} catch(Exception e){
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on filler w/w%",
					ActivitylogType.Calculation, fillerId);
		}
		try{
			/**************Filler W/W [gr/kg] CALC *******************/
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Filler W/W [gr/kg] calculation=></br>");
			sbInfo.append("Formula:(1000 gr/kg) minus (sum (w/w [gr/kg]) of all other materials)");
			generalUtilLogger.logWriter(LevelType.INFO,
					sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
			String sql = "select distinct sum(nvl(ww_grk,0)) over (partition by parentid) sum\n"
					+ " from fg_s_composition_v \n"
					+ " where parentid = '"+parentId+"'\n"
					+ (fillerId.isEmpty()?"":" and formId <>'"+fillerId+"'\n")
					+ " and nvl(active,'1') = '1'"
					+ " and tableType = '"+originElementValueMap.get("tableType")+"'"
					+ generalUtilFormState.getWherePartForTmpData("Composition", parentId);
			double ww_grk = 1000-Double.parseDouble(generalUtil.getEmpty(generalDao.selectSingleStringNoException(sql),"0"));
			
			String onChangeColumnName = "WW_GRK";
			String onChangeColumnVal = String.valueOf(ww_grk);
			updateEditableForm(formCode,fillerId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,mainArg);
			computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
		} catch(Exception e){
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on filler w/w[gr/kg]",
					ActivitylogType.Calculation, fillerId);
		}
		if(compositionType.equalsIgnoreCase("liquid")){
			try{
				/**************Filler W/V[gr/L] CALC ************/
				if(!computedArgsJson.has("WW_P")||computedArgsJson.getString("WW_P").isEmpty()){
					generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on Filler W/V[gr/L]",
						ActivitylogType.Calculation, fillerId);
				} else {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Filler W/V[gr/L] calculation=></br>");
					sbInfo.append("Formula:filler w/w% * density[gr/ml]*10.</br> Args: "+computedArgsJson.getString("WW_P")+"*"+originElementValueMap.get("density")+"*10");
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
					
					if(originElementValueMap.get("density").isEmpty()){
						generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on filler W/V[gr/L] since the density is empty.",
						ActivitylogType.Calculation, fillerId);
						computedArgsJson.put("warningMsg", "The following value is missing: Density. Calculation of W/V[gr/L] cannot be completed.");
					} else {
						double wv_grl = Double.parseDouble(computedArgsJson.getString("WW_P"))*Double.parseDouble(originElementValueMap.get("density"))*10;
						
						String onChangeColumnName = "WV_GRL";
						String onChangeColumnVal = String.valueOf(wv_grl);
						updateEditableForm(formCode,fillerId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,mainArg);
						computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
					}
				}
			} catch(Exception e){
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on filler w/v[gr/L]",
						ActivitylogType.Calculation, fillerId);
			}
		}
		String parentFormCode = formDao.getFormCodeBySeqId(parentId);
		if(parentFormCode.equals("RecipeFormulation")){
			return createReturnJsonArray(fillerId, new StringBuilder(computedArgsJson.toString()));
		}
		//in the formulation experiment and in the stepMinFr calculate the planned for X batch
		if(batchSizeUomName.equalsIgnoreCase("Liter")){
			try{
				/**************Filler planned for X batch CALC ************/
				if(!computedArgsJson.has("WV_GRL")||computedArgsJson.getString("WV_GRL").isEmpty()){
					generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on Filler planned for X batch since the w/v[gr/L] is empty",
						ActivitylogType.Calculation, fillerId);
				} else if(originElementValueMap.get("batchSize").isEmpty()){
					generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on Filler planned for X batch since the batch size is empty",
						ActivitylogType.Calculation, fillerId);
				} else {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Filler planned for X batch calculation=></br>");
					sbInfo.append("Formula:filler w/v[gr/L] * batch size.</br> Args: "+computedArgsJson.getString("WV_GRL")+"*"+originElementValueMap.get("batchSize"));
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
					
					double plannedForBatch = Double.parseDouble(computedArgsJson.getString("WV_GRL"))*Double.parseDouble(originElementValueMap.get("batchSize"));
					
					String onChangeColumnName = "plannedToBatch";
					String onChangeColumnVal = String.valueOf(plannedForBatch);
					updateEditableForm(formCode,fillerId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,mainArg);
					computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
				}
			} catch(Exception e){
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on filler planned for X batch",
						ActivitylogType.Calculation, fillerId);
			}
		} else {//solid
			try{
				/**************Filler planned for X batch CALC ************/
				if(!computedArgsJson.has("WW_GRK")||computedArgsJson.getString("WW_GRK").isEmpty()){
					generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on Filler planned for X batch since the w/w[gr/kg] is empty",
						ActivitylogType.Calculation, fillerId);
				} else if(originElementValueMap.get("batchSize").isEmpty()){
					generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on Filler planned for X batch since the batch size is empty",
						ActivitylogType.Calculation, fillerId);
				} else {
					StringBuilder sbInfo = new StringBuilder();
					sbInfo.append("Filler planned for X batch calculation=></br>");
					sbInfo.append("Formula:filler w/w[gr/kg] * batch size.</br> Args: "+computedArgsJson.getString("WW_GRK")+"*"+originElementValueMap.get("batchSize"));
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
					
					double plannedForBatch = Double.parseDouble(computedArgsJson.getString("WW_GRK"))*Double.parseDouble(originElementValueMap.get("batchSize"));
					
					String onChangeColumnName = "plannedToBatch";
					String onChangeColumnVal = String.valueOf(plannedForBatch);
					updateEditableForm(formCode,fillerId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,mainArg);
					computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
				}
			} catch(Exception e){
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on filler planned for X batch",
						ActivitylogType.Calculation, fillerId);
			}
		}
		if(parentFormCode.equals("StepFr")){
			//compute the batch size for x formulation batch
			String plannedBatchSizeUomName = formDao.getFromInfoLookup("Uom", LookupType.ID, plannedBatchSizeUOM, "name");
				if(plannedBatchSizeUomName.equals("Liter")){//compositionType.equalsIgnoreCase("liquid")
					try{
						/**************Filler planned for X batch CALC ************/
						if(!computedArgsJson.has("WV_GRL")||computedArgsJson.getString("WV_GRL").isEmpty()){
							generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on Filler planned for X formulation batch since the w/v[gr/L] is empty",
								ActivitylogType.Calculation, fillerId);
						} else if(plannedBatchSize.isEmpty()){
							generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on Filler planned for X formulation batch since the batch size is empty",
								ActivitylogType.Calculation, fillerId);
						} else {
							StringBuilder sbInfo = new StringBuilder();
							sbInfo.append("Filler planned for X formulation batch calculation=></br>");
							sbInfo.append("Formula:filler w/v[gr/L] * batch size.</br> Args: "+computedArgsJson.getString("WV_GRL")+"*"+plannedBatchSize);
							generalUtilLogger.logWriter(LevelType.INFO,
									sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
							
							double plannedForBatch = Double.parseDouble(computedArgsJson.getString("WV_GRL"))*Double.parseDouble(plannedBatchSize);
							
							String onChangeColumnName = "plannedToExpBatch";
							String onChangeColumnVal = String.valueOf(plannedForBatch);
							updateEditableForm(formCode,fillerId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,mainArg);
							computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
						}
					} catch(Exception e){
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on filler planned for X batch",
								ActivitylogType.Calculation, fillerId);
					}
				} else {//solid
					try{
						/**************Filler planned for X batch CALC ************/
						if(!computedArgsJson.has("WW_GRK")||computedArgsJson.getString("WW_GRK").isEmpty()){
							generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on Filler planned for X formulation batch since the w/w[gr/kg] is empty",
								ActivitylogType.Calculation, fillerId);
						} else if(plannedBatchSize.isEmpty()){
							generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on Filler planned for X formulation batch since the batch size is empty",
								ActivitylogType.Calculation, fillerId);
						} else {
							StringBuilder sbInfo = new StringBuilder();
							sbInfo.append("Filler planned for X formulation batch calculation=></br>");
							sbInfo.append("Formula:filler w/w[gr/kg] * batch size.</br> Args: "+computedArgsJson.getString("WW_GRK")+"*"+plannedBatchSize);
							generalUtilLogger.logWriter(LevelType.INFO,
									sbInfo.toString() ,ActivitylogType.Calculation,originElementValueMap.get("parentId"));
							
							double plannedForBatch = Double.parseDouble(computedArgsJson.getString("WW_GRK"))*Double.parseDouble(plannedBatchSize);
							
							String onChangeColumnName = "plannedToExpBatch";
							String onChangeColumnVal = String.valueOf(plannedForBatch);
							updateEditableForm(formCode,fillerId,onChangeColumnName,onChangeColumnVal,originElementValueMap.get("parentId"),userId,mainArg);
							computedArgsJson.put(onChangeColumnName, onChangeColumnVal);
						}
					} catch(Exception e){
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on filler planned for X formulation batch",
								ActivitylogType.Calculation, fillerId);
					}
				}
			}
		return createReturnJsonArray(fillerId, new StringBuilder(computedArgsJson.toString()));
		
	}

	private void updateEditableForm(String formCode, String formId, String onChangeColumnName,
			String onChangeColumnVal,String parentId, String userId,String mainArg) {
		String sessionId = generalUtilFormState.checkAndReturnSessionId("Composition", parentId);
		String sql = "";
		String update = "";
		sql = "update FG_S_" + formCode + "_PIVOT " + " set "+ onChangeColumnName +"= '"
				+ generalUtil.getNull(onChangeColumnVal).replaceAll("'","''") + "'" 
				+(generalUtil.getNull(mainArg).isEmpty()?"":", mainArg = '"+mainArg+"'")
				+ " where formId = '" + formId + "' "
				+ ((sessionId == null)?"and sessionid is null":"and sessionid='" + sessionId + "'") + " and active=1";
		update = formSaveDao.updateSingleStringInfoNoTryCatch(sql);
		if(update.equals("0") && sessionId != null){ //if there is no record in the current session then insert one, if and only if the session is temporary.
													//In case the session is null and updating the record was not succeeded-> then this is an error.No insert needed.
			String colList = generalDao.getTableColCsv("FG_S_" + formCode + "_PIVOT");						
			String valList = "," +colList+",";
			valList = valList.replace(",CHANGE_BY,", ","+userId+",")
					.replace(",TIMESTAMP,", ",sysdate,").replace(",CREATION_DATE,", ",sysdate,")
					.replace(",CREATED_BY,", ","+userId+",").replace(",SESSIONID,", ","+sessionId+",")
					.replace(","+onChangeColumnName.toUpperCase()+",", ",'"+ generalUtil.getNull(onChangeColumnVal).replaceAll("'","''")+"',");
			if(!generalUtil.getNull(mainArg).isEmpty())	{
				valList.replace(",MAINARG,", ","+ mainArg +",");
			}
			valList = valList.substring(1, valList.length()-1);
			sql = String.format(
					"insert into FG_S_" + formCode + "_PIVOT (%1$s) select %2$s from FG_S_%3$s_PIVOT t where formid = %4$s and sessionid is null and active=1",
					colList, valList, formCode, formId);
			update = formSaveDao.updateSingleStringInfoNoTryCatch(sql);
		}
		
	}

	@Override
	public String doCalcUI(String api, String eventActin, String mainArgCode, String mainArgVal,
			Map<String, String> originElementValueMap, String[] apiCodesArray, String[] elementsMatchArray,
			String formCode, String formId, String userId, JSONObject elementValueJson) {
		JSONObject toReturn = new JSONObject();

		//evaluating isLimited value
		Map<String, String> elementValueMap = new HashMap<>();
		String stepStatusName = formDao.getFromInfoLookup("StepStatus", LookupType.ID,
				originElementValueMap.get("STATUS_ID"), "name");
		elementValueMap.put("stepStatusName", stepStatusName);
		String isLimited = "0";
		String limitingAgentMole = null;
		String limitingMoleUomId = null;
		String limitingAgentMoleRate = null;
		String limitingMoleRateUomId = null;
		Iterator<String> keysItr = elementValueJson.keys();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			String id = key;
			if (id.equals(formId)) {
				continue;
			}
			JSONObject rowData = elementValueJson.getJSONObject(key);
			if (generalUtil.getJsonValById(rowData.toString(), "limitingAgent").equals("1")) {
				isLimited = "1";
				limitingAgentMole = generalUtil.getJsonValById(rowData.toString(), "mole");
				limitingMoleUomId = generalUtil.getJsonValById(rowData.toString(), "MOLEUOM_ID");
				limitingAgentMoleRate = generalUtil.getJsonValById(rowData.toString(), "moleRate");
				limitingMoleRateUomId = generalUtil.getJsonValById(rowData.toString(), "MOLERATEUOM_ID");
				break;
			}
		}
		elementValueMap.put("isLimited", isLimited);
		if (limitingAgentMole != null) {
			elementValueMap.put("limitingAgentMole", limitingAgentMole);
			elementValueMap.put("limitingAgentMoleUomId", limitingMoleUomId);
			elementValueMap.put("limitingAgentMoleRate", limitingAgentMoleRate);
			elementValueMap.put("limitingMoleRateUomId", limitingMoleRateUomId);
		}

		//building the map of the row
		JSONObject rowData = elementValueJson.getJSONObject(formId);
		elementValueMap.put("formId", formId);
		keysItr = rowData.keys();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			elementValueMap.put(key, generalUtil.getJsonValById(rowData.toString(), key));
		}

		StringBuilder sb = new StringBuilder();
		if (eventActin.equals("calcLimitingAgentOnChange") || eventActin.equals("calcMoleOnChange")) {//limiting agent row
			String preparation_run = formDao.getFromInfoLookup("Step", LookupType.ID,
					originElementValueMap.get("parentId"), "preparation_run");
			if(generalUtil.getNull(preparation_run).equals("Run")){
				calcLimitingAgentSiblingsByUI_Rate(elementValueMap, formId, elementValueJson);
				JSONObject toReturnGeneralCalc = RatioTotalRateCalcByUI(elementValueMap, elementValueJson, formId);
				mergeActualValuesAndElementValueJson(elementValueJson, toReturnGeneralCalc);
			}else{
				calcLimitingAgentSiblingsByUI(elementValueMap, formId, elementValueJson);
				JSONObject toReturnGeneralCalc = RatioTotalCalcByUI(elementValueMap, elementValueJson, formId);
				mergeActualValuesAndElementValueJson(elementValueJson, toReturnGeneralCalc);
			}
			toReturn = elementValueJson;
		} else if (eventActin.equals("calcQuantityOnChange") || eventActin.equals("calcVolumeOnChange")) {//TODO: ask Irit when this should happen. mole has to be defined
			String preparation_run = formDao.getFromInfoLookup("Step", LookupType.ID,
					originElementValueMap.get("parentId"), "preparation_run");
			if(generalUtil.getNull(preparation_run).equals("Run")){
				toReturn = RatioTotalRateCalcByUI(elementValueMap, elementValueJson, formId);
			}else{
			toReturn = RatioTotalCalcByUI(elementValueMap, elementValueJson, formId);
			}
		}else if(eventActin.equals("calcReactionMassOnChange")){
			String preparation_run = formDao.getFromInfoLookup("Step", LookupType.ID,
					originElementValueMap.get("parentId"), "preparation_run");
			if(generalUtil.getNull(preparation_run).equals("Run")){
				elementValueMap.put("isRun","1");
			}
			calcProductConcentration(elementValueMap,formId, elementValueJson,"");
			JSONObject json = new JSONObject();
			json.put("concInReactionMass", elementValueMap.get("concInReactionMass"));
			json.put("concentrationMole", elementValueMap.get("concentrationMole"));
			sb = new StringBuilder(json.toString());
			toReturn = createReturnJsonArray(formId, sb);
		}else if (elementValueMap.get("tableType").equals("Reactant") && mainArgCode.equalsIgnoreCase("Quantity")) {
			sb = materialReactantQuantityCalc(elementValueMap, mainArgVal, formId);//TODO:add a call to the RatioTotalCalcCalc
			toReturn = createReturnJsonArray(formId, sb);
			mergeActualValuesAndElementValueJson(elementValueJson, toReturn);
			JSONObject toReturnGeneralCalc = RatioTotalCalcByUI(elementValueMap, elementValueJson, formId);

			if (elementValueMap.get("limitingAgent").equals("1")) {
				mergeActualValuesAndElementValueJson(elementValueJson, toReturnGeneralCalc);
				calcLimitingAgentSiblingsByUI(elementValueMap, formId, elementValueJson);
				toReturn = elementValueJson;
			} else {
				mergeActualValuesAndElementValueJson(toReturn, toReturnGeneralCalc);
			}
		}else if (elementValueMap.get("tableType").equals("Reactant") && mainArgCode.equalsIgnoreCase("QuantityRate")) {
			sb = materialReactantQuantityRateCalc(elementValueMap, mainArgVal, formId);//TODO:add a call to the RatioTotalCalcCalc
			toReturn = createReturnJsonArray(formId, sb);
			mergeActualValuesAndElementValueJson(elementValueJson, toReturn);
			JSONObject toReturnGeneralCalc = RatioTotalRateCalcByUI(elementValueMap, elementValueJson, formId);
            ///done
			if (elementValueMap.get("limitingAgent").equals("1")) {
				mergeActualValuesAndElementValueJson(elementValueJson, toReturnGeneralCalc);
				calcLimitingAgentSiblingsByUI_Rate(elementValueMap, formId, elementValueJson);
				toReturn = elementValueJson;
			} else {
				mergeActualValuesAndElementValueJson(toReturn, toReturnGeneralCalc);
			}
		} else if (elementValueMap.get("tableType").equals("Solvent") && mainArgCode.equalsIgnoreCase("Quantity")) {
			updateSolvenValuesMapByConnectedReactantValues (elementValueJson, elementValueMap, formId);
			sb = materialSolventQuantityCalc(elementValueMap, mainArgVal, formId);
			toReturn = createReturnJsonArray(formId, sb);
			mergeActualValuesAndElementValueJson(elementValueJson, toReturn);
			JSONObject toReturnGeneralCalc = RatioTotalCalcByUI(elementValueMap, elementValueJson, formId);
			mergeActualValuesAndElementValueJson(toReturn, toReturnGeneralCalc);
		}else if (elementValueMap.get("tableType").equals("Solvent") && mainArgCode.equalsIgnoreCase("QuantityRate")) {
			updateSolvenValuesMapByConnectedReactantRateValues (elementValueJson, elementValueMap, formId);
			sb = materialSolventQuantityRateCalc(elementValueMap, mainArgVal, formId);
			toReturn = createReturnJsonArray(formId, sb);
			mergeActualValuesAndElementValueJson(elementValueJson, toReturn);
			JSONObject toReturnGeneralCalc = RatioTotalCalcByUI(elementValueMap, elementValueJson, formId);
			mergeActualValuesAndElementValueJson(toReturn, toReturnGeneralCalc);
		} else if (elementValueMap.get("tableType").equals("Reactant") && mainArgCode.equalsIgnoreCase("Mole")) {
			sb = materialReactanttMoleCalc(elementValueMap, mainArgVal, formId);
			toReturn = createReturnJsonArray(formId, sb);
			mergeActualValuesAndElementValueJson(elementValueJson, toReturn);
			JSONObject toReturnGeneralCalc = RatioTotalCalcByUI(elementValueMap, elementValueJson, formId);
			if (elementValueMap.get("limitingAgent").equals("1")) {
				mergeActualValuesAndElementValueJson(elementValueJson, toReturnGeneralCalc);
				calcLimitingAgentSiblingsByUI(elementValueMap, formId, elementValueJson); 
				toReturn = elementValueJson;
			} else {
				mergeActualValuesAndElementValueJson(toReturn, toReturnGeneralCalc);
			}
		} else if (elementValueMap.get("tableType").equals("Reactant") && mainArgCode.equalsIgnoreCase("MoleRate")) {
			sb = materialReactantMoleRateCalc(elementValueMap, mainArgVal, formId);
			toReturn = createReturnJsonArray(formId, sb);
			mergeActualValuesAndElementValueJson(elementValueJson, toReturn);
			JSONObject toReturnGeneralCalc = RatioTotalCalcByUI(elementValueMap, elementValueJson, formId);
			if (elementValueMap.get("limitingAgent").equals("1")) {
				mergeActualValuesAndElementValueJson(elementValueJson, toReturnGeneralCalc);
				calcLimitingAgentSiblingsByUI_Rate(elementValueMap, formId, elementValueJson);
				toReturn = elementValueJson;
			} else {
				mergeActualValuesAndElementValueJson(toReturn, toReturnGeneralCalc);
			}
		} else if (elementValueMap.get("tableType").equals("Solvent") && mainArgCode.equalsIgnoreCase("Mole")) {
			updateSolvenValuesMapByConnectedReactantValues (elementValueJson, elementValueMap, formId);
			sb = materialSolventMoleCalc(elementValueMap, mainArgVal, formId);
			toReturn = createReturnJsonArray(formId, sb);
			mergeActualValuesAndElementValueJson(elementValueJson, toReturn);
			JSONObject toReturnGeneralCalc = RatioTotalCalcByUI(elementValueMap, elementValueJson, formId);
			mergeActualValuesAndElementValueJson(toReturn, toReturnGeneralCalc);
		} else if (elementValueMap.get("tableType").equals("Solvent") && mainArgCode.equalsIgnoreCase("MoleRate")) {
			updateSolvenValuesMapByConnectedReactantRateValues (elementValueJson, elementValueMap, formId);
			sb = materialSolventMoleRateCalc(elementValueMap, mainArgVal, formId);
			toReturn = createReturnJsonArray(formId, sb);
			mergeActualValuesAndElementValueJson(elementValueJson, toReturn);
			JSONObject toReturnGeneralCalc = RatioTotalCalcByUI(elementValueMap, elementValueJson, formId);
			mergeActualValuesAndElementValueJson(toReturn, toReturnGeneralCalc);
		} else if (elementValueMap.get("tableType").equals("Reactant") && mainArgCode.equalsIgnoreCase("Volume")) {
			sb = materialReactantVolumeCalc(elementValueMap, mainArgVal, formId);
			toReturn = createReturnJsonArray(formId, sb);
			mergeActualValuesAndElementValueJson(elementValueJson, toReturn);
			JSONObject toReturnGeneralCalc = RatioTotalCalcByUI(elementValueMap, elementValueJson, formId);
			if (elementValueMap.get("limitingAgent").equals("1")) {
				mergeActualValuesAndElementValueJson(elementValueJson, toReturnGeneralCalc);
				calcLimitingAgentSiblingsByUI(elementValueMap, formId, elementValueJson);
				toReturn = elementValueJson;
			} else {
				mergeActualValuesAndElementValueJson(toReturn, toReturnGeneralCalc);
			}
		}  else if (elementValueMap.get("tableType").equals("Reactant") && mainArgCode.equalsIgnoreCase("VolumeRate")) {
			sb = materialReactantVolumeRateCalc(elementValueMap, mainArgVal, formId);
			toReturn = createReturnJsonArray(formId, sb);
			mergeActualValuesAndElementValueJson(elementValueJson, toReturn);
			JSONObject toReturnGeneralCalc = RatioTotalCalcByUI(elementValueMap, elementValueJson, formId);
			if (elementValueMap.get("limitingAgent").equals("1")) {
				mergeActualValuesAndElementValueJson(elementValueJson, toReturnGeneralCalc);
				calcLimitingAgentSiblingsByUI_Rate(elementValueMap, formId, elementValueJson);
				toReturn = elementValueJson;
			} else {
				mergeActualValuesAndElementValueJson(toReturn, toReturnGeneralCalc);
			}
		} else if (elementValueMap.get("tableType").equals("Solvent") && mainArgCode.equalsIgnoreCase("Volume")) {
			updateSolvenValuesMapByConnectedReactantValues (elementValueJson, elementValueMap, formId);
			sb = materialSolventVolumeCalc(elementValueMap, mainArgVal, formId);
			toReturn = createReturnJsonArray(formId, sb);
			mergeActualValuesAndElementValueJson(elementValueJson, toReturn);
			JSONObject toReturnGeneralCalc = RatioTotalCalcByUI(elementValueMap, elementValueJson, formId);
			mergeActualValuesAndElementValueJson(toReturn, toReturnGeneralCalc);
		} else if (elementValueMap.get("tableType").equals("Solvent") && mainArgCode.equalsIgnoreCase("VolumeRate")) {
			updateSolvenValuesMapByConnectedReactantRateValues (elementValueJson, elementValueMap, formId);
			sb = materialSolventVolumeRateCalc(elementValueMap, mainArgVal, formId);
			toReturn = createReturnJsonArray(formId, sb);
			mergeActualValuesAndElementValueJson(elementValueJson, toReturn);
			JSONObject toReturnGeneralCalc = RatioTotalCalcByUI(elementValueMap, elementValueJson, formId);
			mergeActualValuesAndElementValueJson(toReturn, toReturnGeneralCalc);
		} else if (elementValueMap.get("tableType").equals("Product")) {
			String preparation_run = formDao.getFromInfoLookup("Step", LookupType.ID,
					originElementValueMap.get("parentId"), "preparation_run");
			elementValueMap.put("parentId",originElementValueMap.get("parentId"));
			if(generalUtil.getNull(preparation_run).equals("Run")){
				elementValueMap.put("isRun","1");
			}
			sb = materialProductCalc(elementValueMap, mainArgCode, formId,elementValueJson );
			toReturn = createReturnJsonArray(formId, sb);
		} else if (elementValueMap.get("tableType").equals("Reactant")
				|| elementValueMap.get("tableType").equals("Product")) {
			sb = materialReactionsGeneralcalc(elementValueMap, mainArgCode, mainArgVal, formId);
			toReturn = createReturnJsonArray(formId, sb);
			if (elementValueMap.get("tableType").equals("Reactant")) {
				mergeActualValuesAndElementValueJson(elementValueJson, toReturn);
				JSONObject toReturnGeneralCalc = RatioTotalCalcByUI(elementValueMap, elementValueJson, formId);
				mergeActualValuesAndElementValueJson(toReturn, toReturnGeneralCalc);
			}
		} else if (elementValueMap.get("tableType").equals("Solvent") && mainArgCode.equalsIgnoreCase("Ratio")) {
			updateSolvenValuesMapByConnectedReactantValues (elementValueJson, elementValueMap, formId);
			sb = materialSolventByRatioTypeCalc(elementValueJson, elementValueMap, mainArgVal, formId);			
			toReturn = createReturnJsonArray(formId, sb);
			mergeActualValuesAndElementValueJson(elementValueJson, toReturn);
			JSONObject toReturnGeneralCalc = RatioTotalCalcByUI(elementValueMap, elementValueJson, formId);
			mergeActualValuesAndElementValueJson(toReturn, toReturnGeneralCalc);			
		} else {
			sb.append("{");
			sb.append("}");
		}
		return toReturn.toString();
	}

	private void calcLimitingAgentSiblingsByUI(Map<String, String> elementValueMap, String formId,
			JSONObject elementValueJson) {
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"calcLimitingAgentSiblings - update elementValueMap in db ," + elementValueMap.toString(),
				ActivitylogType.Calculation, formId);
		Double normalLimitingMole = getNormalNumber(elementValueMap.get("mole"), elementValueMap.get("MOLEUOM_ID"));
		StringBuilder sbInfo = new StringBuilder();
		if (normalLimitingMole == null) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed. Missing args -> mole " + elementValueMap.get("mole"),
					ActivitylogType.Calculation, formId);
		} else {

			Iterator<String> keysItr = elementValueJson.keys();
			while (keysItr.hasNext()) {
				JSONObject json = new JSONObject();
				String id = keysItr.next();
				if (id.equals(formId)) {
					continue;
				}
				JSONObject rowData = elementValueJson.getJSONObject(id);
				String tableType = generalUtil.getJsonValById(rowData.toString(), "tableType");
				if (tableType.equals("Reactant")) {
					if (elementValueMap.get("stepStatusName").equals("Active")) {
						Double normalMole = getNormalNumber(
								generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "mole")),
								generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "MOLEUOM_ID")));
						if (normalMole == null) {
							generalUtilLogger.logWriter(LevelType.WARN,
									"The equivalent calculation of sibling "
											+ generalUtil.getNull(
													generalUtil.getJsonValById(rowData.toString(), "materialref_id"))
											+ " was not performed. Missing args -> mole ",
									ActivitylogType.Calculation, formId);
						} else {
							String eq = String.valueOf(normalMole / normalLimitingMole);
							json.put("equivalent", eq);
						}
					} else {//planned status
						String equivalent = generalUtil
								.getNull(generalUtil.getJsonValById(rowData.toString(), "equivalent"));
						if (equivalent.isEmpty()) {
							generalUtilLogger.logWriter(LevelType.WARN,
									"The mole calculation of sibling "
											+ generalUtil.getNull(
													generalUtil.getJsonValById(rowData.toString(), "materialref_id"))
											+ " was not performed. Missing args -> equivalent ",
									ActivitylogType.Calculation, formId);
						} else {
							String moleSibling = getFromNormalNumber(
									String.valueOf(normalLimitingMole * Double.parseDouble(equivalent)),
									generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "MOLEUOM_ID")),
									sbInfo);
							sbInfo.append("Limiting agent Sibling Mole Calculatin =></br>");
							sbInfo.append("Formula: Limiting_agent_Mole * Equivalent.</br> Normal Args:"
									+ String.valueOf(normalLimitingMole) + "*" + equivalent + "</br>");
							json.put("mole", moleSibling);
						}
						Double normalMwSibling = getNormalNumber(
								generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "mwInf")),
								generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "MWUOM_ID_INF")));
						Double normalPuritySibling = getNormalNumber(
								generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "purityInf")),
								generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "PURITYUOM_ID_INF")),
								(double) 100);
						Double normaldensitysibling = getNormalNumber(
								generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "densityInf")),
								generalUtil
										.getNull(generalUtil.getJsonValById(rowData.toString(), "DENSITYUOM_ID_INF")));

						try {

							if (normalMwSibling == null || generalUtil.getNull(equivalent).isEmpty()) {
								generalUtilLogger.logWriter(LevelType.WARN,
										"The calculation of limiting sibling Quantity was not performed. Missing args -> MW of limiting sibling. formId="
												+ generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(),
														"materialref_id")),
										ActivitylogType.Calculation, formId);
							} else {
								sbInfo = new StringBuilder();
								sbInfo.append("Limiting agent Sibling Quantity Calculation =></br>");
								sbInfo.append(
										"Formula: Limiting_agent_Mole* MW*Equivalent/(purity/100).</br> Normal Args:"
												+ String.valueOf(normalLimitingMole) + "*" + normalMwSibling.toString()
												+ "*" + equivalent + "/(" + normalPuritySibling.toString() + "/100)"
												+ "</br>");
								String quantitySibling = getFromNormalNumber(
										String.valueOf(normalLimitingMole * normalMwSibling
												* Double.parseDouble(equivalent) / (normalPuritySibling / 100)),
										generalUtil.getNull(
												generalUtil.getJsonValById(rowData.toString(), "QUANTITYUOM_ID")),
										sbInfo);

								double normalQuantitySibling = getNormalNumber(quantitySibling, generalUtil
										.getNull(generalUtil.getJsonValById(rowData.toString(), "QUANTITYUOM_ID")));
								json.put("quantity", quantitySibling);
								try {
									sbInfo = new StringBuilder();
									if (normaldensitysibling == null || normaldensitysibling == (double) 0) {
										generalUtilLogger.logWriter(LevelType.WARN,
												"The calculation of limiting sibling Volume was not performed. Normal Args -> density of limiting sibling = "
														+ normaldensitysibling + ". formId=" + id,
												ActivitylogType.Calculation, formId);
										if(normaldensitysibling == null || normaldensitysibling == (double) 0){
											elementValueMap.put("warningMsg", "The following value is missing \""+elementValueMap.get("tableType")+" - Density\". Calculation cannot be completed.");
										}
									} else {
										sbInfo.append("Limiting agent Sibling Volume Calculation =></br>");
										sbInfo.append("Formula: Quantity/Density.</br> Normal Args:"
												+ String.valueOf(normalQuantitySibling) + "/"
												+ normaldensitysibling.toString() + "</br>");
										String volumesibling = getFromNormalNumber(
												String.valueOf(normalQuantitySibling / normaldensitysibling),
												generalUtil.getNull(
														generalUtil.getJsonValById(rowData.toString(), "VOLUOM_ID")),
												sbInfo);
										json.put("volume", volumesibling);
									}
								} catch (Exception ex) {
									generalUtilLogger.logWriter(LevelType.WARN,
											"The calculation was not performed on volume ="
													+ elementValueMap.get("volume"),
											ActivitylogType.Calculation, formId);
								}
							}
						} catch (Exception e) {
							generalUtilLogger.logWriter(LevelType.WARN,
									"The calculation was not performed on quantity =" + elementValueMap.get("volume"),
									ActivitylogType.Calculation, formId);
						}
					}
				} else {
					if (tableType.equals("Product")) {
						try {
							sbInfo = new StringBuilder();
							String moleSibling = getFromNormalNumber(normalLimitingMole.toString(),
									generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "MOLEUOM_ID")),
									sbInfo);
							sbInfo.append("Limiting agent Sibling Mole Calculatin =></br>");
							sbInfo.append("Formula: Limiting_agent_Mole.</br> Normal Args:"
									+ String.valueOf(normalLimitingMole) + "</br>");
							json.put("mole", moleSibling);

							Double normalYieldSibling =  generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "yield")).isEmpty()?null:Double.parseDouble(generalUtil.getJsonValById(rowData.toString(), "yield"));
							/*Double normalYieldSibling = getNormalNumber(
									generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "yield")),
									generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "YIELDUOM_ID")));*/
							Double normalMw = getNormalNumber(
									generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "mwInf")),
									generalUtil
											.getNull(generalUtil.getJsonValById(rowData.toString(), "MWUOM_ID_INF")));
							if (normalYieldSibling == null || normalMw == null) {
								generalUtilLogger.logWriter(LevelType.WARN,
										"The calculation of limiting sibling Mass was not performed. Missing args -> Yield or MW of limiting sibling. formId="
												+ id,
										ActivitylogType.Calculation, formId);
							} else {
								sbInfo = new StringBuilder();
								sbInfo.append("Limiting agent Sibling Mass Calculation =></br>");
								sbInfo.append("Formula: Limiting_agent_Mole*Yield*MW/100.</br> Normal Args:"
										+ String.valueOf(normalLimitingMole) + "*" + normalYieldSibling.toString()
										+ "</br>");
								String masssibling = getFromNormalNumber(
										String.valueOf(normalLimitingMole * normalYieldSibling * normalMw / 100),
										generalUtil.getNull(
												generalUtil.getJsonValById(rowData.toString(), "MASSUOM_ID")),
										sbInfo);
								json.put("mass", masssibling);
							}
						} catch (Exception e) {
							generalUtilLogger.logWriter(LevelType.WARN,
									"The calculation was not performed on mass =" + elementValueMap.get("volume"),
									ActivitylogType.Calculation, formId);
						}
					}
				}
				JSONObject current = elementValueJson.getJSONObject(id);
				Iterator<String> newkeysItr = json.keys();
				while (newkeysItr.hasNext()) {
					String key = newkeysItr.next();
					current.put(key, generalUtil.getJsonValById(json.toString(), key));
				}
			}
		}

	}
	
	private void calcLimitingAgentSiblingsByUI_Rate(Map<String, String> elementValueMap, String formId,
			JSONObject elementValueJson) {
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"calcLimitingAgentSiblings - update elementValueMap in db ," + elementValueMap.toString(),
				ActivitylogType.Calculation, formId);
		Double normalLimitingMole = getNormalNumber(elementValueMap.get("moleRate"), elementValueMap.get("MOLERATEUOM_ID"));
		StringBuilder sbInfo = new StringBuilder();
		if (normalLimitingMole == null) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed. Missing args -> mole " + elementValueMap.get("moleRate"),
					ActivitylogType.Calculation, formId);
		} else {
			Map<String, String> run_uom_default = generalDao.sqlToHashMap("select * from fg_i_calcrun_uom_override_v");
			
			String defaultMWUomId = (run_uom_default.get("DEFAULT_MW_UOM_ID") != null?run_uom_default.get("DEFAULT_MW_UOM_ID"):"").toString();	
			String defaultQuantityUomId = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();	
			String defaultdensityUomId = (run_uom_default.get("DEFAULT_DENSITY_UOM_ID") != null?run_uom_default.get("DEFAULT_DENSITY_UOM_ID"):"").toString();	
			String defaultVolumeUOMId = (run_uom_default.get("DEFAULT_VOLUME_UOM_ID") != null?run_uom_default.get("DEFAULT_VOLUME_UOM_ID"):"").toString();	
			

			Iterator<String> keysItr = elementValueJson.keys();
			while (keysItr.hasNext()) {
				JSONObject json = new JSONObject();
				String id = keysItr.next();
				if (id.equals(formId)) {
					continue;
				}
				JSONObject rowData = elementValueJson.getJSONObject(id);
				String tableType = generalUtil.getJsonValById(rowData.toString(), "tableType");
				if (tableType.equals("Reactant")) {
					if (elementValueMap.get("stepStatusName").equals("Active")) {
						Double normalMole = getNormalNumber(
								generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "moleRate")),
								generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "MOLERATEUOM_ID")));
						if (normalMole == null) {
							generalUtilLogger.logWriter(LevelType.WARN,
									"The equivalent calculation of sibling "
											+ generalUtil.getNull(
													generalUtil.getJsonValById(rowData.toString(), "materialref_id"))
											+ " was not performed. Missing args -> mole ",
									ActivitylogType.Calculation, formId);
						} else {
							String eq = String.valueOf(normalMole / normalLimitingMole);
							json.put("equivalent", eq);
						}
					} else {//planned status
						String equivalent = generalUtil
								.getNull(generalUtil.getJsonValById(rowData.toString(), "equivalent"));
						if (equivalent.isEmpty()) {
							generalUtilLogger.logWriter(LevelType.WARN,
									"The mole calculation of sibling "
											+ generalUtil.getNull(
													generalUtil.getJsonValById(rowData.toString(), "materialref_id"))
											+ " was not performed. Missing args -> equivalent ",
									ActivitylogType.Calculation, formId);
						} else {
							String moleSibling = getFromNormalNumber(
									String.valueOf(normalLimitingMole * Double.parseDouble(equivalent)),
									generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "MOLERATEUOM_ID")),
									sbInfo);
							sbInfo.append("Limiting agent Sibling Mole Calculatin =></br>");
							sbInfo.append("Formula: Limiting_agent_Mole * Equivalent.</br> Normal Args:"
									+ String.valueOf(normalLimitingMole) + "*" + equivalent + "</br>");
							json.put("moleRate", moleSibling);
						}
						Double normalMwSibling = getCustomNormalNumber(
								generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "mwInf")),
								generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "MWUOM_ID_INF")),defaultMWUomId,new StringBuilder());
						Double normalPuritySibling = getNormalNumber(
								generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "purityInf")),
								generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "PURITYUOM_ID_INF")),
								(double) 100);
						Double normaldensitysibling = getCustomNormalNumber(
								generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(), "densityInf")),
								generalUtil
										.getNull(generalUtil.getJsonValById(rowData.toString(), "DENSITYUOM_ID_INF")),defaultdensityUomId,new StringBuilder());

						try {

							if (normalMwSibling == null || generalUtil.getNull(equivalent).isEmpty()) {
								generalUtilLogger.logWriter(LevelType.WARN,
										"The calculation of limiting sibling Quantity was not performed. Missing args -> MW of limiting sibling. formId="
												+ generalUtil.getNull(generalUtil.getJsonValById(rowData.toString(),
														"materialref_id")),
										ActivitylogType.Calculation, formId);
							} else {
								sbInfo = new StringBuilder();
								sbInfo.append("Limiting agent Sibling Quantity Calculation =></br>");
								sbInfo.append(
										"Formula: Limiting_agent_Mole* MW*Equivalent/(purity/100).</br> Normal Args:"
												+ String.valueOf(normalLimitingMole) + "*" + normalMwSibling.toString()
												+ "*" + equivalent + "/(" + normalPuritySibling.toString() + "/100)"
												+ "</br>");
								String quantitySibling = getCustomNormalNumber(
										String.valueOf(normalLimitingMole * normalMwSibling
												* Double.parseDouble(equivalent) / (normalPuritySibling / 100)),defaultQuantityUomId,
										generalUtil.getNull(
												generalUtil.getJsonValById(rowData.toString(), "QUANTITYRATE_UOM")),
										sbInfo).toString();

								double normalQuantitySibling = getCustomNormalNumber(quantitySibling, generalUtil
										.getNull(generalUtil.getJsonValById(rowData.toString(), "QUANTITYRATE_UOM")),defaultQuantityUomId,sbInfo);
								json.put("quantityRate", quantitySibling);
								try {
									sbInfo = new StringBuilder();
									if (normaldensitysibling == null || normaldensitysibling == (double) 0) {
										generalUtilLogger.logWriter(LevelType.WARN,
												"The calculation of limiting sibling Volume was not performed. Normal Args -> density of limiting sibling = "
														+ normaldensitysibling + ". formId=" + id,
												ActivitylogType.Calculation, formId);
										if(normaldensitysibling == null || normaldensitysibling == (double) 0){
											elementValueMap.put("warningMsg", "The following value is missing \""+elementValueMap.get("tableType")+" - Density\". Calculation cannot be completed.");
										}
									} else {
										sbInfo.append("Limiting agent Sibling Volume Calculation =></br>");
										sbInfo.append("Formula: Quantity/Density.</br> Normal Args:"
												+ String.valueOf(normalQuantitySibling) + "/"
												+ normaldensitysibling.toString() + "</br>");
										String volumesibling = getCustomNormalNumber(
												String.valueOf(normalQuantitySibling / normaldensitysibling),
												generalUtil.getNull(
														generalUtil.getJsonValById(rowData.toString(), "VOLRATEUOM_ID")),defaultVolumeUOMId,
												sbInfo).toString();
										json.put("volumeRate", volumesibling);
									}
								} catch (Exception ex) {
									generalUtilLogger.logWriter(LevelType.WARN,
											"The calculation was not performed on volume rate ="
													+ elementValueMap.get("volumeRate"),
											ActivitylogType.Calculation, formId);
								}
							}
						} catch (Exception e) {
							generalUtilLogger.logWriter(LevelType.WARN,
									"The calculation was not performed on quantity =" + elementValueMap.get("volume"),
									ActivitylogType.Calculation, formId);
						}
					}
				} 
				JSONObject current = elementValueJson.getJSONObject(id);
				Iterator<String> newkeysItr = json.keys();
				while (newkeysItr.hasNext()) {
					String key = newkeysItr.next();
					current.put(key, generalUtil.getJsonValById(json.toString(), key));
				}
			}
		}

	}

	private void mergeActualValuesAndElementValueJson(JSONObject elementValueJson, JSONObject actualData) {
		JSONObject json = null;
		boolean isKeyFound = false;
		Iterator<String> keysItr = elementValueJson.keys();
		while (keysItr.hasNext()) {
			String id = keysItr.next();
			if (actualData.isNull(id)) {
				continue;
			}
			isKeyFound = true;
			JSONObject rowActualData = actualData.getJSONObject(id);
			Iterator<String> rowActkeysItr = rowActualData.keys();
			while (rowActkeysItr.hasNext()) {
				String key = rowActkeysItr.next();
				String columnName = key;
				String columnVal = generalUtil.getJsonValById(rowActualData.toString(), key);
				json = elementValueJson.getJSONObject(id);
				json.put(columnName, columnVal);
			}
			if (json != null) {
				elementValueJson.put(id, json);
			}
		}
		if(!isKeyFound){
			keysItr = actualData.keys();
			while (keysItr.hasNext()) {
				String key = keysItr.next();
				String columnVal = generalUtil.getJsonValById(actualData.toString(), key);
				elementValueJson.put(key, new JSONObject(columnVal));
			}
		}
	}

	private JSONObject RatioTotalCalcByUI(Map<String, String> elementValueMap, JSONObject elementValueJson,
			String formId) {
		JSONObject toReturn = new JSONObject();
		if (elementValueMap.get("tableType").equals("Solvent") || elementValueMap.get("tableType").equals("Reactant")) {

			Double totalQuantity = 0.0;
			Double totalVolume = 0.0;
			Iterator<String> keysItr = elementValueJson.keys();
			while (keysItr.hasNext()) {
				String id = keysItr.next();
				JSONObject rowData = elementValueJson.getJSONObject(id);
				Double rowQuantity = 0.0;
				String rowQuantityUOM = "";
				Double rowVolume = 0.0;
				String rowVolumeUOM = "";
				if (elementValueMap.containsKey("quantityRate")) {
					rowQuantity = Double.parseDouble(
							generalUtil.getEmpty(generalUtil.getJsonValById(rowData.toString(), "quantityRate"), "0.0"));
					rowVolume = Double.parseDouble(
							generalUtil.getEmpty(generalUtil.getJsonValById(rowData.toString(), "volumeRate"), "0.0"));
					rowQuantityUOM = generalUtil
							.getEmpty(generalUtil.getJsonValById(rowData.toString(), "QUANTITYRATE_UOM"), "");
					rowVolumeUOM = generalUtil.getEmpty(generalUtil.getJsonValById(rowData.toString(), "VOLRATEUOM_ID"),
							"");

				}
				else{
					rowQuantity = Double.parseDouble(
						generalUtil.getEmpty(generalUtil.getJsonValById(rowData.toString(), "quantity"), "0.0"));
				    rowVolume = Double.parseDouble(
						generalUtil.getEmpty(generalUtil.getJsonValById(rowData.toString(), "volume"), "0.0"));
				    rowQuantityUOM = generalUtil.getEmpty(generalUtil.getJsonValById(rowData.toString(), "QUANTITYUOM_ID"),
						"");
				    rowVolumeUOM = generalUtil.getEmpty(generalUtil.getJsonValById(rowData.toString(), "VOLUOM_ID"), "");
				}
				totalQuantity += getNormalNumber(rowQuantity.toString(), rowQuantityUOM);
				totalVolume += getNormalNumber(rowVolume.toString(), rowVolumeUOM);
			}

			toReturn = RatioTotalCalc(elementValueMap, formId, totalQuantity.toString(), totalVolume.toString(),
					elementValueJson);
		}

		return toReturn;
	}
	
	private JSONObject RatioTotalRateCalcByUI(Map<String, String> elementValueMap, JSONObject elementValueJson,
			String formId) {
		JSONObject toReturn = new JSONObject();
		if (elementValueMap.get("tableType").equals("Solvent") || elementValueMap.get("tableType").equals("Reactant")) {

			Map<String, String> run_uom_default = generalDao.sqlToHashMap("select * from fg_i_calcrun_uom_override_v");
			
			String defaultQuantityUomId = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();	
			String defaultVolumeUOMId = (run_uom_default.get("DEFAULT_VOLUME_UOM_ID") != null?run_uom_default.get("DEFAULT_VOLUME_UOM_ID"):"").toString();	
			
			Double totalQuantity = 0.0;
			Double totalVolume = 0.0;
			Iterator<String> keysItr = elementValueJson.keys();
			while (keysItr.hasNext()) {
				String id = keysItr.next();
				JSONObject rowData = elementValueJson.getJSONObject(id);
				Double rowQuantity = 0.0;
				String rowQuantityUOM = "";
				Double rowVolume = 0.0;
				String rowVolumeUOM = "";
				rowQuantity = Double.parseDouble(
						generalUtil.getEmpty(generalUtil.getJsonValById(rowData.toString(), "quantityRate"), "0.0"));
				rowVolume = Double.parseDouble(
						generalUtil.getEmpty(generalUtil.getJsonValById(rowData.toString(), "volumeRate"), "0.0"));
				rowQuantityUOM = generalUtil
						.getEmpty(generalUtil.getJsonValById(rowData.toString(), "QUANTITYRATE_UOM"), "");
				rowVolumeUOM = generalUtil.getEmpty(generalUtil.getJsonValById(rowData.toString(), "VOLRATEUOM_ID"),
						"");

				
				totalQuantity += getCustomNormalNumber(rowQuantity.toString(), rowQuantityUOM,defaultQuantityUomId,new StringBuilder());
				totalVolume += getCustomNormalNumber(rowVolume.toString(), rowVolumeUOM,defaultVolumeUOMId,new StringBuilder());
			}

			toReturn = RatioTotalRateCalc(elementValueMap, formId, totalQuantity.toString(), totalVolume.toString(),
					elementValueJson);
		}

		return toReturn;
	}
	
	

	private JSONObject createReturnJsonArray(String formId, StringBuilder sb) {
		JSONObject sbJson = new JSONObject(sb);
		sbJson.put(formId, new JSONObject(sb.toString()));
		return sbJson;
	}
	
	private void calcProductConcentration(Map<String, String> elementValueMap, String formId, JSONObject elementValueJson,String mainArgCode) {
		String assayRes = "";
		if(!generalUtil.getNull(elementValueMap.get("SAMPLE_ID")).isEmpty()){//fixed bug 8043
			String sql ="select t.RESULT_VALUE from FG_I_SELECTEDRESULTS_V t where t.SAMPLE_ID = "+generalUtil.getNull(elementValueMap.get("SAMPLE_ID"))+" and t.RESULT_NAME = 'Assay' and t.RESULT_MATERIAL_ID ="+elementValueMap.get("INVITEMMATERIAL_ID");
			assayRes = generalUtil.getNull(generalDao.selectSingleStringNoException(sql));			
		}
		if(generalUtil.getNull(elementValueMap.get("SAMPLE_ID")).isEmpty() || assayRes.isEmpty()){

			String reagentsMaterialref = "";
			if(elementValueJson != null){
			Iterator<String> keysItr = elementValueJson.keys();
			while (keysItr.hasNext()) {
				String id = keysItr.next();
				JSONObject rowData = elementValueJson.getJSONObject(id);
				String tableType = generalUtil.getJsonValById(rowData.toString(), "tableType");
				String materialId = generalUtil.getJsonValById(rowData.toString(), "INVITEMMATERIAL_ID");
				if(tableType.equals("Reactant") && materialId.equals(elementValueMap.get("INVITEMMATERIAL_ID"))){
					reagentsMaterialref = id;
					continue;
				}
			}	
			}else{
				String sql = "select t.materialref_id from fg_s_materialref_v t where t.PARENTID = '"+elementValueMap.get("parentId")+"' and t.INVITEMMATERIAL_ID = '"+elementValueMap.get("INVITEMMATERIAL_ID")+"' and t.TABLETYPE = 'Reactant' and rownum<=1 " + generalUtilFormState
					.getWherePartForTmpData("materialref", elementValueMap.get("parentId"));
				reagentsMaterialref = generalDao.selectSingleStringNoException(sql);
			}
			reagentsMaterialref = generalUtil.getNull(reagentsMaterialref);
			StringBuilder sbInfo = new StringBuilder();
			
	        Map<String, String> run_uom_default = generalDao.sqlToHashMap("select * from fg_i_calcrun_uom_override_v");
	
	        String defaultMWUomId_runStep = (run_uom_default.get("DEFAULT_MW_UOM_ID") != null?run_uom_default.get("DEFAULT_MW_UOM_ID"):"").toString();	
	        String defaultQuantityUomId = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();	
	        
			String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
					elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
			String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
					elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
			generalUtilLogger.logWriter(
					LevelType.DEBUG, "calcLimitingAgentProduct - elementValueMap before calulation= mass:"
							+ elementValueMap.get("mass") + ",yield:" + elementValueMap.get("yield"),
					ActivitylogType.Calculation, formId);
			Double normalMw = 0.0;
			Double normalLimitinigMole = null;
			String totalQuantity = "";
			Double normalMole = null;
			Double normalYield =  generalUtil.getNull(elementValueMap.get("yield")).isEmpty()?null:Double.parseDouble(elementValueMap.get("yield"));
			if(generalUtil.getNull(elementValueMap.get("isRun")).equals("1")){
				normalMw = getCustomNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
						generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId),defaultMWUomId_runStep,new StringBuilder());
				String limitingMoleStr ="";
				if(!reagentsMaterialref.isEmpty()){
					String reactantMole = "";
					if (elementValueJson != null) {
						JSONObject rowData = elementValueJson.getJSONObject(reagentsMaterialref);
						reactantMole = generalUtil.getJsonValById(rowData.toString(), "moleRate");
						String reactantMoleUom = generalUtil.getJsonValById(rowData.toString(), "MOLERATEUOM_ID");
						normalLimitinigMole = getNormalNumber(reactantMole,reactantMoleUom);
					}
					limitingMoleStr = generalUtil.getEmpty(generalUtil.getNull(reactantMole),generalDao.selectSingleStringNoException("select fg_get_num_normal(molerate,MOLERATEUOM_ID) from fg_s_materialref_v t where t.PARENTID = '"
							+ elementValueMap.get("parentId") + "' and t.MATERIALREF_ID =  '"+ reagentsMaterialref +"' "+ generalUtilFormState
									.getWherePartForTmpData("materialref", elementValueMap.get("parentId"))));
					if (!generalUtil.getNull(limitingMoleStr).isEmpty() && generalUtil.getNull(reactantMole).isEmpty()) {
						normalLimitinigMole = Double.parseDouble(limitingMoleStr);}
				} else {
					if (elementValueMap.containsKey("limitingAgentMoleRate")) {
						normalLimitinigMole = getNormalNumber(elementValueMap.get("limitingAgentMoleRate"),
								elementValueMap.get("limitingAgentMolerateUomId"));
					} else {
						limitingMoleStr = generalDao.selectSingleStringNoException(
										"select fg_get_num_normal(molerate,MOLERATEUOM_ID) from fg_s_materialref_v t where t.PARENTID = '"
												+ elementValueMap.get("parentId") + "' and t.LIMITINGAGENT = 1 "
												+ generalUtilFormState.getWherePartForTmpData("materialref",
														elementValueMap.get("parentId")));
						if (!generalUtil.getNull(limitingMoleStr).isEmpty()) {
							normalLimitinigMole = Double.parseDouble(limitingMoleStr);
						}
					}
				}
				
				  totalQuantity = generalDao.selectSingleStringNoException(
							"select distinct nvl(sum(fg_get_num_normal(t.QUANTITYRATE,t.QUANTITYRATE_UOM)) over (partition by t.PARENTID),0)"
									+ " from fg_s_materialref_v t where parentId = '" + elementValueMap.get("parentId") + "'"
									+ generalUtilFormState.getWherePartForTmpData("MaterialRef",
											elementValueMap.get("parentId")));
				  if(!generalUtil.getNull(totalQuantity).isEmpty()){
					  totalQuantity = getFromNormalNumber(totalQuantity,defaultQuantityUomId,sbInfo);
				  }
				// Mole of product = Mole of limiting agent*yield/100%
					if(normalLimitinigMole!=null && normalYield!=null){
						normalMole = getNormalNumber(String.valueOf(normalLimitinigMole * normalYield / 100),
								elementValueMap.get("MOLERATEUOM_ID"));
					}
			}
			else{
				normalMw = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
						generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
				
				String limitingMoleStr ="";
				if(!reagentsMaterialref.isEmpty()){
					String reactantMole = "";
					if (elementValueJson != null) {
						JSONObject rowData = elementValueJson.getJSONObject(reagentsMaterialref);
						reactantMole = generalUtil.getJsonValById(rowData.toString(), "mole");
						String reactantMoleUom = generalUtil.getJsonValById(rowData.toString(), "MOLEUOM_ID");
						normalLimitinigMole = getNormalNumber(reactantMole,reactantMoleUom);
					}
					limitingMoleStr = generalUtil.getEmpty(generalUtil.getNull(reactantMole),generalDao.selectSingleStringNoException("select fg_get_num_normal(mole,MOLEUOM_ID) from fg_s_materialref_v t where t.PARENTID = '"
							+ elementValueMap.get("parentId") + "' and t.MATERIALREF_ID =  '"+ reagentsMaterialref +"' "+ generalUtilFormState
									.getWherePartForTmpData("materialref", elementValueMap.get("parentId"))));
					if (!generalUtil.getNull(limitingMoleStr).isEmpty() && generalUtil.getNull(reactantMole).isEmpty()) {
						normalLimitinigMole = Double.parseDouble(limitingMoleStr);
					}
				}else{
					if (elementValueMap.containsKey("limitingAgentMole")) {
						normalLimitinigMole = getNormalNumber(elementValueMap.get("limitingAgentMole"),
								elementValueMap.get("limitingAgentMoleUomId"));
					} else {
						limitingMoleStr = generalDao.selectSingleStringNoException("select fg_get_num_normal(mole,MOLEUOM_ID) from fg_s_materialref_v t where t.PARENTID = '"
										+ elementValueMap.get("parentId") + "' and t.LIMITINGAGENT = 1 " + generalUtilFormState
												.getWherePartForTmpData("materialref", elementValueMap.get("parentId")));
						if (!generalUtil.getNull(limitingMoleStr).isEmpty()) {
							Double.parseDouble(limitingMoleStr);
						}
					}
				}
			  
			
			   String gramsUOM = formDao.getFromInfoLookup("UOM", LookupType.NAME,
						"gr", "id");
			   totalQuantity = generalDao.selectSingleStringNoException(
						"select distinct nvl(sum(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) over (partition by t.PARENTID),0)"
								+ " from fg_s_materialref_v t where parentId = '" + elementValueMap.get("parentId") + "'"
								+ generalUtilFormState.getWherePartForTmpData("MaterialRef",
				
										elementValueMap.get("parentId")));
			   String expFormCode = formDao.getFromInfoLookup("STEP", LookupType.ID,
						elementValueMap.get("parentId"), "expFormCode");
				if(generalUtil.getNull(expFormCode).equals("ExperimentCP")){//preparation step
					normalMw = getCustomNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
							generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId),defaultMWUomId_runStep,new StringBuilder());
					if(!generalUtil.getNull(totalQuantity).isEmpty()){
						   totalQuantity = getFromNormalNumber(totalQuantity,gramsUOM,sbInfo);
						  }
				}
			// Mole of product = Mole of limiting agent*yield/100%
				if(normalLimitinigMole!=null && normalYield!=null){
					normalMole = getNormalNumber(String.valueOf(normalLimitinigMole * normalYield / 100),
							elementValueMap.get("MOLEUOM_ID"));
				}
			}
			Double normalMass = generalUtil.getNull(totalQuantity).isEmpty()?null:Double.parseDouble(totalQuantity);
			//getNormalNumber(elementValueMap.get("yield"), elementValueMap.get("YIELDUOM_ID"));
			
			
			
			// calc ConcInReactionMass
			try {
				if(!mainArgCode.equals("concInReactionMass")){
				sbInfo = new StringBuilder();
				sbInfo.append("Product Conc in reaction mass Calculation =></br>");
				// Conc. in reaction mass % = Mole of product*MW/Mass of product*100%
				if (normalMass == null || normalLimitinigMole == null || normalMass == 0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on Conc in reaction mass. Parameters: Mass ='"
									+ elementValueMap.get("mass") + "';Mw = '"
									+ generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw) + "';Mole = '"
									+ normalMole + "'",
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: Mole of product*MW/Mass of product*100%.</br> Normal Args:"
							+ String.valueOf(normalMole) + "*" + normalMw.toString() + "/" + normalMass.toString() + "*100"
							+ "</br>");
					elementValueMap.put("concInReactionMass",String.valueOf(normalMole * normalMw / normalMass * 100));
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation,elementValueMap.get("parentId"));
				}
				
			}} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN, "The calculation was not performed on concInReactionMass ="
						+ elementValueMap.get("concInReactionMass"), ActivitylogType.Calculation, formId);
			}
			//Continuous Experiment 
			try{
				String expFormCode = formDao.getFromInfoLookup("STEP", LookupType.ID,
						elementValueMap.get("parentId"), "expFormCode");
				if(generalUtil.getNull(expFormCode).equals("ExperimentCP")){
					
				sbInfo = new StringBuilder();
				sbInfo.append("Product concentration Mole Calculation =></br>");
				/*Double normalDensity = getNormalNumber(elementValueMap.get("productDensity"),
				  elementValueMap.get("DENSITYUOM_ID_INF")); density uom?*/
				Double normalDensity = null;
				if(!generalUtil.getNull(elementValueMap.get("productDensity")).isEmpty()){
					normalDensity = Double.parseDouble(elementValueMap.get("productDensity"));
				}
				if(normalDensity == null)
				{
					elementValueMap.put("warningMsg", "The following value is missing \"Product - Density\". Calculation cannot be completed.");
				}
				
				//Conc. M = Mole of product/Mass of product*density*1000
							if (normalMass == null || normalMole == null || normalDensity == null || normalMass == 0) {
								generalUtilLogger.logWriter(LevelType.WARN,
										"The calculation was not performed on Conc. M. Parameters: Mass ='"
												+ elementValueMap.get("mass") + "';Mw = '"
												+ generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw) + "';density = '"
												+ normalMole + "';Mole = '" + normalDensity + "'",
										ActivitylogType.Calculation, formId);
							} else {
								//Mole rate of product/ Mass rate of product *density*1000
								sbInfo.append("Formula: Mole of product/Mass of product*density*1000.</br> Normal Args:"
										+ String.valueOf(normalMole) + "/" + normalMass.toString() + "*" + normalDensity.toString() + "*1000"
										+ "</br>");
								/*elementValueMap.put("concentrationMole",
										getFromNormalNumber(String.valueOf(normalMole / normalMass * normalDensity * 1000),
												elementValueMap.get("YIELDUOM_ID"), sbInfo));*/
								elementValueMap.put("concentrationMole",String.valueOf(normalMole / normalMass * normalDensity * 1000));
								generalUtilLogger.logWriter(LevelType.INFO,
										sbInfo.toString() ,ActivitylogType.Calculation, elementValueMap.get("parentId"));
							}
			}
			}
			catch(Exception e){
				generalUtilLogger.logWriter(LevelType.WARN, "The calculation was not performed on concentrationMole ="
						+ elementValueMap.get("concentrationMole"), ActivitylogType.Calculation, elementValueMap.get("parentId"));
			}
		
		}else{
			calcProductConcentrationWithSample(elementValueMap,formId,elementValueJson);
		}
	}

	private void calcProductConcentrationWithSample(Map<String, String> elementValueMap,String formId, JSONObject elementValueJson)
	{
		String sql ="select t.RESULT_VALUE from FG_I_SELECTEDRESULTS_V t where t.SAMPLE_ID = "+elementValueMap.get("SAMPLE_ID")+" and t.RESULT_NAME = 'Assay' and t.RESULT_MATERIAL_ID ="+elementValueMap.get("INVITEMMATERIAL_ID");//and t.RESULT_MATERIAL_ID =
		String assayRes = generalUtil.getNull(generalDao.selectSingleStringNoException(sql));
		elementValueMap.put("concInReactionMass",assayRes);
		
		//Continuous Experiment
		try {
			String expFormCode = formDao.getFromInfoLookup("STEP", LookupType.ID,
					elementValueMap.get("parentId"), "expFormCode");
			if(generalUtil.getNull(expFormCode).equals("ExperimentCP")){
				
			String reagentsMaterialref = "";//generalUtil.getNull(generalDao.selectSingleStringNoException("select t.materialref_id from fg_s_materialref_v t where t.PARENTID = '"+elementValueMap.get("parentId")+"' and t.INVITEMMATERIAL_ID = '"+elementValueMap.get("INVITEMMATERIAL_ID")+"' and t.TABLETYPE = 'Reactant' and rownum<=1"));
			Iterator<String> keysItr = elementValueJson.keys();
			while (keysItr.hasNext()) {
				String id = keysItr.next();
				JSONObject rowData = elementValueJson.getJSONObject(id);
				String tableType = generalUtil.getJsonValById(rowData.toString(), "tableType");
				String materialId = generalUtil.getJsonValById(rowData.toString(), "INVITEMMATERIAL_ID");
				if(tableType.equals("Reactant") && materialId.equals(elementValueMap.get("INVITEMMATERIAL_ID"))){
					reagentsMaterialref = id;
				}
			}	
			Map<String, String> run_uom_default = generalDao.sqlToHashMap("select * from fg_i_calcrun_uom_override_v");
			
	        String defaultMWUomId_runStep = (run_uom_default.get("DEFAULT_MW_UOM_ID") != null?run_uom_default.get("DEFAULT_MW_UOM_ID"):"").toString();	
	        
			String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
					elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
			String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
					elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
			generalUtilLogger.logWriter(
					LevelType.DEBUG, "calcLimitingAgentProduct - elementValueMap before calulation= mass:"
							+ elementValueMap.get("mass") + ",yield:" + elementValueMap.get("yield"),
					ActivitylogType.Calculation, formId);
			Double normalMw = 0.0;
			//Double limitingMole = 0.0;
			Double normalLimitinigMole = 0.0;
			normalMw = getCustomNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
					generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId),defaultMWUomId_runStep,new StringBuilder());
			
			if(generalUtil.getNull(elementValueMap.get("isRun")).equals("1")){
				String limitingMoleStr ="";
				if(!reagentsMaterialref.isEmpty()){
					String reactantMole = "";
					if (elementValueJson != null) {
						JSONObject rowData = elementValueJson.getJSONObject(reagentsMaterialref);
						reactantMole = generalUtil.getJsonValById(rowData.toString(), "moleRate");
						String reactantMoleUom = generalUtil.getJsonValById(rowData.toString(), "MOLERATEUOM_ID");
						normalLimitinigMole = getNormalNumber(reactantMole,reactantMoleUom);
					}
					limitingMoleStr = generalUtil.getEmpty(generalUtil.getNull(reactantMole),generalDao.selectSingleStringNoException("select fg_get_num_normal(molerate,MOLERATEUOM_ID) from fg_s_materialref_v t where t.PARENTID = '"
							+ elementValueMap.get("parentId") + "' and t.MATERIALREF_ID =  '"+ reagentsMaterialref +"' "+ generalUtilFormState
									.getWherePartForTmpData("materialref", elementValueMap.get("parentId"))));
					if (!generalUtil.getNull(limitingMoleStr).isEmpty() && generalUtil.getNull(reactantMole).isEmpty()) {
						normalLimitinigMole = Double.parseDouble(limitingMoleStr);
						}
				}else{
					if (elementValueMap.containsKey("limitingAgentMoleRate")) {
						normalLimitinigMole = getNormalNumber(elementValueMap.get("limitingAgentMoleRate"),
								elementValueMap.get("limitingAgentMolerateUomId"));
					} else {
						limitingMoleStr = generalDao.selectSingleStringNoException(
										"select fg_get_num_normal(molerate,MOLERATEUOM_ID) from fg_s_materialref_v t where t.PARENTID = '"
												+ elementValueMap.get("parentId") + "' and t.LIMITINGAGENT = 1 "
												+ generalUtilFormState.getWherePartForTmpData("materialref",
														elementValueMap.get("parentId")));
						if (!generalUtil.getNull(limitingMoleStr).isEmpty()) {
							normalLimitinigMole = Double.parseDouble(limitingMoleStr);
						}
					}
				}   
			}
			else {
				/*normalMw = getNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
						generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId));
				*/
				String limitingMoleStr = "";
				if (!reagentsMaterialref.isEmpty()) {
					String reactantMole = "";
					if (elementValueJson != null) {
						JSONObject rowData = elementValueJson.getJSONObject(reagentsMaterialref);
						reactantMole = generalUtil.getJsonValById(rowData.toString(), "mole");
						String reactantMoleUom = generalUtil.getJsonValById(rowData.toString(), "MOLEUOM_ID");
						normalLimitinigMole = getNormalNumber(reactantMole,reactantMoleUom);
					}
					limitingMoleStr = generalUtil.getEmpty(generalUtil.getNull(reactantMole),generalDao
							.selectSingleStringNoException("select fg_get_num_normal(mole,MOLEUOM_ID) from fg_s_materialref_v t where t.PARENTID = '"
									+ elementValueMap.get("parentId") + "' and t.MATERIALREF_ID =  '"
									+ reagentsMaterialref + "' " + generalUtilFormState
											.getWherePartForTmpData("materialref", elementValueMap.get("parentId"))));
					if (!generalUtil.getNull(limitingMoleStr).isEmpty() && generalUtil.getNull(reactantMole).isEmpty()) {
						normalLimitinigMole = Double.parseDouble(limitingMoleStr);
					}
				} else {
					if (elementValueMap.containsKey("limitingAgentMole")) {
						normalLimitinigMole = getNormalNumber(elementValueMap.get("limitingAgentMole"),
								elementValueMap.get("limitingAgentMoleUomId"));
					} else {
						limitingMoleStr = generalDao.selectSingleStringNoException("select fg_get_num_normal(mole,MOLEUOM_ID) from fg_s_materialref_v t where t.PARENTID = '"
										+ elementValueMap.get("parentId") + "' and t.LIMITINGAGENT = 1 " + generalUtilFormState
												.getWherePartForTmpData("materialref", elementValueMap.get("parentId")));
						if (!generalUtil.getNull(limitingMoleStr).isEmpty()) {
							Double.parseDouble(limitingMoleStr);
						}
					}
				}
			}
			elementValueMap.put("normalLimitinigMole", normalLimitinigMole.toString());
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("Concentration Mole/L Calculation =></br>");
			
			Double normalDensity = null;
			if(!generalUtil.getNull(elementValueMap.get("productDensity")).isEmpty()){
				normalDensity = Double.parseDouble(elementValueMap.get("productDensity"));
			}
			if(normalDensity == null)
			{
				elementValueMap.put("warningMsg", "The following value is missing \"Product - Density\". Calculation cannot be completed.");
			}
			Double assayResult = null;
			if(!assayRes.isEmpty()){
				assayResult = Double.parseDouble(assayRes);
			}
			//concentrationMole = sample main Assay result / MW * density * 10
			if (normalMw == null || normalLimitinigMole == null || normalMw == 0 || normalDensity == null || assayResult ==null) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on Conc. M/L . Parameters:sample main Assay result='"+assayRes+"'; density ='"
								+ elementValueMap.get("productDensity") + "';Mw = '"
								+ generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw) + "';limiting Mole = '" 
								+ normalLimitinigMole + "'",
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: sample main Assay result / MW * density * 10.</br> Normal Args:"
						+ assayRes + "/" + normalMw.toString() + "*"
						+ normalDensity.toString() + "*10" + "</br>");
				elementValueMap.put("concentrationMole",String.valueOf(assayResult/ normalMw * normalDensity * 10));
				generalUtilLogger.logWriter(LevelType.INFO,
						sbInfo.toString() ,ActivitylogType.Calculation, elementValueMap.get("parentId"));
				}
			}
		
			} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on concentrationMole =" + elementValueMap.get("concentrationMole"),
					ActivitylogType.Calculation, formId);
		}
	}
	
	private void calcMassRateForPP(Map<String, String> elementValueMap, String formId, JSONObject elementValueJson){

		try{
			String expFormCode = formDao.getFromInfoLookup("STEP", LookupType.ID,
					elementValueMap.get("parentId"), "expFormCode");
			if(generalUtil.getNull(expFormCode).equals("ExperimentCP")
					&& generalUtil.getNull(elementValueMap.get("isRun")).equals("1")){
				Map<String, String> run_uom_default = generalDao.sqlToHashMap("select * from fg_i_calcrun_uom_override_v");
				
		        String defaultQuantityUomId = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();	
		        
				String totalQuantity = generalDao.selectSingleStringNoException(
						"select distinct nvl(sum(fg_get_num_normal(t.QUANTITYRATE,t.QUANTITYRATE_UOM)) over (partition by t.PARENTID),0)"
								+ " from fg_s_materialref_v t where parentId = '" + elementValueMap.get("parentId") + "'"
								+ generalUtilFormState.getWherePartForTmpData("MaterialRef",
										elementValueMap.get("parentId")));
				if(!generalUtil.getNull(totalQuantity).isEmpty()){
					  totalQuantity = getFromNormalNumber(totalQuantity,defaultQuantityUomId,new StringBuilder());
				  }
				Double normalMass = generalUtil.getNull(totalQuantity).isEmpty()?null:Double.parseDouble(totalQuantity);
				
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Mass Rate for Pure Product =></br>");
				Double concentration = generalUtil.getNull(elementValueMap.get("concInReactionMass")).isEmpty()?null:Double.parseDouble(elementValueMap.get("concInReactionMass"));
				if (normalMass == null || concentration == null) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on Mass Rate for Pure Product Parameters: Mass ='"
									+ elementValueMap.get("mass") + "';conc. % = '"
									+ concentration + "'",
							ActivitylogType.Calculation, formId);
				} else {
					//Mass rate of product (from above calculations) * Conc. %
					sbInfo.append("Formula: Mass rate of product * Conc. % /100.</br> Normal Args:"
							+ normalMass.toString() + " * "+ concentration/100
							+ "</br>");
					elementValueMap.put("massrateforpp",
							getCustomNormalNumber(String.valueOf(normalMass * concentration/100),defaultQuantityUomId,
									elementValueMap.get("MASSRATEPPUOM_ID"), sbInfo).toString());
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation, elementValueMap.get("parentId"));
				}
			}
		}
		catch(Exception e){
			generalUtilLogger.logWriter(LevelType.WARN, "The calculation was not performed on Mass Rate for Pure Product ="
					+ elementValueMap.get("massrateforpp"), ActivitylogType.Calculation, formId);
		}
	}
	private void calcProductYield(Map<String, String> elementValueMap, String formId, JSONObject elementValueJson)
	{
		try{
			String expFormCode = formDao.getFromInfoLookup("STEP", LookupType.ID,
					elementValueMap.get("parentId"), "expFormCode");
			if(generalUtil.getNull(expFormCode).equals("ExperimentCP")
					&& generalUtil.getNull(elementValueMap.get("isRun")).equals("1")){
				
				Map<String, String> run_uom_default = generalDao.sqlToHashMap("select * from fg_i_calcrun_uom_override_v");
				
		        String defaultMWUomId_runStep = (run_uom_default.get("DEFAULT_MW_UOM_ID") != null?run_uom_default.get("DEFAULT_MW_UOM_ID"):"").toString();	
		        String defaultQuantityUomId = (run_uom_default.get("DEFAULT_QUANTITY_UOM_ID") != null?run_uom_default.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();	

                StringBuilder sbInfo = new StringBuilder();
				Double mRateOfPP = getCustomNormalNumber(elementValueMap.get("massrateforpp"),
						elementValueMap.get("MASSRATEPPUOM_ID"),defaultQuantityUomId,sbInfo); 
				String defaultMw = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
						elementValueMap.get("INVITEMMATERIAL_ID"), "mw");
				String defaultMwUomId = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
						elementValueMap.get("INVITEMMATERIAL_ID"), "MW_UOM_ID");
				Double normalMw = getCustomNormalNumber(generalUtil.getEmpty(elementValueMap.get("mwInf"), defaultMw),
						generalUtil.getEmpty(elementValueMap.get("MWUOM_ID_INF"), defaultMwUomId),defaultMWUomId_runStep,sbInfo); 
				sbInfo.append("Yield =></br>");
				
				Double limitingMole = 0.0;
				Double normalLimitinigMole = 0.0;
				if (elementValueMap.containsKey("limitingAgentMoleRate")) {
					normalLimitinigMole = getNormalNumber(elementValueMap.get("limitingAgentMoleRate"),
							elementValueMap.get("limitingAgentMolerateUomId"));
				} else {
					String limitingMoleStr = generalDao.selectSingleStringNoException(
									"select fg_get_num_normal(molerate,MOLERATEUOM_ID) from fg_s_materialref_v t where t.PARENTID = '"
											+ elementValueMap.get("parentId") + "' and t.LIMITINGAGENT = 1 "
											+ generalUtilFormState.getWherePartForTmpData("materialref",
													elementValueMap.get("parentId")));
					if (!generalUtil.getNull(limitingMoleStr).isEmpty()) {
						normalLimitinigMole = Double.parseDouble(limitingMoleStr);
					}
				}
			
				//Double normalLimitinigMole = getNormalNumber(elementValueMap.get("limitingAgentMoleRate"), elementValueMap.get("limitingMoleRateUomId"));
				if (mRateOfPP == null || normalMw == null || normalMw == 0 || normalLimitinigMole == null || normalLimitinigMole ==0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on yield Parameters: M rate of PP ='"
									+ elementValueMap.get("massrateforpp") + "';mw = '"
									+ normalMw + "';Mol rate of limiting agent = '" +
									normalLimitinigMole +"'",
							ActivitylogType.Calculation, formId);
				} else {
					//Yield = M rate of PP / MW of product / Mol rate of limiting agent (actual rate!) * 100 %
					sbInfo.append("Formula: M rate of PP / MW of product / Mol rate of limiting agent * 100 %</br> Normal Args:"
							+ mRateOfPP + " / "+ normalMw +" / " + normalLimitinigMole + "* 100%"
							+ "</br>");
					elementValueMap.put("yield",String.valueOf(mRateOfPP/ normalMw / normalLimitinigMole * 100));
					generalUtilLogger.logWriter(LevelType.INFO,
							sbInfo.toString() ,ActivitylogType.Calculation, "");
				}
			
			}
		}
		catch(Exception e){
			generalUtilLogger.logWriter(LevelType.WARN, "The calculation was not performed on Mass Rate for Pure Product ="
					+ elementValueMap.get("massrateforpp"), ActivitylogType.Calculation, formId);
		}
	}
	
	@Override
	public String doCalcRuns(String formId, String parentId, StringBuilder sbCalcInfo) {
		 
		StringBuilder sbWarning = new StringBuilder();
		Integer idx = 1;
		String runStatusId = formDao.getFromInfoLookup("RunStatus", LookupType.NAME, "Planned", "id");
		String wherePart = (formId.equals("-1"))?" experimentid='"+parentId+"'":" formid='"+formId+"'";
		List<String> runsList = generalDao.getListOfStringBySql("select distinct formid from FG_S_EXPRUNPLANNING_PIVOT \n where "+wherePart+" and runstatusid = '"+runStatusId+"'");
		for(String runId: runsList)
		{
			sbWarning.append(doCalcRun(runId, parentId, sbCalcInfo,idx));
			idx++;
		}
		generalUtilLogger.logWriter(LevelType.INFO, sbCalcInfo.toString() ,ActivitylogType.Calculation, "");
		return sbWarning.toString();
	}
	
	
	private String doCalcRun(String formId, String parentId, StringBuilder sbInfo,Integer idx) {
		
		StringBuilder sbWarning = new StringBuilder();
		try {
			boolean doCalc = true;
			List<String> runStepsList = generalDao.getListOfStringBySql("select stepdata from (select distinct STEPID||','||STEPNAME as stepdata, stepnumber from fg_s_exprunplanning_all_v where experimentid='"+parentId+"' order by stepnumber)");
			for(String step: runStepsList)
			{
				sbInfo.append("--------------------BEGIN---------------------\n</br>");
				String[] stepDataArr = step.split(",");
				String stepId = stepDataArr[0];
				String stepName = stepDataArr[1];
				List<Map<String, Object>> listOfMap = generalDao.getListOfMapsBySql("select * from fg_i_expstep_runs_calc_v where formid='"+formId+"' and stepid='"+stepId+"'");				
				if(listOfMap.size() > 0) {
										
					Map<String, Object> stepData = listOfMap.get(0);					
					String runNumber = (stepData.get("RUNNUMBER") != null?stepData.get("RUNNUMBER"):"").toString();
					String firstStepNumber = generalDao.selectSingleStringNoException("select min(stepnumber) from fg_s_exprunplanning_all_v s where s.EXPERIMENTID ='"+stepData.get("EXPERIMENTID") +"' and s.PREPARATION_RUN = 'Run'");
					String stepNumber = (stepData.get("STEPNUMBER") != null?stepData.get("STEPNUMBER"):"").toString();
					Boolean isFirstStep = generalUtil.getNull(firstStepNumber).equals(stepNumber);
					String step_id = (stepData.get("STEPID") != null?stepData.get("STEPID"):"").toString();
					String apss = (stepData.get("APSS") != null?stepData.get("APSS"):"").toString();
					if(!apss.equals("0") && !isFirstStep){
						sbInfo.append("\n **** Material Rates Calculation for Run " + runNumber +", Step " + stepName + ".****\n</br>");
						sbInfo.append("**Intermediate Calculation of Limiting Agent Material Rate by formula: \n</br>" 
								+ "***Rate_LA = MR_A / Density_A***.\n</br>"
								+ " Where 'LA' - Limiting Agent reagent, MR_A = Mass rate of product from previous step \n</br>");					
						
						//data per whole STEP
						String limitingAgent = generalUtil.getEmpty((stepData.get("LIMITINGAGENT") != null?stepData.get("LIMITINGAGENT"):"0").toString(),"0");
						if(limitingAgent.equals("0"))
						{
							if(idx == 1){
								sbWarning.append("Limiting Agent was not found for step " + stepName + ".\n");
							}
							generalUtilLogger.logWriter(LevelType.WARN,
									"The calculation was not performed on Run " + runNumber +", Step " + stepName + "(id="+stepId+"). The Limiting Agent was not found for current step.",
									ActivitylogType.Calculation, formId);
							continue;
						}
						
						/* defaults already defined inside view */
						sbInfo.append("Default UOM used for calculations: \n</br>");
						sbInfo.append("Default Volume Rate UOM: " + (stepData.get("DEFAULT_VOLUME_UOM") != null?stepData.get("DEFAULT_VOLUME_UOM"):"").toString() + ".\n</br>");	
						sbInfo.append("Default Time UOM: " + (stepData.get("DEFAULT_TIME_UOM") != null?stepData.get("DEFAULT_TIME_UOM"):"").toString() + ".\n</br>");	
						sbInfo.append("Default Density UOM: " + (stepData.get("DEFAULT_DENSITY_UOM") != null?stepData.get("DEFAULT_DENSITY_UOM"):"").toString() + ".\n</br>");	
						sbInfo.append("Default MW UOM: " + (stepData.get("DEFAULT_MW_UOM") != null?stepData.get("DEFAULT_MW_UOM"):"").toString() + ".\n</br>");	
						sbInfo.append("Default Reactor Volume UOM: " + (stepData.get("DEFAULT_REACTORVOLUME_UOM") != null?stepData.get("DEFAULT_REACTORVOLUME_UOM"):"").toString() + ".\n</br>");
						
					    String defaultTimeUomId = (stepData.get("DEFAULT_TIME_UOM_ID") != null?stepData.get("DEFAULT_TIME_UOM_ID"):"").toString();		
						String defaultVolumeUomId = (stepData.get("DEFAULT_VOLUME_UOM_ID") != null?stepData.get("DEFAULT_VOLUME_UOM_ID"):"").toString();										
						String defaultDensityUomId = (stepData.get("DEFAULT_DENSITY_UOM_ID") != null?stepData.get("DEFAULT_DENSITY_UOM_ID"):"").toString();		
						String defaultMWUomId = (stepData.get("DEFAULT_MW_UOM_ID") != null?stepData.get("DEFAULT_MW_UOM_ID"):"").toString();	
						String defaultReactorVolumeUomId = (stepData.get("DEFAULT_REACTORVOLUME_UOM_ID") != null?stepData.get("DEFAULT_REACTORVOLUME_UOM_ID"):"").toString();
						String defaultQuantityUomId = (stepData.get("DEFAULT_QUANTITY_UOM_ID") != null?stepData.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();
											
						String reactorVolume = (stepData.get("REACTORVOLUME") != null?stepData.get("REACTORVOLUME"):"").toString();
						String reactorVolumeUOM = (stepData.get("REACTORVOLUMEUOM") != null?stepData.get("REACTORVOLUMEUOM"):"").toString();
						Double normalReactorVolume = getCustomNormalNumber(reactorVolume, reactorVolumeUOM, defaultReactorVolumeUomId, sbInfo);
						
						if(normalReactorVolume == null)
						{
							if(normalReactorVolume == null)
							{
								sbWarning.append("Reactor Volume/Reactor Volume UOM field is empty for step " + stepName + ".\n");
							}
							
							generalUtilLogger.logWriter(LevelType.WARN,
									"The calculation was not performed on Retention time = Reactor volume of current step / Total Compound Volume rate. Arg: Reactor Volume= "
											+ reactorVolume + ". Run " + runNumber +", Step " + stepName + "(id="+stepId+")",
									ActivitylogType.Calculation, formId);
						}else if(normalReactorVolume == 0)
						{
							/*if(normalReactorVolume == 0)
							{
								//sbWarning.append("Normal Reactor Volume equals 0 for step " + stepName + ".\n");
							}*/
							
							generalUtilLogger.logWriter(LevelType.WARN,
									"The calculation was not performed on Retention time = Reactor volume of current step / Total Compound Volume rate. Arg: Reactor Volume= "
											+ reactorVolume + ". Run " + runNumber +", Step " + stepName + "(id="+stepId+")",
									ActivitylogType.Calculation, formId);
						}
						else
						{
						//MR_A = Mass rate of product from previous step = Sum (Mass rate of all reagents and solvents from previous step)//
						String prevStepNumber = generalDao.selectSingleStringNoException("select max(stepnumber) from fg_s_exprunplanning_all_v s where s.EXPERIMENTID ='"+stepData.get("EXPERIMENTID")+"' and s.stepnumber< '" + stepData.get("STEPNUMBER") +"'and s.PREPARATION_RUN = 'Run'");
						String prevStepId = generalDao.selectSingleStringNoException("select distinct STEPID from fg_s_exprunplanning_all_v s where s.EXPERIMENTID ='"+stepData.get("EXPERIMENTID")+"' and s.stepnumber='"+prevStepNumber+"' and runnumber = '"+runNumber+"'" );
						Double massRatePrevStep = 0.0; 
						List<Map<String, Object>> listOfMapPrevStep = generalDao.getListOfMapsBySql("select * from fg_i_expstep_runs_calc_v t where stepid='"+prevStepId+"' and runnumber = '"+runNumber+"'");				
						Double totalVolRateSum = 0.0;
						
						sbInfo.append("Calculate Mass rate of product from previous step:\n</br>");
						sbInfo.append("Formula:Sum (Mass rate of all reagents and solvents)");
						
						// get data per material
						for (Map<String, Object> materialData : listOfMapPrevStep) 
						{
							String materialRateType = (materialData.get("MATERIALRATETYPE") != null?materialData.get("MATERIALRATETYPE"):"").toString();
							String materialRateUOM = (materialData.get("MATERIALRATEUOM") != null?materialData.get("MATERIALRATEUOM"):"").toString();
							String materialRateValue = (materialData.get("MATERIALREFVALUE") != null?materialData.get("MATERIALREFVALUE"):"").toString();
							String densityInf = (materialData.get("DENSITYINF") != null?materialData.get("DENSITYINF"):"").toString();
							String densityInfUOM = (materialData.get("DENSITYUOM_ID_INF") != null?materialData.get("DENSITYUOM_ID_INF"):"").toString();
							String purityInf = (materialData.get("PURITYINF") != null?materialData.get("PURITYINF"):"").toString();
							String purityInfUOM = (materialData.get("PURITYUOM_ID_INF") != null?materialData.get("PURITYUOM_ID_INF"):"").toString();
							String mwInf = (materialData.get("MWINF") != null?materialData.get("MWINF"):"").toString();
							String mwInfUOM = (materialData.get("MWUOM_ID_INF") != null?materialData.get("MWUOM_ID_INF"):"").toString();
							
							Double normalMWInf = getCustomNormalNumber(mwInf, mwInfUOM, defaultMWUomId, sbInfo);
							Double normalPurityInf = getNormalNumber(purityInf, purityInfUOM, (double) 100);
							
							
							Double rateNormal = getNormalNumber(String.valueOf(materialRateValue), materialRateUOM);
							Double normalQuantity = 0.0;
							
							/*Formula to calculate Quantity Rate: Volume Rate * Density
					          Formula to calculate Mole Rate: (Quantity Rate * Purity)/MW
					        */
							switch (materialRateType.toLowerCase()) {
								case "volume":
									if (rateNormal != null) {
										String vol_str = getFromNormalNumber(rateNormal.toString(), defaultVolumeUomId,sbInfo);
										if(!generalUtil.getNull(vol_str).isEmpty()){
											rateNormal = Double.parseDouble(vol_str);
									}
									}
									Double normalDensityInf = getCustomNormalNumber(densityInf, densityInfUOM,
											defaultDensityUomId, sbInfo);
									//sbInfo.append("Normal Density = " + normalDensityInf + ".\n</br>");
									if (rateNormal != null) {
										normalQuantity = rateNormal * normalDensityInf;
									}
									break;
								case "quantity":
									if (rateNormal != null) {
										String qnt = getFromNormalNumber(rateNormal.toString(), defaultQuantityUomId,sbInfo);
										if(!generalUtil.getNull(qnt).isEmpty()){
											normalQuantity = Double.parseDouble(qnt);
										}
									}
									break;
								case "mole":
									if (normalPurityInf != null && normalPurityInf != 0 && rateNormal != null) {
										Double mole = rateNormal;
										normalQuantity = (normalMWInf * mole) / (normalPurityInf/100);
									}
									break;
								} 
							massRatePrevStep += normalQuantity;
						}
						sbInfo.append("Mass rate of product from previous step = "+massRatePrevStep+".\n</br>");
						
						if(massRatePrevStep == 0){
							//sbWarning.append("Mass rate of product from previous step equals to 0 for step " + stepName + ".\n");
							generalUtilLogger.logWriter(LevelType.WARN,
									"Mass rate of product from previous step equals to 0\n</br>The calculation was not performed\n"
											+ ". Run " + runNumber +", Step " + stepName + "(id="+stepId+")",
									ActivitylogType.Calculation, formId);
						}else{
						
						/* First calculate Limiting Agent data */
						sbInfo.append("------------------------------------------\n</br>");
						String invItemMaterialName_LA = (stepData.get("INVITEMMATERIALNAME") != null?stepData.get("INVITEMMATERIALNAME"):"").toString();
						String invItemMaterialId_LA = (stepData.get("INVITEMMATERIALID") != null?stepData.get("INVITEMMATERIALID"):"").toString();
						sbInfo.append("Calculation for Limiting Agent Material "+invItemMaterialName_LA+":\n</br>");
						
						
						String mwInf_LA = (stepData.get("MWINF") != null?stepData.get("MWINF"):"").toString();
						String mwInfUOM_LA = (stepData.get("MWUOM_ID_INF") != null?stepData.get("MWUOM_ID_INF"):"").toString();
						String densityInf_LA = (stepData.get("DENSITYINF") != null?stepData.get("DENSITYINF"):"").toString();
						String densityInfUOM_LA = (stepData.get("DENSITYUOM_ID_INF") != null?stepData.get("DENSITYUOM_ID_INF"):"").toString();
						sbInfo.append("Calculate normal Density:\n</br>");
						Double normalDensityInf_LA = getCustomNormalNumber(densityInf_LA, densityInfUOM_LA, defaultDensityUomId, sbInfo);
						sbInfo.append("Normal Density = "+normalDensityInf_LA+".\n</br>");
						sbInfo.append("Calculate normal MW:\n</br>");
						Double normalMWInf_LA = getCustomNormalNumber(mwInf_LA, mwInfUOM_LA, defaultMWUomId, sbInfo);
						sbInfo.append("Normal MW = "+normalMWInf_LA+".\n</br>");
						
						
						//Mol rate of LA 
						Double molLA = 0.0;
						Double concPrevStep =0.0;
						//If LA of the second or any next step IS the product from the previous RUN step
						String isProductFromPrevStep = generalDao.selectSingleStringNoException("select distinct 1 from fg_s_materialref_v t where t.INVITEMMATERIAL_ID = '"+invItemMaterialId_LA+"' and t.PARENTID = '"+prevStepId+"' and t.TABLETYPE = 'Product'");
							if (!generalUtil.getNull(isProductFromPrevStep).equals("1")) {
								sbWarning.append(
										"Product from the previous step does not exist in the Reactant table of step "+ stepName+" as a limiting agent reactant.\n</br>Calculation cannot be completed.");

								generalUtilLogger.logWriter(LevelType.WARN,
										"Product from the previous step does not exists in the Reactant table of step "+stepName+" as a limiting agent reactant.\n</br>Calculation cannot be completed."
												+ " Run " + runNumber + ", Step " + stepName + "(id=" + stepId + ")",
										ActivitylogType.Calculation, formId);

							}else{
								sbInfo.append("Calculate Mole LA:\n</br>");
								String laSameAsPrev = generalUtil.getNull("select distinct 1 from fg_s_materialref_v t where t.INVITEMMATERIAL_ID = '"+invItemMaterialId_LA+"' and t.PARENTID = '"+prevStepId+"' and t.LIMITINGAGENT = '1'");
					    	    //LA material name of current step is the same LA material name of previous step
					    	   if(generalUtil.getNull(laSameAsPrev).equals("1")){
					    		   //Mol RA = Mol RA from previous step 
					    		   concPrevStep = calcConcentration(invItemMaterialId_LA, normalMWInf_LA, massRatePrevStep,prevStepId,runNumber);
					    		   molLA = massRatePrevStep*concPrevStep/100/normalMWInf_LA;
					    	   }else{//LA material name of current step is different from LA material name of previous step
					    		   concPrevStep = calcConcentration(invItemMaterialId_LA, normalMWInf_LA, massRatePrevStep,prevStepId,runNumber);
					    		   molLA = massRatePrevStep*concPrevStep/100/normalMWInf_LA;
					    		}
					    	   sbInfo.append("Formula: Mol LA = MR (Mass rate of product from previous step) * Conc. % (of this compound in product of previous step)/100/MW.\n</br> Args: MR = " 
										+ massRatePrevStep + "; Conc. % = " + concPrevStep + "; MW = "+normalMWInf_LA + ".\n</br>");			
								
					    	   }
							if(molLA ==0 || molLA == null || normalDensityInf_LA == null){
								if(molLA ==0 || molLA == null){
								generalUtilLogger.logWriter(LevelType.WARN,
										"Mol LA is empty.\n</br>Calculation cannot be completed."
												+ " Run " + runNumber + ", Step " + stepName + "(id=" + stepId + ")",
										ActivitylogType.Calculation, formId);
								}
								if(normalDensityInf_LA == null)
								{
									sbWarning.append("Density/Density UOM field is empty for Limiting Agent material " + invItemMaterialName_LA + " for step " + stepName + ".\n");
								}
							}else{
							
					    
					    Double rateLA = 0.0; 
						int index = 0;
						Map<String, String> rateN_map = new HashMap<String,String>();
					    
					 // get data per material
						for (Map<String, Object> materialData : listOfMap) 
						{
							index++;
							// ignore first row, used for Limiting Agent in above code
							if(index == 1)continue;
							
							String invItemMaterialName = (materialData.get("INVITEMMATERIALNAME") != null?materialData.get("INVITEMMATERIALNAME"):"").toString();
							String invItemMaterialId = (materialData.get("INVITEMMATERIALID") != null?materialData.get("INVITEMMATERIALID"):"").toString();
							sbInfo.append("------------------------------------------\n</br>");
							sbInfo.append("Calculation for Material "+invItemMaterialName+":\n</br>");
							
							String equivalent = (materialData.get("MATERIALREFEQUIVALENT") != null?materialData.get("MATERIALREFEQUIVALENT"):"").toString();
							String mwInf = (materialData.get("MWINF") != null?materialData.get("MWINF"):"").toString();
							String mwInfUOM = (materialData.get("MWUOM_ID_INF") != null?materialData.get("MWUOM_ID_INF"):"").toString();
							String densityInf = (materialData.get("DENSITYINF") != null?materialData.get("DENSITYINF"):"").toString();
							String densityInfUOM = (materialData.get("DENSITYUOM_ID_INF") != null?materialData.get("DENSITYUOM_ID_INF"):"").toString();
							String purityInf = (materialData.get("PURITYINF") != null?materialData.get("PURITYINF"):"").toString();
							String purityInfUOM = (materialData.get("PURITYUOM_ID_INF") != null?materialData.get("PURITYUOM_ID_INF"):"").toString();
							
							
							sbInfo.append("Calculate normal Density:\n</br>");
							Double normalDensityInf = getCustomNormalNumber(densityInf, densityInfUOM, defaultDensityUomId, sbInfo);
							sbInfo.append("Normal Density = "+normalDensityInf+".\n</br>");
							sbInfo.append("Calculate normal MW:\n</br>");
							Double normalMWInf = getCustomNormalNumber(mwInf, mwInfUOM, defaultMWUomId, sbInfo);
							sbInfo.append("Normal MW = "+normalMWInf+".\n</br>");
							Double normalPurityInf = getNormalNumber(purityInf, purityInfUOM, (double) 100);
							
							
							sbInfo.append("Calculate concentration:\n</br>");
							Double concentration = 0.0;
							String isSameProductExist = generalDao.selectSingleStringNoException("select distinct 1 from fg_s_materialref_v t where t.INVITEMMATERIAL_ID = '"+invItemMaterialId+"' and t.PARENTID = '"+prevStepId+"' and t.tabletype = 'Product' and nvl(active,'1') = '1' and sessionid is null");
				    	    if(generalUtil.getNull(isSameProductExist).equals("1")){
				    	    	concentration = calcConcentration(invItemMaterialId, normalMWInf, massRatePrevStep,prevStepId,runNumber);
				    	    }else{
				    	    	concentration = normalPurityInf;
				    	    	if(normalPurityInf == 0)
								{
									sbWarning.append("Purity/Purity UOM field equals 0 for material " + invItemMaterialName + " for step " + stepName + ".\n");
								}
								if(normalPurityInf == null)
								{
									sbWarning.append("Purity/Purity UOM field is empty for material " + invItemMaterialName + " for step " + stepName + ".\n");
								}
				    	    }
				    	    sbInfo.append("Concentration = "+concentration+".\n</br>");
							
							if(normalDensityInf == null || normalDensityInf == 0 || equivalent.equals("") || normalMWInf == null || concentration == 0 || concentration == null || molLA == null)
							{
								if(normalDensityInf == null)
								{
									sbWarning.append("Density/Density UOM field is empty for material " + invItemMaterialName + " for step " + stepName + ".\n");
								}
								if(equivalent == null || equivalent.equals(""))
								{
									sbWarning.append("Equivalent field is empty for material " + invItemMaterialName + " for step " + stepName + ".\n");
								}
								if(normalMWInf == null)
								{
									sbWarning.append("MW/MW UOM field is empty for material " + invItemMaterialName + " for step " + stepName + ".\n");
								}
								if(normalDensityInf == 0)
								{
									sbWarning.append("density field equals 0 for material " + invItemMaterialName + " for step " + stepName + ".\n");
								}
								if(generalUtil.getNull(isSameProductExist).equals("1")){
									if(concentration == 0)
									{
										sbWarning.append("concentration equals 0 for material " + invItemMaterialName + " for step " + stepName + ".\n");
									}
								    if(concentration == null)
								    {
									    sbWarning.append("concentration is empty for material " + invItemMaterialName + " for step " + stepName + ".\n");
						            }
								}
								generalUtilLogger.logWriter(LevelType.WARN,
										"The calculation was not performed for Material "+invItemMaterialName+" on Mol RA * Equivalent_N * MW_N / Density_N / Concentration_N%. Args: Mol LA = "
												+ molLA + "; Equivalent = " + equivalent + "; MW = "+normalMWInf + "; Density = "+normalMWInf+"; Concentration = "+concentration+"."
												+" Run " + runNumber +", Step " + stepName + "(id="+stepId+")",
										ActivitylogType.Calculation, formId);
								doCalc = false;
								break;
							}
							Double result = molLA*Double.parseDouble(equivalent)*normalMWInf/normalDensityInf/(concentration/100);
							totalVolRateSum += result;
							sbInfo.append("Formula: Mol RA * Equivalent_N * MW_N / Density_N / Concentration_N%.\n</br> Args: Mol RA = " 
									+ molLA + "; Equivalent = " + equivalent + "; MW = "+normalMWInf + "; Density = "+normalDensityInf+"; Concentration = "+concentration+".\n</br>");			
							
							String materialRefId = (materialData.get("MATERIALREFID") != null?materialData.get("MATERIALREFID"):"").toString();
							rateN_map.put(materialRefId, String.valueOf(result));
						}
						
						if(doCalc)
						{
							sbInfo.append("------------------------------------------\n</br>");
							sbInfo.append("***Calculate Limiting Agent Material Rate: ***\n</br>"
									+ "***Rate_LA = = Mass rate of product from previous step / Density_LA.\n</br>"
								    + " Args: Mass rate of product from previous step = " +  massRatePrevStep
								    + ";Density_LA = " + normalDensityInf_LA + ".\n</br>");
									sbInfo.append("Limiting Agent Material Rate = "+rateLA+ ".\n</br>");
									
							rateLA = massRatePrevStep/normalDensityInf_LA;
							totalVolRateSum += rateLA;
							Double retentionTime = 0.0;
							if(totalVolRateSum == null || totalVolRateSum == 0){
								generalUtilLogger.logWriter(LevelType.WARN,
										"The calculation was not performed on Retention time = Reactor volume of current step / Total Compound Volume rate. Arg: Reactor Volume= "
												+ reactorVolume + "; Total Compound Volume rate = " + totalVolRateSum + ". Run " + runNumber +", Step " + stepName + "(id="+stepId+")",
										ActivitylogType.Calculation, formId);
								} else {
									sbInfo.append("***Calculate Retention time: ***\n</br>"
											+ "***Retention time =  Reactor volume of current step / Total Compound Volume rate.\n</br>"
											+ " Args: Total Compounds Volume rate = " + totalVolRateSum
											+ ";Reactor volume of current step = " + normalReactorVolume + ".\n</br>");
									retentionTime = normalReactorVolume / totalVolRateSum;
									String retentionTimeUOM = (stepData.get("RETENTIONTIMEUOM") != null?stepData.get("RETENTIONTIMEUOM"):"").toString();
									retentionTime = getCustomNormalNumber(retentionTime.toString(),defaultTimeUomId, retentionTimeUOM,new StringBuilder());
									sbInfo.append("Retention time = " + retentionTime + ".\n</br>");
									
									String sql = "update FG_S_EXPRUNPLANNING_PIVOT t \n"
											+ "set t.retentiontime = '"+retentionTime+"'  \n"
											+" where  t.formid = '" + formId + "' \n"
											+" and t.sessionid is null and t.active = 1"
											+" and t.stepid = '" + step_id + "'";
								formSaveDao.updateStructTableByFormId(sql, "FG_S_EXPRUNPLANNING_PIVOT",Arrays.asList("retentiontime"), formId); 
								}
							
							sbInfo.append("**Main calculation of each material rate by Formula:\n</br>"
									+ "***Rate_N = Mol RA * Equivalent_N * MW_N / Density_N / Concentration_N%.\n</br>");
							
							int index2 = 0;
							for (Map<String, Object> entry : listOfMap) {
								
								Double rateN = 0.0;
								Double rateN_final = 0.0;
								index2++;
								String invItemMaterialName = (entry.get("INVITEMMATERIALNAME") != null?entry.get("INVITEMMATERIALNAME"):"").toString();
								sbInfo.append("------------------------------------------\n</br>");
								if(index2 == 1)
								{
									rateN = rateLA;
									sbInfo.append("Rate for Limiting Agent material '"+invItemMaterialName+"' = " + String.valueOf(rateN) + ".\n</br>");
								}
								else
								{
									String materialRefId = (entry.get("MATERIALREFID") != null?entry.get("MATERIALREFID"):"").toString();
									rateN = Double.parseDouble(rateN_map.get(materialRefId));
									sbInfo.append("Rate for material '"+invItemMaterialName+"' = " + String.valueOf(rateN) + ".\n</br>");
								}
								String purityInf = (entry.get("PURITYINF") != null?entry.get("PURITYINF"):"").toString();
								String purityInfUOM = (entry.get("PURITYUOM_ID_INF") != null?entry.get("PURITYUOM_ID_INF"):"").toString();
								String mwInf = (entry.get("MWINF") != null?entry.get("MWINF"):"").toString();
								String mwInfUOM = (entry.get("MWUOM_ID_INF") != null?entry.get("MWUOM_ID_INF"):"").toString();
								String densityInf = (entry.get("DENSITYINF") != null?entry.get("DENSITYINF"):"").toString();
								String densityInfUOM = (entry.get("DENSITYUOM_ID_INF") != null?entry.get("DENSITYUOM_ID_INF"):"").toString();
								String materialRateType = (entry.get("MATERIALRATETYPE") != null?entry.get("MATERIALRATETYPE"):"").toString();
								String materialRateUOM = (entry.get("MATERIALRATEUOM") != null?entry.get("MATERIALRATEUOM"):"").toString();
								String materialRateUOMName = (entry.get("MATERIALRATEUOM_NAME") != null?entry.get("MATERIALRATEUOM_NAME"):"").toString();
								Double normalDensityInf = getCustomNormalNumber(densityInf, densityInfUOM, defaultDensityUomId, sbInfo);
								
								/*Double rateNVolumeNormal = getNormalNumber(String.valueOf(rateN), defaultVolumeUomId);
								sbInfo.append("Convert Rate to Default Volume UOM = " + String.valueOf(rateNVolumeNormal) + ".\n</br>");									
								*/sbInfo.append("Current material Rate type = " + materialRateType + "; Current material Rate UOM = " + materialRateUOMName + ".\n</br>");									
																	
								switch(materialRateType.toLowerCase()) {
								case "volume":
									if(materialRateUOM.equals(defaultVolumeUomId))
									{
										//rateN_final = rateNVolumeNormal;
										rateN_final = rateN;
									}
									else
									{
										rateN_final =  getCustomNormalNumber(String.valueOf(rateN),defaultVolumeUomId, materialRateUOM,new StringBuilder());
										
									}
									break;
								case "quantity":	
									sbInfo.append("Calculate normal Density:\n</br>");
									//Double normalDensityInf = getCustomNormalNumber(densityInf, densityInfUOM, defaultDensityUomId, sbInfo);
									sbInfo.append("Normal Density = "+normalDensityInf+".\n</br>");
									Double rateNQuantityNormal = rateN * normalDensityInf;
									sbInfo.append("Formula to calculate Quantity Rate: Volume Rate * Density.</br>"
											+ " Normal Args: "+ rateN + " * " + normalDensityInf + " = "+rateNQuantityNormal+".\n</br>");
										
									rateN_final = getCustomNormalNumber(String.valueOf(rateNQuantityNormal),defaultQuantityUomId, materialRateUOM,new StringBuilder());
									
									break;
								case "mole":
									sbInfo.append("Calculate normal MW:\n</br>");
									Double normalMWInf = getCustomNormalNumber(mwInf, mwInfUOM, defaultMWUomId, sbInfo);
									sbInfo.append("Normal MW = "+normalMWInf+".\n</br>");
									sbInfo.append("Calculate normal Density:\n</br>");
									Double normalDensityInfQ = getCustomNormalNumber(densityInf, densityInfUOM, defaultDensityUomId, sbInfo);
									sbInfo.append("Normal Density = "+normalDensityInfQ+".\n</br>");
									Double normalPurityInf = getNormalNumber(purityInf, purityInfUOM, (double) 100);
									
									Double rateNQuantityNorm = rateN * normalDensityInfQ;
									Double rateNMoleNormal = rateNQuantityNorm * normalPurityInf/100 / normalMWInf;
									
									sbInfo.append("Formula to calculate Mole Rate: (Quantity Rate * Purity/100)/MW.</br> "
											+ "Normal Args: "+ rateNQuantityNorm + " * " + (normalPurityInf/100) + " / " + normalMWInf + " = "+rateNMoleNormal+".\n</br>");
									String rate_final_str = getFromNormalNumber(String.valueOf(rateNMoleNormal), materialRateUOM,new StringBuilder());
									if(!generalUtil.getNull(rate_final_str).isEmpty()){
										rateN_final = Double.parseDouble(rate_final_str);
									}
									break;
							}
								
								sbInfo.append("Final Rate value converted to " + materialRateType + " = " + rateN_final + ".\n</br>");
								
								String sql = "update FG_S_EXPRUNPLANNING_PIVOT t \n"
											+ "set t.MATERIALREFVALUE = '"+ rateN_final +"' \n"
											+"    ,t.dencity = '"+normalDensityInf+"'  \n"
											+"    ,t.dencityuom = '"+defaultDensityUomId+"' \n"
											//+"    ,t.retentiontime = '"+retentionTime+"'  \n"
											+"    ,t.purity = '"+purityInf+"'  \n"
											+"    ,t.purityuom = '"+purityInfUOM+"' \n"
											+"    ,t.mw = '"+mwInf+"' \n"
											+"    ,t.mwuom = '"+mwInfUOM+"' \n"
											+" where  t.MATERIALREFID = '"+entry.get("MATERIALREFID").toString()+"'\n"
											+" and t.formid = '" + entry.get("formid").toString() + "' \n"
											+" and t.sessionid is null and t.active = 1";
								formSaveDao.updateStructTableByFormId(sql, "FG_S_EXPRUNPLANNING_PIVOT",Arrays.asList("MATERIALREFVALUE"), entry.get("formid").toString()); 
							}
						}
						}
						}
						}
					}else{
					sbInfo.append("\n **** Material Rates Calculation for Run " + runNumber +", Step " + stepName + ".****\n</br>");
					sbInfo.append("**Intermediate Calculation of Limiting Agent Material Rate by formula: \n</br>" 
							+ "***Rate_LA = Total Compounds Volume rate/(1 + (Equivalent_B*Dencity_LA*Concentration_LA%/MW_LA/Dencity_B*Concentration_B%/MW_B) +...+ (Equivalent_N*Dencity_LA*Concentration_LA%/MW_LA/Dencity_N*Concentration_N%/MW_N))***.\n</br>"
							+ " Where 'LA' - Limiting Agent reagent, 'B'&'N' - other reagents and solvents.\n</br>");					
					
					//data per whole STEP
					String limitingAgent = generalUtil.getEmpty((stepData.get("LIMITINGAGENT") != null?stepData.get("LIMITINGAGENT"):"0").toString(),"0");
					if(limitingAgent.equals("0"))
					{
						if(idx == 1){
							sbWarning.append("Limiting Agent was not found for step " + stepName + ".\n");
						}
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on Run " + runNumber +", Step " + stepName + "(id="+stepId+"). The Limiting Agent was not found for current step.",
								ActivitylogType.Calculation, formId);
						continue;
					}
					/* defaults already defined inside view */
					sbInfo.append("Default UOM used for calculations: \n</br>");
					sbInfo.append("Default Volume Rate UOM: " + (stepData.get("DEFAULT_VOLUME_UOM") != null?stepData.get("DEFAULT_VOLUME_UOM"):"").toString() + ".\n</br>");	
					sbInfo.append("Default Time UOM: " + (stepData.get("DEFAULT_TIME_UOM") != null?stepData.get("DEFAULT_TIME_UOM"):"").toString() + ".\n</br>");	
					sbInfo.append("Default Density UOM: " + (stepData.get("DEFAULT_DENSITY_UOM") != null?stepData.get("DEFAULT_DENSITY_UOM"):"").toString() + ".\n</br>");	
					sbInfo.append("Default MW UOM: " + (stepData.get("DEFAULT_MW_UOM") != null?stepData.get("DEFAULT_MW_UOM"):"").toString() + ".\n</br>");	
					sbInfo.append("Default Reactor Volume UOM: " + (stepData.get("DEFAULT_REACTORVOLUME_UOM") != null?stepData.get("DEFAULT_REACTORVOLUME_UOM"):"").toString() + ".\n</br>");
					
					String defaultVolumeUomId = (stepData.get("DEFAULT_VOLUME_UOM_ID") != null?stepData.get("DEFAULT_VOLUME_UOM_ID"):"").toString();										
					String defaultTimeUomId = (stepData.get("DEFAULT_TIME_UOM_ID") != null?stepData.get("DEFAULT_TIME_UOM_ID"):"").toString();		
					String defaultDensityUomId = (stepData.get("DEFAULT_DENSITY_UOM_ID") != null?stepData.get("DEFAULT_DENSITY_UOM_ID"):"").toString();		
					String defaultMWUomId = (stepData.get("DEFAULT_MW_UOM_ID") != null?stepData.get("DEFAULT_MW_UOM_ID"):"").toString();	
					String defaultReactorVolumeUomId = (stepData.get("DEFAULT_REACTORVOLUME_UOM_ID") != null?stepData.get("DEFAULT_REACTORVOLUME_UOM_ID"):"").toString();
					String defaultQuantityUomId = (stepData.get("DEFAULT_QUANTITY_UOM_ID") != null?stepData.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();
										
					String retentionTime = (stepData.get("RETENTIONTIME") != null?stepData.get("RETENTIONTIME"):"").toString();
					String retentionTimeUOM = (stepData.get("RETENTIONTIMEUOM") != null?stepData.get("RETENTIONTIMEUOM"):"").toString();
					String reactorVolume = (stepData.get("REACTORVOLUME") != null?stepData.get("REACTORVOLUME"):"").toString();
					String reactorVolumeUOM = (stepData.get("REACTORVOLUMEUOM") != null?stepData.get("REACTORVOLUMEUOM"):"").toString();
					sbInfo.append("Calculate normal Retention Time:\n</br>");
					Double normalRetentionTime = getCustomNormalNumber(retentionTime, retentionTimeUOM, defaultTimeUomId, sbInfo);
					sbInfo.append("Normal Retention Time = "+normalRetentionTime+".\n</br>");
					Double normalReactorVolume = getCustomNormalNumber(reactorVolume, reactorVolumeUOM, defaultReactorVolumeUomId, sbInfo);
					
					if(normalRetentionTime == null || normalReactorVolume == null)
					{
						if(normalRetentionTime == null)
						{
							sbWarning.append("Retention Time/Retention Time UOM field is empty for Run " + runNumber +", Step " + stepName + ".\n");
						}
						if(normalReactorVolume == null)
						{
							sbWarning.append("Reactor Volume/Reactor Volume UOM field is empty for step " + stepName + ".\n");
						}
						
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on Total Compounds Volume rate = Reactor Volume/Retention Time. Args: Reactor Volume= "
										+ reactorVolume + "; Retention Time = " + retentionTime + ". Run " + runNumber +", Step " + stepName + "(id="+stepId+")",
								ActivitylogType.Calculation, formId);
					}
					else if(normalRetentionTime == 0)
					{
						sbWarning.append("Retention Time field equals 0 for Run " + runNumber +", Step " + stepName + ".\n");
						
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed on Total Compounds Volume rate = Reactor Volume/Retention Time. Args: Reactor Volume= "
										+ reactorVolume + "; Retention Time = " + retentionTime + ". The dividing parameter [Retention Time] in the calculation is 0."
										+ " Run " + runNumber +", Step " + stepName + "(id="+stepId+")",
								ActivitylogType.Calculation, formId);
					}
					else
					{	
						Double totalCompoundsVolumeRate = normalReactorVolume/normalRetentionTime;
						sbInfo.append("Formula: Total Compounds Volume rate = Reactor Volume/Retention Time.\n</br>Normal Args: "
								     + String.valueOf(normalReactorVolume)+"/"+String.valueOf(normalRetentionTime) + " = "+totalCompoundsVolumeRate+".\n</br>"
								);

						/* First calculate Limiting Agent data */ 
						sbInfo.append("------------------------------------------\n</br>");
						String invItemMaterialName_LA = (stepData.get("INVITEMMATERIALNAME") != null?stepData.get("INVITEMMATERIALNAME"):"").toString();
						String invItemMaterialId_LA = (stepData.get("INVITEMMATERIALID") != null?stepData.get("INVITEMMATERIALID"):"").toString();
						sbInfo.append("Calculation for Limiting Agent Material "+invItemMaterialName_LA+":\n</br>");
						
						
						String purityInf_LA = (stepData.get("PURITYINF") != null?stepData.get("PURITYINF"):"").toString();
						String purityInfUOM_LA = (stepData.get("PURITYUOM_ID_INF") != null?stepData.get("PURITYUOM_ID_INF"):"").toString();
						String mwInf_LA = (stepData.get("MWINF") != null?stepData.get("MWINF"):"").toString();
						String mwInfUOM_LA = (stepData.get("MWUOM_ID_INF") != null?stepData.get("MWUOM_ID_INF"):"").toString();
						String densityInf_LA = (stepData.get("DENSITYINF") != null?stepData.get("DENSITYINF"):"").toString();
						String densityInfUOM_LA = (stepData.get("DENSITYUOM_ID_INF") != null?stepData.get("DENSITYUOM_ID_INF"):"").toString();
						sbInfo.append("Calculate normal Density:\n</br>");
						Double normalDensityInf_LA = getCustomNormalNumber(densityInf_LA, densityInfUOM_LA, defaultDensityUomId, sbInfo);
						sbInfo.append("Normal Density = "+normalDensityInf_LA+".\n</br>");
						Double normalPurityInf_LA = getNormalNumber(purityInf_LA, purityInfUOM_LA, (double) 100);
						sbInfo.append("Calculate normal MW:\n</br>");
						Double normalMWInf_LA = getCustomNormalNumber(mwInf_LA, mwInfUOM_LA, defaultMWUomId, sbInfo);
						sbInfo.append("Normal MW = "+normalMWInf_LA+".\n</br>");
						
						String prevStepNumber = generalDao.selectSingleStringNoException("select max(stepnumber) from fg_s_exprunplanning_all_v s where s.EXPERIMENTID ='"+stepData.get("EXPERIMENTID")+"' and s.stepnumber< '" + stepData.get("STEPNUMBER") +"'and s.PREPARATION_RUN = 'Run'");
						String prevStepId = generalDao.selectSingleStringNoException("select distinct STEPID from fg_s_exprunplanning_all_v s where s.EXPERIMENTID ='"+stepData.get("EXPERIMENTID")+"' and s.stepnumber='"+prevStepNumber+"' and runnumber = '"+runNumber+"'" );
						String isProductFromPrevStep = generalDao.selectSingleStringNoException("select distinct 1 from fg_s_materialref_v t where t.INVITEMMATERIAL_ID = '"+invItemMaterialId_LA+"' and t.PARENTID = '"+prevStepId+"' and t.TABLETYPE = 'Product'");
						Double massRatePrevStep = 0.0; 
						Double concentration_LA = 0.0;
						
						if(!isFirstStep){
							List<Map<String, Object>> listOfMapPrevStep = generalDao.getListOfMapsBySql("select * from fg_i_expstep_runs_calc_v t where stepid='"+prevStepId+"' and runnumber = '"+runNumber+"'");				
							
							sbInfo.append("Calculate Mass rate of product from previous step:\n</br>");
							// get data per material
							for (Map<String, Object> materialData : listOfMapPrevStep) 
							{
								String materialRateType = (materialData.get("MATERIALRATETYPE") != null?materialData.get("MATERIALRATETYPE"):"").toString();
								String materialRateUOM = (materialData.get("MATERIALRATEUOM") != null?materialData.get("MATERIALRATEUOM"):"").toString();
								String materialRateValue = (materialData.get("MATERIALREFVALUE") != null?materialData.get("MATERIALREFVALUE"):"").toString();
								String densityInf = (materialData.get("DENSITYINF") != null?materialData.get("DENSITYINF"):"").toString();
								String densityInfUOM = (materialData.get("DENSITYUOM_ID_INF") != null?materialData.get("DENSITYUOM_ID_INF"):"").toString();
								String purityInf = (materialData.get("PURITYINF") != null?materialData.get("PURITYINF"):"").toString();
								String purityInfUOM = (materialData.get("PURITYUOM_ID_INF") != null?materialData.get("PURITYUOM_ID_INF"):"").toString();
								String mwInf = (materialData.get("MWINF") != null?materialData.get("MWINF"):"").toString();
								String mwInfUOM = (materialData.get("MWUOM_ID_INF") != null?materialData.get("MWUOM_ID_INF"):"").toString();
								
								Double normalMWInf = getCustomNormalNumber(mwInf, mwInfUOM, defaultMWUomId, sbInfo);
								Double normalPurityInf = getNormalNumber(purityInf, purityInfUOM, (double) 100);
								
								
								Double rateNormal = getNormalNumber(String.valueOf(materialRateValue), materialRateUOM);
								Double normalQuantity = 0.0;
								
								/*Formula to calculate Quantity Rate: Volume Rate * Density
						          Formula to calculate Mole Rate: Quantity Rate * Purity/100/MW
						        */
								switch (materialRateType.toLowerCase()) {
									case "volume":
										if (rateNormal != null) {
											String volume = getFromNormalNumber(rateNormal.toString(), defaultVolumeUomId,sbInfo).toString();
											if(!generalUtil.getNull(volume).isEmpty()){
												rateNormal = Double.parseDouble(volume);
											}
										}
										Double normalDensityInf = getCustomNormalNumber(densityInf, densityInfUOM,
												defaultDensityUomId, sbInfo);
										sbInfo.append("Normal Density = " + normalDensityInf + ".\n</br>");
										if (rateNormal != null) {
											normalQuantity = rateNormal * normalDensityInf;
										}
										break;
									case "quantity":
										if (rateNormal != null) {
										    String qnt = getFromNormalNumber(rateNormal.toString(), defaultQuantityUomId,sbInfo);
										    if(!generalUtil.getNull(qnt).isEmpty()){
										    	normalQuantity = Double.parseDouble(qnt);
										    }
										}
										break;
									case "mole":
										if (normalPurityInf != null && normalPurityInf != 0 && rateNormal != null) {
											Double mole = rateNormal;
											normalQuantity = (normalMWInf * mole) / (normalPurityInf/100);
										}
										break;
									} 
								massRatePrevStep += normalQuantity;
							}
							sbInfo.append("Mass rate of product from previous step = "+massRatePrevStep+".\n</br>");
							
						}
						if(!isFirstStep && generalUtil.getNull(isProductFromPrevStep).equals("1")){
							sbInfo.append("Calculate concentration:\n</br>");
							concentration_LA = calcConcentration(invItemMaterialId_LA, normalMWInf_LA, massRatePrevStep,prevStepId,runNumber);
							sbInfo.append("concentration =  "+concentration_LA+"\n</br>");
						}
						else{
							concentration_LA = normalPurityInf_LA;
							if(normalPurityInf_LA == null)
							{
								sbWarning.append("Purity/Purity UOM field is empty for Limiting Agent material " + invItemMaterialName_LA + " for step " + stepName + ".\n");
							}
						}
						
						if(normalDensityInf_LA == null || concentration_LA == null || concentration_LA == 0 || normalMWInf_LA == null)
						{
							if(normalDensityInf_LA == null)
							{
								sbWarning.append("Density/Density UOM field is empty for Limiting Agent material " + invItemMaterialName_LA + " for step " + stepName + ".\n");
							}
							if(normalMWInf_LA == null)
							{
								sbWarning.append("MW/MW UOM field is empty for Limiting Agent material " + invItemMaterialName_LA + " for step " + stepName + ".\n");
							}
							
							generalUtilLogger.logWriter(LevelType.WARN,
									"The calculation was not performed for LIMITING AGENT Material "+invItemMaterialName_LA+" on Dencity_LA*Concentration_LA%/MW_LA. Args: Dencity LA = "
											+ normalDensityInf_LA + "; Concentration LA = " + concentration_LA + "; MW LA = "+normalMWInf_LA+"."
											+ " Run " + runNumber +", Step " + stepName + "(id="+stepId+")",
									ActivitylogType.Calculation, formId);
						}
						else if(normalMWInf_LA == 0)
						{
							sbWarning.append("MW field equals 0 for Limiting Agent material " + invItemMaterialName_LA + " for step " + stepName + ".\n");
							
							generalUtilLogger.logWriter(LevelType.WARN,
									"he calculation was not performed for LIMITING AGENT Material "+invItemMaterialName_LA+" on Dencity_LA*Concentration_LA%/MW_LA. Dencity LA = "
											+ normalDensityInf_LA + "; Concentration LA = " + concentration_LA + "; MW LA = "
											+normalMWInf_LA + ". The dividing parameter [MW] in the calculation is 0."
											+ " Run " + runNumber +", Step " + stepName + "(id="+stepId+")",
									ActivitylogType.Calculation, formId);
						}
						else
						{
							Double DensConcMW_LA = normalDensityInf_LA * concentration_LA/normalMWInf_LA;
							sbInfo.append("Formula: Dencity_LA*Concentration_LA%/MW_LA.\n</br>Normal Args: "
								     + String.valueOf(normalDensityInf_LA)+"*"+String.valueOf(concentration_LA) + "/" + String.valueOf(normalMWInf_LA) +" = "+DensConcMW_LA+".\n</br>"
								);
							
							// rateLA = totalCompoundsVolumeRate/(1 + (Equivalent_B*rateDCM_A/rateDCM_B) +...+ (Equivalent_N*rateDCM_A/rateDCM_N))
							Double rateLA = 0.0; 
							Double equivalentSum = 0.0;
							int index = 0;
							Map<String,String> mapDensConcMW = new HashMap<String,String>();
							
							// get data per material
							for (Map<String, Object> materialData : listOfMap) 
							{
								index++;
								// ignore first row, used for Limiting Agent in above code
								if(index == 1)continue;
								
								String invItemMaterialName = (materialData.get("INVITEMMATERIALNAME") != null?materialData.get("INVITEMMATERIALNAME"):"").toString();
								String invItemMaterialId = (materialData.get("INVITEMMATERIALID") != null?materialData.get("INVITEMMATERIALID"):"").toString();
								sbInfo.append("------------------------------------------\n</br>");
								sbInfo.append("Calculation for Material "+invItemMaterialName+":\n</br>");
								
								String equivalent = (materialData.get("MATERIALREFEQUIVALENT") != null?materialData.get("MATERIALREFEQUIVALENT"):"").toString();
								if(equivalent.equals(""))
								{
									sbWarning.append("Equivalent field is empty for material " + invItemMaterialName + " step " + stepName + ".\n");
									
									generalUtilLogger.logWriter(LevelType.WARN,
											"The calculation was not performed for Material "
													+invItemMaterialName+" on  Equivalent_N*Dencity_LA*Concentration_LA%/MW_LA / Dencity_N*Concentration_N%/MW_N. Args: Equivalent = "+ equivalent+"."
													+" Run " + runNumber +", Step " + stepName + "(id="+stepId+")",
											ActivitylogType.Calculation, formId);
									doCalc = false;
									break;
								}
				
								String purityInf = (materialData.get("PURITYINF") != null?materialData.get("PURITYINF"):"").toString();
								String purityInfUOM = (materialData.get("PURITYUOM_ID_INF") != null?materialData.get("PURITYUOM_ID_INF"):"").toString();
								String mwInf = (materialData.get("MWINF") != null?materialData.get("MWINF"):"").toString();
								String mwInfUOM = (materialData.get("MWUOM_ID_INF") != null?materialData.get("MWUOM_ID_INF"):"").toString();
								String densityInf = (materialData.get("DENSITYINF") != null?materialData.get("DENSITYINF"):"").toString();
								String densityInfUOM = (materialData.get("DENSITYUOM_ID_INF") != null?materialData.get("DENSITYUOM_ID_INF"):"").toString();
								
								sbInfo.append("Calculate normal Density:\n</br>");
								Double normalDensityInf = getCustomNormalNumber(densityInf, densityInfUOM, defaultDensityUomId, sbInfo);
								sbInfo.append("Normal Density = "+normalDensityInf+".\n</br>");
								Double normalPurityInf = getNormalNumber(purityInf, purityInfUOM, (double) 100);
								sbInfo.append("Calculate normal MW:\n</br>");
								Double normalMWInf = getCustomNormalNumber(mwInf, mwInfUOM, defaultMWUomId, sbInfo);
								sbInfo.append("Normal MW = "+normalMWInf+".\n</br>");
								Double concentration = 0.0;
								
								if(!isFirstStep){
									sbInfo.append("Calculate concentration:\n</br>");
									String isSameProductExist = generalDao.selectSingleStringNoException("select distinct 1 from fg_s_materialref_v t where t.INVITEMMATERIAL_ID = '"
												+ invItemMaterialId + "' and t.PARENTID = '" + prevStepId
												+ "' and t.tabletype = 'Product'");
									if (generalUtil.getNull(isSameProductExist).equals("1")) {
										concentration = calcConcentration(invItemMaterialId, normalMWInf,
										massRatePrevStep, prevStepId, runNumber);
									} else {
										concentration = normalPurityInf;
										if (normalPurityInf == null) {
											sbWarning.append("Purity/Purity UOM field is empty for material "
												+ invItemMaterialName + " for step " + stepName + ".\n");
										}
									}
									sbInfo.append("Concentration = " + concentration + ".\n</br>");
								}else{
									concentration = normalPurityInf;
									if (normalPurityInf == null) {
										sbWarning.append("Purity/Purity UOM field is empty for material "
											+ invItemMaterialName + " for step " + stepName + ".\n");
									}
									sbInfo.append("Concentration = " + concentration + ".\n</br>");
								}
								
								if(normalDensityInf == null || concentration == null /*|| concentration == 0*/ || normalMWInf == null)
								{
									if(normalDensityInf == null)
									{
										sbWarning.append("Density/Density UOM field is empty for material " + invItemMaterialName + " for step " + stepName + ".\n");
									}
									if(normalMWInf == null)
									{
										sbWarning.append("MW/MW UOM field is empty for material " + invItemMaterialName + " for step " + stepName + ".\n");
									}
									generalUtilLogger.logWriter(LevelType.WARN,
											"The calculation was not performed for Material "+invItemMaterialName+" on Dencity*Concentration%/MW. Args: Dencity = "
													+ normalDensityInf + "; Concentration = " + concentration + "; MW = "+normalMWInf + "."
													+" Run " + runNumber +", Step " + stepName + "(id="+stepId+")",
											ActivitylogType.Calculation, formId);
									doCalc = false;
									break;
								}
								else if(normalMWInf == 0)
								{
									sbWarning.append("MW/MW UOM field equals 0 for material " + invItemMaterialName + " for step " + stepName + ".\n");
									
									generalUtilLogger.logWriter(LevelType.WARN,
											"The calculation was not performed for Material "+invItemMaterialName+" on Dencity*Concentration%/MW. Dencity = "
													+ normalDensityInf + "; Concentration = " + concentration + "; MW = "
													+normalMWInf + ". The dividing parameter [MW] in the calculation is 0."
													+" Run " + runNumber +", Step " + stepName + "(id="+stepId+")",
											ActivitylogType.Calculation, formId);
									doCalc = false;
									break;
								}
								Double DensConcMW = normalDensityInf * concentration/normalMWInf;
								sbInfo.append("Formula: Dencity*Concentration%/MW.\n</br>Normal Args: "
									     + String.valueOf(normalDensityInf)+"*"+String.valueOf(concentration) + "/" + String.valueOf(normalMWInf) +" = "+DensConcMW+".\n</br>"
									);
								if(DensConcMW == 0)
								{
									sbWarning.append("[Dencity*Concentration%/MW] equals 0 for material " + invItemMaterialName + " for step " + stepName + ".\n");
									
									generalUtilLogger.logWriter(LevelType.WARN,
											"he calculation was not performed for Material "+invItemMaterialName+" on Equivalent_N*Dencity_LA*Concentration_LA%/MW_LA / Dencity_N*Concentration_N%/MW_N. Equivalent_N = "
													+ equivalent + "; Dencity_LA*Concentration_LA%/MW_LA = " + DensConcMW_LA + "; Dencity_N*Concentration_N%/MW_N = "
													+DensConcMW + ". The dividing parameter [Dencity_N*Concentration_N%/MW_N] in the calculation is 0."
													+" Run " + runNumber +", Step " + stepName + "(id="+stepId+")",
											ActivitylogType.Calculation, formId);
									doCalc = false;
									break;
								}
								Double result = Double.parseDouble(equivalent)*DensConcMW_LA/DensConcMW;
								equivalentSum += result;
								sbInfo.append("Formula: Equivalent_N*Dencity_LA*Concentration_LA%/MW_LA / Dencity_N*Concentration_N%/MW_N.\n</br> Args: "
										 		+ equivalent + "*" + DensConcMW_LA + "/" + DensConcMW + " = "+result+".\n</br>");			
								
								String materialRefId = (materialData.get("MATERIALREFID") != null?materialData.get("MATERIALREFID"):"").toString();
								mapDensConcMW.put(materialRefId, String.valueOf(DensConcMW));
							}
							if(doCalc)
							{
								sbInfo.append("------------------------------------------\n</br>");
								rateLA = totalCompoundsVolumeRate/(1 + equivalentSum);
								sbInfo.append("***Calculate Limiting Agent Material Rate: ***\n</br>"
							+ "***Rate_LA = Total Compounds Volume rate/(1 + (Equivalent_B*Dencity_LA*Concentration_LA%/MW_LA/Dencity_B*Concentration_B%/MW_B) +...+ (Equivalent_N*Dencity_LA*Concentration_LA%/MW_LA/Dencity_N*Concentration_N%/MW_N)).\n</br>"
							+ " Args: Total Compounds Volume rate = " +  totalCompoundsVolumeRate
							+ "; sum of all (Equivalent_N*Dencity_LA*Concentration_LA%/MW_LA/Dencity_N*Concentration_N%/MW_N) = " + equivalentSum + ".\n</br>");
								sbInfo.append("Limiting Agent Material Rate = "+rateLA+ ".\n</br>");
								
								sbInfo.append("**Main calculation of each material rate by Formula:\n</br>"
										+ "***Rate_N = Rate_LA*(Dencity_LA*Concentration_LA%/MW_LA)*Equivalent_N/(Dencity_N*Concentration_N%/MW_N).\n</br>");
								
								int index2 = 0;
								for (Map<String, Object> entry : listOfMap) {
									
									Double rateN = 0.0;
									Double rateN_final = 0.0;
									index2++;
									String invItemMaterialName = (entry.get("INVITEMMATERIALNAME") != null?entry.get("INVITEMMATERIALNAME"):"").toString();
									sbInfo.append("------------------------------------------\n</br>");
									if(index2 == 1)
									{
										rateN = rateLA;
										sbInfo.append("Rate for Limiting Agent material '"+invItemMaterialName+"' = " + String.valueOf(rateN) + ".\n</br>");
									}
									else
									{
										String materialRefId = (entry.get("MATERIALREFID") != null?entry.get("MATERIALREFID"):"").toString();
										rateN = rateLA * DensConcMW_LA * Double.parseDouble(entry.get("MATERIALREFEQUIVALENT").toString()) / Double.parseDouble(mapDensConcMW.get(materialRefId));
										sbInfo.append("Rate for material '"+invItemMaterialName+"' = " + String.valueOf(rateN) + ".\n</br>");
									}
									String purityInf = (entry.get("PURITYINF") != null?entry.get("PURITYINF"):"").toString();
									String purityInfUOM = (entry.get("PURITYUOM_ID_INF") != null?entry.get("PURITYUOM_ID_INF"):"").toString();
									String mwInf = (entry.get("MWINF") != null?entry.get("MWINF"):"").toString();
									String mwInfUOM = (entry.get("MWUOM_ID_INF") != null?entry.get("MWUOM_ID_INF"):"").toString();
									String densityInf = (entry.get("DENSITYINF") != null?entry.get("DENSITYINF"):"").toString();
									String densityInfUOM = (entry.get("DENSITYUOM_ID_INF") != null?entry.get("DENSITYUOM_ID_INF"):"").toString();
									String materialRateType = (entry.get("MATERIALRATETYPE") != null?entry.get("MATERIALRATETYPE"):"").toString();
									String materialRateUOM = (entry.get("MATERIALRATEUOM") != null?entry.get("MATERIALRATEUOM"):"").toString();
									String materialRateUOMName = (entry.get("MATERIALRATEUOM_NAME") != null?entry.get("MATERIALRATEUOM_NAME"):"").toString();
									Double normalDensityInf = getCustomNormalNumber(densityInf, densityInfUOM, defaultDensityUomId, sbInfo);
									
									/*Double rateNVolumeNormal = getNormalNumber(String.valueOf(rateN), defaultVolumeUomId);
									sbInfo.append("Convert Rate to Default Volume UOM = " + String.valueOf(rateNVolumeNormal) + ".\n</br>");									
									*/sbInfo.append("Current material Rate type = " + materialRateType + "; Current material Rate UOM = " + materialRateUOMName + ".\n</br>");									
																		
									switch(materialRateType.toLowerCase()) {
										case "volume":
											if(materialRateUOM.equals(defaultVolumeUomId))
											{
												//rateN_final = rateNVolumeNormal;
												rateN_final = rateN;
											}
											else
											{
												rateN_final =  getCustomNormalNumber(String.valueOf(rateN),defaultVolumeUomId, materialRateUOM,new StringBuilder());
												
											}
											break;
										case "quantity":	
											sbInfo.append("Calculate normal Density:\n</br>");
											//Double normalDensityInf = getCustomNormalNumber(densityInf, densityInfUOM, defaultDensityUomId, sbInfo);
											sbInfo.append("Normal Density = "+normalDensityInf+".\n</br>");
											Double rateNQuantityNormal = rateN * normalDensityInf;
											sbInfo.append("Formula to calculate Quantity Rate: Volume Rate * Density.</br>"
													+ " Normal Args: "+ rateN + " * " + normalDensityInf + " = "+rateNQuantityNormal+".\n</br>");
												
											rateN_final = getCustomNormalNumber(String.valueOf(rateNQuantityNormal),defaultQuantityUomId, materialRateUOM,new StringBuilder());
											
											break;
										case "mole":
											sbInfo.append("Calculate normal MW:\n</br>");
											Double normalMWInf = getCustomNormalNumber(mwInf, mwInfUOM, defaultMWUomId, sbInfo);
											sbInfo.append("Normal MW = "+normalMWInf+".\n</br>");
											sbInfo.append("Calculate normal Density:\n</br>");
											Double normalDensityInfQ = getCustomNormalNumber(densityInf, densityInfUOM, defaultDensityUomId, sbInfo);
											sbInfo.append("Normal Density = "+normalDensityInfQ+".\n</br>");
											Double normalPurityInf = getNormalNumber(purityInf, purityInfUOM, (double) 100);
											
											Double rateNQuantityNorm = rateN * normalDensityInfQ;
											Double rateNMoleNormal = rateNQuantityNorm * normalPurityInf / normalMWInf;
											
											sbInfo.append("Formula to calculate Mole Rate: (Quantity Rate * Purity/100)/MW.</br> "
													+ "Normal Args: "+ rateNQuantityNorm + " * " + normalPurityInf/100 + " / " + normalMWInf + " = "+rateNMoleNormal+".\n</br>");
											String rate_final_str = getFromNormalNumber(String.valueOf(rateNMoleNormal), materialRateUOM,new StringBuilder());
											if(!generalUtil.getNull(rate_final_str).isEmpty()){
												rateN_final = Double.parseDouble(rate_final_str);
											}
											break;
									}
									
									sbInfo.append("Final Rate value converted to " + materialRateType + " = " + rateN_final + ".\n</br>");
									
									String sql = "update FG_S_EXPRUNPLANNING_PIVOT t \n"
												+ "set t.MATERIALREFVALUE = '"+ rateN_final +"' \n"
												+"    ,t.dencity = '"+normalDensityInf+"'  \n"
												+"    ,t.dencityuom = '"+defaultDensityUomId+"' \n"
												+"    ,t.purity = '"+purityInf+"'  \n"
												+"    ,t.purityuom = '"+purityInfUOM+"' \n"
												+"    ,t.mw = '"+mwInf+"' \n"
												+"    ,t.mwuom = '"+mwInfUOM+"' \n"
												+" where  t.MATERIALREFID = '"+entry.get("MATERIALREFID").toString()+"'\n"
												+" and t.formid = '" + entry.get("formid").toString() + "' \n"
												+" and t.sessionid is null and t.active = 1";
									formSaveDao.updateStructTableByFormId(sql, "FG_S_EXPRUNPLANNING_PIVOT",Arrays.asList("MATERIALREFVALUE"), entry.get("formid").toString()); 
								}
							}
						}
					}
				}
				}
			}
			sbInfo.append("--------------------END---------------------\n</br>");
			return sbWarning.toString();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error in Runs Planning calculations";
		}
	}
	
	private double calcConcentration(String invItemMaterial_id, Double mw, Double massRate, String parentId, String runNumber/*, StringBuilder sbInfo*/){
		StringBuilder sbInfo = new StringBuilder();
		// concPrevStep = calcConcentration(invItemMaterialId_LA, normalMWInf_LA, massRatePrevStep,prevStepId,runNumber);
		String stepName = formDao.getFromInfoLookup("Step", LookupType.ID,
				parentId, "name");
	    String materialName = formDao.getFromInfoLookup("InvItemMaterial", LookupType.ID,
	    		invItemMaterial_id, "name");
		sbInfo.append("\n ****Intermediate Calculation - Calculat concentration for Run " + runNumber +", Step " + stepName + ", Material "+ materialName +".****\n</br>");
		sbInfo.append("**Formula:Concentration % = Mole rate of product*MW/ Mass rate of product*100%  \n</br>");
		sbInfo.append("Mass rate of product = Sum (Mass rate of all reagents and solvents) =" +massRate+ "\n</br>");
		Double concentration = 0.0;
		Double moleRate = 0.0;
		Double yield = 0.0;
		String yield_str = generalDao.selectSingleStringNoException("select distinct yield from fg_s_materialref_v t where t.INVITEMMATERIAL_ID = '"+invItemMaterial_id+"' and t.PARENTID = '"+parentId+"' and t.TABLETYPE = 'Product' and nvl(t.active,1) = 1 and sessionid is null and rownum<=1");
		if(generalUtil.getNull(yield_str).isEmpty()){
			return concentration;
		}else{
			yield = Double.parseDouble(yield_str);
		}
		/* Mole rate of product is calculated in 2 optional cases.

           1)	Case 1: If the Products and Reagents are identical (Product & Reagents from the current step)
                Mole rate of product = Mole rate of identical reagent * yield / 100%
           2)	Case 2: If the Products and Reagents are different (Product & Reagents from the current step)
                Mole rate of product = Mole rate of limiting agent * yield / 100%*/
		
		String rateFormId="";
		String productLikeReagents = generalDao.selectSingleStringNoException("select max(formId) from fg_s_materialref_v t where t.INVITEMMATERIAL_ID = '"+invItemMaterial_id+"' and t.PARENTID = '"+parentId+"' and t.TABLETYPE = 'Reactant' and nvl(active,'1')='1' and sessionid is null");
		if(!generalUtil.getNull(productLikeReagents).isEmpty()){
			rateFormId =productLikeReagents;
			sbInfo.append("Formula to calculate Mole rate of product =  Mole rate of identical reagent * yield / 100 \n</br>");
		}else{
			String formId_LA = generalDao.selectSingleStringNoException("select distinct formId from fg_s_materialref_v t where t.PARENTID = '"+parentId+"' and t.LIMITINGAGENT='1' and  nvl(active,'1')='1' and sessionid is null");
			rateFormId = formId_LA;
			sbInfo.append("Formula to calculate  Mole rate of product = Mole rate of limiting agent * yield / 100  \n</br>");
			
		}
         Double moleRateReagents = convertToMoleRateByRefId(rateFormId,runNumber,sbInfo);
         if(moleRateReagents != null){
        	 moleRate = moleRateReagents *yield/100;
        	 sbInfo.append(" Normal Args(Mole rate of product): "+ moleRateReagents + " * " + yield  + ") / 100 = "+moleRate+".\n</br>");
         }
         if(moleRate != null  && mw != null){
        	 concentration = moleRate*mw/massRate*100;
         }
         sbInfo.append("Concentration = "+moleRate +" * " + mw +" / "+massRate+ " * 100 = " +concentration+".\n</br>");
         generalUtilLogger.logWriter(LevelType.INFO, sbInfo.toString() ,ActivitylogType.Calculation, "");
 		return concentration;
	}
	
	private double convertToMoleRateByRefId(String materialRef_id,String runNumber,StringBuilder sbInfo){
		Double moleRate = 0.0;
		//StringBuilder sbInfo = new StringBuilder();
		Map<String, String> materialData = generalDao.sqlToHashMap("select * from fg_i_expstep_runs_calc_v where MATERIALREFID='"+materialRef_id+"' and runnumber ='"+runNumber+"'");	
		
		String defaultDensityUomId = (materialData.get("DEFAULT_DENSITY_UOM_ID") != null?materialData.get("DEFAULT_DENSITY_UOM_ID"):"").toString();		
		String defaultMWUomId = (materialData.get("DEFAULT_MW_UOM_ID") != null?materialData.get("DEFAULT_MW_UOM_ID"):"").toString();	
		String defaultVolumeUomId = (materialData.get("DEFAULT_VOLUME_UOM_ID") != null?materialData.get("DEFAULT_VOLUME_UOM_ID"):"").toString();										
		String defaultQuantityUomId = (materialData.get("DEFAULT_QUANTITY_UOM_ID") != null?materialData.get("DEFAULT_QUANTITY_UOM_ID"):"").toString();	
		
		String materialRateType = (materialData.get("MATERIALRATETYPE") != null?materialData.get("MATERIALRATETYPE"):"").toString();
		String materialRateUOM = (materialData.get("MATERIALRATEUOM") != null?materialData.get("MATERIALRATEUOM"):"").toString();
		String materialRateValue = (materialData.get("MATERIALREFVALUE") != null?materialData.get("MATERIALREFVALUE"):"").toString();
		String densityInf = (materialData.get("DENSITYINF") != null?materialData.get("DENSITYINF"):"").toString();
		String densityInfUOM = (materialData.get("DENSITYUOM_ID_INF") != null?materialData.get("DENSITYUOM_ID_INF"):"").toString();
		String purityInf = (materialData.get("PURITYINF") != null?materialData.get("PURITYINF"):"").toString();
		String purityInfUOM = (materialData.get("PURITYUOM_ID_INF") != null?materialData.get("PURITYUOM_ID_INF"):"").toString();
		String mwInf = (materialData.get("MWINF") != null?materialData.get("MWINF"):"").toString();
		String mwInfUOM = (materialData.get("MWUOM_ID_INF") != null?materialData.get("MWUOM_ID_INF"):"").toString();
		
		Double normalMWInf = getCustomNormalNumber(mwInf, mwInfUOM, defaultMWUomId, new StringBuilder());
		Double normalPurityInf = getNormalNumber(purityInf, purityInfUOM, (double) 100);
		Double normalDensityInf = getCustomNormalNumber(densityInf, densityInfUOM, defaultDensityUomId, new StringBuilder());
		
		
		Double rateNormal = getNormalNumber(String.valueOf(materialRateValue), materialRateUOM);
		/*Formula to calculate Quantity Rate: Volume Rate * Density
          Formula to calculate Mole Rate: (Quantity Rate * Purity)/MW
        */
		switch(materialRateType.toLowerCase()) {
		case "volume":
			//Mole Rate: (Volume Rate * Density * Purity)/MW
			if (rateNormal != null) {
				String volume = getFromNormalNumber(rateNormal.toString(), defaultVolumeUomId, sbInfo).toString();
				if (!generalUtil.getNull(volume).isEmpty()) {
					rateNormal = Double.parseDouble(volume);
				}
			}
			
			sbInfo.append("Calculate normal mole rate:\n</br>");
			sbInfo.append("Normal MW = "+normalMWInf+".\n</br>");
			sbInfo.append("Normal Density = "+normalDensityInf+".\n</br>");
			sbInfo.append("Normal Purity = "+normalPurityInf+".\n</br>"); 
			
			if(normalMWInf != null && normalMWInf != 0 && rateNormal!=null){
				moleRate = (rateNormal * normalDensityInf*(normalPurityInf/100))/normalMWInf;
			}
			
			sbInfo.append("Formula to calculate Mole Rate: (Volume Rate * Density * Purity)/MW.</br>"
					+ " Normal Args: ("+ rateNormal + " * " + normalDensityInf + " * " + (normalPurityInf/100) + ") / "+normalMWInf+" = "+moleRate+".\n</br>");
			break;
		case "quantity":
			sbInfo.append("Calculate normal mole rate:\n</br>");
			sbInfo.append("Normal MW = "+normalMWInf+".\n</br>");
			sbInfo.append("Normal Purity = "+normalPurityInf+".\n</br>");
			if (rateNormal != null) {
			    String qnt = getFromNormalNumber(rateNormal.toString(), defaultQuantityUomId,sbInfo);
			    if(!generalUtil.getNull(qnt).isEmpty()){
			    	rateNormal = Double.parseDouble(qnt);
			    }
			}
			if(normalMWInf != null && normalMWInf != 0){
				moleRate = rateNormal*normalPurityInf/100/normalMWInf;
			}
			
			sbInfo.append("Formula to calculate Mole Rate: (Quantity Rate * Purity/100)/MW.</br>"
					+ " Normal Args: ( "+ rateNormal + " * " + normalPurityInf + "/100 / "+normalMWInf+" = "+moleRate+".\n</br>");
			break;
		case "mole":
			if(rateNormal != null){
				moleRate = rateNormal;
			}
			sbInfo.append("Mole Rate = moleRate.\n</br>");
			break;
	}
		return moleRate;
	}

}
