package com.mansion.tele.db.bean.elemnet;

import java.io.Serializable;

/**
 * 
 * @author zhaozhongyi
 *
 */

public class BusLine implements Serializable{


	private static final long serialVersionUID = -101075600731121700L;

	private String gid;
	
	private String blineid;
	
	private String namec;

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getBlineid() {
		return blineid;
	}

	public void setBlineid(String blineid) {
		this.blineid = blineid;
	}

	public String getNamec() {
		return namec;
	}

	public void setNamec(String namec) {
		this.namec = namec;
	}
	
}
