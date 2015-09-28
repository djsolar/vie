package com.mansion.tele.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.measure.quantity.Length;

import com.mansion.tele.business.common.TeleConfig;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.common.GeoRect;

import SQLite.Constants;
import SQLite.Database;
import SQLite.Exception;
import SQLite.Stmt;

/**
 * TeleEDB操作类
 * @author yangzhen
 *
 */
public class TeleToEdb {
	/**
	 * View数据表名
	 */
	private static final String TABLENAMESTR = "tmap";
	
	/**
	 * 临时IO缓存大小
	 */
	private static final int TEMP_BUFFER_SIZE = 1024*1024*4;
	
	/**
	 * 表中关键字的分隔符
	 */
	private static final String KEY_SEPERATOR = ".";
	/**
	 * 文件路径。
	 */
	private String strFilePathString;
	/**
	 * 临时文件路径。
	 */
	private String strTempFilePathString;
	/**
	 * 开始事务标志
	 */
	private boolean bIsTransaction;
	
	/**
	 * 是否变更了
	 */
	private boolean bChanged;
	
	/**
	 * 是否保存临时文件
	 */
	private boolean bSaveTempFile;
	
	/**
	 * 数据库连接
	 */
	private Database db = new Database();
	
	/**
	 * 构造函数。本函数会将DB重写。
	 * @param strDbFilePath		DB文件路径。
	 * @param bTruncate			是否删除源文件
	 * @param bSaveTempFile		是否保留临时文件
	 * @throws IOException 		IO异常
	 * @throws Exception 		其他异常
	 */
	public TeleToEdb(String strDbFilePath, boolean bTruncate, boolean bSaveTempFile) throws Exception, IOException{
		this.bSaveTempFile = bSaveTempFile;
		this.strTempFilePathString = strDbFilePath + ".temp";
		this.strFilePathString = strDbFilePath;
		
		File tempFile = new File(this.strTempFilePathString);
		if (tempFile.exists()) {
			tempFile.delete();
		}
		
		// 删除已经存在的文件。
		File fileData = new File(strDbFilePath);
		if (fileData.exists()) {
			if (bTruncate){
				fileData.delete();
			}else {
				this.depress(this.strFilePathString, this.strTempFilePathString);
			}
		}
		
		// 创建并打开文件
		this.db.open(this.strTempFilePathString, 
				Constants.SQLITE_OPEN_CREATE | Constants.SQLITE_OPEN_READWRITE);

		if (!this.isTableExist(this.TABLENAMESTR)) {
			this.db.exec("create table " + this.TABLENAMESTR 
					+ " (key TEXT PRIMARY KEY, value blob);", null);
			this.bChanged = true;
		}
	}
	
	/**
	 * 压缩文件
	 * @param strFilePath		输入文件路径
	 * @param strDestFilePath	压缩后文件路径
	 * @throws IOException		IO异常
	 */
	private static void compress(String strFilePath, String strDestFilePath) throws IOException{
		// 压缩到真正的文件。
		FileOutputStream fileOutputStream = new FileOutputStream(new File(strDestFilePath));
		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
		
		FileInputStream fileInputStream = new FileInputStream(new File(strFilePath));
		byte[] tempBuffer = new byte[TeleToEdb.TEMP_BUFFER_SIZE];
		int length = fileInputStream.read(tempBuffer);
		while(-1 != length){
			gzipOutputStream.write(tempBuffer, 0, length);
			length = fileInputStream.read(tempBuffer);
		}
		
		gzipOutputStream.flush();
		fileOutputStream.flush();

		gzipOutputStream.close();
		fileOutputStream.close();
	}
	
	/**
	 * 解压缩文件
	 * @param strFilePath 		输入文件路径
	 * @param strDestFilePath	解压缩文件路径
	 * @throws IOException		IO异常
	 */
	private static void depress (String strFilePath, String strDestFilePath) throws IOException{
		FileInputStream fileInputStream = new FileInputStream(new File(strFilePath));
		GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
		BufferedInputStream bufferedInputStream = new BufferedInputStream(gzipInputStream);
		
		FileOutputStream fileOutputStream = new FileOutputStream(new File(strDestFilePath));
		byte[] tempBuffer = new byte[TeleToEdb.TEMP_BUFFER_SIZE];
		int length = bufferedInputStream.read(tempBuffer);
		while (-1 != length) {
			fileOutputStream.write(tempBuffer, 0, length);
			
			length = bufferedInputStream.read(tempBuffer);
		}
		
		bufferedInputStream.close();
		
		gzipInputStream.close();
		fileInputStream.close();
		
		fileOutputStream.flush();
		fileOutputStream.close();
	}
	
	/**
	 * 关闭资源
	 * @throws IOException IO异常
	 * @throws Exception	其他异常
	 */
	public void cloese()throws Exception, IOException {
		this.commitTransaction();
		
//		this.db.close();
		
		// compress temp file to real data file
		this.compress(this.strTempFilePathString, this.strFilePathString);

		if (!this.bSaveTempFile) {
			// delete temp file
			File fileTemp = new File(this.strTempFilePathString);
			if (fileTemp.exists()) {
				fileTemp.delete();
			}
		}
	}

	/**
	 * 开始事务
	 * @throws Exception	异常
	 */
	public void beginTransaction()throws Exception{
		if (this.bIsTransaction){
			return;
		}
		
		this.db.exec("begin transaction;", null);
		this.bIsTransaction = true;
	}

	/**
	 * 提交事务
	 * @throws Exception 异常
	 */
	public void commitTransaction()throws Exception{
		if (!this.bIsTransaction){
			return;
		}
		
		this.bIsTransaction = false;

		this.db.exec("commit transaction;", null);
	}

	/**
	 * 插入一个Block块
	 * @param level		Block数据块所在Level
	 * @param x			Block数据块的x编号
	 * @param y			Block数据块的y编号
	 * @param value		Block数据块数据
	 * @return true：成功，false：失败
	 * @throws Exception 异常
	 */
	public boolean insertBlock(byte level, int x, int y, byte[] value) throws Exception{
		String keyString = "" + x + TeleToEdb.KEY_SEPERATOR + y + TeleToEdb.KEY_SEPERATOR + level;
		
		String strStmtString = "insert into " + this.TABLENAMESTR + " (key, value) values(\""
		+ keyString + "\", ?);";
		Stmt stmt= this.db.prepare(strStmtString);
		stmt.bind(1, value);
		stmt.step();

		this.bChanged = true;

		return true;
	}
	
	/**
	 * 判断表是否已经存在
	 * @param strTableName 表名
	 * @return true:成功,false:失败
	 * @throws Exception 异常
	 */
	private boolean isTableExist(String strTableName) throws Exception{
		String strStmtString = "SELECT COUNT(*) FROM sqlite_master where type='table' and name=\""
			+ strTableName + "\";";

		Stmt stmt = this.db.prepare(strStmtString);
		stmt.step();
		int iCount = stmt.column_int(0);
		if (0 < iCount) {
			return true;
		}
		return false;
	}
	/**
	 * 读取一个block数据
	 * @param level		Block数据块所在Level
	 * @param x			Block数据块的x编号
	 * @param y			Block数据块的y编号
	 * @return		Block数据块数据，失败时返回null。
	 */
	public byte[] readBlock(byte level, int x, int y){
		String keyString = "" + x + TeleToEdb.KEY_SEPERATOR + y + TeleToEdb.KEY_SEPERATOR + level;
		String strStmtString = "select value from " + this.TABLENAMESTR
		+ " where key=\"" + keyString + "\" ;";

		try {
			Stmt stmt = this.db.prepare(strStmtString);
			stmt.step();
			byte[] value = stmt.column_bytes(0);
			return value;
		} catch (SQLite.Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

//	/**
//	 * 测试入口函数
//	 * @param aString 命令行参数
//	 * @return 执行结果
//	 */
//	public static int main(String[] aString ){
//		try {
//			TeleToEdb st = new TeleToEdb("d:/temp/北京市/tele.jpg", false, false);
//			byte [] buffer = st.readBlock((byte) 3, 385, 239);
//			System.out.println("" + buffer.toString());
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (SQLite.Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		return 0;
//	}
//	/**
//	 * 测试入口函数
//	 * @param aString 命令行参数
//	 */
//	public static void main2(String aString []){
//		GeoRect dataRect = new GeoRect();
//		// 北京市
//		String strProvinceNameString = "北京市"; 
//		dataRect.LeftDown.Longitude = 1063981319;
//		dataRect.LeftDown.Latitude = 364141665;
//		dataRect.RightUp.Longitude = 1082514525;
//		dataRect.RightUp.Latitude = 378271753;
//
//		// 广东省
////		String strProvinceNameString = "广东省";
////		dataRect.LeftDown.Longitude = 1011499432;
////		dataRect.LeftDown.Latitude = 187439581;
////		dataRect.RightUp.Longitude = 1078703959;
////		dataRect.RightUp.Latitude = 235603432;
//		
//		TeleToEdb st = null;
//		try {
//			st = new TeleToEdb("d:/temp/" + strProvinceNameString + "/tele.jpg", true, false);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		long lStart = System.currentTimeMillis();
//		
//		TeleViewData viewData = new TeleViewData("D:/temp/wwwww/view");
//		
//		try {
//			int count = 0;
//			st.beginTransaction();
//			for (int iLevel = 3; iLevel < 4; iLevel++) {
//				int[] blockDiv = TeleConfig.get().getBlockDivInfo(iLevel);
//				BlockNo startBlockNo = BlockNo.valueOf(dataRect.LeftDown, blockDiv[0], blockDiv[1]);
//				BlockNo endBlockNo = BlockNo.valueOf(dataRect.RightUp, blockDiv[0], blockDiv[1]);
//				int blockXStart = startBlockNo.iX;
//				int blockYStart = startBlockNo.iY;
//				int blockXEnd = endBlockNo.iX;
//				int blockYEnd = endBlockNo.iY;
//				for (int xLoop = blockXStart; xLoop <= blockXEnd; xLoop++) {
//					for (int yLoop = blockYStart; yLoop <= blockYEnd; yLoop++) {
//						byte[] blockData = viewData.getBlockForEDB(iLevel, xLoop, yLoop);
//						if (null != blockData) {
//							st.insertBlock((byte)iLevel, xLoop, yLoop, blockData);
//							++count;
//							if (0 == count%1000) {
//								System.out.println("insert " + count);
//								st.commitTransaction();
//								st.beginTransaction();
//							}
//						}
//					}
//				}
//			}
//			st.commitTransaction();
//			st.cloese();
//			System.out.println("block count = " + count);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (java.lang.Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		long lEnd = System.currentTimeMillis();
//		System.out.println("time = " + (lEnd - lStart));
//
//		System.out.println("test");
//	}
}
