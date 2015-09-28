package com.sunmap.businessDao.Parse;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mansion.tele.business.DataManager;
import com.mansion.tele.business.DataManager.LevelInfo;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.common.UnitNo;
import com.sunmap.been.BlockData;

public class ParseViewByBlocks {
	
	public static Comparator<BlockData> comparator = new Comparator<BlockData>() {
		
		public int compare(BlockData o1, BlockData o2) {
			if(o1.level == o2.level){
				if(o1.blocky == o2.blocky){
					return o1.blockx - o2.blockx;
				}else{
					return o1.blocky - o2.blocky;
				}
			}else{
				return o1.level - o2.level;
			}
		}
	};
	
	private List<BlockData> blocks;
	
	public ParseViewByBlocks(List<BlockData> blockDatas) {
		this.blocks = blockDatas;
	}
	
	public List<BlockData> parse(File view) throws IOException {
		Collections.sort(this.blocks, comparator);
		RandomAccessFile dataInputStream = new RandomAccessFile(view, "r");
		RandomAccessFile blockDataInputStream = new RandomAccessFile(view, "r");
		int headsize = dataInputStream.readByte() & 0xff;
		int unitmanagersize = dataInputStream.readInt();
		int blockmanagersize = dataInputStream.readInt();
		long blockdatasize = dataInputStream.readLong();
		int levelnum = dataInputStream.readByte();
		List<LeveLManager> levelms = new ArrayList<LeveLManager>();
		for (int i = 0; i < levelnum; i++) {
			LeveLManager leveLManager = new LeveLManager();
			leveLManager.level = dataInputStream.readShort();
			leveLManager.fristunitoffset = dataInputStream.readInt();
			leveLManager.minunitx = dataInputStream.readInt();
			leveLManager.minunity = dataInputStream.readInt();
			leveLManager.unitxnum = dataInputStream.readInt();
			leveLManager.unitynum = dataInputStream.readInt();
			levelms.add(leveLManager);
		}
		
		for (int i = 0; i < blocks.size(); i++) {
			BlockData blockData = blocks.get(i);
			LeveLManager leveLManager = levelms.get(blockData.level);
			LevelInfo levelInfo = DataManager.getLevelInfo(leveLManager.level);
			int unitindex = (blockData.unitx - leveLManager.minunitx) + ((blockData.unity - leveLManager.minunity) * leveLManager.unitxnum);
			dataInputStream.seek(headsize + unitindex * 12 + leveLManager.fristunitoffset);
			int unitx = dataInputStream.readInt() & 0xffffffff;
			int unity = dataInputStream.readInt() & 0xffffffff;
			int offset = dataInputStream.readInt() & 0xffffffff;
			if(offset == -1){
				continue;
			}
			if(unitx == blockData.unitx && unity == blockData.unity){
				UnitNo unitNo = new UnitNo();
				unitNo.iX = unitx;
				unitNo.iY = unity;
				int blockxnum = levelInfo.unitWidth / levelInfo.iBlockWidth;
				int blockynum = levelInfo.unitHeight / levelInfo.iBlockHight;
				BlockNo minblockNo = BlockNo.valueOf(unitNo.toGeoLocation((byte) blockData.level), levelInfo.iBlockWidth, levelInfo.iBlockHight);
				int blockindex = (blockData.blockx - minblockNo.iX) + (blockData.blocky - minblockNo.iY) * blockxnum;
				dataInputStream.seek(headsize + unitmanagersize + offset + blockindex * 10);
				long blockoffset = dataInputStream.readLong();
				int size = dataInputStream.readShort() & 0xffff;
				if(blockoffset == -1){
					continue;
				}
				blockDataInputStream.seek(headsize + unitmanagersize + blockmanagersize + blockoffset);
				blockData.data = new byte[size * 2];
				blockDataInputStream.read(blockData.data);
				blockData.size = blockData.data.length;
				
			}
		}
		dataInputStream.close();
		blockDataInputStream.close();
		return blocks;
	}
	
	class LeveLManager{
		public short level;
		public int fristunitoffset;
		public int minunitx;
		public int minunity;
		public int unitxnum;
		public int unitynum;
		
	}
	
	class UnitManager{
		public int unitx;
		public int unity;
		public int fristBlockOffset;
	}

	
}
