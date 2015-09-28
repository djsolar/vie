package com.mansion.tele.business.viewdata;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mansion.tele.action.distriManage.TestConServlet;
import com.mansion.tele.business.TaskData;
import com.mansion.tele.business.TaskData.VectoryStyle;
import com.mansion.tele.business.background.Area;
import com.mansion.tele.business.background.Background.Type;
import com.mansion.tele.business.background.Line;
import com.mansion.tele.business.background.Polygon;
import com.mansion.tele.business.background.Polyline;
import com.mansion.tele.business.common.BaseShape;
import com.mansion.tele.business.common.TelePolygonShp;
import com.mansion.tele.business.landmark.MarkPoint;
import com.mansion.tele.business.network.DirRoad;
import com.mansion.tele.business.network.RoadNew;
import com.mansion.tele.business.network.RoadNew.defineFOW;
import com.mansion.tele.business.network.RoadNew.defineNRC;
import com.mansion.tele.business.network.Route;
import com.mansion.tele.common.BlockNo;
import com.mansion.tele.db.bean.elemnet.ShpPoint;

public class ViewData {
	TaskData taskData;
	public List<BlockData> blockDatas = new ArrayList<BlockData>();
	
	 public ViewData(TaskData taskData){
		 this.taskData = taskData;
	 }
	 /**
	  * 把blockdatas按照格式写到os
	  */
	 public void createFormat(OutputStream os){
		 
	 }
	 
	 public void createViewData() throws IOException{
		 //创建link列
		 List<Route> routes = createRoutes();
		 //Route简引
		 //分割路网
		 taskData.network.splitRoadByBlock();
		 
		 
		 //分割背景
		 taskData.background.splitPolygonByBlock();
		 //背景线简引
		 
		 //背景线分割
		 taskData.background.splitPolylineByBlock();
		 
		 //分割地标
		 taskData.landmark.makeMarkInfor();
		 
		 //
		 List<Landmark> marks = createLandmark(); 
		 
		 List<Area> areas = createAreas();
		 List<Line> lines = createLines();
		 //创建block数据
		 createBlockData(routes, marks,areas,lines);
		 //过滤BlockData 内容为空
//		 filterBlock();
		 
	 }
	 /**
	  * 在task范围内创建Polygon面结构
	  * @return
	  */
	 public List<Area> createAreas(){
		 List<Area> areas = new ArrayList<Area>();
		 for(Polygon polygon:taskData.background.polygongs){
			 Area area = new Area(taskData);
			 area.init(polygon);
			 areas.add(area);
		 }
		 return areas;
	 }
	 /**
	  * 在task范围内创建背景Line结构
	  * 
	  */
	 public List<Line> createLines(){
		 List<Line> lines = new ArrayList<Line>();
		 for(Polyline polyline:taskData.background.polylines){
			 Line line = new Line(taskData);
			 line.init(polyline);
			 lines.add(line);
		 }
		 return lines;
	 }

	/**
	 * 在task范围内创建link列
	 * 
	 * @return
	 * @throws IOException 
	 */
	public List<Route> createRoutes() throws IOException {
		List<Route> routeList = new ArrayList<Route>();
		for (RoadNew road : taskData.network.roadList) {
			road.routed = false;
		}
		List<RoadNew> collectionJunctions = new ArrayList<RoadNew>();
		// 遍历所有道路
		for (RoadNew seedRoad : taskData.network.roadList) {
			// if(seedRoad.blockNo.iX == 26457 || seedRoad.blockNo.iY == 16059){
			// System.out.println(seedRoad.regularCoordinate+"QP "+seedRoad.roadId+seedRoad.routed);
			// }
			// TODO 公用道路
			if (seedRoad.routed == true || seedRoad.isJunctionLink()) {
				if(seedRoad.isJunctionLink()){
					collectionJunctions.add(seedRoad);
				}
				continue;
			}
			Route route = new Route(this.taskData);
			route.id = routeList.size();
			// 添加种子道路
			DirRoad nextDirRoad = new DirRoad(seedRoad, true);
			route.roads.add(nextDirRoad);
			nextDirRoad.road.routed = true;

			// 反序链接
			for (int i = 0; i < 5000; i++) {
				if (nextDirRoad.dir == true) {
					nextDirRoad = route.nextRoadReverse();
				} else {
					nextDirRoad = route.nextRoadForward();
				}
				if (nextDirRoad == null) {
					break;
				}
				route.roads.add(nextDirRoad);

				nextDirRoad.road.routed = true;
			}

			// 调转顺序
			Collections.reverse(route.roads);

			nextDirRoad = route.roads.get(route.roads.size() - 1);
			// 正序链接
			for (int i = 0; i < 5000; i++) {
				if (nextDirRoad.dir == true) {
					nextDirRoad = route.nextRoadForward();
				} else {
					nextDirRoad = route.nextRoadReverse();
				}
				if (nextDirRoad == null) {
					break;
				}
				route.roads.add(nextDirRoad);
				nextDirRoad.road.routed = true;
			}
			// route 初始化 添加属性值
			route.init();
			if(TestConServlet.OutRouteStrid && taskData.task.getLevel() == 0){
				route.writeRouteStrid();
			}
			route.addStartEndInterRoad();
//			route.roadStyleTransform();
			routeList.add(route);
		}
		for (RoadNew roadNew : collectionJunctions) {
			if(!roadNew.routed){
				Route route = new Route(this.taskData);
				route.id = routeList.size();
				DirRoad dirRoad = new DirRoad(roadNew, true);
				route.roads.add(dirRoad);
				route.init();
				routeList.add(route);
			}
		}
		return routeList;
	}
	 private void createBlockData(List<Route> routes, List<Landmark> landmarks,List<Area> areas,List<Line> lines){
		 //初始创建BlockData.blockNo
		 BlockNo leftDownBlockNo = taskData.calcBlockNo(new ShpPoint(taskData.task.getLeft(), taskData.task.getBottom()));
		 BlockNo rightUpBlockNo = taskData.calcBlockNo(new ShpPoint(taskData.task.getRight(), taskData.task.getTop()));
		 
		 blockDatas = new ArrayList<BlockData>();
		 for (int y = leftDownBlockNo.getiY(); y < rightUpBlockNo.getiY(); y++) {
			 for (int x = leftDownBlockNo.getiX(); x < rightUpBlockNo.getiX(); x++) {
				BlockData blockData = new BlockData();
				blockData.blockNo = new BlockNo(x, y);
				blockDatas.add(blockData);
			}
		}
//		 System.out.println("block "+count );
		//route block 分割
		List<Route> blockRoutes = new ArrayList<Route>();
		for (Route route : routes) {
			int pointCount = 0;
			Route blockRoute = null;
			//对每个Task范围内的route进行Block分割
			for(int index = 0;index < route.roads.size();index++){
				DirRoad dirRoad = route.roads.get(index);
				if(dirRoad.road.coordinate.size() >= 256){
					System.out.println("一个道路的形状点个数 ： "+dirRoad.road.coordinate.size());
				}
//				
				pointCount += dirRoad.road.coordinate.size();
				
				if (blockRoute != null && (blockRoute.blockNo.equals(dirRoad.road.blockNo) && pointCount < 256)) {
					blockRoute.roads.add(dirRoad);
				} else {
					pointCount = dirRoad.road.coordinate.size();
					blockRoute = new Route(taskData);
					blockRoute.roads.add(dirRoad);
					blockRoute.layerNo = route.layerNo;
					blockRoute.blockNo = dirRoad.road.blockNo;
					blockRoutes.add(blockRoute);
				}
			}
		}
		
		//对Route block进行排序，Y递增,Y相同时X递增
		Collections.sort(blockRoutes, new Comparator<Route>() {

			@Override
			public int compare(Route route1, Route route2) {
				BlockNo blockNo1 = route1.blockNo;
				BlockNo blockNo2 = route2.blockNo;
				if(blockNo1.iY > blockNo2.iY){
					return 1;
				}
				if(blockNo1.iY < blockNo2.iY){
					return -1;
				}
				if(blockNo1.iX > blockNo2.iX){
					return 1;
				}
				if(blockNo1.iX < blockNo2.iX){
					return -1;
				}
				return 0;
			}
		});
		
		//对BlockData进行Route赋值，route对应
		int routeIndex = 0, blockIndex = 0; 
		for (;blockIndex < this.blockDatas.size() && routeIndex < blockRoutes.size();) {
			BlockData blockData = this.blockDatas.get(blockIndex);
			Route route = blockRoutes.get(routeIndex);
			if (route.blockNo.equals(blockData.blockNo)) {
				blockData.routes.add(route);
				routeIndex++;
			} else {
				if(route.blockNo.iY <leftDownBlockNo.iY || route.blockNo.iX <leftDownBlockNo.iX ||route.blockNo.iX >= rightUpBlockNo.iX || route.blockNo.iY >= rightUpBlockNo.iY){//因为分割会导致道路数据所在的图页号超过一格
					routeIndex++;
				}else{
					blockIndex++;
				}
			}
		}
//		System.out.println(routeIndex+" : "+ blockIndex);
		//----route赋值完毕---
		
		Collections.sort(landmarks, new Comparator<Landmark>() {

			@Override
			public int compare(Landmark route1, Landmark route2) {
				BlockNo blockNo1 = route1.blockNo;
				BlockNo blockNo2 = route2.blockNo;
				if(blockNo1.iY > blockNo2.iY){
					return 1;
				}
				if(blockNo1.iY < blockNo2.iY){
					return -1;
				}
				if(blockNo1.iX > blockNo2.iX){
					return 1;
				}
				if(blockNo1.iX < blockNo2.iX){
					return -1;
				}
				return 0;
			}
		});
		
		//对BlockData进行Route赋值，route对应
		for (int landindex = 0, blockindex = 0; blockindex < this.blockDatas.size() && landindex < landmarks.size();) {
			BlockData blockData = this.blockDatas.get(blockindex);
			Landmark landmark = landmarks.get(landindex);
			if (landmark.blockNo.equals(blockData.blockNo)) {
				blockData.landmarks.add(landmark);
				landindex++;
			} else {
				if(landmark.blockNo.iX >= rightUpBlockNo.iX || landmark.blockNo.iY >= rightUpBlockNo.iY
						|| landmark.blockNo.iX < leftDownBlockNo.iX || landmark.blockNo.iY < leftDownBlockNo.iY){//因为分割会导致道路数据所在的图页号超过一格
					landindex++;
				}else{
					blockindex++;
				}
			}
		}
		// --赋值landmark完毕
		Collections.sort(areas, new Comparator<Area>() {

			@Override
			public int compare(Area area1, Area area2) {
				BlockNo blockNo1 = area1.blockNo;
				BlockNo blockNo2 = area2.blockNo;
				if(blockNo1.iY > blockNo2.iY){
					return 1;
				}
				if(blockNo1.iY < blockNo2.iY){
					return -1;
				}
				if(blockNo1.iX > blockNo2.iX){
					return 1;
				}
				if(blockNo1.iX < blockNo2.iX){
					return -1;
				}
				return 0;
			}
		});
		
		//对BlockData进行Area赋值，Area对应
		for (int areaIndex = 0, blockindex = 0; blockindex < this.blockDatas.size() && areaIndex < areas.size();) {
			BlockData blockData = this.blockDatas.get(blockindex);
			Area area = areas.get(areaIndex);
			if (area.blockNo.equals(blockData.blockNo)) {
				blockData.polygons.add(area);
				areaIndex++;
			} else {
				if(area.blockNo.iX >= rightUpBlockNo.iX || area.blockNo.iY >= rightUpBlockNo.iY){//因为分割会导致道路数据所在的图页号超过一格
					areaIndex++;
				}else{
					blockindex++;
				}
			}
		}
		// 面赋值完毕
		//背景线处理
		//对Route block进行排序，Y递增,Y相同时X递增
		Collections.sort(lines, new Comparator<Line>() {

			@Override
			public int compare(Line line1, Line line2) {
				BlockNo blockNo1 = line1.blockNo;
				BlockNo blockNo2 = line2.blockNo;
				if(blockNo1.iY > blockNo2.iY){
					return 1;
				}
				if(blockNo1.iY < blockNo2.iY){
					return -1;
				}
				if(blockNo1.iX > blockNo2.iX){
					return 1;
				}
				if(blockNo1.iX < blockNo2.iX){
					return -1;
				}
				return 0;
			}
		});
		
		//对BlockData进行Line赋值，Line对应
		for (int lineIndex = 0, blockindex = 0; blockindex < this.blockDatas.size() && lineIndex < lines.size();) {
			BlockData blockData = this.blockDatas.get(blockindex);
			Line line = lines.get(lineIndex);
			if (line.blockNo.equals(blockData.blockNo)) {
				blockData.polylines.add(line);
				lineIndex++;
			} else {
				if(line.blockNo.iX >= rightUpBlockNo.iX || line.blockNo.iY >= rightUpBlockNo.iY){//因为分割会导致道路数据所在的图页号超过一格
					lineIndex++;
				}else{
					blockindex++;
				}
			}
		}
		//海洋合并
		for (BlockData blockData : this.blockDatas) {
			boolean merger = true;
			if(blockData.polygons.isEmpty() || blockData.polygons.size() == 1){
				merger = false;
				continue;
			}
			List<ShpPoint> pointList = new ArrayList<ShpPoint>();
			for (Area area : blockData.polygons) {
				if(area.layerNo != VectoryStyle.SeaArea){
					merger = false;
					break;
				}
				BaseShape baseShpe = area.getBaseShape();
				if(baseShpe != null){
					List<ShpPoint> points = baseShpe.getCoordinate();
					for (ShpPoint shpPoint : points) {
						if(shpPoint.x != 0 && shpPoint.x != 255 && shpPoint.y != 0 && shpPoint.y != 255){
							merger = false;
							break;
						}
						pointList.add(shpPoint);
					}
				}
			}
			if(merger && (!pointList.contains(new ShpPoint(0, 0)) ||!pointList.contains(new ShpPoint(0, 255)) ||!pointList.contains(new ShpPoint(255, 255)) ||!pointList.contains(new ShpPoint(255, 0)))){
				merger = false;
			}
			if(merger){
				blockData.polygons.clear();
				Area area = new Area(taskData);
				area.layerNo = VectoryStyle.SeaArea;
				area.blockNo = blockData.blockNo;
				area.shape = new TelePolygonShp();
				List<ShpPoint> coordinate = new ArrayList<ShpPoint>();
				area.shape.setCoordinate(coordinate);
				blockData.polygons.add(area);
				coordinate.add(new ShpPoint(0, 0));
				coordinate.add(new ShpPoint(255, 0));
				coordinate.add(new ShpPoint(255, 255));
				coordinate.add(new ShpPoint(0, 255));
				coordinate.add(new ShpPoint(0, 0));
			}
		}
	 }

	 public List<Landmark> createLandmark(){
		 List<MarkPoint> landmarks = this.taskData.landmark.LandMarkType_list;
		 List<Landmark> marks = new ArrayList<Landmark>();
		 for (MarkPoint landMarkType : landmarks) {
			Landmark landmark = landMarkType.changeToForm(this.taskData.task.getLevel());
			if(landmark != null){
				marks.add(landmark);
			}
		}
		 for (int i = 0; i < this.taskData.landmark.RoadName.size(); i++) {
			 Landmark landmark = this.taskData.landmark.RoadName.get(i).changeToForm(this.taskData.task.getLevel());
				if(landmark != null){
					marks.add(landmark);
				}
		}
		 return marks;
	 }
}
