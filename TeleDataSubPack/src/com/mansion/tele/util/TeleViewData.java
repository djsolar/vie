package com.mansion.tele.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.mansion.tele.business.T7.util.BasicDataUtil;
import com.mansion.tele.business.common.MapDivInfo;
import com.mansion.tele.business.common.ScaleKey;
import com.mansion.tele.business.common.TeleConfig;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.common.GeoLocation;
import com.mansion.tele.common.UnitNo;
/**
 * 
 * @author Administrator
 *
 */
public class TeleViewData {
	/**
	 * Unit文件头的size
	 */
	public static final int IUNITHEADERSIZELEN = 18;
	/**
	 * Block管理记录偏移的长度
	 */
	public static final int IBLOCKMANAGEOFFSETLEN = 4;
	/**
	 * Block管理记录size的长度
	 */
	public static final int IBLOCKMANAGESIZELEN = 2;
	/**
	 * 分包数据的路径
	 */
	private String strFolderPathString;
	/**
	 * TeleView数据对象
	 */
	private TeleViewFile vieFile;
	/**
	 * @param strFolderPath	分包数据的路径
	 */
	public TeleViewData(String strFolderPath){
		this.strFolderPathString = strFolderPath;
	}
	/**
	 * 
	 */
	public TeleViewData() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 取得Block对应数据块
	 * @param level		level
	 * @param blockX	X编号
	 * @param blockY	Y编号
	 * @return 如果失败返回null。
	 * @throws Exception 取得Block的异常。
	 */
	public byte[] getBlock(int level, int blockX, int blockY) throws Exception{

		
		if (null == this.vieFile || !this.vieFile.containBlock(blockX, blockY)){

			// 计算Unit号
			BlockNo blockNo = new BlockNo(blockX, blockY);
			int blockDiv[] = TeleConfig.get().getBlockDivInfo(level);
			GeoLocation geoLocation = blockNo.toGeoLocation(blockDiv[0], blockDiv[1]);
			
			UnitNo unitNo = UnitNo.valueOf(geoLocation, (byte) level);

			this.vieFile = new TeleViewFile(level, unitNo.iX, unitNo.iY, 
								this.getUnitFilePath(level, unitNo.iX, unitNo.iY));
			
		}
		
		return this.vieFile.getBlock(blockX, blockY);
	}

	/**
	 * 取得Block对应数据块
	 * @param level		level
	 * @param blockX	X编号
	 * @param blockY	Y编号
	 * @return 如果失败返回null。
	 * @throws Exception 取得Block的异常。
	 */
	public byte[] getBlockForEDB(int level, int blockX, int blockY) throws Exception{

		
		if (null == this.vieFile || !this.vieFile.containBlock(blockX, blockY)){

			// 计算Unit号
			BlockNo blockNo = new BlockNo(blockX, blockY);
			int blockDiv[] = TeleConfig.get().getBlockDivInfo(level);
			GeoLocation geoLocation = blockNo.toGeoLocation(blockDiv[0], blockDiv[1]);
			
			UnitNo unitNo = UnitNo.valueOf(geoLocation, (byte) level);

			this.vieFile = new TeleViewFile(level, unitNo.iX, unitNo.iY, 
								this.getUnitFilePath(level, unitNo.iX, unitNo.iY));
			
		}
		
		return this.vieFile.getBlockForEDB(blockX, blockY);
	}
	
	/**
	 * 取得Unit文件路径
	 * @param level		level
	 * @param unitX		Unit文件X编号
	 * @param unitY		Unit文件Y编号
	 * @return			返回Unit文件路径			
	 */
	private String getUnitFilePath(int level, int unitX, int unitY){
		
		String sign = "/";
		
		String style = "%06d";
		
		String strUnitX = String.format(style, unitX);
		
		String strUnitY = String.format(style, unitY);
		
		return this.strFolderPathString 
					+ sign 
					+ level 
					+ sign 
					+ strUnitX 
					+ sign 
					+ strUnitY + ".tuv";
	}
}

/**
 * View文件操作类。
 * @author yangzhen
 *
 */
class TeleViewFile{
	public final int EDB_START_FROM_BLOCK = 12;
	int iLevel;
	int iUnitX;
	int iUnitY;
	int iBlockXStart;
	int iBlockXEnd;
	int iBlockYStart;
	int iBlockYEnd;
	
	class ManageData{
		int offset;
		int size;
	}
	/**
	 * 缓存
	 */
	private Map<BlockNo, TeleViewFile.ManageData> manageMap = new HashMap<BlockNo, TeleViewFile.ManageData>();
	/**
	 * block内容
	 */
	private byte[] buffer;
	/**
	 * 
	 * @param level int
	 * @param iUnitX int
	 * @param iUnitY int
	 * @param strFilePath String
	 * @throws Exception 1
	 */
	public TeleViewFile(int level, int iUnitX, int iUnitY, String strFilePath) throws Exception{
		this.iUnitX = iUnitX;
		this.iUnitY = iUnitY;
		this.iLevel = level;
		
		// 通过Level计算Block的编号的范围
		this.calcBlockNoRange();
		
		// 加载文件到内存中
		this.load(strFilePath);
		
		// 取得Block在文件中的管理记录信息
		this.getManage();
	}
	
	/**
	 * 是否包含Block
	 * @param blockX	blick的X编号	
	 * @param blockY	blick的Y编号
	 * @return true:包含；false:不包含。
	 */
	public boolean containBlock(int blockX, int blockY){
		if (this.iBlockXStart > blockX || this.iBlockXEnd < blockX
				|| this.iBlockYStart > blockY || this.iBlockYEnd < blockY){
			return false;
		}
		
		return true;
	}
	
	/**	取得Block数据
	 * @param blockX	取得数据的BlockX
	 * @param blockY	取得数据的BlockY
	 * @return			返回BlockBuffer
	 * @throws Exception 1
	 */
	public byte[] getBlock(int blockX, int blockY) throws Exception{
		if (!this.containBlock(blockX, blockY)){
			return null;
		}
		
		BlockNo blockNo = new BlockNo(blockX, blockY);
		ManageData manageData = this.manageMap.get(blockNo);
		
		byte[] byBlock = null;
		
		if (null == manageData || 0xffffffff == manageData.offset ) {
			byBlock = null;
		}else{
			
			byBlock = Arrays.copyOfRange(this.buffer, manageData.offset,
										manageData.offset + manageData.size);
		}
		return byBlock;
	}
	
	/**	取得Block数据
	 * @param blockX	取得数据的BlockX
	 * @param blockY	取得数据的BlockY
	 * @return			返回BlockBuffer
	 * @throws Exception 1
	 */
	public byte[] getBlockForEDB(int blockX, int blockY) throws Exception{
		if (!this.containBlock(blockX, blockY)){
			return null;
		}
		
		BlockNo blockNo = new BlockNo(blockX, blockY);
		ManageData manageData = this.manageMap.get(blockNo);
		
		byte[] byBlockArr = null;
		
		if (null == manageData || 0xffffffff == manageData.offset ) {
			byBlockArr =  null;
		}else{
			
			byBlockArr = Arrays.copyOfRange(this.buffer, 
					manageData.offset + this.EDB_START_FROM_BLOCK,
					manageData.size + manageData.offset);
		}

		return byBlockArr;
	}
	
	
	/**
	 * 	取得Unit文件中的管理记录信息
	 * @throws Exception 1
	 */
	private void getManage()throws Exception{
		
		if (null == this.buffer) {
			return;
		}

			int iBlockManageOffset = TeleViewData.IUNITHEADERSIZELEN;
			for (int j = this.iBlockYStart; j <= this.iBlockYEnd; j++) {
				for (int i = this.iBlockXStart; i <= this.iBlockXEnd; i++) {
					BlockNo blockNo = new BlockNo(i, j);
					
					ManageData manageData = new ManageData();
					// 偏移
					byte []arcBlockOffset = Arrays.copyOfRange(this.buffer, 
							iBlockManageOffset, 
							iBlockManageOffset + TeleViewData.IBLOCKMANAGEOFFSETLEN);
					manageData.offset =BasicDataUtil.converIntFromByte(arcBlockOffset);
					iBlockManageOffset += TeleViewData.IBLOCKMANAGEOFFSETLEN;
					// BlockSize
					byte []arcBlockSize = Arrays.copyOfRange(this.buffer, 
							iBlockManageOffset, 
							iBlockManageOffset + TeleViewData.IBLOCKMANAGESIZELEN);
					manageData.size =BasicDataUtil.converIntFromByte(arcBlockSize) * 2;
					iBlockManageOffset += TeleViewData.IBLOCKMANAGESIZELEN;
					
					this.manageMap.put(blockNo, manageData);
				}
			}
			
	}
	
	/**
	 * 	计算当前Unit文件中BlockNo的范围
	 */
	private void calcBlockNoRange(){
		// Block的宽和高
		int blockDiv[] = TeleConfig.get().getBlockDivInfo(this.iLevel);
		
		MapDivInfo mapDiv = TeleConfig.get().getMapDivInfo(new ScaleKey(this.iLevel, 1));
		
		this.iBlockXStart = this.iUnitX * mapDiv.getiUnitWidthToGeo() / blockDiv[0];
		this.iBlockYStart = this.iUnitY * mapDiv.getiUnitHightToGeo() / blockDiv[1];
		
		this.iBlockXEnd = this.iBlockXStart + mapDiv.getiUnitWidthToGeo() / mapDiv.getiBlockWidthToGeo() - 1;
		this.iBlockYEnd = this.iBlockYStart + mapDiv.getiUnitHightToGeo() / mapDiv.getiBlockHightToGeo() - 1;
		
	}
	/**
	 * 加载文件到内存。
	 * @param strFilePath	文件路径。
	 * @throws IOException	IO异常。
	 */
	private void load(String strFilePath) throws IOException{
		File file = new File(strFilePath);
		if (!file.exists()) {
			return;
		}
		
		this.buffer = new byte[(int) file.length()];

		FileInputStream fileStream = new FileInputStream(file);
		BufferedInputStream fileBufferedInputStream = new BufferedInputStream(fileStream);
		RandomAccessFile file2 = new RandomAccessFile(file, "r");
		fileBufferedInputStream.read(this.buffer, 0, this.buffer.length);
		fileBufferedInputStream.close();
		fileStream.close();
	}
	
//	public static void main(String [] a){
//		try {
//			TeleViewData teleViewData = new TeleViewData("D:/work_1920000/data/view");
//			byte[] blockBuffer  = teleViewData.getBlock(5, 26, 15);
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
