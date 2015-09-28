package com.mansion.tele.db.bean.elemnet;


@SuppressWarnings("serial")
public class PointShp extends BaseShp
{
	private ShpPoint coordinate;

	public PointShp() {

	}

	public ShpPoint getCoordinate()
	{
		return coordinate;
	}

	public void setCoordinate(ShpPoint coordinate)
	{
		this.coordinate = coordinate;
	}
}
