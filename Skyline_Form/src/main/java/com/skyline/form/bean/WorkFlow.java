package com.skyline.form.bean;

import java.util.List;

public class WorkFlow {
	private String nodeKeyProperty;
	private List<State> nodeDataArray;
	private List<StateLink> linkDataArray;
//	private String statusTableName;
	private String firstState;
	
	public String getNodeKeyProperty()
	{
		return nodeKeyProperty;
	}

	public List<State> getNodeDataArray()
	{
		return nodeDataArray;
	}
	
	public List<StateLink> getLinkDataArray()
	{
		return linkDataArray;
	}

//	public String getStatusTableName() {
//		return statusTableName;
//	}
	
	public String getFirstState(){
		return firstState;
	}
}