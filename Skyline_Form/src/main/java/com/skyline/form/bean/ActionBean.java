package com.skyline.form.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * A request bean. Using as a wrapper to the DataBean
 * Note!!!: in order to be used in the controllers ->
 * "...
 * @RequestMapping(value = .... = { RequestMethod.GET, RequestMethod.POST })
	public ... (@RequestBody ActionBean actionBean, HttpServletRequest request) { ..."
 * This class must have:
 * - constructor that match the json object we send in the client
 * - camel case getter setter 
 * - toJsonString (!?)
 * It is very sensitive! without this match the controller will not get the call!!!
 */
public class ActionBean implements Serializable { //

	private static final long serialVersionUID = 1L;

	private String action;

	private String errorMsg;

	private List<DataBean> data;

	public ActionBean(String action, List<DataBean> data, String errorMsg) {
		super();
		this.action = action;
		this.data = data;
		this.errorMsg = errorMsg;
	}

	public ActionBean() {
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<DataBean> getData() {
		return data;
	}

	public void setData(List<DataBean> data) {
		this.data = data;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
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
}
