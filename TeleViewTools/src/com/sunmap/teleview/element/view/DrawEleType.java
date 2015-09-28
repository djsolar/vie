package com.sunmap.teleview.element.view;

public class DrawEleType {
	public static boolean isDrawRegionLine = false;
	public static boolean isDrawBg = false;
	public static boolean isDrawRoad = false;
	public static boolean isDrawLandMark = false;
	public static boolean isDrawRoadTextLine;
	public static boolean isDrawBlockEdg;
	
	public static boolean pickRegionLine;
	public static boolean pickBg;
	public static boolean pickRoad;
	public static boolean pickLandMark;
	public static boolean pickRoadTextLine;
	
	
	@SuppressWarnings("static-access")
	public void setDrawViewEleType(DrawEleType drawViewEleType){
		this.isDrawRegionLine = drawViewEleType.isDrawRegionLine;
		this.isDrawBg = drawViewEleType.isDrawBg;
		this.isDrawRoad = drawViewEleType.isDrawRoad;
		this.isDrawLandMark = drawViewEleType.isDrawLandMark;
		this.isDrawRoadTextLine = drawViewEleType.isDrawRoadTextLine;
		this.isDrawBlockEdg = drawViewEleType.isDrawBlockEdg;
		this.pickRegionLine = drawViewEleType.pickRegionLine;
		this.pickBg = drawViewEleType.pickBg;
		this.pickRoad = drawViewEleType.pickRoad;
		this.pickLandMark = drawViewEleType.pickLandMark;
		this.pickRoadTextLine = drawViewEleType.pickRoadTextLine;
	}
}
