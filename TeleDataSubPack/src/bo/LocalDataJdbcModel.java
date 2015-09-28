package bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import util.JdbcUtil;
/**
 * ���Ӷ���bo
 * @author wenxc
 *
 */
public class LocalDataJdbcModel {

	/**
	 * ����
	 */
	private Connection connection;

	/**
	 * ������
	 */
	private Statement statement;
	
	
	/**
	 * ���췽��
	 */
	public LocalDataJdbcModel() {

	}
	
	/**
	 * �вι��췽��
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
	 * �������
	 * @return Connection
	 */
	public Connection getConnection() {
		return this.connection;
	}

	/**
	 * �������
	 * @param connection Connection
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * ���state
	 * @return Statement
	 */
	public Statement getStatement() {
		return this.statement;
	}

	/**
	 * ����state
	 * @param statement Statement
	 */
	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	

	

	/**
	 * �ر�����
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
	 * ִ��sql
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
	 * ִֻ��������
	 * @throws SQLException 1
	 */
	public void executeOnlyBatch() throws SQLException{
		
		if(this.statement != null){
			
			this.statement.executeBatch();
			
			this.connection.commit();
		}
		
	}

}
