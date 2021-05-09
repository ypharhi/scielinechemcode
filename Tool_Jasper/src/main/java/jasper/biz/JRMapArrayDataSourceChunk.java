 
package jasper.biz;

import jasper.dal.DbBasicProvider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
 
public class JRMapArrayDataSourceChunk implements JRRewindableDataSource
{ 
	/**
	 *
	 */
	private Object[] records = null;
	private int index = -1;
//        private String dbUrl = "";
//        private String dbUser = "";
//        private String dbPassword = "";
	private Connection conn = null;
        private String sql = "";
        int chunkSize = 0;
        int blockCounter = 0;

	/**
	 *
	 */
	public JRMapArrayDataSourceChunk(Connection conn, String sql, int chunkSize)
	{ 
//                this.dbUrl = dbUrl;
//                this.dbUser = dbUser;
//                this.dbPassword = dbPassword;
				this.conn= conn;
                this.sql =  sql;
                this.chunkSize = chunkSize;
	}
	

	/**
	 *
	 */
	public boolean next() 
        {
		index++;
                
                if(index%chunkSize == 0) 
                {
                    //update record with next chunk
                    initData();
                    index = 0;
                    blockCounter ++;
                }

		if (records != null)
		{
			return (index < records.length && records[index] != null);
		}

		return false;
	}
	 
	public Object getFieldValue(JRField field)
	{
		Object value = null;
		
		Map currentRecord = (Map)records[index];

		if (currentRecord != null)
		{
			value = currentRecord.get(field.getName());
		}

		return value;
	}

	
	/**
	 * in this imp it move to the start of the chunk
	 */
	public void moveFirst()
	{
		this.index = -1;
	}

	/**
	 * Returns the underlying map array used by this data source.
	 * 
	 * @return the underlying map array
	 */
	public Object[] getData()
	{
		return records;
	}

	/**
	 * Returns the total number of records/maps that this data source
	 * contains.
	 * 
	 * @return the total number of records of this data source
	 */
	public int getRecordCount()
	{
		return records == null ? 0 : records.length;
	}
	
	/**
	 * Clones this data source by creating a new instance that reuses the same
	 * underlying map array.
	 * 
	 * @return a clone of this data source
	 */
	public JRMapArrayDataSource cloneDataSource()
	{
		return new JRMapArrayDataSource(records);
	}

        private void initData( ) 
        { 
            DbBasicProvider dbBasicProvider = new DbBasicProvider();
            ResultSet rs = dbBasicProvider.getResultSet(conn, "select t2.* from ( select t1.* , rownum - 1 as myrownum from (" + sql + ") t1 ) t2 where myrownum between " + ( blockCounter * chunkSize ) + " and " +  ((blockCounter + 1) * chunkSize - 1));
            int rsIndex = 0;
            ResultSetMetaData rsmd = null;
            HashMap hm = null;
            records = null; // init the record to null
            try 
            { 
                rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                while(rs.next()) 
                {
                    if(rsIndex == 0) 
                    {
                        records = new Object[chunkSize];
                    }
                    
                    hm = new HashMap();
                    for(int i= 1; i <= columnCount; i++) 
                    {
                        hm.put(rsmd.getColumnName(i),rs.getObject(i));
                    }
                    records[rsIndex++] = hm;
                } 
            } 
            catch(Exception e)
            {
                System.out.println(e.toString());
            }
            finally 
            {
                try
                {
                    rsmd= null;
                    hm = null;
                    rs.close();
                }
                catch(Exception e) 
                {
                    System.out.println(e.toString());
                }
//                dbBasicProvider.colseConnection();
            }
        }
}
