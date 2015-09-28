package com.mansion.tele.business.common;


/**
 * 道路形状
 * @author wenxc
 *
 */
public class TeleRoadPloylineShp extends BaseShape implements Comparable<TeleRoadPloylineShp>{
	
	
	/**
	 * 标识是否为建设中道路 1为是 0 为不是
	 */
	private byte buildindRoad;
	/**
	 * 标识是否为隧道 1为是 0为不是
	 */
	private byte tunnel;
	/**
	 * 桥
	 */
	private byte bridge;
	/**
	 * 添加相对跨压
	 */
	private byte grade; 
	/**
	 * 起始点跨压
	 */
	private byte byStartGrade;
	/**
	 * 终止点跨压
	 */
	private byte byEndGrade;
	/**
	 * 添加道路种别
	 */
	private byte nrc;
	/**
	 * LayerNo
	 */
	private byte layerNO;
	/**
	 * 获得在建道路
	 * @return byte
	 */ 
	public byte getBuildindRoad() {
		return this.buildindRoad;
	}
	/**
	 * 设置buildindRoad
	 * @param buildindRoad byte
	 */
	public void setBuildindRoad(byte buildindRoad) {
		this.buildindRoad = buildindRoad;
	}
	/**
	 * 返回tunnel
	 * @return byte
	 */
	public byte getTunnel() {
		return this.tunnel;
	}
	/**
	 * 设置tunnel
	 * @param tunnel byte
	 */
	public void setTunnel(byte tunnel) {
		this.tunnel = tunnel;
	}
	/**
	 * 获得bridge
	 * @return byte
	 */
	public byte getBridge() {
		return this.bridge;
	}
	/**
	 * 设置brigde
	 * @param bridge byte
	 */
	public void setBridge(byte bridge) {
		this.bridge = bridge;
	}
	/**
	 * 获得grade
	 * @return byte
	 */
	public byte getGrade() {
		return this.grade;
	}
	/**
	 * 设置grade
	 * @param grade byte
	 */
	public void setGrade(byte grade) {
		this.grade = grade;
	}
	/**
	 * 获得StartGrade
	 * @return byte
	 */
	public byte getByStartGrade() {
		return this.byStartGrade;
	}
	/**
	 * 设置StartGrade
	 * @param byStartGrade byte
	 */
	public void setByStartGrade(byte byStartGrade) {
		this.byStartGrade = byStartGrade;
	}
	/**
	 * 获得EndGrade
	 * @return byte
	 */
	public byte getByEndGrade() {
		return this.byEndGrade;
	}
	/**
	 * 设置EndGrade
	 * @param byEndGrade byte
	 */
	public void setByEndGrade(byte byEndGrade) {
		this.byEndGrade = byEndGrade;
	}
	/**
	 * 获得nrc
	 * @return byte
	 */
	public byte getNrc() {
		return this.nrc;
	}
	/**
	 * 设置nrc
	 * @param nrc byte
	 */
	public void setNrc(byte nrc) {
		this.nrc = nrc;
	}
	/**
	 * 获得layerNo
	 * @return byte
	 */
	public byte getLayerNO() {
		return this.layerNO;
	}
	/**
	 * 设置layerNo
	 * @param layerNO byte
	 */
	public void setLayerNO(byte layerNO) {
		this.layerNO = layerNO;
	}
	/**
	 * 由大到小
	 * @param o 道路类型
	 * @return int
	 * 
	 */
	@Override
	public int compareTo(TeleRoadPloylineShp o) {
		if(this.nrc != o.nrc){
			return o.nrc - this.nrc;
		}
		return 0;
	}
}
