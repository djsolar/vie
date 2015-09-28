package com.sunmap.teleview.element.mm;


public class DrawEleType {
	public static boolean drawMM;
	public static boolean drawMMBlock;
	public static boolean pickMM;
	
	@SuppressWarnings("static-access")
	public void setDrawMMEleType(DrawEleType drawMMEleType){
		this.drawMM = drawMMEleType.drawMM;
		this.drawMMBlock = drawMMEleType.drawMMBlock;
		this.pickMM = drawMMEleType.pickMM;
	}

}
