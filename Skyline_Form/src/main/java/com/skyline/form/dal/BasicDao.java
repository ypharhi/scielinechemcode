package com.skyline.form.dal;

import java.sql.Connection;

//import java.util.Properties;

//import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

@Component
public class BasicDao {

	protected JdbcTemplate jdbcTemplate;

	private DataSource dataSource;

	@Value("${sqlTimeOut:360}")
	private int sqlTimeOut;

	@Value("${jdbc.username}")
	protected String jdbcUsername;

	@Value("${useReleaseConnection:1}")
	private int useReleaseConnection; // yp23052019 it seems that we need to use ReleaseConnection (that check if Returning JDBC Connection to DataSource is needed)

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		if (jdbcTemplate == null) {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
			this.jdbcTemplate.setQueryTimeout(sqlTimeOut);
		}
	}

	public Connection getConnectionFromDataSurce() {
		// ....use dataSource to create connection
		//		logConnectionPoolInfo(); show if needed...
		Connection conn = DataSourceUtils.getConnection(dataSource);
		//         System.out.println("track connection - getConnectionFromDataSurce - " + conn);
		return conn;
	}

	public void releaseConnectionFromDataSurce(Connection con) {
		if (useReleaseConnection == 1) {
			//			System.out.println("track connection - releaseConnectionFromDataSurce - " + con);
			DataSourceUtils.releaseConnection(con, dataSource);
		}
	}

	//	private void logConnectionPoolInfo() {
	//		System.out.println("connection pool info - getMaxActive=" + ((BasicDataSource)dataSource).getMaxActive());
	//		System.out.println("connection pool info - getMaxOpenPreparedStatements=" + ((BasicDataSource)dataSource).getMaxOpenPreparedStatements());
	//		System.out.println("connection pool info - getMinIdle=" + ((BasicDataSource)dataSource).getMinIdle());
	//		System.out.println("connection pool info - getMaxIdle=" + ((BasicDataSource)dataSource).getMaxIdle());
	//		System.out.println("connection pool info - getMaxWait=" + ((BasicDataSource)dataSource).getMaxWait());
	//    }

	public void setScheSQLTimeOut() {
		this.jdbcTemplate.setQueryTimeout(0);

	}

	public void setDefaultSQLTimeOut() {
		this.jdbcTemplate.setQueryTimeout(sqlTimeOut);
	}

	//	public void chnageDatasourceUrl() { 
	//		
	//		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	//		HttpSession session = attr.getRequest().getSession();
	//		
	////		ApplicationContext context = new ClassPathXmlApplicationContext("C:\\code\\gitComp\\AdamaMaster\\Adama\\Skyline_Form\\src\\main\\webapp\\WEB-INF\\mvc-beans.xml");
	//		ApplicationContext context =
	//                WebApplicationContextUtils.
	//                      getWebApplicationContext(session.getServletContext());
	//		DataSource ds = (DataSource) context.getBean("dataSource");
	//		this.jdbcTemplate.setDataSource(ds);
	//	}

}
