package com.mansion.tele.business.landmark;

import java.io.Serializable;

/**
 * 
 * @author Administrator
 * 
 */
@SuppressWarnings("serial")
public class ShowStyle implements Serializable {

	/**
	 * 比例尺编码，对整个数据的比例尺按照从下到上的顺序进行编号，从1开始。
	 */
	 int level;

	/**
	 * Tele标记类型
	 */
	 long lMarkType;

	/**
	 * icon字体宽度（单位：mm）
	 */
	 double font;

	/**
	 * 列宽
	 */
	 double xSpace;

	/**
	 * 行高
	 */
	 double ySpace;
	 /**
	  * 显示方式 -1:此层没有该类型数据;0:不显示文字；1：不显示icon；2：中心显示；3：外面显示
	  */
	 byte display;
	 /**
	 * 文字间距
	 */
	double letterSpacing;
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public long getlMarkType() {
		return lMarkType;
	}
	public void setlMarkType(long lMarkType) {
		this.lMarkType = lMarkType;
	}
	public double getFont() {
		return font;
	}
	public void setFont(double font) {
		this.font = font;
	}
	public double getxSpace() {
		return xSpace;
	}
	public void setxSpace(double xSpace) {
		this.xSpace = xSpace;
	}
	public double getySpace() {
		return ySpace;
	}
	public void setySpace(double ySpace) {
		this.ySpace = ySpace;
	}
	public byte getDisplay() {
		return display;
	}
	public void setDisplay(byte display) {
		this.display = display;
	}
	public double getLetterSpacing() {
		return letterSpacing;
	}
	public void setLetterSpacing(double letterSpacing) {
		this.letterSpacing = letterSpacing;
	}

}
