package jasper.biz;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

/**
 * Ireport data helper class
 */
public class JasperReportDataHelper
{
    public JasperReportDataHelper() 
    {
    }
  
     /** 
     * @param ds
     * @param JRDataSourceClass - the name of JRDataSource class implementation (for now it only works with JRMapArrayDataSource)
     * @return JRDataSource with cursor moved back to the start
     */
    public JRDataSource getJRDataSourceMoveFirst(JRDataSource ds, String jrDataSourceClass) 
    {
        if(jrDataSourceClass != null && jrDataSourceClass.toLowerCase().trim().equals("jrmaparraydatasource")) 
        {
            ((JRMapArrayDataSource)ds).moveFirst();
        }
        return ds;
    }
}
