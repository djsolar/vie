package bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import util.JdbcUtil;
/**
 * 链接对象bo
 * @author wenxc
 *
 */
public class LocalDataJdbcModel {

	/**
	 * 链接
	 */
	private Connection connection;

	/**
	 * 处理器
	 */
	private Statement statement;
	
	
	/**
	 * 构造方法
	 */
	public LocalDataJdbcModel() {

	}
	
	/**
	 * 有参构造方法
	 * @param createConFlag boolean
	 * @throws Exception 1
	 */
	public LocalDataJdbcModel(boolean createConFlag) throws Exception {
		// TODO Auto-generated constructor stub
		if (createConFlag) {

			this.connection = JdbcUtil.getLocalConnection();
			
			this.connection.setAutoCommit(false);

			this.statement = this.connection.createStatement();

		}
	}

	/**
	 * 获得链接
	 * @return Connection
	 */
	public Connection getConnection() {
		return this.connection;
	}

	/**
	 * 添加链接
	 * @param connection Connection
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * 获得state
	 * @return Statement
	 */
	public Statement getStatement() {
		return this.statement;
	}

	/**
	 * 设置state
	 * @param statement Statement
	 */
	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	

	

	/**
	 * 关闭链接
	 * @return boolean
	 * @throws SQLException 1
	 */
	public boolean closeLocalData() throws SQLException {

		if (this.statement != null) {

			this.statement.close();

		}

		if (this.connection != null) {

			this.connection.close();

		}
		return true;
	}

	/**
	 * 执行sql
	 * @param sql String
	 * @param commitFlag boolean
	 * @return boolean
	 * @throws SQLException 1
	 */
	public boolean executeBatchSql(String sql, boolean commitFlag)
			throws SQLException {

		if (this.statement != null) {

			this.statement.addBatch(sql);

			if (commitFlag) {

				this.statement.executeBatch();

				this.connection.commit();

			}

			return true;

		} else {

			return false;

		}

	}
	
	/**
	 * 只执行批处理
	 * @throws SQLException 1
	 */
	public void executeOnlyBatch() throws SQLException{
		
		if(this.statement != null){
			
			this.statement.executeBatch();
			
			this.connection.commit();
		}
		
	}

}
