package com.skyline.form.dal;

import java.util.List;

import com.skyline.form.bean.DataBean;

public interface FormResourceLookupDao {
	
    /**
     * 
     * @param type - resource BeanType name
     * @param formCode
     * @return List of DataBean 
     * Note: The parameters type / formCode support SQL % wildcard char
     */
	public List<DataBean> resourceLookUp(String type, String formCode);

}
