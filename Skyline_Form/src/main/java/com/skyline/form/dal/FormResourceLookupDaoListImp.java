package com.skyline.form.dal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.FormEntity;
import com.skyline.form.entity.Catalog;
import com.skyline.form.entity.Element;
import com.skyline.form.entity.Entity;
import com.skyline.form.entity.EntityFactory;
import com.skyline.form.entity.Layout;
import com.skyline.form.service.CacheService;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;

@Repository("FormResourceLookupDao")
public class FormResourceLookupDaoListImp extends BasicDao implements FormResourceLookupDao {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	private EntityFactory entityFactory;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	@Value("${HtmlPath}")
	private String Htmlpath;

	@Value("${jspPath}")
	private String jspPath;

	@Value("${ireportPath}")
	private String ireportPath;

	private List<String> entityList;

	@Autowired
	ApplicationContext applicationContext;

	//	@Autowired
	//	FormStateManager formStateManager;

	//	@Autowired
	//	GeneralUtilFormState generalUtilFormState;

	@Autowired
	private FormDao formDao;

	@Autowired
	private CacheService cacheService;

	private static final Logger logger = LoggerFactory.getLogger(FormResourceLookupDaoListImp.class);

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<DataBean> resourceLookUp(String type, String formCode) {
		List<DataBean> resourceList = new ArrayList<DataBean>();
		type = type.replaceAll("%", ".*");
		try {

			//+formInstanceResource
			List<DataBean> formInstanceResource = getFormInstanceResource(type, formCode);
			//+FormTypeResource
			List<DataBean> FormTypeResource = getResources(
					"select 0 as ID, 'FORM_CODE_' || t.Form_Type as TYPE, t.formcode as CODE, t.formcode as VALUE, null as INFO from fg_form t where 'FORM_CODE_' || t.Form_Type LIKE '"
							+ type + "'");
			//+ImpSchema
			List<DataBean> impSchemaResource = getImpSchemaResource(type);
			//+filesNames
			List<DataBean> filesNames = getFilesNames(type);
			//+ DB resources from FG_RESOURCE
			resourceList.addAll(getResources("select * from FG_RESOURCE where TYPE LIKE '" + type + "'"));
			resourceList.addAll(formInstanceResource);
			resourceList.addAll(FormTypeResource);
			resourceList.addAll(impSchemaResource);
			resourceList.addAll(filesNames);

			//sort list
			Collections.sort(resourceList);

		} catch (Exception e) {
			resourceList = null;
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return resourceList;
	}

	private List<DataBean> getResources(String query) {
		logger.info("/* SQL resourceLookUp query= */" + query);
		List<DataBean> impSchemaResourceData = jdbcTemplate.query(query, new DataBeanMapperForResourceLookUp());
		return impSchemaResourceData;
	}

	private List<DataBean> getFilesNames(String path) {
		String propertyPath = "";
		List<DataBean> impSchemaResourceData = new ArrayList<DataBean>();
		if (path.equals("PATH_HTML_POOL")) {
			propertyPath = Htmlpath;
		} else if (path.equals("PATH_JSP_GENERAL")) {
			propertyPath = jspPath + "/" + "GENERAL";
		} else if (path.equals("PATH_JSP_REPORT")) {
			propertyPath = jspPath + "/" + "REPORT";
		} else if (path.equals("PATH_JSP_LABEL")) {
			propertyPath = jspPath + "/" + "LABEL";
		} else if (path.equals("PATH_JSP_STRUCT")) {
			propertyPath = jspPath + "/" + "STRUCT";
		} else if (path.equals("PATH_JSP_MAINTENANCE")) {
			propertyPath = jspPath + "/" + "MAINTENANCE";
		} else if (path.equals("PATH_JSP_ATTACHMENT")) {
			propertyPath = jspPath + "/" + "ATTACHMENT";
		} else if (path.equals("PATH_JSP_INVITEM")) {
			propertyPath = jspPath + "/" + "INVITEM";
		} else if (path.equals("PATH_JSP_SMARTSEARCH")) {
			propertyPath = jspPath + "/" + "SMARTSEARCH";
		} else if (path.equals("PATH_JSP_SELECT")) {
			propertyPath = jspPath + "/" + "SELECT";
		} else if (path.equals("PATH_JSP_REF")) {
			propertyPath = jspPath + "/" + "REF";
		} else if (path.equals("PATH_JSP")) {
			propertyPath = jspPath;
		} else if (path.equals("PATH_IREPORT_POOL")) {
			propertyPath = ireportPath;
		} else {
			return impSchemaResourceData;
		}
		List<String> files = generalUtil.getFilesNames(propertyPath);
		for (String file : files) {
			if ((!file.contains("demoFormBuilder")) && (!file.contains("demoFormBuilderMain"))
					&& (!file.equals("index.jsp")) && (!file.equals("include.jsp"))
					&& (!file.equals("PageHeaderJsoTemplateForm.inc")) && (!file.equals("PageHeaderNewForm.inc"))
					&& (propertyPath != ireportPath || file.endsWith(".xml"))) {

				DataBean dataBean = new DataBean();
				dataBean.setId((long) 0);
				dataBean.setType(BeanType.valueOf(path));
				dataBean.setCode(file);
				dataBean.setVal(file);
				dataBean.setInfo(file);

				impSchemaResourceData.add(dataBean);
			}
		}
		return impSchemaResourceData;

	}

	private List<DataBean> getFormInstanceResource(String type, String formCode) {
		List<DataBean> resourceForFormBuilderList = new ArrayList<DataBean>();
		Map<String, Entity> cacheServiceMap = cacheService.getFormEntityClassSingleToneMap();
		//		for (Map.Entry<String, Entity> entry : cacheServiceMap.entrySet()) {
		//			//toReturn.append("\nid: " + entry.getKey() + ", value: " + entry.getValue());
		//		}
		//		long emptyStateKey = 0l;
		//		if (!generalUtil.getNull(formCode).equals("")) { 
		//			for (DataBean databean : generalUtilFormState.getFormBeanImpResource(emptyStateKey, formCode)) {
		//				if(databean.getType().getTypeName().matches(type))	
		//				{
		//					databean.setId((long) 0);
		//					resourceForFormBuilderList.add(databean);
		//				}
		//			}
		//		}
		if (!generalUtil.getNull(formCode).equals("")) {
			List<FormEntity> formEntityList = formDao.getFormEntityInfoLookup(formCode, "%");
			for (FormEntity formEntity : formEntityList) {
				Entity entity = cacheServiceMap.get(formEntity.getEntityImpClass());
				for (DataBean databean : entity.getImpResourceForFormBuilder(formEntity.getFormCode(),
						formEntity.getEntityImpCode(), formEntity.getEntityImpInit())) {
					if (databean.getType().getTypeName().matches(type)) {
						databean.setId((long) 0);
						resourceForFormBuilderList.add(databean);
					}
				}
			}
		}
		return resourceForFormBuilderList;
	}

	private List<DataBean> getImpSchemaResource(String type) {
		List<DataBean> impSchemaResourceData = new ArrayList<DataBean>();
		String beanLayer;
		if (type.matches("LAYER_.*_SCHEMA")) {
			try {
				// get implemented interfaces			
				if (entityList == null) {
					entityList = getEntityList();
				}
				for (String entityName : entityList) {
					beanLayer = ""; //"%";
					Entity entity = entityFactory.getEntity(entityName);//TODO too slow
					if (entity instanceof Layout) {
						beanLayer = "LAYOUT";
					}
					if (entity instanceof Catalog) {
						beanLayer = "CATALOG";
					}
					if (entity instanceof Element) {
						beanLayer = "ELEMENT";
					}
					if (entity.getActive()) {

						if (!beanLayer.isEmpty() && ("LAYER_" + beanLayer + "_SCHEMA").matches(type)) {
							DataBean dataBean = new DataBean();
							dataBean.setId((long) 0);
							dataBean.setType(BeanType.valueOf("LAYER_" + beanLayer + "_SCHEMA"));
							dataBean.setCode(entityName);
							dataBean.setVal(entity.getInitSchemaVal().replaceAll("'", "\""));
							dataBean.setInfo(entity.getImpName());

							impSchemaResourceData.add(dataBean);
						}
					}
				}
			} catch (Exception e) {
				generalUtilLogger.logWrite(e);
				e.printStackTrace();
			}
		}
		return impSchemaResourceData;
	}

	private List<String> getEntityList() {
		List<String> entityList = new ArrayList<String>();
		List<String> beanList = Arrays.asList(applicationContext.getBeanDefinitionNames());
		for (String bean : beanList) {
			if ((bean.endsWith("Imp")) && (applicationContext.getBean(bean) instanceof Entity)) {
				Entity entity = (Entity) applicationContext.getBean(bean);
				if (entity.getActive()) {
					entityList.add(entity.getClass().getSimpleName());
				}
			}
		}
		//		List<String> entityList = new ArrayList<String>();
		//		entityList.add("CatalogDBTableImp");
		//		entityList.add("ElementAutoCompleteDDLImp");
		//		entityList.add("ElementDataTableImp");
		//		entityList.add("ElementIreportImp");
		//		entityList.add("LayoutDesignHtmlImp");
		//		entityList.add("LayoutJspSelectImp");
		return entityList;
	}

	public class DataBeanMapperForResourceLookUp implements RowMapper<DataBean> {
		public DataBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			DataBean DataBean = new DataBean(Long.parseLong(rs.getString("ID")), BeanType.valueOf(rs.getString("type")),
					rs.getString("Code"), rs.getString("value"), rs.getString("info"));
			return DataBean;
		}
	}

}
