package com.sunmap.common;

import com.sunmap.been.Point;
import com.sunmap.been.Polygon;

public class JudgeRelation {

	/**
	 * 拿出矩形的各顶点分别判断是否在城市形状内，
	 * 只要有一个顶点在就说明相交，或者矩形的顶点
	 * 不在多边形内，但多边形有顶点在矩形内
	 * @param block block矩形
	 * @param polygon 城市多边形
	 * @return block矩形是否和城市形状相交
	 * true：相交	false：不相交
	 */
	public boolean relation(Polygon block, Polygon polygon){
		Point left_bottom = new Point(block.left, block.bottom);
		Point right_bottom = new Point(block.right, block.bottom);
		Point left_top = new Point(block.left, block.top);
		Point right_top = new Point(block.right, block.top);
		if(isPointInPolygon(left_bottom, polygon, block)){
			return true;
		}
		if(isPointInPolygon(right_bottom, polygon, block)){
			return true;
		}
		if(isPointInPolygon(left_top, polygon, block)){
			return true;
		}
		if(isPointInPolygon(right_top, polygon, block)){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断点P是否在多边形内，以点P为端点向左做水平射线，
	 * 若与多边形有奇数个交点，则P在多边形内。
	   算法：
	 count ← 0;
                以P为端点，作从右向左的射线L; 
     for 多边形的每条边s
      do if P在边s上 
           then return true;
         if s不是水平的
          then if s的一个端点在L上
                 if 该端点是s两端点中纵坐标较大的端点
                   then count ← count+1
                else if s和L相交
                 then count ← count+1;
     if count mod 2 = 1 
       then return true;
     else return false;
	 * @param p block顶点
	 * @param polygon 城市多边形
	 * @param block block矩形
	 * @return 点是否在多边形内，true：在	false：不在
	 */
	public boolean isPointInPolygon(Point p, Polygon polygon, Polygon block){ 
		int count = 0;
		// 射线PP1
		Point p1 = new Point(0, p.y);
		// 多边形边AB
		Point a = polygon.points.get(0);
		Point b = null;
		for(int i = 1; i<polygon.points.size(); i++){
			// 当多边形有点在矩形内时，肯定相交
			if(isPointInRect(a, block)){
				return true;
			}
			b = polygon.points.get(i);
			if(isPointOnRay(p, a, b)){
				return true;
			}
			if(a.y != b.y){
				if(a.y == p.y && a.x <= p.x){
					if(a.y > b.y){
						count ++;
					}
				}
				else if(b.y == p.y && b.x <= p.x){
					if(b.y > a.y){
						count ++;
					}
				}
				else if(isSegmentCrossSegment(p, p1, a, b)){
					count ++;
				}
			}
			
			a = b;
		}
		if(count % 2 == 1){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断点p是否在线段ab上，依据为：
	 * 向量pa×向量ab=0且p在以ab为对角线的矩形内
	 * @param p
	 * @param a
	 * @param b
	 * @return 点是否在线段上，true：在	false：不在
	 */
	public boolean isPointOnRay(Point p, Point a, Point b){
		long product = (p.x - a.x) * (b.y - a.y) - (p.y - a.y) * (b.x - a.x);
		if(product == 0 && Math.min(a.x, b.x) <= p.x && Math.max(a.x, b.y) >= p.x
				&& Math.min(a.y, b.y) <= p.y && Math.max(a.y, b.y) >= p.y){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断线段和线段是否相交
	 * @param p
	 * @param p1
	 * @param a
	 * @param b
	 * @return true：相交 false：不相交
	 */
	public boolean isSegmentCrossSegment(Point p, Point p1, Point a, Point b){
		if(quickRejection(p, p1, a, b) && across(p, p1, a, b)){
			return true;
		}
		return false;
	}
	
	/**
	 * 快速排斥试验，判断以pp1为对角线的矩形
	 * 是否与以ab为对角线的矩形相交（此处pp1为射线，
	 * p1坐标较小，只需判断ab）
	 * @param p
	 * @param p1
	 * @param a
	 * @param b
	 * @return
	 */
	public boolean quickRejection(Point p, Point p1, Point a, Point b){
		if(p.x > Math.min(a.x, b.x) && p.y > Math.min(a.y, b.y)
				&& p.y < Math.max(a.y, b.y)){
			return true;
		}
		return false;
	} 
	
	/**
	 * 跨立试验，利用向量的符号分别判断
	 * a、b是否在pp1两侧和p、p1是否在ab两侧
	 * @param p
	 * @param p1
	 * @param a
	 * @param b
	 * @return
	 */
	public boolean across(Point p, Point p1, Point a, Point b){
		// (p1-a) × (b-a)
		long v1 = (p1.x-a.x)*(b.y-a.y) - (p1.y-a.y)*(b.x-a.x);
		// (b-a) × (p-a)
		long v2 = (b.x-a.x)*(p.y-a.y) - (b.y-a.y)*(p.x-a.x);
		// (a-p1) × (p-p1)
		long v3 = (a.x-p1.x)*(p.y-p1.y) - (a.y-p1.y)*(p.x-p1.x);
		// (p-p1) × (b-p1)
		long v4 = (p.x-p1.x)*(b.y-p1.y) - (p.y-p1.y)*(b.x-p1.x);
		if(multiply(v1, v2) && multiply(v3, v4)){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断乘法的符号
	 * @param arg1
	 * @param arg2
	 * @return true：大于0 false：小于0
	 */
	public boolean multiply(long arg1, long arg2){
		if(arg1>0 && arg2>0){
			return true;
		}
		if(arg1<0 && arg2<0){
			return true;
		}
		if(arg1==0 || arg2==0){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断点是否在矩形中
	 * @param p
	 * @param rect
	 * @return true:在 false：不在
	 */
	public boolean isPointInRect(Point p, Polygon rect){
		if(p.x >= rect.left && p.x <= rect.right
				&& p.y >= rect.bottom && p.y <= rect.top){
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
//		JudgeRelation jr = new JudgeRelation();
//		Point a = new Point(0,0);
//		Point p = new Point(5, 5);
//		Point b = new Point(20, 20);
//		System.out.println(j.isPointOnRay(p, a, b));
//		Polygon block = new Polygon();
//		Polygon polygon = new Polygon();
//		Point left_bottom = new Point(100, 100);
//		Point right_bottom = new Point(200, 100);
//		Point left_top = new Point(100, 200);
//		Point right_top = new Point(200, 200);
//		block.left_bottom = left_bottom;
//		block.left_top = left_top;
//		block.right_bottom = right_bottom;
//		block.right_top = right_top;
//		polygon.points.add(new Point(50, 50));
//		polygon.points.add(new Point(50, 250));
//		polygon.points.add(new Point(250, 250));
//		polygon.points.add(new Point(250, 50));
//		polygon.points.add(new Point(50, 50));
//		System.out.println(jr.relation(block, polygon));
	}
}
