package com.skyline.form.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class SearchTreeBeanList implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public List<SearchTreeBean> searchTreeBeanList;
	
	public SearchTreeBeanList() { 
		this.searchTreeBeanList = new ArrayList<SearchTreeBean>();
	}

	public void add(SearchTreeBean searchTreeBean) {
		if(!searchTreeBeanList.contains(searchTreeBean)) {
			searchTreeBeanList.add(searchTreeBean);
		}
	} 

	public String toJsonString() {
		String toReturn = "";
		try {
			toReturn = new ObjectMapper().writeValueAsString(this.searchTreeBeanList);
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

	public int getSize() {
		// TODO Auto-generated method stub
		return searchTreeBeanList.size();
	}

	
}
