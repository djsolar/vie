package com.sunmap.shpdata.tools.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import oracle.spatial.geometry.JGeometry;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sunmap.shpdata.tools.conf.ShpConf;
import com.sunmap.shpdata.tools.dao.ISHPFielddao;
import com.sunmap.shpdata.tools.daoimpl.fieldreadimpl.ReadFieldByBoolean;
import com.sunmap.shpdata.tools.daoimpl.fieldreadimpl.ReadFieldByInt;
import com.sunmap.shpdata.tools.daoimpl.fieldreadimpl.ReadFieldByStr;
import com.sunmap.shpdata.tools.filter.MyFileFilter;
import com.sunmap.shpdata.tools.filter.MyFileNameFilter;
import com.sunmap.shpdata.tools.vo.Table;
import com.sunmap.struts2.Com.TypeDef.EleType;
import com.sunmap.struts2.Com.TypeDef.SwEleType;

/**
 * JBOSS Ant 帮助类 created by
 * 
 * @author daig
 */
public class Util {

	public final static String ORACLEDRIVERNAME = "oracle.jdbc.driver.OracleDriver";

	public final static String MYSQLDRIVERNAME = "com.mysql.jdbc.Driver";

	public final static String POSTGRESQLNAME = "org.postgresql.Driver";

	public static Set<String> mymapidset = new HashSet<String>();

	public static int countNum = 0;

	public static String logName = "";
	// public static int countNum1;

	public static Map<String, Integer> map = new HashMap<String, Integer>();

	public static List<String> tablenameList = new ArrayList<String>();
	
	public static Map<String, Integer> tableFieldmap = new HashMap<String, Integer>();
	
	public static Map<String, Integer> tableRecordnum = new HashMap<String, Integer>();
	
	public static Map<String, Map<String, String>> tableFieldsmap = new HashMap<String, Map<String, String>>();

	public static Map<String, String[]> element = new HashMap<String, String[]>();
	
	public static Map<String, String[]> allelement = new HashMap<String, String[]>();
	
	public static Map<String, Table> tableMap = new HashMap<String, Table>();
	
	public static Map<String, String> allelementatt = new HashMap<String, String>();
	
	public static List<Table> createtableList = new ArrayList<Table>();
	
	public static Map<String, List<String>> subAreaName = new HashMap<String, List<String>>();
	
//	public static Map<String, List<String>> createTableMap = new HashMap<String, List<String>>();
	
	public static int threadNum = 0;
	
	public static boolean flag = true;
	
	public static byte parentFiledirFlag = 0;
	
	public static byte PARENTDIR = 1;
	public static byte PARENTDIRADDMAPID = 2;

	/**
	 * 解决waitFor的阻塞问题。
	 * 
	 * @param p
	 * @return 退出int
	 */
	@SuppressWarnings("static-access")
	public static int doWaitFor(Process p) {
		int exitValue = -1; // returned to caller when p is finished
		byte[] t = new byte[1024];
		try {

			InputStream in = p.getInputStream();
			InputStream err = p.getErrorStream();
			boolean finished = false; // Set to true when p is finished
			while (!finished) {
				try {
					while (in.available() > 0) {
						int length = in.read(t, 0, 1024);
						System.out.print(new String(t, 0, length));
					}
					while (err.available() > 0) {
						int length = err.read(t, 0, 1024);
						System.out.print(new String(t, 0, length));
					}
					exitValue = p.exitValue();
					finished = true;
				} catch (IllegalThreadStateException e) {
					Thread.currentThread().sleep(100);
				}
			}

		} catch (Exception e) {
			System.err.println("doWaitFor(): unexpected exception - ");
			e.printStackTrace();
		}
		return exitValue;
	}

	/**
	 * 
	 * @param strPath
	 *            :文件路径
	 * @return 文件list
	 */
	public static List<File> getFileList(String strPath) {

		List<File> fileList = new ArrayList<File>();

		File file = new File(Util.UnicodeToString(strPath));
		File[] dirFiles = file.listFiles(new MyFileFilter());
		List<File> list = null;
		for (int i = 0; i < dirFiles.length; i++) {
			list = getFileList(dirFiles[i].getPath());
			fileList.addAll(list);
		}
		File[] files = file.listFiles(new MyFileNameFilter(JdbcUtil.getConf()));
		fileList.addAll(Arrays.asList(files));
		// File[] list1 = file.listFiles(new MyFileNameFilter());
		// for(int i=0 ; i<list1.length ; i++ ){
		//			
		// if(list1[i].isDirectory()){
		// //是目录，则遍历
		// // File[] list2 = list1[i].listFiles();
		// List<File> list2 = getFileList(list1[i].getPath());
		//				
		// fileList.addAll(list2);
		// }else{
		//				
		// //不是目录，则直接读取文件
		// if (list1[i].getName().endsWith("dbf")){
		// fileList.add(list1[i]);
		// }
		//				
		// }
		// }

		return fileList;
	}

	public static EleType getEleTypeByFile(File file) throws IOException {

		return null;
	}

	/**
	 * 转换成INT类型
	 * 
	 * @param str
	 *            byte
	 * @param len
	 *            int
	 * @param big
	 *            boolean
	 * @return int
	 * @throws Exception
	 */
	public static int convertToInt(byte str[], int len, boolean big)
			throws Exception {
		ByteBuffer bb = null;
		// if (len!= 4) {
		// throw new Exception(" The byte length is wrong!");
		// }
		byte res[] = reversEndian(str, len, big);
		bb = ByteBuffer.wrap(res);
		int result = bb.getInt();
		return result;
	}

	/**
	 * 转换成short类型
	 * 
	 * @param str
	 *            byte
	 * @param len
	 *            int
	 * @param big
	 *            boolean
	 * @return int
	 * @throws Exception
	 */
	public static int convertToShort(byte str[], int len, boolean big)
			throws Exception {
		ByteBuffer bb = null;
		if (len != 2) {
			throw new Exception(" The byte length is wrong!");
		}
		byte res[] = reversEndian(str, len, big);
		bb = ByteBuffer.wrap(res);
		short result = bb.getShort();
		return result;
	}

	/**
	 * 转换成Float类型
	 * 
	 * @param str
	 *            byte
	 * @param len
	 *            int
	 * @param big
	 *            boolean
	 * @return float
	 * @throws Exception
	 */
	public static float convertToFloat(byte str[], int len, boolean big)
			throws Exception {
		ByteBuffer bb = null;
		if (len != 4) {
			throw new Exception(" The byte length is wrong!");
		}
		byte res[] = reversEndian(str, len, big);
		bb = ByteBuffer.wrap(res);
		float result = bb.getFloat();
		return result;

	}

	/**
	 * 转换成Double类型
	 * 
	 * @param str
	 *            byte
	 * @param len
	 *            int
	 * @param big
	 *            boolean
	 * @return
	 * @throws Exception
	 */
	public static double convertToDouble(byte str[], int len, boolean big)
			throws Exception {
		ByteBuffer bb = null;
		if (len != 8) {
			throw new Exception(" The byte length is wrong!");
		}
		byte res[] = reversEndian(str, len, big);
		bb = ByteBuffer.wrap(res);
		double result = bb.getDouble();
		return result;

	}

	/**
	 * 字序转换
	 * 
	 * @param str
	 *            byte
	 * @param len
	 *            int
	 * @param big
	 *            boolean
	 * @return byte[]
	 */
	public static byte[] reversEndian(byte str[], int len, boolean big) {
		byte b;
		byte res[] = new byte[len];
		for (int i = 0; i < len; i++) {
			res[i] = str[i];

		}
		if (!big) {
			for (int i = 0; i < len; i++) {
				b = str[i];
				res[len - i - 1] = b;
			}
		}
		return res;
	}

	/**
	 * 字序转换
	 * 
	 * @param str
	 *            byte
	 * @param len
	 *            int
	 * @param big
	 *            boolean
	 * @return byte[]
	 */
	public static String convertByteStr(byte str[]) {
		return null;

	}

	/**
	 * 将字段类型转换为系统数据类型
	 * 
	 * @param type
	 *            byte
	 * @return IConvertData
	 */
	public static ISHPFielddao fieldTypeToColumnType(byte type) {
		switch (type) {
		// 二进制型 各种字符
		case (byte) 'B':
			return new ReadFieldByStr();

			// C 字符型 各种字符。
		case (byte) 'C':
			return new ReadFieldByStr();

			// D - 日期型
		case (byte) 'D':
			return new ReadFieldByStr();

			// G - 通用型
		case (byte) 'G':
			return new ReadFieldByStr();

			// N - 通用型
		case (byte) 'N':
			return new ReadFieldByInt();

			// L - 逻辑型
		case (byte) 'L':
			return new ReadFieldByBoolean();

			// M - 各种字符
		case (byte) 'M':
			return new ReadFieldByStr();

			// 缺省字符串型
		default:
			return new ReadFieldByStr();
		}
	}

	/**
	 * 传入一个map（key：属性名 value：属性值）和类名，生成该类的一个实例并填充相应的值
	 * 
	 * @param EleRec
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public static Object getEleBean(Map map, String className) throws Exception {

		// 0.创建该类的一个实例
		Class cls = Class.forName(className);
		Object obj = cls.newInstance();

		// 1.获取该类的属性列表
		Field[] fs = cls.getDeclaredFields();

		// 2.遍历属性列表中的没有属性，在map中查找是否有相应的值
		for (Field f : fs) {

			Object v = map.get(f.getName().toUpperCase().replace("_", ""));
			// System.err.println(f.getName().toUpperCase());
			if (v == null) {
				// 3.如果没有找到相应的值，则遍历下个属性
				continue;
			} else {
				// 4.如果找到相应的值，则给实例的属性赋值
				// 4.1 根据属性名得到方法名称
				String methodName = "set"
						+ f.getName().substring(0, 1).toUpperCase()
						+ f.getName().substring(1);

				// 4.2 根据方法名称和方法参数列表得到给属性赋值的方法
				Class[] ps = new Class[1];
				ps[0] = f.getType();
				Method method = cls.getDeclaredMethod(methodName, ps);

				// 4.3 对该类的实例调用该方法给对应的属性赋值
				try {
					method.invoke(obj, v);
				} catch (Exception e) {
					System.err.println(f.getName());
					System.err.println(ps[0]);
					e.printStackTrace();
				}

			}
		}

		return obj;

	}

	/**
	 * 根据文件名称返回枚举类型
	 * 
	 * @param strEleTypeName
	 * @return
	 */
	public static EleType getEleTypeByFileName(File file) {

		int local = file.getName().lastIndexOf(".dbf");
		String strEleName = file.getName().substring(0, local);

		EleType[] asTypes = EleType.values();
		for (int i = 0; i < asTypes.length; i++) {
			if (strEleName.equals(asTypes[i].getStrFileName())) {
				return asTypes[i];
			}
		}
		return null;

	}

	public static SwEleType getEleTypeByFileName(File file, String strParentName) {

		int local = file.getName().lastIndexOf(strParentName + ".dbf");
		String strEleName = file.getName().substring(0, local);

		SwEleType[] asTypes = SwEleType.values();
		for (int i = 0; i < asTypes.length; i++) {
			if (strEleName.equals(asTypes[i].getStrFileName())) {
				return asTypes[i];
			}
		}
		return null;

	}

	/**
	 * 比较两个字符串，将相同的部分取出
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static List<String> selectSameString(String str1, String str2) {
		List<String> list = new Vector<String>();
		List<String> list1 = new Vector<String>();
		for (int i = 0; i < str1.length(); i++) {
			for (int j = 0; j < str2.length(); j++) {
				for (int len = 1; len < str2.length() - j + 1; len++) {
					if (!str1.regionMatches(i, str2, j, len)) {
						if (str1.substring(i, i + len - 1) != null
								&& !str1.substring(i, i + len - 1).equals("")) {
							list.add(str1.substring(i, i + len - 1));
						}
						break;
					} else if (str1.regionMatches(i, str2, j, len)
							&& j + len == str2.length()) {
						if (str1.substring(i, i + len) != null
								&& !str1.substring(i, i + len).equals("")) {
							list.add(str1.substring(i, i + len));
						}
						break;
					}
				}
			}
		}
		Collections.sort(list, cmpObject);
		for (int i = list.size() - 1; i > 0; i--) {
			list1.add(list.get(i));
			if (list.get(i).length() > list.get(i - 1).length()) {
				break;
			}
		}
		return list1;
	}

	static Comparator<String> cmpObject = new Comparator<String>() {
		public int compare(String str1, String str2) {
			// modify by yangliang 修改中间代码使之能为变化而使用。
			int len1 = str1.length();
			int len2 = str2.length();
			return len1 - len2;
		}
	};

	public static Set<String> returnString(List<File> filelist) {
		List<String> list = new Vector<String>();
		Set<String> set = new HashSet<String>();
		for (File file : filelist) {
			if (!file.getName().endsWith("dbf")) {
				continue;
			}
			int local = file.getName().lastIndexOf(".dbf");
			String strEleName = file.getName().substring(0, local);
			list.add(strEleName);
		}
		if (list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				List<String> list1 = new Vector<String>();
				String str1 = list.get(i);
				for (int j = 0; j < list.size(); j++) {
					if (i == j) {
						continue;
					}
					list1.addAll(Util.selectSameString(str1, list.get(j)));
				}
				Collections.sort(list1, cmpObject);
				for (int t = list1.size() - 1; t > 0; t--) {
					set.add(list1.get(t));
					if (list1.get(t).length() > list1.get(t - 1).length()) {
						break;
					} else if (t == 1) {
						set.add(list1.get(0));
					}
				}
			}
		}
		if (set.size() == 0) {
			return null;
		} else {
			List<String> list2 = new Vector<String>();
			for (String string : set) {
				for (String string1 : set) {
					if (!string.equals(string1) && string.contains(string1)) {
						list2.add(string);
					}
				}
			}
			if (list2 != null) {
				for (String string2 : list2) {
					set.remove(string2);
				}
			}
		}
		return set;
	}

	/**
	 * 将字段类型转换为mysql存储数据类型
	 * 
	 * @param type
	 *            byte
	 * @return IConvertData
	 */
	public static String mySqlfieldType(byte type) {
		switch (type) {
		// 二进制型 各种字符
		case (byte) 'B':
			return "varchar";

			// C 字符型 各种字符。
		case (byte) 'C':
			return "varchar";

			// D - 日期型
		case (byte) 'D':
			return "varchar";

			// G - 通用型
		case (byte) 'G':
			return "varchar";

			// N - 通用型
		case (byte) 'N':
			return "bigint";

			// L - 逻辑型
		case (byte) 'L':
			return "bigint";

			// M - 各种字符
		case (byte) 'M':
			return "varchar";

			// 缺省字符串型
		default:
			return "varchar";
		}
	}

	/**
	 * 将字段类型转换为oracle存储数据类型
	 * 
	 * @param type
	 *            byte
	 * @return IConvertData
	 */
	public static String oraclefieldType(byte type) {
		switch (type) {
		// 二进制型 各种字符
		case (byte) 'B':
			return "varchar2";

			// C 字符型 各种字符。
		case (byte) 'C':
			return "varchar2";

			// D - 日期型
		case (byte) 'D':
			return "varchar2";

			// G - 通用型
		case (byte) 'G':
			return "varchar2";

			// N - 通用型
		case (byte) 'N':
			return "NUMBER";

			// L - 逻辑型
		case (byte) 'L':
			return "NUMBER";

			// M - 各种字符
		case (byte) 'M':
			return "varchar2";

			// 缺省字符串型
		default:
			return "varchar2";
		}
	}

	/**
	 * 将字段类型转换为PostgreSql存储数据类型
	 * 
	 * @param type
	 *            byte
	 * @return IConvertData
	 */
	public static String postgresqlfieldType(byte type) {
		switch (type) {
		// 二进制型 各种字符
		case (byte) 'B':
			return "varchar";

			// C 字符型 各种字符。
		case (byte) 'C':
			return "varchar";

			// D - 日期型
		case (byte) 'D':
			return "varchar";

			// G - 通用型
		case (byte) 'G':
			return "varchar";

			// N - 通用型
		case (byte) 'N':
			return "numeric";

			// L - 逻辑型
		case (byte) 'L':
			return "boolean";

			// M - 各种字符
		case (byte) 'M':
			return "varchar";

			// 缺省字符串型
		default:
			return "varchar";
		}
	}

	public static String[] returnTableName(File file) throws Exception {
		// ShpConf conf = new ShpConf();
		// try {
		// conf.initProperties();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// String strInputpath = conf.getInputpath();
		String strPath = file.getParent();
		// strPath = strPath.replace(strInputpath, "");
		String[] strCom = strPath.split("\\\\");
		int local = file.getName().lastIndexOf(".dbf");
		String strEleName = file.getName().substring(0, local);
		File xmlFile = new File(Util.class.getClassLoader()
				.getResource("div.xml").toURI());
//		File xmlFile = new File("./cofig/div.xml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(xmlFile);
		NodeList nl = doc.getElementsByTagName("level");
		String strPassName = "";
		String[] strTableInfo = new String[2];
		if (nl.getLength() != 0) {
			// System.out.println(no.getNodeName());
			for (Node no = nl.item(0); no != null; no = no.getNextSibling()) {
				NodeList nll = no.getChildNodes();
				boolean flag = false;
				boolean flag3 = false;
				for (Node node = nll.item(0); node != null; node = node
						.getNextSibling()) {
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						// System.out.println(node.getTextContent());
						if ("leveldiv_name".equals(node.getNodeName())) {
							String strleveldiv = node.getTextContent().trim().replaceAll("\n", "").replaceAll("\r", "");
							strTableInfo[1] = strleveldiv;
							if (strPath.contains(strleveldiv)
									|| strleveldiv.equals("")) {
								flag = true;
							}
						}
						if ("passname_parentdiv".equals(node.getNodeName())
								&& flag
								&& (strPath.contains("\\"
										+ node.getTextContent().trim().replaceAll("\n", "").replaceAll("\r", "") + "\\") || strPath
										.contains(node.getTextContent().trim().replaceAll("\n", "").replaceAll("\r", "") + ":\\"))) {
							strPassName = node.getTextContent().trim().replaceAll("\n", "").replaceAll("\r", "");
							flag3 = true;
							break;
						}
					}
				}
				if (flag3) {
					break;
				}
			}
			if (!"".equals(strPassName)) {
				for (int i = 0; i < strCom.length; i++) {
					if (strPassName.equals(strCom[i])) {
						String strTableName = strEleName.replace(strCom[i + 1],
								"");
						// System.out.println(strTableName);
						// break;
						strTableInfo[0] = strTableName;
						return strTableInfo;
					}
				}
			}
		}
		strTableInfo[0] = strEleName;
		strTableInfo[1] = "";
		return strTableInfo;
	}

	// public static void createResolveTables(List<File> fileList) throws
	// Exception{
	// ShpConf conf = new ShpConf();
	// conf.initProperties();
	// String strInputpath = conf.getInputpath();
	// for (File file : fileList) {
	// if (!file.getName().endsWith("dbf")) {
	// continue;
	// }
	// String strPath = file.getParent();
	// strPath = strPath.replace(strInputpath, "");
	// String[] strCom = strPath.split("\\\\");
	// int local = file.getName().lastIndexOf(".dbf");
	// String strEleName = file.getName().substring(0, local);
	// boolean flag1 = true;
	// for (String string : strCom) {
	// if(!string.equals("") && strEleName.contains(string)){
	// String strTableName = strEleName.replace(string, "");
	// System.out.println(strTableName);
	// Util.resolveTable(strTableName, file);
	// flag1 = false;
	// break;
	// }
	// }
	// if(flag1){
	// Util.resolveTable(strEleName, file);
	// }
	// }
	// }
	//
	// public static boolean resolveTable(String strTableName, File file){
	// boolean flag = false;
	// Vector<String> list2 = new Vector<String>();
	// try{
	// DataInputStream inData = new DataInputStream(
	// new BufferedInputStream(
	// new FileInputStream(file)));
	// byte[] head = new byte[ConstantValue.HEAD_LENGTH];
	// inData.read(head);
	// /**文件头的长度*/
	// byte[] headLength = Arrays.copyOfRange(head, 8, 10);
	// int iHeadLength = Util.convertToShort(headLength,headLength.length,
	// false);
	// /** 读取记录项名*/
	// byte[] recordName = new byte[iHeadLength-32];
	// int countNum = (iHeadLength-32)/32;
	// inData.read(recordName);
	// byte[] name = new byte[18];
	// for (int i = 0; i < countNum; i++) {
	// name = Arrays.copyOfRange(recordName, i*32, i*32+18);
	// String fieldName = new String(Arrays.copyOfRange(name, 0, 11));
	// ShpConf conf = new ShpConf();
	// conf.initProperties();
	// String fieldType = "";
	// if(MYSQLDRIVERNAME.equals(conf.getDriver())){
	// fieldType = Util.mySqlfieldType(name[11]);
	// }else if(ORACLEDRIVERNAME.equals(conf.getDriver())){
	// fieldType = Util.oraclefieldType(name[11]);
	// }
	// byte[] fieldlength = Arrays.copyOfRange(name, 16, 17);
	// int fieldlengthint = fieldlength[0];
	// if(fieldlength[0] < 0){
	// fieldlengthint = fieldlength[0] + 256;
	// }
	// fieldName = fieldName.trim();
	// String strfield = fieldName + " " + fieldType + "(" + fieldlengthint +
	// ")";
	// for (String string : list2) {
	// if(string.split(" ")[0].equals((fieldName))){
	// File file1 = new File("error.txt");
	// file1.createNewFile();
	// FileWriter out = new FileWriter(file1, true);
	// out.write("文件"+strTableName+"不能建表\r");
	// out.close();
	// return false;
	// }
	// }
	// list2.add(strfield);
	// }
	// inData.close();
	// flag = Util.createTable(strTableName, list2);
	// }catch(Exception e){
	// System.out.println("解析，存储时错误");
	// e.printStackTrace();
	// }
	// return flag;
	// }
	//
	//
	// public static boolean createTable(String str1, Vector<String> vector)
	// throws Exception{
	// boolean flag = false;
	// ShpConf conf = new ShpConf();
	// conf.initProperties();
	// String strDriver = conf.getDriver();
	// Connection con = JdbcUtil.getConnection();
	// Statement stm = con.createStatement();
	// if("com.mysql.jdbc.Driver".equals(strDriver)){
	// String tableField = "";
	// for (String string : vector) {
	// tableField = tableField + string + ",";
	// }
	// // tableField = tableField.substring(0, tableField.length() - 1);
	// String sql ="CREATE TABLE IF NOT EXISTS " + str1.toUpperCase()
	// + "(GID varchar(255) not null,"
	// + tableField + "MYMAPID varchar(64),GEOM varchar(64),"+ "PRIMARY KEY
	// (GID),KEY "+
	// str1.toUpperCase() + " (MYMAPID)" + ")ENGINE=MyISAM DEFAULT
	// CHARSET=utf8";
	// String sql2 = "CREATE TABLE " + str1.toUpperCase()
	// + "(GID varchar(255) not null,"
	// + tableField + "MYMAPID varchar(64),GEOM varchar(64),"+ "PRIMARY KEY
	// (GID),KEY "+
	// str1.toUpperCase() + " (MYMAPID)" + ")ENGINE=MyISAM DEFAULT
	// CHARSET=utf8";
	// System.out.println(sql);
	// String sql3 = "show index from " + str1.toUpperCase()
	// + " where key_name = '" + str1.toUpperCase() + "_MYMAPID_IDEX" + "'";
	// String sql1 = "create index IF NOT EXISTS "+ str1.toUpperCase()
	// +"_MYMAPID_IDEX on "+
	// str1.toUpperCase() +" (MYMAPID)";
	// boolean flag1 = stm.execute(sql);
	// ResultSet rs1 = stm.executeQuery("select * from " + str1.toUpperCase() +
	// " limit 1");
	// ResultSetMetaData rsmd = rs1.getMetaData();
	// int columnNum = rsmd.getColumnCount();
	// if(columnNum < (vector.size() + 3)){
	// stm.execute("drop table " + str1.toUpperCase());
	// System.out.println(sql2);
	// flag1 = stm.execute(sql2);
	// }
	// ResultSet rs = stm.executeQuery(sql3);
	// if(rs.next()){
	// boolean flag2 = stm.execute(sql1);
	// flag = flag1 && flag2;
	// }
	// }else if("oracle.jdbc.driver.OracleDriver".equals(strDriver)){
	// String tableField = "";
	// for (String string : vector) {
	// tableField = tableField + string + ",";
	// }
	// // tableField = tableField.substring(0, tableField.length() - 1);
	// String sql ="CREATE TABLE " + str1.toUpperCase()
	// + "(GID varchar2(255) not null,"
	// + tableField + "MYMAPID varchar2(64),GEOM varchar2(64)" + ")";
	// String sql1 = "alter table " +str1.toUpperCase() + " add primary key
	// (GID) using index ";
	// String sql2 = "create index "+ str1.toUpperCase() +"_MYMAPID_IDEX on "+
	// str1.toUpperCase() +" (MYMAPID)";
	// ResultSet result = stm.executeQuery("select * from user_tables where
	// table_name = '"+
	// str1.toUpperCase() + "'");
	//				
	// if(!result.next()){
	// boolean flag1 = stm.execute(sql);
	// boolean flag2 = stm.execute(sql1);
	// boolean flag3 = stm.execute(sql2);
	// flag = flag1 && flag2 && flag3;
	// }else{
	// ResultSet rs1 = stm.executeQuery(
	// "select * from " + str1.toUpperCase() + " where rownum = 1");
	// ResultSetMetaData rsmd = rs1.getMetaData();
	// int columnNum = rsmd.getColumnCount();
	// if(columnNum < (vector.size() + 3)){
	// stm.execute("drop table " + str1.toUpperCase());
	// System.out.println(sql2);
	// boolean flag1 = stm.execute(sql);
	// boolean flag2 = stm.execute(sql1);
	// boolean flag3 = stm.execute(sql2);
	// flag = flag1 && flag2 && flag3;
	// }
	// }
	// }
	// return flag;
	// }

	/**
	 * 令特定的数据库表主键约束失效或者复原
	 * 
	 * @param tableName
	 *            String 表名称的数组
	 * @param operateType
	 *            String 令约束失效或者复原，可以为disable,enable
	 * @param stm
	 *            Statement
	 * @throws SQLException
	 */
	public static void operate(String tableName, String operateType,
			Statement stm) throws SQLException {
		String sql = "ALTER TABLE " + tableName.toUpperCase() + " "
				+ operateType + " constraint "
				+ Util.getConstraints(tableName, stm);
		stm.executeUpdate(sql);
		stm.close();

	}

	/**
	 * 根据传入条件得到所有符合条件的约束
	 * 
	 * @param tablename
	 *            String 表名称
	 * @param stm
	 *            Statement
	 * @return String
	 */
	public static String getConstraints(String tablename, Statement stm) {
		try {
			String sql = "SELECT CONSTRAINT_NAME from SYS.USER_CONSTRAINTS WHERE "
					+ "TABLE_NAME = '"
					+ tablename.toUpperCase()
					+ "' AND USER_CONSTRAINTS.constraint_type='P'";
			ResultSet rs = stm.executeQuery(sql);
			String strCONSTRAINT_NAME = "";
			while (rs.next()) {
				strCONSTRAINT_NAME = rs.getString("CONSTRAINT_NAME");
			}
			return strCONSTRAINT_NAME;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	final static int SPLIT_LENGTH = 65534;// 65535;

	public static byte[][] splitBytes(byte[] bytes) {
		if (bytes.length <= SPLIT_LENGTH) {
			return null;
		}
		int length = bytes.length % SPLIT_LENGTH;
		byte[][] b = new byte[2][];
		b[0] = new byte[SPLIT_LENGTH];
		b[1] = new byte[length];
		int i;
		for (i = 0; i < SPLIT_LENGTH; i++) {
			b[0][i] = bytes[i];
		}
		for (i = 0; i < length; i++) {
			b[1][i] = bytes[SPLIT_LENGTH + i];
		}
		return b;
	}

	public static synchronized void setCountNum() {
		Util.countNum++;
	}

	public static String encode(String str) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) <= 256) {
				sb.append(str.charAt(i));
//				sb.append("\\u00");
			} else {
				sb.append("\\u");
				sb.append(Integer.toHexString(str.charAt(i)));
			}
		}
		return sb.toString();
	}
	
	public static String UnicodeToString(String str){
		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
		Matcher matcher = pattern.matcher(str);
		char ch;
		while(matcher.find()){
			ch = (char)Integer.parseInt(matcher.group(2),16);
			str = str.replace(matcher.group(1), ch+"");
		}
		return str;
	}
	
	public static String isGeom(String data, String datatype){
		ShpConf shpConf = new ShpConf();
		String dataContent = "";
		String data1 = data;
		if(POSTGRESQLNAME.equals(shpConf.getDriver()) || MYSQLDRIVERNAME.equals(shpConf.getDriver())){
			if("1".equals(datatype)){
				dataContent = "GeomFromText('POINT(" +data1 + ")')";
			}else if ("3".equals(datatype)) {
				data1 = data1.replace(")_(", ",").replace("(", "").replace(")", "");
				dataContent = "GeomFromText('LINESTRING(" + data1 + ")')";
			}else if ("5".equals(datatype)) {
				data1 = data1.replace("_", ",");
				dataContent = "GeomFromText('POLYGON(" + data1 + ")')";
			}
		}
		return dataContent;
	}
	
	public static JGeometry isOracleGeometry(String data, String datatype){
		String data1 = data;
		ShpConf shpConf = new ShpConf();
		JGeometry geometry = null;
			if("1".equals(datatype)){
				String[] data2 = data1.split(" ");
//				dataContent = "SDO_GEOMETRY(2001,8199,NULL,SDO_ELEM_INFO_ARRAY(1,1,1),SDO_ORDINATE_ARRAY(" + data1 + "))";
				if(data2.length >= 2){
					geometry = new JGeometry(Double.parseDouble(data2[0]),Double.parseDouble(data2[1]),8199);
				}
			}else if ("3".equals(datatype)) {
				data1 = data1.replace(" ", ",").replace(",", ",").replace("_", ",").replace("(", "").replace(")", "");
				String[] data2 = data1.split(",");
				double[] doudata2 = new double[data2.length];
				for (int i = 0 ; i < data2.length ; i++) {
					doudata2[i] = Double.parseDouble(data2[i]);
				}
				geometry = new JGeometry(2002,8199,new int[]{1,2,1},doudata2);
			}else if ("5".equals(datatype)) {
				String[] strarr = data1.split("_");
				String sdoele = "1,1003,1";
				int a = 0;
				for (int i = 0; i < strarr.length - 1; i++) {
					if(strarr.length > 1){
					 a = a + strarr[i].replace(" ", ",").split(",").length + 1;
						sdoele =  sdoele + ", " + a + ",1003,1";
						a = a - 1;
					}
				}
				String[] sdoStrings = sdoele.split(",");
				int[] all = new int[sdoStrings.length];
				for (int i = 0; i < all.length; i++) {
					all[i] = Integer.parseInt(sdoStrings[i]);
				}
				data1 = data1.replace(" ", ",").replace(",", ",").replace("_", ",").replace("(", "").replace(")", "");
				String[] data2 = data1.split(",");
				double[] doudata2 = new double[data2.length];
				for (int i = 0 ; i < data2.length ; i++) {
					doudata2[i] = Double.parseDouble(data2[i]);
				}
				geometry = new JGeometry(2003,8199,all,doudata2);
//				dataContent = "SDO_GEOMETRY(2003,8199,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1" + sdoele + "),SDO_ORDINATE_ARRAY(" + data1 + "))";
			}
			return geometry;
	}
	
	public static List<File> filterList(List<File> list) throws Exception{
		List<File> filter = new ArrayList<File>();
		List<String> tablenameList = new ArrayList<String>();
		for (Table table : Util.createtableList) {
			tablenameList.add(table.getTableName());
		}
		for (File file : list) {
			if(tablenameList.contains(Util.returnTableName(file)[0].toLowerCase())){
				filter.add(file);
			}
		}
		return filter;
	}
	/**
	 * 判断是否包含此字段
	 * @param tablename
	 * @param Fieldname
	 * @return
	 */
	public static boolean isExistField(String tablename, String Fieldname){
		String[] arrStrings = Util.element.get(tablename);
		if(arrStrings.length > 1){
			String[] strarr = arrStrings[0].split(",");
			for (String string : strarr) {
				if (string.equals(Fieldname)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static void setMymapidToParentdir(String[] table, File file){
		
		if(Util.parentFiledirFlag == Util.PARENTDIR 
				|| Util.parentFiledirFlag == Util.PARENTDIRADDMAPID){
			table[1] = file.getParentFile().getName();
		}
		
	}
	
	public static void main(String[] args) {
//		Util.isGeom("(1 1,2 2,2 1,1 1)_(2 2,3 3,4 4,2 2)_(2 1,3 3,4 4,2 1)", "5");
		Util.isGeom("(1 1,2 2,2 1,1 1)", "5");
//		String aString = "(a)_(b)";
//		aString.split(")");
	}

//	public static void main(String[] args) {
//		String aString = "D:\\数据\\5956";
//		String s = Util.encode(aString);
//		System.out.println(s);
////		char[] a = s.toCharArray();
////		System.out.println(new String(a));
//		String dString = Util.UnicodeToString(s);
//		System.out.println(dString);
//		System.out.println(aString);
//	}
}
