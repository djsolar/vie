package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * @author wenxc
 * 
 */
public class JdbcUtil {

	/**
	 * 
	 */
	private JdbcUtil() {
		// TODO Auto-generated constructor stub
	}

	/***
	 * 获得本地的链接
	 * 
	 * @return Connection
	 * @throws Exception
	 *             1
	 */
	public static Connection getLocalConnection() throws Exception {

		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");

		String url = ReadFile.readProFileURL();

		String usrname = ReadFile.readProFileUSR();

		String password = ReadFile.readProFilePASSWD();

		Connection connection = DriverManager.getConnection(url, usrname,
				password);

		return connection;

	}

	/**
	 * 
	 * @param sql
	 *            String
	 * @return boolean
	 * @throws Exception
	 *             1
	 */
	public static boolean executeSql(String sql) throws Exception {

		Connection connection = JdbcUtil.getLocalConnection();

		connection.setAutoCommit(false);

		Statement statement = connection.createStatement();

		boolean flag = statement.execute(sql);

		statement.close();

		connection.close();

		return flag;

	}

	/**
	 * 获得sqlite的链接
	 * 
	 * @return Connection
	 */
	public static Connection getSqliteConnection(String filepath) {

		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn;
			conn = DriverManager.getConnection("jdbc:sqlite:" + filepath);
			return conn;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		
//		Connection connection = getSqliteConnection("D:/temp/temp/tele.db");
		

		Connection connection = getSqliteConnection("tele.jpg.temp");
		
		try {
			Statement statement = connection.createStatement();
			
			statement.execute("create table a(b int)");
			
//			connection.commit();
			
			statement.close();
			
			connection.close();
			
//			String sql = "select key from tmap limit 1";
//			
//			ResultSet rs = statement.executeQuery(sql);
//			
//			System.out.println(rs.getString(1));
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
