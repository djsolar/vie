package com.sunmap.teleview.parser.tele;

public class Header {
	private int headerSize;	
	private long unitRecord;	
	private long blockTabel;	
	private long blockSize;	
	private int levelNum;
	public int getHeaderSize() {
		return headerSize;
	}
	public void setHeaderSize(int headerSize) {
		this.headerSize = headerSize;
	}
	public long getUnitRecord() {
		return unitRecord;
	}
	public void setUnitRecord(long unitRecord) {
		this.unitRecord = unitRecord;
	}
	public long getBlockTabel() {
		return blockTabel;
	}
	public void setBlockTabel(long blockTabel) {
		this.blockTabel = blockTabel;
	}
	public long getBlockSize() {
		return blockSize;
	}
	public void setBlockSize(long blockSize) {
		this.blockSize = blockSize;
	}
	public int getLevelNum() {
		return levelNum;
	}
	public void setLevelNum(int levelNum) {
		this.levelNum = levelNum;
	}	
}
