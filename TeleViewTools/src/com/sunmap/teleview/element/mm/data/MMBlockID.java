package com.sunmap.teleview.element.mm.data;
/**
 * 
 */


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.sunmap.teleview.element.mm.Device;
import com.sunmap.teleview.util.GeoPoint;
import com.sunmap.teleview.util.GeoRect;
import com.sunmap.teleview.util.ToolsUnit;


/**
 * @author lijingru
 * 
 */
public class MMBlockID  {
	public int xIndex;
	public int yIndex;
	public int size;
	public List<Road> roads;
	
	public MMBlockID(){
		
	}
	public MMBlockID(int xIndex,int yIndex){
		this.xIndex = xIndex;
		this.yIndex = yIndex;
	}

	static{
		double meterPerLon = ToolsUnit.getMeterPerLon() / 2560.0;
		MMBaseInfo.meterPerUnit = MMBaseInfo.LonBlockWidth * meterPerLon / MMBaseInfo.MaxUnitX;		
	}

	static public Comparator<MMBlockID> compareByIndex = new Comparator<MMBlockID>() {
		public int compare(MMBlockID left, MMBlockID right)
		{
			if(left.xIndex > right.xIndex){
				return 1;
			}
			if(left.xIndex < right.xIndex){
				return -1;
			}
			
			if(left.yIndex > right.yIndex){
				return 1;
			}
			if(left.yIndex < right.yIndex){
				return -1;
			}
			return 0;
		}
	};
	/**
	 * 获取block的键
	 * 
	 * @return
	 */
	public String getKey() {
		String key = xIndex + "." + yIndex;
		return key;
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
		builder.append(0);
		builder.append(".");
		builder.append(xIndex);
		builder.append(".");
		builder.append(yIndex);
		return builder.toString();
	}
	// 管理等级
	static final class defineNRC {
		public static final byte HightWay = 0;// 高速道路
		public static final byte NationalRoad = 1;// 国道
		public static final byte CityFreeWay = 2;// 城市快速道路，城市间快速道路
		public static final byte ProcinceRoad = 3;// 省道
		public static final byte CityMajorRoad = 4;// 城市主干道
		public static final byte CityMinorRoad = 5;// 城市次干道
		public static final byte NormalRoad = 6;// 一般道
		public static final byte MinorRoad1 = 7;// 細道路（細道路1）
		public static final byte MinorRoad2 = 8;// 細道路（細道路2）
		public static final byte Freey = 9;// Ferry link（航道）
	}

	// 管理等级对应颜色
	static final class colorNRC {
		public static final Color HightWay = new Color(0, 0, 255,100);
		public static final Color NationalRoad = new Color(143, 31, 207,100);// 紫色
		public static final Color CityFreeWay = new Color(51, 153, 255,100);// 湛蓝
		public static final Color ProcinceRoad = new Color(31, 207, 63,100);// 墨绿
		public static final Color CityMajorRoad = new Color(102, 153, 0,100);// 橘黄
		public static final Color CityMinorRoad = new Color(86, 170, 150,100);// 浅蓝
		public static final Color NormalRoad = new Color(132, 152, 182,100);// 土黄
		public static final Color MinorRoad1 = new Color(53, 219, 199,100);// 深紫色
		public static final Color MinorRoad2 = new Color(255, 255, 0,100);// 灰色
		public static final Color Freey = new Color(100, 145, 97,100);// 棕色
	}

	/**
	 * 描画
	 */
	public void draw(Graphics2D g) {
		// 先判定当前BLOCK内有无speRoad
		if (roads != null) {
			for (int i = 0; i < roads.size(); i++) {
				Color color = null;
				Road inRoad = roads.get(i);
				// 为道路选择颜色
				color = selectColor(inRoad);
				// 描画路
				drawRoadPoints(g, inRoad, color);
			}
		}
	}

	@Override
	public boolean equals(Object other1){
		MMBlockID other = (MMBlockID)other1;
		if(other == null){
			return false;
		}
		if(xIndex == other.xIndex && yIndex == other.yIndex){
			return true;
		}
		return false;
	}
	public void drawMMBlockID(Graphics2D g) {
		//描画BLOCK边框
		g.setColor(new Color(163,73,164));
		g.setStroke(new BasicStroke(1.5f));
		GeoPoint p1 = this.getGeoRect().pointLB;
		GeoPoint p2 = this.getGeoRect().pointRT;
		Point2D pointA = Device.geoToPix(new Point(p1.longitude, p2.latitude));
		Point2D pointB = Device.geoToPix(new Point(p1.longitude, p1.latitude));
		Point2D pointC = Device.geoToPix(new Point(p2.longitude, p1.latitude));
		Point2D pointD = Device.geoToPix(new Point(p2.longitude, p2.latitude));
		int[] xPoints = new int[]{(int) (pointA.getX() + 0.5),(int) (pointB.getX() + 0.5),(int) (pointC.getX() + 0.5),(int) (pointD.getX() + 0.5)};
		int[] yPoints = new int[]{(int) (pointA.getY() + 0.5),(int) (pointB.getY() + 0.5),(int) (pointC.getY() + 0.5),(int) (pointD.getY() + 0.5)};
		Shape s = new Polygon(xPoints, yPoints, 4);
		g.draw(s);
		
	}
	

	/**
	 * 选择颜色
	 * 
	 * @param inRoad
	 *            道路
	 * @return
	 */
	private Color selectColor(Road inRoad) {
		Color color;
		switch (inRoad.getNRC()) {
		case defineNRC.HightWay:// 高速道路
			color = colorNRC.HightWay;
			break;
		case defineNRC.NationalRoad:// 国道
			color = colorNRC.NationalRoad;
			break;
		case defineNRC.CityFreeWay:// 城市快速道路，城市间快速道路
			color = colorNRC.CityFreeWay;
			break;
		case defineNRC.ProcinceRoad:// 省道
			color = colorNRC.ProcinceRoad;
			break;
		case defineNRC.CityMajorRoad:// 城市主干道
			color = colorNRC.CityMajorRoad;
			break;
		case defineNRC.CityMinorRoad:// 城市次干道
			color = colorNRC.CityMinorRoad;
			break;
		case defineNRC.NormalRoad:// 一般道
			color = colorNRC.NormalRoad;
			break;
		case defineNRC.MinorRoad1:// 細道路（細道路1）
			color = colorNRC.MinorRoad1;
			break;
		case defineNRC.MinorRoad2:// 細道路（細道路2）
			color = colorNRC.MinorRoad2;
			break;
		default:
			color = colorNRC.Freey;// Ferry link（航道）
			break;
		}
		return color;
	}

	/**
	 * 根据每条道路内的形状点描画
	 * 
	 * @param g
	 * @param inRoad
	 * @param color
	 */
	private void drawRoadPoints(Graphics g, Road inRoad, Color color) {
		g.setColor(color);
		for (int j = 1; j < inRoad.pointxsf.length; j++) {
			// 正规化坐标转像素
			//正规化坐标变成1/2560秒
			Point prePoint = unitToGeo(inRoad.pointxsf[j - 1], inRoad.pointysf[j - 1]);
			//1/2560秒变成像素
			Point2D.Double prePix = Device.geoToPix(prePoint);
			//正规化坐标变成1/2560秒
			Point curPoint = unitToGeo(inRoad.pointxsf[j], inRoad.pointysf[j]);
			//1/2560秒变成像素
			Point2D.Double curPix = Device.geoToPix(curPoint);
			int x1 = (int) (prePix.x + 0.5);
			int y1 = (int) (prePix.y + 0.5);
			int x2 = (int) (curPix.x + 0.5);
			int y2 = (int) (curPix.y + 0.5);
			g.drawLine(x1, y1, x2, y2);
		}
	}
	
	/**
	 * 将正规化坐标转换成1/2560秒坐标
	 * @param blockID 
	 * @return
	 */
	public Point unitToGeo(double longitude, double latitude) {
		double left = MMBaseInfo.LonBlockWidth * xIndex + MMBaseInfo.XStart;
		double bottom = MMBaseInfo.LatBlockHeight * yIndex + MMBaseInfo.YStart;
		int width = MMBaseInfo.LonBlockWidth;
		int heigth = MMBaseInfo.LatBlockHeight;
		Point geoPoint = new Point();
		geoPoint.x = (int) (longitude * width / MMBaseInfo.MaxUnitX + left);
		geoPoint.y = (int) (latitude * heigth / MMBaseInfo.MaxUnitY + bottom);
		return geoPoint;
	}
	
	
	/**
	 * 取得block的左下角经度
	 * @return
	 */
	public double getLeft()
	{
		double left = MMBaseInfo.LonBlockWidth * xIndex + MMBaseInfo.XStart;
		return left;
	}
	
	/**
	 * 取得block的左下角纬度
	 * @return
	 */
	public double getBottom()
	{
		double bottom = MMBaseInfo.LatBlockHeight * yIndex + MMBaseInfo.YStart;
		return bottom;
	}

	
	public static MMBlockID getMMBlockIDbyGeoPoint(GeoPoint geoPoint){
		MMBlockID mmBlockID = new MMBlockID();
		GeoPoint point = geoPoint;
		if (point != null) {
			mmBlockID.xIndex = (geoPoint.getLongitude() - MMBaseInfo.XStart) / MMBaseInfo.LonBlockWidth;
			mmBlockID.yIndex = (geoPoint.getLatitude() - MMBaseInfo.YStart) / MMBaseInfo.LatBlockHeight;
		}
		return mmBlockID;
	}
	
	

	public GeoRect getGeoRect() {
		GeoRect result = new GeoRect((int) getLeft(), (int) getBottom() + MMBaseInfo.LatBlockHeight,
				 (int) getLeft() + MMBaseInfo.LonBlockWidth, (int) getBottom());
		return result;
	}

	
	public void parse(byte[] bs) throws IOException {
		MMBlockID blockId = new MMBlockID(xIndex, yIndex);
		if (bs != null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(bs);
			DataInputStream dis = new DataInputStream(bis);
			Block block = new Block(blockId.xIndex,blockId.yIndex);
			block.parse(dis);
			// 通过block制作roads 
			roads = makeRoads(block);
		}
	}
	private List<Road> makeRoads(Block block) {
		List<Road> roads = new ArrayList<Road>();
		for(LinkRec linkRec : block.links){
			Road road = linkRec.road;
			road.blockid = new MMBlockID(block.blockIndexX,block.blockIndexY);
			
			//统一xy比列
			for(int i = 0; i < road.pointxsf.length; i++){
				road.pointysf[i] *= MMBaseInfo.xyUnitRatio;
			}
			
			//计算每段形状的长度
			road.distance = new short[road.pointxsf.length - 1];
			for(int i = 0; i < road.pointxsf.length - 1; i++){
				double dertX = road.pointxsf[i+1] - road.pointxsf[i];
				double dertY = road.pointysf[i+1] - road.pointysf[i];
				road.distance[i] = (short) (Math.sqrt(dertX * dertX + dertY* dertY) * MMBaseInfo.meterPerUnit + 0.5);
				if(road.distance[i] == 0){
					road.distance[i] = 1;
				}
				road.length += road.distance[i];
			}
			
			//计算每段形状的角度
			road.angles = new short[road.pointxsf.length - 1];
			for(int i = 0; i < road.pointxsf.length - 1; i++){
				double dertX = road.pointxsf[i+1] - road.pointxsf[i];
				double dertY = road.pointysf[i+1] - road.pointysf[i];
				road.angles[i] = (short) ((-Math.atan2(dertY, dertX) + Math.PI / 2) * 180 / Math.PI);
				if(road.angles[i] < 0){
					road.angles[i] += 360;
				}
				if(road.angles[i] >= 360){
					road.angles[i] -= 360;
				}
			}

			roads.add(road);
		}
		
		return roads;
	}
	
	/**
	 * 将正规化坐标转换成1/2560秒坐标
	 * @param geoPoint
	 * @return
	 */
	public Point UnitToGeo(float longitude, float latitude) {
		Point geoPoint = new Point();
		geoPoint.x = (int) (longitude * MMBaseInfo.LonBlockWidth/ MMBaseInfo.MaxUnitX + getLeft());
		geoPoint.y = (int) (latitude * MMBaseInfo.LatBlockHeight/ MMBaseInfo.MaxUnitY + getBottom());
		return geoPoint;
		
	}
	
}