package com.skyline.form.dal;

public interface SchedTaskDao {

	void updateFilesSrcTable(String dbTransactionId);

	void updateResultSearch(String dbTransactionId);

	void updateWebixSearch(String dbTransactionId); 

	void handelTransactionFailure();

	void fixInfTableDisplayValues(String dbTransactionId, boolean onLastChangesOnly) throws Exception;

	void handelInfId(String dbTransactionId) throws Exception;

	void updateFgSeqSearchMatch(String formCode, String fromFormId);

	void correctInfTable(boolean completeRows, boolean completeData, boolean onLastChangesOnly,
			boolean correctAllPathObj) throws Exception;

	void inserIntoFgSysSched(String name, String startDate, String endDate, String status, String interval);

	void dbCleanup();
}
