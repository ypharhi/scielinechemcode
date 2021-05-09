package com.skyline.form.bean;

public class Result {
	
	public Result() {
		
	}
	
	public Result(String experiment_id, String result_test_name, String result_name, String sample_id, String result_value
			,String result_uom_id, String result_type, String result_material_id, String result_comment, String selftest_id,String uom_type, String resultref_id, String result_is_webix, String result_material_name, String result_request_id){
		this.experiment_id = experiment_id;
		this.result_test_name = result_test_name;
		this.result_name = result_name;
		this.sample_id = sample_id;
		this.result_value = result_value;
		this.result_uom_id = result_uom_id;
		this.result_type = result_type;
		this.result_material_id = result_material_id;
		this.result_comment = result_comment;
		this.selftest_id=selftest_id;
		this.resultref_id=resultref_id;
		this.uom_type = uom_type;
		this.result_is_webix = result_is_webix;
		this.result_material_name = result_material_name;
		this.result_request_id = result_request_id;

	}
	
	private String experiment_id;
	private String result_test_name;
	private String result_name;
	private String sample_id;
	private String result_value;
	private String result_uom_id;
	private String uom_type;
	private String result_type;
	private String result_material_id;
	private String result_comment;
	private String selftest_id;
	private String resultref_id;
	private String result_is_webix;
	private String result_material_name;
	private String result_request_id;
	
	public String getExperimentId()
	{
		return experiment_id;
	}
	
	public String getResultTestName()
	{
		return result_test_name;
	}
	
	public String getResultName()
	{
		return result_name;
	}
	
	public String getSampleId()
	{
		return sample_id;
	}
	
	public String getResultValue()
	{
		return result_value;
	}
	
	public String getResultUomId()
	{
		return result_uom_id;
	}
	
	public String getUomType()
	{
		return uom_type;
	}
	
	public String getResultType()
	{
		return result_type;
	}
	
	public String getResultMaterialId()
	{
		return result_material_id;
	}
	
	public String getResultComment()
	{
		return result_comment;
	}
	
	public String getSelfTestId()
	{
		return selftest_id;
	}
	
	public String getResultRefId()
	{
		return resultref_id;
	}
	
	public String getresultIsWebix()
	{
		return result_is_webix;
	}
	
	public String getresultMaterialName()
	{
		return result_material_name;
	}
	
	public String getResultRequestId()
	{
		return result_request_id;
	}
	
	public void setExperimentId(String experiment_id)
	{
		this.experiment_id = experiment_id;
	}
	
	public void setResultTestName(String result_test_name)
	{
		this.result_test_name = result_test_name;
	}
	
	public void setResultName(String result_name)
	{
		this.result_name = result_name;
	}
	
	public void setSampleId(String sample_id)
	{
		this.sample_id = sample_id;
	}
	
	public void setResultValue(String result_value)
	{
		this.result_value = result_value;
	}
	
	public void setResultUomId(String result_uom_id)
	{
		this.result_uom_id = result_uom_id;
	}
	
	public void setUomType(String uom_type)
	{
		this.uom_type = uom_type;
	}
	
	public void setResultType(String result_type)
	{
		this.result_type = result_type;
	}
	
	public void setResultMaterialId(String result_material_id)
	{
		this.result_material_id = result_material_id;;
	}
	
	public void setResultComment(String comment)
	{
		this.result_comment = comment;
	}
	
	public void setSelfTestId(String selftest_id)
	{
		this.selftest_id=selftest_id;
	}
	
	public void setResultrefId(String resultref_id)
	{
		this.resultref_id=resultref_id;
	}
	
	public void setResultIsWebix(String result_is_webix)
	{
		this.result_is_webix=result_is_webix;
	}
	
	public void setResultMaterialName(String result_material_name)
	{
		this.result_material_name=result_material_name;
	}
	
	public void setResultRequestId(String result_request_id)
	{
		this.result_request_id = result_request_id;
	}
}
