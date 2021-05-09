package com.skyline.form.entity;

import java.util.List;

import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;

public abstract class Layout extends Entity implements LayoutInterface {

	@Override
	public abstract String getJspName();
	
	@Override
	public List<DataBean> getImpResourceForFormBuilder(String formCode, String impCode, String initVal) {
		List<DataBean> dataBeanList = super.getImpResourceForFormBuilder( formCode,  impCode,  initVal); //super return the bean
		dataBeanList.add(new DataBean(impCode, impCode, BeanType.LAYOUT_IMP_CODE, "Info: " + impCode));
		return dataBeanList;
	}

}
