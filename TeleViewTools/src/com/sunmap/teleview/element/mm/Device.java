package com.sunmap.teleview.element.mm;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

import com.sunmap.teleview.element.mm.data.MMBaseInfo;
import com.sunmap.teleview.element.view.data.ViewBaseInfo;
import com.sunmap.teleview.util.GeoRect;

public class Device {
	public static double p2x;	//一个2560分之一秒代表的像素个数
	public static double p2y; //一个2560分之一秒代表的像素个数
	public static int widthPixels;
	public static int heightPixels;
	
	public static void init(){
		//获取屏幕分辨率
		double resolution_Inch  = Toolkit.getDefaultToolkit().getScreenResolution()/ 2.54f;
		p2x = 100 * 2560 * 3600 / resolution_Inch / 111111;
		p2y = 100 * 2560 * 3600 / resolution_Inch / 111111;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		widthPixels = screenSize.width;//窗口的像素宽度
		heightPixels = screenSize.height;//窗口的像素高度
	}
	
	/**
	 * 取得坐标矩形
	 * @return
	 */
	public static GeoRect getGeoRect(){
		float st1ScreenX = 0;
		float st1ScreenY = heightPixels;
		Point2D.Float st1Screen = new Point2D.Float(st1ScreenX, st1ScreenY);
		float st2ScreenX = widthPixels;
		float st2ScreenY = 0;
		Point2D.Float st2Screen = new Point2D.Float(st2ScreenX, st2ScreenY);
		Point rectA = PixToGeo(st1Screen);//像素转经纬度
		Point rectB = PixToGeo(st2Screen);
		// 截取改矩形框的全部block
		GeoRect rectIn = new GeoRect(rectA.x, rectB.y, rectB.x, rectA.y);
		return rectIn;
	}
	
	/**
	 *  像素转经纬度
	 * @param geo   中心点坐标 单位1/2560秒  如果传入 0.0  采用默认中心点经纬度
	 * @param point	屏幕中想要的点   （单位：像素）
	 * @param scale 
	 * @param centerPoint 
	 * @return
	 */
	public static Point PixToGeo(Point2D.Float point) {
		int x = (int) (ViewBaseInfo.centerPoint.x + (point.x - widthPixels/2) * p2x * ViewBaseInfo.curMapScale / 100 + 0.5);
		int y = (int) (ViewBaseInfo.centerPoint.y - (point.y - heightPixels/2) * p2y * ViewBaseInfo.curMapScale / 100 + 0.5);
		return new Point(x, y);
	}
	
	/**
	 * 经纬度转像素
	 * @param point	经纬度点（单位：1/2560秒）
	 * @return
	 */
	public static Point2D.Double geoToPix(Point point) {
		double stscreenX = (point.x - MMBaseInfo.centerPoint.x) / (p2x * MMBaseInfo.curMapScale / 100) + widthPixels/2;
		double stscreenY = (MMBaseInfo.centerPoint.y - point.y) / (p2y * MMBaseInfo.curMapScale / 100) + heightPixels/2;
		Point2D.Double point2d = new Point2D.Double(stscreenX, stscreenY);
		return point2d;

	}

}
