package com.skyline.form.entitypool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.entity.Element;

/**
 * ElementGeneralCodeImp
 * 
 * codeName - CODE_STB_SELECT_TABLE ....TODO full description after taro development for stability (this is the reason for entering this element)
 * 
 */
public class ElementGeneralCodeImp extends Element {

	private String codeName;
	
	@Value("${ireportPath}")
	private String ireportPath;

	@Autowired
	private GeneralDao generalDao;

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{
				codeName = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "codeName");
				return "";
			}
			return "Creation failed";
		}
		catch (Exception e) {
			generalUtilLogger.logWrite(e);
			return "Creation failed";
		}
	}

	@Override
	public Map<String, String> getInitHtml(long stateKey, String formId, boolean renderEmpty, String value, String userLastSaveVal, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		
		Map<String, String> html = new HashMap<String, String>();		
		
		
		if (codeName.equals("CODE_STB_SELECT_TABLE")) {
			// TODO: put this to the new private method
			String html_ = "";
			String htmlByCodeName = "";
			htmlByCodeName = 
					"<input type=\"hidden\" name=\"txtStageID\" id=\"txtStageID\" value=\"\">\n" + 
					"<input type=\"hidden\" name=\"txtSpIdList\" id=\"txtSpIdList\" value=\"\">\n";
			html_ = "<div codeName=\"" + codeName + "\" " + inputAttribute + " id=\"" + domId + "\" element=\"" + this.getClass().getSimpleName() + "\">\n" + htmlByCodeName + "</div>";
			html.put(layoutBookMark, html_);
			html.put(layoutBookMark + "_ready", "");
		} else if (codeName.equals("CODE_DESIGN_REPORT_PARAMETERS")) {
			html = getReportDesignParameters(stateKey);
		} else if (codeName.equals("CODE_PURITY_LIST")){
			String plannedCompositionId = generalUtilFormState.getFormParam(stateKey, "PurityList", "$P{PARENTID}");
			html = getPurityList(plannedCompositionId,stateKey);
		} else if (codeName.equals("CODE_USER_GUIDE_VIEWER")) {
			String html_ = "";
			try {
				String userGuideId = generalUtilFormState.getFormParam(stateKey, "UserGuideViewer", "$P{USERGUIDEID}");

				Map<String, String> data = generalUtilForm.getCurrrentIdInfo(userGuideId);
				String contentCode = data.get("CONTENT_CODE_UG");
				String name = data.get("NAME");
				String fileName = data.get("FILE_NAME");
				String fileId = data.get("FILE_ID");
//				String desc = data.get("USERGUIDEDESCRIPTION");

				File file_ = new File(ireportPath.replace("ireport", "userguide") + "/" + fileId + "_" + fileName);
				if(!file_.exists()) {
					try {
						file_.createNewFile(); // if file already exists will do nothing 
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					try (FileOutputStream fos = new FileOutputStream(file_, false)) {
						fos.write(generalUtilFormState.getByteArrayFromBlobWrapper(fileId));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				String src="../skylineFormWebapp/userguide_pool/" + fileId + "_" + fileName;
				
//				if (contentCode.equals("VIDEO_UG")) {
				String styleDiv = "width=\"100%\"  style=\"text-align: center;\"";
				String embed = "<embed width=\"90%\" height=\"650px\" src=\"" + src + "\">";
				 
				html_ = "<div " + styleDiv + ">" + embed + "</div>";
				
				generalUtilFormState.setFormParam(stateKey, "UserGuideViewer", "USERGUIDE_NAME", name);
			} catch (Exception e) {
				e.printStackTrace();
			}

			html.put(layoutBookMark, html_);
		}
		return html;
	}
	
	private Map<String, String> getPurityList(String plannedCompositionId, long stateKey) {
		String experiment_id = generalDao.selectSingleStringNoException("select distinct parentid from fg_s_composition_v where formid = '"+plannedCompositionId+"'");
		String sql = "select purity\n"
				+ " from fg_s_composition_v\n"
				+ " where formid = '"+plannedCompositionId+"'\n"
				+ " and active = 1"
				+ generalUtilFormState.getWherePartForTmpData("composition", experiment_id);
		String sampleResultsList = generalDao.selectSingleStringNoException(sql);
		StringBuilder valueByCode = new StringBuilder();
		StringBuilder valueByCode_onReady = new StringBuilder();
		Map<String, String> html = new HashMap<String, String>();
		if(sampleResultsList.startsWith("{")){
			try {	
				JSONObject json = new JSONObject(sampleResultsList);
				Iterator<String> keysItr = json.keys();
				while (keysItr.hasNext()) {
					String material_id = keysItr.next();
					String value = generalUtil.getJsonValById(sampleResultsList, material_id);
					if(material_id.isEmpty()){
						continue;
					}
					String materialName = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID, material_id, "name");
					valueByCode.append("<div class=\"row\">");
					valueByCode.append("<div class=\"column cell-label\" style = \"width:20%\">");
					valueByCode.append("<div formlabelelement=\"1\" id=\"Label_"+material_id+"\" style=\"\">"
									+"<label style=\"display: table-cell\"> "+materialName+"</label></div>");
					valueByCode.append("</div>");
					valueByCode.append("<div class=\"column cell-element\" style=\"width:5%\"></div>");		
					valueByCode.append("<div class=\"column cell-element\" style=\"width:15%\">");
					valueByCode.append(
							"<input autocomplete=\"off\" style=\"width:100%;\" type=\"text\" id=\"material_id_"+material_id+"\"  placeholder=\"Add Assay\" \n" + 
									"formElement=1 material_id = \""+material_id+"\"  formPreventSave=0 saveType=\"none\" value=\""+value+"\"  maxlength=\"500\" \n" +
							"element=\"ElementInputImp\" onkeypress=\"if(event.keyCode == 46 || event.keyCode== 101 ||(event.keyCode > 47 &amp;&amp; event.keyCode < 58)){ }else return false;\" type=\"Number\" >");
					valueByCode.append("</div>");	
					valueByCode.append("<div class=\"column cell-element\" style=\"width:5%\">");	
					valueByCode.append("<p class=\"column cell-element\" >%</p>");
					valueByCode.append("</div>");
					valueByCode.append("<div class=\"column cell-element\"></div>");	
					valueByCode.append("</div>");	
				}
			} catch (Exception e) {
				return html;
			}
		}
		html.put(layoutBookMark, valueByCode.toString());
		html.put(layoutBookMark + "_ready", valueByCode_onReady.toString());
		return html;
		
	}

	@Override
	public String getDefaultValue(long stateKey, String formId, String formCode) {
		String dv_ = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "defaultValue");
		if(dv_.contains("$P{")) {
			generalUtilLogger.logWrite(LevelType.WARN, "default value not found for element[impCode]=" + impCode + "value=" + dv_, "", ActivitylogType.InfoLookUp, null);
			dv_= "";
		}
		
		return dv_;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId,
			String inputAttribute, String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp
		String htmlBody = "";
		String valueByCode = "";
		if (codeName.equals("CODE_STB_SELECT_TABLE")) { //value_ contains the experimentTypeID
			try {
				//					String experimentTypeName = generalUtilFormState.getFormParam(stateKey, "ExperimentMain", "$P{CURRENT_EXPERIMENTTYPE_ID_NAME}");
				String experimentTypeID = generalUtilFormState.getFormParam(stateKey, "ExperimentMain",
						"$P{CURRENT_EXPERIMENTTYPE_ID}");
				Map<String, String> infoMap = generalUtilForm.getCurrrentIdInfo(experimentTypeID);
				valueByCode = infoMap.get("PLANNINGCODE");
				if(generalUtil.getNull(valueByCode).equals("")) { // renderEmpty
					return "";
				}
			} catch (Exception e) {
				return "";
			}
		}

		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "','isMandatory':'"
				+ isMandatory + "','val':'','domId':'" + domId + "','type':'elementGeneralCode','value_':'"
				+ valueByCode + "'});";

		return htmlBody;
	}

	private Map<String, String> getReportDesignParameters(long stateKey){
		StringBuilder valueByCode = new StringBuilder();
		StringBuilder valueByCode_onReady = new StringBuilder();
		Map<String, String> html = new HashMap<String, String>();
		try {
			//get "Organic_Step_Parameters" mp type id
			String organicStepParametersId = generalUtilForm.getCurrrentINameSingleStringInfo("MPType", "Organic_Step_Parameters", "ID");
			List<Map<String,String>> data = generalUtilForm.getCurrrentNameInfoAllContainsId("MP","%");
			List<String> steps = generalUtilForm.getStepList();
		
			for (Map<String, String> map : data) {
				String mpTypeIdList = map.get("MPTYPE_ID");
				if(!generalUtil.getNull(mpTypeIdList).equals("") && ("," + mpTypeIdList + ",").contains("," + organicStepParametersId + ",")) {
					
					String name = map.get("NAME");
					String nameNoSpaces = name.replaceAll("\\s+","");
					
					valueByCode.append("<div class=\"layout-row designreport-parameters-row\">");
					valueByCode.append("<div class=\"layout-cell cell-element checkbox\">");
					valueByCode.append(
							"<input class=\"designReportParameter\" autocomplete=\"off\" style=\"width:100%;\" type=\"checkbox\" id=\"input" + nameNoSpaces + "\"  placeholder=\"Enter\" \n" + 
									"formElement=1  formPreventSave=0 saveType=\"none\" value=\"0\"  maxlength=\"500\" \n" +
							"element=\"ElementInputImp\" onchange=\"hideAdditInfoDialog()\" >");
					valueByCode.append("</div>");
					valueByCode.append("<div class=\"layout-cell cell-label\">");
					valueByCode.append(
							"<div formlabelelement=\"1\" id=\"l" + nameNoSpaces +"Label\" style=\"\">"
									+"<label class=\"designReportParameter\" style=\"display: table-cell\">" + name + "</label></div>");
					valueByCode.append("</div>");
					valueByCode.append("<div class=\"layout-cell cell-element select-element\">");
					
					valueByCode.append("<select id=\"ddl" + nameNoSpaces + "\" formelement=\"1\" formpreventsave=\"0\" savetype=\"none\" lastvalue=\"ALL\" savevalueasjson=\"false\" \n" 
							+ "     style=\"width: 100%; display: none;\" data-placeholder=\"Choose:\" class=\"chosen-select designReportParameter\" multiple element=\"ElementAutoCompleteDDLImp\"  > \n");
					int i = -1;
					for(String step : steps) 
					{
						if (i == -1)
						{
							valueByCode.append("<option value=\"" + i + "\"> Experiment</option> \n");
						}
						i++;
						if (i == 0)
						{
							
							valueByCode.append("<option value=\"" + i + "\">All steps</option> \n");
						}
						i++;
						
						valueByCode.append("<option value=\"" + i + "\"> "+step+"</option> \n");
					}
					valueByCode.append("</select> \n");				
					valueByCode.append("</div>");				
					valueByCode.append("</div>");
					
					//valueByCode_onReady.append("$('[id=\"ddl" + nameNoSpaces + "\"]').chosen({allow_single_deselect:true ,search_contains:true, width: '100%'}).on('change', function(){ onChangeAjax('ddl" + nameNoSpaces + "'); }); \n");
					 valueByCode_onReady.append("$('[id=\"ddl" + nameNoSpaces + "\"]').chosen({allow_single_deselect:true ,search_contains:true, width: '100%'}).on('change', function(){ onChangeAjax('ddl" + nameNoSpaces + "');"
					+ "var checkBoxId=\"input\"+$(this).attr(\"id\").replace(\"ddl\",\"\");\r\n" +
					" if($(this).find('option:selected').length>0){\r\n" +
					" \r\n" +
					" $('#'+checkBoxId).prop('checked',true);\r\n"
					+ "$(this).prop('required',false)" +
					" }\r\n" +
					" else  if($(this).find('option:selected').length<=0) \r\n" +
					" $('#'+checkBoxId).prop('checked',false);\r\n" +
					" \r\n" +
					" "
					+ " }); \n");
					 valueByCode_onReady.append("$('[id=\"input" + nameNoSpaces + "\"]').on('change', function(){"
					 		+ "var ddlId=\"ddl\"+$(this).attr(\"id\").replace(\"input\",\"\");\r\n" + 
					 		"					if ($(this).is(\":checked\")) {\r\n" + 
					 		"						\r\n" + 
					 		"					\r\n" + 
					 		"						$('#'+ddlId).prop('required',true);\r\n" + 
					 		"					}\r\n" + 
					 		"					else{\r\n" + 
					 		"						$('#'+ddlId).prop('required',false);\r\n" + 
					 	
					 		"$('#'+ddlId+' option').prop('selected', false).trigger('chosen:updated');\r\n" + 
					 		"					}});\n");
				
				}
			}
		} catch (Exception e) {
			return html;
		}
		html.put(layoutBookMark, valueByCode.toString());
		html.put(layoutBookMark + "_ready", valueByCode_onReady.toString());
		return html;
	}
	
	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema= "schema:{ \n" +
				"   codeName:{  \r\n" + 
				"      type: 'string',\r\n" + 
				"      title:'code name',\r\n" +			
				"   }\n" +
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}
	
 
}
