package com.mansion.tele.db.bean.elemnet;

import java.io.Serializable;

/**
 * 
 * @author zhaozhongyi
 *
 */

public class CRailwayStopL implements Serializable{


	private String gid;
	
	private long crstopid;
	
	private long crlineid;

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public long getCrstopid() {
		return crstopid;
	}

	public void setCrstopid(long crstopid) {
		this.crstopid = crstopid;
	}

	public long getCrlineid() {
		return crlineid;
	}

	public void setCrlineid(long crlineid) {
		this.crlineid = crlineid;
	}
	
}
