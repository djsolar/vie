package com.sunmap.teleview.element.view;

import com.sunmap.teleview.element.view.LevelManage.DataLevel;
import com.sunmap.teleview.element.view.data.ViewBaseInfo;
import com.sunmap.teleview.util.GeoPoint;

public class UnitID {
	private static DataLevel dataLevels[];
	private static final int xStart = 184320000;// 起始点经度
	private static final int yStart = 0;// 起始点纬度
	public byte level;
	public int xIndex;
	public int yIndex;
	
	static{
		init();
	}
	
	/**
	 * 根据经纬度计算BlockID
	 * @param geoPoint
	 * @return
	 */
	public  UnitID getUnitIDbyGeoPoint(GeoPoint geoPoint){
		level = (byte) ViewBaseInfo.curDataLevel;
		GeoPoint point = geoPoint;
		if (point != null) {
			xIndex = (geoPoint.getLongitude() - xStart) / getWidthLong();
			yIndex = (geoPoint.getLatitude() - yStart) / getHeightLat();
		}
		return this;
	}
	
	/**
	 * 获取key
	 * @return
	 */
	public String getKey() {
		return level +"."+xIndex+"."+yIndex;
	}
	
	/**
	 * 取得block的左下角经度
	 * @return
	 */
	public double getLeft()
	{
		double left = getDataLevel(level).lonBlockWidth * xIndex + xStart;
		return left;
	}
	
	/**
	 * 取得block的左下角纬度
	 * @return
	 */
	public double getBottom()
	{
		double bottom = getDataLevel(level).latBlockHeight * yIndex + yStart;
		return bottom;
	}
	
	/**
	 * 取得unit的宽度（1/2560秒）
	 * @return
	 */
	private int getWidthLong(){
		return getDataLevel(level).lonBlockWidth;
	}
	
	/**
	 * 取得unit的高度（1/2560秒）
	 * @return
	 */
	private int getHeightLat(){
		return getDataLevel(level).latBlockHeight;
	}
 	
	private static DataLevel getDataLevel(byte dataLevel){
		return dataLevels[dataLevel];
	}
	
	private static void init(){
		dataLevels = new DataLevel[8];
		for (int i = 0; i < dataLevels.length; i++) {
			dataLevels[i] = new DataLevel();
		}
		dataLevels[0].level = 0;
		dataLevels[0].lonBlockWidth = 9216000;
		dataLevels[0].latBlockHeight = 6144000;
		dataLevels[1].level = 1;
		dataLevels[1].lonBlockWidth = 36864000;
		dataLevels[1].latBlockHeight = 24576000;
		for(int i = 2; i < 6; i++){
			dataLevels[i].level = (byte) i;
			dataLevels[i].lonBlockWidth = dataLevels[1].lonBlockWidth * 2;
			dataLevels[i].latBlockHeight = dataLevels[1].latBlockHeight * 2;
		}
		dataLevels[6].level = 6;
		dataLevels[6].lonBlockWidth = dataLevels[5].lonBlockWidth * 4;
		dataLevels[6].latBlockHeight = dataLevels[5].latBlockHeight * 4;
		dataLevels[7].level = 7;
		dataLevels[7].lonBlockWidth = dataLevels[6].lonBlockWidth * 2;
		dataLevels[7].latBlockHeight = dataLevels[6].latBlockHeight * 2;
	}

}
