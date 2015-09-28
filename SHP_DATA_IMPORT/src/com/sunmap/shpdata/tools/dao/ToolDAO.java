package com.sunmap.shpdata.tools.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.sunmap.shpdata.tools.conf.ShpConf;

/**
 * ���ݿ���� created by
 * 
 * @author daig
 */
public class ToolDAO {
	/**
	 * ORACLE���Ӵ�
	 */
	private static final String JDBC_ORACLE = "jdbc:oracle:thin:@192.168.1.72:1521:orcl";
	/**
	 * �����ļ�
	 */
	private ShpConf conf;

	/**
	 * �����Ը�DAO
	 * 
	 * @param conf
	 */
	public ToolDAO(ShpConf conf) {
		this.conf = conf;
	}

	/**
	 * �ҵ���ǰ�����gid
	 * 
	 * @param tableName
	 *            ����
	 * @return ���gid
	 * @throws Exception
	 *             sql�쳣
	 */
	public int findMaxGid(String tableName) throws Exception {
		Class.forName("oracle.jdbc.OracleDriver");
		Connection con = DriverManager.getConnection(ToolDAO.JDBC_ORACLE,
				this.conf.getUsername(), this.conf.getPassword());
		Statement sta = con.createStatement();
		String sql = "select max(gid) from " + tableName;
		System.out.println("��ѯ���gid��" + sql);
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
	 * �ϲ����ݿ��
	 * 
	 * @param tableNameHasMapID
	 * ���� @
	 */
	public void margeTableDate(String tableNameHasMapID) throws Exception {
		// insert into tableName select * from tableName_MapID
		String eleTypeTableName = tableNameHasMapID.subSequence(0,
				tableNameHasMapID.lastIndexOf("_")).toString();
		Class.forName("oracle.jdbc.OracleDriver");
		Connection con = DriverManager.getConnection(ToolDAO.JDBC_ORACLE,
				this.conf.getUsername(), this.conf.getPassword());
		Statement sta = con.createStatement();
		// �ϲ�����
		String sql = "insert into " + eleTypeTableName + " select * from "
				+ tableNameHasMapID;
		System.out.println(sql);
		try {
			sta.execute(sql);
		} catch (Exception e) {
			throw e;
		}
		// ɾ����������ŵı�drop table CROSS_SCENE_6263
		try {
			sta.executeUpdate("drop table " + tableNameHasMapID);
		} catch (Exception e) {
			System.err.println("drop table ����,����ִ��");
		}
		sta.close();
		con.close();
	}

}
