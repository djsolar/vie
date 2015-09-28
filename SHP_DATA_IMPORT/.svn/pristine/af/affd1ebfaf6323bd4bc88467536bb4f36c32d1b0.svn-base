package formconf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;



public class Pro {
	private Properties conf;
	protected static final URL CONF_FILE_PATH = Pro.class.getClassLoader()
	.getResource("databasefile.properties");
	protected static String strConfFilePath = "./cofig/databasefile.properties";
	public Pro() {
		conf = new Properties();
		try {
//			File file = new File(strConfFilePath);
			File file = new File(strConfFilePath);
			conf.load(new FileInputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	public List<String> getconfproName(){
		List<String> list = new ArrayList<String>();
		 Enumeration<?> name = conf.propertyNames();
		 while (name.hasMoreElements()) {
			String elem = (String) name.nextElement();
			list.add(elem);
		}
		 return list;
	}
	
	public void writeFile(Map<String, String> map) throws Exception {
		String valueString = map.get("datatype") + "%" + map.get("dataname") + "%" + map.get("ip") + "%" + map.get("user") + "%" + map.get("pass");
		conf.setProperty(map.get("SRID"), valueString);
		FileWriter out = new FileWriter(new File(strConfFilePath));
		conf.store(out, null);
	}
	
	public void remove(String srid) throws Exception{
		conf.remove(srid);
		FileWriter out = new FileWriter(new File(strConfFilePath));
		conf.store(out, null);
	}
	
	public String getValue(String keyname){
		return conf.getProperty(keyname);
	}
	
}
