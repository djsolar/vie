package com.sunmap.businessDao.Parse;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.business.DataManager;
import com.mansion.tele.business.DataManager.LevelInfo;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.common.UnitNo;
import com.sunmap.been.BlockData;

public class ParseView {
	
	
	public List<BlockData> parse(File view) throws IOException {
		
		List<BlockData>  blockdatas = new ArrayList<BlockData>();
		
		RandomAccessFile dataInputStream = new RandomAccessFile(view, "r");
		RandomAccessFile bmdataInputStream = new RandomAccessFile(view, "r");
		RandomAccessFile blockDataInputStream = new RandomAccessFile(view, "r");
		int headsize = dataInputStream.readByte() & 0xff;
		int unitmanagersize = dataInputStream.readInt();
		int blockmanagersize = dataInputStream.readInt();
		long blockdatasize = dataInputStream.readLong();
		int levelnum = dataInputStream.readByte();
		List<LeveLManager> levelms = new ArrayList<ParseView.LeveLManager>();
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
		
		int beforeunitx = 0;
		int beforeunity = 0;
		for (int i = 0; i < levelms.size(); i++) {
			LeveLManager leveLManager = levelms.get(i);
			if(leveLManager.level < 3){
				continue;
			}
			dataInputStream.seek(leveLManager.fristunitoffset + headsize);
			test:for (int j = 0; j < leveLManager.unitxnum * leveLManager.unitynum; j++) {
				UnitManager unitManager = new UnitManager();
				unitManager.unitx = dataInputStream.readInt() ;
				unitManager.unity = dataInputStream.readInt() ;
				unitManager.fristBlockOffset = dataInputStream.readInt() ;
				if(unitManager.fristBlockOffset == -1){
					continue;
				}
				LevelInfo levelInfo = DataManager.getLevelInfo(leveLManager.level);
				UnitNo unitNo = new UnitNo();
				unitNo.iX = unitManager.unitx;
				unitNo.iY = unitManager.unity;
				BlockNo minblockNo = BlockNo.valueOf(unitNo.toGeoLocation((byte)levelInfo.iLevel), levelInfo.iBlockWidth, levelInfo.iBlockHight);
				int blockxnum = levelInfo.unitWidth / levelInfo.iBlockWidth;
				int blockynum = levelInfo.unitHeight / levelInfo.iBlockHight;
				bmdataInputStream.seek(unitManager.fristBlockOffset + headsize + unitmanagersize);
				for (int y = minblockNo.iY; y < minblockNo.iY + blockynum; y++) {
					for (int x = minblockNo.iX; x < minblockNo.iX + blockxnum; x++) {
						long blockdataoffset = bmdataInputStream.readLong();
						int oneblockdatasize = bmdataInputStream.readShort() & 0xffff;
						if(blockdataoffset != -1){
//							System.out.println(leveLManager.level + "层,偏移" + blockdataoffset);
//							break test;
							try {
								blockDataInputStream.seek(blockdataoffset +  + headsize + unitmanagersize + blockmanagersize);
							} catch (Exception e) {
								e.printStackTrace();
							}
							byte[] blockdata = new byte[oneblockdatasize * 2];
							blockDataInputStream.read(blockdata);
							BlockData blockDataInfo = new BlockData();
							blockDataInfo.blockx = x;
							blockDataInfo.blocky = y;
							blockDataInfo.unitx = unitManager.unitx;
							blockDataInfo.unity = unitManager.unity;
							blockDataInfo.level = levelInfo.iLevel;
							blockDataInfo.data = blockdata;
							blockDataInfo.size = oneblockdatasize * 2;
							blockdatas.add(blockDataInfo);
						}
					}
				}
				 beforeunitx = unitNo.iX;
				 beforeunity = unitNo.iY;
			}
		}
		dataInputStream.close();
		bmdataInputStream.close();
		blockDataInputStream.close();
		return blockdatas;
				
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
