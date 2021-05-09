package jasper.dal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DbBasicProvider {
 
	public ResultSet getResultSet(Connection conn, String sql) {
		ResultSet rs = null;
//		openConnection();
		try { 
			System.out.println("DbBasicProvider.getTableColumne - openConnection() - connection=" + conn);
			Statement stmt = conn.createStatement();
			System.out.println(sql);
			rs = stmt.executeQuery(sql);
		} catch (Exception e) {
			System.out.println("Error getResultSet: " + e.toString());
		}

		return rs;
	}

	public String[] getColumnSizeByTableName(Connection conn, String tableName, String reportFilter) {
		String[] toReturn = null;
//		openConnection();
		try { 
			System.out
					.println("DbBasicProvider.getColumnSizeByTableName - openConnection() - connection=" + conn);
			Statement stmt = conn.createStatement();
			String sizeSql = "select distinct t.columns_percent_size from " + tableName + " t where rownum = 1 "
					+ reportFilter;
			System.out.println(sizeSql);
			ResultSet rs = stmt.executeQuery(sizeSql);

			while (rs.next()) {
				toReturn = rs.getString("columns_percent_size").split(",");
				break;
			}
			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception e) {
			//System.out.println(e.toString());
		} finally {
//			try {
////				System.out.println(
////						"DbBasicProvider.getColumnSizeByTableName - connection.close() connection=" + connection);
////				connection.close();
//			} catch (Exception e) {
//				System.out.println("DbBasicProvider.getColumnSizeByTableName - close connection failed connection="
//						+ connection + ", e=" + e.toString());
//			}
		}
		return toReturn;
	}

	public List<String> getTableColumne(Connection conn, String tableName) {
		List<String> colList = null;
//		openConnection();
		String sql = " select t.COLUMN_NAME \n" + " from user_tab_columns t\n" + " where lower(t.TABLE_NAME) = lower('"
				+ tableName + "') " + " and lower(t.COLUMN_NAME) not like 'hidden_%' \n"
				+ " and lower(t.COLUMN_NAME) not like 'columns_percent_size' "
				+ " and lower(t.COLUMN_NAME) not like 'change_id' and lower(t.COLUMN_NAME) not like 'id' \n"
				+ " and lower(t.COLUMN_NAME) not like 'max_change_id' and lower(t.COLUMN_NAME) not like 'id' \n"
				+ " order by t.COLUMN_ID ";

		try { 
			
			System.out.println("DbBasicProvider.getTableColumne - openConnection() - connection=" + conn);
			Statement stmt = conn.createStatement();
			System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			colList = new ArrayList<String>();
			while (rs.next()) {
				colList.add(rs.getString("COLUMN_NAME"));
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
//			try {
////				System.out.println("DbBasicProvider.getTableColumne - connection.close() connection=" + connection);
////				connection.close();
//			} catch (Exception e) {
//				System.out.println("DbBasicProvider.getTableColumne - close connection failed connection=" + connection
//						+ ", e=" + e.toString());
//			}
		}
		return colList;
	}

	public List<String> getAuditTrailTableColumne(Connection conn, String tableName) {
		List<String> colList = null;
//		openConnection();
		String sql = "select t.COLUMN_NAME, count(t.COLUMN_ID) over() counter\n" + "  from user_tab_columns t\n"
				+ "  where lower(t.TABLE_NAME) = lower('" + tableName + "')\n"
				+ "  and ( lower(t.COLUMN_NAME) not like 'hidden_%' "
				+ "  and lower(t.COLUMN_NAME) not like 'change_%' " + "  and lower(t.COLUMN_NAME) not like 'comments' "
				+ "  and lower(t.COLUMN_NAME) not like 'id' "
				+ "  and lower(t.COLUMN_NAME) not like 'columns_percent_size' "
				+ "  and lower(t.COLUMN_NAME) not like 'time stamp' )  ";

		try { 
			System.out
					.println("DbBasicProvider.getAuditTrailTableColumne - openConnection() - connection=" + conn);
			Statement stmt = conn.createStatement();
			System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			colList = new ArrayList<String>();
			while (rs.next()) {
				colList.add(rs.getString("COLUMN_NAME"));
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
//			try {
////				System.out.println(
////						"DbBasicProvider.getAuditTrailTableColumne - connection.close() connection=" + connection);
////				connection.close();
//			} catch (Exception e) {
//				System.out.println("DbBasicProvider.getAuditTrailTableColumne - close connection failed connection="
//						+ connection + ", e=" + e.toString());
//			}
		}
		return colList;
	}
}
