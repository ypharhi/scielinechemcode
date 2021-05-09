package com.skyline.form.service;

import javax.servlet.http.HttpServletRequest;

import com.skyline.general.bean.DataTableParamModel;

/*
 * class service for server side DataTable 
 */
public class DataTablesParamUtility 
{
	public static DataTableParamModel getParam(HttpServletRequest request)
	{
            try
            {
                if(request.getParameter("draw")!=null && request.getParameter("draw")!= "")
				{
		                    
		            DataTableParamModel param = new DataTableParamModel();
				    param.sEcho = request.getParameter("draw");
		            param.sSearchKeyword = request.getParameter("search[value]");
		            param.bRegexKeyword = Boolean.parseBoolean(request.getParameter("search[regex]"));
		            param.iDisplayStart = Integer.parseInt( request.getParameter("start") );
				    param.iDisplayLength = Integer.parseInt( request.getParameter("length") );
				    param.iColumns = Integer.parseInt( request.getParameter("colsCount") );
				    param.sSearch = new String[param.iColumns];
				    param.bSearchable = new boolean[param.iColumns];
				    param.bSortable = new boolean[param.iColumns];
				    param.bRegex = new boolean[param.iColumns];
				    for(int i=0; i<param.iColumns; i++)
		            {
				            param.sSearch[i] = request.getParameter("columns["+i+"][search][value]");
				            param.bSearchable[i] = Boolean.parseBoolean(request.getParameter("columns["+i+"][searchable]"));
				            param.bSortable[i] = Boolean.parseBoolean(request.getParameter("columns["+i+"][orderable]"));
				            param.bRegex[i] = Boolean.parseBoolean(request.getParameter("columns["+i+"][search][regex]"));
				    }
				     
				    param.iSortingCols = Integer.parseInt( request.getParameter("sortingCols") );
				    param.sSortDir = new String[param.iSortingCols];
				    param.iSortCol = new int[param.iSortingCols];
				    param.sSortType = new String[param.iSortingCols];
				    param.sDateFormat = new String[param.iSortingCols];
		                    
				    for(int i=0; i<param.iSortingCols; i++)
		            {
				            param.sSortDir[i] = request.getParameter("order["+i+"][dir]");
				            param.iSortCol[i] = Integer.parseInt(request.getParameter("order["+i+"][column]"));
		                    param.sSortType[i] = request.getParameter("order["+i+"][colType]");
		                    //param.sDateFormat[i] = request.getParameter("sSortColDateFormat_"+i);
				    }
				    return param;
				   
				}
                else
                	return null;
            }
            catch(Exception ex)
            {
                return null;     
            }
	}
}
