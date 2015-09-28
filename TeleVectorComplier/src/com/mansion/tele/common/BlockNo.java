package com.mansion.tele.common;

import java.io.Serializable;

import com.mansion.tele.business.DataManager;
import com.mansion.tele.db.bean.elemnet.ShpPoint;

/**
 * Tele编号
 * 
 * @author yangz
 * 
 */
public class BlockNo implements Comparable<BlockNo>,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4270098237506347915L;
	/**
	 * block所在层号
	 */
	public int level;
	/**
	 * block所在的 x方向编号
	 */
	public int iX;
	/**
	 * block所在的y方向编号
	 */
	public int iY;

	/**
	 * 构造器
	 * @param iBlockX block所在的 x方向编号
	 * @param iBlockY block所在的y方向编号
	 */
	public BlockNo(int iBlockX, int iBlockY) {
		this.iX = iBlockX;
		this.iY = iBlockY;
	}

	/**
	 * 默认构造器
	 */
	public BlockNo() {
		super();
	}

	/**
	 * 设置Unit的编号
	 * 
	 * @param stPosInBlock
	 *             Unit范围内的一个经纬度。@see GeoLocation
	 * @param lBlockWidth
	 *             Unit的宽度
	 * @param lBlockHight
	 *             Unit的高度
	 * @return BlockNo
	 */
	public static BlockNo valueOf(GeoLocation stPosInBlock, long lBlockWidth,
			long lBlockHight) {
		BlockNo stTemp = new BlockNo();
		stTemp.iX = (int) ((int) (stPosInBlock.getiLongitude() - 
				DataManager.MAP_GEO_LOCATION_LONGITUDE_MIN) / lBlockWidth);
		stTemp.iY = (int) ((int) (stPosInBlock.getiLatitude() - 
				DataManager.MAP_GEO_LOCATION_LATITUDE_MIN) / lBlockHight);
		return stTemp;
	}
	
	/**
	 * 通过当前经纬度获取blockNo
	 * @param stPosInBlock 当前经纬度
	 * @param lBlockWidth block宽
	 * @param lBlockHight block高
	 * @return BlockNo 
	 */
	public static BlockNo valueOf(ShpPoint stPosInBlock, int lBlockWidth,
			int lBlockHight) {
		BlockNo stTemp = new BlockNo();
		stTemp.iX = (stPosInBlock.x - DataManager.MAP_GEO_LOCATION_LONGITUDE_MIN)
				/ lBlockWidth;
		stTemp.iY = (stPosInBlock.y - DataManager.MAP_GEO_LOCATION_LATITUDE_MIN)
				/ lBlockHight;
		return stTemp;
	}

	/**
	 * 取得Block的左经度
	 * @param lBlockWidth
	 * @return
	 */
	public int getLeft(int lBlockWidth){
		return (int) (this.iX * lBlockWidth + DataManager.MAP_GEO_LOCATION_LONGITUDE_MIN);
	}
	
	/**
	 * 取得Block的下纬度
	 * @param lBlockHight
	 * @return
	 */
	public int getBottom(int lBlockHight){
		return (int) (this.iY * lBlockHight + DataManager.MAP_GEO_LOCATION_LATITUDE_MIN);
	}
	
	/**
	 * 取得Block的左下经纬度
	 * 
	 * @param lBlockWidth block宽
	 * @param lBlockHight block高
	 * @return GeoLocation 左下经纬度
	 */
	public GeoLocation toGeoLocation(int lBlockWidth, int lBlockHight) {
		GeoLocation location = new GeoLocation();
		location.setiLongitude((int) (this.iX * lBlockWidth + DataManager.MAP_GEO_LOCATION_LONGITUDE_MIN));
		location.setiLatitude((int) (this.iY * lBlockHight + DataManager.MAP_GEO_LOCATION_LATITUDE_MIN));
		return location;
	}
	
	/**
	 * 重写equals
	 * @param arg0 比较数据
	 * @return boolean 比较结果
	 */
	@Override
	public boolean equals(Object arg0) {
		boolean flag = false;
		if (arg0 == null || !(arg0 instanceof BlockNo)) {
			flag = false;
		}
		if ((this.iX == ((BlockNo) arg0).iX) && this.iY == ((BlockNo) arg0).iY) {

			flag = true;
		}
		return flag;
	}

	/**
	 * 重写哈希码
	 * @return int 哈希码
	 */
	@Override
	public int hashCode() {
		return this.iX + this.iY;
	}

	/**
	 * 重写比较器
	 * @param o block号
	 * @return int 比较结果
	 */
	@Override
	public int compareTo(BlockNo o) {
		int i = 0;
		if (iY != o.iY) {
			i = iY - o.iY;
		}else if (iX != o.iX) {
			i = iX - o.iX;
		}
		return i;
	}

	/**
	 * 获得iX属性值 
	 * @return iX
	 */
	public int getiX() {
		return this.iX;
	}

	/**
	 * 设置iX属性值
	 * @param iX iX属性值
	 */
	public void setiX(int iX) {
		this.iX = iX;
	}
	/**
	 * 获得iY属性值 
	 * @return iY
	 */
	public int getiY() {
		return this.iY;
	}
	/**
	 * 设置iY属性值
	 * @param iY iY属性值
	 */
	public void setiY(int iY) {
		this.iY = iY;
	}
	
	
}
