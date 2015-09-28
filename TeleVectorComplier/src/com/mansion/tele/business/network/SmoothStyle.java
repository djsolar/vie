package com.mansion.tele.business.network;

public class SmoothStyle {
	/**
	 * 平滑只处理本线和辅路，其他类型道路都不处理
	 */
	
	/**
	 * 角度的余玄小于此值，且点两边的距离有一个小于此值，删除当前点；
	 */
	public static final float angle = 175.0f;//单位： 
	public static final float length_1 = 2f;//单位：CM
	
	/**
	 * 点两边的距离有都小于此值，删除此点
	 * 如果只有一个小于，需要判断防止出现尖角，暂时不处理；
	 */
	public static final float length_2 = 0.2f;//单位：CM
	
}
