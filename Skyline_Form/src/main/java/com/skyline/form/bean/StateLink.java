package com.skyline.form.bean;

public class StateLink {
	private String from;
	private String to;
	private String validation;
	
	/**
	 * 
	 * @return the satte id the link starts from
	 */
	public String getFromState()
	{
		return from;
	}
	
	/**
	 * 
	 * @return the state id the link ends in
	 */
	public String getToState()
	{
		return to;
	}
	
	public String getValidation()
	{
		return validation;
	}
}
