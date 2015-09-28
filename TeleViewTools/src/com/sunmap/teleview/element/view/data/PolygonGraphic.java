package com.sunmap.teleview.element.view.data;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.DataInputStream;
import java.io.IOException;

import com.sunmap.teleview.element.view.Device;



public class PolygonGraphic implements Graphic{

	public short[] pointxs;
	public short[] pointys;
	
	public void read(DataInputStream dis,short flag) {
		try {
			switch(flag){
			case 1:
				read1(dis);
				break;
			case 2:
				read2(dis);
				break;
			case 3:
				read3(dis);
				break;
			case 4:
				read4(dis);
				break;
			case 5:
				read5(dis);
				break;
			case 6:
				read6(dis);
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * 无边框＋无PenUp情报＋无分割情报
	 * @throws IOException 
	 */
	private void read1(DataInputStream dis) throws IOException{
		int pointCount = dis.readUnsignedByte();
		pointxs = new short[pointCount];
		pointys = new short[pointCount];
		for(int i = 0; i < pointCount; i++){
			pointxs[i] = (short) dis.readUnsignedByte();
			pointys[i] = (short) dis.readUnsignedByte();
		}		
	}
	/**
	 * 有边框＋无PenUp情报＋无分割情报
	 * @throws IOException 
	 */
	private void read2(DataInputStream dis) throws IOException{
		int pointCount = dis.readUnsignedByte();
		pointxs = new short[pointCount];
		pointys = new short[pointCount];
		for(int i = 0; i < pointCount; i++){
			pointxs[i] = (short) dis.readUnsignedByte();
			pointys[i] = (short) dis.readUnsignedByte();
		}		
	}
	/**
	 * 有边框＋有PenUp情报＋无分割情报
	 * @throws IOException 
	 */
	private void read3(DataInputStream dis) throws IOException{
		int pointCount = dis.readUnsignedByte();
		pointxs = new short[pointCount];
		pointys = new short[pointCount];
		for(int i = 0; i < pointCount; i++){
			pointxs[i] = (short) dis.readUnsignedByte();
			pointys[i] = (short) dis.readUnsignedByte();
		}		
	}
	/**
	 * 无边框＋无PenUp情报＋有分割情报
	 * @throws IOException 
	 */
	private void read4(DataInputStream dis) throws IOException{
		int pointCount = dis.readUnsignedByte();
		pointxs = new short[pointCount];
		pointys = new short[pointCount];
		for(int i = 0; i < pointCount; i++){
			pointxs[i] = (short) dis.readUnsignedByte();
			pointys[i] = (short) dis.readUnsignedByte();
		}		
	}
	/**
	 * 有边框＋无PenUp情报＋有分割情报
	 * @throws IOException 
	 */
	private void read5(DataInputStream dis) throws IOException{
		int pointCount = dis.readUnsignedByte();
		pointxs = new short[pointCount];
		pointys = new short[pointCount];
		for(int i = 0; i < pointCount; i++){
			pointxs[i] = (short) dis.readUnsignedByte();
			pointys[i] = (short) dis.readUnsignedByte();
		}		
	}
	/**
	 * 有边框＋有PenUp情报＋有分割情报
	 * @throws IOException 
	 */
	private void read6(DataInputStream dis) throws IOException{
		int pointCount = dis.readUnsignedByte();
		pointxs = new short[pointCount];
		pointys = new short[pointCount];
		for(int i = 0; i < pointCount; i++){
			pointxs[i] = (short) dis.readUnsignedByte();
			pointys[i] = (short) dis.readUnsignedByte();
		}		
	}
	
	/**
	 * 制作路径
	 * @param path 原path
	 * @param blockID 所在block
	 * @return
	 */
	public void makePath(GeneralPath path, BlockID blockID) {
		for (int j = 0; j < pointxs.length; j++) {
			Point  geoPoint = blockID.unitToGeo(pointxs[j], pointys[j]);
			Point2D point = Device.geoToPix(geoPoint);
			if (j == 0) {
				path.moveTo(point.getX(), point.getY());
			} else {
				path.lineTo(point.getX(), point.getY());
			}
		}
	}
	
	/**
	 * 点是否在Polygon形状内
	 * 
	 * @param p 点
	 * @param blockID
	 * @return
	 */
	public boolean isPointinPolygon(Point p, BlockID blockID) {
		boolean isInside = false;
		double ESP = 1e-9;
		int count = 0;
		double linePoint1x;
		double linePoint1y;
		double linePoint2x = 0;
		double linePoint2y;
		linePoint1x = p.x;
		linePoint1y = p.y;
		linePoint2y = p.y;
		for (int i = 0; i < pointxs.length - 1; i++) {
			Point  geoPointA = blockID.unitToGeo(pointxs[i], pointys[i]);
			Point2D pA = Device.geoToPix(geoPointA);
			Point  geoPointB = blockID.unitToGeo(pointxs[i + 1], pointys[i + 1]);
			Point2D pB = Device.geoToPix(geoPointB);
			double cx1 = pA.getX();
			double cy1 = pA.getY();
			double cx2 = pB.getX();
			double cy2 = pB.getY();
			if (isPointOnLine(p.x, p.y, cx1, cy1, cx2, cy2)) {
				return true;
			}
			if (Math.abs(pB.getY() - pA.getY()) < ESP) {
				continue;
			}
			if (cx1 < 0 || cx2 < 0) {
				if (cx1 < cx2) {
					linePoint2x = cx1;
				}else {
					linePoint2x = cx2;
				}
			}
			if (isPointOnLine(cx1, cy1, linePoint1x, linePoint1y, linePoint2x,
					linePoint2y)) {
				if (cy1 > cy2)
					count++;
			} else if (isPointOnLine(cx2, cy2, linePoint1x, linePoint1y,
					linePoint2x, linePoint2y)) {
				if (cy2 > cy1)
					count++;
			} else
				if (isIntersect(cx1, cy1, cx2, cy2, linePoint1x,
					linePoint1y, linePoint2x, linePoint2y)) {
				count++;
			}
		}
		if (count % 2 == 1) {
			isInside = true;
		}
		return isInside;
	}

	// 点是否在线上
	private boolean isPointOnLine(double px0, double py0, double px1,
			double py1, double px2, double py2) {
		boolean flag = false;
		double ESP = 1e-9;
		if ((Math.abs((px1 - px0) * (py2 - py0) - (px2 - px0) * (py1 - py0)) < ESP)
				&& ((px0 - px1) * (px0 - px2) <= 0)
				&& ((py0 - py1) * (py0 - py2) <= 0)) {
			flag = true;
		}
		return flag;
	}

	// 是否相交
	private boolean isIntersect(double px1, double py1, double px2, double py2,
			double px3, double py3, double px4, double py4) {
		boolean flag = false;
		double d = (px2 - px1) * (py4 - py3) - (py2 - py1) * (px4 - px3);
		if (d != 0) {
			double r = ((py1 - py3) * (px4 - px3) - (px1 - px3) * (py4 - py3))
					/ d;
			double s = ((py1 - py3) * (px2 - px1) - (px1 - px3) * (py2 - py1))
					/ d;
			if ((r >= 0) && (r <= 1) && (s >= 0) && (s <= 1)) {
				flag = true;
			}
		}
		
		return flag;
	}
		
}

