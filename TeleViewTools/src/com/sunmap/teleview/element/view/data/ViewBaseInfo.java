package com.sunmap.teleview.element.view.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.sunmap.teleview.element.view.LevelManage;
import com.sunmap.teleview.util.GeoRect;


public class ViewBaseInfo {
	public static int curMapScale = 200;
	public static int curDataLevel = 1;
	public static int curDataScale = 2;
	public static Point centerPoint = new Point();
	public static boolean isLockLevel = false;
	public static String viewFilePath;
	public static LevelManage levelManage;
	public static final int xStart = 184320000;// 起始点经度
	public static final int yStart = 0;// 起始点纬度
	public static final float maxUnitX = 255;
	public static final float maxUnitY = 255;
	
	/**
	 * 取得blockID列表
	 * @param geoRect	坐标矩形
	 * @param level		层号
	 * @return
	 */
	public List<BlockID> calcBlockIDs(GeoRect geoRect, int level){
		List<BlockID> screenBlockIDs = new ArrayList<BlockID>();
		byte levelNo = (byte) level;
		int lbLon = geoRect.getLeft() - xStart;
		int lbLat = geoRect.getBottom() - yStart;
		int rtLon = geoRect.getRight() - xStart;
		int rtLat = geoRect.getTop() - yStart;
		int xStartIndex = (int) Math.floor((float) lbLon /  levelManage.getDataLevel(levelNo).lonBlockWidth);
		int xEndIndex = (int) Math.floor((float) rtLon /  levelManage.getDataLevel(levelNo).lonBlockWidth);
		int yStartIndex = (int) Math.floor((float) lbLat /  levelManage.getDataLevel(levelNo).latBlockHeight);
		int yEndIndex = (int) Math.floor((float) rtLat /  levelManage.getDataLevel(levelNo).latBlockHeight);
		short mapCountX = (short) (xEndIndex - xStartIndex + 1);
		short mapCountY = (short) (yEndIndex - yStartIndex + 1);
		for (int i = 0; i < mapCountX; i++) {
			for (int j = 0; j < mapCountY; j++) {
				BlockID blockID = new BlockID();
				blockID.level = levelNo;
				blockID.xIndex = xStartIndex + i;
				blockID.yIndex = yStartIndex + j;
				screenBlockIDs.add(blockID);
			}
		}
		return screenBlockIDs;
	}
	
	
	@SuppressWarnings("static-access")
	public void setViewBaseInfo(Point centerPoint, int scale) {
		this.curMapScale = scale;
		int index = levelManage.getZoomLevelByScale(curMapScale);
		if (isLockLevel == false) {
			this.curDataLevel = levelManage.zoomLevels[index].dataLevel;
			this.curDataScale = levelManage.zoomLevels[index].zoomLevel;
		}
		this.centerPoint.x = centerPoint.x;
		this.centerPoint.y = centerPoint.y;
		
	}
}
