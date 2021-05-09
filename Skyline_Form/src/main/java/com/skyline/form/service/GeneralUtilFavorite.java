package com.skyline.form.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GeneralUtilFavorite implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<String> favoriteList = null;

	public List<String> getFavoriteList() {
		return favoriteList;
	}

	public void initFavoriteList(List<String> favoriteList) {
		this.favoriteList = favoriteList;
	}
	
	public void addToFavoriteList(String formId) {
		if(favoriteList != null) {
			favoriteList.add(formId);
		}
	}
	
	public void removeFromFavoriteList(String formId) {
		if(favoriteList != null) {
			favoriteList.remove(formId);
		}
	}

	public boolean isFavorit(String formId) {
		return favoriteList != null && favoriteList.contains(formId);
	}
}
