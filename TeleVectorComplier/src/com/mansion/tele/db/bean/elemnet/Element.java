package com.mansion.tele.db.bean.elemnet;

@SuppressWarnings("serial")
public class Element extends Entity {
	
	public byte bygrade;//相对跨压
	public int iBlockX;				/*BlockX编号*/
	
	public int iBlockY;				/*BlockY编号*/
	protected String strID;

	public byte getBygrade() {
		return bygrade;
	}
	public int getiBlockX() {
		return iBlockX;
	}

	public void setiBlockX(int iBlockX) {
		this.iBlockX = iBlockX;
	}

	public int getiBlockY() {
		return iBlockY;
	}

	public void setiBlockY(int iBlockY) {
		this.iBlockY = iBlockY;
	}

	public void setBygrade(byte bygrade) {
		this.bygrade = bygrade;
	}
	public String getStrid() {
		return this.strID;
	}


	public void setStrid(String strid) {
		this.strID = strid;
	}


}
