package com.mansion.tele.business.background;

import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.business.TaskData;
import com.mansion.tele.business.TaskData.VectoryStyle;
import com.mansion.tele.business.common.BaseShape;
import com.mansion.tele.business.common.TelePolygonShp;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.db.bean.elemnet.ShpPoint;

public class Area {
//Layer
	public byte layerNo = -1;
	public BlockNo blockNo;
	List<Edge> edgeList = null;
	TaskData taskData;
	public TelePolygonShp shape = null;
	public Area(TaskData taskData) {
		this.taskData = taskData;
	}

	public void init(Polygon polygon) {
		edgeList = polygon.edges;
		blockNo = polygon.blockNo;
		switch (polygon.type) {
		case Land:
			layerNo = VectoryStyle.Land;
			break;
		case Island:
			layerNo = VectoryStyle.Island;
			break;
		case Water:
			layerNo = VectoryStyle.Water;
			break;
		case ProvinceAdmArea:
			layerNo = VectoryStyle.ProvinceAdmArea;
			break;
		case SeaArea:
			layerNo = VectoryStyle.SeaArea;
			break;
		default:
			break;
		}
	}
	/**
	 * 获取形状
	 * @return
	 */
	public BaseShape getBaseShape(){
		if(shape != null){
			return shape;
		}
		List<ShpPoint> coordinate = new ArrayList<ShpPoint>();
		for (Edge edge : edgeList) {
			for (ShpPoint shpPoint : edge.coordinate) {
				ShpPoint point = taskData.calcRegular(shpPoint);
				if(coordinate.isEmpty() || !coordinate.get(coordinate.size()-1).equals(point)){
					coordinate.add(point);
				}
			}
		}
		
		if(coordinate.size() >2){
			if(!coordinate.get(coordinate.size()-1).equals(coordinate.get(0))){
				coordinate.add(coordinate.get(0));
			}
			shape = new TelePolygonShp();
			shape.setCoordinate(coordinate);
			if(shape.getCoordinate().size() == 4){
				return shape;
			}
		int index = PolygonHandle.returnConvex(shape.getCoordinate());
		while(index >= 0){
			if(index == 0){
				shape.getCoordinate().remove(0);
				shape.getCoordinate().remove(shape.getCoordinate().size() - 1);
				shape.getCoordinate().add(shape.getCoordinate().get(0));
				index = PolygonHandle.returnConvex(shape.getCoordinate());
			}else{
				shape.getCoordinate().remove(index);
				index = PolygonHandle.returnConvex(shape.getCoordinate());
			}
		}
		}
		return shape;
	}
}
