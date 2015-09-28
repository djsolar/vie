package com.sunmap.shpdata.tools.daoimpl.shpparseimpl;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sunmap.shpdata.tools.conf.ShpConf;
import com.sunmap.shpdata.tools.dao.ParseHead;
import com.sunmap.shpdata.tools.util.ConstantValue;
import com.sunmap.shpdata.tools.util.JdbcUtil;
import com.sunmap.shpdata.tools.util.Util;
import com.sunmap.shpdata.tools.vo.Table;

public class ParseHeadImpl implements ParseHead{
	
	public final static String ORACLEDRIVERNAME = "oracle.jdbc.driver.OracleDriver";

	public final static String MYSQLDRIVERNAME = "com.mysql.jdbc.Driver";

	public final static String POSTGRESQLNAME = "org.postgresql.Driver";
	
	public  boolean createResolveTables(List<File> fileList) throws Exception{
//		ShpConf conf = new ShpConf();
//		conf.initProperties();
//		String strInputpath = conf.getInputpath();
//		Util.createTableMap = new HashMap<String, List<String>>();
		Util.element = new HashMap<String, String[]>();
		Util.allelement = new HashMap<String, String[]>();
		Util.tableMap = new HashMap<String, Table>();
		Util.tablenameList = new ArrayList<String>();
		Util.allelementatt = new HashMap<String, String>();
		Util.tableFieldsmap = new HashMap<String, Map<String, String>>();
		Util.tableRecordnum = new HashMap<String, Integer>();
		boolean flag2 = false;
		for (File file : fileList) {
			if (!file.getName().endsWith("dbf")) {
				continue;
			}
			String strPath = file.getParent();
//			strPath = strPath.replace(strInputpath, "");
			String[] strCom = strPath.split("\\\\");
			int  local = file.getName().lastIndexOf(".dbf");
			String strEleName = file.getName().substring(0, local).toLowerCase();
//			if(!strEleName.equals("Poi")){
//				continue;
//			}
			boolean flag1 = true;
//			File xmlFile = new File(this.getClass().getClassLoader()
//					.getResource("div.xml").toURI());
			File xmlFile = new File("./cofig/div.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);
			NodeList nl = doc.getElementsByTagName("level");
			if(nl.getLength() != 0){
//			System.out.println(no.getNodeName());
			String strPassName = "";
			for (Node no = nl.item(0); no != null; no = no.getNextSibling()) {
			NodeList nll = no.getChildNodes();
			boolean flag = false;
			boolean flag3 = false;
			for (Node node = nll.item(0); node != null; node = node.getNextSibling()) {
				if (node.getNodeType() == Node.ELEMENT_NODE) {
//					System.out.println(node.getTextContent());
					if("leveldiv_name".equals(node.getNodeName())){
						String strleveldiv = node.getTextContent().trim().replaceAll("\n", "").replaceAll("\r", "");
						if(strPath.contains(strleveldiv) || strleveldiv.equals("")){
							flag = true;
						}
					}
					if("passname_parentdiv".equals(node.getNodeName())
							&& flag && (strPath.contains("\\" + node.getTextContent().trim().replaceAll("\n", "").replaceAll("\r", "") + "\\")
									|| strPath.contains(node.getTextContent().trim().replaceAll("\n", "").replaceAll("\r", "") + ":\\"))){
						strPassName = node.getTextContent().trim().replaceAll("\n", "").replaceAll("\r", "");
						flag3 = true;
						break;
					}
				}
			}
			if(flag3){
				break;
			}
			}
			if(!"".equals(strPassName)){
			for (int i = 0; i < strCom.length; i++) {
				if(strPassName.equals(strCom[i])){
					String strTableName = strEleName.replace(strCom[i+1], "");
					System.out.println(strTableName);
					flag2 = this.resolveTable(strTableName, file);
					flag1 = false;
					break;
				}
			}
			}
			}
			if(flag1){
				System.out.println(strEleName);
				flag2 = this.resolveTable(strEleName, file);
			}
		}
		return flag2;
	}
	/**
	 * 解析表的字段
	 * @param strTableName String
	 * @param file File
	 * @return boolean
	 */
	public boolean resolveTable(String strTableName, File file) throws Exception{
		boolean flag = false;
		String sign = "@";
		Vector<String> list2 = new Vector<String>(); 
		Table table = new Table();
		table.setTableName(strTableName);
		DataInputStream inData = new DataInputStream(
	     		new BufferedInputStream(
	     			new FileInputStream(file)));
	 		byte[] head = new byte[ConstantValue.HEAD_LENGTH];
			inData.read(head);
			/** 记录条数 */
			byte[] registNum = Arrays.copyOfRange(head, 4, 8);
			int iRecordNum = Util.convertToInt(registNum, registNum.length,
					false);
			/**文件头的长度*/
			byte[] headLength = Arrays.copyOfRange(head, 8, 10);
			int iHeadLength = Util.convertToShort(headLength,headLength.length, false);
			/** 读取记录项名*/
			byte[] recordName = new byte[iHeadLength-32];
			int countNum =  (iHeadLength-32)/32;
			inData.read(recordName);
			byte[] name = new byte[18];
//			if(Util.tableFieldmap.get(strTableName.toUpperCase()) == null){
//				Connection con = JdbcUtil.getConnection();
//				Statement stm = con.createStatement();
//				ShpConf conf = new ShpConf();
//				String strURL = conf.getDatabaseURL();
//				if(this.MYSQLDRIVERNAME.equals(conf.getDriver())){
//					int index = strURL.lastIndexOf("/");
//					String strTablespace = strURL.substring(index + 1, strURL.length());
//						String sql = "show tables where Tables_in_" 
//							+ strTablespace + "='" + strTableName + "'";
//					ResultSet res = stm.executeQuery(sql);
//					if(res.next()){
//						Util.tableFieldmap.put(strTableName.toUpperCase(), -1);
//						return false;
//					}
//				}else if(this.ORACLEDRIVERNAME.equals(conf.getDriver())){
//						String sql = "select * from user_tables where table_name ='" 
//							 + strTableName.toUpperCase() + "'";
//					ResultSet res = stm.executeQuery(sql);
//					if(res.next()){
//						Util.tableFieldmap.put(strTableName.toUpperCase(), -1);
//						return false;
//					}
//				}else if(this.POSTGRESQLNAME.equals(conf.getDriver())){
//						String sql = "select * from pg_tables where tablename ='" 
//							 + strTableName.toLowerCase() + "'";
//					ResultSet res = stm.executeQuery(sql);
//					if(res.next()){
//						Util.tableFieldmap.put(strTableName.toUpperCase(), -1);
//						return false;
//					}
//				}
				Util.tableFieldmap.put(strTableName.toUpperCase(), countNum + 3);
//			}else{
//				if(Util.tableFieldmap.get(strTableName.toUpperCase()) == -1 
//						|| Util.tableFieldmap.get(strTableName.toUpperCase()) >= (countNum + 4)){
//					return false;
//				}
////				Util.tableFieldmap.put(key, value)
//			}
				Map<String, String> map = Util.tableFieldsmap.get(strTableName);
				if(map == null){
					map = new HashMap<String, String>();
				}
				int fieldnum = map.size();
			for (int i = 0; i < countNum; i++) {
				name = Arrays.copyOfRange(recordName, i*32, i*32+18);
				String fieldName = new String(Arrays.copyOfRange(name, 0, 11));
				ShpConf conf = new ShpConf();
				String fieldType = "";
				if(this.MYSQLDRIVERNAME.equals(conf.getDriver())){
					if(i == 0){
//						table.addField("gid", "VARCHAR", "256", "not null");
//						table.addField("mymapid", "VARCHAR", "64", "");
//						table.addField("geom", "geometry", "", "not null");
//						table.addField("geom1", "text", "65535", "");
//						table.addField("geom2", "text", "65535", "");
						map.put("gid","VARCHAR;256;not null");
						map.put("mymapid","VARCHAR;64;@");
						map.put("geom","geometry;@;not null");
						map.put("geom1","text;65535;@");
						map.put("geom2","text;65535;@");
					}
					fieldType = Util.mySqlfieldType(name[11]);
				}else if(this.ORACLEDRIVERNAME.equals(conf.getDriver())){
					if(i == 0){
//						table.addField("gid", "VARCHAR2", "256", "not null");
//						table.addField("mymapid", "VARCHAR2", "64", "");
//						table.addField("geom", "MDSYS.SDO_GEOMETRY", "", "not null");
//						table.addField("geom1", "clob", "", "");
						map.put("gid","VARCHAR2;256;not null");
						map.put("mymapid","VARCHAR2;64;@");
						map.put("geom","MDSYS.SDO_GEOMETR;@;not null");
						map.put("geom1","clob;@;@");
					}
					fieldType = Util.oraclefieldType(name[11]);
				}else if(this.POSTGRESQLNAME.equals(conf.getDriver())){
					if(i == 0){
//						table.addField("gid", "VARCHAR", "256", "not null");
//						table.addField("mymapid", "VARCHAR", "64", "");
//						table.addField("geom", "geometry", "", "not null");
//						table.addField("geom1", "text", "", "");
//						table.addField("geom2", "text", "", "");
						map.put("gid","VARCHAR;256;not null");
						map.put("mymapid","VARCHAR;64;@");
						map.put("geom","geometry;@;not null");
						map.put("geom1","text;@;@");
						map.put("geom2","text;@;@");
					}
					fieldType = Util.postgresqlfieldType(name[11]);
				}
				byte[] fieldlength = Arrays.copyOfRange(name, 16, 18);
				int fieldlengthint = fieldlength[0];
				if(fieldlength[0] < 0){
					fieldlengthint = fieldlength[0] + 256;
				}
				String strfieldlengthint = fieldlengthint + "";
				if("numeric".equals(fieldType.toLowerCase().trim())
						&& fieldlength.length > 1){
					int fieldlengthdou = fieldlength[1];
					if(fieldlengthdou != 0){
						if(fieldlengthdou < 0){
							fieldlengthdou = fieldlengthdou + 256;
						}
						strfieldlengthint = strfieldlengthint + "," + fieldlengthdou;
					}
				}
				fieldName = fieldName.trim();
				for (String string : list2) {
					if(string.equals((fieldName))){
						Util.tableFieldmap.remove(strTableName.toUpperCase());
						File file1 = new File("error.txt");
						file1.createNewFile();
						FileWriter out = new FileWriter(file1, true);
						out.write("文件"+strTableName+"不能建表\r");
						out.close();
						return false;
					}
				}
				list2.add(fieldName);
//				table.addField(fieldName, fieldType, fieldlengthint + "", "");
				if(map.get(fieldName) != null){
					String strFieldType = map.get(fieldName);
					String[] arrFieldType = strFieldType.split(";");
					if(arrFieldType.length >= 2){
						String strFieldlen = arrFieldType[1];
						int iField = 0;
						try{
							
						if(strFieldlen.contains(",")){
							iField = Integer.parseInt(strFieldlen.split(",")[0]);
						}else {
							iField = Integer.parseInt(strFieldlen);
						}
						if(iField > Integer.parseInt(strfieldlengthint)){
							strfieldlengthint = arrFieldType[1];
						}
						}catch (Exception e) {
							// TODO: handle exception
							strfieldlengthint = arrFieldType[1];
						}
					}
					
				}
				map.put(fieldName,  fieldType + sign + ";" + strfieldlengthint + sign + ";" + sign);
			}
			int num = 0;
			if(Util.tableRecordnum.get(strTableName.toLowerCase()) != null){
				num = Util.tableRecordnum.get(strTableName.toLowerCase());
			}
			Util.tableRecordnum.put(strTableName.toLowerCase(), num + iRecordNum);
			if(map.size() - fieldnum > 0){
				for (String string : map.keySet()) {
					String[] fieldinfo = map.get(string).split(";");
					table.addField(string, fieldinfo[0].replace(sign, ""), fieldinfo[1].replace(sign, ""), fieldinfo[2].replace(sign, ""));
//					int abc = 0;
//					if(fieldinfo[0].replace(sign, "").toUpperCase().equals("NAMEC")
//							&&  fieldinfo[2].replace(sign, "").equals("80")){
//						abc = abc + 1;
//					}
//					if(abc == 2){
//						System.out.println(file.getName());
//					}
				}
				Util.tableFieldsmap.put(strTableName, map);
			}else {
				return true;
			}
			inData.close();
			String scpoe = "Taskdata";
//			if(file.getPath().contains("china")
//					|| file.getPath().contains("China")){
//				scpoe = "Country";
//			}
			list2.insertElementAt("gid", 0);
			list2.add("geom");
			list2.add("mymapid");
			list2.add("geom1");
			list2.add("geom2");
			flag = this.create(table, list2, scpoe);
		return flag;	
	}
	/**
	 * 根据需求建表
	 * @param table Table
	 * @return boolean
	 * @throws Exception
	 */
	public boolean create(Table table, List<String> list, String scpoe) throws Exception{
		boolean flag = false;
		String allattname = "";
		for (String string : list) {
			allattname = allattname + string + ",";
		}
		allattname = allattname.substring(0, allattname.length() - 1);
					String[] strallttnamescope = new String[]{allattname,scpoe};
					Util.element.put(table.getTableName(), strallttnamescope);
					Util.allelement.put(table.getTableName(), strallttnamescope);
					Util.allelementatt.put(table.getTableName(), allattname);
					Util.tableMap.put(table.getTableName(), table);
					if(!Util.tablenameList.contains(table.getTableName())){
						Util.tablenameList.add(table.getTableName());
					}
		return flag;
	}


}
