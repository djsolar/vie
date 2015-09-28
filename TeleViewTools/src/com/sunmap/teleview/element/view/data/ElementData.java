package com.sunmap.teleview.element.view.data;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sunmap.teleview.element.view.Device;
import com.sunmap.teleview.element.view.LayerManage.ElementManageRec;
import com.sunmap.teleview.util.ToolsUnit;


/**
 * 要素类
 * @author lijingru
 *
 */
public class ElementData{

	public short layerNo;
	public List<Graphic> graphics = new ArrayList<Graphic>();
	
	// 解析每个Layer要素的内容
	public void parse(DataInputStream dis, ElementManageRec manage,
			TxtMessage txtMessage) {
		try {
			int graphicCount = manage.graphicCount;
			for (int i = 0; i < graphicCount; i++) {
				short flag = dis.readByte();
				if (flag == 11) {// 线数据的格式比较特殊,参考格式说明
					for (int j = 0; j < graphicCount; j++) {
						PolylineGraphic graphic = new PolylineGraphic();
						graphic.read(dis, flag);
						graphics.add((Graphic) graphic);
					}
					dis.readByte();
					break;
				} else if (flag >= 1 && flag <= 7) {
					PolygonGraphic graphic = new PolygonGraphic();
					graphic.read(dis, flag);
					graphics.add(graphic);
				} else if ((flag >= 21 && flag <= 26)
						|| (flag >= 31 && flag <= 34)) {
					PointGraphic graphic = new PointGraphic();
					graphic.read(dis, flag);
					graphics.add((Graphic) graphic);
				} else {// 其他类型
					System.out.println(" 未处理的数据类型");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 制作线类型描画数据
	 * @param mapOverRoad
	 * @param mapRoad
	 * @param blockID
	 */
	public void makePolylineDrawData(Map<Short, GeneralPath> mapRoad, BlockID blockID,short priority){
		for (int i = 0; i < graphics.size(); i++) {
			Graphic graphic = graphics.get(i);
			if (graphic instanceof PolylineGraphic) {
				if (mapRoad.get(layerNo)==null) {
					GeneralPath path = new GeneralPath(PathIterator.WIND_NON_ZERO,1000);
					mapRoad.put(layerNo, path);
				}
				GeneralPath path = mapRoad.get(layerNo);
				((PolylineGraphic) graphic).makePath(path,blockID, priority);
			} 
		}
	}
	
	/**
	 *制作面类型描画数据
	 * @param mapBg
	 * @param blockID
	 */
	public void makePolygonDrawData(Map<Short, GeneralPath> mapBg, BlockID blockID){
		for (int i = 0; i < graphics.size(); i++) {
			Graphic graphic = graphics.get(i);
			if (graphic instanceof PolygonGraphic) {
				// 面类型描画
				if (mapBg.get(layerNo)==null) {
					GeneralPath path = new GeneralPath(PathIterator.WIND_NON_ZERO,1000);
					mapBg.put(layerNo, path);
				}
				GeneralPath path = mapBg.get(layerNo);
				((PolygonGraphic) graphic).makePath(path,blockID);
				
			} 
		}
	}
	/**
	 * 制作点类型描画
	 * @param texts
	 * @param txtMessage
	 * @param blockID
	 */
	public void makePointDrawData(List<TeleViewText> texts,TxtMessage txtMessage, BlockID blockID){
		for (int i = 0; i < graphics.size(); i++) {
			Graphic graphic = graphics.get(i);
			if (graphic instanceof PointGraphic) {
				((PointGraphic) graphic).makeTextsDrawData(texts, txtMessage, blockID);
			}
		}
	}

	/**
	 * 制作道路文字描画线
	 * @param mapRoadTextLine
	 * @param blockID
	 */
	public void makeRoadTextLineDrawData(Map<Short, GeneralPath> mapRoadTextLine, BlockID blockID){
		for (int i = 0; i < graphics.size(); i++) {
			Graphic graphic = graphics.get(i);
			if (graphic instanceof PointGraphic) {
				//文字线
				if (mapRoadTextLine.get(layerNo)==null) {
					GeneralPath path = new GeneralPath(PathIterator.WIND_NON_ZERO,1000);
					mapRoadTextLine.put(layerNo, path);
				}
				GeneralPath path = mapRoadTextLine.get(layerNo);
				// 点类型描画
				((PointGraphic) graphic).makeRoadTextLineData(path, blockID);
			}
		}
	}
	
	/**
	 * 根据点击的点查询背景类型是否是符合要求的元素
	 * @param geoPoint  1/2560秒
	 * @param blockID	
	 * @return
	 */
	public List<BackGroundData> selectBgByPix(Point2D.Float point, BlockID blockID) {
		List<BackGroundData> result = new ArrayList<BackGroundData>();
		for (int i = 0; i < graphics.size(); i++) {
			Graphic graphic = graphics.get(i);
			if (graphic instanceof PolygonGraphic) {
				boolean b = ((PolygonGraphic) graphic).isPointinPolygon(new Point((int)point.x, (int)point.y), blockID);
				if (b == true) {
					BackGroundData item = new BackGroundData();
					item.layerNo = layerNo;
					item.pointxs = ((PolygonGraphic) graphic).pointxs;
					item.pointys = ((PolygonGraphic) graphic).pointys;
					for (int j = 0; j < item.pointxs.length; j++) {
						Point Geopoint = blockID.unitToGeo(item.pointxs[j],item.pointys[j]);
						item.points.add(Geopoint);
					}
					item.blockidString = blockID.getURL();
					result.add(item);
				}
			}
		}		
		return result;
	}
	
	public List<RoadTextLineData> selectRoadLineByPix(Point2D.Float point, int dis,BlockID blockID){
		List<RoadTextLineData> result = new ArrayList<RoadTextLineData>();
		for (int i = 0; i < graphics.size(); i++) {
			Graphic graphic = graphics.get(i);
			if (graphic instanceof PointGraphic) {
				if (((PointGraphic) graphic).isRoadTextLine() == false) {
					continue;
				}
				short[] pointxs = ((PointGraphic) graphic).pointxs;
				short[] pointys=((PointGraphic) graphic).pointys;
				
				List<Point2D> list = new ArrayList<Point2D>();
				for (int j = 0; j < pointxs.length; j++) {
					Point  geoPoint = blockID.unitToGeo(pointxs[j], pointys[j]);
					Point2D pixPoint = Device.geoToPix(geoPoint);
					list.add(pixPoint);
				}
				double apeak = ToolsUnit.getApeak(list, point,true);
				if (apeak < dis) {
					RoadTextLineData roadTextLineData = new RoadTextLineData();
					roadTextLineData.pointxs = ((PointGraphic) graphic).pointxs;
					roadTextLineData.pointys = ((PointGraphic) graphic).pointys;
					roadTextLineData.blockIDString = blockID.getURL();
					for (int j = 0; j < roadTextLineData.pointxs.length; j++) {
						Point Geopoint = blockID.unitToGeo(roadTextLineData.pointxs[j],roadTextLineData.pointys[j]);
						roadTextLineData.points.add(Geopoint);
					}
					result.add(roadTextLineData);
				}
			}
		}		
		return result;
	}
	
	/**
	 * 根据点击的点查询线类型是否是符合要求的元素
	 * @param geoPoint  1/2560秒
	 * @param dis	像素距离
	 * @param blockID	
	 * @param flagString 
	 * @return
	 */
	public List<RoadData> selectLineByPix(Point2D.Float point, int dis,BlockID blockID, String flagString) {
		List<RoadData> result = new ArrayList<RoadData>();
		if (flagString.equals("road") && layerNo < 14) {
			return result;
		}
		if (flagString.equals("RegionLine") && layerNo > 39) {
			return result;
		}
		for (int i = 0; i < graphics.size(); i++) {
			Graphic graphic = graphics.get(i);
			if (graphic instanceof PolylineGraphic) {
				float[] pointxs = ((PolylineGraphic) graphic).pointxs;
				float[] pointys=((PolylineGraphic) graphic).pointys;
				
				List<Point2D> list = new ArrayList<Point2D>();
				for (int j = 0; j < pointys.length; j++) {
					Point  geoPoint = blockID.unitToGeo(pointxs[j], pointys[j]);
					Point2D pixPoint = Device.geoToPix(geoPoint);
					list.add(pixPoint);
				}
				double apeak = ToolsUnit.getApeak(list, point,true);
				if (apeak < dis) {
					RoadData roadData = new RoadData();
					roadData.layerNO = layerNo;
					roadData.attr = ((PolylineGraphic) graphic).attr;
					roadData.blockID = blockID.getURL();
					roadData.pointxs = ((PolylineGraphic) graphic).pointxs;
					roadData.pointys = ((PolylineGraphic) graphic).pointys;
					for (int j = 0; j < roadData.pointxs.length; j++) {
						Point Geopoint = blockID.unitToGeo(roadData.pointxs[j],roadData.pointys[j]);
						roadData.points.add(Geopoint);
					}
					result.add(roadData);
				}
			}
		}		
		return result;
	}
	
}


