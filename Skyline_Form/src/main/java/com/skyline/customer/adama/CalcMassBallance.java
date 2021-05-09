package com.skyline.customer.adama;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;

@Component
public class CalcMassBallance extends CalcBasic {
	// private static final Logger logger = LoggerFactory.getLogger(GeneralDaoImp.class);

	/**
	 * calc MassBallance fields
	 * Parameters required from the map: productMW, PRODUCTMWUOM_ID ,weight, WEIGHTUOM_ID, precOfProduct, limitingReactantMoles, REACTANTMOLESUOM_ID, equivalentPerMole, PERMOLEUOM_ID
	 * 
	 * @param elementValueMap
	 */
	@Override
	public String doCalc(String api, String mainArgCode, String mainArgVal, Map<String, String> elementValueMap,
			String[] apiCodesArray, String[] elementsMatchArray, String formCode, String formId, String userId) {

		if (formCode.equals("MassBalanceRef")) {
			// moles

			Map<String, String> productMap = elementValueMap;
			try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Mole Calculation =></br>");
				Double normalMWProduct = getNormalNumber(productMap.get("productMW"),
						productMap.get("PRODUCTMWUOM_ID"));
				Double normalWeight = getNormalNumber(elementValueMap.get("weight"),
						elementValueMap.get("WEIGHTUOM_ID"));

				if (normalWeight == null || normalMWProduct == null || normalMWProduct == 0.0) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed productMW=" + elementValueMap.get("productMW")
									+ ";weight = " + elementValueMap.get("weight"),
							ActivitylogType.Calculation, formId);
				} else {
					sbInfo.append("Formula: (Weight/Product_MW)*(Percentage_of_Product/100).</br> Normal Args:("
							+ String.valueOf(normalWeight) + "/" + normalMWProduct + ")*("
							+ elementValueMap.get("precOfProduct") + "/100)" + "</br>");
					elementValueMap.put("moles",
							getFromNormalNumber(
									Double.toString((normalWeight / normalMWProduct)
											* ((Double.parseDouble(elementValueMap.get("precOfProduct")) / 100))),
									elementValueMap.get("MOLESUOM_ID"), sbInfo));
				}
			} catch (Exception ex) {
				generalUtilLogger.logWriter(LevelType.ERROR,
						"The calculation was not performed on moles. weight =" + elementValueMap.get("weight")
								+ "; productMW = " + elementValueMap.get("productMW"),
						ActivitylogType.Calculation, formId);
			}

			// Yield Loss
			/*
			 * YL=(n_s×EQ)/n_p
			 * where:
			 * YL – Yield Loss
			 * ns – Mole of current Stream
			 * np – Mole of Product = Limiting Agent Mole
			 * EQ – Equivalent per Mole
			 */
			/* TODO remove: NOT in use because of changes in Experiment screen from version 1.555
			 * 
			 * try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Yield Loss Calculation =></br>");
				Double normalMole = getNormalNumber(elementValueMap.get("moles"), elementValueMap.get("MOLESUOM_ID"));
				// List<String> moleProduct=generalDao.getListOfStringBySql("select t.limitingReactantMoles,t.REACTANTMOLESUOM_ID from fg_s_experiment_pivot t where t.formId = '"+elementValueMap.get("parentId")+"'");
				if (productMap.get("limitingReactantMoles") != null
						&& !productMap.get("limitingReactantMoles").isEmpty()) {
					Double normalMoleProduct = getNormalNumber(productMap.get("limitingReactantMoles"),
							productMap.get("REACTANTMOLESUOM_ID"));
					// List<String> equivalentPerMole=generalDao.getListOfStringBySql("select t.equivalentPerMole,t.PERMOLEUOM_ID from fg_s_experiment_pivot t where t.formId = '"+elementValueMap.get("parentId")+"'");
					Double normalEquivalentPerMole = getNormalNumber(productMap.get("equivalentPerMole"),
							productMap.get("PERMOLEUOM_ID"));
					if (normalEquivalentPerMole == null || normalMoleProduct == null || normalMoleProduct == 0.0) {
						generalUtilLogger.logWriter(LevelType.WARN,
								"The calculation was not performed productMW=" + elementValueMap.get("productMW")
										+ ";weight = " + elementValueMap.get("weight"),
								ActivitylogType.Calculation, formId);
					} else {
						sbInfo.append("Formula: mole*Equivalent_Per_Mole*100/Limiting_reactant_mole .</br> Normal Args:"
								+ String.valueOf(normalMole) + "*" + normalEquivalentPerMole + "*100/"
								+ normalMoleProduct + "</br>");
						elementValueMap.put("yieldLoss",
								getFromNormalNumber(
										Double.toString(
												(normalMole * normalEquivalentPerMole) * 100 / normalMoleProduct), //multiply in 100 for percentage result
										elementValueMap.get("YIELDLOSSUOM_ID"), sbInfo));
					}
				}
			} catch (Exception ex) {
				generalUtilLogger.logWriter(LevelType.ERROR,
						"The calculation was not performed on yieldLoss.  product Mole="
								+ elementValueMap.get("limitingReactantMoles") + "; equivalent per mole = "
								+ productMap.get("limitingReactantMoles") + "; mole = " + elementValueMap.get("moles"),
						ActivitylogType.Calculation, formId);
			}*/
			// there is no reactant
			// TODO:Waiting for Igor's decision - there is no 'Mole' field in Product form, and Reactant is not mandatory, so there could be a situation of missing parameter. Need to decide how to handle this situation.
			// else 
		} else if (formCode.equals("Experiment") || formCode.equals("ExperimentCP")) {

			// Total Chemical Yield

			/*
			 * Total Chemical Yield shall be calculated according to the following formula:
			 * TCY=Sum(〖YL〗_S1:〖YL〗_Sn )+Yp
			 * where:
			 * TCY – Total Chemical Yield
			 * YLs – Yield Loss of Stream
			 * Yp – Yield of Product
			 */
			/*	TODO remove: NOT in use because of changes in Experiment screen from version 1.555
			 * 
			 * try {
				StringBuilder sbInfo = new StringBuilder();
				sbInfo.append("Total Chemical Yield Calculation =></br>");
				if (elementValueMap.get("yield") != null && !elementValueMap.get("yield").isEmpty()) {
					String yieldUom = elementValueMap.get("YIELDUOM_ID");
					Double normalYieldProduct = getNormalNumber(elementValueMap.get("yield"), yieldUom, (double) 1);//ab 060318- updated yield equals to 1 as default-FDS Organic page 131
					Double normalTotaYieldLoss = Double.parseDouble(generalDao.selectSingleString(
							"select nvl(sum(fg_get_num_normal(t.yieldLoss,t.YIELDLOSSUOM_ID)),0) from fg_s_massbalanceref_all_v t where t.parentId = '"
									+ formId + "'" + generalUtilFormState.getWherePartForTmpData("massbalanceref",
											formId elementValueMap.get("parentId"))// gets all the mass balance under the cureent experiment
					));
					sbInfo.append("Formula: SUM(Yield_Loss)+Product_Yield.</br> Normal Args:"
							+ String.valueOf(normalTotaYieldLoss) + "+" + normalYieldProduct + "</br>");
					String totalChemicalYield = getFromNormalNumber(
							Double.toString(normalTotaYieldLoss + normalYieldProduct), yieldUom, sbInfo);
					//elementValueMap.put("totalChemicalYield", totalChemicalYield);
					formSaveDao.updateStructTableByFormId(
							"update FG_S_EXPERIMENT_PIVOT set totalChemicalYield = '" + totalChemicalYield
									+ "' where formid = '" + formId + "'",
							"FG_S_EXPERIMENT_PIVOT", Arrays.asList("totalChemicalYield"), formId);//ab 08042018
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.ERROR,
						"The calculation was not performed on totalChemicalYield. yield product="
								+ elementValueMap.get("yield"),
						ActivitylogType.Calculation, formId);
			}*/

		}
		return "";
	}

}
