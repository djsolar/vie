package com.sunmap.shpdata.tools.daoimpl.shpdaoimpl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import oracle.spatial.geometry.JGeometry;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sunmap.shpdata.tools.conf.ShpConf;
import com.sunmap.shpdata.tools.dao.TableDAO;
import com.sunmap.shpdata.tools.util.ConstantValue;
import com.sunmap.shpdata.tools.util.DataConnectionTool;
import com.sunmap.shpdata.tools.util.JdbcUtil;
import com.sunmap.shpdata.tools.util.Util;
import com.sunmap.shpdata.tools.vo.ShpFieldVO;
import com.sunmap.shpdata.tools.vo.Table;

import form.Form;

/**
 * shp文件交互数据库
 * 
 * @author Administrator
 * 
 */
public class ShpTableDaoImpl implements TableDAO {

	/**
	 * Oracle驱动名
	 */
	public static final String ORACLEDRIVERNAME = "oracle.jdbc.driver.OracleDriver";
	/**
	 * MySQL驱动名
	 */
	public static final String MYSQLDRIVERNAME = "com.mysql.jdbc.Driver";
	/**
	 * PostgreSql驱动名
	 */
	public static final String POSTGRESQLDRIVERNAME = "org.postgresql.Driver";
	
	public static Map<String, Table> createTaMap = new HashMap<String, Table>();
	
	public static String errorString;

	/**
	 * 去掉主键索引
	 * 
	 * @param strTableName
	 *            String
	 * @return Connection
	 * @throws Exception
	 *             Exception
	 */
	public Connection removeIndex(String strTableName) throws Exception {
		ShpConf conf = new ShpConf();
		conf.initProperties();
		int flagx = 0;
		if (this.MYSQLDRIVERNAME.equals(conf.getDriver())) {
			flagx = 1;
		} else if (this.ORACLEDRIVERNAME.equals(conf.getDriver())) {
			flagx = 2;
		}
		Connection con = JdbcUtil.getConnection();
		con.setAutoCommit(false);
		PreparedStatement ps = null;
		if (flagx == 1) {
			ps = con.prepareStatement("alter table " + strTableName
					+ " disable keys");
			ps.execute();
			ps.close();
		} else if (flagx == 2) {
			if (Util.map.get(strTableName) != null) {
				int y = Util.map.get(strTableName);
				Util.map.put(strTableName, y + 1);
			} else {
				Util.map.put(strTableName, 1);
				Statement ss = con.createStatement();
				Util.operate(strTableName, "disable", ss);
			}
		}
		con.commit();
		return con;
	}

	/**
	 * 去掉主键索引
	 * 
	 * @param strTableName
	 *            String
	 * @param astFieldEle
	 *            List
	 * @return Statement
	 * @throws Exception
	 */
	public PreparedStatement createSql(String strTableName,
			List<ShpFieldVO> astFieldEle, Connection con) throws Exception {
		PreparedStatement ps = null;
		String strEle = "";
		String strMark = "";
		for (int i = 0; i < astFieldEle.size(); i++) {
			strEle = strEle + astFieldEle.get(i).getStrName() + ",";
			strMark = strMark + "?,";
		}
		strEle = strEle.substring(0, strEle.length() - 1);
		strMark = strMark.substring(0, strMark.length() - 1);
		String sql = "insert into " + strTableName.toUpperCase() + "(GID,"
				+ strEle + ",MYMAPID,GEOM,GEOM1) " + "values(?,?,?,?,"
				+ strMark + ")";
		ps = con.prepareStatement(sql);
		return ps;
	}

	/**
	 * 创建表 param filelist List return boolean
	 * 
	 * @throws Exception
	 */
	@Override
	public boolean createTable(List<File> filelist) throws Exception {
		ShpConf conf = new ShpConf();
		conf.initProperties();
		Table table = new Table();
		Table table1 = new Table();
		table1.setTableName("mapdata");
		table.setTableName("element");
		Connection con = JdbcUtil.getConnection();
		Statement stm = con.createStatement();
		if (this.ORACLEDRIVERNAME.equals(conf.getDriver())) {
			table.addField("id", "varchar2", "500", "primary key");
			table.addField("elementname", "varchar2", "500", "");
			table.addField("allattributename", "varchar2", "2000", "");
			table.addField("scope", "varchar2", "500", "");
			table.addField("evaltype", "number", "38", "");
			table1.addField("id", "varchar2", "500", "primary key");
			table1.addField("mymapid", "varchar2", "500", "");
			table1.addField("evaltype", "number", "38", "");
			ResultSet result = stm
					.executeQuery("select * from user_tables where table_name = '"
							+ table.getTableName().toUpperCase() + "'");
			if (!result.next()) {
				JdbcUtil.oracleCreateTable(table);
			}
			result = stm
					.executeQuery("select * from user_tables where table_name = '"
							+ table1.getTableName().toUpperCase() + "'");
			if (!result.next()) {
				JdbcUtil.oracleCreateTable(table1);
			}
		}
		if (this.MYSQLDRIVERNAME.equals(conf.getDriver())) {
			table.addField("id", "varchar", "100", "primary key");
			table.addField("elementname", "varchar", "100", "");
			table.addField("allattributename", "varchar", "8000", "");
			table.addField("scope", "varchar", "500", "");
			table.addField("evaltype", "smallint", "100", "");
			table1.addField("id", "varchar", "100", "primary key");
			table1.addField("mymapid", "varchar", "500", "");
			table1.addField("evaltype", "smallint", "100", "");
			JdbcUtil.mysqlCreateTable(table, "MYISAM", "utf8",
					"if not exists ", "");
			JdbcUtil.mysqlCreateTable(table1, "MYISAM", "UTF8",
					"if not exists ", "");
		}
		if (this.POSTGRESQLDRIVERNAME.equals(conf.getDriver())) {
			table.addField("id", "varchar", "100", "primary key");
			table.addField("elementname", "varchar", "500", "");
			table.addField("allattributename", "varchar", "2000", "");
			table.addField("scope", "varchar", "500", "");
			table.addField("evaltype", "smallint", "", "");
			table1.addField("id", "varchar", "100", "primary key");
			table1.addField("mymapid", "varchar", "500", "");
			table1.addField("evaltype", "smallint", "", "");
			ResultSet result = stm
					.executeQuery("select * from pg_tables where tablename = '"
							+ table.getTableName() + "'");
			if (!result.next()) {
				JdbcUtil.postgresqlCreateTable(table);
			}
			result = stm
					.executeQuery("select * from pg_tables where tablename = '"
							+ table1.getTableName() + "'");
			if (!result.next()) {
				JdbcUtil.postgresqlCreateTable(table1);
			}
		}
		boolean flag = false;
		flag = this.createResolveTables(filelist);
		return flag;
	}

	@Override
	public PreparedStatement insertTable(PreparedStatement ps,
			List<ShpFieldVO> astFieldEle, Map<String, Object> EleRec)
			throws Exception {
		ShpConf conf = new ShpConf();
		conf.initProperties();
		if (this.MYSQLDRIVERNAME.equals(conf.getDriver())) {
			List<Object> list = new Vector<Object>();
			for (int j = 0; j < astFieldEle.size(); j++) {
				Object object = EleRec.get((astFieldEle.get(j)).getStrName());
				ps.setObject(j + 2, object);
				list.add(object);
			}
			ps.setString(1, UUID.randomUUID().toString());
			for (int t = 0; t < list.size(); t++) {
				ps.setObject(t + 2, list.get(t));
			}
			ps.setObject(list.size() + 2, EleRec.get("MYMAPID").toString());
			byte[] aa = (byte[]) EleRec.get("GEOM");
			byte[] bb = null;
			if (aa.length > 65534) {
				bb = Util.splitBytes(aa)[1];
				aa = Util.splitBytes(aa)[0];
			}

			InputStream in = new ByteArrayInputStream(aa);
			if (bb != null) {
				InputStream in1 = new ByteArrayInputStream(bb);
				ps.setBinaryStream(list.size() + 4, in1, bb.length);
				in1.close();
			} else {
				ps.setObject(list.size() + 4, null);
			}
			ps.setBinaryStream(list.size() + 3, in, aa.length);
			ps.addBatch();
			ps.executeBatch();
			in.close();
		} else if (this.ORACLEDRIVERNAME.equals(conf.getDriver())
				|| this.POSTGRESQLDRIVERNAME.equals(conf.getDriver())) {
			List<Object> list = new Vector<Object>();
			for (int j = 0; j < astFieldEle.size(); j++) {
				Object object = EleRec.get((astFieldEle.get(j)).getStrName());
				ps.setObject(j + 2, object);
				list.add(object);
			}
			ps.setString(1, UUID.randomUUID().toString());
			for (int t = 0; t < list.size(); t++) {
				ps.setObject(t + 2, list.get(t));
			}
			ps.setObject(list.size() + 2, EleRec.get("MYMAPID").toString());
			byte[] aa = (byte[]) EleRec.get("GEOM");
			byte[] bb = null;
			if (aa.length > 65534) {
				bb = Util.splitBytes(aa)[1];
				aa = Util.splitBytes(aa)[0];
			}
			InputStream in = new ByteArrayInputStream(aa);
			if (bb != null) {
				InputStream in1 = new ByteArrayInputStream(bb);
				ps.setBinaryStream(list.size() + 4, in1, bb.length);
				in1.close();
			} else {
				ps.setObject(list.size() + 4, null);
			}
			ps.setBinaryStream(list.size() + 3, in, aa.length);
			ps.addBatch();
			in.close();
		}
		// System.out.println("sdsd");
		if (EleRec.get("MAPID") != null || EleRec.get("MapID") != null
				|| EleRec.get("MESHID") != null || (EleRec.get("MYMAPID") != null && !EleRec.get("MYMAPID").equals("%"))) {
			Util.mymapidset.add(EleRec.get("MYMAPID").toString());
		}
		return ps;
	}

	/**
	 * 恢复主键索引
	 * 
	 * @param strTableName
	 *            String
	 * @param con
	 *            Connection
	 * @return Connection
	 */
	public Connection renewIndex(String strTableName, Connection con)
			throws Exception {
		ShpConf conf = new ShpConf();
		conf.initProperties();
		int flagx = 0;
		if (this.MYSQLDRIVERNAME.equals(conf.getDriver())) {
			flagx = 1;
		} else if (this.ORACLEDRIVERNAME.equals(conf.getDriver())) {
			flagx = 2;
		}
		if (flagx == 1) {
			PreparedStatement ps = con.prepareStatement("alter table "
					+ strTableName + " enable keys");
			ps.execute();
			ps.close();
		} else if (flagx == 2) {
			int x = Util.map.get(strTableName);
			Util.map.put(strTableName, x - 1);
			if (0 == Util.map.get(strTableName)) {
				Statement ss = con.createStatement();
				Util.operate(strTableName, "enable", ss);
			}
		}
		return con;
	}

	/**
	 * 解析表名
	 * 
	 * @param fileList
	 *            List
	 * @return boolean
	 * @throws Exception
	 */
	public boolean createResolveTables(List<File> fileList) throws Exception {
		// ShpConf conf = new ShpConf();
		// conf.initProperties();
		// String strInputpath = conf.getInputpath();
		boolean flag2 = false;
		for (File file : fileList) {
			if (!file.getName().endsWith("dbf")) {
				continue;
			}
			String strPath = file.getParent();
			// strPath = strPath.replace(strInputpath, "");
			String[] strCom = strPath.split("\\\\");
			int local = file.getName().lastIndexOf(".dbf");
			String strEleName = file.getName().substring(0, local);
			boolean flag1 = true;
			File xmlFile = new File("div.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);
			NodeList nl = doc.getElementsByTagName("level");
			if (nl.getLength() != 0) {
				// System.out.println(no.getNodeName());
				String strPassName = "";
				for (Node no = nl.item(0); no != null; no = no.getNextSibling()) {
					NodeList nll = no.getChildNodes();
					boolean flag = false;
					boolean flag3 = false;
					for (Node node = nll.item(0); node != null; node = node
							.getNextSibling()) {
						if (node.getNodeType() == Node.ELEMENT_NODE) {
							// System.out.println(node.getTextContent());
							if ("leveldiv_name".equals(node.getNodeName())) {
								String strleveldiv = node.getTextContent();
								if (strPath.contains(strleveldiv)
										|| strleveldiv.equals("")) {
									flag = true;
								}
							}
							if ("passname_parentdiv".equals(node.getNodeName())
									&& flag
									&& (strPath.contains("\\"
											+ node.getTextContent() + "\\") || strPath
											.contains(node.getTextContent()
													+ ":\\"))) {
								strPassName = node.getTextContent();
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
							String strTableName = strEleName.replace(
									strCom[i + 1], "");
							System.out.println(strTableName);
							flag2 = this.resolveTable(strTableName, file);
							flag1 = false;
							break;
						}
					}
				}
			}
			if (flag1) {
				System.out.println(strEleName);
				flag2 = this.resolveTable(strEleName, file);
			}
		}
		return flag2;
	}

	/**
	 * 解析表的字段
	 * 
	 * @param strTableName
	 *            String
	 * @param file
	 *            File
	 * @return boolean
	 */
	public boolean resolveTable(String strTableName, File file) {
		boolean flag = false;
		Vector<String> list2 = new Vector<String>();
		Table table = new Table();
		table.setTableName(strTableName);
		try {
			DataInputStream inData = new DataInputStream(
					new BufferedInputStream(new FileInputStream(file)));
			byte[] head = new byte[ConstantValue.HEAD_LENGTH];
			inData.read(head);
			/** 文件头的长度 */
			byte[] headLength = Arrays.copyOfRange(head, 8, 10);
			int iHeadLength = Util.convertToShort(headLength,
					headLength.length, false);
			/** 读取记录项名 */
			byte[] recordName = new byte[iHeadLength - 32];
			int countNum = (iHeadLength - 32) / 32;
			inData.read(recordName);
			byte[] name = new byte[18];
			if (Util.tableFieldmap.get(strTableName.toUpperCase()) == null) {
				Connection con = JdbcUtil.getConnection();
				Statement stm = con.createStatement();
				ShpConf conf = new ShpConf();
				conf.initProperties();
				String strURL = conf.getDatabaseURL();
				if (this.MYSQLDRIVERNAME.equals(conf.getDriver())) {
					int index = strURL.lastIndexOf("/");
					String strTablespace = strURL.substring(index + 1, strURL
							.length());
					String sql = "show tables where Tables_in_" + strTablespace
							+ "='" + strTableName + "'";
					ResultSet res = stm.executeQuery(sql);
					if (res.next()) {
						Util.tableFieldmap.put(strTableName.toUpperCase(), -1);
						return false;
					}
				} else if (this.ORACLEDRIVERNAME.equals(conf.getDriver())) {
					int index = strURL.lastIndexOf(":");
					String strTablespace = strURL.substring(index + 1, strURL
							.length());
					String sql = "select * from user_tables where table_name ='"
							+ strTableName.toUpperCase() + "'";
					ResultSet res = stm.executeQuery(sql);
					if (res.next()) {
						Util.tableFieldmap.put(strTableName.toUpperCase(), -1);
						return false;
					}
				} else if (this.POSTGRESQLDRIVERNAME.equals(conf.getDriver())) {
					int index = strURL.lastIndexOf(":");
					String strTablespace = strURL.substring(index + 1, strURL
							.length());
					String sql = "select * from pg_tables where tablename ='"
							+ strTableName.toLowerCase() + "'";
					ResultSet res = stm.executeQuery(sql);
					if (res.next()) {
						Util.tableFieldmap.put(strTableName.toUpperCase(), -1);
						return false;
					}
				}
				Util.tableFieldmap
						.put(strTableName.toUpperCase(), countNum + 4);
			} else {
				if (Util.tableFieldmap.get(strTableName.toUpperCase()) == -1
						|| Util.tableFieldmap.get(strTableName.toUpperCase()) >= (countNum + 4)) {
					return false;
				}
				// Util.tableFieldmap.put(key, value)
			}
			for (int i = 0; i < countNum; i++) {
				name = Arrays.copyOfRange(recordName, i * 32, i * 32 + 18);
				String fieldName = new String(Arrays.copyOfRange(name, 0, 11));
				ShpConf conf = new ShpConf();
				conf.initProperties();
				String fieldType = "";
				if (this.MYSQLDRIVERNAME.equals(conf.getDriver())) {
					if (i == 0) {
						table.addField("GID", "VARCHAR", "256", "not null");
						table.addField("MYMAPID", "VARCHAR", "64", "");
						table.addField("GEOM", "BLOB", "65535", "");
						table.addField("GEOM1", "BLOB", "65535", "");
					}
					fieldType = Util.mySqlfieldType(name[11]);
				} else if (this.ORACLEDRIVERNAME.equals(conf.getDriver())) {
					if (i == 0) {
						table.addField("GID", "VARCHAR2", "256", "not null");
						table.addField("MYMAPID", "VARCHAR2", "64", "");
						table.addField("GEOM", "BLOB", "", "");
						table.addField("GEOM1", "BLOB", "", "");
					}
					fieldType = Util.oraclefieldType(name[11]);
				} else if (this.POSTGRESQLDRIVERNAME.equals(conf.getDriver())) {
					if (i == 0) {
						table.addField("GID", "VARCHAR", "256", "not null");
						table.addField("MYMAPID", "VARCHAR", "64", "");
						table.addField("GEOM", "BYTEA", "", "");
						table.addField("GEOM1", "BYTEA", "", "");
					}
					fieldType = Util.postgresqlfieldType(name[11]);
				}
				if(name[16] != 0){
					fieldType = fieldType + "(" + name[16] + ")";
					if(name[17] != 0){
						fieldType = fieldType.substring(0, fieldType.length() - 1) + "," + name[17] + ")";
					}
				}
				byte[] fieldlength = Arrays.copyOfRange(name, 16, 17);
				int fieldlengthint = fieldlength[0];
				if (fieldlength[0] < 0) {
					fieldlengthint = fieldlength[0] + 256;
				}
				fieldName = fieldName.trim();
				for (String string : list2) {
					if (string.equals((fieldName))) {
						Util.tableFieldmap.remove(strTableName.toUpperCase());
						File file1 = new File("error.txt");
						file1.createNewFile();
						FileWriter out = new FileWriter(file1, true);
						out.write("文件" + strTableName + "不能建表\r");
						out.close();
						return false;
					}
				}
				list2.add(fieldName);
				table.addField(fieldName, fieldType, fieldlengthint + "", "");
			}
			inData.close();
			String scpoe = "Taskdata";
			if (file.getPath().contains("china")
					|| file.getPath().contains("China")) {
				scpoe = "Country";
			}
			flag = this.create(table, list2, scpoe);
		} catch (Exception e) {
			System.out.println("解析，存储时错误");
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 根据需求建表
	 * 
	 * @param table
	 *            Table
	 * @return boolean
	 * @throws Exception
	 */
	public boolean create(Table table, List<String> list, String scpoe)
			throws Exception {
		boolean flag = false;
		ShpConf conf = new ShpConf();
		conf.initProperties();
		String strDriver = conf.getDriver();
		Connection con = JdbcUtil.getConnection();
		// con.setAutoCommit(false);
		Statement stm = con.createStatement();
		String allattname = "";
		for (String string : list) {
			allattname = allattname + string + ",";
		}
		allattname = allattname.substring(0, allattname.length() - 1);
		if (this.MYSQLDRIVERNAME.equals(strDriver)) {
			// String sql3 = "show index from " +
			// table.getTableName().toUpperCase()
			// + " where key_name = '" + table.getTableName().toUpperCase() +
			// "_MYMAPID_IDEX" + "'";
			// boolean flag1 = JdbcUtil.mysqlCreateTable(table, "MyISAM",
			// "utf8", "IF NOT EXISTS ", "GID");
			// ResultSet rs1 = stm.executeQuery("select * from " +
			// table.getTableName().toUpperCase() + " limit 1");
			// ResultSetMetaData rsmd = rs1.getMetaData();
			// int columnNum = rsmd.getColumnCount();
			// if(columnNum < table.getField().size()){
			stm.execute("drop table IF EXISTS "
					+ table.getTableName().toUpperCase());
			boolean flag1 = JdbcUtil.mysqlCreateTable(table, "MyISAM", "utf8",
					"", "");
			// boolean flag2 =
			// JdbcUtil.createIndex(table.getTableName().toUpperCase()
			// +"_MYMAPID_IDEX ",
			// "mymapid", "", table.getTableName().toUpperCase());
			flag = flag1;
			String[] strallttnamescope = new String[] { allattname, scpoe };
			Util.element.put(table.getTableName().toUpperCase(),
					strallttnamescope);
			// String insertsql =
			// "insert into ELEMENT(elementname,allattributename,scope)
			// values('"
			// + table.getTableName() + "','" + allattname + "'," +
			// "'Country')";
			// try {
			// stm.execute(insertsql);
			// } catch (Exception e) {
			// System.out.println("主键重复");
			// }

			// ResultSet rs = stm.executeQuery(sql3);
			// if(rs.next()){
			// boolean flag2 = JdbcUtil.createIndex(
			// table.getTableName().toUpperCase() + "_MYMAPID_IDEX", "MYMAPID",
			// "", table.getTableName());
			// }
		} else if (this.ORACLEDRIVERNAME.equals(strDriver)) {
			// for (String string : vector) {
			// tableField = tableField + string + ",";
			// }
			// String sql ="CREATE TABLE " + table.getTableName().toUpperCase()
			// + "(GID varchar2(255) not null,"
			// + tableField + "MYMAPID varchar2(64),GEOM varchar2(64)" + ")";
			// String sql1 = "alter table " +table.getTableName().toUpperCase()
			// + " add primary key (GID) using index ";
			// String sql2 = "create index "+ table.getTableName().toUpperCase()
			// +"_MYMAPID_IDEX on "+
			// table.getTableName().toUpperCase() +" (MYMAPID)";
			ResultSet result = stm
					.executeQuery("select * from user_tables where table_name = '"
							+ table.getTableName().toUpperCase() + "'");

			if (result.next()) {
				// boolean flag1 = stm.execute(sql);
				boolean flag3 = stm.execute("drop table "
						+ table.getTableName().toUpperCase());
			}
			boolean flag1 = JdbcUtil.oracleCreateTable(table);
			// boolean flag3 = JdbcUtil.createIndex(table.getTableName() +
			// "_MYMAPID_IDEX ",
			// "MYMAPID", "", table.getTableName());
			// boolean flag2 = stm.execute(sql1);
			flag = flag1;
			String[] strallttnamescope = new String[] { allattname, scpoe };
			Util.element.put(table.getTableName().toUpperCase(),
					strallttnamescope);
			// String insertsql = "insert into
			// ELEMENT(elementname,allattributename,scope)" +
			// " values('" + table.getTableName() + "','" + allattname + "'," +
			// "'Country')";
			// try {
			// stm.execute(insertsql);
			// } catch (Exception e) {
			// System.out.println("主键重复");
			// }
			// else{
			// boolean flag1 = JdbcUtil.oracleCreateTable(table);
			// boolean flag2 = stm.execute(sql1);
			// // boolean flag3 = stm.execute(sql2);
			// boolean flag3 = JdbcUtil.createIndex(
			// table.getTableName().toUpperCase() + "_MYMAPID_IDEX", "MYMAPID",
			// "", table.getTableName());
			// flag = flag1 && flag2 && flag3;
			// ResultSet rs1 = stm.executeQuery(
			// "select * from " + table.getTableName().toUpperCase() + " where
			// rownum = 1");
			// ResultSetMetaData rsmd = rs1.getMetaData();
			// // int columnNum = rsmd.getColumnCount();
			// // if(columnNum < table.getField().size()){
			// stm.execute("drop table " + table.getTableName().toUpperCase());
			// // System.out.println(sql2);
			// // boolean flag1 = stm.execute(sql);
			// // boolean flag1 = JdbcUtil.oracleCreateTable(table);
			// // boolean flag2 = stm.execute(sql1);
			// //// boolean flag3 = stm.execute(sql2);
			// // boolean flag3 = JdbcUtil.createIndex(
			// // table.getTableName().toUpperCase() + "_MYMAPID_IDEX",
			// "MYMAPID",
			// // "", table.getTableName());
			// flag = flag1 && flag2 && flag3;
			// }
			// // }
		} else if (this.POSTGRESQLDRIVERNAME.equals(conf.getDriver())) {
			ResultSet result = stm
					.executeQuery("select * from pg_tables where tablename = '"
							+ table.getTableName() + "'");
			if (result.next() == true) {
				stm.execute("drop table " + table.getTableName());
			}
			boolean flag1 = JdbcUtil.postgresqlCreateTable(table);
			flag = flag1;
			String[] strallttnamescope = new String[] { allattname, scpoe };
			Util.element.put(table.getTableName().toUpperCase(),
					strallttnamescope);
			// String insertsql = "insert into
			// ELEMENT(elementname,allattributename,scope)" +
			// " values('" + table.getTableName() + "','" + allattname + "'," +
			// "'Country')";
			// try {
			// stm.execute(insertsql);
			// } catch (Exception e) {
			// System.out.println("主键重复");
			// }

		}
		// con.commit();
		stm.close();
		con.close();
		return flag;
	}

	@Override
	public boolean createTable1(List<Table> creatMap) throws Exception {
		// TODO Auto-generated method stub
		boolean flag = true;
		boolean flag1 = false;
		flag1 = this.createElemenAndMapDataTable();
		for (Table table : creatMap) {
			boolean flag2 = this.createTab(table);
			flag = flag && flag2;
		}
		flag = flag && flag1;
		return flag;
	}

	public boolean createElemenAndMapDataTable() throws Exception {
		boolean flag = false;
		boolean flag1 = false;
		boolean flag2 = false;
		Table elemneTable = new Table();
		elemneTable.setTableName("element");
		Table mapdaTable = new Table();
		mapdaTable.setTableName("mapdata");
		ShpConf conf = new ShpConf();
		try {
			if (this.ORACLEDRIVERNAME.equals(conf.getDriver())) {
				elemneTable.addField("id", "varchar2", "500", "primary key");
				elemneTable.addField("elementname", "varchar2", "500", "");
				elemneTable
						.addField("allattributename", "varchar2", "2000", "");
				elemneTable.addField("scope", "varchar2", "500", "");
				elemneTable.addField("evaltype", "number", "38", "");
				mapdaTable.addField("id", "varchar2", "500", "primary key");
				mapdaTable.addField("mymapid", "varchar2", "500", "");
				mapdaTable.addField("evaltype", "number", "38", "");
				if (!JdbcUtil.OracleTableISExists(elemneTable.getTableName())) {
					JdbcUtil.oracleCreateTable(elemneTable);
				}
				if (JdbcUtil.OracleTableISExists(elemneTable.getTableName())) {
					flag1 = true;
				}
				if (!JdbcUtil.OracleTableISExists(mapdaTable.getTableName()
						.toUpperCase())) {
					JdbcUtil.oracleCreateTable(mapdaTable);
				}
				if (JdbcUtil.OracleTableISExists(mapdaTable.getTableName()
						.toUpperCase())) {
					flag2 = true;
				}
				flag = flag1 && flag2;
			}
			if (this.MYSQLDRIVERNAME.equals(conf.getDriver())) {
				elemneTable.addField("id", "varchar", "100", "primary key");
				elemneTable.addField("elementname", "varchar", "100", "");
				elemneTable.addField("allattributename", "varchar", "8000", "");
				elemneTable.addField("scope", "varchar", "500", "");
				elemneTable.addField("evaltype", "smallint", "100", "");
				mapdaTable.addField("id", "varchar", "100", "primary key");
				mapdaTable.addField("mymapid", "varchar", "500", "");
				mapdaTable.addField("evaltype", "smallint", "100", "");
				JdbcUtil.mysqlCreateTable(elemneTable, "MYISAM", "utf8",
						"if not exists ", "");
				JdbcUtil.mysqlCreateTable(mapdaTable, "MYISAM", "UTF8",
						"if not exists ", "");
				if (JdbcUtil.MySqlTableISExists(elemneTable.getTableName())
						&& JdbcUtil.MySqlTableISExists(mapdaTable
								.getTableName())) {
					flag = true;
				}

			}
			if (this.POSTGRESQLDRIVERNAME.equals(conf.getDriver())) {
				elemneTable.addField("id", "varchar", "100", "primary key");
				elemneTable.addField("elementname", "varchar", "500", "");
				elemneTable.addField("allattributename", "varchar", "2000", "");
				elemneTable.addField("scope", "varchar", "500", "");
				elemneTable.addField("evaltype", "smallint", "", "");
				mapdaTable.addField("id", "varchar", "100", "primary key");
				mapdaTable.addField("mymapid", "varchar", "500", "");
				mapdaTable.addField("evaltype", "smallint", "", "");
				if (!JdbcUtil.PostGresqlTableISExists(elemneTable
						.getTableName())) {
					JdbcUtil.postgresqlCreateTable(elemneTable);
				}
				if (JdbcUtil
						.PostGresqlTableISExists(elemneTable.getTableName())) {
					flag1 = true;
				}
				if (!JdbcUtil
						.PostGresqlTableISExists(mapdaTable.getTableName())) {
					JdbcUtil.postgresqlCreateTable(mapdaTable);
				}
				if (JdbcUtil.PostGresqlTableISExists(mapdaTable.getTableName())) {
					flag2 = true;
				}
				flag = flag1 && flag2;

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			flag = false;
			throw e;
		}
		return flag;
	}

	public boolean createTab(Table table) throws Exception {
		boolean flag = false;
		try {
			ShpConf conf = new ShpConf();
			String strDriver = conf.getDriver();
			Connection con = JdbcUtil.getConnection();
			Statement stm = con.createStatement();
			String indexlastname = "_geom_index";
			if (this.MYSQLDRIVERNAME.equals(strDriver)) {
				// stm.execute("drop table IF EXISTS "
				// + table.getTableName().toUpperCase());
				if (!JdbcUtil.MySqlTableISExists(table.getTableName())) {
					JdbcUtil.mysqlCreateTable(table, "MyISAM", "utf8", "", "");
				}
				if(!JdbcUtil.mySqlIndexIsExist(table.getTableName(), table.getTableName() + indexlastname)){
					JdbcUtil.createMySqlGeomIndex(table.getTableName(), table.getTableName() + indexlastname);
				}
				if (JdbcUtil.MySqlTableISExists(table.getTableName())) {
					flag = true;
				}
			} else if (this.ORACLEDRIVERNAME.equals(strDriver)) {
				// if (JdbcUtil.OracleTableISExists(table.getTableName()
				// )) {
				// boolean flag3 = stm.execute("drop table "
				// + table.getTableName().toUpperCase());
				// }
				if (!JdbcUtil.OracleTableISExists(table.getTableName()
						.toUpperCase())) {
					JdbcUtil.oracleCreateTable(table);
				}
				if(Util.isExistField(table.getTableName(), "geom") && !JdbcUtil.checkOracleSDOGEOMMEDATAisExist(table.getTableName(), "geom")){
					JdbcUtil.executeSql(JdbcUtil.getInsertCoordinateAreaSql(table.getTableName(), "geom"));
				}
				if(Util.isExistField(table.getTableName(), "geom") &&
						!JdbcUtil.oracleIndexIsExist(table.getTableName(), table.getTableName() + indexlastname)){
					JdbcUtil.executeSql(JdbcUtil.createOracleGeomitryIndexSql(table.getTableName(), table.getTableName() + indexlastname));
				}
				if (JdbcUtil.OracleTableISExists(table.getTableName()
						.toUpperCase())) {
					flag = true;
				}
			} else if (this.POSTGRESQLDRIVERNAME.equals(conf.getDriver())) {
				// ResultSet result = stm
				// .executeQuery("select * from pg_tables where tablename = '"
				// + table.getTableName() + "'");
				// if (result.next() == true) {
				// stm.execute("drop table " + table.getTableName());
				// }
				if (!JdbcUtil.PostGresqlTableISExists(table.getTableName())) {
					JdbcUtil.postgresqlCreateTable(table);
					if(table.getRecordNum() /10000 >500){
						
					}
				}
				if(!JdbcUtil.postgreIndexIsExist(table.getTableName(), table.getTableName() + indexlastname)
						&& Util.isExistField(table.getTableName(), "geom")){
					JdbcUtil.executeSql(JdbcUtil.createPostGISGeomIndex(table.getTableName(), "geom"));
				}
				if (JdbcUtil.PostGresqlTableISExists(table.getTableName())) {
					flag = true;
				}
			}
			stm.close();
			con.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			flag = false;
			throw e;
		}
		return flag;
	}
	
	public void insertDataBatch(DataConnectionTool dataConnectionTool,Map<String, Object> EleRec, String tableName, String datatype) throws Exception{
		List<Table> list = Util.createtableList;
		if(createTaMap.size() == 0){
			for (Table table : list) {
				createTaMap.put(table.getTableName(), table);
			}
		}
//		try {
//			
		Table table = createTaMap.get(tableName);
		String importTablename = tableName;
		if(table == null){
			importTablename = tableName.substring(0, tableName.lastIndexOf("_"));
			table = createTaMap.get(importTablename);
		}
		List<String[]> fieldList = table.getField();
		if(Util.element.get(importTablename)[0].contains("geom") 
				&& Util.ORACLEDRIVERNAME.equals(new ShpConf().getDriver())){
					oracleInsertGeom(dataConnectionTool,EleRec,tableName,datatype,fieldList);
		}else {
		Connection connection = dataConnectionTool.getConnection();
		connection.setAutoCommit(false);
		Statement statement = dataConnectionTool.returncurStatement();
		String fieldName = "";
		String value = "";
		String geom1 = "";
		String geom2 = "";
		for (String[] strings : fieldList) {
			if("".equals(fieldName) && "".equals(value)){
				fieldName = strings[0];
				if("GEOM".equals(strings[0].toUpperCase())){
					String dataString = EleRec.get("GEOM").toString();
					value = Util.isGeom(dataString, datatype);
				}else if(strings[1].toLowerCase().equals("varchar") || strings[1].toLowerCase().equals("varchar2")){
					if(EleRec.get(strings[0].toUpperCase()) == null || "".equals(EleRec.get(strings[0].toUpperCase()))){
						value = "null";
					}else {
						StringBuffer stringBuffer = new StringBuffer(value);
						stringBuffer.append("'");
						stringBuffer.append(EleRec.get(strings[0].toUpperCase()).toString().replace("'", "‘"));
						stringBuffer.append("'");
						value = stringBuffer.toString();
					}
				}else if("GEOM1".equals(strings[0].toUpperCase())){
					String dataString = EleRec.get("GEOM").toString();
					dataString = dataString.replaceAll("\\(", "").replaceAll("\\)", "");
//					if(dataString.length() > 65535){
//						int index = dataString.lastIndexOf(",");
//						geom1 = dataString.substring(0, index);
//						geom2 = dataString.substring(index);
//					}else {
						geom1 = dataString;
//					}
//					value = "'" + geom1 + "'";
					StringBuffer stringBuffer = new StringBuffer(value);
					stringBuffer.append("'");
					stringBuffer.append(geom1);
					stringBuffer.append("'");
					value = stringBuffer.toString();
				}else if("GEOM2".equals(strings[0].toUpperCase())){
					if(!"".equals(geom2)){
//						value = "'" + geom2 + "'";
						StringBuffer stringBuffer = new StringBuffer(value);
						stringBuffer.append("'");
						stringBuffer.append(geom2);
						stringBuffer.append("'");
						value = stringBuffer.toString();
					}else {
						value = "null";
					}
				}else {
					value = EleRec.get(strings[0].toUpperCase()) + "";
				}
			}else {
				fieldName = fieldName + "," + strings[0];
				if("GEOM".equals(strings[0].toUpperCase())){
					String dataString = EleRec.get("GEOM") + "";
					StringBuffer stringBuffer = new StringBuffer(value);
					stringBuffer.append(",");
					stringBuffer.append( Util.isGeom(dataString, datatype));
//					stringBuffer.append("'");
					value = stringBuffer.toString();
				}else if(strings[1].toLowerCase().equals("varchar") || strings[1].toLowerCase().equals("varchar2")){
					if(EleRec.get(strings[0].toUpperCase()) == null || "".equals(EleRec.get(strings[0].toUpperCase()))){
						value = value + ",null";
					}else {
						StringBuffer stringBuffer = new StringBuffer(value);
						stringBuffer.append(",'");
						stringBuffer.append(EleRec.get(strings[0].toUpperCase()).toString().replace("'", "‘"));
						stringBuffer.append("'");
						value = stringBuffer.toString();
//							value + "," + "'" + EleRec.get(strings[0].toUpperCase()).toString().replace("'", "‘") + "'";
					}
				}else if("GEOM1".equals(strings[0].toUpperCase())){
					String dataString = EleRec.get("GEOM") + "";
					dataString = dataString.replaceAll("\\(", "").replaceAll("\\)", "");
//					if(dataString.length() > 65535){
//						int index = dataString.lastIndexOf(",");
//						geom1 = dataString.substring(0, index);
//						geom2 = dataString.substring(index);
//					}else {
						if("".equals(dataString)){
							geom1 = "null";
						}
						geom1 = dataString;
//					}
					StringBuffer stringBuffer = new StringBuffer(value);
					stringBuffer.append(",'");
					stringBuffer.append(geom1);
					stringBuffer.append("'");
					value = stringBuffer.toString();
				}else if("GEOM2".equals(strings[0].toUpperCase())){
					if(!"".equals(geom2)){
						StringBuffer stringBuffer = new StringBuffer(value);
						stringBuffer.append(",'");
						stringBuffer.append(geom2);
						stringBuffer.append("'");
						value = stringBuffer.toString();
					}else {
						StringBuffer stringBuffer = new StringBuffer(value);
						stringBuffer.append(",null");
						value = stringBuffer.toString();
					}
				}else {
					StringBuffer stringBuffer = new StringBuffer(value);
					stringBuffer.append(",");
					stringBuffer.append(EleRec.get(strings[0].toUpperCase()));
					value = stringBuffer.toString();
				}
			}
//			if("MAPID".equals(strings[0].toUpperCase())
//					|| "MESHID".equals(strings[0].toUpperCase())){
				if (EleRec.get("MAPID") != null || EleRec.get("MapID") != null
						|| EleRec.get("MESHID") != null || (EleRec.get("MYMAPID") != null && !EleRec.get("MYMAPID").equals("%"))) {
					Util.mymapidset.add(EleRec.get("MYMAPID").toString());
				}
//			}
		}
//		String sql = "insert into " + tableName + "("+ fieldName +")" + " values(" + value + ")";
		String sql = "";
		StringBuffer stringBuffer = new StringBuffer(sql);
		stringBuffer.append("insert into ");
		stringBuffer.append(tableName);
		stringBuffer.append("(");
		stringBuffer.append(fieldName);
		stringBuffer.append(")");
		stringBuffer.append(" values(");
		stringBuffer.append(value);
		stringBuffer.append(")");
		sql = stringBuffer.toString();
		statement.addBatch(sql);
		dataConnectionTool.setCount(dataConnectionTool.getCount() + 1);
		if(dataConnectionTool.getCount()%100 == 0 && dataConnectionTool.getCount() >= 100){
//			long l = System.currentTimeMillis();
			try {
				statement.executeBatch();
				statement.clearBatch();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			connection.commit();
//			System.out.println(System.currentTimeMillis() - l);
		}else if (dataConnectionTool.CountEqMax()) {
			try {
				statement.executeBatch();
				statement.clearBatch();
				connection.commit();
			} catch (BatchUpdateException e) {
				// TODO: handle exception
				if(e.getMessage().contains("Field 'geom' doesn't have a default value")){
					if(errorString == null || !errorString.equals("由于表"+ tableName + "的字段geom不能空" + ",因此文件导入失败！")){
						errorString = "由于表"+ tableName + "的字段geom不能空" + ",因此文件导入失败！";
						Form.setTextCon(errorString);
						System.out.println(tableName + " " + e.getMessage());
					}
				}else {
					throw e;
				}
			}finally{
				dataConnectionTool.closeStatement();
				dataConnectionTool.closeConnection();
			}
		}
		}
//		} catch (Exception e) {
//			e.printStackTrace();
//			// TODO: handle exception
//		}
	}

	private void oracleInsertGeom(DataConnectionTool dataConnectionTool,Map<String, Object> EleRec, String tableName, String datatype,List<String[]> fieldList) throws Exception {
		// TODO Auto-generated method stub
		String fieldName = "";
		String markString = "";
		List<Object> list = new ArrayList<Object>();
		int geom1state = -1;
		for (String[] strings : fieldList) {
				fieldName = fieldName + strings[0] + "," ;
				markString = markString + "?,";
				if("GEOM".equals(strings[0].toUpperCase())){
					String dataString = EleRec.get("GEOM") + "";
					JGeometry geometry = Util.isOracleGeometry(dataString, datatype);
					try {
						list.add(JGeometry.store(geometry, dataConnectionTool.getConnection()));
						
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}else if(strings[1].toLowerCase().equals("varchar") || strings[1].toLowerCase().equals("varchar2")){
					if(EleRec.get(strings[0].toUpperCase()) == null){
						String value = null;
						list.add(value);
					}else {
						String value = EleRec.get(strings[0].toUpperCase()).toString().replace("'", "‘");
						list.add(value);
					}
				}else if("GEOM1".equals(strings[0].toUpperCase())){
					String dataString = EleRec.get("GEOM") + "";
//					JGeometry geometry = Util.isOracleGeometry(dataString, datatype);
					try {
//						list.add(JGeometry.store(geometry, dataConnectionTool.getConnection()));
						list.add(dataString.replaceAll("\\(", "").replaceAll("\\)", ""));
						geom1state = list.size() - 1;
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}else {
					String value = EleRec.get(strings[0].toUpperCase()) + "";
					list.add(value);
				}
			if("MAPID".equals(strings[0].toUpperCase())){
				if (EleRec.get("MAPID") != null || EleRec.get("MapID") != null) {
					Util.mymapidset.add(EleRec.get("MYMAPID").toString());
				}
			}
		}
		fieldName = fieldName.substring(0, fieldName.length() - 1);
		markString = markString.substring(0, markString.length() - 1);
		if(dataConnectionTool.getPreparedStatement() == null){
			String sql = "insert into " + tableName + "("+ fieldName +")" + " values(" + markString + ")";
			dataConnectionTool.openNewPrePareStatement(sql);
		}
		Connection connection = dataConnectionTool.getConnection();
		PreparedStatement preparedStatement = dataConnectionTool.getPreparedStatement();
		for (int i = 0; i < list.size(); i++) {
			if(i == geom1state){
				String clobString = list.get(i) + "";
				Reader cReader = new StringReader(clobString);
				preparedStatement.setCharacterStream(i, cReader, clobString.length() + 1);
			}else {
				preparedStatement.setObject(i + 1, list.get(i));
			}
		}
		preparedStatement.addBatch();
		dataConnectionTool.setCount(dataConnectionTool.getCount() + 1);
		if(dataConnectionTool.getCount()%1000 == 0 && dataConnectionTool.getCount() > 1000){
			try {
				preparedStatement.executeBatch();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			connection.commit();
		}else if (dataConnectionTool.CountEqMax()) {
			try {
				preparedStatement.executeBatch();
				connection.commit();
			} catch (BatchUpdateException e) {
				// TODO: handle exception
				if(e.getMessage().contains("Field 'geom' doesn't have a default value")){
					if(errorString == null || !errorString.equals("由于表"+ tableName + "的字段geom不能空" + ",因此文件导入失败！")){
						errorString = "由于表"+ tableName + "的字段geom不能空" + ",因此文件导入失败！";
						Form.setTextCon(errorString);
						System.out.println(tableName + " " + e.getMessage());
					}
				}else {
					throw e;
				}
			}finally{
				dataConnectionTool.closeAllTool();
			}
	}
	}
	
//public static void main(String[] args) {
//	try {
//		DataConnectionTool aConnectionTool = new DataConnectionTool();
//		aConnectionTool.setCount(20);
//		new ShpTableDaoImpl().a(aConnectionTool);
//		System.out.println(aConnectionTool.getCount());
//	} catch (Exception e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	
//}
//
//public void a(DataConnectionTool aConnectionTool){
//	int a = aConnectionTool.getCount() + 1;
//	aConnectionTool.setCount(a);
//}
}
