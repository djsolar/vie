package com.mansion.tele.business.network;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.mansion.tele.business.network.RoadNew.defineFOW;
import com.mansion.tele.db.bean.elemnet.ShpPoint;

public class RouteForSingleLine{
	List<DirRoad> roads = new ArrayList<DirRoad>();
	boolean delFlag;
	RouteForSingleLine matchRoute;
	int length;
	String name = "";
	ShpPoint rt;
	ShpPoint lb;
	ShpPoint headPoint;
	ShpPoint tailPoint;
	boolean circleFlag;//环形标志

	/**
	 * 删除原则
	 * 1，垂直距离小于可视范围
	 * 2，删除那一边
	 */
	
	class StyleRecord{
		//要删除的道路
		int fow;
		int length;
		int nrc;
		
		int dis;//双线化道路间的距离
	}
	/**
	 * 配对原则
	 * 1，名称相同
	 * 2，范围近似
	 * 3，首尾、尾首接近
	 * 4，长度接近
	 * 5，
	 *
	 */
	public static Comparator<RouteForSingleLine> comparator= new Comparator<RouteForSingleLine>() {		 
		@Override
		public int compare(RouteForSingleLine o1, RouteForSingleLine o2) {
			RoadNew road1 = o1.roads.get(0).road;
			RoadNew road2 = o2.roads.get(0).road;
			if(road1.nrc!=road2.nrc){
				return road1.nrc-road2.nrc;
			}
			if(o1.length != o2.length){
				return o1.length - o2.length;
			}
			return 0;
		}
	};
//	public int getRouteLength(){
//		if(this.length != 0){
//			return length;
//		}
//		for(DirRoad road:roads){
//			length += road.road.length;
//		}
//		return length;
//	}
	void deleFlagRoute() {
		if (this.roads.isEmpty() || this.matchRoute == null) {
			return;
		}
		int fow = this.roads.get(0).road.fow;
		// 单线化
		if (fow == defineFOW.TwoWay) {
			if (headPoint.y <= tailPoint.y || headPoint.x <= tailPoint.x) {
				this.matchRoute.matchRoute = null;
				for (int i = 1; i < this.roads.size() - 1; i++) {
					RoadNew road = this.roads.get(i).road;
					road.delFlag = true;
				}
				// 开始于环岛
				RoadNew startRoad = this.roads.get(0).road;
				NodeNew startNode = startRoad.startNode;
				if (!this.roads.get(0).dir) {
					startNode = startRoad.endNode;
				}
				boolean joinRoundAbout = false;
				for (RoadNew road : startNode.roads) {
					if (road.fow == defineFOW.RoundAbout) {
						joinRoundAbout = true;
						break;
					}
				}
				if (!joinRoundAbout) {
					startRoad.delFlag = true;
				}
				// 终止于环岛
				RoadNew endRoad = this.roads.get(roads.size() - 1).road;
				NodeNew endNode = endRoad.endNode;
				if (!this.roads.get(roads.size() - 1).dir) {
					endNode = endRoad.startNode;
				}
				joinRoundAbout = false;
				for (RoadNew road : endNode.roads) {
					if (road.fow == defineFOW.RoundAbout) {
						joinRoundAbout = true;
						break;
					}
				}
				if (!joinRoundAbout) {
					endRoad.delFlag = true;
				}
			}
		}
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
				if (RoadNew.isEqualSingleLine(outRoad, inRoad)) {
					inRoads.add(inRoad);
				}
			}
		} else {// 收集可以连接的道路
			for (RoadNew inRoad : outRoad.startNode.getInRoads()) {
				if (outRoad.equals(inRoad) || inRoad.routed) {
					continue;
				}
				if (RoadNew.isEqualSingleLine(outRoad, inRoad)) {
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
				if (RoadNew.isEqualSingleLine(inRoad, outRoad)) {
					outRoads.add(outRoad);
				}
			}
		} else {// 收集可以连接的道路
			for (RoadNew outRoad : inRoad.endNode.getOutRoads()) {
				if (inRoad.equals(outRoad) || outRoad.routed) {
					continue;
				}
				if (RoadNew.isEqualSingleLine(inRoad, outRoad)) {
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
}

