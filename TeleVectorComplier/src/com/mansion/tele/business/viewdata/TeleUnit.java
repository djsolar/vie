package com.mansion.tele.business.viewdata;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mansion.tele.business.DataManager;
import com.mansion.tele.business.DataManager.LevelInfo;
import com.mansion.tele.business.Task;
import com.mansion.tele.business.common.BaseShape;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.common.ConstantValue;
import com.mansion.tele.common.GeoLocation;
import com.mansion.tele.common.UnitNo;

//class allData {
//}
/**
 * 
 */
public abstract class TeleUnit {

//	protected allData data;
	/**
	 * block对应数据信息
	 */
	protected Map<BlockNo, byte[]> blockMap = new HashMap<BlockNo, byte[]>();
	/**
	 * block集合
	 */
	protected List<BlockNo> blockNoList = new ArrayList<BlockNo>();
	/**
	 * unit下blockx数
	 */
	protected short unitBlockCountX;
	/**
	 * unit下blocky数
	 */
	protected short unitBlockCountY;
	/**
	 * unit矩形范围
	 */
	protected GeoLocation geoLocation = new GeoLocation();
//	/***
//	 * unit长宽
//	 */
//	protected long[] unitWidthAndHight;
//	/**
//	 * block长宽
//	 */
//	protected int[] blockWidthAndHight;

	/**
	 * 制作Unit的二进制文件
	 * 
	 * @param task Task
	 * @return int 
	 * @throws Exception 1
	 */
	public void makeUnit(String filePath,Task task,ViewData data) throws Exception{
		LevelInfo levelInfo = DataManager.getLevelInfo(task.getLevel());
		this.geoLocation = GeoLocation.valueOf(task.getLeft(),
				task.getBottom());

		// 收集背景,道路和标记数据
		List<BlockNo> allTeleShp = null;
		//替换该方法  
		allTeleShp = this.collectData(task,data);
		
		if (!allTeleShp.isEmpty()) {
			// x、y方向个数
			this.unitBlockCountX = (short) (levelInfo.unitWidth / levelInfo.iBlockWidth);
			this.unitBlockCountY = (short) (levelInfo.unitHeight / levelInfo.iBlockHight);

			// 制作整个unit文件的block编号
			BlockNo firstBlockNo = BlockNo.valueOf(this.geoLocation, levelInfo.iBlockWidth, levelInfo.iBlockHight);
			this.blockNoList = new ArrayList<BlockNo>();
			this.makeBlcokNoMap(this.blockNoList, firstBlockNo,	this.unitBlockCountX, this.unitBlockCountY);

			// 过滤超出任务范围的数据
			BlockNo rightUpBlockNo = new BlockNo(firstBlockNo.getiX() + this.unitBlockCountX - 1, 
					firstBlockNo.getiY() + this.unitBlockCountX - 1);
			 this.filtrateBlockBeyondTask(allTeleShp, firstBlockNo, rightUpBlockNo);
	
			if (!allTeleShp.isEmpty()) {
				for (BlockNo blockNo : allTeleShp) {
					byte[] blockBody = this.makeBlock(task.getLevel(), blockNo);
					final int iMaxLength = 131070;
					if (blockBody.length > iMaxLength) {
						System.err.println("此block数据长度过大" +
								"，blockNo为:"
								+ blockNo.getiX() + ", " + blockNo.getiY()+" 此block数据长度为："+blockBody.length);
					} 
					else {
						
					}
					this.blockMap.put(blockNo, blockBody);
				}
				this.makeUnitFile(filePath,task, this.blockMap);
			}
		}
	}

	/**
	 * 过滤超出任务范围的数据
	 * @param allTeleShp		加载数据的Block集合
	 * @param leftDownBlockNo	当前任务左下Block号
	 * @param rightUpBlockNo	当前任务右上Block号
	 */
	private void filtrateBlockBeyondTask(List<BlockNo> allTeleShp, BlockNo leftDownBlockNo, BlockNo rightUpBlockNo){
		
		int i = 0;
		long filTime = System.currentTimeMillis();
		while(i < allTeleShp.size()) {
			if (allTeleShp.get(i).getiX() < leftDownBlockNo.getiX()||
					allTeleShp.get(i).getiY() < leftDownBlockNo.getiY()||
					allTeleShp.get(i).getiX() > rightUpBlockNo.getiX()||
					allTeleShp.get(i).getiY() > rightUpBlockNo.getiY()) {
				
				allTeleShp.remove(i);
				i--;
			}
			i++;
		}
	}

	/**
	 * 收集信息
	 * 
	 * @param task Task
	 * @return List<BlockNo> blockNo集合
	 * @throws Exception 1
	 */
	protected abstract List<BlockNo> collectData(Task task,ViewData data) throws Exception;
	/**
	 * 制作Block的二进制信息
	 * 
	 * @param level byte
	 *            输入block中所有的形状数据
	 * @param blickNo BlockNo            
	 * @return block 的byte[]
	 * @throws Exception 1
	 */
	protected abstract byte[] makeBlock(byte level, BlockNo blickNo) throws Exception;

	/**
	 * 组织unit文件
	 * 
	 * @param task Task
	 * @param blockMap Map<BlockNo, byte[]>
	 * @return int
	 * @throws Exception 1
	 */
	private int makeUnitFile(String filePath,Task task, Map<BlockNo, byte[]> blockMap)throws Exception {
		LevelInfo levelInfo = DataManager.getLevelInfo(task.getLevel());
		// unit编号 0:x编号，1：y编号
		UnitNo unitXY = UnitNo.calcUnitNo(this.geoLocation, levelInfo.unitWidth, levelInfo.unitHeight);

		long blockSize = this.totalBlockSize(blockMap);
		if (0 == blockSize) { // 空数据判断
			return ConstantValue.STATUS_NOTDATA;
		}
		// 判断被2整除
		if (0 != blockSize % 2) {
			Exception e = new Exception();
			e.printStackTrace();
			System.err.println("blockSize计算错误(不能被2整除) ： size=" + blockSize);
			throw e;
//			System.exit(-1);
		}

		// block排序
		List<byte[]> blockList = this.getSortedBlock(blockMap);

		final int six = 6;
		
		final int eighteen = 18;
		//可能会溢出
		long unitSize = (int) this.unitBlockCountX * this.unitBlockCountY * six + eighteen
				+ blockSize;
		if(unitSize>Integer.MAX_VALUE){
			System.err.println("unitSize超过范围: "+unitSize+"UnitNo: "+unitXY.iX+","+unitXY.iY);
			System.exit(-1);
		}
		byte[] unit = null;
		
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bao);

			dos.write(task.getLevel());
			dos.write(1);
			dos.writeInt(unitXY.iX);
			dos.writeInt(unitXY.iY);
			dos.writeShort(this.unitBlockCountX);
			dos.writeShort(this.unitBlockCountY);
			dos.writeInt((int)unitSize / 2);
			this.makeBlockRecordsManagement(blockList, dos, (int)(unitSize - blockSize),
					blockMap, this.blockNoList);
			for (int i = 0; i < blockList.size(); i++) {
				dos.write(blockList.get(i), 0, blockList.get(i).length);
			}
			dos.close();

			unit = bao.toByteArray();
			if (unit.length != unitSize) {
				Exception e = new Exception();
				e.printStackTrace();
				System.err.println("Unitsize计算错误 ： 计算size=" + unitSize
						+ ", 数据size=" + unit.length);
//				System.exit(-1);
				throw e;
			}
		
		this.writeFile(filePath,unit, task.getPath());
		return ConstantValue.STATUS_SUCCESS;
	}

	/**
	 * 制作一个unit中的所有blockno编号
	 * 
	 * @param blockNoList List<BlockNo>
	 * @param firstBlockNo BlockNo
	 * @param blockWidthAndHight int[]
	 * @param unitBlockCountX int
	 * @param unitBlockCountY int
	 */
	private void makeBlcokNoMap(List<BlockNo> blockNoList,
			BlockNo firstBlockNo,int unitBlockCountX, int unitBlockCountY) {
		for (int j = 0; j < unitBlockCountY; j++) {
			for (int i = 0; i < unitBlockCountX; i++) {
				BlockNo blockNo = new BlockNo();
				blockNo.setiX(firstBlockNo.getiX() + i);
				blockNo.setiY(firstBlockNo.getiY() + j);
				blockNoList.add(blockNo);
			}
		}
	}

	/**
	 * Block管理记录排列
	 * 
	 * @param blockList List<byte[]>
	 * @param dos DataOutputStream
	 * @param oldoffect int
	 * @param blockMap Map<BlockNo, byte[]>
	 * @param blockNoList List<BlockNo>
	 * @throws Exception 1
	 */
	private void makeBlockRecordsManagement(List<byte[]> blockList,
			DataOutputStream dos, int oldoffect, Map<BlockNo, byte[]> blockMap,
			List<BlockNo> blockNoList) throws Exception{
		
			int offect = oldoffect;
		
			for (int i = 0; i < blockNoList.size(); i++) {
				if (blockMap.get(blockNoList.get(i)) == null||blockMap.get(blockNoList.get(i)).length==0) {
					dos.writeInt(-1);
					dos.writeShort(0);
				} else {
					int length = blockMap.get(blockNoList.get(i)).length;
					dos.writeInt(offect);
					dos.writeShort(length / 2);
					offect += length;
				}
			}
			dos.close();
		
	}

	/**
	 * 创建结果文件
	 * 
	 * @param level byte
	 * @param unitX int
	 * @param unitY int
	 * @return File
	 * @throws Exception 1
	 */
	protected abstract File createResultFile(String filePath) throws Exception;

	/**
	 * 写入文件
	 * 
	 * @param unit byte[]
	 * @param level byte
	 * @param unitXY UnitNo
	 * @return byte
	 * @throws Exception 1
	 */
	private byte writeFile(String filePath,byte[] unit, String taskPath)throws Exception {
		
		File file = this.createResultFile(filePath+File.separator+taskPath);
		DataOutputStream out = new DataOutputStream(
		new BufferedOutputStream(new FileOutputStream(file)));
		out.write(unit, 0, unit.length);
		out.close();
		
		return ConstantValue.STATUS_SUCCESS;
	}

	/**
	 * 统计Block数据的size
	 * 
	 * @param blockMap Map<BlockNo, byte[]>
	 * @return int
	 */
	private long totalBlockSize(Map<BlockNo, byte[]> blockMap) {
		long iSize = 0;
		for (Map.Entry<BlockNo, byte[]> member : blockMap.entrySet()) {
			iSize += member.getValue().length;
		}

		return iSize;
	}

	/**
	 * block排序
	 * 
	 * @param blockMap Map<BlockNo, byte[]>
	 * @return List<byte[]>
	 */
	private List<byte[]> getSortedBlock(Map<BlockNo, byte[]> blockMap) {
		List<BlockNo> blockNoList = new ArrayList<BlockNo>();
		for (Map.Entry<BlockNo, byte[]> member : blockMap.entrySet()) {
			blockNoList.add(member.getKey());
		}
		Collections.sort(blockNoList);
		List<byte[]> blockList = new ArrayList<byte[]>();
		for (BlockNo block : blockNoList) {
			blockList.add(blockMap.get(block));
		}
		//
		// List<byte[]> blockList = new ArrayList<byte[]>();
		// Object[] blockNo = (Object[]) blockMap.keySet().toArray();
		// for (int i = 0; i < blockNo.length; i++) {
		// for (int j = i + 1; j < blockNo.length; j++) {
		// if (((BlockNo) blockNo[i]).iY < ((BlockNo) blockNo[j]).iY) {
		// continue;
		// } else if (((BlockNo) blockNo[i]).iY == ((BlockNo) blockNo[j]).iY) {
		// if (((BlockNo) blockNo[i]).iX < ((BlockNo) blockNo[j]).iX) {
		// continue;
		// } else {
		// BlockNo tmp = new BlockNo(((BlockNo) blockNo[i]).iX,
		// ((BlockNo) blockNo[i]).iY);
		// blockNo[i] = blockNo[j];
		// blockNo[j] = tmp;
		// }
		// } else if (((BlockNo) blockNo[i]).iY > ((BlockNo) blockNo[j]).iY) {
		// BlockNo tmp = new BlockNo(((BlockNo) blockNo[i]).iX,
		// ((BlockNo) blockNo[i]).iY);
		// blockNo[i] = blockNo[j];
		// blockNo[j] = tmp;
		// }
		// }
		// blockSize += blockMap.get(blockNo[i]).length;
		// blockList.add(blockMap.get(blockNo[i]));
		// }
		return blockList;
	}

	/**
	 * 转换数据类型
	 * 
	 * @param baseShape List
	 * @param shape List
	 * @return List
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List typeChange(List<BaseShape> baseShape, List shape) {
		for (int i = 0; i < baseShape.size(); i++) {
			shape.add(baseShape.get(i));
		}
		return shape;
	}

	
}
