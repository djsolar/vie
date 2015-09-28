package com.mansion.tele.business.landmark;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 
 * @author Administrator
 * 
 */
@SuppressWarnings("serial")
@XStreamAlias("DistrictDispInfo")
public class DistrictDispInfo implements Serializable {
	/***
	 * 行政区划名称
	 */
	private String districtName;
	
	/**
	 * 所需修改的层级
	 */
	private int Level;
	/**
	 * 文字的显示位置
	 */
	private int iTxtStation; // 文字的显示位置
	/**
	 * 开始显示的比例尺
	 */
	private int iBeginVS; // 开始显示的比例尺
	/**
	 * 上层是否存在
	 */
	private int iEndVS; // 上层是否存在
	/**
	 * 文字是否显示
	 */
	private boolean bTxtFlag; // 文字是否显示
	/**
	 * Icon是否显示
	 */
	private boolean bIconFlag; // Icon是否显示
	/**
	 * 调整后的经度
	 */
	private int iCoorX; // 调整后的经度
	/**
	 * 调整后的纬度
	 */
	private int iCoorY; // 调整后的纬度
	
	private boolean mustShow; // 此特殊处理必须显示
	public String getDistrictName() {
		return districtName;
	}
	public int getLevel() {
		return Level;
	}
	public int getiTxtStation() {
		return iTxtStation;
	}
	public int getiBeginVS() {
		return iBeginVS;
	}
	public int getiEndVS() {
		return iEndVS;
	}
	public boolean isbTxtFlag() {
		return bTxtFlag;
	}
	public boolean isbIconFlag() {
		return bIconFlag;
	}
	public int getiCoorX() {
		return iCoorX;
	}
	public int getiCoorY() {
		return iCoorY;
	}
	public boolean isMustShow() {
		return mustShow;
	}
	public void setMustShow(boolean mustShow) {
		this.mustShow = mustShow;
	}
	
	
	

}
