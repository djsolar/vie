package com.sunmap.shpdata.tools.conf;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 配置文件 created by
 * 
 * @author daig
 */
public class ShpConf {
	/**
	 * 配置文件路径 
	 */
	protected static final URL CONF_FILE_PATH = ShpConf.class.getClassLoader()
			.getResource("conf.properties");
	protected static final String strCONF_FILE_PATH ="./cofig/conf.properties";
	/**
	 * 配置文件实例
	 */
	private Properties conf ;
//	private static String username ;
//	private static String password;
//	private static String importType;
//	private static String driver;
//	private static String databaseURL;
//	private static String inputPath;
//	private static String fileType;
//	private static String shpThreadnum;
//	private static String srid;
//	static {
//		try {
//			initProperties();
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("conf文件初始化失败");
//		}
//	}
	public ShpConf() {
		// TODO Auto-generated constructor stub
		try {
			initProperties();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 读取外部配置文件
	 */
	public  void initProperties() throws Exception {
		conf = new Properties();
		File file = new File(strCONF_FILE_PATH);
		FileInputStream confInputStream = new FileInputStream(file);
		conf.load(confInputStream);
		confInputStream.close();
//		username = conf.getProperty("username");
//		password = conf.getProperty("password");
//		importType = conf.getProperty("importType");
//		driver = conf.getProperty("driver");
//		databaseURL = conf.getProperty("databaseURL");
//		inputPath = conf.getProperty("inputPath");
//		fileType = conf.getProperty("fileType");
//		shpThreadnum = conf.getProperty("shpThreadnum");
//		srid = conf.getProperty("srid");
	}
	public String getDivideSympol(){
		return conf.getProperty("divideSympol");
	}
	public String getFileType(){
		return conf.getProperty("fileType");
	}

	/**
	 * 获得输入路径
	 * 
	 * @return 输入路径
	 */
	public String getInputpath() {
		return conf.getProperty("inputPath");
	}
	public String getStrImportType(){
		return conf.getProperty("importType");
	}

	/**
	 * 获得输出路径
	 * 
	 * @return 输出路径
	 */
	public String getOutputpath() {
		return conf.getProperty("outputPath");
	}

	/**
	 * 获得数据库用户名
	 * 
	 * @return 用户名
	 */
	public String getUsername() {
		return conf.getProperty("username");
	}

	/**
	 * 获得数据库 密码
	 * 
	 * @return 密码
	 */
	public String getPassword() {
		return conf.getProperty("password");
	}
	/**
	 * 获得数据库驱动
	 * @return 数据库驱动
	 */
	public String getDriver(){
		return conf.getProperty("driver");
	}
	/**
	 * 获得数据库连接串
	 * @return 数据库连接串
	 */
	public String getDatabaseURL(){
		return conf.getProperty("databaseURL");
	}
	/**
	 * 获得shp导入线程数
	 * @return 线程数
	 */
	public  String getThreadnum() {
		return conf.getProperty("shpThreadnum");
	}
	/**
	 * 获得所有MapIDs
	 * 
	 * @return 所有MapIDs File 文件
	 */
	public List<File> getMapIDs() {
		List<File> mapIDs = new ArrayList<File>();
		File f = new File(this.getInputpath());
		File[] listAll = f.listFiles();
		for (File file : listAll) {
			if (file.isDirectory()) {
				mapIDs.add(file);
			}
		}
		return mapIDs;
	}
	public  String getSrid() {
		return conf.getProperty("srid");
	}


}
