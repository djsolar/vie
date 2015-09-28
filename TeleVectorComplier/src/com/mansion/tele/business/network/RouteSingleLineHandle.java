package com.mansion.tele.business.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mansion.tele.business.network.RoadNew.defineFOW;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
import com.mansion.tele.util.PolygonUtil;

/**
 * 该类用于对路网的单线化处理
 * @author zhangj
 *
 */
public class RouteSingleLineHandle {
	Network network;
	public RouteSingleLineHandle(Network network) {
		this.network = network;
	}
	
	//道路的单线化处理
	//1.建立route
	/**
	 * 道路的单线化处理
	 */
	public void singleLine() {
		for (RoadNew road : network.roadList) {
			road.routed = false;
		}
		//创建Route
		List<RouteForSingleLine> routes = createRoutes();
		//属性赋值
		for (RouteForSingleLine routeForSingleLine : routes) {
			//左下
			routeForSingleLine.lb = routeForSingleLine.roads.get(0).road.lb;
			//右上
			routeForSingleLine.rt = routeForSingleLine.roads.get(0).road.rt;
			//长度
			routeForSingleLine.length = routeForSingleLine.roads.get(0).road.length;
			//名称
			routeForSingleLine.name = routeForSingleLine.roads.get(0).road.roadName;
			//头点
			routeForSingleLine.headPoint = routeForSingleLine.roads.get(0).road.coordinate.get(0);
			if(!routeForSingleLine.roads.get(0).dir){
				routeForSingleLine.headPoint = routeForSingleLine.roads.get(0).road.coordinate.get(routeForSingleLine.roads.get(0).road.coordinate.size()-1);
			}
			//尾点
			routeForSingleLine.tailPoint = routeForSingleLine.roads.get(routeForSingleLine.roads.size()-1).road.coordinate.get(routeForSingleLine.roads.get(routeForSingleLine.roads.size()-1).road.coordinate.size()-1);
			if(!routeForSingleLine.roads.get(routeForSingleLine.roads.size()-1).dir){
				routeForSingleLine.tailPoint = routeForSingleLine.roads.get(routeForSingleLine.roads.size()-1).road.coordinate.get(0);
			}
			//左下右上点
			for(int i = 1; i< routeForSingleLine.roads.size()-1;i++){
				ShpPoint lb = routeForSingleLine.roads.get(i).road.lb;
				ShpPoint rt = routeForSingleLine.roads.get(i).road.lb;
				if(routeForSingleLine.lb.x > lb.x){
					routeForSingleLine.lb.x = lb.x;
				}
				if(routeForSingleLine.rt.x < rt.x){
					routeForSingleLine.rt.x = rt.x;
				}
				if(routeForSingleLine.lb.y > lb.y){
					routeForSingleLine.lb.y = lb.y;
				}
				if(routeForSingleLine.rt.y < rt.y){
					routeForSingleLine.rt.y = rt.y;
				}
				routeForSingleLine.length += routeForSingleLine.roads.get(i).road.length;
				
			}
			if(routeForSingleLine.headPoint.equals(routeForSingleLine.tailPoint)){
//				System.out.println("match 环岛 .....");
				routeForSingleLine.circleFlag = true;
			}
		}
		
		//名称排序
		Collections.sort(routes, new Comparator<RouteForSingleLine>() {
			@Override
			public int compare(RouteForSingleLine o1, RouteForSingleLine o2) {
				return o1.name.compareTo(o2.name);
			}
		});
		
//		TODO路线匹配 fow== 2
		/**
		 * 配对原则
		 * 1，名称相同
		 * 2，范围近似
		 * 3，首尾、尾首接近
		 * 4，长度接近
		 * 5，
		 *
		 */
		for(int i=0;i < routes.size()-2;i++){
			RouteForSingleLine routeForSingleLineA = routes.get(i);
//			if(routeForSingleLineA.name.length() == 0){
//				continue;
//			}
			for(int j= i+1;j<routes.size()-1;j++){
				RouteForSingleLine routeForSingleLineB = routes.get(j);
				//名称相同
				if(routeForSingleLineA.name.equals(routeForSingleLineB.name)){
					//开始配对
					if(routeForSingleLineA.matchRoute != null){
						break;
					}
					if(routeForSingleLineB.matchRoute != null){
						continue;
					}
					//范围比较
					if(PolygonUtil.twoPointDistance(routeForSingleLineA.lb, routeForSingleLineB.lb) > 5000 || PolygonUtil.twoPointDistance(routeForSingleLineA.rt, routeForSingleLineB.rt) > 5000){
						continue;
					}
					//长度比较
					if(Math.abs(routeForSingleLineA.length - routeForSingleLineB.length) > 5000){
						continue;
					}
					//环路类型
					if(routeForSingleLineA.circleFlag == true && routeForSingleLineB.circleFlag == true){
						routeForSingleLineA.matchRoute = routeForSingleLineB;
						routeForSingleLineB.matchRoute = routeForSingleLineA;
					}else
					//头尾比较
					if((PolygonUtil.twoPointDistance(routeForSingleLineA.headPoint , routeForSingleLineB.headPoint) < 500 && PolygonUtil.twoPointDistance(routeForSingleLineA.tailPoint , routeForSingleLineB.tailPoint)<500) 
							||(PolygonUtil.twoPointDistance(routeForSingleLineA.headPoint , routeForSingleLineB.tailPoint) < 500 && PolygonUtil.twoPointDistance(routeForSingleLineA.tailPoint , routeForSingleLineB.headPoint)<500)){
						routeForSingleLineA.matchRoute = routeForSingleLineB;
						routeForSingleLineB.matchRoute = routeForSingleLineA;
					}
				}else{
					break;
				}
			}
//			System.out.println(routeForSingleLineA.name+"  :   "+routeForSingleLineB.name);
		}
//		设置删除标记
		for (RouteForSingleLine routeForSingleLine : routes) {
			routeForSingleLine.deleFlagRoute();
		}
//		清理标记为删除的道路
		network.clearRoadList();
		network.clearNodeList();
		network.clearIntersectionList();
	}
	List<RouteForSingleLine> createRoutes(){
		List<RouteForSingleLine> routeList = new ArrayList<RouteForSingleLine>();
		 //遍历所有道路//TODO fow=2 创建Route
		 for (RoadNew seedRoad : network.roadList) {
			 if(seedRoad.routed == true || seedRoad.fow != defineFOW.TwoWay){
				 continue;
			 }
			 
			 RouteForSingleLine route = new RouteForSingleLine();
			 //添加种子道路
			 DirRoad nextDirRoad = new DirRoad(seedRoad, true);
			 route.roads.add(nextDirRoad);
			 nextDirRoad.road.routed = true;

			 //反序链接	
			 for(int i = 0; i <5000; i++){
				 if(nextDirRoad.dir == true){
					 nextDirRoad = route.nextRoadReverse();
				 }else{
					 nextDirRoad = route.nextRoadForward();
				 }
				 if(nextDirRoad == null){
					 break;
				 }
				 route.roads.add(nextDirRoad);
				 
				 nextDirRoad.road.routed = true;
			 }

			 //调转顺序
			 Collections.reverse(route.roads);
			 
			 nextDirRoad=route.roads.get(route.roads.size() - 1);
			//正序链接
			 for(int i = 0; i <5000; i++){
				 if(nextDirRoad.dir == true){
					 nextDirRoad = route.nextRoadForward();
				 }else{
					 nextDirRoad = route.nextRoadReverse();
				 }
				 if(nextDirRoad == null){
					 break;
				 }
				 route.roads.add(nextDirRoad);
				 nextDirRoad.road.routed = true;
			 }
			 //route 初始化 添加属性值
			 routeList.add(route);
		 }
		 return routeList;
	}
}
