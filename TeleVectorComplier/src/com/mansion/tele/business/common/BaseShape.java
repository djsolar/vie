package com.mansion.tele.business.common;

import java.util.ArrayList;
import java.util.List;

import com.mansion.tele.db.bean.elemnet.ShpPoint;
/**
 * 
 * @author Administrator
 *
 */
public class BaseShape {
	/**
	 * 形状坐标集合
	 */
	private List<ShpPoint> coordinate = new ArrayList<ShpPoint>();
	


	/**
	 * 多边形的分割线总数
	 */
	private byte lineIndexNum;
	
	/**
	 * 多边形的分割线所有索引
	 */
	private int[] splitLinesIndexs;
	/**
	 * 获得coordinate
	 * @return List<ShpPoint>
	 */
	public List<ShpPoint> getCoordinate() {
		return this.coordinate;
	}

	/**
	 * 设置coordinate
	 * @param coordinate List<ShpPoint>
	 */
	public void setCoordinate(List<ShpPoint> coordinate) {
		this.coordinate = coordinate;
	}

	/**
	 * 获得lineIndexNum
	 * @return byte
	 */
	public byte getLineIndexNum() {
		return this.lineIndexNum;
	}

	/**
	 * 设置lineIndexNum
	 * @param lineIndexNum byte
	 */
	public void setLineIndexNum(byte lineIndexNum) {
		this.lineIndexNum = lineIndexNum;
	}

	/**
	 * 获得splitLinesIndexs
	 * @return byte[]
	 */
	public int[] getSplitLinesIndexs() {
		return this.splitLinesIndexs;
	}
	/**
	 * 设置splitLinesIndexs
	 * @param splitLinesIndexs byte[]
	 */
	public void setSplitLinesIndexs(int[] splitLinesIndexs) {
		this.splitLinesIndexs = splitLinesIndexs;
	}
}
