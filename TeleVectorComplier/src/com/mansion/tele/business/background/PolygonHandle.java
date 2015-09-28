package com.mansion.tele.business.background;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.mansion.tele.db.bean.elemnet.ShpPoint;
/**
 * 凹变凸工具类
 * @author zhangj
 *
 */
public class PolygonHandle {
	/**
	 * 判断是否为凸边形
	 * 
	 * @param list 逆时针顺序
	 * @return true 是 false 否
	 */
	public static boolean isConvexR(List<ShpPoint> list) {
		if(list.size() < 3){
			return false;
		}
		if(list.size() == 3){
			return true;
		}
		for (int i = 1; i < list.size() - 2; i++) {
			if(pitPoint(list.get(i-1),list.get(i),list.get(i+1))){
				//凹点
//				System.out.println(i);
				return false;
			}
		}
		if(pitPoint(list.get(list.size()-1),list.get(0),list.get(1))){
			//凹点
//			System.out.println(0);
			return false;
		}
		if(pitPoint(list.get(list.size()-2),list.get(list.size()-1),list.get(0))){
			//凹点
//			System.out.println(list.size()-1);
			return false;
		}
		return true;
	}
	/**
	 * 判断是否为凸边形
	 * 
	 * @param list 逆时针顺序
	 * @return true 是 false 否
	 */
	public static boolean isConvex(List<Point> list) {
		if(list.size() < 3){
			return false;
		}
		if(list.size() == 3){
			return true;
		}
		for (int i = 1; i < list.size() - 2; i++) {
			if(pitPoint(list.get(i-1),list.get(i),list.get(i+1))){
				//凹点
//				System.out.println(i);
				return false;
			}
		}
		if(pitPoint(list.get(list.size()-1),list.get(0),list.get(1))){
			//凹点
//			System.out.println(0);
			return false;
		}
		if(pitPoint(list.get(list.size()-2),list.get(list.size()-1),list.get(0))){
			//凹点
//			System.out.println(list.size()-1);
			return false;
		}
		return true;
	}
	
	/**
	 * 判断哪个点是凹点
	 * @param list
	 * @return
	 */
	public static int returnConvex(List<ShpPoint> list) {
		if(list.size() < 3){
			return -1;
		}
		if(list.size() == 3){
			return -1;
		}
		for (int i = 1; i < list.size() - 1; i++) {
			if(testpitPoint(list.get(i-1),list.get(i),list.get(i+1))){
				//凹点
//				System.out.println(i);
				return i;
			}
		}
		if(testpitPoint(list.get(list.size()-2),list.get(0),list.get(1))){
			//凹点
//			System.out.println(0);
			return 0;
		}
		return -1;
	}
	//验证b点是否为凹点
	public static boolean pitPoint(Point a,Point b,Point p){
		return (b.x-a.x)*(p.y-a.y)-(b.y-a.y)*(p.x-a.x)<0;
	}
	//验证b点是否为凹点
	public static boolean pitPoint(ShpPoint a,ShpPoint b,ShpPoint p){
		return (b.x-a.x)*(p.y-a.y)-(b.y-a.y)*(p.x-a.x)<0;
	}
	//验证b点是否为凹点
		public static boolean testpitPoint(ShpPoint a,ShpPoint b,ShpPoint p){
			return (b.x-a.x)*(p.y-a.y)-(b.y-a.y)*(p.x-a.x)>0;
		}
	//凹变凸
	public static List<List<Integer>> converToConvex(PointList pts,Graphics g){
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		DecompPoly relation = null;
		if (!pts.isEmpty()) {
			relation = compute(pts);//计算
		}
		if(relation != null){
			relation.draw(g);//整理关系
			Node[] nodes = relation.getRelations();
			//测试关系
//			for (Node node : nodes) {
//				System.out.println(node.index);
//				for (Node node1 : node.nodes) {
//					System.out.print(node1.index+"    ");
//				}
//				System.out.println();
//				System.out.println("-----------------------");
//			}
			//整理关系
			for(int index = 0; index < nodes.length; ){
				Node startNode = nodes[index];
				while(startNode.nodes.size() > 0){
					int nextNodeIndex = startNode.getNextNode(startNode).index;
					int startNodeIndex = index;
//					System.out.println("========="+startNodeIndex+"====="+nextNodeIndex+"===");
					List<Integer> polygon = new ArrayList<Integer>();
					polygon.add(startNodeIndex);
					polygon.add(nextNodeIndex);
					while(index != nextNodeIndex){
						int newNodeindexId = nodes[nextNodeIndex].getNextNode(nodes[startNodeIndex]).index;
						startNodeIndex = nextNodeIndex;
						nextNodeIndex = newNodeindexId;
//						System.out.println(newNodeindexId);
						polygon.add(newNodeindexId);
					}
					result.add(polygon);
				}
				index++;
			}
		}
		return result;
	}
	/**
	 * 收集面图形的坐标点
	 * @param polygon
	 * @return
	 */
	public static List<Point>  getPolygonPoints(Polygon polygon){
		//收集面坐标集合
		List<Point> points = new ArrayList<Point>();
		for (Edge edge : polygon.edges) {
			for (int i = 0; i< edge.coordinate.size();i++) {
				ShpPoint spoint = edge.coordinate.get(i);
				Point point = new Point(spoint.x,spoint.y);
				if(points.isEmpty()){
					points.add(point);
					continue;
				}
				else {
					// 相邻两点相同
					if (points.get(points.size() - 1).equals(point)) {
						continue;
					}
					// 共线||X共线||Y共线
					if (points.size() >= 2 && ((points.get(points.size() - 1).x() == point.x() && point.x() == points.get(points.size() - 2).x())
							|| ((points.get(points.size() - 1).y() == point.y() && point.y() == points.get(points.size() - 2).y())))) {
						points.set(points.size() - 1, point);
						continue;
					}
				}
				points.add(point);
			}//首尾点相同
			if(points.get(0).equals(points.get(points.size()-1))){
				points.remove(points.size()-1);
			}
		}
		return points;
	}
	//测试代码
	public static List<List<Integer>> convex(List<Point> list){
		DecompPoly relation = null;
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		PointList pts = new PointList(list);//赋值
		if (!pts.isEmpty()) {
			if (!pts.ccw()) {//逆时针方向调整
				pts.reverse();
			}
			relation = compute(pts);//计算
		}
		if(relation != null){
			relation.draw(null);//整理关系
			Node[] nodes = relation.getRelations();
		}
		return result;
	}
	/** Assumes that the point list contains at least three points */
	public static DecompPoly compute(PointList pl) {
		int i, k, n = pl.number();
		DecompPoly dp = new DecompPoly(pl);
		dp.init();

		for (int l = 3; l < n; l++) {
			for (i = dp.reflexIter(); i + l < n; i = dp.reflexNext(i))
				if (dp.visible(i, k = i + l)) {
					dp.initPairs(i, k);
					if (dp.reflex(k))
						for (int j = i + 1; j < k; j++)
							dp.typeA(i, j, k);
					else {
						for (int j = dp.reflexIter(i + 1); j < k - 1; j = dp
								.reflexNext(j))
							dp.typeA(i, j, k);
						dp.typeA(i, k - 1, k); // do this, reflex or not.
					}
				}

			for (k = dp.reflexIter(l); k < n; k = dp.reflexNext(k))
				if ((!dp.reflex(i = k - l)) && dp.visible(i, k)) {
					dp.initPairs(i, k);
					dp.typeB(i, i + 1, k); // do this, reflex or not.
					for (int j = dp.reflexIter(i + 2); j < k; j = dp
							.reflexNext(j))
						dp.typeB(i, j, k);
				}
		}
		dp.guard = 3 * n;
		dp.recoverSolution(0, n - 1);
		return dp;
	}
}
