package com.sunmap.shpdata.tools.daoimpl.shpfiledaoimpl;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.UUID;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sunmap.shpdata.tools.conf.ShpConf;
import com.sunmap.shpdata.tools.constvalue.DataType;
import com.sunmap.shpdata.tools.dao.ISHPDataExpand;
import com.sunmap.shpdata.tools.dao.IShpFiledao;
import com.sunmap.shpdata.tools.dao.IShpParse;
import com.sunmap.shpdata.tools.dao.TableDAO;
import com.sunmap.shpdata.tools.daoimpl.shpdaoimpl.ShpTableDaoImpl;
import com.sunmap.shpdata.tools.daoimpl.shpparseimpl.ShpParseByteImpl;
import com.sunmap.shpdata.tools.daoimpl.shpparseimpl.ShpParseListForTFSZImpl;
import com.sunmap.shpdata.tools.daoimpl.shpparseimpl.ShpParseListImpl;
import com.sunmap.shpdata.tools.factory.SHPDataExpandFactory;
import com.sunmap.shpdata.tools.util.ConstantValue;
import com.sunmap.shpdata.tools.util.DataConnectionTool;
import com.sunmap.shpdata.tools.util.JdbcUtil;
import com.sunmap.shpdata.tools.util.Util;
import com.sunmap.shpdata.tools.vo.ShpDataVO;
import com.sunmap.shpdata.tools.vo.ShpFieldVO;

import form.Form;

/**
 * 
 * @author shanbq
 * 
 */
public class ShpFiledaoImpl implements IShpFiledao {

	private Map<String, Object> EleRec = new HashMap<String, Object>(); 

	private Map<String, HashSet<String>> distIDMap = new HashMap<String, HashSet<String>>();

	/**
	 * 解析文件并插入数据
	 * 
	 * @param file
	 *            File
	 * @return ShpDataVO
	 * @throws Exception
	 */
	public ShpDataVO readShpFile(File dbffile, File shpfile, File shxfile) throws Exception {
		if (dbffile == null) {
			return null;
		}
		DataConnectionTool dataConnectionTool = new DataConnectionTool();
		String[] strTableInfo = Util.returnTableName(dbffile);
		Util.setMymapidToParentdir(strTableInfo, dbffile);
		String strTableName = strTableInfo[0].toLowerCase();
		String strlevel = strTableInfo[1];
		List<String> subAreaTableNameList = Util.subAreaName.get(strTableName);
		if(subAreaTableNameList != null){
			for (String string : subAreaTableNameList) {
				if(Arrays.asList(dbffile.getPath().split("\\\\")).contains(string)){
					strTableName = strTableName + "_" + string;
				}
			}
		}
		// 根据文件名获得要素类型
		String strMapid = dbffile.getParentFile().getName();
		System.out.println(dbffile.getParentFile().getName() + ":"
				+ dbffile.getName());
		ShpConf conf = new ShpConf();
		conf.initProperties();
		Form.setTextCon(dbffile.getParentFile().getName() + ":"
				+ dbffile.getName());
		// 读文件
		TableDAO tabledao = new ShpTableDaoImpl();
		Connection con = null;
		con = JdbcUtil.getConnection();
		con.setAutoCommit(false);
		DataInputStream inData = new DataInputStream(new BufferedInputStream(
				new FileInputStream(dbffile)));
		try {
			byte[] head = new byte[ConstantValue.HEAD_LENGTH];
			inData.read(head);
			
			/** 记录条数 */
			byte[] registNum = Arrays.copyOfRange(head, 4, 8);
			int iRecordNum = Util.convertToInt(registNum, registNum.length,
					false);
			dataConnectionTool.setMaxcount(iRecordNum);
			/** 一条记录的长度 */
			byte[] recordLength = Arrays.copyOfRange(head, 10, 12);
			int iRecordLength = Util.convertToShort(recordLength,
					recordLength.length, false);
			
			/** 文件头的长度 */
			byte[] headLength = Arrays.copyOfRange(head, 8, 10);
			int iHeadLength = Util.convertToShort(headLength,
					headLength.length, false);
			
			/** 读取记录项名 */
			byte[] recordName = new byte[iHeadLength - 32];
			int countNum = (iHeadLength - 32) / 32;
			inData.read(recordName);
			List<ShpFieldVO> astFieldEle = new ArrayList<ShpFieldVO>();
			List<String> astFieldName = new Vector<String>();
			byte[] name = new byte[18];
			for (int i = 0; i < countNum; i++) {
				ShpFieldVO shpFieldVO = new ShpFieldVO();
				name = Arrays.copyOfRange(recordName, i * 32, i * 32 + 18);
				String fieldName = new String(Arrays.copyOfRange(name, 0, 11));
				fieldName = fieldName.trim();
				shpFieldVO.setBPrecision(name[17]);
				shpFieldVO.setStrName(fieldName);
				shpFieldVO.setBType(name[11]);
				shpFieldVO.setBLength(name[16]);
				astFieldEle.add(shpFieldVO);
				if (astFieldName.contains(fieldName)) {
					return null;
				}
				astFieldName.add(fieldName);
			}
			/** 解析记录信息 */
			byte[] recordsBuf = new byte[ConstantValue.MAX_lENGTH
					* iRecordLength];
			long l = System.currentTimeMillis();
			IShpParse shpparse = new ShpParseListForTFSZImpl();
			List<String> list = null;
			if (shpfile.exists()) {
				list =  (List<String>)shpparse.shpparse(shpfile, shxfile ,iRecordNum);
			}
//			PreparedStatement ps = tabledao.createSql(strTableName,
//					astFieldEle, con);
			String geomtype = "";
			if(list != null && list.size() > 0){
				geomtype = list.get(0);
			}
			int count = 1;
			while (true) {
				int readLength = inData.read(recordsBuf);
				if (readLength <= 0) {
					break;
				}
				int recordCount = readLength / iRecordLength;
				for (int i = 0; i < recordCount; i++) {
					byte[] oneRecordBuf = Arrays.copyOfRange(recordsBuf, i
							* iRecordLength, (i + 1) * iRecordLength);
					int startP = 1;
					for (int j = 0; j < astFieldEle.size(); j++) {
						int iFieldLenth = astFieldEle.get(j).getBLength();
						byte[] bData = Arrays.copyOfRange(oneRecordBuf, startP,
								startP + iFieldLenth);
						Object obj = astFieldEle.get(j).getIConvertData()
								.readField(bData, 1);
						startP += iFieldLenth;
						if (!obj.equals("")) {
							EleRec.put(astFieldEle.get(j).getStrName().toUpperCase(), obj);
						}
					}
					ISHPDataExpand shpexpand = SHPDataExpandFactory
							.createShpDataExpand(DataType.SW);
					if("".equals(strlevel)){
						EleRec.put("MYMAPID", shpexpand.dealwith(
								dbffile.getName().toLowerCase().replace(strTableName,
								"").replace(".dbf", ""), EleRec)
								.get("MYMAPID"));
						
					}else {
						EleRec.put("MYMAPID", shpexpand.dealwith(
								strlevel
								+ "%"
								+ dbffile.getName().toLowerCase().replace(strTableName,
								"").replace(".dbf", ""), EleRec)
								.get("MYMAPID"));
						
					}
					if (list == null || list.size() - 1 < count || list.get(count) == null || "".equals(list.get(count))) {
						geomtype = "3";
						EleRec.put("GEOM", "(0 0,0 0,0 0)");
					} else {
						EleRec.put("GEOM", list.get(count));
					}
					EleRec.put("GID", UUID.randomUUID().toString());
					count++;
					try {
//						if(EleRec.get("ADAID").equals(new Long(110228))){
////							System.out.println("asasd");
//							System.out.println(EleRec.get("GEOM").toString());
//						}
						tabledao.insertDataBatch(dataConnectionTool, EleRec, strTableName, geomtype);
//						ps = tabledao.insertTable(ps, astFieldEle, EleRec);
//						if ((i % 1000 == 0) && (i >= 1000)) {
//							if (Util.MYSQLDRIVERNAME.equals(conf.getDriver())) {
//								con.commit();
//								ps.clearBatch();
//							} else if (Util.ORACLEDRIVERNAME.equals(conf
//									.getDriver())) {
//								ps.executeBatch();
//								con.commit();
//								ps.clearBatch();
//							} else if (Util.POSTGRESQLNAME.equals(conf
//									.getDriver())) {
//								ps.executeBatch();
//								con.commit();
//								ps.clearBatch();
//							}
//						} else if (i == recordCount - 1) {
//							if (Util.MYSQLDRIVERNAME.equals(conf.getDriver())) {
//								con.commit();
//								ps.clearBatch();
//							} else if (Util.ORACLEDRIVERNAME.equals(conf
//									.getDriver())) {
//								ps.executeBatch();
//								con.commit();
//								ps.clearBatch();
//							} else if (Util.POSTGRESQLNAME.equals(conf
//									.getDriver())) {
//								ps.executeBatch();
//								con.commit();
//								ps.clearBatch();
//							}
//
//						}
						EleRec.clear();
					} catch (Exception e) {
						File file1 = new File("error.txt");
						File file2 = new File(Util.logName);
						if(file1.exists()){
							file1.delete();
						}
						file1.createNewFile();
						file2.createNewFile();
						FileWriter out1 = new FileWriter(file2, true);
						FileWriter out = new FileWriter(file1, true);
						PrintWriter writer = new PrintWriter(out);
						writer.write(Thread.currentThread().getName() + ":"
								+ strMapid + ":\r");
						e.printStackTrace(writer);
						out1.write(new Date(System.currentTimeMillis())
								.toLocaleString()
								+ Thread.currentThread().getName()
								+ ":"
								+ strMapid + ":" + e + "\r");
						out1.write("任务号" + strMapid + "下的" + strTableName
								+ "导入出错\r");
						writer.close();
						out1.close();
						e.printStackTrace();
						// throw new StrException(e);
					} catch (Throwable t) {
						File file1 = new File("error.txt");
						File file2 = new File(Util.logName);
						if(file1.exists()){
							file1.delete();
						}
						file1.createNewFile();
						file2.createNewFile();
						FileWriter out1 = new FileWriter(file2, true);
						FileWriter out = new FileWriter(file1, true);
						PrintWriter writer = new PrintWriter(out);
						writer.write(Thread.currentThread().getName() + ":"
								+ strMapid + ":\r");
						t.printStackTrace(writer);
						out.write(new Date(System.currentTimeMillis())
								.toLocaleString()
								+ Thread.currentThread().getName()
								+ ":"
								+ strMapid + ":" + t + "\r");
						out1.write("任务号" + strMapid + "下的" + strTableName
								+ "导入出错\r");
						writer.close();
						out1.close();
						t.printStackTrace();
					}
				}
			}
			con.commit();
			con.close();
		} catch (Exception e) {
			File file1 = new File("error.txt");
			File file2 = new File(Util.logName);
			if(file1.exists()){
				file1.delete();
			}
			file1.createNewFile();
			file2.createNewFile();
			FileWriter out = new FileWriter(file1, true);
			FileWriter out1 = new FileWriter(file2, true);
			PrintWriter pw = new PrintWriter(out1);
			pw
					.write(new Date(System.currentTimeMillis())
							.toLocaleString()
							+ Thread.currentThread().getName()
							+ ":"
							+ strMapid
							+ ":\r");
			e.printStackTrace(pw);
			out.write("任务号" + strMapid + "解析出错\r");
			pw.close();
			out.close();
			e.printStackTrace();
		} catch (Throwable t) {
			File file1 = new File("error.txt");
			File file2 = new File(Util.logName);
			if(file1.exists()){
				file1.delete();
			}
			file1.createNewFile();
			file2.createNewFile();
			FileWriter out = new FileWriter(file1, true);
			FileWriter out1 = new FileWriter(file2, true);
			PrintWriter pw = new PrintWriter(out1);
			pw
					.write(new Date(System.currentTimeMillis())
							.toLocaleString()
							+ Thread.currentThread().getName()
							+ ":"
							+ strMapid
							+ ":\r");
			t.printStackTrace(pw);
			out.write("任务号" + strMapid + "解析出错\r");
			pw.close();
			out.close();
			t.printStackTrace();
		} finally {
			if (inData != null) {
				inData.close();
			}
		}
		return null;

	}

	// //头文件固定长度
	// final static int SHP_HEADER_LENGTH = 100;
	//	    
	// /**
	// * 解析点状目标文件
	// * @param fis FileInputStream 文件流
	// * @return String[]
	// * @throws Exception
	// */
	// private List<String> parsePoint ( FileInputStream fis ) throws Exception
	// {
	// byte[] headerBytes = new byte[4];
	// byte[] pointBytes = new byte[8];
	// List<String> list=new ArrayList<String>();
	// StringBuilder str=new StringBuilder();
	// while( fis.read(headerBytes) != -1 ) {
	// //清除str的字符串
	// str.delete(0, str.length());
	// //过滤无用数据
	// fis.skip( 8 );
	// //坐标
	// fis.read(pointBytes);
	// double x = Util.convertToDouble(pointBytes, 8, false);
	// fis.read(pointBytes);
	// double y = Util.convertToDouble(pointBytes, 8, false);
	// str.append( x );
	// str.append( "," );
	// str.append( y );
	// list.add( str.toString() );
	// }
	// return list;
	// }
	// /**
	// * 解析线状目标文件
	// * @param fis FileInputStream 文件流
	// * @return String[]
	// * @throws Exception
	// */
	// private List<String> parsePolyLine (FileInputStream fis) throws Exception
	// {
	// List<String> list=new ArrayList<String>();
	// int recordNumber = -1;
	// byte[] recordNumberBytes = new byte[4];
	// //临时变量
	// byte[] pointsXBytes = new byte[8];
	// byte[] pointsYBytes = new byte[8];
	// byte[] numPartsBytes = new byte[4];
	// byte[] numPointsBytes = new byte[4];
	// byte[] partsBytes = new byte[4];
	// StringBuilder str=new StringBuilder();
	// while( fis.read(recordNumberBytes) != -1 )
	// {
	// //清除str的字符串
	// str.delete(0, str.length());
	// recordNumber =
	// Util.convertToInt(recordNumberBytes,recordNumberBytes.length, true);
	// // 目标的几何类型 读Box...
	// int i = 0;
	// //过滤无用数据
	// fis.skip( 5*8 );
	// // 读NumParts和NumPoints
	// fis.read(numPartsBytes);
	// int numParts = Util.convertToInt(numPartsBytes, 4, false);
	// fis.read(numPointsBytes);
	// int numPoints = Util.convertToInt(numPointsBytes, 4, false);
	// // 读Parts和Points
	// int[] parts = new int[numParts];
	// for (i = 0; i < numParts; i++) {
	// fis.read(partsBytes);
	// parts[i] = Util.convertToInt(partsBytes, 4, false);
	// }
	// int pointNum = 0;
	// for (i = 0; i < numParts; i++) {
	// if (i != numParts - 1) {
	// pointNum = parts[i + 1] - parts[i];
	// } else {
	// pointNum = numPoints - parts[i];
	// }
	// double[] pointsX = new double[pointNum];
	// double[] pointsY = new double[pointNum];
	// for (int j = 0; j < pointNum; j++) {
	// fis.read(pointsXBytes);
	// pointsX[j] = Util
	// .convertToDouble(pointsXBytes, 8, false);
	//			    
	// fis.read(pointsYBytes);
	// pointsY[j] = Util
	// .convertToDouble(pointsYBytes, 8, false);
	// str.append( pointsX[j] );
	// str.append( "," );
	// str.append( pointsY[j] );
	// }
	// }
	// list.add( str.toString() );
	// }
	// return list;
	// }
	//	    
	// /**
	// * 解析面状目标文件
	// * @param fis FileInputStream 文件流
	// * @return String[]
	// * @throws Exception
	// */
	// private List<String> parseArea (FileInputStream fis ) throws Exception {
	// List<String> list=new ArrayList<String>();
	// int recordNumber = -1;
	// byte[] recordNumberBytes = new byte[4];
	// byte[] numPartsBytes = new byte[4];
	// byte[] partsBytes = new byte[4];
	// byte[] pointsXBytes = new byte[8];
	// byte[] pointsYBytes = new byte[8];
	//		    
	// StringBuilder str=new StringBuilder();
	//		
	// while( fis.read(recordNumberBytes) != -1 )
	// {
	// //清除str的字符串
	// str.delete(0, str.length());
	// //str=new StringBuilder();
	//		    
	// recordNumber = Util.convertToInt(recordNumberBytes,
	// recordNumberBytes.length, true);
	//		    
	// int i = 0;
	//		    
	// //过滤无用数据
	// fis.skip( 5*8 );
	//
	// // 读NumParts和NumPoints
	// fis.read(numPartsBytes);
	// int numParts = Util.convertToInt(numPartsBytes, 4, false);
	//
	// byte[] numPointsBytes = new byte[4];
	// fis.read(numPointsBytes);
	// int numPoints = Util.convertToInt(numPointsBytes, 4, false);
	//		    	    
	// // 读Parts和Points
	// int[] parts = new int[numParts];
	// for (i = 0; i < numParts; i++) {
	// fis.read(partsBytes);
	// parts[i] = Util.convertToInt(partsBytes, 4, false);
	// }
	//		    	    
	// //点的数目
	// int pointNum = 0;
	// for (i = 0; i < numParts; i++) {
	//
	// if (i != numParts - 1) {
	// pointNum = parts[i + 1] - parts[i];
	// } else {
	// pointNum = numPoints - parts[i];
	// }
	// double[] pointsX = new double[pointNum];
	// double[] pointsY = new double[pointNum];
	// for (int j = 0; j < pointNum; j++) {
	// fis.read(pointsXBytes);
	// pointsX[j] = Util
	// .convertToDouble(pointsXBytes, 8, false);
	// fis.read(pointsYBytes);
	// pointsY[j] = Util
	// .convertToDouble(pointsYBytes, 8, false);
	// str.append( pointsX[j] );
	// str.append( "," );
	// str.append( pointsY[j] );
	// str.append( ";" );
	// }
	// }
	// list.add( str.toString() );
	// }
	// return list;
	// }
	// /**
	// * 解析shp文件
	// * @param file File
	// * @return String[]
	// * @throws Exception
	// * @throws Exception
	// */
	// public List<String> shpparse ( File file ) throws Exception {
	// List<String> shpList=null;
	// FileInputStream fis=null;
	// fis=new FileInputStream(file);
	// //文件头
	// int headerShapeType=-1;
	// byte[] recordHeaderBytes=new byte[4];
	// fis.skip(32);
	// fis.read(recordHeaderBytes);
	// fis.skip(64);
	// headerShapeType=Util.convertToInt(recordHeaderBytes, 4, false);
	// switch (headerShapeType) {
	// case 1:// 点状目标
	// System.out.println("正在解析的文件类型 点状目标");
	// shpList=this.parsePoint ( fis );
	// break;
	// case 3:// 线状目标
	// System.out.println("正在解析的文件类型 线状目标");
	// shpList=this.parsePolyLine ( fis );
	// break;
	// case 5:// 面状目标
	// System.out.println("正在解析的文件类型 面状目标");
	// shpList=this.parseArea ( fis );
	// break;
	// default:
	// break;
	// }
	// if( fis != null) {
	// fis.close();
	// }
	// return shpList;
	// }

}
