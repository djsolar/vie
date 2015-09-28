package com.sunmap.shpdata.tools.factory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sunmap.shpdata.tools.conf.ShpConf;
import com.sunmap.shpdata.tools.util.JdbcUtil;

public class ConnectionFactory {
	
	private static Connection con = null;
	
	public static Connection getConnection(){
		if(con == null){
			try {
				ShpConf conf = new ShpConf();
				conf.initProperties();
				con = JdbcUtil.getConnection();
			} catch (Exception e) {
				System.out.println("���ݿ�����ʧ��");
				e.printStackTrace();
			}
		}
		return con;
	}
	
	public static void close(ResultSet rs, PreparedStatement ps, Connection con){
		if(rs != null){
			try {
				rs.close();
			} catch (SQLException e) {
				System.out.println("���������ʹ���޷��ر�");
				e.printStackTrace();
			}
		}
		if(ps != null){
			try {
				ps.close();
			} catch (SQLException e) {
				System.out.println("���������ʹ���޷��ر�");
				e.printStackTrace();
			}
		}
		if(con != null){
			try {
				con.close();
			} catch (SQLException e) {
				System.out.println("���������ʹ���޷��ر�");
				e.printStackTrace();
			}
		}
	}

}
