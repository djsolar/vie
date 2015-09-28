package com.sunmap.teleview.util;

public enum ByteType {
	BYTE(1),
	SHORT(2),
	INT(4),
	LONG(8),
	FLOAT(4),
	DOUBLE(8),
	STRING;
	
	public final static boolean BIG_ENDIAN = false;
	public final static boolean LITTLE_ENDIAN = true;
	
	private int len;
	private boolean endian;
	
	private ByteType()
	{
		
	}
	
	private ByteType(int len) {
		this.len = len;		
	}
	
	private ByteType(int len, boolean endian) {
		this.len = len;
		this.endian = endian;
	}
	
	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public boolean isEndian() {
		return endian;
	}

	public void setEndian(boolean endian) {
		this.endian = endian;
	}
	
	
}
