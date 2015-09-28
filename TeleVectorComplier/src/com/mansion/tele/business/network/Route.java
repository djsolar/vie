package com.mansion.tele.business.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mansion.tele.business.TaskData;
import com.mansion.tele.business.TaskData.VectoryStyle;
import com.mansion.tele.business.common.BaseShape;
import com.mansion.tele.business.common.TeleRoadPloylineShp;
import com.mansion.tele.business.network.RoadNew.defineFOW;
import com.mansion.tele.business.network.RoadNew.defineStyleColor;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
import com.mansion.tele.util.Util;

public class Route {
	public int id;
	// 有方向的道路集合
	public List<DirRoad> roads = new ArrayList<DirRoad>();
	public byte layerNo = -1;
	public BlockNo blockNo;
	public byte fow = -1;
	public byte nrc = -1;
	TaskData taskData;
	public Route(TaskData taskData) {
		this.taskData = taskData;
	}
	public void init(){
		//给Route添加属性
		 int length = 0;
		 for(DirRoad dirRoad:roads){
			 if(dirRoad.road.length >= length){
				 layerNo = dirRoad.road.styleColor;
				 length = dirRoad.road.length;
				 fow = dirRoad.road.fow;
				 nrc = dirRoad.road.nrc;
				}
			 }
	}
	/**
	 * 将路线以5km为单位进行输出
	 * @throws IOException
	 */
	public void writeRouteStrid() throws IOException{
		if(this.layerNo == TaskData.VectoryStyle.Ferry 
				|| this.layerNo == TaskData.VectoryStyle.MinorRoad1 
				|| this.layerNo == TaskData.VectoryStyle.MinorRoad2 
				|| this.layerNo == TaskData.VectoryStyle.HightWay 
				|| this.layerNo == TaskData.VectoryStyle.CityFreeWay ){
			return;
		}
		int length = 0;
		List<String> strids = new ArrayList<String>();
		for (int i = 0; i < roads.size(); i++) {
			DirRoad dirRoad = roads.get(i);
			length = length + dirRoad.road.length;
			strids.add(dirRoad.road.roadId);
			if(length > 5000){
				Util.saveRoute(strids);
				strids = new ArrayList<String>();
				length = 0;
			}
		}
		Util.saveRoute(strids);
	}
	
	public BaseShape getBaseShape(){
		TeleRoadPloylineShp shape = new TeleRoadPloylineShp();
		List<ShpPoint> coordinate = new ArrayList<ShpPoint>();
		for (DirRoad dirRoad : roads) {
			if(!dirRoad.dir){ 
				for (int index = dirRoad.road.coordinate.size()-1; index >= 0 ; index--) {
					ShpPoint shpPoint = dirRoad.road.coordinate.get(index);
					if (coordinate.isEmpty()) {
						coordinate.add(shpPoint);
						continue;
					}
					if(!coordinate.get(coordinate.size()-1).equals(shpPoint)){
						coordinate.add(shpPoint);
					}
				}
			}else{
				for (int index = 0; index < dirRoad.road.coordinate.size() ; index++) {
					ShpPoint shpPoint = dirRoad.road.coordinate.get(index);
					if (coordinate.isEmpty()) {
						coordinate.add(shpPoint);
						continue;
					}
					if(!coordinate.get(coordinate.size()-1).equals(shpPoint)){
						coordinate.add(shpPoint);
					}
				}
			}
		}
		if(!isMeandering(roads.get(0).road)){//判断本来弯曲的道路
			taskData.deletePoint(coordinate);
		}
		//正规化后同点删除
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
		if(regularCoordinate.size() > 1 && regularCoordinate.size() < 256){
			RoadNew road = roads.get(0).road;
			shape.setGrade(road.grade); 
//			shape.setBridge(bridge);
			shape.setBuildindRoad(road.cs);
			shape.setLayerNO(layerNo);
			shape.setNrc(road.nrc);
			shape.setCoordinate(regularCoordinate);
			return shape;
		}else{
			return null;
		}
	}
	/**
	 * 判断本来就是比较弯曲的道路
	 * 
	 * @param road
	 * @return true：是弯曲道路
	 */
	boolean isMeandering(RoadNew road) {
		// 2 环岛 ramp道 不处理，左右转道 链接路不处理 回转到 //
		if (road.fow == defineFOW.ByPath || road.fow == defineFOW.OneWay || road.fow == defineFOW.TwoWay) {
			return false;
		}
		return true;
	}

	/**
	 * 正向扩展路线
	 * 
	 * @return 非null：下一个可以连接到路线的道路，null：不能继续连接
	 */
	public DirRoad nextRoadForward() {

		RoadNew inRoad = roads.get(roads.size() - 1).road;
		boolean inRoadDir = roads.get(roads.size()-1).dir;
		if (inRoad.endNode.roads.size() == 1) {
			return null;
		}
		// 存放可以连接的道路集合
		List<RoadNew> outRoads = new ArrayList<RoadNew>();
		// 复杂交通路口
		if (inRoad.endNode.intersection != null) {
			//TODO 内部路暂时没有考虑，会导致虚拟路口画出来缺
			List<RoadNew> roads = inRoad.endNode.intersection
					.getOutRoads();
			for (RoadNew outRoad : roads) {
				if (inRoad.equals(outRoad) || outRoad.routed) {
					continue;
				}
				if (RoadNew.isStyleEqual(inRoad, outRoad)) {
					outRoads.add(outRoad);
				}
			}
		} else {// 收集可以连接的道路
			for (RoadNew outRoad : inRoad.endNode.getOutRoads()) {
				if (inRoad.equals(outRoad) || outRoad.routed) {
					continue;
				}
				if (RoadNew.isStyleEqual(inRoad, outRoad)) {
					outRoads.add(outRoad);
				}
			}
		}
		DirRoad optimalRoad = new DirRoad();
		// 在可以连接的道路中找出最佳链接道路，1. 角度最合理
		
		// 道路可以连接的最小角度
		double minAngle = 70;
		for (RoadNew outRoad : outRoads) {
			if (inRoad.endNode.equals(outRoad.startNode) 
					||(outRoad.startNode.intersection != null && outRoad.startNode.intersection.equals(inRoad.endNode.intersection))) {// 正序
				// 用30米角度进行比较
				double angle30 = Math.abs(Math.abs(inRoad.endAngle30m
						- outRoad.startAngle30m) - 180);
				if (angle30 < minAngle) {
					minAngle = angle30;
					optimalRoad.dir = inRoadDir;
					optimalRoad.road = outRoad;
				}
			}
			else if (inRoad.endNode.equals(outRoad.endNode)
					||(outRoad.endNode.intersection != null	&& outRoad.endNode.intersection.equals(inRoad.endNode.intersection))) {// 反序
				// 用30米角度进行比较
				double angle30 = Math.abs(Math.abs(inRoad.endAngle30m
						- outRoad.endAngle30m) - 180);
				if (angle30 < minAngle) {
					minAngle = angle30;
					optimalRoad.dir = !inRoadDir;
					optimalRoad.road = outRoad;
				}
			} 
		}
		if(optimalRoad.road == null){
			return null;
		}
		this.addInterRoad(inRoad, optimalRoad.road, inRoadDir, inRoad.endNode);
		return optimalRoad;
	}

	/**
	 * 反向扩展路线
	 * 
	 * @return 非null：下一个可以连接到路线的道路，null：不能继续连接
	 */
	public DirRoad nextRoadReverse() {
		RoadNew outRoad = roads.get(roads.size()-1).road;
		boolean outRoadDir = roads.get(roads.size()-1).dir;
		if (outRoad.startNode.roads.size() == 1) {
			return null;
		}
		List<RoadNew> inRoads = new ArrayList<RoadNew>();
		// 复杂交通路口
		if (outRoad.startNode.intersection != null) {
			List<RoadNew> roads = outRoad.startNode.intersection
					.getInRoads();
			for (RoadNew inRoad : roads) {
				if (outRoad.equals(inRoad) || inRoad.routed) {
					continue;
				}
				if (RoadNew.isStyleEqual(outRoad, inRoad)) {
					inRoads.add(inRoad);
				}
			}
		} else {// 收集可以连接的道路
			for (RoadNew inRoad : outRoad.startNode.getInRoads()) {
				if (outRoad.equals(inRoad) || inRoad.routed) {
					continue;
				}
				if (RoadNew.isStyleEqual(outRoad, inRoad)) {
					inRoads.add(inRoad);
				}
			}
		}
		DirRoad optimalRoad = new DirRoad();
		// 在可以连接的道路中找出最佳链接道路，1. 角度最合理
		
		// 道路可以连接的最小角度
		double minAngle = 70;
		for (RoadNew inRoad : inRoads) {
			if (outRoad.startNode.equals(inRoad.endNode) 
					||(inRoad.endNode.intersection != null && inRoad.endNode.intersection.equals(outRoad.startNode.intersection))) {// 正序
				// 用30米角度进行比较
				double angle30 = Math.abs(Math.abs(outRoad.startAngle30m
						- inRoad.endAngle30m) - 180);
				if (angle30 < minAngle) {
					minAngle = angle30;
					optimalRoad.dir = outRoadDir;
					optimalRoad.road = inRoad;
				}
			} 
			else if (outRoad.startNode.equals(inRoad.startNode)
					||(inRoad.startNode.intersection != null	&& inRoad.startNode.intersection.equals(outRoad.startNode.intersection))) {// 反序
				// 用30米角度进行比较
				double angle30 = Math.abs(Math.abs(outRoad.startAngle30m
						- inRoad.startAngle30m) - 180);
				if (angle30 < minAngle) {
					minAngle = angle30;
					optimalRoad.dir = !outRoadDir;
					optimalRoad.road = inRoad;
				}
			}
		}
		if(optimalRoad.road == null){
			return null;
		}
		this.addInterRoad(outRoad, optimalRoad.road, outRoadDir, outRoad.startNode);
		return optimalRoad;
	}
	
	public void addInterRoad(RoadNew inRoad, RoadNew outRoad, boolean dir, NodeNew startnode) {
		if (startnode.isIntersectionNode() && startnode != outRoad.startNode) {
			IntersectionNew intersectionNew = startnode.intersection;
			double minAngle = 20;
			RoadNew innRoad = null;
			innRoad = inRoad;
			List<RoadNew> innerlists = new ArrayList<RoadNew>();
			for (int i = 0; i < intersectionNew.getIntersectInnerRoads().size(); i++) {
				for (RoadNew innerRoad : startnode.roads) {
					if(innerRoad == innRoad || innerRoad.fow != defineFOW.JunctionLink || innerlists.contains(innerRoad)){
						continue;
					}
					double angle30 = -1;
					if(innRoad.endNode == innerRoad.startNode){
						angle30 = Math.abs(Math.abs(innRoad.endAngle30m
								- innerRoad.startAngle30m) - 180);
						if(angle30 < minAngle || (180 - angle30) < minAngle){
							DirRoad dirRoad = new DirRoad();
							dirRoad.dir = dir;
							dirRoad.road = innerRoad;
							innerRoad.routed = true;
							startnode = innerRoad.endNode;
							innRoad = innerRoad;
							this.roads.add(dirRoad);
							innerlists.add(innerRoad);
							break;
						}
					}else if(innRoad.startNode == innerRoad.endNode){
						angle30 = Math.abs(Math.abs(innerRoad.endAngle30m
								- innRoad.startAngle30m) - 180);
						if(angle30 < minAngle || (180 - angle30) < minAngle){
							DirRoad dirRoad = new DirRoad();
							dirRoad.dir = dir;
							dirRoad.road = innerRoad;
							innerRoad.routed = true;
							startnode = innerRoad.startNode;
							innRoad = innerRoad;
							this.roads.add(dirRoad);
							innerlists.add(innerRoad);
							break;
						}
					}else if(innRoad.startNode == innerRoad.startNode){
						angle30 = Math.abs(Math.abs(innerRoad.startAngle30m
								- innRoad.startAngle30m) - 180);
						if(angle30 < minAngle || (180 - angle30) < minAngle){
							DirRoad dirRoad = new DirRoad();
							dirRoad.dir = !dir;
							dirRoad.road = innerRoad;
							innerRoad.routed = true;
							startnode = innerRoad.endNode;
							innRoad = innerRoad;
							this.roads.add(dirRoad);
							innerlists.add(innerRoad);
							break;
						}
					}else if(innRoad.endNode == innerRoad.endNode){
						angle30 = Math.abs(Math.abs(innerRoad.endAngle30m
								- innRoad.endAngle30m) - 180);
						if(angle30 < minAngle || (180 - angle30) < minAngle){
							DirRoad dirRoad = new DirRoad();
							dirRoad.dir = !dir;
							dirRoad.road = innerRoad;
							innerRoad.routed = true;
							startnode = innerRoad.startNode;
							innRoad = innerRoad;
							this.roads.add(dirRoad);
							innerlists.add(innerRoad);
							break;
						}
					}
				}
			}
		}
	}
	
	public void addStartEndInterRoad(){
		DirRoad startRoad = this.roads.get(0);
		DirRoad endRoad = this.roads.get(this.roads.size() - 1);
		double minAngle = 20;
		NodeNew startnode = null;
		if(startRoad.dir){
			startnode = startRoad.road.startNode;
		}else{
			startnode = startRoad.road.endNode;
		}
		if(startnode.isIntersectionNode()){
			for (int i = 0; i < startRoad.road.startNode.roads.size(); i++) {
				RoadNew road = startRoad.road.startNode.roads.get(i);
				if(road.fow != defineFOW.JunctionLink){
					continue;
				}
				double angle30 = Math.abs(Math.abs(road.endAngle30m
						- startRoad.road.startAngle30m) - 180);
				if(road != startRoad.road 
						&& road.styleColor == startRoad.road.styleColor
						&& (angle30 < minAngle || (180 - angle30) < minAngle)){
					List<DirRoad> startRoads = new ArrayList<DirRoad>();
					if(startRoad.dir && startnode == road.endNode){
						DirRoad rDirRoad = new DirRoad(road, startRoad.dir);
						road.routed = true;
						startRoads.add(rDirRoad);
						startRoads.addAll(this.roads);
						this.roads = startRoads;
						break;
					}else{
						DirRoad rDirRoad = new DirRoad(road, !startRoad.dir);
						road.routed = true;
						startRoads.add(rDirRoad);
						startRoads.addAll(this.roads);
						this.roads = startRoads;
						break;
					}
				}
			}
		}
		NodeNew endnode = null;
		if(endRoad.dir){
			endnode = endRoad.road.endNode;
		}else{
			endnode = endRoad.road.startNode;
		}
		if(endnode.isIntersectionNode()){
			for (int i = 0; i < endRoad.road.endNode.roads.size(); i++) {
				RoadNew road = endRoad.road.endNode.roads.get(i);
				if(road.fow != defineFOW.JunctionLink){
					continue;
				}
				double angle30 = Math.abs(Math.abs(endRoad.road.endAngle30m
						- road.startAngle30m) - 180);
				if(road != endRoad.road 
						&& road.styleColor == endRoad.road.styleColor
						&& (angle30 < minAngle || (180 - angle30) < minAngle)){
					if(endRoad.dir && endnode==road.startNode){
						DirRoad rDirRoad = new DirRoad(road, endRoad.dir);
						this.roads.add(rDirRoad);
						break;
					}else{
						DirRoad rDirRoad = new DirRoad(road, !endRoad.dir);
						this.roads.add(rDirRoad);
						break;
					}
				}
			}
		}
	}
	
	public void roadStyleTransform(){
		if(this.fow == defineFOW.RampSlipRoad && this.layerNo == VectoryStyle.CityFreeWay){
				List<RoadNew> firstendroadlist = new ArrayList<RoadNew>();
				firstendroadlist.addAll(this.roads.get(0).road.startNode.roads);
				firstendroadlist.addAll(this.roads.get(0).road.endNode.roads);
				firstendroadlist.addAll(this.roads.get(this.roads.size() - 1).road.startNode.roads);
				firstendroadlist.addAll(this.roads.get(this.roads.size() - 1).road.endNode.roads);
				for (int i = 0; i < firstendroadlist.size(); i++) {
					RoadNew comroad = firstendroadlist.get(i);
					if(this.layerNo > comroad.styleColor && comroad.styleColor > VectoryStyle.Ferry){
						this.layerNo = comroad.styleColor;
						this.nrc = comroad.nrc;
					}
			}
		}
	}
}
