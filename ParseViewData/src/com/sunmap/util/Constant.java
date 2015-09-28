package com.sunmap.util;

public class Constant {
	
	public static final String inPath = "view";
//	public static final String inPath = "D:\\viewtest\\14q2test\\view";
	public static final String outPath = "partdata";
//	public static final String outPath = "E:\\partdata";
	public static final String pro_url = "jdbc:postgresql://10.1.7.104:5432/province";
	public static final String username = "postgres";
	public static final String password = "123456";
	public static final String emg_url = "jdbc:postgresql://10.1.7.104:5432/emg_org0_14q2";
	public static final String sql = "SELECT asText(geom), strname FROM geo.location "
			+ "where admin_code not like '__0000' or admin_code in ('110000','500000','120000','310000','810000','820000')";
//	public static final String sql = "SELECT asText(geom), strname FROM geo.location where admin_code = '210100'";
	
	/** 地图经度方向最小值 */
	public static final int MAP_GEO_LOCATION_LONGITUDE_MIN = 184320000;                                                                                                                                                                                                          
	/** 地图纬度方向最小值 */
	public static final int MAP_GEO_LOCATION_LATITUDE_MIN = 0;
	public static final int minLevel = 0;
	public static final int maxLevel = 2;
	/**
	 * 数据制作线程数
	 */
	public static final int threadCount = 20;
	
	/**
	 * 直辖市优先级
	 */
	public static final int prior_city = 0;
	/**
	 * 普通城市优先级
	 */
	public static final int normal_city = 1;
	/**
	 * 香港澳门优先级
	 */
	public static final int Macco_Hongkong = 2;
	

}
