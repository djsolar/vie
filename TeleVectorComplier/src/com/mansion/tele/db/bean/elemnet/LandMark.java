package com.mansion.tele.db.bean.elemnet;

import java.io.Serializable;

public class LandMark extends Element{
	private static final long serialVersionUID = 1L;
	//主键
	protected String poiId;
	
	//名字
	protected String poiName;
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPoiId() {
		return poiId;
	}

	public void setPoiId(String poiId) {
		this.poiId = poiId;
	}

	public String getPoiName() {
		return poiName;
	}

	public void setPoiName(String poiName) {
		this.poiName = poiName;
	}

	
	
}
