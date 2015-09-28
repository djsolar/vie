package com.mansion.tele.business.network;

import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.business.DataManager;
import com.mansion.tele.business.DataManager.LevelInfo;
import com.mansion.tele.business.TaskData;
import com.mansion.tele.business.background.Edge;
import com.mansion.tele.business.background.Polygon;
import com.mansion.tele.business.network.RoadNew.defineFOW;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
import com.mansion.tele.util.PolygonUtil;
/**
 * 平滑处理
 * @author zhangj
 *
 */
public class SmoothHandle {
	TaskData taskData;
	//与角度合用的距离阀值
	double disAngleThreshold;
	//距离阀值
	double disThreshold;
	
	public SmoothHandle(TaskData taskData) {
		this.taskData = taskData;
		initThreshold();//取得阀值
	} 
	/**
	 * 根据不同的层号设置不同的阀,不同道路Nrc阀值不同
	 * 1.角度判断 高速，国道角度单独处理 //
	 */
	void initThreshold(){
		byte level = taskData.task.getLevel();
		LevelInfo levelAndScale = DataManager.getLevelInfo(level);

		disAngleThreshold = levelAndScale.minScale*SmoothStyle.length_1;
		disThreshold = levelAndScale.minScale*SmoothStyle.length_2;
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
	 * 1，删除接近直线上的中间点
	 * 2，删除距离相邻点近的点
	 */
	public void deletRoadPoint(){
		if(taskData.task.getLevel() == 0){
			return ;
		}
		
		for (RoadNew road : taskData.network.roadList) {
			//形状点少于3个的不考虑
			if(road.coordinate.size()<3){
				continue;
			}
			
			if(isMeandering(road)){//过滤本来弯曲的道路
				continue;
			}
			//计算中间点的角度,以及到两个相邻的的距离
			for (int i = road.coordinate.size() - 2; i > 0;i--) {
				ShpPoint headerPoint = road.coordinate.get(i - 1);
				ShpPoint midPoint = road.coordinate.get(i);
				ShpPoint tailPoint = road.coordinate.get(i + 1);
				// 邻边1 
				double disA = PolygonUtil.twoPointDistance(headerPoint, midPoint);
				// 邻边2
				double disB = PolygonUtil.twoPointDistance(midPoint, tailPoint);
				//平面余玄定理
//				double cosAngle = (disA*disA + disB*disB - disC*disC)/(2*disA*disB);
			
				double angle =  PolygonUtil.sphereAngle(headerPoint, midPoint, tailPoint);
				//根据不同的纬度设置不同的阀值。
				double lonValue = Math.cos(midPoint.y/2560/2400*Math.PI/180);
				double disThresholdWithLon = disThreshold*lonValue;
				double disAngleThresholdWithLon = disAngleThreshold*lonValue;
				// 删除道路点条件,1角度接近直线 并且 距离相离的两点不是很远
				if (angle > SmoothStyle.angle && (disA < disAngleThresholdWithLon || disB < disAngleThresholdWithLon)) {
					road.coordinate.remove(i);
				}else if(disA<disThresholdWithLon && disB < disThresholdWithLon){
					road.coordinate.remove(i);
				}
			}
		}
	}
	/**
	 * 1，删除接近直线上的中间点
	 * 2，删除距离相邻点近的点
	 */
	public void deletBackGroundPoint(){
		for (Polygon polygon : taskData.background.polygongs) {
//			if(polygon.type != Type.ProvinceAdmArea && polygon.type != Type.SeaArea){
//				continue;
//			}
			List<ShpPoint> coordinate = new ArrayList<ShpPoint>();
			for (Edge edge : polygon.edges) {
				coordinate.addAll(edge.coordinate);
			}
			//计算中间点的角度,以及到两个相邻的的距离
			for (int i = coordinate.size() - 2; i > 0;i--) {
				ShpPoint headerPoint = coordinate.get(i - 1);
				ShpPoint midPoint = coordinate.get(i);
				ShpPoint tailPoint = coordinate.get(i + 1);
				// 邻边1 
				double disA = PolygonUtil.twoPointDistance(headerPoint, midPoint);
				// 邻边2
				double disB = PolygonUtil.twoPointDistance(midPoint, tailPoint);
				//平面余玄定理
//				double cosAngle = (disA*disA + disB*disB - disC*disC)/(2*disA*disB);
			
				double angle =  PolygonUtil.sphereAngle(headerPoint, midPoint, tailPoint);
				//根据不同的纬度设置不同的阀值。
				double lonValue = Math.cos(midPoint.y/2560/2400*Math.PI/180);
				double disThresholdWithLon = disThreshold*lonValue;
				double disAngleThresholdWithLon = disAngleThreshold*lonValue;
				// 删除道路点条件,1角度接近直线 并且 距离相离的两点不是很远
				if (angle > SmoothStyle.angle && (disA < disAngleThresholdWithLon || disB < disAngleThresholdWithLon)) {
					coordinate.remove(i);
				}else if(disA<disThresholdWithLon && disB < disThresholdWithLon){
					coordinate.remove(i);
				}
			}
			polygon.edges.clear();
			Edge edge = new Edge();
			polygon.edges.add(edge);
			edge.coordinate = coordinate;
		}
	}
	public static void main(String[] args) {
		double angleq = Math.acos(-0.99619469809174553229501040247389) / Math.PI * 180;
		System.out.println(angleq);
		ShpPoint pointA = new ShpPoint(1137426211, 385704000);
		ShpPoint pointB = new ShpPoint(1137425082, 385716894);
		ShpPoint pointC = new ShpPoint(1137152752, 385066164);

		// 邻边1
		double disA = PolygonUtil.twoPointDistance(pointA, pointB);
		// 邻边2
		double disB = PolygonUtil.twoPointDistance(pointB, pointC);
		// 对边
		double disC = PolygonUtil.twoPointDistance(pointA, pointC);
		double angle = (disA * disA + disB * disB - disC * disC) / (2 * disA * disB);
		double r = Math.acos(angle) / Math.PI * 180;
		System.out.println(angle);
		System.out.println(r);
	}
}