package com.skyline.form.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeneralChemLocatorUtil {
	
	@Value("${chemlocatorwrapperCall:na}")
	private String chemlocatorwrapperCall; // precision
	
	/**
	 * 
	 * @param materialIdList - csv of materilid
	 * @return  - csv of materilid or -1 in case of error or no result
	 */
	public String getFileListByMaterilIdList(String materialIdList) {
		String toReturn = "-1";
		
		if(chemlocatorwrapperCall != null && !chemlocatorwrapperCall.equals("na")) {
			try {
				HttpURLConnection conn = null;
				StringBuilder resonse = new StringBuilder();
				String request = chemlocatorwrapperCall + "/" + materialIdList;
				URL url = new URL(request);
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				// conn.setInstanceFollowRedirects( false );
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-Type", "application/text"); // application/x-www-form-urlencoded
				conn.setRequestProperty("Accept", "application/text");
	
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
					for (String line; (line = reader.readLine()) != null;) {
						System.out.println(line);
						resonse.append(line);
					}
				}
				if(resonse.length() > 0) {
					toReturn = resonse.toString();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return  toReturn;
	}

}
