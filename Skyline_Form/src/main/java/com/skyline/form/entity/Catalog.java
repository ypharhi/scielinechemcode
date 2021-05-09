package com.skyline.form.entity;

import java.util.List;
import java.util.Map;

import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.service.FormTempData;

public abstract class Catalog extends Entity implements CatalogInterface {

	@Override
	public abstract String getItem(long stateKey, Map<String, String> formCatalogMap, FormTempData formTempDataMap,
			String item, StringBuilder info);

	@Override
	public abstract List<String> getItemArray(long stateKey, Map<String, String> formCatalogMap,
			FormTempData formTempDataMap, String item, StringBuilder info);
 
	@Override
	public List<DataBean> getImpResourceForFormBuilder(String formCode, String impCode, String initVal) {
		List<DataBean> dataBeanList = super.getImpResourceForFormBuilder(formCode, impCode, initVal); //super return the bean
		dataBeanList.add(new DataBean(impCode, impCode, BeanType.CATALOG_IMP_CODE, "Info: " + impCode));
		return dataBeanList;
	}
}
