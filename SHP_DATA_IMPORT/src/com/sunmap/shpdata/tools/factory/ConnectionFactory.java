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
				System.out.println("数据库连接失败");
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
				System.out.println("结果集正在使用无法关闭");
				e.printStackTrace();
			}
		}
		if(ps != null){
			try {
				ps.close();
			} catch (SQLException e) {
				System.out.println("结果集正在使用无法关闭");
				e.printStackTrace();
			}
		}
		if(con != null){
			try {
				con.close();
			} catch (SQLException e) {
				System.out.println("结果集正在使用无法关闭");
				e.printStackTrace();
			}
		}
	}

}
