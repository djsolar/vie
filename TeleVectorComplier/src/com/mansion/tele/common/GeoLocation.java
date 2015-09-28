package com.mansion.tele.common;

import java.io.Serializable;
import com.mansion.tele.db.bean.elemnet.ShpPoint;

/**
 * 经纬度
 * 
 * @author yangz
 * 
 */
@SuppressWarnings("serial")
public class GeoLocation implements Serializable, Cloneable {

	/**
	 * 经度
	 */
	private int iLongitude;

	/**
	 * 纬度
	 */
	private int iLatitude;
	
	/**
	 * 重写哈希码
	 * @return int 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * this.iLongitude + this.iLatitude;
		return result;
	}

	/**
	 * 
	 * 重写equals
	 * @param obj 比较对象
	 * @return boolean  
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else {
			GeoLocation location = (GeoLocation) obj;
			if (this.iLongitude == location.iLongitude
					&& this.iLatitude == location.iLatitude) {
				return true;
			} else {
				return false;
			}
		}
	}


	/**
	 * 获取经度值
	 * @return int 经度值  
	 */
	public int getiLongitude() {
		return this.iLongitude;
	}

	/**
	 * 设置经度值
	 * @param iLongitude 经度值
	 */
	public void setiLongitude(int iLongitude) {
		this.iLongitude = iLongitude;
	}

	/**
	 * 获取纬度值
	 * @return int 纬度值  
	 */
	public int getiLatitude() {
		return this.iLatitude;
	}

	/**
	 * 设置纬度值
	 * @param iLatitude 纬度值
	 */
	public void setiLatitude(int iLatitude) {
		this.iLatitude = iLatitude;
	}

	/**
	 * 将shppoint转换为坐标经纬度
	 * @param shpPoint 形状点
	 * @return GeoLocation 坐标经纬度
	 */
	public static GeoLocation valueOf(ShpPoint shpPoint) {
		GeoLocation geoLocation = new GeoLocation();
		geoLocation.iLongitude = shpPoint.x;
		geoLocation.iLatitude = shpPoint.y;
		return geoLocation;
	}

	/**
	 * 获得坐标经纬度
	 * @param x x值
	 * @param y y值
	 * @return GeoLocation 坐标
	 */
	public static GeoLocation valueOf(int x, int y) {
		GeoLocation geoLocation = new GeoLocation();
		geoLocation.iLongitude = x;
		geoLocation.iLatitude = y;
		return geoLocation;
	}
}
