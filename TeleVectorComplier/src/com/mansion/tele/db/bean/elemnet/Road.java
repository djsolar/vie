package com.mansion.tele.db.bean.elemnet;


/**
 * Road entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@SuppressWarnings("serial")
public class Road extends AbstractRoad implements Comparable {
	private byte bydisplaylevel;// 径路等级

	public byte getBydisplaylevel() {
		return bydisplaylevel;
	}

	public void setBydisplaylevel(byte bydisplaylevel) {
		this.bydisplaylevel = bydisplaylevel;
	}

	@Override
	public int compareTo(Object o) {
		if (o == null) {
			return -1;
		}
		Road road = (Road) o;
		return this.strID.compareTo(road.strID);
	}
}
