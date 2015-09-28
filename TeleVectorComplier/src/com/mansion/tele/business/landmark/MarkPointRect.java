package com.mansion.tele.business.landmark;

import java.io.Serializable;

import com.mansion.tele.common.GeoRect;

@SuppressWarnings("serial")
public class MarkPointRect implements Serializable, Cloneable{
	//icon相关文字内容矩形框
	GeoRect geoRects;
	//矩形框位置 1代表右，2代表左，3代表上，4代表下
	byte stations;
	public MarkPointRect(GeoRect geoRects,byte stations) {
		this.geoRects = geoRects;
		this.stations = stations;
	}
	
	public MarkPointRect(GeoRect geoRects){
		this.geoRects = geoRects;
	}
	
	@Override
	public MarkPointRect clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return (MarkPointRect) super.clone();
	}
}
