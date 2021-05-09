package com.skyline.form.aspect;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.SqlTiming;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;

@Aspect
public class SqlProfiler {

//	private final Map<String, SqlTiming> sqlTimings;

	@Autowired
	protected GeneralUtilFormState generalUtilFormState;

	@Autowired
	protected GeneralUtilLogger generalUtilLogger;
	 
	@Value("${sqlTimingToLog:200}")
	private Long sqlTimingToLog;
	
	@Value("${daoFuncTimingToLog:200}")
	private Long daoFuncTimingToLog;

//	public SqlProfiler() {
//		sqlTimings = Collections.synchronizedMap(new HashMap<String, SqlTiming>());
//	}
//  need Aspect been and aop:aspectj-autoproxy in spring-bean-xml (in comment)
	@Pointcut("execution(* com.skyline.form.dal.UploadFileDao.*(..)) || execution(* com.skyline.form.dal.GeneralDao.*(..))")
	public void abcPointcut() {
	}
	
	@Pointcut("execution(* com.skyline.form.dal.FormSaveDao.*(..))")
	public void formSavePointcut() {
	}

	@Around("abcPointcut()")
	public Object myadvice(ProceedingJoinPoint pjp) throws Throwable {

		long start = System.currentTimeMillis();
		Object obj = pjp.proceed();
		long time = System.currentTimeMillis() - start;
		String statement = pjp.toShortString().contains("callPackageFunction")?pjp.getArgs()[1].toString():pjp.getArgs()[0].toString();
		SqlTiming sqlTiming = new SqlTiming(statement);
//		synchronized (sqlTimings) {
//			sqlTiming = sqlTimings.get(statement);
//			if (sqlTiming == null) {
//				sqlTiming = new SqlTiming(statement);
//				sqlTimings.put(statement, sqlTiming);
//			}
//		}
		sqlTiming.recordTiming(time);

		sqlTiming.getCumulativeExecutionTime();
		Map<String, String> inf_ = new HashMap<String, String>();
		inf_.put("SQL_TIME", String.valueOf(sqlTiming.getCumulativeExecutionTime()));
//		inf_.put("SQL_TIME_STRING",
//				String.valueOf((100000000000000l + sqlTiming.getCumulativeExecutionTime())).substring(1));
		if(statement.contains("_DTM_")){//added it in order to always get the queries of the main form
			generalUtilLogger.logWrite(LevelType.INFO,
					sqlTiming.getSqlStatement(), "-1",
					ActivitylogType.PerformanceSQL, inf_);
		} else {
			if(sqlTimingToLog > 0l && sqlTiming.getCumulativeExecutionTime() > sqlTimingToLog) {
				generalUtilLogger.logWrite(LevelType.INFO,
						sqlTiming.getSqlStatement(), "-1",
						ActivitylogType.PerformanceSQL, inf_);
			}
		}
 
		return obj;
	}
	
//	@Around("formSavePointcut()")
	public Object formSavePoint(ProceedingJoinPoint pjp) throws Throwable {

		long start = System.currentTimeMillis();
		Object obj = pjp.proceed();
		long time = System.currentTimeMillis() - start;
		String statement = pjp.toShortString();
		SqlTiming sqlTiming = new SqlTiming(statement);
		sqlTiming.recordTiming(time);

		sqlTiming.getCumulativeExecutionTime();
		Map<String, String> inf_ = new HashMap<String, String>();
		inf_.put("RUN_TIME", String.valueOf(sqlTiming.getCumulativeExecutionTime()));
//		inf_.put("SQL_TIME_STRING",
//				String.valueOf((100000000000000l + sqlTiming.getCumulativeExecutionTime())).substring(1));
		if(sqlTimingToLog > 0l && sqlTiming.getCumulativeExecutionTime() > daoFuncTimingToLog) {
			generalUtilLogger.logWrite(LevelType.DEBUG,
					"Function call:"+statement+".", "-1",
					ActivitylogType.PerformanceSQL, inf_);
		}
 
		return obj;
	}

//	public List<SqlTiming> getTimings(final int sort) {
//		List<SqlTiming> timings = new ArrayList<SqlTiming>(sqlTimings.values());
//		Collections.sort(timings, new Comparator<SqlTiming>() {
//
//			public int compare(SqlTiming o1, SqlTiming o2) {
//				switch (sort) {
//				case 0:// AVG_EXECUTION_TIME:
//					return Math.round(o1.getAvgExecutionTime() - o2.getAvgExecutionTime());
//				case 1:// CUMULATIVE_EXECUTION_TIME:
//					long diff = o1.getCumulativeExecutionTime() - o2.getCumulativeExecutionTime();
//					if (diff > 0l) {
//						return 1;
//					} else if (diff == 0) {
//						return 0;
//					} else {
//						return -1;
//					}
//				case 2:// NUMBER_OF_EXECUTIONS:
//					return o1.getExecutionCount() - o2.getExecutionCount();
//				}
//				return 0;
//			}
//
//		});
//
//		return timings;
//	}  
}