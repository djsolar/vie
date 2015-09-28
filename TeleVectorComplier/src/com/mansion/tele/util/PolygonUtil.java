package com.mansion.tele.util;

import java.util.List;

import com.mansion.tele.db.bean.elemnet.ShpPoint;

/**
 * 面形状的处理工具
 * @author hefeng
 *
 */
public class PolygonUtil {
	private final static double EARTH_RADIUS = 6378137;
	/**
	 * 默认构造器
	 */
	private PolygonUtil() {
		super();
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 球面上的两点之间的弧度
	 */
	public static double sphereAngle(ShpPoint firstPoint, ShpPoint secondPoint){
		// 转单位为度
		double lat1 = firstPoint.y / (double) (3600 * 2560);
		double lng1 = firstPoint.x / (double) (3600 * 2560);

		double lat2 = secondPoint.y / (double) (3600 * 2560);
		double lng2 = secondPoint.x / (double) (3600 * 2560);

		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		
		double radLng1 = rad(lng1);
		double radLng2 = rad(lng2);
		double a = radLat1 - radLat2;
		double b = radLng1 - radLng2;
		double p = Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2)
				   * Math.pow(Math.sin(b / 2), 2)); 
		if(p>1.0){
			p=1.0;
		}
		double result =  2 * Math.asin(p);
//		result = Math.acos(Math.sin(radLat1)*Math.sin(radLat2)+Math.cos(radLat1)*Math.cos(radLat2)*Math.cos(b)) ;
		return result;
	}
	/**
	 * 计算球面上的三点之间的夹角,三角形ABC 角B的度数。
	 * A B C 球面上的坐标点
	 * @param pointA
	 * @param pointB
	 * @param pointC
	 * @return 角B
	 */
	public static double sphereAngle(ShpPoint pointA, ShpPoint pointB,ShpPoint pointC){
		double a = PolygonUtil.sphereAngle(pointA, pointB);
		double b = PolygonUtil.sphereAngle(pointB, pointC);
		double c = PolygonUtil.sphereAngle(pointA, pointC);
		//球面三角形余玄定理
		double cosC = (Math.cos(c)-Math.cos(a)*Math.cos(b))/(Math.sin(a)*Math.sin(b));
		if(cosC > 1){
			return 0.0;
		}
		if(cosC <-1){
			return 180.0;
		}
		double angle =  Math.acos(cosC)/Math.PI*180.0;
		return angle;
	}
	// // return m（米）球面距离
	public static double twoPointDistance(ShpPoint firstPoint, ShpPoint secondPoint) {
		if(firstPoint.equals(secondPoint)){
			return 0;
		}
		// 转单位为度
		double lat1 = firstPoint.y / (double) (3600 * 2560);
		double lng1 = firstPoint.x / (double) (3600 * 2560);

		double lat2 = secondPoint.y / (double) (3600 * 2560);
		double lng2 = secondPoint.x / (double) (3600 * 2560);

		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		
		double radLng1 = rad(lng1);
		double radLng2 = rad(lng2);
		double b = radLng1 - radLng2;

//		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
//				+ Math.cos(radLat1) * Math.cos(radLat2)
//				* Math.pow(Math.sin(b / 2), 2)));
//		s = s * EARTH_RADIUS;
		//一下为球面距离公式，与以上计算结果进行比较，比较结果为误差非常小.
		double a = Math.sin(radLat1)*Math.sin(radLat2)+Math.cos(radLat1)*Math.cos(radLat2)*Math.cos(b);
		//计算存在误差，若a>1则结果为NaN。所以当a>1时舍去小数部分
		if(a>1.0){
			a=1.0;
		}
		double s = EARTH_RADIUS*Math.acos(a) ;
//		// s = Math.round(s * 10000) / 10000;
//		double dis = Math.abs(s - s1)/s;
//		if(dis > 0.05){
//			System.out.println(s);
//			System.out.println(s1);
//			System.out.println("error"+dis*100);
//		}
		return s;
	}
	/**
	 * 计算两点位置坐标北偏东角度
	 * @param pointFrist
	 * @param pointSecond
	 * @return
	 */
	public static double calcAngle(ShpPoint pointFrist,ShpPoint pointSecond){
		ShpPoint point = new ShpPoint(pointFrist.x, pointFrist.y+1000);//虚拟正北点
		double angle = sphereAngle(point,pointFrist, pointSecond);
		
		if(pointFrist.x > pointSecond.x){
			angle = 360 -angle;
		}
		return angle;
	}
	/**
	 * 计算与道路起始点的距离为指定长度的道路坐标点
	 * @param pointList 道路坐标点集合
	 * @param distance 指定长度，与道路集合中第一个坐标点之间的距离
	 * @return
	 */
	public static ShpPoint getPointByDistance(List<ShpPoint> pointList,double distance){
		double length = 0.0;
		for (int index = 0;index < pointList.size()-1; index++) {
			double nextPointDistance = twoPointDistance(pointList.get(index), pointList.get(index+1));
			length += nextPointDistance;
			if(length >= distance){
				double delta = distance - (length-nextPointDistance);
				int x = (int) (delta * (pointList.get(index+1).x - pointList.get(index).x) / nextPointDistance + pointList.get(index).x);
				int y = (int) (delta * (pointList.get(index+1).y - pointList.get(index).y) / nextPointDistance + pointList.get(index).y);
				return new ShpPoint(x, y);
			}
		}
		return pointList.get(pointList.size()-1);
	}
	
	public static void main(String[] args) {
		
		ShpPoint shpPoint1 = new ShpPoint(3600 * 2560 * 0, 3600*2560 * 50);
		ShpPoint shpPoint2 = new ShpPoint(3600 * 2560 * 1, 3600*2560 * 50);
		System.out.println(twoPointDistance(shpPoint1, shpPoint2));
		
	}

}
