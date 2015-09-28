package com.mansion.tele.business.common;

import java.util.List;

import com.mansion.tele.db.bean.elemnet.ShpPoint;

/**
 * 面形状
 * @author hefeng
 *
 */
public class TelePolygonShp extends BaseShape{
	
	/**
	 * 面形状
	 */
	private List<ShpPoint> upliftCoordinate;

	/**
	 * 
	 * @return List<ShpPoint>
	 */
	public List<ShpPoint> getUpliftCoordinate() {
		return this.upliftCoordinate;
	}

	/**
	 * 
	 * @param upliftCoordinate List<ShpPoint>
	 */
	public void setUpliftCoordinate(List<ShpPoint> upliftCoordinate) {
		this.upliftCoordinate = upliftCoordinate;
	}
	
	

}
