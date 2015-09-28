package com.mansion.tele.db.bean.elemnet;

@SuppressWarnings("serial")
public class BaseShp extends SubElement {
	protected String strID;
	

	public transient int iblockX ;
	
	public transient int iblockY ;
	
	
	public int getIblockX() {
		return iblockX;
	}

	public void setIblockX(int iblockX) {
		this.iblockX = iblockX;
	}

	public int getIblockY() {
		return iblockY;
	}

	public void setIblockY(int iblockY) {
		this.iblockY = iblockY;
	}

	public String getStrid()
	{
		return this.strID;
	}

	public void setStrid(String strid)
	{
		this.strID = strid;
	}
}
