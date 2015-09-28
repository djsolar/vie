package com.sunmap.teleview.util;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lijingru
 *
 */
public class AdminCodes {

	private final static String DB_ADDRESS = "jdbc:postgresql://10.1.7.103:5432/newroad";//链接数据库地址
	private final static String USERNAME = "postgres";//用户名
	private final static String PASSWORD = "123456";//密码
	public static List<AdminCode> admins = new ArrayList<AdminCode>();//省名的code
	
	/**
	 * 制作省的AdminCode
	 * @return
	 * @throws SQLException
	 */
	public static void getProvince()throws SQLException{
		java.sql.Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<AdminCode> list = new ArrayList<AdminCode>();
		try{
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(DB_ADDRESS, USERNAME, PASSWORD);
			String strSql = "SELECT admin_code, province, city ,center_lon, center_lat "
					+ " FROM geo.search_address_code where admin_code%10000=0 order by admin_code";
			stmt = con.createStatement();
			rs = stmt.executeQuery(strSql);
			while (rs.next()) {
				AdminCode one = new AdminCode();
				one.setAdmin_code(rs.getInt(1));
				one.setProvince(rs.getString(2));
				one.setLon(rs.getInt(4));
				one.setLat(rs.getInt(5));
				list.add(one);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs != null){
				rs.close();
			}
			if(stmt != null){
				stmt.close();
			}
			if(con != null){
				con.close();
			}
		}
		admins = new ArrayList<AdminCode>(list);
	}
	
	/**
	 * 根据code返回城市名
	 * @param admin_code
	 * @return
	 * @throws SQLException
	 */
	public static String getCityName(int admin_code) throws SQLException{
		java.sql.Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String str = null;
		try{
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(DB_ADDRESS,USERNAME,PASSWORD);
			String strSql = "SELECT admin_code, province, city  ,center_lon, center_lat  FROM geo.search_address_code where admin_code = "
					+ admin_code ;
			stmt = con.createStatement();
			rs = stmt.executeQuery(strSql);
			while (rs.next()) {
				str = rs.getString(3);
				if (str == null||str.equals("")) {
					str = rs.getString(2);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs != null){
				rs.close();
			}
			if(stmt != null){
				stmt.close();
			}
			if(con != null){
				con.close();
			}
		}
		return str;
	}
	/**
	 * 通过省code查询城市code
	 * @param code
	 * @return
	 * @throws SQLException
	 */
	public static List<AdminCode> getCityCode(int code) throws SQLException{
		java.sql.Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<AdminCode> list = new ArrayList<AdminCode>();
		try{
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(DB_ADDRESS,USERNAME,PASSWORD);
			String strSql = "SELECT admin_code, province, city  ,center_lon, center_lat  FROM geo.search_address_code where admin_code>="
					+ code + " and admin_code < "+ (code + 10000)
					+ " and city <> '' and admin_code%100=0 order by admin_code";
			stmt = con.createStatement();
			rs = stmt.executeQuery(strSql);
			while(rs.next()){
				AdminCode one = new AdminCode();
				one.setAdmin_code(rs.getInt(1));
				one.setProvince(rs.getString(2));
				one.setCity(rs.getString(3));
				one.setLon(rs.getInt(4));
				one.setLat(rs.getInt(5));
				list.add(one);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs != null){
				rs.close();
			}
			if(stmt != null){
				stmt.close();
			}
			if(con != null){
				con.close();
			}
		}
		return list;
	}
	
	/**
	 * 通过坐标查询城市
	 * @param code
	 * @return
	 * @throws SQLException
	 */
	public static AdminCode getCityByCode(int longitude, int latitude) throws SQLException{
		java.sql.Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		AdminCode adminCode = new AdminCode();
		try{
			int admincode = getAreaCode(longitude, latitude);
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(DB_ADDRESS,USERNAME,PASSWORD);
			String strhql = "SELECT admin_code, province, city  ,center_lon, center_lat  FROM geo.search_address_code where admin_code ="
					+ admincode ;
			stmt = con.createStatement();
			rs = stmt.executeQuery(strhql);
			while(rs.next()){
				adminCode.setAdmin_code(rs.getInt(1));
				adminCode.setProvince(rs.getString(2));
				adminCode.setCity(rs.getString(3));
				adminCode.setLon(rs.getInt(4));
				adminCode.setLat(rs.getInt(5));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs != null){
				rs.close();
			}
			if(stmt != null){
				stmt.close();
			}
			if(con != null){
				con.close();
			}
		}
		return adminCode;
	}
	
	
	public static String[] getAdminProvince(){
		String[] strs = new String[admins.size()+1];
		for (int i = 0; i < admins.size(); i++) {
			strs[i+1] = admins.get(i).getProvince();
		}
		strs[0] = "";
		return strs;
	}
	
	
	/**
	 * 根据坐标取区域code
	 * @param longitude 坐标点X  单位：度
	 * @param latitude	坐标点Y
	 * @return 返回城市区域code，没找到返回0
	 * @throws SQLException
	 */
	public static int getAreaCode(double longitude, double latitude) throws SQLException{
		java.sql.Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		int admCode = 0;
		try{
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(DB_ADDRESS,USERNAME,PASSWORD);
			String strSql = "select admin_code from geo.location where  ST_DWithin(st_point("+ longitude +","+ latitude +"),strgeom,0.0000000000000001)";
			stmt = con.createStatement();
			rs = stmt.executeQuery(strSql);
			while(rs.next()){
				admCode = rs.getInt(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs != null){
				rs.close();
			}
			if(stmt != null){
				stmt.close();
			}
			if(con != null){
				con.close();
			}
		}
		return admCode;
	}
	
	/**
	 * 根据坐标取区域code
	 * @param longitude 坐标点X  单位：1/2560秒
	 * @param latitude	坐标点Y
	 * @return 返回城市区域code，没找到返回0
	 * @throws SQLException
	 */
	public static int getAreaCode(int longitude, int latitude) throws SQLException{
		if (longitude < 672768000 || longitude > 1253376000|| latitude < 27648000 || latitude > 497664000){
				return 0 ;
		}
		double x = (double)longitude/2560/3600;
		double y = (double)latitude/2560/3600;
		return getAreaCode(x, y);
	}

	
}
