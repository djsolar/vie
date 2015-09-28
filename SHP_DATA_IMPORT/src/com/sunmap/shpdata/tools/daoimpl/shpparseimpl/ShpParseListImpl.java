package com.sunmap.shpdata.tools.daoimpl.shpparseimpl;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sunmap.shpdata.tools.dao.IShpParse;
import com.sunmap.shpdata.tools.util.Util;

public class ShpParseListImpl implements IShpParse {

	private final static Logger logger = Logger
			.getLogger(ShpParseListImpl.class);

	// ͷ�ļ��̶�����
	final static int SHP_HEADER_LENGTH = 100;

	/**
	 * ������״Ŀ���ļ�
	 * 
	 * @param fis
	 *            FileInputStream �ļ���
	 * @return String[]
	 * @throws Exception
	 */
	private List<String> parsePoint(RandomAccessFile fis) throws Exception {
		byte[] headerBytes = new byte[4];
		byte[] pointBytes = new byte[8];
		List<String> list = new ArrayList<String>();
		StringBuilder str = new StringBuilder();
		while (fis.read(headerBytes) != -1) {
			// ���str���ַ���
			str.delete(0, str.length());
			// ������������
			fis.skipBytes(8);
			// ����
			fis.read(pointBytes);
			double x = Util.convertToDouble(pointBytes, 8, false);
			fis.read(pointBytes);
			double y = Util.convertToDouble(pointBytes, 8, false);
			str.append(x);
			str.append(" ");
			str.append(y);
			list.add(str.toString());
		}
		return list;
	}

	/**
	 * ������״Ŀ���ļ�
	 * 
	 * @param fis
	 *            FileInputStream �ļ���
	 * @return String[]
	 * @throws Exception
	 */
	private List<String> parsePolyLine(RandomAccessFile fis) throws Exception {
		List<String> list = new ArrayList<String>();
		int recordNumber = -1;
		byte[] recordNumberBytes = new byte[4];
		// ��ʱ����
		byte[] pointsXBytes = new byte[8];
		byte[] pointsYBytes = new byte[8];
		byte[] numPartsBytes = new byte[4];
		byte[] numPointsBytes = new byte[4];
		byte[] partsBytes = new byte[4];
		StringBuilder str = new StringBuilder();
		while (fis.read(recordNumberBytes) != -1) {
			// ���str���ַ���
			str.delete(0, str.length());
			recordNumber = Util.convertToInt(recordNumberBytes,
					recordNumberBytes.length, true);
			// Ŀ��ļ������� ��Box...
			int i = 0;
			// ������������
			fis.skipBytes(5 * 8);
			// ��NumParts��NumPoints
			fis.read(numPartsBytes);
			int numParts = Util.convertToInt(numPartsBytes, 4, false);
			fis.read(numPointsBytes);
			int numPoints = Util.convertToInt(numPointsBytes, 4, false);
			// ��Parts��Points
			int[] parts = new int[numParts];
			for (i = 0; i < numParts; i++) {
				fis.read(partsBytes);
				parts[i] = Util.convertToInt(partsBytes, 4, false);
			}
			int pointNum = 0;
			for (i = 0; i < numParts; i++) {
				if (i != numParts - 1) {
					pointNum = parts[i + 1] - parts[i];
				} else {
					pointNum = numPoints - parts[i];
				}
				double[] pointsX = new double[pointNum];
				double[] pointsY = new double[pointNum];

				if (i == 0) {
					str.append("(");
				} else {
					str.append("_(");
				}

				for (int j = 0; j < pointNum; j++) {
					fis.read(pointsXBytes);
					pointsX[j] = Util.convertToDouble(pointsXBytes, 8, false);

					fis.read(pointsYBytes);
					pointsY[j] = Util.convertToDouble(pointsYBytes, 8, false);
					str.append(pointsX[j]);
					str.append(" ");
					str.append(pointsY[j]);
					if (j < pointNum - 1)
						str.append(",");
				}

				str.append(")");
			}
			list.add(str.toString());
		}
		return list;
	}

	/**
	 * ������״Ŀ���ļ�
	 * 
	 * @param fis
	 *            FileInputStream �ļ���
	 * @return String[]
	 * @throws Exception
	 */
	private List<String> parseArea(RandomAccessFile fis) throws Exception {

		List<String> list = new ArrayList<String>();
		byte[] recordNumberBytes = new byte[4];
		byte[] numPartsBytes = new byte[4];
		byte[] partsBytes = new byte[4];
		byte[] pointsXBytes = new byte[8];
		byte[] pointsYBytes = new byte[8];
		StringBuilder str = new StringBuilder();
		while (fis.read(recordNumberBytes) != -1) {
			// ���str���ַ���
			str.delete(0, str.length());
			int i = 0;
			// ������������
			fis.skipBytes(5 * 8);
			// ��NumParts��NumPoints
			fis.read(numPartsBytes);
			int numParts = Util.convertToInt(numPartsBytes, 4, false);
			byte[] numPointsBytes = new byte[4];
			fis.read(numPointsBytes);
			int numPoints = Util.convertToInt(numPointsBytes, 4, false);
			// ��Parts��Points
			int[] parts = new int[numParts];
			for (i = 0; i < numParts; i++) {
				fis.read(partsBytes);
				parts[i] = Util.convertToInt(partsBytes, 4, false);
			}
			// �����Ŀ
			int pointNum = 0;
			for (i = 0; i < numParts; i++) {

				if (i != numParts - 1) {
					pointNum = parts[i + 1] - parts[i];
				} else {
					pointNum = numPoints - parts[i];
				}
				double[] pointsX = new double[pointNum];
				double[] pointsY = new double[pointNum];

				if (i == 0) {
					str.append("(");
				} else {
					str.append("_(");
				}

				for (int j = 0; j < pointNum; j++) {
					fis.read(pointsXBytes);
					pointsX[j] = Util.convertToDouble(pointsXBytes, 8, false);
					fis.read(pointsYBytes);
					pointsY[j] = Util.convertToDouble(pointsYBytes, 8, false);

					str.append(pointsX[j]);
					str.append(" ");
					str.append(pointsY[j]);
					if (j < pointNum - 1)
						str.append(",");
				}

				str.append(")");

				String x_first = "" + pointsX[0];
				String y_first = "" + pointsY[0];

				String x_last = "" + pointsX[pointNum - 1];
				String y_last = "" + pointsY[pointNum - 1];

				// System.out.println( x_first );
				// System.out.println( y_first );
				//				
				// System.out.println( x_last );
				// System.out.println( y_last );

				if (!x_first.equals(x_last) || !y_first.equals(y_last)) {
					logger.error("���պϵ�");
					// logger.error("x_first="+x_first);
					// logger.error("y_first="+y_first);
					// logger.error("x_last="+x_last);
					// logger.error("y_last="+y_last);

					logger.error("(" + x_first + " " + y_first + ")");
					logger.error("(" + x_last + " " + y_last + ")");
				}

			}
			list.add(str.toString());
		}

		return list;
	}

	/**
	 * ������״Ŀ���ļ�
	 * 
	 * @param fis
	 *            FileInputStream �ļ���
	 * @return String[]
	 * @throws Exception
	 */
	private List<String> parsePoint(RandomAccessFile fis, int iRecordNum, List<Integer> record)
			throws Exception {
		byte[] headerBytes = new byte[4];
		byte[] pointBytes = new byte[8];
		List<String> list = new ArrayList<String>();
		StringBuilder str = new StringBuilder();
		
		
		for (int i = 0; i < record.size(); i++) {

//			if (fis.read(headerBytes) == -1) {
//				return list;
//			}
			
			fis.seek(record.get(i));
			// ���str���ַ���
			str.delete(0, str.length());
			// ������������
			fis.skipBytes(12);
			// ����
			fis.read(pointBytes);
//			double x = fis.readDouble();
//			double y = fis.readDouble();
			
//			
			double x = Util.convertToDouble(pointBytes, 8, false);
			fis.read(pointBytes);
			double y = Util.convertToDouble(pointBytes, 8, false);
			str.append(x);
			str.append(" ");
			str.append(y);
			list.add(str.toString());
		}
		return list;
	}

	/**
	 * ������״Ŀ���ļ�
	 * 
	 * @param fis
	 *            FileInputStream �ļ���
	 * @return String[]
	 * @throws Exception
	 */
	private List<String> parsePolyLine(RandomAccessFile fis, int iRecordNum, List<Integer> record)
			throws Exception {
		List<String> list = new ArrayList<String>();
		int recordNumber = -1;
		byte[] recordNumberBytes = new byte[4];
		// ��ʱ����
		byte[] pointsXBytes = new byte[8];
		byte[] pointsYBytes = new byte[8];
		byte[] numPartsBytes = new byte[4];
		byte[] numPointsBytes = new byte[4];
		byte[] partsBytes = new byte[4];
		StringBuilder str = new StringBuilder();

		for (int k = 0; k < record.size(); k++) {

//			if (fis.read(recordNumberBytes) == -1) {
//				return list;
//			}
			
			fis.seek(record.get(k));
			// ���str���ַ���
			str.delete(0, str.length());
			recordNumber = Util.convertToInt(recordNumberBytes,
					recordNumberBytes.length, true);
			// Ŀ��ļ������� ��Box...
			int i = 0;
			// ������������
			fis.skipBytes(5 * 8 + 4);
			// ��NumParts��NumPoints
			fis.read(numPartsBytes);
			int numParts = Util.convertToInt(numPartsBytes, 4, false);
			fis.read(numPointsBytes);
			int numPoints = Util.convertToInt(numPointsBytes, 4, false);
			// ��Parts��Points
			int[] parts = new int[numParts];
			for (i = 0; i < numParts; i++) {
				fis.read(partsBytes);
				parts[i] = Util.convertToInt(partsBytes, 4, false);
			}
			int pointNum = 0;
			for (i = 0; i < numParts; i++) {
				if (i != numParts - 1) {
					pointNum = parts[i + 1] - parts[i];
				} else {
					pointNum = numPoints - parts[i];
				}
				double[] pointsX = new double[pointNum];
				double[] pointsY = new double[pointNum];

				if (i == 0) {
					str.append("(");
				} else {
					str.append("_(");
				}

				for (int j = 0; j < pointNum; j++) {
					fis.read(pointsXBytes);
					pointsX[j] = Util.convertToDouble(pointsXBytes, 8, false);

					fis.read(pointsYBytes);
					pointsY[j] = Util.convertToDouble(pointsYBytes, 8, false);
					str.append(pointsX[j]);
					str.append(" ");
					str.append(pointsY[j]);
					if (j < pointNum - 1)
						str.append(",");
				}

				str.append(")");
			}
			list.add(str.toString());
		}
		return list;
	}

	/**
	 * ������״Ŀ���ļ�
	 * 
	 * @param fis
	 *            FileInputStream �ļ���
	 * @return String[]
	 * @throws Exception
	 */
	private List<String> parseArea(RandomAccessFile fis, int iRecordNum, List<Integer> record)
			throws Exception {

		List<String> list = new ArrayList<String>();
		byte[] recordNumberBytes = new byte[4];
		byte[] numPartsBytes = new byte[4];
		byte[] partsBytes = new byte[4];
		byte[] pointsXBytes = new byte[8];
		byte[] pointsYBytes = new byte[8];
		StringBuilder str = new StringBuilder();
		for (int k = 0; k < record.size(); k++) {

//			if (fis.read(recordNumberBytes) == -1) {
//				return list;
//			}
			fis.seek(record.get(k));
			
			// ���str���ַ���
			str.delete(0, str.length());
			int i = 0;
			// ������������
			fis.skipBytes(5 * 8 + 4);
			// ��NumParts��NumPoints
			fis.read(numPartsBytes);
			int numParts = Util.convertToInt(numPartsBytes, 4, false);
			byte[] numPointsBytes = new byte[4];
			fis.read(numPointsBytes);
			int numPoints = Util.convertToInt(numPointsBytes, 4, false);
			// ��Parts��Points
			int[] parts = new int[numParts];
			for (i = 0; i < numParts; i++) {
				fis.read(partsBytes);
				parts[i] = Util.convertToInt(partsBytes, 4, false);
			}
			// �����Ŀ
			int pointNum = 0;
			for (i = 0; i < numParts; i++) {

				if (i != numParts - 1) {
					pointNum = parts[i + 1] - parts[i];
				} else {
					pointNum = numPoints - parts[i];
				}
				double[] pointsX = new double[pointNum];
				double[] pointsY = new double[pointNum];

				if (i == 0) {
					str.append("(");
				} else {
					str.append("_(");
				}

				for (int j = 0; j < pointNum; j++) {
					fis.read(pointsXBytes);
					pointsX[j] = Util.convertToDouble(pointsXBytes, 8, false);
					fis.read(pointsYBytes);
					pointsY[j] = Util.convertToDouble(pointsYBytes, 8, false);

					str.append(pointsX[j]);
					str.append(" ");
					str.append(pointsY[j]);
					if (j < pointNum - 1)
						str.append(",");
				}

				str.append(")");

				String x_first = "" + pointsX[0];
				String y_first = "" + pointsY[0];

				String x_last = "" + pointsX[pointNum - 1];
				String y_last = "" + pointsY[pointNum - 1];

				// System.out.println( x_first );
				// System.out.println( y_first );
				//				
				// System.out.println( x_last );
				// System.out.println( y_last );

				if (!x_first.equals(x_last) || !y_first.equals(y_last)) {
					logger.error("���պϵ�");
					// logger.error("x_first="+x_first);
					// logger.error("y_first="+y_first);
					// logger.error("x_last="+x_last);
					// logger.error("y_last="+y_last);

					logger.error("(" + x_first + " " + y_first + ")");
					logger.error("(" + x_last + " " + y_last + ")");
				}

			}
			list.add(str.toString());
		}

		return list;
	}

	/**
	 * ����shp�ļ�
	 * 
	 * @param file
	 *            File
	 * @return String[]
	 * @throws Exception
	 * @throws Exception
	 */
	public List<String> shpparse(File shpfile, File shxfile , int iRecordNum) throws Exception {
		List<String> shpList = null;
		RandomAccessFile fis = null;
		List<Integer> record = readshxfile(shxfile, iRecordNum);
		fis = new RandomAccessFile(shpfile, "r");
		// �ļ�ͷ
		int headerShapeType = -1;
		byte[] recordHeaderBytes = new byte[4];
		fis.skipBytes(32);
		fis.read(recordHeaderBytes);
		fis.skipBytes(64);
		headerShapeType = Util.convertToInt(recordHeaderBytes, 4, false);
		switch (headerShapeType) {
		case 1:// ��״Ŀ��
			System.out.println("���ڽ������ļ����� ��״Ŀ��");
			shpList = this.parsePoint(fis, iRecordNum, record);
			shpList.add(0, "1");
			break;
		case 3:// ��״Ŀ��
			System.out.println("���ڽ������ļ����� ��״Ŀ��");
			shpList = this.parsePolyLine(fis, iRecordNum, record);
			shpList.add(0, "3");
			break;
		case 5:// ��״Ŀ��
			System.out.println("���ڽ������ļ����� ��״Ŀ��");
			shpList = this.parseArea(fis, iRecordNum, record);
			shpList.add(0, "5");
			break;
		default:
			break;
		}
		if (fis != null) {
			fis.close();
		}
		return shpList;
	}

	/**
	 * ����shp�ļ�
	 * 
	 * @param file
	 *            File
	 * @return String[]
	 * @throws Exception
	 * @throws Exception
	 */
	public List<String> shpparse(File file) throws Exception {
		List<String> shpList = null;
		RandomAccessFile fis = null;
		fis = new RandomAccessFile(file, "r");
		// �ļ�ͷ
		int headerShapeType = -1;
		byte[] recordHeaderBytes = new byte[4];
		fis.skipBytes(32);
		fis.read(recordHeaderBytes);
		fis.skipBytes(64);
		headerShapeType = Util.convertToInt(recordHeaderBytes, 4, false);
		switch (headerShapeType) {
		case 1:// ��״Ŀ��
			System.out.println("���ڽ������ļ����� ��״Ŀ��");
			shpList = this.parsePoint(fis);
			shpList.add(0, "1");
			break;
		case 3:// ��״Ŀ��
			System.out.println("���ڽ������ļ����� ��״Ŀ��");
			shpList = this.parsePolyLine(fis);
			shpList.add(0, "3");
			break;
		case 5:// ��״Ŀ��
			System.out.println("���ڽ������ļ����� ��״Ŀ��");
			shpList = this.parseArea(fis);
			shpList.add(0, "5");
			break;
		default:
			break;
		}
		if (fis != null) {
			fis.close();
		}
		return shpList;
	}
	
	/**
	 * ����shx�ļ�
	 * @param shxfile
	 * @param RecordNum
	 * @return
	 */
	private List<Integer> readshxfile(File shxfile, int RecordNum){
		
		List<Integer> list = new ArrayList<Integer>();
		
		try {
			DataInputStream inData = new DataInputStream(new BufferedInputStream(
					new FileInputStream(shxfile)));
			
				inData.skip(100L);
				
				for (int i = 0; i < RecordNum; i++) {
				
					byte[] readbyte = new byte[4];
					
					inData.read(readbyte);
					
					inData.skip(4);
					
					list.add(Util.convertToInt(readbyte, 4, true) * 2);
					
				}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
		
	}

	public static void main(String[] args) throws UnsupportedEncodingException {

		// System.out.println( new String( "�N".getBytes("gbk"),"gbk") );

		try {
			// List<String> list=new ShpParseListImpl().shpparse(new File(
			// "/home/yangxiuliang/temp/Dbeijing.shp"));
			// List<String> list=new ShpParseListImpl().shpparse(new File(
			// "/home/yangxiuliang/temp/district_line.shp"));

			//			
			// for (String string : list) {
			// System.out.println(string);
			// }

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
