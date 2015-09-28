package com.sunmap.teleview.element.view.data;

import java.awt.Point;

import com.sunmap.teleview.util.GeoPoint;



public class BlockID {
	public byte level;
	public int xIndex;
	public int yIndex;
	public int dataSize;
	
	public BlockID(){
		
	}
	
	public BlockID(BlockID blockID){
		this.level = blockID.level;
		this.xIndex = blockID.xIndex;
		this.yIndex = blockID.yIndex;
		this.dataSize = blockID.dataSize;
	}
	
	/**
	 * 比较图页号
	 * @return true 相同  false 不相同
	 */
	@Override
	public boolean equals(Object object)
	{
		if(object == null){
			return false;
		}
		BlockID other = (BlockID)object;
		if(this.xIndex == other.xIndex 
			&& this.yIndex == other.yIndex
			&& this.level == other.level){
			return true;
		}
		return false;
	}
	
	/**
	 * 取得block的URL
	 * @return
	 */
	public String getURL()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(0);
		builder.append(".");
		builder.append(level);
		builder.append(".");
		builder.append(xIndex);
		builder.append(".");
		builder.append(yIndex);
		return builder.toString();
	}
	
	/**
	 * 将正规化坐标转换成1/2560秒坐标
	 * @param blockID 
	 * @return
	 */
	public Point unitToGeo(double longitude, double latitude) {
		double left = ViewBaseInfo.levelManage.getDataLevel(level).lonBlockWidth * xIndex + ViewBaseInfo.xStart;
		double bottom = ViewBaseInfo.levelManage.getDataLevel(level).latBlockHeight * yIndex + ViewBaseInfo.yStart;
		int width = ViewBaseInfo.levelManage.getDataLevel(level).lonBlockWidth;
		int heigth = ViewBaseInfo.levelManage.getDataLevel(level).latBlockHeight;
		Point geoPoint = new Point();
		geoPoint.x = (int) (longitude * width / ViewBaseInfo.maxUnitX + left);
		geoPoint.y = (int) (latitude * heigth / ViewBaseInfo.maxUnitY + bottom);
		return geoPoint;
	}
	
	
	
	/**
	 * 取得block的左下角经度
	 * @return
	 */
	public double getLeft()
	{
		double left = ViewBaseInfo.levelManage.getDataLevel(level).lonBlockWidth * xIndex + ViewBaseInfo.xStart;
		return left;
	}
	
	/**
	 * 取得block的左下角纬度
	 * @return
	 */
	public double getBottom()
	{
		double bottom = ViewBaseInfo.levelManage.getDataLevel(level).latBlockHeight * yIndex + ViewBaseInfo.yStart;
		return bottom;
	}
	
	public static BlockID getBlockIDbyGeoPoint(byte dataLevel,GeoPoint geoPoint){
		BlockID blockID = new BlockID();
		blockID.level = dataLevel;
		GeoPoint point = geoPoint;
		if (point != null) {
			blockID.xIndex = (geoPoint.getLongitude() - ViewBaseInfo.xStart) / ViewBaseInfo.levelManage.getDataLevel(dataLevel).lonBlockWidth;
			blockID.yIndex = (geoPoint.getLatitude() - ViewBaseInfo.yStart) / ViewBaseInfo.levelManage.getDataLevel(dataLevel).latBlockHeight;
		}
		return blockID;
	}
	
}
