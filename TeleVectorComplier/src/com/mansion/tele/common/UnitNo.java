package com.mansion.tele.common;

import com.mansion.tele.business.DataManager;
import com.mansion.tele.business.DataManager.LevelInfo;

/**
 * Unit编号
 * @author yangz
 *
 */
public class UnitNo {
	/**
	 * X方向上的编号
	 */
	public int iX;
	
	/**
	 * Y方向上的编号
	 */
	public int iY;

	
	/**
	 * 计算Unit的编号
	 * @param stPosInUnit	// Unit范围内的一个经纬度。@see GeoLocation
	 * @param lUnitWidth		// Unit的宽度
	 * @param lUnitHight		// Unit的高度
	 * @return
	 */
	public static UnitNo calcUnitNo(GeoLocation stPosInUnit, long lUnitWidth, long lUnitHight){
		UnitNo temp = new UnitNo();
		temp.iX = (int) ((int) (stPosInUnit.getiLongitude() - DataManager.MAP_GEO_LOCATION_LONGITUDE_MIN)/lUnitWidth);
		temp.iY = (int) ((int) (stPosInUnit.getiLatitude() - DataManager.MAP_GEO_LOCATION_LATITUDE_MIN)/lUnitHight);
		return temp;
	}

	/**
	 * 使用经纬度和level计算对应的Unit编号
	 * @param stPos		经纬度
	 * @param bLevel	所属level
	 * @return 对应Unit的编号
	 */
	public static UnitNo calcUnitNo(GeoLocation stPos, int bLevel) {
		// 取得当前level的unit分割长宽
		LevelInfo levelInfo = DataManager.getLevelInfo(bLevel);
		// 计算Unit编号
		UnitNo block = UnitNo.calcUnitNo(stPos, levelInfo.unitWidth,levelInfo.unitHeight);
		
		return block;
	}

	/**
	 * 取得Unit的左下经纬度
	 * @param bLevel 所属level
	 * @return 对应Unit的左下经纬度
	 */
	public GeoLocation toGeoLocation(byte bLevel){
		// 取得当前level的Unit分割
		LevelInfo levelInfo = DataManager.getLevelInfo(bLevel);

		// 计算经纬度
		GeoLocation location = new GeoLocation();
		location.setiLongitude((int) (iX*levelInfo.unitWidth + DataManager.MAP_GEO_LOCATION_LONGITUDE_MIN));
		location.setiLatitude((int) (iY*levelInfo.unitHeight + DataManager.MAP_GEO_LOCATION_LATITUDE_MIN));
		return location;
	}
}
