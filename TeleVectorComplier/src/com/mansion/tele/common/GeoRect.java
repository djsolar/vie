package com.mansion.tele.common;

import java.io.Serializable;

import com.mansion.tele.business.Task;
import com.mansion.tele.db.bean.elemnet.ShpPoint;

/**
 * 经纬度矩形范围
 * 
 * @author yangz
 * 
 */
@SuppressWarnings("serial")
public class GeoRect implements Serializable, Cloneable {

	/** 矩形关系 相同位置 */
	public static final int RECT_RELATION_EQUAL = 0;
	/** 矩形关系 左侧 */
	public static final int RECT_RELATION_LEFT_IN_RIGHT = 1;
	/** 矩形关系 右侧 */
	public static final int RECT_RELATION_RIGHT_IN_LEFT = 2;
	/** 矩形关系 相交 */
	public static final int RECT_RELATION_CROSS = 3;
	/** 矩形关系 错开 */
	public static final int RECT_RELATION_DIFFRENT = 4;
	public int left,bottom,right,top;

	@Override
	public int hashCode() {
		return left + bottom;
	}

	@Override
	public boolean equals(Object obj) {
		GeoRect other = (GeoRect)obj;
		if(this.left == other.left && this.bottom == other.bottom
				&& this.right == other.right && this.top == other.top){
			return true;
		}
		return false;
	}

	/**
	 * 根据左下右上坐标获得矩形对象
	 * @param lb 左下
	 * @param rt 右上
	 * @return GeoRect 矩形对象
	 */
	public static GeoRect valueOf(GeoLocation lb, GeoLocation rt) {
		GeoRect rect = new GeoRect();
		rect.left = lb.getiLongitude();
		rect.bottom = lb.getiLatitude();
		rect.right = rt.getiLongitude();
		rect.top = rt.getiLatitude();
		return rect;
	}

	/**
	 * 根据左下右上坐标获得矩形对象
	 * @param lb 左下
	 * @param rt 右上
	 * @return GeoRect 矩形对象
	 */
	public static GeoRect valueOf(ShpPoint lb, ShpPoint rt) {
		GeoRect rect = new GeoRect();
		rect.left = lb.x;
		rect.bottom = lb.y;
		rect.right = rt.x;
		rect.top = rt.y;
		return rect;
	}

	/**
	 * 判断两个矩形是否相交
	 * @param geoRec1 矩形1
	 * @param geoRec2 矩形2
	 * @return true:相交。边重叠不认为是相交
	 */
	public static boolean rectInRect(GeoRect geoRec1, GeoRect geoRec2) {
		if (geoRec1.left >= geoRec2.right || geoRec1.bottom >= geoRec2.top
				|| geoRec1.right <= geoRec2.left || geoRec1.top <= geoRec2.bottom) {

			return false;
		}

		return true;
	}

	/**
	 * 重写clone
	 * @return GeoRect
	 */
	public GeoRect clone() {
		try {
			return (GeoRect) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	// add by zhangjin
	//if the point on the edge of rect return true
	public static boolean isLineCrossRect(float x1, float y1, float x2, float y2,
			float minX, float minY, float maxX, float maxY) {
		// Completely outside.
		if ((x1 < minX && x2 < minX) || (y1 < minY && y2 < minY)
				|| (x1 > maxX && x2 > maxX) || (y1 > maxY && y2 > maxY))
			return false;
		// one point inside
		if ((x1 >= minX && x1 <= maxX && y1 >= minY && y1 <= maxY)
				|| (x2 >= minX && x2 <= maxX && y2 >= minY && y2 <= maxY)) {
			return true;
		}

		float m = (y2 - y1) / (x2 - x1);

		float y = m * (minX - x1) + y1;
		if (y >= minY && y <= maxY)
			return true;

		y = m * (maxX - x1) + y1;
		if (y >= minY && y <= maxY)
			return true;

		float x = (minY - y1) / m + x1;
		if (x >= minX && x <= maxX)
			return true;

		x = (maxY - y1) / m + x1;
		if (x > minX && x < maxX)
			return true;

		return false;
	}
	
	/**
	 * 获得线段左右穿过矩形时的
	 * 交点Y（X已知）
	 * @param point1
	 * @param point2
	 * @param x
	 * @return
	 */
	public static int getIntersectionY(ShpPoint point1,ShpPoint point2,int x){
		double x1 = point1.x;
		double y1 = point1.y;
		double x2 = point2.x;
		double y2 = point2.y;
		int y = (int) Math.round((y2 - y1) * (x - x1) / (x2 - x1) + y1);
		return y;
	}
	
	/**
	 * 获得线段上下穿过矩形时的
	 * 交点X（Y已知）
	 * @param point1
	 * @param point2
	 * @param y
	 * @return
	 */
	public static int getIntersectionX(ShpPoint point1,ShpPoint point2,int y){
		double x1 = point1.x;
		double y1 = point1.y;
		double x2 = point2.x;
		double y2 = point2.y;
		int x = (int) Math.round((x2 - x1) * (y - y1)/ (y2 - y1) + x1);
		return x;
	}
	
	/**
	 * 判断点是否在任务块内
	 * @param point
	 * @param task
	 * @return
	 */
	public static int pointTaskRelation(ShpPoint point,Task task){
		if(point == null || task == null){
			return 5;
		}
		if(point.x < task.getLeft()){ // 点在任务块左边
			return 1;
		}
		if(point.x > task.getRight()){ // 点在任务块右边
			return 2;
		}
		if(point.y < task.getBottom()){ // 点在任务块下边
			return 3;
		}
		if(point.y > task.getTop()){ // 点在任务块上边
			return 4;
		}
		return 0;
	}
	
	public static void main(String[] args) {
//		boolean b = isLineCrossRect(0, 0, 100, 199, 100, 100, 200, 200);
//		System.out.println(b);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "左:" +left + "下：" + bottom + "右：" + right + "上：" + top; 
	}
}
