package com.sunmap.businessDao.TransForm;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.sunmap.been.BlockData;
import com.sunmap.businessDao.Parse.ParseViewByBlocks;
import com.sunmap.util.Constant;

public class TransFormToPubData {

	public void transForm(List<BlockData> blockDatas, String cityName)
			throws IOException, DataFormatException {

		File dataFile = new File(Constant.outPath + File.separator + cityName);
		if (dataFile.exists()) {
			dataFile.delete();
		}
		dataFile.createNewFile();
		// level管理记录
		ByteArrayOutputStream levelbaos = new ByteArrayOutputStream();
		DataOutputStream leveldos = new DataOutputStream(levelbaos);
		// block数据
		ByteArrayOutputStream blockbaos = new ByteArrayOutputStream();
		DataOutputStream blockdos = new DataOutputStream(blockbaos);
		// 压缩的block数据
		Deflater def = new Deflater(Deflater.BEST_COMPRESSION);
		// block管理记录
		ByteArrayOutputStream blockMGbaos = new ByteArrayOutputStream();
		DataOutputStream blockMGdos = new DataOutputStream(blockMGbaos);
		// 组装后的文件
		FileOutputStream fos = new FileOutputStream(dataFile);
		DataOutputStream dos = new DataOutputStream(fos);
		// block偏移量
		int offset = 0;
		// block总数
		int blockCount = 0;
		// 压缩后数据块最大值
		int maxSize = 0;
		// 单个block最大值
		int maxBlockSize = 0;
		Collections.sort(blockDatas, ParseViewByBlocks.comparator);
		List<LevelManager> levelMGs = this.getLevelMG(blockDatas);
		// level数
		leveldos.writeByte(levelMGs.size());
		for (int i = 0; i < levelMGs.size(); i++) {
			blockMGdos.flush();
			LevelManager levelMG = levelMGs.get(i);
			leveldos.writeByte(levelMG.level);
			// 一个数据块长宽block数
			leveldos.writeByte(Constant.blockCount);
			leveldos.writeShort(levelMG.minblockx);
			leveldos.writeShort(levelMG.minblocky);
			leveldos.writeShort(levelMG.maxblockx);
			leveldos.writeShort(levelMG.maxblocky);
			leveldos.writeInt(blockMGbaos.toByteArray().length);
//			System.out.println(blockMGbaos.toByteArray().length);
			blockCount = blockCount
					+ (levelMG.maxblockx - levelMG.minblockx + 1)
					* (levelMG.maxblocky - levelMG.minblocky + 1);
			for (int j = levelMG.minblocky; j <= levelMG.maxblocky; j = j + 3) {
				for (int k = levelMG.minblockx; k <= levelMG.maxblockx; k = k + 3) {
					// 大数据块,管理记录 + n*n个block
					ByteArrayOutputStream blockdatabaos = new ByteArrayOutputStream();
					DataOutputStream blockdatados = new DataOutputStream(
							blockdatabaos);
					// 小管理记录
					ByteArrayOutputStream sblockMGbaos = new ByteArrayOutputStream();
					DataOutputStream sblockMGdos = new DataOutputStream(sblockMGbaos);
					// block数据
					ByteArrayOutputStream sblockbaos = new ByteArrayOutputStream();
					DataOutputStream sblockdos = new DataOutputStream(sblockbaos);
					
					// 有数据的block
					int dataCount = 0;
					// 没数据但是在城市范围内
					int noneCount = 0;
					for (int p = j; p < j + 3; p++) {
						for (int q = k; q < k + 3; q++) {
							BlockData block = new BlockData();
							block.level = levelMG.level;
							block.blockx = q;
							block.blocky = p;
							int index = Collections.binarySearch(blockDatas,
									block, ParseViewByBlocks.comparator);
							// 当前block号在分割后的block号中
							if (index >= 0) {
								BlockData blockData = blockDatas.get(index);
								if (blockData.data != null) {
									// 小管理记录
									sblockMGdos.writeShort(blockData.data.length / 2);
									// block数据
									sblockdos.write(blockData.data);
									dataCount ++;
									maxBlockSize = Math.max(maxBlockSize, blockData.data.length);
								}
								// 当前block在城市范围内但是没有数据
								else {
									sblockMGdos.writeShort(0);
									noneCount ++;
								}
							}
							// 当前block不在城市范围内
							else {
								sblockMGdos.writeShort(-1);
							}
						}
					}
					if(dataCount > 0){
						sblockMGdos.flush();
						sblockdos.flush();
						byte[] smg = sblockMGbaos.toByteArray();
						byte[] sblock = sblockbaos.toByteArray();
						blockdatados.write(smg);
						blockdatados.write(sblock);
						blockdatados.flush();
						byte[] data = blockdatabaos.toByteArray();
						byte[] buff = new byte[data.length];
						def.setInput(data);
						def.finish();
						// 压缩后大小
						int defsize = def.deflate(buff);
						def.reset();
						blockdos.write(buff, 0, defsize);
						blockMGdos.writeInt(offset);
						blockMGdos.writeInt(defsize);
						offset += defsize;
						maxSize = Math.max(maxSize, defsize);
					}
					else if(noneCount > 0){
						blockMGdos.writeInt(-1);
						blockMGdos.writeInt(0);
					}
					else{
						blockMGdos.writeInt(-1);
						blockMGdos.writeInt(-1);
					}
					sblockMGdos.close();
					sblockdos.close();
					blockdatados.close();
				}
			}
		}
		System.out.println(cityName + "转换后block总数：" + blockCount);
		System.out.println("压缩后最大数据块size：" + maxSize);
		System.out.println("最大blockSize：" + maxBlockSize);
//		 System.out.println(levelbaos.toByteArray().length);
//		 System.out.println(blockMGbaos.toByteArray().length);
//		 System.out.println(blockbaos.toByteArray().length);
		leveldos.flush();
		blockMGdos.flush();
		blockdos.flush();
		dos.write(levelbaos.toByteArray());
		dos.write(blockMGbaos.toByteArray());
		dos.write(blockbaos.toByteArray());
		leveldos.close();
		blockdos.close();
		blockMGdos.close();
		dos.flush();
		dos.close();
	}

	/**
	 * 获得此城市的level管理记录
	 * 
	 * @param blockDatas
	 * @return
	 */
	private List<LevelManager> getLevelMG(List<BlockData> blockDatas) {
		List<LevelManager> levelMGs = new ArrayList<TransFormToPubData.LevelManager>();
		// 前一个block的level
		int forwordLevel = blockDatas.get(0).level;
		int minx = Integer.MAX_VALUE;
		int miny = Integer.MAX_VALUE;
		int maxx = 0;
		int maxy = 0;
		LevelManager levelMG = null;
		for (int i = 0; i < blockDatas.size(); i++) {
			BlockData blockData = blockDatas.get(i);
			if (blockData.level != forwordLevel) {
				levelMG = new LevelManager();
				levelMG.level = blockData.level - 1;
				levelMG.minblockx = minx;
				levelMG.minblocky = miny;
				// 向右上扩充，组成完整数据块
				levelMG.maxblockx = maxx
						+ (3-((maxx - minx + 1) % Constant.blockCount));
				levelMG.maxblocky = maxy
						+ (3-((maxy - miny + 1) % Constant.blockCount));
				levelMGs.add(levelMG);
//				System.out.println(levelMG.toString());
				minx = blockData.blockx;
				miny = blockData.blocky;
				maxx = blockData.blockx;
				maxy = blockData.blocky;
			} else {
				minx = Math.min(minx, blockData.blockx);
				miny = Math.min(miny, blockData.blocky);
				maxx = Math.max(maxx, blockData.blockx);
				maxy = Math.max(maxy, blockData.blocky);
			}
			forwordLevel = blockData.level;
		}
		// 最后一层数据
		levelMG = new LevelManager();
		levelMG.level = forwordLevel;
		levelMG.minblockx = minx;
		levelMG.minblocky = miny;
		// 向右上扩充，组成完整数据块
		levelMG.maxblockx = maxx + (3-((maxx - minx + 1) % Constant.blockCount));
		levelMG.maxblocky = maxy + (3-((maxy - miny + 1) % Constant.blockCount));
//		System.out.println(levelMG.toString());
		levelMGs.add(levelMG);
		return levelMGs;
	}


	class LevelManager {
		int level;
		int minblockx, minblocky, maxblockx, maxblocky;
		int offset;
		@Override
		public String toString() {
			
			return "level:" + level + "minblockx:" + minblockx + "minblocky:" + minblocky + "maxblockx:" + maxblockx
					+ "maxblocky:" + maxblocky + "total:" + (maxblocky-minblocky+1) * (maxblockx - minblockx +1);
		}
	}

}
