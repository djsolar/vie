package com.sunmap.teleview.view.mainfram;


/**
 * 描画标记数据
 * @author lijingru
 *
 */
public class DrawEleType {

	public boolean drawRoad;
	public boolean drawBg;
	public boolean drawRoadName;
	public boolean drawPOI;
	public boolean drawEye;
	public boolean drawBlock;
	public boolean pickEye;
	public boolean pickRoad;
	public boolean pickBg;
	public boolean pickPoi;
	public boolean pickRoadName;
	
	public boolean drawMM;
	public boolean drawMMBlock;
	public boolean pickMM;
	
	
	public String getDrawEleType() {
		StringBuffer drawEleType = new StringBuffer();
		drawEleType.append("com.sunmap.teleview.look.road").append("=").append(drawRoad).append(",");
		drawEleType.append("com.sunmap.teleview.look.bg").append("=").append(drawBg).append(",");
		drawEleType.append("com.sunmap.teleview.look.roadname").append("=").append(drawRoadName).append(",");
		drawEleType.append("com.sunmap.teleview.look.poi").append("=").append(drawPOI).append(",");
		drawEleType.append("com.sunmap.teleview.look.regionline").append("=").append(drawEye).append(",");
		drawEleType.append("com.sunmap.teleview.look.block").append("=").append(drawBlock).append(",");
		drawEleType.append("com.sunmap.teleview.pick.regionline").append("=").append(pickEye).append(",");
		drawEleType.append("com.sunmap.teleview.pick.road").append("=").append(pickRoad).append(",");
		drawEleType.append("com.sunmap.teleview.pick.bg").append("=").append(pickBg).append(",");
		drawEleType.append("com.sunmap.teleview.pick.poi").append("=").append(pickPoi).append(",");
		drawEleType.append("com.sunmap.teleview.pick.roadname").append("=").append(pickRoadName).append(",");
		drawEleType.append("com.sunmap.teleview.look.MM").append("=").append(drawMM).append(",");
		drawEleType.append("com.sunmap.teleview.look.MMblock").append("=").append(drawMMBlock).append(",");
		drawEleType.append("com.sunmap.teleview.pick.MM").append("=").append(pickMM);
		return drawEleType.toString();
	}
	
	public void setDrawEleType(String string){
		String[] drawEleTypes = string.split(",");
		for (int i = 0; i < drawEleTypes.length; i++) {
			String[] drawEle = drawEleTypes[i].split("=");
			if (drawEle[0].equals("com.sunmap.teleview.look.road")) {
				drawRoad = Boolean.parseBoolean(drawEle[1]);
			}
			if (drawEle[0].equals("com.sunmap.teleview.look.bg")) {
				drawBg = Boolean.parseBoolean(drawEle[1]);
			}
			if (drawEle[0].equals("com.sunmap.teleview.look.roadname")) {
				drawRoadName = Boolean.parseBoolean(drawEle[1]);
			}
			if (drawEle[0].equals("com.sunmap.teleview.look.poi")) {
				drawPOI = Boolean.parseBoolean(drawEle[1]);
			}
			if (drawEle[0].equals("com.sunmap.teleview.look.regionline")) {
				drawEye = Boolean.parseBoolean(drawEle[1]);
			}
			if (drawEle[0].equals("com.sunmap.teleview.look.block")) {
				drawBlock = Boolean.parseBoolean(drawEle[1]);
			}
			if (drawEle[0].equals("com.sunmap.teleview.pick.regionline")) {
				pickEye = Boolean.parseBoolean(drawEle[1]);
			}
			if (drawEle[0].equals("com.sunmap.teleview.pick.road")) {
				pickRoad = Boolean.parseBoolean(drawEle[1]);
			}
			if (drawEle[0].equals("com.sunmap.teleview.pick.bg")) {
				pickBg = Boolean.parseBoolean(drawEle[1]);
			}
			if (drawEle[0].equals("com.sunmap.teleview.pick.poi")) {
				pickPoi = Boolean.parseBoolean(drawEle[1]);
			}
			if (drawEle[0].equals("com.sunmap.teleview.pick.roadname")) {
				pickRoadName = Boolean.parseBoolean(drawEle[1]);
			}
			if (drawEle[0].equals("com.sunmap.teleview.look.MM")) {
				drawMM = Boolean.parseBoolean(drawEle[1]);
			}
			if (drawEle[0].equals("com.sunmap.teleview.look.MMblock")) {
				drawMMBlock = Boolean.parseBoolean(drawEle[1]);
			}
			if (drawEle[0].equals("com.sunmap.teleview.pick.MM")) {
				pickMM = Boolean.parseBoolean(drawEle[1]);
			}
		}
		
	}
	
	public void setValue(DrawEleType drawEleType){
		this.drawRoad = drawEleType.drawRoad;
		this.drawBg = drawEleType.drawBg;
		this.drawRoadName = drawEleType.drawRoadName;
		this.drawPOI = drawEleType.drawPOI;
		this.drawEye = drawEleType.drawEye;
		this.drawBlock = drawEleType.drawBlock;
		this.pickEye = drawEleType.pickEye;
		this.pickRoad = drawEleType.pickRoad;
		this.pickBg = drawEleType.pickBg;
		this.pickPoi = drawEleType.pickPoi;
		this.pickRoadName = drawEleType.pickRoadName;
		this.drawMM = drawEleType.drawMM;
		this.drawMMBlock = drawEleType.drawMMBlock;
		this.pickMM = drawEleType.pickMM;
	}
	
}
