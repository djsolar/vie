package com.mansion.tele.business.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mansion.tele.business.DataManager;
import com.mansion.tele.business.Task;
import com.mansion.tele.business.TaskData;
import com.mansion.tele.business.landmark.Landmark;
import com.mansion.tele.business.landmark.MarkPoint;
import com.mansion.tele.business.landmark.MarkPointRect;
import com.mansion.tele.business.landmark.ShowStyle;
import com.mansion.tele.business.network.RoadNew.defineFOW;
import com.mansion.tele.business.network.RoadNew.defineNRC;
import com.mansion.tele.common.GeoRect;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
import com.mansion.tele.util.NumberUtil;
import com.mansion.tele.util.PolygonUtil;

public class RouteForRoadName {


	TaskData taskData;
	
	List<ShowStyle> roadStyleList = null;
	
	ShowStyle showStyle = new ShowStyle();
	
	private final double screenlen = 3d;
	private final double minlen = 2d;
	// 一纬度等于多少米
	int oneLatToMeter;
	// 一经度等于多少米
	int oneLonToMeter;
	
	static Comparator<RoadNameLine> rlcComparator = new Comparator<RouteForRoadName.RoadNameLine>() {
		
		@Override
		public int compare(RoadNameLine o1, RoadNameLine o2) {
			return o1.name.compareTo(o2.name);
		}
	};
	
	static Comparator<RoadNameLine> rnlComparator = new Comparator<RouteForRoadName.RoadNameLine>() {
		
		@Override
		public int compare(RoadNameLine o1, RoadNameLine o2) {
			return (int) (o1.length - o2.length);
		}
	};
	
	static Comparator<ShowStyle> showStyleComparator = new Comparator<ShowStyle>() {
		@Override
		public int compare(ShowStyle o1, ShowStyle o2) {
			return (o1.getLevel() + ";" + o1.getlMarkType()).compareTo(o2.getLevel() + ";"
					+ o2.getlMarkType());
		}
	};
	
	/**
	 * 根据配置文件获得显示类型
	 * @param task
	 * @param road_M
	 * @param scaleNo
	 * @return
	 */
	private SaveType judgeSaveType(Task task, RoadNameLine road_M, int scaleNo) {
		boolean makeRoad = false;
		boolean makeNo = false;
		if(Landmark.style.isShowByLevelAndScale(road_M.nrc, task.getLevel(), scaleNo)){
			makeRoad = true;
		}
		if(Landmark.style.isShowByLevelAndScale(road_M.nrc+11, task.getLevel(), scaleNo)){
			makeNo = true;
		}
		if(makeRoad && makeNo){
			return SaveType.BOTH_MARK_LINE;
		}
		else if(makeRoad && !makeNo){
			return SaveType.ONLY_LINE;
		}
		else if(!makeRoad && makeNo){
			return SaveType.ONLY_MARK;
		}
		else{
			return SaveType.NOTHING;
			}
		}

//	private boolean isShowSignLandMark(RoadNameLine road_M) {
//		if (RoadNew.defineNRC.HightWay == road_M.nrc || // 高速
//			RoadNew.defineNRC.NationalRoad == road_M.nrc || // 国道
//			RoadNew.defineNRC.ProcinceRoad == road_M.nrc) { // 省道
//			return true;
//		}
//		return false;
//	}


	// 创建插入点
	private ShpPoint creatInsertPoint(double twoPointDistance, double distance,
			ShpPoint left, ShpPoint right) {
		if (twoPointDistance == 0) {
			return right;
		}
		int x = (int) (distance * (right.x - left.x) / twoPointDistance + left.x);
		int y = (int) (distance * (right.y - left.y) / twoPointDistance + left.y);
		return new ShpPoint(x, y);
	}

	/**
	 * 清楚标记
	 */
	private void clearRoadMark(){
		for (RoadNew roadNew : taskData.network.roadList) {
			roadNew.routed = false;
		}
	}
	
	
	public void init(TaskData taskData, List<ShowStyle> roadStyleList){
		this.taskData = taskData;
		this.roadStyleList = roadStyleList;
		this.oneLatToMeter = (int) PolygonUtil.twoPointDistance(
				new ShpPoint(taskData.task.getLeft(), taskData.task.getBottom())
				, new ShpPoint(taskData.task.getLeft(), taskData.task.getBottom() + 3600*2560));
		this.oneLonToMeter = (int) PolygonUtil.twoPointDistance(
				new ShpPoint(taskData.task.getLeft(), taskData.task.getBottom())
				, new ShpPoint(taskData.task.getLeft() + 3600*2560, taskData.task.getBottom()));
		this.clearRoadMark();
	}
	
	
	
	List<RoadNameLine> createRoutes(){
		List<RoadNameLine> routeList = new ArrayList<RoadNameLine>();
		 //遍历所有道路//TODO fow=2 创建Route
		 for (RoadNew seedRoad : taskData.network.roadList) {
			 if(seedRoad.routed == true || (seedRoad.fow != defineFOW.TwoWay
					 && seedRoad.fow != defineFOW.OneWay)
					 || seedRoad.styleColor == TaskData.VectoryStyle.BackGroudRoad
					 || seedRoad.roadName == null
					 || seedRoad.roadName.trim().isEmpty()){
				 continue;
			 }
			 
			 RoadNameLine route = new RoadNameLine();
			 route.name = seedRoad.roadName;
			 route.nrc = seedRoad.nrc;
			 route.fow = seedRoad.fow;
			 if(seedRoad.routeNo != null && seedRoad.routeNo.size() > 0){
				 route.route = seedRoad.routeNo.get(0);
			 }
			 //添加种子道路
			 DirRoad nextDirRoad = new DirRoad(seedRoad, true);
			 route.road.add(nextDirRoad);
			 route.length = route.length + nextDirRoad.road.length;
			 nextDirRoad.road.routed = true;

			 //反序链接	
			 for(int i = 0; i <5000; i++){
				 if(nextDirRoad.dir == true){
					 nextDirRoad = this.nextRoadReverse(route.road);
				 }else{
//					 System.out.println("eror ....反向 。。。");
					 nextDirRoad = this.nextRoadForward(route.road);
				 }
				 if(nextDirRoad == null){
					 break;
				 }
				 route.length = route.length + nextDirRoad.road.length;
				 route.road.add(nextDirRoad);
				 if(route.fow == RoadNew.defineFOW.JunctionLink && nextDirRoad.road.fow != RoadNew.defineFOW.JunctionLink){
					 route.fow = nextDirRoad.road.fow;
				 }
				 
				 nextDirRoad.road.routed = true;
			 }

			 //调转顺序
			 Collections.reverse(route.road);
			 
			 nextDirRoad=route.road.get(route.road.size() - 1);
			//正序链接
			 for(int i = 0; i <5000; i++){
				 if(nextDirRoad.dir == true){
					 nextDirRoad = this.nextRoadForward(route.road);
				 }else{
//					 System.out.println("eror ....反向 3。。。");
					 nextDirRoad = this.nextRoadReverse(route.road);
				 }
				 if(nextDirRoad == null){
					 break;
				 }
				 route.length = route.length + nextDirRoad.road.length;
				 route.road.add(nextDirRoad);
				 nextDirRoad.road.routed = true;
			 }
			 //route 初始化 添加属性值
			 routeList.add(route);
		 }
		 return routeList;
	}
	
	/**
	 * 反向扩展路线
	 * 
	 * @return 非null：下一个可以连接到路线的道路，null：不能继续连接
	 */
	public DirRoad nextRoadReverse(List<DirRoad> roadds) {
		RoadNew outRoad = roadds.get(roadds.size()-1).road;
		boolean outRoadDir = roadds.get(roadds.size()-1).dir;
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
				if (this.RoadNameCompare(outRoad, inRoad)) {
					inRoads.add(inRoad);
				}
			}
		} else {// 收集可以连接的道路
			for (RoadNew inRoad : outRoad.startNode.getInRoads()) {
				if (outRoad.equals(inRoad) || inRoad.routed) {
					continue;
				}
				if (this.RoadNameCompare(outRoad, inRoad)) {
					inRoads.add(inRoad);
				}
			}
		}
		DirRoad optimalRoad = new DirRoad();
		// 在可以连接的道路中找出最佳链接道路，1. 角度最合理
		
		// 道路可以连接的最小角度
		double minAngle = 75;
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
		return optimalRoad;
	}
	/**
	 * 正向扩展路线
	 * 
	 * @return 非null：下一个可以连接到路线的道路，null：不能继续连接
	 */
	public DirRoad nextRoadForward(List<DirRoad> roadds) {

		RoadNew inRoad = roadds.get(roadds.size() - 1).road;
		boolean inRoadDir = roadds.get(roadds.size()-1).dir;
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
				if (this.RoadNameCompare(inRoad, outRoad)) {
					outRoads.add(outRoad);
				}
			}
		} else {// 收集可以连接的道路
			for (RoadNew outRoad : inRoad.endNode.getOutRoads()) {
				if (inRoad.equals(outRoad) || outRoad.routed) {
					continue;
				}
				if (this.RoadNameCompare(inRoad, outRoad)) {
					outRoads.add(outRoad);
				}
			}
		}
		DirRoad optimalRoad = new DirRoad();
		// 在可以连接的道路中找出最佳链接道路，1. 角度最合理
		
		// 道路可以连接的最小角度
		double minAngle = 75;
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
		return optimalRoad;
	}

	public enum SaveType {
		ONLY_MARK, ONLY_LINE, BOTH_MARK_LINE, NOTHING
	}
	/**
	 * 判断道路名路线判断条件
	 * @param roadNewA
	 * @param roadNewB
	 * @return
	 */
	private boolean RoadNameCompare(RoadNew roadNewA, RoadNew roadNewB){
		if(roadNewA.roadName != null 
				&& roadNewB.roadName != null 
				&& roadNewA.roadName.equals(roadNewB.roadName)
				&& roadNewA.nrc == roadNewB.nrc
				&& (((roadNewA.fow == RoadNew.defineFOW.OneWay || roadNewA.fow == RoadNew.defineFOW.JunctionLink) && (roadNewB.fow == RoadNew.defineFOW.OneWay || roadNewB.fow == RoadNew.defineFOW.JunctionLink))
				|| ((roadNewA.fow == RoadNew.defineFOW.TwoWay || roadNewA.fow == RoadNew.defineFOW.JunctionLink) && (roadNewB.fow == RoadNew.defineFOW.TwoWay || roadNewB.fow == RoadNew.defineFOW.JunctionLink)))){
			return true;
		}
		return false;
		
	}
	class RoadNameLine{
		String name;
		int nrc;
		int fow;
		String route;
		double length;
		boolean makeName = true;
		List<DirRoad> road = new ArrayList<DirRoad>();
		List<ShpPoint> pointlist;
	}
	
	class RoadNamePair{
		RoadNameLine roadname1;
		RoadNameLine roadname2;
	}
	
	public void fillShpPonit(RoadNameLine road_M){
		List<DirRoad> roads = road_M.road;
		List<ShpPoint> coordinate = new ArrayList<ShpPoint>();
		for (DirRoad dirRoad : roads) {
			if(!dirRoad.dir){
				for (int index = dirRoad.road.coordinate.size()-1; index >= 0 ; index--) {
					ShpPoint shpPoint = dirRoad.road.coordinate.get(index);
					if (coordinate.isEmpty()) {
						coordinate.add(shpPoint);
						continue;
					}
					if (coordinate.get(coordinate.size() - 1).equals(shpPoint)) {
						coordinate.set(coordinate.size() - 1, shpPoint);
						continue;
					}
					// 简单简引 过滤共线
					if (coordinate.size() >= 2
							&& ((coordinate.get(coordinate.size() - 1).x == shpPoint.x && shpPoint.x == coordinate.get(coordinate.size() - 2).x) || ((coordinate
									.get(coordinate.size() - 1).y == shpPoint.y && shpPoint.y == coordinate.get(coordinate.size() - 2).y)))) {
						coordinate.set(coordinate.size() - 1, shpPoint);
						continue;
					}
					coordinate.add(shpPoint);
				}
			}else{
				for (int index = 0; index < dirRoad.road.coordinate.size() ; index++) {
					ShpPoint shpPoint = dirRoad.road.coordinate.get(index);
					if (coordinate.isEmpty()) {
						coordinate.add(shpPoint);
						continue;
					}
					if (coordinate.get(coordinate.size() - 1).equals(shpPoint)) {
						coordinate.set(coordinate.size() - 1, shpPoint);
						continue;
					}
					// 简单简引 过滤共线
					if (coordinate.size() >= 2
							&& ((coordinate.get(coordinate.size() - 1).x == shpPoint.x && shpPoint.x == coordinate.get(coordinate.size() - 2).x) || ((coordinate
									.get(coordinate.size() - 1).y == shpPoint.y && shpPoint.y == coordinate.get(coordinate.size() - 2).y)))) {
						coordinate.set(coordinate.size() - 1, shpPoint);
						continue;
					}
					coordinate.add(shpPoint);
				}
			}
		}
		road_M.pointlist = coordinate;
	}
	/**
	 * 制作道路名称
	 * @param taskData
	 * @return
	 */
	public List<MarkPoint> getRoadName(TaskData taskData, List<ShowStyle> roadStyleList) {
		this.init(taskData, roadStyleList);
		List<MarkPoint> roadnamemarks = new ArrayList<MarkPoint>();
		if (taskData.task.getLevel() < 6) {

			List<RoadNameLine> roadNameLines = this.createRoutes();
			List<RoadNameLine> twoRoadNameLines = new ArrayList<RouteForRoadName.RoadNameLine>();
			List<RoadNameLine> oneRoadNameLines = new ArrayList<RouteForRoadName.RoadNameLine>();
			for (int i = 0; i < roadNameLines.size(); i++) {
				RoadNameLine roadNameLine = roadNameLines.get(i);
				this.fillShpPonit(roadNameLine);
				if(roadNameLine.fow == RoadNew.defineFOW.TwoWay){
					twoRoadNameLines.add(roadNameLine);
				}else{
					oneRoadNameLines.add(roadNameLine);
				}
			}
			
			this.twoRoadPair(twoRoadNameLines);
			roadnamemarks.addAll(this.makeRoadNameAndRouteNo(roadNameLines));
			
		}
		System.out.println("道路名填充完成");
		return roadnamemarks;
	}
	
	private void twoRoadPair(List<RoadNameLine> twoRoadNameLines) {
		// TODO Auto-generated method stub
		if(twoRoadNameLines == null || twoRoadNameLines.size() == 0){
			return;
		}
		Collections.sort(twoRoadNameLines, rlcComparator);
		List<RoadNameLine> collectTwoRoad = new ArrayList<RouteForRoadName.RoadNameLine>();
		RoadNameLine roadNameLine = twoRoadNameLines.get(0);
		for (int i = 0; i < twoRoadNameLines.size(); i++) {
			RoadNameLine compLine = twoRoadNameLines.get(i);
			if(roadNameLine.name.equals(compLine.name)){
				collectTwoRoad.add(compLine);
			}else{
				this.sameNamePair(collectTwoRoad);
				collectTwoRoad = new ArrayList<RouteForRoadName.RoadNameLine>();
				roadNameLine = compLine;
				collectTwoRoad.add(compLine);
			}
			
		}
	}

	private void sameNamePair(List<RoadNameLine> collectTwoRoad) {
		if(collectTwoRoad == null || collectTwoRoad.size() < 2){
			return;
		}
		Collections.sort(collectTwoRoad, rnlComparator);
		RoadNameLine roadNameLine = collectTwoRoad.get(0);
		for (int i = 1; i < collectTwoRoad.size(); i++) {
			RoadNameLine otherroadNameLine = collectTwoRoad.get(i);
			if(Math.abs(roadNameLine.length - otherroadNameLine.length) < 5000){
				if((PolygonUtil.twoPointDistance(roadNameLine.pointlist.get(0) , otherroadNameLine.pointlist.get(0)) < 700 && PolygonUtil.twoPointDistance(roadNameLine.pointlist.get(roadNameLine.pointlist.size() - 1) , otherroadNameLine.pointlist.get(otherroadNameLine.pointlist.size() - 1))<700) 
						||(PolygonUtil.twoPointDistance(roadNameLine.pointlist.get(0) , otherroadNameLine.pointlist.get(otherroadNameLine.pointlist.size() - 1)) < 700 && PolygonUtil.twoPointDistance(roadNameLine.pointlist.get(roadNameLine.pointlist.size() - 1) , otherroadNameLine.pointlist.get(0))<700)){
					otherroadNameLine.makeName = false;
					if(i + 1 < collectTwoRoad.size()){
						i = i + 1;
						roadNameLine = collectTwoRoad.get(i);
					}
				}else{
					roadNameLine = otherroadNameLine;
				}
			}else{
				roadNameLine = otherroadNameLine;
			}
		}
		
	}

	private List<MarkPoint> makeRoadNameAndRouteNo(List<RoadNameLine> roadnamelines){
		List<MarkPoint> roadmark = new ArrayList<MarkPoint>();
		for (int i = 0; i < roadnamelines.size(); i++) {
			RoadNameLine roadnameline = roadnamelines.get(i);
			if(!roadnameline.makeName){
				continue;
			}
			List<ShpPoint> pointlist = roadnameline.pointlist;
			int[] scaleValues = DataManager.getLevelInfo(taskData.task.getLevel()).scales;
			for (int j = 0; j < scaleValues.length; j++) {
				double needlen = this.getOneNameNeedLength(roadnameline, scaleValues[j]);
				double roadnamelen = this.getOneNameLength(roadnameline, scaleValues[j]);
				if(roadnameline.length < minlen * scaleValues[j]){
					break;
				}
				else if(roadnameline.length<2 * roadnamelen){
					roadmark.addAll(this.makeRoadMark(roadnameline.pointlist, roadnameline, j, roadnameline.length, true));
				}
				else if(roadnameline.length<2*needlen){
					roadmark.addAll(this.makeRoadMark(roadnameline.pointlist, roadnameline, j, roadnameline.length, true));
				}
				else{
					double startdistance = 0;
					List<ShpPoint> roadneeds = new ArrayList<ShpPoint>();
					ShpPoint forwardPoint = pointlist.get(0);
					roadneeds.add(pointlist.get(0));
					for (int k = 1; k < pointlist.size(); k++) {
						double twodis = Math.abs(PolygonUtil.twoPointDistance(forwardPoint,
								pointlist.get(k)));
						startdistance = startdistance + twodis;
						if(startdistance >= needlen){
							double discon = twodis- (startdistance - needlen);
							ShpPoint insertpoint = this.creatInsertPoint(twodis, discon, forwardPoint, pointlist.get(k));
							roadneeds.add(insertpoint);
							roadmark.addAll(this.makeRoadMark(roadneeds, roadnameline, j, needlen, true));
							roadneeds = new ArrayList<ShpPoint>();
							roadneeds.add(insertpoint);
							forwardPoint = insertpoint;
							startdistance = 0;
							k--;
						}else{
							roadneeds.add(pointlist.get(k));
							forwardPoint = pointlist.get(k);
						}
						
					}
				}
			}
		}
		return roadmark;
	}
	
	
	private List<MarkPoint> makeRoadMark(List<ShpPoint> needpoints, RoadNameLine roadNameLine, int scaleNo, double needleng, boolean makeLine){
		List<MarkPoint> landlist = new ArrayList<MarkPoint>();
		int scaleValue = DataManager.getLevelInfo(this.taskData.task.getLevel()).scales[scaleNo];
		if(SaveType.ONLY_MARK.equals(judgeSaveType(this.taskData.task, roadNameLine, scaleNo))){
			if(roadNameLine.route != null && !roadNameLine.route.trim().isEmpty()){
			ShpPoint midlepoint = this.findMidlePoint(needpoints, scaleValue, needleng);
			if(midlepoint==null){
				return landlist;
			}
			List<ShpPoint> points = new ArrayList<ShpPoint>();
			points.add(midlepoint);
			List<MarkPointRect> rects = this.makeRoadNameRect(points, scaleValue, roadNameLine);
			MarkPoint landMarkType = new MarkPoint(roadNameLine.nrc + 11,
					roadNameLine.route, null, midlepoint.x, midlepoint.y, "",rects);
			landMarkType.bvs[scaleNo] = true;
			landMarkType.isRoadName = true;
			landlist.add(landMarkType);
			}
		}else if(SaveType.ONLY_LINE.equals(judgeSaveType(this.taskData.task, roadNameLine, scaleNo)) && makeLine){
			List<ShpPoint> roadnamepoints = this.findAppropriateStation(needpoints, scaleValue, roadNameLine, needleng);
			if(roadnamepoints.isEmpty() ||this.isRoadNameCover(roadnamepoints, roadNameLine, scaleValue)){
				return landlist;
			}
			this.shapePointHandle(roadnamepoints, scaleValue);
			List<MarkPointRect> rects = this.makeRoadNameRect(roadnamepoints, scaleValue, roadNameLine);
			ShpPoint startpoint = roadnamepoints.remove(0);
			MarkPoint landMarkType = new MarkPoint(roadNameLine.nrc, roadNameLine.name, null, startpoint.x, startpoint.y, roadnamepoints,rects);
			landMarkType.bvs[scaleNo] = true;
			landMarkType.isRoadName = true;
			landlist.add(landMarkType);
		}else if(SaveType.BOTH_MARK_LINE.equals(judgeSaveType(this.taskData.task, roadNameLine, scaleNo))){
			List<ShpPoint> roadnamepoints = this.findAppropriateStation(needpoints, scaleValue, roadNameLine, needleng);
			if(roadnamepoints.isEmpty() ||this.isRoadNameCover(roadnamepoints, roadNameLine, scaleValue)){
				return landlist;
			}
			this.shapePointHandle(roadnamepoints, scaleValue);
			List<MarkPointRect> rectsName = this.makeRoadNameRect(roadnamepoints, scaleValue, roadNameLine);
			ShpPoint startpoint = roadnamepoints.remove(0);
			MarkPoint landMarkType = new MarkPoint(roadNameLine.nrc, roadNameLine.name, null, startpoint.x, startpoint.y, roadnamepoints, rectsName);
			landMarkType.bvs[scaleNo] = true;
			landMarkType.isRoadName = true;
			landlist.add(landMarkType);
			if(roadNameLine.route != null && !roadNameLine.route.trim().isEmpty()){
				List<ShpPoint> points = new ArrayList<ShpPoint>();
				points.add(needpoints.get(needpoints.size() - 1));
				List<MarkPointRect> rectsNo = this.makeRoadNameRect(points, scaleValue, roadNameLine);
				MarkPoint signlandMarkType = new MarkPoint(roadNameLine.nrc + 11,
							roadNameLine.route, null, needpoints.get(needpoints.size() - 1).x, needpoints.get(needpoints.size() - 1).y, "", rectsNo);
				signlandMarkType.bvs[scaleNo] = true;
				signlandMarkType.isRoadName = true;
				landlist.add(signlandMarkType);
			}
		}
		return landlist;
	}
	
	private List<ShpPoint> findAppropriateStation(List<ShpPoint> needpoints, int scale, RoadNameLine roadNameLine, double needlenght){
		double startToCurr = 0;
		ShpPoint forward = needpoints.get(0);
		List<ShpPoint> roadname = new ArrayList<ShpPoint>();
		boolean addpoint = false;
		double namelen  = this.getOneNameLength(roadNameLine, scale);
		double jumpLength = (needlenght - namelen) / 2;
		for (int i = 1; i < needpoints.size(); i++) {
			double twodis = PolygonUtil.twoPointDistance(forward, needpoints.get(i));
			startToCurr = startToCurr + twodis;
			if(startToCurr >= jumpLength  && !addpoint){
				ShpPoint point = this.creatInsertPoint(twodis, twodis-startToCurr+jumpLength, forward, needpoints.get(i));
				roadname.add(point);
				addpoint = true;
			}
			if(addpoint && startToCurr < jumpLength + namelen){
				roadname.add(needpoints.get(i));
			}else if(addpoint && startToCurr >= jumpLength + namelen){
				ShpPoint point = this.creatInsertPoint(twodis, twodis-startToCurr+jumpLength+namelen, forward, needpoints.get(i));
				roadname.add(point);
				addpoint = false;
				break;
			}
			forward = needpoints.get(i);
		}
		return roadname;
	}
	
	/**
	 * 寻找路名线中点
	 * @param needpoints
	 * @param scale
	 * @param needlenght
	 * @return
	 */
	private ShpPoint findMidlePoint(List<ShpPoint> needpoints, int scale, double needlenght){
		double startToCurr = 0;
		ShpPoint forward = needpoints.get(0);
		List<ShpPoint> roadname = new ArrayList<ShpPoint>();
		boolean addpoint = false;
		double jumpLength = needlenght / 2;
		ShpPoint point = null;
		for (int i = 1; i < needpoints.size(); i++) {
			double twodis = PolygonUtil.twoPointDistance(forward, needpoints.get(i));
			startToCurr = startToCurr + twodis;
			if(startToCurr >= jumpLength  && !addpoint){
				point = this.creatInsertPoint(twodis, twodis-startToCurr+jumpLength, forward, needpoints.get(i));
				roadname.add(point);
				addpoint = true;
			}
			forward = needpoints.get(i);
		}
		return point;
	}
	
	/**
	 * 制作道路名排版矩形框
	 * @param roadNamePoints
	 * @param scale
	 * @param roadNameLine
	 * @return
	 */
	private List<MarkPointRect> makeRoadNameRect(List<ShpPoint> roadNamePoints, int scale, RoadNameLine roadNameLine){
		List<MarkPointRect> rects = new ArrayList<MarkPointRect>();
		//阀值
		double value = 1;
		ShpPoint point = null;
		ShowStyle style = null;
	
		// 道路番号
		if(roadNamePoints.size() == 1){
			style = this.getRoadStyle(roadNameLine, false);
			double oneFontSize = style.getFont() / 10.0;
			point = roadNamePoints.get(0);
			GeoRect georect = this.makeRect(point, oneFontSize, scale, value);
			MarkPointRect rect = new MarkPointRect(georect, (byte)0);
			rects.add(rect);
		}
		// 道路名
		else{
			style = this.getRoadStyle(roadNameLine, true);
			double oneFontSize = style.getFont() / 10.0;
			// 字间空隙
			double letterSpacing = style.getLetterSpacing() / 10.0;
			if(letterSpacing <  0.9 * oneFontSize || letterSpacing < 0.001){
				return rects;
			}
			ShpPoint forward = roadNamePoints.get(0);
//			GeoRect georect0 = this.makeRect(forward, oneFontSize, scale, value);
//			MarkPointRect rect0 = new MarkPointRect(georect0, (byte)0);
//			rects.add(rect0);
			double remain = letterSpacing * scale;
			for(int i=1;i<roadNamePoints.size();){
				double twodis = PolygonUtil.twoPointDistance(forward, roadNamePoints.get(i));
				if(twodis < remain){
					remain -= twodis;
					forward = roadNamePoints.get(i);
					i++;
					continue;
				}
				point = this.creatInsertPoint(twodis, remain, forward, roadNamePoints.get(i));
				//制作矩形框
				GeoRect georect = this.makeRect(point, oneFontSize, scale, value);
				MarkPointRect rect = new MarkPointRect(georect, (byte)0);
				rects.add(rect);
				remain = letterSpacing * scale;
				forward = point;
			}
//			forward = roadNamePoints.get(roadNamePoints.size()-1);
//			GeoRect georectn = this.makeRect(forward, oneFontSize, scale, value);
//			MarkPointRect rectn = new MarkPointRect(georectn, (byte)0);
//			rects.add(rectn);
		}
		return rects;
	}
	
	/**
	 * 创建文字矩形框，判断道路名是否自压盖
	 * @param roadNamePoints
	 * @param roadNameLine
	 * @param scale
	 * @return
	 */
	private boolean isRoadNameCover(List<ShpPoint> roadNamePoints, RoadNameLine roadNameLine, int scale){
		List<GeoRect> rects = new ArrayList<GeoRect>();
		double startToCurr = 0;
		ShpPoint forward = roadNamePoints.get(0);
		ShpPoint point = null;
		showStyle.setLevel(taskData.task.getLevel());
		showStyle.setlMarkType(roadNameLine.nrc);
		int index = Collections.binarySearch(roadStyleList, showStyle, showStyleComparator);
		if(index<0){
			System.out.println("no style");
			return true;
		}
		ShowStyle style = roadStyleList.get(index);
		double oneFontSize = style.getFont() / 10.0;
		// 字间空隙
		double letterSpacing = style.getLetterSpacing() / 10.0;
		if(letterSpacing <  0.9 * oneFontSize || letterSpacing < 0.001){
			return false;
		}
		double remain = letterSpacing * scale;
		for(int i=1;i<roadNamePoints.size();){
			double twodis = PolygonUtil.twoPointDistance(forward, roadNamePoints.get(i));
			startToCurr += twodis;
			if(twodis < remain){
				remain -= twodis;
				forward = roadNamePoints.get(i);
				i++;
				continue;
			}
			point = this.creatInsertPoint(twodis, remain, forward, roadNamePoints.get(i));
			//制作矩形框
			GeoRect rect = this.makeNameRect(point, oneFontSize, scale);
			rects.add(rect);
			remain = letterSpacing * scale;
			forward = point;
		}
		//比较矩形框是否压盖 压盖返回true
		for(int j=0;j<rects.size()-1;j++){
			GeoRect rect1 = rects.get(j);
			for(int k=j+1;k<rects.size();k++){
				GeoRect rect2 = rects.get(k);
				if(GeoRect.rectInRect(rect1, rect2)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 制作矩形框
	 * @param center
	 * @param oneFontSize
	 * @param scale
	 * @return
	 */
	private GeoRect makeNameRect(ShpPoint center, double oneFontSize, int scale){
		int left =0;
		int bottom = 0;
		int right = 0;
		int top = 0;
		//阀值
		double valve = 0.9;
		left = (int) (center.x - valve * oneFontSize * scale / this.oneLonToMeter / 2 *2560 * 3600);
		right = (int) (center.x + valve * oneFontSize * scale / this.oneLonToMeter / 2 *2560 * 3600);
		bottom = (int) (center.y - valve * oneFontSize * scale / this.oneLatToMeter / 2 *2560 * 3600);
		top = (int) (center.y + valve * oneFontSize * scale / this.oneLatToMeter / 2 *2560 * 3600);
		ShpPoint lb = new ShpPoint(left, bottom);
		ShpPoint rt = new ShpPoint(right, top);
		GeoRect rect = GeoRect.valueOf(lb, rt);
		return rect;
	}
	
	/**
	 * 根据中心点制作一个矩形框
	 * @param center
	 * @param oneFontSize
	 * @param scale
	 * @param valve
	 * @return
	 */
	private GeoRect makeRect(ShpPoint center, double oneFontSize, int scale, double valve){
		int left =0;
		int bottom = 0;
		int right = 0;
		int top = 0;
		
		left = (int) (center.x - valve * oneFontSize * scale / this.oneLonToMeter / 2 *2560 * 3600);
		right = (int) (center.x + valve * oneFontSize * scale / this.oneLonToMeter / 2 *2560 * 3600);
		bottom = (int) (center.y - valve * oneFontSize * scale / this.oneLatToMeter / 2 *2560 * 3600);
		top = (int) (center.y + valve * oneFontSize * scale / this.oneLatToMeter / 2 *2560 * 3600);
		ShpPoint lb = new ShpPoint(left, bottom);
		ShpPoint rt = new ShpPoint(right, top);
		GeoRect rect = GeoRect.valueOf(lb, rt);
		return rect;
	}
	
	/**
	 * 获得道路名和道路番号式样
	 * @param roadNameLine
	 * @param isRoadName 是否是道路名
	 * true：道路名 false：道路番号
	 * @return
	 */
	private ShowStyle getRoadStyle(RoadNameLine roadNameLine, boolean isRoadName){
		showStyle.setLevel(taskData.task.getLevel());
		// 道路名
		if(isRoadName){
			showStyle.setlMarkType(roadNameLine.nrc);
		}
		// 道路番号
		else{
			if(roadNameLine.nrc == defineNRC.HightWay){
				showStyle.setlMarkType(NumberUtil.HightWayNo);
			}
			else if(roadNameLine.nrc == defineNRC.NationalRoad){
				showStyle.setlMarkType(NumberUtil.NationalRoadNo);
			}
			else{
				showStyle.setlMarkType(NumberUtil.ProcinceRoadNo);
			}
		}
		int index = Collections.binarySearch(roadStyleList, showStyle, showStyleComparator);
		return roadStyleList.get(index);
	}
	
//	private ShpPoint getRoute(List<ShpPoint> needpoints, int scale){
//		for (int i = 0; i < needpoints.size(); i++) {
//			
//		}
//	}
 	/**
	 * 一个名字需要的长度
	 * @param name
	 * @param scaleValue
	 * @return
	 */
	private double getOneNameNeedLength(RoadNameLine roadnameline, long scaleValue){
		if (roadnameline.name != null) {
			double needLength = this.getOneNameLength(roadnameline, scaleValue)
			+ this.screenlen * scaleValue;
			return needLength;
		}
		return this.screenlen * scaleValue;
	}
	
	private double getOneNameLength(RoadNameLine roadnameline, long scaleValue){
		if (roadnameline.name != null) {
			showStyle.setLevel(taskData.task.getLevel());
			showStyle.setlMarkType(roadnameline.nrc);
			int index = Collections.binarySearch(roadStyleList, showStyle, showStyleComparator);
			if(index<0){
				System.out.println("no style");
				return 0;
			}
			ShowStyle style = roadStyleList.get(index);
			double oneFontSize = style.getFont() / 10.0;
			double letterSpacing = style.getLetterSpacing() / 10.0;
			double needLength = (oneFontSize + (roadnameline.name.length()-1) * letterSpacing)
			* scaleValue + 2 * oneFontSize * scaleValue;
					
			return needLength;
		}
		return this.screenlen * scaleValue;
	}
 	

	/**
	 * 
	 * @param pointlist
	 */
	private void shapePointHandle(List<ShpPoint> pointlist, int scale) {

		for (int i = pointlist.size() - 2; i > 0; i--) {
			ShpPoint headerPoint = pointlist.get(i - 1);
			ShpPoint midPoint = pointlist.get(i);
			ShpPoint tailPoint = pointlist.get(i + 1);
			// 邻边1
			double disA = PolygonUtil.twoPointDistance(headerPoint, midPoint);
			// 邻边2
			double disB = PolygonUtil.twoPointDistance(midPoint, tailPoint);
			// 平面余玄定理
			// double cosAngle = (disA*disA + disB*disB -
			// disC*disC)/(2*disA*disB);

			double angle = PolygonUtil.sphereAngle(headerPoint, midPoint,
					tailPoint);
			// 根据不同的纬度设置不同的阀值。
			double lonValue = Math
					.cos(midPoint.y / 2560 / 2400 * Math.PI / 180);
			double disAngleThreshold = scale*SmoothStyle.length_1;
			double disThreshold = scale*SmoothStyle.length_2;
			double disThresholdWithLon = disThreshold * lonValue;
			double disAngleThresholdWithLon = disAngleThreshold * lonValue;
			// 删除道路点条件,1角度接近直线 并且 距离相离的两点不是很远
			if (angle > 170
					&& (disA < disAngleThresholdWithLon || disB < disAngleThresholdWithLon)) {
				pointlist.remove(i);
			} else if (disA < disThresholdWithLon && disB < disThresholdWithLon) {
				pointlist.remove(i);
			}
		}

	}
}
