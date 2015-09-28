package com.mansion.tele.db.bean.elemnet;

import java.io.Serializable;

/**
 * 
 * @author zhaozhongyi
 *
 */

@SuppressWarnings("serial")
public class BusStopL implements Serializable{


	private String gid;
	
	private long bstopid;
	
	private long blineid;

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public long getBstopid() {
		return bstopid;
	}

	public void setBstopid(long bstopid) {
		this.bstopid = bstopid;
	}

	public long getBlineid() {
		return blineid;
	}

	public void setBlineid(long blineid) {
		this.blineid = blineid;
	}
	
}
