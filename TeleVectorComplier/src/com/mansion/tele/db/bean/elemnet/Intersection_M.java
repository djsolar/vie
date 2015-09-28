package com.mansion.tele.db.bean.elemnet;

@SuppressWarnings("serial")
public class Intersection_M extends Intersection {


	/** 在block边界上 */
	private byte byBlockBorder;

	public byte getByBlockBorder() {
		return byBlockBorder;
	}

	public void setByBlockBorder(byte byBlockBorder) {
		this.byBlockBorder = byBlockBorder;
	}
	public void convert(Intersection_M intersection_m) {
		this.setAstInnerNodeID(intersection_m.getAstInnerNodeID()) ;
		this.setAstOutNodeID(intersection_m.getAstOutNodeID());
		this.setAstInnerRoadList(intersection_m.getAstInnerRoadList());
		this.setAstOutRoadList(intersection_m.getAstOutRoadList());
		this.setByintersectiontype(intersection_m.getByintersectiontype());
		this.setByBlockBorder(intersection_m.getByBlockBorder());

	}

}
