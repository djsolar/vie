package com.mansion.tele.db.bean.elemnet;

import java.awt.Point;
import java.io.Serializable;

import com.mansion.tele.common.GeoLocation;

@SuppressWarnings("serial")
public class ShpPoint implements Comparable<ShpPoint>,Serializable{
	public int x;
	public int y;
//	public int z;
	public short z;
	
	
	public static ShpPoint valueOf(GeoLocation location){
		ShpPoint shpPoint = new ShpPoint(location.getiLongitude(),location.getiLatitude());
		return shpPoint;
	}
	public void convert(ShpPoint lbCoordinate) {
		this.x = lbCoordinate.x;
		this.y = lbCoordinate.y;
		
		
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ShpPoint && ((((ShpPoint)obj).x == x) &&((ShpPoint)obj).y == y)) {
		 return true;	
		}
		return false;
	}
	public ShpPoint(){
		
	}
	@Override
	public int hashCode(){
		return x * 36 + y;
		
	}
	public ShpPoint(int x, int y){
		this.x = x;
		this.y = y;
	}
	public ShpPoint(int x, int y, short z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public ShpPoint(String x, String y){
		this.x = Integer.parseInt(x);
		this.y = Integer.parseInt(y);
	}
	public ShpPoint(Point p){
		this.x = p.x;
		this.y = p.y;
	}
	public ShpPoint(String string, String string2, String string3) {
		this.x = Integer.parseInt(string);
		this.y = Integer.parseInt(string2);
		this.z = (short)Integer.parseInt(string3);
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public int compareTo(ShpPoint o) {
		if (o == null) {
			return -1;
		}else {
			if (this.x>o.x) {
				return 1;
			}else if(this.x==o.x){
				if(this.y>o.y){
					return 1;
					
				}else if (this.y<o.y) {
					return -1;
				}
				else {
					return 0;
				}
				
			}else {
				return -1;
			}
		}
	}
	public short getZ() {
		return z;
	}
	public void setZ(short z) {
		this.z = z;
	}
	@Override
	public String toString() {
		return  x + ";" + y + ";" + z ;
	}
	
	public static ShpPoint valueOf(ShpPoint point){
		return new ShpPoint(point.x, point.y);
	}
//	public boolean near(ShpPoint point){
//		if(Math.abs(point.x - this.x) < 2 && Math.abs(point.y - this.y) < 2){
//			return true;
//		}
//		return false;
//	}
}