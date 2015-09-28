package com.mansion.tele.db.bean.elemnet;

import java.io.Serializable;

public class RailWayStop extends Element implements Serializable {

	private static final long serialVersionUID = -6667073963714105104L;

	protected String gid;
	
	protected String smid;
	
	protected String crstopid;
	
	protected String namec;

	protected String namep;

	protected String ledgeid;

	protected String type;

	protected String geom1;

	protected int admin_code;
	
	protected int pri;
	//通过地铁站的线路信息
	protected String railwayStopLineInfo;

	public String getRailwayStopLineInfo() {
		return railwayStopLineInfo;
	}

	public void setRailwayStopLineInfo(String railwayStopLineInfo) {
		this.railwayStopLineInfo = railwayStopLineInfo;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getSmid() {
		return smid;
	}

	public void setSmid(String smid) {
		this.smid = smid;
	}

	public String getCrstopid() {
		return crstopid;
	}

	public void setCrstopid(String crstopid) {
		this.crstopid = crstopid;
	}

	public String getNamec() {
		return namec;
	}

	public void setNamec(String namec) {
		this.namec = namec;
	}

	public String getNamep() {
		return namep;
	}

	public void setNamep(String namep) {
		this.namep = namep;
	}

	public String getLedgeid() {
		return ledgeid;
	}

	public void setLedgeid(String ledgeid) {
		this.ledgeid = ledgeid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGeom1() {
		return geom1;
	}

	public void setGeom1(String geom1) {
		this.geom1 = geom1;
	}

	public int getAdmin_code() {
		return admin_code;
	}

	public void setAdmin_code(int admin_code) {
		this.admin_code = admin_code;
	}
	
	
	
}
