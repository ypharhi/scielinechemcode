package com.skyline.form.bean;

import org.json.JSONObject;

public class WebixOutput {
	
	public WebixOutput(String experiment_id,String step_id, String batch_id, String mass, String result_test_name, String result_name, String result_value
			, String result_type, String result_comment,String uom_type, String material_id,String samples){
		this.step_id = step_id;
		this.experiment_id = experiment_id;
		this.mass = mass;
		this.result_test_name = result_test_name;
		this.result_name = result_name;
		this.batch_id = batch_id;
		this.result_value = result_value;
		this.result_type = result_type;
		this.result_comment = result_comment;
		this.uom_type = uom_type;
		this.material_id = material_id;
		this.samples = samples;
	}
	
	//ctor used for data teken from massbalance in step
	public WebixOutput(String experiment_id,String step_id, String batch_id, String mass, String result_test_name, String result_name, String result_value
			, String result_type, String result_comment,String uom_type, String material_id,String samples,String weight,String yield,String moles,
			String indication_mb,String sample_mb,String stream_name,String stream_order, String webixTableNumber, String webixTableGroupNumber){
		this.step_id = step_id;
		this.experiment_id = experiment_id;
		this.mass = mass;
		this.result_test_name = result_test_name;
		this.result_name = result_name;
		this.batch_id = batch_id;
		this.result_value = result_value;
		this.result_type = result_type;
		this.result_comment = result_comment;
		this.uom_type = uom_type;
		this.material_id = material_id;
		this.samples = samples;
		this.weight = weight;
		this.yield = yield;
		this.moles = moles;
		this.indication_mb = indication_mb;
		this.sample_mb = sample_mb;
		this.webixTableNumber = webixTableNumber;
		this.webixTableGroupNumber = webixTableGroupNumber;
		this.setStreamData(stream_name,stream_order);
	}
	
	//ctor used for data taken from analytical experiment in step
	public WebixOutput(String experiment_id,String step_id, String batch_id, String mass, String result_test_name, String result_name, String result_value
			, String result_type, String result_comment,String uom_type, String material_id,String samples,String weight,String sample_id,String component_id,String preparationtrf_id,String analytic_data,String weighting){
		this.step_id = step_id;
		this.experiment_id = experiment_id;
		this.mass = mass;
		this.result_test_name = result_test_name;
		this.result_name = result_name;
		this.batch_id = batch_id;
		this.result_value = result_value;
		this.result_type = result_type;
		this.result_comment = result_comment;
		this.uom_type = uom_type;
		this.material_id = material_id;
		this.samples = samples;
		this.weight = weight;
		this.setComponent_id(component_id);
		this.setSample_id(sample_id);
		this.setPreparation_id(preparationtrf_id);
		this.setAnalytic_data(analytic_data);
		this.setWeighting(weighting);
	}
	
	private String experiment_id;
	private String step_id;
	private String mass;
	private String result_test_name;
	private String result_name;
	private String result_value;
	private String uom_type;
	private String result_type;
	private String batch_id;
	private String result_comment;
	private String material_id;
	private String samples;
	private String weight = "";
	private String yield = "";
	private String moles = "";
	private String indication_mb ="";
	private String sample_mb = "";
	private String sample_id = "";
	private String component_id = "";
	private String preparation_id ="";
	private String analytic_data = "";
	private String weighting = "";
	private String stream_data = "";
	private String webixTableNumber = "";
	private String webixTableGroupNumber = "";
	
	public String getStreamData()
	{
		return stream_data;
	}
	
	public String getSampleMB()
	{
		return sample_mb;
	}
	
	public String getWeight()
	{
		return weight;
	}
	
	public String getYield()
	{
		return yield;
	}
	
	public String getMoles()
	{
		return moles;
	}
	
	public String getIndication_mb()
	{
		return indication_mb;
	}
	
	public String getSamplesCsv()
	{
		return samples;
	}
	
	public String getExperimentId()
	{
		return experiment_id;
	}
	
	public String getStepId()
	{
		return step_id;
	}
	
	public String getBatchId()
	{
		return batch_id;
	}
	
	public String getMaterialId()
	{
		return material_id;
	}
	
	public String getMass()
	{
		return mass;
	}
	
	public String getResultTestName()
	{
		return result_test_name;
	}
	
	public String getResultName()
	{
		return result_name;
	}
	
	public String getResultValue()
	{
		return result_value;
	}
	
	public String getUomType()
	{
		return uom_type;
	}
	
	public String getResultType()
	{
		return result_type;
	}
	
	public String getResultComment()
	{
		return result_comment;
	}
	
	public void setStreamData(String stream_name,String stream_order)
	{
		JSONObject jsonData = new JSONObject();
		jsonData.put("stream_name", stream_name);
		jsonData.put("stream_order", stream_order);
		this.stream_data = jsonData.toString();
	}
	
	public void setExperimentId(String experiment_id)
	{
		this.experiment_id = experiment_id;
	}
	
	public void setStepId(String step_id)
	{
		this.step_id = step_id;
	}
	
	public void setBatchId(String batch_id)
	{
		this.batch_id = batch_id;
	}
	
	public void setMaterialId(String material_id)
	{
		this.material_id = material_id;
	}
	
	public void setMass(String mass)
	{
		this.mass = mass;
	}
	
	public void setResultTestName(String result_test_name)
	{
		this.result_test_name = result_test_name;
	}
	
	public void setResultName(String result_name)
	{
		this.result_name = result_name;
	}
	
	public void setResultValue(String result_value)
	{
		this.result_value = result_value;
	}
	
	public void setUomType(String uom_type)
	{
		this.uom_type = uom_type;
	}
	
	public void setResultType(String result_type)
	{
		this.result_type = result_type;
	}
	
	public void setResultComment(String comment)
	{
		this.result_comment = comment;
	}
	
	public void setWeight(String weight)
	{
		this.weight = weight;
	}
	
	public void setYield(String yield)
	{
		this.yield = yield;
	}
	
	public void setMoles(String moles)
	{
		this.moles = moles;
	}
	
	public void setIndicationMB(String indication_mb)
	{
		this.indication_mb = indication_mb;
	}
	
	public void setSampleMB(String sample_mb)
	{
		this.sample_mb = sample_mb;
	}
	
	public void setSamplesCsv(String samples)
	{
		this.samples = samples;
	}

	public String getSample_id() {
		return sample_id;
	}

	public void setSample_id(String sample_id) {
		this.sample_id = sample_id;
	}

	public String getAnalytic_data() {
		return analytic_data;
	}

	public void setAnalytic_data(String analytic_data) {
		this.analytic_data = analytic_data;
	}

	public String getPreparation_id() {
		return preparation_id;
	}

	public void setPreparation_id(String preparation_id) {
		this.preparation_id = preparation_id;
	}

	public String getComponent_id() {
		return component_id;
	}

	public void setComponent_id(String component_id) {
		this.component_id = component_id;
	}

	public String getWeighting() {
		return weighting;
	}

	public void setWeighting(String weighting) {
		this.weighting = weighting;
	}

	public String getWebixTableNumber() {
		return webixTableNumber;
	}

	public void setWebixTableNumber(String webixTableNumber) {
		this.webixTableNumber = webixTableNumber;
	}

	public String getWebixTableGroupNumber() {
		return webixTableGroupNumber;
	}

	public void setWebixTableGroupNumber(String webixTableGroupNumber) {
		this.webixTableGroupNumber = webixTableGroupNumber;
	}
}
