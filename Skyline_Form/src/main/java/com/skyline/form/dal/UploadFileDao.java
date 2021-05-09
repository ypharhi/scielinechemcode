package com.skyline.form.dal;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.web.multipart.MultipartFile;

public interface UploadFileDao {

	String saveFile(MultipartFile file, String formCodeFull);

	void saveFileAsClob(String FILE_ID, MultipartFile file);

	String saveStringAsClob(String elementID, String fullData);

	String saveStringAsClobRenderId(String desc, String data);

	String getContentType(String FORM_ID);

	//	Blob getContent(String FORM_ID);

	InputStream getContent(String FILE_ID, StringBuilder sbFileName);

	InputStream getContentDisplay(String FILE_ID, StringBuilder sbOriginalFilename);

	String getStringContent(String id, String formCode, String domID, String formID);

	void saveChemImageFile(byte[] arr, String imgURL, String objID);

	String saveRichText(String elementID, String clobString, String plainText, boolean isTransactional);

	String getRichTextContent(String ID);

	String getFileName(String FILE_ID);

	List<JSONArray> getWebixAnalytCalcData(String parentID, boolean isUpload);

	JSONArray getWebixFormulCalcData(String parentID);

	JSONArray getWebixExperimentStepCalcData(String parentID);

	JSONArray getWebixGeneralTableData(String sql);

	String getWebixContent(String ID, boolean isPln);

	JSONArray getWebixMassBalanceCalcData(String parentID);

	JSONArray updateWebixMassBalanceSamplesList(String samplesScope, String parentID, String runNumber);
	
	String getTooltipForWebixMassBalanceSamplesField(String sampleID);

	JSONArray updateWebixResultTypeList(Map<String, String> elementValueMap);

	String saveFile(String path_file, String formCodeFull, String fileName, boolean isTemp);

	String saveStringAsClobNewConnection(String elementID, String clobString);

	byte[] getByteArrayFromBlob(String FILE_ID);

	String saveByteArrayAsBlob(byte[] arr, String formCodeFull, String fileName);

	void updateUserBreadcrumbLink(String string, String fileID);

	String getUserBreadcrumbLink(String userName);

	boolean isDisplayContent(String FILE_ID);

	JSONArray getWebixMassBalanceResCalcData(String parentID,String sampleId);

	void saveDiagramImage(byte[] arr, String imgURL, String objID);

	String getDiagramContent(String ID);
}
