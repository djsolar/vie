package com.mansion.tele.business.background;

import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.business.TaskData;
import com.mansion.tele.business.TaskData.VectoryStyle;
import com.mansion.tele.business.common.BaseShape;
import com.mansion.tele.business.common.TelePolylineShp;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.db.bean.elemnet.ShpPoint;

public class Line {
	public byte layerNo = -1;
	public BlockNo blockNo;
	List<Edge> edgeList = null;
	TaskData taskData = null;
	TelePolylineShp shape;
	
	public Line(TaskData taskData) {
		this.taskData = taskData;
	}
	//初始化信息
	public void init(Polyline polyline) {
		edgeList = polyline.edges;
		blockNo = polyline.blockNo;
		
		switch (polyline.type) {
		case RailWay:
			layerNo = VectoryStyle.RailWay;
			break;
		case ProviceBorder:
			layerNo = VectoryStyle.AdmaBorderProvince;
			break;
		case AdmaBorderNation:
			layerNo = VectoryStyle.AdmaBorderNation;
			break;
		case UnNationBorder:
			layerNo = VectoryStyle.AdmaBorderUnNation;
			break;
		case UnProviceBorder:
			layerNo = VectoryStyle.AdmaBorderProvince;
			break;
		case SpecialBorder:
			layerNo = VectoryStyle.AdmaBorderSpecial;
			break;
		case WaterLine:
			layerNo = VectoryStyle.WaterLine;
			break;
		case SubwayOne:
			layerNo = VectoryStyle.SubwayOne;
			break;
		case SubwayTwo:
			layerNo = VectoryStyle.SubwayTwo;
			break;
		case SubwayThree:
			layerNo = VectoryStyle.SubwayThree;
			break;
		case SubwayFour:
			layerNo = VectoryStyle.SubwayFour;
			break;
		case SubwayFive:
			layerNo = VectoryStyle.SubwayFive;
		case SubwaySix:
			layerNo = VectoryStyle.SubwaySix;
			break;
		case SubwaySeven:
			layerNo = VectoryStyle.SubwaySeven;
			break;
		case SubwayEight:
			layerNo = VectoryStyle.SubwayEight;
			break;
		case SubwayNine:
			layerNo = VectoryStyle.SubwayNine;
			break;
		case SubwayTen:
			layerNo = VectoryStyle.SubwayTen;
			break;
		case SubwayEleven:
			layerNo = VectoryStyle.SubwayEleven;
			break;
		case SubwayTwelve:
			layerNo = VectoryStyle.SubwayTwelve;
			break;
		case SubwayThirteen:
			layerNo = VectoryStyle.SubwayThirteen;
			break;
		case SubwayFourteen:
			layerNo = VectoryStyle.SubwayFourteen;
			break;
		case LightRailOne:
			layerNo = VectoryStyle.LightRailOne;
			break;
		case LightRailTwo:
			layerNo = VectoryStyle.LightRailTwo;
			break;
		case LightRailThree:
			layerNo = VectoryStyle.LightRailThree;
			break;
		default:
//			layerNo = VectoryStyle.SubwayOne;
			break;
		}
	}
	/**
	 * 获取形状
	 */
	public BaseShape getBaseShape(){
		List<ShpPoint> coordinate = new ArrayList<ShpPoint>();
		//收集形状点
		for (Edge edge : edgeList) {
			for (ShpPoint shpPoint : edge.coordinate) {
				if(coordinate.isEmpty() || !coordinate.get(coordinate.size()-1).equals(shpPoint)){
					coordinate.add(shpPoint);
				}
			}
		}
		//形状点简引
//		taskData.deletePoint(coordinate);
		List<ShpPoint> regularCoordinate = new ArrayList<ShpPoint>();
		for (int i = 0; i < coordinate.size(); i++) {
			ShpPoint shpPoint = coordinate.get(i);
			ShpPoint point = taskData.calcRegular(shpPoint);
			//首末点不能删除
			if(!regularCoordinate.isEmpty() && i != coordinate.size()-1){
				ShpPoint lastPoint = regularCoordinate.get(regularCoordinate.size()-1);
				if (lastPoint.equals(point)){
					continue;
				}if(Math.abs(lastPoint.x - point.x) < 2 && Math.abs(lastPoint.y - point.y) < 2){
					continue;
				}
			}
			regularCoordinate.add(point);
		}
		if(regularCoordinate.size() > 1){
			shape = new TelePolylineShp();
			shape.setCoordinate(regularCoordinate);
		}
		return shape;
	}
}
