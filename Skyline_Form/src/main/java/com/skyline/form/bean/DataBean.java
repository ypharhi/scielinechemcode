package com.skyline.form.bean;

import java.io.IOException;
import java.io.Serializable;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * DataBean is a general data bean type that its: value / code / info properties are fill with different data in different context. In the BeanType (enum) we summarize the various usages of this bean
 * @author YPharhi
 *
 */
public class DataBean implements Serializable, Comparable<DataBean> { //we use it when we need more information on the spring passing between entities 

	@Override
	public String toString() {
		return "DataBean [id=" + id + ", code=" + code + ", val=" + val + ", type=" + type + ", info=" + info + "]";
	}

	private static final long serialVersionUID = 1L;
	private Long id;
	private String code;
	private String val;
	private BeanType type;
	private String info;

	public DataBean() {
	}

	public DataBean(Long id, BeanType type, String code, String val, String info) {
		super();
		this.id = id;
		this.type = type;
		this.code = code;
		this.val = val;
		this.info = info;
	}

	public DataBean(String code, String val, BeanType type, String info) {
		//new DataBean("code", "val", "type", "info")
		super();
		this.code = code;
		this.val = val; // FROM LABEL
		this.type = type;
		this.info = info;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public BeanType getType() {
		return type;
	}

	public void setType(BeanType type) {
		this.type = type;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String toJsonString() {
		String toReturn = "";
		try {
			toReturn = new ObjectMapper().writeValueAsString(this);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toReturn;
	}
 
	@Override
	public int compareTo(DataBean o) {
		return this.val.compareTo(o.val);
	}

}
