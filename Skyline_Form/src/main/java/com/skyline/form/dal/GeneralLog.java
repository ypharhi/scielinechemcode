package com.skyline.form.dal;

import java.util.Map;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;

public interface GeneralLog {
	String logWriterDao(LevelType levelType, String comments, String formId, ActivitylogType activitylogType,
			Map<String, String> additionalInfo, String stackTrace, String userId);

}