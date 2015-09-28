package com.sunmap.shpdata.tools.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DataConnectionTool {
	private Connection connection;
	private Statement statement;
	private PreparedStatement preparedStatement;
	private int count;
	private int maxcount;
	public int getMaxcount() {
		return maxcount;
	}
	public void setMaxcount(int maxcount) {
		this.maxcount = maxcount;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getCount() {
		return count;
	}
	public DataConnectionTool() throws Exception {
		// TODO Auto-generated constructor stub
		connection = JdbcUtil.getConnection();
	}
	
	public PreparedStatement getPreparedStatement() {
		return preparedStatement;
	}
	public Connection getConnection() {
		return connection;
	}
	public Statement getStatement() {
		return statement;
	}
	
	public Connection openNewConnection() throws Exception{
		connection = JdbcUtil.getConnection();
		return  connection;
	}
	
	public Statement openNewStatement() throws Exception{
		statement = connection.createStatement();
		return statement;
	}
	
	public Statement openNewPrePareStatement(String sql) throws Exception{
		preparedStatement = connection.prepareStatement(sql);
		return preparedStatement;
	}
	
	public void closeStatement() throws Exception{
		statement.close();
	}
	
	public void closePrePareStatement() throws Exception{
		preparedStatement.close();
	}
	
	public void closeConnection() throws Exception{
		connection.close();
	}
	
	public void closeAllTool() throws Exception{
		if (statement != null) {
			statement.close();
		}
		if (preparedStatement != null) {
			preparedStatement.close();
		}
		if (connection != null) {
			connection.close();
		}
	}
	
	public boolean CountEqMax(){
		return count == maxcount;
	}
	
	public void setConnectionCommit(boolean flag) throws Exception{
		connection.setAutoCommit(flag);
	}
	
	public Statement returncurStatement() throws Exception{
		if(statement == null){
			statement = connection.createStatement();
		}
		return statement;
	}
	
	public PreparedStatement returncurrPreparedStatement(String sql) throws Exception{
		if(preparedStatement == null){
			preparedStatement = connection.prepareStatement(sql);
		}
		return preparedStatement;
	}
	
	

}
