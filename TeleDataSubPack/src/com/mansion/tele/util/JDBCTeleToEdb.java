package com.mansion.tele.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.measure.quantity.Length;

import util.JdbcUtil;

import com.mansion.tele.business.common.TeleConfig;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.common.GeoRect;

import SQLite.Constants;
import SQLite.Database;
import SQLite.Exception;
import SQLite.Stmt;

/**
 * TeleEDB鎿嶄綔绫�
 * @author yangzhen
 *
 */
public class JDBCTeleToEdb {
	/**
	 * View鏁版嵁琛ㄥ悕
	 */
	private static final String TABLENAMESTR = "tmap";
	
	/**
	 * 涓存椂IO缂撳瓨澶у皬
	 */
	private static final int TEMP_BUFFER_SIZE = 1024*1024*4;
	
	/**
	 * 琛ㄤ腑鍏抽敭瀛楃殑鍒嗛殧绗�
	 */
	private static final String KEY_SEPERATOR = ".";
	/**
	 * 鏂囦欢璺緞銆�
	 */
	private String strFilePathString;
	/**
	 * 涓存椂鏂囦欢璺緞銆�
	 */
	private String strTempFilePathString;
	/**
	 * 寮�浜嬪姟鏍囧織
	 */
	private boolean bIsTransaction;
	
	/**
	 * 鏄惁鍙樻洿浜�
	 */
	private boolean bChanged;
	
	/**
	 * 鏄惁淇濆瓨涓存椂鏂囦欢
	 */
	private boolean bSaveTempFile;
	
	/**
	 * 鏁版嵁搴撹繛鎺�
	 */
//	private Database db = new Database();
	/**
	 * 鏁版嵁搴撻摼鎺�
	 */
	private Connection connection;
	
	/**
	 * 澶勭悊鏁版嵁宸ュ叿
	 */
	private Statement statement;
	/**
	 * 
	 */
	private PreparedStatement preparedStatement;
	/**
	 * 
	 */
	private String strSqlString = "insert into " + this.TABLENAMESTR + " (key, value) values(? , ?)";
	
	
	private Set<String> set = new HashSet<String>();
	/**
	 * 鏋勯�鍑芥暟銆傛湰鍑芥暟浼氬皢DB閲嶅啓銆�
	 * @param strDbFilePath		DB鏂囦欢璺緞銆�
	 * @param bTruncate			鏄惁鍒犻櫎婧愭枃浠�
	 * @param bSaveTempFile		鏄惁淇濈暀涓存椂鏂囦欢
	 * @throws IOException 		IO寮傚父
	 * @throws Exception 		鍏朵粬寮傚父
	 * @throws SQLException 
	 */
	public JDBCTeleToEdb(String strDbFilePath, boolean bTruncate, boolean bSaveTempFile) throws Exception, IOException, SQLException{
		this.bSaveTempFile = bSaveTempFile;
		this.strTempFilePathString = strDbFilePath + ".temp";
		this.strFilePathString = strDbFilePath;
		
		File tempFile = new File(this.strTempFilePathString);
		if (tempFile.exists()) {
			tempFile.delete();
		}
		
		// 鍒犻櫎宸茬粡瀛樺湪鐨勬枃浠躲�
		File fileData = new File(strDbFilePath);
		if (fileData.exists()) {
			if (bTruncate){
				fileData.delete();
			}else {
				this.depress(this.strFilePathString, this.strTempFilePathString);
			}
		}
		
//		// 鍒涘缓骞舵墦寮�枃浠�
//		this.db.open(this.strTempFilePathString, 
//				Constants.SQLITE_OPEN_CREATE | Constants.SQLITE_OPEN_READWRITE);

		this.connection = JdbcUtil.getSqliteConnection(this.strTempFilePathString);
		this.connection.setAutoCommit(false);
		if (!this.isJDBCTableExist(this.TABLENAMESTR)) {
			this.statement = connection.createStatement();
			this.bChanged =	statement.execute("create table " + this.TABLENAMESTR 
					+ " (key TEXT PRIMARY KEY, value blob);");
			this.statement.clearBatch();
			this.connection.commit();
		}
	}
	
	/**
	 * 鍘嬬缉鏂囦欢
	 * @param strFilePath		杈撳叆鏂囦欢璺緞
	 * @param strDestFilePath	鍘嬬缉鍚庢枃浠惰矾寰�
	 * @throws IOException		IO寮傚父
	 */
	private static void compress(String strFilePath, String strDestFilePath) throws IOException{
		// 鍘嬬缉鍒扮湡姝ｇ殑鏂囦欢銆�
		FileOutputStream fileOutputStream = new FileOutputStream(new File(strDestFilePath));
		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
		
		FileInputStream fileInputStream = new FileInputStream(new File(strFilePath));
		byte[] tempBuffer = new byte[JDBCTeleToEdb.TEMP_BUFFER_SIZE];
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
	 * 瑙ｅ帇缂╂枃浠�
	 * @param strFilePath 		杈撳叆鏂囦欢璺緞
	 * @param strDestFilePath	瑙ｅ帇缂╂枃浠惰矾寰�
	 * @throws IOException		IO寮傚父
	 */
	private static void depress (String strFilePath, String strDestFilePath) throws IOException{
		FileInputStream fileInputStream = new FileInputStream(new File(strFilePath));
		GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
		BufferedInputStream bufferedInputStream = new BufferedInputStream(gzipInputStream);
		
		FileOutputStream fileOutputStream = new FileOutputStream(new File(strDestFilePath));
		byte[] tempBuffer = new byte[JDBCTeleToEdb.TEMP_BUFFER_SIZE];
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
	 * 鍏抽棴璧勬簮
	 * @throws IOException IO寮傚父
	 * @throws Exception	鍏朵粬寮傚父
	 */
//	public void cloese()throws Exception, IOException {
//		this.commitTransaction();
//		
////		this.db.close();
//		
//		// compress temp file to real data file
//		this.compress(this.strTempFilePathString, this.strFilePathString);
//
//		if (!this.bSaveTempFile) {
//			// delete temp file
//			File fileTemp = new File(this.strTempFilePathString);
//			if (fileTemp.exists()) {
//				fileTemp.delete();
//			}
//		}
//	}

	
	/**
	 * 鍏抽棴璧勬簮
	 * @throws IOException IO寮傚父
	 * @throws Exception	鍏朵粬寮傚父
	 * @throws SQLException 
	 */
	public void jdbccloese()throws Exception, java.lang.Exception {
//		if(this.statement != null && !this.statement.isClosed()){
			
			this.statement.executeBatch();
//		}
//		if(this.preparedStatement != null && !this.preparedStatement.isClosed()){
			this.preparedStatement.executeBatch();
//		}
		this.jdbcCommit();
		this.statement.close();
		this.preparedStatement.close();
		this.connection.close();
		
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
	 * 寮�浜嬪姟
//	 * @throws Exception	寮傚父
//	 */
//	public void beginTransaction()throws Exception{
//		if (this.bIsTransaction){
//			return;
//		}
//		
//		this.db.exec("begin transaction;", null);
//		this.bIsTransaction = true;
//	}

	/**
	 * 鎻愪氦浜嬪姟
	 * @throws Exception 寮傚父
	 */
//	public void commitTransaction()throws Exception{
//		if (!this.bIsTransaction){
//			return;
//		}
//		
//		this.bIsTransaction = false;
//
//		this.db.exec("commit transaction;", null);
//	}

	/**
	 * 鎻愪氦浜嬪姟
	 * @throws Exception 寮傚父
	 * @throws SQLException 
	 */
	public void jdbcCommit()throws Exception, SQLException{
		if (this.connection == null || this.connection.isClosed()){
			return;
		}
		
		this.connection.commit();
		
	}
	
	public void executeBatch(boolean flag) throws SQLException{
		
		if(flag && this.connection != null && !this.connection.isClosed()){
			
			this.statement.executeBatch();
		}
		
	}
	
	public void executePreBatch(boolean flag) throws SQLException{
		
		if(flag && this.connection != null && !this.connection.isClosed() && this.preparedStatement != null){
			
			this.preparedStatement.executeBatch();
			this.preparedStatement.clearBatch();
		}
		
	}
	
	/**
	 * 鎻掑叆涓�釜Block鍧�
	 * @param level		Block鏁版嵁鍧楁墍鍦↙evel
	 * @param x			Block鏁版嵁鍧楃殑x缂栧彿
	 * @param y			Block鏁版嵁鍧楃殑y缂栧彿
	 * @param value		Block鏁版嵁鍧楁暟鎹�
	 * @return true锛氭垚鍔燂紝false锛氬け璐�
	 * @throws Exception 寮傚父
	 */
//	public boolean insertBlock(byte level, int x, int y, byte[] value) throws Exception{
//		String keyString = "" + x + JDBCTeleToEdb.KEY_SEPERATOR + y + JDBCTeleToEdb.KEY_SEPERATOR + level;
//		
//		String strStmtString = "insert into " + this.TABLENAMESTR + " (key, value) values(\""
//		+ keyString + "\", ?);";
//		Stmt stmt= this.db.prepare(strStmtString);
//		stmt.bind(1, value);
//		stmt.step();
//
//		this.bChanged = true;
//
//		return true;
//	}
	
	/**
	 * 鎻掑叆涓�釜Block鍧�
	 * @param level		Block鏁版嵁鍧楁墍鍦↙evel
	 * @param x			Block鏁版嵁鍧楃殑x缂栧彿
	 * @param y			Block鏁版嵁鍧楃殑y缂栧彿
	 * @param value		Block鏁版嵁鍧楁暟鎹�
	 * @return true锛氭垚鍔燂紝false锛氬け璐�
	 * @throws Exception 寮傚父
	 * @throws SQLException 
	 */
	public boolean insertJDBCBlock(byte level, int x, int y, byte[] value) throws Exception, SQLException{
		String keyString = "" + x + JDBCTeleToEdb.KEY_SEPERATOR + y + JDBCTeleToEdb.KEY_SEPERATOR + level;
		
		String strStmtString = "insert into " + this.TABLENAMESTR + " (key, value) values(\""
		+ keyString + "\", ?);";
//		Stmt stmt= this.db.prepare(strStmtString);
//		stmt.bind(1, value);
//		stmt.step();
		this.statement = this.connection.createStatement();
		this.bChanged = statement.execute(strStmtString);
		return true;
	}
	
	/**
	 * 鎻掑叆涓�釜Block鍧�
	 * @param level		Block鏁版嵁鍧楁墍鍦↙evel
	 * @param x			Block鏁版嵁鍧楃殑x缂栧彿
	 * @param y			Block鏁版嵁鍧楃殑y缂栧彿
	 * @param value		Block鏁版嵁鍧楁暟鎹�
	 * @return true锛氭垚鍔燂紝false锛氬け璐�
	 * @throws Exception 寮傚父
	 * @throws SQLException 
	 */
	public boolean insertJDBCBlockADDBATCH(byte level, int x, int y, byte[] value) throws Exception, SQLException{
		String keyString = "" + x + JDBCTeleToEdb.KEY_SEPERATOR + y + JDBCTeleToEdb.KEY_SEPERATOR + level;
		
		String strStmtString = "insert into " + this.TABLENAMESTR + " (key, value) values(\""
		+ keyString + "\", ?);";
//		Stmt stmt= this.db.prepare(strStmtString);
//		stmt.bind(1, value);
//		stmt.step();
		this.statement = this.connection.createStatement();
//		this.bChanged = statement.execute(strStmtString);
		statement.addBatch(strStmtString);
		return true;
	}
	
	/**
	 * 鎻掑叆涓�釜Block鍧�
	 * @param level		Block鏁版嵁鍧楁墍鍦↙evel
	 * @param x			Block鏁版嵁鍧楃殑x缂栧彿
	 * @param y			Block鏁版嵁鍧楃殑y缂栧彿
	 * @param value		Block鏁版嵁鍧楁暟鎹�
	 * @return true锛氭垚鍔燂紝false锛氬け璐�
	 * @throws Exception 寮傚父
	 * @throws SQLException 
	 */
	public boolean insertJDBCPAREBlockADDBATCH(byte level, int x, int y, byte[] value) throws Exception, SQLException{
		String keyString = "" + x + JDBCTeleToEdb.KEY_SEPERATOR + y + JDBCTeleToEdb.KEY_SEPERATOR + level;
//		Stmt stmt= this.db.prepare(strStmtString);
//		stmt.bind(1, value);
//		stmt.step();
		if(this.preparedStatement == null){
			this.preparedStatement = this.connection.prepareStatement(strSqlString);
		}
		preparedStatement.setString(1, keyString);
		preparedStatement.setBytes(2, value);
		if(set.contains(keyString)){
			System.out.println(keyString);
		}else{
			set.add(keyString);
		}
//		this.bChanged = statement.execute(strStmtString);
		preparedStatement.addBatch();
		return true;
	}
	/**
	 * 鍒ゆ柇琛ㄦ槸鍚﹀凡缁忓瓨鍦�
	 * @param strTableName 琛ㄥ悕
	 * @return true:鎴愬姛,false:澶辫触
	 * @throws Exception 寮傚父
	 */
//	private boolean isTableExist(String strTableName) throws Exception{
//		String strStmtString = "SELECT COUNT(*) FROM sqlite_master where type='table' and name=\""
//			+ strTableName + "\";";
//
//		Stmt stmt = this.db.prepare(strStmtString);
//		stmt.step();
//		int iCount = stmt.column_int(0);
//		if (0 < iCount) {
//			return true;
//		}
//		return false;
//	}
	
	/**
	 * 鍒ゆ柇琛ㄦ槸鍚﹀凡缁忓瓨鍦�
	 * @param strTableName 琛ㄥ悕
	 * @return true:鎴愬姛,false:澶辫触
	 * @throws Exception 寮傚父
	 */
	private boolean isJDBCTableExist(String strTableName) throws Exception {
		String strStmtString = "SELECT COUNT(*) FROM sqlite_master where type='table' and name=\""
				+ strTableName + "\";";

		if (this.connection == null) {
			return false;
		}

		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(strStmtString);
			int iCount = rs.getInt(1);
			if (0 < iCount) {
				return true;
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 璇诲彇涓�釜block鏁版嵁
	 * @param level		Block鏁版嵁鍧楁墍鍦↙evel
	 * @param x			Block鏁版嵁鍧楃殑x缂栧彿
	 * @param y			Block鏁版嵁鍧楃殑y缂栧彿
	 * @return		Block鏁版嵁鍧楁暟鎹紝澶辫触鏃惰繑鍥瀗ull銆�
	 */
//	public byte[] readBlock(byte level, int x, int y){
//		String keyString = "" + x + JDBCTeleToEdb.KEY_SEPERATOR + y + JDBCTeleToEdb.KEY_SEPERATOR + level;
//		String strStmtString = "select value from " + this.TABLENAMESTR
//		+ " where key=\"" + keyString + "\" ;";
//
//		try {
//			Stmt stmt = this.db.prepare(strStmtString);
//			stmt.step();
//			byte[] value = stmt.column_bytes(0);
//			return value;
//		} catch (SQLite.Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}

//	/**
//	 * 娴嬭瘯鍏ュ彛鍑芥暟
//	 * @param aString 鍛戒护琛屽弬鏁�
//	 * @return 鎵ц缁撴灉
//	 */
//	public static int main(String[] aString ){
//		try {
//			TeleToEdb st = new TeleToEdb("d:/temp/鍖椾含甯�tele.jpg", false, false);
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
//	 * 娴嬭瘯鍏ュ彛鍑芥暟
//	 * @param aString 鍛戒护琛屽弬鏁�
//	 */
//	public static void main2(String aString []){
//		GeoRect dataRect = new GeoRect();
//		// 鍖椾含甯�
//		String strProvinceNameString = "鍖椾含甯�; 
//		dataRect.LeftDown.Longitude = 1063981319;
//		dataRect.LeftDown.Latitude = 364141665;
//		dataRect.RightUp.Longitude = 1082514525;
//		dataRect.RightUp.Latitude = 378271753;
//
//		// 骞夸笢鐪�
////		String strProvinceNameString = "骞夸笢鐪�;
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
