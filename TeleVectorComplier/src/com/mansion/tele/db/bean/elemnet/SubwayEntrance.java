package com.mansion.tele.db.bean.elemnet;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SubwayEntrance extends Element implements Serializable{

	protected String gid;

	protected String smid;

	protected String namec;

	protected String namep;

	protected String poiid;

	protected String type;

	protected String geom1;

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

	public String getPoiid() {
		return poiid;
	}

	public void setPoiid(String poiid) {
		this.poiid = poiid;
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
	
	
}
