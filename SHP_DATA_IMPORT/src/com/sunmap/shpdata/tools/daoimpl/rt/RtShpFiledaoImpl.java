package com.sunmap.shpdata.tools.daoimpl.rt;

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

import com.sunmap.shpdata.tools.conf.ShpConf;
import com.sunmap.shpdata.tools.constvalue.DataType;
import com.sunmap.shpdata.tools.dao.ISHPDataExpand;
import com.sunmap.shpdata.tools.dao.IShpFiledao;
import com.sunmap.shpdata.tools.dao.IShpParse;
import com.sunmap.shpdata.tools.dao.TableDAO;
import com.sunmap.shpdata.tools.daoimpl.shpdaoimpl.ShpTableDaoImpl;
import com.sunmap.shpdata.tools.daoimpl.shpparseimpl.ShpParseByteImpl;
import com.sunmap.shpdata.tools.daoimpl.shpparseimpl.ShpParseListImpl;
import com.sunmap.shpdata.tools.factory.SHPDataExpandFactory;
import com.sunmap.shpdata.tools.util.ConstantValue;
import com.sunmap.shpdata.tools.util.JdbcUtil;
import com.sunmap.shpdata.tools.util.Util;
import com.sunmap.shpdata.tools.vo.ShpDataVO;
import com.sunmap.shpdata.tools.vo.ShpFieldVO;

import form.f;

/**
 * 
 * @author shanbq
 * 
 */
public class RtShpFiledaoImpl implements IShpFiledao {

	private Map<String, Object> EleRec = new HashMap<String, Object>(); 

	private Map<String, HashSet<String>> distIDMap = new HashMap<String, HashSet<String>>();

	/**
	 * �����ļ�����������
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
		String[] strTableInfo = Util.returnTableName(dbffile);
		String strTableName = strTableInfo[0];
		String strlevel = strTableInfo[1];
		// if(strTableName.equals("meta_")
		// ){
		// return null;
		// }
		
		
		// �����ļ������Ҫ������
		String strMapid = dbffile.getParentFile().getName();
		System.out.println(dbffile.getParentFile().getName() + ":"
				+ dbffile.getName());
		ShpConf conf = new ShpConf();
		conf.initProperties();
		// FileOutputStream fos = new FileOutputStream(new
		// File(RtShpFiledaoImpl.class.getClassLoader().getResource("log.txt").toURI()),true);
		// PrintWriter writer = new PrintWriter(fos);
		// writer.println(dbffile.getParentFile().getName() + ":"
		// + dbffile.getName() + "\r");
		// writer.flush();
		// writer.close();
		f.setTextCon(dbffile.getParentFile().getName() + ":"
				+ dbffile.getName());
		// ���ļ�
		TableDAO tabledao = new ShpTableDaoImpl();
		Connection con = null;
		con = JdbcUtil.getConnection();
		con.setAutoCommit(false);
		DataInputStream inData = new DataInputStream(new BufferedInputStream(
				new FileInputStream(dbffile)));
		try {
			byte[] head = new byte[ConstantValue.HEAD_LENGTH];
			inData.read(head);
			
			/** ��¼���� */
			byte[] registNum = Arrays.copyOfRange(head, 4, 8);
			int iRecordNum = Util.convertToInt(registNum, registNum.length,
					false);
			
			/** һ����¼�ĳ��� */
			byte[] recordLength = Arrays.copyOfRange(head, 10, 12);
			int iRecordLength = Util.convertToShort(recordLength,
					recordLength.length, false);
			
			/** �ļ�ͷ�ĳ��� */
			byte[] headLength = Arrays.copyOfRange(head, 8, 10);
			int iHeadLength = Util.convertToShort(headLength,
					headLength.length, false);
			
			/** ��ȡ��¼���� */
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
			/** ������¼��Ϣ */
			byte[] recordsBuf = new byte[ConstantValue.MAX_lENGTH
					* iRecordLength];
			long l = System.currentTimeMillis();
			IShpParse shpparse = new ShpParseListImpl();
			List<String> list = null;
			if (shpfile.exists()) {
				list =  (List<String>)shpparse.shpparse(shpfile);
			}
			// PreparedStatement ps = ShpEleDaoImpl.createSql(astFieldEle,
			// strTableName, con);
			PreparedStatement ps = tabledao.createSql(strTableName,
					astFieldEle, con);
			String geomtype = list.get(0);
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
								dbffile.getName().replace(strTableName,
								"").replace(".dbf", ""), EleRec)
								.get("MYMAPID"));
						
					}else {
						EleRec.put("MYMAPID", shpexpand.dealwith(
								strlevel
								+ "%"
								+ dbffile.getName().replace(strTableName,
								"").replace(".dbf", ""), EleRec)
								.get("MYMAPID"));
						
					}
					if (list == null) {
						EleRec.put("GEOM", "");
					} else {
						EleRec.put("GEOM", list.get(count));
					}
					count++;
					// String strGeom = this.shpparse(file);
					// JGeometryType type = new JGeometryType();
					// EleRec.put("GEOM", type);
					// String strClassName = eleType.getStrClassName();
					// if (eleType.equals(EleType.NaviPoint) && iRecordNum >
					// 200000) {
					// NaviPoint naviPoint = (NaviPoint) Util.getEleBean(EleRec,
					// strClassName);
					// if (naviPoint != null &&
					// distIDMap.get(naviPoint.getMapid().toString()) == null) {
					// HashSet<String> set = new HashSet<String>();
					// set.add(naviPoint.getDistid().toString());
					// distIDMap.put(naviPoint.getMapid().toString(), set);
					// } else {
					// HashSet<String> set1 = distIDMap.get(naviPoint
					// .getMapid().toString());
					// set1.add(naviPoint.getDistid().toString());
					// distIDMap.put(naviPoint.getMapid().toString(), set1);
					// }
					// }
					try {
						// ShpEleDaoImpl.insertSql(astFieldEle, EleRec, ps);
						ps = tabledao.insertTable(ps, astFieldEle, EleRec);
						if ((i % 100 == 0) && (i >= 100)) {
							// System.out.println("1000����");
							if (Util.MYSQLDRIVERNAME.equals(conf.getDriver())) {
								con.commit();
								ps.clearBatch();
							} else if (Util.ORACLEDRIVERNAME.equals(conf
									.getDriver())) {
								ps.executeBatch();
								con.commit();
								ps.clearBatch();
							} else if (Util.POSTGRESQLNAME.equals(conf
									.getDriver())) {
								ps.executeBatch();
								con.commit();
								ps.clearBatch();
							}
						} else if (i == recordCount - 1) {
							if (Util.MYSQLDRIVERNAME.equals(conf.getDriver())) {
								con.commit();
								ps.clearBatch();
							} else if (Util.ORACLEDRIVERNAME.equals(conf
									.getDriver())) {
								ps.executeBatch();
								con.commit();
								ps.clearBatch();
							} else if (Util.POSTGRESQLNAME.equals(conf
									.getDriver())) {
								ps.executeBatch();
								con.commit();
								ps.clearBatch();
							}

						}
						EleRec.clear();
					} catch (Exception e) {
						File file1 = new File("error.txt");
						File file2 = new File(Util.logName);
						file1.createNewFile();
						file2.createNewFile();
						FileWriter out1 = new FileWriter(file2, true);
						FileWriter out = new FileWriter(file1, true);
						PrintWriter writer = new PrintWriter(out);
						writer.write(Thread.currentThread().getName() + ":"
								+ strMapid + ":\r");
						e.printStackTrace(writer);
						out.write(new Date(System.currentTimeMillis())
								.toLocaleString()
								+ Thread.currentThread().getName()
								+ ":"
								+ strMapid + ":" + e + "\r");
						out1.write("�����" + strMapid + "�µ�" + strTableName
								+ "�������\r");
						writer.close();
						out1.close();
						e.printStackTrace();
						// throw new StrException(e);
					} catch (Throwable t) {
						File file1 = new File("error.txt");
						File file2 = new File(Util.logName);
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
						out1.write("�����" + strMapid + "�µ�" + strTableName
								+ "�������\r");
						writer.close();
						out1.close();
						t.printStackTrace();
					}
				}
			}
			// con = tabledao.renewIndex(strTableName, con);
			con.commit();
			con.close();
			// if(!distIDMap.isEmpty()){
			// JGeometryType type = new JGeometryType();
			// Set<Entry<String, HashSet<String>>> set2 = distIDMap.entrySet();
			// Iterator<Map.Entry<String, HashSet<String>>> ite =
			// set2.iterator();
			// if(ite.hasNext()){
			// Map.Entry<String, HashSet<String>> entry = ite.next();
			// HashSet<String> set3 = entry.getValue();
			// List<Object> astObj = new ArrayList<Object>();
			// for (String string : set3) {
			// Cityname cityname1 = new
			// Cityname(null,entry.getKey(),type,string);
			// astObj.add(cityname1);
			// }
			// try{
			// ShpEleDaoImpl.saveEleBean(astObj);
			// astObj.clear();
			// }catch (Exception e) {
			// File file1 = new File("error.txt");
			// file1.createNewFile();
			// FileWriter out = new FileWriter(file1, true);
			// out.write("�����" + strMapid + "�µ�cityname�������" + "�������\r");
			// out.close();
			// e.printStackTrace();
			// }
			// }
			// }
		} catch (Exception e) {
			File file1 = new File("error.txt");
			File file2 = new File(Util.logName);
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
			out.write("�����" + strMapid + "��������\r");
			pw.close();
			out.close();
			e.printStackTrace();
		} catch (Throwable t) {
			File file1 = new File("error.txt");
			File file2 = new File(Util.logName);
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
			out.write("�����" + strMapid + "��������\r");
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

	// //ͷ�ļ��̶�����
	// final static int SHP_HEADER_LENGTH = 100;
	//	    
	// /**
	// * ������״Ŀ���ļ�
	// * @param fis FileInputStream �ļ���
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
	// //���str���ַ���
	// str.delete(0, str.length());
	// //������������
	// fis.skip( 8 );
	// //����
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
	// * ������״Ŀ���ļ�
	// * @param fis FileInputStream �ļ���
	// * @return String[]
	// * @throws Exception
	// */
	// private List<String> parsePolyLine (FileInputStream fis) throws Exception
	// {
	// List<String> list=new ArrayList<String>();
	// int recordNumber = -1;
	// byte[] recordNumberBytes = new byte[4];
	// //��ʱ����
	// byte[] pointsXBytes = new byte[8];
	// byte[] pointsYBytes = new byte[8];
	// byte[] numPartsBytes = new byte[4];
	// byte[] numPointsBytes = new byte[4];
	// byte[] partsBytes = new byte[4];
	// StringBuilder str=new StringBuilder();
	// while( fis.read(recordNumberBytes) != -1 )
	// {
	// //���str���ַ���
	// str.delete(0, str.length());
	// recordNumber =
	// Util.convertToInt(recordNumberBytes,recordNumberBytes.length, true);
	// // Ŀ��ļ������� ��Box...
	// int i = 0;
	// //������������
	// fis.skip( 5*8 );
	// // ��NumParts��NumPoints
	// fis.read(numPartsBytes);
	// int numParts = Util.convertToInt(numPartsBytes, 4, false);
	// fis.read(numPointsBytes);
	// int numPoints = Util.convertToInt(numPointsBytes, 4, false);
	// // ��Parts��Points
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
	// * ������״Ŀ���ļ�
	// * @param fis FileInputStream �ļ���
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
	// //���str���ַ���
	// str.delete(0, str.length());
	// //str=new StringBuilder();
	//		    
	// recordNumber = Util.convertToInt(recordNumberBytes,
	// recordNumberBytes.length, true);
	//		    
	// int i = 0;
	//		    
	// //������������
	// fis.skip( 5*8 );
	//
	// // ��NumParts��NumPoints
	// fis.read(numPartsBytes);
	// int numParts = Util.convertToInt(numPartsBytes, 4, false);
	//
	// byte[] numPointsBytes = new byte[4];
	// fis.read(numPointsBytes);
	// int numPoints = Util.convertToInt(numPointsBytes, 4, false);
	//		    	    
	// // ��Parts��Points
	// int[] parts = new int[numParts];
	// for (i = 0; i < numParts; i++) {
	// fis.read(partsBytes);
	// parts[i] = Util.convertToInt(partsBytes, 4, false);
	// }
	//		    	    
	// //�����Ŀ
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
	// * ����shp�ļ�
	// * @param file File
	// * @return String[]
	// * @throws Exception
	// * @throws Exception
	// */
	// public List<String> shpparse ( File file ) throws Exception {
	// List<String> shpList=null;
	// FileInputStream fis=null;
	// fis=new FileInputStream(file);
	// //�ļ�ͷ
	// int headerShapeType=-1;
	// byte[] recordHeaderBytes=new byte[4];
	// fis.skip(32);
	// fis.read(recordHeaderBytes);
	// fis.skip(64);
	// headerShapeType=Util.convertToInt(recordHeaderBytes, 4, false);
	// switch (headerShapeType) {
	// case 1:// ��״Ŀ��
	// System.out.println("���ڽ������ļ����� ��״Ŀ��");
	// shpList=this.parsePoint ( fis );
	// break;
	// case 3:// ��״Ŀ��
	// System.out.println("���ڽ������ļ����� ��״Ŀ��");
	// shpList=this.parsePolyLine ( fis );
	// break;
	// case 5:// ��״Ŀ��
	// System.out.println("���ڽ������ļ����� ��״Ŀ��");
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
