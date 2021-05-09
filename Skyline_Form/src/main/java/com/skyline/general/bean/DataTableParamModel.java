package com.skyline.general.bean;

public class DataTableParamModel {

	 /**
	 * Request sequence number sent by DataTable, same value must be returned in response
	 */
	 public String sEcho;

	 /**
	 * Text used for filtering
	 */
	 public String sSearchKeyword;
	 
	 /**
	 * Is regular expression search used for filtering
	 */
	 public boolean bRegexKeyword;

	 /**
	 * Number of records that should be shown in table
	 */
	 public int iDisplayLength;

	 /**
	 * First record that should be shown(used for paging)
	 */
	 public int iDisplayStart;

	 /**
	 * Number of columns in table
	 */
	 public int iColumns;
	 
	 /**
	 * Keywords for the individual column filtering is multi-keyword filtering is used
	 */
	 public String[] sSearch;

	 /**
	 * Array that defines what columns are searchable
	 */
	 public boolean[] bSearchable;
	 
	 /**
	 * Array that defines what columns are sortable
	 */
	 public boolean[] bSortable;
	 
	 public boolean[] bRegex;
	 
	 /**
	 * Number of columns that are used in sorting
	 */
	 public int iSortingCols;
	 
	 /**
	 * Directions for the column sorting "asc" or "desc"
	 */
	 public String[] sSortDir;
	 
	 /**
	 * Order of sorting columns
	 */
	 public int[] iSortCol;
        
       /**
       * Type of sorting columns
       */
        public String[] sSortType;
        
	 /**
	 * Comma separated list of column names
	 */
	 public String sColumns;
        
       /**
        * Date format of sorting column.
        */
        public String[] sDateFormat;
        
        /**
         *  Default date format of sorting column.
         */
         public final String defaultDateFormat = "dd/MM/yyyy";
   
   /// <summary>
   /// Index of the column that is used for sorting
   /// </summary>
   public int iSortColumnIndex;
}