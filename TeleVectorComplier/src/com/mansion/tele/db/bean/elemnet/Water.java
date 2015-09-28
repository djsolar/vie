package com.mansion.tele.db.bean.elemnet;

import java.util.List;

/**
 * Water entity.
 * 
 * @author MyEclipse Persistence Tools
 */

@SuppressWarnings("serial")
public class Water extends PolygonElement {
	private int iWaterType; 
	private transient List<WaterBorder> astWaterBorders; 

	/**
	 * 由母库的结构转换为中间库M1的结构
	 * @param water 要素
	 * @param bLevel level号
	 * @param bStep  任务阶段号
	 * @param iUnitNo 任务号
	 */
	public void convert(Water water) {
		this.setIwatertype(water.getIwatertype());
		this.lArea = water.getlArea();

		if (water.getAstWaterBorders() != null && water.getAstWaterBorders().size() > 0) {
			
			PolygonShp polygonShp = new PolygonShp();
			polygonShp.convert(water.getAstWaterBorders());
			this.stGeom = polygonShp;
			
		}
		
	}
	
	public int getIwatertype() {
		return iWaterType;
	}
	public void setIwatertype(int iwatertype) {
		this.iWaterType = iwatertype;
	}
	public List<WaterBorder> getAstWaterBorders() {
		return astWaterBorders;
	}
	public void setAstWaterBorders(List<WaterBorder> astWaterBorders) {
		if (astWaterBorders == null && this.astWaterBorders != null) {
			this.astWaterBorders.clear();
		} else {
			this.astWaterBorders = astWaterBorders;
		}
	}

}