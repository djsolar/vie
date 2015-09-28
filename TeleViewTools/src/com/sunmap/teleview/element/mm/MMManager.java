package com.sunmap.teleview.element.mm;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.sunmap.teleview.Controller;
import com.sunmap.teleview.element.mm.data.MMBaseInfo;
import com.sunmap.teleview.element.mm.data.MMBlockID;
import com.sunmap.teleview.element.mm.data.Road;
import com.sunmap.teleview.util.GeoRect;

public class MMManager {
	private MMBlockDataManager mmBlockDataManager;
	private DrawEleType drawEleType;
	private MMBaseInfo curMMBaseInfo;
	
	public void init(){
		Device.init();
		this.mmBlockDataManager = new MMBlockDataManager();
		this.drawEleType = new DrawEleType();
		this.curMMBaseInfo = new MMBaseInfo();
	}
	
	/**
	 * 设置文件路径
	 * @param mmFilePath
	 */
	public void setMMPath(String mmFilePath){
		MMBaseInfo.mmFilePath = mmFilePath;
	}
	
	
	/**
	 * 设置描画元素类型
	 * @param drawEleType
	 */
	public void setDrawEleType(DrawEleType drawEleType){
		this.drawEleType.setDrawMMEleType(drawEleType);
	}
	
	/**
	 * 描画
	 * @param graphics		描画工具
	 * @param centerPoint	中心点
	 * @param scale			比例尺
	 * @throws Exception 
	 */
	public void draw(Graphics2D graphics, Point centerPoint, int scale) throws Exception{
		this.curMMBaseInfo.setMMBaseInfo(centerPoint, scale);
		if (MMBaseInfo.curMapScale > 50) {
			return;
		}
		GeoRect geoRect = Device.getGeoRect();
		List<MMBlockID> blockIDs = this.curMMBaseInfo.calcMMBlockIDs(geoRect);
		List<MMBlockID> blockdatas;
		blockdatas = this.mmBlockDataManager.getData(blockIDs);
		this.mmBlockDataManager.draw(graphics, blockdatas);	
		if(this.mmBlockDataManager.cancelDrawFlag == true){
			Exception e = new Exception("cancelDrawFlag");
			throw e;
		}
	}
		
	/**
	 * 取消描画
	 */
	public void cancelDraw(){
		this.mmBlockDataManager.cancelDrawFlag = true;
	}
	
	public void revertDraw(){
		this.mmBlockDataManager.cancelDrawFlag = false;
	}

	/**
	 * 取得View基本信息
	 * @return
	 */
	@SuppressWarnings("static-access")
	public MMBaseInfo getMMBaseInfo(){
		MMBaseInfo viewBaseInfo = new MMBaseInfo();
		viewBaseInfo.centerPoint = this.curMMBaseInfo.centerPoint;
		viewBaseInfo.curMapScale = this.curMMBaseInfo.curMapScale;
		viewBaseInfo.mmFilePath = this.curMMBaseInfo.mmFilePath;
		return viewBaseInfo;
	}
	
	public void clearMMData(){
		mmBlockDataManager.clearMMData();
	}
	
	public Map<List<Object>, Double> calcPickUpInfo(Point point, int dis){
		Point2D.Float pointFloat = new Point2D.Float(point.x, point.y);
		// 以这个点计算 一个矩形框
		int pixhigh = 10;
		// 像素矩形2个点
		Point2D.Float pointLBpix = new Point2D.Float(point.x - pixhigh, point.y + pixhigh);// 左下坐标（像素）
		Point2D.Float pointRTpix = new Point2D.Float(point.x + pixhigh, point.y - pixhigh);// 右上坐标（像素）
		// 像素转经纬度//经纬度   单位1/2560秒
		Point rectA = Controller.drawParams.PixToGeo( pointLBpix.x,  pointLBpix.y);
		Point rectB = Controller.drawParams.PixToGeo( pointRTpix.x,  pointRTpix.y);
		GeoRect rect = new GeoRect(rectA.x, rectB.y, rectB.x, rectA.y);
		// 获取矩形框包含MMBlock
		List<MMBlockID> blockIDs = curMMBaseInfo.calcMMBlockIDs(rect);
		Map<List<Object>, Double> pickUpInfos = mmBlockDataManager.calcPickUpInfo(blockIDs, pointFloat, dis);
		return pickUpInfos;
	}

	public Road getNodeLinkRoad(int roadIndex, int blockIndexX, int blockIndexY) {
		Road road = null;
		MMBlockID mmBlockID = new MMBlockID(blockIndexX, blockIndexY);
		int index = Collections.binarySearch(mmBlockDataManager.mmBlockIDs, mmBlockID, MMBlockID.compareByIndex);
		if (index >= 0) {
			mmBlockID = mmBlockDataManager.mmBlockIDs.get(index);
			road = mmBlockID.roads.get(roadIndex);
			if (road.points.size() != road.pointxsf.length ) {
				road.points.clear();
				for (int a = 0; a < road.pointxsf.length; a++) {
				Point Geopoint = mmBlockID.UnitToGeo(road.pointxsf[a],road.pointysf[a]);
				road.points.add(Geopoint);
				}
			}
		}
		return road;
	}

}
