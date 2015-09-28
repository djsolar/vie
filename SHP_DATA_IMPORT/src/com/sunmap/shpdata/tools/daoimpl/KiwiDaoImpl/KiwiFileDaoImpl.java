package com.sunmap.shpdata.tools.daoimpl.KiwiDaoImpl;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.sunmap.shpdata.tools.conf.ShpConf;
import com.sunmap.shpdata.tools.dao.IKiwiFileDao;
import com.sunmap.shpdata.tools.dao.TableDAO;
import com.sunmap.shpdata.tools.util.JdbcUtil;
import com.sunmap.shpdata.tools.util.Util;
import com.sunmap.shpdata.tools.vo.ShpFieldVO;

import form.f;

public class KiwiFileDaoImpl implements IKiwiFileDao{

	
	@Override
	public void readFile(File dbffile) throws Exception{
		int local = dbffile.getName().lastIndexOf(".");
		String strTableName = dbffile.getName().substring(0, local);
		f.setTextCon(dbffile.getParentFile().getName() + ":"
				+ dbffile.getName());
		// 读文件
		TableDAO tabledao = new KiwiDaoImpl();
		Connection con = JdbcUtil.getConnection();
		ShpConf conf = JdbcUtil.getConf();
		con.setAutoCommit(false);
		FileInputStream fis = new FileInputStream(dbffile);
		InputStreamReader isr = new InputStreamReader(fis,"GBK");
		BufferedReader br = new BufferedReader(isr);
		String recodeLine = br.readLine();
		String[] arrField = recodeLine.split(conf.getDivideSympol());
		List<ShpFieldVO> astFieldEle = new ArrayList<ShpFieldVO>();
		ShpFieldVO fieldVO = new ShpFieldVO();
		fieldVO.setStrName("gid");
		astFieldEle.add(fieldVO);
		for (int i = 0; i < arrField.length; i++) {
			if (i == 0 && strTableName.equalsIgnoreCase("POI_gd")) {
				continue;
			}
			fieldVO = new ShpFieldVO();
			if (strTableName.equalsIgnoreCase("NODERECEXPORTER") && arrField[i].equalsIgnoreCase("IntersectMultiLinkDisplayClassNum")) {
				arrField[i] = "IntersectMultiLinkDisplayNum";
			}
			if (strTableName.equalsIgnoreCase("NODECLASSEXPORTER") && arrField[i].equalsIgnoreCase("Level")) {
				arrField[i] = "Level1";
			}
			fieldVO.setStrName(arrField[i]);
			astFieldEle.add(fieldVO);
		}
		//添加uuid
		long begin = System.currentTimeMillis();
		long end = System.currentTimeMillis();
		System.out.println(strTableName+"--"+"--"+(end-begin)+"-----createSql");
		PreparedStatement ps = tabledao.createSql(strTableName, astFieldEle, con);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(format.format(new Date(System.currentTimeMillis()))+"--"+dbffile.getName()+"while开始");
		Map<String, Object> EleRec = new HashMap<String, Object>();
			int index = 0;
			boolean flag = false;
				while ((recodeLine = br.readLine()) != null) {
					flag = false;
					arrField = recodeLine.split(conf.getDivideSympol());
					
					for (int i = 0; i < arrField.length; i++) {
						try {
							Object object = arrField[i].trim();
							if (object == null || "".equals(object)) {
								System.out.println(object+"object");
							}
								EleRec.put(astFieldEle.get(i+1).getStrName(), object);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
					
						ps = tabledao.insertTable(ps, astFieldEle, EleRec);
							if (++index % 1000 == 0) {
								flag = true;
								if(!Util.MYSQLDRIVERNAME.equals(conf.getDriver())){
									try {
										ps.executeBatch();
									} catch (Exception e) {
										e.printStackTrace();
									}
										
								}
								con.commit();
								ps.clearBatch();
							}
					
					EleRec.clear();
				}
				if (!flag) {
						ps.executeBatch();
						con.commit();
						ps.clearBatch();
					
				}
					con.close();
					br.close();
	}
	public static void main(String[] args)  {
	
		importPOI();
		
	}
	public static void importNode()throws SQLException, Exception{
		String sql = "INSERT INTO Node VALUES (?, ?, ?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection con = JdbcUtil.getConnection();
		PreparedStatement ps = con .prepareStatement(sql);
		File file = new File("D:/kiwidata/NodeRecExporter.txt");
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis,"GBK");
		BufferedReader br = new BufferedReader(isr);
		String lineString = null;
		int index = 0;
		boolean flag = false;
		con.setAutoCommit(false);
		br.readLine();
		String key24 = "";
		String LEVELNUM = "";
		String BLOCKSETNUM = "";
		String BLOCKNUM = "";
		String PARCELNUM = "";
		String LINKID = "";
		String ROADKINDCODE = "";
		String LINKKINDCODE = "";
		String ONEWAYCODE = "";
		String SHAPELIST = "";
		String LINKIDLIST = "";
		String LENGTH = "";
		String FWDLANESCNT = "";
		String BWDLANESCNT = "";
		String[] arrStrings = null; 
		while ((lineString = br.readLine()) != null) {
			flag = false;
			arrStrings = lineString.split(",");
			key24 = arrStrings[0];
			ps.setString(1, key24);
			LEVELNUM = arrStrings[2];
			ps.setInt(2, Integer.parseInt(LEVELNUM));
			BLOCKSETNUM = arrStrings[3];
			ps.setInt(3, Integer.parseInt(BLOCKSETNUM));
			BLOCKNUM = arrStrings[4];
			ps.setInt(4, Integer.parseInt(BLOCKNUM));
			PARCELNUM = arrStrings[5];
			ps.setInt(5, Integer.parseInt(PARCELNUM));
			LINKID = arrStrings[10];
			ps.setInt(6, Integer.parseInt(LINKID));
			ROADKINDCODE = arrStrings[12];
			ps.setInt(7, Integer.parseInt(ROADKINDCODE));
			LINKKINDCODE = arrStrings[36];
			ps.setInt(8, Integer.parseInt(LINKKINDCODE));
			ONEWAYCODE = arrStrings[42];
			ps.setInt(9, Integer.parseInt(ONEWAYCODE));
			SHAPELIST = arrStrings[49];
			char[] buf = SHAPELIST.toCharArray();
			Reader in = new CharArrayReader(buf);
			try {
				ps.setCharacterStream(10, in,buf.length);
			} catch (Exception e) {
				e.printStackTrace();
			}
			LINKIDLIST = arrStrings[50];
			ps.setString(11, LINKIDLIST);
			LENGTH = arrStrings[51];
			ps.setInt(12, Integer.parseInt(LENGTH));
			FWDLANESCNT = arrStrings[55];
			ps.setInt(13, Integer.parseInt(FWDLANESCNT));
			BWDLANESCNT = arrStrings[57];
			ps.setInt(14, Integer.parseInt(BWDLANESCNT));
			ps.addBatch();
			if (++index % 1000 == 0) {
				flag = true;
				ps.executeBatch();
				con.commit();
				ps.clearBatch();
			}
		}
		if (!flag) {
			ps.executeBatch();
			con.commit();
			ps.clearBatch();
		}
		con.close();
		fis.close();
		isr.close();
		br.close();
	}
	public static void importPOI(){
		
		String sql = "INSERT INTO POI(gid, longitude, latitude) VALUES (?, ?, ?)";
		try {
			Connection con = JdbcUtil.getConnection();
			Statement statement = con.createStatement();
			String string = "create table POI(gid varchar(256), longitude varchar(256), latitude varchar(256))";
			statement.execute(string);
			PreparedStatement ps = con .prepareStatement(sql);
			File file = new File("D:/kiwidata/POI.txt");
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis,"GBK");
			BufferedReader br = new BufferedReader(isr);
			String lineString = null;
			String strX = "";
			String strY = "";
			int index = 0;
			boolean flag = false;
			con.setAutoCommit(false);
			while ((lineString = br.readLine()) != null) {
				flag = false;
				strX = lineString.split("\t")[1];
				strY = lineString.split("\t")[2];
				ps.setString(1, UUID.randomUUID().toString());
				ps.setString(2, strX);
				ps.setString(3, strY);
				ps.addBatch();
				if (++index % 1000 == 0) {
					flag = true;
					ps.executeBatch();
					con.commit();
					ps.clearBatch();
				}
			}
			if (!flag) {
				ps.executeBatch();
				con.commit();
				ps.clearBatch();
			}
			con.close();
			fis.close();
			isr.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
