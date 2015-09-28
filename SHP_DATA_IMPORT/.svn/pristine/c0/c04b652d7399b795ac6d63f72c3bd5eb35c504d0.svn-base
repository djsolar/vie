package com.sunmap.shpdata.tools.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.naming.NamingException;
import javax.xml.parsers.ParserConfigurationException;

import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

import org.xml.sax.SAXException;

import com.sunmap.shpdata.tools.conf.ShpConf;
import com.sunmap.shpdata.tools.vo.Table;

import form.f;


public class JdbcUtil {
//	private static ShpConf conf = null;
	
	public static final double minLongitude = 28.000000000;	//最小的经度值
	
	public static final double maxLongitude = 156.000000000;	//最大的经度值
	
	public static final double minLatitude = 1.333333333; //最小纬度值
	
	public static final double maxLatitude = 86.666666667; //最大纬度值
	
	public static final double coordinatePrecision = 0.000000050;
	
	private static final int xChangeValue = 60 * 60 * 2560; //一度包含的1/2560秒数。
	private static final int yChangeValue = 60 * 2560;	//	一分所包含的1/2560秒数。
//	static{
//		conf = new ShpConf();
//		try {
//			conf.initProperties();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	public static ShpConf getConf(){
		ShpConf conf = new ShpConf();
		return conf;
		
	}
	/**
	 * 获得数据库连接
	 * @return Connection
	 * @throws Exception 
	 */
	public static Connection getConnection() throws Exception{
//		Class.forName("oracle.jdbc.driver.OracleDriver");
//		String url = "jdbc:oracle:thin:@192.168.6.249:1521:swshp";
//		String username = "shp";
//		String password = "shp123";
//		getConf();
		ShpConf conf = new ShpConf();
		Class.forName(conf.getDriver());
		String url = conf.getDatabaseURL();
		String username = conf.getUsername();
		String password = conf.getPassword();
		Connection con = DriverManager.getConnection(url, username, password);
		return con;
	}
	/**
	 * 对mysql数据库建表
	 * @param strTableName
	 * @param allFieldName
	 * @param tableType
	 * @param charset
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean mysqlCreateTable(Table table,
			String tableType, String charset, 
			String strIFEXISTS, String primaryKey) throws Exception{
		String strPrimary = "";
		if(primaryKey != null && !primaryKey.equals("")){
			strPrimary = "PRIMARY KEY (" + primaryKey + ")";
		}
		List<String[]> list = table.getField();
		String strFieldName = "";
		for (String[] strings : list) {
			if("BLOB".equalsIgnoreCase(strings[1])|| "text".equalsIgnoreCase(strings[1]) 
					|| "CLOB".equalsIgnoreCase(strings[1]) 
					|| "int".equalsIgnoreCase(strings[1])
					|| "geometry".equalsIgnoreCase(strings[1])){
				strFieldName =  strFieldName + strings[0] + " " + strings[1] + " "  
				                                                          + strings[3] + ",";	
			}else{
				strFieldName =  strFieldName + strings[0] + " " + strings[1] + "(" + strings[2] + ") " 
				+ strings[3] + ",";
			}
			if(strings[0] == null || strings[1] == null
					|| strings[2] == null){
				return false;
			}
		}
		if(strPrimary == ""){
			strFieldName = strFieldName.substring(0, strFieldName.length() - 1);
		}
		String sql = "create table " + strIFEXISTS + " " + table.getTableName().toUpperCase() + "(" 
		+ strFieldName + strPrimary + ")ENGINE=" + tableType + " DEFAULT CHARSET=" + charset;
		Connection con = JdbcUtil.getConnection();
		con.setAutoCommit(false);
		Statement stm = con.createStatement();
		boolean flag = false;
		try {
			flag = stm.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		con.commit();
		stm.close();
		con.close();
		return flag;

	}
	/**
	 * 对oracle数据库建表
	 * @param strTableName
	 * @param allFieldName
	 * @param tableType
	 * @param charset
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean oracleCreateTable(Table table) throws Exception{
		List<String[]> list = table.getField();
		String strFieldName = "";
		for (String[] strings : list) {
			if("BLOB".equalsIgnoreCase(strings[1]) || "CLOB".equalsIgnoreCase(strings[1]) 
					|| "NUMBER".equalsIgnoreCase(strings[1])
					|| "MDSYS.SDO_GEOMETRY".equalsIgnoreCase(strings[1])){
				strFieldName =  strFieldName + strings[0] + " " + strings[1] +" "  
				                                                          + strings[3] + ",";	
			}else{
				strFieldName =  strFieldName + strings[0] + " " + strings[1] + "(" + strings[2] + ") " 
				+ strings[3] + ",";
			}
			if(strings[0] == null || strings[1] == null
					|| strings[2] == null){
				return false;
			}
		}
		strFieldName = strFieldName.substring(0, strFieldName.length() - 1);
		String sql = "create table " + table.getTableName().toUpperCase() + "(" 
		+ strFieldName + ")";
		Connection con = JdbcUtil.getConnection();
		con.setAutoCommit(false);
		Statement stm = con.createStatement();
		boolean flag = false;
		try {
			flag = stm.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		con.commit();
		stm.close();
		con.close();
		return flag;
	}
	/**
	 * 对PostgreSql数据库建表
	 * @param strTableName
	 * @param allFieldName
	 * @param tableType
	 * @param charset
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean postgresqlCreateTable(Table table) throws Exception{
		List<String[]> list = table.getField();
		String strFieldName = "";
		for (String[] strings : list) {
			if("BYTEA".equalsIgnoreCase(strings[1])
					|| "bigint".equalsIgnoreCase(strings[1])
					|| "smallint".equalsIgnoreCase(strings[1])
					|| "integer".equalsIgnoreCase(strings[1])
					|| "geometry".equalsIgnoreCase(strings[1])
					|| "text".equalsIgnoreCase(strings[1].toLowerCase())){
				strFieldName =  strFieldName + strings[0] + " " + strings[1] +" "   
				                                                          + strings[3] + ",";	
			}else{
				strFieldName =  strFieldName + strings[0] + " " + strings[1] + "(" + strings[2] + ") " 
				+ strings[3] + ",";
			}
			if(strings[0] == null || strings[1] == null
					|| strings[2] == null){
				return false;
			}
		}
		strFieldName = strFieldName.substring(0, strFieldName.length() - 1);
		String sql = "create table " + table.getTableName().toUpperCase() + "(" 
		+ strFieldName + ")";
		Connection con = JdbcUtil.getConnection();
		con.setAutoCommit(false);
		Statement stm = con.createStatement();
		
		boolean flag = true;
		try{
			
			stm.execute(sql);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		con.commit();
		stm.close();
		con.close();
		return flag;
	}
	/**
	 * 建索引
	 * @param strIndexName String
	 * @param FieldName String
	 * @param mysqlExists String
	 * @param strTableName String
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean createIndex(String strIndexName, 
			String FieldName, String mysqlExists, String strTableName) throws Exception{
		String sql1 = "create index " + mysqlExists + " " + strIndexName.toUpperCase() +" on "+ 
		strTableName.toUpperCase() +" (" + FieldName + ")";
		Connection con = JdbcUtil.getConnection();
		con.setAutoCommit(false);
		Statement stm = con.createStatement();
		boolean flag = stm.execute(sql1);
		con.commit();
		stm.close();
		con.close();
		return flag;
	}
	
	/**
	 * 删除索引
	 * @param strIndexName String
	 * @param FieldName String
	 * @param mysqlExists String
	 * @param strTableName String
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean removeIndex(String strIndexName, String mysqlExists) throws Exception{
		String sql1 = "drop index " + mysqlExists + " " + strIndexName;
		Connection con = JdbcUtil.getConnection();
		con.setAutoCommit(false);
		Statement stm = con.createStatement();
		boolean flag = stm.execute(sql1);
		con.commit();
		stm.close();
		con.close();
		return flag;
	}
	/**
	 * 删除索引
	 * @param strIndexName String
	 * @param FieldName String
	 * @param mysqlExists String
	 * @param strTableName String
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean mysqlremoveIndex(String strIndexName, String strTableName) throws Exception{
		String sql1 = "drop index " + strIndexName + " on " + strTableName;
		Connection con = JdbcUtil.getConnection();
		con.setAutoCommit(false);
		Statement stm = con.createStatement();
		boolean flag = stm.execute(sql1);
		con.commit();
		stm.close();
		con.close();
		return flag;
	}
	/**
	 * 添加主键
	 * @param strTableName
	 * @param strPrimary
	 * @return
	 * @throws Exception
	 */
	public static boolean addPrimaryKey(String strTableName, String strPrimary) throws Exception{
		String sql1 = "alter table "+ strTableName +" add primary key (" + strPrimary + ")";
		Connection con = JdbcUtil.getConnection();
		con.setAutoCommit(false);
		Statement stm = con.createStatement();
		boolean flag = stm.execute(sql1);
		con.commit();
		stm.close();
		con.close();
		return flag;
	}
	public static boolean removePriamrykey(String strTableName) throws Exception{
		String sql = "alter table " + strTableName + " drop priamry key";
		Connection con = JdbcUtil.getConnection();
		con.setAutoCommit(false);
		Statement stm = con.createStatement();
		boolean flag = stm.execute(sql);
		con.commit();
		stm.close();
		con.close();
		return flag;
	}
	/**
	 * 获得插入的sql
	 * @param strTableName String
	 * @param list List
	 * @return String
	 */
	public static String insertSql(String strTableName, List<String> list){
		String strField = "";
		String strLength = "";
		for (String string : list) {
			strField = strField + string + ",";
			strLength = strLength + "?,";
		}
		if(strField.length() == 0){
			return null;
		}
		strField = strField.substring(0, strField.length() - 1);
		strLength = strLength.substring(0, strLength.length() - 1);
		String sql = "insert into " + strTableName.toUpperCase() + "(" + strField + ") " + "values(" + strLength + ")";
		return sql;
	}
	
	public static boolean OracleTableISExists(String tablename){
		boolean flag = false;
		Connection connection;
		try {
			connection = JdbcUtil.getConnection();
			Statement stm = connection.createStatement();
			ResultSet result = stm.executeQuery("select * from user_tables where table_name = '"+ 
					tablename.toUpperCase() + "'");
			if(result.next()){
				flag = true;
			}
			stm.close();
			connection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}
	public static boolean PostGresqlTableISExists(String tablename){
		boolean flag = false;
		Connection connection;
		try {
			connection = JdbcUtil.getConnection();
			Statement stm = connection.createStatement();
			ResultSet result = stm.executeQuery("select * from pg_tables where tablename = '"+ 
					tablename.toLowerCase() + "'");
			if(result.next()){
				flag = true;
			}
			stm.close();
			connection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}
	public static boolean MySqlTableISExists(String tablename){
		boolean flag = false;
		Connection connection;
		try {
			connection = JdbcUtil.getConnection();
			Statement stm = connection.createStatement();
			ShpConf conf = new ShpConf();
			String strURL = conf.getDatabaseURL();
			int index = strURL.lastIndexOf("/");
			String strTablespace = strURL.substring(index + 1, strURL.length());
				String sql = "show tables where Tables_in_" 
					+ strTablespace + "='" + tablename + "'";
			ResultSet res = stm.executeQuery(sql);
			if(res.next()){
				flag = true;
			}
			stm.close();
			connection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}
	
	public static boolean dropTable(String tablename) {
		String sql = "drop table " + tablename + " CASCADE";
		Connection connection;
		boolean flag = true;
		try {
			connection = JdbcUtil.getConnection();
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			flag = statement.execute(sql);
			connection.commit();
			statement.close();
			connection.close();
		} catch (Exception e) {
			System.out.println(tablename + "表不存在");
			e.printStackTrace();
		}finally{
			return flag;
		}
	}
	
	public static void dropListTable(List<String> tabList) throws Exception{
		Connection connection = JdbcUtil.getConnection();
		connection.setAutoCommit(false);
		Statement statement = connection.createStatement();
		for (String string : tabList) {
			String sql = "drop  table " + string + " CASCADE";
			statement.execute(sql);
		}
		connection.commit();
		statement.close();
		connection.close();
	}
	
	public static void addcolumn(String table, String column, String columntype) throws Exception{
		Connection connection = JdbcUtil.getConnection();
		connection.setAutoCommit(false);
		String sql = "alter table " + table + " add " + column + " " + columntype;
		Statement statement = connection.createStatement();
		statement.execute(sql);
		connection.commit();
		statement.close();
		connection.close();
	}
	
	public static List<String> retuTableColumnList(String tablename) throws Exception{
		Connection connection = JdbcUtil.getConnection();
		String sql = "select * from " + tablename + " where 1 = 2";
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql);
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
		List<String> list = new ArrayList<String>();
		for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
			list.add(resultSetMetaData.getColumnName(i).toLowerCase());
		}
		resultSet.close();
		statement.close();
		connection.close();
		return list;
	}
	
	/**
	 * 插入形状经纬度范围
	 * 
	 * @param tableName
	 * @return
	 */
	public static String getInsertCoordinateAreaSql(String tableName, String fieldName) {
		String sql = "INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)"
				+ "VALUES ('"
				+ tableName
				+ "','"
				+ fieldName
				+ "',"
				+ "MDSYS.SDO_DIM_ARRAY"
				+ "(MDSYS.SDO_DIM_ELEMENT('X',"
				+ minLongitude
				+ ","
				+ maxLongitude
				+ ","
				+ coordinatePrecision
				+ "),"
				+ " MDSYS.SDO_DIM_ELEMENT('Y',"
				+ minLatitude
				+ ","
				+ maxLatitude
				+ ","
				+ coordinatePrecision + ")" + "), 8199)";
		return sql;
	}
	
	public static void executeSql(String sql) throws Exception{
		Connection connection = JdbcUtil.getConnection();
		connection.setAutoCommit(false);
		Statement statement = connection.createStatement();
		statement.execute(sql);
		connection.commit();
		statement.close();
		connection.close();
	}
	
	public static String createMySqlGeomIndex(String tableName, String FieldName){
		String sql = "CREATE SPATIAL INDEX " + tableName + "_geom_index ON " + tableName + " (" + FieldName + ")";
		return sql;
	}
	
	public static String createPostGISGeomIndex(String tableName, String FieldName){
		String sql = "create index " + tableName + "_geom_index on " + tableName + " using gist (" + FieldName + ")";;
		return sql;
	}
	
	public static String createOracleGeomitryIndexSql(String tableName, String FieldName) {
		String sql = "create index " + tableName + "_geom_geomitry " + "on "
				+ tableName + "(" + FieldName + ") indextype is MDSYS.SPATIAL_INDEX";
		return sql;
	}
	
	public static boolean mySqlIndexIsExist(String tableName, String indexName) throws Exception{
		String sql = "show index from " + tableName + " where Key_name='" + indexName + "'";
		Connection connection = JdbcUtil.getConnection();
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql);
		boolean flag = false;
		if(resultSet.next()){
			flag = true;
		}
		return flag;
	}
	
	public static boolean oracleIndexIsExist(String tableName, String indexName) throws Exception{
		String sql = "select * from dba_indexes where index_name='" + indexName.toUpperCase() 
		+ "' and table_name='" + tableName.toUpperCase() + "'";
		Connection connection = JdbcUtil.getConnection();
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql);
		boolean flag = false;
		if(resultSet.next()){
			flag = true;
		}
		resultSet.close();
		statement.close();
		connection.close();
		return flag;
	}
	
	public static boolean postgreIndexIsExist(String tableName, String indexName)throws Exception{
		String sql = "select * from pg_indexes where tablename='" 
			+ tableName.toLowerCase() + "' and indexname='" + indexName.toLowerCase() + "'";
		Connection connection = JdbcUtil.getConnection();
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql);
		boolean flag = false;
		if(resultSet.next()){
			flag = true;
		}
		resultSet.close();
		statement.close();
		connection.close();
		return flag;
	}
	
	public static boolean checkOracleSDOGEOMMEDATAisExist(String tablename, String fieldname) throws Exception{
		String sql = "select * from USER_SDO_GEOM_METADATA where table_name = '" + tablename.toUpperCase() 
		+ "' and column_name = '" + fieldname.toUpperCase() + "'";
		Connection connection = JdbcUtil.getConnection();
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql);
		boolean flag = false;
		if(resultSet.next()){
			flag = true;
		}
		resultSet.close();
		statement.close();
		connection.close();
		return flag;
		
	}
	
	public static boolean createPostSubArea(String tablename, String subtablename, String condition, String column) throws Exception{
		String sql = "CREATE TABLE " + subtablename + " (" +
				"CHECK ( " + column + " like '%" + condition + "%' )) " +
				"INHERITS (" + tablename + ")"; 
		Connection connection = JdbcUtil.getConnection();
		connection.setAutoCommit(false);
		Statement statement = connection.createStatement();
		statement.execute(sql);
		connection.commit();
		String indexsql = "CREATE INDEX " + subtablename + "_index ON " + tablename + " (" + column + ")";
		statement.execute(indexsql);
		connection.commit();
		boolean flag = JdbcUtil.PostGresqlTableISExists(subtablename);
		return flag;
//		String sqlRule = "CREATE OR REPLACE RULE measurement_current_partition AS ON INSERT TO measurement DO INSTEAD INSERT INTO measurement_yy06mm01 VALUES ( NEW.city_id, NEW.logdate, NEW.peaktemp, NEW.unitsales );"
	}
	
//	public static DataConnectionTool selectConnectionTool() throws Exception{
//		DataConnectionTool dataConnectionTool = new DataConnectionTool();
//		ShpConf conf = new ShpConf();
//		if(Util.ORACLEDRIVERNAME.equals(conf.getDriver())){
//			dataConnectionTool.openNewPrePareStatement(sql)
//		}
//	}
	

//	/**
//	 * 保存线类型形状
//	 * 
//	 * @param tableName
//	 * @param shps
//	 * @throws Exception
//	 * @throws DBException
//	 */
//	public void savePolylineShp(String tableName, List<String> shps)
//			throws Exception {
//		ShpConf conf = new ShpConf();
//		conf.initProperties();
//		Class.forName(conf.getDriver());
//		String url = conf.getDatabaseURL();
//		String username = conf.getUsername();
//		String password = conf.getPassword();
//		Connection con = null;
//		con = DriverManager.getConnection(url, username, password);
//		con.setAutoCommit(false);
//		String sql = null;
//		PreparedStatement preStatement = null;
//		try {
//			sql = "insert into " + tableName + " (GID,stGeom) values (?,?)";
//			preStatement = con.prepareStatement(sql);
//			int iCount = 0;
//			int iSize = shps.size();
//			for (String polylin : shps) {
//				++iCount;
//				String[] polylinarr = polylin.split(";");
//				int iPointSize = polylinarr.length;
//				double[] coordinate = new double[iPointSize * 2];
//				for (int i = 0; i < iPointSize; i++) {
//					coordinate[i * 2] = Double.parseDouble(polylinarr[i]
//							.split(",")[0]);
//					coordinate[i * 2 + 1] = Double.parseDouble(polylinarr[i]
//							.split(",")[1]);
//				}
//				JGeometry Ls = JGeometry.createLinearLineString(coordinate, 2,
//						8199);
//				STRUCT GeomObject = JGeometry.store(Ls, con);
//				preStatement.setString(1, UUID.randomUUID().toString());
//				preStatement.setObject(2, GeomObject);
//				preStatement.addBatch();
//				if (iCount % 1000 == 0 || iCount == iSize) {
//					preStatement.executeBatch();
//					con.commit();
//					preStatement.clearBatch();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		} finally {
//			if (preStatement != null) {
//				preStatement.close();
//			}
//			if (con != null) {
//				con.close();
//			}
//		}
//	}

//	/**
//	 * 保存面类型形状
//	 * 
//	 * @param tableName
//	 * @param shps
//	 * @throws DBException
//	 * @throws SQLException
//	 * @throws ClassNotFoundException
//	 * @throws NamingException
//	 * @throws ParserConfigurationException
//	 * @throws SAXException
//	 * @throws IOException
//	 */
//	public void savePolygonShp(String tableName, List<String> shps)
//			throws SQLException, ClassNotFoundException, NamingException,
//			ParserConfigurationException, SAXException, IOException {
//		String sql = null;
//		Connection conn = null;
//		PreparedStatement preStatement = null;
//		try {
//			sql = "insert into " + tableName + " (strID,stGeom) values (?,?)";
//			preStatement = conn.prepareStatement(sql);
//			int iCount = 0;
//			int iSize = shps.size();
//			for (String polygon : shps) {
//				++iCount;
//				int iPointSize = polygon.split("_")[0].split(",").length;
//				double[] coordinate = new double[(iPointSize - 1) * 2];
//				// 最后一个点不用加入坐标点内，保存面要素形状时会自动将最后一个点加入面形状内
//				for (int i = 0; i < iPointSize - 1; i++) {
////					coordinate[i * 2] = CoordnateTransfer
////							.formatLongitudeToDegree(polygon.getCoordinate()
////									.get(i).getX());
////					coordinate[i * 2 + 1] = CoordnateTransfer
////							.formatLatitudeToDegree(polygon.getCoordinate()
////									.get(i).getY());
//					coordinate[i * 2] = Double.parseDouble(polylinarr[i]
//					                      							.split(",")[0]);
//					                      					coordinate[i * 2 + 1] = Double.parseDouble(polylinarr[i]
//					                      							.split(",")[1]);
//				}
//				JGeometry Ls = JGeometry.createLinearPolygon(coordinate, 2,
//						8199);
//				STRUCT GeomObject = JGeometry.store(Ls, conn);
//				preStatement.setString(1, polygon.getStrid());
//				preStatement.setObject(2, GeomObject);
//				preStatement.addBatch();
//				if (iCount % Constants.iGeomBatchSize == 0 || iCount == iSize) {
//					preStatement.executeBatch();
//					preStatement.clearBatch();
//				}
//			}
//		} finally {
//			if (preStatement != null) {
//				preStatement.close();
//			}
//		}
//	}

//	/**
//	 * 保存点类型形状
//	 * 
//	 * @param tableName
//	 * @param shps
//	 * @throws DBException
//	 * @throws SQLException
//	 * @throws ClassNotFoundException
//	 * @throws NamingException
//	 * @throws ParserConfigurationException
//	 * @throws SAXException
//	 * @throws IOException
//	 */
//	public void savePointShp(String tableName, List<PointShp> shps)
//			throws SQLException, ClassNotFoundException, NamingException,
//			ParserConfigurationException, SAXException, IOException {
//		DatabaseConnector dc = null;
//		try {
//			dc = new DatabaseConnector();
//			String sql = "insert into "
//					+ tableName
//					+ " (strID,stGeom)"
//					+ " values (?,SDO_GEOMETRY(2001,8199,NULL,SDO_ELEM_INFO_ARRAY(1,1,1),"
//					+ " SDO_ORDINATE_ARRAY(?,?)))";
//			dc.prepareStatement(sql);
//			int iCount = 0;
//			int iSize = shps.size();
//			for (PointShp shp : shps) {
//				++iCount;
//				dc.setString(1, shp.getStrid());
//				dc.setDouble(2, CoordnateTransfer.formatLongitudeToDegree(shp
//						.getCoordinate().getX()));
//				dc.setDouble(3, CoordnateTransfer.formatLatitudeToDegree(shp
//						.getCoordinate().getY()));
//				dc.addBatch();
//
//				if (iCount % Constants.ibatchSize == 0 || iCount == iSize) {
//					dc.executeBatch();
//					dc.clearBatch();
//				}
//			}
//		} finally {
//			if (dc != null) {
//				dc.close();
//			}
//		}
//	}

	
	public static void main(String[] args) {
		JdbcUtil.dropTable("aas");
	}
	
}
