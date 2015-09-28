package com.sunmap.shpdata.tools.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.sunmap.shpdata.tools.conf.ShpConf;

/**
 * 数据库访问 created by
 * 
 * @author daig
 */
public class ToolDAO {
	/**
	 * ORACLE连接串
	 */
	private static final String JDBC_ORACLE = "jdbc:oracle:thin:@192.168.1.72:1521:orcl";
	/**
	 * 配置文件
	 */
	private ShpConf conf;

	/**
	 * 创建以个DAO
	 * 
	 * @param conf
	 */
	public ToolDAO(ShpConf conf) {
		this.conf = conf;
	}

	/**
	 * 找到当前的最大gid
	 * 
	 * @param tableName
	 *            表名
	 * @return 最大gid
	 * @throws Exception
	 *             sql异常
	 */
	public int findMaxGid(String tableName) throws Exception {
		Class.forName("oracle.jdbc.OracleDriver");
		Connection con = DriverManager.getConnection(ToolDAO.JDBC_ORACLE,
				this.conf.getUsername(), this.conf.getPassword());
		Statement sta = con.createStatement();
		String sql = "select max(gid) from " + tableName;
		System.out.println("查询最大gid：" + sql);
		ResultSet rs = sta.executeQuery(sql);
		int maxGid = 0;
		while (rs.next()) {
			maxGid = rs.getInt(1);
		}
		rs.close();
		sta.close();
		con.close();
		return maxGid;
	}

	/**
	 * 合并数据库表
	 * 
	 * @param tableNameHasMapID
	 * 表名 @
	 */
	public void margeTableDate(String tableNameHasMapID) throws Exception {
		// insert into tableName select * from tableName_MapID
		String eleTypeTableName = tableNameHasMapID.subSequence(0,
				tableNameHasMapID.lastIndexOf("_")).toString();
		Class.forName("oracle.jdbc.OracleDriver");
		Connection con = DriverManager.getConnection(ToolDAO.JDBC_ORACLE,
				this.conf.getUsername(), this.conf.getPassword());
		Statement sta = con.createStatement();
		// 合并数据
		String sql = "insert into " + eleTypeTableName + " select * from "
				+ tableNameHasMapID;
		System.out.println(sql);
		try {
			sta.execute(sql);
		} catch (Exception e) {
			throw e;
		}
		// 删除带有任务号的表：drop table CROSS_SCENE_6263
		try {
			sta.executeUpdate("drop table " + tableNameHasMapID);
		} catch (Exception e) {
			System.err.println("drop table 出错,继续执行");
		}
		sta.close();
		con.close();
	}

}
