package com.sunmap.shpdata.tools.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import com.sunmap.shpdata.tools.util.Util;
/**
 * 为向配置文件中写入信息所用
 * @author wenxc
 *
 */

public class ConfPro {
	protected static final URL CONF_FILE_PATH = ConfPro.class.getClassLoader()
	.getResource("conf.properties");
	protected static String strConfFilePath = "./cofig/conf.properties";
	private Properties conf;
	public ConfPro() {
		conf = new Properties();
		try {
			File file = new File(strConfFilePath);
				FileInputStream fileInputStream = new FileInputStream(file);
				conf.load(fileInputStream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setSrid(String srid){
		conf.setProperty("srid", srid);
		try {
			FileWriter out = new FileWriter(new File(strConfFilePath));
			conf.store(out, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setURL(String url){
		conf.setProperty("databaseURL", url);
		try {
			FileWriter out = new FileWriter(new File(strConfFilePath));
			conf.store(out, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setDriver(String driver){
		conf.setProperty("driver", driver);
		try {
			FileWriter out = new FileWriter(new File(strConfFilePath));
			conf.store(out, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setUser(String username){
		conf.setProperty("username", username);
		try {
			FileWriter out = new FileWriter(new File(strConfFilePath));
			conf.store(out, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setPass(String pass){
		conf.setProperty("password", pass);
		try {
			FileWriter out = new FileWriter(new File(strConfFilePath));
			conf.store(out, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setInputPath(String inputPath){
		try {
			String inputPath1 = Util.encode(inputPath);
			conf.setProperty("inputPath", inputPath1);
			FileWriter out = new FileWriter(new File(strConfFilePath));
			conf.store(out, "GBK");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setImportType(String importType){
		conf.setProperty("importType", importType);
		try {
			FileWriter out = new FileWriter(new File(strConfFilePath));
			conf.store(out, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setShpFileType(){
		conf.setProperty("fileType", ".dbf");
		try {
			FileWriter out = new FileWriter(new File(strConfFilePath));
			conf.store(out, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setShpThreadnum(String shpThreadnum){
		conf.setProperty("shpThreadnum", shpThreadnum);
		try {
			FileWriter out = new FileWriter(new File(strConfFilePath));
			conf.store(out, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ConfPro confPro=new ConfPro();
	}
}
