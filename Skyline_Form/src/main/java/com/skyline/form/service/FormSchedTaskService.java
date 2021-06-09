package com.skyline.form.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.dal.ChemDao;
import com.skyline.form.dal.ChemMatrixTaskDao;
import com.skyline.form.dal.FormSaveElementDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.dal.SchedTaskDao;

/**
 * Contains 1) A pool of tasks that the system user can run from the main form builder screen 2) Scheduled task (in next versions)
 * 
 * @author YPharhi
 *
 */
//@Service - in comment see "Scheduled task" in this class
public class FormSchedTaskService {
	@Value("${ireportPath}")
	private String DIR_TEP_FILES;

	@Value("${dataMigrationList:na}")
	private String dataMigrationList;

	@Value("${schedTaskList:na}")
	private String schedTaskList;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	private ChemDao chemDao;

	@Value("${jdbc.username}")
	private String DB_USER;

	@Autowired
	private SchedTaskDao schedTaskDao;

	@Autowired
	private GeneralUtilLogger generalUtilLogger;

	@Autowired
	private GeneralUtilVersionData generalUtilVersionData;

	@Autowired
	private FormSaveElementDao formSaveElemetDao;

//	@Value("${chem.imgpropreact:png:w900,h250,b32,#ffffff}")
//	private String imgprop;// = "png:w2000,h250,b32,#ffffff";

	@Autowired
	private IntegrationEvent integrationEvent;

	@Autowired
	private GeneralUtil generalUtil;

	@Value("${systemSynDelim:@syndlm@}")
	private String systemSynonymDelim;

	@Value("${customerSynDelim:\\r?\\n}")
	private String customerSynDelim;

	@Value("${cleanChemTableAndCheckForDuplicates:0}")
	private String cleanChemTableAndCheckForDuplicates;

	@Autowired
	private CacheService cacheService;

	@Autowired
	private SpringMessages springMessages;
	
	@Autowired
	private SpringNameDictionary springNameDictionary;
	
	@Autowired
	private ChemMatrixTaskDao chemMatrixTaskDao; 

	private static final Logger logger = LoggerFactory.getLogger(FormSchedTaskService.class);
	
	/////////////////////////////////////////
	// run task on load
	////////////////////////////////////////
	@PostConstruct
	public void dataMigrationPostConstruct() {
		try {
			dataMigration("");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	//////////////////////////////
	// run Scheduled task - Note if this class is service it might not work
	//	cron configuration:
	//	+-------------------- second (0 - 59)
	//	|  +----------------- minute (0 - 59)
	//	|  |  +-------------- hour (0 - 23)
	//	|  |  |  +----------- day of month (1 - 31)
	//	|  |  |  |  +-------- month (1 - 12)
	//	|  |  |  |  |  +----- day of week (0 - 6) (Sunday=0 or 7)
	//	|  |  |  |  |  |  +-- year [optional]
	//	|  |  |  |  |  |  |
	//	*  *  *  *  *  *  * command to be executed 
	// to make every x min use this example: 0 0/30 * * * * - every 30 min
	
	//***** schedNightTask
	@Scheduled(cron = "${cron.schedNightTask:0 0 3 * * ?}") //3:00 every night
	private void schedNightTask() {
		String schedInfo = "at 3:00 every night";
		if (("," + schedTaskList + ",").contains(",schedNightTask,") && !isSuspend("nightTask")) {
			String startDate = getDBSysDate();
			generalUtilLogger.logWrite(LevelType.INFO,
					"schnightTask - START [use cron.schedNightTask prop]", "-1",
					ActivitylogType.Scheduler, null);
			try {
				if(!isRunning("nightTask") && !isRunning("correctRecentSearchData")) {
					schedTaskDao.inserIntoFgSysSched("nightTask", startDate, "", "R", schedInfo);
					nightTask();
					String endDate = getDBSysDate();
					schedTaskDao.inserIntoFgSysSched("nightTask", startDate, endDate, "S", schedInfo);
				}
			} catch (Exception e) {
				generalUtilLogger.logWrite(LevelType.ERROR,"schedNightTask - " + schedInfo + " - ERROR=" + e.getMessage(),"",ActivitylogType.Scheduler,null,e);
				String endDate = getDBSysDate();
				schedTaskDao.inserIntoFgSysSched("nightTask", startDate, endDate, "F", schedInfo);
			}
		}
	}
	
	//***** schedCorrectRecentSearchData
	@Scheduled(cron = "${cron.schedCorrectRecentSearchData:0 */5 * ? * *}") //every 5 min
	private void schedCorrectRecentSearchData() {
		String schedInfo = "every 5 min (not between 02:00 - 05:00)";
		if (("," + schedTaskList + ",").contains(",schedCorrectRecentSearchData,") && !isSuspend("correctRecentSearchData")) {
			int h_ = 0;
			try {
				h_ = LocalDateTime.now().getHour();
				if (h_ >= 2 && h_ < 5) {
					return;
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			String startDate = getDBSysDate();
			generalUtilLogger.logWrite(LevelType.INFO,
					"schedCorrectRecentSearchData - START [use cron.schedCorrectRecentSearchData prop]", "-1",
					ActivitylogType.Scheduler, null);
			try {
				if(!isRunning("nightTask") && !isRunning("correctRecentSearchData")) {
					schedTaskDao.inserIntoFgSysSched("correctRecentSearchData", startDate, "", "R", schedInfo);
					correctRecentSearchData();
					String endDate = getDBSysDate();
					schedTaskDao.inserIntoFgSysSched("correctRecentSearchData", startDate, endDate, "S", schedInfo);
				}
			} catch (Exception e) {
				generalUtilLogger.logWrite(LevelType.ERROR,
						"schedCorrectRecentSearchData - " + schedInfo + " - ERROR=" + e.getMessage(), "",
						ActivitylogType.Scheduler, null, e);
				String endDate = getDBSysDate();
				schedTaskDao.inserIntoFgSysSched("correctRecentSearchData", startDate, endDate, "F", schedInfo);
			}
		}
	}
	
	//***** schedCorrectResultToUpdate
	@Scheduled(cron = "${cron.schedCorrectResultToUpdate:0 */10 * ? * *}") //every 10 min
	private void schedCorrectResultToUpdate() {
		String schedInfo = "every 10 min (not between 19:00 - 07:00)";
		if (("," + schedTaskList + ",").contains(",schedCorrectResultToUpdate,") && !isSuspend("correctResultToUpdate")) {
			int h_ = 0;
			try {
				h_ = LocalDateTime.now().getHour();
				if (h_ >= 19 && h_ < 7) {
					return;
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String startDate = getDBSysDate();
			generalUtilLogger.logWrite(LevelType.INFO,
					"schcorrectResultToUpdate - START [use cron.schedCorrectResultToUpdate prop]", "-1",
					ActivitylogType.Scheduler, null);
			try {
				if (!isRunning("correctResultToUpdate")) {
					schedTaskDao.inserIntoFgSysSched("correctResultToUpdate", startDate, "", "R", schedInfo);
					generalDao.updateSingleString("begin dbms_mview.refresh('FG_I_RESUSINGTOUPDATE_MV'); end;");
					String endDate = getDBSysDate();
					schedTaskDao.inserIntoFgSysSched("correctResultToUpdate", startDate, endDate, "S", schedInfo);
				}
			} catch (Exception e) {
				generalUtilLogger.logWrite(LevelType.ERROR,
						"schedCorrectResultToUpdate - " + schedInfo + " - ERROR=" + e.getMessage(), "",
						ActivitylogType.Scheduler, null, e);
				String endDate = getDBSysDate();
				schedTaskDao.inserIntoFgSysSched("schedCorrectResultToUpdate", startDate, endDate, "F", schedInfo);
			}
		}
	}
	
//	@Scheduled(cron = "${cron.echo:0 */1 * ? * *}") //every 1 minute
//	private void echo() {
//		System.out.println("cron.echo Hint!");
//	}
	
	///////////////////////////////////////////
	// run task from form builder screen
	//////////////////////////////////////////
	public void setInfDataInCachMapRefresh(ActionBean requestAction) throws Exception {
		String dropDownListValue = requestAction.getData().get(0).getVal();
		// kd 29102018 newFormCode is - 1:Init Properties; 2:Set Inf Data In Cach Map;
		// 3:Make Version Data; 4:Data Migration; 5:Adding Data For Performance
		if (dropDownListValue.equals("1")) {
			initProperties();
		} else if (dropDownListValue.equals("2")) {
			cacheService.setInfDataInCachMap();
		} else if (dropDownListValue.equals("3")) {
			generalUtilVersionData.makeVersionDataDiff();
			generalUtilVersionData.makeVersionData();
		} else if (dropDownListValue.equals("4")) {
			String dataMigrationListPar = requestAction.getData().get(1).getVal();
			dataMigration(dataMigrationListPar);
		} else if (dropDownListValue.equals("6")) {
			String formCode = requestAction.getData().get(1).getVal();
			String deleteFormDef = requestAction.getData().get(2).getVal();
			deleteData(formCode, deleteFormDef);
		} else if (dropDownListValue.equals("7")) {
			findOutDuplicateMaterials();
		}
	}

	private void dataMigration(String dataMigrationListPar) throws Exception {
		//**************
		//*** check if system user wrote parameters in dialog box, then use it, else use default value/from app.properties and run dataMigrationPostConstruct
		//**************
		if (!generalUtil.getNull(dataMigrationListPar).equals("")) {
			dataMigrationList = dataMigrationListPar;
		}
		
		//**************
		//*** general migration functions
		//**************
		//refreshElementInfoAuditTrailMetaTmpTable
		if (("," + dataMigrationList + ",").contains(",refreshElementInfoAuditTrailMetaTmpTable,")) {
			refreshElementInfoAuditTrailMetaTmpTable();
		}
		//marvin
		if (("," + dataMigrationList + ",").contains(",jchemDataMigration,")) {
			jchemDataMigration();
		}
		// material synonym
		if (("," + dataMigrationList + ",").contains(",updateMaterialSynonym,")) {
			updateMaterialSynonym();
		}
//		if (("," + dataMigrationList + ",").contains(",deleteCancelledMaterialJchemSearch,")) {
//			deleteCancelledMaterialJchemSearch();
//		}
		if (("," + dataMigrationList + ",").contains(",updateChemImage,")) {
			try {
				updateChemImage();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (("," + dataMigrationList + ",").contains(",buildMoleculesMatrix,")) {
			chemMatrixTaskDao.buildMoleculesMatrix();
		}
		if (("," + dataMigrationList + ",").contains(",dbCleanUp,")) {
			dbCleanUp();
		}
		if (("," + dataMigrationList + ",").contains(",cleanUp,")) {
			cleanUp();
		}
		if (("," + dataMigrationList + ",").contains(",correctMobilephase,")) {
			correctMobilephase();
		}
		if (("," + dataMigrationList + ",").contains(",deleteNoMaterialJchemSearch,")) {
			deleteNoMaterialJchemSearch();
		}
		
		//**************
		//*** sched migration functions (needed sync using isRunning function) 
		//**************
		//correctRecentSearchData sched
		if (("," + dataMigrationList + ",").contains(",correctRecentSearchData,")) {
			String schedInfo = "run in data migration by the system!";
			String startDate = "";
			String endDate = "";
			try {
				if(!isRunning("correctRecentSearchData") && !isRunning("nightTask")) {
					startDate = getDBSysDate();
					schedTaskDao.inserIntoFgSysSched("correctRecentSearchData", startDate, "", "R", schedInfo);
					correctRecentSearchData();
					endDate = getDBSysDate();
					schedTaskDao.inserIntoFgSysSched("correctRecentSearchData", startDate, endDate, "S", schedInfo);
				}
			} catch (Exception e) {
				generalUtilLogger.logWrite(LevelType.ERROR,"correctRecentSearchData (run by system) - " + schedInfo + " - ERROR=" + e.getMessage(),"",ActivitylogType.Scheduler,null,e);
				endDate = getDBSysDate();
				schedTaskDao.inserIntoFgSysSched("correctRecentSearchData", startDate, endDate, "F", schedInfo);
			}
		}
		//nightTask sched
		if (("," + dataMigrationList + ",").contains(",nightTask,")) {
			String schedInfo = "run in data migration by the system!";
			String startDate = "";
			String endDate = "";
			try {
				if(!isRunning("nightTask") && !isRunning("correctRecentSearchData")) {
					startDate = getDBSysDate();
					schedTaskDao.inserIntoFgSysSched("nightTask", startDate, "", "R", schedInfo);
					nightTask();
					endDate = getDBSysDate();
					schedTaskDao.inserIntoFgSysSched("nightTask", startDate, endDate, "S", schedInfo);
				}
			} catch (Exception e) {
				generalUtilLogger.logWrite(LevelType.ERROR,"nightTask (run by system) - " + schedInfo + " - ERROR=" + e.getMessage(),"",ActivitylogType.Scheduler,null,e);
				endDate = getDBSysDate();
				schedTaskDao.inserIntoFgSysSched("nightTask", startDate, endDate, "F", schedInfo);
			}
		}
		// sched suspend task function
		if (("," + dataMigrationList).startsWith(",changesuspendsched_")) { 
			String [] dataArray = dataMigrationList.split("_");
			String schedName = dataArray[1];
			String suspend = dataArray[2];
			
			String sql = "update FG_SYS_SCHED t set t.SUSPEND = " + suspend + " where sched_name = '" + schedName + "'";
			generalDao.updateSingleString(sql);
			
			generalUtilLogger.logWrite(LevelType.INFO,
					"update sched task=" + schedName + ", suspend=" + suspend, "-1",
					ActivitylogType.Scheduler, null);
		} 
		// deleteSysSchedLog
		if (("," + dataMigrationList + ",").contains(",deleteSysSchedLog,")) {
			deleteSysSchedLog();
		} 
		
		if (("," + dataMigrationList + ",").contains(",fixInfTableDisplayValues,")) {
			schedTaskDao.fixInfTableDisplayValues("", false);
		} 
	}

	private boolean isRunning(String schedName) {
		boolean toReturn = false;
		String r_ = generalDao.selectSingleStringNoException("select decode(t.end_date,null,1,0) as isrunning from fg_sys_sched t where t.sched_name = '" + schedName+ "'");
		toReturn = r_.equals("1");
		if(toReturn) {
			generalUtilLogger.logWrite(LevelType.WARN,
					schedName + " will not execute because it is already running!", "-1",
					ActivitylogType.Scheduler, null);
		}
		return toReturn;
	}
	
	private boolean isSuspend(String schedName) { //Suspend 
		boolean toReturn = false;
		String r_ = generalDao.selectSingleStringNoException("select count(*) as issuspend from fg_sys_sched t where t.sched_name = '" + schedName+ "' and nvl(Suspend,0)=1");
		toReturn = r_.equals("1");
		if(toReturn) {
			generalUtilLogger.logWrite(LevelType.WARN,
					schedName + " will not execute because it is suspend!", "-1",
					ActivitylogType.Scheduler, null);
		}
		return toReturn;
	}

	private String getDBSysDate() {
		String toReturn = null;
		try {
			toReturn = generalDao.selectSingleString("SELECT TO_CHAR(SYSDATE,'DD/MM/YYYY HH24:MI:SS') AS DATE_ FROM DUAL");
		} catch (Exception e) {
			//DO nothing
		}
		return toReturn;
	}
	
	private void deleteSysSchedLog() {
		generalDao.updateSingleString("delete from fg_sys_sched");
		
	}

	private void correctRecentSearchData() throws Exception {
		
		//update webix data - insert data from webix table to webixat pivot//update Webix data 
		generalUtilLogger.logWrite(LevelType.INFO, "task-correctRecentSearchData-updateWebixSearch", "-1",
				ActivitylogType.Scheduler, null);
		schedTaskDao.updateWebixSearch("");

		//update result data - insert data from result table to resultat pivot
		generalUtilLogger.logWrite(LevelType.INFO, "task-correctRecentSearchData-updateResultSearch", "-1",
				ActivitylogType.Scheduler, null);
		schedTaskDao.updateResultSearch("");
		
		//complete inf data - add rows that are exists in pivot but not in inf
		generalUtilLogger.logWrite(LevelType.INFO,
				"task-correctRecentSearchData-correctInfTable(true, true, true, false)", "-1", ActivitylogType.Scheduler,
				null);
		schedTaskDao.correctInfTable(true, true, true, false); //TODO
		
		// update missing match id's in fg_seq
		generalUtilLogger.logWrite(LevelType.INFO, "task-correctRecentSearchData-updateFgSeqSearchMatch", "-1",
				ActivitylogType.Scheduler, null);
		schedTaskDao.updateFgSeqSearchMatch("", "-1");
		
		//fixInfTableDisplayValues
		generalUtilLogger.logWrite(LevelType.INFO, "task-correctRecentSearchData-fixInfTableDisplayValues", "-1",
				ActivitylogType.Scheduler, null);
		schedTaskDao.fixInfTableDisplayValues("", true);
		
		//update files table for search
		generalUtilLogger.logWrite(LevelType.INFO, "task-correctRecentSearchData-updateFilesSrcTable", "-1",
				ActivitylogType.Scheduler, null);
		schedTaskDao.updateFilesSrcTable("");
		
		//handelInfId - separate id csv to records
		generalUtilLogger.logWrite(LevelType.INFO, "task-correctRecentSearchData-handelInfId", "-1",
				ActivitylogType.Scheduler, null);
		schedTaskDao.handelInfId("");
	}

	private void nightTask() throws Exception {
		//correctSearchData
		correctSearchData();
		
		//dbCleanUp
		dbCleanUp();
		
		//cleanUp
		cleanUp();
	}

	private void correctSearchData() throws Exception {
		//handelTransactionFailure
		generalUtilLogger.logWrite(LevelType.INFO, "task-nightTask-correctSearchData-handelTransactionFailure", "-1",
				ActivitylogType.Scheduler, null);
		schedTaskDao.handelTransactionFailure();

		//update webix data - insert data from webix table to webixat pivot//update Webix data 
		generalUtilLogger.logWrite(LevelType.INFO, "task-nightTask-correctSearchData-updateWebixSearch", "-1",
				ActivitylogType.Scheduler, null);
		schedTaskDao.updateWebixSearch("");

		//update result data - insert data from result table to resultat pivot
		generalUtilLogger.logWrite(LevelType.INFO, "task-nightTask-correctSearchData-updateResultSearch", "-1",
				ActivitylogType.Scheduler, null);
		schedTaskDao.updateResultSearch("");

		//complete inf data - add all missing rows
		generalUtilLogger.logWrite(LevelType.INFO,
				"task-nightTask-correctSearchData-correctInfTable(true, true, false, true)", "-1",
				ActivitylogType.Scheduler, null);
		schedTaskDao.correctInfTable(true, true, false, true); //TODO

		// update all missing match id's in fg_seq
		generalUtilLogger.logWrite(LevelType.INFO, "task-nightTask-correctSearchData-updateFgSeqSearchMatch(,-1)", "-1",
				ActivitylogType.Scheduler, null);
		schedTaskDao.updateFgSeqSearchMatch("", "-1");

		//fixInfTableDisplayValues all
		generalUtilLogger.logWrite(LevelType.INFO, "task-nightTask-correctSearchData-fixInfTableDisplayValues(,-1)", "-1",
				ActivitylogType.Scheduler, null);
		schedTaskDao.fixInfTableDisplayValues("", false);

		//update files table for search
		generalUtilLogger.logWrite(LevelType.INFO, "task-nightTask-correctSearchData-updateFilesSrcTable", "-1",
				ActivitylogType.Scheduler, null);
		schedTaskDao.updateFilesSrcTable("");

		//handelInfId - separate id csv to records
		generalUtilLogger.logWrite(LevelType.INFO, "task-nightTask-correctSearchData-handelInfId", "-1",
				ActivitylogType.Scheduler, null);
		schedTaskDao.handelInfId("");

		//schedBuildMoleculesMatrix
		generalUtilLogger.logWrite(LevelType.INFO, "task-nightTask-correctSearchData-buildMoleculesMatrix", "-1",
				ActivitylogType.Scheduler, null);
		chemMatrixTaskDao.buildMoleculesMatrix();
		
	}
	
	private String deleteData(String formCode, String deleteFormDef) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("formCode_in", formCode);
		parameters.put("deleteFormDef_in", deleteFormDef);
		return generalDao.callPackageFunction("FORM_TOOL", "deleteFormData", parameters);
	}
	
	private void deleteNoMaterialJchemSearch() {
		List<String> materialList = generalDao
				.getListOfStringBySql("select formid from FG_I_JCHEM_WITH_NO_MATERIAL t");
		for (String materialId : materialList) {
			chemDao.deleteRowJChemSearchTableNoMaterial(materialId);
		}

	}
	 
	@Transactional
	private void updateChemImage() throws Exception {
		String sql = "select parent_id,mol_type,reaction_all_data_link,FULL_IMG_FILE_ID from FG_CHEM_DOODLE_DATA where reaction_all_data_link is not null";
		List<Map<String, String>> rows = generalDao.getListOfMapsWithClob(sql);
		for (Map<String, String> row : rows) {
			/*
			 * String fullData = generalDao.getSingleStringFromClob("select file_content from fg_clob_files where file_id = '"+ row.get("reaction_all_data_link")+"'"); if(fullData.isEmpty()){ continue; }
			 */
			//String newImgId = chemDao.getNewChemImg(fullData);
			//formSaveDao.updateSingleString("update fg_chem_doodle_data set FULL_IMG_FILE_ID = '"+newImgId+"' where parent_id = '"+row.get("parent_id")+"'");
			integrationEvent.generalEvent(0, row, "", "", "", "", "updateChemImage");
		}
		return;
	}

	private void refreshElementInfoAuditTrailMetaTmpTable() {
		//update updateElementInfoAuditTrailMetaTmpTable
		generalUtilLogger.logWrite(LevelType.INFO, "task-refreshElementInfoAuditTrailMetaTmpTable", "-1",
				ActivitylogType.Scheduler, null);
		cacheService.updateElementInfoAuditTrailMetaTmpTable();
	}

	
	
	//general DB nighth tasks that:
	// clean old log, unlock admin/system password
	private void dbCleanUp() {
		try {
			String desc = "clean old log, unlock admin/system password";
			generalUtilLogger.logWrite(LevelType.INFO, "task-nightTask-dbCleanup-(" + desc + ")", "-1",
					ActivitylogType.Scheduler, null);
			schedTaskDao.dbCleanup();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			generalUtilLogger.logWrite(LevelType.ERROR, "Error in dbCleanUp call", "",
					ActivitylogType.Scheduler, null, e);
		}
	}
	
	private void cleanUp() {
		try {
			String desc = "clean ireport tmp files";
			generalUtilLogger.logWrite(LevelType.INFO, "task-nightTask-cleanup-(" + desc + ")", "-1",
					ActivitylogType.Scheduler, null);
			removeTempFiles(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			generalUtilLogger.logWrite(LevelType.ERROR, "Error in cleanUp call", "",
					ActivitylogType.Scheduler, null, e);
		}
	}
	 
	//remove temporary files that were created before period finish
	private void removeTempFiles(int period) {
		Calendar currentDate = Calendar.getInstance(); // creates calendar

		File tmpDir = new File(DIR_TEP_FILES + "/tmp");
		File[] fileListArray = tmpDir.listFiles();
		if (fileListArray.length > 0) {
			for (File tmpFile : fileListArray) {

				Calendar fileTimeStamp = Calendar.getInstance();
				fileTimeStamp.setTime(new Date(tmpFile.lastModified()));
				fileTimeStamp.add(Calendar.HOUR, period);
				if (!tmpFile.getName().contains("readme") && currentDate.getTime().after(fileTimeStamp.getTime())) {
					tmpFile.delete();
				}
			}
		}
	}

	private void correctMobilephase() {
		// Note: before we need to run:
		//update fg_s_MobilePhaseCompos_pivot t set t.mobilephasecomposobj = REGEXP_REPLACE (t.mobilephasecomposobj,'"parentId":"(.*?)"','"parentId":"' || t.parentId || '"') where instr(t.mobilephasecomposobj,'"parentId":"' || t.parentId || '"') = 0

		String sql = "select t.mobilephasecomposobj from FG_S_MOBILEPHASECOMPOS_PIVOT t where t.parentid not in (\r\n"
				+ "select FG_DYNAMICPARAMS.Parent_Id from FG_DYNAMICPARAMS\r\n" + ")";

		List<String> mpData = generalDao.getListOfStringBySql(sql);

		for (String jobj_ : mpData) {
			formSaveElemetDao.saveDynamicParams(jobj_);
		}
	}

	// **************** marvin ****************
	private void jchemDataMigration() throws Exception {
		moveChemCmlToClob(); // (1)
		// move the cml data from fg_chem_doodle_data.reaction_all_data if not null to fg_clob_file 
		// and put the id in fg_chem_doodle_data.reaction_all_data_link (see the function saveChemData in ChemDaoMarvinImp) -  (you can check your with the sql in getMRVContent)

		updateChemSearch(); // (2)
		// insert chem xml for each fg_chem_doodle_data.parent_id that exists in fg_s_invitemmaterial_pivot.structure (with active 1) 
		// (see the function saveJChem in ChemDaoMarvinImp) to fg_chem_search. 
		// USE THIS SQL: select m.InvItemMaterialName, c.* from FG_CHEM_DOODLE_DATA c, fg_s_invitemmaterial_v m where m.STRUCTURE = c.parent_id and m.active = 1	
	}

	private void moveChemCmlToClob() {
		Map<String, String> parameters = new HashMap<String, String>();
		generalDao.callPackageFunction("FG_ADAMA_TASK_BY_DATE", "FG_FIX_CHEM_DOODLE_DATA", parameters);
	}

	private void updateChemSearch() throws Exception {
		String str = generalDao.getCSVBySql(
				"select c.parent_id ||';'|| m.formid ||';'|| c.mol_type ||';'|| c.reaction_all_data_link"
						+ " from fg_s_invitemmaterial_v m, FG_CHEM_DOODLE_DATA c"
						+ " where m.STRUCTURE = c.parent_id and m.active = 1 and c.reaction_all_data_link is not null and c.smiles_data is not null",
				false);
		String[] arr = str.split(",");
		String[] arrOfElements;
		String sClob;
		for (int i = 0; i < arr.length; i++) {
			arrOfElements = arr[i].split(";");
			sClob = "";
			try {
				sClob = generalDao.getSingleStringFromClob(
						"select t.file_content from fg_clob_files t where t.file_id = '" + arrOfElements[3] + "'");
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (!sClob.equals("")) {
					System.out.println("parent_id= " + arrOfElements[0] + ", formid= " + arrOfElements[1] + ", "
							+ arrOfElements[2]);
					chemDao.saveJChemWithBySchedTask(arrOfElements[0], arrOfElements[1], "InvItemMaterial.structure",
							arrOfElements[2], sClob, DB_USER + ".FG_CHEM_SEARCH");
				}
			} catch (Exception e) { //presumably this exception will never happen
				e.printStackTrace();
				logger.error("updateChemSearch Exception!");
			}
		}
		System.out.println("updateChemSearch is finished to run");
	}

	// **************** MATERIAL DUPLICATION CHECK ****************
	private void findOutDuplicateMaterials() {
		List<Map<String, Object>> tableMainList = null;
		List<Map<String, Object>> tableSecList = null;
		Map<String, String> materialNameDuplicatesResultsMap = new HashMap<String, String>();
		Map<String, String> casnumberDuplicatesResultsMap = new HashMap<String, String>();
		Map<String, String> itemidDuplicatesResultsMap = new HashMap<String, String>();
		Map<String, String> synonymDuplicatesResultsMap = new HashMap<String, String>();
		Set<String> objUniqueSet = new HashSet<String>();

		try {
			String sql = "select distinct t.formid, t.synonymsadapted, t.invitemmaterialname, t.casnumber, t.itemid, t.synonyms \n"
					+ " from fg_s_invitemmaterial_pivot t, fg_s_materialstatus_v st \n" + " where 1=1 \n"
					+ " and nvl(st.\"MATERIALSTATUSNAME\",'Active') != 'Cancelled' \n"
					+ " and t.status_id = st.materialstatus_id(+) \n" + " and t.sessionid is null \n"
					+ " and lower(t.materialprotocoltype) = 'chemical material' and nvl(t.coformulant,'0') <> '1' and t.active = 1";

			tableMainList = generalDao.getListOfMapsBySql(sql);
			tableSecList = tableMainList;

			//1: check material name for duplicates in material names, synonyms or part of synonyms
			for (Map<String, Object> mainMap : tableMainList) {
				Object maintmpvalue = mainMap.get("INVITEMMATERIALNAME");
				String mainMaterialName = (maintmpvalue == null) ? "" : maintmpvalue.toString().trim().toLowerCase();
				if (!objUniqueSet.contains(mainMaterialName)) {
					objUniqueSet.add(mainMaterialName);
				} else {
					continue;
				}
				StringBuilder formidStr = new StringBuilder();
				String mainFormId = mainMap.get("FORMID").toString();

				for (Map<String, Object> secMap : tableSecList) {
					String currFormId = secMap.get("FORMID").toString();
					if (mainFormId.equals(currFormId)) {
						continue;
					}

					Object mattmpvalue = secMap.get("INVITEMMATERIALNAME");
					String currMaterialName = (mattmpvalue == null) ? "" : mattmpvalue.toString().trim().toLowerCase();
					if (mainMaterialName.equals(currMaterialName)) {
						formidStr.append(currFormId).append(",");

						//*** add to LOG
						Map<String, String> additInfo = new HashMap<String, String>();
						additInfo.put("material name", mattmpvalue.toString().trim());
						//additInfo.put("form_id", currFormId);
						generalUtilLogger.logWrite(LevelType.Other, "material name", maintmpvalue.toString().trim(),
								ActivitylogType.DuplicatedMaterials, additInfo);

						continue;
					}

					Object syntmpvalue = secMap.get("SYNONYMSADAPTED");
					String currSynonymAdapted = (syntmpvalue == null) ? ""
							: syntmpvalue.toString().trim().toLowerCase();
					if (!currSynonymAdapted.equals("")) {
						if (mainMaterialName.equals(currSynonymAdapted)) {
							formidStr.append(currFormId).append(",");

							//*** add to LOG
							Map<String, String> additInfo = new LinkedHashMap<String, String>();
							additInfo.put("synonym", secMap.get("synonyms").toString().trim());
							additInfo.put("material name", mattmpvalue.toString().trim());
							//additInfo.put("form_id", currFormId);
							generalUtilLogger.logWrite(LevelType.Other, "material name", maintmpvalue.toString().trim(),
									ActivitylogType.DuplicatedMaterials, additInfo);

							continue;
						}
						//check if main material name is part of current synonym
						List<String> currSynonymAdaptedArr = Arrays
								.asList(currSynonymAdapted.split(systemSynonymDelim));
						for (String cursynonymPart : currSynonymAdaptedArr) {
							if (cursynonymPart.equals(mainMaterialName)) {
								formidStr.append(currFormId).append(",");

								//*** add to LOG
								Map<String, String> additInfo = new LinkedHashMap<String, String>();
								additInfo.put("synonyms", secMap.get("synonyms").toString().trim());
								additInfo.put("material name", mattmpvalue.toString().trim());
								//additInfo.put("form_id", currFormId);
								generalUtilLogger.logWrite(LevelType.Other, "material name",
										maintmpvalue.toString().trim(), ActivitylogType.DuplicatedMaterials, additInfo);

								break;
							}
						}
					}
				}
				if (formidStr.length() > 0) {
					//*** add to LOG
					Map<String, String> additInfo = new HashMap<String, String>();
					additInfo.put("material name", maintmpvalue.toString().trim());
					//additInfo.put("form_id", mainFormId);
					generalUtilLogger.logWrite(LevelType.Other, "material name", maintmpvalue.toString().trim(),
							ActivitylogType.DuplicatedMaterials, additInfo);

					materialNameDuplicatesResultsMap.put(mainMaterialName, formidStr.append(mainFormId).toString());
				}
			}
			//2: check synonym for duplicates
			objUniqueSet = new HashSet<String>();
			for (Map<String, Object> mainMap : tableMainList) {
				Object mainmattmpvalue = mainMap.get("INVITEMMATERIALNAME");
				String mainMaterialName = (mainmattmpvalue == null) ? "" : mainmattmpvalue.toString().trim();

				Object maintmpvalue = mainMap.get("SYNONYMSADAPTED");
				String mainSynonymAdapted = (maintmpvalue == null) ? "" : maintmpvalue.toString().trim().toLowerCase();

				if (mainSynonymAdapted.equals("")) {
					continue;
				}

				if (!objUniqueSet.contains(mainSynonymAdapted)) {
					objUniqueSet.add(mainSynonymAdapted);
				} else {
					continue;
				}
				StringBuilder formidStr = new StringBuilder();
				String mainFormId = mainMap.get("FORMID").toString();
				List<String> mainSynonymAdaptedArr = Arrays.asList(mainSynonymAdapted.split(systemSynonymDelim));

				for (Map<String, Object> secMap : tableSecList) {
					String currFormId = secMap.get("FORMID").toString();
					if (mainFormId.equals(currFormId)) {
						continue;
					}

					Object tmpvalue = secMap.get("SYNONYMSADAPTED");
					String currSynonymAdapted = (tmpvalue == null) ? "" : tmpvalue.toString().trim().toLowerCase();

					if (currSynonymAdapted.equals("")) {
						continue;
					}

					Object mattmpvalue = secMap.get("INVITEMMATERIALNAME");
					String currMaterialName = (mattmpvalue == null) ? "" : mattmpvalue.toString().trim();
					//1: compare whole main synonymsAdapted with current synonymsAdapted
					if (mainSynonymAdapted.equals(currSynonymAdapted)) {
						formidStr.append(currFormId).append(",");

						//*** add to LOG
						Map<String, String> additInfo = new LinkedHashMap<String, String>();
						additInfo.put("synonyms", secMap.get("synonyms").toString().trim());
						additInfo.put("material name", currMaterialName);
						//additInfo.put("form_id", currFormId);
						generalUtilLogger.logWrite(LevelType.Other, "synonyms", mainMaterialName,
								ActivitylogType.DuplicatedMaterials, additInfo);

						continue;
					}
					//2: check if each part of a main synonym is equal to part of current synonym
					List<String> currSynonymAdaptedArr = Arrays.asList(currSynonymAdapted.split(systemSynonymDelim));
					for (String mainsynonymPart : mainSynonymAdaptedArr) {
						if (currSynonymAdaptedArr.contains(mainsynonymPart)) {
							formidStr.append(currFormId).append(",");

							//*** add to LOG
							Map<String, String> additInfo = new LinkedHashMap<String, String>();
							additInfo.put("synonyms", secMap.get("synonyms").toString().trim());
							additInfo.put("material name", currMaterialName);
							//additInfo.put("form_id", currFormId);
							generalUtilLogger.logWrite(LevelType.Other, "synonyms", mainMaterialName,
									ActivitylogType.DuplicatedMaterials, additInfo);

							break;
						}
					}
				}
				if (formidStr.length() > 0) {
					//*** add to LOG
					Map<String, String> additInfo = new LinkedHashMap<String, String>();
					additInfo.put("synonyms", mainMap.get("synonyms").toString().trim());
					additInfo.put("material name", mainMaterialName);
					//additInfo.put("form_id", mainFormId);
					generalUtilLogger.logWrite(LevelType.Other, "synonyms", mainMaterialName,
							ActivitylogType.DuplicatedMaterials, additInfo);

					synonymDuplicatesResultsMap.put(mainSynonymAdapted, formidStr.append(mainFormId).toString());
				}
			}
			//3: check casnumber for duplicates
			objUniqueSet = new HashSet<String>();
			for (Map<String, Object> mainMap : tableMainList) {
				Object mainmattmpvalue = mainMap.get("INVITEMMATERIALNAME");
				String mainMaterialName = (mainmattmpvalue == null) ? "" : mainmattmpvalue.toString().trim();

				Object maintmpvalue = mainMap.get("casnumber");
				String mainCasnumber = (maintmpvalue == null) ? "" : maintmpvalue.toString().trim().toLowerCase();
				if (mainCasnumber.equals("")) {
					continue;
				}
				if (!objUniqueSet.contains(mainCasnumber)) {
					objUniqueSet.add(mainCasnumber);
				} else {
					continue;
				}
				StringBuilder formidStr = new StringBuilder();
				String mainFormId = mainMap.get("FORMID").toString();

				for (Map<String, Object> secMap : tableSecList) {
					String currFormId = secMap.get("FORMID").toString();
					if (mainFormId.equals(currFormId)) {
						continue;
					}

					Object tmpvalue = secMap.get("casnumber");
					String currCasnumber = (tmpvalue == null) ? "" : tmpvalue.toString().trim().toLowerCase();

					if (currCasnumber.equals("")) {
						continue;
					}
					if (mainCasnumber.equals(currCasnumber)) {
						//*** add to LOG
						Map<String, String> additInfo = new LinkedHashMap<String, String>();
						Object mattmpvalue = secMap.get("INVITEMMATERIALNAME");
						String currMaterialName = (mattmpvalue == null) ? "" : mattmpvalue.toString().trim();

						additInfo.put("casnumber", tmpvalue.toString().trim());
						additInfo.put("material name", currMaterialName);
						//additInfo.put("form_id", currFormId);
						generalUtilLogger.logWrite(LevelType.Other, "casnumber", mainMaterialName,
								ActivitylogType.DuplicatedMaterials, additInfo);

						formidStr.append(currFormId).append(",");
						continue;
					}
				}
				if (formidStr.length() > 0) {
					//*** add to LOG
					Map<String, String> additInfo = new LinkedHashMap<String, String>();
					additInfo.put("casnumber", maintmpvalue.toString().trim());
					additInfo.put("material name", mainMaterialName);
					//additInfo.put("form_id", mainFormId);
					generalUtilLogger.logWrite(LevelType.Other, "casnumber", mainMaterialName,
							ActivitylogType.DuplicatedMaterials, additInfo);

					casnumberDuplicatesResultsMap.put(mainCasnumber, formidStr.append(mainFormId).toString());
				}
			}
			//4: check itemid for duplicates
			objUniqueSet = new HashSet<String>();
			for (Map<String, Object> mainMap : tableMainList) {
				Object mainmattmpvalue = mainMap.get("INVITEMMATERIALNAME");
				String mainMaterialName = (mainmattmpvalue == null) ? "" : mainmattmpvalue.toString().trim();

				Object maintmpvalue = mainMap.get("itemid");
				String mainItemId = (maintmpvalue == null) ? "" : maintmpvalue.toString().trim().toLowerCase();

				if (mainItemId.equals("")) {
					continue;
				}

				if (!objUniqueSet.contains(mainItemId)) {
					objUniqueSet.add(mainItemId);
				} else {
					continue;
				}
				StringBuilder formidStr = new StringBuilder();
				String mainFormId = mainMap.get("FORMID").toString();

				for (Map<String, Object> secMap : tableSecList) {
					String currFormId = secMap.get("FORMID").toString();
					if (mainFormId.equals(currFormId)) {
						continue;
					}

					Object tmpvalue = secMap.get("itemid");
					String currItemId = (tmpvalue == null) ? "" : tmpvalue.toString().trim().toLowerCase();

					if (currItemId.equals("")) {
						continue;
					}
					if (mainItemId.equals(currItemId)) {
						//*** add to LOG
						Map<String, String> additInfo = new LinkedHashMap<String, String>();
						Object mattmpvalue = secMap.get("INVITEMMATERIALNAME");
						String currMaterialName = (mattmpvalue == null) ? "" : mattmpvalue.toString().trim();

						additInfo.put("item id", tmpvalue.toString().trim());
						additInfo.put("material name", currMaterialName);
						//additInfo.put("form_id", currFormId);
						generalUtilLogger.logWrite(LevelType.Other, "item id", mainMaterialName,
								ActivitylogType.DuplicatedMaterials, additInfo);

						formidStr.append(currFormId).append(",");
						continue;
					}
				}
				if (formidStr.length() > 0) {
					//*** add to LOG
					Map<String, String> additInfo = new LinkedHashMap<String, String>();
					additInfo.put("item id", maintmpvalue.toString().trim());
					additInfo.put("material name", mainMaterialName);
					//additInfo.put("form_id", mainFormId);
					generalUtilLogger.logWrite(LevelType.Other, "item id", mainMaterialName,
							ActivitylogType.DuplicatedMaterials, additInfo);

					itemidDuplicatesResultsMap.put(mainItemId, formidStr.append(mainFormId).toString());
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (cleanChemTableAndCheckForDuplicates.equals("1")) {
			String isCleaned = chemDao.cleanJChemSearchTable();

			if (isCleaned.equals("1")) {

				try {
					int existsFormId = 0;
					String str = generalDao.getCSVBySql(
							"select c.parent_id ||';'|| m.formid ||';'|| c.mol_type ||';'|| c.reaction_all_data_link"
									+ " from fg_s_invitemmaterial_v m, FG_CHEM_DOODLE_DATA c"
									+ " where m.STRUCTURE = c.parent_id and m.active = 1 and c.reaction_all_data_link is not null and c.smiles_data is not null",
							false);
					String[] arr = str.split(",");
					String[] arrOfElements;
					String sClob;
					for (int i = 0; i < arr.length; i++) {
						arrOfElements = arr[i].split(";");
						sClob = "";
						try {
							sClob = generalDao.getSingleStringFromClob(
									"select t.file_content from fg_clob_files t where t.file_id = '" + arrOfElements[3] + "'");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							if (!sClob.equals("")) {
								System.out.println("parent_id= " + arrOfElements[0] + ", formid= " + arrOfElements[1]
										+ ", " + arrOfElements[2]);
								existsFormId = chemDao.saveJChemWithBySchedTask(arrOfElements[0], arrOfElements[1],
										"InvItemMaterial.structure", arrOfElements[2], sClob,
										DB_USER + ".FG_CHEM_SEARCH");
							}
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("updateChemSearch Exception!");
						}

						if (existsFormId < 0) {
							String existsStructMaterialName = generalDao.selectSingleString(
									"select i.INVITEMMATERIALNAME from fg_chem_search s, fg_s_invitemmaterial_all_v i "
											+ " where s.fullformcode = 'InvItemMaterial.structure' and s.elementid = i.STRUCTURE and s.cd_id = '"
											+ String.valueOf(Math.abs(existsFormId)) + "'");
							String newStructMaterialName = generalDao.selectSingleString(
									"select t.invitemmaterialname from fg_s_invitemmaterial_pivot t where t.formid = '"
											+ arrOfElements[1] + "'");

							//*** add to LOG
							Map<String, String> additInfo = new HashMap<String, String>();
							additInfo.put("material name", newStructMaterialName);
							//additInfo.put("form_id", arrOfElements[1]);
							generalUtilLogger.logWrite(LevelType.Other, "structure", existsStructMaterialName,
									ActivitylogType.DuplicatedMaterials, additInfo);

							additInfo = new HashMap<String, String>();
							additInfo.put("material name", existsStructMaterialName);
							//additInfo.put("form_id", String.valueOf(Math.abs(existsFormId)));
							generalUtilLogger.logWrite(LevelType.Other, "structure", existsStructMaterialName,
									ActivitylogType.DuplicatedMaterials, additInfo);

						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		System.out.println("findOutDuplicateMaterials is done");
		//generalUtilLogger.logWrite(LevelType.Other, "", formId, ActivitylogType, infoMap);
	}

	private void updateMaterialSynonym() {
		List<Map<String, Object>> mainList = null;

		try {
			String sql = "select t.formid, t.synonyms \n " + " from fg_s_invitemmaterial_pivot t \n"
					+ " where t.synonyms is not null \n"
					//+" and t.synonymsadapted is null \n"
					+ " and t.sessionid is null and t.active = 1 and lower(t.materialprotocoltype) = 'chemical material' and nvl(t.coformulant,'0') <> '1' ";

			mainList = generalDao.getListOfMapsBySql(sql);

			for (Map<String, Object> mainMap : mainList) {
				try {
					String fieldVal = mainMap.get("synonyms").toString().trim();
					if (!fieldVal.equals("")) {
						/* split value by delimiter with removing leading and trailing spaces around the delimiter */
						System.out.println("value to split:|" + fieldVal + "|");
						String[] fieldValArr = fieldVal.split("\\s*" + customerSynDelim + "\\s*");
						String fieldValAdapted = String.join(systemSynonymDelim, fieldValArr);
						System.out.println("join:|" + fieldValAdapted + "|");
						String fieldValAdaptedNoUpperCommas = fieldValAdapted.replaceAll("'", "''");

						String updateSql = "update fg_s_invitemmaterial_pivot pt \n" + " set pt.synonymsadapted = '"
								+ fieldValAdaptedNoUpperCommas + "' \n" + " where pt.formid = " + mainMap.get("formid");
						generalDao.updateSingleString(updateSql);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initProperties() {
		PropertiesConfiguration config = null;
		try {
			config = new PropertiesConfiguration("app.properties");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		config.setReloadingStrategy(new FileChangedReloadingStrategy());

		try {
			springMessages.reloadMessages();
			springNameDictionary.reloadDictionary();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
