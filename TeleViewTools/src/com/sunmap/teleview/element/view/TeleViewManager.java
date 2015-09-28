package com.sunmap.teleview.element.view;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.sunmap.teleview.element.view.data.BlockData;
import com.sunmap.teleview.element.view.data.BlockID;
import com.sunmap.teleview.element.view.data.TeleViewText;
import com.sunmap.teleview.element.view.data.ViewBaseInfo;
import com.sunmap.teleview.util.GeoRect;


public class TeleViewManager {

	private BlockDataManager blockDataManager;
	private DrawEleType drawEleType;
	private ViewBaseInfo curViewBaseInfo;
	
	public void init(){
		Device.init();
		IconSearcher.getInstance().loadIcons(true);
		this.blockDataManager = new BlockDataManager();
		this.drawEleType = new DrawEleType();
		this.curViewBaseInfo = new ViewBaseInfo();
		ViewBaseInfo.levelManage = new LevelManage();
		ViewBaseInfo.levelManage.init();
	}
	
	/**
	 * 设置文件路径
	 * @param viewFilePath
	 */
	public void setViewPath(String viewFilePath){
		ViewBaseInfo.viewFilePath = viewFilePath;
	}
	
	/**
	 * 设置是否锁层
	 * @param lockLevel
	 */
	public void setLockLevel(boolean lockLevel){
		ViewBaseInfo.isLockLevel = lockLevel;
	}
	
	/**
	 * 设置描画元素类型
	 * @param drawEleType
	 */
	public void setDrawEleType(DrawEleType drawEleType){
		this.drawEleType.setDrawViewEleType(drawEleType);
	}
	
	/**
	 * 描画
	 * @param graphics		描画工具
	 * @param centerPoint	中心点
	 * @param scale			比例尺
	 * @throws Exception 
	 */
	public void draw(Graphics2D graphics, Point centerPoint, int scale) throws Exception{
		this.curViewBaseInfo.setViewBaseInfo(centerPoint, scale);
		GeoRect geoRect = Device.getGeoRect();
		List<BlockID> blockIDs = this.curViewBaseInfo.calcBlockIDs(geoRect, ViewBaseInfo.curDataLevel);
		List<BlockData> blockdatas;
		blockdatas = this.blockDataManager.getData(blockIDs);
		this.blockDataManager.draw(graphics, blockdatas);	
		if (blockDataManager.cancelDrawFlag == true) {
			Exception e = new Exception("cancelDrawFlag");
			throw e;
		}
	}
		
	/**
	 * 取消描画
	 */
	public void cancelDraw(){
		this.blockDataManager.cancelDrawFlag = true;
	}
	
	public void revertDraw(){
		this.blockDataManager.cancelDrawFlag = false;
	}

	/**
	 * 取得View基本信息
	 * @return
	 */
	@SuppressWarnings("static-access")
	public ViewBaseInfo getViewBaseInfo(){
		ViewBaseInfo viewBaseInfo = new ViewBaseInfo();
		viewBaseInfo.centerPoint = this.curViewBaseInfo.centerPoint;
		viewBaseInfo.curMapScale = this.curViewBaseInfo.curMapScale;
		viewBaseInfo.curDataLevel = this.curViewBaseInfo.curDataLevel;
		viewBaseInfo.curDataScale = this.curViewBaseInfo.curDataScale;
		viewBaseInfo.isLockLevel = this.curViewBaseInfo.isLockLevel;
		viewBaseInfo.viewFilePath = this.curViewBaseInfo.viewFilePath;
		viewBaseInfo.levelManage = this.curViewBaseInfo.levelManage;
		return viewBaseInfo;
	}
	
	public void clearViewData(){
		blockDataManager.clearViewData();
	}
	
	public Map<List<Object>, Double> calcPickUpInfo(Point point, int dis){
		Point2D.Float pointFloat = new Point2D.Float(point.x, point.y);
		//将点转换成1/2560秒
		int longitude = Device.PixToGeo(pointFloat).x;
		int latitude = Device.PixToGeo(pointFloat).y;
		//根据点的坐标寻找到Block
		int scale = ViewBaseInfo.curMapScale;
		GeoRect geoRect = new GeoRect(longitude-scale, latitude+scale, longitude+scale, latitude-scale);
		List<BlockID> blockIDs = curViewBaseInfo.calcBlockIDs(geoRect,ViewBaseInfo.curDataLevel);
		Map<List<Object>, Double> pickUpInfos = blockDataManager.calcPickUpInfo(blockIDs, pointFloat, dis);
		return pickUpInfos;
	}
	
	public List<TeleViewText> selectLandMark(String landMarkName, String provinceName, String cityName) throws SQLException{
		List<TeleViewText> landMarks = blockDataManager.selectLandMark(landMarkName, provinceName, cityName);
		return landMarks;
	}



}
