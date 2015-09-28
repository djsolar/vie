package com.mansion.tele.business.network;

public class DirRoad {
	//路网中的道路
	public RoadNew road;
	//道路的方向
	public boolean dir;

	public DirRoad() {
	}

	// roads中道路的方向：true：顺方向
	public DirRoad(RoadNew road, boolean dir) {
		this.road = road;
		this.dir = dir;
	}
}
