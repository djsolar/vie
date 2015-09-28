package com.sunmap.teleview.parser.tele;

public class Level {
	private int index;
	private long firstUnitOffset;
	private long xMinUnitCode;
	private long yMinUnitCode;
	private long xUnitNum;
	private long yUnitNum;
	
	public Level(int index, long firstUnitOffset, long xMinUnitCode,
			long yMinUnitCode, long xUnitNum, long yUnitNum) {
		super();
		this.index = index;
		this.firstUnitOffset = firstUnitOffset;
		this.xMinUnitCode = xMinUnitCode;
		this.yMinUnitCode = yMinUnitCode;
		this.xUnitNum = xUnitNum;
		this.yUnitNum = yUnitNum;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public long getFirstUnitOffset() {
		return firstUnitOffset;
	}

	public void setFirstUnitOffset(long firstUnitOffset) {
		this.firstUnitOffset = firstUnitOffset;
	}

	public long getxMinUnitCode() {
		return xMinUnitCode;
	}

	public void setxMinUnitCode(long xMinUnitCode) {
		this.xMinUnitCode = xMinUnitCode;
	}

	public long getyMinUnitCode() {
		return yMinUnitCode;
	}

	public void setyMinUnitCode(long yMinUnitCode) {
		this.yMinUnitCode = yMinUnitCode;
	}

	public long getxUnitNum() {
		return xUnitNum;
	}

	public void setxUnitNum(long xUnitNum) {
		this.xUnitNum = xUnitNum;
	}

	public long getyUnitNum() {
		return yUnitNum;
	}

	public void setyUnitNum(long yUnitNum) {
		this.yUnitNum = yUnitNum;
	}

}
