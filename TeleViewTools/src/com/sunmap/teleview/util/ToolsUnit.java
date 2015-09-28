/**
 * 
 */
package com.sunmap.teleview.util;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sunmap.teleview.util.Tuple.TwoTuple;



/**
 * @author lijingru
 *	工具 常量与 共同方法
 */
public class ToolsUnit {
	
	public static final double lons[] =new double[91];
	public static final double pi =Math.PI;
	private static final long basicLat = 354964700;//纬度基准点。用于计算经纬度之间的长度比例。
	
	static{
		for(int i =0 ;i<=90;i++){
			lons[i]=(111190/3600)*Math.cos(i*pi/180);
		}
	}
	
	 /**
     * 将年月日  时分秒格式时间转化为毫秒时间 （注：年月日 十分秒的时间格式为yyyyMMddHHmmss)
     * @param time
     * @return
     */
    public static long getTimeInMillis(String time){
    	 Calendar c = Calendar.getInstance();
    	  try{
    		  c.setTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(time));
    	  }catch(Exception e){
    		  e.printStackTrace();
    	  }
    	  return c.getTimeInMillis();
    }
    
    /**
     * 将毫秒数换算成x天x时x分x秒x毫秒  	
     * @param ms
     * @return  yyyyMMddHHmmss
     */
    public static String format(long ms) {    
	    SimpleDateFormat simpday = new SimpleDateFormat("yyyyMMddHHmmss");
	    Date date = new Date(ms);
	    String str = simpday.format(date);
	    
	    return str;
    }
    /**
     * 将毫秒数换算成x天x时x分x秒x毫秒  	
     * @param ms
     * @return  yyyyMMddHHmmss
     */
    public static java.sql.Date forms(long ms) {    
    	java.sql.Date date = new java.sql.Date(ms);
	    
	    return date;
    }
    
    /**
     * 将年月日  时分秒格式时间转化为Date时间 （注：年月日 十分秒的时间格式为yyyyMMddHHmmss)
     * @param time
     * @return
     */
    public static java.sql.Date forms(String time){
    	if (time.equalsIgnoreCase("")||time.equalsIgnoreCase("000000")) {
    		  return  null;
		}else{
			Calendar c = Calendar.getInstance();
			try{
				c.setTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(time));
			}catch(Exception e){
			}
			long ms = c.getTimeInMillis();
			return forms(ms);
		}
    }
    
    /**
	 * 检验 正整数不能以0开头
	 * 
	 * @param str
	 * @return true:是正整数
	 */
	public static boolean isNumber(String str) {
		String reg = "[1-9]+\\d*|0";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 检验字符串长度
	 * 
	 * @param str 字符串
	 * @param minlen 最小长度
	 * @param maxlen 最大长度
	 * @return
	 */
	public static boolean checkoutStringSize(String str, int minlen, int maxlen) {
		return str.length() >= minlen && str.length() <= maxlen;
	}
	
	
	public static double calcDistanceOnEarth(Point p1, Point p2){
		return calcDistanceOnEarth(p1.x, p1.y, p2.x, p2.y);
		
	}
	/**
	 * 计算两点的球面距离（单位：米）
	 * @param x1 单位（1/2560秒）
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double calcDistanceOnEarth(int x1, int y1, int x2, int y2){
		double startX = x1 / 3600.0 / 2560;
		double startY = y1 / 3600.0 / 2560;
		double endX = x2 / 3600.0 / 2560;
		double endY = y2 / 3600.0 / 2560;
		double radY1 = rad(startY);
		double radY2 = rad(endY);
		double resultY = radY1 - radY2;
		double resultX = rad(startX) - rad(endX);
		double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(resultY / 2.0), 2) + Math.cos(radY1) * Math.cos(radY2)
				* Math.pow(Math.sin(resultX / 2.0), 2)));
		distance = distance * 6378137.0;
		return distance;
	}
	/**
	 * 计算缺省纬度上，经度方向一秒代表的距离（单位：米）。使用于地图描画
	 * @return
	 */
	public static double getMeterPerLon() {
		int index = (int) (Math.abs(Math.round(basicLat / 2560 / 3600)));
		if (index < 0) {
			index = 0;
		}
		if (index >= lons.length) {
			index = lons.length - 1;
		}
		return lons[index];
	}
	
	public static TwoTuple<Point, Boolean> point_to_line_apeakPoint2(Point staPoint, Point endPoint, Point point) {
		Point2D staPoint2D = new Point2D.Float(staPoint.x, staPoint.y);
		Point2D endPoint2D = new Point2D.Float(endPoint.x, endPoint.y);
		Point2D point2D = new Point2D.Float(point.x, point.y);
		TwoTuple<Point2D, Boolean> tuple = point_to_line_apeakPoint2(staPoint2D, endPoint2D, point2D);
		Point firstPoint = new Point((int)tuple.first.getX(), (int)tuple.first.getY());
		return new TwoTuple<Point, Boolean>(firstPoint, tuple.second);
	
	}
	/**
	 * 计算点到线段的垂足坐标
	 * 
	 * @param staPoint
	 * @param endPoint
	 * @param point
	 * @return Point:垂足坐标；Boolean：垂足类型：true：在线段上，false：在延长线上
	 */
	public static TwoTuple<Point2D, Boolean> point_to_line_apeakPoint2(Point2D staPoint, Point2D endPoint, Point2D point) {
		int x ,y ;
		boolean apeakType = true;
		if (staPoint.getX() == endPoint.getX() && staPoint.getY() == endPoint.getY()) {
			apeakType = false;
			x = (int) staPoint.getX();
			y = (int) staPoint.getY();
		} else if (staPoint.getX() == endPoint.getX()) {
			x = (int) staPoint.getX();
			y = (int) point.getY();
		} else if (staPoint.getY() == endPoint.getY()) {
			x = (int) point.getX();
			y = (int) staPoint.getY();
		} else {
			double dX = endPoint.getX() - staPoint.getX();
			double dY = endPoint.getY() - staPoint.getY();
			double dMathK = dY / dX;
			double dNegiK = -1 / dMathK;
			double b = staPoint.getY() - dMathK * staPoint.getX();
			double c = point.getY() - dNegiK * point.getX();
			double xx = (c - b) / (dMathK - dNegiK);
			double yy = dMathK * xx + b;
			x = (int) xx;
			y = (int) yy;
		}

		// 如果垂足在延长线上，取线上最近点
		if (staPoint.getX() < endPoint.getX()) {
			if (x > endPoint.getX()) {
				apeakType = false;
				x = (int) endPoint.getX();
				y = (int) endPoint.getY();
			}
			if (x < staPoint.getX()) {
				apeakType = false;
				x = (int) staPoint.getX();
				y = (int) staPoint.getY();
			}
		}
		if (staPoint.getX() > endPoint.getX()) {
			if (x < endPoint.getX()) {
				apeakType = false;
				x = (int) endPoint.getX();
				y = (int) endPoint.getY();
			}
			if (x > staPoint.getX()) {
				apeakType = false;
				x = (int) staPoint.getX();
				y = (int) staPoint.getY();
			}
		}

		if (staPoint.getY() < endPoint.getY()) {
			if (y > endPoint.getY()) {
				apeakType = false;
				x = (int) endPoint.getX();
				y = (int) endPoint.getY();
			}
			if (y < staPoint.getY()) {
				apeakType = false;
				x = (int) staPoint.getX();
				y = (int) staPoint.getY();
			}
		}
		if (staPoint.getY() > endPoint.getY()) {
			if (y < endPoint.getY()) {
				apeakType = false;
				x = (int) endPoint.getX();
				y = (int) endPoint.getY();
			}
			if (y > staPoint.getY()) {
				apeakType = false;
				x = (int) staPoint.getX();
				y = (int) staPoint.getY();
			}
		}
		Point2D apeakPointF =  new Point2D.Float(x, y);
		return Tuple.tuple(apeakPointF, apeakType);
	}

	/**
	 * 2个点的距离
	 * 
	 * @param staPoint
	 * @param endPoint
	 * @return
	 */
	public static double calcTwoPointDistance(Point2D staPoint, Point2D endPoint) {
		return Math.sqrt((endPoint.getX() - staPoint.getX()) * (endPoint.getX() - staPoint.getX()) + (endPoint.getY() - staPoint.getY())
				* (endPoint.getY() - staPoint.getY()));
	}

	/**
	 * 获取点到路径的垂距
	 * @param listPoint 路径  单位：像素
	 * @param point  像素点
	 * @param flag  是否延长线去掉    flase ： 不去掉 ，延迟线上的垂足也包含，true：去掉
	 * @return
	 */
	public static double getApeak(List<Point2D> listPoint, Point2D.Float point,boolean flag) {
		double apeakdis = Integer.MAX_VALUE;
		for (int i = 0; i < listPoint.size()-1; i++) {
			Point2D startPoint = listPoint.get(i);
			Point2D endPoint = listPoint.get(i+1);
			TwoTuple<Point2D, Boolean> apeak = point_to_line_apeakPoint2(startPoint, endPoint, point);
			boolean b ;
			if (flag == false) {
				b = apeak.first != null ;
			}else {
				b = apeak.first != null && apeak.second == true;
			}
			if (b) {
				double distance =  calcTwoPointDistance(point, apeak.first);
				if (apeakdis > distance) {
					apeakdis = distance;
				}
			}
		}
		return apeakdis;
	}
	/**
	 * 获取指定位的值
	 * @param attr
	 * @param startbit
	 * @param endbit
	 * @return
	 */
	public static int getBits(int attr,int startbit,int endbit){
		attr = attr << (31 - endbit);
		attr = attr >>> (startbit + (31 - endbit));
		return attr;
	}
	/**
	 * 获取指定位的值
	 * @param attr
	 * @param startbit
	 * @param endbit
	 * @return
	 */
	public static short getBits(short attr,short startbit,short endbit){
		attr = (short) (attr << (15 - endbit));
		attr = (short) (attr >>> (startbit + (15 - endbit)));
		return attr;
	}
	
	
	/**
	 * 角度转换为弧度
	 * @param value
	 * @return
	 */
	public static double rad(double value){
		return value * Math.PI / 180.0;
	}
	
	public static Point2D betweenPoint(Point2D p1,Point2D p2){
		double x1 = p1.getX();
		double y1 = p1.getY();
		double x2 = p2.getX();
		double y2 = p2.getY();
//		中间点坐标x=(x1+x2)/2
//		          y=(y1+y2)/2
		double xsum = x1+x2;
		double ysum = y1+y2;
		double x = xsum/2;
		double y = ysum/2;
		Point2D.Double between = new Point2D.Double(x, y);
		return between;
		
	}
	
	/**
	 * 计算五分之一的距离坐标
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static Point2D oneTenthPoint(Point2D p1,Point2D p2){
		double x1 = p1.getX();
		double y1 = p1.getY();
		double x2 = p2.getX();
		double y2 = p2.getY();
		double x = x1 - (x1 - x2) / 10;
		double y = y1 - (y1 - y2) / 10;
		Point2D.Double between = new Point2D.Double(x, y);
		return between;
		
	}
	
	
	
	/**
	 * 通过两点计算相对于正北方向的弧度,顺时针
	 * @param x1:1/2560秒
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return [0,2PI)
	 */
	public static float calcAngel(int x1, int y1, int x2, int y2){
		double startX = x1 / 3600.0 / 2560;
		double startY = y1 / 3600.0 / 2560;
		double endX = x2 / 3600.0 / 2560;
		double endY = y2 / 3600.0 / 2560;
		return calcAngel(startX, startY, endX, endY);
	}
	public static float calcAngel(double x1, double y1, double x2, double y2){
		float angel = 0;
		double Ec = Rj + (Rc - Rj) * (90.0 - y1) / 90;
		double Ed = Ec * Math.cos(y1 * Math.PI / 180);
		float y = (float) ((y2 - y1) * Ec);
		float x = (float) ((x2 - x1) * Ed);
		angel = (float)( Math.PI * 0.5 - Math.atan2(y, x));
		if(angel >= Math.PI * 2){
			angel = (float) (angel - Math.PI * 2);
		}else if(angel < 0){
			angel = (float) (angel + Math.PI * 2);
		}
		return angel;
//		a=arctan((y2-y1)/(x2-x1))
	}
	//赤道半径
	public static final double Rc = 6378137; 
	//极半径
	public static final double Rj = 6356725;
	
	
	/**
	 *  该函数主要是用作距离数据舍去
	 * @param ulDist 距离
	 * @return
	 */
	public static String FormatDist(int ulDist) {
		String strdist = null;
		int ulDistanceResult = 0;
		float fDistanceResult = (float) 0.0;
		if (ulDist < 10) {
			ulDistanceResult = 10;
			strdist = ulDistanceResult + "米";
		} else if (ulDist < 100) {
			ulDistanceResult = ulDist / 10 * 10;
			strdist = ulDistanceResult + "米";
		} else if (ulDist < 300) {
			ulDistanceResult = ulDist / 20 * 20;
			strdist = ulDistanceResult + "米";
		} else if (ulDist < 500) {
			ulDistanceResult = ulDist / 50 * 50;
			strdist = ulDistanceResult + "米";
		} else if (ulDist < 1000) {
			ulDistanceResult = ulDist / 100 * 100;
			strdist = ulDistanceResult + "米";
		} else if (ulDist < 10000) {
			/* [CN] 避免显示出1.0km、2.0km、9.0km等[CN] */
			if (ulDist / 100 * 100 % 1000 == 0) {
				ulDistanceResult = ulDist / 1000;
				strdist = ulDistanceResult + "公里";
			} else {
				// ulDist = (ulDist ) /100 * 100;
				/* [CN]sprintf函数会自动四舍五入[CN] */
				fDistanceResult = (float) ulDist / (float) 1000;
				BigDecimal org = new BigDecimal(fDistanceResult);
				org = org.setScale(1, BigDecimal.ROUND_DOWN);
				fDistanceResult = org.floatValue();
				if (fDistanceResult * 10 % 10 == 0) {
					strdist = (int) fDistanceResult + "公里";
				} else {
					strdist = fDistanceResult + "公里";
				}
			}
		} else {
			ulDistanceResult = ulDist / 1000;
			if (ulDistanceResult > 9999) {
				ulDistanceResult = 9999;
			}
			strdist = ulDistanceResult + "公里";
		}
		return strdist;
	}

	/**
	 * 判断点是否在中国范围内
	 * @param x	经纬度 单位 1/2560秒
	 * @param y
	 * @return
	 */
	public static boolean isInChina(int x, int y) {
		if (x < 672768000 || x > 1253376000 || y < 27648000 || y > 497664000) {
			return false;
		}
		return true;
	}
	
}
