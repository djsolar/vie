package com.mansion.tele.business.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mansion.tele.config.project.DataBaseInfo;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
/**
 * 
 * @author Administrator
 *
 */
public class TeleContext {

	/**
	 * Context解析
	 */
	private static TeleContext context;
	
	/**
	 * 名称jndi对应链接的库
	 */
	private Map<String, DataBaseInfo> map = new HashMap<String, DataBaseInfo>();
	
	/**
	 * 取得TeleContext
	 */
	private TeleContext() {
		
		this.loadDataSource();
	}
	private XStream xstream = new XStream(new DomDriver());

	
	/**
	 * 取得单件实例。
	 * 
	 * @return TeleContext
	 */
	public static synchronized TeleContext get() {
		if (null == TeleContext.context) {
			TeleContext.context = new TeleContext();
		}

		return TeleContext.context;
	}
	
	
	/**
	 * 获得数据源
	 */
	public  void  loadDataSource(){
		xstream.alias("DataBaseInfo", DataBaseInfo.class);
		List<DataBaseInfo> list = (List<DataBaseInfo>)xstream.fromXML(DataBaseInfo.class.getResourceAsStream("DataBaseInfo.xml"));
		for (DataBaseInfo dataBaseInfo : list) {
			this.map.put(dataBaseInfo.getStrJNDI(), dataBaseInfo);
		}	
	}

	/**
	 * 
	 * @return Map<String, DataBaseInfo>
	 */
	public Map<String, DataBaseInfo> getMap() {
		return this.map;
	}


	/**
	 * 
	 * @param map Map<String, DataBaseInfo>
	 */
	public void setMap(Map<String, DataBaseInfo> map) {
		this.map = map;
	}
	/**
	 * 
	 * @author Administrator
	 *
	 */
//	public class DataBaseInfo{
//		/**
//		 * jndi
//		 */
//		private String strJNDI;
//		/**
//		 * url
//		 */
//		private String strURL;
//		/**
//		 * name
//		 */
//		private String strUserName;
//		/**
//		 * passwd
//		 */
//		private String strPassword;
//		/**
//		 * 
//		 * @return String
//		 */
//		public String getStrJNDI() {
//			return this.strJNDI;
//		}
//		/**
//		 * 
//		 * @param strJNDI String
//		 */
//		public void setStrJNDI(String strJNDI) {
//			this.strJNDI = strJNDI;
//		}
//		/**
//		 * 
//		 * @return String
//		 */
//		public String getStrURL() {
//			return this.strURL;
//		}
//		/**
//		 * 
//		 * @param strURL String
//		 */
//		public void setStrURL(String strURL) {
//			this.strURL = strURL;
//		}
//		/**
//		 * 
//		 * @return String
//		 */
//		public String getStrUserName() {
//			return this.strUserName;
//		}
//		/**
//		 * 
//		 * @param strUserName String
//		 */
//		public void setStrUserName(String strUserName) {
//			this.strUserName = strUserName;
//		}
//		/**
//		 * 
//		 * @return String
//		 */
//		public String getStrPassword() {
//			return this.strPassword;
//		}
//		/**
//		 * 
//		 * @param strPassword String
//		 */
//		public void setStrPassword(String strPassword) {
//			this.strPassword = strPassword;
//		}
//	}
//	public static void main(String[] args) {
//		try {
//			new TeleContext().loadDataSource();
//		} catch (TeleException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
