package com.skyline.form.entity;

import java.util.List;

import com.skyline.form.bean.DataBean;

public interface EntityInterface {

	public boolean getActive(); //Lead programmer will active the imp when the programmer is done in the spring-bean

	public void setActive(boolean active);

	public String getInitVal();

	public String getInitSchemaVal();

	public String getImpCode();
	
	public String getFormCode();

	public String init(long stateKey, String formCode, String impCode, String initVal);
	
	public List<DataBean> getImpResourceForFormBuilder(String formCode, String impCode, String initVal);

}
