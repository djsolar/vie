package com.sunmap.teleview.parser;

import java.io.FileInputStream;
import java.util.Properties;

public class App {
	public static final Properties PROPERTIES = new Properties();
	static{
		try{
			
//			InputStream ins=App.class.getResourceAsStream("./app.properties");
			FileInputStream ins = new FileInputStream("./app.properties");
			PROPERTIES.load(ins);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
