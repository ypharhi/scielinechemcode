package com.skyline.form.entity;

import java.util.List;
import java.util.Map;

import com.skyline.form.service.FormTempData;

public interface CatalogInterface extends EntityInterface {

	/**
	 * 
	 * @param item name
	 * @return current state CSV of item's values after filter
	 */
	public String getItem(long stateKey, Map<String,String> formCatalogMap, FormTempData formTempDataMap, String item, StringBuilder info);
	
	/**
	 * 
	 * @param item name
	 * @return current state array of item's values after filter
	 */
	
	
	public List<String> getItemArray(long stateKey, Map<String,String> formCatalogMap, FormTempData formTempDataMap, String item, StringBuilder info);
 
}
