package com.sunmap.been;

public class Point {

	public long x;
	public long y;
	
	public Point(){
		
	}
	
	public Point(long x, long y){
		this.x = x;
		this.y = y;
	}
	public Point(Point p){
		this.x = p.x;
		this.y = p.y;
	}
}
