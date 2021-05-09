package jasper.biz;
 
import jasper.dal.DbBasicProvider;

import java.lang.reflect.Field;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;


public class JasperDataSourceSupplier 
{
//    String dbUrl = "";
//    String dbUser = "";
//    String dbPassword = ""; 
    Connection conn = null; 
    boolean overrideDataTypeToString = true;
 
//    public JasperDataSourceSupplier(String dbUrl, String dbUser, String dbPassword)
//    {
//            this.dbUrl = dbUrl;
//            this.dbUser = dbUser;
//            this.dbPassword = dbPassword;
//    }
    
    /**
     * 
     * @param dbUrl
     * @param dbUser
     * @param dbPassword
     * @param overrideDataTypeToString true - for template factory report - all the data cast to string (as the template fields)
     */
//    public JasperDataSourceSupplier(String dbUrl, String dbUser, String dbPassword, boolean overrideDataTypeToString)
//    {
//            this.dbUrl = dbUrl;
//            this.dbUser = dbUser;
//            this.dbPassword = dbPassword;
//            this.overrideDataTypeToString = overrideDataTypeToString;
//    }
    
    public JasperDataSourceSupplier(Connection conn)
    {
            this.conn = conn;
    }
    
    /**
     * 
     * @param dbUrl
     * @param dbUser
     * @param dbPassword
     * @param overrideDataTypeToString true - for template factory report - all the data cast to string (as the template fields)
     */
    public JasperDataSourceSupplier(Connection conn, boolean overrideDataTypeToString)
    {
    	this.conn = conn;
        this.overrideDataTypeToString = overrideDataTypeToString;
    }
    
    public JasperDataSourceSupplier() 
    {
        
    }
     
    
     /**
     * 
     * @param sql
     * @param JRDataSourceClass - the name of JRDataSource class implementation (for now it only works with JRMapArrayDataSource)
     * @return JRDataSource with JRDataSourceClass implementation
     * @throws Exception
     */
    public JRDataSource createReportDataSource(String sql, String jrDataSourceClass) throws Exception 
    {
        JRDataSource dataSource = null;
        if(jrDataSourceClass != null && jrDataSourceClass.toLowerCase().trim().equals("jrmaparraydatasource")) 
        {
            Map[] reportRows = initializeMapArray(sql);
            dataSource = new JRMapArrayDataSource(reportRows);
        } 
        return dataSource;
    }
    
	public JRDataSource createReportDataSource(List list, String jrDataSourceClass) throws Exception 
    {
        JRDataSource dataSource = null;
        if(jrDataSourceClass != null && jrDataSourceClass.toLowerCase().trim().equals("jrmaparraydatasource")) 
        {
            Map[] reportRows = initializeMapArray(list);
            dataSource = new JRMapArrayDataSource(reportRows);
        } 
        return dataSource;
    }

	public JRDataSource createReportDataSource(JSONObject jsonObject, String jrDataSourceClass, int startWithColumn) throws Exception 
    {
        JRDataSource dataSource = null;
        if(jrDataSourceClass != null && jrDataSourceClass.toLowerCase().trim().equals("jrmaparraydatasource")) 
        {
            Map[] reportRows = initializeMapArray(jsonObject, startWithColumn);
            dataSource = new JRMapArrayDataSource(reportRows);
        } 
        return dataSource;
    }
	
	private Map[] initializeMapArray(String sql) throws Exception 
    {
        HashMap[] hmArray = null;
        DbBasicProvider dbBasicProvider = new DbBasicProvider();
        ResultSet rs = dbBasicProvider.getResultSet(conn, sql);
        List<HashMap> hmList = null;
        ResultSetMetaData rsmd = null;
        Iterator<HashMap> hmIterator = null;
        HashMap hm = null;
        try 
        {
            hmList = new ArrayList <HashMap>();
            rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while(rs.next()) 
            {
                hm = new HashMap();
                for(int i= 1; i <= columnCount; i++) 
                {
                    if(overrideDataTypeToString) 
                    {
                        hm.put(rsmd.getColumnName(i),String.valueOf(rs.getObject(i) == null ? "" : rs.getObject(i)));
                    }
                    else 
                    {
                        hm.put(rsmd.getColumnName(i),rs.getObject(i));
                    }
                }
                hmList.add(hm);
            }
            
            hmArray = new HashMap[hmList.size()];
            hmIterator = hmList.iterator();
            for(int i = 0; hmIterator.hasNext(); i++)
            {
               hmArray[i] =  hmIterator.next();
            }
        } 
        finally 
        {
            rs.close();
            hmList = null;
            rsmd = null;
            hmIterator = null;
            hm = null;
//            dbBasicProvider.colseConnection();
        }
        return hmArray;
    }
    
    private Map[] initializeMapArray(List list) throws Exception 
    {
        HashMap[] hmArray = null; 
        List<HashMap> hmList = null; 
        Iterator<HashMap> hmIterator = null;
        HashMap hm = null;
        try 
        {
            hmList = new ArrayList <HashMap>();
            Field[] clasFields = (list.get(0).getClass()).getFields();
          
            for(Object x : list) 
            {
                hm = new HashMap();
                for (int i =0; i < clasFields.length; i++) 
                {
                    Field field = x.getClass().getField(clasFields[i].getName());
                    Object fieldValue = field.get(x);
                    hm.put(clasFields[i].getName(),fieldValue);
                    
                    if(overrideDataTypeToString) 
                    {
                        hm.put(clasFields[i].getName(),String.valueOf(fieldValue == null ? "" : fieldValue));
                    }
                    else 
                    {
                        hm.put(clasFields[i].getName(),fieldValue);
                    }
                }
                hmList.add(hm);
            }
           
            hmArray = new HashMap[hmList.size()];
            hmIterator = hmList.iterator();
            for(int i = 0; hmIterator.hasNext(); i++)
            {
               hmArray[i] =  hmIterator.next();
            }
        } 
        finally 
        {
            hmList = null;
            hmIterator = null;
            hm = null;
        }
        return hmArray;
    }
    
    private Map[] initializeMapArray(JSONObject jsonObject, int startWithColumn) {
    	
    	//TODO KONSTA
    	HashMap[] hmArray = null; 
        List<HashMap> hmList = null; 
        Iterator<HashMap> hmIterator = null;
        HashMap hm = null;
        Map<String,String> colMap = new HashMap<String,String>();
        String [] colHeader = null;
//        ArrayList<String> colHeader = new ArrayList<String>(); 
        
//        ArrayList<String> col = new ArrayList<String>();
        try 
        {
            hmList = new ArrayList <HashMap>();
            
            //col = (ArrayList<String>)jsonObject.get("columns");
            
            JSONArray jsonArr = (JSONArray)jsonObject.get("columns");
            String s;
            JSONObject mp = null;
            
            colHeader = new String[jsonArr.length()];
            for (int i=0; i < jsonArr.length(); i++)
            {
            	//colHeader  
//            	if (i >= startWithColumn)
//            	{
	            	mp = jsonArr.getJSONObject(i);
	            	
	            	colMap.put(mp.getString("uniqueTitle"), mp.getString("title"));
	            	colHeader[i] = mp.getString("uniqueTitle");
//            	}
            }
            
            jsonArr = (JSONArray)jsonObject.get("data");
            JSONArray jsonArrData = new JSONArray();
            for (int i=0; i < jsonArr.length(); i++)
            {
            	//colHeader  
            	jsonArrData = jsonArr.getJSONArray(i);
            	hm = new HashMap();
            	String header = null, data = null;
            	JSONObject dataSmartlink = null;
            	int indexSmart = 0;
            	for (int j=0; j < jsonArrData.length(); j++)
                {
//            		if (j >= startWithColumn)
//                	{
	            		//if (!colHeader[j].contains("_SMART"))
            			if (!colMap.get(colHeader[j]).contains("_SMART"))
	            		{
	            			hm.put(colHeader[j],jsonArrData.get(j));
	            		} else
	            		{
	            			try {
	            				indexSmart = colMap.get(colHeader[j]).indexOf("_SMART");
	            				if (indexSmart != 0) {
	            					header = colMap.get(colHeader[j]).substring(0, indexSmart);
	            				} else {
	            					header = "na"; // in case if column header equals "_SMART..." (nothing before _smart)
	            				}
	            				String val_ = (String)jsonArrData.get(j);
	            				if(val_ != null && val_.trim().startsWith("{") && val_.contains("displayName")) {
	            					dataSmartlink = new JSONObject(val_);
		            				data = dataSmartlink.getString(("displayName"));
		            				hm.put(header,data);
	            				} else {
//	            					System.out.println("not a json");
	            				}
	            			} catch (Exception e) {
	            				System.out.println("Exception when convert Object to JSONObject " + e);
	            			}
	            		} 
//                	}
                }
            	hmList.add(hm);
            }
            
            hmArray = new HashMap[hmList.size()];
            hmIterator = hmList.iterator();
            for(int i = 0; hmIterator.hasNext(); i++)
            {
               hmArray[i] =  hmIterator.next();
            }
        } 
        finally 
        {
            hmList = null;
            hmIterator = null;
            hm = null;
        }
        return hmArray;
	}
    
}
