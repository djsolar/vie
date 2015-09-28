package com.sunmap.teleview.element.view.data;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.DataInputStream;
import java.io.IOException;

import com.sunmap.teleview.element.view.Device;
public class PolylineGraphic implements Graphic{

	public float[] pointxs;
	public float[] pointys;
	public short attr;//线的属性，比如是否是桥梁、计划道路等，和layerNo配合使用
	
	public void read(DataInputStream dis,short flag) {
		try {
			int pointCount = dis.readUnsignedByte();
			attr = dis.readByte();
			pointxs = new float[pointCount];
			pointys = new float[pointCount];
			for(int i = 0; i < pointCount; i++){
				pointxs[i] =  dis.readUnsignedByte();
				pointys[i] =  dis.readUnsignedByte();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 制作路径
	 * @param path 原path
	 * @param blockID 所在block
	 * @return
	 */
	public void makePath(GeneralPath path, BlockID blockID,short priority) {
		for (int j = 0; j < pointxs.length; j++) {
			Point  geoPointB = blockID.unitToGeo(pointxs[j], pointys[j]);
			Point2D point = Device.geoToPix(geoPointB);
			if (j == 0) {
				path.moveTo(point.getX(), point.getY());
			} else {
				path.lineTo(point.getX(), point.getY());
			}
		}
	}

}
