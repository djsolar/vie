package com.sunmap.teleview.view.assist.style;


import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;


/**
 * 形状管理类
 * @author lijingru
 *
 */
public class ShapeManage {

	/**
	 * 十字形状
	 * @param 十字左上角坐标
	 * @param    单位像素
	 * @return
	 */
	public static Shape crossShape(Point2D point ,int i) {
		int x = (int) (point.getX()-5); 
		int y = (int) (point.getY()-5);
		int[] arg0 = new int[]{(int) (x+0.4*i) , (int) (x+0.6*i) ,(int) (x+0.6*i) ,x+1*i
				,x+1*i,(int) (x+0.6*i),(int) (x+0.6*i), (int) (x+0.4*i)
				,(int) (x+0.4*i),x  ,x  , (int) (x+0.4*i)};
		int[] arg1 = new int[]{y   , y   ,(int) (y+0.4*i) ,(int) (y+0.4*i)
				,(int) (y+0.6*i) ,(int) (y+0.6*i),y+1*i,y+1*i
				,(int) (y+0.6*i),(int) (y+0.6*i),(int) (y+0.4*i), (int) (y+0.4*i)};
		Shape s = new Polygon(arg0, arg1, 12);
		return s;
	}
	
	/**
	 * 三角形
	 * @param point  三角形中心点坐标
	 * @return
	 */
	public static Shape triangleShape(Point2D point,int line) {
		double x1 = point.getX()+0.1*line;
		double y1 = point.getY() - line / Math.sqrt(3) +0.2*line;
		double x2 = point.getX() - line / 2 +0.1*line;
		double y2 = point.getY() + line / 2 / Math.sqrt(3) +0.2*line;
		double x3 = point.getX() + line / 2 +0.1*line;
		double y3 = point.getY() + line / 2 / Math.sqrt(3) +0.2*line;
		int[] arg0 = new int[]{(int)(x1 + 0.5),(int)(x2 + 0.5),(int)(x3 + 0.5)};
		int[] arg1 = new int[]{(int)(y1 + 0.5),(int)(y2 + 0.5),(int)(y3 + 0.5),};

		Shape s = new Polygon(arg0, arg1, 3);
		return s;
	}

	/**
	 * 箭头
	 * @param point	头点坐标
	 * @param theta  正北夹角
	 * @param dc 边长
	 * @return
	 */
	public static Shape arrowShape(Point2D point, double theta,int dc) {
		theta = theta-Math.PI/2;
		double ac = Math.PI/8;
		double x = point.getX();
		double y = point.getY();
//		P3：  x3=x1-3cos(a+30)    y3=y1-3sin(a+30)
//		P2：  x2=x1-3cos(a-30)    y2=y1-3sin(a-30)
		GeneralPath path = new GeneralPath();
		double x1 = x-dc*Math.cos(theta+ac);
		double y1 = y-dc*Math.sin(theta+ac);
		double x2 = x-dc*Math.cos(theta-ac);
		double y2 = y-dc*Math.sin(theta-ac);
		path.moveTo(x1, y1 );
		path.lineTo(x, y);
		path.lineTo(x2, y2);
		return path;
		
	}
	
	/**
	 * 刻度尺
	 * @param 十字左上角坐标
	 * @param    单位像素
	 * @return
	 */
	public static Shape rule(Point2D point ,double pix_cm) {
		double x  = point.getX();
		double y = point.getY();
		GeneralPath path = new GeneralPath();
		path.moveTo(x,y);
		path.lineTo(x, y+pix_cm/5);
		path.lineTo(x+pix_cm, y+pix_cm/5);
		path.lineTo(x+pix_cm, y);
		return path;
	}
	
	
}
