package com.skyline.form.dal;

import java.util.Map;

import com.skyline.form.bean.DataBean;

public interface FormSaveElementDao {

	String saveFormTestConfigData(Map<String, String> paramMonitoringValueMap,
			Map<String, String> paramMonitoringUomMap, String formId);

	String saveDynamicParams(String data);

	String saveFormMonitoParamData(Map<String, String> paramMonitoringValueMap,
			Map<String, String> paramMonitoringUomMap, Map<String, String> paramMonitoringFormIdMap, String formId,
			String jsonSource);

	String addMonitorParam(String newformId, String string);

	String saveRichText(String formCode, DataBean dataBean, boolean b);

	String saveMonitoringParam(String mpFormId, String newVal, String mpName, String oldVal, boolean isUom);

	String saveDiagram(String formCode, DataBean dataBean, String elementId) throws Exception;

}
