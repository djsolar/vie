package com.sunmap.shpdata.tools.daoimpl.KiwiDaoImpl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import com.sunmap.shpdata.tools.conf.ShpConf;
import com.sunmap.shpdata.tools.dao.TableDAO;
import com.sunmap.shpdata.tools.util.ConstantValue;
import com.sunmap.shpdata.tools.util.DataConnectionTool;
import com.sunmap.shpdata.tools.util.JdbcUtil;
import com.sunmap.shpdata.tools.util.Util;
import com.sunmap.shpdata.tools.vo.ShpFieldVO;
import com.sunmap.shpdata.tools.vo.Table;

public class KiwiDaoImpl implements TableDAO{

	static Map<String , String > driverSqlMap;
	
	@Override
	public PreparedStatement createSql(String strTableName,
			List<ShpFieldVO> astFieldEle, Connection con) throws Exception {
		PreparedStatement ps = null;
		StringBuffer strEle = new StringBuffer();
		StringBuffer strMark = new StringBuffer();
		for (int i = 0; i < astFieldEle.size(); i++) {
			if (i == astFieldEle.size()-1) {
				strEle.append(astFieldEle.get(i).getStrName());
				strMark.append("?");
				break;
			}
			strEle.append(astFieldEle.get(i).getStrName() + ",");
			strMark.append("?,");
		}
		String sql = "insert into " + strTableName.toUpperCase() + "("
		+ strEle.toString() + ") " + "values(" + strMark.toString() + ")";
		ps = con.prepareStatement(sql);
		return ps;
	}

	@Override
	public boolean createTable(List<File> filelist) throws Exception {
		boolean flag = false;
		ShpConf conf = JdbcUtil.getConf();
		int  local = 0;
		String strTableName ="";
		for (File file : filelist) {
			Vector<String> list2 = new Vector<String>(); 
			local = file.getName().lastIndexOf(".");
			strTableName = file.getName().substring(0, local);
			Table table = new Table();
			table.setTableName(strTableName);
			String idfieldType = "";
			if(ConstantValue.MYSQLDRIVERNAME.equals(conf.getDriver())){
				idfieldType = Util.mySqlfieldType((byte)0);
				table.addField("gid", idfieldType, "256","");
			}else if(ConstantValue.ORACLEDRIVERNAME.equals(conf.getDriver())){
				idfieldType = Util.oraclefieldType((byte)0);
				table.addField("gid", idfieldType, "256","");
			}else if(ConstantValue.POSTGRESQLDRIVERNAME.equals(conf.getDriver())){
				idfieldType = Util.postgresqlfieldType((byte)0);
				table.addField("gid", idfieldType, "65536","");
			}
			try{
				FileInputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis,"GBK");
				BufferedReader br = new BufferedReader(isr);
				String[] arrFileld = br.readLine().split(conf.getDivideSympol());
					for (int i = 0; i < arrFileld.length; i++) {
						String fieldName = arrFileld[i].trim();
						String fieldType = "";
						if(ConstantValue.MYSQLDRIVERNAME.equals(conf.getDriver())){
							fieldType = Util.mySqlfieldType((byte)0);
						}else if(ConstantValue.ORACLEDRIVERNAME.equals(conf.getDriver())){
							fieldType = Util.oraclefieldType((byte)0);
						}else if(ConstantValue.POSTGRESQLDRIVERNAME.equals(conf.getDriver())){
							fieldType = Util.postgresqlfieldType((byte)0);
						}
						if (list2.contains(fieldName)) {
								File fileError = new File("error.txt");
								fileError.createNewFile();
								FileWriter out = new FileWriter(fileError, true);
								out.write("文件"+strTableName+"不能建表\r");
								out.close();
								return false;
						}
						list2.add(fieldName);

						if (strTableName.equalsIgnoreCase("NODERECEXPORTER") && fieldName.equalsIgnoreCase("IntersectMultiLinkDisplayClassNum")) {
							fieldName = "IntersectMultiLinkDisplayNum";
						}
						if (strTableName.equalsIgnoreCase("NODECLASSEXPORTER") && fieldName.equalsIgnoreCase("Level")) {
							fieldName = "Level1";
						}
						if (strTableName.equalsIgnoreCase("NODERECEXPORTER") && fieldName.equalsIgnoreCase("SHAPELIST")) {
							fieldType = "text";
						}
						if (strTableName.equalsIgnoreCase("NODERECEXPORTER") && fieldName.equalsIgnoreCase("LinkID")) {
							fieldType = "int";
						}
						if (strTableName.equalsIgnoreCase("linkid_node") && fieldName.equalsIgnoreCase("LinkID")) {
							fieldType = "int";
						}
						table.addField(fieldName, fieldType, "256","");
					}
					fis.close();
					isr.close();
					br.close();
					flag = this.create(conf,table);
			}catch (Exception e) {
				System.out.println("解析，存储时错误");
				e.printStackTrace();
			}
		}
		return flag;
	}
	/**
	 * 根据需求建表
	 * @param conf 
	 * @param table Table
	 * @return boolean
	 * @throws Exception 
	 * @throws Exception
	 */
	public boolean create(ShpConf conf, Table table) throws Exception{
		boolean flag = false;
		String strDriver = conf.getDriver();
		Connection con = JdbcUtil.getConnection();
		Statement stm = con.createStatement();
		if(ConstantValue.MYSQLDRIVERNAME.equals(strDriver)){
				stm.execute("drop table IF EXISTS " + table.getTableName().toUpperCase());
				flag = JdbcUtil.mysqlCreateTable(table, "MyISAM", "utf8", "", "");
		}else if(ConstantValue.ORACLEDRIVERNAME.equals(strDriver)){
				ResultSet result = stm.executeQuery("select * from user_tables where table_name = '"+ 
						table.getTableName().toUpperCase() + "'");
				if(result.next()){
					try {
						
						stm.execute("drop table " + table.getTableName().toUpperCase());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				flag = JdbcUtil.oracleCreateTable(table);
		}else if(ConstantValue.POSTGRESQLDRIVERNAME.equals(conf.getDriver())){
				ResultSet result = stm.executeQuery("select * from pg_tables where tablename = '"+ 
						table.getTableName().toLowerCase() + "'");
				if(result.next()){
					stm.execute("drop table " + table.getTableName().toLowerCase());
				}
				flag = JdbcUtil.postgresqlCreateTable(table);
			}
		stm.close();
		con.close();
		return flag;
	}

	@Override
	public PreparedStatement insertTable(PreparedStatement ps,
			List<ShpFieldVO> astFieldEle, Map<String, Object> EleRec)
			throws Exception {
		for (int j = 1; j < astFieldEle.size(); j++) {
			Object object = EleRec.get( (astFieldEle.get(j)).getStrName());
			if(object == null || "".equals(object)){
				System.out.println(object+"object");
			}
//			if(astFieldEle.get(j).getStrName().equalsIgnoreCase("SHAPELIST")){
//				char[] buf = ((String)object).toCharArray();
//				Reader in = new CharArrayReader(buf);
//				try {
//					
//					ps.setCharacterStream(j+1, in,buf.length);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				
//			}
			if(astFieldEle.get(j).getStrName().equalsIgnoreCase("linkid")){
				String string = (String)object;
				ps.setInt(j+1, Integer.parseInt(string));
			}else {
				ps.setObject(j+1, object);
			}
		}
		ps.setString(1, UUID.randomUUID().toString());
		ps.addBatch();
		if (ConstantValue.MYSQLDRIVERNAME.equals(JdbcUtil.getConf().getDriver())) {
			try {
				ps.executeBatch();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ps;
	}

	@Override
	public Connection removeIndex(String strTableName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Connection renewIndex(String strTableName, Connection con)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean createTable1(List<Table> creatMap) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void insertDataBatch(DataConnectionTool dataConnectionTool,
			Map<String, Object> EleRec, String tableName, String datatype)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
